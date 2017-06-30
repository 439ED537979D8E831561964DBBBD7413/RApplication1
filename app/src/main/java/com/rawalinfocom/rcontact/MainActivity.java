package com.rawalinfocom.rcontact;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.calldialer.DialerActivity;
import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ContactsFragment;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.PhoneBookCallLogs;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.PhoneBookSMSLogs;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableNotificationStateMaster;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.database.TableWebsiteMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Address;
import com.rawalinfocom.rcontact.model.CallLogType;
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
import com.rawalinfocom.rcontact.model.SmsDataType;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.Website;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.notifications.EventsActivity;
import com.rawalinfocom.rcontact.notifications.NotificationsActivity;
import com.rawalinfocom.rcontact.notifications.RatingHistory;
import com.rawalinfocom.rcontact.notifications.TimelineActivity;
import com.rawalinfocom.rcontact.receivers.NetworkConnectionReceiver;
import com.rawalinfocom.rcontact.sms.SmsFragment;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
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

public class MainActivity extends BaseActivity implements NavigationView
        .OnNavigationItemSelectedListener, WsResponseListener {

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
    SmsFragment smsFragment;
    private PhoneBookContacts phoneBookContacts;
    NetworkConnectionReceiver networkConnectionReceiver;
    RContactApplication rContactApplication;
    private ArrayList<ProfileData> arrayListReSyncUserContact;
    private String currentStamp;
    int LIST_PARTITION_COUNT = 10;
    private ArrayList<CallLogType> callLogTypeArrayListMain;
    ArrayList<CallLogType> callLogsListbyChunck;
    ArrayList<CallLogType> newList;
    ArrayList<SmsDataType> newSmsList;
    int logsSyncedCount = 10;
    MaterialDialog permissionConfirmationDialog;
    private String[] requiredPermissions = {Manifest.permission.READ_CONTACTS, Manifest
            .permission.READ_CALL_LOG, Manifest.permission.READ_SMS};
    boolean isCompaseIcon = false;
    private SyncCallLogAsyncTask syncCallLogAsyncTask;
    private ReSyncContactAsyncTask reSyncContactAsyncTask;
    private SyncSmsLogAsyncTask syncSmsLogAsyncTask;
    private ArrayList<SmsDataType> smsLogTypeArrayListMain;
    ArrayList<SmsDataType> smsLogsListbyChunck;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_main);

        ButterKnife.bind(this);
        rContactApplication = (RContactApplication) getApplicationContext();
        phoneBookContacts = new PhoneBookContacts(this);
        callLogTypeArrayListMain = new ArrayList<>();
        smsLogTypeArrayListMain = new ArrayList<>();
        CallLogFragment.callLogTypeReceiver = new CallLogType();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        networkConnectionReceiver = new NetworkConnectionReceiver();
        init();
        registerBroadcastReceiver();
        registerLocalBroadCastReceiver();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute() {
        boolean contacts = ContextCompat.checkSelfPermission(MainActivity.this,
                requiredPermissions[0]) ==
                PackageManager.PERMISSION_GRANTED;
        boolean logs = ContextCompat.checkSelfPermission(MainActivity.this,
                requiredPermissions[1]) ==
                PackageManager.PERMISSION_GRANTED;
        boolean smsLogs = ContextCompat.checkSelfPermission(MainActivity.this,
                requiredPermissions[2]) ==
                PackageManager.PERMISSION_GRANTED;
        if (logs) {
            if (Utils.isNetworkAvailable(this)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)
                    && !Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, false)) {
                syncCallLogAsyncTask = new SyncCallLogAsyncTask();
                syncCallLogAsyncTask.execute();
            }

        }
        if (smsLogs) {
            if (Utils.isNetworkAvailable(this)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, false)
                    && !Utils.getBooleanPreference(this, AppConstants.PREF_SMS_SYNCED, false)) {
                syncSmsLogAsyncTask = new SyncSmsLogAsyncTask();
                syncSmsLogAsyncTask.execute();
            }
        }
        if (contacts) {
            if (Utils.isNetworkAvailable(this)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, false)
                    && Utils.getBooleanPreference(this, AppConstants.PREF_SMS_SYNCED, false)) {
                reSyncContactAsyncTask = new ReSyncContactAsyncTask();
                reSyncContactAsyncTask.execute();
            }
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

    private void updateNotificationCount() {
        int count = getNotificationCount(databaseHandler);
        if (count > 0) {
            badgeLayout.setVisibility(View.VISIBLE);
            badgeTextView.setText(String.valueOf(count));
        } else {
            badgeLayout.setVisibility(View.GONE);
        }
        count = getTimeLineNotificationCount(databaseHandler);
        LinearLayout view = (LinearLayout) navigationView.getMenu().findItem(R.id
                .nav_user_timeline).getActionView();
        TextView textView = (TextView) view.findViewById(R.id.badge_count);
        if (count > 0) {
            view.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(count));
        } else {
            view.setVisibility(View.GONE);
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = AppConstants.PLAY_STORE_LINK + getPackageName();
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
        } else if (id == R.id.nav_invite) {
            startActivityIntent(MainActivity.this, ContactListingActivity.class, null);
        } else if (id == R.id.nav_db_export) {
            if (BuildConfig.DEBUG) {
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
            }
        } else if (id == R.id.nav_user_timeline) {
            startActivityIntent(MainActivity.this, TimelineActivity.class, null);
        } else if (id == R.id.nav_user_events) {
            startActivityIntent(MainActivity.this, EventsActivity.class, null);
        } else if (id == R.id.nav_user_rating_history) {
            startActivityIntent(this, RatingHistory.class, new Bundle());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

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

                    String currentTimeStamp = (StringUtils.split(serviceType, "_"))[1];
//                    Log.i("MAULIK", "currentTimeStamp" + currentTimeStamp);
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

                    if (Utils.getBooleanPreference(this, AppConstants
                            .PREF_CALL_LOG_SYNCED, false)) {
                        LIST_PARTITION_COUNT = 10;
                        ArrayList<CallLogType> temp = divideCallLogByChunck(newList);
                        if (temp.size() >= LIST_PARTITION_COUNT) {
                            if (temp != null && temp.size() > 0)
                                insertServiceCall(newList);
                        } else {
                            Log.e("onDeliveryResponse: ", "All Call Logs Synced");
                        }

                    } else {
                        ArrayList<CallLogType> callLogTypeArrayList = divideCallLogByChunck();
                        if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0) {
                            insertServiceCall(callLogTypeArrayList);
                            logsSyncedCount = logsSyncedCount + callLogTypeArrayList.size();
                        } else {
//                            Toast.makeText(this,"All Call Logs Synced",Toast.LENGTH_SHORT).show();
                            Utils.setBooleanPreference(this, AppConstants
                                    .PREF_CALL_LOG_SYNCED, true);

                            Intent localBroadcastIntent = new Intent(AppConstants
                                    .ACTION_LOCAL_BROADCAST_SYNC_SMS);
                            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                                    .getInstance(MainActivity.this);
                            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                        }
                        Utils.setIntegerPreference(this, AppConstants.PREF_CALL_LOG_SYNCED_COUNT,
                                logsSyncedCount);


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

            // <editor-fold desc="REQ_UPLOAD_SMS_LOGS">
            else if (serviceType.equalsIgnoreCase(WsConstants.REQ_UPLOAD_SMS_LOGS)) {

                WsResponseObject smsLogInsertionResponse = (WsResponseObject) data;
                if (smsLogInsertionResponse != null && StringUtils.equalsIgnoreCase
                        (smsLogInsertionResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    if (Utils.getBooleanPreference(this, AppConstants
                            .PREF_SMS_SYNCED, false)) {
                        ArrayList<SmsDataType> temp = divideSmsLogByChunck(newSmsList);
                        LIST_PARTITION_COUNT = 20;
                        if (temp.size() >= LIST_PARTITION_COUNT) {
                            if (temp != null && temp.size() > 0)
                                insertSMSLogServiceCall(newSmsList);
                        } else {
                            Log.e("onDeliveryResponse: ", "All SMS Logs Synced");
                        }
                    } else {
                        ArrayList<SmsDataType> callLogTypeArrayList = divideSmsLogByChunck();
                        if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0) {
                            insertSMSLogServiceCall(callLogTypeArrayList);
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
            //</editor-fold>
        } else {
            Log.e("error", error.getMessage());
        }
    }


    private void storeToMobileMapping(ArrayList<ProfileDataOperation> profileData) {
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
    }

    private void storeToEmailMapping(ArrayList<ProfileDataOperation> profileData) {
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
    }

    private void storeProfileDataToDb(ArrayList<ProfileDataOperation> profileData,
                                      ArrayList<ProfileData> mapping) {

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
            userProfile.setPmSuffix(profileData.get(i).getPbNameSuffix());
            userProfile.setPmPrefix(profileData.get(i).getPbNamePrefix());
            userProfile.setPmFirstName(profileData.get(i).getPbNameFirst());
            userProfile.setPmMiddleName(profileData.get(i).getPbNameMiddle());
            userProfile.setPmLastName(profileData.get(i).getPbNameLast());
            userProfile.setPmPhoneticFirstName(profileData.get(i).getPbPhoneticNameFirst());
            userProfile.setPmPhoneticMiddleName(profileData.get(i).getPbPhoneticNameMiddle());
            userProfile.setPmPhoneticLastName(profileData.get(i).getPbPhoneticNameLast());
            userProfile.setPmIsFavourite(profileData.get(i).getIsFavourite());
            userProfile.setPmNotes(profileData.get(i).getPbNote());
            userProfile.setPmNickName(profileData.get(i).getPbNickname());
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
                        email.setEmIsVerified(arrayListEmailId.get(j).getEmRcpType());
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

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        if (syncCallLogAsyncTask != null)
            syncCallLogAsyncTask.cancel(true);
        if (syncSmsLogAsyncTask != null)
            syncSmsLogAsyncTask.cancel(true);
        if (networkConnectionReceiver != null) {
            unregisterBroadcastReceiver();
        }

        unRegisterLocalBroadCastReceiver();
        Utils.setBooleanPreference(this, AppConstants
                .PREF_CALL_LOG_STARTS_FIRST_TIME, true);

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
                else {
                    AppConstants.isComposingSMS = true;
                    openSMSComposerPage();
                }
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (BuildConfig.DEBUG) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_db_export).setVisible(true);
        }

        tabMain = (TabLayout) findViewById(R.id.tab_main);

        bindWidgetsWithAnEvent();
        setupTabLayout();
        Utils.changeTabsFont(this, tabMain);

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
        smsFragment = SmsFragment.newInstance();
        tabMain.addTab(tabMain.newTab().setText(getString(R.string.tab_contact)), true);
        tabMain.addTab(tabMain.newTab().setText(getString(R.string.tab_call)));
        tabMain.addTab(tabMain.newTab().setText(getString(R.string.tab_sms)));
    }

    int tabPosition = -1;

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

    MaterialDialog callConfirmationDialog;

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
                showAddToContact(true);
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable
                        .ic_mode_edit));
                isCompaseIcon = true;
                replaceFragment(smsFragment);
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


        LocalBroadcastManager localBroadcastManagerReceiveRecentSms = LocalBroadcastManager
                .getInstance(MainActivity.this);
        IntentFilter intentFilter2 = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_SMS);
        localBroadcastManagerReceiveRecentSms.registerReceiver(localBroadCastReceiverRecentSMS,
                intentFilter2);

        LocalBroadcastManager localBroadcastManagerSyncSmsLogs = LocalBroadcastManager
                .getInstance(this);
        IntentFilter intentFilter1 = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_SYNC_SMS);
        localBroadcastManagerSyncSmsLogs.registerReceiver(localBroadcastReceiverSmsLogSync,
                intentFilter1);

        LocalBroadcastManager localBroadcastManagerUpdateNotificationCount = LocalBroadcastManager
                .getInstance(MainActivity.this);
        IntentFilter intentFilterUpdateCount = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_UPDATE_NOTIFICATION_COUNT);
        localBroadcastManagerUpdateNotificationCount.registerReceiver
                (localBroadCastReceiverUpdateCount, intentFilterUpdateCount);

        LocalBroadcastManager contactDisplayed = LocalBroadcastManager.getInstance(MainActivity.this);
        IntentFilter intentFilterContactDisplayed = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_CONTACT_DISPLAYED);
        contactDisplayed.registerReceiver
                (localBroadcastReceiverContactDisplayed, intentFilterContactDisplayed);

    }

    private void unRegisterLocalBroadCastReceiver() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                .getInstance(this);

        localBroadcastManager.unregisterReceiver(localBroadcastReceiverCallLogSync);
        localBroadcastManager.unregisterReceiver(localBroadcastReceiverRecentCalls);
        localBroadcastManager.unregisterReceiver(localBroadCastReceiverRecentSMS);
        localBroadcastManager.unregisterReceiver(localBroadcastReceiverSmsLogSync);
        localBroadcastManager.unregisterReceiver(localBroadCastReceiverUpdateCount);
        localBroadcastManager.unregisterReceiver(localBroadcastReceiverContactDisplayed);
    }

    private void getCallLogsByRawId() {

        ArrayList<String> callLogsIdsList = Utils.getArrayListPreference(this, AppConstants
                .PREF_CALL_LOGS_ID_SET);
        if (callLogsIdsList == null) {
            PhoneBookCallLogs phoneBookCallLogs = new PhoneBookCallLogs(this);
            callLogsIdsList = new ArrayList<>();
            Cursor cursor = phoneBookCallLogs.getAllCallLogId();
            if (cursor != null) {
                int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                while (cursor.moveToNext()) {
                    callLogsIdsList.add(cursor.getString(rowId));
                }
                cursor.close();
            }
            Utils.setArrayListPreference(this, AppConstants.PREF_CALL_LOGS_ID_SET, callLogsIdsList);
        }

        if (callLogsIdsList != null && callLogsIdsList.size() > 0) {
            int indexToBeginSync = Utils.getIntegerPreference(this, AppConstants
                    .PREF_CALL_LOG_SYNCED_COUNT, 0);
            ArrayList<String> tempIdsList = new ArrayList<>();
            for (int i = indexToBeginSync; i < callLogsIdsList.size(); i++) {
                String ids = callLogsIdsList.get(i);
                tempIdsList.add(ids);
            }
            LIST_PARTITION_COUNT = 10;
            if (tempIdsList.size() > LIST_PARTITION_COUNT) {
                for (ArrayList<String> partition : chopped(tempIdsList, LIST_PARTITION_COUNT)) {
                    // do something with partition
                    fetchCallLogsFromIds(partition);
                }
            } else {
                fetchCallLogsFromIds(tempIdsList);
            }

        } else {
            Utils.setBooleanPreference(this, AppConstants
                    .PREF_CALL_LOG_SYNCED, true);
            Intent localBroadcastIntent = new Intent(AppConstants
                    .ACTION_LOCAL_BROADCAST_SYNC_SMS);
            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                    .getInstance(MainActivity.this);
            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
        }
    }


    private void fetchCallLogsFromIds(ArrayList<String> listOfRowIds) {
        try {
            for (int i = 0; i < listOfRowIds.size(); i++) {
                String uniqueCallLogId = listOfRowIds.get(i);
                if (!TextUtils.isEmpty(uniqueCallLogId)) {
                    String order = CallLog.Calls.DATE + " ASC";
                    Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                            null, CallLog.Calls._ID + " = " + uniqueCallLogId, null, order);

                    if (cursor != null) {
                        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                        int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                        int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);

                        while (cursor.moveToNext()) {

                            if (syncCallLogAsyncTask != null && syncCallLogAsyncTask.isCancelled())
                                return;

                            CallLogType log = new CallLogType(this);
                            log.setNumber(cursor.getString(number));
                            String userName = cursor.getString(name);
                            if (!TextUtils.isEmpty(userName))
                                log.setName(userName);
                            else
                                log.setName("");

                            log.setType(cursor.getInt(type));
                            log.setDuration(cursor.getInt(duration));
                            log.setDate(cursor.getLong(date));
                            log.setUniqueContactId(cursor.getString(rowId));
                            String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                            log.setNumberType(numberTypeLog);
                            String userNumber = cursor.getString(number);
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
                                log.setCallDateAndTime(tempCallLogType.getCallDateAndTime());
                                log.setTypeOfCall(tempCallLogType.getTypeOfCall());
                                log.setDurationToPass(tempCallLogType.getDurationToPass());
                                if (!StringUtils.isEmpty(tempCallLogType.getHistoryCallSimNumber()))
                                    log.setHistoryCallSimNumber(tempCallLogType
                                            .getHistoryCallSimNumber());
                                else
                                    log.setHistoryCallSimNumber(" ");
                                // 16/06/2017 changed done end
                            }
                            int logCount = arrayListHistoryCount.size();
                            log.setHistoryLogCount(logCount);
                            callLogTypeArrayListMain.add(log);
                            rContactApplication.setArrayListCallLogType(callLogTypeArrayListMain);
                        }
                        cursor.close();
                    }
                }
            }
            syncCallLogDataToServer(callLogTypeArrayListMain);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
                            && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CONTACT_SYNCED, false)
                            && !Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CALL_LOG_SYNCED, false)) {
                        syncCallLogAsyncTask = new SyncCallLogAsyncTask();
                        syncCallLogAsyncTask.execute();
                    }
                }
            }
        }
    };


    private BroadcastReceiver localBroadcastReceiverSmsLogSync = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (smsLogTypeArrayListMain != null && smsLogTypeArrayListMain.size() > 0)
                syncSMSLogDataToServer(smsLogTypeArrayListMain);
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionToExecute();
                } else {
                    if (Utils.isNetworkAvailable(MainActivity.this)
                            && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CONTACT_SYNCED, false)
                            && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CALL_LOG_SYNCED, false)
                            && !Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_SMS_SYNCED, false)) {
                        syncSmsLogAsyncTask = new SyncSmsLogAsyncTask();
                        syncSmsLogAsyncTask.execute();
                    }
                }
            }
        }
    };

    private void syncCallLogDataToServer(ArrayList<CallLogType> list) {
        if (syncCallLogAsyncTask != null && syncCallLogAsyncTask.isCancelled())
            return;
        if (Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)) {
            if (!Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED,
                    false)) {
                LIST_PARTITION_COUNT = 10;
                if (list.size() > LIST_PARTITION_COUNT) {
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

    private void insertServiceCall(ArrayList<CallLogType> callLogTypeArrayList) {

        if (Utils.isNetworkAvailable(MainActivity.this)) {
            WsRequestObject deviceDetailObject = new WsRequestObject();
            deviceDetailObject.setArrayListCallLogType(callLogTypeArrayList);
            deviceDetailObject.setFlag(IntegerConstants.SYNC_INSERT_CALL_LOG);
            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        deviceDetailObject, null, WsResponseObject.class, WsConstants
                        .REQ_UPLOAD_CALL_LOGS, null, true).execute
                        (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CALL_LOGS);
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

    private ArrayList<ArrayList<SmsDataType>> choppedSmsLog(ArrayList<SmsDataType> list, final
    int L) {
        ArrayList<ArrayList<SmsDataType>> parts = new ArrayList<ArrayList<SmsDataType>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<SmsDataType>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    private ArrayList<CallLogType> divideCallLogByChunck() {
        int size = callLogTypeArrayListMain.size();
        callLogsListbyChunck = new ArrayList<>();
        LIST_PARTITION_COUNT = 10;
        for (ArrayList<CallLogType> partition : choppedCallLog(callLogTypeArrayListMain,
                LIST_PARTITION_COUNT)) {
            // do something with partition
//            Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
            callLogsListbyChunck.addAll(partition);
            callLogTypeArrayListMain.removeAll(partition);
            break;
        }
        return callLogsListbyChunck;
    }

    private ArrayList<SmsDataType> divideSmsLogByChunck() {
        int size = smsLogTypeArrayListMain.size();
        smsLogsListbyChunck = new ArrayList<>();
        for (ArrayList<SmsDataType> partition : choppedSmsLog(smsLogTypeArrayListMain,
                LIST_PARTITION_COUNT)) {
            // do something with partition
//            Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
            smsLogsListbyChunck.addAll(partition);
            smsLogTypeArrayListMain.removeAll(partition);
            break;
        }
        return smsLogsListbyChunck;
    }

    private ArrayList<SmsDataType> divideSmsLogByChunck(ArrayList<SmsDataType> list) {
        int size = 0;
        smsLogsListbyChunck = new ArrayList<>();
        LIST_PARTITION_COUNT = 20;
        if (list != null && list.size() > 0) {
            size = list.size();
            if (size > LIST_PARTITION_COUNT) {
                for (ArrayList<SmsDataType> partition : choppedSmsLog(list,
                        LIST_PARTITION_COUNT)) {
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
    }


    private ArrayList<CallLogType> divideCallLogByChunck(ArrayList<CallLogType> list) {
        int size = 0;
        callLogsListbyChunck = new ArrayList<>();
        LIST_PARTITION_COUNT = 10;
        if (list != null && list.size() > 0) {
            size = list.size();
            if (size > LIST_PARTITION_COUNT) {
                for (ArrayList<CallLogType> partition : choppedCallLog(list,
                        LIST_PARTITION_COUNT)) {
                    // do something with partition
//                    Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
                    callLogsListbyChunck.addAll(partition);
                    newList.removeAll(partition);
                    break;
                }
            } else {
                callLogsListbyChunck.addAll(list);
                newList.removeAll(list);

            }
        }

        return callLogsListbyChunck;
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

    private ArrayList callLogHistory(String number) {
        ArrayList<CallLogType> callDetails = new ArrayList<>();
        Cursor cursor;

        Pattern numberPat = Pattern.compile("\\d+");
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

                    Date date1 = new Date(dateOfCall);
                    String callDataAndTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format
                            (date1);
                    logObject.setCallDateAndTime(callDataAndTime);

                    String typeOfCall = getLogType(callType);
                    if (typeOfCall.equalsIgnoreCase(getString(R.string.call_log_rejected))) {
                        typeOfCall = getString(R.string.call_log_missed);
                    }
                    logObject.setTypeOfCall(typeOfCall);

                    String durationtoPass = logObject.getCoolDuration(Float.parseFloat
                            (callDuration));
                    logObject.setDurationToPass(durationtoPass);

                    callDetails.add(logObject);
                    break;
                }
            }

            cursor.close();
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


    private class ReSyncContactAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            reSyncPhoneBookContactList();
            return null;
        }
    }

    private class SyncCallLogAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getCallLogsByRawId();
            return null;
        }
    }

    private class SyncSmsLogAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getSmsLogsByRawIds();
            return null;
        }
    }

    private void getSmsLogsByRawIds() {
        PhoneBookSMSLogs phoneBookSmsLogs = new PhoneBookSMSLogs(MainActivity.this);
        ArrayList<String> listOfIds = new ArrayList<>();
        Cursor cursor = phoneBookSmsLogs.getAllSMSLogId();
        if (cursor != null) {
            int rowId = cursor.getColumnIndex(Telephony.Sms._ID);
            while (cursor.moveToNext()) {
                listOfIds.add(cursor.getString(rowId));
            }
        }
        cursor.close();

        if (listOfIds != null && listOfIds.size() > 0) {
            int indexToBeginSync = Utils.getIntegerPreference(this, AppConstants
                    .PREF_SMS_LOG_SYNCED_COUNT, 0);
            ArrayList<String> tempIdsList = new ArrayList<>();
            for (int i = indexToBeginSync; i < listOfIds.size(); i++) {
                String ids = listOfIds.get(i);
                tempIdsList.add(ids);
            }

            LIST_PARTITION_COUNT = 20;
            if (tempIdsList.size() > LIST_PARTITION_COUNT) {
                for (ArrayList<String> partition : chopped(tempIdsList, LIST_PARTITION_COUNT)) {
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

    }

    private void fetchSMSDataById(ArrayList<String> listOfRowIds) {

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
                        if (cursor != null && cursor.getCount() > 0) {
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
                                        // Todo: Add format number method before setting the address
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
            makeSimpleDataThreadWise(smsDataTypeList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeSimpleDataThreadWise(ArrayList<SmsDataType> filteredList) {
        if (filteredList != null && filteredList.size() > 0) {
            ArrayList<SmsDataType> smsLogTypeArrayListMain = new ArrayList<>();
            for (int k = 0; k < filteredList.size(); k++) {
                SmsDataType smsDataType = filteredList.get(k);
                String threadId = smsDataType.getThreadId();
                if (smsLogTypeArrayListMain.size() == 0) {
                    smsLogTypeArrayListMain.add(smsDataType);

                } else {
                    boolean isNumberExists = false;
                    for (int j = 0; j < smsLogTypeArrayListMain.size(); j++) {
                        if (smsLogTypeArrayListMain.get(j) instanceof SmsDataType) {
                            if (!((smsLogTypeArrayListMain.get(j))
                                    .getThreadId().equalsIgnoreCase(threadId))) {
                                isNumberExists = false;
                            } else {
                                isNumberExists = true;
                                break;
                            }
                        }
                    }
                    if (!isNumberExists) {
                        smsLogTypeArrayListMain.add(smsDataType);
                    }
                }
            }
            rContactApplication.setArrayListSmsLogType(smsLogTypeArrayListMain);
        }
    }

    private void syncSMSLogDataToServer(ArrayList<SmsDataType> list) {
        if (syncSmsLogAsyncTask != null && syncSmsLogAsyncTask.isCancelled())
            return;
        if (Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)) {
            if (Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED,
                    false) && !Utils.getBooleanPreference(this, AppConstants.PREF_SMS_SYNCED,
                    false)) {
                LIST_PARTITION_COUNT = 20;
                if (list.size() > LIST_PARTITION_COUNT) {
                    ArrayList<SmsDataType> callLogTypeArrayList = divideSmsLogByChunck();
                    if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0) {
                        insertSMSLogServiceCall(callLogTypeArrayList);
                    }
                } else {
                    insertSMSLogServiceCall(list);
                }

            }

        }
    }


    private void insertSMSLogServiceCall(ArrayList<SmsDataType> smsLogTypeArrayList) {

        if (Utils.isNetworkAvailable(MainActivity.this)) {
            WsRequestObject deviceDetailObject = new WsRequestObject();
            deviceDetailObject.setArrayListSmsDataType(smsLogTypeArrayList);
            deviceDetailObject.setFlag(11);
            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        deviceDetailObject, null, WsResponseObject.class, WsConstants
                        .REQ_UPLOAD_SMS_LOGS, null, true).execute
                        (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_SMS_LOGS);
            }
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

    private BroadcastReceiver localBroadcastReceiverContactDisplayed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.i("MAULIK", "onReceive() of localBroadcastReceiverContactDisplayed");
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkPermissionToExecute();
                        } else {
                            if (Utils.isNetworkAvailable(MainActivity.this)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CONTACT_SYNCED, false)
                                    && !Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CALL_LOG_SYNCED, false)) {
                                syncCallLogAsyncTask = new SyncCallLogAsyncTask();
                                syncCallLogAsyncTask.execute();
                            }

                            if (Utils.isNetworkAvailable(MainActivity.this)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CONTACT_SYNCED, false)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CALL_LOG_SYNCED, false)
                                    && !Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_SMS_SYNCED, false)) {
                                syncSmsLogAsyncTask = new SyncSmsLogAsyncTask();
                                syncSmsLogAsyncTask.execute();
                            }
                            if (Utils.isNetworkAvailable(MainActivity.this)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CONTACT_SYNCED, false)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_CALL_LOG_SYNCED, false)
                                    && Utils.getBooleanPreference(MainActivity.this, AppConstants.PREF_SMS_SYNCED, false)) {
//                                Log.i("MAULIK", " looking for updated contacts");
                                reSyncContactAsyncTask = new ReSyncContactAsyncTask();
                                reSyncContactAsyncTask.execute();
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
                            CallLogFragment.isIdsFetchedFirstTime = false;
//                                rContactApplication.setArrayListCallLogType(null);
                        }
                    }
                }, 100);

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

    private BroadcastReceiver localBroadCastReceiverRecentSMS = new BroadcastReceiver() {
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
    };

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
//        Log.i("MAULIK", "reSyncPhoneBookContactList.currentStamp: " + currentStamp);
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
//        Log.i("MAULIK", " getAllContactRawId");

        Cursor contactNameCursor = phoneBookContacts.getAllContactRawId();

        Set<String> arrayListNewContactId = new HashSet<>();
        while (contactNameCursor.moveToNext()) {
            arrayListNewContactId.add(contactNameCursor.getString(contactNameCursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID)));
        }

        contactNameCursor.close();
//        Log.i("MAULIK", " getAllContactRawId Complete");

        Set<String> removedContactIds = new HashSet<>(arrayListOldContactIds);
        Set<String> insertedContactIds = new HashSet<>(arrayListNewContactId);
        removedContactIds.removeAll(insertedContactIds);
        insertedContactIds.removeAll(arrayListOldContactIds);

//        Log.i("MAULIK", "inserted" + insertedContactIds.toString());
//
//        Log.i("MAULIK", "removed" + removedContactIds.toString());

        updatedContactIds.removeAll(insertedContactIds);
//        Log.i("MAULIK", "updated" + updatedContactIds.toString());

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
//            Log.i("MAULIK", "updated inCaluse:" + inClause);
            prepareData(IntegerConstants.SYNC_UPDATE_CONTACT, inClause);
        }
        if (insertedContactIds.size() > 0) {
            //inserted 1
            ArrayList<String> list = new ArrayList<>(insertedContactIds);
            String inClause = list.toString();
            inClause = inClause.replace("[", "(");
            inClause = inClause.replace("]", ")");
//            Log.i("MAULIK", "inserted inCaluse:" + inClause);
            prepareData(IntegerConstants.SYNC_INSERT_CONTACT, inClause);

        }
//        Log.i("MAULIK", "arrayListReSyncUserContact.size: " + arrayListReSyncUserContact.size());
        if (Utils.isNetworkAvailable(this) && arrayListReSyncUserContact.size() > 0) {
            if (arrayListReSyncUserContact.size() <= 100) {
                Log.i(TAG, "sending updated contacts to server");
                uploadContacts();
            } else {

                Log.i(TAG, "need to apply resync mechanism:");
            }
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
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?, ?, ?, ?, ?, ?, ?) and " + ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " in " + inCaluse;
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

                        phoneNumber.setPhoneNumber(Utils.getFormattedNumber(this, cursor
                                .getString(cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Phone.NUMBER))));
                        phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                                (cursor.getInt(cursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.TYPE))));
                        phoneNumber.setPhonePublic(IntegerConstants.PRIVACY_EVERYONE);

                        phoneBookContact.addPhone(phoneNumber);
                        break;
                    case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                        ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                        emailId.setEmEmailId(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
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
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .CITY)));
                        address.setCountry(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .COUNTRY)));
                        address.setNeighborhood(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .NEIGHBORHOOD)));
                        address.setPostCode(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .POSTCODE)));
                        address.setPoBox(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .POBOX)));
                        address.setStreet(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
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
//            Log.i("MAULIK", "actually deleted" + deletedRawId);

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

    private void uploadContacts() {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setPmId(Integer.parseInt(Utils.getStringPreference(this, AppConstants.PREF_USER_PM_ID, "0")));
        uploadContactObject.setProfileData(arrayListReSyncUserContact);
        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS + "_" + currentStamp, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CONTACTS);
        }
    }
}
