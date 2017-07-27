package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Aniruddh on 26/07/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpamDataType implements Serializable {

    @JsonProperty("pb_name_last")
    private String lastName;
    @JsonProperty("pb_name_suffix")
    private String suffix;
    @JsonProperty("pb_name_first")
    private String firstName;
    @JsonProperty("pb_rcp_verify")
    private String rcpVerfiy;
    @JsonProperty("pb_name_middle")
    private String middleName;
    @JsonProperty("rcp_pm_id")
    private String rcpPmId;
    @JsonProperty("mobile_number")
    private String mobileNumber;
    @JsonProperty("pb_name_prefix")
    private String prefix;
    @JsonProperty("spam_count")
    private String spamCount;
    @JsonProperty("profile_rating")
    private String profileRating;
    @JsonProperty("total_profile_rate_user")
    private String totalProfileRateUser;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getRcpVerfiy() {
        return rcpVerfiy;
    }

    public void setRcpVerfiy(String rcpVerfiy) {
        this.rcpVerfiy = rcpVerfiy;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getRcpPmId() {
        return rcpPmId;
    }

    public void setRcpPmId(String rcpPmId) {
        this.rcpPmId = rcpPmId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSpamCount() {
        return spamCount;
    }

    public void setSpamCount(String spamCount) {
        this.spamCount = spamCount;
    }

    public String getProfileRating() {
        return profileRating;
    }

    public void setProfileRating(String profileRating) {
        this.profileRating = profileRating;
    }

    public String getTotalProfileRateUser() {
        return totalProfileRateUser;
    }

    public void setTotalProfileRateUser(String totalProfileRateUser) {
        this.totalProfileRateUser = totalProfileRateUser;
    }
}
