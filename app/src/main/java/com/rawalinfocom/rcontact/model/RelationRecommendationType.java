package com.rawalinfocom.rcontact.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Aniruddh on 04/10/17.
 */

public class RelationRecommendationType implements Serializable {

    private String firstName;
    private String lastName;
    private String number;
    private String pmId;
    private String dateAndTime;
    private String profileImage;
    private String gender;

    private ArrayList<IndividualRelationType> individualRelationTypeList;
    private ArrayList<RelationRequest> relationRecommendations;

    public ArrayList<IndividualRelationType> getIndividualRelationTypeList() {
        return individualRelationTypeList;
    }

    public void setIndividualRelationTypeList(ArrayList<IndividualRelationType> individualRelationTypeList) {
        this.individualRelationTypeList = individualRelationTypeList;
    }

    public ArrayList<RelationRequest> getRelationRecommendations() {
        return relationRecommendations;
    }

    public void setRelationRecommendations(ArrayList<RelationRequest> relationRecommendations) {
        this.relationRecommendations = relationRecommendations;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
