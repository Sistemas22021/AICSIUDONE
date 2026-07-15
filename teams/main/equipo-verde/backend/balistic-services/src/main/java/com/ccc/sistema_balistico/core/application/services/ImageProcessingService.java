package com.ccc.sistema_balistico.core.application.services;

import com.ccc.sistema_balistico.core.application.dto.ExtractedFeatures;
import com.ccc.sistema_balistico.core.application.dto.MatchResult;
import org.opencv.core.Mat;

public interface ImageProcessingService {


    ExtractedFeatures extractFeatures(byte[] imageBytes);

    MatchResult matchFeatures(
        byte[] srcKeypoints, byte[] srcDescriptors, byte[] srcImgBytes,
        byte[] candKeypoints, byte[] candDescriptors, byte[] candImgBytes
    );

    byte[] serializeMat(Mat mat);

    Mat deserializeMat(byte[] data);
}
