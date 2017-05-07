package com.a.n.smartgym.model;

import java.sql.Date;

/**
 * Created by Tan on 1/26/2016.
 */
public class Muscle {

    public static final String TAG = Muscle.class.getSimpleName();
    public static final String TABLE = "MUSCLE";
    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_MUSCLE = "muscle";
    public static final String KEY_MAIN = "main";
    public static final String KEY_SECONDARY = "secondary";
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DESCRIPTION = "description";

    private String id;
    private String muscle;
    private String main;
    private String secondary;
    private String name;
    private String image;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
