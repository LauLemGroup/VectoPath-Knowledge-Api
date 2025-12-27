package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;

public interface FileResourceCreationService {
    Resource createFromFileContent(Resource resource);
}
