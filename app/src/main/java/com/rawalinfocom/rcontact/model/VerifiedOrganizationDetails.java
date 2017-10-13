package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 10/10/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifiedOrganizationDetails implements Serializable {

    private String eomLogoPath;
    private String eomLogoName;
    private VerifiedIndustryType verifiedIndustryType;

    @JsonProperty("industry_type")
    public VerifiedIndustryType getVerifiedIndustryType() {
        return verifiedIndustryType;
    }

    @JsonProperty("eom_logo_path")
    public String getEomLogoPath() {
        return eomLogoPath;
    }

    public void setEomLogoPath(String eomLogoPath) {
        this.eomLogoPath = eomLogoPath;
    }

    @JsonProperty("eom_logo_name")
    public String getEomLogoName() {
        return eomLogoName;
    }

    public void setEomLogoName(String eomLogoName) {
        this.eomLogoName = eomLogoName;
    }


    public void setVerifiedIndustryType(VerifiedIndustryType verifiedIndustryType) {
        this.verifiedIndustryType = verifiedIndustryType;
    }


}
