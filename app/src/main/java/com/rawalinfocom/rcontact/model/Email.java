package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 14/11/16.
 */

public class Email {

    private String emId;
    private String emEmailAddress;
    private String emEmailType;
    private String emRecordIndexId;
    private String emEmailPrivacy;
    private String emIsVerified;
    private int emIsPrivate;
    private String rcProfileMasterPmId;

    //    private String emCloudId;
    //    private String emCustomType;
    //    private String emIsPrimary;
    //    private String emIsDefault;

    public String getEmId() {
        return emId;
    }

    public void setEmId(String emId) {
        this.emId = emId;
    }

    public String getEmEmailAddress() {
        return emEmailAddress;
    }

    public void setEmEmailAddress(String emEmailAddress) {
        this.emEmailAddress = emEmailAddress;
    }

    public String getEmEmailType() {
        return emEmailType;
    }

    public void setEmEmailType(String emEmailType) {
        this.emEmailType = emEmailType;
    }

    public String getEmEmailPrivacy() {
        return emEmailPrivacy;
    }

    public void setEmEmailPrivacy(String emEmailPrivacy) {
        this.emEmailPrivacy = emEmailPrivacy;
    }

    public String getEmIsVerified() {
        return emIsVerified;
    }

    public void setEmIsVerified(String emIsVerified) {
        this.emIsVerified = emIsVerified;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }

    public String getEmRecordIndexId() {
        return emRecordIndexId;
    }

    public void setEmRecordIndexId(String emRecordIndexId) {
        this.emRecordIndexId = emRecordIndexId;
    }

    public int getEmIsPrivate() {
        return emIsPrivate;
    }

    public void setEmIsPrivate(int emIsPrivate) {
        this.emIsPrivate = emIsPrivate;
    }
}
