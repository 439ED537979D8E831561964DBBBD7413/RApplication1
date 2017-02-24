package com.rawalinfocom.rcontact.model;

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
}
