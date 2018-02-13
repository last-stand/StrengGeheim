package com.stegano.strenggeheim.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.stegano.strenggeheim.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
