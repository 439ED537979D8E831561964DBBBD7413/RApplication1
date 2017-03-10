package com.rawalinfocom.rcontact.timeline;

/**
 * Created by maulik on 08/03/17.
 */

public class TimelineItem {


    private String wisherName;
    private String eventName;
    private String eventDetail;
    private String wisherComment;
    private String userComment;
    private int notiType;

    //0 for event
    //1 for ratings


    public TimelineItem(String wisherName, String eventName, String eventDetail, String wisherComment, String userComment, int notiType) {
        this.wisherName = wisherName;
        this.eventName = eventName;
        this.eventDetail = eventDetail;
        this.wisherComment = wisherComment;
        this.userComment = userComment;
        this.notiType = notiType;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }


    public int getNotiType() {
        return notiType;
    }

    public void setNotiType(int notiType) {
        this.notiType = notiType;
    }

    public String getWisherName() {
        return wisherName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setWisherName(String wisherName) {
        this.wisherName = wisherName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getWisherComment() {
        return wisherComment;
    }

    public void setWisherComment(String wisherComment) {
        this.wisherComment = wisherComment;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }
}
