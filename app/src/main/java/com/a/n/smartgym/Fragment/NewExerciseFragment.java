package com.a.n.smartgym.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.Adapter.ViewPagerAdapter;
import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.a.n.smartgym.DBRepo.MuscleExerciseRepo;
import com.a.n.smartgym.Object.LastExercise;
import com.a.n.smartgym.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import devlight.io.library.ArcProgressStackView;

/**
 * Created by DAT on 9/1/2015.
 */
public class NewExerciseFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int selectedTabPosition;
    private int[] colors;
    private int[] bgColors;
    ExerciseProgressFragment mExerciseProgressFragment;
    List<Integer> mCurrentValues;
    private static final String TAG = NewExerciseFragment.class.getSimpleName();
    private ExerciseFragmentTab mExerciseFragmentTab;
    private ExerciseFragmentTab mExerciseFragmentTab2;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_exercise_new, container, false);
        GetIdPortrait(view);
        setEvents();

        return view;
    }


    private void GetIdPortrait(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);
        adapter = new ViewPagerAdapter(getFragmentManager(), getActivity(), viewPager, tabLayout);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        mExerciseProgressFragment = (ExerciseProgressFragment) getChildFragmentManager().findFragmentById(R.id.exercise_progress);
        mCurrentValues = new ArrayList<>();
        adjustColors();
        mExerciseFragmentTab = new ExerciseFragmentTab();
        mExerciseFragmentTab2 = new ExerciseFragmentTab();
        addPage("TAB1",mExerciseFragmentTab);
        addPage("TAB2",mExerciseFragmentTab2);


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
                mExerciseFragmentTab.buildview();


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d("Unselected", "Unselected " + tab.getPosition());
            }
        });
    }

    public void addPage(String pagename, Fragment fragment) {
        adapter.addFrag(fragment, pagename);
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
