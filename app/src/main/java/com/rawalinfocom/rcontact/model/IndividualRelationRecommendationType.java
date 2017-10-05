package com.rawalinfocom.rcontact.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Aniruddh on 04/10/17.
 */

public class IndividualRelationRecommendationType implements Serializable {

    String relationName;
    Integer relationType;
    String organization;

//    ArrayList<String> coWorkerList;

   /* public ArrayList<String> getCoWorkerList() {
        return coWorkerList;
    }

    public void setCoWorkerList(ArrayList<String> coWorkerList) {
        this.coWorkerList = coWorkerList;
    }*/

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public Integer getRelationType() {
        return relationType;
    }

    public void setRelationType(Integer relationType) {
        this.relationType = relationType;
    }
}
