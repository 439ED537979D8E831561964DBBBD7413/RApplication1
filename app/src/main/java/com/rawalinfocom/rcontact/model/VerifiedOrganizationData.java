package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 10/10/17.
 */

public class VerifiedOrganizationData implements Serializable {

    private String omOrgId;
    private String omOrgName;
    private String omOrgIsVerify;
    private VerifiedOrganizationDetails omOrgDetails;

    private String eomLogoPath;
    private String eomLogoName;

    private String eitId;
    private String eitType;

    @JsonProperty("organization_detail")
    public VerifiedOrganizationDetails getOmOrgDetails() {
        return omOrgDetails;
    }

    public void setOmOrgDetails(VerifiedOrganizationDetails omOrgDetails) {
        this.omOrgDetails = omOrgDetails;
    }

    @JsonProperty("id")
    public String getOmOrgId() {
        return omOrgId;
    }

    public void setOmOrgId(String omOrgId) {
        this.omOrgId = omOrgId;
    }

    @JsonProperty("om_name")
    public String getOmOrgName() {
        return omOrgName;
    }

    public void setOmOrgName(String omOrgName) {
        this.omOrgName = omOrgName;
    }

    @JsonProperty("om_is_verified")
    public String getOmOrgIsVerify() {
        return omOrgIsVerify;
    }

    public void setOmOrgIsVerify(String omOrgIsVerify) {
        this.omOrgIsVerify = omOrgIsVerify;
    }

    public String getEomLogoPath() {
        return eomLogoPath;
    }

    public void setEomLogoPath(String eomLogoPath) {
        this.eomLogoPath = eomLogoPath;
    }

    public String getEomLogoName() {
        return eomLogoName;
    }

    public void setEomLogoName(String eomLogoName) {
        this.eomLogoName = eomLogoName;
    }

    public String getEitId() {
        return eitId;
    }

    public void setEitId(String eitId) {
        this.eitId = eitId;
    }

    public String getEitType() {
        return eitType;
    }

    public void setEitType(String eitType) {
        this.eitType = eitType;
    }

}
