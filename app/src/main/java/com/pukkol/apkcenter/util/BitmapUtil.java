package com.pukkol.apkcenter.util;

import android.graphics.Bitmap;

public class BitmapUtil {

    public static Bitmap resizeBitmap(Bitmap source, int maxHeight) {
        try {
            if (source.getHeight() <= maxHeight) { // if image already smaller than the required height
                return source;
            }

            double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
            int targetWidth = (int) (maxHeight * aspectRatio);

            return Bitmap.createScaledBitmap(source, targetWidth, maxHeight, false);
        }
        catch (Exception e)
        {
            return source;
        }
    }
}


