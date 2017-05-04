package com.a.n.smartgym;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.a.n.smartgym.Adapter.GridViewAdapter;
import com.a.n.smartgym.Adapter.ImageItem;
import com.a.n.smartgym.Objects.ExercisesDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ratan on 7/29/2015.
 */
public class MuscleFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private String UUID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            UUID = getArguments().getString("uuid");
        }

        View rootFragment = inflater.inflate(R.layout.fragment_muscle, null);

        gridView = (GridView) rootFragment.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                Bundle bundle = new Bundle();
                bundle.putString("group",ExercisesDB.getInstance().keys.get(position));
                bundle.putString("uuid",UUID);
                Fragment newFragment = new SubMuscleFragment();
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.containerView, newFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        return rootFragment;
    }

    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.muscle_id);
        for (int i = 0; i < imgs.length(); i++) {
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            String img = imgs.getString(i);
            imageItems.add(new ImageItem(img, ExercisesDB.getInstance().keys.get(i)));
        }
        return imageItems;
    }


}
