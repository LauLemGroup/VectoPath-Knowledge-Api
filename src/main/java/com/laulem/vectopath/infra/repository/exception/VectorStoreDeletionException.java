package com.laulem.vectopath.infra.repository.exception;

import java.util.UUID;

public class VectorStoreDeletionException extends RuntimeException {

    public VectorStoreDeletionException(UUID resourceId, Throwable cause) {
        super("Failed to delete resource from vector store: " + resourceId, cause);
    }
}
