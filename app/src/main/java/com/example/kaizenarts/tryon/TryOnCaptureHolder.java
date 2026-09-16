package com.example.kaizenarts.tryon;

import android.graphics.Bitmap;

/** Passes the composited AR capture from TryOnActivity to TryOnResultActivity in-process. */
public class TryOnCaptureHolder {
    private static volatile Bitmap latestCapture;

    public static void set(Bitmap bitmap) {
        latestCapture = bitmap;
    }

    public static Bitmap get() {
        return latestCapture;
    }

    public static void clear() {
        latestCapture = null;
    }
}
