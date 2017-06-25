package com.a.n.smartgym.Object;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class MuscleItem {
    private String image;
    private String title;
    private String main_muscle;
    boolean selected;

    public String getMain_muscle() {
        return main_muscle;
    }

    public void setMain_muscle(String main_muscle) {
        this.main_muscle = main_muscle;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public MuscleItem(String image, String title,String main) {
        super();
        this.main_muscle = main;
        this.image = image;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
