package com.rawalinfocom.rcontact.model;

/**
 * Created by admin on 11/07/17.
 */

public class Contact {

    private String DisplayName;
    private String Number;
    private String PhotoUri;
    private String Message;
    private long Date;
    private String MessageType;
    private String isRead;
    private String threadId;
    private String uniqueRowId;

    public void setContactName(String DisplayName) {
        this.DisplayName = DisplayName;
    }

    public String getDisplayName() {
        return this.DisplayName;
    }

    public void setContactNumber(String Number) {
        this.Number = Number;
    }

    public String getContactNumber() {
        return this.Number;
    }


    public void setContactPhotoUri(String PhotoUri) {
        this.PhotoUri = PhotoUri;
    }

    public String getContactPhotoUri() {
        return this.PhotoUri;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getMessage() {
        return this.Message;
    }

    public void setDate(long Date) {
        this.Date = Date;
    }

    public long getDate() {
        return this.Date;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    public String getMessageType() {
        return this.MessageType;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getUniqueRowId() {
        return uniqueRowId;
    }

    public void setUniqueRowId(String uniqueRowId) {
        this.uniqueRowId = uniqueRowId;
    }

}
