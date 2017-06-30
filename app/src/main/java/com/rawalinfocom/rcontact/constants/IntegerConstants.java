package com.rawalinfocom.rcontact.constants;

/**
 * Created by user on 28/04/17.
 */

public class IntegerConstants {

    // Launcher Screen Constants
    public static int LAUNCH_MOBILE_REGISTRATION = 0;
    public static int LAUNCH_OTP_VERIFICATION = 1;
    public static int LAUNCH_PROFILE_REGISTRATION = 2;
    public static int LAUNCH_RE_LOGIN_PASSWORD = 3;
    public static int LAUNCH_ENTER_PASSWORD = 4;
    public static int LAUNCH_SET_PASSWORD = 5;
    public static int LAUNCH_MAIN_ACTIVITY = 6;

    // Profile Registration Constants
    public static int REGISTRATION_VIA = 0;
    public static int REGISTRATION_VIA_EMAIL = 0;
    public static int REGISTRATION_VIA_FACEBOOK = 1;
    public static int REGISTRATION_VIA_GOOGLE = 2;
    public static int REGISTRATION_VIA_LINED_IN = 3;

    // User Profile Verification Constants
    public static int PROFILE_NOT_VERIFIED = 0;
    public static int PROFILE_ALREADY_VERIFIED = 1;

    // RCP Type Constants (For Mobile Number and Email Id)
    public static int RCP_TYPE_CLOUD_PHONE_BOOK = 0;
    public static int RCP_TYPE_PRIMARY = 1;  // verified
    public static int RCP_TYPE_SECONDARY = 2;
    public static int RCP_TYPE_LOCAL_PHONE_BOOK = 3;

    // Rating Status Constants
    public static int RATING_DONE = 1;  // sent
    public static int RATING_RECEIVED = 2;  // received

    // Send Profile Type
    public static int SEND_PROFILE_RCP = 1;
    public static int SEND_PROFILE_NON_RCP_SOCIAL = 2;
    public static int SEND_PROFILE_NON_RCP = 3;

    // Contact sync status flags
    public static int SYNC_INSERT_CONTACT = 1;
    public static int SYNC_UPDATE_INSERT_CONTACT = 2;
    public static int SYNC_UPDATE_UPDATE_CONTACT = 3;
    public static int SYNC_UPDATE_DELETE_CONTACT = 4;
    public static int SYNC_DELETE_CONTACT = 5;
    public static int SYNC_UPDATE_CONTACT = 6;
    public static int SYNC_INSERT_CALL_LOG = 7;

    // Privacy
    public static int PRIVACY_EVERYONE = 1;
    public static int PRIVACY_MY_CONTACT = 2;
    public static int PRIVACY_PRIVATE = 3;
    public static int IS_PRIVATE = 1;
    public static int IS_YEAR_HIDDEN = 1;
}
