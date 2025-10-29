package com.laulem.vectopath.infra.repository.exception;

public class VectorStoreAdditionException extends RuntimeException {

    public VectorStoreAdditionException(String resourceName, Throwable cause) {
        super("Error adding resource to vector store: " + resourceName, cause);
    }
}
