package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.helper.Utils;

public class SplashActivity extends BaseActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
           /*     // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();*/

                if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                                .PREF_LAUNCH_SCREEN_INT,
                        IntegerConstants.LAUNCH_MOBILE_REGISTRATION) == IntegerConstants
                        .LAUNCH_MOBILE_REGISTRATION) {

                    startActivityIntent(SplashActivity.this, MobileNumberRegistrationActivity.class, null);
                    finish();

                }
//                else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
//                                .PREF_LAUNCH_SCREEN_INT,
//                        IntegerConstants.LAUNCH_MOBILE_REGISTRATION) == IntegerConstants
//                        .LAUNCH_OTP_VERIFICATION) {
//
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean(AppConstants.EXTRA_IS_FROM_MOBILE_REGIS, false);
//                    startActivityIntent(SplashActivity.this, OtpVerificationActivity.class, bundle);
//                    finish();
//
//                }
                else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                                .PREF_LAUNCH_SCREEN_INT,
                        IntegerConstants.LAUNCH_MOBILE_REGISTRATION) == IntegerConstants
                        .LAUNCH_PROFILE_REGISTRATION) {

                    startActivityIntent(SplashActivity.this, ProfileRegistrationActivity.class, null);
                    finish();

                } else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                                .PREF_LAUNCH_SCREEN_INT,
                        IntegerConstants.LAUNCH_MOBILE_REGISTRATION) == IntegerConstants
                        .LAUNCH_SET_PASSWORD) {

                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_IS_FROM, "splash");
                    startActivityIntent(SplashActivity.this, SetPasswordActivity.class, bundle);
                    finish();

                } else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                                .PREF_LAUNCH_SCREEN_INT,
                        IntegerConstants.LAUNCH_MOBILE_REGISTRATION) == IntegerConstants
                        .LAUNCH_ENTER_PASSWORD) {

                    startActivityIntent(SplashActivity.this, EnterPasswordActivity.class, null);
                    finish();

                } else if (Utils.getIntegerPreference(SplashActivity.this, AppConstants
                                .PREF_LAUNCH_SCREEN_INT,
                        IntegerConstants.LAUNCH_MOBILE_REGISTRATION) == IntegerConstants
                        .LAUNCH_RE_LOGIN_PASSWORD) {

                    Intent intent = new Intent(SplashActivity.this, ReLoginEnterPasswordActivity.class);
                    intent.putExtra(AppConstants.PREF_IS_FROM, AppConstants.PREF_FORGOT_PASSWORD);
                    startActivity(intent);
                    finish();

                } else {

                    startActivityIntent(SplashActivity.this, MainActivity.class, null);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
