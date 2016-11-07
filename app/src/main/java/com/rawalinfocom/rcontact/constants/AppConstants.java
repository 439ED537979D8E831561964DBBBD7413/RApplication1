package com.rawalinfocom.rcontact.constants;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Defines several constants used as Keys for Arguments, Bundles, Preferences, Tags, Arrays or
 * Application Constants.
 */

public class AppConstants {

    public static final int OTP_VALIDITY_DURATION = 20;
    public static final int OTP_LENGTH = 6;

    //<editor-fold desc="Request Codes">

    public static int REQUEST_CODE_COUNTRY_REGISTRATION = 2;

    //</editor-fold>

    //<editor-fold desc="Intent Extra">

    public static String EXTRA_OBJECT_COUNTRY = "extra_object_country";
    public static String EXTRA_MOBILE_NUMBER = "extra_mobile_number";
    public static String EXTRA_OTP_SERVICE_END_TIME = "extra_otp_service_end_time";
    public static String EXTRA_CALL_MSP_SERVER = "extra_call_msp_server";

    //</editor-fold>

    //<editor-fold desc="Shared Preferences">

    public static String KEY_PREFERENCES = "pref_rcontact";

    public static String PREF_SELECTED_COUNTRY = "pref_selected_country";

    public static String[] arrayPrefKeys = {PREF_SELECTED_COUNTRY,};

    //</editor-fold>

}
