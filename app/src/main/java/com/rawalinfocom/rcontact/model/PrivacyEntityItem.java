package com.rawalinfocom.rcontact.model;

/**
 * Created by Maulik on 18/4/17.
 */

import com.fasterxml.jackson.annotation.JsonProperty;


public class PrivacyEntityItem {
    @JsonProperty("id")
    private String id;
    @JsonProperty("value")
    private Integer value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}