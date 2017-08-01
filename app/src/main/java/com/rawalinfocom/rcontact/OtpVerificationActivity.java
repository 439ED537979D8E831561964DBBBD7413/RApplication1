package com.rawalinfocom.rcontact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Country;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtpVerificationActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.includeToolbar)
    LinearLayout includeToolbar;
    ImageView imageActionBack;
    RippleView rippleActionBack;
    Toolbar toolbarOtpVerification;
    TextView textToolbarTitle;
    @BindView(R.id.text_verify_number)
    TextView textVerifyNumber;
    @BindView(R.id.text_enter_otp)
    TextView textEnterOtp;
    @BindView(R.id.input_otp)
    EditText inputOtp;
    @BindView(R.id.button_submit)
    Button buttonSubmit;
    @BindView(R.id.ripple_submit)
    RippleView rippleSubmit;
    @BindView(R.id.button_resend)
    Button buttonResend;
    @BindView(R.id.ripple_resend)
    RippleView rippleResend;
    @BindView(R.id.relative_root_otp_verification)
    RelativeLayout relativeRootOtpVerification;

    String mobileNumber;
    Country selectedCountry;
    private String isFrom = "";

    Intent otpServiceIntent;
    BroadcastReceiver receiver;
    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(getString(R.string.str_rawal_otp))) {
                    final String message = intent.getStringExtra("message");
                    if (StringUtils.length(message) == AppConstants.OTP_LENGTH)
                        inputOtp.setText(message);
                    otpConfirmed(message);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.str_rawal_otp));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mobileNumber = Utils.getStringPreference(OtpVerificationActivity.this, AppConstants
                .PREF_REGS_MOBILE_NUMBER, "");
        selectedCountry = (Country) Utils.getObjectPreference(OtpVerificationActivity.this,
                AppConstants.PREF_SELECTED_COUNTRY_OBJECT, Country.class);

        if (selectedCountry == null) {
            selectedCountry = new Country();
        }

        init();

        if (bundle != null) {
            isFrom = bundle.getString(AppConstants.EXTRA_IS_FROM, "");
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                finish();
                break;

            case R.id.ripple_submit:

                if (inputOtp.getText().toString().equals("")) {
                    Utils.showErrorSnackBar(OtpVerificationActivity.this,
                            relativeRootOtpVerification, getString(R.string.msg_otp_error));
                } else {
                    otpConfirmed(inputOtp.getText().toString());
                }
                break;

            case R.id.ripple_resend:
                inputOtp.setText("");
                sendOtp();
                break;
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
                    Utils.showSuccessSnackBar(OtpVerificationActivity.this,
                            relativeRootOtpVerification, otpDetailResponse.getMessage());
                } else {
                    if (otpDetailResponse != null) {
                        Log.e("error response", otpDetailResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootOtpVerification,
                                otpDetailResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootOtpVerification, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_OTP_CONFIRMED">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_OTP_CONFIRMED)) {
                WsResponseObject confirmOtpResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (confirmOtpResponse != null && StringUtils.equalsIgnoreCase(confirmOtpResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    UserProfile userProfile = confirmOtpResponse.getUserProfile();

                    Utils.setObjectPreference(OtpVerificationActivity.this,
                            AppConstants.PREF_REGS_USER_OBJECT, userProfile);

                    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

                    if (isFrom.equals(AppConstants.PREF_FORGOT_PASSWORD)) {

                        // set launch screen as OtpVerificationActivity
                        Utils.setIntegerPreference(OtpVerificationActivity.this,
                                AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                        .LAUNCH_MOBILE_REGISTRATION);

                        // Redirect to SetPassWordActivity
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConstants.EXTRA_IS_FROM, AppConstants
                                .PREF_FORGOT_PASSWORD);
                        Intent intent = new Intent(this, SetPasswordActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();

                    } else {

                        // set launch screen as OtpVerificationActivity
                        Utils.setIntegerPreference(OtpVerificationActivity.this,
                                AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                        .LAUNCH_PROFILE_REGISTRATION);

                        // Redirect to ProfileRegistrationActivity
                        Intent intent = new Intent(this, ProfileRegistrationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }

                } else {
                    if (confirmOtpResponse != null) {
                        Log.e("error response", confirmOtpResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootOtpVerification,
                                confirmOtpResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootOtpVerification, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootOtpVerification, "" + error
                    .getLocalizedMessage());
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        imageActionBack = ButterKnife.findById(includeToolbar, R.id.image_action_back);
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        toolbarOtpVerification = ButterKnife.findById(includeToolbar, R.id.toolbar);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);

        textToolbarTitle.setText(getString(R.string.title_verification));

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textVerifyNumber.setTypeface(Utils.typefaceRegular(this));
        textEnterOtp.setTypeface(Utils.typefaceRegular(this));
        inputOtp.setTypeface(Utils.typefaceRegular(this));
        buttonSubmit.setTypeface(Utils.typefaceSemiBold(this));
        buttonResend.setTypeface(Utils.typefaceSemiBold(this));

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleResend.setOnRippleCompleteListener(this);
        rippleSubmit.setOnRippleCompleteListener(this);
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void sendOtp() {

        WsRequestObject otpObject = new WsRequestObject();
        otpObject.setCountryCode(selectedCountry.getCountryCodeNumber());
        otpObject.setMobileNumber(mobileNumber.replace("+91", ""));
//        otpObject.setDeviceId(getDeviceId());
        if (isFrom.equals(AppConstants.PREF_FORGOT_PASSWORD))
            otpObject.setForgotPassword(1);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_CHECK_NUMBER, getString(R.string
                    .msg_please_wait), false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT +
                            WsConstants.REQ_CHECK_NUMBER);
            Utils.showSuccessSnackBar(OtpVerificationActivity.this,
                    relativeRootOtpVerification, getString(R.string.msg_success_otp_request));
        } else {
            Utils.showErrorSnackBar(this, relativeRootOtpVerification, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void otpConfirmed(String otp) {

        WsRequestObject otpObject = new WsRequestObject();
        otpObject.setOtp(otp);
//        otpObject.setDeviceId(getDeviceId());
        otpObject.setMobileNumber(mobileNumber.replace("+", ""));
        if (isFrom.equals(AppConstants.PREF_FORGOT_PASSWORD))
            otpObject.setForgotPassword(1);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_OTP_CONFIRMED, getString(R
                    .string.msg_please_wait), false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT +
                            WsConstants.REQ_OTP_CONFIRMED);
        } else {
            Utils.showErrorSnackBar(this, relativeRootOtpVerification, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>
}

