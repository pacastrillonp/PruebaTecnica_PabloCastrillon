package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

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

public class MotionActivity extends FragmentManagerActivity implements SensorEventListener, WebServiceListener, SelectionFragment.ButtonActionClickListener, NotificationListAdapter.ButtonActionClickListener{

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
    private DateTime dateTimeTem;


    private String initTime;


    private SimpleDateFormat simpleDateFormat;


    //fragments
    private SelectionFragment selectionFragment;
    private final String selectionFragmentTag = "selectionFragmentTag";

    private BarPlotFragment barPlotFragment;
    private final String barPlotFragmentTag = "barPlotFragmentTag";

    private NotificationFragment notificationFragment;
    private final String notificationFragmentTag = "notificationFragmentTag";

    private String activeFragment;

    private Bundle bundle;

    public static final String ARKBOX_LAUNCHER_FILTER = "LauncherDate";


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_motion);

            try {
                LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(ARKBOX_LAUNCHER_FILTER));
            } catch (Exception e) {
                e.printStackTrace();
            }


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

                dateTimeTem = new DateTime();


                // error base de 5%, superado este umbral se considera como movimiento
                if (errorBase > 5) {
                    shake = true;
                } else {
                    shake = false;
                }


                if (shake) {
                    if (!stopWatchStart) {
                        dateTimeIni = new DateTime();
                        initTime = simpleDateFormat.format(new Date());
                        stopWatchStart = true;
                    } else {
                        if (Seconds.secondsBetween(dateTimeIni, dateTimeTem).getSeconds() >= 2) {

                            if (!notificationPostSend) {
                                notificationBody.setNotificationId(0);
                                notificationBody.setDate(initTime);
                                notificationBody.setDuration(0);
                                webService.postNotification(notificationBody);
                                notificationPostSend = true;
                            }
                        }
                    }
                } else {
                    if (stopWatchStart) {
                        stopWatchStart = false;
                        notificationPostSend = false;
                        dateTimeFin = new DateTime();

                        int seconds = Seconds.secondsBetween(dateTimeIni, dateTimeFin).getSeconds();
                        if (seconds >= 2) {
                            while (true) {
                                if (notificationBody.getNotificationId() != 0) {
                                    notificationBody.setDuration(seconds);
                                    webService.putNotification(notificationBody, String.valueOf(notificationBody.getNotificationId()));
                                    break;
                                }
                            }
                        }
                    }
                }
                acelIni = 0;
                varMuestreo = 0;
            }


        }


    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    //regionWebServiceListener


    @Override
    public void onGetNotifications(NotificationBody[] notificationBodies) {

        this.notificationBodies = notificationBodies;

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
    public void onGetNotificationService(NotificationBody notificationBodies) {

    }

    @Override
    public void onPutNotificationService() {

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

        bundle.putInt("index", notificationBodies.length);
        bundle.putString("notificationBodies", SerializationTool.serializeToJson(notificationBodies));
        removeFragment(activeFragment);
        barPlotFragment.setArguments(bundle);
        addFragment(barPlotFragment, barPlotFragmentTag);
        activeFragment = barPlotFragmentTag;

    }

//    endregion


    @Override
    public void onBackPressed() {

        switch (activeFragment) {
            case selectionFragmentTag:
                super.onBackPressed();
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
    protected void onResume() {
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(ARKBOX_LAUNCHER_FILTER));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {


        super.onPause();
    }

    @Override
    protected void onStop() {
        startService(new Intent(this,MotionDetectorService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        stopService(new Intent(this,MotionDetectorService.class));
        super.onStart();
    }

    @Override
    public void OnDeleteChannelClick(int notificationId) {
        webService.delNotification(String.valueOf(notificationId));
    }


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("llegue");
        }
    };

}
