package com.laulem.vectopath.client.service;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.FileResourceCreationService;
import com.laulem.vectopath.business.service.TextResourceCreationService;
import com.laulem.vectopath.business.service.UrlResourceCreationService;
import com.laulem.vectopath.client.dto.CreateResourceRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service de la couche client pour orchestrer les appels vers les services business
 * selon le type de source de la ressource
 */
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

    /**
     * Orchestre la création d'une ressource selon le type de source
     */
    public Resource createResource(CreateResourceRequest request, MultipartFile file) throws IOException {
        CreateResourceRequest.SourceType sourceType = request.getSourceType() != null ? request.getSourceType() : CreateResourceRequest.SourceType.TEXT;
        switch (sourceType) {
            case TEXT:
                return textResourceCreationService.createFromText(request.getName(), request.getContent(), request.getMetadata());
            case URL:
                return urlResourceCreationService.createFromUrl(request.getName(), request.getUrl(), request.getMetadata());
            case FILE:
                return fileResourceCreationService.createFromFileContent(request.getName(), file.getBytes(), file.getOriginalFilename(), request.getMetadata());
            default:
                throw new IllegalArgumentException("Type de source non supporté : " + sourceType);
        }
    }


}

