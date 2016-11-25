package com.rawalinfocom.rcontact.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.ArrayList;
import java.util.HashSet;

public class ContactIdFetchService extends Service {
    public ContactIdFetchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Cursor contactNameCursor = getAllContactId();

        ArrayList<String> arrayListContactIds = new ArrayList<>();

        while (contactNameCursor.moveToNext()) {
            arrayListContactIds.add(contactNameCursor.getString(contactNameCursor
                    .getColumnIndex(ContactsContract.Contacts._ID)));
        }

        HashSet<String> contactIdSet = new HashSet<>(arrayListContactIds);
        Utils.setStringSetPreference(this, AppConstants.PREF_CONTACT_ID_SET, contactIdSet);
        sendMessage();

        return super.onStartCommand(intent, flags, startId);
    }

    private Cursor getAllContactId() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
        };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '1'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

//        return getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        return getContentResolver().query(uri, projection, null, null, sortOrder);
    }

    private void sendMessage() {
        Intent intent = new Intent(AppConstants.ACTION_CONTACT_FETCH);
        intent.putExtra(AppConstants.EXTRA_LOCAL_BROADCAST_MESSAGE, WsConstants
                .RESPONSE_STATUS_TRUE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
