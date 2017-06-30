package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Aniruddh on 21/04/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsDataType implements Serializable {

    @JsonIgnore
    String number;
    @JsonIgnore
    String name;
    @JsonIgnore
    String profileImage;
    @JsonIgnore
    Integer flag;

    @JsonIgnore
    Integer recordPosition;
    @JsonIgnore
    private boolean isRcpUser;
    @JsonIgnore
    private String rcpFirstName;
    @JsonIgnore
    private String rcpLastName;
    @JsonIgnore
    private String rcpId;
    @JsonIgnore
    private String prefix;
    @JsonIgnore
    private String suffix;
    @JsonIgnore
    private String middleName;


    @JsonProperty("sms_row_id")
    String uniqueRowId ;
    @JsonProperty("sms_thread_id")
    String threadId;
    @JsonProperty("sms_address")
    String address;
    @JsonProperty("sms_body")
    String body;
    @JsonProperty("sms_date")
    long dataAndTime;
    @JsonProperty("sms_is_read")
    String isRead;
    @JsonProperty("sms_type")
    String typeOfMessage;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDataAndTime() {
        return dataAndTime;
    }

    public void setDataAndTime(long dataAndTime) {
        this.dataAndTime = dataAndTime;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getTypeOfMessage() {
        return typeOfMessage;
    }

    public void setTypeOfMessage(String typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

    public String getUniqueRowId() {
        return uniqueRowId;
    }

    public void setUniqueRowId(String uniqueRowId) {
        this.uniqueRowId = uniqueRowId;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRecordPosition() {
        return recordPosition;
    }

    public void setRecordPosition(Integer recordPosition) {
        this.recordPosition = recordPosition;
    }

    public boolean isRcpUser() {
        return isRcpUser;
    }

    public void setRcpUser(boolean rcpUser) {
        isRcpUser = rcpUser;
    }

    public String getRcpFirstName() {
        return rcpFirstName;
    }

    public void setRcpFirstName(String rcpFirstName) {
        this.rcpFirstName = rcpFirstName;
    }

    public String getRcpLastName() {
        return rcpLastName;
    }

    public void setRcpLastName(String rcpLastName) {
        this.rcpLastName = rcpLastName;
    }

    public String getRcpId() {
        return rcpId;
    }

    public void setRcpId(String rcpId) {
        this.rcpId = rcpId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
}
