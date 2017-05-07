package com.a.n.smartgym.Objects;

/**
 * Created by nirb on 03/05/2017.
 */

public class Muscles {

    String id;
    String name;
    String main;
    String secondary;
    String image;
    String description;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getSecondary() {
        return secondary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}
