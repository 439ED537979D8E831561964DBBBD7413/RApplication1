package com.rawalinfocom.rcontact.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.v4.content.LocalBroadcastManager;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookCallLogs;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.ArrayList;

/**
 * Created by Aniruddh on 04/04/17.
 */

public class CallLogIdFetchService extends Service {

    public CallLogIdFetchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PhoneBookCallLogs phoneBookCallLogs = new PhoneBookCallLogs(this);

        Cursor cursor =  phoneBookCallLogs.getAllCallLogId();
        ArrayList<String> arrayListCallLogIds = new ArrayList<>();
        if (cursor != null){
            int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
            while (cursor.moveToNext()) {
                arrayListCallLogIds.add(cursor.getString(rowId));
            }
        }

        cursor.close();

        Utils.setArrayListPreference(this, AppConstants.PREF_CALL_LOGS_ID_SET, arrayListCallLogIds);
        sendMessage();

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendMessage() {
        Intent intent = new Intent(AppConstants.ACTION_CALL_LOG_FETCH);
        intent.putExtra(AppConstants.EXTRA_LOCAL_BROADCAST_MESSAGE, WsConstants
                .RESPONSE_STATUS_TRUE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
