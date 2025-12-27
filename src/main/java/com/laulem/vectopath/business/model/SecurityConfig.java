package com.laulem.vectopath.business.model;

import java.util.List;

public interface SecurityConfig {
    String getAdminRole();
    List<String> getNotAffectableRoles();
}
