package com.ccc.sistema_balistico.core.application.dto;

public record MatchResult(
    int inliersCount,
    String comparisonImageBase64
) {}
