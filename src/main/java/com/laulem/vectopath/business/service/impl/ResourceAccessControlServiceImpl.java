package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.AuthenticationService;
import com.laulem.vectopath.business.service.ResourceAccessControlService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceAccessControlServiceImpl implements ResourceAccessControlService {

    private final AuthenticationService authenticationService;

    public ResourceAccessControlServiceImpl(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public List<Resource> filterAccessibleResources(List<Resource> resources) {
        return resources.stream()
                .filter(this::hasAccess)
                .toList();
    }

    @Override
    public boolean hasAccess(Resource resource) {
        if (resource.getAccessLevel() == null) {
            // Par défaut, si pas de niveau d'accès défini, on considère comme PRIVATE
            return hasPrivateAccess(resource);
        }

        return switch (resource.getAccessLevel()) {
            case PUBLIC -> true;
            case PRIVATE -> hasPrivateAccess(resource);
            case ROLE_LIST -> hasRoleListAccess(resource);
        };
    }

    private boolean hasPrivateAccess(Resource resource) {
        String currentUser = authenticationService.getUser().orElse(null);
        if (currentUser == null) {
            return false;
        }
        return currentUser.equals(resource.getCreatedBy());
    }

    private boolean hasRoleListAccess(Resource resource) {
        if (resource.getAllowedRoles() == null || resource.getAllowedRoles().isEmpty()) {
            // Si aucun rôle n'est défini, on considère comme PRIVATE
            return hasPrivateAccess(resource);
        }

        // Vérifier si l'utilisateur a au moins un des rôles autorisés
        List<String> userAuthorities = authenticationService.getAuthorities();
        return resource.getAllowedRoles().stream()
                .anyMatch(userAuthorities::contains);
    }
}

