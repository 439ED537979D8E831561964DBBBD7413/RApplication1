package com.rawalinfocom.rcontact.model;

/**
 * Created by Maulik on 18/4/17.
 */

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


public class PrivacyDataItem {

    @JsonProperty("pb_phone_number")
    private ArrayList<PrivacyEntityItem> pbPhoneNumber;
    @JsonProperty("pb_event")
    private ArrayList<PrivacyEntityItem> pbEvent;
    @JsonProperty("pb_email_id")
    private ArrayList<PrivacyEntityItem> pbEmailId;
    @JsonProperty("pb_im_accounts")
    private ArrayList<PrivacyEntityItem> pbIMAccounts;
    @JsonProperty("pb_address")
    private ArrayList<PrivacyEntityItem> pbAddress;
    @JsonProperty("is_hide")
    private Integer isHide;

    @JsonProperty("pb_aadhaar")
    private ArrayList<PrivacyEntityItem>  pbAadhaar;



    public ArrayList<PrivacyEntityItem> getPbPhoneNumber() {
        return pbPhoneNumber;
    }

    public void setPbPhoneNumber(ArrayList<PrivacyEntityItem> pbPhoneNumber) {
        this.pbPhoneNumber = pbPhoneNumber;
    }

    public ArrayList<PrivacyEntityItem> getPbEvent() {
        return pbEvent;
    }

    public void setPbEvent(ArrayList<PrivacyEntityItem> pbEvent) {
        this.pbEvent = pbEvent;
    }

    public ArrayList<PrivacyEntityItem> getPbEmailId() {
        return pbEmailId;
    }

    public void setPbEmailId(ArrayList<PrivacyEntityItem> pbEmailId) {
        this.pbEmailId = pbEmailId;
    }

    public ArrayList<PrivacyEntityItem> getPbIMAccounts() {
        return pbIMAccounts;
    }

    public void setPbIMAccounts(ArrayList<PrivacyEntityItem> pbIMAccounts) {
        this.pbIMAccounts = pbIMAccounts;
    }

    public ArrayList<PrivacyEntityItem> getPbAddress() {
        return pbAddress;
    }

    public void setPbAddress(ArrayList<PrivacyEntityItem> pbAddress) {
        this.pbAddress = pbAddress;
    }

    public Integer getIsHide() {
        return isHide;
    }

    public void setIsHide(Integer isHide) {
        this.isHide = isHide;
    }

    public ArrayList<PrivacyEntityItem> getPbAadhaar() {
        return pbAadhaar;
    }

    public void setPbAadhaar(ArrayList<PrivacyEntityItem> pbAadhaar) {
        this.pbAadhaar = pbAadhaar;
    }
}