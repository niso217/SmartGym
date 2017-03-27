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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
    private int counter;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRandom = new Random();

        mAuth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        mRound = new ArrayList<Rounds>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootFragment = inflater.inflate(R.layout.firebase_fragment, null);

        Button up = (Button) rootFragment.findViewById(R.id.btn_up);
        Button finish = (Button) rootFragment.findViewById(R.id.btn_finish);
        text = (TextView) rootFragment.findViewById(R.id.tv_number);


        up.setOnClickListener(this);
        finish.setOnClickListener(this);


        if (getArguments() != null){
            mUUID = UUID.randomUUID().toString();
            ScanResult = getArguments().getString("scanresult");
            CurrentSession = getArguments().getString("uuid");

            ExerciseRepo exerciseRepo = new ExerciseRepo();
            Exercise exercise = new Exercise();
            exercise.setexerciseid(mUUID);
            exercise.setVisitid(CurrentSession);
            exercise.setMachinename(ScanResult);
            exercise.setStart(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
            exerciseRepo.insert(exercise);
            //CurrentSession = mFirebaseDatabase.push().getKey();
           // addSessionChangeListener();
            //initObject();

        }
        return rootFragment;
    }

    private void initObject(){
        mUser = new User(mAuth.getCurrentUser().getUid());
        mDates = new Dates(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        mDevice = new Device(ScanResult);
        mSessions = new Sessions(CurrentSession);

        mUser.addDate(mDates);
        mDates.addDevice(mDevice);
        mDevice.addSession(mSessions);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_up:
                SetsRepo setsRepo = new SetsRepo();
                Sets sets = new Sets();
                sets.setSetid(UUID.randomUUID().toString());
                sets.setWeight(mRandom.nextInt(80 - 65) + 65);
                sets.setexerciseid(mUUID);
                setsRepo.insert(sets);
//                mRound.add(new Rounds(mRandom.nextInt(80 - 65) + 65));
//
//                mFirebaseDatabase
//                        .child(mAuth.getCurrentUser().getUid()) //user id
//                        .child(new SimpleDateFormat("dd-MM-yyyy").format(new Date())) //current date
//                        .child(ScanResult) //current exercise
//                        .child(CurrentSession).setValue(mRound); //current exercise



                break;
            case R.id.btn_finish:
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new PrimaryFragment()).commitAllowingStateLoss();
                break;
        }
    }

    private void addSessionChangeListener() {


        //user.addDate(new Dates(new SimpleDateFormat("dd-MM-yyyy").format(new Date())));

        // User data change listener
        mFirebaseDatabase
                .child(mAuth.getCurrentUser().getUid())
                .child(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))
                .child(ScanResult)
                .child(CurrentSession)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    counter++;
                    Log.d(TAG,postSnapshot.toString());
                }
                text.setText(counter+"");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }
}