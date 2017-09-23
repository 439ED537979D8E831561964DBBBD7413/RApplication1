package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class Organization {

    private String omId;
    private String omRecordIndexId;
    private String omOrganizationCompany;
    private String omOrganizationDesignation;
    private String omIsCurrent;
    private Integer omIsPrivate;
    private String omOrganizationPrivacy;
    private String rcProfileMasterPmId;
    private String omOrganizationFromDate;
    private String omOrganizationToDate;

    /*private String omOrganizationType;
    private String omCustomType;
    private String omOrganizationTitle;
    private String omOrganizationDepartment;
    private String omJobDescription;
    private String omOfficeLocation;*/

    public String getOmId() {
        return omId;
    }

    public void setOmId(String omId) {
        this.omId = omId;
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
}
