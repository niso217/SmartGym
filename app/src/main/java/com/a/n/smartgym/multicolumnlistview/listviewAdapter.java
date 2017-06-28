package com.a.n.smartgym.multicolumnlistview;

import java.util.ArrayList;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a.n.smartgym.R;

/**
 * 
 * @author anfer
 * 
 */
public class listviewAdapter extends BaseAdapter {
	public ArrayList<Model> productList;
	Activity activity;

	public listviewAdapter(Activity activity, ArrayList<Model> productList) {
		super();
		this.activity = activity;
		this.productList = productList;
	}

	@Override
	public int getCount() {
		return productList.size();
	}

	@Override
	public Object getItem(int position) {
		return productList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {

		TextView mExercise_name;
		TextView mNumber_sets;
		TextView mNumber_reps;
		TextView mWeight;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_row, null);
			holder = new ViewHolder();
			holder.mExercise_name = (TextView) convertView.findViewById(R.id.name);
			holder.mNumber_sets = (TextView) convertView.findViewById(R.id.sets);
			holder.mNumber_reps = (TextView) convertView.findViewById(R.id.reps);
			holder.mWeight = (TextView) convertView.findViewById(R.id.weight);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Model item = productList.get(position);
		holder.mExercise_name.setText(item.getExercise_name().toString());
		holder.mNumber_sets.setText(item.getNumber_sets().toString());
		holder.mNumber_reps.setText(item.getNumber_reps().toString());
		holder.mWeight.setText(item.getWeight().toString());

		return convertView;
	}

}