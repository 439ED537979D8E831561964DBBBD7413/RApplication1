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

    @JsonProperty("receive_comment")
    private ArrayList<EventCommentData> eventReceiveCommentData;

    @JsonProperty("send_comment")
    private ArrayList<EventCommentData> eventSendCommentData;

    public ArrayList<EventCommentData> getEventReceiveCommentData() {
        return eventReceiveCommentData;
    }

    public void setEventReceiveCommentData(ArrayList<EventCommentData> eventReceiveCommentData) {
        this.eventReceiveCommentData = eventReceiveCommentData;
    }

    public ArrayList<EventCommentData> getEventSendCommentData() {
        return eventSendCommentData;
    }

    public void setEventSendCommentData(ArrayList<EventCommentData> eventSendCommentData) {
        this.eventSendCommentData = eventSendCommentData;
    }

    private String status;
    private String message;

    private OtpLog otpLog;
    private UserProfile userProfile;

    private ProfileDataOperation profileDetail;

    private ArrayList<Country> arrayListCountry;
    private ArrayList<ProfileDataOperation> arrayListUserRcProfile;
    private ArrayList<ProfileData> arrayListMapping;

    private String profileSharingData;

    @JsonProperty("profile_rating")
    private Rating profileRating;

    @JsonProperty("event_comment")
    private EventComment eventComment;

    public EventComment getEventComment() {
        return eventComment;
    }

    public void setEventComment(EventComment eventComment) {
        this.eventComment = eventComment;
    }

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
//    @JsonProperty("profile_details")
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

    @JsonProperty("profile_sharing_data")
    public String getProfileSharingData() {
        return profileSharingData;
    }

    public void setProfileSharingData(String profileSharingData) {
        this.profileSharingData = profileSharingData;
    }

    @JsonProperty("mapping")
    public ArrayList<ProfileData> getArrayListMapping() {
        return arrayListMapping;
    }

    public void setArrayListMapping(ArrayList<ProfileData> arrayListMapping) {
        this.arrayListMapping = arrayListMapping;
    }
}
