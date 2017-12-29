package com.rawalinfocom.rcontact.webservice;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.ReLoginEnterPasswordActivity;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.helper.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * A Class to send data to and/or get data from Get type web service
 */

public class WebServiceGet {

    private static final String TAG_LOG = "WebServiceGetForCallPopup";
    private final Lock lock = new ReentrantLock();
    private String url;
    private Activity activity;
    private ObjectMapper mapper = null;

    WebServiceGet(Activity activity, String url) {
        url = url.replace(" ", "%20");
        this.url = url;
        this.activity = activity;
    }

    public <CLS> CLS execute(Class<CLS> responseType) throws Exception {

        CLS response;

        InputStream inputStream;
        HttpURLConnection urlConnection;
        int statusCode = 0;

        try {

            /* forming th java.net.URL object */

            System.setProperty("http.keepAlive", "false");

            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");

            System.out.println("RContact set header token --> " + Utils
                    .getStringPreference
                            (activity, AppConstants.PREF_ACCESS_TOKEN, ""));

            urlConnection.addRequestProperty(WsConstants.REQ_HEADER, Utils
                    .getStringPreference
                            (activity, AppConstants.PREF_ACCESS_TOKEN, ""));


            urlConnection.connect();

            /* Get Response and execute WebService request*/
            statusCode = urlConnection.getResponseCode();

            System.out.println("RContact statusCode --> " + url + " --> " + statusCode);

            /* 200 represents HTTP OK */
            if (statusCode == HttpsURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String responseString = convertInputStreamToString(inputStream);
                response = getMapper().readValue(responseString, responseType);
            } else if (statusCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                response = null;

                Utils.setIntegerPreference(activity, AppConstants.PREF_LAUNCH_SCREEN_INT,
                        IntegerConstants
                                .LAUNCH_RE_LOGIN_PASSWORD);
                Utils.setBooleanPreference(activity, AppConstants.PREF_TEMP_LOGOUT, true);
                Utils.setBooleanPreference(activity, AppConstants.PREF_IS_LOGIN, false);

                // Redirect to MobileNumberRegistrationActivity
                Intent intent = new Intent(activity, ReLoginEnterPasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(AppConstants.EXTRA_IS_FROM, AppConstants.EXTRA_IS_FROM_RE_LOGIN);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                activity.finish();

            } else {
                response = null;
            }

        } catch (Exception e) {
            System.out.println("Status code: " + Integer.toString(statusCode)
                    + " Exception thrown: " + e.getMessage());
            throw e;
        }

        return response;

    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        inputStream.close();

        return result;
    }

    private synchronized ObjectMapper getMapper() {

        if (mapper != null) {
            return mapper;
        }

        try {
            lock.lock();
            if (mapper == null) {
                mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            }
            lock.unlock();
        } catch (Exception ex) {
            System.out.println("Mapper Initialization Failed Exception : "
                    + ex.getMessage());
        }

        return mapper;
    }

}
