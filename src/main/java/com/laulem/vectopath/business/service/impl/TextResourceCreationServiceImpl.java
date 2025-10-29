package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.exception.ParamException;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.business.service.TextResourceCreationService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class TextResourceCreationServiceImpl implements TextResourceCreationService {
    private final ResourceService resourceService;

    public TextResourceCreationServiceImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Resource createFromText(String name, String content, String metadata) {
        validateInput(name, content);
        return resourceService.createResource(name.trim(), content.trim(), MediaType.TEXT_PLAIN_VALUE, metadata);
    }

    private void validateInput(String name, String content) {
        if (Strings.isBlank(name)) {
            throw new ParamException("REQUIRED", "Resource name is required", "name");
        }

        if (Strings.isBlank(content)) {
            throw new ParamException("REQUIRED", "Content is required for TEXT resource type", "content");
        }
    }
}
