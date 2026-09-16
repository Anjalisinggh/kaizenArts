package com.example.kaizenarts.tryon;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.util.List;

/**
 * CameraX {@link androidx.camera.core.ImageAnalysis.Analyzer} that runs ML Kit face
 * detection on the live feed and reports ear positions + head tilt in the coordinate
 * space of a target view, so {@link EarringOverlayView} can draw earrings that track
 * the wearer's real head movement, scale and rotation.
 */
public class FaceOverlayAnalyzer implements androidx.camera.core.ImageAnalysis.Analyzer {

    /** Ear positions and scale/rotation already mapped into the overlay view's coordinate space. */
    public static class Result {
        @Nullable public final PointF leftEar;
        @Nullable public final PointF rightEar;
        public final float earringWidthPx;
        public final float rollDegrees;

        Result(@Nullable PointF leftEar, @Nullable PointF rightEar, float earringWidthPx, float rollDegrees) {
            this.leftEar = leftEar;
            this.rightEar = rightEar;
            this.earringWidthPx = earringWidthPx;
            this.rollDegrees = rollDegrees;
        }
    }

    public interface Listener {
        void onResult(@Nullable Result result);
    }

    private final FaceDetector detector;
    private final int overlayWidth;
    private final int overlayHeight;
    private final boolean isFrontCamera;
    private final Listener listener;

    public FaceOverlayAnalyzer(int overlayWidth, int overlayHeight, boolean isFrontCamera, Listener listener) {
        this.overlayWidth = overlayWidth;
        this.overlayHeight = overlayHeight;
        this.isFrontCamera = isFrontCamera;
        this.listener = listener;

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .build();
        detector = FaceDetection.getClient(options);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage == null || overlayWidth == 0 || overlayHeight == 0) {
            imageProxy.close();
            return;
        }

        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        InputImage inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees);

        // ML Kit reports coordinates already in the rotated frame's space.
        boolean swapped = rotationDegrees == 90 || rotationDegrees == 270;
        int imageWidth = swapped ? mediaImage.getHeight() : mediaImage.getWidth();
        int imageHeight = swapped ? mediaImage.getWidth() : mediaImage.getHeight();

        detector.process(inputImage)
                .addOnSuccessListener(faces -> handleResult(faces, imageWidth, imageHeight))
                .addOnFailureListener(e -> listener.onResult(null))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void handleResult(List<Face> faces, int imageWidth, int imageHeight) {
        if (faces.isEmpty()) {
            listener.onResult(null);
            return;
        }

        // Largest face = closest / primary subject.
        Face face = faces.get(0);
        for (Face f : faces) {
            if (f.getBoundingBox().width() > face.getBoundingBox().width()) face = f;
        }

        float scale = Math.max((float) overlayWidth / imageWidth, (float) overlayHeight / imageHeight);
        float offsetX = (overlayWidth - imageWidth * scale) / 2f;
        float offsetY = (overlayHeight - imageHeight * scale) / 2f;

        PointF leftEar = mapLandmark(face, FaceLandmark.LEFT_EAR, face.getBoundingBox().left, scale, offsetX, offsetY);
        PointF rightEar = mapLandmark(face, FaceLandmark.RIGHT_EAR, face.getBoundingBox().right, scale, offsetX, offsetY);

        float boxWidthView = face.getBoundingBox().width() * scale;
        float earringWidthPx = boxWidthView * 0.24f;

        float roll = face.getHeadEulerAngleZ();
        float rollDegrees = isFrontCamera ? roll : -roll;

        listener.onResult(new Result(leftEar, rightEar, earringWidthPx, rollDegrees));
    }

    /**
     * Maps one ear landmark (or, if ML Kit didn't find it, a bounding-box fallback)
     * from raw image space into the overlay view's coordinate space, mirroring for
     * the front camera so it matches the mirrored preview the user sees.
     */
    private PointF mapLandmark(Face face, int landmarkType, float fallbackX, float scale,
                                float offsetX, float offsetY) {
        float rawX, rawY;
        FaceLandmark landmark = face.getLandmark(landmarkType);
        if (landmark != null) {
            rawX = landmark.getPosition().x;
            rawY = landmark.getPosition().y;
        } else {
            rawX = fallbackX;
            rawY = face.getBoundingBox().top + face.getBoundingBox().height() * 0.55f;
        }

        float viewX = rawX * scale + offsetX;
        float viewY = rawY * scale + offsetY;

        if (isFrontCamera) {
            viewX = overlayWidth - viewX;
        }

        return new PointF(viewX, viewY);
    }
}
