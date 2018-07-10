package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.controller.utils.FragmentManagerActivity;

public class MotionActivity extends FragmentManagerActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            setContentView(R.layout.activity_motion);

            mSensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
            assert mSensorManager != null;
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.v("MotionActivity","Value of X: "+event.values[0]);
        Log.v("MotionActivity","Value of Y: "+event.values[1]);
        Log.v("MotionActivity","Value of Z: "+event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
