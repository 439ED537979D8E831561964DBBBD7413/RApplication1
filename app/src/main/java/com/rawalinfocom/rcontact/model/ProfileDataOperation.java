package com.rawalinfocom.rcontact.model;

import java.util.ArrayList;

public class ProfileDataOperation {

    private ArrayList<ProfileDataOperationPhoneNumber> pb_phone_number;
    private String flag;
    private ArrayList<String> pb_web_address;
    private ArrayList<ProfileDataOperationEvent> pb_event;
    private String device_update_at;
    private String pb_name_suffix;
    private ArrayList<ProfileDataOperationEmail> pb_email_id;
    private String pb_name_first;
    private String pb_name_middle;
    private ArrayList<ProfileDataOperationOrganization> pb_organization;
    private String is_favourite;
    private ArrayList<ProfileDataOperationRelationship> pb_relationship;
//    private ProfileDataOperationRelationship[] pb_relationship;
    private String pb_source;
    private String pb_name_prefix;
    private String pb_name_last;
    private String pb_phonetic_name_first;
    private String pb_phonetic_name_last;
    private ArrayList<ProfileDataOperationImAccount> pb_IM_accounts;
    private String pb_phonetic_name_middle;
    private ArrayList<ProfileDataOperationAddress> pb_address;
    private String pb_note;
    private String pb_nickname;

    public ArrayList<ProfileDataOperationPhoneNumber> getPbPhoneNumber() {
        return pb_phone_number;
    }

    public void setPbPhoneNumber(ArrayList<ProfileDataOperationPhoneNumber> pb_phone_number) {
        this.pb_phone_number = pb_phone_number;
    }

    public String getFlag() {return this.flag;}

    public void setFlag(String flag) {this.flag = flag;}

    public ArrayList<String> getPbWebAddress() {
        return pb_web_address;
    }

    public void setPbWebAddress(ArrayList<String> pb_web_address) {
        this.pb_web_address = pb_web_address;
    }

    public ArrayList<ProfileDataOperationEvent> getPbEvent() {
        return pb_event;
    }

    public void setPbEvent(ArrayList<ProfileDataOperationEvent> pb_event) {
        this.pb_event = pb_event;
    }

    public String getDeviceUpdateAt() {return this.device_update_at;}

    public void setDeviceUpdateAt(String device_update_at) {
        this.device_update_at =
                device_update_at;
    }

    public String getPbNameSuffix() {return this.pb_name_suffix;}

    public void setPbNameSuffix(String pb_name_suffix) {this.pb_name_suffix = pb_name_suffix;}

    public ArrayList<ProfileDataOperationEmail> getPbEmailId() {
        return pb_email_id;
    }

    public void setPbEmailId(ArrayList<ProfileDataOperationEmail> pb_email_id) {
        this.pb_email_id = pb_email_id;
    }

    public String getPbNameFirst() {return this.pb_name_first;}

    public void setPbNameFirst(String pb_name_first) {this.pb_name_first = pb_name_first;}

    public String getPbNameMiddle() {return this.pb_name_middle;}

    public void setPbNameMiddle(String pb_name_middle) {this.pb_name_middle = pb_name_middle;}

    public ArrayList<ProfileDataOperationOrganization> getPbOrganization() {
        return pb_organization;
    }

    public void setPbOrganization(ArrayList<ProfileDataOperationOrganization> pb_organization) {
        this.pb_organization = pb_organization;
    }

    public String getIsFavourite() {return this.is_favourite;}

    public void setIsFavourite(String is_favourite) {this.is_favourite = is_favourite;}

    public ArrayList<ProfileDataOperationRelationship> getPbRelationship() {
        return pb_relationship;
    }

    public void setPbRelationship(ArrayList<ProfileDataOperationRelationship> pb_relationship) {
        this.pb_relationship = pb_relationship;
    }

    public String getPbSource() {return this.pb_source;}

    public void setPbSource(String pb_source) {this.pb_source = pb_source;}

    public String getPbNamePrefix() {return this.pb_name_prefix;}

    public void setPbNamePrefix(String pb_name_prefix) {this.pb_name_prefix = pb_name_prefix;}

    public String getPbNameLast() {return this.pb_name_last;}

    public void setPbNameLast(String pb_name_last) {this.pb_name_last = pb_name_last;}

    public String getPbPhoneticNameFirst() {return this.pb_phonetic_name_first;}

    public void setPbPhoneticNameFirst(String pb_phonetic_name_first) {
        this
                .pb_phonetic_name_first = pb_phonetic_name_first;
    }

    public String getPbPhoneticNameLast() {return this.pb_phonetic_name_last;}

    public void setPbPhoneticNameLast(String pb_phonetic_name_last) {
        this
                .pb_phonetic_name_last = pb_phonetic_name_last;
    }

    public ArrayList<ProfileDataOperationImAccount> getPbIMAccounts() {
        return pb_IM_accounts;
    }

    public void setPbIMAccounts(ArrayList<ProfileDataOperationImAccount> pb_IM_accounts) {
        this.pb_IM_accounts = pb_IM_accounts;
    }

    public String getPbPhoneticNameMiddle() {return this.pb_phonetic_name_middle;}

    public void setPbPhoneticNameMiddle(String pb_phonetic_name_middle) {
        this
                .pb_phonetic_name_middle = pb_phonetic_name_middle;
    }

    public ArrayList<ProfileDataOperationAddress> getPbAddress() {
        return pb_address;
    }

    public void setPbAddress(ArrayList<ProfileDataOperationAddress> pb_address) {
        this.pb_address = pb_address;
    }

    public String getPbNote() {return this.pb_note;}

    public void setPbNote(String pb_note) {this.pb_note = pb_note;}

    public String getPbNickname() {return this.pb_nickname;}

    public void setPbNickname(String pb_nickname) {this.pb_nickname = pb_nickname;}
}
