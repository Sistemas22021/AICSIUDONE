package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.ExtractedFeatures;
import com.ccc.sistema_balistico.core.application.dto.MatchResult;
import com.ccc.sistema_balistico.core.application.services.ImageProcessingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ImageProcessingServiceImplTest {

    private static ImageProcessingService service;
    private static byte[] testImageBytes;

    @BeforeAll
    static void setUp() throws IOException {
        service = new ImageProcessingServiceImpl();

        // Generate a synthetic image in-memory for testing
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = img.createGraphics();
        
        // Draw some visual patterns/textures for ORB to detect
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 200, 200);
        g.setColor(Color.BLACK);
        g.fillRect(30, 30, 50, 50);
        g.fillOval(100, 100, 60, 60);
        g.drawLine(10, 190, 190, 10);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        testImageBytes = baos.toByteArray();
    }

    @Test
    void testExtractFeaturesSuccess() {
        ExtractedFeatures features = service.extractFeatures(testImageBytes);

        assertNotNull(features);
        assertNotNull(features.descriptors());
        assertNotNull(features.keypoints());
        assertTrue(features.descriptors().length > 0, "Descriptors should not be empty");
        assertTrue(features.keypoints().length > 0, "Keypoints should not be empty");
    }

    @Test
    void testSerializationAndDeserialization() {
        ExtractedFeatures features = service.extractFeatures(testImageBytes);
        
        Mat descMat = service.deserializeMat(features.descriptors());
        assertNotNull(descMat);
        assertFalse(descMat.empty());
        assertEquals(32, descMat.cols(), "ORB descriptors should have 32 columns");

        byte[] reSerialized = service.serializeMat(descMat);
        assertArrayEquals(features.descriptors(), reSerialized, "Serialized byte arrays should match");

        descMat.release();
    }

    @Test
    void testMatchFeaturesIdenticalImage() {
        ExtractedFeatures features = service.extractFeatures(testImageBytes);

        MatchResult result = service.matchFeatures(
                features.keypoints(), features.descriptors(), testImageBytes,
                features.keypoints(), features.descriptors(), testImageBytes
        );

        assertNotNull(result);
        assertTrue(result.inliersCount() > 0, "Matches count should be greater than 0");
        assertNotNull(result.comparisonImageBase64(), "Comparison image should be generated");
    }
}
