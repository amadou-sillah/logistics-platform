package com.logistics.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entity, String field, String value) {
        super(String.format("%s not found with %s = %s", entity, field, value));
    }
}
