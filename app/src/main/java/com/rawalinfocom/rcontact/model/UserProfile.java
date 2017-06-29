package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String pmAccessToken;
    private String pmNosqlMasterId;
    private String pmSignupSocialMediaType;
    private String pmJoiningDate;

    private String emailId;
    private String mobileNumber;
    private String profileRating;
    private String totalProfileRateUser;


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

    @JsonIgnore
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

    @JsonProperty("mobile_number")
    public String getMobileNumber() {
        return StringUtils.defaultString(mobileNumber);
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @JsonProperty("profile_image")
    public String getPmProfileImage() {
        return StringUtils.defaultString(pmProfileImage);
    }

    public void setPmProfileImage(String pmProfileImage) {
        this.pmProfileImage = pmProfileImage;
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

    public String getPmMiddleName() {
        return StringUtils.defaultString(pmMiddleName);
    }

    public void setPmMiddleName(String pmMiddleName) {
        this.pmMiddleName = pmMiddleName;
    }

    public String getPmRawId() {
        return StringUtils.defaultString(pmRawId);
    }

    public void setPmRawId(String pmRawId) {
        this.pmRawId = pmRawId;
    }

    public String getPmPrefix() {
        return StringUtils.defaultString(pmPrefix);
    }

    public void setPmPrefix(String pmPrefix) {
        this.pmPrefix = pmPrefix;
    }

    public String getPmSuffix() {
        return StringUtils.defaultString(pmSuffix);
    }

    public void setPmSuffix(String pmSuffix) {
        this.pmSuffix = pmSuffix;
    }

    public String getPmNickName() {
        return StringUtils.defaultString(pmNickName);
    }

    public void setPmNickName(String pmNickName) {
        this.pmNickName = pmNickName;
    }

    public String getPmPhoneticFirstName() {
        return StringUtils.defaultString(pmPhoneticFirstName);
    }

    public void setPmPhoneticFirstName(String pmPhoneticFirstName) {
        this.pmPhoneticFirstName = pmPhoneticFirstName;
    }

    public String getPmPhoneticMiddleName() {
        return StringUtils.defaultString(pmPhoneticMiddleName);
    }

    public void setPmPhoneticMiddleName(String pmPhoneticMiddleName) {
        this.pmPhoneticMiddleName = pmPhoneticMiddleName;
    }

    public String getPmPhoneticLastName() {
        return StringUtils.defaultString(pmPhoneticLastName);
    }

    public void setPmPhoneticLastName(String pmPhoneticLastName) {
        this.pmPhoneticLastName = pmPhoneticLastName;
    }

    public String getPmRcpId() {
        return StringUtils.defaultString(pmRcpId, "0");
    }

    public void setPmRcpId(String pmRcpId) {
        this.pmRcpId = pmRcpId;
    }

    public String getPmNickNamePrivacy() {
        return StringUtils.defaultString(pmNickNamePrivacy);
    }

    public void setPmNickNamePrivacy(String pmNickNamePrivacy) {
        this.pmNickNamePrivacy = pmNickNamePrivacy;
    }

    public String getPmNotes() {
        return StringUtils.defaultString(pmNotes);
    }

    public void setPmNotes(String pmNotes) {
        this.pmNotes = pmNotes;
    }

    public String getPmNotesPrivacy() {
        return StringUtils.defaultString(pmNotesPrivacy);
    }

    public void setPmNotesPrivacy(String pmNotesPrivacy) {
        this.pmNotesPrivacy = pmNotesPrivacy;
    }

    public String getPmGender() {
        return StringUtils.defaultString(pmGender);
    }

    public void setPmGender(String pmGender) {
        this.pmGender = pmGender;
    }

    public String getPmGenderPrivacy() {
        return StringUtils.defaultString(pmGenderPrivacy);
    }

    public void setPmGenderPrivacy(String pmGenderPrivacy) {
        this.pmGenderPrivacy = pmGenderPrivacy;
    }

    public String getPmIsFavourite() {
        return StringUtils.defaultString(pmIsFavourite);
    }

    public void setPmIsFavourite(String pmIsFavourite) {
        this.pmIsFavourite = pmIsFavourite;
    }

    public String getPmAccessToken() {
        return StringUtils.defaultString(pmAccessToken);
    }

    public void setPmAccessToken(String pmAccessToken) {
        this.pmAccessToken = pmAccessToken;
    }

    public String getPmNosqlMasterId() {
        return StringUtils.defaultString(pmNosqlMasterId);
    }

    public void setPmNosqlMasterId(String pmNosqlMasterId) {
        this.pmNosqlMasterId = pmNosqlMasterId;
    }

    public String getPmSignupSocialMediaType() {
        return StringUtils.defaultString(pmSignupSocialMediaType);
    }

    public void setPmSignupSocialMediaType(String pmSignupSocialMediaType) {
        this.pmSignupSocialMediaType = pmSignupSocialMediaType;
    }

    public String getPmJoiningDate() {
        return pmJoiningDate;
    }

    public void setPmJoiningDate(String pmJoiningDate) {
        this.pmJoiningDate = pmJoiningDate;
    }
}
