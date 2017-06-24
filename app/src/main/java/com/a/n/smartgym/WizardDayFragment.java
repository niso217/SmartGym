package com.a.n.smartgym;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.a.n.smartgym.Objects.TrainingProgram;
import com.a.n.smartgym.Utils.Constants;

import static com.a.n.smartgym.Utils.Constants.WIZARD_DAY_UPDATE;
import static com.a.n.smartgym.Utils.Constants.WIZARD_MAIN_UPDATE;

/**
 * Created by Ratan on 7/29/2015.
 */
public class WizardDayFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    RadioGroup radioGroup;


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
                TrainingProgram.getInstance().setDay(Constants.SUNDAY);
                break;
            case R.id.monday:
                TrainingProgram.getInstance().setDay(Constants.MONDAY);
                break;
            case R.id.tuesday:
                TrainingProgram.getInstance().setDay(Constants.TUESDAY);
                break;
            case R.id.wednesday:
                TrainingProgram.getInstance().setDay(Constants.WEDNESDAY);
                break;
            case R.id.thursday:
                TrainingProgram.getInstance().setDay(Constants.THURSDAY);
                break;
            case R.id.friday:
                TrainingProgram.getInstance().setDay(Constants.FRIDAY);
                break;
            case R.id.saturday:
                TrainingProgram.getInstance().setDay(Constants.SATURDAY);
                break;
        }
        broadcastUpdate();
    }

    private void broadcastUpdate() {
        final Intent intent = new Intent(WIZARD_DAY_UPDATE);
        getActivity().sendBroadcast(intent);
    }
}
