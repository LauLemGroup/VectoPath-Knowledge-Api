package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;

import java.io.IOException;

public interface UrlResourceCreationService {

    Resource createFromUrl(Resource resource) throws IOException;
}
