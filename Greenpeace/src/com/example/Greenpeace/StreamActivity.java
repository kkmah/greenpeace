package com.example.Greenpeace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class StreamActivity extends FragmentActivity {

    ViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyAdapter(fragmentManager));
    }

    public void submit(View view) {
        Intent intent = new Intent(this, PetitionConfrimActivity.class);
        startActivity(intent);
    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
//        Log.d("VIVZ", "get Item is called "+i);
            if (i == 0) {
                fragment = new FragmentPetitions();
            }
            if (i == 1) {
                fragment = new FragmentStream();
            }
            if (i == 2) {
                fragment = new FragmentFeed();
            }
            return fragment;
        }

        @Override
        public int getCount() {
//        Log.d("VIVZ", "get Count is called");
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Tab 1";
            }
            if (position == 1) {
                return "Tab 2";
            }
            if (position == 2) {
                return "Tab 3";
            }
            return null;
        }
    }
}