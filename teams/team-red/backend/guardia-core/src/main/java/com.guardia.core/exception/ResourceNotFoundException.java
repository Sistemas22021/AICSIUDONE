package com.guardia.core.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " con id " + id + " no encontrado.");
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
