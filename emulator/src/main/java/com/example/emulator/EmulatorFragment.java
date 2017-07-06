/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.emulator;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;


public class EmulatorFragment extends ServiceFragment implements OnSeekBarChangeListener {

  private static final UUID BATTERY_SERVICE_UUID = UUID
          .fromString("0000180F-0000-1000-8000-00805f9b34fb");

  private static final UUID BATTERY_LEVEL_UUID = UUID
          .fromString("00002A19-0000-1000-8000-00805f9b34fb");
  private static final int INITIAL_BATTERY_LEVEL = 20;
  private static final int BATTERY_LEVEL_MAX = 100;
  private static final String BATTERY_LEVEL_DESCRIPTION = "The current charge level of a " +
          "battery. 100% represents fully charged while 0% represents fully discharged.";

  private ServiceFragmentDelegate mDelegate;
  private boolean running;
  private int mTrashHold = 10;
  private boolean Resting;

  private SeekBar sb_sets,sb_reps,sb_sleep,sb_weight;
  private TextView tv_sets,tv_reps,tv_sleep,tv_weight;
  private TextView et_sets,et_reps,et_sleep,et_weight;
  private int sets,reps,sleep,weight;

  private int mCurrentWeight,mCurrentRepetition,mCalculatedWeight,mCurrentDirection=1;
  private Handler mHandler;
  private AsyncTaskDemoPractice mDemoPractice;


  // Define the code block to be executed
  private Runnable runnable = new Runnable() {
    @Override
    public void run() {

      String value = "";
      if (!Resting)
        value = mCalculatedWeight+"," + mCurrentRepetition +"," + mCalculatedWeight + "," + mCurrentDirection +"";
      else
      {
        value = "0,0,0,0";

      }
      mBatteryLevelCharacteristic.setValue(value);
      // Insert custom code here
      mDelegate.sendNotificationToDevices(mBatteryLevelCharacteristic);
      // Repeat every 2 seconds
      mHandler.postDelayed(runnable, 50);
    }
  };



  @Override
  public void onStop() {
    super.onStop();
    mDemoPractice.cancel(true);
  }




  // GATT
  private BluetoothGattService mBatteryService;
  private BluetoothGattCharacteristic mBatteryLevelCharacteristic;

  public EmulatorFragment() {
    mBatteryLevelCharacteristic =
            new BluetoothGattCharacteristic(BATTERY_LEVEL_UUID,
                    BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                    BluetoothGattCharacteristic.PERMISSION_READ);

    mBatteryLevelCharacteristic.addDescriptor(
            Peripheral.getClientCharacteristicConfigurationDescriptor());

    mBatteryLevelCharacteristic.addDescriptor(
            Peripheral.getCharacteristicUserDescriptionDescriptor(BATTERY_LEVEL_DESCRIPTION));

    mBatteryService = new BluetoothGattService(BATTERY_SERVICE_UUID,
            BluetoothGattService.SERVICE_TYPE_PRIMARY);
    mBatteryService.addCharacteristic(mBatteryLevelCharacteristic);
  }

  // Lifecycle callbacks
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_emulator, container, false);

    sb_sets = (SeekBar) view.findViewById(R.id.sb_sets);
    sb_reps = (SeekBar) view.findViewById(R.id.sb_reps);
    sb_sleep = (SeekBar) view.findViewById(R.id.sb_sleep);
    sb_weight = (SeekBar) view.findViewById(R.id.sb_weight);

    sb_sets.setOnSeekBarChangeListener(this);
    sb_reps.setOnSeekBarChangeListener(this);
    sb_sleep.setOnSeekBarChangeListener(this);
    sb_weight.setOnSeekBarChangeListener(this);

    tv_sets = (TextView) view.findViewById(R.id.tv_sets);
    tv_reps = (TextView) view.findViewById(R.id.tv_reps);
    tv_sleep = (TextView) view.findViewById(R.id.tv_sleep);
    tv_weight = (TextView) view.findViewById(R.id.tv_weight);

    et_sets = (TextView) view.findViewById(R.id.et_sets);
    et_reps = (TextView) view.findViewById(R.id.et_reps);
    et_sleep = (TextView) view.findViewById(R.id.et_sleep);

    mHandler = new Handler(getActivity().getMainLooper());
    mCalculatedWeight = mCurrentWeight = sb_weight.getProgress();

    sets = sb_sets.getProgress();
    reps = sb_reps.getProgress();
    sleep = sb_sleep.getProgress();
    weight = sb_weight.getProgress();

    mDemoPractice = new AsyncTaskDemoPractice();


    Button btn_stop = (Button) view.findViewById(R.id.btn_stop);
    btn_stop.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
//        if (Resting){
//          mDemoPractice.execute();
//          Resting = false;
//        }
//        else
//        {
//          mDemoPractice.cancel(true);
//          Resting = true;
//          et_reps.setText("0");
//          et_sets.setText("0");
//          et_sleep.setText("0");
//
//        }
        mDemoPractice.cancel(true);
//        et_reps.setText("0");
//        et_sets.setText("0");
//        et_sleep.setText("0");

      }
    });


    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mDelegate = (ServiceFragmentDelegate) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
              + " must implement ServiceFragmentDelegate");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().unregisterReceiver(mGattUpdateReceiver);

  }

  @Override
  public void onDetach() {
    super.onDetach();
    mDelegate = null;
  }

  public BluetoothGattService getBluetoothGattService() {
    return mBatteryService;
  }

  @Override
  public ParcelUuid getServiceUUID() {
    return new ParcelUuid(BATTERY_SERVICE_UUID);
  }

  @Override
  public void notificationsEnabled(BluetoothGattCharacteristic characteristic, boolean indicate) {

    if (characteristic.getUuid() != BATTERY_LEVEL_UUID) {
      return;
    }

    if (indicate) {
      return;
    }
    mCurrentRepetition = 0;
    mHandler.post(runnable);

    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getActivity(), R.string.notificationsEnabled, Toast.LENGTH_SHORT)
                .show();
      }
    });

    mDemoPractice.execute();


  }

  @Override
  public void notificationsDisabled(BluetoothGattCharacteristic characteristic) {
    if (characteristic.getUuid() != BATTERY_LEVEL_UUID) {
      return;
    }
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getActivity(), R.string.notificationsNotEnabled, Toast.LENGTH_SHORT)
                .show();
      }
    });
    mDemoPractice.execute();
  }



  private void updateUi(final TextView textView, final String text){
    new Handler(getActivity().getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        textView.setText(text);
      }
    });
  }

  private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      final String action = intent.getAction();
      if (Peripheral.ACTION_GATT_CONNECTION.equals(action)) {
          boolean isconnected= intent.getBooleanExtra(Peripheral.CONNECTION_STATUS,false);
          if (!isconnected){
              mDemoPractice.cancel(true);


          }


      }
    }
  };

  private static IntentFilter makeGattUpdateIntentFilter() {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Peripheral.ACTION_GATT_CONNECTION);
    return intentFilter;
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    switch(seekBar.getId())
    {
      case R.id.sb_sets:
        tv_sets.setText(seekBar.getProgress()+"");
        sets = sb_sets.getProgress();
        break;
      case R.id.sb_reps:
        reps = sb_reps.getProgress();
        tv_reps.setText(seekBar.getProgress()+"");
        break;
      case R.id.sb_sleep:
        sleep = sb_sleep.getProgress();
        tv_sleep.setText(seekBar.getProgress()+"");
        break;
      case R.id.sb_weight:
        weight = sb_weight.getProgress();
        mCalculatedWeight = seekBar.getProgress();
        tv_weight.setText(seekBar.getProgress()+"");
        break;
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {

  }

  private class AsyncTaskDemoPractice extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        mCurrentDirection = 1;
          try {
            for (int i = 0; i < sets; i++) {
              updateUi(et_sets,i+"");
              if (i!=0) //first set dont rest
              {
                Resting = true;
                Thread.sleep(sleep * 1000); //rest x seconds
              }
              Resting = false;
              for (int j = 0; j < reps; j++) {
                updateUi(et_reps,j+"");
                mCurrentRepetition = j;
                Thread.sleep(1000);
              }
            }


          } catch (InterruptedException e) {
            e.printStackTrace();
          }

        System.out.println("Server is stopped....");
      return null;

    }

  }
}

