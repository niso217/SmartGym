package com.a.n.smartgym.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.R;
import com.daimajia.numberprogressbar.NumberProgressBar;


import java.util.ArrayList;

/**
 * Created by DAT on 9/1/2015.
 */
public class DynamicFragmentTab extends Fragment {

    private static final String TAG = DynamicFragmentTab.class.getSimpleName();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamictab, container, false);
        Bundle bundle = getArguments();
        getIDs(view);
        setEvents();

        NumberProgressBar progress2 = (NumberProgressBar) view.findViewById(R.id.number_progress_bar);
        progress2.setMax(100);
        progress2.setProgress(75);

        return view;
    }




    private void getIDs(View view) {
    }

    private void setEvents() {

    }
}
