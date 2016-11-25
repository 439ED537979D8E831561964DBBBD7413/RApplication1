package com.rawalinfocom.rcontact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
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
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.FileUtils;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileRegistrationActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener, GoogleApiClient.OnConnectionFailedListener {

    private final int RC_SIGN_IN = 7;

    private final String host = "api.linkedin.com";
    private final String topCardUrl = "https://" + host + "/v1/people/~:(email-address," +
            "first-name,last-name,phone-numbers,picture-url,picture-urls::(original))";

    final String FIRST_NAME = "first_name";
    final String LAST_NAME = "last_name";
    final String PROFILE_IMAGE = "profile_image";
    final String EMAIL_ID = "email_id";
    final String GENDER = "gender";

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

    // Facebook Callback Manager
    CallbackManager callbackManager;

    // Google API Client
    private GoogleApiClient googleApiClient;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_registration);
        ButterKnife.bind(this);

//        generateHashkey();

        /*// Facebook Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();*/

        userProfile = (UserProfile) Utils.getObjectPreference(ProfileRegistrationActivity.this,
                AppConstants.PREF_REGS_USER_OBJECT, UserProfile.class);
        if (userProfile == null) {
            userProfile = new UserProfile();
        }

//        generateHashkey();

//        registerFacebookCallback();

        // Google+ Registration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi
                (Auth.GOOGLE_SIGN_IN_API, gso).build();


        init();
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

        if (setLoginVia == getResources().getInteger(R.integer.registration_via_facebook)) {
            // Facebook Callback
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (setLoginVia == getResources().getInteger(R.integer.registration_via_lined_in)) {
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

                if (firstName.equalsIgnoreCase("") || lastName.equalsIgnoreCase("")) {
                    Utils.showErrorSnackBar(this, relativeRootProfileRegistration, "Please add " +
                            "First Name and Last Name");
                } else {
                    profileRegistration(firstName, lastName, emailId, null, "",
                            getResources().getInteger(R.integer.registration_via_email));
                }
                break;
            //</editor-fold>

            //<editor-fold desc="ripple_facebook">
            case R.id.ripple_facebook:

                setLoginVia = getResources().getInteger(R.integer.registration_via_facebook);

                // Facebook Initialization
                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();

                // Callback registration
                registerFacebookCallback();

                LoginManager.getInstance().logInWithReadPermissions(ProfileRegistrationActivity
                        .this, Arrays.asList("public_profile", "email"));
                break;
            //</editor-fold>

            //<editor-fold desc="ripple_google">
            case R.id.ripple_google:

                setLoginVia = getResources().getInteger(R.integer.registration_via_google);

                googleSignIn();

                break;
            //</editor-fold>

            // <editor-fold desc="ripple_linked_in">
            case R.id.ripple_linked_in:

                setLoginVia = getResources().getInteger(R.integer.registration_via_lined_in);

                linkedInSignIn();

                break;
            //</editor-fold>
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


                    // set launch screen as MainActivity
                    Utils.setIntegerPreference(ProfileRegistrationActivity.this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, getResources().getInteger(R
                                    .integer.launch_main_activity));

                    TableProfileMaster tableProfileMaster = new TableProfileMaster(this,
                            databaseHandler);
                    if (userProfileRegistered != null) {
                        tableProfileMaster.addProfile(userProfileRegistered);
                    }

                    // Redirect to MainActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                } else {
                    if (userProfileResponse != null) {
                        Log.e("error response", userProfileResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getString(R
                                .string.msg_try_later));
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

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        inputFirstName.setText(userProfile.getPmFirstName());
        inputLastName.setText(userProfile.getPmLastName());
        inputEmailId.setText(userProfile.getEmailId());

        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);

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

        rippleActionBack.setOnRippleCompleteListener(this);
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
                            imageToBase64, loginResult.getAccessToken().getToken(), getResources
                                    ().getInteger(R.integer.registration_via_facebook));
                } else {
                    Log.e("onResourceReady: ", "There is some error in storing Image!");
                    profileRegistration(facebookData.getString(FIRST_NAME), facebookData
                                    .getString(LAST_NAME), facebookData.getString(EMAIL_ID),
                            null, loginResult.getAccessToken().getToken(), getResources
                                    ().getInteger(R.integer.registration_via_facebook));
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
        if (acct.getPhotoUrl() != null) {
            personPhotoUrl = StringUtils.defaultString(acct.getPhotoUrl().toString());
        } else {
            profileRegistration(firstName, lastName, email, null, null,
                    getResources().getInteger(R.integer.registration_via_google));
        }

        SimpleTarget googleTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                FileUtils fileUtils = new FileUtils(bitmap);
                if (fileUtils.saveImageToDirectory()) {
                    Log.i("file absolute path: ", fileUtils.getrContactDir().getAbsolutePath());
                    String imageToBase64 = Utils.convertBitmapToBase64(BitmapFactory.decodeFile
                            (fileUtils.getrContactDir().getAbsolutePath()));

                    profileRegistration(firstName, lastName, email, imageToBase64, null,
                            getResources().getInteger(R.integer.registration_via_google));
                } else {
                    Log.e("onResourceReady: ", "There is some error in storing Image!");
                    profileRegistration(firstName, lastName, email, null, null,
                            getResources().getInteger(R.integer.registration_via_google));
                }
            }
        };

        Glide.with(this)
                .load(personPhotoUrl)
                .asBitmap()
                .into(googleTarget);

    }

    private void getLinkedInBitmapFromUrl(final JSONObject response) {

        String firstName, lastName, emailAddress, pictureUrl;

        try {
            firstName = response.get("firstName").toString();
            lastName = response.get("lastName").toString();
            emailAddress = response.get("emailAddress").toString();
            pictureUrl = response.get("pictureUrl").toString();
        } catch (JSONException e) {
            firstName = "";
            lastName = "";
            emailAddress = "";
            pictureUrl = "";
            e.printStackTrace();
        }

        final String finalFirstName = firstName;
        final String finalLastName = lastName;
        final String finalEmailAddress = emailAddress;

        SimpleTarget googleTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                FileUtils fileUtils = new FileUtils(bitmap);
                if (fileUtils.saveImageToDirectory()) {
                    Log.i("file absolute path: ", fileUtils.getrContactDir().getAbsolutePath());
                    String imageToBase64 = Utils.convertBitmapToBase64(BitmapFactory.decodeFile
                            (fileUtils.getrContactDir().getAbsolutePath()));

                    profileRegistration(finalFirstName, finalLastName, finalEmailAddress,
                            imageToBase64, null, getResources().getInteger(R.integer
                                    .registration_via_lined_in));

                } else {
                    Log.e("onResourceReady: ", "There is some error in storing Image!");
                    profileRegistration(finalFirstName, finalLastName, finalEmailAddress, null,
                            null, getResources().getInteger(R.integer.registration_via_lined_in));

                }
            }
        };

        Glide.with(this)
                .load(pictureUrl)
                .asBitmap()
                .into(googleTarget);

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
            profileImage, String socialMediaToken, int type) {

        WsRequestObject profileRegistrationObject = new WsRequestObject();
        profileRegistrationObject.setFirstName(firstName);
        profileRegistrationObject.setLastName(lastName);
        profileRegistrationObject.setEmailId(emailId);
        profileRegistrationObject.setPmId(userProfile.getPmId());
        profileRegistrationObject.setProfileImage(profileImage);
        profileRegistrationObject.setType(String.valueOf(type));
        profileRegistrationObject.setSocialMediaTokenId(socialMediaToken);
        profileRegistrationObject.setDeviceId(getDeviceTokenId());

        userProfileRegistered = new UserProfile();
        userProfileRegistered.setPmId(userProfile.getPmId());
        userProfileRegistered.setPmFirstName(profileRegistrationObject.getFirstName());
        userProfileRegistered.setPmLastName(profileRegistrationObject.getLastName());
        userProfileRegistered.setPmSignupSocialMediaType(String.valueOf(type));
        userProfileRegistered.setPmAccessToken(getDeviceTokenId() + "_" + userProfile.getPmId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    profileRegistrationObject, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_REGISTRATION, getString(R.string.msg_please_wait)).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_REGISTRATION);
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
                                                        .getString(EMAIL_ID), null, loginResult
                                                        .getAccessToken().getToken(),
                                                getResources().getInteger(R.integer
                                                        .registration_via_facebook));
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

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id +
                        "/picture?width=200&height=150");
                bundle.putString(PROFILE_IMAGE, profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);

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
