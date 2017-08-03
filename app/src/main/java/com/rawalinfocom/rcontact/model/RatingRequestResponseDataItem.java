package com.rawalinfocom.rcontact.model;

/**
 * Created by JacksonGenerator on 16/1/17.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingRequestResponseDataItem {

    @JsonProperty("date")
    private String date;
    @JsonProperty("comment_id")
    private String commentId;
    @JsonProperty("pr_id")
    private String prId;
    @JsonProperty("from_pm_id")
    private Integer fromPmId;
    @JsonProperty("to_pm_id")
    private Integer toPmId;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("reply")
    private String reply;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("reply_at")
    private String replyAt;
    @JsonProperty("name")
    private String Name;
    @JsonProperty("event_record_index_id")
    private String eventRecordIndexId;
    @JsonProperty("pm_profile_photo")
    private String pmProfilePhoto;
    @JsonProperty("rating_stars")
    private String prRatingStars;
    @JsonProperty("profile_rating")
    private String profileRating;
    @JsonProperty("total_profile_rate_user")
    private String totalProfileRateUser;


    public String getPmProfilePhoto() {
        return pmProfilePhoto;
    }

    public void setPmProfilePhoto(String pmProfilePhoto) {
        this.pmProfilePhoto = pmProfilePhoto;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getEventRecordIndexId() {
        return eventRecordIndexId;
    }

    public void setEventRecordIndexId(String eventRecordIndexId) {
        this.eventRecordIndexId = eventRecordIndexId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplyAt() {
        return replyAt;
    }

    public void setReplyAt(String replyAt) {
        this.replyAt = replyAt;
    }

    public String getPrRatingStars() {
        return prRatingStars;
    }

    public void setPrRatingStars(String prRatingStars) {
        this.prRatingStars = prRatingStars;
    }

    public String getPrId() {
        return prId;
    }

    public void setPrId(String prId) {
        this.prId = prId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Integer getFromPmId() {
        return fromPmId;
    }

    public void setFromPmId(Integer fromPmId) {
        this.fromPmId = fromPmId;
    }

    public Integer getToPmId() {
        return toPmId;
    }

    public void setToPmId(Integer toPmId) {
        this.toPmId = toPmId;
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