package com.a.n.smartgym;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a.n.smartgym.BLE.BluetoothLeService;
import com.a.n.smartgym.Graphs.DayAverageFragment;
import com.a.n.smartgym.Graphs.TrendAverageFragment;
import com.a.n.smartgym.Graphs.UsageAverageFragment;
import com.a.n.smartgym.Graphs.VisitsFragment;
import com.a.n.smartgym.Helpers.BluetoothScanner;
import com.a.n.smartgym.Helpers.NdefReaderTask;
import com.a.n.smartgym.Listener.BluetoothListener;
import com.a.n.smartgym.Listener.PermissionsGrantedCallback;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.Utils.Constants;
import com.a.n.smartgym.barcode.BarcodeCaptureActivity;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.model.Visits;
import com.a.n.smartgym.repo.MuscleRepo;
import com.a.n.smartgym.repo.VisitsRepo;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@TargetApi(21)
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        SettingsFragment.onSharedPreferenceChangedListener,
        PermissionsGrantedCallback,
        View.OnClickListener,
        BluetoothListener {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FloatingActionButton fab;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private Fragment mCurrentFragment;
    private SettingsDialogFragment mHeadlessFrag;
    private BluetoothScanner mBluetoothScanner;
    private BluetoothLeService mBluetoothLeService;
    private String mBluetoothDeviceAddress;
    private Intent mCurrentIntent;
    public ProgressDialog mProgressDialog;
    private Handler mHandler;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sharedPreferences;
    private String mCurrentMode;
    private String mCurrentTagId = "";
    private Tag mCurrentTag;
    boolean mPermissionInProgress;


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                if (mCurrentTag != null)
                    StartExerciseActivity();
                //UpdateUi(mConnectionState,getString(R.string.connected));
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                // UpdateUi(mConnectionState,getString(R.string.disconnected));
                //invalidateOptionsMenu();
                //clearUI();
                //mBluetoothLeService.setCharacteristicNotification(false);
                setBLENotification(false);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                // mBluetoothLeService.setCharacteristicNotification(true);
                setBLENotification(true);

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

            } else if (BluetoothLeService.TROUBLESHOOT.equals(action)) {

                hideProgressDialog();

                //showProgressDialog(getString(R.string.reconnecting));
                Toast.makeText(getApplicationContext(), "Unable connect to BLE Device",
                        Toast.LENGTH_SHORT).show();
            } else if (
                    BluetoothAdapter.ACTION_STATE_CHANGED.equals(action) ||
                            NfcAdapter.ACTION_ADAPTER_STATE_CHANGED.equals(action) ||
                            LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {

                mHeadlessFrag.setLocation();
            }
//            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                Log.d(TAG, intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                ExercisesFragment fragment = (ExercisesFragment) getSupportFragmentManager().findFragmentByTag("EX");
//                if (fragment != null && fragment.isVisible()) {
//                    fragment.getMessage(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                }
//                //  UpdateUi(mValue,intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//            }
        }
    };

    // Code to manage Service lifecycle.
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                Log.d(TAG, "finish");
                finish();
            }

            handleIntent(getIntent());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        mHandler = new Handler();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setCurrentMode();

        InitializeGoogleApiClient();

        setContentView(R.layout.activity_main);

        setCurrentMode();

        mBluetoothScanner = new BluetoothScanner(this);

        mAuth = FirebaseAuth.getInstance();

        ExercisesDB.getInstance().keys = new MuscleRepo().getMainMuscle();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(this);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mCurrentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
        } else {
            mCurrentFragment = new DayAverageFragment();
            mFragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();

        }

        InitializeUserDetails();

        mNavigationView.setNavigationItemSelectedListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);


        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        nfc();

        bindService(new Intent(this, BluetoothLeService.class), mServiceConnection, BIND_AUTO_CREATE);


    }

    private void HeadLessFragment(boolean add) {
        mHeadlessFrag = (SettingsDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(SettingsDialogFragment.TAG);
        if (mHeadlessFrag == null && add) {

            if (add) {
                mHeadlessFrag = SettingsDialogFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(mHeadlessFrag, SettingsDialogFragment.TAG)
                        .commit();
            }
        } else if (!add)
            getSupportFragmentManager().beginTransaction().remove(mHeadlessFrag);
    }

    private void setCurrentMode() {
        if (sharedPreferences != null)
            mCurrentMode = sharedPreferences.getString(getString(R.string.mode_key), getString(R.string.default_mode));
    }

    public void setBLENotification(boolean val) {
        if (mBluetoothLeService != null && mBluetoothLeService.getConnectionState() == Constants.STATE_CONNECTED)
            mBluetoothLeService.setCharacteristicNotification(val);

    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Log.d(TAG, "onSaveInstanceState");
//        super.onSaveInstanceState(outState);
//        Fragment current = getSupportFragmentManager().getFragment(outState, "myFragmentName");
//        if (current != null)
//            getSupportFragmentManager().putFragment(outState, "myFragmentName", mCurrentFragment);
//        //getSupportFragmentManager().beginTransaction().add(R.id.containerView,mCurrentFragment, "myFragmentName").commit();
//    }

    private void nfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[]{
                ndef,
        };
        // Setup a tech list for all NfcF tags
        mTechLists = new String[][]{new String[]{NfcF.class.getName()}};
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (!mPermissionInProgress)
            navigateToCaptureFragment();

        setCurrentMode();

        Log.d(TAG, "Current Mode " + mCurrentMode);
        mBluetoothScanner.setListener(this);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mFilters,
                    mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mBluetoothScanner.getBluetoothAdapter() != null && mBluetoothScanner.getBluetoothAdapter().isEnabled()) {
            mBluetoothScanner.scanLeDevice(false);
            mBluetoothScanner.setListener(null);
        }
        unregisterReceiver(mGattUpdateReceiver);

        if (mNfcAdapter != null) mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    public void connectToDevice(String deviceAddress) {
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(deviceAddress);
            Log.d(TAG, "Connect request result=" + result);
            mBluetoothScanner.scanLeDevice(false);
        } else
            Log.d(TAG, "mBluetoothLeService is null");

    }


    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        if (mBluetoothLeService != null && mBluetoothLeService.getConnectionState() == Constants.STATE_DISCONNECTED) {
            handleIntent(intent);

        } else {
            ExercisesFragment myFragment = (ExercisesFragment) getSupportFragmentManager().findFragmentByTag("myFragmentName");
            if (myFragment != null && myFragment.isVisible()) {
                return;
            } else

                StartExerciseActivity();
        }


    }

    private void handleIntent(Intent intent) {

//        if (mBluetoothLeService!=null && mBluetoothLeService.getConnectionState()==Constants.STATE_CONNECTED){
//            Log.d(TAG, "the device is connected, trying to restart...");
//            return;
//        }

        String action = intent.getAction();
        Log.d(TAG, action);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                StartExercise(tag);

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

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mCurrentFragment = new SettingsFragment();
            fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawers();
        Bundle arguments = new Bundle();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        switch (item.getItemId()) {
//            case R.id.nav_item_inbox:
//                mCurrentFragment = new TabFragment();
//                fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();
//                break;
            case R.id.exercise:
                //StartExercise("");
                //mCurrentFragment = new MuscleFragment();
                //fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commit();

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
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//        Log.d(TAG, "onBackPressed");
//    }

    public void logout() {

        Log.d(TAG, "LOGOUT");

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
        Log.d(TAG, "finish");
        finish();
    }


    private void StartExercise(Tag tag) {

        mCurrentTag = tag;
        try {
            mCurrentTagId = new NdefReaderTask().execute(tag).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        hideProgressDialog();

        if (mBluetoothLeService.getConnectionState() != Constants.STATE_CONNECTED) {

            if (mCurrentMode.equals(Constants.DEVICE_NAME)) {

                showProgressDialog(getString(R.string.connecting));
                connectToDevice(mBluetoothDeviceAddress = Constants.GYM1_ADDRESS);

            } else {
                showProgressDialog(getString(R.string.scanning));
                mBluetoothScanner.scanLeDevice(true);

            }
        } else {
            StartExerciseActivity();
        }

    }


    private void StartExerciseActivity() {
        VisitsRepo visitsRepo = new VisitsRepo();
        String uuid = visitsRepo.getCurrentUUID(mAuth.getCurrentUser().getUid());
        if (uuid.isEmpty()) {
            Visits visits = new Visits();
            uuid = UUID.randomUUID().toString();
            visits.setVisitid(uuid);
            visits.setUserid(mAuth.getCurrentUser().getUid());
            visits.setDate(new Date(Calendar.getInstance().getTime().getTime()));
            visitsRepo.insert(visits);
        }


        Bundle bundle = new Bundle();
        bundle.putString("uuid", uuid);
        hideProgressDialog();

        if (mCurrentTagId.isEmpty()) {
            mCurrentFragment = new MuscleFragment();
            mCurrentFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commitAllowingStateLoss();
        } else {
            MuscleRepo muscleRepo = new MuscleRepo();
            Muscle ex = muscleRepo.getExerciseByID(mCurrentTagId);
            bundle.putParcelable("muscle", ex);
            bundle.putParcelable("tag", mCurrentTag);
            mCurrentFragment = new ExercisesFragment();
            mCurrentFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, mCurrentFragment, "myFragmentName").commit();
        }
    }

    @Override
    public void ScanResult(BluetoothDevice device) {
        hideProgressDialog();
        showProgressDialog(getString(R.string.connecting));
        connectToDevice(mBluetoothDeviceAddress = device.getAddress());
        Log.d(TAG, "ScanResult");
    }

    @Override
    public void ScanTroubleshoot(String msg) {
        hideProgressDialog();
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.TROUBLESHOOT);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);

        return intentFilter;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void isModeChanged(String mode) {
        mBluetoothLeService.ChangeMode(mCurrentMode = mode);
        Log.d(TAG, "Current Mode " + mCurrentMode);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
                break;
        }
    }


    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void closeBLE() {
        mBluetoothLeService.close();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void navigateToCaptureFragment() {
        if (isPermissionGranted()) {
            mPermissionInProgress = false;
            HeadLessFragment(true);
            //SettingsDialogFragment.newInstance().show(getSupportFragmentManager(), SettingsDialogFragment.class.getName());
        } else {
            HeadLessFragment(false);
            mPermissionInProgress = true;
            PermissionsDialogFragment.newInstance().show(getSupportFragmentManager(), PermissionsDialogFragment.class.getName());

        }
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void progressState(boolean progress) {
        mPermissionInProgress = progress;
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHeadlessFrag.onActivityResult(requestCode, resultCode, data);

    }
}

