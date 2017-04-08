package com.rawalinfocom.rcontact.model;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by user on 29/11/16.
 */

public class Address {

    private String amId;
    private String amRecordIndexId;
    private String amCity;
    private String amCountry;
    private String amFormattedAddress;
    private String amNeighborhood;
    private String amPostCode;
    private String amPoBox;
    private String amState;
    private String amStreet;
    private String amAddressType;
    private String amGoogleLatitude;
    private String amGoogleLongitude;
    private String amAddressPrivacy;
    private String rcProfileMasterPmId;

//    private String amCustomType;
//    private String amGoogleAddress;

    public String getAmId() {
        return amId;
    }

    public void setAmId(String amId) {
        this.amId = amId;
    }

    public String getAmCity() {
        return amCity;
    }

    public void setAmCity(String amCity) {
        this.amCity = amCity;
    }

    public String getAmCountry() {
        return amCountry;
    }

    public void setAmCountry(String amCountry) {
        this.amCountry = amCountry;
    }

    public String getAmFormattedAddress() {
        return amFormattedAddress;
    }

    public void setAmFormattedAddress(String amFormattedAddress) {
        this.amFormattedAddress = amFormattedAddress;
    }

    public String getAmNeighborhood() {
        return amNeighborhood;
    }

    public void setAmNeighborhood(String amNeighborhood) {
        this.amNeighborhood = amNeighborhood;
    }

    public String getAmPostCode() {
        return amPostCode;
    }

    public void setAmPostCode(String amPostCode) {
        this.amPostCode = amPostCode;
    }

    public String getAmPoBox() {
        return amPoBox;
    }

    public void setAmPoBox(String amPoBox) {
        this.amPoBox = amPoBox;
    }

    public String getAmState() {
        return amState;
    }

    public void setAmState(String amState) {
        this.amState = amState;
    }

    public String getAmStreet() {
        return amStreet;
    }

    public void setAmStreet(String amStreet) {
        this.amStreet = amStreet;
    }

    public String getAmAddressType() {
        return amAddressType;
    }

    public void setAmAddressType(String amAddressType) {
        this.amAddressType = amAddressType;
    }

    public String getAmGoogleLatitude() {
        return amGoogleLatitude;
    }

    public void setAmGoogleLatitude(String amGoogleLatitude) {
        this.amGoogleLatitude = amGoogleLatitude;
    }

    public String getAmGoogleLongitude() {
        return amGoogleLongitude;
    }

    public void setAmGoogleLongitude(String amGoogleLongitude) {
        this.amGoogleLongitude = amGoogleLongitude;
    }

    public String getAmAddressPrivacy() {
        return StringUtils.defaultString(amAddressPrivacy, "1");
    }

    public void setAmAddressPrivacy(String amAddressPrivacy) {
        this.amAddressPrivacy = amAddressPrivacy;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }

    public String getAmRecordIndexId() {
        return amRecordIndexId;
    }

    public void setAmRecordIndexId(String amRecordIndexId) {
        this.amRecordIndexId = amRecordIndexId;
    }
}
