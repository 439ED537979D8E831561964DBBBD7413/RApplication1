package com.rawalinfocom.rcontact.constants;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Defines several constants related to Web Services
 */

public class WsConstants {

    // Monal
//    public static final String WS_ROOT = "http://10.0.21.163/api/v1/";
//    public static final String WS_ROOT = "http://54.202.191.41/api/v1/";
//    public static final String WS_ROOT = "http://10.0.21.123/api/v1/";

    // QA Server
//    public static final String WS_ROOT = "http://54.186.222.230/api/v1/";
    public static final String WS_ROOT = "http://apiqa.rcontacts.in/api/v1/";

    // Aniruddh
//    public static final String WS_ROOT = "http://10.0.21.122/api/v1/";
    //  public static final String WS_ROOT = "http://10.0.21.122/api/v1/";

    //    Maulik
//    public static final String WS_ROOT = "http://10.0.51.119/api/v1/";

    // Shailesh
//    public static final String WS_ROOT = "http://10.0.21.14:8001/api/v1/";

    //Jignesh
//    public static final String WS_ROOT = "http://10.0.21.15/api/v1/";
//    public static final String WS_ROOT = "  http://10.0.21.15:8000/api/v1/";

    // Satyam bhai
//    public static final String WS_ROOT = "http://10.0.21.115:/api/v1/";

    // Pooja
//    public static final String WS_ROOT = "http://10.0.21.182:/api/v1/";

    // Vishal
//    public static final String WS_ROOT = "http://10.0.21.121:/api/v1/";
//    public static final String WS_ROOT = "http://10.0.51.101:/api/v1/";

    // Swati
//    public static final String WS_ROOT = "http://10.0.21.131/api/v1/";

    // Vijay
//     public static final String WS_ROOT = "http://10.0.21.16/api/v1/";


    // Web Server
    //public static final String WS_PROFILE_VIEW_ROOT = "http://web.rcontacts.in/global/public/";

    // QA Server
    public static final String WS_PROFILE_VIEW_ROOT = "http://webqa.rcontacts.in/global/public/";

    public static final String RESPONSE_STATUS_TRUE = "true";

    public static final String REQ_GOOGLE_TEXT_BY_LOCATIONS = "https://maps.googleapis" +
            ".com/maps/api/place/textsearch/json?";

    public static final String REQ_COUNTRY_CODE_DETAIL = "country-code-detail"; // not used
    public static final String REQ_SEND_OTP = "send_otp";  // working
    public static final String REQ_MSP_DELIVERY_TIME = "msp-delivery-time"; // not required to test
    public static final String REQ_OTP_CONFIRMED = "otp_confirmed"; // working
    public static final String REQ_STORE_DEVICE_DETAILS = "store-device-details"; //working
    public static final String REQ_PROFILE_REGISTRATION = "profile_registration"; // working
    public static final String REQ_UPLOAD_CONTACTS = "uploadContacts"; //working
    public static final String REQ_UPLOAD_CALL_LOGS = "sync-call-log"; // working
    public static final String REQ_UPLOAD_SMS_LOGS = "sync-sms-log";   // working
    public static final String REQ_REVERSE_GEO_CODING_ADDRESS = "req_reverse_geo_coding_address"; //google api, working
    public static final String REQ_GEO_CODING_ADDRESS = "req_geo_coding_address";//google api, working

    public static final String REQ_GET_CALL_LOG_HISTORY_REQUEST = "call-history"; //anirudh
    public static final String REQ_GET_GLOBAL_SEARCH_RECORDS = "search"; //anirudh

    public static final String REQ_MARK_AS_FAVOURITE = "mark-as-favourite"; //working
    public static final String REQ_PROFILE_RATING = "profile-rating"; //working
    public static final String REQ_RCP_PROFILE_SHARING = "rcp-profile-sharing"; //functionality pending
    public static final String REQ_SEND_INVITATION = "send-invitation"; //working
    public static final String REQ_ADD_PROFILE_VISIT = "add-profile-visit"; //working
    public static final String REQ_PROFILE_UPDATE = "profile-update"; // monal

    public static final String REQ_SET_PRIVACY_SETTING = "set-privacy-setting"; //maulik
    public static final String REQ_GET_PROFILE_PRIVACY_REQUEST = "get-profile-privacy-request"; //maulik
    public static final String REQ_PROFILE_PRIVACY_REQUEST = "profile-privacy-request"; //maulik
    public static final String REQ_ADD_EVENT_COMMENT = "event-comment"; //maulik
    public static final String REQ_GET_EVENT_COMMENT = "get-event-comment"; //maulik
    public static final String REQ_GET_RCONTACT_UPDATES = "get-rcontact-update"; //maulik


}
