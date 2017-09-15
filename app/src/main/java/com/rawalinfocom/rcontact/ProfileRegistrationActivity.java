package com.rawalinfocom.rcontact;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileRegistrationActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener, GoogleApiClient.OnConnectionFailedListener {

    private final int RC_SIGN_IN = 7;
    private final int RC_LINKEDIN_SIGN_IN = 8;

    final String FIRST_NAME = "first_name";
    final String LAST_NAME = "last_name";
    final String PROFILE_IMAGE = "profile_image";
    final String EMAIL_ID = "email_id";
    final String GENDER = "gender";
    final String SOCIAL_ID = "social_id";

    @BindView(R.id.includeToolbar)
    LinearLayout includeToolbar;
    RippleView rippleActionBack;
    TextView textToolbarTitle;
    @BindView(R.id.input_first_name)
    EditText inputFirstName;
    @BindView(R.id.input_last_name)
    EditText inputLastName;
    @BindView(R.id.input_email_id)
    EditText inputEmailId;
    @BindView(R.id.button_continue)
    Button buttonContinue;
    @BindView(R.id.ripple_continue)
    RippleView rippleRegister;
    @BindView(R.id.text_or)
    TextView textOr;
    @BindView(R.id.relative_or)
    RelativeLayout relativeOr;
    @BindView(R.id.button_facebook)
    Button buttonFacebook;
    @BindView(R.id.ripple_facebook)
    RippleView rippleFacebook;
    @BindView(R.id.button_google)
    Button buttonGoogle;
    @BindView(R.id.ripple_google)
    RippleView rippleGoogle;
    @BindView(R.id.button_linked_in)
    Button buttonLinkedIn;
    @BindView(R.id.ripple_linked_in)
    RippleView rippleLinkedIn;
    @BindView(R.id.relative_root_profile_registration)
    RelativeLayout relativeRootProfileRegistration;

    UserProfile userProfile;
    UserProfile userProfileRegistered;

    //    int setLoginVia = 0;
    private String firstName, lastName, email;

    public static boolean isFromSettings;
    private boolean isLikedinSuccess = false;

//    GPSTracker gpsTracker;

    // Facebook Callback Manager
    CallbackManager callbackManager;

    // Google API Client
    private GoogleApiClient googleApiClient;

    private static final int FACEBOOK_LOGIN_PERMISSION = 21;
    private static final int GOOGLE_LOGIN_PERMISSION = 22;
    private static final int LINKEDIN_LOGIN_PERMISSION = 23;

    private String[] requiredPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
            .permission.WRITE_EXTERNAL_STORAGE};

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_registration);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        userProfile = (UserProfile) Utils.getObjectPreference(ProfileRegistrationActivity.this,
                AppConstants.PREF_REGS_USER_OBJECT, UserProfile.class);
        if (userProfile == null) {
            userProfile = new UserProfile();
        }

        // Google+ Registration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi
                (Auth.GOOGLE_SIGN_IN_API, gso).build();

        switch (Utils.getIntegerPreference(ProfileRegistrationActivity.this, AppConstants
                .PREF_LOGIN_TYPE, IntegerConstants.REGISTRATION_VIA)) {
            case 0:
                IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_EMAIL;
                inputEmailId.setEnabled(true);
                break;
            case 1:
                IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_FACEBOOK;
                inputEmailId.setEnabled(false);
                break;
            case 2:
                IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_GOOGLE;
                inputEmailId.setEnabled(false);
                break;
            case 3:
                IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_LINED_IN;
                inputEmailId.setEnabled(false);
                break;
        }

        init();

       /* gpsTracker = new GPSTracker(this, null);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Utils.isLocationEnabled(this)) {
                getLocationDetail();
            } else {
                gpsTracker.showSettingsAlert();
            }
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (isFromSettings) {
            isFromSettings = false;
            if (Utils.isLocationEnabled(this)) {
                getLocationDetail();
            }
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (IntegerConstants.REGISTRATION_VIA == IntegerConstants.REGISTRATION_VIA_FACEBOOK) {
            // Facebook Callback
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (IntegerConstants.REGISTRATION_VIA == IntegerConstants
                .REGISTRATION_VIA_LINED_IN) {
            // LinkedIn Callback
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,
                    requestCode, resultCode, data);
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
            new RetrieveTokenTask().execute(result);
        }

        if (resultCode == RESULT_OK && requestCode == RC_LINKEDIN_SIGN_IN) {

            if (data != null) {
                if (data.getStringExtra("isBack").equalsIgnoreCase("0")) {
                    //If everything went Ok, change to another activity.
                    profileRegistrationViaSocial(data.getStringExtra("accessToken"), "linkedin");
                } else {
                    Utils.showErrorSnackBar(this, relativeRootProfileRegistration, "Login cancelled!");
                }
            } else {
                Utils.showErrorSnackBar(this, relativeRootProfileRegistration, "Login cancelled!");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        if (connectionResult != null && !StringUtils.isEmpty(connectionResult.getErrorMessage()))
            Log.d("onConnectionFailed:", connectionResult.getErrorMessage());
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            //<editor-fold desc="ripple_action_back">
            case R.id.ripple_action_back:
                onBackPressed();
                break;
            //</editor-fold>

            //<editor-fold desc="ripple_continue">
            case R.id.ripple_continue:
                firstName = inputFirstName.getText().toString();
                lastName = inputLastName.getText().toString();
                email = inputEmailId.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (firstName.equalsIgnoreCase("") || lastName.equalsIgnoreCase("")) {
                    Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getString(R
                            .string.str_valid_both_name));
                } else if (email.length() > 0 && !email.matches(emailPattern)) {
                    Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getString(R
                            .string.str_valid_email));
                } else {
                    profileRegistration(firstName, lastName, email, IntegerConstants
                            .REGISTRATION_VIA);
                }
                break;
            //</editor-fold>

            //<editor-fold desc="ripple_facebook">
            case R.id.ripple_facebook:

                IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_FACEBOOK;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute(requiredPermissions, FACEBOOK_LOGIN_PERMISSION);
                } else {
                    // Facebook Initialization
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    callbackManager = CallbackManager.Factory.create();

                    // Callback registration
                    registerFacebookCallback();

                    LoginManager.getInstance().logInWithReadPermissions(ProfileRegistrationActivity
                            .this, Arrays.asList(getString(R.string.str_public_profile),
                            getString(R.string.str_small_cap_email)));

                }
                break;
            //</editor-fold>

            //<editor-fold desc="ripple_google">
            case R.id.ripple_google:

                IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_GOOGLE;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute(requiredPermissions, GOOGLE_LOGIN_PERMISSION);
                } else {
                    googleSignIn();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="ripple_linked_in">
            case R.id.ripple_linked_in:

                IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_LINED_IN;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute(requiredPermissions, LINKEDIN_LOGIN_PERMISSION);
                } else {
                    linkedInSignIn();
                }
                break;
            //</editor-fold>
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String[] permissions, int requestCode) {
        boolean READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission
                (ProfileRegistrationActivity
                        .this, permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission
                (ProfileRegistrationActivity
                        .this, permissions[1]) !=
                PackageManager.PERMISSION_GRANTED;
        if (READ_EXTERNAL_STORAGE || WRITE_EXTERNAL_STORAGE) {
            requestPermissions(permissions, requestCode);
        } else {
            prepareToLoginUsingSocialMedia(requestCode);
        }
    }

    private void prepareToLoginUsingSocialMedia(int requestCode) {
        switch (requestCode) {
            case FACEBOOK_LOGIN_PERMISSION:

                // Facebook Initialization
                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();

                // Callback registration
                registerFacebookCallback();

                LoginManager.getInstance().logInWithReadPermissions(ProfileRegistrationActivity
                        .this, Arrays.asList(getString(R.string.str_public_profile),
                        getString(R.string.str_small_cap_email)));
                break;
            case GOOGLE_LOGIN_PERMISSION:
                googleSignIn();
                break;
            case LINKEDIN_LOGIN_PERMISSION:
                linkedInSignIn();
                break;
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_PROFILE_REGISTRATION">

            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_REGISTRATION)) {
                WsResponseObject userProfileResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();

                if (userProfileResponse != null && StringUtils.equalsIgnoreCase(userProfileResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    // set launch screen as SetPassword
                    Utils.setIntegerPreference(ProfileRegistrationActivity.this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                    .LAUNCH_SET_PASSWORD);

                    userProfileRegistered = new UserProfile();
                    userProfileRegistered.setPmFirstName(inputFirstName.getText().toString());
                    userProfileRegistered.setPmLastName(inputLastName.getText().toString());
                    userProfileRegistered.setEmailId(inputEmailId.getText().toString());

                    Utils.setObjectPreference(ProfileRegistrationActivity.this,
                            AppConstants.PREF_REGS_USER_OBJECT, userProfileRegistered);

//                    if (userProfileRegistered != null) {
//                        Utils.setStringPreference(this, AppConstants.PREF_USER_PM_ID,
//                                userProfileRegistered.getPmId());
//                    }

                    // Redirect to SetPasswordActivity
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_IS_FROM, "profile");
                    startActivityIntent(ProfileRegistrationActivity.this, SetPasswordActivity
                            .class, bundle);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                    //deviceDetail();

                } else {
                    if (userProfileResponse != null) {
                        Log.e("error response", userProfileResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootProfileRegistration,
                                userProfileResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "userProfileResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_STORE_DEVICE_DETAILS">

            if (serviceType.equalsIgnoreCase(WsConstants.REQ_STORE_DEVICE_DETAILS)) {
                WsResponseObject deviceDetailResponse = (WsResponseObject) data;

                Utils.hideProgressDialog();

                if (deviceDetailResponse != null && StringUtils.equalsIgnoreCase
                        (deviceDetailResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String
                            .valueOf(System.currentTimeMillis()));
                    Utils.setBooleanPreference(this, AppConstants.KEY_IS_FIRST_TIME, true);
                    Utils.setBooleanPreference(this, AppConstants.KEY_IS_RESTORE_DONE, true);

                    Utils.setBooleanPreference(this, AppConstants.PREF_IS_LOGIN, true);

                    // Redirect to MainActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                } else {
                    if (deviceDetailResponse != null) {
                        Log.e("error response", deviceDetailResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "deviceDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_REVERSE_GEO_CODING_ADDRESS">
         /*   else if (serviceType.equalsIgnoreCase(WsConstants.REQ_REVERSE_GEO_CODING_ADDRESS)) {
                ReverseGeocodingAddress objAddress = (ReverseGeocodingAddress) data;
                if (objAddress == null) {
                    if (locationCall < 2) {
                        getLocationDetail();
                        locationCall++;
                    }
                } else {
                    try {
                        locationString = objAddress.getCity() + ", " + objAddress.getState() + "," +
                                " " + objAddress.getCountry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }*/
            //</editor-fold>

            //<editor-fold desc="REQ_REGISTER_WITH_SOCIAL_MEDIA">

            if (serviceType.equalsIgnoreCase(WsConstants.REQ_REGISTER_WITH_SOCIAL_MEDIA)) {
                WsResponseObject userProfileResponse = (WsResponseObject) data;

                if (userProfileResponse != null && StringUtils.equalsIgnoreCase(userProfileResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    if (IntegerConstants.REGISTRATION_VIA == IntegerConstants.REGISTRATION_VIA_FACEBOOK)
                        LoginManager.getInstance().logOut();

                    // set launch screen as MainActivity
                    Utils.setIntegerPreference(this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                    .LAUNCH_MAIN_ACTIVITY);
                    Utils.setBooleanPreference(this, AppConstants.KEY_IS_RESTORE_DONE, true);

                    ProfileDataOperation profileDetail = userProfileResponse.getProfileDetail();
                    Utils.setObjectPreference(this, AppConstants
                            .PREF_REGS_USER_OBJECT, profileDetail);

                    Utils.setStringPreference(this, AppConstants.PREF_USER_PM_ID,
                            profileDetail.getRcpPmId());

                    Utils.setStringPreference(this, AppConstants.PREF_USER_NAME,
                            profileDetail.getPbNameFirst() + " " + profileDetail
                                    .getPbNameLast());

                    Utils.setStringPreference(this, AppConstants.PREF_USER_FIRST_NAME,
                            profileDetail.getPbNameFirst());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_LAST_NAME,
                            profileDetail.getPbNameLast());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_JOINING_DATE,
                            profileDetail.getJoiningDate());

                    Utils.setStringPreference(this, AppConstants.PREF_USER_NUMBER,
                            profileDetail.getVerifiedMobileNumber());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING,
                            profileDetail.getTotalProfileRateUser());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_RATING,
                            profileDetail.getProfileRating());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_PHOTO,
                            profileDetail.getPbProfilePhoto());

                    Utils.setBooleanPreference(ProfileRegistrationActivity.this, AppConstants
                            .PREF_DISABLE_PUSH, false);
                    Utils.setBooleanPreference(ProfileRegistrationActivity.this, AppConstants
                            .PREF_DISABLE_EVENT_PUSH, false);
                    Utils.setBooleanPreference(ProfileRegistrationActivity.this, AppConstants
                            .PREF_DISABLE_POPUP, false);

                    Utils.storeProfileDataToDb(ProfileRegistrationActivity.this, profileDetail, databaseHandler);

                    Utils.setStringPreference(this, AppConstants.EXTRA_LOGIN_TYPE, "social");

                    deviceDetail();

                } else {

                    Utils.hideProgressDialog();

                    if (userProfileResponse != null) {
                        Log.e("error response", userProfileResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootProfileRegistration,
                                userProfileResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "userProfileResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getString(R
                                .string.msg_try_later));
                    }
                }
            }

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            /*case AppConstants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission Granted
                    if (Utils.isLocationEnabled(this)) {
                        getLocationDetail();
                    } else {
                        gpsTracker.showSettingsAlert();
                    }
                }
            }
            break;*/
            case FACEBOOK_LOGIN_PERMISSION:
            case GOOGLE_LOGIN_PERMISSION:
            case LINKEDIN_LOGIN_PERMISSION:
                if (permissions[0].equals(Manifest.permission
                        .READ_EXTERNAL_STORAGE) && permissions[1].equals(Manifest.permission
                        .WRITE_EXTERNAL_STORAGE)) {
                    prepareToLoginUsingSocialMedia(requestCode);
                }
                break;
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        inputFirstName.setText(userProfile.getPmFirstName());
        inputLastName.setText(userProfile.getPmLastName());
        inputEmailId.setText(userProfile.getEmailId());

        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);

        rippleActionBack.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textToolbarTitle.setLayoutParams(layoutParams);
        textToolbarTitle.setText(R.string.title_profile_registration);

        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));
        inputFirstName.setTypeface(Utils.typefaceRegular(this));
        inputLastName.setTypeface(Utils.typefaceRegular(this));
        inputEmailId.setTypeface(Utils.typefaceRegular(this));
        buttonContinue.setTypeface(Utils.typefaceSemiBold(this));
        buttonFacebook.setTypeface(Utils.typefaceSemiBold(this));
        buttonGoogle.setTypeface(Utils.typefaceSemiBold(this));
        buttonLinkedIn.setTypeface(Utils.typefaceSemiBold(this));
        textOr.setTypeface(Utils.typefaceRegular(this));

        Utils.setRoundedCornerBackground(buttonFacebook, ContextCompat.getColor
                (ProfileRegistrationActivity.this, R.color.colorFacebookBlue), 5, 0, ContextCompat
                .getColor(ProfileRegistrationActivity.this, R.color.colorFacebookBlue));

        Utils.setRoundedCornerBackground(buttonGoogle, ContextCompat.getColor
                (ProfileRegistrationActivity.this, R.color.colorGoogleRed), 5, 0, ContextCompat
                .getColor(ProfileRegistrationActivity.this, R.color.colorGoogleRed));

        Utils.setRoundedCornerBackground(buttonLinkedIn, ContextCompat.getColor
                (ProfileRegistrationActivity.this, R.color.colorLinkedInBlue), 5, 0, ContextCompat
                .getColor(ProfileRegistrationActivity.this, R.color.colorLinkedInBlue));

        Utils.setRoundedCornerBackground(buttonContinue, ContextCompat.getColor
                (ProfileRegistrationActivity.this, R.color.colorAccent), 5, 0, ContextCompat
                .getColor(ProfileRegistrationActivity.this, R.color.colorAccent));

        rippleRegister.setOnRippleCompleteListener(this);
        rippleFacebook.setOnRippleCompleteListener(this);
        rippleGoogle.setOnRippleCompleteListener(this);
        rippleLinkedIn.setOnRippleCompleteListener(this);

    }

//    private void getFacebookBitmapFromUrl(final Bundle facebookData, final LoginResult
//            loginResult) {
//
//        SimpleTarget facebookTarget = new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
//                FileUtils fileUtils = new FileUtils(bitmap);
//                if (fileUtils.saveImageToDirectory()) {
//                    Log.i("file absolute path: ", fileUtils.getrContactDir().getAbsolutePath());
//                    String imageToBase64 = Utils.convertBitmapToBase64(BitmapFactory.decodeFile
//                            (fileUtils.getrContactDir().getAbsolutePath()));
//                    profileRegistration(facebookData.getString(FIRST_NAME), facebookData
//                                    .getString(LAST_NAME), facebookData.getString(EMAIL_ID),
//                            imageToBase64, facebookData.getString(SOCIAL_ID), loginResult
//                                    .getAccessToken().getToken(), IntegerConstants
//                                    .REGISTRATION_VIA_FACEBOOK);
//                } else {
//                    Log.e("onResourceReady: ", "There is some error in storing Image!");
//                    profileRegistration(facebookData.getString(FIRST_NAME), facebookData
//                                    .getString(LAST_NAME), facebookData.getString(EMAIL_ID),
//                            null, facebookData.getString(SOCIAL_ID), loginResult.getAccessToken()
//                                    .getToken(), IntegerConstants.REGISTRATION_VIA_FACEBOOK);
//                }
//            }
//        };
//
//        Glide.with(this)
//                .load(facebookData.getString(PROFILE_IMAGE))
//                .asBitmap()
//                .into(facebookTarget);
//
//    }

//    private void getGoogleBitmapFromUrl(GoogleSignInAccount acct) {
//
//        String personPhotoUrl = "";
//        String givenName = StringUtils.defaultString(acct.getGivenName());
//        if (StringUtils.contains(givenName, " ")) {
//            String[] names = givenName.split(" ");
//            givenName = names[0];
//        }
//        final String firstName = givenName;
//        final String lastName = StringUtils.defaultString(acct.getFamilyName());
//        final String email = StringUtils.defaultString(acct.getEmail());
//        final String personId = StringUtils.defaultString(acct.getId());
//        if (acct.getPhotoUrl() != null) {
//            personPhotoUrl = StringUtils.defaultString(acct.getPhotoUrl().toString());
//        } else {
//            profileRegistration(firstName, lastName, email, null, personId, null,
//                    IntegerConstants.REGISTRATION_VIA_GOOGLE);
//        }
//
//        SimpleTarget googleTarget = new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
//                FileUtils fileUtils = new FileUtils(bitmap);
//                if (fileUtils.saveImageToDirectory()) {
//                    Log.i("file absolute path: ", fileUtils.getrContactDir().getAbsolutePath());
//                    String imageToBase64 = Utils.convertBitmapToBase64(BitmapFactory.decodeFile
//                            (fileUtils.getrContactDir().getAbsolutePath()));
//
//                    profileRegistration(firstName, lastName, email, imageToBase64, personId, null,
//                            IntegerConstants.REGISTRATION_VIA_GOOGLE);
//                } else {
//                    Log.e("onResourceReady: ", "There is some error in storing Image!");
//                    profileRegistration(firstName, lastName, email, null, personId, null,
//                            IntegerConstants.REGISTRATION_VIA_GOOGLE);
//                }
//            }
//        };
//
//        Glide.with(this)
//                .load(personPhotoUrl)
//                .asBitmap()
//                .into(googleTarget);
//
//    }

//    private void getLinkedInBitmapFromUrl(final JSONObject response) {
//
//        String id, firstName, lastName, emailAddress, pictureUrl;
//
//        try {
//            id = response.get("id").toString();
//            firstName = response.get("firstName").toString();
//            lastName = response.get("lastName").toString();
//            emailAddress = response.get("emailAddress").toString();
//            pictureUrl = response.get("pictureUrl").toString();
//        } catch (JSONException e) {
//            id = "";
//            firstName = "";
//            lastName = "";
//            emailAddress = "";
//            pictureUrl = "";
//            e.printStackTrace();
//        }
//
//        final String finalFirstName = firstName;
//        final String finalLastName = lastName;
//        final String finalEmailAddress = emailAddress;
//        final String finalId = id;
//
//        SimpleTarget googleTarget = new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
//                FileUtils fileUtils = new FileUtils(bitmap);
//                if (fileUtils.saveImageToDirectory()) {
//                    Log.i("file absolute path: ", fileUtils.getrContactDir().getAbsolutePath());
//                    String imageToBase64 = Utils.convertBitmapToBase64(BitmapFactory.decodeFile
//                            (fileUtils.getrContactDir().getAbsolutePath()));
//
//                    // TODO: 09/02/17 social id
//                    profileRegistration(finalFirstName, finalLastName, finalEmailAddress,
//                            imageToBase64, finalId, null, IntegerConstants
//                                    .REGISTRATION_VIA_LINED_IN);
//
//                } else {
//
//                    // TODO: 09/02/17 social id
//                    Log.e("onResourceReady: ", "There is some error in storing Image!");
//                    profileRegistration(finalFirstName, finalLastName, finalEmailAddress, null,
//                            finalId, null, IntegerConstants.REGISTRATION_VIA_LINED_IN);
//
//                }
//            }
//        };
//
//        Glide.with(this)
//                .load(pictureUrl)
//                .asBitmap()
//                .into(googleTarget);
//
//    }

    /*private void getLocationDetail() {
        gpsTracker = new GPSTracker(this, null);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        AsyncReverseGeoCoding asyncReverseGeoCoding = new AsyncReverseGeoCoding(this, WsConstants
                .REQ_REVERSE_GEO_CODING_ADDRESS, false);
        asyncReverseGeoCoding.execute(new LatLng(latitude, longitude));

    }*/

    /**
     * To get key hash
     */
    @SuppressLint("PackageManagerGetSignatures")
    @SuppressWarnings("unused")
    public void generateHashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.rawalinfocom.rcontact",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.i("generateHashkey: ", "Package Name: " + info.packageName);
                Log.i("generateHashkey: ", "Hash Key: " + Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));

            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.d("Hash Key", e.getMessage(), e);
        }
    }

    private boolean isNameValid(String name) {
        String pattern = ".*[a-zA-Z]+.*";
        return name.matches(pattern);
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">
    private void profileRegistrationViaSocial(String accessToken, String socialMedia) {

        WsRequestObject profileRegistrationObject = new WsRequestObject();
        profileRegistrationObject.setAccessToken(StringUtils.trimToEmpty(accessToken));
        profileRegistrationObject.setSocialMedia(socialMedia);
        profileRegistrationObject.setCreatedBy("2"); // For Android Devices
        profileRegistrationObject.setGcmToken(getDeviceTokenId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    profileRegistrationObject, null, WsResponseObject.class, WsConstants
                    .REQ_REGISTER_WITH_SOCIAL_MEDIA, getString(R.string.msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            WsConstants.WS_ROOT_V2 + WsConstants.REQ_REGISTER_WITH_SOCIAL_MEDIA);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void profileRegistration(String firstName, String lastName, String emailId, int type) {

        if (StringUtils.length(firstName) > 2 && StringUtils.length(firstName) < 51 &&
                isNameValid(firstName)) {
            if (StringUtils.length(lastName) > 2 && StringUtils.length(lastName) < 51 &&
                    isNameValid(lastName)) {
                WsRequestObject profileRegistrationObject = new WsRequestObject();
                profileRegistrationObject.setFirstName(StringUtils.trimToEmpty(firstName));
                profileRegistrationObject.setLastName(StringUtils.trimToEmpty(lastName));
                profileRegistrationObject.setEmailId(StringUtils.trimToEmpty(emailId));
                profileRegistrationObject.setCreatedBy("2"); // For Android Devices
                profileRegistrationObject.setType(String.valueOf(type));
                profileRegistrationObject.setGcmToken(getDeviceTokenId());
//                profileRegistrationObject.setDeviceId(getDeviceId());

                if (Utils.isNetworkAvailable(this)) {
                    new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                            profileRegistrationObject, null, WsResponseObject.class, WsConstants
                            .REQ_PROFILE_REGISTRATION, getString(R.string.msg_please_wait), true)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                    WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_REGISTRATION);
                } else {
                    Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getResources()
                            .getString(R.string.msg_no_network));
                }
            } else {
                Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getString(R.string
                        .error_required_last_name));
            }
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getString(R.string
                    .error_required_first_name));
        }
    }

    private void deviceDetail() {

        String model = android.os.Build.MODEL;
        String androidVersion = android.os.Build.VERSION.RELEASE;
        String brand = android.os.Build.BRAND;
        String device = android.os.Build.DEVICE;
        String secureAndroidId = Settings.Secure.getString(getContentResolver(), Settings.Secure
                .ANDROID_ID);

        WsRequestObject deviceDetailObject = new WsRequestObject();
        deviceDetailObject.setDmModel(StringUtils.defaultString(model));
        deviceDetailObject.setDmVersion(StringUtils.defaultString(androidVersion));
        deviceDetailObject.setDmBrand(StringUtils.defaultString(brand));
        deviceDetailObject.setDmDevice(StringUtils.defaultString(device));
        deviceDetailObject.setDmUniqueid(StringUtils.defaultString(secureAndroidId));
//        deviceDetailObject.setDmLocation(StringUtils.defaultString(locationString));

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    deviceDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_STORE_DEVICE_DETAILS, null, true).executeOnExecutor(AsyncTask
                            .THREAD_POOL_EXECUTOR,
                    WsConstants.WS_ROOT + WsConstants.REQ_STORE_DEVICE_DETAILS);
        }
        /*else {
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getResources()
                    .getString(R.string.msg_no_network));
        }*/
    }

    //</editor-fold>

    //<editor-fold desc="Facebook Registration">

    private void registerFacebookCallback() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult
                                .getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse
                                    graphResponse) {
                                Bundle facebookData = getFacebookData(jsonObject);

                                if (facebookData != null) {

//                                    firstName = facebookData.getString(FIRST_NAME);
//                                    lastName = facebookData.getString(LAST_NAME);
//                                    email = facebookData.getString(EMAIL_ID);

//                                    inputFirstName.setText(firstName);
//                                    inputLastName.setText(lastName);
//                                    inputEmailId.setText(email);

//                                    inputEmailId.setEnabled(false);

                                    profileRegistrationViaSocial(loginResult.getAccessToken().getToken(), "facebook");
                                }

                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, " +
                                "birthday, location");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Utils.showErrorSnackBar(ProfileRegistrationActivity.this,
                                relativeRootProfileRegistration, getString(R.string
                                        .error_facebook_login_cancelled));
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Utils.showErrorSnackBar(ProfileRegistrationActivity.this,
                                relativeRootProfileRegistration, exception.getMessage());
                    }
                });


    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");
            bundle.putString(SOCIAL_ID, id);
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id +
                        "/picture?width=200&height=150");
                bundle.putString(PROFILE_IMAGE, profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

//            bundle.putString("idFacebook", id);

            if (object.has("first_name"))
                bundle.putString(FIRST_NAME, object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString(LAST_NAME, object.getString("last_name"));
            if (object.has("email"))
                bundle.putString(EMAIL_ID, object.getString("email"));
            if (object.has("gender"))
                bundle.putString(GENDER, object.getString("gender"));
           /* if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));*/


        } catch (Exception e) {
            e.printStackTrace();
        }
        return bundle;
    }

    //</editor-fold>

    //<editor-fold desc="Google Registration">

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @SuppressWarnings("unused")
    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
//                        updateUI(false);
                        Log.e("onResult: Sign Out!", status.getStatusMessage());
                    }
                });
    }

    @SuppressWarnings("unused")
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResult(@NonNull Status status) {
//                        updateUI(false);
                        Log.e("onResult: Revoke Access!", status.getStatusMessage());
                    }
                });
    }

    private class RetrieveTokenTask extends AsyncTask<GoogleSignInResult, Void, String> {

        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.showProgressDialog(ProfileRegistrationActivity.this, "Please wait...", true);
        }

        @Override
        protected String doInBackground(GoogleSignInResult... params) {

            if (params[0].isSuccess()) {
                // Signed in successfully.
                GoogleSignInAccount acct = params[0].getSignInAccount();

                if (acct != null) {


                    String scopes = "oauth2:profile email";
                    try {
                        token = GoogleAuthUtil.getToken(getApplicationContext(), acct.getEmail(), scopes);
                    } catch (IOException | GoogleAuthException e) {
                        System.out.println("RContacts token error --> " + e.getMessage());
                    }

//                    firstName = StringUtils.defaultString(acct.getGivenName());
//                    lastName = StringUtils.defaultString(acct.getFamilyName());
//                    email = StringUtils.defaultString(acct.getEmail());

                }

            } else {
                // Signed out.
                Utils.showErrorSnackBar(ProfileRegistrationActivity.this,
                        relativeRootProfileRegistration, getString(R.string.error_retrieving_details));
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Utils.hideProgressDialog();

//            inputFirstName.setText(firstName);
//            inputLastName.setText(lastName);
//            inputEmailId.setText(email);

//            inputEmailId.setEnabled(false);

            profileRegistrationViaSocial(token, "google");
        }
    }

//    private void handleSignInResult(GoogleSignInResult result) {
//        Log.i("Sign In Result", "handleSignInResult:" + result.isSuccess());
//        if (result.isSuccess()) {
//            // Signed in successfully.
//            GoogleSignInAccount acct = result.getSignInAccount();
//
//            if (acct != null) {
//
//
//                String scopes = "oauth2:profile email";
//                String token = null;
//                try {
//                    token = GoogleAuthUtil.getToken(getApplicationContext(), acct.getEmail(), scopes);
//                } catch (IOException | GoogleAuthException e) {
//                    System.out.println("RContacts token error --> " + e.getMessage());
//                }
//
//                social_id = token;
//                social_id = acct.getId();
//                firstName = StringUtils.defaultString(acct.getGivenName());
//                lastName = StringUtils.defaultString(acct.getFamilyName());
//                email = StringUtils.defaultString(acct.getEmail());
//
//                inputFirstName.setText(firstName);
//                inputLastName.setText(lastName);
//                inputEmailId.setText(email);
//
//                inputEmailId.setEnabled(false);
//
//                profileRegistrationViaSocial(social_id, "google");
//
//            }
//
//        } else {
//            // Signed out.
//            Utils.showErrorSnackBar(ProfileRegistrationActivity.this,
//                    relativeRootProfileRegistration, getString(R.string.error_retrieving_details));
//        }
//    }

    //</editor-fold>

    //<editor-fold desc="Linked In Registration">

    /**
     * This method is used to make permissions to retrieve data from linkedin
     */
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public void linkedInSignIn() {

        Intent intent = new Intent(ProfileRegistrationActivity.this, LinkedinLoginActivity.class);
        startActivityForResult(intent, RC_LINKEDIN_SIGN_IN);// Activity is started with requestCode

//        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new
//                AuthListener() {
//                    @Override
//                    public void onAuthSuccess() {
//                        getUserData();
//                    }
//
//                    @Override
//                    public void onAuthError(LIAuthError error) {
//                        Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast
//                                .LENGTH_LONG).show();
//                    }
//                }, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setIntegerPreference(ProfileRegistrationActivity.this, AppConstants
                .PREF_LOGIN_TYPE, 0);
    }

    public void getUserData() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        String host = "api.linkedin.com";
        String topCardUrl = "https://" + host + "/v1/people/~:(id,email-address," +
                "first-name,last-name,phone-numbers,picture-url,picture-urls::(original))";
        apiHelper.getRequest(ProfileRegistrationActivity.this, topCardUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {

//                    JSONObject response = result.getResponseDataAsJson();

//                    firstName = response.get("firstName").toString();
//                    lastName = response.get("lastName").toString();
//                    email = response.get("emailAddress").toString();

//                    inputFirstName.setText(firstName);
//                    inputLastName.setText(lastName);
//                    inputEmailId.setText(email);
//
//                    inputEmailId.setEnabled(false);

                    profileRegistrationViaSocial(LISessionManager.getInstance(getApplicationContext()).getSession()
                            .getAccessToken().getValue(), "linkedin");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError error) {
                Log.e("onApiError: ", error.toString());
            }
        });
    }
    //</editor-fold>
}
