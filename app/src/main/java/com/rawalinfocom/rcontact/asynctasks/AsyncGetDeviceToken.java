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

public class AsyncGetDeviceToken extends AsyncTask<Void, Void, String> {

    private Context context;
    private String deviceTokenId = "";
    private Exception error = null;

    public AsyncGetDeviceToken(Context context) {
        super();
        this.context = context;
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
//        Utils.showProgressDialog(context, context.getString(R.string.msg_please_wait), false);
    }

    @Override
    protected String doInBackground(Void... params) {

        InstanceID instanceID = InstanceID.getInstance(context);

//        deviceTokenId  = FirebaseInstanceId.getInstance().getToken();
        try {
            deviceTokenId = instanceID.getToken(AppConstants.GCM_SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            this.error = e;
            e.printStackTrace();
        }

        Utils.setStringPreference(context, AppConstants.PREF_DEVICE_TOKEN_ID, deviceTokenId);

        Log.i("AsyncGetDeviceToken", "GCM Registration Token: " + deviceTokenId);

        return deviceTokenId;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
//        Utils.hideProgressDialog();
    }
}
