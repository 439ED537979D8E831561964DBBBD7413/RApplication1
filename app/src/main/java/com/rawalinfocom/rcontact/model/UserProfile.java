package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by Monal on 08/11/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile implements Serializable {

    private String pmId;
    private String firstName;
    private String lastName;
    private String emailId;

    @JsonProperty("first_name")
    public String getFirstName() {
        return StringUtils.defaultString(firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("last_name")
    public String getLastName() {
        return StringUtils.defaultString(lastName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("email_id")
    public String getEmailId() {
        return StringUtils.defaultString(emailId);
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @JsonProperty("pm_id")
    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }
}
