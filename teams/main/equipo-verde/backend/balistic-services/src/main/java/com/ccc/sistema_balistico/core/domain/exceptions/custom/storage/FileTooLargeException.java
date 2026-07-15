package com.ccc.sistema_balistico.core.domain.exceptions.custom.storage;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException(String message) {
        super(message);
    }
}
