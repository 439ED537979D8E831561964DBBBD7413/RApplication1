package com.rawalinfocom.rcontact.services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by maulik on 13/05/17.
 */

public class FCMNotificationInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMNotification";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Intent service = new Intent(getApplicationContext(), GCMRegistrationIntentService.class);
        startService(service);

//        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
}
