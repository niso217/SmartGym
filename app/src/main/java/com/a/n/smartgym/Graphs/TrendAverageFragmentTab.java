package com.a.n.smartgym.Graphs;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.Object.DailyAverage;
import com.a.n.smartgym.R;
import com.a.n.smartgym.Views.MyMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

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
        setData();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    private void setData() {

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

        Legend l = mLineChart.getLegend();
        l.setEnabled(false);

        mLineChart.getDescription().setEnabled(false);


        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(mLineChart); // For bounds control
        mLineChart.setMarker(mv); // Set the marker to the chart



        float start = 0f;

        ArrayList<Entry> values = new ArrayList<Entry>();

        float val = 0.0f;
        for (int i = (int) start; i < mDailyAverage.size(); i++) {

            val = (float) mDailyAverage.get(i).getAverage();
            values.add(new BarEntry(i + 1f, val));
        }



        LineDataSet set1;

        if (mLineChart.getData() != null &&
                mLineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);


            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mLineChart.setData(data);
        }
    }



    private void getIDs(View view) {
        mLineChart = (LineChart) view.findViewById(R.id.linechart);

    }

    private void setEvents() {

    }
}
