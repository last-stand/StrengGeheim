package com.stegano.strenggeheim.utils.encrypt;

public enum HashingAlgo {
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    MD5("MD5");


    private String algoName;
    HashingAlgo(String algoName) {
        this.algoName = algoName;
    }

    public String getAlgoName() {
        return algoName;
    }
}
