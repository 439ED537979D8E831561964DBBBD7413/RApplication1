package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by admin on 16/11/17.
 */

public class RelationMaster {

    private Integer id;
    private String rmParticular;

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

}
