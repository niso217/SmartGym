package com.a.n.smartgym;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

import com.a.n.smartgym.BLE.BluetoothLeService;
import com.a.n.smartgym.Graphs.DayAverageFragment;
import com.a.n.smartgym.Graphs.TrendAverageFragment;
import com.a.n.smartgym.Graphs.UsageAverageFragment;
import com.a.n.smartgym.Graphs.VisitsFragment;
import com.a.n.smartgym.Helpers.NdefReaderTask;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.Objects.Muscles;
import com.a.n.smartgym.Objects.NFCResult;
import com.a.n.smartgym.Utils.Constance;
import com.a.n.smartgym.barcode.BarcodeCaptureActivity;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.model.Visits;
import com.a.n.smartgym.repo.MuscleRepo;
import com.a.n.smartgym.repo.VisitsRepo;
import com.facebook.FacebookSdk;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
    private Gson gson;
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private Tag mTag;
    private String mDeviceName;
    private String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        InitBluetooth();

        MuscleRepo muscleRepo = new MuscleRepo();
        ExercisesDB.getInstance().keys = muscleRepo.getMainMuscle();

        mAuth = FirebaseAuth.getInstance();

        gson = new Gson();

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
        } else {
            mCurrentFragment = new DayAverageFragment();
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


    private void InitBluetooth() {
        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "myFragmentName", mCurrentFragment);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
            //scanLeDevice(true);
        }

        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();


        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
        unregisterReceiver(mGattUpdateReceiver);

        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }

    private void ScanBLE() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
            //scanLeDevice(true);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mGatt == null) {
            return;
        }

        if (mServiceConnection != null)
            unbindService(mServiceConnection);
        mBluetoothLeService = null;


        mGatt.close();

        mGatt = null;


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
        mFilters = new IntentFilter[]{
                ndef,
        };
        // Setup a tech list for all NfcF tags
        mTechLists = new String[][]{new String[]{NfcF.class.getName()}};
    }

    @Override
    public void onNewIntent(Intent intent) {
        //FacebookSdk.sdkInitialize(this);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                scanLeDevice(true);

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

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }

        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    //mResultTextView.setText(barcode.displayValue);

                    //create new visit
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
                    bundle.putString("scanresult", barcode.displayValue);
                    bundle.putString("uuid", uuid);

                    Log.d(TAG, barcode.displayValue);
                    ExercisesFragment fb = new ExercisesFragment();
                    fb.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, fb).commitAllowingStateLoss();

                } else
                    Log.d(TAG, "No barcode captured");

                //mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void StartExercise(Tag tag) {

        String Tagid = "";
        try {
            Tagid = new NdefReaderTask().execute(tag).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //NFCResult nfcTag = gson.fromJson(Tagid, NFCResult.class);

        //create new visit
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

        if (Tagid.isEmpty()) {
            mCurrentFragment = new MuscleFragment();
            mCurrentFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, mCurrentFragment, "current").commitAllowingStateLoss();
        } else {
            MuscleRepo muscleRepo = new MuscleRepo();
            Muscle ex = muscleRepo.getExerciseByID(Tagid);
            bundle.putParcelable("muscle", ex);
            bundle.putParcelable("tag", tag);
            mCurrentFragment = new ExercisesFragment();
            mCurrentFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, mCurrentFragment, "current").commit();
        }


    }

    //============BLE=============

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private void scanLeDevice(final boolean enable) {
        if (mLEScanner == null) {
            ScanBLE();
        }
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);

                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }
    }


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice btDevice = result.getDevice();
            mDeviceName = btDevice.getName();
            mDeviceAddress = btDevice.getAddress();
            if (mDeviceName.equals("niso217"))
                connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            connectToDevice(device);
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            //mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }

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
                mConnected = true;
                StartExercise(mTag);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //DATA!
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                ExercisesFragment fragment = (ExercisesFragment) mCurrentFragment;
                fragment.getMessage(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}