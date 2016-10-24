package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Country;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MobileNumberRegistrationActivity extends AppCompatActivity implements RippleView
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

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number_registration);
        ButterKnife.bind(this);

        // Hide the status bar.
        if (Utils.hasJellybean()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                    .LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

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
            case R.id.ripple_submit: {
                if (selectedCountry == null) {
                    selectedCountry = new Country();
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
//                        sendOtp();
                        Intent intent = new Intent(MobileNumberRegistrationActivity.this,
                                OtpVerificationActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_SEND_OTP">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_SEND_OTP)) {
                WsResponseObject countryListResponse = (WsResponseObject) data;
                if (countryListResponse.getStatus().equalsIgnoreCase(WsConstants
                        .RESPONSE_STATUS_TRUE)) {
//                    AppUtils.hideProgressDialog();


                } else {
                    Log.e("error response", countryListResponse.getMessage());
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

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), null, null,
                    WsResponseObject.class, WsConstants.REQ_SEND_OTP, getString(R.string
                    .msg_please_wait)).execute(WsConstants.WS_ROOT + WsConstants.REQ_SEND_OTP);
        } else {
            Utils.showErrorSnackBar(this, relativeRootMobileRegistration, getResources()
                    .getString(R.string.msg_no_network));
        }

    }

    //</editor-fold>
}
