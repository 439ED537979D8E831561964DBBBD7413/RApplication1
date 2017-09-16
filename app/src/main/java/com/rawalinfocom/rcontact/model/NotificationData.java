package com.rawalinfocom.rcontact.model;

/**
 * Created by Maulik on 13/5/17.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationData {

    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("type_text")
    private String type_text;
    @JsonProperty("url")
    private String url;
    @JsonProperty("title")
    private String title;
    @JsonProperty("details")
    private String details;
    @JsonProperty("API")
    private String aPI;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;

//    @JsonProperty("subject")
//    private String subject;
//    @JsonProperty("isNotification")
//    private Integer isNotification;

//    public String getSubject() {
//        return subject;
//    }

//    public void setSubject(String subject) {
//        this.subject = subject;
//    }

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

    public String getTypeText() {
        return type_text;
    }

    public String getType() {
        return type;
    }

    public void setTypeText(String type_text) {
        this.type_text = type_text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public Integer getIsNotification() {
//        return isNotification;
//    }

//    public void setIsNotification(Integer isNotification) {
//        this.isNotification = isNotification;
//    }

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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}