package org.gtf.valorantlineup.services;

import org.apache.commons.io.FilenameUtils;
import org.gtf.valorantlineup.dto.request.*;
import org.gtf.valorantlineup.dto.response.*;
import org.gtf.valorantlineup.enums.Peta;
import org.gtf.valorantlineup.exception.GTFException;
import org.gtf.valorantlineup.models.*;
import org.gtf.valorantlineup.repositories.*;
import org.hibernate.sql.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class LineupService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    LineupRepository lineupRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    AsyncLineupRepository asyncLineupRepository;

    @Autowired
    AsyncNodeRepository asyncNodeRepository;

    @Value("${upload.path}")
    String uploadPath;

    @Value("${download.path}")
    String downloadPath;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public List<LineupMetaResponse> getLineUps() {
        List<LineupMetaResponse> response = new ArrayList<>();
        User user = authenticationService.getCurrentUser().orElseThrow(() -> new GTFException(HttpStatus.NOT_FOUND, "Error: User not found"));
        List<Lineup> lineups = lineupRepository.findAllByUser(user);
        for (int i = 0; i < lineups.size(); i++) {
            LineupMetaResponse row = new LineupMetaResponse();
            row.setUuidLineup(lineups.get(i).getUuid());
            row.setTitle(lineups.get(i).getTitle());
            row.setMap(lineups.get(i).getMap().name());
            response.add(row);
        }
        return response;
    }

    public LineupPaginatedResponse getPublicLineUps(int page, int size, String sortBy, String title, Peta map) {
        //Check title
        //Prevent remarks from null value
        if (title == null) title = "";
        Pageable halaman = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Lineup> lineups;
        if(map == null)
        {
            lineups = lineupRepository.filterLineup(title,halaman);
        }
        else
        {
            lineups = lineupRepository.filterLineupEnum(title,map.name(),halaman);
        }
        LineupPaginatedResponse paginate = generateLineupMetaPagination(lineups);
        return paginate;
    }

    private LineupPaginatedResponse generateLineupMetaPagination(Page<Lineup> lineups) {
        List<LineupMetaResponse> vo = new ArrayList<>();
        LineupPaginatedResponse paginate = new LineupPaginatedResponse();
        for (Lineup x : lineups) {
            vo.add(convertLineupMetaDTO(x));
        }
        paginate.setLineups(vo);
        paginate.setCurrentPage(lineups.getNumber());
        paginate.setTotalPage(lineups.getTotalPages());
        paginate.setTotalElements(lineups.getTotalElements());
        paginate.setHasContent(lineups.hasContent());
        paginate.setHasNext(lineups.hasNext());
        paginate.setHasPrevious(lineups.hasPrevious());
        return paginate;
    }

    private LineupMetaResponse convertLineupMetaDTO(Lineup lineup){
        LineupMetaResponse response = new LineupMetaResponse();
        response.setTitle(lineup.getTitle());
        response.setUuidLineup(lineup.getUuid());
        response.setMap(lineup.getMap().name());
        return response;
    }

    private LineupNodeResponse convertLineupDTO(LineupMetaResponse meta, List<NodeResponse> nodes){
        LineupNodeResponse response = new LineupNodeResponse();
        response.setMeta(meta);
        response.setNodes(nodes);
        return response;
    }

    private NodeResponse convertNodeDTO(Node node){
        NodeResponse response = new NodeResponse();
        response.setUuidNode(node.getUuid());
        response.setTitle(node.getTitle());
        response.setDescription(node.getDescription());
        response.setSkillType(node.getSkillType());
        HashMap<String, Double> source = new HashMap<>();
        source.put("x", node.getSource().getX());
        source.put("y", node.getSource().getY());
        response.setSource(source);
        HashMap<String, Double> destination = new HashMap<>();
        if(node.getDestination() == null){
            destination.put("x",null);
            destination.put("y",null);
        } else{
            destination.put("x", node.getDestination().getX());
            destination.put("y", node.getDestination().getY());
        }
        response.setDestination(destination);
        response.setTags(node.getTags());
        List<ImageResponse> imageResponses = new ArrayList<>();
        List<Image> images = imageRepository.findAllByNode(node);
        for (int j = 0; j < images.size(); j++) {
            ImageResponse imageResponse = new ImageResponse();
            imageResponse.setUuid(images.get(j).getUuid());
            imageResponse.setUrl(images.get(j).getUrl());
            imageResponses.add(imageResponse);
        }
        response.setImages(imageResponses);
        return response;
    }



    @Async
    public CompletableFuture<LineupNodeResponse> asyncLineup(String uuid) {
        CompletableFuture<LineupMetaResponse> meta = asyncLineupRepository.findUuid(uuid).thenApply(this::convertLineupMetaDTO);
        CompletableFuture<List<NodeResponse>> nodes = asyncNodeRepository.findAllByLineupUuid(uuid).thenApply(n-> n.stream().map(this::convertNodeDTO).collect(Collectors.toList()));
        CompletableFuture<LineupNodeResponse> response = meta.thenCombine(nodes, (m,n) -> {
            LineupNodeResponse result = new LineupNodeResponse();
            result.setMeta(m);
            result.setNodes(n);
            return result;
        });
    return response;
    }

    public LineupNodeResponse getNodes(String uuid) {
        LineupNodeResponse response = new LineupNodeResponse();
        List<NodeResponse> nodeResponse = new ArrayList<>();
        Lineup lineup = lineupRepository.findByUuid(uuid);
        if(lineup == null){
            throw new GTFException(HttpStatus.NOT_FOUND,"Error: Lineup not found!");
        } else {
            LineupMetaResponse meta = convertLineupMetaDTO(lineup);
            response.setMeta(meta);
        }
        List<Node> nodes = nodeRepository.findAllByLineupUuid(uuid);
        if(nodes == null)
        {
            response.setNodes(Collections.emptyList());
        }
        else
        {
        for (int i = 0; i < nodes.size(); i++) {
            NodeResponse row = convertNodeDTO(nodes.get(i));
            nodeResponse.add(row);
        }
            response.setNodes(nodeResponse);
        }
        return response;
    }

    public LineupMetaResponse postLineup(PostRequest request){
        User user = authenticationService.getCurrentUser().orElseThrow(() -> new GTFException(HttpStatus.NOT_FOUND, "Error: User not found"));
        Lineup lineup = new Lineup();
        lineup.setUser(user);
        lineup.setTitle(request.getMeta().getTitle());
        try{
        lineup.setMap(Peta.valueOf(request.getMeta().getMap()));}
        catch (IllegalArgumentException e)
        {
            throw new GTFException(HttpStatus.BAD_REQUEST, "Error: " + request.getMeta().getMap() + " enum not recognized.");
        }
        lineup = lineupRepository.saveAndFlush(lineup);
        //Insert nodes
        UpdateRequest updateRequest = new UpdateRequest();
        LineupEditRequest meta = new LineupEditRequest();
        meta.setTitle(lineup.getTitle());
        updateRequest.setMeta(meta);
        updateRequest.setNodes(request.getNodes());
        updateNodes(lineup.getUuid(),updateRequest);
        //Construct response
        LineupMetaResponse lineupMetaResponse = new LineupMetaResponse();
        lineupMetaResponse.setMap(lineup.getMap().name());
        lineupMetaResponse.setTitle(lineup.getTitle());
        lineupMetaResponse.setUuidLineup(lineup.getUuid());
        return lineupMetaResponse;
    }

    public LineupMetaResponse editLineup(String uuid, LineupEditRequest request){
        User user = authenticationService.getCurrentUser().orElseThrow(() -> new GTFException(HttpStatus.NOT_FOUND, "Error: User not found"));
        Lineup lineup = lineupRepository.findByUuid(uuid);
        if(lineup==null){
            throw new GTFException(HttpStatus.CONFLICT,"Lineup not found!");
        }
        if(lineup.getUser().getUuid()!=user.getUuid()){
            throw new GTFException(HttpStatus.FORBIDDEN, "Can't edit other user's lineup!");
        }
        lineup.setTitle(request.getTitle());
        lineup = lineupRepository.saveAndFlush(lineup);
        LineupMetaResponse lineupMetaResponse = new LineupMetaResponse();
        lineupMetaResponse.setMap(lineup.getMap().name());
        lineupMetaResponse.setTitle(lineup.getTitle());
        lineupMetaResponse.setUuidLineup(lineup.getUuid());
        return lineupMetaResponse;
    }

    public String deleteLineup(String uuid){
        Lineup lineup = lineupRepository.findByUuid(uuid);
        if(lineup==null){
            throw new GTFException(HttpStatus.CONFLICT,"Lineup not found!");
        }
        lineupRepository.delete(lineup);
        return "Lineup has been deleted!";
    }

    @Transactional
    public LineupNodeResponse updateNodes(String uuid, UpdateRequest request){
        List<NodeRequest> nodeRequests = request.getNodes();
        Lineup lineup = lineupRepository.findByUuid(uuid);
        lineup.setTitle(request.getMeta().getTitle());
        lineup = lineupRepository.saveAndFlush(lineup);
        if(lineup==null)
        {
            throw new GTFException(HttpStatus.NOT_FOUND, "Error: Lineup not found");
        }
        nodeRepository.deleteAllByLineupUuid(uuid);
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < nodeRequests.size(); i++)
        {
            Node node = new Node();
            node.setTitle(nodeRequests.get(i).getTitle());
            node.setDescription(nodeRequests.get(i).getDescription());
            node.setLineup(lineup);
            node.setSource(new Coordinate(nodeRequests.get(i).getSource().getX(),nodeRequests.get(i).getSource().getY()));
            if(nodeRequests.get(i).getDestination() == null){
                node.setDestination(new Coordinate(null,null));
            }
            else
            {
                node.setDestination(new Coordinate(nodeRequests.get(i).getDestination().getX(),nodeRequests.get(i).getDestination().getY()));
            }
            node.setSkillType(nodeRequests.get(i).getSkillType());
            node.setTags(Arrays.asList(nodeRequests.get(i).getTags()));
            node = nodeRepository.save(node);
            nodes.add(node);
            for(int j = 0; j < nodeRequests.get(i).getImages().length; j++)
            {
                Image image = imageRepository.findByUuid(nodeRequests.get(i).getImages()[j].getUuid());
                if(image == null)
                {
                    throw new GTFException(HttpStatus.CONFLICT, "Error: Image (" + nodeRequests.get(i).getImages()[j] +  ") not found");
                }
                image.setNode(node);
                imageRepository.save(image);
            }
        }
        return getNodes(uuid);
    }

    public ImageResponse addImage(MultipartFile file)
    {
        SimpleDateFormat datecode = new SimpleDateFormat("ddMMyyyyhhmmss");
        String savedName = "";
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (file.isEmpty()) {
            throw new GTFException(HttpStatus.BAD_REQUEST,"Error: File is empty.");
        }
        try {
            savedName = datecode.format(new Date()) + "." + extension;
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadPath + savedName);
            if (!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image image = new Image();
        image.setFileSize(file.getSize());
        image.setOriginalName(file.getOriginalFilename());
        image.setSavedName(savedName);
        image.setUrl(downloadPath + savedName);
        image = imageRepository.saveAndFlush(image);
        ImageResponse response = new ImageResponse();
        response.setUrl(image.getUrl());
        response.setUuid(image.getUuid());
        return response;
    }

}
