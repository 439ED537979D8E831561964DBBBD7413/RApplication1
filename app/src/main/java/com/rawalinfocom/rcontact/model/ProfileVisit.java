package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by user on 01/03/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileVisit {

    @JsonProperty("visitor_pm_id")
    private Integer visitorPmId;
    @JsonProperty("visit_count")
    private Integer visitCount;

    public Integer getVisitorPmId() {
        return visitorPmId;
    }

    public void setVisitorPmId(Integer visitorPmId) {
        this.visitorPmId = visitorPmId;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }
}
