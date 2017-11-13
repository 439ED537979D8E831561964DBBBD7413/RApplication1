package com.rawalinfocom.rcontact.asynctasks;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.GetGoogleLocationResponse;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.webservice.RequestWs;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Monal on 20/10/16.
 * <p>
 * Class to execute all Web Services
 */

public class AsyncGetWebServiceCall extends AsyncTask<String, Void, Object> {

    private final String LOG_TAG = "AsyncWebServiceCall";
    private Exception error = null;

    private Activity activity;
    private WsResponseListener wsResponseListener;
    private String progressDialogMessage;
    private Class responseClass;
    private String serviceType;

    public AsyncGetWebServiceCall(Activity activity, Class responseClass, String
            serviceType, String progressDialogMessage) {

        this.activity = activity;
        this.serviceType = serviceType;
        this.progressDialogMessage = progressDialogMessage;
        this.responseClass = responseClass;

        wsResponseListener = (WsResponseListener) activity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialogMessage != null) {
            Utils.showProgressDialog(activity, progressDialogMessage, true);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        try {
            return new RequestWs().getGetRequest(activity, params[0], responseClass);
        } catch (Exception e) {
            if (progressDialogMessage != null)
                Utils.hideProgressDialog();
            this.error = e;
            Log.e(LOG_TAG, e.getMessage() + "");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        wsResponseListener.onDeliveryResponse(serviceType, result, error);
    }
}
