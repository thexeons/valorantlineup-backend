package org.gtf.valorantlineup.services;

import org.apache.commons.io.FilenameUtils;
import org.gtf.valorantlineup.dto.request.LineupRequest;
import org.gtf.valorantlineup.dto.request.NodeRequest;
import org.gtf.valorantlineup.dto.response.ImageResponse;
import org.gtf.valorantlineup.dto.response.NodeResponse;
import org.gtf.valorantlineup.dto.response.LineupResponse;
import org.gtf.valorantlineup.exception.GTFException;
import org.gtf.valorantlineup.models.*;
import org.gtf.valorantlineup.repositories.ImageRepository;
import org.gtf.valorantlineup.repositories.NodeRepository;
import org.gtf.valorantlineup.repositories.LineupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Value("${upload.path}")
    String uploadPath;

    @Value("${download.path}")
    String downloadPath;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public List<LineupResponse> getLineUps() {
        List<LineupResponse> response = new ArrayList<>();
        User user = authenticationService.getCurrentUser().orElseThrow(() -> new GTFException(HttpStatus.NOT_FOUND, "Error: User not found"));
        List<Lineup> lineups = lineupRepository.findAllByUser(user);
        for (int i = 0; i < lineups.size(); i++) {
            LineupResponse row = new LineupResponse();
            row.setUuidLineup(lineups.get(i).getUuid());
            row.setTitle(lineups.get(i).getTitle());
            row.setMap(lineups.get(i).getMap());
            response.add(row);
        }
        return response;
    }

    public List<NodeResponse> getNodes(String uuid) {
        List<NodeResponse> response = new ArrayList<>();
        if(!lineupRepository.existsByUuid(uuid)){
            throw new GTFException(HttpStatus.NOT_FOUND,"Error: Lineup not found!");
        }
        List<Node> nodes = nodeRepository.findAllByLineupUuid(uuid);
        for (int i = 0; i < nodes.size(); i++) {
            NodeResponse row = new NodeResponse();
            row.setUuidNode(nodes.get(i).getUuid());
            row.setTitle(nodes.get(i).getTitle());
            row.setDescription(nodes.get(i).getDescription());
            row.setSkillType(nodes.get(i).getSkillType());
            HashMap<String,Double> source = new HashMap<>();
            source.put("x",nodes.get(i).getSource().getX());
            source.put("y",nodes.get(i).getSource().getY());
            row.setSource(source);
            HashMap<String,Double> destination = new HashMap<>();
            destination.put("x",nodes.get(i).getDestination().getX());
            destination.put("y",nodes.get(i).getDestination().getY());
            row.setDestination(destination);
            List<ImageResponse> imageResponses = new ArrayList<>();
            List<Image> images = imageRepository.findAllByNode(nodes.get(i));
            for(int j=0;j<images.size();j++)
            {
                ImageResponse imageResponse = new ImageResponse();
                imageResponse.setUuid(images.get(j).getUuid());
                imageResponse.setUrl(images.get(j).getUrl());
                imageResponses.add(imageResponse);
            }
            row.setImages(imageResponses);
            response.add(row);
        }
        return response;
    }

    public LineupResponse postLineup(LineupRequest lineupRequest){
        User user = authenticationService.getCurrentUser().orElseThrow(() -> new GTFException(HttpStatus.NOT_FOUND, "Error: User not found"));
        if(lineupRepository.existsByTitle(lineupRequest.getTitle())){
            throw new GTFException(HttpStatus.CONFLICT, "Error: Title existed.");
        }
        Lineup lineup = new Lineup();
        lineup.setUser(user);
        lineup.setTitle(lineupRequest.getTitle());
        lineup.setMap(lineupRequest.getMap());
        lineup = lineupRepository.saveAndFlush(lineup);
        LineupResponse lineupResponse = new LineupResponse();
        lineupResponse.setMap(lineup.getMap());
        lineupResponse.setTitle(lineup.getTitle());
        lineupResponse.setUuidLineup(lineup.getUuid());
        return lineupResponse;
    }

    public LineupResponse editLineup(String uuid, LineupRequest lineupRequest){
        User user = authenticationService.getCurrentUser().orElseThrow(() -> new GTFException(HttpStatus.NOT_FOUND, "Error: User not found"));
        Lineup lineup = lineupRepository.findByUuid(uuid);
        if(lineup==null){
            throw new GTFException(HttpStatus.CONFLICT,"Lineup not found!");
        }
        if(lineup.getUser().getUuid()!=user.getUuid()){
            throw new GTFException(HttpStatus.FORBIDDEN, "Can't edit other user's lineup!");
        }
        lineup.setTitle(lineupRequest.getTitle());
        lineup = lineupRepository.saveAndFlush(lineup);
        LineupResponse lineupResponse = new LineupResponse();
        lineupResponse.setMap(lineup.getMap());
        lineupResponse.setTitle(lineup.getTitle());
        lineupResponse.setUuidLineup(lineup.getUuid());
        return lineupResponse;
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
    public List<NodeResponse> updateNodes(String uuid, List<NodeRequest> nodeRequests){
        Lineup lineup = lineupRepository.findByUuid(uuid);
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
            node.setSource(new Coordinate(nodeRequests.get(i).getSourceX(),nodeRequests.get(i).getSourceY()));
            if(nodeRequests.get(i).getDestinationX() == null || nodeRequests.get(i).getDestinationY() == null ){
                node.setDestination(new Coordinate(null,null));
            }
            else
            {
                node.setDestination(new Coordinate(nodeRequests.get(i).getDestinationX(),nodeRequests.get(i).getDestinationY()));
            }
            node.setSkillType(nodeRequests.get(i).getSkillType());
            nodes.add(node);
            node = nodeRepository.save(node);
            for(int j = 0; j < nodeRequests.get(i).getUriIds().length; j++)
            {
                Image image = imageRepository.findByUuid(nodeRequests.get(i).getUriIds()[j]);
                if(image == null)
                {
                    throw new GTFException(HttpStatus.CONFLICT, "Error: Image not found");
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

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (file.isEmpty()) {
            throw new GTFException(HttpStatus.BAD_REQUEST,"Error: File is empty.");
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadPath + datecode.format(new Date()) + "." + extension);
            if (!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image image = new Image();
        image.setFileSize(file.getSize());
        image.setOriginalName(file.getOriginalFilename());
        image.setUrl(downloadPath + datecode.format(new Date()) + "." + extension);
        image = imageRepository.saveAndFlush(image);
        ImageResponse response = new ImageResponse();
        response.setUrl(image.getUrl());
        response.setUuid(image.getUuid());
        return response;
    }

}
