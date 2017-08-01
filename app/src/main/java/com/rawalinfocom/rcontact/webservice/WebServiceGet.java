package com.rawalinfocom.rcontact.webservice;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
    private ObjectMapper mapper = null;

    public WebServiceGet(String url) {
        url = url.replace(" ", "%20");
        this.url = url;
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

            urlConnection.connect();

            /* Get Response and execute WebService request*/
            statusCode = urlConnection.getResponseCode();

            /* 200 represents HTTP OK */
            if (statusCode == HttpsURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String responseString = convertInputStreamToString(inputStream);
                response = getMapper().readValue(responseString, responseType);
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
