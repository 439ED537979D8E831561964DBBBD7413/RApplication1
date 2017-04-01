package com.rawalinfocom.rcontact.model;

/**
 * Created by JacksonGenerator on 31/3/17.
 */

import com.fasterxml.jackson.annotation.JsonProperty;


public class RcontactUpdatesData {

    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("details")
    private String details;
    @JsonProperty("id")
    private String id;
    @JsonProperty("unm_status")
    private Integer unmStatus;
    @JsonProperty("title")
    private String title;
    @JsonProperty("unm_id")
    private Integer unmId;
    @JsonProperty("status")
    private Integer status;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDetails() {
        return details;
    }

    public String getId() {
        return id;
    }

    public Integer getUnmStatus() {
        return unmStatus;
    }

    public String getTitle() {
        return title;
    }

    public Integer getUnmId() {
        return unmId;
    }

    public Integer getStatus() {
        return status;
    }

}