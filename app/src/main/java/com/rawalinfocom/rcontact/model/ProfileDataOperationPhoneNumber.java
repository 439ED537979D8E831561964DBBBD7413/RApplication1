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
    private int phonePublic;
    private String phoneId;
    private String pbRcpType;
//    private int pbRcpType;

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
    public String getPhoneId() {
        return this.phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    //    @JsonIgnore
   /* @JsonProperty("pb_rcp_type")
    public int getPbRcpType() {
        return pbRcpType;
    }

    public void setPbRcpType(int pbRcpType) {
        this.pbRcpType = pbRcpType;
    }*/

    @JsonProperty("pb_rcp_type")
    public String getPbRcpType() {
        return pbRcpType;
    }

    public void setPbRcpType(String pbRcpType) {
        this.pbRcpType = pbRcpType;
    }
}
