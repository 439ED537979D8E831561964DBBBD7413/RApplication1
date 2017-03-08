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
    private int visitorPmId;
    @JsonProperty("visit_count")
    private int visitCount;

    public int getVisitorPmId() {
        return visitorPmId;
    }

    public void setVisitorPmId(int visitorPmId) {
        this.visitorPmId = visitorPmId;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
}
