package com.a.n.smartgym.Graphs;

import android.content.res.Configuration;
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
import com.a.n.smartgym.Quary.DailyAverage;
import com.a.n.smartgym.R;
import com.a.n.smartgym.repo.ExerciseRepo;
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
public class TrendAverageFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private ArrayList<DailyAverage> mDailyAverage;
    private LinkedHashMap<String, LinkedHashMap<String, Double>> dicCodeToIndex;
    private String mGraphType;
    private List<List<String>> labels = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private ListView mListView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trend_avg, container, false);

        getIDs(view);
        setEvents();
        extractData2();


        return view;
    }



    private void extractData2() {
        dicCodeToIndex = new LinkedHashMap <String, LinkedHashMap<String, Double>>();

        mDailyAverage = new ExerciseRepo().getAllDaysAverages(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Iterator iterator = mDailyAverage.iterator();
        while (iterator.hasNext()) {
            DailyAverage temp = (DailyAverage) iterator.next();
            LinkedHashMap<String, Double> in = dicCodeToIndex.get(temp.getMachine_name());
            if (in != null) {
                in.put(temp.getDate(), temp.getAverage());
                dicCodeToIndex.put(temp.getMachine_name(), in);

            } else {
                LinkedHashMap<String, Double> inner = new LinkedHashMap<>();
                inner.put(temp.getDate(), temp.getAverage());
                dicCodeToIndex.put(temp.getMachine_name(), inner);
            }

        }

        Iterator<Map.Entry<String, LinkedHashMap<String, Double>>> parent = dicCodeToIndex.entrySet().iterator();
        while (parent.hasNext()) {
            mDailyAverage = new ArrayList<>();
            Map.Entry<String, LinkedHashMap<String, Double>> parentPair = parent.next();
            Iterator<Map.Entry<String, Double>> mIterator = parentPair.getValue().entrySet().iterator();
            while (mIterator.hasNext()) {
                Map.Entry childPair = mIterator.next();
                String name = childPair.getKey().toString();
                double val = Double.valueOf(childPair.getValue().toString());
                mDailyAverage.add(new DailyAverage(name,val));
            }
            if (mDailyAverage.size()>2)
            addPage(parentPair.getKey().toString());

        }

    }


    private void getIDs(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);
        adapter = new ViewPagerAdapter(getFragmentManager(), getActivity(), viewPager, tabLayout);
        viewPager.setAdapter(adapter);
    }

    int selectedTabPosition;

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

    public void addPage(String pagename) {
        Bundle bundle = new Bundle();
        bundle.putString("date", pagename);
        bundle.putParcelableArrayList("data",mDailyAverage);
        TrendAverageFragmentTab fragmentChild = new TrendAverageFragmentTab();
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
