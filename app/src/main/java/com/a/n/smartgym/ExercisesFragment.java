package com.a.n.smartgym;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a.n.smartgym.BLE.BluetoothLeService;
import com.a.n.smartgym.Helpers.CircularProgressBar;
import com.a.n.smartgym.Helpers.LayoutTouchListener;
import com.a.n.smartgym.Listener.GestureListener;
import com.a.n.smartgym.Objects.LastExercise;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.SetsRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ExercisesFragment extends Fragment implements
        View.OnClickListener,
        CircularProgressBar.ProgressAnimationListener,
        GestureListener {

    private static final String TAG = ExercisesFragment.class.getSimpleName();
    private Button btnFinish;
    private FirebaseAuth mAuth;
    private String CurrentExercisesId;
    private String CurrentVisitId;
    private Sets mCurrentSet;
    private Context activity;
    private String mCurrentWeight = "0", mCurrentRepetition = "0", mCalculatedWeight = "0", mCurrentDirection = "0";
    private Handler mHandler = new Handler();
    private int mZeroValueCounter;
    private CircularProgressBar mSets, mRepetition, mSeconds, mWeightRatio, mCalcWeight;
    private LinearLayout linearLayout, mLinerLayoutContainer, mExamplelinearLayout, mMainLinearLayout;
    private TextView mTextViewSet, mTextViewRep, mTextViewWeight, mTextViewDate;
    private TabHost host;
    private int mNumberOfSetsCounter;
    private int mNumberOfSecondssCounter;
    private Typeface mTypeface;
    private double mSettingsWeight;
    private String mSettingsReps,mSettingsSets,mSettingsSeconds;

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


    // Handles various events fired by the Service.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        mHandler = new Handler();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sharedPreferences != null){
            int reps = sharedPreferences.getInt(getString(R.string.key_reps), Integer.parseInt(getString(R.string.default_reps)));
            int sets = sharedPreferences.getInt(getString(R.string.key_sets), Integer.parseInt(getString(R.string.default_sets)));
            int seconds = sharedPreferences.getInt(getString(R.string.key_seconds), Integer.parseInt(getString(R.string.default_seconds)));
            mSettingsWeight = sharedPreferences.getInt(getString(R.string.key_weight), Integer.parseInt(getString(R.string.default_weight)));
            mSettingsSeconds = seconds+"";
            mSettingsReps = reps+"";
            mSettingsSets = sets+"";
        }

        mTicker.run();


    }


    private void ToIntArray(String val) {
        String[] stringArray = val.split(",");
        if (stringArray.length != 4) return;

        mCalculatedWeight = stringArray[2];
        if (!stringArray[1].equals(mCurrentRepetition)) {
            mCurrentRepetition = stringArray[1];
            if (!mCurrentRepetition.equals("0")) {
                int progress = Integer.parseInt(mCurrentRepetition);
                mRepetition.setProgress(progress);
                mRepetition.setTitle(progress + "/" +mSettingsReps);
                StartNewSetInstance(progress, Integer.parseInt(mCalculatedWeight));
                Log.d(TAG, "AddSet");

            }
        }

        mCurrentWeight = stringArray[0];
        mCurrentDirection = stringArray[3];


        int calc = CalcBodyRatio(mSettingsWeight, Double.parseDouble(mCalculatedWeight));
        mWeightRatio.setProgress(calc);
        mWeightRatio.setTitle(calc + "%");
        mCalcWeight.setProgress(Integer.parseInt(mCalculatedWeight));
        mCalcWeight.setTitle(Integer.parseInt(mCalculatedWeight)+"");

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

    private int CalcBodyRatio(double weight, double plate) {
        return new Double(plate / weight * 100).intValue();
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

    private void StartNewSetInstance(int count, int weight) {
        if (mCurrentSet == null) {
            mSeconds.removeAnimation();
            mSeconds.setProgress(0);
            mSeconds.setTitle("0/"+mSettingsSeconds);
            mCurrentSet = new Sets();
            mCurrentSet.setSetid(UUID.randomUUID().toString());
            mCurrentSet.setexerciseid(CurrentExercisesId);
        }

        mCurrentSet.setCount(count);
        mCurrentSet.setWeight(weight);

    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootFragment = inflater.inflate(R.layout.activity_main2, null);

        mTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/nir.ttf");

        mSets = (CircularProgressBar) rootFragment.findViewById(R.id.pb_sets);
        mRepetition = (CircularProgressBar) rootFragment.findViewById(R.id.pb_repetition);
        mRepetition.setMax(10);
        mRepetition.setTitle("0/" + mSettingsReps);
        mRepetition.setTitleFontSize(60);
        mRepetition.setSubTitleFontSize(40);

        mSets.setMax(3);
        mSets.setTitle("0/"+mSettingsSets);
        mSets.setTitleFontSize(60);
        mSets.setSubTitleFontSize(40);

        mCalcWeight = (CircularProgressBar) rootFragment.findViewById(R.id.pb_calc_weight);
        mCalcWeight.setMax(180);
        mWeightRatio = (CircularProgressBar) rootFragment.findViewById(R.id.pb_weight_ratio);
        mWeightRatio.setMax(100);


        mSeconds = (CircularProgressBar) rootFragment.findViewById(R.id.pb_seconds);
        mSeconds.setSubTitleFontSize(40);
        mSeconds.setTitleFontSize(60);
        mSeconds.setMax(60);

        btnFinish = (Button) rootFragment.findViewById(R.id.btn_ex_finish);
        btnFinish.setOnClickListener(this);
        Controllers(true);

        linearLayout = (LinearLayout) rootFragment.findViewById(R.id.ll);
        linearLayout.setOnTouchListener(new LayoutTouchListener(this));
        mLinerLayoutContainer = (LinearLayout) rootFragment.findViewById(R.id.linear_parent);
        mExamplelinearLayout = (LinearLayout) rootFragment.findViewById(R.id.example);
        mMainLinearLayout = (LinearLayout) rootFragment.findViewById(R.id.main);
        mTextViewSet = (TextView) rootFragment.findViewById(R.id.tv_sets);
        mTextViewWeight = (TextView) rootFragment.findViewById(R.id.tv_weight);
        mTextViewRep = (TextView) rootFragment.findViewById(R.id.tv_rep);
        mTextViewDate = (TextView) rootFragment.findViewById(R.id.tv_date);

        host = (TabHost) rootFragment.findViewById(R.id.tab_host);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Tab One");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Tab Two");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Tab Three");
        host.addTab(spec);


        if (getArguments() != null) {

            CurrentExercisesId = UUID.randomUUID().toString();
            Muscle current = getArguments().getParcelable("muscle");
            //Tag tag = getArguments().getParcelable("tag");
            //getMessagesFromTag(tag);


            if (current != null) {
                try {
//                    mName.setText(current.getName());
//                    mPrimaryMuscle.setText(current.getMain());
//                    mSecondaryMuscle.setText(current.getSecondary());
//                    mInstruction.setText(current.getDescription());
                    //Picasso.with(getContext()).load(current.getImage()).into(mImage);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }

            }

            CurrentVisitId = getArguments().getString("uuid");


            if (!CurrentExercisesId.isEmpty() && current != null && !CurrentVisitId.isEmpty()) {
                BuildLastExString(current.getName());
                setNewExercise(CurrentExercisesId, CurrentVisitId, current.getName());
                StartNewSetInstance(0, 0);

                //mID.setText(ScanResult);
            }


        }
        return rootFragment;
    }

    private void BuildLastExString(String name) {
        ArrayList<LastExercise> lastExercise = new ExerciseRepo().getLastExercise(mAuth.getCurrentUser().getUid(), name);



        if (lastExercise != null && lastExercise.size() > 0) {
            mTextViewDate.setText(lastExercise.get(0).getDate());
            mTextViewWeight.setTypeface(mTypeface);
            mTextViewRep.setTypeface(mTypeface);
            mTextViewSet.setTypeface(mTypeface);
            mTextViewDate.setTypeface(mTypeface);

            for (int i = 0; i < lastExercise.size(); i++) {
                LastExercise current = lastExercise.get(i);

                buildview(current);

            }
        }
        else
        {
            mTextViewRep.setText("");
            mTextViewSet.setText("");
            mTextViewWeight.setText("");
        }
    }

    private void buildview(LastExercise ex) {

        LinearLayout layout2 = new LinearLayout(getContext());
        layout2.setLayoutParams(mExamplelinearLayout.getLayoutParams());
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout2.setGravity(Gravity.CENTER);

        for (int i = 0; i < 3; i++) {
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(mTextViewSet.getLayoutParams());
            textView.setTypeface(mTypeface);
            textView.setGravity(mTextViewSet.getGravity());
            textView.setTextColor(mTextViewSet.getTextColors());
            textView.setTextSize(15);
            textView.setWidth(mTextViewSet.getWidth());
            switch (i) {
                case 0:
                    textView.setText(ex.getSets() + "");
                    break;
                case 1:
                    textView.setText(ex.getCount() + "");
                    break;
                case 2:
                    textView.setText(ex.getWeight() + "");
                    break;
            }
            layout2.addView(textView);

        }


        mLinerLayoutContainer.addView(layout2);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
        mHandler.removeCallbacks(mTicker);
    }

    private void closeFragment() {

        try {
            ((MainActivity) activity).performIdentifierAction(R.id.device_day_average); //finisdhed
        } catch (ClassCastException cce) {

        }

    }


    private void stop() {
        try {
            ((MainActivity) activity).closeBLE(); //finisdhed
        } catch (ClassCastException cce) {

        }
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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private Drawable getDrawableForSdkVersion(String name) {

        int resId = getResources().getIdentifier(name, "drawable", getContext().getPackageName());

        Drawable drawable = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            drawable = getResources().getDrawable(resId, null);
        } else {
            drawable = getResources().getDrawable(resId);
        }

        return drawable;
    }


    private void Controllers(boolean b) {
        btnFinish.setEnabled(b);
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

    private void updateExerciseEndTime() {
        if (CurrentExercisesId != null) {
            ExerciseRepo exerciseRepo = new ExerciseRepo();
            exerciseRepo.update(CurrentExercisesId, System.currentTimeMillis());
            Log.d(TAG, "End time updated");

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ex_finish:
                finishExersise();
                break;

        }
    }

    private void getMessagesFromTag(final Tag tag) {

        new Thread(new Runnable() {
            public void run() {
                Ndef ndef = Ndef.get(tag);

                try {
                    while (true) {
                        try {
                            Thread.sleep(3000);

                            ndef.connect();
                            NdefMessage msg = ndef.getNdefMessage();

                            // TODO: do something

                        } catch (IOException e) {
                            Log.d(TAG, "tag is gone");
                            finishExersise();
                            // if the tag is gone we might want to end the thread:
                            break;
                        } catch (FormatException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                ndef.close();
                            } catch (Exception e) {
                            }
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    private void finishExersise() {
        InsertToDataBase();
        updateExerciseEndTime();
        mSeconds.removeAnimation();
        stop();
        closeFragment();
    }

    private void InsertToDataBase() {
        if (mCurrentSet != null && mCurrentSet.getCount() > 0) {
            mSets.setProgress(++mNumberOfSetsCounter);
            mSets.setTitle(mNumberOfSetsCounter + "/"+mSettingsSets);
            mRepetition.setTitle("0/"+mSettingsReps);
            mRepetition.setProgress(0);
            SetsRepo setsRepo = new SetsRepo(getContext());
            setsRepo.insert(mCurrentSet);
            Toast.makeText(activity, mCurrentSet.getCount() + " " + getString(R.string.database_update), Toast.LENGTH_SHORT).show();
            Log.d(TAG, mCurrentSet.getCount() + " Sets Inserted to DataBase");
            mCurrentSet = null;

            mSeconds.animateProgressTo(0, 60, new CircularProgressBar.ProgressAnimationListener() {
                @Override
                public void onAnimationStart(View view) {
                    Log.d("dsf", view.getId() + "");
                }

                @Override
                public void onAnimationFinish(View view) {
                    Log.d("dsf", view.getId() + "");

                }

                @Override
                public void onAnimationProgress(int progress, View view) {
                    Log.d("dsf", view.getId() + "");
                    mSeconds.setTitle(progress + "/"+mSettingsSeconds);


                }
            });

        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);

        return intentFilter;
    }


    @Override
    public void onAnimationStart(View view) {
        switch (view.getId()) {
            case R.id.pb_repetition:
                break;
            case R.id.pb_sets:
                break;

        }
    }

    @Override
    public void onAnimationFinish(View view) {

    }

    @Override
    public void onAnimationProgress(int progress, View view) {

    }


    @Override
    public void onRightToLeftSwipe() {
        int current = host.getCurrentTab() - 1;
        if (current < 0) current += 2;
        host.setCurrentTab(current % 2);
        Log.d("============= ", current + " onLeftToRightSwipe");
    }

    @Override
    public void onLeftToRightSwipe() {
        int current = host.getCurrentTab() + 1;
        host.setCurrentTab(current % 2);
        Log.d("============= ", current + " onRightToLeftSwipe");
    }


}