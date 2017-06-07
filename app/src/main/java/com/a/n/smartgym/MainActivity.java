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
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Build;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
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
    private String mCurrentMode;
    private String mCurrentTagId = "";
    private Tag mCurrentTag;
    boolean mPermissionInProgress;
    private Toolbar mToolbar;
    private Menu mMenu;


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch ((intent.getAction())) {
                case BluetoothLeService.ACTION_GATT_CONNECTED: //BLE device connected
                    setIcon(R.id.bluetooth_searching, R.drawable.ic_bluetooth_connected_white_36dp);
                    //if (mCurrentTag != null)
                    StartExerciseActivity(); //
                    Log.d(TAG, "ACTION_GATT_CONNECTED");

                    break;
                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                    setIcon(R.id.bluetooth_searching, R.drawable.ic_bluetooth_white_36dp);
                    //setBLENotification(false); //set BLE device Characteristic Notification off
                    Log.d(TAG, "ACTION_GATT_DISCONNECTED");
                    break;
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
                    setBLENotification(true); //set BLE device Characteristic Notification on
                    break;
                case BluetoothLeService.TROUBLESHOOT:
                    Log.d(TAG, "TROUBLESHOOT");
                    setIcon(R.id.bluetooth_searching, R.drawable.ic_bluetooth_white_36dp);
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(), getString(R.string.ble_unable),
                            Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    mHeadlessFrag.setLocation();
                    break;
                case NfcAdapter.ACTION_ADAPTER_STATE_CHANGED:
                    mHeadlessFrag.setLocation();
                    break;
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    mHeadlessFrag.setLocation();
                    break;
            }
        }
    };

    // Code to manage Service lifecycle.
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            //StartBLEScan(true, true);

            if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
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

        mAuth = FirebaseAuth.getInstance();

        SetUpActivityView();

        setCurrentMode();

        InitializeGoogleApiClient();

        mBluetoothScanner = new BluetoothScanner(this);


        ExercisesDB.getInstance().keys = new MuscleRepo().getMainMuscle();

        InitializeNFC();

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mCurrentFragment = getSupportFragmentManager().getFragment(savedInstanceState, TAG);
        } else {
            mCurrentFragment = new DayAverageFragment();
            performIdentifierAction();
        }

        bindService(new Intent(this, BluetoothLeService.class), mServiceConnection, BIND_AUTO_CREATE);


    }

    public void performIdentifierAction() {
        mNavigationView.getMenu().performIdentifierAction(R.id.device_day_average, 0);

    }

    private void SetUpActivityView() {
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        mNavigationView.setNavigationItemSelectedListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fake_nfc);
        fab.setOnClickListener(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        InitializeUserDetails();
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences != null)
            mCurrentMode = sharedPreferences.getString(getString(R.string.mode_key), getString(R.string.default_mode));
    }

    public void setBLENotification(boolean val) {
        if (mBluetoothLeService != null && mBluetoothLeService.getConnectionState() == Constants.STATE_CONNECTED)
            mBluetoothLeService.setCharacteristicNotification(val);

    }


    private void InitializeNFC() {
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

        if (!mPermissionInProgress) //do not enter if Permission fragment dialog not finished
            navigateToCaptureFragment();

        setCurrentMode();

        Log.d(TAG, "Current Mode " + mCurrentMode);
        mBluetoothScanner.setListener(this);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mFilters,
                    mTechLists);

        //start BLE scan when device is emulator
        //StartBLEScan(false, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mBluetoothScanner.getBluetoothAdapter() != null && mBluetoothScanner.getBluetoothAdapter().isEnabled()) {
            StartBLEScan(false, false);
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
            StartBLEScan(false, false);
        } else
            Log.d(TAG, "mBluetoothLeService is null");

    }


    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        if (mBluetoothLeService != null && mBluetoothLeService.getConnectionState() == Constants.STATE_DISCONNECTED) {
            handleIntent(intent);

        } else {
            ExercisesFragment myFragment = (ExercisesFragment) getSupportFragmentManager().findFragmentByTag(TAG);
            if (myFragment != null && myFragment.isVisible()) {
                return;
            } else

                StartExerciseActivity();
        }


    }


    private void handleIntent(Intent intent) {

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
        mMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentMode = settings.getString(getString(R.string.mode_key), "");
        MenuItem item = null;
        isModeChanged(mCurrentMode);
        switch (mCurrentMode) {
            case Constants.DEVICE_NAME:
                item = menu.findItem(R.id.device);
                break;
            default:
                item = menu.findItem(R.id.emulator);
                break;
        }
        item.setChecked(true);
        return true;
    }

    private void setIcon(int resource, int drawable) {
        if (mMenu != null)
            mMenu.findItem(resource).setIcon(drawable);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        switch (item.getItemId()) {
            case R.id.action_settings:
                getFragmentManager().beginTransaction().replace(R.id.containerView, new SettingsFragment()).commit();
                break;
            case R.id.bluetooth_searching:
                StartBLEScan(true, true);
                break;
            case R.id.emulator:
                item.setChecked(!item.isChecked());
                editor.putString(getString(R.string.mode_key), Constants.EMULATOR_NAME);
                editor.commit();
                isModeChanged(Constants.EMULATOR_NAME);

                break;
            case R.id.device:
                item.setChecked(!item.isChecked());
                editor.putString(getString(R.string.mode_key), Constants.DEVICE_NAME);
                editor.commit();
                isModeChanged(Constants.DEVICE_NAME);

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawers();
        Log.d(TAG, "onNavigationItemSelected");
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
                getSupportFragmentManager().beginTransaction().replace(R.id.containerView, mCurrentFragment).commit();
                mToolbar.setTitle("");
                break;
            case R.id.day_device_average:
                mCurrentFragment = new TrendAverageFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.containerView, mCurrentFragment).commit();
                mToolbar.setTitle("");
                break;
            case R.id.usage_average:
                mCurrentFragment = new UsageAverageFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.containerView, mCurrentFragment).commit();
                mToolbar.setTitle("");

                break;
            case R.id.visits:
                mCurrentFragment = new VisitsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.containerView, mCurrentFragment).commit();
                mToolbar.setTitle("");
            case R.id.settings:
                //getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
                //getFragmentManager().beginTransaction().replace(R.id.containerView, new SettingsFragment()).commit();

                mToolbar.setTitle("");

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


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Log.d(TAG, "onBackPressed");
    }

    public void logout() {

        Log.d(TAG, "LOGOUT");

        // Firebase sign out
        mAuth.signOut();

        //LoginManager.getInstance().logOut();

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

        if (mBluetoothDeviceAddress == null && mCurrentMode != Constants.DEVICE_NAME) {
            showScanBLEDialog();
            return;
        }

        mCurrentTag = tag;
        if (mCurrentTag != null) {
            try {
                mCurrentTagId = new NdefReaderTask().execute(tag).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }


        hideProgressDialog();

        if (mBluetoothLeService.getConnectionState() != Constants.STATE_CONNECTED) {
            showProgressDialog(getString(R.string.connecting));
            if (mCurrentMode.equals(Constants.DEVICE_NAME))
                connectToDevice(mBluetoothDeviceAddress = Constants.GYM1_ADDRESS);
            else
                connectToDevice(mBluetoothDeviceAddress);

        } else {
            StartExerciseActivity();
        }

    }

    private void StartBLEScan(boolean showprogress, boolean on) {
        if (showprogress)
            showProgressDialog(getString(R.string.scanning));

        if (mBluetoothLeService != null && mBluetoothLeService.getConnectionState() != Constants.STATE_CONNECTED) {
            mBluetoothScanner.scanLeDevice(on);
            if (on) {
                setIcon(R.id.bluetooth_searching, R.drawable.ic_bluetooth_searching_white_36dp);
            } else
                setIcon(R.id.bluetooth_searching, R.drawable.ic_bluetooth_white_36dp);
        }

    }


    private void StartExerciseActivity() {

        Bundle bundle = new Bundle();
        bundle.putString("uuid", isVisitExist());
        hideProgressDialog();

        if (mCurrentTagId.isEmpty()) {
            mCurrentFragment = new MuscleFragment();
            mCurrentFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.containerView, mCurrentFragment).commitAllowingStateLoss();
        } else {
            MuscleRepo muscleRepo = new MuscleRepo();
            Muscle ex = muscleRepo.getExerciseByID(mCurrentTagId);
            bundle.putParcelable("muscle", ex);
            //bundle.putParcelable("tag", mCurrentTag);
            mCurrentFragment = new ExercisesFragment();
            mCurrentFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.containerView, mCurrentFragment, TAG).commit();
        }
    }

    /**
     * check if user has a UUID for today session, otherwise return new one
     *
     * @return existing  / new UUID
     */
    private String isVisitExist() {
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
        return uuid;
    }

    @Override
    public void ScanResult(BluetoothDevice device) {
        hideProgressDialog();
        //showProgressDialog(getString(R.string.connecting));
        mBluetoothDeviceAddress = device.getAddress();
        mToolbar.setTitle(device.getName());
        StartBLEScan(false, false);
        Log.d(TAG, "ScanResult");
    }

    @Override
    public void ScanTroubleshoot(String msg) {
        hideProgressDialog();
        StartBLEScan(false, false);
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
        mToolbar.setTitle("No BLE Device Was Found");
        mBluetoothDeviceAddress = null;
        setIcon(R.id.bluetooth_searching, R.drawable.ic_bluetooth_disabled_white_36dp);


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
        if (mBluetoothLeService != null)
            mBluetoothLeService.ChangeMode(mCurrentMode = mode);

        if (mBluetoothScanner != null)
            mBluetoothScanner.ChangeFilter(mCurrentMode);
        switch (mode) {
            case Constants.DEVICE_NAME:
                mBluetoothDeviceAddress = Constants.GYM1_ADDRESS;
                mToolbar.setTitle(Constants.DEVICE_NAME);
                break;
            case Constants.EMULATOR_NAME:
                mBluetoothDeviceAddress = null;
                mToolbar.setTitle("");
                break;

        }
        Log.d(TAG, "Current Mode " + mCurrentMode);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fake_nfc:
                ArrayList<String> muscle = new MuscleRepo().getAllMuscleID();
                Random r = new Random();
                int i1 = r.nextInt(muscle.size() - 1);
                mCurrentTagId = muscle.get(i1);
                StartExercise(null);
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
            //if all Permission Granted load the setting fragment (Bluetooth,GPS,NFC)
            mPermissionInProgress = false;
            HeadLessFragment(true);
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

    private void showScanBLEDialog() {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("No BLE device")
                .setMessage("search for device")
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StartBLEScan(true, true);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                }).create().show();
    }
}

