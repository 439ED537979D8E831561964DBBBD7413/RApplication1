package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Monal on 25/10/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpLog {

    private String oldId;
    private String oldOtpString;
    private String oldValidUpto;
    private String oldDeliveredTime;
    private String createdAt;
    private String rcProfileMasterPmId;

    @JsonProperty("otp_id")
    public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

    @JsonProperty("otp")
    public String getOldOtpString() {
        return oldOtpString;
    }

    public void setOldOtpString(String oldOtpString) {
        this.oldOtpString = oldOtpString;
    }

    public String getOldValidUpto() {
        return oldValidUpto;
    }

    public void setOldValidUpto(String oldValidUpto) {
        this.oldValidUpto = oldValidUpto;
    }

    public String getOldDeliveredTime() {
        return oldDeliveredTime;
    }


    public void setOldDeliveredTime(String oldDeliveredTime) {
        this.oldDeliveredTime = oldDeliveredTime;
    }

    @JsonProperty("otp_generation_time")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("pm_id")
    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }
}
