package com.a.n.smartgym;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.util.UUID;

/**
 * Created by Ratan on 7/29/2015.
 */
public class SubMuscleFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private String muscle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootFragment = inflater.inflate(R.layout.fragment_muscle, null);

        gridView = (GridView) rootFragment.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

            }
        });

        if (getArguments() != null) {
            muscle  = getArguments().getString("key");

        }

        return rootFragment;
    }

    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.muscle_id);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new ImageItem(bitmap, ExercisesDB.getInstance().keys.get(i)));
        }
        return imageItems;
    }


}
