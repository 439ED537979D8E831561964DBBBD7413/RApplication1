package com.rawalinfocom.rcontact.model;

/**
 * Created by JacksonGenerator on 23/3/17.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventCommentData {
    @JsonProperty("_id")
    private String id;

    @JsonProperty("rcp_pm_id")
    private Integer rcpPmId;

    @JsonProperty("birthday")
    private ArrayList<EventComment> birthday;

    @JsonProperty("custom")
    private ArrayList<EventComment> custom;

    @JsonProperty("anniversary")
    private ArrayList<EventComment> anniversary;

    public ArrayList<EventComment> getBirthday() {
        return birthday;
    }

    public void setBirthday(ArrayList<EventComment> birthday) {
        this.birthday = birthday;
    }

    public ArrayList<EventComment> getCustom() {
        return custom;
    }

    public void setCustom(ArrayList<EventComment> custom) {
        this.custom = custom;
    }

    public ArrayList<EventComment> getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(ArrayList<EventComment> anniversary) {
        this.anniversary = anniversary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRcpPmId() {
        return rcpPmId;
    }

    public void setRcpPmId(Integer rcpPmId) {
        this.rcpPmId = rcpPmId;
    }
}