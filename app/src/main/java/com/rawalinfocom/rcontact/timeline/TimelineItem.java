package com.rawalinfocom.rcontact.timeline;

/**
 * Created by maulik on 08/03/17.
 */

public class TimelineItem {


    private String wisherName;
    private String eventName;
    private String notiTime;
    private String eventDetail;
    private String wisherComment;
    private String wisherCommentTime;
    private String userComment;
    private String userCommentTime;
    private int notiType;

    //0 for event
    //1 for ratings
    public TimelineItem() {

    }

    public TimelineItem(String wisherName, String eventName, String notiTime, String eventDetail, String wisherComment,
                        String wisherCommentTime, String userComment, String userCommentTime, int notiType) {
        this.wisherName = wisherName;
        this.eventName = eventName;
        this.notiTime = notiTime;
        this.eventDetail = eventDetail;
        this.wisherComment = wisherComment;
        this.wisherCommentTime = wisherCommentTime;
        this.userComment = userComment;
        this.userCommentTime = userCommentTime;
        this.notiType = notiType;
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

    public String getWisherComment() {
        return wisherComment;
    }

    public void setWisherComment(String wisherComment) {
        this.wisherComment = wisherComment;
    }

    public String getWisherCommentTime() {
        return wisherCommentTime;
    }

    public void setWisherCommentTime(String wisherCommentTime) {
        this.wisherCommentTime = wisherCommentTime;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getUserCommentTime() {
        return userCommentTime;
    }

    public void setUserCommentTime(String userCommentTime) {
        this.userCommentTime = userCommentTime;
    }

    public int getNotiType() {
        return notiType;
    }

    public void setNotiType(int notiType) {
        this.notiType = notiType;
    }
}
