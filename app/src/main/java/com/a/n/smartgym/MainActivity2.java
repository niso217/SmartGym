package com.a.n.smartgym;

/**
 * Created by nirb on 22/05/2017.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.a.n.smartgym.BLE.BluetoothLeService;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@TargetApi(21)
public class MainActivity2 extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 20000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private BluetoothDevice mCurrentBluetoothDevice;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    private Button mStart, mEnd,mConnection;
    private TextView mValue, mStatus;
    private int mCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mStart = (Button) findViewById(R.id.btn_start);
        mEnd = (Button) findViewById(R.id.btn_end);
        mValue = (TextView) findViewById(R.id.tv_val);
        mStatus = (TextView) findViewById(R.id.tv_status);
        mConnection = (Button) findViewById(R.id.btn_connect);

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnableCharacteristicNotification();
            }
        });

        mEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisableCharacteristicNotification();
            }
        });

        mConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGatt!=null){
                    switch (mCurrentState){
                        case BluetoothProfile.STATE_CONNECTED:
                            DisableCharacteristicNotification();
                            mGatt.disconnect();
                            break;
                        default:
                            scanLeDevice(true);
                            break;
                    }

                }
            }
        });


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
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {

                ScanFilter scanFilter = new ScanFilter.Builder()
                        .setDeviceName("niso217")
                        .build();
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                        .build();
                filters = new ArrayList<>();
                filters.add(scanFilter);
            }
            scanLeDevice(true);
        }

        //scan specified devices only with ScanFilter


        ScanSettings scanSettings =
                new ScanSettings.Builder().build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
    }

    private void DisableCharacteristicNotification() {

        for (BluetoothGattDescriptor descriptor : mBluetoothGattCharacteristic.getDescriptors()) {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            mGatt.writeDescriptor(descriptor);
        }
        mGatt.setCharacteristicNotification(mBluetoothGattCharacteristic, false);
    }

    private void EnableCharacteristicNotification() {
        for (BluetoothGattDescriptor descriptor : mBluetoothGattCharacteristic.getDescriptors()) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mGatt.writeDescriptor(descriptor);
        }
        mGatt.setCharacteristicNotification(mBluetoothGattCharacteristic, true);
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

    private void scanLeDevice(final boolean enable) {
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
            Log.d("result", btDevice.getName());
            if (btDevice.getName().equals("niso217")) {
                mCurrentBluetoothDevice = btDevice;
                connectToDevice(btDevice);

            }
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
        mGatt = device.connectGatt(this, false, gattCallback);
        scanLeDevice(false);
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    UpdateUi(mStatus, "CONNECTED");
                    UpdateUi(mConnection,"DISCONNECT");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if (status == 133) {
                        mGatt.disconnect();
                        connectToDevice(mCurrentBluetoothDevice);
                    }
                    UpdateUi(mStatus, "DISCONNECTED");
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    UpdateUi(mConnection,"CONNECT");
                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    Log.e("gattCallback", "STATE_CONNECTING");
                    UpdateUi(mStatus, "CONNECTING");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
                    UpdateUi(mStatus, "OTHER");


            }
            mCurrentState = newState;
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            mBluetoothGattCharacteristic = services.get(2).getCharacteristics().get(0);


        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            final byte[] data = characteristic.getValue();

            if (gatt.getDevice().getName().equals("niso217")) {
                Log.d("onCharacteristicChanged", data[0] + "");
                UpdateUi(mValue, data[0] + "");

            } else {
                try {
                    String x = new String(data, "UTF-8");
                    Log.d("onCharacteristicChanged", x);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();

                }
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
                        mStatus.setText(str);
                        break;
                    case R.id.btn_connect:
                        mConnection.setText(str);
                        break;
                }
            }
        });

    }

}
