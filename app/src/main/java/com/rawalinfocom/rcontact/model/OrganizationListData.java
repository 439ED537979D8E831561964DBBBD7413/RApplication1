package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationListData implements Serializable {

    private String orgId;
    private String orgName;
    private String orgJobTitle;
    private String orgProfileImage;

    @JsonProperty("org_id")
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @JsonProperty("org_job_title")
    public String getOrgJobTitle() {
        return StringUtils.defaultString(this.orgJobTitle);
    }

    public void setOrgJobTitle(String orgJobTitle) {
        this.orgJobTitle = orgJobTitle;
    }

    @JsonProperty("org_name")
    public String getOrgName() {
        return StringUtils.defaultString(this.orgName);
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @JsonProperty("org_profile_image")
    public String getOrgProfileImage() {
        return orgProfileImage;
    }

    public void setOrgProfileImage(String orgProfileImage) {
        this.orgProfileImage = orgProfileImage;
    }
}
