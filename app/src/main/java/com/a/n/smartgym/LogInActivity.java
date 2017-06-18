package com.a.n.smartgym;

import android.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.a.n.smartgym.Utils.PermissionsUtil;
import com.a.n.smartgym.model.User;
import com.a.n.smartgym.repo.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Iterator;
import java.util.List;

/**
 * Created by nirb on 19/03/2017.
 */

public class LogInActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = LogInActivity.class.getSimpleName();

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
        super.onCreate(savedInstanceState);
        // startBLEScanActivity();
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && !mIgnorehStateChanged) {
            mIgnorehStateChanged = true;
            // Rounds is signed in
            SaveUserToDataBase(user);
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            startActivity(new Intent(this, WizardActivity.class));
            finish();
        } else {
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
