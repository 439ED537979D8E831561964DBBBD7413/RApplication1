package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class ImAccount {

    private String imId;
    private String imImType;
    private String imCustomType;
    private String imImProtocol;
    private String imImPrivacy;
    private String rcProfileMasterPmId;

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }

    public String getImImType() {
        return imImType;
    }

    public void setImImType(String imImType) {
        this.imImType = imImType;
    }

    public String getImCustomType() {
        return imCustomType;
    }

    public void setImCustomType(String imCustomType) {
        this.imCustomType = imCustomType;
    }

    public String getImImProtocol() {
        return imImProtocol;
    }

    public void setImImProtocol(String imImProtocol) {
        this.imImProtocol = imImProtocol;
    }

    public String getImImPrivacy() {
        return imImPrivacy;
    }

    public void setImImPrivacy(String imImPrivacy) {
        this.imImPrivacy = imImPrivacy;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }
}
