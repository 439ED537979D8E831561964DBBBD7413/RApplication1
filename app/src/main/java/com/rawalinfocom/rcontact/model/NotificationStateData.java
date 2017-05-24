package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 15/05/17.
 */

public class NotificationStateData {

    private int notificationState;
    private int notificationType;
    private String cloudNotificationId;
    private String createdAt;
    private String updatedAt;
    private String notificationMasterId;

    public String getNotificationMasterId() {
        return notificationMasterId;
    }

    public void setNotificationMasterId(String notificationMasterId) {
        this.notificationMasterId = notificationMasterId;
    }

    public int getNotificationState() {
        return notificationState;
    }

    public void setNotificationState(int notificationState) {
        this.notificationState = notificationState;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getCloudNotificationId() {
        return cloudNotificationId;
    }

    public void setCloudNotificationId(String cloudNotificationId) {
        this.cloudNotificationId = cloudNotificationId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
