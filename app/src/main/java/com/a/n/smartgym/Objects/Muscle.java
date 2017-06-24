package com.a.n.smartgym.Objects;

import com.a.n.smartgym.Adapter.MuscleItem;
import com.a.n.smartgym.model.MuscleExercise;
import com.a.n.smartgym.model.Plan;
import com.a.n.smartgym.model.PlanMuscle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nirb on 21/06/2017.
 */

public class Muscle{
    private ArrayList<MuscleItem> main = new ArrayList<>();
    private ArrayList<MuscleItem> sub = new ArrayList<>();

    private List<Plan> planList = new ArrayList<>();
    private List<PlanMuscle> planMuscleList = new ArrayList<>();
    private List<MuscleExercise> muscleExerciseList = new ArrayList<>();


    public List<Plan> getPlanList() {
        return planList;
    }

    public void setPlanList(List<Plan> planList) {
        this.planList = planList;
    }

    public List<PlanMuscle> getPlanMuscleList() {
        return planMuscleList;
    }

    public void setPlanMuscleList(List<PlanMuscle> planMuscleList) {
        this.planMuscleList = planMuscleList;
    }

    public List<MuscleExercise> getMuscleExerciseList() {
        return muscleExerciseList;
    }

    public void setMuscleExerciseList(List<MuscleExercise> muscleExerciseList) {
        this.muscleExerciseList = muscleExerciseList;
    }

    public ArrayList<MuscleItem> getMain() {
        return main;
    }

    public void setMain(ArrayList<MuscleItem> main) {
        this.main = new ArrayList<>(main);
    }

    public ArrayList<MuscleItem> getSub() {
        return sub;
    }

    public void setSub(ArrayList<MuscleItem> sub) {
        this.sub = new ArrayList<>(sub);
    }


}


