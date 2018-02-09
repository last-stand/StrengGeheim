package com.stegano.strenggeheim.utils.stego;

import android.graphics.Bitmap;
import android.graphics.Color;

import static android.view.MotionEvent.ACTION_MASK;

public class BitmapEncoder {
    private static final String END_FLAG = "{^!EOF!^}";

    public static Bitmap encode(Bitmap inBitmap, String message) {
        message = message.concat(END_FLAG);
        return encodeByteArrayIntoBitmap(inBitmap, message);
    }

    public static byte[] decode(Bitmap inBitmap) {
        return decodeBitmapToByteArray(inBitmap);
    }

    private static Bitmap encodeByteArrayIntoBitmap(Bitmap inBitmap, String message) {
        Bitmap outBitmap = inBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int counter = 0;
        int width = inBitmap.getWidth();
        int height = inBitmap.getHeight();
        loop0:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Integer pixel = Integer.valueOf(inBitmap.getPixel(x, y));
                Integer red = Integer.valueOf(Color.red(pixel.intValue()));
                Integer green = Integer.valueOf(Color.green(pixel.intValue()));
                Integer blue = Integer.valueOf(Color.blue(pixel.intValue()));
                int ascii = message.charAt(counter);
                Integer hundreds = Integer.valueOf(ascii / 100);
                Integer tens = Integer.valueOf((ascii / 10) % 10);
                Integer ones = Integer.valueOf(ascii % 10);
                Integer newRed = Integer.valueOf((red.intValue()
                        - (red.intValue() % 10))
                        + hundreds.intValue());
                Integer newGreen = Integer.valueOf((green.intValue()
                        - (green.intValue() % 10))
                        + tens.intValue());
                Integer newBlue = Integer.valueOf((blue.intValue()
                        - (blue.intValue() % 10))
                        + ones.intValue());
                int overflowCorrectedRed = Integer.valueOf(correctOverflow(newRed.intValue()))
                        .intValue();
                int overflowCorrectedGreen = Integer.valueOf(correctOverflow(newGreen.intValue()))
                        .intValue();
                int overflowCorrectedBlue = Integer.valueOf(correctOverflow(newBlue.intValue()))
                        .intValue();
                outBitmap.setPixel(x, y, Color.rgb(overflowCorrectedRed, overflowCorrectedGreen,
                        overflowCorrectedBlue));
                counter++;
                if (counter == message.length()) {
                    break loop0;
                }
            }
        }
        return outBitmap;
    }

    private static int correctOverflow(int color) {
        if (color > ACTION_MASK) {
            return color - 2;
        }
        return color;
    }


    private static byte[] decodeBitmapToByteArray(Bitmap bitmap) {
        String message = "";
        int charCounter = 1;
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                char character = decodePixel(Integer.valueOf(bitmap.getPixel(x, y)));
                message = new StringBuilder(String.valueOf(message)).append(character).toString();
                if(message.endsWith(END_FLAG)){
                    return message.replace(END_FLAG, "").getBytes();
                };
                charCounter++;
            }
        }
        return message.getBytes();
    }

    private static char decodePixel(Integer pixel) {
        int pixelValueRed = (Integer.valueOf(Color.red(pixel.intValue())).intValue() % 10) * 100;
        int pixelValueGreen = (Integer.valueOf(Color.green(pixel.intValue())).intValue() % 10) * 10;
        int pixelValueBlue = (Integer.valueOf(Color.blue(pixel.intValue())).intValue() % 10);
        return Character.toChars(pixelValueRed + pixelValueGreen +pixelValueBlue)[0];
    }
}
