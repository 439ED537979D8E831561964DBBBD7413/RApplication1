package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationImAccount implements Serializable {

    private String IMId;
    private String IMAccountType;
    private String IMAccountDetails;
    private String IMAccountProtocol;
    private String IMAccountPublic;

//    private int IMRcpType;
    private String IMRcpType;

    @JsonProperty("im_id")
    public String getIMId() {
        return IMId;
    }

    public void setIMId(String IMId) {
        this.IMId = IMId;
    }

    @JsonProperty("im_account_type")
    public String getIMAccountType() {
        return StringUtils.defaultString(this.IMAccountType);
    }

    public void setIMAccountType(String IMAccountType) {
        this.IMAccountType = IMAccountType;
    }

    @JsonProperty("im_account_details")
    public String getIMAccountDetails() {
        return StringUtils.defaultString(this.IMAccountDetails);
    }

    public void setIMAccountDetails(String IMAccountDetails) {
        this.IMAccountDetails = IMAccountDetails;
    }

    @JsonProperty("im_account_public")
    public String getIMAccountPublic() {
        return StringUtils.defaultString(this.IMAccountPublic);
    }

    public void setIMAccountPublic(String IMAccountPublic) {
        this.IMAccountPublic = IMAccountPublic;
    }

    @JsonProperty("im_account_protocol")
    public String getIMAccountProtocol() {
        return StringUtils.defaultString(IMAccountProtocol);
    }

    public void setIMAccountProtocol(String IMAccountProtocol) {
        this.IMAccountProtocol = IMAccountProtocol;
    }

   /* public int getIMRcpType() {
        return IMRcpType;
    }

    public void setIMRcpType(int IMRcpType) {
        this.IMRcpType = IMRcpType;
    }*/

    public String getIMRcpType() {
        return IMRcpType;
    }

    public void setIMRcpType(String IMRcpType) {
        this.IMRcpType = IMRcpType;
    }
}
