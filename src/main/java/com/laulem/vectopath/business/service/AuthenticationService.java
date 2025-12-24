package com.laulem.vectopath.business.service;

import java.util.List;
import java.util.Optional;

public interface AuthenticationService {
    Optional<String> getUser();
    List<String> getAuthorities();
    boolean isAuthenticated();
}
