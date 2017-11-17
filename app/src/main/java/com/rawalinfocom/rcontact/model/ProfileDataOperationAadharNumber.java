package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Aniruddh on 02/11/17.
 */

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationAadharNumber implements Serializable {

    Integer aadharId;
    String aadharNumber;
    Integer aadharIsVerified;
    Integer aadharPublic;
    String rcProfileMasterPmId;

    @JsonProperty("aadhaar_id")
    public Integer getAadharId() {
        return aadharId;
    }

    public void setAadharId(Integer aadharId) {
        this.aadharId = aadharId;
    }

    @JsonProperty("aadhaar_number")
    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    @JsonProperty("is_verified")
    public Integer getAadharIsVerified() {
        return aadharIsVerified;
    }

    public void setAadharIsVerified(Integer aadharIsVerified) {
        this.aadharIsVerified = aadharIsVerified;
    }

    @JsonProperty("aadhaar_public")
    public Integer getAadharPublic() {
        return aadharPublic;
    }

    public void setAadharPublic(Integer aadharPublic) {
        this.aadharPublic = aadharPublic;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }

}
