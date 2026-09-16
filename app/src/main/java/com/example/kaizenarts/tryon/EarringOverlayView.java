package com.example.kaizenarts.tryon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * Draws the selected earring drawable pinned to the live ear landmark positions
 * reported by {@link FaceOverlayAnalyzer}, following head movement, scale and tilt.
 */
public class EarringOverlayView extends View {

    private Bitmap earringBitmap;
    private final Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    @Nullable
    private volatile FaceOverlayAnalyzer.Result result;

    public EarringOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setEarringDrawable(com.example.kaizenarts.R.drawable.ic_earring_drop);
    }

    public void setEarringDrawable(@DrawableRes int resId) {
        android.graphics.drawable.Drawable drawable = AppCompatResources.getDrawable(getContext(), resId);
        if (drawable == null) return;
        int w = 240, h = (int) (240f * drawable.getIntrinsicHeight() / Math.max(1, drawable.getIntrinsicWidth()));
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        earringBitmap = bmp;
        postInvalidate();
    }

    public void updateFace(@Nullable FaceOverlayAnalyzer.Result newResult) {
        this.result = newResult;
        postInvalidate();
    }

    public boolean hasFace() {
        return result != null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        FaceOverlayAnalyzer.Result r = result;
        if (r == null || earringBitmap == null) return;

        drawEarring(canvas, r.leftEar, r.earringWidthPx, r.rollDegrees);
        drawEarring(canvas, r.rightEar, r.earringWidthPx, r.rollDegrees);
    }

    private void drawEarring(Canvas canvas, @Nullable PointF earPoint, float targetWidthPx, float rollDegrees) {
        if (earPoint == null || earringBitmap == null) return;

        float scale = targetWidthPx / earringBitmap.getWidth();
        Matrix matrix = new Matrix();
        // Center the bitmap horizontally on the ear point; let it hang downward from the earlobe.
        matrix.postTranslate(-earringBitmap.getWidth() / 2f, 0);
        matrix.postScale(scale, scale);
        matrix.postRotate(rollDegrees, 0, 0);
        matrix.postTranslate(earPoint.x, earPoint.y);

        canvas.drawBitmap(earringBitmap, matrix, bitmapPaint);
    }
}
