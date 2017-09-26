package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationEmail implements Serializable {

    private String emId;
    private String emEmailId;
    private String emType;
    private String emSocialType;
    private Integer emPublic;
    private Integer emRcpType;
    private Integer emIsSocial;
    private String emIsVerified;
    private Integer emIsPrivate;

    @JsonProperty("em_id")
    public String getEmId() {
        return this.emId;
    }

    public void setEmId(String emId) {
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

    @JsonProperty("social_type")
    public String getEmSocialType() {
        return StringUtils.defaultString(this.emSocialType);
    }

    public void setEmSocialType(String emSocialType) {
        this.emSocialType = emSocialType;
    }

    @JsonProperty("is_social")
    public Integer getEmIsSocail() {
        return this.emIsSocial;
    }

    public void setEmIsSocial(Integer emIsSocial) {
        this.emIsSocial = emIsSocial;
    }

    @JsonProperty("em_public")
    public Integer getEmPublic() {
        return this.emPublic;
    }

    public void setEmPublic(Integer emPublic) {
        this.emPublic = emPublic;
    }

    //    @JsonProperty("em_rcp_type")
    @JsonProperty("is_verified")
    public Integer getEmRcpType() {
        return emRcpType;
    }

    public void setEmRcpType(Integer emRcpType) {
        this.emRcpType = emRcpType;
    }

    /*@JsonProperty("is_verified")
    public String getEmIsVerified() {
        return emIsVerified;
    }

    public void setEmIsVerified(String emIsVerified) {
        this.emIsVerified = emIsVerified;
    }*/

    @JsonProperty("is_private")
    public Integer getEmIsPrivate() {
        return emIsPrivate;
    }

    public void setEmIsPrivate(Integer emIsPrivate) {
        this.emIsPrivate = emIsPrivate;
    }
}
