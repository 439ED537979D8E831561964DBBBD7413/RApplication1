package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileRegistrationActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

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

    // Facebook Callback Manager
    CallbackManager callbackManager;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_registration);
        ButterKnife.bind(this);

        /*// Facebook Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();*/

        userProfile = (UserProfile) Utils.getObjectPreference(ProfileRegistrationActivity.this,
                AppConstants.PREF_REGS_USER_OBJECT, UserProfile.class);
        if (userProfile == null) {
            userProfile = new UserProfile();
        }

//        registerFacebookCallback();

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                    profileRegistration(firstName, lastName, emailId,
                            getResources().getInteger(R.integer.registration_via_email));
                }
                break;
            //</editor-fold>

            //<editor-fold desc="button_facebook">
            case R.id.ripple_facebook:

                // Facebook Initialization
                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();

                // Callback registration
                registerFacebookCallback();

                LoginManager.getInstance().logInWithReadPermissions(ProfileRegistrationActivity
                        .this, Arrays.asList("public_profile", "email"));
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
                if (userProfileResponse.getStatus().equalsIgnoreCase(WsConstants
                        .RESPONSE_STATUS_TRUE)) {

                   /* Utils.showSuccessSnackbar(this, relativeRootProfileRegistration,
                            userProfileResponse.getMessage());*/

                    // set launch screen as MainActivity
                    Utils.setIntegerPreference(ProfileRegistrationActivity.this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, getResources().getInteger(R
                                    .integer.launch_main_activity));

                    // Redirect to MainActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                } else {
                    Log.e("error response", userProfileResponse.getMessage());
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

        inputFirstName.setText(userProfile.getFirstName());
        inputLastName.setText(userProfile.getLastName());
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

    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void profileRegistration(String firstName, String lastName, String emailId, int type) {

        WsRequestObject profileRegistrationObject = new WsRequestObject();
        profileRegistrationObject.setFirstName(firstName);
        profileRegistrationObject.setLastName(lastName);
        profileRegistrationObject.setEmailId(emailId);
        profileRegistrationObject.setPmId(userProfile.getPmId());
        profileRegistrationObject.setType(String.valueOf(type));
        profileRegistrationObject.setDeviceId(AppConstants.DEVICE_TOKEN_ID);

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
                    public void onSuccess(LoginResult loginResult) {

                        Log.e("facebook access token: ", loginResult.getAccessToken().getToken());

                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult
                                .getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse
                                    graphResponse) {
                                Bundle facebookData = getFacebookData(jsonObject);

                                if (facebookData != null) {

                                    profileRegistration(facebookData.getString(FIRST_NAME),
                                            facebookData.getString(LAST_NAME), facebookData
                                                    .getString(EMAIL_ID), getResources()
                                                    .getInteger(R.integer
                                                            .registration_via_facebook));

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
}
