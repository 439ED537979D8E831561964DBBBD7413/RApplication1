package com.rawalinfocom.rcontact;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.rawalinfocom.rcontact.asynctasks.AsyncReverseGeoCoding;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableWebsiteMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.FileUtils;
import com.rawalinfocom.rcontact.helper.GPSTracker;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Address;
import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.ImAccount;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.ReverseGeocodingAddress;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.Website;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileRegistrationActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener, GoogleApiClient.OnConnectionFailedListener {

    private final int RC_SIGN_IN = 7;

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
    @BindView(R.id.button_register)
    Button buttonRegister;
    @BindView(R.id.ripple_register)
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

    int setLoginVia = 0;
    int locationCall = 0;
    String locationString;

    public static boolean isFromSettings;

    GPSTracker gpsTracker;

    // Facebook Callback Manager
    CallbackManager callbackManager;

    // Google API Client
    private GoogleApiClient googleApiClient;
    private double latitude, longitude;

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

        init();

        gpsTracker = new GPSTracker(this, null);

       /* if (ContextCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission
                    .ACCESS_FINE_LOCATION}, AppConstants
                    .MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            if (Utils.isLocationEnabled(this)) {
               *//* latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                getCityName();*//*
                getLocationDetail();
            } else {
                gpsTracker.showSettingsAlert();
            }
        } */
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Utils.isLocationEnabled(this)) {
                       /* latitude = gpsTracker.getLatitude();
                        longitude = gpsTracker.getLongitude();
                        getCityName();*/
                getLocationDetail();
            } else {
                gpsTracker.showSettingsAlert();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFromSettings) {
            isFromSettings = false;
            if (Utils.isLocationEnabled(this)) {
               /* gpsTracker = new GPSTracker(this, null);
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                getCityName();*/
                getLocationDetail();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       /* OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn
                (googleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("On Start", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (setLoginVia == IntegerConstants.REGISTRATION_VIA_FACEBOOK) {
            // Facebook Callback
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (setLoginVia == IntegerConstants.REGISTRATION_VIA_LINED_IN) {
            // LinkedIn Callback
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,
                    requestCode, resultCode, data);
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
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

            //<editor-fold desc="ripple_register">
            case R.id.ripple_register:
//                verifyOtp();
                String firstName = inputFirstName.getText().toString();
                String lastName = inputLastName.getText().toString();
                String emailId = inputEmailId.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (firstName.equalsIgnoreCase("") || lastName.equalsIgnoreCase("")) {
                    Utils.showErrorSnackBar(this, relativeRootProfileRegistration, "Please add " +
                            "First Name and Last Name");
                } else if (emailId.length() > 0 && !emailId.matches(emailPattern)) {
                    Utils.showErrorSnackBar(this, relativeRootProfileRegistration, "Please enter " +
                            "valid email");
                } else {
                    profileRegistration(firstName, lastName, emailId, null, "", "",
                            IntegerConstants.REGISTRATION_VIA_EMAIL);
                }
                break;
            //</editor-fold>

            //<editor-fold desc="ripple_facebook">
            case R.id.ripple_facebook:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute(requiredPermissions, FACEBOOK_LOGIN_PERMISSION);
                } else {
                    setLoginVia = IntegerConstants.REGISTRATION_VIA_FACEBOOK;

                    // Facebook Initialization
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    callbackManager = CallbackManager.Factory.create();

                    // Callback registration
                    registerFacebookCallback();

                    LoginManager.getInstance().logInWithReadPermissions(ProfileRegistrationActivity
                            .this, Arrays.asList("public_profile", "email"));

                }
                break;
            //</editor-fold>

            //<editor-fold desc="ripple_google">
            case R.id.ripple_google:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute(requiredPermissions, GOOGLE_LOGIN_PERMISSION);
                } else {
                    setLoginVia = IntegerConstants.REGISTRATION_VIA_GOOGLE;
                    googleSignIn();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="ripple_linked_in">
            case R.id.ripple_linked_in:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute(requiredPermissions, LINKEDIN_LOGIN_PERMISSION);
                } else {
                    setLoginVia = IntegerConstants.REGISTRATION_VIA_LINED_IN;
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
                setLoginVia = IntegerConstants.REGISTRATION_VIA_FACEBOOK;

                // Facebook Initialization
                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();

                // Callback registration
                registerFacebookCallback();

                LoginManager.getInstance().logInWithReadPermissions(ProfileRegistrationActivity
                        .this, Arrays.asList("public_profile", "email"));
                break;
            case GOOGLE_LOGIN_PERMISSION:
                setLoginVia = IntegerConstants.REGISTRATION_VIA_GOOGLE;
                googleSignIn();
                break;
            case LINKEDIN_LOGIN_PERMISSION:
                setLoginVia = IntegerConstants.REGISTRATION_VIA_LINED_IN;
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
//                Utils.hideProgressDialog();
                if (userProfileResponse != null && StringUtils.equalsIgnoreCase(userProfileResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    // set launch screen as MainActivity
                    Utils.setIntegerPreference(ProfileRegistrationActivity.this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                    .LAUNCH_MAIN_ACTIVITY);

                    if (userProfileRegistered != null) {
                        Utils.setStringPreference(this, AppConstants.PREF_USER_PM_ID,
                                userProfileRegistered.getPmId());
                    }

                    ProfileDataOperation profileDetail = userProfileResponse.getProfileDetail();
                    Utils.setObjectPreference(ProfileRegistrationActivity.this, AppConstants
                            .PREF_REGS_USER_OBJECT, profileDetail);

                    storeProfileDataToDb(profileDetail);

                    deviceDetail();

                    // Redirect to MainActivity
                  /*  Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);*/

                } else {
                    if (userProfileResponse != null) {
                        Log.e("error response", userProfileResponse.getMessage());
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
            else if (serviceType.equalsIgnoreCase(WsConstants.REQ_REVERSE_GEO_CODING_ADDRESS)) {
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
            }
            //</editor-fold>

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
            case AppConstants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission Granted

                    if (Utils.isLocationEnabled(this)) {
                      /*  gpsTracker = new GPSTracker(this, null);
                        latitude = gpsTracker.getLatitude();
                        longitude = gpsTracker.getLongitude();
                        getCityName();
                        Log.i("onCreate", latitude + ":" + longitude);*/
                        getLocationDetail();
                    } else {
                        gpsTracker.showSettingsAlert();
                    }


                } else {

                    // Permission Denied


                }
            }
            break;
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

        textToolbarTitle.setText(R.string.title_profile_registration);

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        inputFirstName.setTypeface(Utils.typefaceRegular(this));
        inputLastName.setTypeface(Utils.typefaceRegular(this));
        inputEmailId.setTypeface(Utils.typefaceRegular(this));
        buttonRegister.setTypeface(Utils.typefaceSemiBold(this));
        buttonFacebook.setTypeface(Utils.typefaceSemiBold(this));
        buttonGoogle.setTypeface(Utils.typefaceSemiBold(this));
        buttonLinkedIn.setTypeface(Utils.typefaceSemiBold(this));
        textOr.setTypeface(Utils.typefaceSemiBold(this));

        rippleRegister.setOnRippleCompleteListener(this);
        rippleFacebook.setOnRippleCompleteListener(this);
        rippleGoogle.setOnRippleCompleteListener(this);
        rippleLinkedIn.setOnRippleCompleteListener(this);

    }

    private void getFacebookBitmapFromUrl(final Bundle facebookData, final LoginResult
            loginResult) {

        SimpleTarget facebookTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                FileUtils fileUtils = new FileUtils(bitmap);
                if (fileUtils.saveImageToDirectory()) {
                    Log.i("file absolute path: ", fileUtils.getrContactDir().getAbsolutePath());
                    String imageToBase64 = Utils.convertBitmapToBase64(BitmapFactory.decodeFile
                            (fileUtils.getrContactDir().getAbsolutePath()));
                    profileRegistration(facebookData.getString(FIRST_NAME), facebookData
                                    .getString(LAST_NAME), facebookData.getString(EMAIL_ID),
                            imageToBase64, facebookData.getString(SOCIAL_ID), loginResult
                                    .getAccessToken().getToken(), IntegerConstants
                                    .REGISTRATION_VIA_FACEBOOK);
                } else {
                    Log.e("onResourceReady: ", "There is some error in storing Image!");
                    profileRegistration(facebookData.getString(FIRST_NAME), facebookData
                                    .getString(LAST_NAME), facebookData.getString(EMAIL_ID),
                            null, facebookData.getString(SOCIAL_ID), loginResult.getAccessToken()
                                    .getToken(), IntegerConstants.REGISTRATION_VIA_FACEBOOK);
                }
            }
        };

        Glide.with(this)
                .load(facebookData.getString(PROFILE_IMAGE))
                .asBitmap()
                .into(facebookTarget);

    }

    private void getGoogleBitmapFromUrl(GoogleSignInAccount acct) {

        String personPhotoUrl = "";
        String givenName = StringUtils.defaultString(acct.getGivenName());
        if (StringUtils.contains(givenName, " ")) {
            String[] names = givenName.split(" ");
            givenName = names[0];
        }
        final String firstName = givenName;
        final String lastName = StringUtils.defaultString(acct.getFamilyName());
        final String email = StringUtils.defaultString(acct.getEmail());
        final String personId = StringUtils.defaultString(acct.getId());
        if (acct.getPhotoUrl() != null) {
            personPhotoUrl = StringUtils.defaultString(acct.getPhotoUrl().toString());
        } else {
            profileRegistration(firstName, lastName, email, null, personId, null,
                    IntegerConstants.REGISTRATION_VIA_GOOGLE);
        }

        SimpleTarget googleTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                FileUtils fileUtils = new FileUtils(bitmap);
                if (fileUtils.saveImageToDirectory()) {
                    Log.i("file absolute path: ", fileUtils.getrContactDir().getAbsolutePath());
                    String imageToBase64 = Utils.convertBitmapToBase64(BitmapFactory.decodeFile
                            (fileUtils.getrContactDir().getAbsolutePath()));

                    profileRegistration(firstName, lastName, email, imageToBase64, personId, null,
                            IntegerConstants.REGISTRATION_VIA_GOOGLE);
                } else {
                    Log.e("onResourceReady: ", "There is some error in storing Image!");
                    profileRegistration(firstName, lastName, email, null, personId, null,
                            IntegerConstants.REGISTRATION_VIA_GOOGLE);
                }
            }
        };

        Glide.with(this)
                .load(personPhotoUrl)
                .asBitmap()
                .into(googleTarget);

    }

    private void getLinkedInBitmapFromUrl(final JSONObject response) {

        String id, firstName, lastName, emailAddress, pictureUrl;

        try {
            id = response.get("id").toString();
            firstName = response.get("firstName").toString();
            lastName = response.get("lastName").toString();
            emailAddress = response.get("emailAddress").toString();
            pictureUrl = response.get("pictureUrl").toString();
        } catch (JSONException e) {
            id = "";
            firstName = "";
            lastName = "";
            emailAddress = "";
            pictureUrl = "";
            e.printStackTrace();
        }

        final String finalFirstName = firstName;
        final String finalLastName = lastName;
        final String finalEmailAddress = emailAddress;
        final String finalId = id;

        SimpleTarget googleTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                FileUtils fileUtils = new FileUtils(bitmap);
                if (fileUtils.saveImageToDirectory()) {
                    Log.i("file absolute path: ", fileUtils.getrContactDir().getAbsolutePath());
                    String imageToBase64 = Utils.convertBitmapToBase64(BitmapFactory.decodeFile
                            (fileUtils.getrContactDir().getAbsolutePath()));

                    // TODO: 09/02/17 social id
                    profileRegistration(finalFirstName, finalLastName, finalEmailAddress,
                            imageToBase64, finalId, null, IntegerConstants
                                    .REGISTRATION_VIA_LINED_IN);

                } else {

                    // TODO: 09/02/17 social id
                    Log.e("onResourceReady: ", "There is some error in storing Image!");
                    profileRegistration(finalFirstName, finalLastName, finalEmailAddress, null,
                            finalId, null, IntegerConstants.REGISTRATION_VIA_LINED_IN);

                }
            }
        };

        Glide.with(this)
                .load(pictureUrl)
                .asBitmap()
                .into(googleTarget);

    }

    private void storeProfileDataToDb(ProfileDataOperation profileDetail) {

        //<editor-fold desc="Basic Details">
        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

        UserProfile userProfile = new UserProfile();
        userProfile.setPmRcpId(userProfileRegistered.getPmId());
        userProfile.setPmPrefix(profileDetail.getPbNamePrefix());
        userProfile.setPmFirstName(profileDetail.getPbNameFirst());
        userProfile.setPmMiddleName(profileDetail.getPbNameMiddle());
        userProfile.setPmLastName(profileDetail.getPbNameLast());
        userProfile.setPmSuffix(profileDetail.getPbNameSuffix());
        userProfile.setPmNickName(profileDetail.getPbNickname());
        userProfile.setPmPhoneticFirstName(profileDetail.getPbPhoneticNameFirst());
        userProfile.setPmPhoneticMiddleName(profileDetail.getPbPhoneticNameMiddle());
        userProfile.setPmPhoneticLastName(profileDetail.getPbPhoneticNameLast());
        userProfile.setPmProfileImage(profileDetail.getPbProfilePhoto());
        userProfile.setPmNotes(profileDetail.getPbNote());
        userProfile.setProfileRating(profileDetail.getProfileRating());
        userProfile.setTotalProfileRateUser(profileDetail.getTotalProfileRateUser());
        userProfile.setPmIsFavourite(profileDetail.getIsFavourite());
        userProfile.setPmNosqlMasterId(profileDetail.getNoSqlMasterId());
        userProfile.setPmJoiningDate(profileDetail.getJoiningDate());
        userProfile.setPmGender(profileDetail.getPbGender());

        tableProfileMaster.addProfile(userProfile);
        //</editor-fold>

        //<editor-fold desc="Mobile Number">
        TableMobileMaster tableMobileMaster = new TableMobileMaster(databaseHandler);

        ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber =
                profileDetail.getPbPhoneNumber();
        if (!Utils.isArraylistNullOrEmpty(arrayListPhoneNumber)) {
            for (int i = 0; i < arrayListPhoneNumber.size(); i++) {
                MobileNumber mobileNumber = new MobileNumber();
                mobileNumber.setMnmRecordIndexId(arrayListPhoneNumber.get(i)
                        .getPhoneId());
                mobileNumber.setMnmNumberType(arrayListPhoneNumber.get(i)
                        .getPhoneType());
                mobileNumber.setMnmMobileNumber(arrayListPhoneNumber.get(i)
                        .getPhoneNumber());
                mobileNumber.setMnmNumberPrivacy(String.valueOf(arrayListPhoneNumber
                        .get(i).getPhonePublic()));
                mobileNumber.setMnmIsPrimary(arrayListPhoneNumber.get(i).getPbRcpType());
                mobileNumber.setRcProfileMasterPmId(userProfileRegistered.getPmId());
                arrayListMobileNumber.add(mobileNumber);
            }
            tableMobileMaster.addArrayMobileNumber(arrayListMobileNumber);
        }
        //</editor-fold>

        //<editor-fold desc="Email Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId())) {
            ArrayList<ProfileDataOperationEmail> arrayListEmailId = profileDetail.getPbEmailId();
            ArrayList<Email> arrayListEmail = new ArrayList<>();
            for (int i = 0; i < arrayListEmailId.size(); i++) {
                Email email = new Email();
                email.setEmRecordIndexId(arrayListEmailId.get(i).getEmId());
                email.setEmEmailAddress(arrayListEmailId.get(i).getEmEmailId());
                email.setEmEmailType(arrayListEmailId.get(i).getEmType());
                email.setEmEmailPrivacy(String.valueOf(arrayListEmailId.get(i).getEmPublic()));
                email.setEmIsVerified(String.valueOf(arrayListEmailId.get(i).getEmRcpType()));
//                email.setEmIsPrimary(String.valueOf(arrayListEmailId.get(i).getEmRcpType()));
                email.setRcProfileMasterPmId(userProfileRegistered.getPmId());
                arrayListEmail.add(email);
            }

            TableEmailMaster tableEmailMaster = new TableEmailMaster(databaseHandler);
            tableEmailMaster.addArrayEmail(arrayListEmail);
        }
        //</editor-fold>

        //<editor-fold desc="Organization Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbOrganization())) {
            ArrayList<ProfileDataOperationOrganization> arrayListOrganization = profileDetail
                    .getPbOrganization();
            ArrayList<Organization> organizationList = new ArrayList<>();
            for (int i = 0; i < arrayListOrganization.size(); i++) {
                Organization organization = new Organization();
                organization.setOmRecordIndexId(arrayListOrganization.get(i).getOrgId());
                organization.setOmOrganizationCompany(arrayListOrganization.get(i).getOrgName
                        ());
               /* organization.setOmOrganizationType(arrayListOrganization.get(i).getOrgType());
                organization.setOmOrganizationTitle(arrayListOrganization.get(i).getOrgName());
                organization.setOmOrganizationDepartment(arrayListOrganization.get(i)
                        .getOrgDepartment());
                organization.setOmJobDescription(arrayListOrganization.get(i)
                        .getOrgJobTitle());
                organization.setOmOfficeLocation(arrayListOrganization.get(i)
                        .getOrgOfficeLocation());*/
                organization.setOmOrganizationDesignation(arrayListOrganization.get(i)
                        .getOrgJobTitle());
                organization.setOmIsCurrent(String.valueOf(arrayListOrganization.get(i)
                        .getIsCurrent()));
                organization.setRcProfileMasterPmId(userProfileRegistered.getPmId());
                organizationList.add(organization);
            }

            TableOrganizationMaster tableOrganizationMaster = new TableOrganizationMaster
                    (databaseHandler);
            tableOrganizationMaster.addArrayOrganization(organizationList);
        }
        //</editor-fold>

        // <editor-fold desc="Website Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress())) {
//            ArrayList<String> arrayListWebsite = profileDetail.getPbWebAddress();
            ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = profileDetail
                    .getPbWebAddress();
            ArrayList<Website> websiteList = new ArrayList<>();
            for (int j = 0; j < arrayListWebsite.size(); j++) {
                Website website = new Website();
                website.setWmRecordIndexId(arrayListWebsite.get(j).getWebId());
                website.setWmWebsiteUrl(arrayListWebsite.get(j).getWebAddress());
                website.setWmWebsiteType(arrayListWebsite.get(j).getWebType());
                website.setRcProfileMasterPmId(userProfileRegistered.getPmId());
                websiteList.add(website);
            }

            TableWebsiteMaster tableWebsiteMaster = new TableWebsiteMaster(databaseHandler);
            tableWebsiteMaster.addArrayWebsite(websiteList);
        }
        //</editor-fold>

        //<editor-fold desc="Address Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress())) {
            ArrayList<ProfileDataOperationAddress> arrayListAddress = profileDetail.getPbAddress();
            ArrayList<Address> addressList = new ArrayList<>();
            for (int j = 0; j < arrayListAddress.size(); j++) {
                Address address = new Address();
                address.setAmRecordIndexId(arrayListAddress.get(j).getAddId());
                address.setAmCity(arrayListAddress.get(j).getCity());
                address.setAmState(arrayListAddress.get(j).getState());
                address.setAmCountry(arrayListAddress.get(j).getCountry());
                address.setAmFormattedAddress(arrayListAddress.get(j).getFormattedAddress());
                address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                address.setAmPostCode(arrayListAddress.get(j).getPostCode());
                address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                address.setAmStreet(arrayListAddress.get(j).getStreet());
                address.setAmAddressType(arrayListAddress.get(j).getAddressType());
                if (arrayListAddress.get(j).getGoogleLatLong() != null && arrayListAddress.get(j)
                        .getGoogleLatLong().size() == 2) {
                    address.setAmGoogleLatitude(arrayListAddress.get(j).getGoogleLatLong().get(1));
                    address.setAmGoogleLongitude(arrayListAddress.get(j).getGoogleLatLong().get(0));
                }
                address.setAmAddressPrivacy(String.valueOf(arrayListAddress.get(j).getAddPublic()));
                address.setAmGoogleAddress(arrayListAddress.get(j).getGoogleAddress());
                address.setRcProfileMasterPmId(userProfileRegistered.getPmId());
                addressList.add(address);
            }

            TableAddressMaster tableAddressMaster = new TableAddressMaster(databaseHandler);
            tableAddressMaster.addArrayAddress(addressList);
        }
        //</editor-fold>

        // <editor-fold desc="Im Account Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts())) {
            ArrayList<ProfileDataOperationImAccount> arrayListImAccount = profileDetail
                    .getPbIMAccounts();
            ArrayList<ImAccount> imAccountsList = new ArrayList<>();
            for (int j = 0; j < arrayListImAccount.size(); j++) {
                ImAccount imAccount = new ImAccount();
                imAccount.setImRecordIndexId(arrayListImAccount.get(j).getIMId());
                imAccount.setImImProtocol(arrayListImAccount.get(j).getIMAccountProtocol());
                imAccount.setImImPrivacy(String.valueOf(arrayListImAccount.get(j)
                        .getIMAccountPublic()));
                imAccount.setImImDetail(arrayListImAccount.get(j).getIMAccountDetails());
//                imAccount.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                imAccount.setRcProfileMasterPmId(userProfileRegistered.getPmId());
                imAccountsList.add(imAccount);
            }

            TableImMaster tableImMaster = new TableImMaster(databaseHandler);
            tableImMaster.addArrayImAccount(imAccountsList);
        }
        //</editor-fold>

        // <editor-fold desc="Event Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent())) {
            ArrayList<ProfileDataOperationEvent> arrayListEvent = profileDetail.getPbEvent();
            ArrayList<Event> eventList = new ArrayList<>();
            for (int j = 0; j < arrayListEvent.size(); j++) {
                Event event = new Event();
                event.setEvmRecordIndexId(arrayListEvent.get(j).getEventId());
                event.setEvmStartDate(arrayListEvent.get(j).getEventDateTime());
                event.setEvmEventType(arrayListEvent.get(j).getEventType());
                event.setEvmEventPrivacy(String.valueOf(arrayListEvent.get(j).getEventPublic()));
//                event.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                event.setRcProfileMasterPmId(userProfileRegistered.getPmId());
                eventList.add(event);
            }

            TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);
            tableEventMaster.addArrayEvent(eventList);
        }
        //</editor-fold>
    }

    private void getLocationDetail() {
        gpsTracker = new GPSTracker(this, null);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        AsyncReverseGeoCoding asyncReverseGeoCoding = new AsyncReverseGeoCoding(this, WsConstants
                .REQ_REVERSE_GEO_CODING_ADDRESS, false);
        asyncReverseGeoCoding.execute(new LatLng(latitude, longitude));

    }

    /**
     * To get key hash
     */
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

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void profileRegistration(String firstName, String lastName, String emailId, String
            profileImage, String socialMediaId, String socialMediaToken, int type) {

        WsRequestObject profileRegistrationObject = new WsRequestObject();
        profileRegistrationObject.setFirstName(firstName);
        profileRegistrationObject.setLastName(lastName);
        profileRegistrationObject.setEmailId(emailId);
        profileRegistrationObject.setPmId(Integer.parseInt(userProfile.getPmId()));
        profileRegistrationObject.setPbSocialId(socialMediaId);
        profileRegistrationObject.setProfileImage(profileImage);
        profileRegistrationObject.setType(String.valueOf(type));
        profileRegistrationObject.setSocialMediaTokenId(socialMediaToken);
        profileRegistrationObject.setDeviceId(getDeviceTokenId());
        profileRegistrationObject.setCreatedBy("2"); // For Android Devices

        userProfileRegistered = new UserProfile();
        userProfileRegistered.setPmId(userProfile.getPmId());
        userProfileRegistered.setPmFirstName(profileRegistrationObject.getFirstName());
        userProfileRegistered.setPmLastName(profileRegistrationObject.getLastName());
        userProfileRegistered.setPmSignupSocialMediaType(String.valueOf(type));
        userProfileRegistered.setPmAccessToken(getDeviceTokenId() + "_" + userProfile.getPmId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    profileRegistrationObject, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_REGISTRATION, getString(R.string.msg_please_wait), true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_REGISTRATION);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getResources()
                    .getString(R.string.msg_no_network));
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
        deviceDetailObject.setDmLocation(StringUtils.defaultString(locationString));

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    deviceDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_STORE_DEVICE_DETAILS, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_STORE_DEVICE_DETAILS);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getResources()
                    .getString(R.string.msg_no_network));
        }
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

                                    if (StringUtils.length(facebookData.getString(PROFILE_IMAGE))
                                            > 0) {
                                        getFacebookBitmapFromUrl(facebookData, loginResult);
                                    } else {
                                        profileRegistration(facebookData.getString(FIRST_NAME),
                                                facebookData.getString(LAST_NAME), facebookData
                                                        .getString(EMAIL_ID), null, facebookData
                                                        .getString(SOCIAL_ID), loginResult
                                                        .getAccessToken().getToken(),
                                                IntegerConstants.REGISTRATION_VIA_FACEBOOK);
                                    }
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
                                relativeRootProfileRegistration, "Facebook Login Cancelled!!");
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

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i("Sign In Result", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct != null) {

             /*   String firstName = StringUtils.defaultString(acct.getGivenName());
                String lastName = StringUtils.defaultString(acct.getFamilyName());
                String personPhotoUrl = StringUtils.defaultString(acct.getPhotoUrl().toString());
                String email = StringUtils.defaultString(acct.getEmail());

                profileRegistration(firstName, lastName, email, null, getResources().getInteger(R
                        .integer.registration_via_google));*/

                getGoogleBitmapFromUrl(acct);

            }


        } else {
            // Signed out.
            Utils.showErrorSnackBar(ProfileRegistrationActivity.this,
                    relativeRootProfileRegistration, "Error in Retrieving Details!");
        }
    }

    //</editor-fold>

    //<editor-fold desc="Linked In Registration">

    /**
     * This method is used to make permissions to retrieve data from linkedin
     */
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public void linkedInSignIn() {
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new
                AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        getUserData();
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast
                                .LENGTH_LONG).show();
                    }
                }, true);
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

                    JSONObject response = result.getResponseDataAsJson();
/*
                    profileRegistration(response.get("firstName").toString(), response.get
                                    ("lastName").toString(), response.get("emailAddress")
                                    .toString(), null,
                            getResources().getInteger(R.integer.registration_via_lined_in));*/
                    getLinkedInBitmapFromUrl(response);
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
