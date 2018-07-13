package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebService;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebServiceListener;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.SerializationTool;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MotionDetectorService extends Service implements SensorEventListener, WebServiceListener  {

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

    private NotificationFragment notificationFragment;
    private String initTime;


    private SimpleDateFormat simpleDateFormat;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        notificationFragment = new NotificationFragment();


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





        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
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
                                webService.postNotificationService(notificationBody);
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
                                    webService.putNotificationService(notificationBody, String.valueOf(notificationBody.getNotificationId()));
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



    @Override
    public void onGetNotifications(NotificationBody[] notificationBodies) {

    }

    @Override
    public void onGetNotification(NotificationBody notificationBody) {

    }

    @Override
    public void onGetNotificationService(NotificationBody notificationBody) {
        this.notificationBody = notificationBody;
    }

    @Override
    public void onPutNotificationService() {

    }

    @Override
    public void onPutNotification(NotificationBody notificationBodies) {



    }

    @Override
    public void onDelNotification() {

    }


}
