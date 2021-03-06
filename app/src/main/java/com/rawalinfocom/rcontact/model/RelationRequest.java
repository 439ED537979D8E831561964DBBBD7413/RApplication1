package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by user on 29/11/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationRequest {

    private Integer id;
    private Integer rrmToPmId;
    private Integer rrmFromPmId;
    private Integer rcStatus;
    private Integer rrmType;
    private Integer rcRelationMasterId;
    private Integer gender;
    private Integer rcOrgId;
    private String omName;
    private String createdAt;
    private String rmParticular;
    private String orgName;

    @JsonProperty("rrm_to_pm_id")
    public Integer getRrmToPmId() {
        return rrmToPmId;
    }

    public void setRrmToPmId(Integer rrmToPmId) {
        this.rrmToPmId = rrmToPmId;
    }

    @JsonProperty("rrm_type")
    public Integer getRrmType() {
        return rrmType;
    }

    public void setRrmType(Integer rrmType) {
        this.rrmType = rrmType;
    }

    @JsonProperty("rc_relation_master_id")
    public Integer getRcRelationMasterId() {
        return rcRelationMasterId;
    }

    public void setRcRelationMasterId(Integer rcRelationMasterId) {
        this.rcRelationMasterId = rcRelationMasterId;
    }

    @JsonProperty("gender")
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    @JsonProperty("rc_org_id")
    public Integer getRcOrgId() {
        return rcOrgId;
    }

    public void setRcOrgId(Integer rcOrgId) {
        this.rcOrgId = rcOrgId;
    }

    @JsonProperty("om_name")
    public String getOmName() {
        return omName;
    }

    public void setOmName(String omName) {
        this.omName = omName;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("rrm_from_pm_id")
    public Integer getRrmFromPmId() {
        return rrmFromPmId;
    }

    public void setRrmFromPmId(Integer rrmFromPmId) {
        this.rrmFromPmId = rrmFromPmId;
    }

    @JsonProperty("rc_status")
    public Integer getRcStatus() {
        return rcStatus;
    }

    public void setRcStatus(Integer rcStatus) {
        this.rcStatus = rcStatus;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRmParticular() {
        return rmParticular;
    }

    public void setRmParticular(String rmParticular) {
        this.rmParticular = rmParticular;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
