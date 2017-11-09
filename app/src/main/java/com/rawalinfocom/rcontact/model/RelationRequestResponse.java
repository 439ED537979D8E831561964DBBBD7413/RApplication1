package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by user on 29/11/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationRequestResponse {

    private Integer id;
    private Integer rrmToPmId;
    private Integer rrmFromPmId;
    private Integer rcStatus;
    private Integer rrmType;
    private Integer rcRelationMasterId;
    private Integer rcOrgId;
    private String createdAt;

    public Integer getRcOrgId() {
        return rcOrgId;
    }

    public void setRcOrgId(Integer rcOrgId) {
        this.rcOrgId = rcOrgId;
    }

    public Integer getRrmToPmId() {
        return rrmToPmId;
    }

    public void setRrmToPmId(Integer rrmToPmId) {
        this.rrmToPmId = rrmToPmId;
    }

    public Integer getRrmType() {
        return rrmType;
    }

    public void setRrmType(Integer rrmType) {
        this.rrmType = rrmType;
    }

    public Integer getRcRelationMasterId() {
        return rcRelationMasterId;
    }

    public void setRcRelationMasterId(Integer rcRelationMasterId) {
        this.rcRelationMasterId = rcRelationMasterId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRrmFromPmId() {
        return rrmFromPmId;
    }

    public void setRrmFromPmId(Integer rrmFromPmId) {
        this.rrmFromPmId = rrmFromPmId;
    }

    public Integer getRcStatus() {
        return rcStatus;
    }

    public void setRcStatus(Integer rcStatus) {
        this.rcStatus = rcStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
