package com.laulem.vectopath.business.repository;

import com.laulem.vectopath.business.model.PartialResource;
import com.laulem.vectopath.business.model.Resource;

import java.util.List;
import java.util.UUID;

public interface VectorStoreRepository {

    void addResource(Resource resource);

    List<PartialResource> searchSimilar(String query, int limit, double minSimilarity, String currentUser, List<String> userAuthorities, List<UUID> resourceIds);

    void deleteResource(UUID resourceId);
}
