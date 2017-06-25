package com.a.n.smartgym.Graphs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.a.n.smartgym.Adapter.ChartDataAdapter;
import com.a.n.smartgym.Adapter.ViewPagerAdapter;
import com.a.n.smartgym.Object.DailyAverage;
import com.a.n.smartgym.R;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by DAT on 9/1/2015.
 */
public class DayAverageFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private LinkedHashMap<String, LinkedHashMap<String, Double>> dicCodeToIndex;
    private List<List<String>> labels = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private ListView mListView;
    private int selectedTabPosition;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dicCodeToIndex = new LinkedHashMap<>();

        ExactDataFromDB();

        View view = inflater.inflate(R.layout.day_avrage_fragment, container, false);

        //SetupScreenComponents(view, getResources().getConfiguration().orientation);

        GetIdPortrait(view);
        setEvents();
        extractData1();

        return view;
    }


//    private void SetupScreenComponents(View view, int orientation) {
//
//
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            GetIdPortrait(view);
//            setEvents();
//            extractData1();
//        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            GetIdLandscape(view);
//            extractData3();
//
//        }
//    }


//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        populateViewForOrientation(inflater, (ViewGroup) getView(), newConfig);
//    }


//    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup, Configuration newConfig) {
//
//        viewGroup.removeAllViewsInLayout();
//        View subview = inflater.inflate(R.layout.day_avg, viewGroup);
//
//        SetupScreenComponents(subview, newConfig.orientation);
//    }


    private BarData generateData(Iterator<Map.Entry<String, Double>> child) {
        List list = new ArrayList();
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        int i = 0;
        while (child.hasNext()) {
            Map.Entry childPair = child.next();
            double val = Double.valueOf(childPair.getValue().toString());
            entries.add(new BarEntry(i++, (float) val));
            list.add(childPair.getKey().toString());
            //child.remove(); // avoids a ConcurrentModificationException
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

    private void extractData1() {


        Iterator<Map.Entry<String, LinkedHashMap<String, Double>>> parent = dicCodeToIndex.entrySet().iterator();
        while (parent.hasNext()) {
            ArrayList<DailyAverage> temp = new ArrayList<>();
            Map.Entry<String, LinkedHashMap<String, Double>> parentPair = parent.next();
            Iterator<Map.Entry<String, Double>> mIterator = parentPair.getValue().entrySet().iterator();
            int i = 0;
            while (mIterator.hasNext()) {
                Map.Entry childPair = mIterator.next();
                String name = childPair.getKey().toString();
                double val = Double.valueOf(childPair.getValue().toString());
                //temp.add(new DailyAverage(name, val));
            }
            addPage(parentPair.getKey().toString(), temp);

        }

    }

    //get all data from database
    private void extractData3() {
        ArrayList<BarData> list = new ArrayList<BarData>();

        Iterator<Map.Entry<String, LinkedHashMap<String, Double>>> parent = dicCodeToIndex.entrySet().iterator();
        while (parent.hasNext()) {
            Map.Entry<String, LinkedHashMap<String, Double>> parentPair = parent.next();
            list.add(generateData((parentPair.getValue()).entrySet().iterator()));
            titles.add(parentPair.getKey());
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list, labels, titles);
        mListView.setAdapter(cda);

    }

    private void ExactDataFromDB() {

        ArrayList<DailyAverage> DailyAverage = new ExerciseRepo().getAllDaysAverages(FirebaseAuth.getInstance().getCurrentUser().getUid());


        Iterator iterator = DailyAverage.iterator();
        while (iterator.hasNext()) {
            DailyAverage temp = (DailyAverage) iterator.next();
            LinkedHashMap<String, Double> in = dicCodeToIndex.get(temp.getDate());
            if (in != null) {
                //in.put(temp.getMachine_name(), temp.getAverage());
                dicCodeToIndex.put(temp.getDate(), in);

            } else {
                LinkedHashMap<String, Double> inner = new LinkedHashMap<>();
                //inner.put(temp.getMachine_name(), temp.getAverage());
                dicCodeToIndex.put(temp.getDate(), inner);
            }

        }
    }


    private void GetIdPortrait(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);
        adapter = new ViewPagerAdapter(getFragmentManager(), getActivity(), viewPager, tabLayout);
        viewPager.setAdapter(adapter);
    }

    private void GetIdLandscape(View rootFragment) {
        mListView = (ListView) rootFragment.findViewById(R.id.listView_avg);
    }


    private void setEvents() {

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPager.setCurrentItem(tab.getPosition());
                selectedTabPosition = viewPager.getCurrentItem();
                Log.d("Selected", "Selected " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d("Unselected", "Unselected " + tab.getPosition());
            }
        });
    }

    public void addPage(String pagename, ArrayList<DailyAverage> data) {
        Bundle bundle = new Bundle();
        bundle.putString("date", pagename);
        bundle.putParcelableArrayList("data", data);
        DayAverageFragmentTab fragmentChild = new DayAverageFragmentTab();
        fragmentChild.setArguments(bundle);
        adapter.addFrag(fragmentChild, pagename);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0) tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(0);
        setupTabLayout();
    }

    public void setupTabLayout() {
        selectedTabPosition = viewPager.getCurrentItem();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i));
        }
    }
}
