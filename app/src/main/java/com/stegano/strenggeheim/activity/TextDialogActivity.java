package com.stegano.strenggeheim.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

import com.stegano.strenggeheim.R;

public class TextDialogActivity extends AppCompatActivity {
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_dialog_layout);

        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec=tabHost.newTabSpec("tag1");

        spec.setContent(R.id.encode_dialog_text_tab);
        spec.setIndicator("Edit Text");
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("tag2");
        spec.setContent(R.id.encode_dialog_browse_tab);
        spec.setIndicator("Browse");
        tabHost.addTab(spec);

    }
}
