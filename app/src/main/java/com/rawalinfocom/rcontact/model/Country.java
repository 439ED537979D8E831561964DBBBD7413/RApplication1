package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by Monal on 20/10/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country implements Serializable {

    private String countryId;
    private String countryCode;
    private String countryCodeNumber;
    private String countryName;
    private String countryNumberMaxDigits;
    private String countryNumberMinDigits;

    @JsonProperty("cm_id")
    public String getCountryId() {
        return StringUtils.defaultString(countryId);
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    @JsonProperty("cm_country_code")
    public String getCountryCode() {
        return StringUtils.defaultString(countryCode);
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("cm_country_code_number")
    public String getCountryCodeNumber() {
        return StringUtils.defaultString(countryCodeNumber);
    }

    public void setCountryCodeNumber(String countryCodeNumber) {
        this.countryCodeNumber = countryCodeNumber;
    }

    @JsonProperty("cm_country_name")
    public String getCountryName() {
        return StringUtils.defaultString(countryName);
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @JsonProperty("cm_max_digits")
    public String getCountryNumberMaxDigits() {
        return StringUtils.defaultString(countryNumberMaxDigits);
    }

    public void setCountryNumberMaxDigits(String countryNumberMaxDigits) {
        this.countryNumberMaxDigits = countryNumberMaxDigits;
    }

    @JsonProperty("cm_min_digits")
    public String getCountryNumberMinDigits() {
        return countryNumberMinDigits;
    }

    public void setCountryNumberMinDigits(String countryNumberMinDigits) {
        this.countryNumberMinDigits = countryNumberMinDigits;
    }
}
