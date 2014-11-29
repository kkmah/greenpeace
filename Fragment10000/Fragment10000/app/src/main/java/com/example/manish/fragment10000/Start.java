package com.example.manish.fragment10000;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Joost on 11/29/2014.
 */
public class Start extends Activity {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.start);
        }

        public void play() {
            Intent intent = new Intent(this, MyActivity.class);
            startActivity(intent);
        }
}