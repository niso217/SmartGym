package com.a.n.smartgym.Objects;

/**
 * Created by nirb on 03/05/2017.
 */

public class Muscles {

    long id;
    String name;
    String mainMuscles;
    String secondaryMuscles;
    String image;
    String description;

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getMainMuscles() {
        return mainMuscles;
    }

    public String getSecondaryMuscles() {
        return secondaryMuscles;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}
