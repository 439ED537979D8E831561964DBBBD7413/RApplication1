package com.rawalinfocom.rcontact.model;

/**
 * Created by JacksonGenerator on 20/3/17.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventComment {
    @JsonProperty("date")
    private String date;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("from_pm_id")
    private int fromPmId;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("id")
    private String id;
    @JsonProperty("reply")
    private String reply;
    @JsonProperty("reply_at")
    private String replyAt;
    @JsonProperty("to_pm_id")
    private int toPmId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("status")
    private String status;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getFromPmId() {
        return fromPmId;
    }

    public void setFromPmId(int fromPmId) {
        this.fromPmId = fromPmId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public int getToPmId() {
        return toPmId;
    }

    public void setToPmId(int toPmId) {
        this.toPmId = toPmId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}