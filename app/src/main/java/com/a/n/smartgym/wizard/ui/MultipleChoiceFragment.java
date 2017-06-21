/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.a.n.smartgym.wizard.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.a.n.smartgym.Adapter.GridViewAdapter;
import com.a.n.smartgym.Adapter.ImageItem;
import com.a.n.smartgym.Helpers.SharedPreferenceHelper;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.Objects.Muscle;
import com.a.n.smartgym.Objects.TrainingProgram;
import com.a.n.smartgym.R;
import com.a.n.smartgym.WizardActivity;
import com.a.n.smartgym.wizard.model.MultipleFixedChoicePage;
import com.a.n.smartgym.wizard.model.Page;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultipleChoiceFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private Context mContext;
    private String mKey;
    private List<String> mChoices;
    private Page mPage;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<String> selections;
    private static final String TAG = MultipleChoiceFragment.class.getSimpleName();


    public static MultipleChoiceFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        MultipleChoiceFragment fragment = new MultipleChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MultipleChoiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);


        MultipleFixedChoicePage fixedChoicePage = (MultipleFixedChoicePage) mPage;
        mChoices = new ArrayList<String>();
        for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
            mChoices.add(fixedChoicePage.getOptionAt(i));
        }



        selections = new ArrayList<>();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_muscle, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String val = ((ImageItem) parent.getItemAtPosition(position)).getTitle();
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                int selectedIndex = TrainingProgram.getInstance().getMainMusclesValue("sunday",val);
                if (selectedIndex > -1) {
                    item.setState(0);
                    selections.remove(val);
                } else {
                    item.setState(1);
                    selections.add(val);
                }
                gridAdapter.notifyDataSetChanged();
                mPage.getData().putStringArrayList(Page.SIMPLE_DATA_KEY, selections);
                TrainingProgram.getInstance().SetMainMuscle(selections);
                mPage.notifyDataChanged();
            }
        });

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        // Pre-select currently selected items.
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                ArrayList<String> selected = TrainingProgram.getInstance().getMainMuscles("sunday");
                for (int i = 0; i < mChoices.size(); i++) {
                    if (selected.contains(mChoices.get(i))) {
                        ImageItem item = (ImageItem) gridView.getItemAtPosition(i);
                        item.setState(1);
                        gridAdapter.selectedPositions.add(i);
                        selections.add(item.getTitle());
                        gridAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        mContext = activity;

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    private ArrayList<ImageItem> getData() {



        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        Enumeration e = ExercisesDB.getInstance().keys.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            int s = getResources().getIdentifier(key, "string", getContext().getPackageName());
            if (s != 0) {
                String result = getString(s);
                ImageItem im = new ImageItem(result, key);
                imageItems.add(im);
            }


        }

        return imageItems;
    }
}