package com.example.triglobal.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.triglobal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FreeLeadsFragment extends Fragment {

    public static final String TAG = FreeLeadsFragment.class.getSimpleName();

    public FreeLeadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_free_leads, container, false);
    }

}
