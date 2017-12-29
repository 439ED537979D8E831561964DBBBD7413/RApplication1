package com.rawalinfocom.rcontact;

import android.*;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.common.base.MoreObjects;
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
import com.rawalinfocom.rcontact.model.Country;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReLoginEnterPasswordActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.text_number)
    TextView textNumber;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_set_password_logo)
    ImageView imageSetPasswordLogo;
    @BindView(R.id.text_password_protected)
    TextView textPasswordProtected;
    @BindView(R.id.text_msg_enter_password)
    TextView textMsgEnterPassword;
    @BindView(R.id.input_enter_password)
    EditText inputEnterPassword;
    @BindView(R.id.linear_layout_edit_box)
    LinearLayout linearLayoutEditBox;
    @BindView(R.id.button_login)
    Button buttonLogin;
    @BindView(R.id.ripple_login)
    RippleView rippleLogin;
    @BindView(R.id.button_forgot_password)
    Button buttonForgotPassword;
    @BindView(R.id.ripple_forget_password)
    RippleView rippleForgetPassword;
    @BindView(R.id.relativeRootEnterPassword)
    RelativeLayout relativeRootEnterPassword;
    @BindView(R.id.text_sign_in_up_diff_account)
    TextView textSignInUpDiffAccount;

    // Social Login
    @BindView(R.id.linear_layout_login)
    LinearLayout linearLayoutLogin;
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
    @BindView(R.id.linear_layout_social_login)
    LinearLayout linearLayoutSocialLogin;
    CallbackManager callbackManager;
    // Google API Client
    private GoogleApiClient googleApiClient;

    private static final int FACEBOOK_LOGIN_PERMISSION = 21;
    private static final int GOOGLE_LOGIN_PERMISSION = 22;
    private static final int LINKEDIN_LOGIN_PERMISSION = 23;
    private final int RC_SIGN_IN = 7;
    private final int RC_LINKEDIN_SIGN_IN = 8;

    private String[] requiredPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest
            .permission.WRITE_EXTERNAL_STORAGE};


    private String mobileNumber, isFrom = "";
    private Country selectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_enter_password);
        ButterKnife.bind(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        // Google+ Registration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi
                (Auth.GOOGLE_SIGN_IN_API, gso).build();

        init();
    }

    private void init() {

        rippleForgetPassword.setOnRippleCompleteListener(this);
        rippleLogin.setOnRippleCompleteListener(this);

        if (Utils.getStringPreference(this, AppConstants.EXTRA_LOGIN_TYPE, "").equalsIgnoreCase("password"))
            rippleForgetPassword.setVisibility(View.VISIBLE);
        else
            rippleForgetPassword.setVisibility(View.GONE);

        rippleFacebook.setOnRippleCompleteListener(this);
        rippleGoogle.setOnRippleCompleteListener(this);
        rippleLinkedIn.setOnRippleCompleteListener(this);

        buttonFacebook.setTypeface(Utils.typefaceSemiBold(this));
        buttonGoogle.setTypeface(Utils.typefaceSemiBold(this));
        buttonLinkedIn.setTypeface(Utils.typefaceSemiBold(this));

        Utils.setRoundedCornerBackground(buttonFacebook, ContextCompat.getColor
                (this, R.color.colorFacebookBlue), 5, 0, ContextCompat
                .getColor(this, R.color.colorFacebookBlue));

        Utils.setRoundedCornerBackground(buttonGoogle, ContextCompat.getColor
                (this, R.color.colorGoogleRed), 5, 0, ContextCompat
                .getColor(this, R.color.colorGoogleRed));

        Utils.setRoundedCornerBackground(buttonLinkedIn, ContextCompat.getColor
                (this, R.color.colorLinkedInBlue), 5, 0, ContextCompat
                .getColor(this, R.color.colorLinkedInBlue));


        textSignInUpDiffAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.clearData(ReLoginEnterPasswordActivity.this);
                getDatabaseHandler().clearAllData();

                Utils.setIntegerPreference(ReLoginEnterPasswordActivity.this, AppConstants
                        .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_MOBILE_REGISTRATION);

                Intent intent = new Intent(ReLoginEnterPasswordActivity.this,
                        MobileNumberRegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));
        textPasswordProtected.setTypeface(Utils.typefaceRegular(this));
        textMsgEnterPassword.setTypeface(Utils.typefaceRegular(this));
        inputEnterPassword.setTypeface(Utils.typefaceRegular(this));
        buttonLogin.setTypeface(Utils.typefaceRegular(this));
        buttonForgotPassword.setTypeface(Utils.typefaceRegular(this));

        mobileNumber = Utils.getStringPreference(ReLoginEnterPasswordActivity.this, AppConstants
                .PREF_REGS_MOBILE_NUMBER, "");
        selectedCountry = (Country) Utils.getObjectPreference(ReLoginEnterPasswordActivity.this,
                AppConstants.PREF_SELECTED_COUNTRY_OBJECT, Country.class);

        if (selectedCountry != null) {
            textNumber.setText(mobileNumber);
        }

        isFrom = getIntent().getStringExtra(AppConstants.EXTRA_IS_FROM);

        if (isFrom.equals(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD) || isFrom.equals(AppConstants
                .EXTRA_IS_FROM_RE_LOGIN)) {
            textToolbarTitle.setText(getResources().getString(R.string.password_verification));
        } else {
            textToolbarTitle.setText(getResources().getString(R.string.str_enter_password));
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            //<editor-fold desc="REQ_SEND_OTP">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_CHECK_NUMBER)) {
                WsResponseObject otpDetailResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (otpDetailResponse != null && StringUtils.equalsIgnoreCase(otpDetailResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    // set launch screen as OtpVerificationActivity
                    Utils.setIntegerPreference(ReLoginEnterPasswordActivity.this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                    .LAUNCH_MOBILE_REGISTRATION);

                    // Redirect to OtpVerificationActivity
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_IS_FROM, AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD);
                    startActivityIntent(ReLoginEnterPasswordActivity.this,
                            OtpVerificationActivity.class, bundle);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                } else {
                    if (otpDetailResponse != null) {
                        Log.e("error response", otpDetailResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword,
                                otpDetailResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_CHECK_LOGIN">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_CHECK_LOGIN)) {
                WsResponseObject enterPassWordResponse = (WsResponseObject) data;

                if (enterPassWordResponse != null && StringUtils.equalsIgnoreCase
                        (enterPassWordResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    // set launch screen as MainActivity
                    Utils.setIntegerPreference(this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                    .LAUNCH_MAIN_ACTIVITY);

                    ProfileDataOperation profileDetail = enterPassWordResponse.getProfileDetail();

                    setProfileData(profileDetail);
                    Utils.storeProfileDataToDb(ReLoginEnterPasswordActivity.this, profileDetail, databaseHandler);

//                    if (MoreObjects.firstNonNull(enterPassWordResponse.getReSync(), 0).equals(1)) {
//                        Utils.setBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false);
//                        Utils.setBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, false);
//                        Utils.setBooleanPreference(this, AppConstants.PREF_SMS_SYNCED, false);
//                    }

                    Utils.setStringPreference(this, AppConstants.EXTRA_LOGIN_TYPE, "password");

                    // Redirect to MainActivity
                    if (isFrom.equals(AppConstants.EXTRA_IS_FROM_RE_LOGIN)) {
                        Utils.hideProgressDialog();
                        Utils.setBooleanPreference(ReLoginEnterPasswordActivity.this,
                                AppConstants.PREF_TEMP_LOGOUT, false);
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    } else {
                        deviceDetail();
                    }

                } else {

                    Utils.hideProgressDialog();

                    if (enterPassWordResponse != null) {
                        Log.e("error response", enterPassWordResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword,
                                enterPassWordResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "enterPassWordResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_STORE_DEVICE_DETAILS">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_STORE_DEVICE_DETAILS)) {
                WsResponseObject enterPassWordResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (enterPassWordResponse != null && StringUtils.equalsIgnoreCase
                        (enterPassWordResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.setBooleanPreference(this, AppConstants.PREF_IS_LOGIN, true);
                    Utils.setBooleanPreference(this, AppConstants.KEY_IS_RESTORE_DONE, true);
                    Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String
                            .valueOf(System.currentTimeMillis()));
                    Utils.setContactArrayListPreference(RContactApplication.getInstance(), AppConstants.PREF_ALL_CONTACT,
                            new ArrayList());

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();

//                    if (isFrom.equals(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD)) {
//                        if (Utils.getBooleanPreference(this, AppConstants.KEY_IS_RESTORE_DONE,
//                                false)) {
//                            // Redirect to MainActivity
//                            Utils.setBooleanPreference(this, AppConstants.KEY_IS_RESTORE_DONE,
//                                    true);
//                            Utils.setBooleanPreference(this, AppConstants.PREF_IS_LOGIN, true);
//                            Intent intent = new Intent(this, MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.enter, R.anim.exit);
//                            finish();
//                        } else {
//                            // Redirect to RestorationActivity
//                            Intent intent = new Intent(this, RestorationActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.enter, R.anim.exit);
//                            finish();
//                        }
//                    } else {
//
//                        Utils.setBooleanPreference(this, AppConstants.KEY_IS_RESTORE_DONE, true);
//
//                        Intent intent = new Intent(this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                        finish();
//                    }

                } else {

                    if (enterPassWordResponse != null) {
                        Log.e("error response", enterPassWordResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword,
                                enterPassWordResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "enterPassWordResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_REGISTER_WITH_SOCIAL_MEDIA">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_LOGIN_WITH_SOCIAL_MEDIA)) {
                WsResponseObject userProfileResponse = (WsResponseObject) data;

                if (userProfileResponse != null && StringUtils.equalsIgnoreCase(userProfileResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    if (IntegerConstants.REGISTRATION_VIA == IntegerConstants.REGISTRATION_VIA_FACEBOOK)
                        LoginManager.getInstance().logOut();

                    ProfileDataOperation profileDetail = userProfileResponse.getProfileDetail();

                    setProfileData(profileDetail);
                    Utils.storeProfileDataToDb(this, profileDetail, databaseHandler);

                    Utils.setStringPreference(this, AppConstants.EXTRA_LOGIN_TYPE, "social");

                    deviceDetail();

                } else {

                    Utils.hideProgressDialog();

                    if (userProfileResponse != null) {
                        Log.e("error response", userProfileResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword,
                                userProfileResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "userProfileResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootEnterPassword, "" + error
                    .getLocalizedMessage());
        }
    }

    private void setProfileData(ProfileDataOperation profileDetail) {

        // set launch screen as MainActivity
        Utils.setIntegerPreference(this,
                AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                        .LAUNCH_MAIN_ACTIVITY);

        Long date_firstLaunch = System.currentTimeMillis();
        Utils.setLongPreference(this, AppConstants.PREF_RATE_APP_DATE, date_firstLaunch);

        Utils.setObjectPreference(this, AppConstants
                .PREF_REGS_USER_OBJECT, profileDetail);

        Utils.setStringPreference(this, AppConstants.PREF_USER_PM_ID,
                profileDetail.getRcpPmId());

        Utils.setStringPreference(this, AppConstants.PREF_USER_NAME,
                profileDetail.getPbNameFirst() + " " + profileDetail
                        .getPbNameLast());

        Utils.setStringPreference(this, AppConstants.PREF_CALL_LOG_SYNC_TIME,
                profileDetail.getCallLogTimestamp());
        Utils.setStringPreference(this, AppConstants.PREF_SMS_SYNC_TIME,
                profileDetail.getSmsLogTimestamp());
        Utils.setStringPreference(this, AppConstants.PREF_CALL_LOG_ROW_ID,
                profileDetail.getCallLogId());

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

        Utils.setBooleanPreference(this, AppConstants
                .PREF_DISABLE_PUSH, false);
        Utils.setBooleanPreference(this, AppConstants
                .PREF_DISABLE_EVENT_PUSH, false);
        Utils.setBooleanPreference(this, AppConstants
                .PREF_DISABLE_POPUP, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (IntegerConstants.REGISTRATION_VIA == IntegerConstants.REGISTRATION_VIA_FACEBOOK) {
            // Facebook Callback
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
//        else if (IntegerConstants.REGISTRATION_VIA == IntegerConstants
//                .REGISTRATION_VIA_LINED_IN) {
//            // LinkedIn Callback
//            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,
//                    requestCode, resultCode, data);
//        }

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
                    profileLoginViaSocial(data.getStringExtra("accessToken"), "linkedin");
                } else {
                    Utils.showErrorSnackBar(this, relativeRootEnterPassword, "Login cancelled!");
                }
            } else {
                Utils.showErrorSnackBar(this, relativeRootEnterPassword, "Login cancelled!");
            }
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
                if (permissions[0].equals(android.Manifest.permission
                        .READ_EXTERNAL_STORAGE) && permissions[1].equals(android.Manifest.permission
                        .WRITE_EXTERNAL_STORAGE)) {
                    prepareToLoginUsingSocialMedia(requestCode);
                }
                break;
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.ripple_login:

                String password = inputEnterPassword.getText().toString();
                if (StringUtils.isEmpty(password)) {
                    Utils.showErrorSnackBar(this, relativeRootEnterPassword,
                            getResources().getString(R.string.err_msg_please_enter_password));
                } else {
                    checkLogin(password);
                }

                break;

            case R.id.ripple_forget_password:
                sendOtp();
                break;

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

                    LoginManager.getInstance().logInWithReadPermissions(ReLoginEnterPasswordActivity
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
                (ReLoginEnterPasswordActivity.this, permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission
                (ReLoginEnterPasswordActivity.this, permissions[1]) !=
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

                LoginManager.getInstance().logInWithReadPermissions(ReLoginEnterPasswordActivity.this,
                        Arrays.asList(getString(R.string.str_public_profile),
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
                                profileLoginViaSocial(loginResult.getAccessToken().getToken(), "facebook");
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
                        Utils.showErrorSnackBar(ReLoginEnterPasswordActivity.this,
                                relativeRootEnterPassword, getString(R.string
                                        .error_facebook_login_cancelled));
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Utils.showErrorSnackBar(ReLoginEnterPasswordActivity.this,
                                relativeRootEnterPassword, exception.getMessage());
                    }
                });
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private class RetrieveTokenTask extends AsyncTask<GoogleSignInResult, Void, String> {

        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.showProgressDialog(ReLoginEnterPasswordActivity.this, "Please wait...", true);
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
                }

            } else {
                // Signed out.
                Utils.showErrorSnackBar(ReLoginEnterPasswordActivity.this,
                        relativeRootEnterPassword, getString(R.string.error_retrieving_details));
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Utils.hideProgressDialog();

            profileLoginViaSocial(token, "google");
        }
    }

    /**
     * This method is used to make permissions to retrieve data from linkedin
     */
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public void linkedInSignIn() {

        Intent intent = new Intent(ReLoginEnterPasswordActivity.this, LinkedinLoginActivity.class);
        intent.putExtra("from", "login");
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

    public void getUserData() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        String host = "api.linkedin.com";
        String topCardUrl = "https://" + host + "/v1/people/~:(id,email-address," +
                "first-name,last-name,phone-numbers,picture-url,picture-urls::(original))";
        apiHelper.getRequest(ReLoginEnterPasswordActivity.this, topCardUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {

                    profileLoginViaSocial(LISessionManager.getInstance(getApplicationContext()).getSession()
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

    private void sendOtp() {

        WsRequestObject otpObject = new WsRequestObject();
        if (selectedCountry != null)
            otpObject.setCountryCode(selectedCountry.getCountryCodeNumber());
        else otpObject.setCountryCode("+91");
        otpObject.setMobileNumber(mobileNumber.replace("+91", ""));
        otpObject.setForgotPassword(1);
        otpObject.setDeviceId(getDeviceId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_CHECK_NUMBER, getString(R.string
                    .msg_please_wait), false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_CHECK_NUMBER);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEnterPassword, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void checkLogin(String password) {

        WsRequestObject reLoginObject = new WsRequestObject();
        reLoginObject.setMobileNumber(mobileNumber.replace("+", ""));
        reLoginObject.setPassword(password);
        if (isFrom.equals(AppConstants.EXTRA_IS_FROM_RE_LOGIN) || Utils.getBooleanPreference
                (ReLoginEnterPasswordActivity.this,
                        AppConstants.PREF_TEMP_LOGOUT, false)) {
            reLoginObject.setReAuthenticate(1);
        }
        reLoginObject.setCreatedBy("2"); // For Android Devices
        reLoginObject.setGcmToken(getDeviceTokenId());
//        enterPassWordObject.setDeviceId(getDeviceId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    reLoginObject,
                    null, WsResponseObject.class, WsConstants.REQ_CHECK_LOGIN, getString(R.string
                    .msg_please_wait), false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_CHECK_LOGIN);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEnterPassword, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //<editor-fold desc="Web Service Call">
    private void profileLoginViaSocial(String accessToken, String socialMedia) {

        WsRequestObject reLoginObject = new WsRequestObject();
        reLoginObject.setMobileNumber(mobileNumber.replace("+", ""));
        reLoginObject.setAccessToken(StringUtils.trimToEmpty(accessToken));
        if (isFrom.equals(AppConstants.EXTRA_IS_FROM_RE_LOGIN) || Utils.getBooleanPreference
                (ReLoginEnterPasswordActivity.this,
                        AppConstants.PREF_TEMP_LOGOUT, false)) {
            reLoginObject.setReAuthenticate(1);
        }
        reLoginObject.setSocialMedia(socialMedia);
        reLoginObject.setCreatedBy("2"); // For Android Devices
        reLoginObject.setGcmToken(getDeviceTokenId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    reLoginObject, null, WsResponseObject.class, WsConstants
                    .REQ_LOGIN_WITH_SOCIAL_MEDIA, getString(R.string.msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            BuildConfig.WS_ROOT_V2 + WsConstants.REQ_LOGIN_WITH_SOCIAL_MEDIA);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEnterPassword, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void deviceDetail() {

        String model = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String brand = Build.BRAND;
        String device = Build.DEVICE;
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
                    BuildConfig.WS_ROOT + WsConstants.REQ_STORE_DEVICE_DETAILS);
        }
        /*else {
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getResources()
                    .getString(R.string.msg_no_network));
        }*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
