package com.rawalinfocom.rcontact.model;

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
    private String crmCloudPrId;
    private String crmType;
    private String crmRating;
    private String evmRecordIndexId;
    private String wisherProfileImage;
    private String userprofileImage;

    public String getCrmRating() {
        return crmRating;
    }

    public void setCrmRating(String crmRating) {
        this.crmRating = crmRating;
    }

    public String getCrmCloudPrId() {
        return crmCloudPrId;
    }

    public String getEvmRecordIndexId() {
        return evmRecordIndexId;
    }

    public void setEvmRecordIndexId(String evmRecordIndexId) {
        this.evmRecordIndexId = evmRecordIndexId;
    }

    public void setCrmCloudPrId(String crmCloudPrId) {
        this.crmCloudPrId = crmCloudPrId;
    }

    public String getCrmType() {
        return crmType;
    }

    public void setCrmType(String crmType) {
        this.crmType = crmType;
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

    public String getWisherProfileImage() {
        return wisherProfileImage;
    }

    public void setWisherProfileImage(String wisherProfileImage) {
        this.wisherProfileImage = wisherProfileImage;
    }

    public String getUserprofileImage() {
        return userprofileImage;
    }

    public void setUserprofileImage(String userprofileImage) {
        this.userprofileImage = userprofileImage;
    }
}
