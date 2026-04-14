package com.laulem.vectopath.business.service;

import java.util.List;
import java.util.Optional;

public interface AuthenticationService {
    String DEFAULT_UNKNOWN_USERNAME = "anonymous";

    String getCurrentUser();

    Optional<String> findCurrentUser();

    List<String> getAuthorities();
}
