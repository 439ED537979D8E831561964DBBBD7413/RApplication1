package com.rawalinfocom.rcontact.constants;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Defines several constants used as Keys for Arguments, Bundles, Preferences, Tags, Arrays or
 * Application Constants.
 */

public class AppConstants {

    //<editor-fold desc="App Specific final constants">
    public static final String OTP_CONFIRMED_STATUS = "2";
    //</editor-fold>

    //<editor-fold desc="GCM Constants">
    public static final String GCM_SERVER_KEY = "AIzaSyBv-bFhNJmIhNW4_8jROcy46_axuK3Fl6g";
    public static final String GCM_SENDER_ID = "842331483294";
    //</editor-fold>

    //<editor-fold desc="OTP Constants">
    public static final int OTP_VALIDITY_DURATION = 20;
    public static final int OTP_LENGTH = 6;
    //</editor-fold>

    //<editor-fold desc="Profile Registration Constants">
    public static final int REGISTRATION_VIA_EMAIL = 0;
    public static final int REGISTRATION_VIA_FACEBOOK = 1;
    public static final int REGISTRATION_VIA_GOOGLE = 2;
    public static final int REGISTRATION_VIA_LINKEDIN = 3;
    //</editor-fold>

    //<editor-fold desc="Device Token">
    public static String DEVICE_TOKEN_ID = "";
    //</editor-fold>

    //<editor-fold desc="Request Codes">

    public static int REQUEST_CODE_COUNTRY_REGISTRATION = 2;

    //</editor-fold>

    //<editor-fold desc="Intent Extra">

    public static String EXTRA_OBJECT_COUNTRY = "extra_object_country";
    public static String EXTRA_OBJECT_USER = "extra_object_user";
    public static String EXTRA_MOBILE_NUMBER = "extra_mobile_number";
    public static String EXTRA_OTP_SERVICE_END_TIME = "extra_otp_service_end_time";
    public static String EXTRA_CALL_MSP_SERVER = "extra_call_msp_server";

    //</editor-fold>

    //<editor-fold desc="Shared Preferences">

    public static String KEY_PREFERENCES = "pref_rcontact";

    public static String PREF_SELECTED_COUNTRY = "pref_selected_country";
    public static String PREF_DEVICE_TOKEN_ID = "pref_device_token_id";

    public static String[] arrayPrefKeys = {PREF_SELECTED_COUNTRY, PREF_DEVICE_TOKEN_ID};

    //</editor-fold>

}
