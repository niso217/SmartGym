package com.a.n.smartgym;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by nirb on 19/03/2017.
 */

public class LogInActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = LogInActivity.class.getSimpleName();
    private FirebaseAuth mAuth;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Rounds is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // Rounds is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }
}
