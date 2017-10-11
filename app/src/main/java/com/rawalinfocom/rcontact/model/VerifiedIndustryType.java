package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by admin on 10/10/17.
 */

public class VerifiedIndustryType implements Serializable {

    private String eitId;
    private String eitType;

    @JsonProperty("id")
    public String getEitId() {
        return eitId;
    }

    public void setEitId(String eitId) {
        this.eitId = eitId;
    }

    @JsonProperty("eit_type")
    public String getEitType() {
        return eitType;
    }

    public void setEitType(String eitType) {
        this.eitType = eitType;
    }
}
