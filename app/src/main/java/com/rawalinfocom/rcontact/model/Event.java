package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class Event {

    private String evmId;
    private String evmRecordIndexId;
    private String evmStartDate;
    private String evmEventType;
    private String evmEventPrivacy;
    private Integer evmIsPrivate;
    private Integer evmIsYearHidden;
    private String rcProfileMasterPmId;

    //    private String evmCustomType;

    public String getEvmId() {
        return evmId;
    }

    public void setEvmId(String evmId) {
        this.evmId = evmId;
    }

    public String getEvmStartDate() {
        return evmStartDate;
    }

    public void setEvmStartDate(String evmStartDate) {
        this.evmStartDate = evmStartDate;
    }

    public String getEvmEventType() {
        return evmEventType;
    }

    public void setEvmEventType(String evmEventType) {
        this.evmEventType = evmEventType;
    }

    public String getEvmEventPrivacy() {
        return evmEventPrivacy;
    }

    public void setEvmEventPrivacy(String evmEventPrivacy) {
        this.evmEventPrivacy = evmEventPrivacy;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }

    public String getEvmRecordIndexId() {
        return evmRecordIndexId;
    }

    public void setEvmRecordIndexId(String evmRecordIndexId) {
        this.evmRecordIndexId = evmRecordIndexId;
    }

    public Integer getEvmIsYearHidden() {
        return evmIsYearHidden;
    }

    public void setEvmIsYearHidden(Integer evmIsYearHidden) {
        this.evmIsYearHidden = evmIsYearHidden;
    }

    public Integer getEvmIsPrivate() {
        return evmIsPrivate;
    }

    public void setEvmIsPrivate(Integer evmIsPrivate) {
        this.evmIsPrivate = evmIsPrivate;
    }
}
