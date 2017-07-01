package com.a.n.smartgym.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.a.n.smartgym.Object.LastExercise;
import com.a.n.smartgym.R;
import com.a.n.smartgym.Views.ExerciseLinearLayout;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by DAT on 9/1/2015.
 */
public class ExerciseFragmentTab extends Fragment {

    private static final String TAG = ExerciseFragmentTab.class.getSimpleName();
    private LinearLayout mExamplelinearLayout, mLinerLayoutContainer;
    private TextView mTextViewName;
    private NumberProgressBar mNumberProgressBar;
    ArrayList<MuscleExercise> muscleExerciseList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_tab, container, false);
        Bundle bundle = getArguments();


        getIDs(view);
        setEvents();

        buildview();
        return view;
    }


    private void getIDs(View view) {
        mExamplelinearLayout = (LinearLayout) view.findViewById(R.id.example);
        mTextViewName = (TextView) view.findViewById(R.id.exercise_name);
        mLinerLayoutContainer = (LinearLayout) view.findViewById(R.id.linear_parent);
        mNumberProgressBar = (NumberProgressBar) view.findViewById(R.id.number_progress_bar);
    }

    private void setEvents() {

    }


    public void buildview() {


            ExerciseLinearLayout exercisesFragment = new ExerciseLinearLayout(getContext(),R.layout.custom_exercise_tab);
            exercisesFragment.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mLinerLayoutContainer.addView(exercisesFragment);



    }

}
