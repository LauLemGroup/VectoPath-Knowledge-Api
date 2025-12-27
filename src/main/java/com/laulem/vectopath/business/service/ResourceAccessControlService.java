package com.laulem.vectopath.business.service;
import com.laulem.vectopath.business.model.Resource;
import java.util.List;
public interface ResourceAccessControlService {
    List<Resource> filterAccessibleResources(List<Resource> resources);
    boolean hasAccess(Resource resource);
}
