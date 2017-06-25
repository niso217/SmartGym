package com.a.n.smartgym.DBModel;


/**
 * Created by Tan on 1/26/2016.
 */
public class PlanMuscle {

    public static final String TAG = PlanMuscle.class.getSimpleName();
    public static final String TABLE = "PlanMuscle";
    public static final String KEY_PLAN_MUSCLE_ID = "planmuscleid";
    public static final String KEY_PLAN_ID = "planid";
    public static final String KEY_MUSCLE_ID = "muscleid";

    private String planmuscleid;
    private String planid;
    private String muscleid;

    public String getPlanmuscleid() {
        return planmuscleid;
    }

    public void setPlanmuscleid(String planmuscleid) {
        this.planmuscleid = planmuscleid;
    }

    public String getPlanid() {
        return planid;
    }

    public void setPlanid(String planid) {
        this.planid = planid;
    }

    public String getMuscleid() {
        return muscleid;
    }

    public void setMuscleid(String muscleid) {
        this.muscleid = muscleid;
    }
}
