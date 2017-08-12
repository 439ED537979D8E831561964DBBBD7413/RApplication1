package com.rawalinfocom.rcontact.constants;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Defines several constants used as Keys for Arguments, Bundles, Preferences, Tags, Arrays or
 * Application Constants.
 */

public class AppConstants {

    public static String PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=";

    //<editor-fold desc="Permission Request Code">
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1003;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1004;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1005;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1006;
    public static final int MY_PERMISSIONS_CALL_LOG = 1007;
    public static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 1008;
    //</editor-fold>

    //<editor-fold desc="Profile Detail Section">
    public static final int PHONE_NUMBER = 0;
    public static final int EMAIL = 1;
    public static final int WEBSITE = 2;
    public static final int ADDRESS = 3;
    public static final int IM_ACCOUNT = 4;
    public static final int EVENT = 5;
    public static final int GENDER = 6;
    public static final int ORGANIZATION = 7;
    public static final int PROFILE_IMAGE = 8;
    public static final int NAME = 9;
    public static boolean isRecentCallFromSMSTab = false;
    //</editor-fold>

    //<editor-fold desc="App Specific final constants">

    public static final String OTP_CONFIRMED_STATUS = "2";

    //</editor-fold>

    //<editor-fold desc="GCM Constants">

    public static final String GCM_SERVER_KEY = "AIzaSyBv-bFhNJmIhNW4_8jROcy46_axuK3Fl6g";
    public static final String GCM_SENDER_ID = "376651096541";

    //</editor-fold>

    //<editor-fold desc="OTP Constants">

    public static final int OTP_VALIDITY_DURATION = 20;
    public static final int OTP_LENGTH = 6;
    public static boolean isProgressShowing = false;
    public static boolean isBackgroundProcessStopped = false;

    //</editor-fold>

    //<editor-fold desc="Request Codes">

    public static int REQUEST_CODE_COUNTRY_REGISTRATION = 2;
    public static int REQUEST_CODE_MAP_LOCATION_SELECTION = 3;
    public static int REQUEST_CODE_PROFILE_DETAIL = 4;

    //</editor-fold>

    // <editor-fold desc="Result Codes">

    public static int RESULT_CODE_MAP_LOCATION_SELECTION = 3;
    public static int RESULT_CODE_MY_LOCATION_SELECTION = 4;
    public static int RESULT_CODE_EDIT_PROFILE = 5;

    //</editor-fold>

    //<editor-fold desc="Intent Extra">

    public static String EXTRA_OBJECT_COUNTRY = "extra_object_country";
    public static String EXTRA_PM_ID = "extra_pm_id";
    public static String EXTRA_CHECK_NUMBER_FAVOURITE = "extra_check_number_favourite";
    public static String EXTRA_PHONE_BOOK_ID = "extra_phone_book_id";
    public static String EXTRA_OBJECT_USER = "extra_object_user";
    public static String EXTRA_MOBILE_NUMBER = "extra_mobile_number";
    public static String EXTRA_OTP_SERVICE_END_TIME = "extra_otp_service_end_time";
    public static String EXTRA_CALL_MSP_SERVER = "extra_call_msp_server";
    //    public static String EXTRA_IS_FROM_MOBILE_REGIS = "extra_is_from_mobile_regis";
    public static String EXTRA_IS_FROM = "extra_is_from";
    public static String EXTRA_LOCAL_BROADCAST_MESSAGE = "message";
    public static String EXTRA_CONTACT_NAME = "contact_name";
    public static String EXTRA_PROFILE_IMAGE_URL = "profile_image_url";
    public static String EXTRA_CLOUD_CONTACT_NAME = "cloud_contact_name";
    public static String EXTRA_CONTACT_POSITION = "contact_position";
    public static String EXTRA_RCONTACT_POSITION = "rcontact_position";
    public static String EXTRA_FAVOURITE_POSITION = "extra_favourite_position";
    public static String EXTRA_OBJECT_CONTACT = "extra_object_contact";
    public static String EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE = "extra_call_instance";
    public static String EXTRA_CALL_HISTORY_NAME = "extra_call_history_name";
    public static String EXTRA_CALL_HISTORY_NUMBER = "extra_call_history_number";
    public static String EXTRA_CALL_HISTORY_DATE = "extra_call_history_date";
    public static String EXTRA_3DOTS_SHOW_HISTORY_INSTANCE = "extra_3dots_show_history_instance";
    public static String EXTRA_CALL_LOG_BROADCAST_KEY = "message";
    public static boolean EXTRA_CALL_LOG_BROADCAST_VALUE = false;
    public static String EXTRA_CALL_LOG_DELETED_KEY = "message";
    public static boolean EXTRA_CALL_LOG_DELETED_VALUE = true;
    public static String EXTRA_CALL_LOG_SWITCH_TAB = "message";
    public static boolean EXTRA_CALL_LOG_SWITCH_TAB_VALUE = true;
    public static String EXTRA_CALL_LOG_BLOCK = "message";
    public static boolean EXTRA_CALL_LOG_BLOCK_VALUE = true;
    public static String EXTRA_CALL_ARRAY_LIST = "extra_call_array_list";
    public static String EXTRA_DELETE_ALL_CALL_LOGS = "extra_delete_all_call_logs";
    public static String EXTRA_REMOVE_CALL_LOGS = "extra_remove_call_logs";
    public static String EXTRA_RATING_UPDATE = "extra_rating_update";
    public static String EXTRA_CLEAR_CALL_LOGS = "extra_clear_call_logs";
    public static String EXTRA_CLEAR_CALL_LOGS_FROM_CONTACTS =
            "extra_clear_call_logs_from_contacts";
    //    public static String EXTRA_OBJECT_LOCATION = "extra_object_location";
    public static String EXTRA_OBJECT_ADDRESS = "extra_object_address";
    public static String EXTRA_CALL_UNIQUE_ID = "extra_call_unique_id";
    public static String EXTRA_UNIQUE_CONTACT_ID = "extra_unique_contact_id";
    public static String EXTRA_FORMATTED_ADDRESS = "extra_formatted_address";
    public static String EXTRA_LATITUDE = "extra_latitude";
    public static String EXTRA_LONGITUDE = "extra_longitude";
    public static String EXTRA_CONTACT_PROFILE_IMAGE = "extra_contact_profile_image";
    public static String EXTRA_IS_FROM_FAVOURITE = "extra_is_from_favourite";
    public static String EXTRA_GLOBAL_PUBLIC_PROFILE_URL = "extra_global_public_profile_url";
    public static String EXTRA_FEEDBACK_URL = "extra_feedback_url";
    public static String EXTRA_IS_RCP_USER = "extra_is_rcp_user";
    public static String EXTRA_CALL_LOG_CLOUD_NAME = "extra_call_log_cloud_name";
    public static String EXTRA_IS_RCP_VERIFIED_SPAM = "extra_is_rcp_verified_spam";
    public static String EXTRA_RCP_VERIFIED_ID = "extra_rcp_verified_id";
    public static String EXTRA_RCP_FROM_NOTI = "extra_rcp_from_noti";




    //</editor-fold>

    //<editor-fold desc="Intent Action">

    public static String ACTION_CONTACT_FETCH = "action_contact_fetch";
    public static String ACTION_CALL_LOG_FETCH = "action_call_log_fetch";
    public static String ACTION_START_CALL_LOG_INSERTION = "action_start_call_log_insertion";
    public static String ACTION_LOCAL_BROADCAST = "action_local_broadcast";
    public static String ACTION_LOCAL_BROADCAST_DIALOG = "action_local_broadcast_dialog";
    public static String ACTION_LOCAL_BROADCAST_TABCHANGE = "action_local_broadcast_tab_change";
    public static String ACTION_LOCAL_BROADCAST_PROFILE = "action_local_broadcast_profile";
    public static String ACTION_LOCAL_BROADCAST_DELETE_LOGS = "action_local_broadcast_delete_logs";
    public static String ACTION_LOCAL_BROADCAST_REMOVE_CALL_LOGS =
            "action_local_broadcast_remove_call_logs";
    public static String ACTION_LOCAL_BROADCAST_RATING_UPDATE =
            "action_local_broadcast_rating_update";
    public static String ACTION_LOCAL_BROADCAST_PROFILE_BLOCK =
            "action_local_broadcast_profile_block";
    public static String ACTION_LOCAL_BROADCAST_UNBLOCK = "action_local_broadcast_unblock";
    public static String ACTION_LOCAL_BROADCAST_CALL_HISTORY_ACTIVITY =
            "action_local_broadcast_call_history_activity";
    public static String ACTION_LOCAL_BROADCAST_CALL_LOG_SYNC =
            "action_local_broadcast_call_log_sync";
    public static String ACTION_LOCAL_BROADCAST_DIALOG_SMS = "action_local_broadcast_dialog_sms";
    public static String ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_CALLS =
            "action_local_broadcast_recent_calls";
    public static String ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_CALLS_CALL_LOGS =
            "action_local_broadcast_recent_calls_call_logs";
    public static String ACTION_LOCAL_BROADCAST_SMS_RECEIVER =
            "action_local_broadcast_sms_receiver";
    public static String ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_SMS =
            "action_local_broadcast_recent_sms";
    public static String ACTION_LOCAL_BROADCAST_DELETE_SMS_RECEIVER =
            "action_local_broadcast_delete_sms_receiver";
    public static String ACTION_LOCAL_BROADCAST_SYNC_SMS = "action_local_broadcast_sync_sms";
    public static String ACTION_LOCAL_BROADCAST_UPDATE_NOTIFICATION_COUNT =
            "action_local_broadcast_update_notification_count";
    public static String ACTION_LOCAL_BROADCAST_CONTACT_DISPLAYED =
            "action_local_broadcast_contact_displayed";
    public static String ACTION_LOCAL_BROADCAST_GET_GLOBAL_PROFILE_DATA =
            "action_local_broadcast_get_global_profile_data";

    //</editor-fold>

    //<editor-fold desc="Fragment Tags">

    public static String TAG_FRAGMENT_ALL_CONTACTS = "tag_fragment_all_contacts";
    public static String TAG_FRAGMENT_R_CONTACTS = "tag_fragment_r_contacts";
    public static String TAG_FRAGMENT_FAVORITES = "tag_fragment_favorites";

    //</editor-fold>

    //<editor-fold desc="Shared Preferences">

    // User profile
    public static String PREF_USER_PROFILE_UPDATE = "pref_user_profile_update";
    public static String PREF_USER_NAME = "pref_user_name";
    public static String PREF_USER_NUMBER = "pref_user_number";
    public static String PREF_USER_RATING = "pref_user_rating";
    public static String PREF_USER_TOTAL_RATING = "pref_user_total_rating";
    public static String PREF_USER_PHOTO = "pref_user_photo";
    public static String PREF_USER_FIRST_NAME = "pref_user_first_name";
    public static String PREF_USER_LAST_NAME = "pref_user_last_name";
    public static String PREF_USER_JOINING_DATE = "pref_user_joining_date";
    public static String PREF_USER_VERIFIED_EMAIL = "pref_user_verified_email";
    public static String PREF_RESPONSE_KEY = "pref_response_key";
    public static String PREF_CALL_LOG_RESPONSE_KEY = "pref_call_log_response_key";

    public static String KEY_PREFERENCES = "pref_rcontact";
    public static String PREF_APP_LANGUAGE = "pref_app_language";
    public static String PREF_SHORT_BY_CONTACT = "pref_short_by_contact";
    public static String PREF_EXPORT_CONTACT = "pref_export_contact";

    public static String KEY_API_CALL_TIME = "key_api_call_time";
    public static String KEY_API_CALL_TIME_STAMP = "key_api_call_time_stamp";
    public static String KEY_IS_FIRST_TIME = "key_is_first_time";
    public static String KEY_IS_RESTORE_DONE = "key_is_restore_done";

    public static String PREF_DISABLE_PUSH = "pref_disable_push";
    public static String PREF_DISABLE_POPUP = "pref_disable_popup";
    public static String PREF_DISABLE_EVENT_PUSH = "pref_disable_event_push";

    public static String PREF_LOGIN_TYPE = "pref_login_type";

    public static String PREF_RE_LOGIN = "pref_re_login";
    public static String PREF_FORGOT_PASSWORD = "pref_forgot_password";
    public static String PREF_IS_FROM = "pref_is_from";
    public static String PREF_TEMP_LOGOUT = "pref_temp_logout";
    public static String PREF_SELECTED_COUNTRY_OBJECT = "pref_selected_country_object";
    public static String PREF_DEVICE_TOKEN_ID = "pref_device_token_id";
    public static String PREF_LAUNCH_SCREEN_INT = "pref_launch_screen_int";
    public static String PREF_REGS_USER_OBJECT = "pref_regs_user_object";
    public static String PREF_REGS_MOBILE_NUMBER = "pref_regs_mobile_number";
    //    public static String PREF_CONTACT_ID_SET = "pref_contact_id_set";
    public static String PREF_SYNCED_CONTACTS = "pref_synced_contacts";
    public static String PREF_USER_PM_ID = "pref_user_pm_id";
    public static String PREF_ACCESS_TOKEN = "pref_access_token";
    public static String PREF_FAVOURITE_CONTACT_NUMBER_EMAIL =
            "pref_favourite_contact_number_email";
    public static String PREF_PROFILE_VIEWS = "pref_profile_views";

    public static String PREF_CALL_LOG_SYNC_TIME = "pref_call_log_sync_time";
    public static String PREF_CALL_LOG_ROW_ID = "pref_call_log_row_id";
    public static String PREF_SMS_SYNC_TIME = "pref_sms_sync_time";

    public static String PREF_CALL_LOG_SIZE = "pref_call_log_size";
    public static String PREF_CALL_LOG_STARTS_FIRST_TIME = "pref_call_log_start_first_time";
    public static String PREF_CALL_LOG_SIZE_WITH_DIFF = "pref_call_log_size_with_diff";
    public static String PREF_CONTACT_SYNCED = "pref_contact_synced";
    public static String PREF_CALL_LOG_SYNCED = "pref_call_log_synced";
    public static String PREF_SMS_SYNCED = "pref_sms_synced";
    public static String PREF_BLOCK_CONTACT_LIST = "pref_block_contact_list";
    public static String PREF_BLOCK_PROFILE = "pref_block_profile";
    public static String PREF_CALL_LOG_LIST = "pref_call_log_list";
    public static String PREF_CONTACT_LAST_SYNC_TIME = "pref_contact_last_sync_time";
    public static String PREF_CALL_LOGS_ID_SET = "pref_call_logs_id_set";
    public static String PREF_CALL_LOG_SYNCED_COUNT = "pref_call_log_synced_count";
    public static String PREF_CALL_LOG_TO_FETCH_COUNT = "pref_call_log_to_fetch_count";
    public static String PREF_CONTACT_ID_SET = "pref_contact_id_set";
    public static String PREF_SMS_LOG_STARTS_FIRST_TIME = "pref_sms_log_start_first_time";
    public static String PREF_SMS_LOGS_ID_SET = "pref_sms_logs_id_set";
    public static String PREF_RECENT_CALLS_BROADCAST_RECEIVER_MAIN_INSTANCE =
            "pref_recent_calls_broadcast_receiver_main_instance";
    public static String PREF_RECENT_SMS_BROADCAST_RECEIVER_MAIN_INSTANCE =
            "pref_recent_sms_broadcast_receiver_main_instance";
    public static String PREF_RECENT_CALLS_BROADCAST_RECEIVER_CALL_LOG_TAB =
            "pref_recent_calls_broadcast_receiver_main_instance_call_log_tab";
    public static String PREF_SMS_LOG_SYNCED_COUNT = "pref_sms_log_synced_count";
    public static String PREF_SMS_LOG_SYNCED = "pref_sms_log_synced";
    public static String PREF_GOT_ALL_PROFILE_DATA = "pref_got_all_profile_data";
    public static String PREF_IS_CALL_UPLOAD_CONTACT = "pref_is_call_upload_contact";
    public static String PREF_LATEST_CALL_DATE_TIME =  "pref_latest_call_date_time";
    public static String PREF_LATEST_CALL_RAW_ID= "pref_latest_call_raw_id";


    public static String[] arrayPrefKeys = {PREF_SELECTED_COUNTRY_OBJECT, PREF_DEVICE_TOKEN_ID,
            PREF_LAUNCH_SCREEN_INT, PREF_REGS_USER_OBJECT, PREF_REGS_MOBILE_NUMBER,
            PREF_USER_PM_ID, PREF_ACCESS_TOKEN,
            PREF_FAVOURITE_CONTACT_NUMBER_EMAIL, PREF_PROFILE_VIEWS};

    //</editor-fold>

    //<editor-fold desc="Phone Sim State">
    public static boolean isDualSimPhone;

    public static boolean isDualSimPhone() {
        return isDualSimPhone;
    }

    public static void setIsDualSimPhone(boolean isDualSimPhone) {
        AppConstants.isDualSimPhone = isDualSimPhone;
    }

    public static boolean isCallLogFragment = false;

    public static boolean isCallLogFragment() {
        return isCallLogFragment;
    }

    public static void setIsCallLogFragment(boolean isCallLogFragment) {
        AppConstants.isCallLogFragment = isCallLogFragment;
    }

    private static boolean isFirstTime;

    public static boolean isFirstTime() {
        return isFirstTime;
    }

    public static void setIsFirstTime(boolean isFirstTime) {
        AppConstants.isFirstTime = isFirstTime;
    }
    //</editor-fold>

    public static boolean isFromReceiver = false;
    public static boolean isSMSFromReceiver = false;
    public static boolean isComposingSMS = false;
    public static boolean isFromSearchActivity = false;
    public static boolean isFromSettingActivity = false;

    //<editor-fold desc="Call-log constants">
    public static final int READ_LOGS = 725;
    public static final int READ_SMS = 785;
    public static final int INCOMING_CALLS = 672;
    public static final int OUTGOING_CALLS = 609;
    public static final int MISSED_CALLS = 874;
    public static final int ALL_CALLS = 814;
    public static final int INCOMING = 1;
    public static final int OUTGOING = 2;
    public static final int MISSED = 3;
    public static final int NEW_CONTACT_MI = 10;
    public static final int BLOCKED = 4;
    public static final int UNBLOCK = 0;

    //</editor-fold>

    //<editor-fold desc="Event constants">
    public static final int COMMENT_STATUS_SENT = 1;
    public static final int COMMENT_STATUS_RECEIVED = 2;

    public static final int COMMENT_TYPE_BIRTHDAY = 1;
    public static final int COMMENT_TYPE_RATING = 2;
    public static final int COMMENT_TYPE_ANNIVERSARY = 3;
    public static final int COMMENT_TYPE_REQUESTS = 4;
    public static final int COMMENT_TYPE_RUPDATES = 5;
    //</editor-fold>

    //<editor-fold desc="Otp constants">

    // SMS provider identification
    public static final String SMS_ORIGIN = "RCNTCT";

    // special character to prefix the otp.
    public static final String OTP_DELIMITER = "is:";

    //<editor-fold desc="Notification constants">

    public static final int NOTIFICATION_TYPE_TIMELINE = 1;
    public static final int NOTIFICATION_TYPE_RATE = 2;
    public static final int NOTIFICATION_TYPE_COMMENTS = 3;
    public static final int NOTIFICATION_TYPE_PROFILE_REQUEST = 4;
    public static final int NOTIFICATION_TYPE_PROFILE_RESPONSE = 5;
    public static final int NOTIFICATION_TYPE_RUPDATE = 6;

    //</editor-fold>
}
