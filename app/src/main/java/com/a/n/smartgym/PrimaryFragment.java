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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Ratan on 7/29/2015.
 */
public class PrimaryFragment extends Fragment {
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mAuth;
    private static final String TAG = PrimaryFragment.class.getSimpleName();
    private List<DailyAverage> mDailyAvrage;
    private FloatingActionButton fba;
    private String[] dates;
    private BarChart mBarChart;
    private ImageView id1, id2;
    private LinkedHashMap<String, Map<String, Double>> dicCodeToIndex;
    private List<List<String>> labels = new ArrayList<>();
    private List<String> titles = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();


        extractData();

        super.onCreate(savedInstanceState);
    }

    //get all data from database
    private void extractData() {
        dicCodeToIndex = new LinkedHashMap <String, Map<String, Double>>();

        mDailyAvrage = new ExerciseRepo().getAllDaysAverages(mAuth.getCurrentUser().getUid());

        Iterator iterator = mDailyAvrage.iterator();
        while (iterator.hasNext()) {
            DailyAverage temp = (DailyAverage) iterator.next();
            Map<String, Double> in = dicCodeToIndex.get(temp.getDate());
            if (in != null) {
                in.put(temp.getMachine_name(), temp.getAverage());
                dicCodeToIndex.put(temp.getDate(), in);

            } else {
                Map<String, Double> inner = new HashMap<>();
                inner.put(temp.getMachine_name(), temp.getAverage());
                dicCodeToIndex.put(temp.getDate(), inner);
            }

        }
        Log.d(TAG, "");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootFragment = inflater.inflate(R.layout.primary_layout, null);



        ListView lv = (ListView) rootFragment.findViewById(R.id.listView1);

        ArrayList<BarData> list = new ArrayList<BarData>();

        Iterator<Map.Entry<String, Map<String, Double>>> parent = dicCodeToIndex.entrySet().iterator();
        while (parent.hasNext()) {
            Map.Entry<String, Map<String, Double>> parentPair = parent.next();
            list.add(generateData((parentPair.getValue()).entrySet().iterator()));
            titles.add(parentPair.getKey());
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list, labels, titles);
        lv.setAdapter(cda);

        return rootFragment;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private BarData generateData(Iterator<Map.Entry<String, Double>> child) {
        List list = new ArrayList();
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        int i = 0;
        while (child.hasNext()) {
            Map.Entry childPair = child.next();
            double val = Double.valueOf(childPair.getValue().toString());
            entries.add(new BarEntry(i++, (float) val));
            list.add(childPair.getKey().toString());
            child.remove(); // avoids a ConcurrentModificationException
        }

        BarDataSet d = new BarDataSet(entries, "");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<IBarDataSet>();
        sets.add(d);


        BarData cd = new BarData(sets);
        labels.add(list);
        //cd.setBarWidth(0.9f);
        return cd;
    }


}
