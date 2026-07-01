package com.ccc.sistema_balistico.core.application.dto;

public record ExtractedFeatures(
    byte[] keypoints,
    byte[] descriptors
) {}
