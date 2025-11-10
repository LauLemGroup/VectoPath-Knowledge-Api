package com.laulem.vectopath.client.service;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.FileResourceCreationService;
import com.laulem.vectopath.business.service.TextResourceCreationService;
import com.laulem.vectopath.business.service.UrlResourceCreationService;
import com.laulem.vectopath.client.dto.CreateResourceRequest;
import com.laulem.vectopath.client.exception.UnsupportedSourceTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
public class ResourceCreationOrchestrator {
    private final TextResourceCreationService textResourceCreationService;
    private final UrlResourceCreationService urlResourceCreationService;
    private final FileResourceCreationService fileResourceCreationService;

    public ResourceCreationOrchestrator(TextResourceCreationService textResourceCreationService, UrlResourceCreationService urlResourceCreationService,
                                        FileResourceCreationService fileResourceCreationService) {
        this.textResourceCreationService = textResourceCreationService;
        this.urlResourceCreationService = urlResourceCreationService;
        this.fileResourceCreationService = fileResourceCreationService;
    }

    public Resource createResource(CreateResourceRequest request, MultipartFile file) throws IOException {
        CreateResourceRequest.SourceType sourceType = getSourceTypeOrDefault(request);
        switch (sourceType) {
            case TEXT:
                return textResourceCreationService.createFromText(request.name(), request.content(), request.metadata());
            case URL:
                return urlResourceCreationService.createFromUrl(request.name(), request.url(), request.metadata());
            case FILE:
                Objects.requireNonNull(file, "MultipartFile must not be null for FILE source type");
                return fileResourceCreationService.createFromFileContent(request.name(), file.getBytes(), file.getOriginalFilename(), request.metadata());
            default:
                throw new UnsupportedSourceTypeException(sourceType);
        }
    }

    private CreateResourceRequest.SourceType getSourceTypeOrDefault(final CreateResourceRequest request) {
        return request.sourceType() != null ? request.sourceType() : CreateResourceRequest.SourceType.TEXT;
    }
}

