package com.a.n.smartgym.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.a.n.smartgym.Adapter.ChartDataAdapter;
import com.a.n.smartgym.Adapter.ViewPagerAdapter;
import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.a.n.smartgym.DBRepo.MuscleExerciseRepo;
import com.a.n.smartgym.Graphs.DayAverageFragmentTab;
import com.a.n.smartgym.Object.DailyAverage;
import com.a.n.smartgym.Object.LastExercise;
import com.a.n.smartgym.R;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import devlight.io.library.ArcProgressStackView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by DAT on 9/1/2015.
 */
public class MyDayFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int selectedTabPosition;
    private int[] colors;
    private int[] bgColors;
    MyDayProgressFragment myDayProgressFragment;
    List<Long> mCurrentValues;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myday, container, false);
        GetIdPortrait(view);
        setEvents();

        return view;
    }



    private void GetIdPortrait(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);
        adapter = new ViewPagerAdapter(getFragmentManager(), getActivity(), viewPager, tabLayout);
        viewPager.setAdapter(adapter);
        myDayProgressFragment = (MyDayProgressFragment)getChildFragmentManager().findFragmentById(R.id.my_day_progress);
        mCurrentValues = new ArrayList<>();
        adjustColors();
        addFragmentToTab();

    }

    private void setEvents() {

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPager.setCurrentItem(tab.getPosition());
                selectedTabPosition = viewPager.getCurrentItem();
                Log.d("Selected", "Selected " + tab.getPosition());
                tabLayout.setEnabled(false);

                myDayProgressFragment.setValueAnimator(mCurrentValues.get(tab.getPosition()));
                myDayProgressFragment.SetCurrentIndex(tab.getPosition());
                myDayProgressFragment.startAnimation();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.setEnabled(true);

                    }
                },5000);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d("Unselected", "Unselected " + tab.getPosition());
            }
        });
    }

    public void addPage(String pagename, ArrayList<MuscleExercise> data) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", data);
        DynamicFragmentTab fragmentChild = new DynamicFragmentTab();
        fragmentChild.setArguments(bundle);
        adapter.addFrag(fragmentChild, pagename);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0) tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(0);
        setupTabLayout();
    }

    private void addFragmentToTab(){
        final ArrayList<ArcProgressStackView.Model> models = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        int index = 0;
        Map<String,ArrayList<MuscleExercise>> exercises = new MuscleExerciseRepo().getDayPlan(dayOfTheWeek.toLowerCase());
        ArrayList<MuscleExercise> muscleExerciseList = new ArrayList<>();
        myDayProgressFragment.setModelCount(exercises.keySet().size());

        for (String key : exercises.keySet()) {
            models.add(new ArcProgressStackView.Model(key.toUpperCase(), 1, bgColors[index] , colors[index]));
            // gets the value
             muscleExerciseList = exercises.get(key);
            // checks for null value
            if (muscleExerciseList != null) {
                // iterates over String elements of value
                addPage(key.toUpperCase(), muscleExerciseList);

            }
            index++;
        }

        myDayProgressFragment.AddModel(models);
        for (int i = 0; i < index; i++) {

            long random = getRandom(0,100);
            myDayProgressFragment.SetProgress(i,random);
            mCurrentValues.add(random);

        }
        myDayProgressFragment.setUpAnimation();
        myDayProgressFragment.startAnimation();

    }

    public void setupTabLayout() {
        selectedTabPosition = viewPager.getCurrentItem();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i));
        }


    }

    public void adjustColors() {
        final String[] stringColors = getResources().getStringArray(R.array.progress);
        final String[] stringBgColors = getResources().getStringArray(R.array.bg);

        colors = new int[stringColors.length];
        bgColors = new int[stringBgColors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.parseColor(stringColors[i]);
            bgColors[i] = Color.parseColor(stringBgColors[i]);
        }
    }

    public static int getRandom(int from, int to) {
        if (from < to)
            return from + new Random().nextInt(Math.abs(to - from));
        return from - new Random().nextInt(Math.abs(to - from));
    }

}
