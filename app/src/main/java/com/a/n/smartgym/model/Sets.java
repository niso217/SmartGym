package com.a.n.smartgym.model;

import java.sql.Date;

/**
 * Created by Tan on 1/26/2016.
 */
public class Sets {

    public static final String TAG = Sets.class.getSimpleName();
    public static final String TABLE = "Sets";
    // Labels Table Columns names
    public static final String KEY_SET_ID = "setid";
    public static final String KEY_EXERCISE_ID = "exerciseid";
    public static final String KEY_COUNT = "count";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_START = "start";
    public static final String KEY_END = "end";


    private String setid;
    private String exerciseid;
    private int count;
    private int weight;
    private long start;
    private long end;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getSetid() {
        return setid;
    }

    public void setSetid(String setid) {
        this.setid = setid;
    }

    public String getexerciseid() {
        return exerciseid;
    }

    public void setexerciseid(String exerciseid) {
        this.exerciseid = exerciseid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
