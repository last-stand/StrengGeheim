package com.stegano.strenggeheim.utils.stego;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.stegano.strenggeheim.utils.encrypt.Encryptor;

import java.io.File;

import static com.stegano.strenggeheim.Constants.end;
import static com.stegano.strenggeheim.Constants.midWithOutPwd;
import static com.stegano.strenggeheim.Constants.midWithPwd;
import static com.stegano.strenggeheim.Constants.start;

public class Steganographer {

    private String key = "";
    private Bitmap inBitmap = null;
    private static String additionalInfo;

    public static Steganographer withInput(@NonNull String filePath) {
        Steganographer steg = new Steganographer();
        steg.setInputBitmap(BitmapFactory.decodeFile(filePath));
        return steg;
    }

    public static Steganographer withInput(@NonNull File file) {
        Steganographer steg = new Steganographer();
        steg.setInputBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        return steg;
    }

    public static Steganographer withInput(@NonNull Bitmap bitmap) {
        Steganographer steg = new Steganographer();
        steg.setInputBitmap(bitmap);
        return steg;
    }

    /**
     * @param bitmap - set the input bitmap to operate on
     */
    private void setInputBitmap(@NonNull Bitmap bitmap) {
        this.inBitmap = bitmap;
    }

    public Steganographer withPassword(@NonNull String key) {
        this.key = key;
        return this;
    }

    // DECODING
    public DecodedObject decode() throws Exception {
        return new DecodedObject(BitmapEncoder.decode(inBitmap));
    }

    // ENCODING

    public EncodedObject encode(@NonNull File file) throws Exception {

        // FIXME:
        if (1 == 1) {
            throw new RuntimeException("Not implemented yet");
        }

        return null;
    }

    public EncodedObject encode(@NonNull String inputString, String encryptionAlgo,
                                String hashingAlgo) throws Exception{
        setAdditionalInfo(encryptionAlgo, hashingAlgo);
        String encryptedString = Encryptor.encrypt(inputString, key, encryptionAlgo, hashingAlgo);
        encryptedString = additionalInfo + encryptedString;
        return encode(encryptedString.getBytes());
    }

    private void setAdditionalInfo(String encryptionAlgo, String hashingAlgo){
        String mid = key != "" ? midWithPwd : midWithOutPwd;
        additionalInfo = start + encryptionAlgo + mid + hashingAlgo + end;
        if(mid == midWithOutPwd){
            key = additionalInfo;
        }
    }

    public EncodedObject encode(@NonNull byte[] bytes) throws Exception {
        String message = "Not enough space in bitmap to hold data (max:";
        if (bytes.length>bytesAvaliableInBitmap()){
            throw new IllegalArgumentException(message + bytesAvaliableInBitmap()+")");
        }
        return new EncodedObject(BitmapEncoder.encode(inBitmap, bytes));
    }

    /**
     *
     * @return The bytes available to store in the bitmap
     */
    private int bytesAvaliableInBitmap() {
        if (inBitmap == null) return 0;
        return (inBitmap.getWidth() * inBitmap.getHeight())*3/8 - BitmapEncoder.HEADER_SIZE;
    }

}
