package com.laulem.vectopath.client.service;

import com.laulem.vectopath.client.dto.CreateResourceRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResourceRequestAdapter {

    public CreateResourceRequest adaptMultipartRequest(MultipartFile file, CreateResourceRequest existingRequest) {
        CreateResourceRequest request = new CreateResourceRequest();
        request.setSourceType(CreateResourceRequest.SourceType.FILE);

        if (existingRequest != null) {
            request.setName(existingRequest.getName());
            request.setMetadata(existingRequest.getMetadata());
            request.setContentType(existingRequest.getContentType());
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            request.setName(file.getOriginalFilename());
        }

        return request;
    }
}
