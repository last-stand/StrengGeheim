package com.stegano.strenggeheim;

import static com.stegano.strenggeheim.Constants.MESSAGE_MISSING_PASSWORD;

public class PasswordMissingException extends Exception{
    public PasswordMissingException()
    {
        super(MESSAGE_MISSING_PASSWORD);
    }

}
