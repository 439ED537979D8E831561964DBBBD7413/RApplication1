package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.linkedin.platform.LISessionManager;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.helper.MaterialProgressDialog;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.GetGoogleLocationResponse;
import com.rawalinfocom.rcontact.webservice.RequestWs;
import com.rawalinfocom.rcontact.webservice.WebServiceGet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 15/09/17.
 */

public class LinkedinLoginActivity extends AppCompatActivity {

    /****
     * FILL THIS WITH YOUR INFORMATION
     *********/
    //This is the public api key of our application
    private static final String API_KEY = "81fo9xdmek6bnj";
    //This is the private api key of our application
    private static final String SECRET_KEY = "GWtJsdhMrGiZUetq";
    //This is any string we want to use. This will be used for avoid CSRF attacks. You can generate one here: http://strongpasswordgenerator.com/
    private static final String STATE = "E3ZYKC1T6H2yP4z";
    //This is the url that LinkedIn Auth process will redirect to. We can put whatever we want that starts with http:// or https:// .
    //We use a made up url that we will intercept when redirecting. Avoid Uppercases.
    private static final String REDIRECT_URI = "https://www.rcontacts.in";
    /*********************************************/

    //These are constants used for build the urls
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE = "code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    /*---------------------------------------*/
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    @BindView(R.id.linkedin_webview)
    WebView webView;

    private Activity activity;
    private MaterialProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_linkedin_webview);
        ButterKnife.bind(this);

        activity = LinkedinLoginActivity.this;

        //Request focus for the webview
        webView.requestFocus(View.FOCUS_DOWN);

        //Show a progress dialog to the user

        progressDialog = (MaterialProgressDialog) MaterialProgressDialog.ctor(activity, getString(R.string.msg_please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        //Set a custom web view client
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //This method will be executed each time a page finished loading.
                //The only we do is dismiss the progressDialog, in case we are showing any.
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                //This method will be called when the Auth proccess redirect to our RedirectUri.
                //We will check the url looking for our RedirectUri.
                if (authorizationUrl.startsWith(REDIRECT_URI)) {
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);
                    //We take from the url the authorizationToken and the state token. We have to check that the state token returned by the Service is the same we sent.
                    //If not, that means the request may be a result of CSRF and must be rejected.
                    String stateToken = uri.getQueryParameter(STATE_PARAM);
                    if (stateToken == null || !stateToken.equals(STATE)) {
                        Log.e("Authorize", "State token doesn't match");
                        return true;
                    }

                    //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                    String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                    if (authorizationToken == null) {
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: " + authorizationToken);

                    //Generate URL for requesting Access Token
                    String accessTokenUrl = getAccessTokenUrl(authorizationToken);
                    //We make the request in a AsyncTask

                    if (getIntent().getStringExtra("from").equalsIgnoreCase("profile")) {
                        new PostUrlRequestAsyncTask().execute(accessTokenUrl);
                    } else {
                        new PostRequestAsyncTask().execute(accessTokenUrl);
                    }

                } else {
                    //Default behaviour
                    Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });

        //Get the authorization Url
        String authUrl = getAuthorizationUrl();
        Log.i("Authorize", "Loading Auth Url: " + authUrl);
        //Load the authorization URL into the webView
        webView.loadUrl(authUrl);
    }

    /**
     * Method that generates the url for get the access token from the Service
     *
     * @return Url
     */
    private static String getAccessTokenUrl(String authorizationToken) {
        return ACCESS_TOKEN_URL
                + QUESTION_MARK
                + GRANT_TYPE_PARAM + EQUALS + GRANT_TYPE
                + AMPERSAND
                + RESPONSE_TYPE_VALUE + EQUALS + authorizationToken
                + AMPERSAND
                + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND
                + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND
                + SECRET_KEY_PARAM + EQUALS + SECRET_KEY;
    }

    /**
     * Method that generates the url for get the authorization token from the Service
     *
     * @return Url
     */
    private static String getAuthorizationUrl() {
        return AUTHORIZATION_URL
                + QUESTION_MARK + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE_VALUE
                + AMPERSAND + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND + STATE_PARAM + EQUALS + STATE
                + AMPERSAND + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI;
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        String accessToken;

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if (urls.length > 0) {
                String url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpost = new HttpPost(url);
                try {
                    HttpResponse response = httpClient.execute(httpost);
                    if (response != null) {
                        //If status is OK 200
                        if (response.getStatusLine().getStatusCode() == 200) {
                            String result = EntityUtils.toString(response.getEntity());
                            //Convert the string result to a JSON Object
                            JSONObject resultJson = new JSONObject(result);
                            //Extract data from JSON Response
                            accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;

                            return true;
                        }
                    }
                } catch (IOException e) {
                    Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                } catch (ParseException | JSONException e) {
                    Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (status) {

                Intent intent = new Intent();
                intent.putExtra("accessToken", accessToken);
                intent.putExtra("isBack", "0");
                setResult(RESULT_OK, intent);
                finish();//finishing activity
            } else {
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("accessToken", "");
        intent.putExtra("isBack", "1");
        setResult(RESULT_OK, intent);
        finish();//finishing activity
    }

    public class PostUrlRequestAsyncTask extends AsyncTask<String, Void, Object> {

        String accessToken;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = (MaterialProgressDialog) MaterialProgressDialog.ctor(activity, getString(R.string.msg_please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(String... urls) {
            try {

                if (urls.length > 0) {
                    String url = urls[0];
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpost = new HttpPost(url);
                    try {
                        HttpResponse response = httpClient.execute(httpost);
                        if (response != null) {
                            //If status is OK 200
                            if (response.getStatusLine().getStatusCode() == 200) {
                                String result = EntityUtils.toString(response.getEntity());
                                //Convert the string result to a JSON Object
                                JSONObject resultJson = new JSONObject(result);
                                //Extract data from JSON Response
                                accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;

                                HttpGet httpGet = new HttpGet("https://api.linkedin.com/v1/people/~?format=json");
                                httpGet.setHeader(WsConstants.REQ_AUTHORIZATION, "Bearer " + accessToken);
                                HttpResponse httpResponse = httpClient.execute(httpGet);

                                if (httpResponse != null) {
                                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                                        String result1 = EntityUtils.toString(httpResponse.getEntity());

                                        return new JSONObject(result1);
                                    }
                                }

//                                return new WebServiceGet(activity, "https://api.linkedin.com/v1/people/~?format=json",
//                                        accessToken).execute(cls);
                            }
                        }
                    } catch (IOException e) {
                        Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                    } catch (ParseException | JSONException e) {
                        Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                    }
                }

            } catch (Exception e) {
                Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object status) {
            super.onPostExecute(status);

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            JSONObject jsonObject;

            try {
                jsonObject = new JSONObject(status.toString());

                Intent intent = new Intent();
                intent.putExtra("url", jsonObject.getJSONObject("siteStandardProfileRequest").getString("url"));
                intent.putExtra("first_name", jsonObject.getString("firstName"));
                intent.putExtra("last_name", jsonObject.getString("lastName"));
                intent.putExtra("profileImage", jsonObject.getString("image"));
                intent.putExtra("email", jsonObject.getString("email"));
                intent.putExtra("isBack", "0");
                setResult(RESULT_OK, intent);
                finish();//finishing activity

            } catch (JSONException e) {
                e.printStackTrace();
                onBackPressed();
            }
        }
    }
}
