package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationWebAddress {

    private String webAddress;
    private int webId;
    private String webType;

    //    private int webRcpType;
    private String webRcpType;

    @JsonProperty("web_address")
    public String getWebAddress() {
        return StringUtils.defaultString(webAddress);
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    @JsonProperty("web_id")
    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    @JsonProperty("web_type")
    public String getWebType() {
        return StringUtils.defaultString(webType);
    }

    public void setWebType(String webType) {
        this.webType = webType;
    }

   /* public int getWebRcpType() {
        return webRcpType;
    }

    public void setWebRcpType(int webRcpType) {
        this.webRcpType = webRcpType;
    }*/

    public String getWebRcpType() {
        return webRcpType;
    }

    public void setWebRcpType(String webRcpType) {
        this.webRcpType = webRcpType;
    }
}
