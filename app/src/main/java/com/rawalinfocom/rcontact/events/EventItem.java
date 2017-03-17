package com.rawalinfocom.rcontact.events;

/**
 * Created by maulik on 08/03/17.
 */

public class EventItem {


    private String wisherName;
    private String eventName;
    private String notiTime;
    private String eventDetail;
    private String userComment;
    private int notiType;

    //0 for event
    //1 for ratings


    public EventItem(String wisherName, String eventName, String notiTime, String eventDetail
            , String userComment, int notiType) {
        this.wisherName = wisherName;
        this.eventName = eventName;
        this.notiTime = notiTime;
        this.eventDetail = eventDetail;
        this.userComment = userComment;
        this.notiType = notiType;
    }

    public EventItem() {
    }

    public String getWisherName() {
        return wisherName;
    }

    public void setWisherName(String wisherName) {
        this.wisherName = wisherName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getNotiTime() {
        return notiTime;
    }

    public void setNotiTime(String notiTime) {
        this.notiTime = notiTime;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public int getNotiType() {
        return notiType;
    }

    public void setNotiType(int notiType) {
        this.notiType = notiType;
    }
}
