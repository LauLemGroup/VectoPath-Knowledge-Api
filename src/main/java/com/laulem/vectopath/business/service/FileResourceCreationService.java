package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;

public interface FileResourceCreationService {

    Resource createFromFileContent(String name, byte[] fileContent, String originalFilename, String metadata);
}
