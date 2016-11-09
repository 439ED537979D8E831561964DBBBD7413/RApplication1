package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileRegistrationActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {


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

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_registration);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userProfile = (UserProfile) bundle.getSerializable(AppConstants.EXTRA_OBJECT_USER);

        init();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_register:
//                verifyOtp();
                String firstName = inputFirstName.getText().toString();
                String lastName = inputLastName.getText().toString();
                String emailId = inputEmailId.getText().toString();

                if (firstName.equalsIgnoreCase("") || lastName.equalsIgnoreCase("")) {
                    Utils.showErrorSnackBar(this, relativeRootProfileRegistration, "Please add " +
                            "First Name and Last Name");
                } else {
                    profileRegistration(firstName, lastName, emailId, userProfile.getPmId(),
                            AppConstants.REGISTRATION_VIA_EMAIL, AppConstants.DEVICE_TOKEN_ID);
                }

                break;
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

                    Utils.showSuccessSnackbar(this, relativeRootProfileRegistration,
                            userProfileResponse.getMessage());

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

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleRegister.setOnRippleCompleteListener(this);

    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void profileRegistration(String firstName, String lastName, String emailId, String
            pmId, int type, String deviceId) {

        WsRequestObject profileRegistrationObject = new WsRequestObject();
        profileRegistrationObject.setFirstName(firstName);
        profileRegistrationObject.setLastName(lastName);
        profileRegistrationObject.setEmailId(emailId);
        profileRegistrationObject.setPmId(pmId);
        profileRegistrationObject.setType(String.valueOf(type));
        profileRegistrationObject.setDeviceId(deviceId);

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
}
