package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.exception.ParamException;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.business.service.RoleValidationService;
import com.laulem.vectopath.business.service.TextResourceCreationService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class TextResourceCreationServiceImpl implements TextResourceCreationService {
    private final ResourceService resourceService;
    private final RoleValidationService roleValidationService;

    public TextResourceCreationServiceImpl(ResourceService resourceService,
                                           RoleValidationService roleValidationService) {
        this.resourceService = resourceService;
        this.roleValidationService = roleValidationService;
    }

    @Override
    public Resource createFromText(Resource resource) {
        validateInput(resource);
        resource.setContentType(MediaType.TEXT_PLAIN_VALUE);

        return resourceService.createResource(resource);
    }

    private void validateInput(Resource resource) {
        if (Strings.isBlank(resource.getName())) {
            throw new ParamException("REQUIRED", "Resource name is required", "name");
        }

        if (Strings.isBlank(resource.getContent())) {
            throw new ParamException("REQUIRED", "Content is required for TEXT resource type", "content");
        }

        roleValidationService.validateAllowedRoles(resource.getAllowedRoles());
    }
}
