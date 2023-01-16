package com.example.success;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Assets {

    @NonNull
    public static String getTessDataPath(@NonNull Context context) {
        // We need to return folder that contains the "tessdata" folder,
        // which is in this sample directly the app's files dir
        System.out.println(context.getFilesDir().getAbsolutePath());
        return context.getFilesDir().getAbsolutePath();
    }

    @NonNull
    public static String getLanguage() {
        return "chi_sim";
    }

    @NonNull
    public static File getImageFile(@NonNull Context context) {
        return new File(context.getFilesDir(), "sample1.jpg");
    }

    @Nullable
    public static Bitmap getImageBitmap(@NonNull Context context) {
        return BitmapFactory.decodeFile(getImageFile(context).getAbsolutePath());
    }

    public static void extractAssets(@NonNull Context context) {
        AssetManager am = context.getAssets();

        File tessDir = new File(getTessDataPath(context), "tessdata");
        if (!tessDir.exists()) {
            tessDir.mkdir();
        }

        File engFile = new File(tessDir, "eng.traineddata");
        File chiFile = new File(tessDir, "chi_sim.traineddata");
        File txtFIle = new File(tessDir, "test.txt");
        copyFile(am, "eng.traineddata", engFile);
        copyFile(am, "chi_sim.traineddata", chiFile);
        copyFile(am, "test.txt", txtFIle);
        File sample = new File(context.getFilesDir(), "sample1.jpg");
        copyFile(am, "sample1.jpg", sample);
    }

    private static void copyFile(@NonNull AssetManager am, @NonNull String assetName,
                                 @NonNull File outFile) {
        try (
                InputStream in = am.open(assetName);
                OutputStream out = new FileOutputStream(outFile)
        ) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
