package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class Relation {

    private String rmId;
    private String rmRelationName;
    private String rmRelationType;
    private String rmCustomType;
    private String rmRelatedPmId;
    private String rmIsValid;
    private String rmRelationPrivacy;
    private String rcProfileMasterPmId;

    public String getRmId() {
        return rmId;
    }

    public void setRmId(String rmId) {
        this.rmId = rmId;
    }

    public String getRmRelationName() {
        return rmRelationName;
    }

    public void setRmRelationName(String rmRelationName) {
        this.rmRelationName = rmRelationName;
    }

    public String getRmRelationType() {
        return rmRelationType;
    }

    public void setRmRelationType(String rmRelationType) {
        this.rmRelationType = rmRelationType;
    }

    public String getRmCustomType() {
        return rmCustomType;
    }

    public void setRmCustomType(String rmCustomType) {
        this.rmCustomType = rmCustomType;
    }

    public String getRmRelatedPmId() {
        return rmRelatedPmId;
    }

    public void setRmRelatedPmId(String rmRelatedPmId) {
        this.rmRelatedPmId = rmRelatedPmId;
    }

    public String getRmIsValid() {
        return rmIsValid;
    }

    public void setRmIsValid(String rmIsValid) {
        this.rmIsValid = rmIsValid;
    }

    public String getRmRelationPrivacy() {
        return rmRelationPrivacy;
    }

    public void setRmRelationPrivacy(String rmRelationPrivacy) {
        this.rmRelationPrivacy = rmRelationPrivacy;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }
}
