package com.laulem.vectopath.client.controller;

import com.laulem.vectopath.business.service.impl.VectorizedResourceService;
import com.laulem.vectopath.client.dto.DocumentChunkResponse;
import com.laulem.vectopath.client.dto.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/search")
@CrossOrigin(origins = "*")
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final VectorizedResourceService vectorizedResourceService;

    public SearchController(VectorizedResourceService vectorizedResourceService) {
        this.vectorizedResourceService = vectorizedResourceService;
    }

    @PostMapping("/semantic")
    public List<DocumentChunkResponse> searchSemantic(@RequestBody SearchRequest request) {
        logger.info("Semantic search: {}", request.getQuery());

        return vectorizedResourceService.searchSimilar(request.getQuery(), request.getLimit())
                .stream()
                .map(DocumentChunkResponse::new)
                .toList();
    }
}
