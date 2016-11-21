package com.rawalinfocom.rcontact.model;

public class ProfileDataOperationOrganization {
    private String org_job_title;
    private String org_department;
    private String org_name;
    private String org_type;
    private String org_job_description;
    private String org_office_location;

    public String getOrgJobTitle() {return this.org_job_title;}

    public void setOrgJobTitle(String org_job_title) {this.org_job_title = org_job_title;}

    public String getOrgDepartment() {return this.org_department;}

    public void setOrgDepartment(String org_department) {this.org_department = org_department;}

    public String getOrgName() {return this.org_name;}

    public void setOrgName(String org_name) {this.org_name = org_name;}

    public String getOrgType() {
        return org_type;
    }

    public void setOrgType(String org_type) {
        this.org_type = org_type;
    }

    public String getOrgJobDescription() {
        return org_job_description;
    }

    public void setOrgJobDescription(String org_job_description) {
        this.org_job_description = org_job_description;
    }

    public String getOrgOfficeLocation() {
        return org_office_location;
    }

    public void setOrgOfficeLocation(String org_office_location) {
        this.org_office_location = org_office_location;
    }
}
