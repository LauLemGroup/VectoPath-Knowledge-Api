package com.laulem.vectopath.client.service.resource;

import com.laulem.vectopath.business.exception.ParamException;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.ContentDownloaderService;
import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.client.dto.CreateResourceRequest;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UrlResourceGeneration implements GeneralResourceGeneration {

    private static final Logger logger = LoggerFactory.getLogger(UrlResourceGeneration.class);

    private final ResourceService resourceService;
    private final ContentDownloaderService contentDownloaderService;

    public UrlResourceGeneration(ResourceService resourceService,
                                 ContentDownloaderService contentDownloaderService) {
        this.resourceService = resourceService;
        this.contentDownloaderService = contentDownloaderService;
    }

    @Override
    public String getSourceType() {
        return "URL";
    }

    @Override
    public Resource processResource(Resource resource, CreateResourceRequest request) throws IOException {
        validateInput(resource, request);
        logger.info("Creating resource from URL: {}", request.url());
        String content = contentDownloaderService.downloadContent(request.url().trim());
        resource.setSourceType(getSourceType());
        resource.setSourceName(request.url());
        resource.setContent(content);
        resource.setSize((long) content.getBytes().length);
        resource.setContentType(MediaType.TEXT_PLAIN_VALUE);
        return resourceService.createResource(resource);
    }

    private void validateInput(Resource resource, CreateResourceRequest request) {
        if (Strings.isBlank(resource.getName())) {
            throw new ParamException("REQUIRED", "Resource name is required", "name");
        }
        if (Strings.isBlank(request.url())) {
            throw new ParamException("REQUIRED", "URL is required for URL resource type", "url");
        }
    }
}

