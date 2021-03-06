package com.stegano.strenggeheim;

public interface Constants {
    String IMAGE_DIRECTORY = "/StrengGeheim";
    int GALLERY = 0;
    int CAMERA = 1;
    int TEXTFILE = 2;
    String FILE_TYPE_TEXT = "text/*";
    String FILE_TYPE_IMAGE = "image/*";
    int IMAGE_WIDTH = 1280;
    int IMAGE_HEIGHT = 800;

    String DEFAULT_TEXT_MESSAGE = "Nothing to show";
    String FILE_CHOOSER_TITLE = "Select a File to Upload";
    String PNG = ".png";

    String TAG_HOME = "home";
    String TAB_ENCODE_TITLE = "Encode";
    String TAB_DECODE_TITLE = "Decode";

    String ENCODE_PROGRESS_TITLE = "Encoding";
    String ENCODE_PROGRESS_MESSAGE = "Please wait while encoding...";
    String DECODE_PROGRESS_TITLE = "Decoding";
    String DECODE_PROGRESS_MESSAGE = "Please wait while decoding...";

    String SECRET_DATA_KEY = "secret_data";

    String TEXT_DIALOG_TAB1_INDICATOR = "Edit Text";
    String TEXT_DIALOG_TAB2_INDICATOR = "Browse";
    String ERROR_FILE_CANT_OPEN = "Unable to open file ";

    String PICTURE_DIALOG_TITLE = "Select Action";
    String PICTURE_DIALOG_ITEM1 = "Select photo from gallery";
    String PICTURE_DIALOG_ITEM2 = "Capture photo from camera";
    String PICTURE_DIALOG_ITEM3 = "Cancel";

    String ASSET_ABOUT_US = "file:///android_asset/about_us.html";

    String midWithPwd = "!0π";
    String midWithOutPwd = "!0Ω";
    String end = "eƒ<";

    String MESSAGE_MISSING_PASSWORD = "Please enter password, this is password protected file";
    int MIN_PASSWORD_LENGTH = 8;
    String ERROR_SHORT_PASSWORD = "Password must have minimum 8 characters";
}
