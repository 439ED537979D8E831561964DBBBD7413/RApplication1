package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;
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
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableOtpLogDetails;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Country;
import com.rawalinfocom.rcontact.model.OtpLog;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.services.OtpTimerService;

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

    Intent otpServiceIntent;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mobileNumber = bundle.getString(AppConstants.EXTRA_MOBILE_NUMBER);
        selectedCountry = (Country) bundle.getSerializable(AppConstants.EXTRA_OBJECT_COUNTRY);

        init();

        startOtpService();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_submit:
                verifyOtp();
                break;

            case R.id.ripple_resend:
                stopService(otpServiceIntent);
                Utils.showSuccessSnackbar(OtpVerificationActivity.this,
                        relativeRootOtpVerification, getString(R.string.msg_success_otp_request));
                inputOtp.setText("");
                sendOtp();
                break;
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_SEND_OTP">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_SEND_OTP)) {
                WsResponseObject otpDetailResponse = (WsResponseObject) data;
                if (otpDetailResponse.getStatus().equalsIgnoreCase(WsConstants
                        .RESPONSE_STATUS_TRUE)) {

                    OtpLog otpLogResponse = otpDetailResponse.getOtpLog();

                    TableOtpLogDetails tableOtpLogDetails = new TableOtpLogDetails(this,
                            databaseHandler);

                    if (tableOtpLogDetails.getOtpCount() > 0 && tableOtpLogDetails
                            .getLastOtpDetails().getOldOtp().equalsIgnoreCase
                                    (otpLogResponse.getOldOtp())) {
                        // Update OTP validation Timing
                        OtpLog otpLog = new OtpLog();
                        otpLog.setOldId(tableOtpLogDetails.getLastOtpDetails().getOldId());
                        otpLog.setOldOtp(otpLogResponse.getOldOtp());
                        otpLog.setOldGeneratedAt(otpLogResponse.getOldGeneratedAt());
                        otpLog.setOldValidUpto(Utils.getOtpExpirationTime(otpLog
                                .getOldGeneratedAt()));
                        otpLog.setOldValidityFlag("1");
                        otpLog.setRcProfileMasterPmId(otpLogResponse.getRcProfileMasterPmId());

                        tableOtpLogDetails.updateOtp(otpLog);

                    } else {
                        // Add data to OTP table
                        OtpLog otpLog = new OtpLog();
                        otpLog.setOldOtp(otpLogResponse.getOldOtp());
                        otpLog.setOldGeneratedAt(otpLogResponse.getOldGeneratedAt());
                        otpLog.setOldValidUpto(Utils.getOtpExpirationTime(otpLog
                                .getOldGeneratedAt()));
                        otpLog.setOldValidityFlag("1");
                        otpLog.setRcProfileMasterPmId(otpLogResponse.getRcProfileMasterPmId());

                        tableOtpLogDetails.addOtp(otpLog);
                    }

                    startOtpService();

                } else {
                    Log.e("error response", otpDetailResponse.getMessage());
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

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleResend.setOnRippleCompleteListener(this);
        rippleSubmit.setOnRippleCompleteListener(this);
    }

    private void startOtpService() {
        otpServiceIntent = new Intent(this, OtpTimerService.class);
        otpServiceIntent.putExtra(AppConstants.EXTRA_MOBILE_NUMBER, mobileNumber);
        otpServiceIntent.putExtra(AppConstants.EXTRA_OTP_SERVICE_END_TIME, (long) 60 * 1000);
        otpServiceIntent.putExtra(AppConstants.EXTRA_CALL_MSP_SERVER, true);
        startService(otpServiceIntent);
    }

    private void verifyOtp() {
        if (StringUtils.length(inputOtp.getText().toString()) == AppConstants.OTP_LENGTH) {
            TableOtpLogDetails tableOtpLogDetails = new TableOtpLogDetails
                    (OtpVerificationActivity.this, databaseHandler);
            OtpLog otpLog = tableOtpLogDetails.getLastOtpDetails();
            if (otpLog.getOldValidityFlag().equalsIgnoreCase("1")) {
                if (otpLog.getOldOtp().equals(inputOtp.getText().toString())) {
                    stopService(otpServiceIntent);
                    Utils.showSuccessSnackbar(OtpVerificationActivity.this,
                            relativeRootOtpVerification, getString(R.string
                                    .msg_otp_verification_success));
                    startActivityIntent(this, ProfileRegistrationActivity.class, null);
                } else {
                    Utils.showErrorSnackBar(OtpVerificationActivity.this,
                            relativeRootOtpVerification, getString(R.string
                                    .error_otp_verification_fail));
                }
            } else {
                Utils.showErrorSnackBar(OtpVerificationActivity.this,
                        relativeRootOtpVerification, getString(R.string.error_otp_expired));
            }
        } else {
            Utils.showErrorSnackBar(OtpVerificationActivity.this,
                    relativeRootOtpVerification, getString(R.string
                            .error_otp_length_incorrect, AppConstants.OTP_LENGTH));
        }
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void sendOtp() {

        WsRequestObject otpObject = new WsRequestObject();
        otpObject.setCountryCode(selectedCountry.getCountryCodeNumber());
        otpObject.setMobileNumber(mobileNumber);


        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_SEND_OTP, getString(R.string
                    .msg_please_wait)).execute(WsConstants.WS_ROOT + WsConstants.REQ_SEND_OTP);
        } else {
            Utils.showErrorSnackBar(this, relativeRootOtpVerification, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>
}
