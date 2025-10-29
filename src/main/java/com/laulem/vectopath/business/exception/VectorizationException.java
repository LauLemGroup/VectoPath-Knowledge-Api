package com.laulem.vectopath.business.exception;

public class VectorizationException extends RuntimeException {

    public VectorizationException(String resourceName, Throwable cause) {
        super("Error during vectorization of resource: " + resourceName, cause);
    }
}
