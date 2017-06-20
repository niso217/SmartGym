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
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.repo.MuscleRepo;
import com.a.n.smartgym.wizard.model.AbstractWizardModel;
import com.a.n.smartgym.wizard.model.ModelCallbacks;
import com.a.n.smartgym.wizard.model.MultipleFixedChoicePage;
import com.a.n.smartgym.wizard.model.MultipleSubChoicePage;
import com.a.n.smartgym.wizard.model.Page;
import com.a.n.smartgym.wizard.model.ReviewItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubMuscleChoiceFragment extends Fragment implements ModelCallbacks {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private Context mContext;
    private String mKey;
    private List<String> mChoices;
    private Page mPage;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<String> selections;
    private static final String TAG = SubMuscleChoiceFragment.class.getSimpleName();
    private AbstractWizardModel mWizardModel;
    private List<ReviewItem> mCurrentReviewItems;


    public static SubMuscleChoiceFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        SubMuscleChoiceFragment fragment = new SubMuscleChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SubMuscleChoiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        ((MultipleSubChoicePage) mPage).setChoices(new String[] {"1","2","3"});

        MultipleSubChoicePage fixedChoicePage = (MultipleSubChoicePage) mPage;
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
        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, getData2("'arms'"));
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                int selectedIndex = gridAdapter.selectedPositions.indexOf(position);
                if (selectedIndex > -1) {
                    item.setState(0);
                    gridAdapter.selectedPositions.remove(selectedIndex);
                    selections.remove(((ImageItem) parent.getItemAtPosition(position)).getTitle());
                } else {
                    item.setState(1);
                    gridAdapter.selectedPositions.add(position);
                    selections.add(((ImageItem) parent.getItemAtPosition(position)).getTitle());
                }
                gridAdapter.notifyDataSetChanged();
                mPage.getData().putStringArrayList(Page.SIMPLE_DATA_KEY, selections);
                mPage.notifyDataChanged();
            }
        });

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        // Pre-select currently selected items.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> selectedItems = mPage.getData().getStringArrayList(
                        Page.SIMPLE_DATA_KEY);
                if (selectedItems == null || selectedItems.size() == 0) {
                    return;
                }

                Set<String> selectedSet = new HashSet<String>(selectedItems);
                for (int i = 0; i < mChoices.size(); i++) {
                    if (selectedSet.contains(mChoices.get(i))) {
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
        mWizardModel = mCallbacks.onGetModel();
        mWizardModel.registerListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        mWizardModel.unregisterListener(this);


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

    @Override
    public void onPageDataChanged(Page changedPage) {
        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
        for (Page page : mWizardModel.getCurrentPageSequence()) {
            page.getReviewItems(reviewItems);
        }

        mCurrentReviewItems = reviewItems;

        String[] parts = mCurrentReviewItems.get(1).getDisplayValue().replaceAll("\\s+","").split(",");
        String word="";
        for (int i = 0; i < parts.length; i++) {
            word += "'"+parts[i]+"'";
            if (i!=parts.length-1)
                word += ",";
        }


        if (gridAdapter != null) {
            gridAdapter.clear();
            gridAdapter.addAll(getData2(word));
            gridAdapter.notifyDataSetChanged();
            gridView.invalidateViews();

        }
    }

    @Override
    public void onPageTreeChanged() {

    }

    private ArrayList<ImageItem> getData2(String main) {

        if (main.equals("")) return new ArrayList<ImageItem>();


        MuscleRepo muscleRepo = new MuscleRepo();
        List<Muscle> exname = muscleRepo.getSubMuscle(main,"");

        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        for (int i = 0; i < exname.size(); i++) {
            imageItems.add(new ImageItem(exname.get(i).getImage(), exname.get(i).getName()));
        }

        return imageItems;

    }
}