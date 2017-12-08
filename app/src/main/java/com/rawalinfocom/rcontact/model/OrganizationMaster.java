package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by admin on 16/11/17.
 */

public class OrganizationMaster {

    private Integer id;
    private String orgName;
    private Integer omIsVerified;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("om_name")
    public String getRmParticular() {
        return orgName;
    }

    public void setRmParticular(String orgName) {
        this.orgName = orgName;
    }

    @JsonProperty("om_is_verified")
    public Integer getOmIsVerified() {
        return omIsVerified;
    }

    public void setOmIsVerified(Integer omIsVerified) {
        this.omIsVerified = omIsVerified;
    }
}
