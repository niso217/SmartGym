package com.a.n.smartgym;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.a.n.smartgym.BLE.BluetoothLeService;
import com.a.n.smartgym.Helpers.CircularProgressBar;
import com.a.n.smartgym.Helpers.LayoutTouchListener;
import com.a.n.smartgym.Objects.LastExercise;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.SetsRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ExercisesFragment extends Fragment implements View.OnClickListener,CircularProgressBar.ProgressAnimationListener {

    private static final String TAG = ExercisesFragment.class.getSimpleName();
    private Button btnFinish;
    private FirebaseAuth mAuth;
    private String CurrentExercisesId;
    private String CurrentVisitId;
    private int Counter;
    private Sets mCurrentSet;
    private boolean mInProgress;
    private boolean[] arr;
    //    private ImageView mImage, mArrowImage;
    //    private TextView mName, mPrimaryMuscle, mSecondaryMuscle, mInstruction, mCounter, mWeight, mCurrentWeightTV;
    long mAccelLast, mAccelCurrent;
    double mAccel;
    float[] mGravity;
    private SensorManager sensorManager;
    private boolean mBeenHere;
    private Context activity;
    private String mCurrentWeight = "0", mCurrentRepetition = "0", mCalculatedWeight = "0", mCurrentDirection = "0";
    private Handler mHandler = new Handler();
    private int mZeroValueCounter;
    private int mSameReapedCounter;
    private CircularProgressBar mSets,mRepetition,mBodyWeight;
    private LinearLayout linearLayout, mLinerLayoutContainer, mExamplelinearLayout,mMainLinearLayout;
    private TextView mTextViewSet,mTextViewRep,mTextViewWeight;
    private TabHost host;
    private int mNumberOfSetsCounter;
    private int mNumberOfSecondssCounter;
    private EditText mEditText;
    private ArrayList<LastExercise> mLastExercise;
    private TableLayout mTableLayout;


    private final Runnable mTicker = new Runnable() {
        public void run() {
            //user interface interactions and updates on screen
            if (Integer.parseInt(mCalculatedWeight) < 3) {
                mZeroValueCounter++;
                if (mZeroValueCounter > 3) { //3 seconds pasted throw data to database
                    InsertToDataBase();
                    //mBodyWeight.setProgress(++mNumberOfSecondssCounter);

                }
            } else{
                mNumberOfSecondssCounter = 0;
                //mBodyWeight.setProgress(mNumberOfSecondssCounter);
                //mBodyWeight.setTitle("0/60");
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
                mRepetition.setTitle(progress+ "/10");
                StartNewSetInstance(progress, Integer.parseInt(mCalculatedWeight));
                Log.d(TAG, "AddSet");

            }
        }

        mCurrentWeight = stringArray[0];

        mCurrentDirection = stringArray[3];

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
            mBodyWeight.removeAnimation();
            mBodyWeight.setProgress(0);
            mBodyWeight.setTitle("0/60");
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

        //mEditText = (EditText) rootFragment.findViewById(R.id.summary);
        //Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/nir.ttf");
        //mEditText.setTypeface(typeface);


//        mName = (TextView) rootFragment.findViewById(R.id.tv_ex_name_txt);
//        mPrimaryMuscle = (TextView) rootFragment.findViewById(R.id.tv_ex_primary_txt);
//        mSecondaryMuscle = (TextView) rootFragment.findViewById(R.id.tv_ex_secondary_txt);
//        mInstruction = (TextView) rootFragment.findViewById(R.id.tv_ex_instructions_txt);
//        mCounter = (TextView) rootFragment.findViewById(R.id.tv_ex_counter_txt);
//        mWeight = (TextView) rootFragment.findViewById(R.id.tv_ex_weight_txt);
//        mImage = (ImageView) rootFragment.findViewById(R.id.img_ex);
//        mArrowImage = (ImageView) rootFragment.findViewById(R.id.img_arrow);
//        mCurrentWeightTV = (TextView) rootFragment.findViewById(R.id.tv_current_weight);
        mSets = (CircularProgressBar) rootFragment.findViewById(R.id.pb_sets);
        mRepetition = (CircularProgressBar) rootFragment.findViewById(R.id.pb_repetition);
        mRepetition.setMax(10);
        mRepetition.setTitle("0/10");
        mRepetition.setTitleFontSize(60);
        mRepetition.setSubTitleFontSize(40);

        mSets.setMax(3);
        mSets.setTitle("0/3");
        mSets.setTitleFontSize(60);
        mSets.setSubTitleFontSize(40);

        mBodyWeight = (CircularProgressBar) rootFragment.findViewById(R.id.pb_bodyweight);
        mBodyWeight.setSubTitleFontSize(40);
        mBodyWeight.setTitleFontSize(60);
        mBodyWeight.setMax(60);

        btnFinish = (Button) rootFragment.findViewById(R.id.btn_ex_finish);
        btnFinish.setOnClickListener(this);
        Controllers(true);

        linearLayout = (LinearLayout) rootFragment.findViewById(R.id.ll);
        linearLayout.setOnTouchListener(new LayoutTouchListener(getActivity()));
        mLinerLayoutContainer = (LinearLayout) rootFragment.findViewById(R.id.linear_parent);
        mExamplelinearLayout = (LinearLayout) rootFragment.findViewById(R.id.example);
        mMainLinearLayout = (LinearLayout) rootFragment.findViewById(R.id.main);
        mTextViewSet = (TextView) rootFragment.findViewById(R.id.tv_sets);
        mTextViewWeight = (TextView) rootFragment.findViewById(R.id.tv_weight);
        mTextViewRep= (TextView) rootFragment.findViewById(R.id.tv_rep);

        host = (TabHost)rootFragment.findViewById(R.id.tab_host);
        host.setup();

        //mTableLayout = (TableLayout) rootFragment.findViewById(R.id.tablelayout);

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

    private void BuildLastExString(String name){
        StringBuilder sb = new StringBuilder();
        ArrayList<LastExercise> lastExercise = new ExerciseRepo().getLastExercise(mAuth.getCurrentUser().getUid(),name);
        if (lastExercise!=null && lastExercise.size()>0)
        {
//            sb.append(lastExercise.get(0).getDate());
//            sb.append(System.getProperty("line.separator"));
//            sb.append(System.getProperty("line.separator"));
//            sb.append(String.format("%-5s%-5s%-5s","Sets","Rep","Kg"));
//            sb.append(System.getProperty("line.separator"));

            for (int i = 0; i < lastExercise.size(); i++) {
                LastExercise current = lastExercise.get(i);

               // buildview(current);
                //sb.append(current.getSets());
               // sb.append(String.format("%-5s",current.getSets()));
               // sb.append(String.format("%-5s",current.getWeight()));
               // sb.append(String.format("%-5s",current.getWeight()));

               // sb.append(current.getCount());
               // sb.append(current.getWeight());
                //sb.append(System.getProperty("line.separator"));

               // android:layout_weight="0.3"
                //android:gravity="center"

//                TableRow row= new TableRow(getContext());
//                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
//                row.setLayoutParams(lp);
//
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                lp.weight = 0.3f;
//                lp.gravity = Gravity.CENTER;
//
//                TextView sets = new TextView(getContext());
//                sets.setText(current.getSets()+"");
//                sets.setTypeface(typeface);
//                sets.setLayoutParams(lp);
//                row.addView(sets);
//
//                TextView counts = new TextView(getContext());
//                counts.setText(current.getCount()+"");
//                counts.setTypeface(typeface);
//                counts.setLayoutParams(lp);
//                row.addView(counts);
//
//                TextView weight = new TextView(getContext());
//                weight.setText(current.getWeight()+"");
//                weight.setTypeface(typeface);
//                weight.setLayoutParams(lp);
//                row.addView(weight);
//
//                mTableLayout.addView(row,i+1);
            }
        }
       //return sb;
    }

    private void buildview(LastExercise ex){

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/nir.ttf");

        LinearLayout layout2 = new LinearLayout(getContext());
        layout2.setLayoutParams(mExamplelinearLayout.getLayoutParams());
        layout2.setOrientation(LinearLayout.HORIZONTAL);


        TextView tv1 = new TextView(getContext());
        tv1.setLayoutParams(mTextViewSet.getLayoutParams());
        tv1.setTypeface(typeface);

        tv1.setGravity(mTextViewSet.getGravity());
        tv1.setTextColor(mTextViewSet.getTextColors());
        tv1.setTextSize(15);
        tv1.setText(ex.getSets()+"");

        TextView tv2 = new TextView(getContext());
        tv2.setLayoutParams(mTextViewSet.getLayoutParams());
        tv2.setText(ex.getCount()+"");
        tv2.setTypeface(typeface);
        tv2.setGravity(mTextViewSet.getGravity());
        tv2.setTextColor(mTextViewSet.getTextColors());
        tv2.setTextSize(15);

        TextView tv3 = new TextView(getContext());
        tv3.setLayoutParams(mTextViewSet.getLayoutParams());
        tv3.setText(ex.getWeight()+"");
        tv3.setTypeface(typeface);
        tv3.setGravity(mTextViewSet.getGravity());
        tv3.setTextColor(mTextViewSet.getTextColors());
        tv3.setTextSize(15);

        mTextViewWeight.setTypeface(typeface);
        mTextViewRep.setTypeface(typeface);
        mTextViewSet.setTypeface(typeface);



        layout2.addView(tv1);
        layout2.addView(tv2);
        layout2.addView(tv3);

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
            ((MainActivity) activity).performIdentifierAction(); //finisdhed
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
        mBodyWeight.removeAnimation();
        stop();
        closeFragment();
    }

    private void InsertToDataBase() {
        if (mCurrentSet != null && mCurrentSet.getCount()>0) {
            mSets.setProgress(++mNumberOfSetsCounter);
            mSets.setTitle(mNumberOfSetsCounter+ "/3");
            mRepetition.setTitle("0/10");
            mRepetition.setProgress(0);
            SetsRepo setsRepo = new SetsRepo(getContext());
            setsRepo.insert(mCurrentSet);
            Toast.makeText(activity, mCurrentSet.getCount() + " " + getString(R.string.database_update), Toast.LENGTH_SHORT).show();
            Log.d(TAG, mCurrentSet.getCount() + " Sets Inserted to DataBase");
            mCurrentSet = null;

            mBodyWeight.animateProgressTo(0, 60, new CircularProgressBar.ProgressAnimationListener() {
                @Override
                public void onAnimationStart(View view) {
                    Log.d("dsf",view.getId()+"");
                }

                @Override
                public void onAnimationFinish(View view) {
                    Log.d("dsf",view.getId()+"");

                }

                @Override
                public void onAnimationProgress(int progress, View view) {
                    Log.d("dsf",view.getId()+"");
                    mBodyWeight.setTitle(progress+"/60");



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
        switch(view.getId())
        {
            case R.id.pb_repetition:
                break;
            case R.id.pb_sets:
                break;
            case R.id.pb_bodyweight:
                break;

        }
    }

    @Override
    public void onAnimationFinish(View view) {

    }

    @Override
    public void onAnimationProgress(int progress, View view) {

    }

    public void onLeftToRightSwipe() {
        int current = host.getCurrentTab()+1;
        host.setCurrentTab(current% 3);
        Log.d("============= ",current+" onRightToLeftSwipe");


    }

    public void onRightToLeftSwipe() {
        int current = host.getCurrentTab()-1;
        if (current<0) current += 3;
        host.setCurrentTab(current%3);
        Log.d("============= ",current+" onLeftToRightSwipe");


    }
}