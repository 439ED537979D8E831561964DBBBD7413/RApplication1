package com.rawalinfocom.rcontact.events;

/**
 * Created by maulik on 08/03/17.
 */

public class EventItem {


    private String personName;
    private String eventName;
    private String commentTime;
    private String eventDetail;
    private String userComment;
    private int eventType;
    private int personRcpPmId;
    private String eventDate;
    private String eventRecordIndexId;
    private boolean eventCommentPending;

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
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

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getPersonRcpPmId() {
        return personRcpPmId;
    }

    public void setPersonRcpPmId(int personRcpPmId) {
        this.personRcpPmId = personRcpPmId;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventRecordIndexId() {
        return eventRecordIndexId;
    }

    public void setEventRecordIndexId(String eventRecordIndexId) {
        this.eventRecordIndexId = eventRecordIndexId;
    }

    public boolean isEventCommentPending() {
        return eventCommentPending;
    }

    public void setEventCommentPending(boolean eventCommentPending) {
        this.eventCommentPending = eventCommentPending;
    }
}
