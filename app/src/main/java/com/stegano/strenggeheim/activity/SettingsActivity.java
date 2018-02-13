package com.stegano.strenggeheim.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stegano.strenggeheim.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
