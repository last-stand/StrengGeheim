package com.stegano.strenggeheim.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.stegano.strenggeheim.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.stegano.strenggeheim.Constants.ERROR_FILE_CANT_OPEN;
import static com.stegano.strenggeheim.Constants.FILE_CHOOSER_TITLE;
import static com.stegano.strenggeheim.Constants.FILE_TYPE_TEXT;
import static com.stegano.strenggeheim.Constants.SECRET_DATA_KEY;
import static com.stegano.strenggeheim.Constants.TEXTFILE;
import static com.stegano.strenggeheim.Constants.TEXT_DIALOG_TAB1_INDICATOR;
import static com.stegano.strenggeheim.Constants.TEXT_DIALOG_TAB2_INDICATOR;

public class TextDialogActivity extends AppCompatActivity {
    TabHost tabHost;
    
    private Button browse;
    private Button cancel_button1;
    private Button cancel_button2;
    private Button ok_button1;
    private Button ok_button2;
    private TextView text_preview;
    private EditText textInput;
    private String secretText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encode_text_dialog_layout);

        browse = findViewById(R.id.browse_file_button);
        text_preview = findViewById(R.id.text_preview);
        cancel_button1 = findViewById(R.id.cancel_button);
        cancel_button2 = findViewById(R.id.cancel_button2);
        ok_button1 = findViewById(R.id.ok_button);
        ok_button2 = findViewById(R.id.ok_button2);
        textInput = findViewById(R.id.encode_dialog_edit_text);

        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec=tabHost.newTabSpec("tag1");
        spec.setContent(R.id.encode_dialog_text_tab);
        spec.setIndicator(TEXT_DIALOG_TAB1_INDICATOR);
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("tag2");
        spec.setContent(R.id.encode_dialog_browse_tab);
        spec.setIndicator(TEXT_DIALOG_TAB2_INDICATOR);
        tabHost.addTab(spec);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        cancel_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancel_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ok_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secretText = textInput.getText().toString();
                getSecretText();
            }
        });

        ok_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSecretText();
            }
        });

    }

    private void getSecretText() {
        if(secretText != null && !secretText.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra(SECRET_DATA_KEY, secretText);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(FILE_TYPE_TEXT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, FILE_CHOOSER_TITLE),
                    TEXTFILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.msg_install_file_manager,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filePath = null;
        try {
            switch (requestCode) {
                case TEXTFILE:
                    if (resultCode == RESULT_OK) {
                        Uri fileUri = data.getData();
                        filePath = fileUri.getPath();
                        secretText = readTextFromUri(fileUri);
                        text_preview.setText(secretText);
                    }
                    break;
            }
        }
        catch (Exception ex){
            Toast.makeText(this, ERROR_FILE_CANT_OPEN + filePath,
                    Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
}
