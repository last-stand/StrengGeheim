package com.stegano.strenggeheim.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.stegano.strenggeheim.R;

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences sharedPref;
    ListPreference hashingPreference;
    ListPreference encryptionPreference;

    private String hashingPref;
    private String encryptionPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        final SharedPreferences.Editor editor = sharedPref.edit();
        hashingPref = getString(R.string.list_prefs_key_hashing);
        encryptionPref = getString(R.string.list_prefs_key_encryption);

        hashingPreference = (ListPreference) findPreference(hashingPref);
        encryptionPreference = (ListPreference) findPreference(encryptionPref);

        hashingPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                editor.putString(hashingPref, (String) newValue);
                return editor.commit();
            }
        });

        encryptionPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                editor.putString(encryptionPref, (String) newValue);
                return editor.commit();
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
    }

}
