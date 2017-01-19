package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by Monal on 20/10/16.
 * <p>
 * Class containing all the Response Objects
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WsResponseObject {

    private String status;
    private String message;

    private OtpLog otpLog;
    private UserProfile userProfile;

    private ProfileDataOperation profileDetail;

    private ArrayList<Country> arrayListCountry;
    private ArrayList<ProfileDataOperation> arrayListUserRcProfile;

    @JsonProperty("profile_rating")
    private Rating profileRating;

    @JsonProperty("status")
    public String getStatus() {
        return StringUtils.defaultString(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("message")
    public String getMessage() {
        return StringUtils.defaultString(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("country_list")
    public ArrayList<Country> getArrayListCountry() {
        return arrayListCountry;
    }

    public void setArrayListCountry(ArrayList<Country> arrayListCountry) {
        this.arrayListCountry = arrayListCountry;
    }

    @JsonProperty("otp_detail")
    public OtpLog getOtpLog() {
        return otpLog;
    }

    public void setOtpLog(OtpLog otpLog) {
        this.otpLog = otpLog;
    }

    @JsonProperty("profile_data")
    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @JsonProperty("rcp_profiles")
    public ArrayList<ProfileDataOperation> getArrayListUserRcProfile() {
        return arrayListUserRcProfile;
    }

    public void setArrayListUserRcProfile(ArrayList<ProfileDataOperation> arrayListUserRcProfile) {
        this.arrayListUserRcProfile = arrayListUserRcProfile;
    }

    @JsonProperty("profile_details")
    public ProfileDataOperation getProfileDetail() {
        return profileDetail;
    }

    public void setProfileDetail(ProfileDataOperation profileDetail) {
        this.profileDetail = profileDetail;
    }

    public Rating getProfileRating() {
        return profileRating;
    }

    public void setProfileRating(Rating profileRating) {
        this.profileRating = profileRating;
    }
}
