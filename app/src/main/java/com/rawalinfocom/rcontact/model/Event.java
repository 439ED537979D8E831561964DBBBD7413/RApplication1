package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class Event {

    private String evmId;
    private String evmStartDate;
    private String evmEventType;
    private String evmCustomType;
    private String evmEventPrivacy;
    private String rcProfileMasterPmId;

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

    public String getEvmCustomType() {
        return evmCustomType;
    }

    public void setEvmCustomType(String evmCustomType) {
        this.evmCustomType = evmCustomType;
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
}
