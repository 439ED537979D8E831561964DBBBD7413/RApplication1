package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Monal on 20/10/16.
 * <p>
 * Class containing all the Response Objects
 */

public class WsRequestObject {

    private String countryCode;
    private String mobileNumber;
    private String pmId;
    private String otp;
    private String otpGenerationTime;

    @JsonProperty("country_code")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("mobile_number")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @JsonProperty("pm_id")
    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    @JsonProperty("otp")
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @JsonProperty("otp_generation_time")
    public String getOtpGenerationTime() {
        return otpGenerationTime;
    }

    public void setOtpGenerationTime(String otpGenerationTime) {
        this.otpGenerationTime = otpGenerationTime;
    }
}
