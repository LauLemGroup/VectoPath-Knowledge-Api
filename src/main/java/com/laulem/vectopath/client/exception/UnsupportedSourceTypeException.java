package com.laulem.vectopath.client.exception;

import com.laulem.vectopath.client.dto.CreateResourceRequest;

public class UnsupportedSourceTypeException extends RuntimeException {

    public UnsupportedSourceTypeException(CreateResourceRequest.SourceType sourceType) {
        super("Unsupported source type: " + sourceType);
    }
}

