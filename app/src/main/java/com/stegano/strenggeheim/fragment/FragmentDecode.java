package com.stegano.strenggeheim.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stegano.strenggeheim.R;
import com.stegano.strenggeheim.utils.stego.Steganographer;

public class FragmentDecode extends Fragment {

    private static final int GALLERY = 0;
    private ImageView decodeImage;
    private Button decodeButton;
    private Bitmap bmpImage;
    private TextView imageTextMessage;
    private TextView decodedText;
    private Button closeButton;
    private Button copyButton;
    private String decodedMessage;

    public FragmentDecode(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decode, container, false);

        decodeButton = view.findViewById(R.id.decodeButton);
        imageTextMessage = view.findViewById(R.id.imageTextDecodeMessage);
        decodeImage = view.findViewById(R.id.loadDecodeImage);

        decodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
            }
        });

        decodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isImageSelected()){
                    showToastMessage(getString(R.string.error_no_image));
                    return;
                }
                else {
                    decodeTextFromImage();
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.decode_text_dialog_layout);

                    decodedText = dialog.findViewById(R.id.decodedText);
                    decodedText.setText(decodedMessage);
                    closeButton = dialog.findViewById(R.id.close_button);
                    copyButton = dialog.findViewById(R.id.copy_button);

                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    copyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager)
                                    getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("SecretText", decodedMessage);
                            clipboard.setPrimaryClip(clip);
                            showToastMessage(getString(R.string.copied_to_clipboard));
                        }
                    });

                    dialog.show();
                }
            }
        });

        return view;
    }

    private boolean isImageSelected() {
        return decodeImage != null;
    }

    private void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        try {
            if (requestCode == GALLERY && data != null) {
                bmpImage = getBitmapFromData(data, getContext());
                decodeImage.setImageBitmap(bmpImage);
                showToastMessage(getString(R.string.message_image_selected));
                imageTextMessage.setVisibility(View.INVISIBLE);
            }
        }
        catch(Exception ex){}
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

    private void showToastMessage(final String message){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void decodeTextFromImage() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    decodedMessage = Steganographer.withInput(bmpImage).decode().intoString();
                    Log.d(getClass().getSimpleName(), "Decoded Message: " + decodedMessage);
                    showToastMessage(getString(R.string.message_encoding_success));
                } catch (Exception e) {
                    showToastMessage(getString(R.string.error_decoding_failed));
                }
            }
        }).start();
    }

}
