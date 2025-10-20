package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.FileResourceCreationService;
import com.laulem.vectopath.business.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FileResourceCreationServiceImpl implements FileResourceCreationService {

    private static final Logger logger = LoggerFactory.getLogger(FileResourceCreationServiceImpl.class);

    private final ResourceService resourceService;

    public FileResourceCreationServiceImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Resource createFromFileContent(String name, byte[] fileContent, String originalFilename, String metadata) {
        validateInput(name, fileContent, originalFilename);
        logger.info("Creating resource from file: {}", originalFilename);

        try {
            String content = new String(fileContent, "UTF-8");
            return resourceService.createResource(name.trim(), content, "text/plain", metadata);
        } catch (Exception e) {
            logger.error("Error reading file: {}", originalFilename, e);
            throw new RuntimeException("Unable to read file content", e);
        }
    }


    private void validateInput(String name, byte[] fileContent, String originalFilename) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource name is required");
        }
        if (fileContent == null || fileContent.length == 0) {
            throw new IllegalArgumentException("File is required for FILE resource type");
        }
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".txt")) {
            throw new IllegalArgumentException("Only .txt files are accepted");
        }
    }
}
