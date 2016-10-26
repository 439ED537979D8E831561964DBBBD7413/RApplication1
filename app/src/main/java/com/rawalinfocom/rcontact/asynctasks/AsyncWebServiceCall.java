package com.rawalinfocom.rcontact.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.webservice.RequestWs;

/**
 * Created by Monal on 20/10/16.
 * <p>
 * Class to execute all Web Services
 */

public class AsyncWebServiceCall extends AsyncTask<String, Void, Object> {

    private final String LOG_TAG = "AsyncWebServiceCall";
    private Exception error = null;

    private Context context;
    private WsRequestObject requestObject;
    private WsResponseListener wsResponseListener;
    private String progressDialogMessage;

    private int requestType;
    private ContentValues contentValues;

    private Class responseClass;

    private String serviceType;

    public AsyncWebServiceCall(Context context, int requestType, WsRequestObject requestObject,
                               ContentValues contentValues, Class responseClass, String
                                       serviceType, String progressDialogMessage) {

        this.context = context;
        this.requestObject = requestObject;
        this.serviceType = serviceType;
        this.progressDialogMessage = progressDialogMessage;
        this.responseClass = responseClass;
        this.requestType = requestType;
        this.contentValues = contentValues;

        wsResponseListener = (WsResponseListener) context;

    }

    public AsyncWebServiceCall(Fragment fragment, int requestType, WsRequestObject requestObject,
                               ContentValues contentValues, Class responseClass, String
                                       serviceType, String progressDialogMessage) {

        this.context = fragment.getContext();
        this.requestObject = requestObject;
        this.serviceType = serviceType;
        this.progressDialogMessage = progressDialogMessage;
        this.responseClass = responseClass;
        this.requestType = requestType;
        this.contentValues = contentValues;

        wsResponseListener = (WsResponseListener) fragment;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       /* if (progressDialogMessage != null) {
            AppUtils.showProgressDialog(context, progressDialogMessage, true);
        }*/
    }

    @Override
    protected Object doInBackground(String... params) {
        try {
            return new RequestWs().getPostRequest(params[0], requestType, requestObject,
                    responseClass, contentValues);
        } catch (Exception e) {
            this.error = e;
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        wsResponseListener.onDeliveryResponse(serviceType, result, error);
    }

}
