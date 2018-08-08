package com.ridho.shareblood.Model;


public class Sender {
    public String to;
    public Notification notification;
    String message_id;

    public Sender() {
    }

    public Sender(String to, Notification notification, String message_id) {
        this.to = to;
        this.notification = notification;
        this.message_id = message_id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
