package com.rawalinfocom.rcontact.constants;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Defines several constants used as Keys for Arguments, Bundles, Preferences, Tags, Arrays or
 * Application Constants.
 */

public class AppConstants {

    //<editor-fold desc="Profile Detail Section">
    public static final int PHONE_NUMBER = 0;
    public static final int EMAIL = 1;
    public static final int WEBSITE = 2;
    public static final int ADDRESS = 3;
    public static final int IM_ACCOUNT = 4;
    public static final int EVENT = 5;
    public static final int GENDER = 6;
    //</editor-fold>

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

    //<editor-fold desc="Request Codes">

    public static int REQUEST_CODE_COUNTRY_REGISTRATION = 2;

    //</editor-fold>

    //<editor-fold desc="Intent Extra">

    public static String EXTRA_OBJECT_COUNTRY = "extra_object_country";
    public static String EXTRA_PM_ID = "extra_pm_id";
    public static String EXTRA_PHONE_BOOK_ID = "extra_phone_book_id";
    public static String EXTRA_OBJECT_USER = "extra_object_user";
    public static String EXTRA_MOBILE_NUMBER = "extra_mobile_number";
    public static String EXTRA_OTP_SERVICE_END_TIME = "extra_otp_service_end_time";
    public static String EXTRA_CALL_MSP_SERVER = "extra_call_msp_server";
    public static String EXTRA_IS_FROM_MOBILE_REGIS = "extra_is_from_mobile_regis";
    public static String EXTRA_LOCAL_BROADCAST_MESSAGE = "message";
    public static String EXTRA_CONTACT_NAME = "contact_name";
    public static String EXTRA_CLOUD_CONTACT_NAME = "cloud_contact_name";

    //</editor-fold>

    //<editor-fold desc="Intent Action">

    public static String ACTION_CONTACT_FETCH = "action_contact_fetch";

    //</editor-fold>

    //<editor-fold desc="Fragment Tags">

    public static String TAG_FRAGMENT_ALL_CONTACTS = "tag_fragment_all_contacts";
    public static String TAG_FRAGMENT_R_CONTACTS = "tag_fragment_r_contacts";
    public static String TAG_FRAGMENT_FAVORITES = "tag_fragment_favorites";

    //</editor-fold>

    //<editor-fold desc="Shared Preferences">

    public static String KEY_PREFERENCES = "pref_rcontact";

    public static String PREF_SELECTED_COUNTRY_OBJECT = "pref_selected_country_object";
    public static String PREF_DEVICE_TOKEN_ID = "pref_device_token_id";
    public static String PREF_LAUNCH_SCREEN_INT = "pref_launch_screen_int";
    public static String PREF_REGS_USER_OBJECT = "pref_regs_user_object";
    public static String PREF_REGS_MOBILE_NUMBER = "pref_regs_mobile_number";
    public static String PREF_CONTACT_ID_SET = "pref_contact_id_set";
    public static String PREF_SYNCED_CONTACTS = "pref_synced_contacts";
    public static String PREF_USER_PM_ID = "pref_user_pm_id";
    public static String PREF_ACCESS_TOKEN = "pref_access_token";

    public static String[] arrayPrefKeys = {PREF_SELECTED_COUNTRY_OBJECT, PREF_DEVICE_TOKEN_ID,
            PREF_LAUNCH_SCREEN_INT, PREF_REGS_USER_OBJECT, PREF_REGS_MOBILE_NUMBER,
            PREF_CONTACT_ID_SET, PREF_USER_PM_ID, PREF_ACCESS_TOKEN};

    //</editor-fold>

}
