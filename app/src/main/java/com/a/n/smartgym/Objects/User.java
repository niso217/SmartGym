package com.a.n.smartgym.Objects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;


@IgnoreExtraProperties
public class User {

    public String uid;
    public List<Dates> dates = new ArrayList<>();

    public User(String id) {
        uid = id;
    }

    public void addDate(Dates date) {
        dates.add(date);
    }


}
