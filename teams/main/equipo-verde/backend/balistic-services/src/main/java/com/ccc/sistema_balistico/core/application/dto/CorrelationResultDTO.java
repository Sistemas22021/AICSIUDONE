package com.ccc.sistema_balistico.core.application.dto;

public record CorrelationResultDTO(
    Long idBullet,
    String caseFile,
    String manufacturer,
    double matchScore,
    CorrelationBreakdown breakdown
) {}
