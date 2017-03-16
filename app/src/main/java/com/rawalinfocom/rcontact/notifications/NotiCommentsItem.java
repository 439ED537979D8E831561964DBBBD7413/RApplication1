package com.rawalinfocom.rcontact.notifications;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiCommentsItem {

    private String commenterName;
    private String commenterInfo;
    private String notiCommentTime;

    public NotiCommentsItem(String commenterName, String commenterInfo, String notiCommentTime) {
        this.commenterName = commenterName;
        this.commenterInfo = commenterInfo;
        this.notiCommentTime = notiCommentTime;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public String getCommenterInfo() {
        return commenterInfo;
    }

    public String getNotiCommentTime() {
        return notiCommentTime;
    }
}
