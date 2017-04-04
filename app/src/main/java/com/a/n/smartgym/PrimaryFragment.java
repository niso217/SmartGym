package com.a.n.smartgym;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.Objects.Dates;
import com.a.n.smartgym.Quary.DailyAvrage;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ratan on 7/29/2015.
 */
public class PrimaryFragment extends Fragment {
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mAuth;
    private static final String TAG = PrimaryFragment.class.getSimpleName();
    private List<DailyAvrage> mDailyAvrage;
    private FloatingActionButton fba;
    private String [] dates;
    private BarChart mBarChart;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();


        // get reference to 'users' node
        //mFirebaseDatabase = mFirebaseInstance.getReference("users");


        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootFragment = inflater.inflate(R.layout.primary_layout, null);

        //addSessionChangeListener();

        mBarChart = (BarChart) rootFragment.findViewById(R.id.chart);
        initGraph();

        return rootFragment;
    }

    public void initGraph() {

        //data
        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.46f; // x2 dataset




        mDailyAvrage = new ExerciseRepo().getDailyAvrage(mAuth.getCurrentUser().getUid());

        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
        final String [] labels = new String[mDailyAvrage.size()];

        for (int i = 0; i < mDailyAvrage.size(); i++) {
            yVals1.add(new BarEntry(i, (float) mDailyAvrage.get(i).getAvrage()));
            labels[i] = mDailyAvrage.get(i).getDate();
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels[(int) value];
            }

        };

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        BarDataSet set1;

        if (mBarChart.getData() != null && mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            set1.setStackLabels(labels);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            // create 2 datasets with different types
            set1 = new BarDataSet(yVals1, "Weight");
            //set1.setColor(Color.rgb(104, 241, 175));
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            mBarChart.setData(data);
            mBarChart.getDescription().setText("");
            mBarChart.getBarData().setBarWidth(barWidth);
            mBarChart.invalidate();
            mBarChart.animateY(500);

        }

    }

    private Date StringtoDate(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date theDate = null;
        try {
            theDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return theDate;
    }

}
