package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class Organization {

    private String omId;
    private String omEnterpriseOrgId;
    private String omRecordIndexId;
    private String omOrganizationCompany;
    private String omOrganizationDesignation;
    private String omIsCurrent;
    private Integer omIsPrivate;
    private String omOrganizationPrivacy;
    private String rcProfileMasterPmId;
    private String omOrganizationFromDate;
    private String omOrganizationToDate;
    private String omOrganizationType;
    private String omOrganizationLogo;
    private String omIsVerified;
    private String orgUrlSlug;

    /*private String omCustomType;
    private String omOrganizationTitle;
    private String omJobDescription;
    private String omOfficeLocation;*/

    public String getOmId() {
        return omId;
    }

    public void setOmId(String omId) {
        this.omId = omId;
    }

    public String getOmEnterpriseOrgId() {
        return omEnterpriseOrgId;
    }

    public void setOmEnterpriseOrgId(String omEnterpriseOrgId) {
        this.omEnterpriseOrgId = omEnterpriseOrgId;
    }

    public String getOrgUrlSlug() {
        return orgUrlSlug;
    }

    public void setOrgUrlSlug(String orgUrlSlug) {
        this.orgUrlSlug = orgUrlSlug;
    }

    public String getOmOrganizationCompany() {
        return omOrganizationCompany;
    }

    public void setOmOrganizationCompany(String omOrganizationCompany) {
        this.omOrganizationCompany = omOrganizationCompany;
    }

    public String getOmOrganizationPrivacy() {
        return omOrganizationPrivacy;
    }

    public void setOmOrganizationPrivacy(String omOrganizationPrivacy) {
        this.omOrganizationPrivacy = omOrganizationPrivacy;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }

    public String getOmRecordIndexId() {
        return omRecordIndexId;
    }

    public void setOmRecordIndexId(String omRecordIndexId) {
        this.omRecordIndexId = omRecordIndexId;
    }

    public String getOmOrganizationDesignation() {
        return omOrganizationDesignation;
    }

    public void setOmOrganizationDesignation(String omOrganizationDesignation) {
        this.omOrganizationDesignation = omOrganizationDesignation;
    }

    public String getOmIsCurrent() {
        return omIsCurrent;
    }

    public void setOmIsCurrent(String omIsCurrent) {
        this.omIsCurrent = omIsCurrent;
    }

    public Integer getOmIsPrivate() {
        return omIsPrivate;
    }

    public void setOmIsPrivate(Integer omIsPrivate) {
        this.omIsPrivate = omIsPrivate;
    }

    public String getOmOrganizationFromDate() {
        return omOrganizationFromDate;
    }

    public void setOmOrganizationFromDate(String omOrganizationFromDate) {
        this.omOrganizationFromDate = omOrganizationFromDate;
    }

    public String getOmOrganizationToDate() {
        return omOrganizationToDate;
    }

    public void setOmOrganizationToDate(String omOrganizationToDate) {
        this.omOrganizationToDate = omOrganizationToDate;
    }

    public String getOmOrganizationType() {
        return omOrganizationType;
    }

    public void setOmOrganizationType(String omOrganizationType) {
        this.omOrganizationType = omOrganizationType;
    }

    public String getOmOrganizationLogo() {
        return omOrganizationLogo;
    }

    public void setOmOrganizationLogo(String omOrganizationLogo) {
        this.omOrganizationLogo = omOrganizationLogo;
    }

    public String getOmIsVerified() {
        return omIsVerified;
    }

    public void setOmIsVerified(String omIsVerified) {
        this.omIsVerified = omIsVerified;
    }

}
