package com.laulem.vectopath.client.service.resource;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.client.dto.CreateResourceRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileResourceGeneration {
    String getFileExtension();
    Resource processResource(Resource resource, CreateResourceRequest request, MultipartFile file) throws IOException;
}
