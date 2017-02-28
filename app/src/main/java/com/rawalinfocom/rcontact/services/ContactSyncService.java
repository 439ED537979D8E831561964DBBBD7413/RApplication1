package com.rawalinfocom.rcontact.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.observer.PhoneContentObserver;

import org.apache.commons.lang3.StringUtils;

public class ContactSyncService extends Service implements WsResponseListener {

    public ContactSyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
                new PhoneContentObserver(this, new Handler()));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            // <editor-fold desc="REQ_UPLOAD_CONTACTS">

            if (serviceType.contains(WsConstants.REQ_UPLOAD_CONTACTS)) {
                WsResponseObject syncDeleteResponse = (WsResponseObject) data;
                if (syncDeleteResponse != null && StringUtils.equalsIgnoreCase
                        (syncDeleteResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Log.i("onDeliveryResponse", "Sync Successful");
                } else {
                    if (syncDeleteResponse != null) {
                        Log.e("error response", syncDeleteResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "uploadContactResponse null");
                    }
                }
            }
            //</editor-fold>

        } else {
            Log.e("Sync Service: ", error.getLocalizedMessage());
        }
    }
}
