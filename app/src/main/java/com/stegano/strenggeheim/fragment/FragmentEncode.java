package com.stegano.strenggeheim.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stegano.strenggeheim.BuildConfig;
import com.stegano.strenggeheim.R;
import com.stegano.strenggeheim.activity.TextDialogActivity;
import com.stegano.strenggeheim.utils.imageUtil.BitmapHelper;
import com.stegano.strenggeheim.utils.stego.Steganographer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.stegano.strenggeheim.Constants.CAMERA;
import static com.stegano.strenggeheim.Constants.ENCODE_PROGRESS_MESSAGE;
import static com.stegano.strenggeheim.Constants.ENCODE_PROGRESS_TITLE;
import static com.stegano.strenggeheim.Constants.FILE_TYPE_IMAGE;
import static com.stegano.strenggeheim.Constants.GALLERY;
import static com.stegano.strenggeheim.Constants.IMAGE_DIRECTORY;
import static com.stegano.strenggeheim.Constants.IMAGE_HEIGHT;
import static com.stegano.strenggeheim.Constants.IMAGE_WIDTH;
import static com.stegano.strenggeheim.Constants.PICTURE_DIALOG_ITEM1;
import static com.stegano.strenggeheim.Constants.PICTURE_DIALOG_ITEM2;
import static com.stegano.strenggeheim.Constants.PICTURE_DIALOG_ITEM3;
import static com.stegano.strenggeheim.Constants.PICTURE_DIALOG_TITLE;
import static com.stegano.strenggeheim.Constants.PNG;
import static com.stegano.strenggeheim.Constants.SECRET_DATA_KEY;
import static com.stegano.strenggeheim.Constants.TEXTFILE;

public class FragmentEncode extends Fragment {
    private static int requestType;
    private File imageFile;
    private Bitmap bmpImage;
    private String secretText;
    private String hashingAlgo;
    private String encryptionAlgo;
    TextView imageTextMessage;
    TextView passwordToEncode;
    ImageView loadImage;
    Button textInputButton;
    Button encodeButton;
    ProgressDialog progress;

    public FragmentEncode() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType(FILE_TYPE_IMAGE);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA);
    }

    private Uri getOutputMediaFileUri() {
        try {
            imageFile = getOutputMediaFile();
            return FileProvider.getUriForFile(getActivity(),
                    BuildConfig.APPLICATION_ID + ".provider", imageFile);
        }
        catch (IOException ex){
            showToastMessage(getString(R.string.error_fail_message));
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encode, container, false);

        imageTextMessage = view.findViewById(R.id.imageTextMessage);
        loadImage =  view.findViewById(R.id.loadImage);
        textInputButton = view.findViewById(R.id.textInputButton);
        encodeButton = view.findViewById(R.id.encodeButton);
        passwordToEncode = view.findViewById(R.id.passwordToEncode);

        initializeProgressDialog();

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        textInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), TextDialogActivity.class);
                startActivityForResult(intent, TEXTFILE);
            }
        });

        encodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isTextExist()) {
                    showToastMessage(getString(R.string.error_no_text));
                    return;
                }
                else if(!isImageExist()) {
                    showToastMessage(getString(R.string.error_no_image));
                    return;
                }
                else {
                    switch (requestType) {
                        case GALLERY:
                            encodeImageFromGallery();
                            break;
                        case CAMERA:
                            encodeImageFromCamera();
                            break;
                    }
                }
            }
        });
        return view;
    }

    private boolean isImageExist() {
        return imageFile != null && imageFile.exists();
    }

    private boolean isTextExist() {
        return secretText != null && !secretText.isEmpty();
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle(PICTURE_DIALOG_TITLE);
        String[] pictureDialogItems = {
                PICTURE_DIALOG_ITEM1,
                PICTURE_DIALOG_ITEM2,
                PICTURE_DIALOG_ITEM3
        };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                galleryIntent();
                                break;
                            case 1:
                                cameraIntent();
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        try {
            if (requestCode == GALLERY && data != null) {
                requestType = GALLERY;
                bmpImage = getBitmapFromData(data, getContext());
                imageFile = getOutputMediaFile();
                loadImage.setImageBitmap(bmpImage);
                showToastMessage(getString(R.string.message_image_selected));
                imageTextMessage.setVisibility(View.INVISIBLE);
            }
            else if (requestCode == CAMERA) {
                requestType = CAMERA;
                bmpImage = compressBitmap(imageFile.getAbsolutePath());
                loadImage.setImageBitmap(bmpImage);
                showToastMessage(getString(R.string.message_image_selected));
                imageTextMessage.setVisibility(View.INVISIBLE);
            }
            else if (requestCode == TEXTFILE && data != null){
                secretText = data.getExtras().getString(SECRET_DATA_KEY);
            }
        } catch (Exception ex) {
            showToastMessage(getString(R.string.error_fail_message));
        }
    }

    private Bitmap getBitmapFromData(Intent intent, Context context){
        Uri selectedImage = intent.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver()
                .query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return compressBitmap(picturePath);
    }

    private Bitmap compressBitmap(String picturePath){
        return BitmapHelper.decodeSampledBitmap(picturePath, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    private void encodeImageFromGallery() {
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                encode();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isImageExist()) {
                            MediaScannerConnection.scanFile(getContext(),
                                    new String[]{imageFile.getPath()},
                                    new String[]{FILE_TYPE_IMAGE}, null);
                            showToastMessage(getString(R.string.message_encoding_success));
                        }
                        reset();
                    }
                });
            }
        }).start();
    }

    private void encodeImageFromCamera() {
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                encode();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isImageExist()) {
                            Intent mediaScanIntent =
                                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(imageFile);
                            mediaScanIntent.setData(contentUri);
                            getContext().sendBroadcast(mediaScanIntent);
                            showToastMessage(getString(R.string.message_encoding_success));
                        }
                        else {
                            showToastMessage(getString(R.string.error_encoding_failed));
                        }
                        reset();
                    }
                });
            }
        }).start();
    }

    private void encode() {
        try {
            getAlgoNamesFromSharedPreferences();
            String password = passwordToEncode.getText().toString();
            if(isPasswordExist(password)) {
                Steganographer.withInput(bmpImage)
                        .withPassword(password)
                        .encode(secretText, encryptionAlgo, hashingAlgo)
                        .intoFile(imageFile);
            }
            else {
                Steganographer.withInput(bmpImage)
                        .encode(secretText, encryptionAlgo, hashingAlgo)
                        .intoFile(imageFile);
            }
        }
        catch (Exception e) {
            deleteFile();
        }
    }

    private void getAlgoNamesFromSharedPreferences() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultHashingAlgo = getString(R.string.list_prefs_default_hashing);
        String defaultEncryptionAlgo = getString(R.string.list_prefs_default_encryption);
        String hashingPref = getString(R.string.list_prefs_key_hashing);
        String encryptionPref = getString(R.string.list_prefs_key_encryption);
        hashingAlgo = sharedPref.getString(hashingPref, defaultHashingAlgo);
        encryptionAlgo = sharedPref.getString(encryptionPref, defaultEncryptionAlgo);
    }

    private boolean isPasswordExist(String password) {
        return password != null && !password.isEmpty();
    }

    private void initializeProgressDialog(){
        progress = new ProgressDialog(getContext());
        progress.setTitle(ENCODE_PROGRESS_TITLE);
        progress.setMessage(ENCODE_PROGRESS_MESSAGE);
        progress.setCancelable(false);
    }

    private File getOutputMediaFile() throws IOException {
        File encodeImageDirectory =
                new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        if (!encodeImageDirectory.exists()) {
            encodeImageDirectory.mkdirs();
        }
        String uniqueId = UUID.randomUUID().toString();
        File mediaFile = new File(encodeImageDirectory, uniqueId + PNG);
        mediaFile.createNewFile();
        return mediaFile;
    }

    private void deleteFile(){
        if (isImageExist()) {
            imageFile.delete();
        }
    }

    private void reset(){
        secretText = "";
        imageFile = null;
        bmpImage = null;
        requestType = -1;
        progress.dismiss();
        loadImage.setImageResource(android.R.color.transparent);
        imageTextMessage.setVisibility(View.VISIBLE);
        passwordToEncode.setText("");
    }

    private void showToastMessage(final String message){
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
