package com.rawalinfocom.rcontact.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Aniruddh on 04/10/17.
 */

public class IndividualRelationType implements Serializable {

    private Integer relationType;
    private String relationId;
    private String id;
    private String relationName;
    private String organizationName;
    private String organizationId;
    private String FamilyName;
    private String relationDate;
    private boolean isFriendRelation;
    private String isVerify;
    private boolean isSelected;

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

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(String isVerify) {
        this.isVerify = isVerify;
    }

    public String getFamilyName() {
        return FamilyName;
    }

    public void setFamilyName(String FamilyName) {
        this.FamilyName = FamilyName;
    }

    public String getRelationDate() {
        return relationDate;
    }

    public void setRelationDate(String relationDate) {
        this.relationDate = relationDate;
    }

    public boolean getIsFriendRelation() {
        return isFriendRelation;
    }

    public void setIsFriendRelation(boolean isFriendRelation) {
        this.isFriendRelation = isFriendRelation;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFriendRelation() {
        return isFriendRelation;
    }

    public void setFriendRelation(boolean friendRelation) {
        isFriendRelation = friendRelation;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
