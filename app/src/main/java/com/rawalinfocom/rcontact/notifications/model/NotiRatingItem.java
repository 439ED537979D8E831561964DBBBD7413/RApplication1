package com.rawalinfocom.rcontact.notifications.model;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRatingItem {

    private String raterName;
    private String raterInfo;
    private String notiRatingTime;

    public NotiRatingItem(String raterName, String raterInfo, String notiRatingTime) {
        this.raterName = raterName;
        this.raterInfo = raterInfo;
        this.notiRatingTime = notiRatingTime;
    }

    public String getRaterName() {
        return raterName;
    }

    public String getRaterInfo() {
        return raterInfo;
    }

    public String getNotiRatingTime() {
        return notiRatingTime;
    }
}
