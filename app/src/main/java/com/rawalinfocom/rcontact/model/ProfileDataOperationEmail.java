package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationEmail {
    private int emId;
    private String emEmailId;
    private String emType;
    private int emPublic;

    @JsonProperty("em_id")
    public int getEmId() {
        return this.emId;
    }

    public void setEmId(int emId) {
        this.emId = emId;
    }

    @JsonProperty("em_email_id")
    public String getEmEmailId() {
        return StringUtils.defaultString(this.emEmailId);
    }

    public void setEmEmailId(String emEmailId) {
        this.emEmailId = emEmailId;
    }

    @JsonProperty("em_type")
    public String getEmType() {
        return StringUtils.defaultString(this.emType);
    }

    public void setEmType(String emType) {
        this.emType = emType;
    }

    @JsonProperty("em_public")
    public int getEmPublic() {
        return this.emPublic;
    }

    public void setEmPublic(int emPublic) {
        this.emPublic = emPublic;
    }
}
