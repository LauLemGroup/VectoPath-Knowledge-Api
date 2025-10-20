package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;

import java.util.Map;

public interface TextResourceCreationService {

    Resource createFromText(String name, String content, String metadata);
}
