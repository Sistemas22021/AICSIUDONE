package com.ccc.sistema_balistico.core.domain.exceptions.custom.storage;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String message) {
        super(message);
    }
}
