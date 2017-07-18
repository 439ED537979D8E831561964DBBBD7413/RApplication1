package com.rawalinfocom.rcontact.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

import com.rawalinfocom.rcontact.RContactApplication;

/**
 * Created by Aniruddh on 04/04/17.
 */

public class PhoneBookCallLogs {
    private Context context;

    public PhoneBookCallLogs(Context context) {
        this.context = context;
    }

    public Cursor getAllCallLogId() {
        Uri uri = CallLog.Calls.CONTENT_URI;
        String order = CallLog.Calls.DATE + " DESC";
        return RContactApplication.getInstance().getContentResolver().query(uri, null, null, null, order);
    }

    public Cursor getSyncAllCallLogId() {
        Uri uri = CallLog.Calls.CONTENT_URI;
        String order = CallLog.Calls.DATE + " ASC";
        return RContactApplication.getInstance().getContentResolver().query(uri, null, null, null, order);
    }
}
