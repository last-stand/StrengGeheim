package com.stegano.strenggeheim.utils.encrypt;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static String MODE_OF_OPERATION = "ECB";
    private static String PADDING_SCHEME = "PKCS5Padding" ;
    private static final byte[] SALT = { (byte) 0x28, (byte) 0x5F, (byte) 0x71, (byte) 0xC9,
            (byte) 0x1E, (byte) 0x35, (byte) 0x0A, (byte) 0x62 };
    private static HashMap<String, Integer> algoKeyLength;

    static {
        algoKeyLength = new HashMap<>();
        algoKeyLength.put("AES", 32);
        algoKeyLength.put("DESede", 24);
        algoKeyLength.put("RC5", 128);
        algoKeyLength.put("RC6", 128);
    }

    public static void setKey(String secret, String encryptionAlgo, String hashingAlgo)
            throws NoSuchAlgorithmException{
        key = secret.getBytes();
        MessageDigest sha = MessageDigest.getInstance(hashingAlgo);
        sha.update(SALT);
        byte[] digestOfPassword = sha.digest(key);
        int keyLength = algoKeyLength.get(encryptionAlgo);
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, keyLength);
        secretKey = new SecretKeySpec(keyBytes, encryptionAlgo);
    }

    public static String encrypt(String strDataToEncrypt, String secret, String encryptionAlgo,
                                 String hashingAlgo) throws Exception {
        setKey(secret,encryptionAlgo, hashingAlgo);
        Cipher aesCipher = Cipher.getInstance(encryptionAlgo + "/" + MODE_OF_OPERATION +
                "/" + PADDING_SCHEME);
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
        String strCipherText =  Base64.encodeToString(byteCipherText, Base64.DEFAULT);
        return strCipherText;
    }

    public static String decrypt(String cipherText, String secret, String encryptionAlgo,
                                 String hashingAlgo) throws Exception {
        setKey(secret, encryptionAlgo, hashingAlgo);
        Cipher aesCipher = Cipher.getInstance(encryptionAlgo + "/" + MODE_OF_OPERATION +
                "/" + PADDING_SCHEME);
        aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] byteCipherText = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] byteDecryptedText = aesCipher.doFinal(byteCipherText);
        return new String(byteDecryptedText);
    }
}
