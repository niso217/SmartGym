package com.a.n.smartgym.Objects;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Rounds {

    public String start_time;
    public int weight;


    public Rounds() {
    }

    public Rounds(int weight) {
        this.start_time = String.valueOf(System.currentTimeMillis());;
        this.weight = weight;
    }
}
