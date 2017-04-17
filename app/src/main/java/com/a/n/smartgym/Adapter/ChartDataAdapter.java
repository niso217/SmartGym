package com.a.n.smartgym.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.a.n.smartgym.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by nirb on 09/04/2017.
 */

public class ChartDataAdapter extends ArrayAdapter<BarData> {

    private List<List<String>> labels;
    private List<String> titles;

    public ChartDataAdapter(Context context, List<BarData> objects, List<List<String>> labels,List titles) {
        super(context, 0, objects);
        this.labels = labels;
        this.titles = titles;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final int pos = position;

        BarData data = getItem(position);

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_barchart, null);
            holder.chart = (BarChart) convertView.findViewById(R.id.chart);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(titles.get(pos));
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }


        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get(pos).get((int)value);
            }
        };

        // apply styling
        //data.setValueTypeface(mTfLight);
        data.setValueTextColor(Color.BLACK);
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setDrawGridBackground(false);

        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setTypeface(mTfLight);
        xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = holder.chart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        //leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = holder.chart.getAxisRight();
        // rightAxis.setTypeface(mTfLight);
        //rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(15f);

        // set data
        holder.chart.setData(data);
        holder.chart.setFitBars(true);

        // do not forget to refresh the chart
//            holder.chart.invalidate();
        holder.chart.animateY(700);

        return convertView;
    }

    private class ViewHolder {

        BarChart chart;
    }


}
