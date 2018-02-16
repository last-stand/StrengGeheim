package com.stegano.strenggeheim;

import android.app.Application;

public class SteganoGlobal extends Application {

    private String encryptionAlgo;
    private String hashingAlgo;


    public String getEncryptionAlgo() {
        return encryptionAlgo;
    }

    public void setEncryptionAlgo(String encryptionAlgo) {
        this.encryptionAlgo = encryptionAlgo;
    }

    public String getHashingAlgo() {
        return hashingAlgo;
    }

    public void setHashingAlgo(String hashingAlgo) {
        this.hashingAlgo = hashingAlgo;
    }
}
