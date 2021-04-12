package org.gtf.valorantlineup.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gtf.valorantlineup.dto.request.LineupRequest;
import org.gtf.valorantlineup.dto.request.NodeRequest;
import org.gtf.valorantlineup.exception.AbstractRequestHandler;
import org.gtf.valorantlineup.services.LineupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/lineup")
@Tag(name = "Lineup", description = "Lineup management API")
public class LineupController {

    @Autowired
    LineupService lineupService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Fetch list of lineups.")
    public ResponseEntity<?> getLineups() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.getLineUps();
            }
        };
        return handler.getResult();
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new lineup.")
    public ResponseEntity<?> postLineup(@RequestBody LineupRequest lineupRequest) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.postLineup(lineupRequest);
            }
        };
        return handler.getResult();
    }

    @GetMapping("/{uuidLineup}")
    @Operation(summary = "Fetch a specific lineup data.")
    public ResponseEntity<?> getLineupNodes(@PathVariable String uuidLineup) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return lineupService.getNodes(uuidLineup);
            }
        };
        return handler.getResult();
    }

    @PutMapping("/{uuidLineup}")
    @Operation(summary = "Edit lineup title.")
    public ResponseEntity<?> editLineup(@PathVariable String uuidLineup, @RequestBody LineupRequest lineupRequest) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return  lineupService.editLineup(uuidLineup,lineupRequest);
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

    @PutMapping("/update/{uuidLineup}")
    @Operation(summary = "Update a specific lineup data.")
    public ResponseEntity<?> updateLineupNodes(@PathVariable String uuidLineup, @RequestBody List<NodeRequest> lineupRequest) {
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
