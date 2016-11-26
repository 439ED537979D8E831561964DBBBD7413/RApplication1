package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationImAccount {
    private String IMAccountType;
    private String IMAccountDetails;
    private String IMAccountProtocol;
    private String IMAccountPublic;

    @JsonProperty("IM_account_type")
    public String getIMAccountType() {return this.IMAccountType;}

    public void setIMAccountType(String IMAccountType) {this.IMAccountType = IMAccountType;}

    @JsonProperty("IM_account_details")
    public String getIMAccountDetails() {return this.IMAccountDetails;}

    public void setIMAccountDetails(String IMAccountDetails) {
        this.IMAccountDetails = IMAccountDetails;
    }

    @JsonProperty("IM_account_public")
    public String getIMAccountPublic() {return this.IMAccountPublic;}

    public void setIMAccountPublic(String IMAccountPublic) {
        this.IMAccountPublic = IMAccountPublic;
    }

    @JsonProperty("IM_account_protocol")
    public String getIMAccountProtocol() {
        return IMAccountProtocol;
    }

    public void setIMAccountProtocol(String IMAccountProtocol) {
        this.IMAccountProtocol = IMAccountProtocol;
    }
}
