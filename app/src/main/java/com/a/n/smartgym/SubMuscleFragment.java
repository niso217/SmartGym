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
import com.a.n.smartgym.Helpers.URLtoBitmap;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.Objects.Muscles;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.repo.MuscleRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ratan on 7/29/2015.
 */
public class SubMuscleFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private String group, UUID;
    List<Muscle> exname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootFragment = inflater.inflate(R.layout.fragment_muscle, null);

        if (getArguments() != null) {
            group = getArguments().getString("group");
            UUID = getArguments().getString("uuid");
            MuscleRepo muscleRepo = new MuscleRepo();
            exname = muscleRepo.getSubMuscle(group,"");
        }

        gridView = (GridView) rootFragment.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);


                Bundle bundle = new Bundle();
                bundle.putInt("index", position);
                bundle.putString("group", group);
                bundle.putString("uuid", UUID);
                bundle.putParcelable("muscle",exname.get(position));
                Fragment newFragment = new ExercisesFragment();
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

        for (int i = 0; i < exname.size(); i++) {
            imageItems.add(new ImageItem(exname.get(i).getImage(), exname.get(i).getName()));
        }

        return imageItems;

    }

}
