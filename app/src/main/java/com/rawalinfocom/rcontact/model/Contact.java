package com.rawalinfocom.rcontact.model;

/**
 * Created by admin on 11/07/17.
 */

public class Contact {

    String DisplayName;
    String Number;
    String PhotoUri;
    String Message;
    String Date;

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

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getDate() {
        return this.Date;
    }

}
