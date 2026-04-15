package com.laulem.vectopath.client.config;

import com.laulem.vectopath.business.exception.DownloadInterruptedException;
import com.laulem.vectopath.business.exception.HttpDownloadException;
import com.laulem.vectopath.business.exception.NotFoundException;
import com.laulem.vectopath.business.exception.ParamException;
import com.laulem.vectopath.business.exception.ResourceDeletionException;
import com.laulem.vectopath.business.exception.VectorizationException;
import com.laulem.vectopath.client.dto.GeneralResponseException;
import com.laulem.vectopath.client.exception.UnsupportedFileExtensionException;
import com.laulem.vectopath.client.exception.UnsupportedSourceTypeException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ParamException.class)
    public ResponseEntity<GeneralResponseException> handleParamException(ParamException ex, HttpServletRequest request) {
        logger.warn("ParamException: code={}, field={}, path={}, message={}",
            ex.getCode(), ex.getField(), request.getRequestURI(), ex.getMessage());
        GeneralResponseException response = new GeneralResponseException(
            ex.getCode(),
            ex.getMessage(),
            buildPath(request),
            ex.getField(),
            ex.getInformation()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GeneralResponseException> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        logger.warn("NotFoundException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        GeneralResponseException response = new GeneralResponseException(
            "NOT_FOUND",
            ex.getMessage(),
            buildPath(request),
            null,
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnsupportedSourceTypeException.class)
    public ResponseEntity<GeneralResponseException> handleUnsupportedSourceTypeException(UnsupportedSourceTypeException ex, HttpServletRequest request) {
        logger.warn("UnsupportedSourceTypeException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        GeneralResponseException response = new GeneralResponseException(
            "UNSUPPORTED_SOURCE_TYPE",
            ex.getMessage(),
            buildPath(request),
            null,
            null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnsupportedFileExtensionException.class)
    public ResponseEntity<GeneralResponseException> handleUnsupportedFileExtensionException(UnsupportedFileExtensionException ex, HttpServletRequest request) {
        logger.warn("UnsupportedFileExtensionException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        GeneralResponseException response = new GeneralResponseException(
            "UNSUPPORTED_FILE_EXTENSION",
            ex.getMessage(),
            buildPath(request),
            null,
            null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(VectorizationException.class)
    public ResponseEntity<GeneralResponseException> handleVectorizationException(VectorizationException ex, HttpServletRequest request) {
        logger.error("VectorizationException: path={}, message={}", request.getRequestURI(), ex.getMessage(), ex);
        GeneralResponseException response = new GeneralResponseException(
            "VECTORIZATION_ERROR",
            ex.getMessage(),
            buildPath(request),
            null,
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(HttpDownloadException.class)
    public ResponseEntity<GeneralResponseException> handleHttpDownloadException(HttpDownloadException ex, HttpServletRequest request) {
        logger.error("HttpDownloadException: path={}, message={}", request.getRequestURI(), ex.getMessage(), ex);
        GeneralResponseException response = new GeneralResponseException(
            "HTTP_DOWNLOAD_ERROR",
            ex.getMessage(),
            buildPath(request),
            null,
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DownloadInterruptedException.class)
    public ResponseEntity<GeneralResponseException> handleDownloadInterruptedException(DownloadInterruptedException ex, HttpServletRequest request) {
        logger.error("DownloadInterruptedException: path={}, message={}", request.getRequestURI(), ex.getMessage(), ex);
        GeneralResponseException response = new GeneralResponseException(
            "DOWNLOAD_INTERRUPTED",
            ex.getMessage(),
            buildPath(request),
            null,
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ResourceDeletionException.class)
    public ResponseEntity<GeneralResponseException> handleVectorStoreDeletionException(ResourceDeletionException ex, HttpServletRequest request) {
        logger.error("VectorStoreDeletionException: path={}, message={}", request.getRequestURI(), ex.getMessage(), ex);
        GeneralResponseException response = new GeneralResponseException(
            "VECTOR_STORE_DELETION_ERROR",
            ex.getMessage(),
            buildPath(request),
            null,
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponseException> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected exception: path={}, message={}", request.getRequestURI(), ex.getMessage(), ex);
        GeneralResponseException response = new GeneralResponseException(
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            buildPath(request),
            null,
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GeneralResponseException> handleGenericException(NoResourceFoundException ex, HttpServletRequest request) {
        logger.error("Endpoint not found: path={}, message={}", request.getRequestURI(), ex.getMessage(), ex);
        GeneralResponseException response = new GeneralResponseException(
            "NOT_FOUND",
            "Endpoint not found",
            buildPath(request),
            null,
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    private String buildPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}

