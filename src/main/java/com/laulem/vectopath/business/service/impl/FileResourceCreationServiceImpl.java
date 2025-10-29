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

import java.nio.charset.StandardCharsets;

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
        String content = new String(fileContent, StandardCharsets.UTF_8);
        return resourceService.createResource(name.trim(), content, MediaType.TEXT_PLAIN_VALUE, metadata);
    }

    private void validateInput(String name, byte[] fileContent, String originalFilename) {
        if (Strings.isBlank(name)) {
            throw new ParamException("REQUIRED", "Resource name is required", "name");
        }

        if (fileContent == null || fileContent.length == 0) {
            throw new ParamException("REQUIRED", "File is required for FILE resource type", "file");
        }

        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".txt")) {
            throw new ParamException("REQUIRED", "Only .txt files are accepted", "file");
        }
    }
}
