package com.laulem.vectopath.client.controller;

import com.laulem.vectopath.business.model.DocumentChunk;
import com.laulem.vectopath.business.service.DocumentChunkService;
import com.laulem.vectopath.client.dto.DocumentChunkResponse;
import com.laulem.vectopath.client.dto.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/search")
@CrossOrigin(origins = "*")
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final DocumentChunkService documentChunkService;

    public SearchController(DocumentChunkService documentChunkService) {
        this.documentChunkService = documentChunkService;
    }

    @PostMapping("/semantic")
    public ResponseEntity<List<DocumentChunkResponse>> searchSemantic(@RequestBody SearchRequest request) {
        logger.info("Semantic search: {}", request.getQuery());

        try {
            List<DocumentChunk> chunks = documentChunkService.searchSimilarChunks(
                request.getQuery(),
                request.getLimit()
            );
            List<DocumentChunkResponse> responses = chunks.stream()
                    .map(DocumentChunkResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            logger.error("Error during semantic search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
