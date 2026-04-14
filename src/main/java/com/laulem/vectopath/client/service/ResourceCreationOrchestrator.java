package com.laulem.vectopath.client.service;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.AuthenticationService;
import com.laulem.vectopath.business.service.FileResourceCreationService;
import com.laulem.vectopath.business.service.TextResourceCreationService;
import com.laulem.vectopath.business.service.UrlResourceCreationService;
import com.laulem.vectopath.client.dto.CreateResourceRequest;
import com.laulem.vectopath.client.exception.UnsupportedSourceTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class ResourceCreationOrchestrator {
    private final TextResourceCreationService textResourceCreationService;
    private final UrlResourceCreationService urlResourceCreationService;
    private final FileResourceCreationService fileResourceCreationService;
    private final AuthenticationService authenticationService;

    public ResourceCreationOrchestrator(TextResourceCreationService textResourceCreationService,
                                        UrlResourceCreationService urlResourceCreationService,
                                        FileResourceCreationService fileResourceCreationService,
                                        AuthenticationService authenticationService) {
        this.textResourceCreationService = textResourceCreationService;
        this.urlResourceCreationService = urlResourceCreationService;
        this.fileResourceCreationService = fileResourceCreationService;
        this.authenticationService = authenticationService;
    }

    public Resource createResource(CreateResourceRequest request, MultipartFile file) throws IOException {
        CreateResourceRequest.SourceType sourceType = getSourceTypeOrDefault(request);
        String createdBy = authenticationService.getCurrentUser();
        Resource.AccessLevel accessLevel = request.accessLevel() != null ? request.accessLevel() : Resource.AccessLevel.PRIVATE;

        Resource resource = new Resource();
        resource.setName(request.name());
        resource.setMetadata(request.metadata());
        resource.setCreatedBy(createdBy);
        resource.setAccessLevel(accessLevel);
        resource.setAllowedRoles(request.allowedRoles());

        switch (sourceType) {
            case TEXT:
                resource.setContent(request.content());
                resource.setSourceType(Resource.SourceType.TEXT);
                resource.setSize((long) request.content().getBytes().length);
                return textResourceCreationService.createFromText(resource);
            case URL:
                resource.setSourceType(Resource.SourceType.URL);
                resource.setSourceName(request.url());
                return urlResourceCreationService.createFromUrl(resource);
            case FILE:
                Objects.requireNonNull(file, "MultipartFile must not be null for FILE source type");
                resource.setSourceType(Resource.SourceType.FILE);
                resource.setSourceName(file.getOriginalFilename());
                resource.setContent(new String(file.getBytes(), StandardCharsets.UTF_8));
                resource.setSize(file.getSize());
                return fileResourceCreationService.createFromFileContent(resource);
            default:
                throw new UnsupportedSourceTypeException(sourceType);
        }
    }

    private CreateResourceRequest.SourceType getSourceTypeOrDefault(final CreateResourceRequest request) {
        return request.sourceType() != null ? request.sourceType() : CreateResourceRequest.SourceType.TEXT;
    }
}

