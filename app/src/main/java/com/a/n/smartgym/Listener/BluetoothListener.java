package com.a.n.smartgym.Listener;

import android.bluetooth.BluetoothDevice;

/**
 * Created by nirb on 24/05/2017.
 */

public interface BluetoothListener {
    public void ScanResult(BluetoothDevice device);
    public void ScanTroubleshoot(String msg);

}

