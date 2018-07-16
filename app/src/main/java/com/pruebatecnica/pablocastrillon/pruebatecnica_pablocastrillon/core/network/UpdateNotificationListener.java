package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;
//interfaz para capturar las respues web en el servicio
public interface UpdateNotificationListener {
    void onGetNotificationService (NotificationBody notificationBodies);
    void onPutNotificationService ();
}
