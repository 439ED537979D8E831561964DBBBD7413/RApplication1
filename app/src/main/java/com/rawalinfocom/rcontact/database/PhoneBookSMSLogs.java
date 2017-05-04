package com.rawalinfocom.rcontact.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Telephony;

/**
 * Created by Aniruddh on 03/05/17.
 */

public class PhoneBookSMSLogs {

    private Context context;

    public PhoneBookSMSLogs(Context context) {
        this.context = context;
    }

    public Cursor getAllSMSLogId() {
        String order = Telephony.Sms.DEFAULT_SORT_ORDER;
        Uri uri = Telephony.Sms.CONTENT_URI;
        return context.getContentResolver().query(uri, null, null, null, order);
    }
}
