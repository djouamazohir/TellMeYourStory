package com.zohirdev.mystory.items;

public class NotificationsItem {

    String notificationID;
    String notificationType;
    String order;
    String storyID;
    String sender;
    String receiver;
    String seen;
    String time;
    String date;

    public NotificationsItem(String notificationID, String notificationType, String order, String storyID, String sender, String receiver, String seen, String time, String date) {
        this.notificationID = notificationID;
        this.notificationType = notificationType;
        this.order = order;
        this.storyID = storyID;
        this.sender = sender;
        this.receiver = receiver;
        this.seen = seen;
        this.time = time;
        this.date = date;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getOrder() {
        return order;
    }

    public String getStoryID() {
        return storyID;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSeen() {
        return seen;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
