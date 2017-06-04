package com.a.n.smartgym;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a.n.smartgym.BLE.BluetoothLeService;
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

public class ExercisesFragment extends Fragment implements View.OnClickListener, SensorEventListener {

    private static final String TAG = ExercisesFragment.class.getSimpleName();
    private Button btnFinish;
    private FirebaseAuth mAuth;
    private String CurrentExercisesId;
    private String CurrentVisitId;
    private int Counter;
    private Sets mCurrentSet;
    private boolean mInProgress;
    private boolean[] arr;
    private ImageView mImage, mArrowImage;
    private SeekBar mSeekBar;
    private TextView mName, mPrimaryMuscle, mSecondaryMuscle, mInstruction, mCounter, mWeight, mCurrentWeightTV;
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


    private final Runnable mTicker = new Runnable() {
        public void run() {
            //user interface interactions and updates on screen
            if (Integer.parseInt(mCalculatedWeight) < 1) {
                mZeroValueCounter++;
                if (mZeroValueCounter > 5) { //3 seconds pasted throw data to database
                    InsertToDataBase();
                    mZeroValueCounter = 0;
                }
            } else
                mZeroValueCounter = 0;

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
                StartNewSetInstance(Integer.parseInt(mCurrentRepetition), Integer.parseInt(mCalculatedWeight));
                Log.d(TAG, "AddSet");

            }
        }

        mCurrentWeight = stringArray[0];

        mCurrentDirection = stringArray[3];
        mWeight.setText(mCalculatedWeight);
        mCounter.setText(mCurrentRepetition);
        mCurrentWeightTV.setText(mCurrentWeight);


        if (mCurrentDirection.equals("-1")) {
            mArrowImage.setImageDrawable(getDrawableForSdkVersion("ic_arrow_downward_black_48dp"));

        } else if (mCurrentDirection.equals("1")) {
            mArrowImage.setImageDrawable(getDrawableForSdkVersion("ic_arrow_upward_black_48dp"));

        } else
            mArrowImage.setImageDrawable(null);

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
        View rootFragment = inflater.inflate(R.layout.exercises_fragment, null);

        mSeekBar = (SeekBar) rootFragment.findViewById(R.id.seekbar_ex);
        mName = (TextView) rootFragment.findViewById(R.id.tv_ex_name_txt);
        mPrimaryMuscle = (TextView) rootFragment.findViewById(R.id.tv_ex_primary_txt);
        mSecondaryMuscle = (TextView) rootFragment.findViewById(R.id.tv_ex_secondary_txt);
        mInstruction = (TextView) rootFragment.findViewById(R.id.tv_ex_instructions_txt);
        mCounter = (TextView) rootFragment.findViewById(R.id.tv_ex_counter_txt);
        mWeight = (TextView) rootFragment.findViewById(R.id.tv_ex_weight_txt);
        mImage = (ImageView) rootFragment.findViewById(R.id.img_ex);
        mArrowImage = (ImageView) rootFragment.findViewById(R.id.img_arrow);
        mCurrentWeightTV = (TextView) rootFragment.findViewById(R.id.tv_current_weight);

        btnFinish = (Button) rootFragment.findViewById(R.id.btn_ex_finish);
        btnFinish.setOnClickListener(this);
        Controllers(true);

        if (getArguments() != null) {

            CurrentExercisesId = UUID.randomUUID().toString();
            Muscle current = getArguments().getParcelable("muscle");
            //Tag tag = getArguments().getParcelable("tag");
            //getMessagesFromTag(tag);


            if (current != null) {
                try {
                    mName.setText(current.getName());
                    mPrimaryMuscle.setText(current.getMain());
                    mSecondaryMuscle.setText(current.getSecondary());
                    mInstruction.setText(current.getDescription());
                    Picasso.with(getContext()).load(current.getImage()).into(mImage);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }

            }

            CurrentVisitId = getArguments().getString("uuid");


            if (!CurrentExercisesId.isEmpty() && current != null && !CurrentVisitId.isEmpty()) {
                setNewExercise(CurrentExercisesId, CurrentVisitId, current.getName());
                StartNewSetInstance(0, 0);

                //mID.setText(ScanResult);
            }


        }
        return rootFragment;
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
        mSeekBar.setEnabled(b);
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
        stop();
        closeFragment();
    }

    private void InsertToDataBase() {
        if (mCurrentSet != null && mCurrentSet.getCount()>0) {
            SetsRepo setsRepo = new SetsRepo(getContext());
            setsRepo.insert(mCurrentSet);
            Toast.makeText(activity, mCurrentSet.getCount() + " " + getString(R.string.database_update), Toast.LENGTH_SHORT).show();
            Log.d(TAG, mCurrentSet.getCount() + " Sets Inserted to DataBase");
            mCurrentSet = null;

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity = event.values.clone();
                // Shake detection
                float x = mGravity[0];
                float y = mGravity[1];
                float z = mGravity[2];

                //Log.d("TYPE_ACCELEROMETER","x " + x );
                // Log.d("TYPE_ACCELEROMETER","y " + y );
                // Log.d("TYPE_ACCELEROMETER","z " + z );


                float yAbs = Math.abs(mGravity[1]);

                mAccelLast = mAccelCurrent;
                mAccelCurrent = (long) Math.sqrt(x * x + y * y + z * z);
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta;

                if (yAbs > 3.0 && !mBeenHere) {
                    mBeenHere = true;
                    Log.d("TYPE_ACCELEROMETER", "===alert===");
                    Log.d("TYPE_ACCELEROMETER", yAbs + "");
                    finishExersise();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);

        return intentFilter;
    }


}