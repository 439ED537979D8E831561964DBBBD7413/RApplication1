package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationWebAddress implements Serializable {

    private String webAddress;
    private String webId;
    private String webType;
    private int webPublic;
    private int webIsPrivate;

    private String webRcpType;

    @JsonProperty("web_address")
    public String getWebAddress() {
        return StringUtils.defaultString(webAddress);
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    @JsonProperty("web_id")
    public String getWebId() {
        return webId;
    }

    public void setWebId(String webId) {
        this.webId = webId;
    }

    @JsonProperty("web_type")
    public String getWebType() {
        return StringUtils.defaultString(webType);
    }

    public void setWebType(String webType) {
        this.webType = webType;
    }

    @JsonProperty("web_public")
    public int getWebPublic() {
        return webPublic;
    }

    public void setWebPublic(int webPublic) {
        this.webPublic = webPublic;
    }

    public String getWebRcpType() {
        return webRcpType;
    }

    public void setWebRcpType(String webRcpType) {
        this.webRcpType = webRcpType;
    }

    @JsonProperty("is_private")
    public int getWebIsPrivate() {
        return webIsPrivate;
    }

    public void setWebIsPrivate(int webIsPrivate) {
        this.webIsPrivate = webIsPrivate;
    }
}
