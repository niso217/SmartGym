package com.a.n.smartgym;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.a.n.smartgym.Adapter.GridViewAdapter;
import com.a.n.smartgym.Adapter.MuscleItem;
import com.a.n.smartgym.Listener.WizardDataChanged;
import com.a.n.smartgym.Objects.TrainingProgram;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.repo.MuscleRepo;

import java.util.ArrayList;
import java.util.List;

import static com.a.n.smartgym.Utils.Constants.WIZARD_DAY_UPDATE;
import static com.a.n.smartgym.Utils.Constants.WIZARD_MAIN_UPDATE;

/**
 * Created by Ratan on 7/29/2015.
 */
public class SubMuscleFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private String group, UUID;
    private List<Muscle> exname;
    private ArrayList<MuscleItem> gridSelections;
    private ArrayList<MuscleItem>  muscleItemArrayList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridSelections = new ArrayList<>();
        muscleItemArrayList = new ArrayList<>();


    }

    private final BroadcastReceiver mWizardUpdates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch ((intent.getAction())) {
                case WIZARD_MAIN_UPDATE:
                    onDataChanged();
                case WIZARD_DAY_UPDATE:
                    onDataChanged();
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().registerReceiver(mWizardUpdates, makeWizardUpdateIntentFilter());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mWizardUpdates);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootFragment = inflater.inflate(R.layout.fragment_muscle, null);


        gridView = (GridView) rootFragment.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, muscleItemArrayList);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MuscleItem item = (MuscleItem) parent.getItemAtPosition(position);

                boolean selectedIndex = TrainingProgram.getInstance().getSubMusclesIndex(item);
                if (selectedIndex) {
                    item.setSelected(false);
                    remove(item);
                } else {
                    item.setSelected(true);
                    gridSelections.add(item);
                }
                gridAdapter.notifyDataSetChanged();
                TrainingProgram.getInstance().SetSubMuscle(gridSelections);
            }
        });




        return rootFragment;
    }

    private void remove(MuscleItem item){
        for (int i = 0; i < gridSelections.size(); i++) {
            if (gridSelections.get(i).getTitle().equals(item.getTitle()))
                gridSelections.remove(i);
        }
    }

    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<MuscleItem> getData() {

        final ArrayList<MuscleItem> muscleItems = new ArrayList<>();
        String selected_main = TrainingProgram.getInstance().getMainMusclesString();
        MuscleRepo muscleRepo = new MuscleRepo();
        List<Muscle> exname = muscleRepo.getSubMuscle(selected_main, "");

        if (exname==null) return muscleItems;

        for (int i = 0; i < exname.size(); i++) {
            muscleItems.add(new MuscleItem(exname.get(i).getImage(), exname.get(i).getName()));
        }

        return muscleItems;

    }

    public void onDataChanged() {
        muscleItemArrayList = getData();
        gridAdapter.clear();
        gridAdapter.addAll(muscleItemArrayList);

        //gridAdapter.notifyDataSetChanged();
        //gridView.invalidateViews();
        ChangeSelections();

    }

    private void ChangeSelections(){
        // Pre-select currently selected items.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                gridSelections = TrainingProgram.getInstance().getSubMuscles();
                ArrayList<String> selected = TrainingProgram.getInstance().getSubMusclesStringArray();
                for (int i = 0; i < muscleItemArrayList.size(); i++) {
                    if (selected.contains(muscleItemArrayList.get(i).getTitle())) {
                        MuscleItem item = (MuscleItem) gridView.getItemAtPosition(i);
                        item.setSelected(true);
                    }
                    else{
                        MuscleItem item = (MuscleItem) gridView.getItemAtPosition(i);
                        item.setSelected(false);

                    }
                }
                gridAdapter.notifyDataSetChanged();

            }
        });
    }

    private static IntentFilter makeWizardUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIZARD_MAIN_UPDATE);
        intentFilter.addAction(WIZARD_DAY_UPDATE);
        return intentFilter;
    }

}
