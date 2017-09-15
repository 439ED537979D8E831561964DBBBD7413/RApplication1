package com.rawalinfocom.rcontact.constants;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Defines several constants related to Web Services
 */

public class WsConstants {

    /**
     * Production
     */
    //<editor-fold desc="Production">

    /*// API ROOT
    public static final String WS_ROOT = "https://api.rcontacts.in/api/v1/";
    // TERMS-CONDITIONS URL
    public static final String URL_TERMS_CONDITIONS = "https://www.rcontacts.in/global/terms";
    // FAQ URL
    public static final String URL_FAQ = "https://www.rcontacts.in/global/faqs";
    // PROFILE SHARE
    public static final String WS_PROFILE_VIEW_ROOT = "https://www.rcontacts.in/global/public/";
    public static final String WS_PROFILE_VIEW_BADGE_ROOT = "http://rcrc.ac/p/";
    // WEBSITE URL
    public static final String WS_WEBSITE_URL = "https://www.rcontacts.in";
    // AVERAGE RATING SHARE
    public static final String WS_AVG_RATING_SHARE_BADGE_ROOT = "http://rcrc.ac/r/";
    //PLAY STORE LINK
    public static String PLAY_STORE_LINK = "http://rcrc.ac/a/";*/

    //</editor-fold>

    /**
     * Staging
     */
    //<editor-fold desc="Staging">

    /*// API ROOT
    public static final String WS_ROOT = "http://apistaging.rcontacts.in/api/v1/";
    // TERMS-CONDITIONS URL
    public static final String URL_TERMS_CONDITIONS = "http://webstaging.rcontacts.in/global/terms";
    // FAQ URL
    public static final String URL_FAQ = "http://webstaging.rcontacts.in/global/faqs";
    // PROFILE SHARE
    public static final String WS_PROFILE_VIEW_ROOT = "http://webstaging.rcontacts" +
            ".in/global/public/";
    public static final String WS_PROFILE_VIEW_BADGE_ROOT = "http://staging.rcrc.ac/p/";
    // WEBSITE URL
    public static final String WS_WEBSITE_URL = "http://webstaging.rcontacts.in";
    // AVERAGE RATING SHARE
    public static final String WS_AVG_RATING_SHARE_BADGE_ROOT = "http://staging.rcrc.ac/r/";
    //PLAY STORE LINK
    public static String PLAY_STORE_LINK = "http://staging.rcrc.ac/a/";*/

    //</editor-fold>

    /**
     * QA
     */
    //<editor-fold desc="QA">

    // API ROOT
//    public static final String WS_ROOT = "http://apiqa.rcontacts.in/api/v1/";
//    public static final String WS_ROOT_V2 = "http://apiqa.rcontacts.in/api/v2/";
    // TERMS-CONDITIONS URL
    public static final String URL_TERMS_CONDITIONS = "http://webqa.rcontacts.in/global/terms";
    // FAQ URL
    public static final String URL_FAQ = "http://webqa.rcontacts.in/global/faqs";
    // PROFILE SHARE
    public static final String WS_PROFILE_VIEW_ROOT = "http://webqa.rcontacts.in/global/public/";
    public static final String WS_PROFILE_VIEW_BADGE_ROOT = "http://qa.rcrc.ac/p/";
    // WEBSITE URL
    public static final String WS_WEBSITE_URL = "http://webqa.rcontacts.in";
    // AVERAGE RATING SHARE
    public static final String WS_AVG_RATING_SHARE_BADGE_ROOT = "http://qa.rcrc.ac/r/";
    //PLAY STORE LINK
    public static String PLAY_STORE_LINK = "http://qa.rcrc.ac/a/";


    //</editor-fold>

    /**
     * Local
     */
    //<editor-fold desc="Local">
    // Monal
//    public static final String WS_ROOT = "http://10.0.21.163/api/v1/";

    // Aniruddh
    public static final String WS_ROOT = "http://10.0.21.122/api/v1/";
    public static final String WS_ROOT_V2 = "http://10.0.21.122/api/v2/";

    // Maulik
//    public static final String WS_ROOT = "http://10.0.51.119/api/v1/";

    // Hardik
//    public static final String WS_ROOT = "http://10.0.30.11/api/v1/";

    // Shailesh
//    public static final String WS_ROOT = "http://10.0.21.14:8001/api/v1/";

    // Jignesh
//    public static final String WS_ROOT = "http://10.0.21.15:8000/api/v1/";
//    public static final String WS_ROOT_V2 = "http://10.0.21.15:8000/api/v2/";

    // Vijay
//     public static final String WS_ROOT = "http://10.0.21.16/api/v1/";
    //</editor-fold>

    /**
     * Feedback URL
     */
    public static final String URL_FEEDBACK = "http://feedback.rcontacts.in";

    /**
     * PRIVACY-POLICY URL
     */
    public static final String URL_PRIVACY_POLICY = "https://www.rcontacts" +
            ".in/global/privacy-policy";


    public static final String WS_FACEBOOK_URL = "https://www.facebook.com/RContacts/";
    public static final String WS_TWITTER_URL = "https://twitter.com/RContactsApp";


    public static final String RESPONSE_STATUS_TRUE = "true";

    public static final String REQ_GOOGLE_TEXT_BY_LOCATIONS = "https://maps.googleapis" +
            ".com/maps/api/place/textsearch/json?";

    /**
     * API Headers
     */
    public static final String REQ_HEADER = "rcAuthToken";
    public static final String REQ_THROTTLING_HEADER = "Retry-After";

    /**
     * API End Points
     */
    public static final String REQ_CHECK_NUMBER = "check-number";
    public static final String REQ_OTP_CONFIRMED = "confirm-otp";
    public static final String REQ_PROFILE_REGISTRATION = "profile-registration";
    public static final String REQ_REGISTER_WITH_SOCIAL_MEDIA = "register-with-social-media";
    public static final String REQ_SAVE_PASSWORD = "save-password";
    public static final String REQ_CHECK_LOGIN = "check-login";
    public static final String REQ_LOGIN_WITH_SOCIAL_MEDIA = "login-with-social-media";
    public static final String REQ_COUNTRY_CODE_DETAIL = "country-code-detail";
    public static final String REQ_MSP_DELIVERY_TIME = "msp-delivery-time";
    public static final String REQ_STORE_DEVICE_DETAILS = "store-device-details";
    public static final String REQ_UPLOAD_CONTACTS = "uploadContacts";
    public static final String REQ_UPLOAD_CALL_LOGS = "sync-call-log";
    public static final String REQ_UPLOAD_SMS_LOGS = "sync-sms-log";
    public static final String REQ_REVERSE_GEO_CODING_ADDRESS = "req_reverse_geo_coding_address";
    public static final String REQ_GEO_CODING_ADDRESS = "req_geo_coding_address";
    public static final String REQ_GET_CALL_LOG_HISTORY_REQUEST = "call-history";
    public static final String REQ_GET_GLOBAL_SEARCH_RECORDS = "search";
    public static final String REQ_MARK_AS_FAVOURITE = "mark-as-favourite";
    public static final String REQ_PROFILE_RATING = "profile-rating";
    public static final String REQ_RCP_PROFILE_SHARING = "rcp-profile-sharing";
    public static final String REQ_SAVE_PACKAGE = "save-package";
    public static final String REQ_SEND_INVITATION = "send-invitation";
    public static final String REQ_ADD_PROFILE_VISIT = "add-profile-visit";
    public static final String REQ_GET_PROFILE_DETAILS = "get-profile-details";
    public static final String REQ_PROFILE_UPDATE = "profile-update";
    public static final String REQ_SET_PRIVACY_SETTING = "set-privacy-setting";
    public static final String REQ_GET_PROFILE_PRIVACY_REQUEST = "get-profile-privacy-request";
    public static final String REQ_PROFILE_PRIVACY_REQUEST = "profile-privacy-request";
    public static final String REQ_ADD_EVENT_COMMENT = "event-comment";
    public static final String REQ_GET_EVENT_COMMENT = "get-event-comment";
    public static final String REQ_GET_RCONTACT_UPDATES = "get-rcontact-update";
    public static final String REQ_GET_PROFILE_DATA = "get-global-profile";
    public static final String REQ_CONTACT_US = "contact-us";
    public static final String REQ_MAKE_SPAM = "make-spam";
    public static final String REQ_GET_CONTACT_REQUEST = "get-contact-request";
    public static final String REQ_GET_RATING_DETAILS = "get-rating-details";
    public static final String REQ_GET_COMMENT_DETAILS = "get-comment-details";
    public static final String REQ_GET_RCP_CONTACT = "get-rcp-contact";

}
