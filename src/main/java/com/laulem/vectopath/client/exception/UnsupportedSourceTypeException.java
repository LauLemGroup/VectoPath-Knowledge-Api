package com.laulem.vectopath.client.exception;

public class UnsupportedSourceTypeException extends RuntimeException {

    public UnsupportedSourceTypeException(String sourceType) {
        super("Unsupported source type: " + sourceType);
    }
}

