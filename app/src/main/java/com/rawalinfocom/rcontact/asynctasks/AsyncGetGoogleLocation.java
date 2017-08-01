package com.rawalinfocom.rcontact.asynctasks;

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

    private Context context;
    private boolean displayProgress;

    private WsResponseListener wsResponseListener;

    private String serviceType;

    public AsyncGetGoogleLocation(Context context, boolean displayProgress, String serviceType) {

        this.context = context;
        this.displayProgress = displayProgress;
        this.serviceType = serviceType;

        wsResponseListener = (WsResponseListener) context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (displayProgress) {
            Utils.showProgressDialog(context, context.getString(R.string.str_searching), true);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        try {
            // return new RequestWsForCallPopup().getPostRequest(params[0],
            // GetGoogleLocationResponse.class, addLocationReqObject, null);
            return new RequestWs().getGetRequest(params[0],
                    GetGoogleLocationResponse.class);
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
