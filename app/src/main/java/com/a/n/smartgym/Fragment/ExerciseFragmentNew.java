package com.a.n.smartgym.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import com.a.n.smartgym.Activity.MainActivity;
import com.a.n.smartgym.Adapter.ViewPagerAdapter;
import com.a.n.smartgym.DBModel.Exercise;
import com.a.n.smartgym.DBModel.Muscle;
import com.a.n.smartgym.DBModel.Sets;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.a.n.smartgym.DBRepo.PlanMuscleRepo;
import com.a.n.smartgym.DBRepo.SetsRepo;
import com.a.n.smartgym.Object.LastExercise;
import com.a.n.smartgym.R;
import com.a.n.smartgym.Services.BluetoothLeService;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by DAT on 9/1/2015.
 */
public class ExerciseFragmentNew extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int selectedTabPosition;
    private String mCurrentWeight = "0", mCurrentRepetition = "0", mCalculatedWeight = "0", mCurrentDirection = "0";
    private int[] colors;
    private int[] bgColors;
    ExerciseProgressFragment mExerciseProgressFragment;
    private String CurrentExercisesId;
    private String CurrentVisitId;
    private Handler mHandler;
    private int mZeroValueCounter;
    private Sets mCurrentSet;
    List<Integer> mCurrentValues;
    private int mNumberOfSecondssCounter;
    private Context activity;
    private static final String TAG = ExerciseFragmentNew.class.getSimpleName();
    private ExerciseFragmentTab mExerciseFragmentTab;
    private ExerciseFragmentTab mExerciseFragmentTabLastSession;


    private final Runnable mTicker = new Runnable() {
        public void run() {
            //user interface interactions and updates on screen
            if (Integer.parseInt(mCurrentWeight) < 3) {
                mZeroValueCounter++;
                if (mZeroValueCounter > 3) { //3 seconds pasted throw data to database
                    InsertToDataBase();

                }
            } else {
                mNumberOfSecondssCounter = 0;
                mZeroValueCounter = 0;
            }

            mHandler.postDelayed(mTicker, 1000);


        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                showProgressDialog(null, false);
                String extra = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                ToIntArray(extra);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                showProgressDialog(null, false);
                Toast.makeText(activity, getString(R.string.disconnected_device), Toast.LENGTH_SHORT).show();
                finishExersise();
            }
        }
    };


    private void InsertToDataBase() {
        if (mCurrentSet != null && mCurrentSet.getCount() > 0) {
//            mSets.setProgress(++mNumberOfSetsCounter);
//            mSets.setTitle(mNumberOfSetsCounter + "/"+mSettingsSets);
//            mRepetition.setTitle("0/"+mSettingsReps);
//            mRepetition.setProgress(0);
            SetsRepo setsRepo = new SetsRepo();
            setsRepo.insert(mCurrentSet);
            Toast.makeText(activity, mCurrentSet.getCount() + " " + getString(R.string.database_update), Toast.LENGTH_SHORT).show();
            Log.d(TAG, mCurrentSet.getCount() + " Sets Inserted to DataBase");
            mCurrentSet = null;


        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = context;
        activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        showProgressDialog(getString(R.string.waiting), true);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.unregisterReceiver(mGattUpdateReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_exercise_new, container, false);
        GetIdPortrait(view);
        setEvents();
        getArgs(getArguments());

        return view;
    }

    private void ToIntArray(String val) {
        String[] stringArray = val.split(",");
        if (stringArray.length != 4) return;

        mCalculatedWeight = stringArray[2];
        if (!stringArray[1].equals(mCurrentRepetition)) {
            mCurrentRepetition = stringArray[1];
            if (!mCurrentRepetition.equals("0")) {
                int progress = Integer.parseInt(mCurrentRepetition);
//                mRepetition.setProgress(progress);
//                mRepetition.setTitle(progress + "/" +mSettingsReps);
                StartNewSetInstance(progress, Integer.parseInt(mCalculatedWeight));
                Log.d(TAG, "AddSet");

            }
        }

        mCurrentWeight = stringArray[0];
        mCurrentDirection = stringArray[3];


//        int calc = CalcBodyRatio(mSettingsWeight, Double.parseDouble(mCalculatedWeight));
//        mWeightRatio.setProgress(calc);
//        mWeightRatio.setTitle(calc + "%");
//        mCalcWeight.setProgress(Integer.parseInt(mCalculatedWeight));
//        mCalcWeight.setTitle(Integer.parseInt(mCalculatedWeight)+"");

//        mWeight.setText(mCalculatedWeight);
//        mCounter.setText(mCurrentRepetition);
//        mCurrentWeightTV.setText(mCurrentWeight);


        if (mCurrentDirection.equals("-1")) {
            //mArrowImage.setImageDrawable(getDrawableForSdkVersion("ic_arrow_downward_black_48dp"));

        } else if (mCurrentDirection.equals("1")) {
            //mArrowImage.setImageDrawable(getDrawableForSdkVersion("ic_arrow_upward_black_48dp"));

        }
        //mArrowImage.setImageDrawable(null);

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
        mExerciseFragmentTabLastSession = new ExerciseFragmentTab();
        addPage("TAB1",mExerciseFragmentTab);




    }

    private void showProgressDialog(String msg, boolean show) {
        if (show) {
            try {
                ((MainActivity) activity).showProgressDialog(msg); //finisdhed
            } catch (ClassCastException cce) {

            }
        } else {
            try {
                ((MainActivity) activity).hideProgressDialog(); //finisdhed
            } catch (ClassCastException cce) {

            }
        }

    }

    private void getArgs(Bundle arg)
    {
        if (getArguments() != null) {

            CurrentExercisesId = UUID.randomUUID().toString();
            Muscle current = getArguments().getParcelable("muscle");
            //Tag tag = getArguments().getParcelable("tag");
            //getMessagesFromTag(tag);
            mExerciseProgressFragment.Animate(0,Integer.parseInt(current.getNum_sets()),Integer.parseInt(current.getNum_reps()),1000);

            CurrentVisitId = getArguments().getString("uuid");


            if (!CurrentExercisesId.isEmpty() && current != null && !CurrentVisitId.isEmpty()) {
                BuildLastExString(current.getName());
                setNewExercise(CurrentExercisesId, CurrentVisitId, current.getName());
                StartNewSetInstance(0, 0);

            }


        }
    }

    private void BuildLastExString(String name) {
        ArrayList<LastExercise> lastExercise = new ExerciseRepo().getLastExercise(FirebaseAuth.getInstance().getCurrentUser().getUid(), name);
        for (int i = 0; i < lastExercise.size(); i++) {
            LastExercise last = lastExercise.get(i);
            mExerciseFragmentTabLastSession.buildview(i+1+"",last.getCount()+"",last.getWeight()+"");

        }
    }

    private void StartNewSetInstance(int count, int weight) {
        if (mCurrentSet == null) {
           // mSeconds.removeAnimation();
            //mSeconds.setProgress(0);
            //mSeconds.setTitle("0/"+mSettingsSeconds);
            mCurrentSet = new Sets();
            mCurrentSet.setSetid(UUID.randomUUID().toString());
            mCurrentSet.setexerciseid(CurrentExercisesId);
        }

        mCurrentSet.setCount(count);
        mCurrentSet.setWeight(weight);

    }

    private void setNewExercise(String uuid, String session, String scan) {
        ExerciseRepo exerciseRepo = new ExerciseRepo();
        Exercise exercise = new Exercise();
        exercise.setexerciseid(uuid);
        exercise.setVisitid(session);
        exercise.setMachinename(scan);
        exercise.setStart(System.currentTimeMillis());
        exerciseRepo.insert(exercise);
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

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);

        return intentFilter;
    }

    private void finishExersise() {
        InsertToDataBase();
        updateExerciseEndTime();
        //mSeconds.removeAnimation();
        stop();
        closeFragment();
    }

    private void closeFragment() {

        try {
            ((MainActivity) activity).performIdentifierAction(R.id.my_day); //finisdhed
        } catch (ClassCastException cce) {

        }

    }


    private void stop() {
        try {
            ((MainActivity) activity).closeBLE(); //finisdhed
        } catch (ClassCastException cce) {

        }
    }

    private void updateExerciseEndTime() {
        if (CurrentExercisesId != null) {
            ExerciseRepo exerciseRepo = new ExerciseRepo();
            exerciseRepo.update(CurrentExercisesId, System.currentTimeMillis());
            Log.d(TAG, "End time updated");

        }
    }

}
