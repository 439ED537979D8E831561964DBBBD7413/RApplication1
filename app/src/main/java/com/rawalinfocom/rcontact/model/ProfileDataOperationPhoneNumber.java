package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationPhoneNumber {

    private String phoneType;
    private String phoneNumber;
    private int phonePublic;
    private int phoneId;
    private int pbRcpType;

    @JsonProperty("ph_type")
    public String getPhoneType() {
        return StringUtils.defaultString(this.phoneType);
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    @JsonProperty("ph_no")
    public String getPhoneNumber() {
        return StringUtils.defaultString(this.phoneNumber);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("ph_public")
    public int getPhonePublic() {
        return this.phonePublic;
    }

    public void setPhonePublic(int phonePublic) {
        this.phonePublic = phonePublic;
    }

    @JsonProperty("ph_id")
    public int getPhoneId() {
        return this.phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    @JsonProperty("pb_rcp_type")
    public int getPbRcpType() {
        return pbRcpType;
    }

    public void setPbRcpType(int pbRcpType) {
        this.pbRcpType = pbRcpType;
    }
}
