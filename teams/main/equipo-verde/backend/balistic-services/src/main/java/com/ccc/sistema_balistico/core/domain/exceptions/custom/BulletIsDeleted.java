package com.ccc.sistema_balistico.core.domain.exceptions.custom;

public class BulletIsDeleted extends RuntimeException {
    public BulletIsDeleted() {
        super("Sorry the selected bullet was deleted");
    }
}
