package com.a.n.smartgym;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.a.n.smartgym.barcode.BarcodeCaptureActivity;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.model.User;
import com.a.n.smartgym.model.Visits;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.SetsRepo;
import com.a.n.smartgym.repo.UserRepo;
import com.a.n.smartgym.repo.VisitsRepo;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.util.Calendar;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FloatingActionButton fab;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        InitializeGoogleApiClient();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

        InitializeUserDetails();

        mNavigationView.setNavigationItemSelectedListener(this);


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        //insertSampleData();
        FakeResult();


    }

    private void InitializeUserDetails() {
        //Initializing the header.xml data
        View headerLayout = mNavigationView.getHeaderView(0);

        //Initializing the profile image
        ImageView image = (ImageView) headerLayout.findViewById(R.id.profile_image);

        //Initializing the profile name
        TextView name = (TextView) headerLayout.findViewById(R.id.user_name);

        //Initializing the profile email
        TextView email = (TextView) headerLayout.findViewById(R.id.email);

        Picasso.with(this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .resize(150, 150)
                .centerCrop()
                .into(image);

        name.setText(mAuth.getCurrentUser().getDisplayName());
        email.setText(mAuth.getCurrentUser().getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawers();

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();


        switch (item.getItemId()) {
            case R.id.nav_item_sent:
                fab.setVisibility(View.VISIBLE);
                fragmentTransaction.replace(R.id.containerView, new SentFragment()).commit();
                break;
            case R.id.nav_item_inbox:
                fab.setVisibility(View.INVISIBLE);
                fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                break;
//            case R.id.nav_firebase:
//                fragmentTransaction.replace(R.id.containerView, new FireBaseFragment()).commit();
//                break;
            case R.id.nav_logout:
                logout();
                break;

        }

        return false;
    }

    private void InitializeGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void logout() {

        // Firebase sign out
        mAuth.signOut();

        LoginManager.getInstance().logOut();

        // Google revoke access
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });

        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    //mResultTextView.setText(barcode.displayValue);

                    //create new visit
                    VisitsRepo visitsRepo = new VisitsRepo();
                    String uuid = visitsRepo.getCurrentUUID(mAuth.getCurrentUser().getUid());
                    if (uuid.isEmpty()){
                        Visits visits = new Visits();
                        uuid = UUID.randomUUID().toString();
                        visits.setVisitid(uuid);
                        visits.setUserid(mAuth.getCurrentUser().getUid());
                        visits.setDate(new Date(Calendar.getInstance().getTime().getTime()));
                    }


                    Bundle bundle = new Bundle();
                    bundle.putString("scanresult", barcode.displayValue);
                    bundle.putString("uuid", uuid);

                    Log.d(TAG, barcode.displayValue);
                    FireBaseFragment fb = new FireBaseFragment();
                    fb.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, fb).commitAllowingStateLoss();

                } else
                    Log.d(TAG, "No barcode captured");

                //mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private void FakeResult(){
        //create new visit
        VisitsRepo visitsRepo = new VisitsRepo();
        String uuid = visitsRepo.getCurrentUUID(mAuth.getCurrentUser().getUid());
        if (uuid.isEmpty()){
            Visits visits = new Visits();
            uuid = UUID.randomUUID().toString();
            visits.setVisitid(uuid);
            visits.setUserid(mAuth.getCurrentUser().getUid());
            visits.setDate(new Date(Calendar.getInstance().getTime().getTime()));
            visitsRepo.insert(visits);
        }


        Bundle bundle = new Bundle();
        bundle.putString("scanresult", "ID1");
        bundle.putString("uuid", uuid);

        FireBaseFragment fb = new FireBaseFragment();
        fb.setArguments(bundle);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerView, fb).commitAllowingStateLoss();
    }

    private void insertSampleData() {

        //UserRepo userRepo = new UserRepo();
        //VisitsRepo visitsRepo = new VisitsRepo();
        ExerciseRepo exerciseRepo = new ExerciseRepo();
        SetsRepo setsRepo = new SetsRepo();

        //userRepo.delete();
        //visitsRepo.delete();
        exerciseRepo.delete();
        setsRepo.delete();

//        User user = new User();
//        user.setId("1");
//        user.setFname("nir");
//        user.setLname("ben ezra");
//        userRepo.insert(user);
//
//        Visits visit = new Visits();
//        visit.setUserid(user.getId());
//        visit.setVisitid("123");
//        visit.setDate(new Date(Calendar.getInstance().getTime().getTime()));
//        visitsRepo.insert(visit);
//
//        Exercise exercise = new Exercise();
//        exercise.setexerciseid("100");
//        exercise.setVisitid(visit.getVisitid());
//        exercise.setStart(new Date(Calendar.getInstance().getTime().getTime()));
//        exerciseRepo.insert(exercise);
//
//        Sets sets = new Sets();
//        sets.setSetid("111");
//        sets.setexerciseid(exercise.getexerciseid());
//        sets.setCount(3);
//        sets.setWeight(55);
//        setsRepo.insert(sets);

    }
}