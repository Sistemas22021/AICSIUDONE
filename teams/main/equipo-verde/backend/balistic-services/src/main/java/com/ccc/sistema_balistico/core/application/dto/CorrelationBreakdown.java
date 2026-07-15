package com.ccc.sistema_balistico.core.application.dto;

public record CorrelationBreakdown(
    boolean striaeMatched,
    boolean twistMatched,
    boolean percussionMatched,
    boolean brandMatched,
    int validMatchesCount,
    String comparisonImageBase64
) {}
