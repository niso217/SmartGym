package com.a.n.smartgym.model;


import java.util.Date;

/**
 * Created by Tan on 1/26/2016.
 */
public class Plan {

    public static final String TAG = Plan.class.getSimpleName();
    public static final String TABLE = "plan";
    public static final String KEY_PLAN_ID = "planid";
    public static final String KEY_DATE = "date";

    private String planid;
    private String date;

    public String getPlanid() {
        return planid;
    }

    public void setPlanid(String planid) {
        this.planid = planid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
