package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperation implements Serializable {

    private ArrayList<ProfileDataOperationPhoneNumber> pbPhoneNumber;
    private String flag;
    //    private ArrayList<String> pbWebAddress;
    private ArrayList<ProfileDataOperationWebAddress> pbWebAddress;
    private ArrayList<ProfileDataOperationEvent> pbEvent;
    private String pbNameSuffix;
    private ArrayList<ProfileDataOperationEmail> pbEmailId;
    private String pbNameFirst;
    private String pbNameMiddle;
    private ArrayList<ProfileDataOperationOrganization> pbOrganization;
    private String isFavourite;
    //    private ArrayList<ProfileDataOperationRelationship> pbRelationship;
    private String pbSource;
    private String pbNamePrefix;
    private String pbNameLast;
    private String pbPhoneticNameFirst;
    private String pbPhoneticNameLast;
    private ArrayList<ProfileDataOperationImAccount> pbIMAccounts;
    private String pbPhoneticNameMiddle;
    private ArrayList<ProfileDataOperationAddress> pbAddress;
    private String pbNote;
    private String pbNickname;

    private String createdAt;
    private String rcpPmId;
    private String updatedAt;
    private String noSqlMasterId;
    //    private String verifiedEmailAddress;
//    private String emCloudId;
    private ArrayList<ProfileDataOperationEmail> verifiedEmailIds;
    private String verifiedMobileNumber;
    private String mnmCloudId;

    private String joiningDate;
    private String pbRcpVerify;
    private String profileRating;
    private String totalProfileRateUser;
    private String pbGender;
    private String pbProfilePhoto;


    @JsonProperty("pb_phone_number")
    public ArrayList<ProfileDataOperationPhoneNumber> getPbPhoneNumber() {
        return pbPhoneNumber;
    }

    public void setPbPhoneNumber(ArrayList<ProfileDataOperationPhoneNumber> pbPhoneNumber) {
        this.pbPhoneNumber = pbPhoneNumber;
    }

    @JsonProperty("flag")
    public String getFlag() {
        return StringUtils.defaultString(this.flag);
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

   /* @JsonProperty("pb_web_address")
    public ArrayList<String> getPbWebAddress() {
        return pbWebAddress;
    }

    public void setPbWebAddress(ArrayList<String> pbWebAddress) {
        this.pbWebAddress = pbWebAddress;
    }*/

    @JsonProperty("pb_event")
    public ArrayList<ProfileDataOperationEvent> getPbEvent() {
        return pbEvent;
    }

    public void setPbEvent(ArrayList<ProfileDataOperationEvent> pbEvent) {
        this.pbEvent = pbEvent;
    }

    @JsonIgnore
    @JsonProperty("pb_name_suffix")
    public String getPbNameSuffix() {
        return StringUtils.defaultString(this.pbNameSuffix);
    }

    public void setPbNameSuffix(String pbNameSuffix) {
        this.pbNameSuffix = pbNameSuffix;
    }

    @JsonProperty("pb_email_id")
    public ArrayList<ProfileDataOperationEmail> getPbEmailId() {
        return pbEmailId;
    }

    public void setPbEmailId(ArrayList<ProfileDataOperationEmail> pbEmailId) {
        this.pbEmailId = pbEmailId;
    }

    @JsonProperty("pb_name_first")
    public String getPbNameFirst() {
        return StringUtils.defaultString(this.pbNameFirst);
    }

    public void setPbNameFirst(String pbNameFirst) {
        this.pbNameFirst = pbNameFirst;
    }

    @JsonProperty("pb_name_middle")
    public String getPbNameMiddle() {
        return StringUtils.defaultString(this.pbNameMiddle);
    }

    public void setPbNameMiddle(String pbNameMiddle) {
        this.pbNameMiddle = pbNameMiddle;
    }

    @JsonProperty("pb_organization")
    public ArrayList<ProfileDataOperationOrganization> getPbOrganization() {
        return pbOrganization;
    }

    public void setPbOrganization(ArrayList<ProfileDataOperationOrganization> pbOrganization) {
        this.pbOrganization = pbOrganization;
    }

    @JsonProperty("is_favourite")
    public String getIsFavourite() {
        return StringUtils.defaultString(this.isFavourite);
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }

   /* @JsonProperty("pb_relationship")
    public ArrayList<ProfileDataOperationRelationship> getPbRelationship() {
        return pbRelationship;
    }

    public void setPbRelationship(ArrayList<ProfileDataOperationRelationship> pbRelationship) {
        this.pbRelationship = pbRelationship;
    }*/

    @JsonProperty("pb_source")
    public String getPbSource() {
        return StringUtils.defaultString(this.pbSource);
    }

    public void setPbSource(String pbSource) {
        this.pbSource = pbSource;
    }

    @JsonProperty("pb_name_prefix")
    public String getPbNamePrefix() {
        return StringUtils.defaultString(this.pbNamePrefix);
    }

    public void setPbNamePrefix(String pbNamePrefix) {
        this.pbNamePrefix = pbNamePrefix;
    }

    @JsonProperty("pb_name_last")
    public String getPbNameLast() {
        return StringUtils.defaultString(this.pbNameLast);
    }

    public void setPbNameLast(String pbNameLast) {
        this.pbNameLast = pbNameLast;
    }

    @JsonProperty("pb_phonetic_name_first")
    public String getPbPhoneticNameFirst() {
        return StringUtils.defaultString(this.pbPhoneticNameFirst);
    }

    public void setPbPhoneticNameFirst(String pbPhoneticNameFirst) {
        this.pbPhoneticNameFirst = pbPhoneticNameFirst;
    }

    @JsonProperty("pb_phonetic_name_last")
    public String getPbPhoneticNameLast() {
        return StringUtils.defaultString(this.pbPhoneticNameLast);
    }

    public void setPbPhoneticNameLast(String pbPhoneticNameLast) {
        this.pbPhoneticNameLast = pbPhoneticNameLast;
    }

    @JsonProperty("pb_im_accounts")
    public ArrayList<ProfileDataOperationImAccount> getPbIMAccounts() {
        return pbIMAccounts;
    }

    public void setPbIMAccounts(ArrayList<ProfileDataOperationImAccount> pbIMAccounts) {
        this.pbIMAccounts = pbIMAccounts;
    }

    @JsonProperty("pb_phonetic_name_middle")
    public String getPbPhoneticNameMiddle() {
        return StringUtils.defaultString(this.pbPhoneticNameMiddle);
    }

    public void setPbPhoneticNameMiddle(String pbPhoneticNameMiddle) {
        this.pbPhoneticNameMiddle = pbPhoneticNameMiddle;
    }

    @JsonProperty("pb_address")
    public ArrayList<ProfileDataOperationAddress> getPbAddress() {
        return pbAddress;
    }

    public void setPbAddress(ArrayList<ProfileDataOperationAddress> pbAddress) {
        this.pbAddress = pbAddress;
    }

    @JsonProperty("pb_note")
    public String getPbNote() {
        return StringUtils.defaultString(this.pbNote);
    }

    public void setPbNote(String pbNote) {
        this.pbNote = pbNote;
    }

    @JsonProperty("pb_nickname")
    public String getPbNickname() {
        return StringUtils.defaultString(this.pbNickname);
    }

    public void setPbNickname(String pbNickname) {
        this.pbNickname = pbNickname;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return StringUtils.defaultString(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("rcp_pm_id")
    public String getRcpPmId() {
        return StringUtils.defaultString(rcpPmId);
    }

    public void setRcpPmId(String rcpPmId) {
        this.rcpPmId = rcpPmId;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return StringUtils.defaultString(updatedAt);
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("_id")
    public String getNoSqlMasterId() {
        return StringUtils.defaultString(noSqlMasterId);
    }

    public void setNoSqlMasterId(String noSqlMasterId) {
        this.noSqlMasterId = noSqlMasterId;
    }

    /*@JsonProperty("email_address")
    public String getVerifiedEmailAddress() {
        return StringUtils.defaultString(verifiedEmailAddress);
    }

    public void setVerifiedEmailAddress(String verifiedEmailAddress) {
        this.verifiedEmailAddress = verifiedEmailAddress;
    }

    @JsonProperty("em_id")
    public String getEmCloudId() {
        return StringUtils.defaultString(emCloudId);
    }

    public void setEmCloudId(String emCloudId) {
        this.emCloudId = emCloudId;
    }*/

    @JsonProperty("mobile_number")
    public String getVerifiedMobileNumber() {
        return StringUtils.defaultString(verifiedMobileNumber);
    }

    public void setVerifiedMobileNumber(String verifiedMobileNumber) {
        this.verifiedMobileNumber = verifiedMobileNumber;
    }

    @JsonProperty("mnm_id")
    public String getMnmCloudId() {
        return StringUtils.defaultString(mnmCloudId);
    }

    public void setMnmCloudId(String mnmCloudId) {
        this.mnmCloudId = mnmCloudId;
    }

    @JsonProperty("joining_date")
    public String getJoiningDate() {
        return StringUtils.defaultString(joiningDate);
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    @JsonProperty("pb_rcp_verify")
    public String getPbRcpVerify() {
        return StringUtils.defaultString(pbRcpVerify);
    }

    public void setPbRcpVerify(String pbRcpVerify) {
        this.pbRcpVerify = pbRcpVerify;
    }

    @JsonProperty("profile_rating")
    public String getProfileRating() {
        return StringUtils.defaultString(profileRating, "0");
    }

    public void setProfileRating(String profileRating) {
        this.profileRating = profileRating;
    }

    @JsonProperty("total_profile_rate_user")
    public String getTotalProfileRateUser() {
        return StringUtils.defaultString(totalProfileRateUser, "0");
    }

    public void setTotalProfileRateUser(String totalProfileRateUser) {
        this.totalProfileRateUser = totalProfileRateUser;
    }

    @JsonProperty("emails")
    public ArrayList<ProfileDataOperationEmail> getVerifiedEmailIds() {
        return verifiedEmailIds;
    }

    public void setVerifiedEmailIds(ArrayList<ProfileDataOperationEmail> verifiedEmailIds) {
        this.verifiedEmailIds = verifiedEmailIds;
    }

    @JsonProperty("pb_web_address")
    public ArrayList<ProfileDataOperationWebAddress> getPbWebAddress() {
        return pbWebAddress;
    }

    public void setPbWebAddress(ArrayList<ProfileDataOperationWebAddress> pbWebAddress) {
        this.pbWebAddress = pbWebAddress;
    }

    @JsonProperty("pb_gender")
    public String getPbGender() {
        return StringUtils.defaultString(pbGender);
    }

    public void setPbGender(String pbGender) {
        this.pbGender = pbGender;
    }

    @JsonProperty("pb_profile_photo")
    public String getPbProfilePhoto() {
        return pbProfilePhoto;
    }

    public void setPbProfilePhoto(String pbProfilePhoto) {
        this.pbProfilePhoto = pbProfilePhoto;
    }
}
