package com.guardia.core.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envoltorio genérico de paginación para respuestas de listados.
 * Independiente del tipo de contenido para poder reutilizarse en otros
 * endpoints paginados del proyecto.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}