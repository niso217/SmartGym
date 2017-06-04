package com.a.n.smartgym;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
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
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a.n.smartgym.BLE.BluetoothLeService;
import com.a.n.smartgym.Graphs.DayAverageFragment;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.Objects.Muscles;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.SetsRepo;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static android.content.Context.SENSOR_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ExercisesFragment extends Fragment implements View.OnClickListener, SensorEventListener {

    private static final String TAG = ExercisesFragment.class.getSimpleName();
    private Button btnFinish;
    private FirebaseAuth mAuth;
    private String mUUID;
    private String CurrentSession;
    private int Counter;
    private List<Sets> mSet;
    private boolean mInProgress;
    private boolean[] arr;
    private long mSetStart, mSetEnd;
    private ImageView mImage, mArrowImage;
    private SeekBar mSeekBar;
    private TextView mName, mPrimaryMuscle, mSecondaryMuscle, mInstruction, mCounter, mWeight, mCurrentWeightTV;
    long mAccelLast, mAccelCurrent;
    double mAccel;
    float[] mGravity;
    private SensorManager sensorManager;
    private boolean mBeenHere;
    private Context activity;
    private String mCurrentWeight = "", mCurrentRepetition = "0", mCalculatedWeight = "", mCurrentDirection = "";
    private Handler mHandler = new Handler();
    private int mZeroValueCounter;
    private int mSameReapedCounter;


    private final Runnable mTicker = new Runnable() {
        public void run() {
            //user interface interactions and updates on screen
            if (mCurrentWeight.equals("0")) {
                mZeroValueCounter++;
                if (mZeroValueCounter > 10) { //3 seconds pasted throw data to database
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
            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                showProgressDialog(null, false);
                Toast.makeText(activity,getString(R.string.disconnected_device), Toast.LENGTH_SHORT).show();
                finishExersise();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mHandler = new Handler();
        mSet = new ArrayList<>();

        mTicker.run();


    }


    private void ToIntArray(String val) {
        String[] stringArray = val.split(",");
        if (stringArray.length != 4) return;

        mCalculatedWeight = stringArray[2];
        if (!stringArray[1].equals(mCurrentRepetition)) {
            mCurrentRepetition = stringArray[1];
            if (!mCurrentRepetition.equals("0")) {
                AddSet(mCalculatedWeight, mSetStart, mSetEnd);
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
            mSetEnd = System.currentTimeMillis();

        } else if (mCurrentDirection.equals("1")) {
            mSetStart = System.currentTimeMillis();
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

    private void AddSet(String weight, long start, long end) {
        Sets s1 = new Sets();
        s1.setSetid(UUID.randomUUID().toString());
        s1.setWeight(Integer.parseInt(String.valueOf(weight)));
        s1.setexerciseid(mUUID);
        s1.setStart(start);
        s1.setEnd(end);

        mSet.add(s1);

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

        arr = new boolean[mSeekBar.getMax()];


        if (getArguments() != null) {
            mUUID = UUID.randomUUID().toString();
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

            CurrentSession = getArguments().getString("uuid");


            if (!mUUID.isEmpty() && current != null && !CurrentSession.isEmpty()) {
                setNewExercise(mUUID, CurrentSession, current.getName());
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

    public boolean areAllTrue(boolean[] array) {
        for (boolean b : array) if (!b) return false;
        return true;
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
        exercise.setStart(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        exerciseRepo.insert(exercise);
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
        stop();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView, new DayAverageFragment()).commitAllowingStateLoss();
    }

    private void InsertToDataBase() {
        if (mSet.size() > 0) {
            SetsRepo setsRepo = new SetsRepo(getContext());
            setsRepo.BulkSets(mSet);
            Toast.makeText(activity, mSet.size() + " " + getString(R.string.database_update), Toast.LENGTH_SHORT).show();
            mSet.clear();
            Log.d(TAG, "Sets Inserted to DataBase");
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