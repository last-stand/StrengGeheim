package com.stegano.strenggeheim.utils.encrypt;

public enum EncryptionAlgo {
    AES("AES"),
    TDES("TripleDES"),
    RC5("RC5"),
    RC6("RC6");

    private String algoName;
    EncryptionAlgo(String algoName) {
        this.algoName = algoName;
    }

    public String getAlgoName() {
        return algoName;
    }
}
