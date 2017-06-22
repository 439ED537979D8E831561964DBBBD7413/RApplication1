package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Aniruddh on 14/06/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalSearchType implements Serializable {

    @JsonProperty("pb_rcp_verify")
    private int isRcpVerified;
    @JsonProperty("pb_name_first")
    private String firstName;
    @JsonProperty("pb_name_last")
    private String lastName;
    @JsonProperty("mobile_number")
    private String mobileNumber;
    @JsonProperty("pb_profile_photo")
    private String profileImageUrl;
    @JsonProperty("total_profile_rate_user")
    private String profileRatedCount;
    @JsonProperty("profile_rating")
    private String averageRating;
    @JsonProperty("public_url")
    private String publicProfileUrl;
    @JsonProperty("rcp_pm_id")
    private String rcpPmId;

    public int getIsRcpVerified() {
        return isRcpVerified;
    }

    public void setIsRcpVerified(int isRcpVerified) {
        this.isRcpVerified = isRcpVerified;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileRatedCount() {
        return profileRatedCount;
    }

    public void setProfileRatedCount(String profileRatedCount) {
        this.profileRatedCount = profileRatedCount;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getPublicProfileUrl() {
        return publicProfileUrl;
    }

    public void setPublicProfileUrl(String publicProfileUrl) {
        this.publicProfileUrl = publicProfileUrl;
    }

    public String getRcpPmId() {
        return rcpPmId;
    }

    public void setRcpPmId(String rcpPmId) {
        this.rcpPmId = rcpPmId;
    }
}
