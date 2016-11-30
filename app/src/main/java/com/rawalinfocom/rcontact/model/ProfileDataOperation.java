package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperation {

    private ArrayList<ProfileDataOperationPhoneNumber> pbPhoneNumber;
    private String flag;
    private ArrayList<String> pbWebAddress;
    private ArrayList<ProfileDataOperationEvent> pbEvent;
    private String pbNameSuffix;
    private ArrayList<ProfileDataOperationEmail> pbEmailId;
    private String pbNameFirst;
    private String pbNameMiddle;
    private ArrayList<ProfileDataOperationOrganization> pbOrganization;
    private String isFavourite;
    private ArrayList<ProfileDataOperationRelationship> pbRelationship;
    private String pbSource;
    private String pbNamePrefix;
    private String pbNameLast;
    private String pbPhoneticNameFirst;
    private String pbPhoneticNameLast;
    private ArrayList<ProfileDataOperationImAccount> pbIMAccounts;
    private String pbPhoneticNameMiddle;
    private ArrayList<ProfileDataOperationAddress> pbAddress;
    private String pbNote;
    private String pbNickname;

    private String createdAt;
    private String rcpPmId;
    private String updatedAt;
    private String noSqlMasterId;
    private String verifiedEmailAddress;
    private String emCloudId;
    private String verifiedMobileNumber;
    private String mnmCloudId;


    @JsonProperty("pb_phone_number")
    public ArrayList<ProfileDataOperationPhoneNumber> getPbPhoneNumber() {
        return pbPhoneNumber;
    }

    public void setPbPhoneNumber(ArrayList<ProfileDataOperationPhoneNumber> pbPhoneNumber) {
        this.pbPhoneNumber = pbPhoneNumber;
    }

    @JsonProperty("flag")
    public String getFlag() {return this.flag;}

    public void setFlag(String flag) {this.flag = flag;}

    @JsonProperty("pb_web_address")
    public ArrayList<String> getPbWebAddress() {
        return pbWebAddress;
    }

    public void setPbWebAddress(ArrayList<String> pbWebAddress) {
        this.pbWebAddress = pbWebAddress;
    }

    @JsonProperty("pb_event")
    public ArrayList<ProfileDataOperationEvent> getPbEvent() {
        return pbEvent;
    }

    public void setPbEvent(ArrayList<ProfileDataOperationEvent> pbEvent) {
        this.pbEvent = pbEvent;
    }

    @JsonProperty("pb_name_suffix")
    public String getPbNameSuffix() {return this.pbNameSuffix;}

    public void setPbNameSuffix(String pbNameSuffix) {this.pbNameSuffix = pbNameSuffix;}

    @JsonProperty("pb_email_id")
    public ArrayList<ProfileDataOperationEmail> getPbEmailId() {
        return pbEmailId;
    }

    public void setPbEmailId(ArrayList<ProfileDataOperationEmail> pbEmailId) {
        this.pbEmailId = pbEmailId;
    }

    @JsonProperty("pb_name_first")
    public String getPbNameFirst() {return this.pbNameFirst;}

    public void setPbNameFirst(String pbNameFirst) {this.pbNameFirst = pbNameFirst;}

    @JsonProperty("pb_name_middle")
    public String getPbNameMiddle() {return this.pbNameMiddle;}

    public void setPbNameMiddle(String pbNameMiddle) {this.pbNameMiddle = pbNameMiddle;}

    @JsonProperty("pb_organization")
    public ArrayList<ProfileDataOperationOrganization> getPbOrganization() {
        return pbOrganization;
    }

    public void setPbOrganization(ArrayList<ProfileDataOperationOrganization> pbOrganization) {
        this.pbOrganization = pbOrganization;
    }

    @JsonProperty("is_favourite")
    public String getIsFavourite() {return this.isFavourite;}

    public void setIsFavourite(String isFavourite) {this.isFavourite = isFavourite;}

    @JsonProperty("pb_relationship")
    public ArrayList<ProfileDataOperationRelationship> getPbRelationship() {
        return pbRelationship;
    }

    public void setPbRelationship(ArrayList<ProfileDataOperationRelationship> pbRelationship) {
        this.pbRelationship = pbRelationship;
    }

    @JsonProperty("pb_source")
    public String getPbSource() {return this.pbSource;}

    public void setPbSource(String pbSource) {this.pbSource = pbSource;}

    @JsonProperty("pb_name_prefix")
    public String getPbNamePrefix() {return this.pbNamePrefix;}

    public void setPbNamePrefix(String pbNamePrefix) {this.pbNamePrefix = pbNamePrefix;}

    @JsonProperty("pb_name_last")
    public String getPbNameLast() {return this.pbNameLast;}

    public void setPbNameLast(String pbNameLast) {this.pbNameLast = pbNameLast;}

    @JsonProperty("pb_phonetic_name_first")
    public String getPbPhoneticNameFirst() {return this.pbPhoneticNameFirst;}

    public void setPbPhoneticNameFirst(String pbPhoneticNameFirst) {
        this.pbPhoneticNameFirst = pbPhoneticNameFirst;
    }

    @JsonProperty("pb_phonetic_name_last")
    public String getPbPhoneticNameLast() {return this.pbPhoneticNameLast;}

    public void setPbPhoneticNameLast(String pbPhoneticNameLast) {
        this.pbPhoneticNameLast = pbPhoneticNameLast;
    }

    @JsonProperty("pb_IM_accounts")
    public ArrayList<ProfileDataOperationImAccount> getPbIMAccounts() {
        return pbIMAccounts;
    }

    public void setPbIMAccounts(ArrayList<ProfileDataOperationImAccount> pbIMAccounts) {
        this.pbIMAccounts = pbIMAccounts;
    }

    @JsonProperty("pb_phonetic_name_middle")
    public String getPbPhoneticNameMiddle() {return this.pbPhoneticNameMiddle;}

    public void setPbPhoneticNameMiddle(String pbPhoneticNameMiddle) {
        this.pbPhoneticNameMiddle = pbPhoneticNameMiddle;
    }

    @JsonProperty("pb_address")
    public ArrayList<ProfileDataOperationAddress> getPbAddress() {
        return pbAddress;
    }

    public void setPbAddress(ArrayList<ProfileDataOperationAddress> pbAddress) {
        this.pbAddress = pbAddress;
    }

    @JsonProperty("pb_note")
    public String getPbNote() {return this.pbNote;}

    public void setPbNote(String pbNote) {this.pbNote = pbNote;}

    @JsonProperty("pb_nickname")
    public String getPbNickname() {return this.pbNickname;}

    public void setPbNickname(String pbNickname) {this.pbNickname = pbNickname;}

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("rcp_pm_id")
    public String getRcpPmId() {
        return rcpPmId;
    }

    public void setRcpPmId(String rcpPmId) {
        this.rcpPmId = rcpPmId;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("_id")
    public String getNoSqlMasterId() {
        return noSqlMasterId;
    }

    public void setNoSqlMasterId(String noSqlMasterId) {
        this.noSqlMasterId = noSqlMasterId;
    }

    @JsonProperty("email_address")
    public String getVerifiedEmailAddress() {
        return verifiedEmailAddress;
    }

    public void setVerifiedEmailAddress(String verifiedEmailAddress) {
        this.verifiedEmailAddress = verifiedEmailAddress;
    }

    @JsonProperty("em_id")
    public String getEmCloudId() {
        return emCloudId;
    }

    public void setEmCloudId(String emCloudId) {
        this.emCloudId = emCloudId;
    }

    @JsonProperty("mobile_number")
    public String getVerifiedMobileNumber() {
        return verifiedMobileNumber;
    }

    public void setVerifiedMobileNumber(String verifiedMobileNumber) {
        this.verifiedMobileNumber = verifiedMobileNumber;
    }

    @JsonProperty("mnm_id")
    public String getMnmCloudId() {
        return mnmCloudId;
    }

    public void setMnmCloudId(String mnmCloudId) {
        this.mnmCloudId = mnmCloudId;
    }
}
