package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.exception.ParamException;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.ContentDownloaderService;
import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.business.service.RoleValidationService;
import com.laulem.vectopath.business.service.UrlResourceCreationService;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UrlResourceCreationServiceImpl implements UrlResourceCreationService {

    private static final Logger logger = LoggerFactory.getLogger(UrlResourceCreationServiceImpl.class);

    private final ResourceService resourceService;
    private final ContentDownloaderService contentDownloaderService;
    private final RoleValidationService roleValidationService;

    public UrlResourceCreationServiceImpl(ResourceService resourceService,
                                          ContentDownloaderService contentDownloaderService,
                                          RoleValidationService roleValidationService) {
        this.resourceService = resourceService;
        this.contentDownloaderService = contentDownloaderService;
        this.roleValidationService = roleValidationService;
    }

    @Override
    public Resource createFromUrl(Resource resource) throws IOException {
        validateInput(resource);
        logger.info("Creating resource from URL: {}", resource.getSourceName());

        String content = contentDownloaderService.downloadContent(resource.getSourceName().trim());
        resource.setContent(content);
        resource.setSize((long) content.getBytes().length);
        resource.setContentType(MediaType.TEXT_PLAIN_VALUE);

        return resourceService.createResource(resource);
    }

    private void validateInput(Resource resource) {
        if (Strings.isBlank(resource.getName())) {
            throw new ParamException("REQUIRED", "Resource name is required", "name");
        }

        if (Strings.isBlank(resource.getSourceName())) {
            throw new ParamException("REQUIRED", "URL is required for URL resource type", "url");
        }

        roleValidationService.validateAllowedRoles(resource.getAllowedRoles());
    }
}
