package com.a.n.smartgym.Objects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;


@IgnoreExtraProperties
public class Sessions {

    public List<Rounds> rounds = new ArrayList<>();;
    public String SessionId;

    public Sessions(String id) {
        SessionId = id;
    }

    public void addRounds(Rounds round) {
        rounds.add(round);
    }


}
