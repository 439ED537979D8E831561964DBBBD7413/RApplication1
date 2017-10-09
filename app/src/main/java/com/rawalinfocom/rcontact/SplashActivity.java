package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

public class SplashActivity extends BaseActivity implements WsResponseListener {

    // Splash screen timer
//    private static int SPLASH_TIME_OUT = 300;

    private RelativeLayout relativeRootSplash;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        activity = SplashActivity.this;
        relativeRootSplash = (RelativeLayout) findViewById(R.id.relative_root_splash);

//        redirectToActivity();

        checkVersion();

//        switch (Utils.getIntegerPreference(activity, AppConstants
//                .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY)) {
//            case IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY:
//                SPLASH_TIME_OUT = 1500;
//                break;
//            default:
//                SPLASH_TIME_OUT = 300;
//                break;
//        }

//        new Handler().postDelayed(new Runnable() {
//
//            /*
//             * Showing splash screen with a timer. This will be useful when you
//             * want to show case your app logo / company
//             */
//
//            @Override
//            public void run() {
//            }
//        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            //<editor-fold desc="REQ_PROFILE_UPDATE">
            if (serviceType.contains(WsConstants.REQ_GET_CHECK_VERSION)) {
                WsResponseObject checkVersionResponse = (WsResponseObject) data;

                if (checkVersionResponse != null && StringUtils.equalsIgnoreCase
                        (checkVersionResponse.getMessage(), "force update")) {
                    showForceUpdateDialog();
                } else {
                    redirectToActivity();
                }

            } else {
                redirectToActivity();
            }
        } else {
            redirectToActivity();
        }
    }

    private void redirectToActivity() {

        if (Utils.getIntegerPreference(activity, AppConstants
                .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) {
            startActivityIntent(activity, TutorialActivity.class, null);
            finish();

        } else if (Utils.getIntegerPreference(activity, AppConstants
                .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                IntegerConstants.LAUNCH_TERMS_CONDITIONS_ACTIVITY) {
            startActivityIntent(activity, TermsConditionsActivity.class, null);
            finish();

        } else if (Utils.getIntegerPreference(activity, AppConstants
                .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                IntegerConstants.LAUNCH_MOBILE_REGISTRATION) {
            startActivityIntent(activity, MobileNumberRegistrationActivity
                    .class, null);
            finish();

        } else if (Utils.getIntegerPreference(activity, AppConstants
                .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                IntegerConstants.LAUNCH_PROFILE_REGISTRATION) {
            startActivityIntent(activity, ProfileRegistrationActivity.class,
                    null);
            finish();

        } else if (Utils.getIntegerPreference(activity, AppConstants
                .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                IntegerConstants.LAUNCH_SET_PASSWORD) {
            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.EXTRA_IS_FROM, "splash");
            startActivityIntent(activity, SetPasswordActivity.class, bundle);
            finish();

        } else if (Utils.getIntegerPreference(activity, AppConstants
                .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                IntegerConstants.LAUNCH_ENTER_PASSWORD) {
            startActivityIntent(activity, EnterPasswordActivity.class, null);
            finish();

        } else if (Utils.getIntegerPreference(activity, AppConstants
                .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_TUTORIAL_ACTIVITY) ==
                IntegerConstants.LAUNCH_RE_LOGIN_PASSWORD) {

            Intent intent = new Intent(activity, ReLoginEnterPasswordActivity
                    .class);
            intent.putExtra(AppConstants.EXTRA_IS_FROM, AppConstants.EXTRA_IS_FROM_RE_LOGIN);
            startActivity(intent);
            finish();

        } else {
            if (Utils.getBooleanPreference(activity, AppConstants
                    .KEY_IS_RESTORE_DONE, false)) {
                // Redirect to MainActivity
                startActivityIntent(activity, MainActivity.class, null);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            } else {
                // Redirect to RestorationActivity
                startActivityIntent(activity, RestorationActivity.class, null);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        }
    }

    private void checkVersion() {

        WsRequestObject checkVersionObject = new WsRequestObject();
        checkVersionObject.setAppVersion(String.valueOf(BuildConfig.VERSION_CODE));
        checkVersionObject.setAppPlatform("android");

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), checkVersionObject, null,
                    WsResponseObject.class, WsConstants.REQ_GET_CHECK_VERSION, null, true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + WsConstants
                            .REQ_GET_CHECK_VERSION);
        } else {
            Utils.showErrorSnackBar(this, relativeRootSplash, getResources()
                    .getString(R.string.msg_no_network));
            redirectToActivity();
        }
    }

    public void showForceUpdateDialog() {

        ContextThemeWrapper themedContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            themedContext = new ContextThemeWrapper(activity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        } else {
            themedContext = new ContextThemeWrapper(activity, android.R.style.Theme_Light_NoTitleBar);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(themedContext);

        alertDialogBuilder.setTitle(activity.getString(R.string.youAreNotUpdatedTitle));
        alertDialogBuilder.setMessage(activity.getString(R.string.youAreNotUpdatedMessage));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialogBuilder.show();
    }
}
