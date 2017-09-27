package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class OrganizationData {

    private String omId;
    private String omRecordIndexId;
    private String omOrganizationCompany;
    private String omOrganizationDesignation;
    private String omOrganizationProfileImage;

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

    public String getOmOrganizationProfileImage() {
        return omOrganizationProfileImage;
    }

    public void setOmOrganizationProfileImage(String omOrganizationProfileImage) {
        this.omOrganizationProfileImage = omOrganizationProfileImage;
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
}
