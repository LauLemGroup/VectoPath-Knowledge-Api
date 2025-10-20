package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;

import java.util.Map;

public interface UrlResourceCreationService {

    Resource createFromUrl(String name, String url, String metadata);
}
