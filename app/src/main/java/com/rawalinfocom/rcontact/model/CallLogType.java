package com.rawalinfocom.rcontact.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by user on 08/02/17.
 */

public class CallLogType implements Serializable {

    private String number;
    private long date;
    private int duration;
    private int type;
    private Context context;
    private String callSimNumber;
    private String uniqueContactId;
    private String numberType;
    private int historyId;
    private String profileImage;
    private String logDate;
    private String name;

    private String historyNumber;
    private long historyDate;
    private int historyDuration;
    private int historyType;
    private String historyCallSimNumber;
    private String historyNumberType;
    private int historyLogCount;


    public CallLogType() {
    }

    public CallLogType(Context context) {
        this.context = context;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCallSimNumber() {
        return callSimNumber;
    }

    public void setCallSimNumber(String callSimNumber) {
        this.callSimNumber = callSimNumber;
    }

    public String getCoolDuration() {
        return this.getCoolDuration((float) this.getDuration());
    }

    public String getHistroyCoolDuration() {
        return this.getCoolDuration((float) this.getHistoryDuration());
    }


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

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
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

    public long getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(long historyDate) {
        this.historyDate = historyDate;
    }

    public int getHistoryDuration() {
        return historyDuration;
    }

    public void setHistoryDuration(int historyDuration) {
        this.historyDuration = historyDuration;
    }

    public int getHistoryType() {
        return historyType;
    }

    public void setHistoryType(int historyType) {
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

    public int getHistoryLogCount() {
        return historyLogCount;
    }

    public void setHistoryLogCount(int historyLogCount) {
        this.historyLogCount = historyLogCount;
    }

    public String findNameByNumber(String phoneNumber) {
        Cursor cursor = null;
        String contactName = null;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            cursor = cr.query(uri, new String[]{"display_name"}, (String)null, new String[]{"display_name"}, (String)null);
//            cursor = cr.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " =?", new String[]{number}, null);

            if(cursor == null) {
                return null;
            } else {
                if(cursor.moveToFirst()) {
//                    contactName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                    contactName = cursor.getString(cursor.getColumnIndex("display_name"));

                }

                if(!cursor.isClosed()) {
                    cursor.close();
                }

            }

        }catch (SecurityException e){
            e.printStackTrace();
        }

        return contactName == null?phoneNumber:contactName;

    }

    private String getCoolDuration(float sum) {
        String duration = "";
        String result;
        String decimal;
        String point;
        int hours;
        float minutes;
        DecimalFormat formatter;
        if (sum >= 0.0F && sum < 3600.0F) {
            result = String.valueOf(sum / 60.0F);
            decimal = result.substring(0, result.lastIndexOf("."));
            point = "0" + result.substring(result.lastIndexOf("."));
            hours = Integer.parseInt(decimal);
            minutes = Float.parseFloat(point) * 60.0F;
            formatter = new DecimalFormat("#");
            if (hours < 10 || minutes < 10) {
                duration = "0" + hours + ":" + "0" + formatter.format((double) minutes) + "";

            } else {
                duration = hours + ":" + formatter.format((double) minutes) + "";
            }
        } else if (sum >= 3600.0F) {
            result = String.valueOf(sum / 3600.0F);
            decimal = result.substring(0, result.lastIndexOf("."));
            point = "0" + result.substring(result.lastIndexOf("."));
            hours = Integer.parseInt(decimal);
            minutes = Float.parseFloat(point) * 60.0F;
            formatter = new DecimalFormat("#");
            if (hours < 10 || minutes < 10) {
                duration = "0" + hours + ":" + "0" + formatter.format((double) minutes) + "";

            } else {
                duration = hours + ":" + formatter.format((double) minutes) + "";
            }
        }

        return duration;
    }


}
