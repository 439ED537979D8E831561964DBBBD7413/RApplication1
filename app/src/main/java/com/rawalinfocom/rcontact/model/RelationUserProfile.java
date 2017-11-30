package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by user on 29/11/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationUserProfile {

    private Integer rrmToPmId;
    private String pmFirstName;
    private String pmLastName;
    private String pmBadge;
    private String mobileNumber;
    private String profilePhoto;

    public Integer getRrmToPmId() {
        return rrmToPmId;
    }

    public void setRrmToPmId(Integer rrmToPmId) {
        this.rrmToPmId = rrmToPmId;
    }

    @JsonProperty("pm_first_name")
    public String getPmFirstName() {
        return pmFirstName;
    }

    public void setPmFirstName(String pmFirstName) {
        this.pmFirstName = pmFirstName;
    }

    @JsonProperty("pm_last_name")
    public String getPmLastName() {
        return pmLastName;
    }

    public void setPmLastName(String pmLastName) {
        this.pmLastName = pmLastName;
    }

    @JsonProperty("pm_badge")
    public String getPmBadge() {
        return pmBadge;
    }

    public void setPmBadge(String pmBadge) {
        this.pmBadge = pmBadge;
    }

    @JsonProperty("mobile_number")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @JsonProperty("pm_profile_photo")
    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
