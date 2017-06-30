package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 23/3/17.
 */

/*{

  "event_comment": {
    "id": "14902650648153",
    "from_pm_id": 17,
    "comment": "Happy Birthday",
    "reply": "",
    "date": "1988-03-22 00:00:00",
    "status": "1",
    "event_record_index_id": 5,
    "reply_at": "",
    "to_pm_id": 1,
    "type": "Birthday",
    "created_date": "2017-03-23 10:31:04",
    "updated_date": "2017-03-23 10:31:04"
  }
        "rating": [
        {
          "id": "14907701149044",
          "pr_id": "8",
          "from_pm_id": 17,
          "rating_stars": "1.0",
          "comment": "please update ur profile",
          "reply": "",
          "reply_at": "",
          "created_date": "2017-03-29 06:48:34",
          "updated_date": "2017-03-29 06:48:34"
        }
}*/

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventComment {
    @JsonProperty("date")
    private String date;

    @JsonProperty("event_record_index_id")
    private String eventRecordIndexId;

    @JsonProperty("reply_date")
    private String replyAt;

    @JsonProperty("to_pm_id")
    private Integer toPmId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("from_pm_id")
    private Integer fromPmId;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("rating_stars")
    private String ratingStars;

    @JsonProperty("id")
    private String id;

    @JsonProperty("pr_id")
    private String prId;

    @JsonProperty("created_date")
    private String createdDate;

    @JsonProperty("updated_date")
    private String updatedDate;

    @JsonProperty("reply")
    private String reply;

    @JsonProperty("status")
    private String status;

    public String getRatingStars() {
        return ratingStars;
    }

    public void setRatingStars(String ratingStars) {
        this.ratingStars = ratingStars;
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

    public String getEventRecordIndexId() {
        return eventRecordIndexId;
    }

    public void setEventRecordIndexId(String eventRecordIndexId) {
        this.eventRecordIndexId = eventRecordIndexId;
    }

    public String getReplyAt() {
        return replyAt;
    }

    public void setReplyAt(String replyAt) {
        this.replyAt = replyAt;
    }

    public Integer getToPmId() {
        return toPmId;
    }

    public void setToPmId(Integer toPmId) {
        this.toPmId = toPmId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getFromPmId() {
        return fromPmId;
    }

    public void setFromPmId(Integer fromPmId) {
        this.fromPmId = fromPmId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}