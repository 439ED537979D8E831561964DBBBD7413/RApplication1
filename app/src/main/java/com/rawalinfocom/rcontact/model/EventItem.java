package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 08/03/17.
 */

public class EventItem {


    private String personName;
    private String personFirstName;
    private String personLastName;
    private String personImage;
    private String eventName;
    private String commentTime;
    private String eventDetail;
    private String userComment;
    private Integer eventType;
    private Integer personRcpPmId;
    private String eventDate;
    private String eventRecordIndexId;
    private boolean eventCommentPending;

    public String getPersonName() {
        return personName;
    }

    public String getPersonFirstName() {
        return personFirstName;
    }

    public void setPersonFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }

    public String getPersonLastName() {
        return personLastName;
    }

    public void setPersonLastName(String personLastName) {
        this.personLastName = personLastName;
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

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Integer getPersonRcpPmId() {
        return personRcpPmId;
    }

    public void setPersonRcpPmId(Integer personRcpPmId) {
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

    public String getPersonImage() {
        return personImage;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }
}
