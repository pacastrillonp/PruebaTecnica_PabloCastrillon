package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.controller.utils.FragmentManagerActivity;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebService;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebServiceListener;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.NotificationListAdapter;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.SerializationTool;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MotionActivity extends FragmentManagerActivity implements SensorEventListener, WebServiceListener, SelectionFragment.ButtonActionClickListener, NotificationListAdapter.ButtonActionClickListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;


    private WebService webService;
    private NotificationBody[] notificationBodies;
    private NotificationBody notificationBody;


    private int varMuestreo = 0;


    private float acelIni;
    private float acelFin;
    private float promMuestreo;
    private float errorBase;

    private boolean shake;
    private boolean stopWatchStart = false;
    private boolean notificationPostSend = false;


    private DateTime dateTimeIni;
    private DateTime dateTimeFin;

    private float motionDuration = 0f;


    private String initTime;
    private String finalTime;

    private SimpleDateFormat simpleDateFormat;


    // stop watch
    private Handler handler;
    private long startTime, timeInMilliseconds, timeSwap, upDateTime;
    private Runnable runnable;
    int secs;
    int min;
    int milliSeconds;


    //fragments
    private SelectionFragment selectionFragment;
    private final String selectionFragmentTag = "selectionFragmentTag";

    private BarPlotFragment barPlotFragment;
    private final String barPlotFragmentTag = "barPlotFragmentTag";

    private NotificationFragment notificationFragment;
    private final String notificationFragmentTag = "notificationFragmentTag";

    private String activeFragment;

    private Bundle bundle;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_motion);

            // Inicializacion de sensor
            mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
            assert mSensorManager != null;
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

            // Servicio web
            webService = new WebService(this, this);
            notificationBody = new NotificationBody();

            // Servicio de tiempo
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            // Inicializacion de fragmentos
            selectionFragment = new SelectionFragment();
            barPlotFragment = new BarPlotFragment();
            notificationFragment = new NotificationFragment();
            bundle = new Bundle();

            addFragment(selectionFragment, selectionFragmentTag);
            activeFragment = selectionFragmentTag;


            // StopWatch
            handler = new Handler();

            runnable = new Runnable() {
                @Override
                public void run() {
                    timeInMilliseconds = SystemClock.uptimeMillis() - startTime;


                    upDateTime = timeSwap + timeInMilliseconds;
                    secs = (int) (upDateTime / 1000);
                    min = secs / 60;
                    milliSeconds = (int) (upDateTime % 1000);
                    System.out.println("" + min + ":" + String.format("%2d", secs) + ":" +
                            String.format("%3d", milliSeconds));
                    handler.postDelayed(this, 0);


                }
            };

        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if (varMuestreo < 10) {
                acelIni = acelIni + event.values[2];
                varMuestreo++;
            } else {
                promMuestreo = acelIni / 10;

                acelFin = event.values[2];
                errorBase = ((Math.abs(promMuestreo - acelFin)) / promMuestreo) * 100;
                System.out.println(promMuestreo);
                System.out.println(acelFin);
                System.out.println(errorBase);


                // error base de 5%, superado este umbral se considera como movimiento
                if (errorBase > 5) {
                    shake = true;
                } else {
                    shake = false;
                }


                if (shake) {
                    if (stopWatchStart) {
//                        if (motionDuration > 2) {
                        if (secs > 2) {

                            if (!notificationPostSend) {
                                notificationBody.setNotificationId(0);
                                notificationBody.setDate(initTime);
                                notificationBody.setDuration(0);
                                webService.postNotification(notificationBody);
                                notificationPostSend = true;
                            }

                            //TODO: send notification -> initTime
                        }

                    } else {

                        dateTimeIni = new DateTime();
                        initTime = simpleDateFormat.format(new Date());
                        runStopWatch();
                        stopWatchStart = true;


                    }
                } else {
                    if (stopWatchStart) {
                        pausaStopWatch();
                        //TODO: duracion del movimiento -> motionDuration

                        stopWatchStart = false;
                        notificationPostSend = false;


                        dateTimeFin = new DateTime();
                        finalTime = simpleDateFormat.format(new Date());

                        int seconds = Seconds.secondsBetween(dateTimeIni, dateTimeFin).getSeconds();
                        notificationBody.setDuration(seconds);
                        webService.putNotification(notificationBody, String.valueOf(notificationBody.getNotificationId()));


                    }
                }


                acelIni = 0;
                varMuestreo = 0;
            }


        }


    }

    private void runStopWatch() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

    }

    private void pausaStopWatch() {
        timeSwap += 0;
        timeInMilliseconds = 0;
        upDateTime = 0;
        secs = 0;
        min = 0;
        milliSeconds = 0;

        handler.removeCallbacks(runnable);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    //regionWebServiceListener


    @Override
    public void onGetNotifications(NotificationBody[] notificationBodies) {
        bundle.putInt("index", notificationBodies.length);
        bundle.putString("notificationBodies", SerializationTool.serializeToJson(notificationBodies));
        removeFragment(activeFragment);
        notificationFragment.setArguments(bundle);
        addFragment(notificationFragment, notificationFragmentTag);
        activeFragment = notificationFragmentTag;
    }

    @Override
    public void onGetNotification(NotificationBody notificationBody) {
        this.notificationBody = notificationBody;
    }

    @Override
    public void onPutNotification(NotificationBody notificationBodies) {
        notificationFragment.dataChanged(notificationBodies);
    }

    @Override
    public void onDelNotification() {

    }


    //endregion


    //regionSelectionFragment
    @Override
    public void OnShowNotificationsClick() {
        webService.getNotifications();
    }

    @Override
    public void OnShowBarPlotClick() {
        removeFragment(activeFragment);
        addFragment(barPlotFragment, barPlotFragmentTag);
        activeFragment = barPlotFragmentTag;

    }

//    endregion


    @Override
    public void onBackPressed() {

        switch (activeFragment) {
            case selectionFragmentTag:
                System.exit(0);
                break;
            case barPlotFragmentTag:
                removeFragment(barPlotFragmentTag);
                addFragment(selectionFragment, selectionFragmentTag);
                activeFragment = selectionFragmentTag;
                break;
            case notificationFragmentTag:
                removeFragment(notificationFragmentTag);
                addFragment(selectionFragment, selectionFragmentTag);
                activeFragment = selectionFragmentTag;
                break;

        }

    }

    @Override
    public void OnDeleteChannelClick(int notificationId) {
        webService.delNotification(String.valueOf(notificationId));
    }
}
