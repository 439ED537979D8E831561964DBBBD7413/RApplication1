package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationAddress implements Serializable {

    private String addId;
    private String country;
    private String addressType;
    private String city;
    private String postCode;
    private String street;
    private String googleLongitude;
    private String googleAddress;
    private String neighborhood;
    private String state;
    private String PoBox;
    private String googleLatitude;
    private String formattedAddress;
    private int addPublic;
    private int isPrivate;

    private String rcpType;

    @JsonProperty("add_id")
    public String getAddId() {
        return addId;
    }

    public void setAddId(String addId) {
        this.addId = addId;
    }

    @JsonProperty("country")
    public String getCountry() {
        return StringUtils.defaultString(this.country);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("address_type")
    public String getAddressType() {
        return StringUtils.defaultString(this.addressType);
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    @JsonProperty("city")
    public String getCity() {
        return StringUtils.defaultString(this.city);
    }

    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("post_code")
    public String getPostCode() {
        return StringUtils.defaultString(this.postCode);
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @JsonProperty("street")
    public String getStreet() {
        return StringUtils.defaultString(this.street);
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @JsonProperty("google_lng")
    public String getGoogleLongitude() {
        return StringUtils.defaultString(this.googleLongitude);
    }

    public void setGoogleLongitude(String googleLongitude) {
        this.googleLongitude = googleLongitude;
    }

    @JsonProperty("google_address")
    public String getGoogleAddress() {
        return StringUtils.defaultString(this.googleAddress);
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    @JsonProperty("neighborhood")
    public String getNeighborhood() {
        return StringUtils.defaultString(this.neighborhood);
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    @JsonProperty("state")
    public String getState() {
        return StringUtils.defaultString(this.state);
    }

    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("po_box")
    public String getPoBox() {
        return StringUtils.defaultString(this.PoBox);
    }

    public void setPoBox(String po_box) {
        this.PoBox = po_box;
    }

    @JsonProperty("google_lat")
    public String getGoogleLatitude() {
        return StringUtils.defaultString(this.googleLatitude);
    }

    public void setGoogleLatitude(String googleLatitude) {
        this.googleLatitude = googleLatitude;
    }

    @JsonProperty("formatted_address")
    public String getFormattedAddress() {
        return StringUtils.defaultString(formattedAddress);
    }

    public void setFormattedAddress(String formatted_address) {
        this.formattedAddress = formatted_address;
    }

    @JsonProperty("add_public")
    public int getAddPublic() {
        return addPublic;
    }

    public void setAddPublic(int addPublic) {
        this.addPublic = addPublic;
    }

    public String getRcpType() {
        return rcpType;
    }

    public void setRcpType(String rcpType) {
        this.rcpType = rcpType;
    }

    @JsonProperty("is_private")
    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }
}
