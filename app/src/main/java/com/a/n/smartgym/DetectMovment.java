package com.a.n.smartgym;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.FloatMath;
import android.util.Log;

/**
 * Created by nir on 20/05/2017.
 */

public class DetectMovment extends AppCompatActivity implements SensorEventListener {

    long mAccelLast,mAccelCurrent;
    double mAccel;
    float [] mGravity;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be HelloAndroid (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity = event.values.clone();
                // Shake detection
                float x = mGravity[0];
                float y = mGravity[1];
                float z = mGravity[2];

                //Log.d("TYPE_ACCELEROMETER","x " + x );
               // Log.d("TYPE_ACCELEROMETER","y " + y );
               // Log.d("TYPE_ACCELEROMETER","z " + z );



                float yAbs = Math.abs(mGravity[1]);

                mAccelLast = mAccelCurrent;
                mAccelCurrent = (long)Math.sqrt(x * x + y * y + z * z);
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta;

                if (yAbs > 7.0) {
                    Log.d("TYPE_ACCELEROMETER","===alert===");
                    Log.d("TYPE_ACCELEROMETER",yAbs +"");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
