package com.stegano.strenggeheim.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stegano.strenggeheim.PasswordMissingException;
import com.stegano.strenggeheim.R;
import com.stegano.strenggeheim.utils.stego.Steganographer;

import static com.stegano.strenggeheim.Constants.DECODE_PROGRESS_MESSAGE;
import static com.stegano.strenggeheim.Constants.DECODE_PROGRESS_TITLE;
import static com.stegano.strenggeheim.Constants.DEFAULT_TEXT_MESSAGE;
import static com.stegano.strenggeheim.Constants.FILE_TYPE_IMAGE;
import static com.stegano.strenggeheim.Constants.GALLERY;
import static com.stegano.strenggeheim.Constants.MESSAGE_MISSING_PASSWORD;
import static com.stegano.strenggeheim.Constants.SECRET_DATA_KEY;

public class FragmentDecode extends Fragment {

    private ImageView decodeImage;
    private Button decodeButton;
    private Bitmap bmpImage;
    private TextView imageTextMessage;
    private TextView decodedText;
    private TextView passwordToDecode;
    private Button closeButton;
    private Button copyButton;
    private String decodedMessage;
    private boolean neededPassword;
    ProgressDialog progress;

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
        passwordToDecode = view.findViewById(R.id.passwordToDecode);

        initializeProgressDialog();

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
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.decode_text_dialog_layout);

                    decodedText = dialog.findViewById(R.id.decodedText);
                    decodedText.setMovementMethod(new ScrollingMovementMethod());
                    closeButton = dialog.findViewById(R.id.close_button);
                    copyButton = dialog.findViewById(R.id.copy_button);

                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            decodedMessage = "";
                            dialog.dismiss();
                        }
                    });

                    copyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isDecodedMessageExist()) {
                                showToastMessage(getString(R.string.nothing_to_copy));
                                return;
                            }
                            ClipboardManager clipboard = (ClipboardManager)
                                    getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(SECRET_DATA_KEY, decodedMessage);
                            clipboard.setPrimaryClip(clip);
                            showToastMessage(getString(R.string.copied_to_clipboard));
                        }
                    });

                    getDecodedText(dialog);

                }
            }
        });

        return view;
    }

    private void getDecodedText(final Dialog dialog) {
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                decode();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isDecodedMessageExist()) {
                            decodedText.setText(decodedMessage);
                            showToastMessage(getString(R.string.message_decoding_success));
                            dialog.show();
                        }
                        else {
                            decodedText.setText(DEFAULT_TEXT_MESSAGE);
                            if(neededPassword) {
                                showToastMessage(MESSAGE_MISSING_PASSWORD);
                                return;
                            }
                            showToastMessage(getString(R.string.error_decoding_failed));
                        }
                        progress.dismiss();
                    }
                });
            }
        }).start();
    }

    private void decode() {
        String password = passwordToDecode.getText().toString();
        try {
            if(isPasswordEntered(password)){
                decodedMessage = Steganographer.withInput(bmpImage)
                                .withPassword(password)
                                .decode()
                                .intoString();
            }
            else{
                decodedMessage = Steganographer.withInput(bmpImage).decode().intoString();
            }
        }
        catch (Exception e) {
            if(e instanceof PasswordMissingException){
                neededPassword = true;
            }
            decodedMessage = "";
        }
    }

    private boolean isPasswordEntered(String password) {
        return password != null && !password.isEmpty();
    }

    private void initializeProgressDialog(){
        progress = new ProgressDialog(getContext());
        progress.setTitle(DECODE_PROGRESS_TITLE);
        progress.setMessage(DECODE_PROGRESS_MESSAGE);
        progress.setCancelable(false);
    }

    private boolean isImageSelected() {
        return bmpImage != null;
    }

    private boolean isDecodedMessageExist(){
        return decodedMessage != null && !decodedMessage.isEmpty();
    }

    private void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType(FILE_TYPE_IMAGE);
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
        catch(Exception ex){
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

    private void showToastMessage(final String message){
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
