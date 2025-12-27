package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.exception.ParamException;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.FileResourceCreationService;
import com.laulem.vectopath.business.service.ResourceService;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class FileResourceCreationServiceImpl implements FileResourceCreationService {

    private static final Logger logger = LoggerFactory.getLogger(FileResourceCreationServiceImpl.class);

    private final ResourceService resourceService;

    public FileResourceCreationServiceImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Resource createFromFileContent(Resource resource) {
        validateInput(resource, resource.getContent());
        logger.info("Creating resource from file: {}", resource.getSourceName());
        resource.setContentType(MediaType.TEXT_PLAIN_VALUE);

        return resourceService.createResource(resource);
    }

    private void validateInput(Resource resource, String fileContent) {
        if (Strings.isBlank(resource.getName())) {
            throw new ParamException("REQUIRED", "Resource name is required", "name");
        }

        if (Strings.isBlank(fileContent)) {
            throw new ParamException("REQUIRED", "File is required for FILE resource type", "file");
        }

        if (resource.getSourceName() == null || !resource.getSourceName().toLowerCase().endsWith(".txt")) {
            throw new ParamException("REQUIRED", "Only .txt files are accepted", "file");
        }
    }
}

