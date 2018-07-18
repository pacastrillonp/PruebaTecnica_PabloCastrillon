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

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.UpdateNotificationListener;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebService;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.SerializationTool;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MotionDetectorService extends Service implements SensorEventListener, UpdateNotificationListener {

    // Accelerometro
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private float AccelerationX;
    private float AccelerationY;
    private float AccelerationZ;


    // Servicio Web
    private WebService webService;

    //Model
    private NotificationBody notificationBody;

    // Calculo de movimiento
    private float initialAcceleration;
    private float finalAcceleration;
    private float averageAcceleration;
    private float baseError;

    private int varMuestreo = 0;

    //Maquinas de estado
    private boolean stopWatchStart = false;

    // Servicio de tiempo

    //Captura de tiempo inicial y final
    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private DateTime initialTime;
    private DateTime finalTime;
    private int seconds;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // Inicializacion de sensor
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        assert mSensorManager != null;
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        // Servicio web
        webService = new WebService(this, this);
        notificationBody = new NotificationBody();


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
                AccelerationX = event.values[0];
                AccelerationY = event.values[1];
                AccelerationZ = event.values[2];
                initialAcceleration = initialAcceleration + ((float) Math.sqrt((double) (AccelerationX * AccelerationX + AccelerationY * AccelerationY + AccelerationZ * AccelerationZ)));
                varMuestreo++;
            } else {

                //Calculo de la acceleracion resultante
                //Valor convencionalmente verdadero
                averageAcceleration = initialAcceleration / varMuestreo;
                //Valor Experimental
                finalAcceleration = ((float) Math.sqrt((double) ((AccelerationX * AccelerationX) + (AccelerationY * AccelerationY) + (AccelerationZ * AccelerationZ))));
                baseError = (((Math.abs(averageAcceleration - finalAcceleration)) / averageAcceleration) * 100);
                System.out.println(getApplication().getResources().getString(R.string.service_started));


                // error base de 3%, superado este umbral se considera como movimiento
                if (baseError > 3) {
                    //Captura hora del movimiento
                    if (!stopWatchStart) {
                        initialTime = new DateTime();//
                        stopWatchStart = true;
                    }

                } else {
                    if (stopWatchStart) {
                        finalTime = new DateTime();
                        if (initialTime != null) {
                            seconds = Seconds.secondsBetween(initialTime, finalTime).getSeconds();
                            if (seconds >= 2) {
                                notificationBody.setNotificationId(0);
                                notificationBody.setDate(initialTime.toString(dateTimeFormatter));
                                notificationBody.setDuration(seconds);
                                webService.postNotificationService(notificationBody);
                                stopWatchStart = false;
                            }
                        }

                    }
                }
                initialAcceleration = 0;
                varMuestreo = 0;
            }


        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //actualiza el modelo
    @Override
    public void onGetNotificationService(NotificationBody notificationBody) {
        sendDateBroadcast(SerializationTool.serializeToJson(notificationBody));

    }


    // se comunica con la actividad para agregar items en el fragmento notificacion.
    private void sendDateBroadcast(String notificationBodySt) {
        Intent intent = new Intent("AddItem");
        intent.putExtra("notificationBody", notificationBodySt);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

}
