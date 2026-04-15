package com.laulem.vectopath.client.service.resource;

import com.laulem.vectopath.business.exception.ParamException;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.client.dto.CreateResourceRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class TextResourceGeneration implements GeneralResourceGeneration {
    private final ResourceService resourceService;

    public TextResourceGeneration(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public String getSourceType() {
        return "TEXT";
    }

    @Override
    public Resource processResource(Resource resource, CreateResourceRequest request) {
        validateInput(resource, request);
        resource.setContent(request.content());
        resource.setSourceType(getSourceType());
        resource.setSize((long) request.content().getBytes().length);
        resource.setContentType(MediaType.TEXT_PLAIN_VALUE);
        return resourceService.createResource(resource);
    }

    private void validateInput(Resource resource, CreateResourceRequest request) {
        if (Strings.isBlank(resource.getName())) {
            throw new ParamException("REQUIRED", "Resource name is required", "name");
        }
        if (Strings.isBlank(request.content())) {
            throw new ParamException("REQUIRED", "Content is required for TEXT resource type", "content");
        }
    }
}
