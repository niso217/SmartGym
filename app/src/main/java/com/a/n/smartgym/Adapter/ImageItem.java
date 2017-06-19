package com.a.n.smartgym.Adapter;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class ImageItem {
    private String image;
    private String title;
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ImageItem(String image, String title) {
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
