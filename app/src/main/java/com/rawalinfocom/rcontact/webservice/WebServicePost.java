package com.rawalinfocom.rcontact.webservice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.ReLoginEnterPasswordActivity;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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

    private String jsonObject = "";
    private String url;
    private int requestType = 0;
    private ObjectMapper mapper = null;
    private static final String TAG_LOG = "WebServicePost";
    private final Lock lock = new ReentrantLock();
    private boolean setHeader = false;
    private Activity activity;

    WebServicePost(Activity activity, String url, int requestType, boolean setHeader) {
        this.url = url;
        this.requestType = requestType;
        this.setHeader = setHeader;
        this.activity = activity;
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

                    System.out.println("RContact set header token --> " + Utils
                            .getStringPreference
                                    (activity, AppConstants.PREF_ACCESS_TOKEN, ""));

                    urlConnection.addRequestProperty(WsConstants.REQ_HEADER, Utils
                            .getStringPreference
                                    (activity, AppConstants.PREF_ACCESS_TOKEN, ""));
                }

                urlConnection.connect();
                ObjectWriter writer = getMapper().writer();

                if (request != null) {

                    // Json string passed as request
                    jsonObject = writer.writeValueAsString(request);
                    System.out.println("RContacts param -->  " + jsonObject);
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

//                System.out.println("RContact statusCode --> " + url + " --> " + statusCode);

            /* 200 represents HTTP OK */
                if (statusCode == HttpsURLConnection.HTTP_OK) {

//                    String header = urlConnection.getHeaderField("rc-auth-token");
                    String header = urlConnection.getHeaderField(WsConstants.REQ_HEADER);

                    if (url.toString().endsWith(WsConstants.REQ_SAVE_PASSWORD)
                            || url.toString().endsWith(WsConstants.REQ_REGISTER_WITH_SOCIAL_MEDIA)
                            || url.toString().endsWith(WsConstants.REQ_LOGIN_WITH_SOCIAL_MEDIA)
                            || url.toString().endsWith(WsConstants.REQ_CHECK_LOGIN)
                            || url.toString().endsWith(WsConstants.REQ_OTP_CONFIRMED)) {

                        System.out.println("RContact new token --> " + header);

                        Utils.setStringPreference(activity, AppConstants.PREF_ACCESS_TOKEN, header);
                    }

                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String responseString = convertInputStreamToString(inputStream);
                    response = getMapper().readValue(responseString, responseType);
                } else if (statusCode == HttpsURLConnection.HTTP_BAD_REQUEST) {
//                    Log.e("Status Code: ", HttpsURLConnection.HTTP_BAD_REQUEST + " : Bad Request " +
//                            ": Due to user error");
                    inputStream = new BufferedInputStream(urlConnection.getErrorStream());
                    String responseString = convertInputStreamToString(inputStream);
                    response = getMapper().readValue(responseString, responseType);
                } else if (statusCode == 429) {
//                    Log.e("Status Code: ", ": Due to throttling");
                    final String header = urlConnection.getHeaderField(WsConstants
                            .REQ_THROTTLING_HEADER);
                    String responseString = "{\"message\":\"Retry after " + header + " seconds\"}";
                    response = getMapper().readValue(responseString, responseType);
                } else if (statusCode == 426) {
                    String responseString = "{\"message\":\"force update\"}";
                    response = getMapper().readValue(responseString, responseType);
                } else if (statusCode == HttpsURLConnection.HTTP_INTERNAL_ERROR) {
//                    Log.e("Status Code: ", HttpsURLConnection.HTTP_INTERNAL_ERROR + " : Internal " +
//                            "Server Error : Due to any unhandled error on server");
                    response = null;
                } else if (statusCode == HttpsURLConnection.HTTP_NOT_FOUND) {
//                    Log.e("Status Code: ", HttpsURLConnection.HTTP_NOT_FOUND + " :  Not Found :  " +
//                            "Request resource not found");
                    response = null;
                } else if (statusCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
//                    Log.e("Status Code: ", HttpsURLConnection.HTTP_UNAUTHORIZED + " :  " +
//                            "Unauthorised Access :  Due to invalid credentials or invalid access " +
//                            "token or expired token");
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
                        sb.append(line).append("\n");
                    }
                    String responseString = sb.toString();
//                    Log.e(TAG, "The Response NameValuePair is:::" + json);
                    response = getMapper().readValue(responseString, responseType);

                } else {
                    response = null;
                }

            }

        } catch (Exception e) {
            System.out.println("RContacts Status code: " + Integer.toString(statusCode)
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
//    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
//        @Override
//        public boolean verify(String hostname, SSLSession session) {
//            HostnameVerifier hv =
//                    HttpsURLConnection.getDefaultHostnameVerifier();
//            return hv.verify("api.rcontacts.in", session);
//        }
//    };
//
//    /**
//     * Trust every server - dont check for any certificate
//     */
//    private static void trustAllHosts() {
//        // Create a trust manager that does not validate certificate chains
//        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                return new java.security.cert.X509Certificate[]{};
//            }
//
//            public void checkClientTrusted(X509Certificate[] chain,
//                                           String authType) throws CertificateException {
//            }
//
//            public void checkServerTrusted(X509Certificate[] chain,
//                                           String authType) throws CertificateException {
//            }
//        }};
//
//        // Install the all-trusting trust manager
//        try {
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection
//                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

