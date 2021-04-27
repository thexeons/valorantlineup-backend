package org.gtf.valorantlineup.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gtf.valorantlineup.dto.request.LineupEditRequest;
import org.gtf.valorantlineup.dto.request.LineupMetaRequest;
import org.gtf.valorantlineup.dto.request.PostRequest;
import org.gtf.valorantlineup.dto.request.UpdateRequest;
import org.gtf.valorantlineup.dto.response.*;
import org.gtf.valorantlineup.enums.Peta;
import org.gtf.valorantlineup.exception.AbstractRequestHandler;
import org.gtf.valorantlineup.services.LineupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/lineup")
@Tag(name = "Lineup", description = "Lineup management API")
public class LineupController {

    @Autowired
    LineupService lineupService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Fetch authenticated user's lineups.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Private lineup fetched!",
                    content = @Content(schema = @Schema(implementation = LineupMetaResponse.class))
            )})
    public ResponseEntity<?> getLineups() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.getLineUps();
            }
        };
        return handler.getResult();
    }

    @GetMapping("/public")
    @Operation(summary = "Fetch list of public lineups.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public lineup fetched",
                    content = @Content(schema = @Schema(implementation = LineupPaginatedResponse.class))
            )})
    public ResponseEntity<?> getPublicLineups(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "5") int size, @RequestParam(required = false, defaultValue = "created_date") String sortBy, @RequestParam( required = false) String title, @RequestParam(required = false) Peta map) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.getPublicLineUps(page,size,sortBy,title,map);
            }
        };
        return handler.getResult();
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new lineup.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lineup meta posted",
                        content = @Content(schema = @Schema(implementation = LineupMetaResponse.class))
                        )})
    public ResponseEntity<?> postLineup(@RequestBody PostRequest request) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.postLineup(request);
            }
        };
        return handler.getResult();
    }

    @GetMapping("/{uuidLineup}")
    @Operation(summary = "Fetch a specific lineup data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lineup data fetched",
                    content = @Content(schema = @Schema(implementation = LineupNodeResponse.class))
            )})
    public ResponseEntity<?> getLineupNodes(@PathVariable String uuidLineup) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.getNodes(uuidLineup);
            }
        };
        return handler.getResult();
    }

    @DeleteMapping("/{uuidLineup}")
    @Operation(summary = "Delete a specific lineup.")
    public ResponseEntity<?> deleteLineup(@PathVariable String uuidLineup) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.deleteLineup(uuidLineup);
            }
        };
        return handler.getResult();
    }

//    @PutMapping("/{uuidLineup}")
//    @Operation(summary = "Edit lineup title.")
//    public ResponseEntity<?> editLineup(@PathVariable String uuidLineup, @RequestBody LineupEditRequest request) {
//        AbstractRequestHandler handler = new AbstractRequestHandler() {
//            @Override
//            public Object processRequest() {
//                return lineupService.editLineup(uuidLineup, request);
//            }
//        };
//        return handler.getResult();
//    }

    @PutMapping("/{uuidLineup}")
    @Operation(summary = "Update a specific lineup data.")
    public ResponseEntity<?> updateLineupNodes(@PathVariable String uuidLineup, @RequestBody UpdateRequest lineupRequest) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.updateNodes(uuidLineup,lineupRequest);
            }
        };
        return handler.getResult();
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image upload succeed",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))
            )})
    public ResponseEntity<?> uploadImage(@RequestPart("file") MultipartFile file) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.addImage(file);
            }
        };
        return handler.getResult();
    }
}
