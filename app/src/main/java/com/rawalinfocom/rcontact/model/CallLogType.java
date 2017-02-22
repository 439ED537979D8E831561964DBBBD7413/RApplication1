package com.rawalinfocom.rcontact.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

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
    private int histroyId;
    String profileImage;
    String logDate;
    String name;

    private String histroyNumber;
    private long histroyDate;
    private int histroydDuration;
    private int histroyType;
    private String histroyCallSimNumber;
    private String histroyNumberType;
    private int histroyLogCount;


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
        return this.getCoolDuration((float)this.getDuration());
    }

    public String getHistroyCoolDuration() {
        return this.getCoolDuration((float)this.getHistroydDuration());
    }


    public String getContactName() {
        return this.getNumber() != null?this.findNameByNumber(this.getNumber()):null;
    }

    public String getUniqueContactId() {
        return uniqueContactId;
    }

    public void setUniqueContactId(String uniqueContactId) {
        this.uniqueContactId = uniqueContactId;
    }

    public int getHistroyId() {
        return histroyId;
    }

    public void setHistroyId(int histroyId) {
        this.histroyId = histroyId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getHistroyNumber() {
        return histroyNumber;
    }

    public void setHistroyNumber(String histroyNumber) {
        this.histroyNumber = histroyNumber;
    }

    public long getHistroyDate() {
        return histroyDate;
    }

    public void setHistroyDate(long histroyDate) {
        this.histroyDate = histroyDate;
    }

    public int getHistroydDuration() {
        return histroydDuration;
    }

    public void setHistroydDuration(int histroydDuration) {
        this.histroydDuration = histroydDuration;
    }

    public int getHistroyType() {
        return histroyType;
    }

    public void setHistroyType(int histroyType) {
        this.histroyType = histroyType;
    }

    public String getHistroyCallSimNumber() {
        return histroyCallSimNumber;
    }

    public void setHistroyCallSimNumber(String histroyCallSimNumber) {
        this.histroyCallSimNumber = histroyCallSimNumber;
    }

    public String getHistroyNumberType() {
        return histroyNumberType;
    }

    public void setHistroyNumberType(String histroyNumberType) {
        this.histroyNumberType = histroyNumberType;
    }

    public int getHistroyLogCount() {
        return histroyLogCount;
    }

    public void setHistroyLogCount(int histroyLogCount) {
        this.histroyLogCount = histroyLogCount;
    }

    private String findNameByNumber(String phoneNumber) {
        ContentResolver cr = this.context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{"display_name"}, (String)null, new String[]{"display_name"}, (String)null);
        if(cursor == null) {
            return null;
        } else {
            String contactName = null;
            if(cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex("display_name"));
            }

            if(!cursor.isClosed()) {
                cursor.close();
            }

            return contactName == null?phoneNumber:contactName;
        }
    }

    private String getCoolDuration(float sum) {
        String duration = "";
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
            if(hours<10 || minutes<10)
            {
                duration = "0"+hours + ":" + "0"+formatter.format((double)minutes) + "";

            }else {
                duration = hours + ":" + formatter.format((double)minutes) + "";
            }
        } else if(sum >= 3600.0F) {
            result = String.valueOf(sum / 3600.0F);
            decimal = result.substring(0, result.lastIndexOf("."));
            point = "0" + result.substring(result.lastIndexOf("."));
            hours = Integer.parseInt(decimal);
            minutes = Float.parseFloat(point) * 60.0F;
            formatter = new DecimalFormat("#");
            if(hours<10 || minutes<10){
                duration = "0"+hours + ":" + "0"+formatter.format((double)minutes) + "";

            }else {
                duration = hours + ":" + formatter.format((double)minutes) + "";
            }
        }

        return duration;
    }


}
