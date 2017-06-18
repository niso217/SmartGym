package com.a.n.smartgym.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.a.n.smartgym.R;

import static com.a.n.smartgym.R.id.textView;

public class ImageItem  extends FrameLayout {
    private String image;
    private String title;
    private ImageView imagee;

    public ImageItem(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.grid_item_layout, this);
        imagee = (ImageView) getRootView().findViewById(R.id.image);
    }

    public void setImageItem(String image, String title) {
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


    public void display(boolean isSelected) {
        imagee.setBackgroundResource(isSelected ? R.drawable.border : null);
    }
}
