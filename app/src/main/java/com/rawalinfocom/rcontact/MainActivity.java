package com.rawalinfocom.rcontact;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.account.SlideMenuAccounts;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.calldialer.DialerActivity;
import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.ContactStorageConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ContactsFragment;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.PhoneBookCallLogs;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableNotificationStateMaster;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.database.TableRCContactRequest;
import com.rawalinfocom.rcontact.database.TableSpamDetailMaster;
import com.rawalinfocom.rcontact.database.TableWebsiteMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Address;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.ContactRequestResponseDataItem;
import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.ImAccount;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.ProfileEmailMapping;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.RatingRequestResponseDataItem;
import com.rawalinfocom.rcontact.model.SpamDataType;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.Website;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.notifications.EventsActivity;
import com.rawalinfocom.rcontact.notifications.NotificationsActivity;
import com.rawalinfocom.rcontact.notifications.RatingHistory;
import com.rawalinfocom.rcontact.notifications.TimelineActivity;
import com.rawalinfocom.rcontact.receivers.NetworkConnectionReceiver;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements WsResponseListener, View.OnClickListener {

    private final int CALL_LOG_CHUNK = 100;

    private static final String TAG = "MainActivity";

    @BindView(R.id.relative_root_contacts_main)
    RelativeLayout relativeRootContactsMain;
    Toolbar toolbar;
    ImageView imageNotification;
    LinearLayout badgeLayout;
    TextView badgeTextView;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;
    TabLayout tabMain;

    ContactsFragment contactsFragment;
    CallLogFragment callLogFragment;

    private PhoneBookContacts phoneBookContacts;
    private SyncCallLogAsyncTask syncCallLogAsyncTask;
    private ReSyncContactAsyncTask reSyncContactAsyncTask;
    private GetSpamAndRCPDetailAsyncTask getSpamAndRCPDetailAsyncTask;
    private callPullMechanismService callPullMechanismService;
    NetworkConnectionReceiver networkConnectionReceiver;
    MaterialDialog permissionConfirmationDialog;
    MaterialDialog callConfirmationDialog;

    RContactApplication rContactApplication;

    private ArrayList<ProfileData> arrayListReSyncUserContact;
    private ArrayList<CallLogType> callLogTypeArrayListMain;
    //    private ArrayList<CallLogType> callLogTypeListForGlobalProfile;
    private ArrayList<String> callListForSpamCount;
    ArrayList<CallLogType> callLogsListbyChunck;

    private String[] requiredPermissions = {Manifest.permission.READ_CONTACTS, Manifest
            .permission.READ_CALL_LOG/*, Manifest.permission.READ_SMS*/};

    private String currentStamp;
    int logsSyncedCount = 0;
    boolean isCompaseIcon = false;
    int tabPosition = -1;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLanguage();

        setContentView(R.layout.activity_contacts_main);

        ButterKnife.bind(this);
        rContactApplication = (RContactApplication) getApplicationContext();
        phoneBookContacts = new PhoneBookContacts(this);
        callLogTypeArrayListMain = new ArrayList<>();
//        smsLogTypeArrayListMain = new ArrayList<>();
        callListForSpamCount = new ArrayList<>();
//        CallLogFragment.callLogTypeReceiver = new CallLogType();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        networkConnectionReceiver = new NetworkConnectionReceiver();
        init();
        registerBroadcastReceiver();
        registerLocalBroadCastReceiver();
        callPullMechanismService = new callPullMechanismService();
        callPullMechanismService.execute();
    }

    public void setLanguage() {

        String defaultLANG = Locale.getDefault().getLanguage();

        String languageToLoad = "en"; // your language

        switch (Utils.getStringPreference(MainActivity.this, AppConstants.PREF_APP_LANGUAGE, "0")) {

            case "0":
                languageToLoad = "en";
                break;

            case "1":
                languageToLoad = "hi";
                break;

            case "2":
                languageToLoad = "gu";
                break;
        }

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavigationHeaderData();
    }

    private class callPullMechanismService extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {

                if (Utils.isNetworkAvailable(MainActivity.this)
                        && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CONTACT_SYNCED, false)
                        && (Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CALL_LOG_SYNCED,
                        false))) {

//                    System.out.println("RContact callPullMechanismService ");

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

                    String toDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));

                    long compare = Long.parseLong(Utils.getStringPreference(MainActivity.this, AppConstants.KEY_API_CALL_TIME, String.valueOf(System.currentTimeMillis())));
                    String fromDate = simpleDateFormat.format(new Date(compare));

                    Date currDate = simpleDateFormat.parse(toDate);
                    Date compareDate = simpleDateFormat.parse(fromDate);

                    long difference = currDate.getTime() - compareDate.getTime();

                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;
                    long daysInMilli = hoursInMilli * 24;

                    long elapsedDays = difference / daysInMilli;
                    difference = difference % daysInMilli;

                    long elapsedHours = difference / hoursInMilli;
                    difference = difference % hoursInMilli;

                    long elapsedMinutes = difference / minutesInMilli;

//                    if (elapsedDays > 0 || elapsedHours > 8) {
                    if (elapsedDays > 0 || elapsedHours > 0 || elapsedMinutes > 5) {

                        if (Utils.getBooleanPreference(MainActivity.this, AppConstants.KEY_IS_FIRST_TIME, false)) {
//                            System.out.println("RContact callPullMechanismService first time");
                            fromDate = "";
                        } else {
                            fromDate = Utils.getStringPreference(MainActivity.this, AppConstants.KEY_API_CALL_TIME_STAMP, "");
                        }
                        toDate = "";

                        RCPContactServiceCall(fromDate, WsConstants.REQ_GET_RCP_CONTACT);
                        pullMechanismServiceCall(fromDate, toDate, WsConstants.REQ_GET_CONTACT_REQUEST);
                        pullMechanismServiceCall(fromDate, toDate, WsConstants.REQ_GET_RATING_DETAILS);
                        pullMechanismServiceCall(fromDate, toDate, WsConstants.REQ_GET_COMMENT_DETAILS);
                    }
                }

            } catch (Exception e) {
//                System.out.println("RContact PullMechanismService call error --> " + e.getMessage());
            }

            return null;
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationCount();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.nav_ll_account:
                startActivityIntent(MainActivity.this, SlideMenuAccounts.class, null);
                break;
            case R.id.nav_ll_timeline:
                startActivityIntent(MainActivity.this, TimelineActivity.class, null);
                break;
            case R.id.nav_ll_events:
                startActivityIntent(MainActivity.this, EventsActivity.class, null);
                break;
            case R.id.nav_ll_rating_history:
                startActivityIntent(this, RatingHistory.class, new Bundle());
                break;
            case R.id.nav_ll_invite:
                startActivityIntent(MainActivity.this, ContactListingActivity.class, null);
                break;
            case R.id.nav_ll_share:

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = AppConstants.PLAY_STORE_LINK + getPackageName();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));

                break;
            case R.id.nav_ll_settings:
                startActivityIntent(MainActivity.this, SettingsActivity.class, null);
                break;
            case R.id.nav_ll_rate_us:
                rateUs();
                break;
            case R.id.nav_ll_about:
                startActivityIntent(MainActivity.this, AboutHelpActivity.class, null);
                break;
            case R.id.nav_ll_feedback:

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(WsConstants.URL_FEEDBACK));
                startActivity(i);
                break;

            case R.id.nav_ll_export:

                String exportedFileName = Utils.exportDB(this);
                if (exportedFileName != null) {
                    File fileLocation = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath(), exportedFileName);
                    Uri path = Uri.fromFile(fileLocation);
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("vnd.android.cursor.dir/email");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Database");
                    startActivity(Intent.createChooser(emailIntent, getString(R.string
                            .str_send_email)));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.db_dump_failed),
                            Toast.LENGTH_SHORT)
                            .show();
                }

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            // <editor-fold desc="REQ_GET_RCP_CONTACT">
            if (serviceType.contains(WsConstants.REQ_GET_RCP_CONTACT)) {
                WsResponseObject getRCPContactUpdateResponse = (WsResponseObject) data;
                if (getRCPContactUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getRCPContactUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    if (!Utils.isArraylistNullOrEmpty(getRCPContactUpdateResponse
                            .getArrayListUserRcProfile())) {

                        try {

                            /* Store Unique Contacts to ProfileMobileMapping */
                            storeToMobileMapping(getRCPContactUpdateResponse
                                    .getArrayListUserRcProfile());

                                /* Store Unique Emails to ProfileEmailMapping */
                            storeToEmailMapping(getRCPContactUpdateResponse
                                    .getArrayListUserRcProfile());

                                /* Store Profile Details to respective Table */
                            storeProfileDataToDb(getRCPContactUpdateResponse
                                    .getArrayListUserRcProfile(), getRCPContactUpdateResponse
                                    .getArrayListMapping());

                        } catch (Exception e) {
                            System.out.println("RContact error");
                        }
                    }
                    if (!Utils.isArraylistNullOrEmpty(getRCPContactUpdateResponse
                            .getArrayListMapping())) {
                        removeRemovedDataFromDb(getRCPContactUpdateResponse
                                .getArrayListMapping());
                    }

                    if (!StringUtils.isEmpty(getRCPContactUpdateResponse.getTimestamp())) {
                        Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String.valueOf(System.currentTimeMillis()));
                        Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME_STAMP, getRCPContactUpdateResponse.getTimestamp());
                        Utils.setBooleanPreference(this, AppConstants.KEY_IS_FIRST_TIME, false);
                    }

                } else {
                    if (getRCPContactUpdateResponse != null) {
                        System.out.println("RContact error --> " + getRCPContactUpdateResponse.getMessage());
                    } else {
                        System.out.println("RContact error --> getContactUpdateResponse null");
                    }
                }
            }

            // <editor-fold desc="REQ_GET_CONTACT_REQUEST">
            if (serviceType.contains(WsConstants.REQ_GET_CONTACT_REQUEST)) {
                WsResponseObject getContactUpdateResponse = (WsResponseObject) data;
                if (getContactUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getContactUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    storeContactRequestResponseToDB(getContactUpdateResponse, getContactUpdateResponse.getRequestData(), getContactUpdateResponse.getResponseData());

                } else {
                    if (getContactUpdateResponse != null) {
                        System.out.println("RContact error --> " + getContactUpdateResponse.getMessage());
                    } else {
                        System.out.println("RContact error --> getContactUpdateResponse null");
                    }
                }
            }

            // <editor-fold desc="REQ_GET_RATING_DETAILS">
            if (serviceType.contains(WsConstants.REQ_GET_RATING_DETAILS)) {
                WsResponseObject getRatingUpdateResponse = (WsResponseObject) data;
                if (getRatingUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getRatingUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    storeRatingRequestResponseToDB(getRatingUpdateResponse, getRatingUpdateResponse.getRatingReceive(), getRatingUpdateResponse.getRatingDone()
                            , getRatingUpdateResponse.getRatingDetails());

                } else {
                    if (getRatingUpdateResponse != null) {
                        System.out.println("RContact error --> " + getRatingUpdateResponse.getMessage());
                    } else {
                        System.out.println("RContact error --> getRatingUpdateResponse null");
                    }
                }
            }

            // <editor-fold desc="REQ_GET_COMMENT_DETAILS">
            if (serviceType.contains(WsConstants.REQ_GET_COMMENT_DETAILS)) {
                WsResponseObject getCommentUpdateResponse = (WsResponseObject) data;
                if (getCommentUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getCommentUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    storeCommentRequestResponseToDB(getCommentUpdateResponse, getCommentUpdateResponse.getCommentReceive(), getCommentUpdateResponse.getCommentDone());

                } else {
                    if (getCommentUpdateResponse != null) {
                        System.out.println("RContact error --> " + getCommentUpdateResponse.getMessage());
                    } else {
                        System.out.println("RContact error --> getCommentUpdateResponse null");
                    }
                }
            }

            // <editor-fold desc="REQ_ADD_PROFILE_VISIT">
            if (serviceType.contains(WsConstants.REQ_ADD_PROFILE_VISIT)) {
                WsResponseObject bgProfileVisitResponse = (WsResponseObject) data;
                if (bgProfileVisitResponse != null && StringUtils.equalsIgnoreCase
                        (bgProfileVisitResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Utils.removePreference(this, AppConstants.PREF_PROFILE_VIEWS);
                } else {
                    if (bgProfileVisitResponse != null) {
                        Log.e("error response", bgProfileVisitResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "bgProfileVisitResponse null");
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_UPLOAD_CONTACTS">
            else if (serviceType.contains(WsConstants.REQ_UPLOAD_CONTACTS)) {
                WsResponseObject uploadContactResponse = (WsResponseObject) data;
                if (uploadContactResponse != null && StringUtils.equalsIgnoreCase
                        (uploadContactResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    if (!Utils.isArraylistNullOrEmpty(uploadContactResponse
                            .getArrayListUserRcProfile())) {

                                /* Store Unique Contacts to ProfileMobileMapping */
                        storeToMobileMapping(uploadContactResponse
                                .getArrayListUserRcProfile());

                                /* Store Unique Emails to ProfileEmailMapping */
                        storeToEmailMapping(uploadContactResponse
                                .getArrayListUserRcProfile());

                                /* Store Profile Details to respective Table */
                        storeProfileDataToDb(uploadContactResponse
                                .getArrayListUserRcProfile(), uploadContactResponse
                                .getArrayListMapping());
                    }
                    if (!Utils.isArraylistNullOrEmpty(uploadContactResponse
                            .getArrayListMapping())) {
                        removeRemovedDataFromDb(uploadContactResponse
                                .getArrayListMapping());
                    }
                    Log.i(TAG, "Sync Successful");

                    if (uploadContactResponse.getArrayListMapping().size() > 0) {
                        uploadContacts(uploadContactResponse.getResponseKey(), new ArrayList
                                <ProfileData>());
                    }
                    String currentTimeStamp = (StringUtils.split(serviceType, "_"))[1];
                    PhoneBookContacts phoneBookContacts = new PhoneBookContacts(this);
                    phoneBookContacts.saveRawIdsToPref();
                    Utils.setStringPreference(this, AppConstants.PREF_CONTACT_LAST_SYNC_TIME,
                            currentTimeStamp);
                } else {
                    if (uploadContactResponse != null) {
                        Log.e(TAG, uploadContactResponse.getMessage());
                    } else {
                        Log.e(TAG, "uploadContactResponse null");
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_UPLOAD_CALL_LOGS">
            else if (serviceType.equalsIgnoreCase(WsConstants.REQ_UPLOAD_CALL_LOGS)) {
                WsResponseObject callLogInsertionResponse = (WsResponseObject) data;
                if (callLogInsertionResponse != null && StringUtils.equalsIgnoreCase
                        (callLogInsertionResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    if (!StringUtils.isEmpty(callLogInsertionResponse.getCallDateAndTime())) {

                        try {

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale
                                    .getDefault());

                            if (Utils.getStringPreference
                                    (this, AppConstants.PREF_CALL_LOG_SYNC_TIME, "0").equals("0")) {

                                Utils.setStringPreference(this, AppConstants.PREF_CALL_LOG_SYNC_TIME,
                                        callLogInsertionResponse.getCallDateAndTime());

                            } else {

                                Date prefDate = new Date(sdf.parse(Utils.getStringPreference
                                        (this, AppConstants.PREF_CALL_LOG_SYNC_TIME, "0")).getTime());
                                Date currDate = new Date(sdf.parse(callLogInsertionResponse.getCallDateAndTime()).getTime());

                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale
                                        .getDefault());

                                String prefDateToCompare = dateFormat.format(prefDate);
                                String currDateToCompare = dateFormat.format(currDate);

                                Date curDate = dateFormat.parse(currDateToCompare);
                                Date preferenceDate = dateFormat.parse(prefDateToCompare);

                                if (curDate.getTime() > preferenceDate.getTime()) {
                                    Utils.setStringPreference(this, AppConstants.PREF_CALL_LOG_SYNC_TIME,
                                            callLogInsertionResponse.getCallDateAndTime());
                                }

                            }

                        } catch (Exception e) {
                            System.out.println("RContact call log sync error --> " + e.getMessage());
                        }
                    }

                    if (!StringUtils.isEmpty(callLogInsertionResponse.getCallLogRowId()))
                        Utils.setStringPreference(this, AppConstants.PREF_CALL_LOG_ROW_ID,
                                callLogInsertionResponse.getCallLogRowId());

                    Utils.setStringPreference(MainActivity.this, AppConstants.PREF_CALL_LOG_RESPONSE_KEY,
                            callLogInsertionResponse.getResponseKey());

                    ArrayList<CallLogType> callLogTypeArrayList = divideCallLogByChunck();

                    if (callLogTypeArrayList.size() > 0) {
                        logsSyncedCount = logsSyncedCount + callLogTypeArrayList.size();

                        Utils.setIntegerPreference(this, AppConstants.PREF_CALL_LOG_SYNCED_COUNT,
                                logsSyncedCount);

                        if (callLogTypeArrayList.size() < CALL_LOG_CHUNK) {

                            if (Utils.getBooleanPreference(this, AppConstants
                                    .PREF_CALL_LOG_SYNCED, false)) {
                                callLogSynced();
                            } else {
                                insertServiceCall(new ArrayList<CallLogType>());
                            }
                        } else {
                            if ((Utils.getIntegerPreference(this, AppConstants.PREF_CALL_LOG_SYNCED_COUNT, 0) >=
                                    Utils.getArrayListPreference(this, AppConstants.PREF_CALL_LOGS_ID_SET)
                                            .size())) {
                                insertServiceCall(new ArrayList<CallLogType>());
                            }
                        }

                    } else {
                        callLogSynced();
                    }
                } else {
                    if (callLogInsertionResponse != null) {
                        Log.e("error response", callLogInsertionResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "callLogInsertionResponse null");
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_GET_PROFILE_DATA">
            else if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_PROFILE_DATA)) {
                WsResponseObject getProfileDataResponse = (WsResponseObject) data;
                if (getProfileDataResponse != null && StringUtils.equalsIgnoreCase
                        (getProfileDataResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<SpamDataType> spamDataTypeList = getProfileDataResponse
                            .getSpamDataTypeArrayList();
                    if (spamDataTypeList.size() > 0) {
                        try {
                            TableSpamDetailMaster tableSpamDetailMaster = new
                                    TableSpamDetailMaster(getDatabaseHandler());
                            tableSpamDetailMaster.deleteSpamRecords();
                            tableSpamDetailMaster.insertSpamDetails(spamDataTypeList);
//                            callLogTypeListForGlobalProfile.clear();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Utils.setBooleanPreference(this, AppConstants
                            .PREF_GOT_ALL_PROFILE_DATA, true);

                    /*Intent localBroadcastIntent = new Intent(AppConstants
                            .ACTION_LOCAL_BROADCAST_SYNC_SMS);
                    LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                            .getInstance(MainActivity.this);
                    myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);*/

                } else {
                    if (getProfileDataResponse != null) {
                        Log.e("error response", getProfileDataResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "getProfileDataResponse null");
                    }
                }
            }
            //</editor-fold>
            /*// <editor-fold desc="REQ_UPLOAD_SMS_LOGS">
            else if (serviceType.equalsIgnoreCase(WsConstants.REQ_UPLOAD_SMS_LOGS)) {

                WsResponseObject smsLogInsertionResponse = (WsResponseObject) data;
                if (smsLogInsertionResponse != null && StringUtils.equalsIgnoreCase
                        (smsLogInsertionResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.setStringPreference(this, AppConstants.PREF_SMS_SYNC_TIME,
                    smsLogInsertionResponse.getSmsLogTimestamp());

                    if (Utils.getBooleanPreference(this, AppConstants
                            .PREF_SMS_SYNCED, false)) {
                        ArrayList<SmsDataType> temp = divideSmsLogByChunck(newSmsList);
                        if (temp.size() >= SMS_CHUNK) {
                        } else {
                            Log.e("onDeliveryResponse: ", "All SMS Logs Synced");
                        }
                    } else {
                        ArrayList<SmsDataType> callLogTypeArrayList = divideSmsLogByChunck();
                        if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0) {
//                            insertSMSLogServiceCall(callLogTypeArrayList);
                            logsSyncedCount = logsSyncedCount + callLogTypeArrayList.size();
                        } else {
//                            Toast.makeText(this,"All Call Logs Synced",Toast.LENGTH_SHORT).show();
                            Utils.setBooleanPreference(this, AppConstants
                                    .PREF_SMS_SYNCED, true);
                        }
                        Utils.setIntegerPreference(this, AppConstants.PREF_SMS_LOG_SYNCED_COUNT,
                                logsSyncedCount);
                    }
                } else {
                    if (smsLogInsertionResponse != null) {
                        Log.e("error response", smsLogInsertionResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "smsLogInsertionResponse null");
                    }
                }
            }
            //</editor-fold>*/
        } else {
            Log.e("error", error.toString());
        }
    }

    private void storeCommentRequestResponseToDB(WsResponseObject getCommentUpdateResponse, ArrayList<RatingRequestResponseDataItem> commentReceive,
                                                 ArrayList<RatingRequestResponseDataItem> commentDone) {

        try {
            TableCommentMaster tableCommentMaster = new TableCommentMaster(databaseHandler);

            // eventCommentDone
            for (int i = 0; i < commentDone.size(); i++) {

                RatingRequestResponseDataItem dataItem = commentDone.get(i);

                tableCommentMaster.addReply(dataItem.getCommentId(), dataItem.getReply(),
                        Utils.getLocalTimeFromUTCTime(dataItem.getReplyAt()), Utils
                                .getLocalTimeFromUTCTime(dataItem.getReplyAt()));
            }

            // eventCommentReceive
            for (int i = 0; i < commentReceive.size(); i++) {

                RatingRequestResponseDataItem dataItem = commentReceive.get(i);

                Comment comment = new Comment();
                comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
                comment.setCrmType(getResources().getString(R.string.str_tab_rating));
                comment.setCrmCloudPrId(dataItem.getCommentId());
                comment.setCrmRating(dataItem.getPrRatingStars());
                comment.setRcProfileMasterPmId(dataItem.getFromPmId());
                comment.setCrmComment(dataItem.getComment());
                comment.setCrmProfileDetails(dataItem.getName());
                comment.setCrmImage(dataItem.getPmProfilePhoto());
                comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()));
                comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()));
                String avgRating = dataItem.getProfileRating();
                String totalUniqueRater = dataItem.getTotalProfileRateUser();
                String toPmId = String.valueOf(dataItem.getToPmId());

                TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
                tableProfileMaster.updateUserProfileRating(toPmId, avgRating, totalUniqueRater);
                Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING, totalUniqueRater);
                Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, avgRating);

                tableCommentMaster.addComment(comment);

            }

        } catch (Exception e) {
            System.out.println("RContact storeCommentRequestResponseToDB error ");
        }

        if (!StringUtils.isEmpty(getCommentUpdateResponse.getTimestamp())) {
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String.valueOf(System.currentTimeMillis()));
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME_STAMP, getCommentUpdateResponse.getTimestamp());
            Utils.setBooleanPreference(this, AppConstants.KEY_IS_FIRST_TIME, false);
        }
    }

    private void storeRatingRequestResponseToDB(WsResponseObject getRatingUpdateResponse, ArrayList<RatingRequestResponseDataItem> ratingReceive,
                                                ArrayList<RatingRequestResponseDataItem> ratingDone, RatingRequestResponseDataItem ratingDetails) {

        try {
            TableCommentMaster tableCommentMaster = new TableCommentMaster(databaseHandler);

            // profileRatingComment
            for (int i = 0; i < ratingDone.size(); i++) {

                RatingRequestResponseDataItem dataItem = ratingDone.get(i);

                tableCommentMaster.addReply(dataItem.getPrId(), dataItem.getReply(),
                        Utils.getLocalTimeFromUTCTime(dataItem.getReplyAt()), Utils.getLocalTimeFromUTCTime(dataItem.getReplyAt()));
            }

            // profileRatingReply
            for (int i = 0; i < ratingReceive.size(); i++) {

                RatingRequestResponseDataItem dataItem = ratingReceive.get(i);

                Comment comment = new Comment();
                comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
                comment.setCrmType(getResources().getString(R.string.str_tab_rating));
                comment.setCrmCloudPrId(dataItem.getPrId());
                comment.setCrmRating(dataItem.getPrRatingStars());
                comment.setRcProfileMasterPmId(dataItem.getFromPmId());
                comment.setCrmComment(dataItem.getComment());
                comment.setCrmProfileDetails(dataItem.getName());
                comment.setCrmImage(dataItem.getPmProfilePhoto());
                comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()));
                comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()));

                tableCommentMaster.addComment(comment);
            }

            if (ratingDetails != null) {

                String avgRating = ratingDetails.getProfileRating();
                String totalUniqueRater = ratingDetails.getTotalProfileRateUser();
                String toPmId = getUserPmId();

                TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
                tableProfileMaster.updateUserProfileRating(toPmId, avgRating, totalUniqueRater);
                Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING, totalUniqueRater);
                Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, avgRating);
            }

        } catch (Exception e) {
            System.out.println("RContact storeRatingRequestResponseToDB error ");
        }

        if (!StringUtils.isEmpty(getRatingUpdateResponse.getTimestamp())) {
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String.valueOf(System.currentTimeMillis()));
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME_STAMP, getRatingUpdateResponse.getTimestamp());
            Utils.setBooleanPreference(this, AppConstants.KEY_IS_FIRST_TIME, false);
        }
    }

    private void storeContactRequestResponseToDB(WsResponseObject getContactUpdateResponse, ArrayList<ContactRequestResponseDataItem> requestData,
                                                 ArrayList<ContactRequestResponseDataItem> responseData) {

        try {

            TableRCContactRequest tableRCContactRequest = new TableRCContactRequest
                    (databaseHandler);

            for (int i = 0; i < requestData.size(); i++) {

                ContactRequestResponseDataItem dataItem = requestData.get(i);
                if (String.valueOf(dataItem.getCarPmIdTo()).equals(Utils.getStringPreference(this, AppConstants
                        .PREF_USER_PM_ID, "0"))
                        && String.valueOf(dataItem.getCarAccessPermissionStatus()).equals("0")) {
                    tableRCContactRequest.addRequest(AppConstants
                                    .COMMENT_STATUS_RECEIVED,
                            String.valueOf(dataItem.getCarId()),
                            dataItem.getCarMongodbRecordIndex(),
                            dataItem.getCarPmIdFrom(),
                            dataItem.getCarPpmParticular(),
                            Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()),
                            Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()),
                            dataItem.getName(), dataItem.getPmProfilePhoto());
                }
            }

            TableRCContactRequest tableRCContactRequest1 = new TableRCContactRequest
                    (databaseHandler);

            for (int i = 0; i < responseData.size(); i++) {

                ContactRequestResponseDataItem dataItem = responseData.get(i);

                if (String.valueOf(dataItem.getCarPmIdFrom()).equals(Utils.getStringPreference(this, AppConstants
                        .PREF_USER_PM_ID, "0"))
                        && MoreObjects.firstNonNull(dataItem.getCarAccessPermissionStatus(), "0").equals("0")) {
                    tableRCContactRequest1.addRequest(AppConstants
                                    .COMMENT_STATUS_SENT,
                            String.valueOf(dataItem.getCarId()),
                            dataItem.getCarMongodbRecordIndex(),
                            dataItem.getCarPmIdTo(),
                            dataItem.getCarPpmParticular(),
                            Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()),
                            Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()),
                            dataItem.getName(), dataItem.getPmProfilePhoto());
                }
            }

        } catch (Exception e) {
            System.out.println("RContact storeContactRequestResponseToDB error ");
        }

        if (!StringUtils.isEmpty(getContactUpdateResponse.getTimestamp())) {
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String.valueOf(System.currentTimeMillis()));
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME_STAMP, getContactUpdateResponse.getTimestamp());
            Utils.setBooleanPreference(this, AppConstants.KEY_IS_FIRST_TIME, false);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void callLogSynced() {


        if (Utils.getArrayListPreference(this, AppConstants
                .PREF_CALL_LOGS_ID_SET).size() > 0) {
            Utils.setIntegerPreference(this, AppConstants
                            .PREF_CALL_LOG_SYNCED_COUNT,
                    Utils.getArrayListPreference(this, AppConstants
                            .PREF_CALL_LOGS_ID_SET).size());

        }

        Utils.setBooleanPreference(this, AppConstants
                .PREF_CALL_LOG_SYNCED, true);

        if (callListForSpamCount != null) {
            if (callListForSpamCount.size() > 0) {
                if (Utils.getBooleanPreference(this, AppConstants
                        .PREF_GOT_ALL_PROFILE_DATA, false))
                    Utils.setBooleanPreference(this, AppConstants
                            .PREF_GOT_ALL_PROFILE_DATA, false);
            }
        }


        Intent localBroadcastIntent = new Intent(AppConstants
                .ACTION_LOCAL_BROADCAST_GET_GLOBAL_PROFILE_DATA);
        LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                .getInstance(MainActivity.this);
        myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
    }

    @Override
    protected void onDestroy() {
        if (syncCallLogAsyncTask != null)
            syncCallLogAsyncTask.cancel(true);
        /*if (syncSmsLogAsyncTask != null)
            syncSmsLogAsyncTask.cancel(true);*/
        if (callPullMechanismService != null)
            callPullMechanismService.cancel(true);
        if (reSyncContactAsyncTask != null)
            reSyncContactAsyncTask.cancel(true);
        if (getSpamAndRCPDetailAsyncTask != null)
            getSpamAndRCPDetailAsyncTask.cancel(true);
        if (networkConnectionReceiver != null) {
            unregisterBroadcastReceiver();
        }

        unRegisterLocalBroadCastReceiver();

        Utils.setBooleanPreference(this, AppConstants
                .PREF_SMS_LOG_STARTS_FIRST_TIME, true);

        super.onDestroy();
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        imageNotification = (ImageView) toolbar.findViewById(R.id.image_notification);
        ImageView imageViewSearch = (ImageView) toolbar.findViewById(R.id.image_search);
        badgeLayout = (LinearLayout) toolbar.findViewById(R.id.badge_layout);
        badgeTextView = (TextView) toolbar.findViewById(R.id.badge_count);
        imageNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIntent(MainActivity.this, NotificationsActivity.class, null);
            }
        });

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIntent(MainActivity.this, SearchActivity.class, null);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCompaseIcon)
                    openDialer();
                /* else {
                   AppConstants.isComposingSMS = true;
                    openSMSComposerPage();
                }*/
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // setNavigationHeaderData();
        setNavigationListData();

        tabMain = (TabLayout) findViewById(R.id.tab_main);

        bindWidgetsWithAnEvent();
        setupTabLayout();
        Utils.changeTabsFont(this, tabMain);

    }

    private void setNavigationHeaderData() {

        LinearLayout mainContent = (LinearLayout) navigationView.findViewById(R.id.main_content);
        TextView textUserName = (TextView) navigationView.findViewById(R.id.text_user_name);
        TextView textNumber = (TextView) navigationView.findViewById(R.id.text_number);
        TextView textRatingCount = (TextView) navigationView.findViewById(R.id.text_rating_count);
        RatingBar ratingUser = (RatingBar) navigationView.findViewById(R.id.rating_user);
        ImageView userProfileImage = (ImageView) navigationView.findViewById(R.id.userProfileImage);

        TableMobileMaster tableMobileMaster = new TableMobileMaster(databaseHandler);
        String number = tableMobileMaster.getUserMobileNumber(getUserPmId());

        textUserName.setTypeface(Utils.typefaceSemiBold(MainActivity.this));
        textNumber.setTypeface(Utils.typefaceRegular(MainActivity.this));
        textRatingCount.setTypeface(Utils.typefaceBold(MainActivity.this));

        textUserName.setText(Utils.getStringPreference(this, AppConstants.PREF_USER_NAME, ""));
        textNumber.setText(number);
        textRatingCount.setText(Utils.getStringPreference(this, AppConstants
                .PREF_USER_TOTAL_RATING, ""));
        textUserName.setTypeface(Utils.typefaceSemiBold(MainActivity.this));
        textNumber.setTypeface(Utils.typefaceRegular(MainActivity.this));
        textRatingCount.setTypeface(Utils.typefaceBold(MainActivity.this));

        textUserName.setText(Utils.getStringPreference(this, AppConstants.PREF_USER_NAME, ""));
        textNumber.setText(number);
        textRatingCount.setText(Utils.getStringPreference(this, AppConstants
                .PREF_USER_TOTAL_RATING, "0"));

        if (!StringUtils.isEmpty(Utils.getStringPreference(this, AppConstants
                .PREF_USER_RATING, "")))
            ratingUser.setRating(Float.parseFloat(Utils.getStringPreference(this, AppConstants
                    .PREF_USER_RATING, "0")));
        else
            ratingUser.setRating(0f);

        final String thumbnailUrl = Utils.getStringPreference(this, AppConstants.PREF_USER_PHOTO,
                "");
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Glide.with(MainActivity.this)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(MainActivity.this))
                    .override(500, 500)
                    .into(userProfileImage);
        } else {
            userProfileImage.setImageResource(R.drawable.home_screen_profile);
        }

        Utils.setRatingColor(MainActivity.this, ratingUser);

        mainContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_PM_ID, getUserPmId());
                bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, "");
                bundle.putString(AppConstants.EXTRA_CONTACT_NAME, Utils.getStringPreference
                        (MainActivity.this, AppConstants.PREF_USER_NAME, ""));
                bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, thumbnailUrl);
                bundle.putInt(AppConstants.EXTRA_CONTACT_POSITION, 1);
                startActivityIntent(MainActivity.this, ProfileDetailActivity.class, bundle);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

//        Utils.setBooleanPreference(this, AppConstants.PREF_USER_PROFILE_UPDATE, false);
    }

    private void setNavigationListData() {

        TextView nav_txt_account = (TextView) navigationView.findViewById(R.id.nav_txt_account);
        TextView nav_txt_timeline = (TextView) navigationView.findViewById(R.id.nav_txt_timeline);
        TextView nav_txt_events = (TextView) navigationView.findViewById(R.id.nav_txt_events);
        TextView nav_txt_rating = (TextView) navigationView.findViewById(R.id.nav_txt_rating);
        TextView nav_txt_invite = (TextView) navigationView.findViewById(R.id.nav_txt_invite);
        TextView nav_txt_share = (TextView) navigationView.findViewById(R.id.nav_txt_share);
        TextView nav_txt_settings = (TextView) navigationView.findViewById(R.id.nav_txt_settings);
        TextView nav_txt_rate = (TextView) navigationView.findViewById(R.id.nav_txt_rate);
        TextView nav_txt_about = (TextView) navigationView.findViewById(R.id.nav_txt_about);
        TextView nav_txt_export = (TextView) navigationView.findViewById(R.id.nav_txt_export);
        TextView nav_txt_feedback = (TextView) navigationView.findViewById(R.id.nav_txt_feedback);

        LinearLayout nav_ll_account = (LinearLayout) navigationView.findViewById(R.id
                .nav_ll_account);
        LinearLayout nav_ll_timeline = (LinearLayout) navigationView.findViewById(R.id
                .nav_ll_timeline);
        LinearLayout nav_ll_events = (LinearLayout) navigationView.findViewById(R.id.nav_ll_events);
        LinearLayout nav_ll_rating = (LinearLayout) navigationView.findViewById(R.id
                .nav_ll_rating_history);
        LinearLayout nav_ll_invite = (LinearLayout) navigationView.findViewById(R.id.nav_ll_invite);
        LinearLayout nav_ll_share = (LinearLayout) navigationView.findViewById(R.id.nav_ll_share);
        LinearLayout nav_ll_settings = (LinearLayout) navigationView.findViewById(R.id
                .nav_ll_settings);
        LinearLayout nav_ll_rate = (LinearLayout) navigationView.findViewById(R.id.nav_ll_rate_us);
        LinearLayout nav_ll_about = (LinearLayout) navigationView.findViewById(R.id.nav_ll_about);
        LinearLayout nav_ll_export = (LinearLayout) navigationView.findViewById(R.id.nav_ll_export);
        LinearLayout nav_ll_feedback = (LinearLayout) navigationView.findViewById(R.id
                .nav_ll_feedback);

        nav_ll_account.setOnClickListener(this);
        nav_ll_timeline.setOnClickListener(this);
        nav_ll_events.setOnClickListener(this);
        nav_ll_rating.setOnClickListener(this);
        nav_ll_invite.setOnClickListener(this);
        nav_ll_share.setOnClickListener(this);
        nav_ll_settings.setOnClickListener(this);
        nav_ll_rate.setOnClickListener(this);
        nav_ll_about.setOnClickListener(this);
        nav_ll_export.setOnClickListener(this);
        nav_ll_feedback.setOnClickListener(this);

        nav_txt_export.setVisibility(View.GONE);

        nav_txt_account.setTypeface(Utils.typefaceIcons(this));
        nav_txt_account.setText(R.string.im_icon_user);
        nav_txt_timeline.setTypeface(Utils.typefaceIcons(this));
        nav_txt_timeline.setText(R.string.im_icon_timeline);
        nav_txt_events.setTypeface(Utils.typefaceIcons(this));
        nav_txt_events.setText(R.string.im_icon_events);
        nav_txt_rating.setTypeface(Utils.typefaceIcons(this));
        nav_txt_rating.setText(R.string.im_icon_rating_history);
        nav_txt_invite.setTypeface(Utils.typefaceIcons(this));
        nav_txt_invite.setText(R.string.im_icon_invite_contact);
        nav_txt_share.setTypeface(Utils.typefaceIcons(this));
        nav_txt_share.setText(R.string.im_icon_share);
        nav_txt_settings.setTypeface(Utils.typefaceIcons(this));
        nav_txt_settings.setText(R.string.im_icon_setting);
        nav_txt_rate.setTypeface(Utils.typefaceIcons(this));
        nav_txt_rate.setText(R.string.im_icon_rate_us);
        nav_txt_about.setTypeface(Utils.typefaceIcons(this));
        nav_txt_about.setText(R.string.im_icon_about_help);
        nav_txt_export.setTypeface(Utils.typefaceIcons(this));
        nav_txt_export.setText(R.string.im_icon_about_help);
        nav_txt_feedback.setTypeface(Utils.typefaceIcons(this));
        nav_txt_feedback.setText(R.string.im_icon_about_help);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute() {
        boolean contacts = ContextCompat.checkSelfPermission(MainActivity.this,
                requiredPermissions[0]) ==
                PackageManager.PERMISSION_GRANTED;
        boolean logs = ContextCompat.checkSelfPermission(MainActivity.this,
                requiredPermissions[1]) ==
                PackageManager.PERMISSION_GRANTED;
        /*boolean smsLogs = ContextCompat.checkSelfPermission(MainActivity.this,
                requiredPermissions[2]) ==
                PackageManager.PERMISSION_GRANTED;*/
        if (logs) {
            if (Utils.isNetworkAvailable(this)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)
                    && !Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED,
                    false)) {
                if (syncCallLogAsyncTask != null && syncCallLogAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                    System.out.println("RContact syncCallLogAsyncTask ---> running");
                } else {
                    syncCallLogAsyncTask = new SyncCallLogAsyncTask();
                    syncCallLogAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }

        if (Utils.isNetworkAvailable(this)
                && Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)
                && Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, false)
                && !Utils.getBooleanPreference(this, AppConstants.PREF_GOT_ALL_PROFILE_DATA,
                false)) {
            getSpamAndRCPDetailAsyncTask = new GetSpamAndRCPDetailAsyncTask();
            getSpamAndRCPDetailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

        /*if (smsLogs) {
            if (Utils.isNetworkAvailable(this)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, false)
                    && !Utils.getBooleanPreference(this, AppConstants.PREF_GOT_ALL_PROFILE_DATA,
                    false)) {
                getSpamAndRCPDetailAsyncTask = new GetSpamAndRCPDetailAsyncTask();
                getSpamAndRCPDetailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } else {
                if (Utils.isNetworkAvailable(this)
                        && Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)
                        && Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED,
                        false)
                        && Utils.getBooleanPreference(this, AppConstants
                        .PREF_GOT_ALL_PROFILE_DATA, false)
                        && !Utils.getBooleanPreference(this, AppConstants.PREF_SMS_SYNCED, false)) {
                    syncSmsLogAsyncTask = new SyncSmsLogAsyncTask();
                    syncSmsLogAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }

        }*/
        if (contacts) {
            if (Utils.isNetworkAvailable(this)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)
                    && (Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED,
                    false) || !logs)
                    /*&& (Utils.getBooleanPreference(this, AppConstants.PREF_SMS_SYNCED, false) ||
                    !smsLogs)*/) {
                reSyncContactAsyncTask = new ReSyncContactAsyncTask();
                reSyncContactAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    private void updateNotificationCount() {
        int count = getNotificationCount(databaseHandler);
        if (count > 0) {
            badgeLayout.setVisibility(View.VISIBLE);
            badgeTextView.setText(String.valueOf(count));
        } else {
            badgeLayout.setVisibility(View.GONE);
        }
        count = getTimeLineNotificationCount(databaseHandler);
//        LinearLayout view = (LinearLayout) navigationView.getMenu().findItem(R.id
//                .nav_user_timeline).getActionView();
        TextView badge_count = (TextView) navigationView.findViewById(R.id.badge_count);
        if (count > 0) {
            badge_count.setVisibility(View.VISIBLE);
            badge_count.setText(String.valueOf(count));
        } else {
            badge_count.setVisibility(View.GONE);
        }
    }

    private void storeToMobileMapping(ArrayList<ProfileDataOperation> profileData) {

        try {

            TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                    (databaseHandler);
            ArrayList<ProfileMobileMapping> arrayListProfileMobileMapping = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(profileData)) {
                for (int j = 0; j < profileData.size(); j++) {
                    ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
                    profileMobileMapping.setMpmMobileNumber("+" + profileData.get(j)
                            .getVerifiedMobileNumber());
                    profileMobileMapping.setMpmCloudMnmId(profileData.get(j)
                            .getMnmCloudId());
                    profileMobileMapping.setMpmCloudPmId(profileData.get(j).getRcpPmId());
                    profileMobileMapping.setMpmIsRcp("1");
                    arrayListProfileMobileMapping.add(profileMobileMapping);
                }
            }
            tableProfileMobileMapping.addArrayProfileMobileMapping(arrayListProfileMobileMapping);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeToEmailMapping(ArrayList<ProfileDataOperation> profileData) {

        try {
            TableProfileEmailMapping tableProfileEmailMapping = new TableProfileEmailMapping
                    (databaseHandler);
            ArrayList<ProfileEmailMapping> arrayListProfileEmailMapping = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(profileData)) {
                for (int j = 0; j < profileData.size(); j++) {
                    if (!Utils.isArraylistNullOrEmpty(profileData.get(j).getVerifiedEmailIds())) {
                        for (int k = 0; k < profileData.get(j).getVerifiedEmailIds().size(); k++) {
                            if (!tableProfileEmailMapping.getIsEmailIdExists(profileData.get(j)
                                    .getVerifiedEmailIds().get(k).getEmEmailId())) {
                                ProfileEmailMapping profileEmailMapping = new ProfileEmailMapping();
                                profileEmailMapping.setEpmEmailId(profileData.get(j)
                                        .getVerifiedEmailIds().get(k).getEmEmailId());
                                profileEmailMapping.setEpmCloudEmId(String.valueOf(profileData
                                        .get(j).getVerifiedEmailIds().get(k).getEmId()));
                                profileEmailMapping.setEpmCloudPmId(profileData.get(j).getRcpPmId
                                        ());
                                profileEmailMapping.setEpmIsRcp("1");

                                arrayListProfileEmailMapping.add(profileEmailMapping);
                            }
                        }

                    }
                }

            }
            tableProfileEmailMapping.addArrayProfileEmailMapping(arrayListProfileEmailMapping);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeProfileDataToDb(ArrayList<ProfileDataOperation> profileData,
                                      ArrayList<ProfileData> mapping) {

        try {
            // Hashmap with key as rcpId and value as rawId/s
            HashMap<String, String> mapLocalRcpId = new HashMap<>();

            for (int i = 0; i < mapping.size(); i++) {
                for (int j = 0; j < mapping.get(i).getRcpPmId().size(); j++) {
                    String phonebookRawId;
                    if (mapLocalRcpId.containsKey(mapping.get(i).getRcpPmId().get(j))) {
                        phonebookRawId = mapLocalRcpId.get(mapping.get(i).getRcpPmId().get(j)) +
                                "," + mapping.get(i).getLocalPhoneBookId();
                    } else {
                        phonebookRawId = mapping.get(i).getLocalPhoneBookId();
                    }

                    mapLocalRcpId.put(mapping.get(i).getRcpPmId().get(j), phonebookRawId);
                }
//            }
            }

            // Basic Profile Data
            TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

            ArrayList<UserProfile> arrayListUserProfile = new ArrayList<>();
            for (int i = 0; i < profileData.size(); i++) {

                //<editor-fold desc="Profile Master">
                UserProfile userProfile = new UserProfile();
//            userProfile.setPmSuffix(profileData.get(i).getPbNameSuffix());
//            userProfile.setPmPrefix(profileData.get(i).getPbNamePrefix());
                userProfile.setPmFirstName(profileData.get(i).getPbNameFirst());
//            userProfile.setPmMiddleName(profileData.get(i).getPbNameMiddle());
                userProfile.setPmLastName(profileData.get(i).getPbNameLast());
//            userProfile.setPmPhoneticFirstName(profileData.get(i).getPbPhoneticNameFirst());
//            userProfile.setPmPhoneticMiddleName(profileData.get(i).getPbPhoneticNameMiddle());
//            userProfile.setPmPhoneticLastName(profileData.get(i).getPbPhoneticNameLast());
                userProfile.setPmIsFavourite(profileData.get(i).getIsFavourite());
//            userProfile.setPmNotes(profileData.get(i).getPbNote());
//            userProfile.setPmNickName(profileData.get(i).getPbNickname());
                userProfile.setPmRcpId(profileData.get(i).getRcpPmId());
                userProfile.setPmNosqlMasterId(profileData.get(i).getNoSqlMasterId());
                userProfile.setProfileRating(profileData.get(i).getProfileRating());
                userProfile.setPmProfileImage(profileData.get(i).getPbProfilePhoto());
                userProfile.setTotalProfileRateUser(profileData.get(i).getTotalProfileRateUser());

                if (mapLocalRcpId.containsKey(profileData.get(i).getRcpPmId())) {
                    userProfile.setPmRawId(mapLocalRcpId.get(profileData.get(i).getRcpPmId()));
                }

                String existingRawId = tableProfileMaster.getRawIdFromRcpId(Integer.parseInt
                        (userProfile.getPmRcpId()));
                if (StringUtils.length(existingRawId) <= 0) {

                    arrayListUserProfile.add(userProfile);
                    tableProfileMaster.addArrayProfile(arrayListUserProfile);
                    //</editor-fold>

                    //<editor-fold desc="Mobile Master">
                    ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = profileData
                            .get(i).getPbPhoneNumber();
                    ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
                    for (int j = 0; j < arrayListPhoneNumber.size(); j++) {

                        MobileNumber mobileNumber = new MobileNumber();
                        mobileNumber.setMnmRecordIndexId(arrayListPhoneNumber.get(j).getPhoneId());
                        mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(j)
                                .getPhoneNumber());
                        mobileNumber.setMnmNumberType(arrayListPhoneNumber.get(j).getPhoneType());
                        mobileNumber.setMnmNumberPrivacy(String.valueOf(arrayListPhoneNumber.get(j)
                                .getPhonePublic()));
                        mobileNumber.setMnmIsPrivate(arrayListPhoneNumber.get(j).getIsPrivate());
                        mobileNumber.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        if (StringUtils.equalsIgnoreCase(profileData.get(i).getVerifiedMobileNumber()
                                , mobileNumber.getMnmMobileNumber())) {
                            mobileNumber.setMnmIsPrimary(String.valueOf(IntegerConstants
                                    .RCP_TYPE_PRIMARY));
                        } else {
                            mobileNumber.setMnmIsPrimary(String.valueOf(IntegerConstants
                                    .RCP_TYPE_SECONDARY));
                        }
                        arrayListMobileNumber.add(mobileNumber);
                    }

                    TableMobileMaster tableMobileMaster = new TableMobileMaster
                            (databaseHandler);
                    tableMobileMaster.addArrayMobileNumber(arrayListMobileNumber);
                    //</editor-fold>

                    //<editor-fold desc="Email Master">
                    if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbEmailId())) {
                        ArrayList<ProfileDataOperationEmail> arrayListEmailId = profileData.get(i)
                                .getPbEmailId();
                        ArrayList<Email> arrayListEmail = new ArrayList<>();
                        for (int j = 0; j < arrayListEmailId.size(); j++) {
                            Email email = new Email();
                            email.setEmEmailAddress(arrayListEmailId.get(j).getEmEmailId());
                            email.setEmRecordIndexId(arrayListEmailId.get(j).getEmId());
                            email.setEmEmailType(arrayListEmailId.get(j).getEmType());
                            email.setEmEmailPrivacy(String.valueOf(arrayListEmailId.get(j)
                                    .getEmPublic()));
                            email.setEmIsVerified(String.valueOf(arrayListEmailId.get(j).getEmRcpType
                                    ()));
                            email.setEmIsPrivate(arrayListEmailId.get(j).getEmIsPrivate());

                            email.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());

                            if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getVerifiedEmailIds
                                    ())) {
                                for (int k = 0; k < profileData.get(i).getVerifiedEmailIds().size();
                                     k++) {
                                    if (StringUtils.equalsIgnoreCase(profileData.get(i)
                                            .getVerifiedEmailIds().get(k).getEmEmailId(), email
                                            .getEmEmailAddress())) {
                                        email.setEmIsVerified("1");
                                    } else {
                                        email.setEmIsVerified("0");
                                    }
                                }
                            }
                            arrayListEmail.add(email);
                        }

                        TableEmailMaster tableEmailMaster = new TableEmailMaster
                                (databaseHandler);
                        tableEmailMaster.addArrayEmail(arrayListEmail);
                    }
                    //</editor-fold>

                    //<editor-fold desc="Organization Master">
                    if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbOrganization())) {
                        ArrayList<ProfileDataOperationOrganization> arrayListOrganization =
                                profileData
                                        .get(i).getPbOrganization();
                        ArrayList<Organization> organizationList = new ArrayList<>();
                        for (int j = 0; j < arrayListOrganization.size(); j++) {
                            Organization organization = new Organization();
                            organization.setOmRecordIndexId(arrayListOrganization.get(j).getOrgId
                                    ());
                            organization.setOmOrganizationCompany(arrayListOrganization.get(j)
                                    .getOrgName
                                            ());
                            organization.setOmOrganizationDesignation(arrayListOrganization.get(j)
                                    .getOrgJobTitle());
                            organization.setOmIsCurrent(String.valueOf(arrayListOrganization.get(j)
                                    .getIsCurrent()));
                            organization.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                            organizationList.add(organization);
                        }

                        TableOrganizationMaster tableOrganizationMaster = new
                                TableOrganizationMaster
                                (databaseHandler);
                        tableOrganizationMaster.addArrayOrganization(organizationList);
                    }
                    //</editor-fold>

                    // <editor-fold desc="Website Master">
                    if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbWebAddress())) {
                        ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = profileData
                                .get(i)
                                .getPbWebAddress();
                        ArrayList<Website> websiteList = new ArrayList<>();
                        for (int j = 0; j < arrayListWebsite.size(); j++) {
                            Website website = new Website();
                            website.setWmRecordIndexId(arrayListWebsite.get(j).getWebId());
                            website.setWmWebsiteUrl(arrayListWebsite.get(j).getWebAddress());
                            website.setWmWebsiteType(arrayListWebsite.get(j).getWebType());
                            website.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                            websiteList.add(website);
                        }

                        TableWebsiteMaster tableWebsiteMaster = new TableWebsiteMaster
                                (databaseHandler);
                        tableWebsiteMaster.addArrayWebsite(websiteList);
                    }
                    //</editor-fold>

                    //<editor-fold desc="Address Master">
                    if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbAddress())) {
                        ArrayList<ProfileDataOperationAddress> arrayListAddress = profileData.get(i)
                                .getPbAddress();
                        ArrayList<Address> addressList = new ArrayList<>();
                        for (int j = 0; j < arrayListAddress.size(); j++) {
                            Address address = new Address();
                            address.setAmRecordIndexId(arrayListAddress.get(j).getAddId());
                            address.setAmCity(arrayListAddress.get(j).getCity());
                            address.setAmCountry(arrayListAddress.get(j).getCountry());
                            address.setAmFormattedAddress(arrayListAddress.get(j)
                                    .getFormattedAddress
                                            ());
                            address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                            address.setAmPostCode(arrayListAddress.get(j).getPostCode());
                            address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                            address.setAmStreet(arrayListAddress.get(j).getStreet());
                            address.setAmAddressType(arrayListAddress.get(j).getAddressType());
                            address.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                            address.setAmIsPrivate(arrayListAddress.get(j).getIsPrivate());
                            address.setAmAddressPrivacy(String.valueOf(arrayListAddress.get(j)
                                    .getAddPublic()));
                            addressList.add(address);
                        }

                        TableAddressMaster tableAddressMaster = new TableAddressMaster
                                (databaseHandler);
                        tableAddressMaster.addArrayAddress(addressList);
                    }
                    //</editor-fold>

                    // <editor-fold desc="Im Account Master">
                    if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbIMAccounts())) {
                        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = profileData
                                .get(i)
                                .getPbIMAccounts();
                        ArrayList<ImAccount> imAccountsList = new ArrayList<>();
                        for (int j = 0; j < arrayListImAccount.size(); j++) {
                            ImAccount imAccount = new ImAccount();
                            imAccount.setImRecordIndexId(arrayListImAccount.get(j).getIMId());
                            imAccount.setImImProtocol(arrayListImAccount.get(j)
                                    .getIMAccountProtocol());
                            imAccount.setImImDetail(arrayListImAccount.get(j)
                                    .getIMAccountDetails());
                            imAccount.setImIsPrivate(arrayListImAccount.get(j)
                                    .getIMAccountIsPrivate());
                            imAccount.setImImPrivacy(String.valueOf(arrayListImAccount.get(j)
                                    .getIMAccountPublic()));
                            imAccount.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                            imAccountsList.add(imAccount);
                        }

                        TableImMaster tableImMaster = new TableImMaster(databaseHandler);
                        tableImMaster.addArrayImAccount(imAccountsList);
                    }
                    //</editor-fold>

                    // <editor-fold desc="Event Master">
                    if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbEvent())) {
                        ArrayList<ProfileDataOperationEvent> arrayListEvent = profileData.get(i)
                                .getPbEvent();
                        ArrayList<Event> eventList = new ArrayList<>();
                        for (int j = 0; j < arrayListEvent.size(); j++) {
                            Event event = new Event();
                            event.setEvmRecordIndexId(arrayListEvent.get(j).getEventId());
                            event.setEvmStartDate(arrayListEvent.get(j).getEventDateTime());
                            event.setEvmEventType(arrayListEvent.get(j).getEventType());
                            event.setEvmIsPrivate(arrayListEvent.get(j).getIsPrivate());
                            event.setEvmIsYearHidden(arrayListEvent.get(j).getIsYearHidden());
                            event.setEvmEventPrivacy(String.valueOf(arrayListEvent.get(j)
                                    .getEventPublic()));
                            event.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                            eventList.add(event);
                        }

                        TableEventMaster tableEventMaster = new TableEventMaster
                                (databaseHandler);
                        tableEventMaster.addArrayEvent(eventList);
                    }
                    //</editor-fold>

                } else {
                    if (StringUtils.contains(existingRawId, ",")) {
                        String rawIds[] = existingRawId.split(",");
                        ArrayList<String> arrayListRawIds = new ArrayList<>(Arrays.asList(rawIds));
                        if (arrayListRawIds.contains(mapLocalRcpId.get(profileData.get(i)
                                .getRcpPmId()))) {
                            return;
                        } else {
                            String newRawIds = existingRawId + "," + mapLocalRcpId.get(profileData
                                    .get(i)
                                    .getRcpPmId());
                            tableProfileMaster.updateRawIds(Integer.parseInt(userProfile.getPmRcpId()),
                                    newRawIds);
                        }
                    } else {
                        if (existingRawId.equals(mapLocalRcpId.get(profileData.get(i)
                                .getRcpPmId())))
                            return;
                        else {
                            String newRawIds = existingRawId + "," + mapLocalRcpId.get(profileData
                                    .get(i)
                                    .getRcpPmId());
                            tableProfileMaster.updateRawIds(Integer.parseInt(userProfile.getPmRcpId()),
                                    newRawIds);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeRemovedDataFromDb(ArrayList<ProfileData> mapping) {

        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

        for (int i = 0; i < mapping.size(); i++) {
            String rawId = mapping.get(i).getLocalPhoneBookId();

            ArrayList<String> newRcpIds = new ArrayList<>();
            for (int j = 0; j < mapping.get(i).getRcpPmId().size(); j++) {
                String rcPid = mapping.get(i).getRcpPmId().get(j);
                newRcpIds.add(rcPid);
            }

            ArrayList<String> existingRcpIds = tableProfileMaster.getAllRcpIdFromRawId(rawId);
            existingRcpIds.removeAll(newRcpIds);

            for (int k = 0; k < existingRcpIds.size(); k++) {
                QueryManager queryManager = new QueryManager(databaseHandler);
                queryManager.updateRcProfileDetail(this, Integer.parseInt(existingRcpIds.get(k)),
                        rawId);
            }

        }
    }

    private void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
//        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(AppConstants.PLAY_STORE_LINK + getPackageName())));
        }
    }

    private void openDialer() {
        try {

            Intent intent = new Intent(MainActivity.this, DialerActivity.class);
            ActivityOptions options = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                startActivity(intent, options.toBundle());

            } else {
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getContactNameFromNumber(String phoneNumber) {
        String contactName = "";
        try {

            ContentResolver contentResolver = MainActivity.this.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return contactName;
    }

    private void showAddToContact(boolean value) {
        ImageView imageViewAddContact = (ImageView) findViewById(R.id.image_add_contact);
        if (value) {
            imageViewAddContact.setVisibility(View.GONE);
            imageViewAddContact.setVisibility(View.VISIBLE);
            imageViewAddContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.addToContact(MainActivity.this, "");
                }
            });
        } else {
            imageViewAddContact.setVisibility(View.GONE);
        }
    }

    private void setupTabLayout() {
        contactsFragment = ContactsFragment.newInstance();
        callLogFragment = CallLogFragment.newInstance();
//        smsFragment = SmsFragment.newInstance();
        tabMain.addTab(tabMain.newTab().setText(getString(R.string.tab_contact)), true);
        tabMain.addTab(tabMain.newTab().setText(getString(R.string.tab_call)));
//        tabMain.addTab(tabMain.newTab().setText(getString(R.string.tab_sms)));
    }

    private void bindWidgetsWithAnEvent() {
        tabMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
                if (AppConstants.isProgressShowing) {
                    showFragmentSwitchAlertDialog();
                } else {
                    setCurrentTabFragment(tabPosition);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void showFragmentSwitchAlertDialog() {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        callConfirmationDialog.dismissDialog();
                        setCurrentTabFragment(1);
                        tabMain.getTabAt(1).select();
                        break;

                    case R.id.rippleRight:
                        callConfirmationDialog.dismissDialog();
                        Intent localBroadcastIntent = new Intent(AppConstants
                                .ACTION_LOCAL_BROADCAST_TABCHANGE);
                        localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_SWITCH_TAB,
                                AppConstants.EXTRA_CALL_LOG_SWITCH_TAB_VALUE);
                        LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                                .getInstance(getApplicationContext());
                        myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                        setCurrentTabFragment(tabPosition);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(this, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(getString(R.string.action_ok));
        callConfirmationDialog.setDialogBody(getString(R.string.data_lost_hint_1) + "\n " +
                getString(R.string.data_lost_hint_2));
        callConfirmationDialog.showDialog();

    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {

            case 0:
                showAddToContact(true);
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable
                        .ic_floating_dial_pad));
                isCompaseIcon = false;
                replaceFragment(contactsFragment);
                break;
            case 1:
                showAddToContact(true);
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable
                        .ic_floating_dial_pad));
                isCompaseIcon = false;
                replaceFragment(callLogFragment);
                break;
            case 2:
                /*showAddToContact(true);
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable
                        .ic_mode_edit));
                isCompaseIcon = true;
                replaceFragment(smsFragment);*/
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container_main_tab, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public void registerBroadcastReceiver() {
        this.registerReceiver(networkConnectionReceiver, new IntentFilter("android.net.conn" +
                ".CONNECTIVITY_CHANGE"));


    }

    public void unregisterBroadcastReceiver() {
        this.unregisterReceiver(networkConnectionReceiver);

    }

    private void registerLocalBroadCastReceiver() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance
                (this);
        IntentFilter intentFilter = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_CALL_LOG_SYNC);
        localBroadcastManager.registerReceiver(localBroadcastReceiverCallLogSync, intentFilter);

        LocalBroadcastManager localBroadcastManagerReceiveRecentCalls = LocalBroadcastManager
                .getInstance(MainActivity.this);
        IntentFilter intentFilter5 = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_CALLS);
        localBroadcastManagerReceiveRecentCalls.registerReceiver
                (localBroadcastReceiverRecentCalls, intentFilter5);


        /*LocalBroadcastManager localBroadcastManagerReceiveRecentSms = LocalBroadcastManager
                .getInstance(MainActivity.this);
        IntentFilter intentFilter2 = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_SMS);
        localBroadcastManagerReceiveRecentSms.registerReceiver(localBroadCastReceiverRecentSMS,
                intentFilter2);

        LocalBroadcastManager localBroadcastManagerSyncSmsLogs = LocalBroadcastManager
                .getInstance(this);
        IntentFilter intentFilter1 = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_SYNC_SMS);
        localBroadcastManagerSyncSmsLogs.registerReceiver(localBroadcastReceiverSmsLogSync,
                intentFilter1);*/

        LocalBroadcastManager localBroadcastManagerUpdateNotificationCount = LocalBroadcastManager
                .getInstance(MainActivity.this);
        IntentFilter intentFilterUpdateCount = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_UPDATE_NOTIFICATION_COUNT);
        localBroadcastManagerUpdateNotificationCount.registerReceiver
                (localBroadCastReceiverUpdateCount, intentFilterUpdateCount);

        LocalBroadcastManager contactDisplayed = LocalBroadcastManager.getInstance(MainActivity
                .this);
        IntentFilter intentFilterContactDisplayed = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_CONTACT_DISPLAYED);
        contactDisplayed.registerReceiver
                (localBroadcastReceiverContactDisplayed, intentFilterContactDisplayed);

        LocalBroadcastManager getGlobalProfileData = LocalBroadcastManager.getInstance(MainActivity
                .this);
        IntentFilter intentFiltergetGlobalProfileData = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_GET_GLOBAL_PROFILE_DATA);
        getGlobalProfileData.registerReceiver
                (localBroadCastReceiverGetGlobalProfileData, intentFiltergetGlobalProfileData);


    }

    private void unRegisterLocalBroadCastReceiver() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                .getInstance(this);

        localBroadcastManager.unregisterReceiver(localBroadcastReceiverCallLogSync);
        localBroadcastManager.unregisterReceiver(localBroadcastReceiverRecentCalls);
//        localBroadcastManager.unregisterReceiver(localBroadCastReceiverRecentSMS);
//        localBroadcastManager.unregisterReceiver(localBroadcastReceiverSmsLogSync);
        localBroadcastManager.unregisterReceiver(localBroadCastReceiverUpdateCount);
        localBroadcastManager.unregisterReceiver(localBroadcastReceiverContactDisplayed);
        localBroadcastManager.unregisterReceiver(localBroadCastReceiverGetGlobalProfileData);
    }

    ArrayList<String> callLogsIdsList;

    private void getAllCallLogId() {

        callLogsIdsList = new ArrayList<>();
        PhoneBookCallLogs phoneBookCallLogs = new PhoneBookCallLogs(this);
        Cursor cursor = phoneBookCallLogs.getSyncAllCallLogId();
        if (cursor != null) {
            int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
            while (cursor.moveToNext()) {
                callLogsIdsList.add(cursor.getString(rowId));
            }
            cursor.close();
        }
        Utils.setArrayListPreference(this, AppConstants.PREF_CALL_LOGS_ID_SET, callLogsIdsList);
    }

    private void getCallLogsByRawId() {

        getAllCallLogId();

        if (callLogsIdsList.size() > 0) {
            int indexToBeginSync = Utils.getIntegerPreference(this, AppConstants
                    .PREF_CALL_LOG_SYNCED_COUNT, 0);

            ArrayList<String> tempIdsList = new ArrayList<>();
            for (int i = indexToBeginSync; i < callLogsIdsList.size(); i++) {
                String ids = callLogsIdsList.get(i);
                tempIdsList.add(ids);
            }

            if (tempIdsList.size() > CALL_LOG_CHUNK) {
                for (ArrayList<String> partition : chopped(tempIdsList, CALL_LOG_CHUNK)) {
                    fetchCallLogsFromIds(partition);// do something with partition
                }
            } else {
                fetchCallLogsFromIds(tempIdsList);
            }

//            if (!Utils.getBooleanPreference(this, AppConstants
//                    .PREF_CALL_LOG_SYNCED, false))
//
//                if ((Utils.getIntegerPreference(this, AppConstants.PREF_CALL_LOG_SYNCED_COUNT,
// 0) >=
//                        Utils.getArrayListPreference(this, AppConstants.PREF_CALL_LOGS_ID_SET)
// .size())) {
//
//                    Utils.setIntegerPreference(this, AppConstants.PREF_CALL_LOG_SYNCED_COUNT,
//                            Utils.getArrayListPreference(this, AppConstants
// .PREF_CALL_LOGS_ID_SET).size());
//
//                    Utils.setBooleanPreference(this, AppConstants
//                            .PREF_CALL_LOG_SYNCED, true);
//
//                    if (callLogTypeListForGlobalProfile.size() > 0) {
//                        if (Utils.getBooleanPreference(this, AppConstants
// .PREF_GOT_ALL_PROFILE_DATA, false))
//                            Utils.setBooleanPreference(this, AppConstants
// .PREF_GOT_ALL_PROFILE_DATA, false);
//                    }
//
//                    Intent localBroadcastIntent = new Intent(AppConstants
//                            .ACTION_LOCAL_BROADCAST_GET_GLOBAL_PROFILE_DATA);
//                    LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
//                            .getInstance(MainActivity.this);
//                    myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
//                }

        } else {
            Utils.setBooleanPreference(this, AppConstants
                    .PREF_CALL_LOG_SYNCED, true);
        }
    }

    private void fetchCallLogsFromIds(ArrayList<String> listOfRowIds) {

        ArrayList<CallLogType> tempCallLogTypeArrayList = new ArrayList<>();

        try {
            for (int i = 0; i < listOfRowIds.size(); i++) {
                String uniqueCallLogId = listOfRowIds.get(i);
                if (!TextUtils.isEmpty(uniqueCallLogId)) {
                    String order = CallLog.Calls.DATE + " ASC";
                    Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                            null, CallLog.Calls._ID + " = " + uniqueCallLogId, null, order);

                    if (cursor != null) {
                        cursor.moveToNext();

                        if (syncCallLogAsyncTask != null && syncCallLogAsyncTask.isCancelled())
                            return;

                        CallLogType log = new CallLogType(this);

                        String userNumber = Utils.getFormattedNumber(MainActivity.this, cursor
                                .getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                        String userName = cursor.getString(cursor.getColumnIndex(CallLog.Calls
                                .CACHED_NAME));

                        log.setNumber(userNumber);

                        if (!TextUtils.isEmpty(userName)) {
                            log.setName(getContactNameFromNumber(userNumber));
                        } else
                            log.setName("");

                        log.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                        log.setDurationToPass(log.getCoolDuration(Float.parseFloat
                                (cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)))));

                        log.setCallDateAndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a",
                                Locale.getDefault()).format
                                (cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))));
                        log.setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
                        log.setUniqueContactId(uniqueCallLogId);
                        String numberTypeLog = getPhoneNumberType(cursor.getInt(cursor
                                .getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE)));
                        log.setNumberType(numberTypeLog);

                        String uniquePhoneBookId = getRawContactIdFromNumber(userNumber);
                        if (!TextUtils.isEmpty(uniquePhoneBookId))
                            log.setLocalPbRowId(uniquePhoneBookId);
                        else
                            log.setLocalPbRowId(" ");
                        ArrayList<CallLogType> arrayListHistory;
                        arrayListHistory = callLogHistory(userNumber);
                        ArrayList<CallLogType> arrayListHistoryCount = new ArrayList<>();
                        for (int j = 0; j < arrayListHistory.size(); j++) {
                            CallLogType tempCallLogType = arrayListHistory.get(j);
                            String simNumber = arrayListHistory.get(j)
                                    .getHistoryCallSimNumber();
                            log.setCallSimNumber(simNumber);
                            long tempdate = tempCallLogType.getHistoryDate();
                            Date objDate1 = new Date(tempdate);
                            String arrayDate = new SimpleDateFormat("yyyy-MM-dd", Locale
                                    .getDefault()).format
                                    (objDate1);
                            long callLogDate = log.getDate();
                            Date intentDate1 = new Date(callLogDate);
                            String intentDate = new SimpleDateFormat("yyyy-MM-dd", Locale
                                    .getDefault()).format
                                    (intentDate1);
                            if (intentDate.equalsIgnoreCase(arrayDate)) {
                                arrayListHistoryCount.add(tempCallLogType);
                            }
                            // 25/05/2017 Updated bcz sync format changed
                            // 16/06/2017 changed done start
                            //log.setHistoryNumber(tempCallLogType.getHistoryNumber());
                            //log.setHistoryType(tempCallLogType.getHistoryType());
                            //log.setHistoryDate(tempCallLogType.getHistoryDate());
                            log.setHistoryDuration(tempCallLogType.getHistoryDuration());
                            log.setHistoryCallSimNumber(tempCallLogType
                                    .getHistoryCallSimNumber());
                            log.setHistoryId(tempCallLogType.getHistoryId());
//                                log.setCallDateAndTime(tempCallLogType.getCallDateAndTime());
                            log.setTypeOfCall(tempCallLogType.getTypeOfCall());
//                            log.setDurationToPass(tempCallLogType.getDurationToPass());
                            if (!StringUtils.isEmpty(tempCallLogType.getHistoryCallSimNumber()))
                                log.setHistoryCallSimNumber(tempCallLogType
                                        .getHistoryCallSimNumber());
                            else
                                log.setHistoryCallSimNumber(" ");
                            // 16/06/2017 changed done end
                        }
                        int logCount = arrayListHistoryCount.size();
                        log.setHistoryLogCount(logCount);
                        tempCallLogTypeArrayList.add(log);
                        callLogTypeArrayListMain.add(log);
//                        callLogTypeListForGlobalProfile.add(log);
//                            rContactApplication.setArrayListCallLogType(callLogTypeArrayListMain);
                        cursor.close();
                    }
                }
            }
            if (tempCallLogTypeArrayList.size() > 0)
                syncCallLogDataToServer(tempCallLogTypeArrayList);
            else {
                Utils.setBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLatestCallLogsByRawId() {

        ArrayList<CallLogType> tempCallLogTypeArrayList = new ArrayList<>();
        Utils.setArrayListPreference(this, AppConstants.PREF_CALL_LOGS_ID_SET, new ArrayList());

        try {
            String order = CallLog.Calls.DATE + " ASC";
            String prefDate = Utils.getStringPreference(MainActivity.this, AppConstants
                    .PREF_CALL_LOG_SYNC_TIME, "");
            String prefRowId = Utils.getStringPreference(MainActivity.this, AppConstants
                    .PREF_CALL_LOG_ROW_ID, "");
            String dateToCompare = "", tempDate = "";
            String currentDate = "";
            long dateToConvert = 0;
            if (!StringUtils.isEmpty(prefDate)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale
                        .getDefault());
                dateToConvert = sdf.parse(prefDate).getTime();
                tempDate = String.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale
                        .getDefault()).parse(prefDate).getTime());
                dateToCompare = String.valueOf(dateToConvert);
//                System.out.println("RContact last Call-log date : " + dateToCompare);
                currentDate = String.valueOf(System.currentTimeMillis());
            }

            Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.DATE + " BETWEEN ? AND ?"
                    , new String[]{dateToCompare, currentDate}, order);
            if (cursor != null) {

                while (cursor.moveToNext()) {

                    if (syncCallLogAsyncTask != null && syncCallLogAsyncTask.isCancelled())
                        return;

                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale
                            .getDefault());

                    Date cursorDate = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls
                            .DATE)));
                    String cursorDateToCompare = sdf.format(cursorDate);

                    Date compareDate = new Date(dateToConvert);
                    String prefDateToCompare = sdf.format(compareDate);

                    Date curDate = sdf.parse(cursorDateToCompare);
                    Date preferenceDate = sdf.parse(prefDateToCompare);

                    if (curDate.getTime() > preferenceDate.getTime() && (Integer.parseInt(cursor
                            .getString(cursor.getColumnIndex(CallLog.Calls._ID)))
                            > Integer.parseInt(prefRowId))) {

                        String userNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls
                                .NUMBER));
                        String userName = cursor.getString(cursor.getColumnIndex(CallLog.Calls
                                .CACHED_NAME));

                        CallLogType log = new CallLogType(this);
                        log.setNumber(userNumber);

                        if (!TextUtils.isEmpty(userName))
                            log.setName(userName);
                        else
                            log.setName("");

                        log.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                        log.setDurationToPass(log.getCoolDuration(Float.parseFloat
                                (cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)))));

                        log.setCallDateAndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a",
                                Locale.getDefault()).format
                                (cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))));
                        log.setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
                        log.setUniqueContactId(cursor.getString(cursor.getColumnIndex(CallLog
                                .Calls._ID)));
                        String numberTypeLog = getPhoneNumberType(cursor.getInt(cursor
                                .getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE)));
                        log.setNumberType(numberTypeLog);

                        String uniquePhoneBookId = getRawContactIdFromNumber(userNumber);
                        if (!TextUtils.isEmpty(uniquePhoneBookId))
                            log.setLocalPbRowId(uniquePhoneBookId);
                        else
                            log.setLocalPbRowId(" ");
                        ArrayList<CallLogType> arrayListHistory;
                        arrayListHistory = callLogHistory(userNumber);
                        ArrayList<CallLogType> arrayListHistoryCount = new ArrayList<>();
                        for (int j = 0; j < arrayListHistory.size(); j++) {
                            CallLogType tempCallLogType = arrayListHistory.get(j);
                            String simNumber = arrayListHistory.get(j)
                                    .getHistoryCallSimNumber();
                            log.setCallSimNumber(simNumber);
                            long tempdate = tempCallLogType.getHistoryDate();
                            Date objDate1 = new Date(tempdate);
                            String arrayDate = new SimpleDateFormat("yyyy-MM-dd", Locale
                                    .getDefault()).format
                                    (objDate1);
                            long callLogDate = log.getDate();
                            Date intentDate1 = new Date(callLogDate);
                            String intentDate = new SimpleDateFormat("yyyy-MM-dd", Locale
                                    .getDefault()).format
                                    (intentDate1);
                            if (intentDate.equalsIgnoreCase(arrayDate)) {
                                arrayListHistoryCount.add(tempCallLogType);
                            }
                            // 25/05/2017 Updated bcz sync format changed
                            // 16/06/2017 changed done start
                            //log.setHistoryNumber(tempCallLogType.getHistoryNumber());
                            //log.setHistoryType(tempCallLogType.getHistoryType());
                            //log.setHistoryDate(tempCallLogType.getHistoryDate());
                            log.setHistoryDuration(tempCallLogType.getHistoryDuration());
                            log.setHistoryCallSimNumber(tempCallLogType
                                    .getHistoryCallSimNumber());
                            log.setHistoryId(tempCallLogType.getHistoryId());
//                            log.setCallDateAndTime(tempCallLogType.getCallDateAndTime());
                            log.setTypeOfCall(tempCallLogType.getTypeOfCall());
//                            log.setDurationToPass(tempCallLogType.getDurationToPass());
                            if (!StringUtils.isEmpty(tempCallLogType.getHistoryCallSimNumber()))
                                log.setHistoryCallSimNumber(tempCallLogType
                                        .getHistoryCallSimNumber());
                            else
                                log.setHistoryCallSimNumber(" ");
                            // 16/06/2017 changed done end
                        }
                        int logCount = arrayListHistoryCount.size();
                        log.setHistoryLogCount(logCount);
                        tempCallLogTypeArrayList.add(log);
                        callLogTypeArrayListMain.add(log);
//                        callLogTypeListForGlobalProfile.add(log);
                    }
                }
                cursor.close();
            }

            if (tempCallLogTypeArrayList.size() > 0) {
                Utils.setBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, false);
                syncRecentCallLogDataToServer(tempCallLogTypeArrayList);
            } else {
                Utils.setBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, true);
                getSpamAndRCPDetailAsyncTask = new GetSpamAndRCPDetailAsyncTask();
                getSpamAndRCPDetailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNotificationCount(DatabaseHandler databaseHandler) {
        TableNotificationStateMaster notificationStateMaster = new TableNotificationStateMaster
                (databaseHandler);
        return notificationStateMaster.getTotalUnreadCount();
    }

    private int getTimeLineNotificationCount(DatabaseHandler databaseHandler) {
        TableNotificationStateMaster notificationStateMaster = new TableNotificationStateMaster
                (databaseHandler);
        return notificationStateMaster.getTotalUnreadCountByType(AppConstants
                .NOTIFICATION_TYPE_TIMELINE);
    }

    private void reSyncPhoneBookContactList() {
        currentStamp = String.valueOf(System.currentTimeMillis());
        String lastStamp = Utils.getStringPreference(this, AppConstants
                .PREF_CONTACT_LAST_SYNC_TIME, currentStamp);

        Cursor cursor = phoneBookContacts.getUpdatedRawId(lastStamp);

        if (Utils.getArrayListPreference(this, AppConstants.PREF_CONTACT_ID_SET) == null)
            return;

        Set<String> updatedContactIds = new HashSet<>();
        while (cursor.moveToNext()) {
            String rawId = cursor.getString(cursor.getColumnIndex
                    (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
            updatedContactIds.add(rawId);
        }
        cursor.close();

        Set<String> arrayListOldContactIds = new HashSet<>();
        arrayListOldContactIds.addAll(Utils.getArrayListPreference(this, AppConstants
                .PREF_CONTACT_ID_SET));

        Cursor contactNameCursor = phoneBookContacts.getAllContactRawId();

        Set<String> arrayListNewContactId = new HashSet<>();
        while (contactNameCursor.moveToNext()) {
            arrayListNewContactId.add(contactNameCursor.getString(contactNameCursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID)));
        }

        contactNameCursor.close();

        Set<String> removedContactIds = new HashSet<>(arrayListOldContactIds);
        Set<String> insertedContactIds = new HashSet<>(arrayListNewContactId);
        removedContactIds.removeAll(insertedContactIds);
        insertedContactIds.removeAll(arrayListOldContactIds);

        updatedContactIds.removeAll(insertedContactIds);

        arrayListReSyncUserContact = new ArrayList<>();

        if (removedContactIds.size() > 0) {
            // deleted 5
            ArrayList<String> list = new ArrayList<>(removedContactIds);
            prepareForDeletion(list);
        }
        if (updatedContactIds.size() > 0) {
            //updated 6
            ArrayList<String> list = new ArrayList<>(updatedContactIds);
            String inClause = list.toString();
            inClause = inClause.replace("[", "(");
            inClause = inClause.replace("]", ")");
            prepareData(IntegerConstants.SYNC_UPDATE_CONTACT, inClause);
        }
        if (insertedContactIds.size() > 0) {
            //inserted 1
            ArrayList<String> list = new ArrayList<>(insertedContactIds);
            String inClause = list.toString();
            inClause = inClause.replace("[", "(");
            inClause = inClause.replace("]", ")");
            prepareData(IntegerConstants.SYNC_INSERT_CONTACT, inClause);

        }
        if (Utils.isNetworkAvailable(this) && arrayListReSyncUserContact.size() > 0) {
            uploadContacts("", arrayListReSyncUserContact);
//            if (arrayListReSyncUserContact.size() <= 100) {
//                Log.i(TAG, "sending updated contacts to server");
//                uploadContacts();
//            } else {
//                Log.i(TAG, "need to apply resync mechanism:");
//            }
        }
    }

    private void prepareData(int flag, String inCaluse) {

        LongSparseArray<ProfileDataOperation> profileDetailSparseArray = new LongSparseArray<>();

        //<editor-fold desc="Create Cursor">
        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.STARRED,

                ContactsContract.CommonDataKinds.StructuredName.PREFIX,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME,

                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,

                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.TYPE,

                ContactsContract.CommonDataKinds.Website.TYPE,
                ContactsContract.CommonDataKinds.Website.URL,

                ContactsContract.CommonDataKinds.Organization.COMPANY,
                ContactsContract.CommonDataKinds.Organization.TITLE,
                ContactsContract.CommonDataKinds.Organization.TYPE,
                ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
                ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION,
                ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION,

                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD,
                ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
                ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE,

                ContactsContract.CommonDataKinds.Im.TYPE,
                ContactsContract.CommonDataKinds.Im.DATA1,
                ContactsContract.CommonDataKinds.Im.PROTOCOL,

                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.START_DATE,

        };
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?, ?, ?, ?, ?, ?, ?) and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 and " + ContactsContract
                .RawContacts.ACCOUNT_TYPE + " in (" + ContactStorageConstants.CONTACT_STORAGE +
                ") and " +
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " in " + inCaluse;
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                // starred contact not accessible
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
        };
        //  String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;
        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
        Uri uri = ContactsContract.Data.CONTENT_URI;
//        if (syncingTask != null && syncingTask.isCancelled()) {
//            return;
//        }
        Cursor cursor = getContentResolver().query(uri, projection, selection,
                selectionArgs, sortOrder);
        //</editor-fold>

        //<editor-fold desc="Data Read from Cursor">
        if (cursor != null) {
            try {
                final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
                final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);

                while (cursor.moveToNext()) {
//                if (syncingTask != null && syncingTask.isCancelled()) {
//                    return;
//                }
                    //ProfileDataOperation operation = new ProfileDataOperation();
                    //operation.setFlag(flag);
                    long id = cursor.getLong(idIdx);
                    ProfileDataOperation phoneBookContact = profileDetailSparseArray.get(id);
                    if (phoneBookContact == null) {
                        phoneBookContact = new ProfileDataOperation(id);
                        profileDetailSparseArray.put(id, phoneBookContact);
//                    profileDataList.add(phoneBookContact);
                    }
                    phoneBookContact.setLookupKey(cursor.getString(cursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID)));
                    phoneBookContact.setIsFavourite(cursor.getString(cursor.getColumnIndex
                            (ContactsContract.Contacts.STARRED)));
                    String mimeType = cursor.getString(mimeTypeIdx);
                    switch (mimeType) {
                        case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:

                            phoneBookContact.setPbNamePrefix(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.StructuredName.PREFIX)));
                            phoneBookContact.setPbNameFirst(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.StructuredName.GIVEN_NAME)));
                            phoneBookContact.setPbNameMiddle(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.StructuredName.MIDDLE_NAME)));
                            phoneBookContact.setPbNameLast(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.StructuredName.FAMILY_NAME)));
                            phoneBookContact.setPbNameSuffix(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.StructuredName.SUFFIX)));
                            phoneBookContact.setPbPhoneticNameFirst(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME)));
                            phoneBookContact.setPbPhoneticNameMiddle(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME)));
                            phoneBookContact.setPbPhoneticNameLast(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME)));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                            ProfileDataOperationPhoneNumber phoneNumber = new
                                    ProfileDataOperationPhoneNumber();

                            phoneNumber.setPhoneNumber(cursor
                                    .getString(cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Phone.NUMBER)));
                            phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                                    (cursor.getInt(cursor.getColumnIndex
                                            (ContactsContract.CommonDataKinds.Phone.TYPE))));
                            phoneNumber.setPhonePublic(IntegerConstants.PRIVACY_EVERYONE);

                            phoneBookContact.addPhone(phoneNumber);
                            break;
                        case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                            ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                            emailId.setEmEmailId(cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email
                                            .ADDRESS)));
                            emailId.setEmType(phoneBookContacts.getEmailType(cursor,
                                    cursor.getInt
                                            (cursor.getColumnIndex(ContactsContract
                                                    .CommonDataKinds.Email.TYPE))));
                            emailId.setEmPublic(IntegerConstants.PRIVACY_EVERYONE);


                            phoneBookContact.addEmail(emailId);
                            break;
                        case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                            ProfileDataOperationWebAddress webAddress = new
                                    ProfileDataOperationWebAddress();

                            webAddress.setWebAddress(cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                            webAddress.setWebType(phoneBookContacts.getWebsiteType(cursor, (cursor
                                    .getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                                            .Website.TYPE)))));
                            webAddress.setWebPublic(IntegerConstants.PRIVACY_EVERYONE);

                            phoneBookContact.addWebsite(webAddress);

                            break;
                        case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                            ProfileDataOperationOrganization organization = new
                                    ProfileDataOperationOrganization();

                            organization.setOrgName(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Organization.COMPANY)));
                            organization.setOrgJobTitle(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Organization.TITLE)));
                            organization.setOrgDepartment(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Organization.DEPARTMENT)));
                            organization.setOrgType(phoneBookContacts.getOrganizationType(cursor,
                                    cursor.getInt((cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Organization.TYPE)))));
                            organization.setOrgJobDescription(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                            organization.setOrgOfficeLocation(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Organization.OFFICE_LOCATION)));
                            organization.setOrgPublic(IntegerConstants.PRIVACY_EVERYONE);

                            phoneBookContact.addOrganization(organization);
                            break;
                        case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                            ProfileDataOperationAddress address = new ProfileDataOperationAddress();

                            address.setFormattedAddress(cursor.getString
                                    (cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                            address.setCity(cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds
                                            .StructuredPostal
                                            .CITY)));
                            address.setCountry(cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds
                                            .StructuredPostal
                                            .COUNTRY)));
                            address.setNeighborhood(cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds
                                            .StructuredPostal
                                            .NEIGHBORHOOD)));
                            address.setPostCode(cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds
                                            .StructuredPostal
                                            .POSTCODE)));
                            address.setPoBox(cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds
                                            .StructuredPostal
                                            .POBOX)));
                            address.setStreet(cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds
                                            .StructuredPostal
                                            .STREET)));
                            address.setAddressType(phoneBookContacts.getAddressType(cursor, cursor
                                    .getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                                            .StructuredPostal.TYPE))));
                            address.setAddPublic(IntegerConstants.PRIVACY_EVERYONE);

                            phoneBookContact.addAddress(address);
                            break;
                        case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                            ProfileDataOperationImAccount imAccount = new
                                    ProfileDataOperationImAccount();


                            imAccount.setIMAccountDetails(cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                            imAccount.setIMAccountType(phoneBookContacts.getImAccountType(cursor,
                                    cursor.getInt(cursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Im.TYPE))));

                            imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                                    (cursor.getInt((cursor.getColumnIndex
                                            (ContactsContract.CommonDataKinds.Im.PROTOCOL)))));

                            imAccount.setIMAccountPublic(IntegerConstants.PRIVACY_EVERYONE);


                            phoneBookContact.addImAccount(imAccount);
                            break;
                        case ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE:

                            ProfileDataOperationEvent event = new ProfileDataOperationEvent();

                            event.setEventType(phoneBookContacts.getEventType(cursor, cursor.getInt
                                    (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event
                                            .TYPE))));

                            String eventDate = cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                            .START_DATE));

                            if (StringUtils.startsWith(eventDate, "--")) {
                                eventDate = "1900" + eventDate.substring(1, StringUtils.length
                                        (eventDate));
                            }

                            event.setEventDateTime(eventDate);

                            event.setEventPublic(IntegerConstants.PRIVACY_EVERYONE);

                            phoneBookContact.addEvent(event);
                            break;
                    }
                }
                cursor.close();
            } catch (Exception e) {
                Log.i("MainActivity", "Crash Occured when resyncing changed Contacts" + e
                        .toString());
            }
        }
        //</editor-fold>

        //<editor-fold desc="Prepare Data">
        for (int i = 0; i < profileDetailSparseArray.size(); i++) {
//            if (syncingTask != null && syncingTask.isCancelled()) {
//                return;
//            }
//            AddressBookContact bookContact = profileDetailSparseArray.valueAt(i);
            ProfileDataOperation profileContact = profileDetailSparseArray.valueAt(i);

            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

            ProfileData profileData = new ProfileData();
            profileData.setLocalPhoneBookId(profileContact.getLookupKey());

            ProfileDataOperation operation = new ProfileDataOperation();

            operation.setFlag(flag);
            //operation.setIsFirst(1);

            operation.setPbNamePrefix(profileContact.getPbNamePrefix());
            operation.setPbNameFirst(profileContact.getPbNameFirst());
            operation.setPbNameMiddle(profileContact.getPbNameMiddle());
            operation.setPbNameLast(profileContact.getPbNameLast());
            operation.setPbNameSuffix(profileContact.getPbNameSuffix());
            operation.setPbPhoneticNameFirst(profileContact.getPbPhoneticNameFirst());
            operation.setPbPhoneticNameMiddle(profileContact.getPbPhoneticNameMiddle());
            operation.setPbPhoneticNameLast(profileContact.getPbPhoneticNameLast());

            operation.setIsFavourite(String.valueOf(profileContact.getIsFavourite()));

            operation.setPbPhoneNumber(profileContact.getPbPhoneNumber());
            operation.setPbEmailId(profileContact.getPbEmailId());
            operation.setPbWebAddress(profileContact.getPbWebAddress());
            operation.setPbOrganization(profileContact.getPbOrganization());
            operation.setPbAddress(profileContact.getPbAddress());
            operation.setPbIMAccounts(profileContact.getPbIMAccounts());
            operation.setPbEvent(profileContact.getPbEvent());

            arrayListOperation.add(operation);
            profileData.setOperation(arrayListOperation);

            arrayListReSyncUserContact.add(profileData);
        }
        //</editor-fold>

    }

    private void prepareForDeletion(ArrayList<String> list) {
        for (String deletedRawId : list) {

            ProfileData profileData = new ProfileData();
            profileData.setLocalPhoneBookId(deletedRawId);

            ArrayList<ProfileDataOperation> arrayListOperations = new ArrayList<>();

            ProfileDataOperation operation = new ProfileDataOperation();
            operation.setFlag(IntegerConstants.SYNC_DELETE_CONTACT);

            arrayListOperations.add(operation);

            profileData.setOperation(arrayListOperations);

            arrayListReSyncUserContact.add(profileData);
        }
    }

    private void syncRecentCallLogDataToServer(ArrayList<CallLogType> list) {
        if (syncCallLogAsyncTask != null && syncCallLogAsyncTask.isCancelled())
            return;
        if (Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)) {

            if (list.size() > CALL_LOG_CHUNK) {
                ArrayList<CallLogType> callLogTypeArrayList = divideCallLogByChunck();
                if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0) {
                    insertServiceCall(callLogTypeArrayList);
                }
            } else {
                insertServiceCall(list);
            }
        }
    }

    private void syncCallLogDataToServer(ArrayList<CallLogType> list) {
        if (syncCallLogAsyncTask != null && syncCallLogAsyncTask.isCancelled())
            return;
        if (Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)) {
            if (!Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, false)) {
                if (list.size() > CALL_LOG_CHUNK) {
                    ArrayList<CallLogType> callLogTypeArrayList = divideCallLogByChunck();
                    if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0) {
                        insertServiceCall(callLogTypeArrayList);
                    }
                } else {
                    insertServiceCall(list);
                }
            }
        }
    }

    private ArrayList<ArrayList<String>> chopped(ArrayList<String> list, final int L) {
        ArrayList<ArrayList<String>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    private ArrayList<CallLogType> divideCallLogByChunck() {
        callLogsListbyChunck = new ArrayList<>();
        try {
            for (ArrayList<CallLogType> partition : choppedCallLog(callLogTypeArrayListMain,
                    CALL_LOG_CHUNK)) {
                // do something with partition
                callLogsListbyChunck.addAll(partition);
                callLogTypeArrayListMain.removeAll(partition);
                break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return callLogsListbyChunck;
    }

    private ArrayList<ArrayList<CallLogType>> choppedCallLog(ArrayList<CallLogType> list, final
    int L) {
        ArrayList<ArrayList<CallLogType>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    private String getPhoneNumberType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return getString(R.string.type_home);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return getString(R.string.type_mobile);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return getString(R.string.type_work);

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return getString(R.string.type_fax_work);

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                return getString(R.string.type_fax_home);

            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return getString(R.string.type_pager);

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return getString(R.string.type_other);

            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return getString(R.string.type_callback);

            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return getString(R.string.type_car);

            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                return getString(R.string.type_company_main);

            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                return getString(R.string.type_isdn);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return getString(R.string.type_main);

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                return getString(R.string.type_other_fax);

            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                return getString(R.string.type_radio);

            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                return getString(R.string.type_telex);

            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                return getString(R.string.type_tty_tdd);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return getString(R.string.type_work_mobile);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                return getString(R.string.type_work_pager);

            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return getString(R.string.type_assistant);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                return getString(R.string.type_mms);

        }
        return getString(R.string.type_other);
    }

    private String getLogType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return getString(R.string.call_log_incoming);
            case CallLog.Calls.OUTGOING_TYPE:
                return getString(R.string.call_log_outgoing);
            case CallLog.Calls.MISSED_TYPE:
                return getString(R.string.call_log_missed);
            case CallLog.Calls.REJECTED_TYPE:
                return getString(R.string.call_log_rejected);
            case CallLog.Calls.BLOCKED_TYPE:
                return getString(R.string.call_log_blocked);
            case CallLog.Calls.VOICEMAIL_TYPE:
                return getString(R.string.call_log_voice_mail);

        }
//        return getString(R.string.type_other);
        return getString(R.string.type_other);
    }

    private String getRawContactIdFromNumber(String phoneNumber) {
        String numberId, rawId = "";
        try {

//            numberId = "";
            ContentResolver contentResolver = this.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                   /* String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));*/
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
                    Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] projection1 = new String[]{
                            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
                    };

                    String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
                    String[] selectionArgs = new String[]{numberId};

                    Cursor cursor1 = getContentResolver().query(uri1, projection1, selection,
                            selectionArgs, null);
                    if (cursor1 != null) {
                        while (cursor1.moveToNext()) {
                            rawId = cursor1.getString(cursor1.getColumnIndexOrThrow
                                    (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                        }
                        cursor1.close();
                    }
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rawId;
    }

    private ArrayList<CallLogType> callLogHistory(String number) {
        ArrayList<CallLogType> callDetails = new ArrayList<>();
        Cursor cursor;

        Pattern numberPat = Pattern.compile("\\d+");
//        Pattern numberPat = Pattern.compile("[+][0-9]+");
        Matcher matcher1 = numberPat.matcher(number);
        if (matcher1.find()) {
            cursor = getCallHistoryDataByNumber(number);
        } else {
            cursor = getCallHistoryDataByName(number);
        }

        try {
            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int callLogId = cursor.getColumnIndex(CallLog.Calls._ID);
                int account = -1;
                int account_id = -1;
                int profileImage = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    account = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME);
                    //for versions above lollipop
                    account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                    profileImage = cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);
                } else {
                    account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                }
                while (cursor.moveToNext()) {
                    String phNum = cursor.getString(number1);
                    int callType = Integer.parseInt(cursor.getString(type));
                    String callDate = cursor.getString(date);
                    long dateOfCall = Long.parseLong(callDate);
                    String callDuration = cursor.getString(duration);
                    String accountId = " ";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        accountId = cursor.getString(account_id);
//                        if (!TextUtils.isEmpty(accountId) && account_id > 0)
//                            Log.e("Sim Type", accountId);

                        String accountName = cursor.getString(account);
//                        if (!TextUtils.isEmpty(accountName))
//                            Log.e("Sim Name", accountName);

                    } else {
                        if (account_id > 0) {
                            accountId = cursor.getString(account_id);
//                            Log.e("Sim Type", accountId);
                        }
                    }
                    int histroyId = Integer.parseInt(cursor.getString(callLogId));
                    CallLogType logObject = new CallLogType();
                    logObject.setHistoryNumber(phNum);
                    logObject.setHistoryType(callType);
                    logObject.setHistoryDate(dateOfCall);
                    logObject.setHistoryDuration(Integer.parseInt(callDuration));
                    logObject.setHistoryCallSimNumber(accountId);
                    logObject.setHistoryId(histroyId);

//                    Date date1 = new Date(dateOfCall);
//                    String callDataAndTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format
//                            (date1);
//                    logObject.setCallDateAndTime(callDataAndTime);

                    String typeOfCall = getLogType(callType);
                    if (typeOfCall.equalsIgnoreCase(getString(R.string.call_log_rejected))) {
                        typeOfCall = getString(R.string.call_log_missed);
                    }
                    logObject.setTypeOfCall(typeOfCall);

//                    String durationtoPass = logObject.getCoolDuration(Float.parseFloat
//                            (callDuration));
//
//                    logObject.setDurationToPass(durationtoPass);

                    callDetails.add(logObject);
                    break;
                }

                cursor.close();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return callDetails;
    }

    private Cursor getCallHistoryDataByNumber(String number) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.NUMBER + " =?", new String[]{number}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    private Cursor getCallHistoryDataByName(String name) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.CACHED_NAME + " =?", new String[]{name}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    @SuppressWarnings("unused")
    private void showPermissionConfirmationDialog() {


        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        permissionConfirmationDialog.dismissDialog();
                        finish();
                        break;

                    case R.id.rippleRight:
                        permissionConfirmationDialog.dismissDialog();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        };

        permissionConfirmationDialog = new MaterialDialog(this, cancelListener);
        permissionConfirmationDialog.setTitleVisibility(View.GONE);
        permissionConfirmationDialog.setLeftButtonText(getString(R.string.action_cancel));
        permissionConfirmationDialog.setRightButtonText(getString(R.string.action_ok));
        permissionConfirmationDialog.setDialogBody(getString(R.string.call_log_permission));

        permissionConfirmationDialog.showDialog();

    }

    //</editor-fold>

    //<editor-fold desc="Async Tasks">

    private class ReSyncContactAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            reSyncPhoneBookContactList();

            if (!Utils.getStringPreference(MainActivity.this, AppConstants
                    .PREF_CALL_LOG_SYNC_TIME, "0").equalsIgnoreCase("0")) {
                getLatestCallLogsByRawId();
            }

            return null;
        }
    }

    private class SyncCallLogAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (Utils.getStringPreference(MainActivity.this,
                    AppConstants.PREF_CALL_LOG_SYNC_TIME, "0").equalsIgnoreCase("0"))
                getCallLogsByRawId();
            else {
                getLatestCallLogsByRawId();
            }
            return null;
        }
    }

    private class GetSpamAndRCPDetailAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getNumbersFromCallLog();
            makeListOfNumbersForSpamCount();
            return null;
        }
    }

    private void makeListOfNumbersForSpamCount() {
        if (callListForSpamCount != null && callListForSpamCount.size() > 0) {
            getProfileDataServiceCall(callListForSpamCount);
        }
    }

    private void getNumbersFromCallLog() {
        try {
            if (callListForSpamCount != null && callListForSpamCount.size() > 0)
                callListForSpamCount.clear();
            Uri uri = CallLog.Calls.CONTENT_URI;
            String order = CallLog.Calls.DATE + " DESC";
            Cursor cursor = this.getContentResolver().query(uri, null, null, null, order);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String userNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    String numberToSend = Utils.getFormattedNumber(MainActivity.this, userNumber);
                    String name = getNameFromNumber(numberToSend);
                    if (StringUtils.isEmpty(name))
                        callListForSpamCount.add(numberToSend);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //</editor-fold>

    private String getNameFromNumber(String phoneNumber) {
        String contactName = "";
        try {

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactName;
    }


    //<editor-fold desc="Web Service Call">

    private void insertServiceCall(ArrayList<CallLogType> callLogTypeArrayList) {

        if (Utils.isNetworkAvailable(MainActivity.this)) {
            WsRequestObject deviceDetailObject = new WsRequestObject();
            deviceDetailObject.setArrayListCallLogType(callLogTypeArrayList);
            deviceDetailObject.setResponseKey(Utils.getStringPreference(MainActivity.this,
                    AppConstants.PREF_CALL_LOG_RESPONSE_KEY, ""));
            deviceDetailObject.setFlag(IntegerConstants.SYNC_INSERT_CALL_LOG);
            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        deviceDetailObject, null, WsResponseObject.class, WsConstants
                        .REQ_UPLOAD_CALL_LOGS, null, true).executeOnExecutor(AsyncTask
                                .THREAD_POOL_EXECUTOR,
                        WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CALL_LOGS);
            }
        }

    }

    private void getProfileDataServiceCall(ArrayList<String> numbersList) {

        if (Utils.isNetworkAvailable(MainActivity.this)) {
            WsRequestObject deviceDetailObject = new WsRequestObject();
            deviceDetailObject.setUnknownNumberList(numbersList);

            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    deviceDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_PROFILE_DATA, null, true).execute(
                    WsConstants.WS_ROOT + WsConstants.REQ_GET_PROFILE_DATA);

        }
    }

    private void uploadContacts(String responseKey, ArrayList<ProfileData>
            arrayListUserContact) {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setResponseKey(responseKey);
        uploadContactObject.setProfileData(arrayListUserContact);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS + "_" + currentStamp, null, true).executeOnExecutor
                    (AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + WsConstants
                            .REQ_UPLOAD_CONTACTS);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Broadcast Receiver">

    private BroadcastReceiver localBroadcastReceiverContactDisplayed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkPermissionToExecute();
                        } else {
                            if (Utils.isNetworkAvailable(MainActivity.this)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_CONTACT_SYNCED, false)
                                    && !Utils.getBooleanPreference(MainActivity.this,
                                    AppConstants.PREF_CALL_LOG_SYNCED, false)) {
                                if (syncCallLogAsyncTask != null && syncCallLogAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                                    System.out.println("RContact syncCallLogAsyncTask ---> running");
                                } else {
                                    syncCallLogAsyncTask = new SyncCallLogAsyncTask();
                                    syncCallLogAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }

                            if (Utils.isNetworkAvailable(MainActivity.this)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_CONTACT_SYNCED, false)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_CALL_LOG_SYNCED, false)
                                    && !Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_GOT_ALL_PROFILE_DATA, false)) {

                                getSpamAndRCPDetailAsyncTask = new GetSpamAndRCPDetailAsyncTask();
                                getSpamAndRCPDetailAsyncTask.executeOnExecutor(AsyncTask
                                        .THREAD_POOL_EXECUTOR);
                            }

                            /*if (Utils.isNetworkAvailable(MainActivity.this)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_CONTACT_SYNCED, false)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_CALL_LOG_SYNCED, false)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_GOT_ALL_PROFILE_DATA, false)
                                    && !Utils.getBooleanPreference(MainActivity.this,
                                    AppConstants.PREF_SMS_SYNCED, false)) {

                                syncSmsLogAsyncTask = new SyncSmsLogAsyncTask();
                                syncSmsLogAsyncTask.executeOnExecutor(AsyncTask
                                        .THREAD_POOL_EXECUTOR);
                            }*/

                            if (Utils.isNetworkAvailable(MainActivity.this)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_CONTACT_SYNCED, false)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_CALL_LOG_SYNCED, false)
                                    /*&& Utils.getBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_SMS_SYNCED, false)*/) {
                                reSyncContactAsyncTask = new ReSyncContactAsyncTask();
                                reSyncContactAsyncTask.executeOnExecutor(AsyncTask
                                        .THREAD_POOL_EXECUTOR);
                            }
                        }
                    }
                }, 200);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver localBroadcastReceiverRecentCalls = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.i("CallLogFragment", "onReceive() of LocalBroadcast");
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (Utils.getBooleanPreference(MainActivity.this,
                                AppConstants.PREF_RECENT_CALLS_BROADCAST_RECEIVER_MAIN_INSTANCE,
                                false)) {
                            Utils.setBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_RECENT_CALLS_BROADCAST_RECEIVER_MAIN_INSTANCE, false);
                            Utils.setBooleanPreference(MainActivity.this, AppConstants
                                    .PREF_CALL_LOG_STARTS_FIRST_TIME, true);
                            AppConstants.isFromReceiver = false;

                            if (!Utils.getStringPreference(MainActivity.this, AppConstants
                                    .PREF_CALL_LOG_SYNC_TIME, "0").equalsIgnoreCase("0"))
                                getLatestCallLogsByRawId();
                        }
                    }
                }, 300);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver localBroadCastReceiverUpdateCount = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                updateNotificationCount();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private BroadcastReceiver localBroadcastReceiverCallLogSync = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (callLogTypeArrayListMain != null && callLogTypeArrayListMain.size() > 0)
                syncCallLogDataToServer(callLogTypeArrayListMain);
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute();
                } else {
                    if (Utils.isNetworkAvailable(MainActivity.this)
                            && Utils.getBooleanPreference(MainActivity.this, AppConstants
                            .PREF_CONTACT_SYNCED, false)
                            && !Utils.getBooleanPreference(MainActivity.this, AppConstants
                            .PREF_CALL_LOG_SYNCED, false)) {
                        if (syncCallLogAsyncTask != null && syncCallLogAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                            System.out.println("RContact syncCallLogAsyncTask ---> running");
                        } else {
                            syncCallLogAsyncTask = new SyncCallLogAsyncTask();
                            syncCallLogAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }

                    }
                }
            }
        }
    };

    private BroadcastReceiver localBroadCastReceiverGetGlobalProfileData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.isNetworkAvailable(MainActivity.this)
                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                    .PREF_CONTACT_SYNCED, false)
                    && Utils.getBooleanPreference(MainActivity.this, AppConstants
                    .PREF_CALL_LOG_SYNCED, false)
                    && !Utils.getBooleanPreference(MainActivity.this, AppConstants
                    .PREF_GOT_ALL_PROFILE_DATA, false)) {

                getSpamAndRCPDetailAsyncTask = new GetSpamAndRCPDetailAsyncTask();
                getSpamAndRCPDetailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };

    //</editor-fold>

    //<editor-fold desc="SMS Module">

//    private final int SMS_CHUNK = 50;
//    int LIST_PARTITION_COUNT = 20;
//    SmsFragment smsFragment;
//    ArrayList<SmsDataType> newSmsList;
//    private SyncSmsLogAsyncTask syncSmsLogAsyncTask;
//    ArrayList<CallLogType> newList;
//    private ArrayList<SmsDataType> smsLogTypeArrayListMain;
//    ArrayList<SmsDataType> smsLogsListbyChunck;

    @SuppressWarnings("unused")
    private void openSMSComposerPage() {

        Intent intent = new Intent("android.intent.action.VIEW");

        /** creates an sms uri */
        Uri data = Uri.parse("sms:");

        /** Setting sms uri to the intent */
        intent.setData(data);

        /** Initiates the SMS compose screen, because the activity contain ACTION_VIEW and sms
         * uri */
        startActivity(intent);

    }

    @SuppressWarnings("unused")
    private String getMessageType(int type) {
        switch (type) {
            case Telephony.Sms.MESSAGE_TYPE_DRAFT:
                return getString(R.string.msg_draft);

            case Telephony.Sms.MESSAGE_TYPE_FAILED:
                return getString(R.string.msg_failed);

            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                return getString(R.string.msg_received);

            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                return getString(R.string.msg_outbox);

            case Telephony.Sms.MESSAGE_TYPE_QUEUED:
                return getString(R.string.msg_queued);

            case Telephony.Sms.MESSAGE_TYPE_SENT:
                return getString(R.string.msg_sent);

        }
        return getString(R.string.type_other);
    }

        /*private BroadcastReceiver localBroadcastReceiverSmsLogSync = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (smsLogTypeArrayListMain != null && smsLogTypeArrayListMain.size() > 0)
                syncSMSLogDataToServer(smsLogTypeArrayListMain);
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute();
                } else {
                    if (Utils.isNetworkAvailable(MainActivity.this)
                            && Utils.getBooleanPreference(MainActivity.this, AppConstants
                            .PREF_CONTACT_SYNCED, false)
                            && Utils.getBooleanPreference(MainActivity.this, AppConstants
                            .PREF_CALL_LOG_SYNCED, false)
                            && !Utils.getBooleanPreference(MainActivity.this, AppConstants
                            .PREF_SMS_SYNCED, false)) {
                        syncSmsLogAsyncTask = new SyncSmsLogAsyncTask();
                        syncSmsLogAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        }
    };*/

    /*private ArrayList<ArrayList<SmsDataType>> choppedSmsLog(ArrayList<SmsDataType> list, final
    int L) {
        ArrayList<ArrayList<SmsDataType>> parts = new ArrayList<ArrayList<SmsDataType>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<SmsDataType>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }*/

     /*private ArrayList<SmsDataType> divideSmsLogByChunck() {
        int size = smsLogTypeArrayListMain.size();
        smsLogsListbyChunck = new ArrayList<>();
        for (ArrayList<SmsDataType> partition : choppedSmsLog(smsLogTypeArrayListMain,
                SMS_CHUNK)) {
            // do something with partition
//            Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
            smsLogsListbyChunck.addAll(partition);
            smsLogTypeArrayListMain.removeAll(partition);
            break;
        }
        return smsLogsListbyChunck;
    }*/

    /*private ArrayList<SmsDataType> divideSmsLogByChunck(ArrayList<SmsDataType> list) {
        int size = 0;
        smsLogsListbyChunck = new ArrayList<>();
        if (list != null && list.size() > 0) {
            size = list.size();
            if (size > SMS_CHUNK) {
                for (ArrayList<SmsDataType> partition : choppedSmsLog(list, SMS_CHUNK)) {
                    // do something with partition
//                    Log.i("Partition of Call Logs", partition.size() + " from " + size + "");

                    smsLogsListbyChunck.addAll(partition);
                    newSmsList.removeAll(partition);
                    break;
                }
            } else {
                smsLogsListbyChunck.addAll(list);
                newSmsList.removeAll(list);

            }
        }

        return smsLogsListbyChunck;
    }*/


    /*private ArrayList<CallLogType> divideCallLogByChunck(ArrayList<CallLogType> list) {
        int size = 0;
        callLogsListbyChunck = new ArrayList<>();
        if (list != null && list.size() > 0) {
            size = list.size();
            if (size > SMS_CHUNK) {
                for (ArrayList<CallLogType> partition : choppedCallLog(list, SMS_CHUNK)) {
                    // do something with partition
//                    Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
                    callLogsListbyChunck.addAll(partition);
                    callLogTypeArrayListMain.removeAll(partition);
                    break;
                }
            } else {
                callLogsListbyChunck.addAll(list);
                callLogTypeArrayListMain.removeAll(list);

            }
        }

        return callLogsListbyChunck;
    }*/

    private String getPhotoUrlFromNumber(String phoneNumber) {
        String photoThumbUrl = "";
        try {

            photoThumbUrl = "";
            ContentResolver contentResolver = getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    /*String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));*/
                    photoThumbUrl = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.PHOTO_THUMBNAIL_URI));
//                Log.d("LocalPBId", "contactMatch id: " + numberId + " of " + contactName);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return photoThumbUrl;
    }

    private void pullMechanismServiceCall(String fromDate, String toDate, String url) {

        if (Utils.isNetworkAvailable(MainActivity.this)) {
            WsRequestObject deviceDetailObject = new WsRequestObject();

            deviceDetailObject.setFromDate(fromDate);
            deviceDetailObject.setToDate(toDate);

            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        deviceDetailObject, null, WsResponseObject.class, url, null, true).executeOnExecutor(AsyncTask
                        .THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + url);
            }
        }

    }

    private void RCPContactServiceCall(String timestamp, String url) {

        if (Utils.isNetworkAvailable(MainActivity.this)) {
            WsRequestObject deviceDetailObject = new WsRequestObject();

            deviceDetailObject.setTimeStamp(timestamp);

            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        deviceDetailObject, null, WsResponseObject.class, url, null, true).executeOnExecutor(AsyncTask
                        .THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + url);
            }
        }

    }

    //</editor-fold>

     /*private BroadcastReceiver localBroadCastReceiverRecentSMS = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                if (Utils.getBooleanPreference(MainActivity.this,
                        AppConstants.PREF_RECENT_SMS_BROADCAST_RECEIVER_MAIN_INSTANCE, false)) {
                    Utils.setBooleanPreference(MainActivity.this, AppConstants
                            .PREF_RECENT_SMS_BROADCAST_RECEIVER_MAIN_INSTANCE, false);
                    Utils.setBooleanPreference(MainActivity.this, AppConstants
                            .PREF_SMS_LOG_STARTS_FIRST_TIME, true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };*/

    /*private class SyncSmsLogAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getSmsLogsByRawIds();
            return null;
        }
    }*/

    /*private void getLatestSms() {

        try {
            String prefDate = Utils.getStringPreference(MainActivity.this, AppConstants
            .PREF_SMS_SYNC_TIME, "");
            String dateToCompare = "";
            String currentDate = "";
            long dateToConvert = 0;
            if (!StringUtils.isEmpty(prefDate)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale
                .getDefault());
                dateToConvert = sdf.parse(prefDate).getTime();
                dateToCompare = String.valueOf(dateToConvert);
                currentDate = String.valueOf(System.currentTimeMillis());
            }

            Cursor cursor = this.getContentResolver().query(Telephony.Sms.CONTENT_URI, null,
                    Telephony.Sms.DATE + " BETWEEN ? AND ?"
                    , new String[]{dateToCompare, currentDate}, Telephony.Sms.DEFAULT_SORT_ORDER);

            ArrayList<String> listOfIds = new ArrayList<>();

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    listOfIds.add(cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID)));
                }
                cursor.close();
            }

            if (listOfIds.size() > 0) {
                int indexToBeginSync = Utils.getIntegerPreference(this, AppConstants
                        .PREF_SMS_LOG_SYNCED_COUNT, 0);
                ArrayList<String> tempIdsList = new ArrayList<>();
                for (int i = indexToBeginSync; i < listOfIds.size(); i++) {
                    String ids = listOfIds.get(i);
                    tempIdsList.add(ids);
                }

                if (tempIdsList.size() > SMS_CHUNK) {
                    for (ArrayList<String> partition : chopped(tempIdsList, SMS_CHUNK)) {
                        // do something with partition
                        fetchSMSDataById(partition);
                    }
                } else {
                    if (tempIdsList.size() <= 0)
                        fetchSMSDataById(listOfIds);
                    else {
                        fetchSMSDataById(tempIdsList);
                    }
                }

            } else {
                Utils.setBooleanPreference(this, AppConstants
                        .PREF_SMS_SYNCED, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*private void getSmsLogsByRawIds() {
        PhoneBookSMSLogs phoneBookSmsLogs = new PhoneBookSMSLogs(MainActivity.this);
        ArrayList<String> listOfIds = new ArrayList<>();
        Cursor cursor = phoneBookSmsLogs.getAllSMSLogId();
        if (cursor != null) {
            int rowId = cursor.getColumnIndex(Telephony.Sms._ID);
            while (cursor.moveToNext()) {
                listOfIds.add(cursor.getString(rowId));
            }
            cursor.close();
        }

        if (listOfIds.size() > 0) {
            int indexToBeginSync = Utils.getIntegerPreference(this, AppConstants
                    .PREF_SMS_LOG_SYNCED_COUNT, 0);
            ArrayList<String> tempIdsList = new ArrayList<>();
            for (int i = indexToBeginSync; i < listOfIds.size(); i++) {
                String ids = listOfIds.get(i);
                tempIdsList.add(ids);
            }

            if (tempIdsList.size() > SMS_CHUNK) {
                for (ArrayList<String> partition : chopped(tempIdsList, SMS_CHUNK)) {
                    // do something with partition
                    fetchSMSDataById(partition);
                }
            } else {
                if (tempIdsList.size() <= 0)
                    fetchSMSDataById(listOfIds);
                else {
                    fetchSMSDataById(tempIdsList);

                }
            }

        } else {
            Utils.setBooleanPreference(this, AppConstants
                    .PREF_SMS_SYNCED, true);
        }
    }*/

    // Utils.setStringPreference(this, AppConstants.PREF_SMS_SYNC_TIME, profileDetail
    // .getSmsLogTimestamp());
    /*private void fetchSMSDataById(ArrayList<String> listOfRowIds) {

        try {
            ArrayList<SmsDataType> smsDataTypeList = new ArrayList<>();
            for (int i = 0; i < listOfRowIds.size(); i++) {
                String uniqueCallLogId = listOfRowIds.get(i);
                if (!TextUtils.isEmpty(uniqueCallLogId)) {
                    String order = Telephony.Sms.DEFAULT_SORT_ORDER;
                    Cursor cursor = MainActivity.this.getContentResolver().query(Telephony.Sms
                                    .CONTENT_URI,
                            null, Telephony.Sms._ID + " = " + uniqueCallLogId, null, order);

                    if (cursor != null) {
                        if (cursor.getCount() > 0) {
                            int number = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS);
                            int id = cursor.getColumnIndexOrThrow(Telephony.Sms._ID);
                            int body = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY);
                            int date = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE);
                            int read = cursor.getColumnIndexOrThrow(Telephony.Sms.READ);
                            int type = cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE);
                            int thread_id = cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID);
                            while (cursor.moveToNext()) {
                                if (syncSmsLogAsyncTask != null && syncSmsLogAsyncTask
                                        .isCancelled())
                                    return;
                                SmsDataType smsDataType = new SmsDataType();
                                String address = cursor.getString(number);
                                String contactNumber = "";
                                if (!TextUtils.isEmpty(address)) {
                                    Pattern numberPat = Pattern.compile("[a-zA-Z]+");
                                    Matcher matcher1 = numberPat.matcher(address);
                                    if (matcher1.find()) {
                                        smsDataType.setAddress(address);
                                    } else {
                                        final String formattedNumber = Utils.getFormattedNumber
                                                (MainActivity.this, address);
                                        String contactName = getContactNameFromNumber
                                                (formattedNumber);
                                        if (!TextUtils.isEmpty(contactName)) {
                                            smsDataType.setAddress(contactName);
                                            smsDataType.setNumber(formattedNumber);
                                        } else {
                                            smsDataType.setAddress(formattedNumber);
                                            smsDataType.setNumber(formattedNumber);
                                        }
                                        contactNumber = formattedNumber;
                                    }
                                    smsDataType.setBody(cursor.getString(body));
                                    smsDataType.setDataAndTime(cursor.getLong(date));
                                    smsDataType.setIsRead(cursor.getString(read));
                                    smsDataType.setUniqueRowId(cursor.getString(id));
                                    smsDataType.setThreadId(cursor.getString(thread_id));
                                    String smsType = getMessageType(cursor.getInt(type));
                                    smsDataType.setTypeOfMessage(smsType);
                                    smsDataType.setFlag(11);
                                    String photoThumbNail = getPhotoUrlFromNumber(contactNumber);
                                    if (!TextUtils.isEmpty(photoThumbNail)) {
                                        smsDataType.setProfileImage(photoThumbNail);
                                    } else {
                                        smsDataType.setProfileImage("");
                                    }
                                    smsDataTypeList.add(smsDataType);
                                    smsLogTypeArrayListMain.add(smsDataType);
                                }

                            }
                            cursor.close();

                        }

                    }
                }
            }
            syncSMSLogDataToServer(smsLogTypeArrayListMain);
//            makeSimpleDataThreadWise(smsDataTypeList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

//    private void makeSimpleDataThreadWise(ArrayList<SmsDataType> filteredList) {
//        if (filteredList != null && filteredList.size() > 0) {
//            ArrayList<SmsDataType> smsLogTypeArrayListMain = new ArrayList<>();
//            for (int k = 0; k < filteredList.size(); k++) {
//                SmsDataType smsDataType = filteredList.get(k);
//                String threadId = smsDataType.getThreadId();
//                if (smsLogTypeArrayListMain.size() == 0) {
//                    smsLogTypeArrayListMain.add(smsDataType);
//
//                } else {
//                    boolean isNumberExists = false;
//                    for (int j = 0; j < smsLogTypeArrayListMain.size(); j++) {
//                        if (smsLogTypeArrayListMain.get(j) instanceof SmsDataType) {
//                            if (!((smsLogTypeArrayListMain.get(j))
//                                    .getThreadId().equalsIgnoreCase(threadId))) {
//                                isNumberExists = false;
//                            } else {
//                                isNumberExists = true;
//                                break;
//                            }
//                        }
//                    }
//                    if (!isNumberExists) {
//                        smsLogTypeArrayListMain.add(smsDataType);
//                    }
//                }
//            }
//            rContactApplication.setArrayListSmsLogType(smsLogTypeArrayListMain);
//        }
//    }

    /*private void syncSMSLogDataToServer(ArrayList<SmsDataType> list) {
        if (syncSmsLogAsyncTask != null && syncSmsLogAsyncTask.isCancelled())
            return;
        if (Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)) {
            if (Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED,
                    false) && !Utils.getBooleanPreference(this, AppConstants.PREF_SMS_SYNCED,
                    false)) {
                if (list.size() > SMS_CHUNK) {
                    ArrayList<SmsDataType> callLogTypeArrayList = divideSmsLogByChunck();
                    if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0) {
                        insertSMSLogServiceCall(callLogTypeArrayList);
                    }
                } else {
                    insertSMSLogServiceCall(list);
                }

            }

        }
    }*/


    /*private void insertSMSLogServiceCall(ArrayList<SmsDataType> smsLogTypeArrayList) {

        if (Utils.isNetworkAvailable(MainActivity.this)) {
            WsRequestObject deviceDetailObject = new WsRequestObject();
            deviceDetailObject.setArrayListSmsDataType(smsLogTypeArrayList);
            deviceDetailObject.setFlag(11);
            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        deviceDetailObject, null, WsResponseObject.class, WsConstants
                        .REQ_UPLOAD_SMS_LOGS, null, true).executeOnExecutor(AsyncTask
                                .THREAD_POOL_EXECUTOR,
                        WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_SMS_LOGS);
            }
        }

    }*/
    //</editor-fold>

}
