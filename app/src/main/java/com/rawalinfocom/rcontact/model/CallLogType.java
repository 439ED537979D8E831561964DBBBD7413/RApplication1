package com.rawalinfocom.rcontact.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 08/02/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallLogType implements Serializable {

    @JsonIgnore
    private Boolean isRcpUser;
    @JsonIgnore
    private String rcpFirstName;
    @JsonIgnore
    private String rcpLastName;
    @JsonIgnore
    private String rcpId;
    @JsonIgnore
    private Integer recordPosition;
    @JsonIgnore
    private String prefix;
    @JsonIgnore
    private String suffix;
    @JsonIgnore
    private String middleName;
    @JsonIgnore
    private String isRcpVerfied;
    @JsonIgnore
    private String spamCount;
    @JsonIgnore
    private String callLogProfileRating;
    @JsonIgnore
    private String callLogTotalProfileRateUser;

    @JsonIgnore
    private Long date;
    @JsonIgnore
    private Integer duration;
    @JsonIgnore
    private Integer type;
    @JsonIgnore
    private Context context;
    @JsonIgnore
    private String callSimNumber;
    /*@JsonIgnore
    private String numberType;*/
    @JsonIgnore
    private String profileImage;
    @JsonIgnore
    private String logDate;

     @JsonIgnore
//    @JsonProperty("historyNumber")
    private String historyNumber;
    @JsonIgnore
//    @JsonProperty("historyDate")
    private Long historyDate;

    @JsonProperty("historyDuration")
    private String historyCallTime;
        @JsonIgnore
   // @JsonProperty("historyType")
    private Integer historyType;
    //    @JsonIgnore
    @JsonProperty("historyNumberType")
    private String historyNumberType;

    @JsonIgnore
    private Integer historyDuration;
    @JsonIgnore
    private Integer historyLogCount;
    @JsonIgnore
    private Date callReceiverDate;
    @JsonIgnore
    Integer blockedType = 0;
    @JsonIgnore
    private Boolean isHistoryRcpUser;
    @JsonIgnore
    private String isHistoryRcpVerifiedId;
    @JsonIgnore
    private String webDuration;

    //    @JsonProperty("flag")
//    @JsonIgnore
    private Integer flag = 0;
    @JsonProperty("local_pb_row_id")
    private String localPbRowId;
    @JsonProperty("call_log_row_id")
    private String uniqueContactId;
    @JsonProperty("mobile_number")
    private String number;
    @JsonProperty("number_type")
    private String numberType;
    @JsonProperty("name")
    private String name;
    @JsonProperty("call_history_id")
    private Integer historyId;
    @JsonProperty("call_date_and_time")
    private String callDateAndTime;
    @JsonProperty("call_type")
    private String typeOfCall;
    @JsonProperty("duration")
    private String durationToPass;
    @JsonProperty("simtype")
    private String historyCallSimNumber;
//    @JsonProperty("details")
    @JsonIgnore
    ArrayList<CallLogType> arrayListCallHistory;

    public CallLogType() {
    }

    public CallLogType(Context context) {
        this.context = context;
        this.arrayListCallHistory = new ArrayList<>();
    }

    public String getIsHistoryRcpVerifiedId() {
        return isHistoryRcpVerifiedId;
    }

    public void setIsHistoryRcpVerifiedId(String isHistoryRcpVerifiedId) {
        this.isHistoryRcpVerifiedId = isHistoryRcpVerifiedId;
    }

    public String getDurationToPass() {
        return durationToPass;
    }

    public void setDurationToPass(String durationToPass) {
        this.durationToPass = durationToPass;
    }

    public String getTypeOfCall() {
        return typeOfCall;
    }

    public void setTypeOfCall(String typeOfCall) {
        this.typeOfCall = typeOfCall;
    }

    public String getCallDateAndTime() {
        return callDateAndTime;
    }

    public void setCallDateAndTime(String callDateAndTime) {
        this.callDateAndTime = callDateAndTime;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getLocalPbRowId() {
        return localPbRowId;
    }

    public void setLocalPbRowId(String localPbRowId) {
        this.localPbRowId = localPbRowId;
    }

    public ArrayList<CallLogType> getArrayListCallHistory() {
        return arrayListCallHistory;
    }

    public void setArrayListCallHistory(ArrayList<CallLogType> arrayListCallHistory) {
        this.arrayListCallHistory = arrayListCallHistory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCallSimNumber() {
        return callSimNumber;
    }

    public void setCallSimNumber(String callSimNumber) {
        this.callSimNumber = callSimNumber;
    }

    @JsonIgnore
    public String getCoolDuration() {
        return this.getCoolDuration((float) this.getDuration());
    }

    @JsonIgnore
    public String getHistroyCoolDuration() {
        return this.getCoolDuration((float) this.getHistoryDuration());
    }

    @JsonIgnore
    public String getContactName() {
        return this.getNumber() != null ? this.findNameByNumber(this.getNumber()) : null;
    }

    public String getContactNameOfNumber(String number) {
        return number != null ? this.findNameByNumber(number) : null;
    }

    public String getUniqueContactId() {
        return uniqueContactId;
    }

    public void setUniqueContactId(String uniqueContactId) {
        this.uniqueContactId = uniqueContactId;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getHistoryNumber() {
        return historyNumber;
    }

    public void setHistoryNumber(String historyNumber) {
        this.historyNumber = historyNumber;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Long getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(Long historyDate) {
        this.historyDate = historyDate;
    }

    public Integer getHistoryDuration() {
        return historyDuration;
    }

    public void setHistoryDuration(Integer historyDuration) {
        this.historyDuration = historyDuration;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Integer getHistoryType() {
        return historyType;
    }

    public void setHistoryType(Integer historyType) {
        this.historyType = historyType;
    }

    public String getHistoryCallSimNumber() {
        return historyCallSimNumber;
    }

    public void setHistoryCallSimNumber(String historyCallSimNumber) {
        this.historyCallSimNumber = historyCallSimNumber;
    }

    public String getHistoryNumberType() {
        return historyNumberType;
    }

    public void setHistoryNumberType(String historyNumberType) {
        this.historyNumberType = historyNumberType;
    }

    public Integer getHistoryLogCount() {
        return historyLogCount;
    }

    public void setHistoryLogCount(Integer historyLogCount) {
        this.historyLogCount = historyLogCount;
    }

    public Date getCallReceiverDate() {
        return callReceiverDate;
    }

    public void setCallReceiverDate(Date callReceiverDate) {
        this.callReceiverDate = callReceiverDate;
    }

    public String findNameByNumber(String phoneNumber) {
        Cursor cursor = null;
        String contactName = null;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));
            cursor = cr.query(uri, new String[]{"display_name"}, (String) null, new
                    String[]{"display_name"}, (String) null);
//            cursor = cr.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " =?",
// new String[]{number}, null);

            if (cursor == null) {
                return null;
            } else {
                if (cursor.moveToFirst()) {
//                    contactName = cursor.getString(cursor.getColumnIndex(CallLog.Calls
// .CACHED_NAME));
                    contactName = cursor.getString(cursor.getColumnIndex("display_name"));

                }

                if (!cursor.isClosed()) {
                    cursor.close();
                }

            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return contactName == null ? phoneNumber : contactName;

    }

    public String getCoolDuration(float sum) {
        String duration = "";
        String result;
        String decimal;
        String point;
        int hours;
        float minutes;
        float seconds;
        String durHours;
        String durMins;
        String durSeconds;
        DecimalFormat formatter;
        /*
        if (sum >= 0.0F && sum < 3600.0F) {
            result = String.valueOf(sum / 60.0F);
            decimal = result.substring(0, result.lastIndexOf("."));
            point = "0" + result.substring(result.lastIndexOf("."));
            hours = Integer.parseInt(decimal);
            minutes = Float.parseFloat(point) * 60.0F;
            formatter = new DecimalFormat("#");
            if (hours < 10) {
                duration = "0" + hours + ":" + "" + formatter.format((double) minutes) + "";

            } else if(minutes < 10) {
                duration = hours + ":" + "0" +formatter.format((double) minutes) + "";
            }else if(hours<10 && minutes<10){
                duration = "0" + hours + ":" + "0" + formatter.format((double) minutes) + "";
            }else{
                duration = hours + ":" + formatter.format((double) minutes) + "";
            }
        } else if (sum >= 3600.0F) {
            result = String.valueOf(sum / 3600.0F);
            decimal = result.substring(0, result.lastIndexOf("."));
            point = "0" + result.substring(result.lastIndexOf("."));
            hours = Integer.parseInt(decimal);
            minutes = Float.parseFloat(point) * 60.0F;
            formatter = new DecimalFormat("#");
            *//*if (hours < 10 || minutes < 10) {
                duration = "0" + hours + ":" + "0" + formatter.format((double) minutes) + "";

            } else {
                duration = hours + ":" + formatter.format((double) minutes) + "";
            }*//*
            if (hours < 10) {
                duration = "0" + hours + ":" + "" + formatter.format((double) minutes) + "";

            } else if(minutes < 10) {
                duration = hours + ":" + "0" +formatter.format((double) minutes) + "";
            }else if(hours<10 && minutes<10){
                duration = "0" + hours + ":" + "0" + formatter.format((double) minutes) + "";
            }else{
                duration = hours + ":" + formatter.format((double) minutes) + "";
            }
        }*/


        result = String.valueOf(sum / 60.0F);
        decimal = result.substring(0, result.lastIndexOf("."));
        point = "0" + result.substring(result.lastIndexOf("."));
        minutes = Float.parseFloat(point) * 60.0F;
        formatter = new DecimalFormat("#");

        hours = (int) sum / 3600;
        if (hours < 10) {
            durHours = "0" + hours;
        } else {
            durHours = String.valueOf(hours);
        }

        minutes = (sum % 3600) / 60;
        if (minutes < 10) {
            durMins = "0" + formatter.format((double) minutes);
        } else {
            durMins = String.valueOf(formatter.format((double) minutes));
        }

        seconds = (sum % 3600) % 60;
        if (seconds < 10) {
            durSeconds = "0" + formatter.format((double) seconds);
        } else {
            durSeconds = String.valueOf(formatter.format((double) seconds));
        }

        duration = durHours + ":" + durMins + ":" + durSeconds;

        return duration;

        /*String duration = "";
        String result;
        String decimal;
        String point;
        int hours;
        float minutes;
        DecimalFormat formatter;
        if(sum >= 0.0F && sum < 3600.0F) {
            result = String.valueOf(sum / 60.0F);
            decimal = result.substring(0, result.lastIndexOf("."));
            point = "0" + result.substring(result.lastIndexOf("."));
            hours = Integer.parseInt(decimal);
            minutes = Float.parseFloat(point) * 60.0F;
            formatter = new DecimalFormat("#");
            duration = hours + ":" + formatter.format((double)minutes) + "";
        } else if(sum >= 3600.0F) {
            result = String.valueOf(sum / 3600.0F);
            decimal = result.substring(0, result.lastIndexOf("."));
            point = "0" + result.substring(result.lastIndexOf("."));
            hours = Integer.parseInt(decimal);
            minutes = Float.parseFloat(point) * 60.0F;
            formatter = new DecimalFormat("#");
            duration = hours + ":" + formatter.format((double)minutes) + "";
        }

        return duration;*/

    }

    public Integer getBlockedType() {
        return blockedType;
    }

    public void setBlockedType(Integer blockedType) {
        this.blockedType = blockedType;
    }

    public String getHistoryCallTime() {
        return historyCallTime;
    }

    public void setHistoryCallTime(String historyCallTime) {
        this.historyCallTime = historyCallTime;
    }
    @JsonIgnore
    public Boolean isRcpUser() {
        return isRcpUser;
    }


    public void setRcpUser(Boolean rcpUser) {
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

    public Integer getRecordPosition() {
        return recordPosition;
    }

    public void setRecordPosition(Integer recordPosition) {
        this.recordPosition = recordPosition;
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

    public String getIsRcpVerfied() {
        return isRcpVerfied;
    }

    public void setIsRcpVerfied(String isRcpVerfied) {
        this.isRcpVerfied = isRcpVerfied;
    }

    public String getSpamCount() {
        return spamCount;
    }

    public void setSpamCount(String spamCount) {
        this.spamCount = spamCount;
    }

    public String getCallLogProfileRating() {
        return callLogProfileRating;
    }

    public void setCallLogProfileRating(String callLogProfileRating) {
        this.callLogProfileRating = callLogProfileRating;
    }

    public String getCallLogTotalProfileRateUser() {
        return callLogTotalProfileRateUser;
    }

    public void setCallLogTotalProfileRateUser(String callLogTotalProfileRateUser) {
        this.callLogTotalProfileRateUser = callLogTotalProfileRateUser;
    }

    public String getWebDuration() {
        return webDuration;
    }

    public void setWebDuration(String webDuration) {
        this.webDuration = webDuration;
    }
}
