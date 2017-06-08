package com.a.n.smartgym.Helpers;

/**
 * Created by nirb on 18/04/2017.
 */

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.a.n.smartgym.Listener.BluetoothListener;
import com.a.n.smartgym.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Background task for reading the data. Do not block the UI thread while reading.
 *
 * @author Ralf Wondratschek
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BluetoothScanner {

    private Context mContext;
    private Handler mHandler = new Handler();
    ;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private static final long SCAN_PERIOD = 30000;
    private static final String TAG = BluetoothScanner.class.getSimpleName().toString();
    private BluetoothListener mBluetoothListener;
    private boolean mScanResultStatus;

    public BluetoothScanner(Context mContext) {
        this.mContext = mContext;
        InitBluetooth();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setListener(BluetoothListener listener) {
        this.mBluetoothListener = listener;
    }

    private void InitBluetooth() {


        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= 21) {

            ScanFilter scanFilter = new ScanFilter.Builder()
//                    .setDeviceName(Constants.EMULATOR_NAME)
                    .build();
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_OPPORTUNISTIC)
                    .build();
            filters = new ArrayList<>();
            filters.add(scanFilter);
        }
    }

    public void ChangeFilter(String devicename){
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setDeviceName(devicename)
                .build();
        filters = new ArrayList<>();
        filters.add(scanFilter);
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            mScanResultStatus = false;
            if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);

                    }
                    if (!mScanResultStatus)
                        if (mBluetoothListener != null)
                            mBluetoothListener.ScanTroubleshoot("Scan timeout");

                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {

            mHandler.removeCallbacksAndMessages(null);

            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }

        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {

                    Log.d(TAG, "name:" + device.getName() + " address " + device.getAddress());
                }
            };


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            scanLeDevice(false);
            mScanResultStatus = true;
            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "name:" + device.getName() + " address " + device.getAddress());
            if (mBluetoothListener!=null){
                mBluetoothListener.ScanResult(device);

            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.d(TAG, "onBatchScanResults " + sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            mScanResultStatus = false;
            Log.e("Scan Failed", "Error Code: " + errorCode);
            mBluetoothListener.ScanTroubleshoot("Scan Failed Error Code: " + errorCode);
        }
    };


}
