package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.services.OtpTimerService;

import org.apache.commons.lang3.StringUtils;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopService(new Intent(MainActivity.this, OtpTimerService.class));

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
            UserProfile userProfile = (UserProfile) Utils.getObjectPreference(this, AppConstants
                    .PREF_REGS_USER_OBJECT, UserProfile.class);
            if (userProfile != null && StringUtils.equalsIgnoreCase(userProfile
                    .getIsAlreadyVerified(), String.valueOf(getResources().getInteger(R.integer
                    .profile_not_verified)))) {
                finish();
                startActivityIntent(this, ProfileRegistrationActivity.class, null);
            }
        }

    }


}
