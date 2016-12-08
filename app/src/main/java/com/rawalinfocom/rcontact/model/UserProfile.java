package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by Monal on 08/11/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile implements Serializable {

    private String pmId;
    private String pmFirstName;
    private String pmMiddleName;
    private String pmLastName;
    private String isAlreadyVerified;
    private String pmRawId;
    private String pmPrefix;
    private String pmSuffix;
    private String pmNickName;
    private String pmPhoneticFirstName;
    private String pmPhoneticMiddleName;
    private String pmPhoneticLastName;
    private String pmProfileImage;
    private String pmRcpId;
    private String pmNickNamePrivacy;
    private String pmNotes;
    private String pmNotesPrivacy;
    private String pmGender;
    private String pmGenderPrivacy;
    private String pmIsFavourite;
    private String pmIsFavouritePrivacy;
    private String pmAccessToken;
    private String pmNosqlMasterId;
    private String pmSignupSocialMediaType;

    private String emailId;
    private String mobileNumber;


    @JsonProperty("first_name")
    public String getPmFirstName() {
        return StringUtils.defaultString(pmFirstName);
    }

    public void setPmFirstName(String pmFirstName) {
        this.pmFirstName = pmFirstName;
    }

    @JsonProperty("last_name")
    public String getPmLastName() {
        return StringUtils.defaultString(pmLastName);
    }

    public void setPmLastName(String pmLastName) {
        this.pmLastName = pmLastName;
    }

    @JsonProperty("email_id")
    public String getEmailId() {
        return StringUtils.defaultString(emailId);
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @JsonProperty("pm_id")
    public String getPmId() {
        return StringUtils.defaultString(pmId);
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    @JsonProperty("is_already_verified")
    public String getIsAlreadyVerified() {
        return StringUtils.defaultString(isAlreadyVerified);
    }

    public void setIsAlreadyVerified(String isAlreadyVerified) {
        this.isAlreadyVerified = isAlreadyVerified;
    }

    public String getPmMiddleName() {
        return pmMiddleName;
    }

    public void setPmMiddleName(String pmMiddleName) {
        this.pmMiddleName = pmMiddleName;
    }

    public String getPmRawId() {
        return pmRawId;
    }

    public void setPmRawId(String pmRawId) {
        this.pmRawId = pmRawId;
    }

    public String getPmPrefix() {
        return pmPrefix;
    }

    public void setPmPrefix(String pmPrefix) {
        this.pmPrefix = pmPrefix;
    }

    public String getPmSuffix() {
        return pmSuffix;
    }

    public void setPmSuffix(String pmSuffix) {
        this.pmSuffix = pmSuffix;
    }

    public String getPmNickName() {
        return pmNickName;
    }

    public void setPmNickName(String pmNickName) {
        this.pmNickName = pmNickName;
    }

    public String getPmPhoneticFirstName() {
        return pmPhoneticFirstName;
    }

    public void setPmPhoneticFirstName(String pmPhoneticFirstName) {
        this.pmPhoneticFirstName = pmPhoneticFirstName;
    }

    public String getPmPhoneticMiddleName() {
        return pmPhoneticMiddleName;
    }

    public void setPmPhoneticMiddleName(String pmPhoneticMiddleName) {
        this.pmPhoneticMiddleName = pmPhoneticMiddleName;
    }

    public String getPmPhoneticLastName() {
        return pmPhoneticLastName;
    }

    public void setPmPhoneticLastName(String pmPhoneticLastName) {
        this.pmPhoneticLastName = pmPhoneticLastName;
    }

    public String getPmProfileImage() {
        return pmProfileImage;
    }

    public void setPmProfileImage(String pmProfileImage) {
        this.pmProfileImage = pmProfileImage;
    }

    public String getPmRcpId() {
        return StringUtils.defaultString(pmRcpId, "0");
    }

    public void setPmRcpId(String pmRcpId) {
        this.pmRcpId = pmRcpId;
    }

    public String getPmNickNamePrivacy() {
        return pmNickNamePrivacy;
    }

    public void setPmNickNamePrivacy(String pmNickNamePrivacy) {
        this.pmNickNamePrivacy = pmNickNamePrivacy;
    }

    public String getPmNotes() {
        return pmNotes;
    }

    public void setPmNotes(String pmNotes) {
        this.pmNotes = pmNotes;
    }

    public String getPmNotesPrivacy() {
        return pmNotesPrivacy;
    }

    public void setPmNotesPrivacy(String pmNotesPrivacy) {
        this.pmNotesPrivacy = pmNotesPrivacy;
    }

    public String getPmGender() {
        return pmGender;
    }

    public void setPmGender(String pmGender) {
        this.pmGender = pmGender;
    }

    public String getPmGenderPrivacy() {
        return pmGenderPrivacy;
    }

    public void setPmGenderPrivacy(String pmGenderPrivacy) {
        this.pmGenderPrivacy = pmGenderPrivacy;
    }

    public String getPmIsFavourite() {
        return pmIsFavourite;
    }

    public void setPmIsFavourite(String pmIsFavourite) {
        this.pmIsFavourite = pmIsFavourite;
    }

    public String getPmIsFavouritePrivacy() {
        return pmIsFavouritePrivacy;
    }

    public void setPmIsFavouritePrivacy(String pmIsFavouritePrivacy) {
        this.pmIsFavouritePrivacy = pmIsFavouritePrivacy;
    }

    public String getPmAccessToken() {
        return pmAccessToken;
    }

    public void setPmAccessToken(String pmAccessToken) {
        this.pmAccessToken = pmAccessToken;
    }

    public String getPmNosqlMasterId() {
        return pmNosqlMasterId;
    }

    public void setPmNosqlMasterId(String pmNosqlMasterId) {
        this.pmNosqlMasterId = pmNosqlMasterId;
    }

    public String getPmSignupSocialMediaType() {
        return pmSignupSocialMediaType;
    }

    public void setPmSignupSocialMediaType(String pmSignupSocialMediaType) {
        this.pmSignupSocialMediaType = pmSignupSocialMediaType;
    }

    public String getMobileNumber() {
        return StringUtils.defaultString(mobileNumber);
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
