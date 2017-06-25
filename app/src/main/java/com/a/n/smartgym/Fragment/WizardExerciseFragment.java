package com.a.n.smartgym.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.a.n.smartgym.Adapter.GridViewAdapter;
import com.a.n.smartgym.Listener.onSubmitListener;
import com.a.n.smartgym.Object.MuscleItem;
import com.a.n.smartgym.DBModel.Muscle;
import com.a.n.smartgym.DBRepo.MuscleExerciseRepo;
import com.a.n.smartgym.DBRepo.MuscleRepo;
import com.a.n.smartgym.DBRepo.PlanMuscleRepo;
import com.a.n.smartgym.R;
import com.a.n.smartgym.Views.NumberPickerDialog;

import java.util.ArrayList;
import java.util.List;

import static com.a.n.smartgym.Utils.Constants.PLAN_DAY_UUID;
import static com.a.n.smartgym.Utils.Constants.WIZARD_DAY_UPDATE;
import static com.a.n.smartgym.Utils.Constants.WIZARD_MAIN_UPDATE;

/**
 * Created by Ratan on 7/29/2015.
 */
public class WizardExerciseFragment extends Fragment  implements onSubmitListener {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private String DayUUID;
    private static final String TAG = WizardExerciseFragment.class.getSimpleName();
    private Context mContext;
    private MuscleItem mSelectedMuscleItem;



    private final BroadcastReceiver mWizardUpdates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch ((intent.getAction())) {
                case WIZARD_MAIN_UPDATE:
                    onDataChanged();
                    break;
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
        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mSelectedMuscleItem = (MuscleItem) parent.getItemAtPosition(position);

                boolean selectedIndex = new MuscleExerciseRepo().isSubMuscleExist(DayUUID,mSelectedMuscleItem.getTitle());
                if (selectedIndex) {
                    setGridViewSelection(false);
                } else {
                    ShowDialog();
                }
            }
        });


        return rootFragment;
    }

    private void ShowDialog(){
        NumberPickerDialog dialog = new NumberPickerDialog();
        dialog.setOnSubmitListener(WizardExerciseFragment.this);
        dialog.show(getActivity().getFragmentManager(),"");
    }

    private ArrayList<MuscleItem> getData() {

        final ArrayList<MuscleItem> muscleItems = new ArrayList<>();
        String selected_main = new PlanMuscleRepo().getMainMuscleByDayAsString(DayUUID);
        MuscleRepo muscleRepo = new MuscleRepo();
        List<Muscle> exname = muscleRepo.getSubMuscle(selected_main, "");

        if (exname==null) return muscleItems;

        for (int i = 0; i < exname.size(); i++) {
            muscleItems.add(new MuscleItem(exname.get(i).getImage(), exname.get(i).getName(),exname.get(i).getMuscle()));
        }

        return muscleItems;

    }

    public void onDataChanged() {
        gridAdapter.clear();
        gridAdapter.addAll(getData());

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                List<String> selected = new MuscleExerciseRepo().getSubMuscleByDay(DayUUID);
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


    private static IntentFilter makeWizardUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIZARD_MAIN_UPDATE);
        intentFilter.addAction(WIZARD_DAY_UPDATE);
        return intentFilter;
    }

    @Override
    public void setOnSubmitListener(int sets, int reps, int weight) {
        Log.d(TAG,sets +" " + reps +" " +weight);
        new MuscleExerciseRepo().insertSelection(DayUUID,mSelectedMuscleItem.getTitle(),mSelectedMuscleItem.getMain_muscle(),String.valueOf(sets),String.valueOf(reps),String.valueOf(weight));
        setGridViewSelection(true);

    }

    @Override
    public void setOnDismiss() {
        setGridViewSelection(false);

    }

    private void setGridViewSelection(boolean selection) {
        mSelectedMuscleItem.setSelected(selection);
        gridAdapter.notifyDataSetChanged();
    }
}
