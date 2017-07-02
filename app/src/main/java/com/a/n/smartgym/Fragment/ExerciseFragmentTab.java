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
import com.a.n.smartgym.R;
import com.a.n.smartgym.Views.ExerciseLinearLayout;
import com.a.n.smartgym.Views.MyDayLinearLayout;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.ArrayList;

/**
 * Created by DAT on 9/1/2015.
 */
public class ExerciseFragmentTab extends Fragment {

    private static final String TAG = ExerciseFragmentTab.class.getSimpleName();
    private LinearLayout mLinerLayoutContainer;
    private TextView tv_num,tv_reps,tv_weight;
    private NumberProgressBar mNumberProgressBar;
    ArrayList<MuscleExercise> muscleExerciseList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_tab, container, false);
        getIDs(view);
        return view;
    }


    private void getIDs(View view) {
        tv_num = (TextView) view.findViewById(R.id.tv_num);
        tv_reps = (TextView) view.findViewById(R.id.tv_reps);
        tv_weight = (TextView) view.findViewById(R.id.tv_weight);
        mLinerLayoutContainer = (LinearLayout) view.findViewById(R.id.linear_parent);
    }




    public void buildview(String num, String reps, String weight) {

        ExerciseLinearLayout exercisesFragment = new ExerciseLinearLayout(getContext());
        exercisesFragment.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        exercisesFragment.setTv_num(num);
        exercisesFragment.setTv_reps(reps);
        exercisesFragment.setTv_weight(weight);

        mLinerLayoutContainer.addView(exercisesFragment);



    }

}
