package com.a.n.smartgym;

/**
 * Created by nirb on 22/05/2017.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.a.n.smartgym.BLE.BluetoothLeService;
import com.a.n.smartgym.Helpers.BluetoothScanner;
import com.a.n.smartgym.Listener.BluetoothListener;


@TargetApi(21)
public class MainActivity2 extends AppCompatActivity implements BluetoothListener {
    private int REQUEST_ENABLE_BT = 1;
    private Button mStart, mEnd,mConnection;
    private TextView mValue, mConnectionState;
    private  BluetoothScanner mBluetoothScanner;
    private static final String TAG = MainActivity2.class.getSimpleName().toString();
    private BluetoothLeService mBluetoothLeService;
    private BluetoothDevice mBluetoothDevice;



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

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mStart = (Button) findViewById(R.id.btn_start);
        mEnd = (Button) findViewById(R.id.btn_end);
        mValue = (TextView) findViewById(R.id.tv_val);
        mConnectionState = (TextView) findViewById(R.id.tv_status);
        mConnection = (Button) findViewById(R.id.btn_connect);

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothLeService.setCharacteristicNotification(true);
            }
        });

        mEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothLeService.setCharacteristicNotification(false);
            }
        });

        mConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothLeService.disconnect();
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        mBluetoothScanner = new BluetoothScanner(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBluetoothScanner.setListener(this);
        mBluetoothScanner.scanLeDevice(true);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mBluetoothDevice.getAddress());
            Log.d(TAG, "Connect request result=" + result);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothScanner.getBluetoothAdapter() != null && mBluetoothScanner.getBluetoothAdapter().isEnabled()) {
            mBluetoothScanner.scanLeDevice(false);
            mBluetoothScanner.setListener(null);
        }
        unregisterReceiver(mGattUpdateReceiver);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
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
                UpdateUi(mConnectionState,getString(R.string.connected));
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                UpdateUi(mConnectionState,getString(R.string.disconnected));
                //invalidateOptionsMenu();
                //clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                mBluetoothLeService.setCharacteristicNotification(true);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                UpdateUi(mValue,intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };




    private void UpdateUi(final View view, final String str) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (view.getId()) {
                    case R.id.tv_val:
                        mValue.setText(str);
                        break;
                    case R.id.tv_status:
                        mConnectionState.setText(str);
                        break;
                    case R.id.btn_connect:
                        mConnection.setText(str);
                        break;
                }
            }
        });

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
}
