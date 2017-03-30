package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRContactsItem {

    private String notiTitle;
    private String notiTime;
    private String notiDetails;

    public NotiRContactsItem(String notiTitle, String notiDetails, String notiTime) {
        this.notiTitle = notiTitle;
        this.notiTime = notiTime;
        this.notiDetails = notiDetails;
    }

    public String getNotiTitle() {
        return notiTitle;
    }

    public String getNotiTime() {
        return notiTime;
    }

    public String getNotiDetails() {
        return notiDetails;
    }
}
