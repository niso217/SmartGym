package com.a.n.smartgym.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.a.n.smartgym.DBModel.User;
import com.a.n.smartgym.DBRepo.UserRepo;
import com.a.n.smartgym.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by nirb on 19/03/2017.
 */

public class LogInActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private final String TAG = LogInActivity.class.getSimpleName();
    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 1000;
    public static final int ITEM_DELAY = 300;
    private boolean animationStarted = true;


    private FirebaseAuth mAuth;
    private boolean mIgnorehStateChanged;

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mAuth.removeAuthStateListener(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    private void startBLEScanActivity() {
//        Log.d(TAG, "StartBLEScanActivity calling startActivity()");
//        Intent activityIntent = new Intent(this, MainActivity2.class);
//        startActivity(activityIntent);
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        // startBLEScanActivity();
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus || animationStarted) {
            return;
        }

        animate();

        super.onWindowFocusChanged(hasFocus);
    }

    private void animate() {
        ImageView logoImageView = (ImageView) findViewById(R.id.img_logo);
        ViewGroup container = (ViewGroup) findViewById(R.id.container);

        ViewCompat.animate(logoImageView)
                .translationY(-250)
                .setStartDelay(STARTUP_DELAY)
                .setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new DecelerateInterpolator(1.2f)).start();

        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;

            if (!(v instanceof Button)) {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(50).alpha(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(1000);
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(500);
            }

            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && !mIgnorehStateChanged) {
            animationStarted = true;
            mIgnorehStateChanged = true;
            // Rounds is signed in
            SaveUserToDataBase(user);
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            animationStarted = false;
            // Rounds is signed out
            setContentView(R.layout.activity_login);
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    private void SaveUserToDataBase(FirebaseUser current) {
        UserRepo userRepo = new UserRepo();
        if (!userRepo.isUserExist(current.getUid())) {
            User user = new User();
            user.setId(current.getUid());
            user.setFname(current.getDisplayName());
            userRepo.insert(user);

        }
    }




}
