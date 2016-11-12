package com.rawalinfocom.rcontact.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;

import java.io.IOException;

/**
 * Created by Monal on 07/11/16.
 * <p>
 * A class to get Device Token
 */

public class AsyncGetDeviceToken extends AsyncTask<Void, Void, Void> {

    private Context context;

    public AsyncGetDeviceToken(Context context) {
        super();
        this.context = context;
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        Utils.showProgressDialog(context, context.getString(R.string.msg_please_wait), false);
    }

    @Override
    protected Void doInBackground(Void... params) {

        InstanceID instanceID = InstanceID.getInstance(context);

//        AppConstants.DEVICE_TOKEN_ID = FirebaseInstanceId.getInstance().getToken();
        try {
            AppConstants.DEVICE_TOKEN_ID = instanceID.getToken(AppConstants.GCM_SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Utils.setStringPreference(context, AppConstants.DEVICE_TOKEN_ID, AppConstants
                .PREF_DEVICE_TOKEN_ID);

        Log.i("AsyncGetDeviceToken", "GCM Registration Token: " + AppConstants.DEVICE_TOKEN_ID);

        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Utils.hideProgressDialog();
    }
}
