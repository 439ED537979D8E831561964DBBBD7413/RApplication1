package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Monal on 05/06/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class GetGoogleLocationResultGeometry {

    private GetGoogleLocationResultLocation getGoogleLocationResultLocation;

    @JsonProperty("location")
    public GetGoogleLocationResultLocation getGetGoogleLocationResultLocation() {
        return getGoogleLocationResultLocation;
    }

    public void setGetGoogleLocationResultLocation(
            GetGoogleLocationResultLocation getGoogleLocationResultLocation) {
        this.getGoogleLocationResultLocation = getGoogleLocationResultLocation;
    }
}
