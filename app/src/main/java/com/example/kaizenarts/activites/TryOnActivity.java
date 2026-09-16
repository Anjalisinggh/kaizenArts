package com.example.kaizenarts.activites;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.EarringStyleAdapter;
import com.example.kaizenarts.tryon.EarringOverlayView;
import com.example.kaizenarts.tryon.FaceOverlayAnalyzer;
import com.example.kaizenarts.tryon.TryOnCaptureHolder;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The hero AR Try-On screen: a live camera feed with earrings drawn on the
 * wearer's actual ear positions, tracked in real time via ML Kit face detection.
 */
public class TryOnActivity extends AppCompatActivity {

    private PreviewView previewView;
    private EarringOverlayView overlayView;
    private TextView statusText;
    private android.view.View faceGuide;

    private ProcessCameraProvider cameraProvider;
    private ExecutorService analysisExecutor;
    private boolean useFrontCamera = true;
    private int selectedEarringRes = R.drawable.ic_earring_drop;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    findViewById(R.id.permission_prompt).setVisibility(android.view.View.GONE);
                    bindCameraUseCases();
                } else {
                    Toast.makeText(this, "Camera permission is required to try earrings on", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_on);

        previewView = findViewById(R.id.camera_preview);
        overlayView = findViewById(R.id.earring_overlay);
        statusText = findViewById(R.id.try_on_status);
        faceGuide = findViewById(R.id.face_guide);

        setupProductChip();
        setupEarringCarousel();

        findViewById(R.id.try_on_close).setOnClickListener(v -> finish());
        findViewById(R.id.try_on_flip).setOnClickListener(v -> {
            useFrontCamera = !useFrontCamera;
            bindCameraUseCases();
        });
        findViewById(R.id.try_on_capture).setOnClickListener(v -> capture());
        findViewById(R.id.try_on_info).setOnClickListener(v -> {
            LinearLayout chip = findViewById(R.id.try_on_product_chip);
            chip.setVisibility(chip.getVisibility() == android.view.View.VISIBLE ? android.view.View.GONE : android.view.View.VISIBLE);
        });

        analysisExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            bindCameraUseCases();
        } else {
            findViewById(R.id.permission_prompt).setVisibility(android.view.View.VISIBLE);
            findViewById(R.id.grant_permission_btn).setOnClickListener(v -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA));
        }
    }

    private void setupProductChip() {
        LinearLayout chip = findViewById(R.id.try_on_product_chip);
        String name = getIntent().getStringExtra("product_name");
        if (name == null) {
            chip.setVisibility(android.view.View.GONE);
            return;
        }
        ((TextView) findViewById(R.id.try_on_product_name)).setText(name);
        int price = getIntent().getIntExtra("product_price", 0);
        ((TextView) findViewById(R.id.try_on_product_price)).setText(String.format("₹ %d", price));
    }

    private void setupEarringCarousel() {
        RecyclerView carousel = findViewById(R.id.earring_carousel);
        carousel.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        List<Integer> styles = Arrays.asList(
                R.drawable.ic_earring_drop,
                R.drawable.ic_earring_chandbali,
                R.drawable.ic_earring_hoop,
                R.drawable.ic_earring_stud);
        carousel.setAdapter(new EarringStyleAdapter(this, styles, resId -> {
            selectedEarringRes = resId;
            overlayView.setEarringDrawable(resId);
        }));
        overlayView.setEarringDrawable(selectedEarringRes);
    }

    private void bindCameraUseCases() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                cameraProvider = future.get();
                cameraProvider.unbindAll();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector selector = useFrontCamera
                        ? CameraSelector.DEFAULT_FRONT_CAMERA
                        : CameraSelector.DEFAULT_BACK_CAMERA;

                overlayView.post(() -> {
                    int w = overlayView.getWidth();
                    int h = overlayView.getHeight();
                    if (w == 0 || h == 0) return;

                    ImageAnalysis analysis = new ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();
                    analysis.setAnalyzer(analysisExecutor, new FaceOverlayAnalyzer(w, h, useFrontCamera, result -> runOnUiThread(() -> {
                        overlayView.updateFace(result);
                        boolean found = result != null;
                        faceGuide.setVisibility(found ? android.view.View.GONE : android.view.View.VISIBLE);
                        statusText.setText(found ? "You're ready ✨" : "Position your face inside the frame");
                    })));

                    try {
                        cameraProvider.unbindAll();
                        cameraProvider.bindToLifecycle(this, selector, preview, analysis);
                    } catch (Exception e) {
                        Toast.makeText(this, "Unable to start camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void capture() {
        Bitmap previewBitmap = previewView.getBitmap();
        if (previewBitmap == null) {
            Toast.makeText(this, "Hold still and try again", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap composite = previewBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(composite);
        float scaleX = (float) composite.getWidth() / overlayView.getWidth();
        float scaleY = (float) composite.getHeight() / overlayView.getHeight();
        canvas.save();
        canvas.scale(scaleX, scaleY);
        overlayView.draw(canvas);
        canvas.restore();

        TryOnCaptureHolder.set(composite);

        Intent intent = new Intent(this, TryOnResultActivity.class);
        intent.putExtra("product_name", getIntent().getStringExtra("product_name"));
        intent.putExtra("product_price", getIntent().getIntExtra("product_price", 0));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraProvider != null) cameraProvider.unbindAll();
        if (analysisExecutor != null) analysisExecutor.shutdown();
    }
}
