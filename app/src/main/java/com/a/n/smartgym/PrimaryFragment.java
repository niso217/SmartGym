package com.a.n.smartgym;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.a.n.smartgym.Adapter.ChartDataAdapter;
import com.a.n.smartgym.Quary.DailyAverage;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Ratan on 7/29/2015.
 */
public class PrimaryFragment extends Fragment implements View.OnClickListener {
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mAuth;
    private static final String TAG = PrimaryFragment.class.getSimpleName();
    private List<DailyAverage> mDailyAvrage;
    private FloatingActionButton fba;
    private String[] dates;
    private BarChart mBarChart;
    private ImageView id1, id2;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        mDailyAvrage = new ExerciseRepo().getDailyAverage(mAuth.getCurrentUser().getUid());

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootFragment = inflater.inflate(R.layout.primary_layout, null);
//        id1 = (ImageView) rootFragment.findViewById(R.id.ID1);
//        id2 = (ImageView) rootFragment.findViewById(R.id.ID2);
//        id1.setOnClickListener(this);
//        id2.setOnClickListener(this);
//
//        //addSessionChangeListener();
//
//        mBarChart = (BarChart) rootFragment.findViewById(R.id.chart);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ListView lv = (ListView) rootFragment.findViewById(R.id.listView1);

        ArrayList<BarData> list = new ArrayList<BarData>();

        // 20 items

        for (int i = 0; i < 20; i++) {
            list.add(generateData(i + 1));
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);

        return rootFragment;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private BarData generateData(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, (float) (Math.random() * 70) + 30));
        }

        BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<IBarDataSet>();
        sets.add(d);


        BarData cd = new BarData(sets);
        cd.setBarWidth(0.9f);
        return cd;
    }

//    public void initGraph(String ex_id) {
//
//        mBarChart.clear();
//        //data
//        float groupSpace = 0.04f;
//        float barSpace = 0.02f; // x2 dataset
//        float barWidth = 0.46f; // x2 dataset
//
//        if (mDailyAvrage != null)
//            mDailyAvrage.clear();
//
//

//
//
//        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
//        final String[] labels = new String[mDailyAvrage.size()];
//
//        for (int i = 0; i < mDailyAvrage.size(); i++) {
//            yVals1.add(new BarEntry(i, (float) mDailyAvrage.get(i).getAvrage()));
//            labels[i] = mDailyAvrage.get(i).getDate();
//        }
//
//
//        IAxisValueFormatter formatter = new IAxisValueFormatter() {
//
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                if (mDailyAvrage.size()>0)
//                    return labels[(int) value];
//
//                return null;
//            }
//
//        };
//
//        XAxis xAxis = mBarChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTextSize(10f);
//        xAxis.setDrawGridLines(false);
//        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
//        xAxis.setLabelRotationAngle(60);
//        if (mDailyAvrage.size()>0)
//        xAxis.setValueFormatter(formatter);
//
//        BarDataSet set1;
//
//        if (mBarChart.getData() != null && mBarChart.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
//            set1.setValues(yVals1);
//            set1.setStackLabels(labels);
//            mBarChart.getData().notifyDataChanged();
//            mBarChart.notifyDataSetChanged();
//        } else {
//            set1 = new BarDataSet(yVals1, "Weight");
//            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//            dataSets.add(set1);
//
//            BarData data = new BarData(dataSets);
//            mBarChart.setData(data);
//            mBarChart.getDescription().setText("");
//            mBarChart.getBarData().setBarWidth(barWidth);
//            mBarChart.invalidate();
//            mBarChart.animateY(500);
//
//        }
//
//    }
//
//    private Date StringtoDate(String date) {
//        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
//        Date theDate = null;
//        try {
//            theDate = df.parse(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return theDate;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.ID1:
//                //initGraph("id1");
//                break;
//            case R.id.ID2:
//                //initGraph("id2");
//                break;
        }
    }
}
