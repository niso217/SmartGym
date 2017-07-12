package com.a.n.smartgym.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.a.n.smartgym.Adapter.CategoriesAdapter;
import com.a.n.smartgym.Adapter.GridViewAdapter;
import com.a.n.smartgym.Adapter.SectionedGridRecyclerViewAdapter;
import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.Listener.OnItemClickListener;
import com.a.n.smartgym.Listener.onSubmitListener;
import com.a.n.smartgym.Object.MuscleItem;
import com.a.n.smartgym.DBModel.Muscle;
import com.a.n.smartgym.DBRepo.MuscleExerciseRepo;
import com.a.n.smartgym.DBRepo.MuscleRepo;
import com.a.n.smartgym.DBRepo.PlanMuscleRepo;
import com.a.n.smartgym.R;
import com.a.n.smartgym.Views.NumberPickerDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.a.n.smartgym.Utils.Constants.PLAN_DAY_UUID;
import static com.a.n.smartgym.Utils.Constants.WIZARD_DAY_UPDATE;
import static com.a.n.smartgym.Utils.Constants.WIZARD_MAIN_UPDATE;

/**
 * Created by Ratan on 7/29/2015.
 */
public class WizardExerciseFragment extends Fragment  implements onSubmitListener,OnItemClickListener {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private String DayUUID;
    private static final String TAG = WizardExerciseFragment.class.getSimpleName();
    private Context mContext;
    private MuscleItem mSelectedMuscleItem;
    private ArrayList<MuscleItem> mSelectedMuscleItemArray;

    private RecyclerView mRecyclerView;
    private CategoriesAdapter mAdapter;
    List<SectionedGridRecyclerViewAdapter.Section> sections;




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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_exercise_muscle, container, false);

        //Your RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));

        //Your RecyclerView.Adapter
        mAdapter = new CategoriesAdapter(getActivity(),this);

        sections = new ArrayList<>();



        //This is the code to provide a sectioned grid

        return view;
    }




    public LinkedHashMap<String,ArrayList<Muscle>> getCategories(){
        String selected_main = new PlanMuscleRepo().getMainMuscleByDayAsString(DayUUID);
        LinkedHashMap<String,ArrayList<Muscle>> nir =  new MuscleRepo().getHashSubMuscle(selected_main, "");

        return nir;

    }

    public void onDataChanged() {

        sections.clear();
        int index = 0;
        mSelectedMuscleItemArray = new ArrayList<>();
        LinkedHashMap<String,ArrayList<Muscle>> categories =  getCategories();
        for (Map.Entry<String, ArrayList<Muscle>> entry : categories.entrySet())
        {
            ArrayList<Muscle> exname = entry.getValue();
            for (int i = 0; i < exname.size(); i++) {
                mSelectedMuscleItemArray.add(new MuscleItem(exname.get(i).getImage(), exname.get(i).getName(),exname.get(i).getMuscle()));
            }
            sections.add(new SectionedGridRecyclerViewAdapter.Section(index,entry.getKey().toUpperCase()));
            index += exname.size();
        }

        //Add your adapter to the sectionAdapter
        SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
        SectionedGridRecyclerViewAdapter mSectionedAdapter = new
                SectionedGridRecyclerViewAdapter(getActivity(),R.layout.section,R.id.section_text,mRecyclerView,mAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //Apply this adapter to the RecyclerView
        mRecyclerView.setAdapter(mSectionedAdapter);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                List<String> selected = new MuscleExerciseRepo().getSubMuscleByDay(DayUUID);
                for (int i = 0; i < mAdapter.getItemCount(); i++) {
                    if (selected.contains(mAdapter.getData().get(i).getTitle())) {
                        MuscleItem item = mAdapter.getItemAtPosition(i);
                        item.setSelected(true);
                    }
                    else{
                        MuscleItem item = mAdapter.getItemAtPosition(i);
                        item.setSelected(false);

                    }

                }
                mAdapter.notifyDataSetChanged();

            }
        });


        mAdapter.updateData(mSelectedMuscleItemArray);


    }

    private void ShowDialog(){
        NumberPickerDialog dialog = new NumberPickerDialog();
        dialog.setOnSubmitListener(WizardExerciseFragment.this);
        dialog.show(getActivity().getFragmentManager(),"");
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
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(int index) {
        mSelectedMuscleItem = mSelectedMuscleItemArray.get(index);
        boolean selectedIndex = new MuscleExerciseRepo().isSubMuscleExist(DayUUID,mSelectedMuscleItem.getTitle());
        if (selectedIndex) {
            setGridViewSelection(false);
        } else {
            ShowDialog();
        }
    }
}
