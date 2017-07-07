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
import com.a.n.smartgym.Views.CustomTabLayout;
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
    private CustomTabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int selectedTabPosition;
    private int[] colors;
    private int[] bgColors;
    MyDayProgressFragment myDayProgressFragment;
    List<Integer> mCurrentValues;
    private Map<String, ArrayList<MuscleExercise>> exercises;
    private static final String TAG = MyDayFragment.class.getSimpleName();



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
        exercises = new MuscleExerciseRepo().getDayPlan(getDayOfWeek());
        tabLayout = (CustomTabLayout) view.findViewById(R.id.my_tab_layout);
        tabLayout.setDividerFactor(exercises.keySet().size());
        adapter = new ViewPagerAdapter(getFragmentManager(), getActivity(), viewPager, tabLayout);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        myDayProgressFragment = (MyDayProgressFragment) getChildFragmentManager().findFragmentById(R.id.my_day_progress);
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
                }, 5000);

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

    private String getDayOfWeek(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        return sdf.format(d).toLowerCase();
    }

    private void addFragmentToTab() {

        final ArrayList<ArcProgressStackView.Model> models = new ArrayList<>();

        int index = 0;
        long MainMuscleDonePercentage = 0;

        ArrayList<MuscleExercise> muscleExerciseList = new ArrayList<>();
        myDayProgressFragment.setModelCount(exercises.keySet().size());

        for (String key : exercises.keySet()) {
            models.add(new ArcProgressStackView.Model(key.toUpperCase(), 1, bgColors[index], colors[index]));
            // gets the value
            muscleExerciseList = exercises.get(key);

            // checks for null value
            if (muscleExerciseList != null) {
                // iterates over String elements of value
                mCurrentValues.add(AggregateSets(muscleExerciseList));

                addPage(key.toUpperCase(), muscleExerciseList);

            }
            index++;
        }
        myDayProgressFragment.setWidth((float)(index*65));
        myDayProgressFragment.AddModel(models);

        for (int i = 0; i < mCurrentValues.size(); i++){
            Log.d(TAG,mCurrentValues.get(i).toString());
            myDayProgressFragment.SetProgress(i, mCurrentValues.get(i));

        }

        myDayProgressFragment.setUpAnimation();
        myDayProgressFragment.startAnimation();

    }

    private int AggregateSets(ArrayList<MuscleExercise> muscleExerciseList) {
        int sumTodo = 0;
        int sumDone = 0;
        int RsumTodo = 0;
        int RsumDone = 0;

        for (MuscleExercise muscleExercise : muscleExerciseList) {
            sumDone = sumTodo = 0;
            ArrayList<LastExercise> lastExercise = new ExerciseRepo().getTodayExercise(FirebaseAuth.getInstance().getCurrentUser().getUid(), muscleExercise.getExerciseid());
            for (int i = 0; i < lastExercise.size(); i++) {
                sumDone += lastExercise.get(i).getCount() * lastExercise.get(i).getSets();
            }
            sumTodo += Integer.parseInt(muscleExercise.getNumberofreps()) * Integer.parseInt(muscleExercise.getNumberofsets());

            if (sumDone>sumTodo)
                RsumDone += sumTodo;
            else
                RsumDone+= sumDone;

            RsumTodo +=sumTodo;


        }
        Log.d(TAG,"100" + " * " + RsumDone +" / " + RsumTodo);
        return 100 * RsumDone / RsumTodo;
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
