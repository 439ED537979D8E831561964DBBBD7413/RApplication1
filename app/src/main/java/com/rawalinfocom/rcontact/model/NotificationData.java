package com.rawalinfocom.rcontact.model;

/**
 * Created by Maulik on 13/5/17.
 */

import com.fasterxml.jackson.annotation.JsonProperty;


public class NotificationData {
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("details")
    private String details;
    @JsonProperty("API")
    private String aPI;
    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("isNotification")
    private Integer isNotification;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("created_at")
    private String createdAt;
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getaPI() {
        return aPI;
    }

    public void setaPI(String aPI) {
        this.aPI = aPI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIsNotification() {
        return isNotification;
    }

    public void setIsNotification(Integer isNotification) {
        this.isNotification = isNotification;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}