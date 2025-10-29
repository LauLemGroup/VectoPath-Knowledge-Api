package com.laulem.vectopath.client.exception;

import com.laulem.vectopath.client.dto.CreateResourceRequestDto;

public class UnsupportedSourceTypeException extends RuntimeException {

    public UnsupportedSourceTypeException(CreateResourceRequestDto.SourceType sourceType) {
        super("Unsupported source type: " + sourceType);
    }
}

