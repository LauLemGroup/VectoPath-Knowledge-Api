package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.exception.ParamException;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.ContentDownloaderService;
import com.laulem.vectopath.business.service.ResourceService;
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

    public UrlResourceCreationServiceImpl(ResourceService resourceService, ContentDownloaderService contentDownloaderService) {
        this.resourceService = resourceService;
        this.contentDownloaderService = contentDownloaderService;
    }

    @Override
    public Resource createFromUrl(String name, String url, String metadata) throws IOException {
        validateInput(name, url);
        logger.info("Creating resource from URL: {}", url);
        String content = contentDownloaderService.downloadContent(url.trim());
        return resourceService.createResource(name.trim(), content, MediaType.TEXT_PLAIN_VALUE, metadata);
    }

    private void validateInput(String name, String url) {
        if (Strings.isBlank(name)) {
            throw new ParamException("REQUIRED", "Resource name is required", "name");
        }

        if (Strings.isBlank(url)) {
            throw new ParamException("REQUIRED", "URL is required for URL resource type", "url");
        }
    }
}
