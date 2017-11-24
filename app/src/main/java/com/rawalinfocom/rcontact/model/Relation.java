package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class Relation {

    private int rmId;
    private String rmRelationName;
    private String rmRelationType;
    private String rcProfileMasterPmId;

    public int getRmId() {
        return rmId;
    }

    public void setRmId(int rmId) {
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

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }
}
