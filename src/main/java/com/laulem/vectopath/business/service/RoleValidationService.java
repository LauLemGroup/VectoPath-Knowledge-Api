package com.laulem.vectopath.business.service;

import java.util.List;

public interface RoleValidationService {
    void validateAllowedRoles(List<String> allowedRoles);
}
