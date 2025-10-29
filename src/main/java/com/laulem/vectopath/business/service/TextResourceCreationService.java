package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;

public interface TextResourceCreationService {

    Resource createFromText(String name, String content, String metadata);
}
