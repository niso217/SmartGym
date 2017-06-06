package com.a.n.smartgym.Objects;

import java.util.Date;

/**
 * Created by nirb on 06/06/2017.
 */

public class LastExercise {

    private int weight;
    private int count;
    private String date;
    private int sets;

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
