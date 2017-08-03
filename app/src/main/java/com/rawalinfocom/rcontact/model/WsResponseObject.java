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

    @JsonProperty("contact_request")
    private PrivacyRequestDataItem contactRequestData;
    private String spamCount;

    public PrivacyRequestDataItem getContactRequestData() {
        return contactRequestData;
    }

    public void setContactRequestData(PrivacyRequestDataItem contactRequestData) {
        this.contactRequestData = contactRequestData;
    }

    @JsonProperty("privacy_request_data")
    private ArrayList<PrivacyRequestDataItem> privacyRequestData;

    @JsonProperty("rcontact_update")
    private ArrayList<RcontactUpdatesData> rcontactUpdatesData;

    @JsonProperty("receive_comment")
    private ArrayList<EventCommentData> eventReceiveCommentData;

    @JsonProperty("send_comment")
    private ArrayList<EventCommentData> eventSendCommentData;

    private String status;
    private String message;
    private Integer flag;
    private Integer reSync;
    private String callDateAndTime;
    private String smsLogTimestamp;
    private String timestamp;
    private String callLogRowId;
    private String responseKey;

    private OtpLog otpLog;
    private UserProfile userProfile;

    private ProfileDataOperation profileDetail;

    private ArrayList<Country> arrayListCountry;
    private ArrayList<ProfileDataOperation> arrayListUserRcProfile;
    private ArrayList<ProfileData> arrayListMapping;

    private ArrayList<CallLogType> arrayListCallLogHistory;
    private ArrayList<GlobalSearchType> globalSearchTypeArrayList;
    private ArrayList<SpamDataType> spamDataTypeArrayList;

    private String profileSharingData;

    public ArrayList<PrivacyRequestDataItem> getPrivacyRequestData() {
        return privacyRequestData;
    }

    public ArrayList<RcontactUpdatesData> getRcontactUpdate() {
        return rcontactUpdatesData;
    }

    // <editor-fold desc="Pull Mechanism service">
    // contact
    @JsonProperty("request_data")
    private ArrayList<ContactRequestResponseDataItem> requestData;

    public ArrayList<ContactRequestResponseDataItem> getRequestData() {
        return requestData;
    }

    public void setRequestData(ArrayList<ContactRequestResponseDataItem> requestData) {
        this.requestData = requestData;
    }

    @JsonProperty("response_data")
    private ArrayList<ContactRequestResponseDataItem> responseData;

    public ArrayList<ContactRequestResponseDataItem> getResponseData() {
        return responseData;
    }

    public void setResponseData(ArrayList<ContactRequestResponseDataItem> responseData) {
        this.responseData = responseData;
    }

    @JsonProperty("rating_receive")
    private ArrayList<RatingRequestResponseDataItem> ratingReceive;

    public ArrayList<RatingRequestResponseDataItem> getRatingReceive() {
        return ratingReceive;
    }

    public void setRatingReceive(ArrayList<RatingRequestResponseDataItem> ratingReceive) {
        this.ratingReceive = ratingReceive;
    }

    @JsonProperty("rating_done")
    private ArrayList<RatingRequestResponseDataItem> ratingDone;

    public ArrayList<RatingRequestResponseDataItem> getRatingDone() {
        return ratingDone;
    }

    public void setRatingDone(ArrayList<RatingRequestResponseDataItem> ratingDone) {
        this.ratingDone = ratingDone;
    }

    @JsonProperty("comment_receive")
    private ArrayList<RatingRequestResponseDataItem> commentReceive;

    public ArrayList<RatingRequestResponseDataItem> getCommentReceive() {
        return commentReceive;
    }

    public void setCommentReceive(ArrayList<RatingRequestResponseDataItem> commentReceive) {
        this.commentReceive = commentReceive;
    }

    @JsonProperty("comment_done")
    private ArrayList<RatingRequestResponseDataItem> commentDone;

    public ArrayList<RatingRequestResponseDataItem> getCommentDone() {
        return commentDone;
    }

    public void setCommentDone(ArrayList<RatingRequestResponseDataItem> commentDone) {
        this.commentDone = commentDone;
    }

    // end call

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

    @JsonProperty("sms_date")
    public String getSmsLogTimestamp() {
        return smsLogTimestamp;
    }

    public void setSmsLogTimestamp(String smsLogTimestamp) {
        this.smsLogTimestamp = smsLogTimestamp;
    }

    @JsonProperty("call_date_and_time")
    public String getCallDateAndTime() {
        return StringUtils.defaultString(callDateAndTime);
    }

    public void setCallDateAndTime(String callDateAndTime) {
        this.callDateAndTime = callDateAndTime;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return StringUtils.defaultString(timestamp);
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("call_log_row_id")
    public String getCallLogRowId() {
        return StringUtils.defaultString(callLogRowId);
    }

    public void setCallLogRowId(String callLogRowId) {
        this.callLogRowId = callLogRowId;
    }

    @JsonProperty("message")
    public String getMessage() {
        return StringUtils.defaultString(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("resync")
    public Integer getReSync() {
        return reSync;
    }

    public void setReSync(int reSync) {
        this.reSync = reSync;
    }


    @JsonProperty("flag")
    public Integer getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
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

    @JsonProperty("call_history")
    public ArrayList<CallLogType> getArrayListCallLogHistory() {
        return arrayListCallLogHistory;
    }

    @JsonProperty("result")
    public ArrayList<GlobalSearchType> getGlobalSearchTypeArrayList() {
        return globalSearchTypeArrayList;
    }

    @JsonProperty("spam_details")
    public ArrayList<SpamDataType> getSpamDataTypeArrayList() {
        return spamDataTypeArrayList;
    }

    @JsonProperty("spam_count")
    public String getSpamCount() {
        return spamCount;
    }

    @JsonProperty("response_key")
    public String getResponseKey() {
        return responseKey;
    }

    public void setResponseKey(String responseKey) {
        this.responseKey = responseKey;
    }
}
