package com.example.swapu.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ComUtils {

    public static Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    public static String getFormattedDate(Date createdAt) {
        Date currentDate = new Date();
        SimpleDateFormat formatDateInHr = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat formatDateSimple = new SimpleDateFormat("dd-MMM-yy");
//        String day = Integer.toString(createdAt.getDay());
//        String month = Integer.toString(createdAt.getMonth());
//        String year = Integer.toString(createdAt.getYear());
//        String hour = Integer.toString(createdAt.getHours());
//        String minute = Integer.toString(createdAt.getMinutes());

        long dayDifference = (currentDate.getTime() - createdAt.getTime()) / (1000 * 60 * 60 * 24);
        if (dayDifference < 1) {
            return formatDateInHr.format(createdAt);
        } else if (dayDifference == 1) {
            return "Yesterday";
        } else {
            return formatDateSimple.format(createdAt);
        }
    }

}
