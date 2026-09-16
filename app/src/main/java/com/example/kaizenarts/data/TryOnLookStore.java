package com.example.kaizenarts.data;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** Stores captured AR try-on photos as JPEGs under the app's private files directory. */
public class TryOnLookStore {

    private static final String DIR_NAME = "try_on_looks";

    private static File dir(Context context) {
        File dir = new File(context.getFilesDir(), DIR_NAME);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static File save(Context context, Bitmap bitmap) {
        File file = new File(dir(context), "look_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out);
        } catch (Exception e) {
            return null;
        }
        return file;
    }

    public static List<File> listNewestFirst(Context context) {
        File[] files = dir(context).listFiles();
        List<File> list = new ArrayList<>();
        if (files != null) list.addAll(Arrays.asList(files));
        list.sort(Comparator.comparingLong(File::lastModified).reversed());
        return list;
    }
}
