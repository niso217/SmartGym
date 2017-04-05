package com.a.n.smartgym;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.a.n.smartgym.Objects.Dates;
import com.a.n.smartgym.Objects.Device;
import com.a.n.smartgym.Objects.Rounds;
import com.a.n.smartgym.Objects.Sessions;
import com.a.n.smartgym.Objects.User;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.SetsRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class FireBaseFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = FireBaseFragment.class.getSimpleName();
    private TextView txtDetails;
    private EditText inputName, inputEmail;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String currentDate;
    private String userId;
    private FirebaseAuth mAuth;
    private String ScanResult;
    private String mUUID;
    private String CurrentSession;
    private Random mRandom;
    private List<Rounds> mRound;
    private TextView text;
    private User mUser;
    private Sessions mSessions;
    private Device mDevice;
    private Dates mDates;
    private int Counter;
    private SeekBar mSeekBar;
    private TextView mID, mCounter;
    private EditText mWeight;
    private List<Sets> mSet;
    private boolean mInProgress;
    private List<Integer>  arr;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRandom = new Random();
        mAuth = FirebaseAuth.getInstance();

        //mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        //mFirebaseDatabase = mFirebaseInstance.getReference("users");

        mRound = new ArrayList<Rounds>();

        arr = new ArrayList<>();


    }

    private void fakedata(){
        mSet = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Sets s1 = new Sets();
            s1.setSetid(UUID.randomUUID().toString());
            s1.setWeight(mRandom.nextInt(80 - 65) + 65);
            s1.setexerciseid(mUUID);
            mSet.add(s1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootFragment = inflater.inflate(R.layout.firebase_fragment, null);

        mSeekBar = (SeekBar) rootFragment.findViewById(R.id.seekBar);
        mID = (TextView) rootFragment.findViewById(R.id.device_id);
        mCounter = (TextView) rootFragment.findViewById(R.id.counter);
        mWeight = (EditText) rootFragment.findViewById(R.id.weight);

        Button finish = (Button) rootFragment.findViewById(R.id.btn_finish);
        finish.setOnClickListener(this);




        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        progress = progressValue;
                        if (mInProgress){
                            
                        }

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mInProgress = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mInProgress = false;

                        // Display the value in textview
                    }
                });

        if (getArguments() != null) {
            mUUID = UUID.randomUUID().toString();
            ScanResult = getArguments().getString("scanresult");
            CurrentSession = getArguments().getString("uuid");

            if (!mUUID.isEmpty() && !ScanResult.isEmpty() && !CurrentSession.isEmpty()){
                setNewExercise(mUUID, CurrentSession, ScanResult);
                //fakedata();

            }



        }
        return rootFragment;
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
//                Sets sets = new Sets();
//                sets.setSetid(UUID.randomUUID().toString());
//                sets.setWeight(mRandom.nextInt(80 - 65) + 65);
//                sets.setexerciseid(mUUID);
                Gson gson = new Gson();

                String inputString= gson.toJson(mSet);

                setsRepo.BulkSets(mSet);


//                mRound.add(new Rounds(mRandom.nextInt(80 - 65) + 65));
//
//                mFirebaseDatabase
//                        .child(mAuth.getCurrentUser().getUid()) //user id
//                        .child(new SimpleDateFormat("dd-MM-yyyy").format(new Date())) //current date
//                        .child(ScanResult) //current exercise
//                        .child(CurrentSession).setValue(mRound); //current exercise


            // break;
//            case R.id.btn_finish:
//                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.containerView, new PrimaryFragment()).commitAllowingStateLoss();
//                break;
        }
    }

}