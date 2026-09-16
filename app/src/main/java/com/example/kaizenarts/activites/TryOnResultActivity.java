package com.example.kaizenarts.activites;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.kaizenarts.R;
import com.example.kaizenarts.data.TryOnLookStore;
import com.example.kaizenarts.tryon.TryOnCaptureHolder;

import java.io.File;

public class TryOnResultActivity extends AppCompatActivity {

    private Bitmap capturedLook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_on_result);

        capturedLook = TryOnCaptureHolder.get();
        ImageView resultImage = findViewById(R.id.result_image);
        if (capturedLook != null) {
            resultImage.setImageBitmap(capturedLook);
        } else {
            Toast.makeText(this, "Couldn't load your look", Toast.LENGTH_SHORT).show();
        }

        String productName = getIntent().getStringExtra("product_name");
        if (productName != null) {
            findViewById(R.id.result_product_row).setVisibility(android.view.View.VISIBLE);
            ((TextView) findViewById(R.id.result_product_name)).setText(productName);
            ((TextView) findViewById(R.id.result_product_price))
                    .setText(String.format("₹ %d", getIntent().getIntExtra("product_price", 0)));
        }

        findViewById(R.id.result_close).setOnClickListener(v -> finish());

        findViewById(R.id.action_try_another).setOnClickListener(v -> finish());

        findViewById(R.id.action_save).setOnClickListener(v -> {
            if (capturedLook == null) return;
            File saved = TryOnLookStore.save(this, capturedLook);
            TextView toast = findViewById(R.id.result_saved_toast);
            if (saved != null) {
                toast.setVisibility(android.view.View.VISIBLE);
                toast.postDelayed(() -> toast.setVisibility(android.view.View.GONE), 1800);
            } else {
                Toast.makeText(this, "Couldn't save this look", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.action_share).setOnClickListener(v -> shareLook());

        findViewById(R.id.action_add_to_bag).setOnClickListener(v ->
                Toast.makeText(this, "Added to bag", Toast.LENGTH_SHORT).show());
    }

    private void shareLook() {
        if (capturedLook == null) return;
        File file = TryOnLookStore.save(this, capturedLook);
        if (file == null) {
            Toast.makeText(this, "Couldn't prepare this look for sharing", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share your look"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TryOnCaptureHolder.clear();
    }
}
