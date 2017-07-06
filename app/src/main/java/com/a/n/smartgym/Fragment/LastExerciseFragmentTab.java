package com.a.n.smartgym.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.Object.LastExercise;
import com.a.n.smartgym.R;
import com.a.n.smartgym.Views.ExerciseLinearLayout;
import com.a.n.smartgym.Views.LastExerciseLinearLayout;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.ArrayList;

/**
 * Created by DAT on 9/1/2015.
 */
public class LastExerciseFragmentTab extends Fragment {

    private static final String TAG = LastExerciseFragmentTab.class.getSimpleName();
    private LinearLayout mLinerLayoutContainer;
    ArrayList<LastExercise> muscleExerciseList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_last_exercise_tab, container, false);
        Bundle bundle = getArguments();
        getIDs(view);
        muscleExerciseList = bundle.getParcelableArrayList("data");
        buildview(muscleExerciseList);
        return view;

    }


    private void getIDs(View view) {
        mLinerLayoutContainer = (LinearLayout) view.findViewById(R.id.linear_parent);
    }




    public void buildview(ArrayList<LastExercise> lastExercises) {

        for (int i = 0; i < lastExercises.size(); i++) {
            LastExerciseLinearLayout exercisesFragment = new LastExerciseLinearLayout(getContext());
            exercisesFragment.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            exercisesFragment.setTv_num((i+1) +"");
            exercisesFragment.setTv_reps(lastExercises.get(i).getCount()+"");
            exercisesFragment.setTv_weight(lastExercises.get(i).getWeight()+"");
            exercisesFragment.setTv_sets(lastExercises.get(i).getSets()+"");

            mLinerLayoutContainer.addView(exercisesFragment);
        }





    }

}
