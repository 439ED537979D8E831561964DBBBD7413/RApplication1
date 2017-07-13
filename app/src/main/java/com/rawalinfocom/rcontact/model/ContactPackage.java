package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Monal on 13/07/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactPackage {

    private String brand;
    private String contactPackage;

    @JsonProperty("brand")
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @JsonProperty("package")
    public String getContactPackage() {
        return contactPackage;
    }

    public void setContactPackage(String contactPackage) {
        this.contactPackage = contactPackage;
    }
}
