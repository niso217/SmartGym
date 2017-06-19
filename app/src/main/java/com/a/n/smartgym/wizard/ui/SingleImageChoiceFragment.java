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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.a.n.smartgym.Adapter.GridViewAdapter;
import com.a.n.smartgym.Adapter.ImageItem;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.R;
import com.a.n.smartgym.wizard.model.MultipleFixedChoicePage;
import com.a.n.smartgym.wizard.model.Page;
import com.a.n.smartgym.wizard.model.SingleFixedChoicePage;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SingleImageChoiceFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private List<String> mChoices;
    private Page mPage;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<String> selections;

    public static SingleImageChoiceFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        SingleImageChoiceFragment fragment = new SingleImageChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SingleImageChoiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        SingleFixedChoicePage fixedChoicePage = (SingleFixedChoicePage) mPage;
        mChoices = new ArrayList<String>();
        for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
            mChoices.add(fixedChoicePage.getOptionAt(i));
        }
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
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                mPage.getData().putString(Page.SIMPLE_DATA_KEY,
                        item.getTitle());
                mPage.notifyDataChanged();
            }
        });

        // Pre-select currently selected items.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String selection = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
                for (int i = 0; i < mChoices.size(); i++) {
                    if (mChoices.get(i).equals(selection)) {
                        gridView.setItemChecked(i, true);
                        gridAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

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