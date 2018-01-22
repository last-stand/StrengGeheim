package com.stegano.strenggeheim.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.stegano.strenggeheim.R;

public class AboutUsActivity extends AppCompatActivity {
    private final static String aboutUsHtmlContent =
            "<body>" +
            "  <p style=\"{text-align: justify;}\">" +
            "   <b >About Project:</b><br>" +
            "\"Streng Geheim\" is a German phrase which means \"Top Secret\". This is an " +
            "open-source project which provides Steganography functionality which means concealing "+
            "a secret message, image, or file within another message, image, or file. This app "+
            "uses images for that." +
            "  </p>" +
            "<p style=\"{text-align: justify;}\">" +
            "This app provides AES, RC5, RC6 and TripleDES data encryption and SHA-1, SHA-256 and "+
            "MD-5 hashing algorithm for password protection. It uses XZ(LZMA2) data compression. "+
            "You can choose any encryption and hashing algorithm to protect your data. This app "+
            "needs  24 bit Bitmap(.bmp) image, don't worry app is already doing it for you." +
            "</p>" +
            "   <p style=\"{text-align: justify;}\">" +
            "   <b>About Us:</b><br>" +
            "    Nothing much, just like to do cool stuff. I will appreciate if people contribute "+
            "more to this project and help to make it better. By the way I don't like Batman." +
            "  </p>" +
            "  <p>" +
            "  Project Link: <br>" +
            "<a href=\"https://github.com/last-stand/StrengGeheim\">" +
                    "https://github.com/last-stand/StrengGeheim</a><br>" +
            "  More about Steganography: <br>" +
            " <a href=\"https://en.wikipedia.org/wiki/Steganography\">Steganography</a><br>" +
            " <a href=\"https://null-byte.wonderhowto.com/how-to/steganography-"+
                    "hide-secret-data-inside-image-audio-file-seconds-0180936/\">How it works?</a>"+
            "  </p>" +
            "</body>";

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        webView = findViewById(R.id.aboutUsContent);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(aboutUsHtmlContent, "text/html", "UTF-8");

    }

}
