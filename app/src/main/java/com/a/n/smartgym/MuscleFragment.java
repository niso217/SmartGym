package com.a.n.smartgym;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.a.n.smartgym.Adapter.GridViewAdapter;
import com.a.n.smartgym.Adapter.MuscleItem;
import com.a.n.smartgym.Listener.WizardDataChanged;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.repo.PlanMuscleRepo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.a.n.smartgym.Utils.Constants.PLAN_DAY_UUID;
import static com.a.n.smartgym.Utils.Constants.WIZARD_DAY_UPDATE;
import static com.a.n.smartgym.Utils.Constants.WIZARD_MAIN_UPDATE;

/**
 * Created by Ratan on 7/29/2015.
 */
public class MuscleFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private String DayUUID;
    private ArrayList<MuscleItem> gridSelections;
    ArrayList<MuscleItem>  muscleItemArrayList;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         muscleItemArrayList = getData();
        gridSelections = new ArrayList<>();
    }


    private final BroadcastReceiver mWizardUpdates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch ((intent.getAction())) {
                case WIZARD_DAY_UPDATE:
                    DayUUID = intent.getStringExtra(PLAN_DAY_UUID);
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

                boolean selectedIndex = new PlanMuscleRepo().isMainMuscleExist(DayUUID,item.getTitle());
                if (selectedIndex) {
                    item.setSelected(false);
                } else {
                    item.setSelected(true);
                }
                gridAdapter.notifyDataSetChanged();
                broadcastUpdate();
            }
        });

        // Pre-select currently selected items.
        onDataChanged();

        return rootFragment;
    }

    private ArrayList<MuscleItem> getData() {
        final ArrayList<MuscleItem> imageItems = new ArrayList<>();

        Enumeration e = ExercisesDB.getInstance().keys.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            int s = getResources().getIdentifier(key, "string", getContext().getPackageName());
            if (s != 0) {
                String result = getString(s);
                imageItems.add(new MuscleItem(result, key,""));
            }


        }


        return imageItems;
    }

    public void onDataChanged() {

        // Pre-select currently selected items.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                List<String> selected = new PlanMuscleRepo().getMainMuscleByDay(DayUUID);
                for (int i = 0; i < gridAdapter.getSize(); i++) {
                    if (selected.contains(gridAdapter.getData().get(i).getTitle())) {
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


    private void broadcastUpdate() {
        final Intent intent = new Intent(WIZARD_MAIN_UPDATE);
        intent.putExtra(PLAN_DAY_UUID,DayUUID);
        getActivity().sendBroadcast(intent);
    }

    private static IntentFilter makeWizardUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIZARD_DAY_UPDATE);
        return intentFilter;
    }




}
