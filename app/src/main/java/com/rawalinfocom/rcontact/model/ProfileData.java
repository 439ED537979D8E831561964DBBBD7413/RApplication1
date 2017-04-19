package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileData {

    private String localPhoneBookId;
    private String isFavourite;
    //    private String givenName;
    private ArrayList<ProfileDataOperation> operation;
    private ArrayList<String> rcpPmId;
    private String profileUrl;
    private String tempFirstName;
    private String tempLastName;
    private String tempSufix;
    private String tempPrefix;
    private String tempMiddleName;
    private String tempNumber;


    @JsonProperty("pb_local_phonebook_id")
    public String getLocalPhoneBookId() {
        return StringUtils.defaultString(this.localPhoneBookId);
    }

    public void setLocalPhoneBookId(String localPhoneBookId) {
        this.localPhoneBookId = localPhoneBookId;
    }

    @JsonProperty("operation")
    public ArrayList<ProfileDataOperation> getOperation() {
        return operation;
    }

    public void setOperation(ArrayList<ProfileDataOperation> operation) {
        this.operation = operation;
    }

    @JsonProperty("is_favourite")
    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }

    @JsonProperty("rcp_pm_id")
    public ArrayList<String> getRcpPmId() {
        return rcpPmId;
    }

    public void setRcpPmId(ArrayList<String> rcpPmId) {
        this.rcpPmId = rcpPmId;
    }

    /* public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }*/

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @JsonIgnore
    public String getTempFirstName() {
        return tempFirstName;
    }

    public void setTempFirstName(String tempFirstName) {
        this.tempFirstName = tempFirstName;
    }

    @JsonIgnore
    public String getTempLastName() {
        return tempLastName;
    }

    public void setTempLastName(String tempLastName) {
        this.tempLastName = tempLastName;
    }

    @JsonIgnore
    public String getTempNumber() {
        return tempNumber;
    }

    public void setTempNumber(String tempNumber) {
        this.tempNumber = tempNumber;
    }

    @JsonIgnore
    public String getTempSufix() {
        return tempSufix;
    }

    public void setTempSufix(String tempSufix) {
        this.tempSufix = tempSufix;
    }

    @JsonIgnore
    public String getTempPrefix() {
        return tempPrefix;
    }

    public void setTempPrefix(String tempPrefix) {
        this.tempPrefix = tempPrefix;
    }

    @JsonIgnore
    public String getTempMiddleName() {
        return tempMiddleName;
    }

    public void setTempMiddleName(String tempMiddleName) {
        this.tempMiddleName = tempMiddleName;
    }
}
