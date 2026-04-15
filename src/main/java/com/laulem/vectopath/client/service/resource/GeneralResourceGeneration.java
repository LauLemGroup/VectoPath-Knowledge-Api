package com.laulem.vectopath.client.service.resource;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.client.dto.CreateResourceRequest;

import java.io.IOException;

public interface GeneralResourceGeneration {
    String getSourceType();
    Resource processResource(Resource resource, CreateResourceRequest request) throws IOException;
}
