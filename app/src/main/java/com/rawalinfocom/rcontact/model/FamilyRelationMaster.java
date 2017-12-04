package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by user on 29/11/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FamilyRelationMaster {

    private Integer id;
    private Integer rrmType;
    private String rmParticular;
    private Integer gender;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("rm_particular")
    public String getRmParticular() {
        return rmParticular;
    }

    public void setRmParticular(String rmParticular) {
        this.rmParticular = rmParticular;
    }

    @JsonProperty("rrm_type")
    public Integer getRrmType() {
        return rrmType;
    }

    public void setRrmType(Integer rrmType) {
        this.rrmType = rrmType;
    }

    @JsonProperty("rm_gender")
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

}
