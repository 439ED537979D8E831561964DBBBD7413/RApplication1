package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Monal on 05/06/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetGoogleLocationResponse {

    private String status;
    private String nextPageToken;
    private ArrayList<GetGoogleLocationResultObject> results;
    private ArrayList<GetGoogleLocationResultObject> predictions;

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("next_page_token")
    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    @JsonProperty("results")
    public ArrayList<GetGoogleLocationResultObject> getResults() {
        return results;
    }

    public void setResults(ArrayList<GetGoogleLocationResultObject> results) {
        this.results = results;
    }

    @JsonProperty("predictions")
    public ArrayList<GetGoogleLocationResultObject> getPredictions() {
        return predictions;
    }

    public void setPredictions(ArrayList<GetGoogleLocationResultObject> predictions) {
        this.predictions = predictions;
    }

}
