package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Monal on 25/10/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpLog {

    private String oldId;
    private String oldOtp;
    private String oldGeneratedAt;
    private String oldMspDeliveryTime;
    private String oldValidUpto;
    private String oldValidityFlag;
    private String rcProfileMasterPmId;

    public String getOldId() {
        return StringUtils.defaultString(oldId);
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

    @JsonProperty("otp")
    public String getOldOtp() {
        return StringUtils.defaultString(oldOtp);
    }

    public void setOldOtp(String oldOtp) {
        this.oldOtp = oldOtp;
    }

    @JsonProperty("otp_generation_time")
    public String getOldGeneratedAt() {
        return StringUtils.defaultString(oldGeneratedAt);
    }

    public void setOldGeneratedAt(String oldGeneratedAt) {
        this.oldGeneratedAt = oldGeneratedAt;
    }

    @JsonProperty("msp_delivered_time")
    public String getOldMspDeliveryTime() {
        return StringUtils.defaultString(oldMspDeliveryTime);
    }

    public void setOldMspDeliveryTime(String oldMspDeliveryTime) {
        this.oldMspDeliveryTime = oldMspDeliveryTime;
    }

    public String getOldValidUpto() {
        return StringUtils.defaultString(oldValidUpto);
    }

    public void setOldValidUpto(String oldValidUpto) {
        this.oldValidUpto = oldValidUpto;
    }

    public String getOldValidityFlag() {
        return StringUtils.defaultString(oldValidityFlag);
    }

    public void setOldValidityFlag(String oldValidityFlag) {
        this.oldValidityFlag = oldValidityFlag;
    }

    @JsonProperty("pm_id")
    public String getRcProfileMasterPmId() {
        return StringUtils.defaultString(rcProfileMasterPmId);
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }
}
