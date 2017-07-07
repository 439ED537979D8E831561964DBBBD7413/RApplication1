package com.rawalinfocom.rcontact.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;

/**
 * Created by admin on 07/07/17.
 */

public class GCMRegistrationIntentService extends IntentService {

    private static final String TAG = "FireBaseTokenService";
    //Constants for success and errors
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";

    //Class constructor
    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Registering gcm to the device
        registerGCM();

    }

    private void registerGCM() {
        //Registration complete intent initially null
        Intent registrationComplete = null;

        //Register token is also null
        //we will get the token on successfull registration
        String token = null;
        try {

            //Getting the token from the instance id
            token = FirebaseInstanceId.getInstance().getToken();

            //Displaying the token in the log so that we can copy it to send push notification
            //You can also extend the app by storing the token in to your server
            System.out.println("RContact FCM Notification token: " + token);

            // MyApplication.getInstance().getPreferenceUtility().setToken(token);
            // TODO: Implement this method to send any registration to your app's servers.
            saveRegistrationToSP(token);

            //on registration complete creating intent with success
            registrationComplete = new Intent(REGISTRATION_SUCCESS);

            //Putting the token to the intent
            registrationComplete.putExtra("token", token);
        } catch (Exception e) {
            //If any error occurred
            System.out.println(TAG + " Registration error");
//            registrationComplete = new Intent(REGISTRATION_ERROR);
        }

        //Sending the broadcast that registration is completed
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void saveRegistrationToSP(String token) {
        // Add custom implementation, as needed.
        Utils.setStringPreference(this, AppConstants.PREF_DEVICE_TOKEN_ID, token);
    }
}