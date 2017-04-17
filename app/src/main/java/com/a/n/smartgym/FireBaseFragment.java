package com.a.n.smartgym;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.SetsRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class FireBaseFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = FireBaseFragment.class.getSimpleName();
    private TextView txtDetails;
    private EditText inputName, inputEmail;
    private Button btnFinish;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String currentDate;
    private String userId;
    private FirebaseAuth mAuth;
    private String ScanResult;
    private String mUUID;
    private String CurrentSession;

    private int Counter;
    private SeekBar mSeekBar;
    private TextView mID, mCounter;
    private EditText mWeight;
    private List<Sets> mSet;
    private boolean mInProgress;
    private boolean[] arr;
    private int achieveCounter;
    private long mSetStart,mSetEnd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        mSet = new ArrayList<>();



    }

    private void AddSet(Editable weight,long start, long end) {
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
        View rootFragment = inflater.inflate(R.layout.firebase_fragment, null);

        mSeekBar = (SeekBar) rootFragment.findViewById(R.id.seekBar);
        mID = (TextView) rootFragment.findViewById(R.id.device_id);
        mCounter = (TextView) rootFragment.findViewById(R.id.counter);
        mWeight = (EditText) rootFragment.findViewById(R.id.weight);
        mWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>0)
                    Controllers(true);
                else
                    Controllers(false);


            }
        });

        btnFinish = (Button) rootFragment.findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(this);
        Controllers(false);

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
                                AddSet(mWeight.getText(),mSetStart,mSetEnd);
                                mCounter.setText(Counter + "");

                            }
                            else
                            {
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
            ScanResult = getArguments().getString("scanresult");
            CurrentSession = getArguments().getString("uuid");


            if (!mUUID.isEmpty() && !ScanResult.isEmpty() && !CurrentSession.isEmpty()) {
                setNewExercise(mUUID, CurrentSession, ScanResult);
                mID.setText(ScanResult);
            }


        }
        return rootFragment;
    }

    public boolean areAllTrue(boolean[] array) {
        for (boolean b : array) if (!b) return false;
        return true;
    }

    private void Controllers(boolean b){
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
            case R.id.btn_finish:
                SetsRepo setsRepo = new SetsRepo(getContext());
                setsRepo.BulkSets(mSet);
                mSet.clear();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new PrimaryFragment()).commitAllowingStateLoss();
                break;

        }
    }

}