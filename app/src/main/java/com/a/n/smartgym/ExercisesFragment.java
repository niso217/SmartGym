package com.a.n.smartgym;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
    private ImageView mImage;
    private SeekBar mSeekBar;
    private TextView mName, mPrimaryMuscle, mSecondaryMuscle, mInstruction, mCounter, mWeight;
    long mAccelLast, mAccelCurrent;
    double mAccel;
    float[] mGravity;
    private SensorManager sensorManager;
    private boolean mBeenHere;
    private Context activity;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();


//        sensorManager=(SensorManager) getContext().getSystemService(SENSOR_SERVICE);
//        // add listener. The listener will be HelloAndroid (this) class
//        sensorManager.registerListener(this,
//                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_NORMAL);

        mSet = new ArrayList<>();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = context;
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

        Random r = new Random();
        int i1 = r.nextInt(100 - 40) + 65;
        mWeight.setText(i1 + "");


        btnFinish = (Button) rootFragment.findViewById(R.id.btn_ex_finish);
        btnFinish.setOnClickListener(this);
        Controllers(true);

        arr = new boolean[mSeekBar.getMax()];


        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        if (mInProgress) {

                            if (progressValue == 0)
                                arr[progressValue] = true;
                            else
                                arr[progressValue - 1] = true;


                            if (areAllTrue(arr)) {
                                Counter++;
                                mSetEnd = System.currentTimeMillis();
                                arr = new boolean[mSeekBar.getMax()];
                                AddSet(mWeight.getText().toString(), mSetStart, mSetEnd);
                                mCounter.setText(Counter + "");

                            } else {
                                mSetStart = System.currentTimeMillis();

                            }
                        }

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mInProgress = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mInProgress = false;
                        arr = new boolean[mSeekBar.getMax()];

                        // Display the value in textview
                    }
                });

        if (getArguments() != null) {
            mUUID = UUID.randomUUID().toString();
            Muscle current = getArguments().getParcelable("muscle");
            Tag tag = getArguments().getParcelable("tag");
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
        try{
            ((onExercisesStatusListener) activity).isExercisesStatusChanged(true); //finisdhed
        }catch (ClassCastException cce){

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
        SetsRepo setsRepo = new SetsRepo(getContext());
        setsRepo.BulkSets(mSet);
        mSet.clear();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView, new DayAverageFragment()).commitAllowingStateLoss();
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

    public void getMessage(String msg) {
        Log.d(TAG, msg);
        mWeight.setText(msg + "");

    }

    public interface onExercisesStatusListener {
        public void isExercisesStatusChanged(boolean change);
    }


}