package com.rawalinfocom.rcontact.webservice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * A Class to send data to and/or get data from Post type web service
 */

class WebServicePost {

    private static final String TAG_LOG = "WebServicePost";

    private final Lock lock = new ReentrantLock();
    private String jsonObject = "";
    private String url;
    private int requestType = 0;
    private ObjectMapper mapper = null;
    private boolean setHeader = false;
    private Context context;

    WebServicePost(Context context, String url, int requestType, boolean setHeader) {
        this.url = url;
        this.requestType = requestType;
        this.setHeader = setHeader;
        this.context = context;
    }

    public <Request, Response> Response execute(
            Class<Response> responseType, Request request, ContentValues contentValues) throws
            Exception {

        Response response = null;
        InputStream inputStream;
        HttpURLConnection urlConnection;
        int statusCode = 0;

        try {

            /* forming th java.net.URL object */

            System.setProperty("http.keepAlive", "false");

            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            if (requestType == WSRequestType.REQUEST_TYPE_JSON.getValue()) {

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                if (setHeader) {
                    urlConnection.addRequestProperty("pm-id", Utils.getStringPreference(context,
                            AppConstants.PREF_USER_PM_ID, "0"));
                    urlConnection.addRequestProperty("access-token", Utils.getStringPreference
                            (context, AppConstants.PREF_ACCESS_TOKEN, ""));
                }
                urlConnection.connect();

                ObjectWriter writer = getMapper().writer();


                if (request != null) {
                    /**
                     * Json string passed as request
                     */
                    jsonObject = writer.writeValueAsString(request);
//					 FileUtilities utilities = new FileUtilities();
//					 utilities.write("Filter file", jsonObject);
                }

            /* pass post data */
                byte[] outputBytes = jsonObject.getBytes("UTF-8");
                OutputStream os = urlConnection.getOutputStream();
                os.write(outputBytes);
                os.close();

            /* Get Response and execute WebService request*/
                statusCode = urlConnection.getResponseCode();

            /* 200 represents HTTP OK */
                if (statusCode == HttpsURLConnection.HTTP_OK) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String responseString = convertInputStreamToString(inputStream);
                    response = getMapper().readValue(responseString, responseType);
                } else if (statusCode == HttpsURLConnection.HTTP_BAD_REQUEST) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(context, "Bad Request", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Status Code: ", HttpsURLConnection.HTTP_BAD_REQUEST + " : Bad Request " +
                            ": Due to user error");
                    response = null;
                } else if (statusCode == HttpsURLConnection.HTTP_INTERNAL_ERROR) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                         /*   Toast.makeText(context, "Internal Server Error", Toast.LENGTH_SHORT)
                                    .show();*/
                        }
                    });
                    Log.e("Status Code: ", HttpsURLConnection.HTTP_INTERNAL_ERROR + " : Internal " +
                            "Server Error : Due to any unhandled error on server");
                    response = null;
                } else if (statusCode == HttpsURLConnection.HTTP_NOT_FOUND) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(context, "Not Found", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Status Code: ", HttpsURLConnection.HTTP_NOT_FOUND + " :  Not Found :  " +
                            "Request resource not found");
                    response = null;
                } else if (statusCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(context, "Unauthorised Access", Toast.LENGTH_SHORT)
//                                    .show();
                        }
                    });
                    Log.e("Status Code: ", HttpsURLConnection.HTTP_UNAUTHORIZED + " :  " +
                            "Unauthorised Access :  Due to invalid credentials or invalid access " +
                            "token or expired token");
                    response = null;
                } else if (statusCode == 429) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(context, "Too many request", Toast.LENGTH_SHORT)
// .show();
                        }
                    });
                    Log.e("Status Code: ", 429 + " :  Too many request :  Due to API throttling");
                    response = null;
                } else {
                    response = null;
                }
            } else if (requestType == WSRequestType.REQUEST_TYPE_CONTENT_VALUE.getValue()) {

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.connect();

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os,
                        "UTF-8"));
//                Log.d(TAG, "REQUEST NAME VALUE PAIR: " + contentValues.toString() + "");
                bufferedWriter.write(getPostDataString(contentValues));

                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                statusCode = urlConnection.getResponseCode();

                if (statusCode == HttpsURLConnection.HTTP_OK) {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection
                            .getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    String responseString = sb.toString();
//                    Log.e(TAG, "The Response NameValuePair is:::" + json);
                    response = getMapper().readValue(responseString, responseType);

                } else {
                    response = null;
                }

            }

        } catch (Exception e) {
            Log.e(TAG_LOG, "Status code: " + Integer.toString(statusCode)
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

    private String getPostDataString(ContentValues values) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> pair : values.valueSet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
        }

        return result.toString();
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
//                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }
            lock.unlock();
        } catch (Exception ex) {
            Log.e(TAG_LOG, "Mapper Initialization Failed Exception : "
                    + ex.getMessage());
        }
        return mapper;
    }

}
