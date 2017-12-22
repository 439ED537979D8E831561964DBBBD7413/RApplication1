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
    private String rawContactId;
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
    private String tempEmail;
    private String tempRcpName;
    private String tempRcpId;
    private String tempRcpImageURL;
    private boolean tempIsRcp;
    private String tempRawId;
    private String name;
    private String tempOrganisationName;
    private String tempOrganisationTitle;

    private ArrayList<ProfileDataOperationOrganization> pbOrganization;

    @JsonProperty("pb_local_phonebook_id")
    public String getLocalPhoneBookId() {
        return StringUtils.defaultString(this.localPhoneBookId);
    }

    public void setLocalPhoneBookId(String localPhoneBookId) {
        this.localPhoneBookId = localPhoneBookId;
    }

    @JsonIgnore
    public String getRawContactId() {
        return rawContactId;
    }

    public void setRawContactId(String rawContactId) {
        this.rawContactId = rawContactId;
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

    @JsonIgnore
    public boolean getTempIsRcp() {
        return tempIsRcp;
    }

    public void setTempIsRcp(boolean tempIsRcp) {
        this.tempIsRcp = tempIsRcp;
    }

    @JsonIgnore
    public String getTempRcpName() {
        return tempRcpName;
    }

    public void setTempRcpName(String tempRcpName) {
        this.tempRcpName = tempRcpName;
    }

    @JsonIgnore
    public String getTempRcpId() {
        return tempRcpId;
    }

    public void setTempRcpId(String tempRcpId) {
        this.tempRcpId = tempRcpId;
    }

    @JsonIgnore
    public String getTempEmail() {
        return tempEmail;
    }

    public void setTempEmail(String tempEmail) {
        this.tempEmail = tempEmail;
    }

    @JsonIgnore
    public String getTempRawId() {
        return tempRawId;
    }

    public void setTempRawId(String tempRawId) {
        this.tempRawId = tempRawId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getTempRcpImageURL() {
        return tempRcpImageURL;
    }

    public void setTempRcpImageURL(String tempRcpImageURL) {
        this.tempRcpImageURL = tempRcpImageURL;
    }

    public ArrayList<ProfileDataOperationOrganization> getPbOrganization() {
        return pbOrganization;
    }

    public void setPbOrganization(ArrayList<ProfileDataOperationOrganization> pbOrganization) {
        this.pbOrganization = pbOrganization;
    }

    public String getTempOrganisationName() {
        return tempOrganisationName;
    }

    public void setTempOrganisationName(String tempOrganisationName) {
        this.tempOrganisationName = tempOrganisationName;
    }

    public String getTempOrganisationTitle() {
        return tempOrganisationTitle;
    }

    public void setTempOrganisationTitle(String tempOrganisationTitle) {
        this.tempOrganisationTitle = tempOrganisationTitle;
    }
}
