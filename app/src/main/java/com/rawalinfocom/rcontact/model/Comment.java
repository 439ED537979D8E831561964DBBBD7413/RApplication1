package com.rawalinfocom.rcontact.model;

/**
 * Created by Maulik on 18/03/17.
 */

public class Comment {

    private Integer crmId;
    private Integer crmStatus; //1. Sent, 2. Received
    private String crmRating;
    private String crmType; // "eventName" "birthday" ,"anniversary" , "become father"
    private String crmCloudPrId;
    private Integer rcProfileMasterPmId;
    private String crmComment;
    private String crmReply;
    private String crmImage;
    private String crmProfileDetails;
    private String crmCreatedAt;
    private String crmRepliedAt;
    private String crmUpdatedAt;
    private String evmRecordIndexId;

    public Integer getCrmId() {
        return crmId;
    }

    public void setCrmId(Integer crmId) {
        this.crmId = crmId;
    }

    public Integer getCrmStatus() {
        return crmStatus;
    }

    public void setCrmStatus(Integer crmStatus) {
        this.crmStatus = crmStatus;
    }

    public String getCrmRating() {
        return crmRating;
    }

    public void setCrmRating(String crmRating) {
        this.crmRating = crmRating;
    }

    public String getCrmType() {
        return crmType;
    }

    public void setCrmType(String crmType) {
        this.crmType = crmType;
    }

    public String getCrmCloudPrId() {
        return crmCloudPrId;
    }

    public void setCrmCloudPrId(String crmCloudPrId) {
        this.crmCloudPrId = crmCloudPrId;
    }

    public Integer getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(Integer rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }

    public String getCrmComment() {
        return crmComment;
    }

    public void setCrmComment(String crmComment) {
        this.crmComment = crmComment;
    }

    public String getCrmReply() {
        return crmReply;
    }

    public void setCrmReply(String crmReply) {
        this.crmReply = crmReply;
    }

    public String getCrmCreatedAt() {
        return crmCreatedAt;
    }

    public void setCrmCreatedAt(String crmCreatedAt) {
        this.crmCreatedAt = crmCreatedAt;
    }

    public String getCrmRepliedAt() {
        return crmRepliedAt;
    }

    public void setCrmRepliedAt(String crmRepliedAt) {
        this.crmRepliedAt = crmRepliedAt;
    }

    public String getCrmUpdatedAt() {
        return crmUpdatedAt;
    }

    public void setCrmUpdatedAt(String crmUpdatedAt) {
        this.crmUpdatedAt = crmUpdatedAt;
    }

    public String getEvmRecordIndexId() {
        return evmRecordIndexId;
    }

    public void setEvmRecordIndexId(String evmRecordIndexId) {
        this.evmRecordIndexId = evmRecordIndexId;
    }

    public String getCrmImage() {
        return crmImage;
    }

    public void setCrmImage(String crmImage) {
        this.crmImage = crmImage;
    }

    public String getCrmProfileDetails() {
        return crmProfileDetails;
    }

    public void setCrmProfileDetails(String crmProfileDetails) {
        this.crmProfileDetails = crmProfileDetails;
    }
}
