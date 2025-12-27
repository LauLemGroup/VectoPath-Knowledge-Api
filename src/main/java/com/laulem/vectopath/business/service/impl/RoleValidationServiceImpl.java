package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.exception.ParamException;
import com.laulem.vectopath.business.model.SecurityConfig;
import com.laulem.vectopath.business.service.AuthenticationService;
import com.laulem.vectopath.business.service.RoleValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class RoleValidationServiceImpl implements RoleValidationService {
    private final AuthenticationService authenticationService;
    private final SecurityConfig securityConfig;

    public RoleValidationServiceImpl(AuthenticationService authenticationService,
                                     SecurityConfig securityConfig) {
        this.authenticationService = authenticationService;
        this.securityConfig = securityConfig;
    }

    @Override
    public void validateAllowedRoles(List<String> allowedRoles) {
        if (CollectionUtils.isEmpty(allowedRoles)) {
            return;
        }

        List<String> userAuthorities = authenticationService.getAuthorities();
        if (userAuthorities.contains(securityConfig.getAdminRole())) {
            return;
        }

        List<String> notAffectableRoles = securityConfig.getNotAffectableRoles();
        boolean containsForbiddenRoles = allowedRoles.stream().anyMatch(notAffectableRoles::contains);
        if (containsForbiddenRoles) {
            throw new ParamException("FORBIDDEN_ROLES", "You cannot assign protected roles: " + String.join(", ", notAffectableRoles), "allowedRoles");
        }

        boolean containsUnauthorizedRoles = allowedRoles.stream().anyMatch(role -> !userAuthorities.contains(role));
        if (containsUnauthorizedRoles) {
            throw new ParamException("UNAUTHORIZED_ROLES", "You can only assign roles that you possess.", "allowedRoles");
        }
    }
}

