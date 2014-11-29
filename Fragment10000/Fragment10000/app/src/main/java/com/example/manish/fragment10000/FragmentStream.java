package com.example.manish.fragment10000;

/**
 * Created by Manish on 29-11-2014.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Vivz on 10/25/13.
 */
public class FragmentStream extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.streaming, container, false);
    }
}