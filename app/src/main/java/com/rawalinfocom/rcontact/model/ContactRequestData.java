package com.rawalinfocom.rcontact.model;

/**
 * Created by JacksonGenerator on 12/5/17.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class ContactRequestData {
    @JsonProperty("em_id")
    private String emId;
    @JsonProperty("is_private")
    private Integer isPrivate;
    @JsonProperty("em_email_id")
    private String emEmailId;
    @JsonProperty("em_type")
    private String emType;
    @JsonProperty("em_public")
    private Integer emPublic;
    @JsonProperty("ph_type")
    private String phType;
    @JsonProperty("ph_no")
    private String phNo;
    @JsonProperty("ph_public")
    private Integer phPublic;
    @JsonProperty("ph_id")
    private String phId;
    @JsonProperty("country")
    private String country;
    @JsonProperty("formatted_address")
    private String formattedAddress;
    @JsonProperty("address_type")
    private String addressType;
    @JsonProperty("city")
    private String city;
    @JsonProperty("po_box")
    private String poBox;
    @JsonProperty("add_id")
    private String addId;
    @JsonProperty("add_public")
    private Integer addPublic;
    @JsonProperty("street")
    private String street;
    @JsonProperty("google_address")
    private String googleAddress;
    @JsonProperty("post_code")
    private String postCode;
    @JsonProperty("google_lng")
    private String googleLng;
    @JsonProperty("state")
    private String state;
    @JsonProperty("neighborhood")
    private String neighborhood;
    @JsonProperty("google_lat")
    private String googleLat;
    @JsonProperty("im_account_details")
    private String imAccountDetails;
    @JsonProperty("im_account_public")
    private Integer imAccountPublic;
    @JsonProperty("im_id")
    private String imId;
    @JsonProperty("im_account_protocol")
    private String imAccountProtocol;
    @JsonProperty("im_account_type")
    private String imAccountType;
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("is_year_hidden")
    private Integer isYearHidden;
    @JsonProperty("event_datetime")
    private String eventDatetime;
    @JsonProperty("event_public")
    private Integer eventPublic;

    public String getEmId() {
        return emId;
    }

    public void setEmId(String emId) {
        this.emId = emId;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getEmEmailId() {
        return emEmailId;
    }

    public void setEmEmailId(String emEmailId) {
        this.emEmailId = emEmailId;
    }

    public String getEmType() {
        return emType;
    }

    public void setEmType(String emType) {
        this.emType = emType;
    }

    public Integer getEmPublic() {
        return emPublic;
    }

    public void setEmPublic(Integer emPublic) {
        this.emPublic = emPublic;
    }

    public String getPhType() {
        return phType;
    }

    public void setPhType(String phType) {
        this.phType = phType;
    }

    public String getPhNo() {
        return phNo;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public Integer getPhPublic() {
        return phPublic;
    }

    public void setPhPublic(Integer phPublic) {
        this.phPublic = phPublic;
    }

    public String getPhId() {
        return phId;
    }

    public void setPhId(String phId) {
        this.phId = phId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public String getAddId() {
        return addId;
    }

    public void setAddId(String addId) {
        this.addId = addId;
    }

    public Integer getAddPublic() {
        return addPublic;
    }

    public void setAddPublic(Integer addPublic) {
        this.addPublic = addPublic;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getGoogleLng() {
        return googleLng;
    }

    public void setGoogleLng(String googleLng) {
        this.googleLng = googleLng;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getGoogleLat() {
        return googleLat;
    }

    public void setGoogleLat(String googleLat) {
        this.googleLat = googleLat;
    }

    public String getImAccountDetails() {
        return imAccountDetails;
    }

    public void setImAccountDetails(String imAccountDetails) {
        this.imAccountDetails = imAccountDetails;
    }

    public Integer getImAccountPublic() {
        return imAccountPublic;
    }

    public void setImAccountPublic(Integer imAccountPublic) {
        this.imAccountPublic = imAccountPublic;
    }

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }

    public String getImAccountProtocol() {
        return imAccountProtocol;
    }

    public void setImAccountProtocol(String imAccountProtocol) {
        this.imAccountProtocol = imAccountProtocol;
    }

    public String getImAccountType() {
        return imAccountType;
    }

    public void setImAccountType(String imAccountType) {
        this.imAccountType = imAccountType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getIsYearHidden() {
        return isYearHidden;
    }

    public void setIsYearHidden(Integer isYearHidden) {
        this.isYearHidden = isYearHidden;
    }

    public String getEventDatetime() {
        return eventDatetime;
    }

    public void setEventDatetime(String eventDatetime) {
        this.eventDatetime = eventDatetime;
    }

    public Integer getEventPublic() {
        return eventPublic;
    }

    public void setEventPublic(Integer eventPublic) {
        this.eventPublic = eventPublic;
    }
}