package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class ImAccount {

    private String imId;
    private String imRecordIndexId;
    private String imImFirstName;
    private String imImLastName;
    private String imImProfileImage;
    private String imImDetail;
    private String imImProtocol;
    private String imImPrivacy;
    //    private Integer imIsPrivate;
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

    public String getImImFirstName() {
        return imImFirstName;
    }

    public void setImImFirstName(String imImFirstName) {
        this.imImFirstName = imImFirstName;
    }

    public String getImImLastName() {
        return imImLastName;
    }

    public void setImImLastName(String imImLastName) {
        this.imImLastName = imImLastName;
    }

    public String getImImProfileImage() {
        return imImProfileImage;
    }

    public void setImImProfileImage(String imImProfileImage) {
        this.imImProfileImage = imImProfileImage;
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

//    public Integer getImIsPrivate() {
//        return imIsPrivate;
//    }

//    public void setImIsPrivate(Integer imIsPrivate) {
//        this.imIsPrivate = imIsPrivate;
//    }
}
