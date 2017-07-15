package com.rawalinfocom.rcontact;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MobileNumberRegistrationActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_registration_logo)
    ImageView imageRegistrationLogo;
    @BindView(R.id.text_verify_number)
    TextView textVerifyNumber;
    @BindView(R.id.text_enter_number)
    TextView textEnterNumber;
    @BindView(R.id.input_country_code)
    EditText inputCountryCode;
    /*@BindView(R.id.linear_country_code)
    LinearLayout linearCountryCode;*/
    @BindView(R.id.input_number)
    EditText inputNumber;
    @BindView(R.id.linear_country_number)
    LinearLayout linearCountryNumber;
    @BindView(R.id.button_submit)
    Button buttonSubmit;
    @BindView(R.id.relative_root_mobile_registration)
    RelativeLayout relativeRootMobileRegistration;
    @BindView(R.id.ripple_submit)
    RippleView rippleSubmit;

    Country selectedCountry;
    private String[] requiredPermissions = {Manifest.permission.READ_SMS, Manifest
            .permission.RECEIVE_SMS};

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number_registration);
        ButterKnife.bind(this);
        // Hide the status bar.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);

//        new AsyncGetDeviceToken(this).execute();

        init();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == AppConstants.REQUEST_CODE_COUNTRY_REGISTRATION) {
                selectedCountry = (Country) data.getSerializableExtra(AppConstants
                        .EXTRA_OBJECT_COUNTRY);
                inputCountryCode.setText("(" + selectedCountry.getCountryCode() + ")" +
                        selectedCountry.getCountryCodeNumber());
            }
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_submit:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute(requiredPermissions, AppConstants.READ_SMS);
                } else {
                    prepareToReceiveOtp();
                }
                break;

        }
    }

//    @Override
//    public void onComplete(RippleView rippleView) {
//        switch (rippleView.getId()) {
//            case R.id.ripple_submit:
//                prepareToReceiveOtp();
//                break;
//
//        }
//    }

    private void prepareToReceiveOtp() {
        if (selectedCountry == null) {
            selectedCountry = new Country();
            selectedCountry.setCountryId("1");
            selectedCountry.setCountryCode("IN");
            selectedCountry.setCountryCodeNumber("+91");
            selectedCountry.setCountryName("India");
            selectedCountry.setCountryNumberMaxDigits("10");
        }
        if (StringUtils.length(selectedCountry.getCountryNumberMaxDigits()) > 0) {
            if (inputNumber.getText().length() != Integer.parseInt(selectedCountry
                    .getCountryNumberMaxDigits())) {
                Utils.showErrorSnackBar(MobileNumberRegistrationActivity.this,
                        relativeRootMobileRegistration, getString(R.string
                                .error_invalid_number));
            } else {
                sendOtp();
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String[] permissions, int requestCode) {
        boolean READ_SMS = ContextCompat.checkSelfPermission(MobileNumberRegistrationActivity
                .this, permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean RECEIVE_SMS = ContextCompat.checkSelfPermission(MobileNumberRegistrationActivity
                .this, permissions[1]) !=
                PackageManager.PERMISSION_GRANTED;
        if (READ_SMS || RECEIVE_SMS) {
            requestPermissions(permissions, requestCode);
        } else {
            prepareToReceiveOtp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.READ_SMS && permissions[0].equals(Manifest.permission
                .READ_SMS) && permissions[1].equals(Manifest.permission.RECEIVE_SMS)) {
            prepareToReceiveOtp();
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

                    Utils.setObjectPreference(MobileNumberRegistrationActivity.this, AppConstants
                            .PREF_SELECTED_COUNTRY_OBJECT, selectedCountry);
                    Utils.setStringPreference(MobileNumberRegistrationActivity.this, AppConstants
                            .PREF_REGS_MOBILE_NUMBER, selectedCountry.getCountryCodeNumber() +
                            inputNumber.getText().toString());

                    // set launch screen as OtpVerificationActivity
                    Utils.setIntegerPreference(MobileNumberRegistrationActivity.this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                    .LAUNCH_MOBILE_REGISTRATION);

                    if (otpDetailResponse.getFlag() == 0) {

                        // Redirect to OtpVerificationActivity
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConstants.EXTRA_IS_FROM, "mobile");
                        startActivityIntent(MobileNumberRegistrationActivity.this,
                                OtpVerificationActivity.class, bundle);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    } else {

                        startActivity(new Intent(MobileNumberRegistrationActivity.this,
                                EnterPasswordActivity.class));
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }

                } else {
                    if (otpDetailResponse != null) {
                        Log.e("error response", otpDetailResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootMobileRegistration,
                                otpDetailResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootMobileRegistration, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootMobileRegistration, "" + error
                    .getLocalizedMessage());
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        textVerifyNumber.setTypeface(Utils.typefaceRegular(this));
        textEnterNumber.setTypeface(Utils.typefaceRegular(this));
        inputCountryCode.setTypeface(Utils.typefaceRegular(this));
        inputNumber.setTypeface(Utils.typefaceRegular(this));
        buttonSubmit.setTypeface(Utils.typefaceSemiBold(this));

        rippleSubmit.setOnRippleCompleteListener(this);

        inputCountryCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (MotionEvent.ACTION_UP == event.getAction()) {
                    Intent intent = new Intent(MobileNumberRegistrationActivity.this,
                            CountryListActivity.class);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_COUNTRY_REGISTRATION);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
                return true;
            }
        });
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void sendOtp() {

        WsRequestObject otpObject = new WsRequestObject();
        otpObject.setCountryCode(selectedCountry.getCountryCodeNumber());
        otpObject.setMobileNumber(inputNumber.getText().toString());
//        otpObject.setCmId(selectedCountry.getCountryId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_CHECK_NUMBER, getString(R.string
                    .msg_please_wait), false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + WsConstants.REQ_CHECK_NUMBER);
        } else {
            Utils.showErrorSnackBar(this, relativeRootMobileRegistration, getResources()
                    .getString(R.string.msg_no_network));
        }
    }
    //</editor-fold>
}
