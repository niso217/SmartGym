package com.a.n.smartgym.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.a.n.smartgym.DBRepo.MuscleExerciseRepo;
import com.a.n.smartgym.R;
import com.a.n.smartgym.Utils.Constants;
import com.a.n.smartgym.DBModel.Plan;
import com.a.n.smartgym.DBRepo.PlanRepo;

import java.util.UUID;

import static com.a.n.smartgym.Utils.Constants.PLAN_DAY_UUID;
import static com.a.n.smartgym.Utils.Constants.WIZARD_DAY_UPDATE;

/**
 * Created by Ratan on 7/29/2015.
 */
public class WizardDayFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    RadioGroup radioGroup;
    Snackbar mSnackbar;
    String mSelectedDay;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.social_layout,null);

        radioGroup = (RadioGroup)  root.findViewById(R.id.RGroup);
        radioGroup.setOnCheckedChangeListener(this);

        return root;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        switch(checkedId)
        {
            case R.id.sunday:
                mSelectedDay = Constants.SUNDAY;
                break;
            case R.id.monday:
                mSelectedDay = Constants.MONDAY;
                break;
            case R.id.tuesday:
                mSelectedDay = Constants.TUESDAY;
                break;
            case R.id.wednesday:
                mSelectedDay = Constants.WEDNESDAY;
                break;
            case R.id.thursday:
                mSelectedDay = Constants.THURSDAY;
                break;
            case R.id.friday:
                mSelectedDay = Constants.FRIDAY;
                break;
            case R.id.saturday:
                mSelectedDay = Constants.SATURDAY;
                break;
        }
        String text = new MuscleExerciseRepo().getDayPlan(mSelectedDay);
        mSnackbar = Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).setDuration(30000)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isPlanDayExist(mSelectedDay);
                        //do what ever you want

                    }
                });

        View snackbarView = mSnackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(20);  //set the max lines for textview to show multiple lines

        mSnackbar.show();
    }

    private static int countLines(String str){
        String[] lines = str.split("\r\n|\r|\n");
        return  lines.length;
    }

    private void isPlanDayExist(String day) {
        PlanRepo planRepo = new PlanRepo();
        String uuid = planRepo.getDayUUID(day);
        if (uuid.isEmpty()) {
            Plan plan = new Plan();
            uuid = UUID.randomUUID().toString();
            plan.setPlanid(uuid);
            plan.setDate(day);
            planRepo.insert(plan);
        }
        broadcastUpdate(uuid);
    }

    private void broadcastUpdate(String uuid) {
        final Intent intent = new Intent(WIZARD_DAY_UPDATE);
        intent.putExtra(PLAN_DAY_UUID,uuid);
        getActivity().sendBroadcast(intent);
    }
}
