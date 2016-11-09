package com.rawalinfocom.rcontact;

import android.os.Bundle;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT, getResources()
                .getInteger(R.integer.launch_mobile_registration)) == getResources().getInteger(R
                .integer.launch_mobile_registration)) {
            finish();
            startActivityIntent(this, MobileNumberRegistrationActivity.class, null);
        } else if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT,
                getResources().getInteger(R.integer.launch_mobile_registration)) == getResources
                ().getInteger(R.integer.launch_otp_verification)) {
            finish();
            startActivityIntent(this, OtpVerificationActivity.class, null);
        } else if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT,
                getResources().getInteger(R.integer.launch_mobile_registration)) == getResources
                ().getInteger(R.integer.launch_profile_registration)) {
            finish();
            startActivityIntent(this, ProfileRegistrationActivity.class, null);
        }

    }


}
