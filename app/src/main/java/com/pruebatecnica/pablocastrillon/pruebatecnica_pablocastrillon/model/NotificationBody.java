package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model;

public class NotificationBody {

    private int NotificationId;
    private String Date;
    private int Duration;

    public int getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(int notificationId) {
        NotificationId = notificationId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }
}
