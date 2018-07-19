package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebService;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebServiceListener;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.SerializationTool;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class WebServiceTest {
    private final Object syncObject = new Object();
    private final WebServiceListener webServiceListener = new WebServiceListener() {
        @Override
        public void onGetNotifications(NotificationBody[] notificationBodies) {

            assertNotEquals(notificationBodies, null);

            System.out.println(SerializationTool.serializeToJson(notificationBodies));

            synchronized (syncObject) {
                syncObject.notify();
            }
        }
    };


    @Test
    public void getNotifications() {
        Context context = InstrumentationRegistry.getTargetContext();
        WebService webService = new WebService(context,webServiceListener);
        webService.getNotifications();
    }
}
