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

import com.a.n.smartgym.Utils.Constants;
import com.a.n.smartgym.model.Plan;
import com.a.n.smartgym.repo.PlanRepo;

import java.util.UUID;

import static com.a.n.smartgym.Utils.Constants.PLAN_DAY_UUID;
import static com.a.n.smartgym.Utils.Constants.WIZARD_DAY_UPDATE;

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
                isPlanDayExist(Constants.SUNDAY);
                break;
            case R.id.monday:
                isPlanDayExist(Constants.MONDAY);
                break;
            case R.id.tuesday:
                isPlanDayExist(Constants.TUESDAY);
                break;
            case R.id.wednesday:
                isPlanDayExist(Constants.WEDNESDAY);
                break;
            case R.id.thursday:
                isPlanDayExist(Constants.THURSDAY);
                break;
            case R.id.friday:
                isPlanDayExist(Constants.FRIDAY);
                break;
            case R.id.saturday:
                isPlanDayExist(Constants.SATURDAY);
                break;
        }
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
