package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.ExtractedFeatures;
import com.ccc.sistema_balistico.core.application.dto.MatchResult;
import com.ccc.sistema_balistico.core.application.services.ImageProcessingService;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {

    static {
        nu.pattern.OpenCV.loadShared();
    }

    private static final int MAX_FEATURES = 500;
    private static final float SCALE_FACTOR = 1.2f;
    private static final int N_LEVELS = 8;
    private static final double HAMMING_THRESHOLD = 50.0;
    private static final double LOWE_RATIO = 0.75;

    @Override
    public ExtractedFeatures extractFeatures(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            return new ExtractedFeatures(new byte[0], new byte[0]);
        }

        MatOfByte rawBytes = new MatOfByte(imageBytes);
        Mat original = null;
        Mat preprocessed = null;
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        Mat descriptors = new Mat();
        ORB orb;

        try {
            original = Imgcodecs.imdecode(rawBytes, Imgcodecs.IMREAD_GRAYSCALE);
            if (original.empty()) {
                return new ExtractedFeatures(new byte[0], new byte[0]);
            }

            preprocessed = preprocess(original);

            orb = ORB.create(MAX_FEATURES, SCALE_FACTOR, N_LEVELS);
            orb.detectAndCompute(preprocessed, new Mat(), keypoints, descriptors);

            byte[] keypointsSerialized = serializeMat(keypoints);
            byte[] descriptorsSerialized = serializeMat(descriptors);

            return new ExtractedFeatures(keypointsSerialized, descriptorsSerialized);

        } finally {
            rawBytes.release();
            if (original != null) original.release();
            if (preprocessed != null) preprocessed.release();
            keypoints.release();
            descriptors.release();
        }
    }

    @Override
    public MatchResult matchFeatures(
        byte[] srcKeypoints, byte[] srcDescriptors, byte[] srcImgBytes,
        byte[] candKeypoints, byte[] candDescriptors, byte[] candImgBytes
    ) {
        if (srcDescriptors == null || srcDescriptors.length == 0 ||
            candDescriptors == null || candDescriptors.length == 0) {
            return new MatchResult(0, null);
        }

        Mat srcDescMat = deserializeMat(srcDescriptors);
        Mat candDescMat = deserializeMat(candDescriptors);
        Mat srcKeyMat = deserializeMat(srcKeypoints);
        Mat candKeyMat = deserializeMat(candKeypoints);

        MatOfKeyPoint srcKeypointsMat = new MatOfKeyPoint(srcKeyMat);
        MatOfKeyPoint candKeypointsMat = new MatOfKeyPoint(candKeyMat);

        DescriptorMatcher matcher;
        List<MatOfDMatch> knnMatches = new ArrayList<>();
        MatOfPoint2f srcPoints = new MatOfPoint2f();
        MatOfPoint2f dstPoints = new MatOfPoint2f();
        Mat mask = new Mat();
        Mat homography = null;
        Mat srcImg = null;
        Mat candImg = null;
        Mat comparisonMat = null;
        MatOfByte encodedComparison = null;
        MatOfDMatch inliersMatchesMat = null;

        try {
            matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            matcher.knnMatch(srcDescMat, candDescMat, knnMatches, 2);

            List<DMatch> goodMatches = new ArrayList<>();
            for (MatOfDMatch match : knnMatches) {
                DMatch[] dMatches = match.toArray();
                if (dMatches.length >= 2) {
                    DMatch m1 = dMatches[0];
                    DMatch m2 = dMatches[1];
                    if (m1.distance < LOWE_RATIO * m2.distance && m1.distance <= HAMMING_THRESHOLD) {
                        goodMatches.add(m1);
                    }
                }
                match.release();
            }

            int inliersCount = 0;
            List<DMatch> inliersMatches = new ArrayList<>();

            if (goodMatches.size() >= 4) {
                List<Point> srcPointsList = new ArrayList<>();
                List<Point> dstPointsList = new ArrayList<>();
                List<KeyPoint> srcKeypointsList = srcKeypointsMat.toList();
                List<KeyPoint> candKeypointsList = candKeypointsMat.toList();

                for (DMatch match : goodMatches) {
                    srcPointsList.add(srcKeypointsList.get(match.queryIdx).pt);
                    dstPointsList.add(candKeypointsList.get(match.trainIdx).pt);
                }

                srcPoints.fromList(srcPointsList);
                dstPoints.fromList(dstPointsList);

                homography = Calib3d.findHomography(srcPoints, dstPoints, Calib3d.RANSAC, 3.0, mask);

                if (!homography.empty()) {
                    byte[] maskData = new byte[(int) mask.total()];
                    mask.get(0, 0, maskData);
                    for (int i = 0; i < maskData.length; i++) {
                        if (maskData[i] != 0) {
                            inliersCount++;
                            inliersMatches.add(goodMatches.get(i));
                        }
                    }
                }
            } else {
                inliersCount = goodMatches.size();
                inliersMatches.addAll(goodMatches);
            }

            String comparisonBase64 = null;
            if (srcImgBytes != null && srcImgBytes.length > 0 &&
                candImgBytes != null && candImgBytes.length > 0) {
                
                MatOfByte srcByteMat = new MatOfByte(srcImgBytes);
                MatOfByte candByteMat = new MatOfByte(candImgBytes);
                try {
                    srcImg = Imgcodecs.imdecode(srcByteMat, Imgcodecs.IMREAD_COLOR);
                    candImg = Imgcodecs.imdecode(candByteMat, Imgcodecs.IMREAD_COLOR);

                    if (!srcImg.empty() && !candImg.empty()) {
                        comparisonMat = new Mat();
                        inliersMatchesMat = new MatOfDMatch();
                        inliersMatchesMat.fromList(inliersMatches);

                        Features2d.drawMatches(
                            srcImg, srcKeypointsMat, candImg, candKeypointsMat,
                            inliersMatchesMat, comparisonMat
                        );

                        encodedComparison = new MatOfByte();
                        Imgcodecs.imencode(".png", comparisonMat, encodedComparison);
                        comparisonBase64 = Base64.getEncoder().encodeToString(encodedComparison.toArray());
                    }
                } finally {
                    srcByteMat.release();
                    candByteMat.release();
                }
            }

            return new MatchResult(inliersCount, comparisonBase64);

        } finally {
            srcDescMat.release();
            candDescMat.release();
            srcKeyMat.release();
            candKeyMat.release();
            srcKeypointsMat.release();
            candKeypointsMat.release();
            srcPoints.release();
            dstPoints.release();
            mask.release();
            if (homography != null) homography.release();
            if (srcImg != null) srcImg.release();
            if (candImg != null) candImg.release();
            if (comparisonMat != null) comparisonMat.release();
            if (encodedComparison != null) encodedComparison.release();
            if (inliersMatchesMat != null) inliersMatchesMat.release();
        }
    }

    private Mat preprocess(Mat src) {
        Mat claheResult = new Mat();
        Mat blurred = new Mat();

        CLAHE clahe = Imgproc.createCLAHE(4.0, new Size(8, 8));
        clahe.apply(src, claheResult);

        Imgproc.GaussianBlur(claheResult, blurred, new Size(5, 5), 0);

        claheResult.release();
        clahe.collectGarbage();

        return blurred;
    }

    @Override
    public byte[] serializeMat(Mat mat) {
        if (mat == null || mat.empty()) {
            return new byte[0];
        }

        int rows = mat.rows();
        int cols = mat.cols();
        int type = mat.type();

        if (type == CvType.CV_8UC1) {
            int dataSize = (int) (mat.total() * mat.elemSize());
            byte[] data = new byte[dataSize];
            mat.get(0, 0, data);

            ByteBuffer buffer = ByteBuffer.allocate(13 + dataSize);
            buffer.put((byte) 0);
            buffer.putInt(rows);
            buffer.putInt(cols);
            buffer.putInt(type);
            buffer.put(data);
            byte[] result = new byte[buffer.position()];
            System.arraycopy(buffer.array(), 0, result, 0, result.length);
            return result;
        }
        else {
            MatOfKeyPoint keypointsMat = new MatOfKeyPoint(mat);
            List<KeyPoint> list = keypointsMat.toList();

            int totalKeypoints = list.size();
            ByteBuffer buffer = ByteBuffer.allocate(5 + (totalKeypoints * 28));
            buffer.put((byte) 1);
            buffer.putInt(totalKeypoints);

            for (KeyPoint kp : list) {
                buffer.putFloat((float) kp.pt.x);
                buffer.putFloat((float) kp.pt.y);
                buffer.putFloat(kp.size);
                buffer.putFloat(kp.angle);
                buffer.putFloat(kp.response);
                buffer.putInt(kp.octave);
                buffer.putInt(kp.class_id);
            }
            byte[] result = new byte[buffer.position()];
            System.arraycopy(buffer.array(), 0, result, 0, result.length);
            return result;
        }
    }

    @Override
    public Mat deserializeMat(byte[] data) {
        if (data == null || data.length == 0) {
            return new Mat();
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte marker = buffer.get();

        if (marker == 0) {
            int rows = buffer.getInt();
            int cols = buffer.getInt();
            int type = buffer.getInt();

            byte[] matData = new byte[data.length - 13];
            buffer.get(matData);

            Mat mat = new Mat(rows, cols, type);
            mat.put(0, 0, matData);
            return mat;
        }
        else {
            int totalKeypoints = buffer.getInt();
            List<KeyPoint> list = new ArrayList<>(totalKeypoints);

            for (int i = 0; i < totalKeypoints; i++) {
                float x = buffer.getFloat();
                float y = buffer.getFloat();
                float size = buffer.getFloat();
                float angle = buffer.getFloat();
                float response = buffer.getFloat();
                int octave = buffer.getInt();
                int classId = buffer.getInt();

                list.add(new KeyPoint(x, y, size, angle, response, octave, classId));
            }

            MatOfKeyPoint keypointsMat = new MatOfKeyPoint();
            keypointsMat.fromList(list);
            return keypointsMat;
        }
    }
}
