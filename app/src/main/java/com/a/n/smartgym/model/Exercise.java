package com.a.n.smartgym.model;

import java.sql.Date;

/**
 * Created by Tan on 1/26/2016.
 */
public class Exercise {

    public static final String TAG = Exercise.class.getSimpleName();
    public static final String TABLE = "Exercise";
    // Labels Table Columns names
    public static final String KEY_VISIT_ID = "visitid";
    public static final String KEY_EXERCISE_ID = "exerciseid";
    public static final String KEY_START = "start";
    public static final String KEY_END = "end";
    public static final String KEY_MACHINE_NAME = "machinename";

    private String exerciseid;
    private String visitid;
    private String machinename;
    private Date start;
    private Date end;

    public String getMachinename() {
        return machinename;
    }

    public void setMachinename(String machinename) {
        this.machinename = machinename;
    }

    public String getexerciseid() {
        return exerciseid;
    }

    public void setexerciseid(String exerciseid) {
        this.exerciseid = exerciseid;
    }

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
