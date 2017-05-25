package com.a.n.smartgym;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.Objects.Muscles;
import com.a.n.smartgym.Objects.NFCResult;
import com.a.n.smartgym.Utils.Constance;
import com.a.n.smartgym.Utils.PermissionsUtil;
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
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@TargetApi(21)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, BluetoothListener {
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
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private Fragment mCurrentFragment;
    private int REQUEST_ENABLE_BT = 1;
    private Button mStart, mEnd, mConnection;
    private TextView mValue, mConnectionState;
    private BluetoothScanner mBluetoothScanner;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothDevice mBluetoothDevice;
    private boolean mConnectionStatus;
    final public static int REQUEST_CODE = 123;
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "an" + "droid.permission.WRITE_EXTERNAL_STORAGE";
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";

    // Code to manage Service lifecycle.
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AskForPermissions();

        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        mBluetoothScanner = new BluetoothScanner(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        MuscleRepo muscleRepo = new MuscleRepo();
        ExercisesDB.getInstance().keys = muscleRepo.getMainMuscle();

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

        handleIntent(getIntent());





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
        mFilters = new IntentFilter[]{
                ndef,
        };
        // Setup a tech list for all NfcF tags
        mTechLists = new String[][]{new String[]{NfcF.class.getName()}};
    }

    @Override
    protected void onResume() {
        super.onResume();


        mBluetoothScanner.setListener(this);
        mBluetoothScanner.scanLeDevice(true);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mBluetoothDevice.getAddress());
//            Log.d(TAG, "Connect request result=" + result);
//        }

        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
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

        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    public void connectToDevice(BluetoothDevice device) {
        mBluetoothLeService.connect(device.getAddress());
        mBluetoothScanner.scanLeDevice(false);
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
                //UpdateUi(mConnectionState,getString(R.string.connected));
                mConnectionStatus = true;
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
               // UpdateUi(mConnectionState,getString(R.string.disconnected));
                //invalidateOptionsMenu();
                //clearUI();
                mBluetoothLeService.setCharacteristicNotification(false);
                mConnectionStatus = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                mBluetoothLeService.setCharacteristicNotification(true);

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG,intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                ExercisesFragment fragment = (ExercisesFragment)getSupportFragmentManager().findFragmentByTag("EX");
                if (fragment!=null && fragment.isVisible())
                {
                    fragment.getMessage(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                }
              //  UpdateUi(mValue,intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

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
            fragmentTransaction.replace(R.id.containerView, mCurrentFragment).commitAllowingStateLoss();
        } else {
            MuscleRepo muscleRepo = new MuscleRepo();
            Muscle ex = muscleRepo.getExerciseByID(Tagid);
            bundle.putParcelable("muscle", ex);
            bundle.putParcelable("tag", tag);
            mCurrentFragment = new ExercisesFragment();
            mCurrentFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, mCurrentFragment,"EX").commit();
        }


    }

    @Override
    public void DeviceAvailable(BluetoothDevice device) {
        connectToDevice(mBluetoothDevice = device);
        Log.d(TAG,"DeviceAvailable");
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    private void AskForPermissions() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                requestPermissions();
            }
        });
    }

    private void requestPermissions() {
        List<String> unGranted = PermissionsUtil.getInstance(this).checkPermissions();
        if (unGranted.size() != 0)
            PermissionsUtil.getInstance(this).requestPermissions(unGranted, REQUEST_CODE);
        else {
            Log.d(TAG, "SetUpDisplayView requestPermissions");


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        List<String> unGranted = PermissionsUtil.getInstance(this).checkPermissionsRequest(permissions, grantResults);
        switch (requestCode) {
            //Confirm the result of which request to return
            case REQUEST_CODE:
                if (unGranted.size() == 0) {
                    //All permissions have been granted
                } else {
                    Iterator<String> iterator = unGranted.iterator();
                    PermissionResolver(iterator.next());
                }
                break;
        }
    }

    private void PermissionResolver(String Permission) {
        boolean messege = false;
        String AlertMessege = "";
        switch (Permission) {


            case ACCESS_COARSE_LOCATION:
                messege = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
                AlertMessege = getResources().getString(R.string.request_location);
                break;
            case READ_EXTERNAL_STORAGE:
                messege = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                AlertMessege = getResources().getString(R.string.request_read_write);
                break;
            case WRITE_EXTERNAL_STORAGE:
                messege = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                AlertMessege = getResources().getString(R.string.request_read_write);
                break;
        }
        if (messege) {
            AlertDialog(AlertMessege);
        } else {
            //user has denied with `Never Ask Again`, go to settings
            promptAppSettings();
        }
    }

    private void AlertDialog(String message) {
        //user denied without Never ask again, just show rationale explanation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permission_denied));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(getResources().getString(R.string.re_try), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestPermissions();
            }

        });
        builder.show();
    }

    private void promptAppSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permission_denied));
        builder.setMessage(getResources().getString(R.string.please_fix));
        builder.setPositiveButton(getResources().getString(R.string.go_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goToAppSettings();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void promptLocationSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.location_denied));
        builder.setMessage(getResources().getString(R.string.location_fix));
        builder.setPositiveButton(getResources().getString(R.string.go_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goLocationSettings();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    private void goToAppSettings() {
        Intent AppSettings = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + this.getPackageName()));
        AppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        AppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //myAppSettings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(AppSettings);
        finish();
    }

    private void goLocationSettings() {
        Intent LocationSettings = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        LocationSettings.addCategory(Intent.CATEGORY_DEFAULT);
        LocationSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //myAppSettings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(LocationSettings);
        finish();
    }

}