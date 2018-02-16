package com.stegano.strenggeheim.utils.encrypt;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static String ALGORITHM_NAME = "AES" ;
    private static String MODE_OF_OPERATION = "ECB";
    private static String PADDING_SCHEME = "PKCS5Padding" ;

    public static void setKey(String secret, String hashingAlgorithm) throws NoSuchAlgorithmException{
        key = secret.getBytes();
        MessageDigest sha = MessageDigest.getInstance(hashingAlgorithm);
        byte[] digestOfPassword = sha.digest(key);
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, 32);
        secretKey = new SecretKeySpec(keyBytes, ALGORITHM_NAME);
    }

    public static String encrypt(String secret,String hashingAlgorithm, String strDataToEncrypt) throws Exception {
         setKey(secret, hashingAlgorithm);
        Cipher aesCipher = Cipher.getInstance(ALGORITHM_NAME + "/" + MODE_OF_OPERATION + "/" + PADDING_SCHEME);
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
        String strCipherText =  Base64.encodeToString(byteCipherText, Base64.DEFAULT);
        return strCipherText;
    }

    public static String decrypt(String secret,String hashingAlgorithm, String cipherText) throws Exception {
        setKey(secret, hashingAlgorithm);
        Cipher aesCipher = Cipher.getInstance(ALGORITHM_NAME + "/" + MODE_OF_OPERATION + "/" + PADDING_SCHEME);
        aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] byteCipherText = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] byteDecryptedText = aesCipher.doFinal(byteCipherText);
        return new String(byteDecryptedText);
    }
}
