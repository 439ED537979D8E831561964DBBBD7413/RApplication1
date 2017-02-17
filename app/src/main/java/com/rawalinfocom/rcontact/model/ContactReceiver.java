package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by user on 16/02/17.
 */

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactReceiver {

    private ArrayList<String> mobileNumber;
    private ArrayList<String> emailId;


    @JsonProperty("mobile_number")
    public ArrayList<String> getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(ArrayList<String> mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @JsonProperty("email_id")
    public ArrayList<String> getEmailId() {
        return emailId;
    }

    public void setEmailId(ArrayList<String> emailId) {
        this.emailId = emailId;
    }
}
