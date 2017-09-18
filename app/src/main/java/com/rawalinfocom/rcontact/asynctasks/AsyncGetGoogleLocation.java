package com.rawalinfocom.rcontact.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.GetGoogleLocationResponse;
import com.rawalinfocom.rcontact.webservice.RequestWs;

/**
 * Created by user on 05/06/17.
 */

public class AsyncGetGoogleLocation extends AsyncTask<String, Void, Object> {

    private final String LOG_TAG = "AsyncGetGoogleLocation";
    private Exception error = null;

    private Activity activity;
    private boolean displayProgress;

    private WsResponseListener wsResponseListener;

    private String serviceType;
    private String accessToken;

    public AsyncGetGoogleLocation(Activity activity, boolean displayProgress, String serviceType, String accessToken) {

        this.activity = activity;
        this.displayProgress = displayProgress;
        this.serviceType = serviceType;
        this.accessToken = accessToken;

        wsResponseListener = (WsResponseListener) activity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (displayProgress) {
            Utils.showProgressDialog(activity, activity.getString(R.string.str_searching), true);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        try {
            // return new RequestWsForCallPopup().getPostRequest(params[0],
            // GetGoogleLocationResponse.class, addLocationReqObject, null);
            return new RequestWs().getGetRequest(activity, params[0],
                    GetGoogleLocationResponse.class, accessToken);
        } catch (Exception e) {
            this.error = e;
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if (displayProgress) {
            Utils.hideProgressDialog();
        }
        wsResponseListener.onDeliveryResponse(serviceType, result, error);
    }
}
