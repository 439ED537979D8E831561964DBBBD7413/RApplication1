package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 14/03/17.
 */

public class NotificationItem {

    private String notificationItemTitle;
    private Integer notificationItemCount;
    private Integer notificationItemType;

    public NotificationItem(String notificationItemTitle, Integer notificationItemCount
            , Integer notificationItemType) {
        this.notificationItemTitle = notificationItemTitle;
        this.notificationItemCount = notificationItemCount;
        this.notificationItemType = notificationItemType;
    }

    public String getNotificationItemTitle() {
        return notificationItemTitle;
    }

    public Integer getNotificationItemCount() {
        return notificationItemCount;
    }

    public Integer getNotificationItemType() {
        return notificationItemType;
    }
    // 1= > Timeline
    // 2= > Requests
    // 3= > Rating
    // 4= > Comments
    // 5= > RContacts


}
