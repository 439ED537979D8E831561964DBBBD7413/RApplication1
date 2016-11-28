package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 14/11/16.
 */

public class Email {

    private String emId;
    private String emEmailAddress;
    private String emEmailType;
    private String emCustomType;
    private String emIsPrimary;
    private String emEmailPrivacy;
    private String emIsDefault;
    private String emIsVerified;
    private String rcProfileMasterPmId;

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

    public String getEmCustomType() {
        return emCustomType;
    }

    public void setEmCustomType(String emCustomType) {
        this.emCustomType = emCustomType;
    }

    public String getEmIsPrimary() {
        return emIsPrimary;
    }

    public void setEmIsPrimary(String emIsPrimary) {
        this.emIsPrimary = emIsPrimary;
    }

    public String getEmEmailPrivacy() {
        return emEmailPrivacy;
    }

    public void setEmEmailPrivacy(String emEmailPrivacy) {
        this.emEmailPrivacy = emEmailPrivacy;
    }

    public String getEmIsDefault() {
        return emIsDefault;
    }

    public void setEmIsDefault(String emIsDefault) {
        this.emIsDefault = emIsDefault;
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
}
