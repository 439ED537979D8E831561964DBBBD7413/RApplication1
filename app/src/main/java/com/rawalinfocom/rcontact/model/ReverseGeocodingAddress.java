package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by user on 21/02/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReverseGeocodingAddress implements Serializable {

    private String latitude;
    private String longitude;
    private String country;
    private String city;
    private String state;
    private String address;
    private String postalCode;
    private String addressLine;

    public String getLatitude() {
        return StringUtils.defaultString(latitude);
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return StringUtils.defaultString(longitude);
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return StringUtils.defaultString(country);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return StringUtils.defaultString(city);
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return StringUtils.defaultString(state);
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return StringUtils.defaultString(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }
}
