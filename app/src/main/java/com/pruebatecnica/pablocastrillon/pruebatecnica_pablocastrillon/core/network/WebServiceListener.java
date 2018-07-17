package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

public interface WebServiceListener {
    void onGetNotifications (NotificationBody[] notificationBodies);
}
