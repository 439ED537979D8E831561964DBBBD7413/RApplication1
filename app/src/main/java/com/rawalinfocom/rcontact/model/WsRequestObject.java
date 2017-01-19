package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Monal on 20/10/16.
 * <p>
 * Class containing all the Response Objects
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WsRequestObject {

    private String countryCode;
    private String mobileNumber;
    private String pmId;
    private String cmId;
    private String otp;
    private String otpGenerationTime;

    private String status;
    private String ldOtpDeliveredTimeFromCloudToDevice;
    private String accessToken;

    private String firstName;
    private String lastName;
    private String emailId;
    private String type;
    private String deviceId;
    private String socialMediaTokenId;

    private String profileImage;

    private ArrayList<ProfileData> profileData;
    private ArrayList<ProfileData> favourites;

    @JsonProperty("submit_rating")
    private ArrayList<Rating> ratings;


    @JsonProperty("country_code")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("mobile_number")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @JsonProperty("pm_id")
    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    @JsonProperty("otp")
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @JsonProperty("otp_generation_time")
    public String getOtpGenerationTime() {
        return otpGenerationTime;
    }

    public void setOtpGenerationTime(String otpGenerationTime) {
        this.otpGenerationTime = otpGenerationTime;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("ld_otp_delivered_time_from_cloud_to_device")
    public String getLdOtpDeliveredTimeFromCloudToDevice() {
        return ldOtpDeliveredTimeFromCloudToDevice;
    }

    public void setLdOtpDeliveredTimeFromCloudToDevice(String ldOtpDeliveredTimeFromCloudToDevice) {
        this.ldOtpDeliveredTimeFromCloudToDevice = ldOtpDeliveredTimeFromCloudToDevice;
    }

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("email_id")
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JsonProperty("social_media_token_id")
    public String getSocialMediaTokenId() {
        return socialMediaTokenId;
    }

    public void setSocialMediaTokenId(String socialMediaTokenId) {
        this.socialMediaTokenId = socialMediaTokenId;
    }

    @JsonProperty("profile_image")
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @JsonProperty("data")
    public ArrayList<ProfileData> getProfileData() {
        return profileData;
    }

    public void setProfileData(ArrayList<ProfileData> profileData) {
        this.profileData = profileData;
    }

    @JsonProperty("cm_id")
    public String getCmId() {
        return cmId;
    }

    public void setCmId(String cmId) {
        this.cmId = cmId;
    }

    @JsonProperty("favourites")
    public ArrayList<ProfileData> getFavourites() {
        return favourites;
    }

    public void setFavourites(ArrayList<ProfileData> favourites) {
        this.favourites = favourites;
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(ArrayList<Rating> ratings) {
        this.ratings = ratings;
    }
}
