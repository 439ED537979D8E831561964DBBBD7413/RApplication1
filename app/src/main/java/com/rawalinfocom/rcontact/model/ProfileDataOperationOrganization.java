package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationOrganization implements Serializable {

    private String orgId;
    private String orgEntId;
    private String orgJobTitle;
    private String orgName;
    private String orgIndustryType;
    private Integer isCurrent;
    private Integer orgPublic;
    private Integer isVerify;
    private Integer isPrivate;
    private String orgFromDate;
    private String orgToDate;
    private String orgLogo;

    public String getIsInUse() {
        return isInUse;
    }

    public void setIsInUse(String isInUse) {
        this.isInUse = isInUse;
    }

    private String isInUse;

//    private String orgType;
//    private String orgJobDescription;
//    private String orgOfficeLocation;

    //    private Integer orgRcpType;
    private String orgRcpType;

    @JsonProperty("org_id")
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @JsonProperty("org_ent_id")
    public String getOrgEntId() {
        return orgEntId;
    }

    public void setOrgEntId(String orgEntId) {
        this.orgEntId = orgEntId;
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

    @JsonProperty("org_industry_type")
    public String getOrgIndustryType() {
        return StringUtils.defaultString(this.orgIndustryType);
    }

    public void setOrgIndustryType(String orgIndustryType) {
        this.orgIndustryType = orgIndustryType;
    }

    @JsonProperty("org_logo")
    public String getOrgLogo() {
        return StringUtils.defaultString(this.orgLogo);
    }

    public void setOrgLogo(String orgLogo) {
        this.orgLogo = orgLogo;
    }

    @JsonProperty("is_verified")
    public Integer getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(Integer isVerify) {
        this.isVerify = isVerify;
    }

    @JsonProperty("is_current")
    public Integer getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Integer isCurrent) {
        this.isCurrent = isCurrent;
    }

    @JsonProperty("org_public")
    public Integer getOrgPublic() {
        return orgPublic;
    }

    public void setOrgPublic(Integer orgPublic) {
        this.orgPublic = orgPublic;
    }

    public String getOrgRcpType() {
        return orgRcpType;
    }

    public void setOrgRcpType(String orgRcpType) {
        this.orgRcpType = orgRcpType;
    }

    @JsonProperty("org_from_date")
    public String getOrgFromDate() {
        return orgFromDate;
    }

    public void setOrgFromDate(String orgFromDate) {
        this.orgFromDate = orgFromDate;
    }

    @JsonProperty("org_to_date")
    public String getOrgToDate() {
        return orgToDate;
    }

    public void setOrgToDate(String orgToDate) {
        this.orgToDate = orgToDate;
    }

    @JsonProperty("is_private")
    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }
}
