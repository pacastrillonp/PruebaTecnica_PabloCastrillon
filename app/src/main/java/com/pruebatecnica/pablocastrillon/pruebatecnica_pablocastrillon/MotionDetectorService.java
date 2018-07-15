package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.UpdateNotificationListener;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebService;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.SerializationTool;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MotionDetectorService extends Service implements SensorEventListener, UpdateNotificationListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;


    private WebService webService;
    private NotificationBody[] notificationBodies;
    private NotificationBody notificationBody;

    private int varMuestreo = 0;


    private float acelIni;
    private float acelFin;
    private float acelProm;
    private float errorBase;

    private boolean shake;
    private boolean stopWatchStart = false;
    private boolean notificationPostSend = false;


    int motionCont = 0;
    private DateTime dateTimeIni;
    private DateTime dateTimeFin;


    int seconds;

    private String initTime;


    private  DateTimeFormatter dateTimeFormatter;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // Inicializacion de sensor
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        assert mSensorManager != null;
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        // Servicio web
//        webService = new WebService(this, this);
        webService = new WebService(this, this);
        notificationBody = new NotificationBody();

        // Servicio de tiempo

         dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");


        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {


            if (varMuestreo < 8) {
                acelIni = acelIni + event.values[2];
                varMuestreo++;
            } else {

                //Calculo de la acceleracion en el eje Z
                //Valor convencionalmente verdadero
                acelProm = acelIni / 8;
                //Valor Experimental
                acelFin = event.values[2];
                errorBase = (((Math.abs(acelProm - acelFin)) / acelProm) * 100);
                System.out.println(getApplication().getResources().getString(R.string.service_started));


                // error base de 1%, superado este umbral se considera como movimiento
                if (errorBase > 1) {

                    //Captura hora del movimiento
                    if (!stopWatchStart) {
                        dateTimeIni = new DateTime();
                        initTime  = dateTimeIni.toString(dateTimeFormatter);
                        stopWatchStart = true;

                    }
                    //pregunta si el movimeinto es continuo
                    if (motionCont > 2) {
                        if (!notificationPostSend) {
                            notificationBody.setNotificationId(0);
                            notificationBody.setDate(initTime);
                            notificationBody.setDuration(0);
                            webService.postNotificationService(notificationBody);
                            System.out.println("notificacion enviada ");
                            notificationPostSend = true;
                            shake = true;
                        }
                    }
                    motionCont++;
                } else {
                    if (shake){
                        dateTimeFin = new DateTime();
                        shake = false;
                    }
                    stopWatchStart = false;
                    motionCont = 0;
                }
                if (notificationPostSend) {
                    if (notificationBody.getNotificationId() != 0) {
                        seconds = Seconds.secondsBetween(dateTimeIni, dateTimeFin).getSeconds();
                        notificationBody.setDuration(seconds);
                        webService.putNotificationService(notificationBody, String.valueOf(notificationBody.getNotificationId()));
                        System.out.println("notificacion actualizada ");
                        seconds = 0;
                        notificationPostSend = false;
                        stopWatchStart = false;
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
    public void onGetNotificationService(NotificationBody notificationBody) {
        this.notificationBody = notificationBody;

    }

    @Override
    public void onPutNotificationService() {
        sendDateBroadcast(SerializationTool.serializeToJson(notificationBody));
    }


    private void sendDateBroadcast(String notificationBodySt) {
        Intent intent = new Intent("AddItem");
        intent.putExtra("notificationBody",notificationBodySt );
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

}
