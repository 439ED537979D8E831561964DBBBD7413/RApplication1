package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 15/11/16.
 */

public class MobileNumber {

    private String mnmId;
    private String mnmRecordIndexId;
    private String mnmMobileNumber;
    private String mnmNumberType;
    private String mnmIsPrimary;
    private int mnmIsPrivate;
    private String mnmNumberPrivacy;
    private String mnmMobileServiceProvider;
    private String mnmCircleOfService;
    private String mnmSpamCount;
    private String rcProfileMasterPmId;

    /*private String mnmCloudId;
    private String mnmCustomType;
    private String mnmIsDefault;
    private String mnmIsVerified;*/

    public String getMnmId() {
        return mnmId;
    }

    public void setMnmId(String mnmId) {
        this.mnmId = mnmId;
    }

    public String getMnmMobileNumber() {
        return mnmMobileNumber;
    }

    public void setMnmMobileNumber(String mnmMobileNumber) {
        this.mnmMobileNumber = mnmMobileNumber;
    }

    public String getMnmNumberType() {
        return mnmNumberType;
    }

    public void setMnmNumberType(String mnmNumberType) {
        this.mnmNumberType = mnmNumberType;
    }

    public String getMnmIsPrimary() {
        return mnmIsPrimary;
    }

    public void setMnmIsPrimary(String mnmIsPrimary) {
        this.mnmIsPrimary = mnmIsPrimary;
    }

    public String getMnmNumberPrivacy() {
        return mnmNumberPrivacy;
    }

    public void setMnmNumberPrivacy(String mnmNumberPrivacy) {
        this.mnmNumberPrivacy = mnmNumberPrivacy;
    }

    public String getMnmMobileServiceProvider() {
        return mnmMobileServiceProvider;
    }

    public void setMnmMobileServiceProvider(String mnmMobileServiceProvider) {
        this.mnmMobileServiceProvider = mnmMobileServiceProvider;
    }

    public String getMnmCircleOfService() {
        return mnmCircleOfService;
    }

    public void setMnmCircleOfService(String mnmCircleOfService) {
        this.mnmCircleOfService = mnmCircleOfService;
    }

    public String getMnmSpamCount() {
        return mnmSpamCount;
    }

    public void setMnmSpamCount(String mnmSpamCount) {
        this.mnmSpamCount = mnmSpamCount;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }

    public String getMnmRecordIndexId() {
        return mnmRecordIndexId;
    }

    public void setMnmRecordIndexId(String mnmRecordIndexId) {
        this.mnmRecordIndexId = mnmRecordIndexId;
    }

    public int getMnmIsPrivate() {
        return mnmIsPrivate;
    }

    public void setMnmIsPrivate(int mnmIsPrivate) {
        this.mnmIsPrivate = mnmIsPrivate;
    }
}
