package com.a.n.smartgym.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a.n.smartgym.Object.MuscleItem;
import com.a.n.smartgym.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends ArrayAdapter<MuscleItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<MuscleItem> data = new ArrayList<MuscleItem>();
    public List<Integer> selectedPositions = new ArrayList<>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<MuscleItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public int getSize(){
        return data.size();
    }

    public ArrayList<MuscleItem> getData(){
        return data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.frame = (LinearLayout) row.findViewById(R.id.frame);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }




        MuscleItem item = data.get(position);
        holder.imageTitle.setText(item.getTitle());
        Picasso.with(getContext()).load(item.getImage()).into(holder.image);

        if (item.isSelected()) {
            holder.frame.setBackgroundResource(R.drawable.border);
        } else {
            holder.frame.setBackgroundColor(Color.TRANSPARENT);
        }

        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
        LinearLayout frame;
    }
}