package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by admin on 05/12/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrivateData {

    @JsonProperty("pb_phone_number")
    private ArrayList<String> pbPhoneNumber;
    @JsonProperty("pb_event")
    private ArrayList<String> pbEvent;
    @JsonProperty("pb_email_id")
    private ArrayList<String> pbEmailId;
    @JsonProperty("pb_im_accounts")
    private ArrayList<String> pbIMAccounts;
    @JsonProperty("pb_address")
    private ArrayList<String> pbAddress;
    @JsonProperty("pb_education")
    private ArrayList<String> pbEducation;
    @JsonProperty("pb_aadhaar")
    private ArrayList<String> pbAadhaar;
    @JsonProperty("profile_rating")
    private ArrayList<String> pbRating;

    public ArrayList<String> getPbPhoneNumber() {
        return pbPhoneNumber;
    }

    public void setPbPhoneNumber(ArrayList<String> pbPhoneNumber) {
        this.pbPhoneNumber = pbPhoneNumber;
    }

    public ArrayList<String> getPbEvent() {
        return pbEvent;
    }

    public void setPbEvent(ArrayList<String> pbEvent) {
        this.pbEvent = pbEvent;
    }

    public ArrayList<String> getPbEmailId() {
        return pbEmailId;
    }

    public void setPbEmailId(ArrayList<String> pbEmailId) {
        this.pbEmailId = pbEmailId;
    }

    public ArrayList<String> getPbIMAccounts() {
        return pbIMAccounts;
    }

    public void setPbIMAccounts(ArrayList<String> pbIMAccounts) {
        this.pbIMAccounts = pbIMAccounts;
    }

    public ArrayList<String> getPbAddress() {
        return pbAddress;
    }

    public void setPbAddress(ArrayList<String> pbAddress) {
        this.pbAddress = pbAddress;
    }

    public ArrayList<String> getPbEducation() {
        return pbEducation;
    }

    public void setPbEducation(ArrayList<String> pbEducation) {
        this.pbEducation = pbEducation;
    }

    public ArrayList<String> getPbAadhaar() {
        return pbAadhaar;
    }

    public void setPbAadhaar(ArrayList<String> pbAadhaar) {
        this.pbAadhaar = pbAadhaar;
    }

    public ArrayList<String> getPbRating() {
        return pbRating;
    }

    public void setPbRating(ArrayList<String> pbRating) {
        this.pbRating = pbRating;
    }
}
