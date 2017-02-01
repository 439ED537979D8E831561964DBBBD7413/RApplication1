package com.rawalinfocom.rcontact.model;

/**
 * Created by JacksonGenerator on 16/1/17.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rating {

    @JsonProperty("pr_comment")
    private String prComment;
    @JsonProperty("pr_rating_stars")
    private String prRatingStars;
    @JsonProperty("pr_id")
    private Integer prId;
    @JsonProperty("pr_from_pm_id")
    private String prFromPmId;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("pr_to_pm_id")
    private Integer prToPmId;
    @JsonProperty("pr_status")
    private Integer prStatus;
    @JsonProperty("profile_rating")
    private String profileRating;
    @JsonProperty("total_profile_rate_user")
    private String totalProfileRateUser;


    public String getPrComment() {
        return prComment;
    }

    public void setPrComment(String prComment) {
        this.prComment = prComment;
    }

    public String getPrRatingStars() {
        return prRatingStars;
    }

    public void setPrRatingStars(String prRatingStars) {
        this.prRatingStars = prRatingStars;
    }

    public Integer getPrId() {
        return prId;
    }

    public void setPrId(Integer prId) {
        this.prId = prId;
    }

    public String getPrFromPmId() {
        return prFromPmId;
    }

    public void setPrFromPmId(String prFromPmId) {
        this.prFromPmId = prFromPmId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPrToPmId() {
        return prToPmId;
    }

    public void setPrToPmId(Integer prToPmId) {
        this.prToPmId = prToPmId;
    }

    public Integer getPrStatus() {
        return prStatus;
    }

    public void setPrStatus(Integer prStatus) {
        this.prStatus = prStatus;
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