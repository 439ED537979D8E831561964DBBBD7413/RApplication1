package com.rawalinfocom.rcontact.notifications;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRequestItem {

    private String requesterName;
    private String requestInfo;
    private String notiRequestTime;

    public NotiRequestItem(String requesterName, String requestInfo, String notiRequestTime) {
        this.requesterName = requesterName;
        this.requestInfo = requestInfo;
        this.notiRequestTime = notiRequestTime;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getRequestInfo() {
        return requestInfo;
    }

    public String getNotiRequestTime() {
        return notiRequestTime;
    }
}
