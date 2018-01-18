package com.stegano.strenggeheim;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.stegano.strenggeheim.utils.BitmapHelper;
import com.stegano.strenggeheim.utils.Steganographer;
import com.stegano.strenggeheim.fragments.*;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTab();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentEncode(), "Encode");
        adapter.addFragment(new FragmentDecode(), "Decode");
        viewPager.setAdapter(adapter);
    }

    private void setupTab() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Encode");
//        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_encode, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);
        tabOne.setSelected(true);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Decode");
//        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_encode, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    // Encode Invoker
    private void go(){
        new Thread(new Runnable() {
            @Override public void run() {
                try {
//                    Test.runTests();
                    attempEncoding();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void attempEncoding() throws Exception{
        String hiddenMessage = "Hello this is a hidden message";
        Bitmap bitmap = BitmapHelper.createTestBitmap(200, 200);
        Bitmap encodedBitmap = Steganographer.withInput(bitmap).encode(hiddenMessage).intoBitmap();
        String decodedMessage = Steganographer.withInput(encodedBitmap).decode().intoString();
        Log.d(getClass().getSimpleName(), "Decoded Message: " + decodedMessage);

    }
}
