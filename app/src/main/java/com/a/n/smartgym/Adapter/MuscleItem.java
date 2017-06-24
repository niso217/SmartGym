package com.a.n.smartgym.Adapter;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class MuscleItem {
    private String image;
    private String title;
    boolean selected;
    private String value;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MuscleItem(String image, String title) {
        super();
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
