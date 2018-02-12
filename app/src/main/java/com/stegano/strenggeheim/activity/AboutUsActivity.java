package com.stegano.strenggeheim.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.stegano.strenggeheim.R;

import static com.stegano.strenggeheim.Constants.ASSET_ABOUT_US;

public class AboutUsActivity extends AppCompatActivity {
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        webView = findViewById(R.id.aboutUsContent);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(ASSET_ABOUT_US);

    }

}
