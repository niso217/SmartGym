package com.a.n.smartgym.Graphs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.Quary.DailyAverage;
import com.a.n.smartgym.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by DAT on 9/1/2015.
 */
public class TrendAverageFragmentTab extends Fragment {
    String date;
    ArrayList<DailyAverage> mDailyAverage;
    BarChart mBarChart;
    LineChart mLineChart;
    private static final String TAG = TrendAverageFragmentTab.class.getSimpleName();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trend_average, container, false);
        Bundle bundle = getArguments();
        date = bundle.getString("date");
        mDailyAverage = bundle.getParcelableArrayList("data");
        getIDs(view);
        setEvents();
        setData_BarChart2();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    private void setData_BarChart2() {


        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int val = (int) value;
                if (val<=mDailyAverage.size())
                    return mDailyAverage.get(val-1).getMachine_name();

                return "";
            }

        };

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setLabelRotationAngle(60);

        xAxis.setValueFormatter(formatter);


        float start = 0f;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        float val = 0.0f;
        for (int i = (int) start; i < mDailyAverage.size(); i++) {

            val = (float) mDailyAverage.get(i).getAverage();
            yVals1.add(new BarEntry(i + 1f, val));
        }

        LineDataSet set1;

        if (mLineChart.getData() != null &&
                mLineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(yVals1, "");
            set1.setLineWidth(2f);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            //set1.setCubicIntensity(12f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            data.setValueTextSize(10f);

            // data.setValueTypeface(mTfLight);
            mLineChart.getDescription().setEnabled(false);
            mLineChart.setData(data);
            mLineChart.invalidate();

        }

        Legend l = mLineChart.getLegend();
        l.setEnabled(false);
    }


    private void getIDs(View view) {
        mLineChart = (LineChart) view.findViewById(R.id.linechart);

    }

    private void setEvents() {

    }
}
