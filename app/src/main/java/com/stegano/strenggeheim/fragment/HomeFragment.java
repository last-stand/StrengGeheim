package com.stegano.strenggeheim.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.stegano.strenggeheim.R;

public class HomeFragment extends Fragment {
    private TabLayout tabLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        tabLayout = getActivity().findViewById(R.id.tabs);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
    }
}
