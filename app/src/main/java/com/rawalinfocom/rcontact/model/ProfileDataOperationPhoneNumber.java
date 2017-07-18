package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationPhoneNumber implements Serializable {

    private String phoneType;
    private String phoneNumber;
    private Integer phonePublic;
    private String phoneId;
    private Integer pbRcpType;
    private Integer isPrivate;

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
    public Integer getPhonePublic() {
        return this.phonePublic;
    }

    public void setPhonePublic(Integer phonePublic) {
        this.phonePublic = phonePublic;
    }

    @JsonProperty("ph_id")
    public String getPhoneId() {
        return this.phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

//    @JsonProperty("pb_rcp_type")
    @JsonProperty("is_verified")
    public Integer getPbRcpType() {
        return pbRcpType;
    }

    public void setPbRcpType(Integer pbRcpType) {
        this.pbRcpType = pbRcpType;
    }

    @JsonProperty("is_private")
    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }
}
