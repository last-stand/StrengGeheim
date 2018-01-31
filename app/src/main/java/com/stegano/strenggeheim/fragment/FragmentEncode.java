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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class FragmentEncode extends Fragment {
    private static final String IMAGE_DIRECTORY = "/StrengGeheim";
    private static final int GALLERY = 0, CAMERA = 1, TEXTFILE = 2;
    private static int requestType;
    private File imageFile;
    private Bitmap sourceBitmap;
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
                if(secretText != null && !secretText.isEmpty()){
                    try {
                        switch (requestType) {
                            case GALLERY:
                                saveImage(sourceBitmap, imageFile);
                                break;
                            case CAMERA:
                                saveImage(imageFile);
                                break;
                        }
                        showToastMessage(getString(R.string.message_encoding_success));
                        secretText = "";
                    }
                    catch (Exception ex){
                        showToastMessage(getString(R.string.error_encoding_failed));
                    }
                }
                else {
                    showToastMessage(getString(R.string.error_no_text));
                }
            }
        });
        return view;
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
                sourceBitmap = getBitmapFromData(data, getContext());
                imageFile = getOutputMediaFile();
                loadImage.setImageBitmap(sourceBitmap);
                // saveImage(bitmap, imageFile);
                showToastMessage(getString(R.string.message_image_selected));
                imageTextMessage.setVisibility(View.INVISIBLE);
            }
            else if (requestCode == CAMERA) {
                requestType = CAMERA;
                // saveImage(imageFile);
                sourceBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                loadImage.setImageBitmap(sourceBitmap);
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

    private String saveImage(Bitmap bmpImage, File mediaFile)  throws IOException{
        Bitmap newBmpImage = bmpImage.copy(Bitmap.Config.ARGB_8888, true);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        newBmpImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        FileOutputStream fo = new FileOutputStream(mediaFile);
        fo.write(bytes.toByteArray());
        MediaScannerConnection.scanFile(getContext(),
                new String[]{mediaFile.getPath()},
                new String[]{"image/jpg"}, null);
        fo.close();

        return mediaFile.getAbsolutePath();
    }

    private void saveImage(File mediaImage) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(mediaImage);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
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

    private void showToastMessage(String message){
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
