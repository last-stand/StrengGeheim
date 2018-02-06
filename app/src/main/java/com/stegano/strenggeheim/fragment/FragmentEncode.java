package com.stegano.strenggeheim.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.stegano.strenggeheim.utils.stego.Steganographer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class FragmentEncode extends Fragment {
    private static final String IMAGE_DIRECTORY = "/StrengGeheim";
    private static final int GALLERY = 0, CAMERA = 1, TEXTFILE = 2;
    private static int requestType;
    private File imageFile;
    private Bitmap bmpImage;
    private String secretText;
    TextView imageTextMessage;
    ImageView loadImage;
    Button textInputButton;
    Button encodeButton;

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
            return FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", imageFile);
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
                    loadImage.setImageResource(android.R.color.transparent);
                    imageTextMessage.setVisibility(View.VISIBLE);
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
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera",
                "Cancel"
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
                bmpImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                loadImage.setImageBitmap(bmpImage);
                showToastMessage(getString(R.string.message_image_selected));
                imageTextMessage.setVisibility(View.INVISIBLE);
            }
            else if (requestCode == TEXTFILE && data != null){
                secretText = data.getExtras().getString("popup_data");
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
        return BitmapFactory.decodeFile(picturePath);
    }

    private String encodeImageFromGallery() {
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            bmpImage.compress(Bitmap.CompressFormat.JPEG, 60, byteOutputStream);
            Bitmap newBmp = BitmapFactory.decodeStream(new ByteArrayInputStream(byteOutputStream.toByteArray()));
            Steganographer.withInput(newBmp).encode(secretText).intoFile(imageFile);
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{imageFile.getPath()},
                    new String[]{"image/jpg"}, null);
            showToastMessage(getString(R.string.message_encoding_success));
            return imageFile.getAbsolutePath();
        }
        catch (Exception ex){
            deleteFile();
            showToastMessage(getString(R.string.error_encoding_failed));
        }
        finally {
            reset();
        }
        return "";
    }

    private void encodeImageFromCamera() {
        try {
            Steganographer.withInput(imageFile).encode(secretText).intoFile(imageFile);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            getContext().sendBroadcast(mediaScanIntent);
            showToastMessage(getString(R.string.message_encoding_success));
        }
        catch (Exception ex){
            deleteFile();
            showToastMessage(getString(R.string.error_encoding_failed));
        }
        finally {
            reset();
        }
    }

    private File getOutputMediaFile() throws IOException {
        File encodeImageDirectory =
                new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        if (!encodeImageDirectory.exists()) {
            encodeImageDirectory.mkdirs();
        }
        String uniqueId = UUID.randomUUID().toString();
        File mediaFile = new File(encodeImageDirectory, uniqueId + ".jpg");
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
        secretText = null;
        requestType = -1;
    }

    private void showToastMessage(final String message){
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
