package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationAddress {

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

    @JsonProperty("country")
    public String getCountry() {return this.country;}

    public void setCountry(String country) {this.country = country;}

    @JsonProperty("address_type")
    public String getAddressType() {return this.addressType;}

    public void setAddressType(String addressType) {this.addressType = addressType;}

    @JsonProperty("city")
    public String getCity() {return this.city;}

    public void setCity(String city) {this.city = city;}

    @JsonProperty("post_code")
    public String getPostCode() {return this.postCode;}

    public void setPostCode(String postCode) {this.postCode = postCode;}

    @JsonProperty("street")
    public String getStreet() {return this.street;}

    public void setStreet(String street) {this.street = street;}

    @JsonProperty("google_log")
    public String getGoogleLongitude() {return this.googleLongitude;}

    public void setGoogleLongitude(String googleLongitude) {this.googleLongitude = googleLongitude;}

    @JsonProperty("google_address")
    public String getGoogleAddress() {return this.googleAddress;}

    public void setGoogleAddress(String googleAddress) {this.googleAddress = googleAddress;}

    @JsonProperty("neighborhood")
    public String getNeighborhood() {return this.neighborhood;}

    public void setNeighborhood(String neighborhood) {this.neighborhood = neighborhood;}

    @JsonProperty("state")
    public String getState() {return this.state;}

    public void setState(String state) {this.state = state;}

    @JsonProperty("po_box")
    public String getPoBox() {return this.PoBox;}

    public void setPoBox(String po_box) {this.PoBox = po_box;}

    @JsonProperty("google_lat")
    public String getGoogleLatitude() {return this.googleLatitude;}

    public void setGoogleLatitude(String googleLatitude) {this.googleLatitude = googleLatitude;}

    @JsonProperty("formatted_address")
    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formatted_address) {
        this.formattedAddress = formatted_address;
    }
}
