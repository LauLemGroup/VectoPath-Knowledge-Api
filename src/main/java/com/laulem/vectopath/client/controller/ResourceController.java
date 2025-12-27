package com.laulem.vectopath.client.controller;

import com.laulem.vectopath.business.exception.NotFoundException;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;
import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.client.dto.CreateResourceRequest;
import com.laulem.vectopath.client.dto.ResourceResponse;
import com.laulem.vectopath.client.service.ResourceCreationOrchestrator;
import com.laulem.vectopath.infra.conf.security.SecurityExpressions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {
    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);

    private final ResourceService resourceService;
    private final ResourceCreationOrchestrator resourceCreationOrchestrator;

    public ResourceController(ResourceService resourceService,
                              ResourceCreationOrchestrator resourceCreationOrchestrator) {
        this.resourceService = resourceService;
        this.resourceCreationOrchestrator = resourceCreationOrchestrator;
    }

    @PreAuthorize(SecurityExpressions.RESOURCES_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResourceResponse createResource(@RequestBody @Valid CreateResourceRequest request) throws IOException {
        logger.info("Creating new resource of type {} : {}", request.sourceType(), request.name());
        return new ResourceResponse(resourceCreationOrchestrator.createResource(request, null));
    }

    @PreAuthorize(SecurityExpressions.RESOURCES_WRITE)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResourceResponse createResourceFromFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "metadata", required = false) String metadata,
            @RequestParam(value = "access_level", required = false) Resource.AccessLevel accessLevel,
            @RequestParam(value = "allowed_roles", required = false) List<String> allowedRoles) throws IOException {
        logger.info("Creating resource from file: {} with name: {}", file.getOriginalFilename(), name);
        CreateResourceRequest request = new CreateResourceRequest(name, null, null, CreateResourceRequest.SourceType.FILE, null, metadata, accessLevel, allowedRoles);
        return new ResourceResponse(resourceCreationOrchestrator.createResource(request, file));
    }

    @PreAuthorize(SecurityExpressions.RESOURCES_READ)
    @GetMapping
    public List<ResourceResponse> getAllResources() {
        logger.info("Retrieving all resources");
        return resourceService.getAllResources()
                .stream()
                .map(ResourceResponse::new)
                .toList();
    }

    @PreAuthorize(SecurityExpressions.RESOURCES_READ)
    @GetMapping("/{id}")
    public ResourceResponse getResourceById(@PathVariable UUID id) {
        logger.info("Retrieving resource: {}", id);

        return resourceService.getResourceById(id)
                .map(ResourceResponse::new)
                .orElseThrow(() -> new NotFoundException("Resource", id.toString()));
    }

    @PreAuthorize(SecurityExpressions.RESOURCES_READ)
    @GetMapping("/search")
    public List<ResourceResponse> searchResourcesByName(@RequestParam String name) {
        logger.info("Searching resources by name: {}", name);

        return resourceService.searchResourcesByName(name)
                .stream()
                .map(ResourceResponse::new)
                .toList();
    }

    @PreAuthorize(SecurityExpressions.RESOURCES_READ)
    @GetMapping("/status/{status}")
    public List<ResourceResponse> getResourcesByStatus(@PathVariable ResourceStatus status) {
        logger.info("Retrieving resources by status: {}", status);

        return resourceService.getResourcesByStatus(status)
                .stream()
                .map(ResourceResponse::new)
                .toList();
    }

    @PreAuthorize(SecurityExpressions.RESOURCES_WRITE)
    @PostMapping("/{id}/reprocess")
    public ResourceResponse reprocessResource(@PathVariable UUID id) {
        logger.info("Reprocessing resource: {}", id);
        return new ResourceResponse(resourceService.reprocessResource(id));
    }

    @PreAuthorize(SecurityExpressions.RESOURCES_DELETE)
    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable UUID id) {
        logger.info("Deleting resource: {}", id);
        resourceService.deleteResource(id);
    }
}
