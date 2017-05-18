package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 14/03/17.
 */

public class NotificationItem {

    private String notificationItemTitle;
    private int notificationItemCount;
    private int notificationItemType;

    public NotificationItem(String notificationItemTitle, int notificationItemCount
            , int notificationItemType) {
        this.notificationItemTitle = notificationItemTitle;
        this.notificationItemCount = notificationItemCount;
        this.notificationItemType = notificationItemType;
    }

    public String getNotificationItemTitle() {
        return notificationItemTitle;
    }

    public int getNotificationItemCount() {
        return notificationItemCount;
    }

    public int getNotificationItemType() {
        return notificationItemType;
    }
    // 1= > Timeline
    // 2= > Requests
    // 3= > Rating
    // 4= > Comments
    // 5= > RContacts


}
