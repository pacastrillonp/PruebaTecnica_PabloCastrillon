package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.controller.utils.FragmentManagerActivity;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.AxTimer;

import java.util.Date;

public class MotionActivity extends FragmentManagerActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;


    private int varMuestreo;


    private float acelIni;
    private float acelFin;
    private float promMuestreo;
    private float errorBase;


//    private float shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_motion);

            mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
            assert mSensorManager != null;
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            varMuestreo = 0;

//            acelIni = SensorManager.GRAVITY_EARTH;
//            acelLast = SensorManager.GRAVITY_EARTH;
//            shake = 0.000f;


        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {


            if (varMuestreo < 25) {
                acelIni = acelIni + event.values[2];
                varMuestreo++;
            } else {
                promMuestreo = acelIni / 25;

                acelFin = event.values[2];
                errorBase = ((Math.abs(promMuestreo - acelFin)) / promMuestreo) * 100;
                System.out.println(promMuestreo);
                System.out.println(acelFin);
                System.out.println(errorBase);

                if (errorBase > 10) {
                    long timeIni = event.timestamp;
                    Date date = new Date(timeIni);
                    System.out.println(date);
                    long timeFinal = event.timestamp;
                }
                acelIni = 0;
                varMuestreo = 0;
            }


        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
