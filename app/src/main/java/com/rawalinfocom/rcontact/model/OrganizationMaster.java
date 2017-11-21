package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by admin on 16/11/17.
 */

public class OrganizationMaster {

    private Integer id;
    private String orgName;

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

}
