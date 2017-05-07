package com.a.n.smartgym;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.Objects.Muscles;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.SetsRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ExercisesFragment extends Fragment implements View.OnClickListener {

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
    private TextView mName, mPrimaryMuscle, mSecondaryMuscle, mInstruction, mCounter;
    private EditText mWeight;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        mSet = new ArrayList<>();


    }

    private void AddSet(Editable weight, long start, long end) {
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
        mWeight = (EditText) rootFragment.findViewById(R.id.tv_ex_weight_txt);
        mImage = (ImageView) rootFragment.findViewById(R.id.img_ex) ;

        Random r = new Random();
        int i1 = r.nextInt(100 - 40) + 65;
        mWeight.setText(i1+"");


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
                                AddSet(mWeight.getText(), mSetStart, mSetEnd);
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
            String group = getArguments().getString("group");
            int index = getArguments().getInt("index");

            Muscles current = null;

            List<Muscles> lst = ExercisesDB.getInstance().DB.get(group);
            if (lst != null) {
                current = lst.get(index);
            }
            if (current != null){
               //mImage.setImageDrawable(getDrawableForSdkVersion(current.getImage()));
                Picasso.with(getApplicationContext()).load(current.getImage()).into(mImage);
                mName.setText(current.getName());
                mPrimaryMuscle.setText(current.getMain());
                mSecondaryMuscle.setText(current.getSecondary());
                mInstruction.setText(current.getDescription());

            }

            CurrentSession = getArguments().getString("uuid");


            if (!mUUID.isEmpty() && current!=null && !CurrentSession.isEmpty()) {
                setNewExercise(mUUID, CurrentSession, current.getName());
                //mID.setText(ScanResult);
            }


        }
        return rootFragment;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private Drawable getDrawableForSdkVersion(String name) {

        int resId = getResources().getIdentifier(name , "drawable", getContext().getPackageName());

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
                SetsRepo setsRepo = new SetsRepo(getContext());
                setsRepo.BulkSets(mSet);
                mSet.clear();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new PrimaryFragment()).commitAllowingStateLoss();
                break;

        }
    }

}