package com.ccc.sistema_balistico.core.domain.exceptions.custom.storage;

public class FileAlreadyExistsException extends RuntimeException {
    public FileAlreadyExistsException(String message) {
        super(message);
    }
}
