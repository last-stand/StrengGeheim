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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stegano.strenggeheim.BuildConfig;
import com.stegano.strenggeheim.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class FragmentEncode extends Fragment {
    private static final String MESSAGE_IMAGE_SAVED = "Image Saved!";;
    private static final String MESSAGE_FAILED = "Failed!";
    private static final String IMAGE_DIRECTORY = "/StrengGeheim";
    private static final int GALLERY = 0, CAMERA = 1;
    private Uri fileUri;
    TextView imageTextMessage;
    ImageView loadImage;

    public FragmentEncode() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA);
    }

    private Uri getOutputMediaFileUri() {
        try {
            return FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider",getOutputMediaFile());
        }
        catch (IOException ex){

        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encode, container, false);

        imageTextMessage = view.findViewById(R.id.imageTextMessage);
        loadImage =  view.findViewById(R.id.loadImage);

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
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
        if (requestCode == GALLERY && data != null) {
                try {
                    Bitmap bitmap = getBitmapFromData(data, getContext());
                    String path = saveImage(bitmap);
                    Log.println(Log.INFO, "Message", path);
                    Toast.makeText(getContext(), MESSAGE_IMAGE_SAVED, Toast.LENGTH_SHORT).show();
                    loadImage.setImageBitmap(bitmap);
                    imageTextMessage.setVisibility(View.INVISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), MESSAGE_FAILED, Toast.LENGTH_SHORT).show();
                }

        } else if (requestCode == CAMERA) {
            loadImage.setImageURI(fileUri);
            Toast.makeText(getContext(), MESSAGE_IMAGE_SAVED, Toast.LENGTH_SHORT).show();
            imageTextMessage.setVisibility(View.INVISIBLE);
        }
    }

    private Bitmap getBitmapFromData(Intent intent, Context context){
        Uri selectedImage = intent.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

    public String saveImage(Bitmap bmpImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmpImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        try {
            File mediaFile = getOutputMediaFile();
            FileOutputStream fo = new FileOutputStream(mediaFile);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{mediaFile.getPath()},
                    new String[]{"image/png"}, null);
            fo.close();

            return mediaFile.getAbsolutePath();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private File getOutputMediaFile() throws IOException {
        File encodeImageDirectory =
                new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        if (!encodeImageDirectory.exists()) {
            encodeImageDirectory.mkdirs();
        }
        String uniqueId = UUID.randomUUID().toString();
        File mediaFile = new File(encodeImageDirectory, uniqueId + ".png");
        mediaFile.createNewFile();
        return mediaFile;
    }
}
