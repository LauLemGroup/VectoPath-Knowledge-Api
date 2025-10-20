package com.laulem.vectopath.client.controller;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;
import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.client.dto.CreateResourceRequest;
import com.laulem.vectopath.client.dto.ResourceResponse;
import com.laulem.vectopath.client.service.ResourceCreationOrchestrator;
import com.laulem.vectopath.client.service.ResourceRequestAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/resources")
@CrossOrigin(origins = "*")
public class ResourceController {
    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);

    private final ResourceService resourceService;
    private final ResourceCreationOrchestrator resourceCreationOrchestrator;
    private final ResourceRequestAdapter resourceRequestAdapter;

    public ResourceController(ResourceService resourceService,
                              ResourceCreationOrchestrator resourceCreationOrchestrator,
                              ResourceRequestAdapter resourceRequestAdapter) {
        this.resourceService = resourceService;
        this.resourceCreationOrchestrator = resourceCreationOrchestrator;
        this.resourceRequestAdapter = resourceRequestAdapter;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ResourceResponse> createResource(@RequestBody CreateResourceRequest request) {
        try {
            logger.info("Creating new resource of type {} : {}", request.getSourceType(), request.getName());
            Resource resource = resourceCreationOrchestrator.createResource(request, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResourceResponse(resource));

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            logger.error("Error creating resource", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Error creating resource", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResourceResponse> createResourceFromFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "metadata", required = false) String metadata) {
        try {
            logger.info("Creating resource from file: {}", file.getOriginalFilename());

            CreateResourceRequest request = new CreateResourceRequest();
            request.setSourceType(CreateResourceRequest.SourceType.FILE);
            request.setName(name != null ? name : file.getOriginalFilename());
            request.setMetadata(metadata);

            Resource resource = resourceCreationOrchestrator.createResource(request, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResourceResponse(resource));

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            logger.error("Error creating resource", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Error creating resource", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getAllResources() {
        logger.info("Retrieving all resources");

        try {
            List<Resource> resources = resourceService.getAllResources();
            List<ResourceResponse> responses = resources.stream()
                    .map(ResourceResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            logger.error("Error retrieving resources", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable UUID id) {
        logger.info("Retrieving resource: {}", id);

        try {
            Optional<Resource> resource = resourceService.getResourceById(id);

            if (resource.isPresent()) {
                return ResponseEntity.ok(new ResourceResponse(resource.get()));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Error retrieving resource: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponse>> searchResourcesByName(@RequestParam String name) {
        logger.info("Searching resources by name: {}", name);

        try {
            List<Resource> resources = resourceService.searchResourcesByName(name);
            List<ResourceResponse> responses = resources.stream()
                    .map(ResourceResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            logger.error("Error searching by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ResourceResponse>> getResourcesByStatus(@PathVariable ResourceStatus status) {
        logger.info("Retrieving resources by status: {}", status);

        try {
            List<Resource> resources = resourceService.getResourcesByStatus(status);
            List<ResourceResponse> responses = resources.stream()
                    .map(ResourceResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            logger.error("Error retrieving by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/reprocess")
    public ResponseEntity<ResourceResponse> reprocessResource(@PathVariable UUID id) {
        logger.info("Reprocessing resource: {}", id);

        try {
            Resource resource = resourceService.reprocessResource(id);
            return ResponseEntity.ok(new ResourceResponse(resource));

        } catch (RuntimeException e) {
            logger.error("Resource not found: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error reprocessing resource: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable UUID id) {
        logger.info("Deleting resource: {}", id);

        try {
            resourceService.deleteResource(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            logger.error("Error deleting resource: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
