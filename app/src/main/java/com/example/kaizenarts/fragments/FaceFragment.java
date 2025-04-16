package com.example.kaizenarts.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.kaizenarts.R;

public class FaceFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 101;

    private ValueCallback<Uri[]> mUploadMessage;
    private WebView webView;

    // Register for activity result (for file chooser)
    private final ActivityResultLauncher<Intent> fileChooserLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (mUploadMessage == null) return;

                Uri[] resultUris = null;
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            resultUris = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }

                mUploadMessage.onReceiveValue(resultUris);
                mUploadMessage = null;
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face, container, false);

        // Initialize WebView
        webView = view.findViewById(R.id.webview);

        // Check and request permissions
        checkPermissions();

        // Enable JavaScript in WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Allow camera access in WebView
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                mUploadMessage = filePathCallback;

                // Open gallery using ACTION_PICK
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                fileChooserLauncher.launch(Intent.createChooser(pickIntent, "Select Picture"));
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (request.getResources().length == 1 && request.getResources()[0].equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                    request.grant(request.getResources());
                }
            }
        });
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.makeupar.com/business/showcase/earrings");

        // Enable scrolling
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        return view;
    }

    private void checkPermissions() {
        // Check camera permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

        // Handle permissions for images and storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(requireContext(), "Gallery Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Gallery Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
