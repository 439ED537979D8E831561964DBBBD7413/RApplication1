package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRContactsItem {

    private String notiTitle;
    private String notiType;
    private String notiTime;
    private String notiDetails;
    private String notiId;

    public String getNotiId() {
        return notiId;
    }

    public void setNotiId(String notiId) {
        this.notiId = notiId;
    }

    public String getNotiTitle() {
        return notiTitle;
    }

    public String getNotiType() {
        return notiType;
    }

    public String getNotiTime() {
        return notiTime;
    }

    public String getNotiDetails() {
        return notiDetails;
    }

    public void setNotiTitle(String notiTitle) {
        this.notiTitle = notiTitle;
    }

    public void setNotiType(String notiType) {
        this.notiType = notiType;
    }

    public void setNotiTime(String notiTime) {
        this.notiTime = notiTime;
    }

    public void setNotiDetails(String notiDetails) {
        this.notiDetails = notiDetails;
    }
}
