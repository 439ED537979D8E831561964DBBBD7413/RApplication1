package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class ImAccount {

    private String imId;
    private String imRecordIndexId;
    private String imImDetail;
    private String imImProtocol;
    private String imImPrivacy;
    private int imIsPrivate;
    private String rcProfileMasterPmId;

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }

    public String getImImDetail() {
        return imImDetail;
    }

    public void setImImDetail(String imImDetail) {
        this.imImDetail = imImDetail;
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

    public String getImRecordIndexId() {
        return imRecordIndexId;
    }

    public void setImRecordIndexId(String imRecordIndexId) {
        this.imRecordIndexId = imRecordIndexId;
    }

    public int getImIsPrivate() {
        return imIsPrivate;
    }

    public void setImIsPrivate(int imIsPrivate) {
        this.imIsPrivate = imIsPrivate;
    }
}
