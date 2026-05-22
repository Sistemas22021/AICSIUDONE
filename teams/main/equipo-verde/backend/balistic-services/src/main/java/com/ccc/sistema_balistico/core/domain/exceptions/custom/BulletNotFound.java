package com.ccc.sistema_balistico.core.domain.exceptions.custom;

public class BulletNotFound extends RuntimeException {
    public BulletNotFound(String message) {
        super(message);
    }
}
