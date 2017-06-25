package com.a.n.smartgym.DBModel;


/**
 * Created by Tan on 1/26/2016.
 */
public class MuscleExercise {

    public static final String TAG = MuscleExercise.class.getSimpleName();
    public static final String TABLE = "MuscleExercise";
    public static final String KEY_MUSCLE_EXERCISE_ID = "muscleexerciseid";
    public static final String KEY_PLAN_MUSCLE_ID = "planmuscleid";
    public static final String KEY_EXERCISE_ID = "exerciseid";
    public static final String KEY_NUM_OF_SETS = "numberofsets";
    public static final String KEY_NUM_OF_REPS = "numberofreps";
    public static final String KEY_WEIGHT = "weight";


    private String muscleexerciseid;
    private String planmuscleid;
    private String exerciseid;
    private String numberofsets;
    private String numberofreps;
    private String weight;


    public String getMuscleexerciseid() {
        return muscleexerciseid;
    }

    public void setMuscleexerciseid(String muscleexerciseid) {
        this.muscleexerciseid = muscleexerciseid;
    }

    public String getPlanmuscleid() {
        return planmuscleid;
    }

    public void setPlanmuscleid(String planmuscleid) {
        this.planmuscleid = planmuscleid;
    }

    public String getExerciseid() {
        return exerciseid;
    }

    public void setExerciseid(String exerciseid) {
        this.exerciseid = exerciseid;
    }

    public String getNumberofsets() {
        return numberofsets;
    }

    public void setNumberofsets(String numberofsets) {
        this.numberofsets = numberofsets;
    }

    public String getNumberofreps() {
        return numberofreps;
    }

    public void setNumberofreps(String numberofreps) {
        this.numberofreps = numberofreps;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
