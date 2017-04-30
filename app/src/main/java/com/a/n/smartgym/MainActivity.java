package com.a.n.smartgym;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.a.n.smartgym.Graphs.DayAverageFragment;
import com.a.n.smartgym.Graphs.TrendAverageFragment;
import com.a.n.smartgym.Graphs.UsageAverageFragment;
import com.a.n.smartgym.Graphs.VisitsFragment;
import com.a.n.smartgym.Helpers.NdefReaderTask;
import com.a.n.smartgym.barcode.BarcodeCaptureActivity;
import com.a.n.smartgym.model.Visits;
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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private int mCount = 0;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private Fragment mCurrentFragment;

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

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mCurrentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
        }
        else
        {
            mCurrentFragment = new TabFragment();
            mFragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();
        }

        InitializeUserDetails();

        mNavigationView.setNavigationItemSelectedListener(this);


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
        
        nfc();

        handleIntent(getIntent());


        //insertSampleData();
        //StartExercise();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "myFragmentName", mCurrentFragment);
    }

    private void nfc() {
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };
        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                try {
                    String str = new NdefReaderTask().execute(tag).get();
                    StartExercise(str);
                    Log.d(TAG,str);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
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
        Bundle arguments = new Bundle();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();


        switch (item.getItemId()) {
            case R.id.nav_item_inbox:
                mCurrentFragment = new TabFragment();
                fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();
                break;
            case R.id.exercise:
                StartExercise("");
                break;
            case R.id.device_day_average:
                mCurrentFragment = new DayAverageFragment();
                fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();
                break;
            case R.id.day_device_average:
                mCurrentFragment = new TrendAverageFragment();
                fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();
                break;
            case R.id.usage_average:
                mCurrentFragment = new UsageAverageFragment();
                fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();
                break;
            case R.id.visits:
                mCurrentFragment = new VisitsFragment();
                fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();
                break;

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
                        visitsRepo.insert(visits);

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

    private void StartExercise(String Tagid){
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
        if (Tagid.isEmpty()){
            String id = "ID";
            Random r = new Random();
            int i1 = r.nextInt(20 - 1) + 1;
            bundle.putString("scanresult", id+i1);
        }
        else
            bundle.putString("scanresult", Tagid);

        bundle.putString("uuid", uuid);

        mCurrentFragment = new FireBaseFragment();
        mCurrentFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commitAllowingStateLoss();
    }

}