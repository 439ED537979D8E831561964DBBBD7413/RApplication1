package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class Website {

    private String wmId;
    private String wmRecordIndexId;
    private String wmWebsiteUrl;
    private String wmWebsiteType;
    private String wmWebsitePrivacy;
    private String rcProfileMasterPmId;
    private Integer wmIsPrivate;

//    private String wmCustomType;

    public String getWmId() {
        return wmId;
    }

    public void setWmId(String wmId) {
        this.wmId = wmId;
    }

    public String getWmWebsiteUrl() {
        return wmWebsiteUrl;
    }

    public void setWmWebsiteUrl(String wmWebsiteUrl) {
        this.wmWebsiteUrl = wmWebsiteUrl;
    }

    public String getWmWebsiteType() {
        return wmWebsiteType;
    }

    public void setWmWebsiteType(String wmWebsiteType) {
        this.wmWebsiteType = wmWebsiteType;
    }

    public String getWmWebsitePrivacy() {
        return wmWebsitePrivacy;
    }

    public void setWmWebsitePrivacy(String wmWebsitePrivacy) {
        this.wmWebsitePrivacy = wmWebsitePrivacy;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }

    public String getWmRecordIndexId() {
        return wmRecordIndexId;
    }

    public void setWmRecordIndexId(String wmRecordIndexId) {
        this.wmRecordIndexId = wmRecordIndexId;
    }

    public Integer getWmIsPrivate() {
        return wmIsPrivate;
    }

    public void setWmIsPrivate(Integer wmIsPrivate) {
        this.wmIsPrivate = wmIsPrivate;
    }
}
