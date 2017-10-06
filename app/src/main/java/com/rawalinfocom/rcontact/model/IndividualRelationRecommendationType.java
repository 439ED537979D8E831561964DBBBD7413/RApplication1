package com.rawalinfocom.rcontact.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Aniruddh on 04/10/17.
 */

public class IndividualRelationRecommendationType implements Serializable {

    private Integer relationType;

    private String relationName;
    private String organizationName;
    private String FamilyName;
    private boolean isFriendRelation;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Integer getRelationType() {
        return relationType;
    }

    public void setRelationType(Integer relationType) {
        this.relationType = relationType;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getFamilyName() {
        return FamilyName;
    }

    public void setFamilyName(String FamilyName) {
        this.FamilyName = FamilyName;
    }


    public boolean getIsFriendRelation() {
        return isFriendRelation;
    }

    public void setIsFriendRelation(boolean isFriendRelation) {
        this.isFriendRelation = isFriendRelation;
    }
}
