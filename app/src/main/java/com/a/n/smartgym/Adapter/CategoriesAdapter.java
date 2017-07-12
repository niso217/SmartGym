package com.a.n.smartgym.Adapter;

/**
 * Created by nirb on 11/07/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a.n.smartgym.DBModel.Muscle;
import com.a.n.smartgym.DBRepo.MuscleExerciseRepo;
import com.a.n.smartgym.Fragment.WizardExerciseFragment;
import com.a.n.smartgym.Listener.OnItemClickListener;
import com.a.n.smartgym.Listener.onSubmitListener;
import com.a.n.smartgym.Object.MuscleItem;
import com.a.n.smartgym.R;
import com.a.n.smartgym.Views.NumberPickerDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.SimpleViewHolder> {

    private final Context mContext;
    private ArrayList<MuscleItem> mItems = new ArrayList<>();
    private OnItemClickListener listener;


    private int mCurrentItemId = 0;


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageView image;
        public final LinearLayout frame;


        public SimpleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.text);
            image = (ImageView) view.findViewById(R.id.image);
            frame = (LinearLayout) view.findViewById(R.id.frame);

        }
    }

    public ArrayList<MuscleItem> getData(){
        return mItems;
    }

    public CategoriesAdapter(Context context, OnItemClickListener listener) {
        this.listener = listener;
        mContext = context;
    }

    public void  updateData(ArrayList<MuscleItem> muscleItemArrayList){
            mItems=muscleItemArrayList;
            notifyDataSetChanged();

    }


    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_layout, parent, false);
        return new SimpleViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        holder.title.setText(mItems.get(position).getTitle());

//        final MuscleItem model = mItems.get(position);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClicked(position);
            }
        });
//
//        model.setSelected(!model.isSelected());
//        holder.title.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);

        MuscleItem item = mItems.get(position);
        Picasso.with(mContext).load(item.getImage()).into(holder.image);

        if (item.isSelected()) {
            holder.frame.setBackgroundResource(R.drawable.border);
        } else {
            holder.frame.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public MuscleItem getItemAtPosition(int index){
        return mItems.get(index);
    }


    public void addItem(MuscleItem muscleItem) {
        final int id = mCurrentItemId++;
        mItems.add(muscleItem);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}