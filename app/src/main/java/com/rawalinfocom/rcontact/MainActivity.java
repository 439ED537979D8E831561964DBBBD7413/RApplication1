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
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.calldialer.DialerActivity;
import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ContactsFragment;
import com.rawalinfocom.rcontact.database.PhoneBookCallLogs;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.notifications.EventsActivity;
import com.rawalinfocom.rcontact.notifications.NotificationsActivity;
import com.rawalinfocom.rcontact.notifications.RatingHistory;
import com.rawalinfocom.rcontact.notifications.TimelineActivity;
import com.rawalinfocom.rcontact.receivers.NetworkConnectionReceiver;
import com.rawalinfocom.rcontact.services.CallLogIdFetchService;
import com.rawalinfocom.rcontact.sms.SmsFragment;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView
        .OnNavigationItemSelectedListener, WsResponseListener {

    @BindView(R.id.relative_root_contacts_main)
    RelativeLayout relativeRootContactsMain;
    Toolbar toolbar;
    TextView textImageNotification;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;

    TabLayout tabMain;

    ContactsFragment contactsFragment;
    CallLogFragment callLogFragment;
    SmsFragment smsFragment;

    NetworkConnectionReceiver networkConnectionReceiver;

    private ArrayList<ProfileData> arrayListUserContact;
    private ArrayList<ProfileData> arrayListDeletedUserContact;
    private PhoneBookContacts phoneBookContacts;
    int LIST_PARTITION_COUNT = 10;
    private ArrayList<String> listOfCallLogIds;
    private ArrayList<CallLogType> callLogTypeArrayListMain;
    ArrayList<CallLogType> callLogsListbyChunck;
    ArrayList<CallLogType> newList;
    String callLogResponseRowId = "";
    String callLogResponseDate = "";
    int logsSyncedCount = 10;
    MaterialDialog permissionConfirmationDialog;
    private String[] requiredPermissions = {Manifest.permission.READ_CONTACTS, Manifest
            .permission.READ_CALL_LOG};

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_main);
        ButterKnife.bind(this);
        /*Intent contactIdFetchService = new Intent(this, ContactSyncService.class);
        startService(contactIdFetchService);*/


        if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT, getResources()
                .getInteger(R.integer.launch_mobile_registration)) == getResources().getInteger(R
                .integer.launch_mobile_registration)) {
            finish();
            startActivityIntent(this, MobileNumberRegistrationActivity.class, null);
        } else if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT,
                getResources().getInteger(R.integer.launch_mobile_registration)) == getResources
                ().getInteger(R.integer.launch_otp_verification)) {
            finish();
            startActivityIntent(this, OtpVerificationActivity.class, null);
        } else if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT,
                getResources().getInteger(R.integer.launch_mobile_registration)) == getResources
                ().getInteger(R.integer.launch_profile_registration)) {
            /*UserProfile userProfile = (UserProfile) Utils.getObjectPreference(this, AppConstants
                    .PREF_REGS_USER_OBJECT, UserProfile.class);
            if (userProfile != null && StringUtils.equalsIgnoreCase(userProfile
                    .getIsAlreadyVerified(), String.valueOf(getResources().getInteger(R.integer
                    .profile_not_verified)))) {*/
            finish();
            startActivityIntent(this, ProfileRegistrationActivity.class, null);
//            }
        } else {

            callLogTypeArrayListMain = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermissionToExecute();
            } else {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        getCallLogsByRawId();
                    }
                });
            }
            checkPermissionToExecute();
           /* if (checkPermissionToExecute()) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        getCallLogsByRawId();
                    }
                });
            }*/
            registerLocalBroadCastReceiver();

          /*  if (ContextCompat.checkSelfPermission(this, android.Manifest.permission
                    .READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                Intent contactIdFetchService = new Intent(this, ContactSyncService.class);
                startService(contactIdFetchService);
            }*/

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            phoneBookContacts = new PhoneBookContacts(this);
            arrayListDeletedUserContact = new ArrayList<>();

            networkConnectionReceiver = new NetworkConnectionReceiver();

            init();
            registerBroadcastReceiver();

            /*if (!Utils.getStringPreference(this, AppConstants.PREF_CONTACT_LAST_SYNC_TIME, "")
                    .equalsIgnoreCase("")) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        syncBackgroundContacts();
                    }
                });*/

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute() {

        /*int permissionReadContact = ContextCompat.checkSelfPermission(this, Manifest.permission
                .READ_CONTACTS);
        int permissionReadCallLog = ContextCompat.checkSelfPermission(this, Manifest.permission
                .READ_CALL_LOG);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionReadContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (permissionReadCallLog != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]), AppConstants
                    .MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return false;
        }

        return true;*/

       /* boolean contacts = ContextCompat.checkSelfPermission(MainActivity.this,
       requiredPermissions[0]) !=
                PackageManager.PERMISSION_GRANTED;*/
        boolean logs = ContextCompat.checkSelfPermission(MainActivity.this,
                requiredPermissions[1]) ==
                PackageManager.PERMISSION_GRANTED;
        if (logs) {
            Intent callLogIdFetchService = new Intent(this, CallLogIdFetchService.class);
            startService(callLogIdFetchService);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    getCallLogsByRawId();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissionToExecute();
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            final String appPackageName = getPackageName();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
//                String shareBody = "Here is the share content body";
            String shareBody = AppConstants.PLAY_STORE_LINK + appPackageName;
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Contact Via");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share App Via"));
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
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    Toast.makeText(getApplicationContext(), "DB dump failed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        } else if (id == R.id.nav_user_timeline) {
            startActivityIntent(MainActivity.this, TimelineActivity.class, null);
        } else if (id == R.id.nav_user_events) {
            startActivityIntent(MainActivity.this, EventsActivity.class, null);
        } else if (id == R.id.nav_blocked_contacts) {
            startActivityIntent(this, BlockContactListActivity.class, new Bundle());
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
                WsResponseObject syncDeleteResponse = (WsResponseObject) data;
                if (syncDeleteResponse != null && StringUtils.equalsIgnoreCase
                        (syncDeleteResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Log.i("syncBackgroundContacts", "Sync Successful");
                    Toast.makeText(this, "Sync Successful", Toast.LENGTH_SHORT).show();
                    Utils.setStringPreference(this, AppConstants.PREF_CONTACT_LAST_SYNC_TIME,
                            String.valueOf(System.currentTimeMillis() - 10000));
                } else {
                    if (syncDeleteResponse != null) {
                        Log.e("error response", syncDeleteResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "uploadContactResponse null");
                    }
                }
            }
            //</editor-fold>
            else if (serviceType.equalsIgnoreCase(WsConstants.REQ_UPLOAD_CALL_LOGS)) {
                WsResponseObject callLogInsertionResponse = (WsResponseObject) data;
                if (callLogInsertionResponse != null && StringUtils.equalsIgnoreCase
                        (callLogInsertionResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    if (Utils.getBooleanPreference(this, AppConstants
                            .PREF_CALL_LOG_SYNCED, false)) {
                        ArrayList<CallLogType> temp = divideCallLogByChunck(newList);
                        if (temp.size() >= LIST_PARTITION_COUNT) {
                            if (temp != null && temp.size() > 0)
                                insertServiceCall(newList);
                        } else {
//                            Toast.makeText(this,"All Call Logs Synced",Toast.LENGTH_SHORT).show();
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
                        }
                        Utils.setIntegerPreference(this, AppConstants.PREF_CALL_LOG_SYNCED_COUNT,
                                logsSyncedCount);
                    }
                } else {
                    if (callLogInsertionResponse != null) {
                        Log.e("error response", callLogInsertionResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "userProfileResponse null");
                        Log.e("onDeliveryResponse: ", getString(R.string.msg_try_later));
//                        Toast.makeText(this,getString(R.string.msg_try_later),Toast
// .LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

/*        if (requestCode == AppConstants.READ_LOGS && permissions[0].equals(Manifest.permission
                .READ_CONTACTS) && permissions[1].equals(Manifest.permission.READ_CALL_LOG)) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED && grantResults[1] ==
                    PermissionChecker.PERMISSION_GRANTED) {

                Intent callLogIdFetchService = new Intent(this, CallLogIdFetchService.class);
                startService(callLogIdFetchService);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        getCallLogsByRawId();
                    }
                });

            } else {
                showPermissionConfirmationDialog();
            }
        }*/

       /* switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_CALL_LOG, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                   *//* if (perms.get(Manifest.permission.READ_CALL_LOG) != PackageManager
                            .PERMISSION_GRANTED || perms.get(Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                    }*//*
                    if (perms.get(Manifest.permission.READ_CONTACTS) != PackageManager
                            .PERMISSION_GRANTED) {

                    }
                    if (perms.get(Manifest.permission.READ_CALL_LOG) != PackageManager
                            .PERMISSION_GRANTED) {
                        Intent callLogIdFetchService = new Intent(this, CallLogIdFetchService
                                .class);
                        startService(callLogIdFetchService);

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                getCallLogsByRawId();
                            }
                        });
                    }
                }

            }
        }*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkConnectionReceiver != null) {
            unregisterBroadcastReceiver();
        }

        unRegisterLocalBroadCastReceiver();
        Utils.setBooleanPreference(this, AppConstants
                .PREF_CALL_LOG_STARTS_FIRST_TIME, true);
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        textImageNotification = (TextView) toolbar.findViewById(R.id.text_image_notification);
        textImageNotification.setTypeface(Utils.typefaceIcons(this));
//        textImageNotification.setText(Html.fromHtml("&#xe966;"));
//        textImageNotification.setText(Html.fromHtml("&#xe900;"));
        textImageNotification.setText(Html.fromHtml(getResources().getString(R.string.im_bell)));
        textImageNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIntent(MainActivity.this, NotificationsActivity.class, null);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Dial Pad", Snackbar.LENGTH_SHORT).show();
                openDialer();
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
        tabMain.addTab(tabMain.newTab().setText("CONTACTS"), true);
        tabMain.addTab(tabMain.newTab().setText("CALL LOG"));
        tabMain.addTab(tabMain.newTab().setText("SMS"));
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
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Ok");
        callConfirmationDialog.setDialogBody("If you switch, your current data will be lost. \n " +
                "Would you like to continue?");
        callConfirmationDialog.showDialog();

    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                showAddToContact(false);
                replaceFragment(contactsFragment);
                break;
            case 1:
                showAddToContact(true);
                replaceFragment(callLogFragment);
                break;
            case 2:
                showAddToContact(false);
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
    }

    private void unRegisterLocalBroadCastReceiver() {
        LocalBroadcastManager localBroadcastManagerProfileBlock = LocalBroadcastManager
                .getInstance(this);
        localBroadcastManagerProfileBlock.unregisterReceiver(localBroadcastReceiverCallLogSync);

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
            }
            cursor.close();
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

            if (tempIdsList.size() > LIST_PARTITION_COUNT) {
                for (ArrayList<String> partition : chopped(tempIdsList, LIST_PARTITION_COUNT)) {
                    // do something with partition
                    fetchCallLogsFromIds(partition);
                }
            } else {
                fetchCallLogsFromIds(tempIdsList);
            }

        }
    }

    // =========================================== Call Logs ==================================================//


 /*   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {

                    // Permission Granted
                    Intent callLogIdFetchService =  new Intent(this, CallLogIdFetchService.class);
                    startService(callLogIdFetchService);

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            getCallLogsByRawId();
                        }
                    });

                } else {

                    // Permission Denied
//                    getActivity().onBackPressed();
                    showPermissionConfirmationDialog();

                }
            }
            break;
        }
    }*/


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
                            Log.i("Number Type", numberTypeLog + " of number " + cursor.getString
                                    (number));
                            Log.i("Number Log Type", getLogType(cursor.getInt(type)) + " of " +
                                    "number " +
                                    cursor.getString(number));
                            log.setNumberType(numberTypeLog);
                            String userNumber = cursor.getString(number);
                            String uniquePhoneBookId = getStarredStatusFromNumber(userNumber);
                            Log.i("Unique PhoneBook Id", uniquePhoneBookId + " of no.:" +
                                    userNumber);
                            if (!TextUtils.isEmpty(uniquePhoneBookId))
                                log.setLocalPbRowId(uniquePhoneBookId);
                            else
                                log.setLocalPbRowId(" ");

                            log.setFlag(7);
                            ArrayList<CallLogType> arrayListHistory;
                   /* if (!TextUtils.isEmpty(userName)) {
                        arrayListHistory = callLogHistory(userName);
                    } else {*/
                            arrayListHistory = callLogHistory(userNumber);
//                    }
                            log.setArrayListCallHistory(arrayListHistory);

                            ArrayList<CallLogType> arrayListHistoryCount = new ArrayList<>();
                            for (int j = 0; j < arrayListHistory.size(); j++) {
                                CallLogType tempCallLogType = arrayListHistory.get(j);
                                String simNumber = arrayListHistory.get(j)
                                        .getHistoryCallSimNumber();
                                log.setCallSimNumber(simNumber);
                                long tempdate = tempCallLogType.getHistoryDate();
                                Date objDate1 = new Date(tempdate);
                                String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format
                                        (objDate1);
                                long callLogDate = log.getDate();
                                Date intentDate1 = new Date(callLogDate);
                                String intentDate = new SimpleDateFormat("yyyy-MM-dd").format
                                        (intentDate1);
                                if (intentDate.equalsIgnoreCase(arrayDate)) {
                                    arrayListHistoryCount.add(tempCallLogType);
                                }
                            }
                            int logCount = arrayListHistoryCount.size();
                            log.setHistoryLogCount(logCount);
                            Log.i("History size ", logCount + "" + " of " + cursor.getString
                                    (number));
                            Log.i("History", "----------------------------------");
                            callLogTypeArrayListMain.add(log);

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
            Log.i("CallLogFragment", "onReceive() of LocalBroadcast");
            syncCallLogDataToServer(callLogTypeArrayListMain);
        }
    };

    private void syncCallLogDataToServer(ArrayList<CallLogType> list) {
        if (Utils.getBooleanPreference(this, AppConstants.PREF_SYNC_CALL_LOG, false)) {
            if (!Utils.getBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED,
                    false)) {
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
            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        deviceDetailObject, null, WsResponseObject.class, WsConstants
                        .REQ_UPLOAD_CALL_LOGS, null, true).execute
                        (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CALL_LOGS);
            }
        }

    }

    private ArrayList<ArrayList<String>> chopped(ArrayList<String> list, final int L) {
        ArrayList<ArrayList<String>> parts = new ArrayList<ArrayList<String>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<String>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    private ArrayList<ArrayList<CallLogType>> choppedCallLog(ArrayList<CallLogType> list, final
    int L) {
        ArrayList<ArrayList<CallLogType>> parts = new ArrayList<ArrayList<CallLogType>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<CallLogType>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    private ArrayList<CallLogType> divideCallLogByChunck() {
        int size = callLogTypeArrayListMain.size();
        callLogsListbyChunck = new ArrayList<>();
        for (ArrayList<CallLogType> partition : choppedCallLog(callLogTypeArrayListMain,
                LIST_PARTITION_COUNT)) {
            // do something with partition
            Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
            callLogsListbyChunck.addAll(partition);
            callLogTypeArrayListMain.removeAll(partition);
            break;
        }
        return callLogsListbyChunck;
    }

    private ArrayList<CallLogType> divideCallLogByChunck(ArrayList<CallLogType> list) {
        int size = 0;
        callLogsListbyChunck = new ArrayList<>();
        if (list != null && list.size() > 0) {
            size = list.size();
            if (size > LIST_PARTITION_COUNT) {
                for (ArrayList<CallLogType> partition : choppedCallLog(list,
                        LIST_PARTITION_COUNT)) {
                    // do something with partition
                    Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
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
                return "Home";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "Mobile";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "Work";

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return "Fax Work";

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                return "Fax Home";

            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return "Pager";

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return "Callback";

            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return "Car";

            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                return "Company Main";

            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                return "ISDN";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return "Main";

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                return "Other Fax";

            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                return "Radio";

            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                return "Telex";

            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                return "Tty Tdd";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return "Work Mobile";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                return "Work Pager";

            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return "Assistant";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                return "MMS";

        }
        return "Other";
    }

    private String getLogType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "Missed";
            case CallLog.Calls.REJECTED_TYPE:
                return "Rejected";
            case CallLog.Calls.BLOCKED_TYPE:
                return "Blocked";
            case CallLog.Calls.VOICEMAIL_TYPE:
                return "Voicemail";

        }
        return "OTHERS";
    }

    private String getStarredStatusFromNumber(String phoneNumber) {
        String numberId = "";
        try {

            numberId = "";
            ContentResolver contentResolver = this.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
//                Log.d("LocalPBId", "contactMatch id: " + numberId + " of " + contactName);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return numberId;
    }

    private ArrayList callLogHistory(String number) {
        String numberToSearch = number;
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
                        if (!TextUtils.isEmpty(accountId) && account_id > 0)
                            Log.e("Sim Type", accountId);

                        String accountName = cursor.getString(account);
                        if (!TextUtils.isEmpty(accountName))
                            Log.e("Sim Name", accountName);

//                        String userImage = cursor.getString(profileImage);
//                        if (userImage != null)
//                            Log.e("User Image", userImage);
                    } else {
                        if (account_id > 0) {
                            accountId = cursor.getString(account_id);
                            Log.e("Sim Type", accountId);
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
                    if (typeOfCall.equalsIgnoreCase("Rejected")) {
                        typeOfCall = "Missed";
                    }
                    logObject.setTypeOfCall(typeOfCall);

                    String durationtoPass = logObject.getCoolDuration(Float.parseFloat
                            (callDuration));
                    logObject.setDurationToPass(durationtoPass);

                    callDetails.add(logObject);
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
        permissionConfirmationDialog.setLeftButtonText("Cancel");
        permissionConfirmationDialog.setRightButtonText("OK");
        permissionConfirmationDialog.setDialogBody("Call log permission is required. Do you want " +
                "to try again?");

        permissionConfirmationDialog.showDialog();

    }

    //============================================ Contacts ===================================================//
    public void syncBackgroundContacts() {

        /*Cursor cursor = phoneBookContacts.getUpdatedContacts(String.valueOf(System
                .currentTimeMillis() - 10000));*/
        Log.i("syncBackgroundContacts", "Started");
        Cursor cursor = phoneBookContacts.getUpdatedContacts(Utils.getStringPreference(this,
                AppConstants.PREF_CONTACT_LAST_SYNC_TIME, ""));

        String rawId = "-1";

        if (cursor != null && cursor.moveToNext()) {
            while (cursor.moveToNext()) {
                rawId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts
                        .LOOKUP_KEY));


                ArrayList<String> arrayListContactIds = new ArrayList<>();
                arrayListContactIds.addAll(Utils.getArrayListPreference(this, AppConstants
                        .PREF_CONTACT_ID_SET));

                if (arrayListContactIds.contains(rawId)) {

                    // Update

                    phoneBookOperations(rawId, String.valueOf(getResources().getInteger(R.integer
                            .sync_update)));

                } else if (rawId.equalsIgnoreCase("-1")) {

                    // Delete

                    Cursor contactNameCursor = phoneBookContacts.getAllContactId();

                    ArrayList<String> arrayListNewContactId = new ArrayList<>();

                    while (contactNameCursor.moveToNext()) {
                        arrayListNewContactId.add(contactNameCursor.getString(contactNameCursor
                                .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)));
//                        .getColumnIndex(ContactsContract.Contacts._ID)));
                    }

                    contactNameCursor.close();

                    Utils.setArrayListPreference(this, AppConstants.PREF_CONTACT_ID_SET,
                            arrayListNewContactId);

                    Set<String> oldContactIds = new HashSet<>(arrayListContactIds);
                    Set<String> newContactIds = new HashSet<>(arrayListNewContactId);
                    oldContactIds.removeAll(newContactIds);
                    ArrayList<String> arrayListDeletedIds = new ArrayList<>(oldContactIds);

                    if (arrayListDeletedIds.size() > 0) {
                        ProfileData profileData = new ProfileData();
                        profileData.setLocalPhoneBookId(arrayListDeletedIds.get(0));

                        ArrayList<ProfileDataOperation> arrayListOperations = new ArrayList<>();
                        ProfileDataOperation profileDataOperation = new ProfileDataOperation();
                        profileDataOperation.setFlag(getResources().getInteger(R.integer
                                .sync_delete));
                        arrayListOperations.add(profileDataOperation);

                        profileData.setOperation(arrayListOperations);

                        arrayListDeletedUserContact.add(profileData);

                        deleteContact();
                    }

                } else {

                    // Insert

                    if (!arrayListContactIds.contains(rawId)) {
                        Cursor contactNameCursor = phoneBookContacts.getAllContactId();

                        ArrayList<String> arrayListNewContactId = new ArrayList<>();

                        while (contactNameCursor.moveToNext()) {
                            arrayListNewContactId.add(contactNameCursor.getString(contactNameCursor
                                    .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)));
//                        .getColumnIndex(ContactsContract.Contacts._ID)));
                        }

                        contactNameCursor.close();

                        Utils.setArrayListPreference(this, AppConstants.PREF_CONTACT_ID_SET,
                                arrayListNewContactId);
                        phoneBookOperations(rawId, String.valueOf(getResources().getInteger(R
                                .integer
                                .sync_insert)));
                    }

                }
            }

            RContactApplication rContactApplication = (RContactApplication)
                    getApplicationContext();
            rContactApplication.setArrayListAllPhoneBookContacts(new ArrayList<>());
            rContactApplication.setArrayListFavPhoneBookContacts(new ArrayList<>());

            cursor.close();
        } else {
            Log.i("syncBackgroundContacts", "Nothing to sync");
        }
    }

    private void phoneBookOperations(String rawId, String flag) {
        arrayListUserContact = new ArrayList<>();

//        for (int i = forFrom; i < forTo; i++) {

        ProfileData profileData = new ProfileData();

        profileData.setLocalPhoneBookId(rawId);

        ProfileDataOperation operation = new ProfileDataOperation();
        operation.setFlag(Integer.parseInt(flag));

        //<editor-fold desc="Structured Name">
        Cursor contactStructuredNameCursor = phoneBookContacts.getStructuredName(rawId);
        ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

        if (contactStructuredNameCursor != null && contactStructuredNameCursor.getCount() > 0) {

            while (contactStructuredNameCursor.moveToNext()) {

                operation.setPbNamePrefix(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.PREFIX)));
                operation.setPbNameFirst(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.GIVEN_NAME)));
                operation.setPbNameMiddle(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.MIDDLE_NAME)));
                operation.setPbNameLast(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.FAMILY_NAME)));
                operation.setPbNameSuffix(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.SUFFIX)));
                operation.setPbPhoneticNameFirst(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME)));
                operation.setPbPhoneticNameMiddle(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME)));
                operation.setPbPhoneticNameLast(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME)));

            }
            contactStructuredNameCursor.close();
        }
//                arrayListOperation.add(operation);
        //</editor-fold>

        // <editor-fold desc="Starred Contact">
        Cursor starredContactCursor = phoneBookContacts.getStarredStatus(rawId);

        if (starredContactCursor != null && starredContactCursor.getCount() > 0) {

            if (starredContactCursor.moveToNext()) {
                String isFavourite = starredContactCursor.getString(starredContactCursor
                        .getColumnIndex(ContactsContract.Contacts.STARRED));
                operation.setIsFavourite(isFavourite);
            }
            starredContactCursor.close();
        }
        //</editor-fold>

        //<editor-fold desc="Contact Number">
        Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(rawId);
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

        if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
            int numberCount = 0;
            while (contactNumberCursor.moveToNext()) {

                ProfileDataOperationPhoneNumber phoneNumber = new
                        ProfileDataOperationPhoneNumber();

                phoneNumber.setPhoneId(String.valueOf(++numberCount));
                phoneNumber.setPhoneNumber(Utils.getFormattedNumber(this, contactNumberCursor
                        .getString(contactNumberCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.NUMBER))));
                phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType(contactNumberCursor
                        .getInt(contactNumberCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.TYPE))));
                phoneNumber.setPhonePublic(1);

                arrayListPhoneNumber.add(phoneNumber);

            }
            contactNumberCursor.close();
        }
        operation.setPbPhoneNumber(arrayListPhoneNumber);
        //</editor-fold>

        //<editor-fold desc="Email Id">
        Cursor contactEmailCursor = phoneBookContacts.getContactEmail(rawId);
        ArrayList<ProfileDataOperationEmail> arrayListEmailId = new ArrayList<>();

        if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
            int emailCount = 0;
            while (contactEmailCursor.moveToNext()) {

                ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                emailId.setEmId(String.valueOf(++emailCount));
                emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                        contactEmailCursor.getInt
                                (contactEmailCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Email.TYPE))));
                emailId.setEmPublic(1);

                arrayListEmailId.add(emailId);

            }
            contactEmailCursor.close();
        }
        operation.setPbEmailId(arrayListEmailId);
        //</editor-fold>

        //<editor-fold desc="Website">
        Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(rawId);
        ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();

        if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
            int websiteCount = 0;
            while (contactWebsiteCursor.moveToNext()) {

                ProfileDataOperationWebAddress webAddress = new
                        ProfileDataOperationWebAddress();

                webAddress.setWebId(String.valueOf(++websiteCount));
                webAddress.setWebAddress(contactWebsiteCursor.getString(contactWebsiteCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                webAddress.setWebType(phoneBookContacts.getWebsiteType(contactWebsiteCursor,
                        (contactWebsiteCursor.getInt(contactWebsiteCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Website.TYPE)))));

                arrayListWebsite.add(webAddress);

            }
            contactWebsiteCursor.close();
        }

        operation.setPbWebAddress(arrayListWebsite);
        //</editor-fold>

        //<editor-fold desc="Organization">
        Cursor contactOrganizationCursor = phoneBookContacts.getContactOrganization(rawId);
        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new
                ArrayList<>();

        if (contactOrganizationCursor != null && contactOrganizationCursor.getCount() > 0) {

            int organizationCount = 0;

            while (contactOrganizationCursor.moveToNext()) {

                ProfileDataOperationOrganization organization = new
                        ProfileDataOperationOrganization();

                organization.setOrgId(String.valueOf(++organizationCount));
                organization.setOrgName(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.COMPANY)));
                organization.setOrgJobTitle(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.TITLE)));
                organization.setOrgDepartment(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.DEPARTMENT)));
                organization.setOrgType(phoneBookContacts.getOrganizationType
                        (contactOrganizationCursor,
                                contactOrganizationCursor.getInt((contactOrganizationCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .Organization.TYPE)))));
                organization.setOrgJobDescription(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                organization.setOrgOfficeLocation(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.OFFICE_LOCATION)));

                arrayListOrganization.add(organization);

            }
            contactOrganizationCursor.close();
        }

        operation.setPbOrganization(arrayListOrganization);
        //</editor-fold>

        //<editor-fold desc="Address">
        Cursor contactAddressCursor = phoneBookContacts.getContactAddress(rawId);
        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();

        if (contactAddressCursor != null && contactAddressCursor.getCount() > 0) {
            int addressCount = 0;
            while (contactAddressCursor.moveToNext()) {

                ProfileDataOperationAddress address = new ProfileDataOperationAddress();

                address.setAddId(String.valueOf(++addressCount));
                address.setFormattedAddress(contactAddressCursor.getString
                        (contactAddressCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                address.setCity(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .CITY)));
                address.setCountry(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .COUNTRY)));
                address.setNeighborhood(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .NEIGHBORHOOD)));
                address.setPostCode(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POSTCODE)));
                address.setPoBox(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POBOX)));
                address.setStreet(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .STREET)));
                address.setAddressType(phoneBookContacts.getAddressType(contactAddressCursor,
                        contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));

                arrayListAddress.add(address);

            }
            contactAddressCursor.close();
        }

        operation.setPbAddress(arrayListAddress);
        //</editor-fold>

        //<editor-fold desc="IM Account">
        Cursor contactImCursor = phoneBookContacts.getContactIm(rawId);
        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();

        if (contactImCursor != null && contactImCursor.getCount() > 0) {

            int imCount = 0;
            while (contactImCursor.moveToNext()) {

                ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();

                imAccount.setIMId(String.valueOf(++imCount));
                imAccount.setIMAccountDetails(contactImCursor.getString(contactImCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                imAccount.setIMAccountType(phoneBookContacts.getImAccountType(contactImCursor,
                        contactImCursor.getInt(contactImCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Im.TYPE))));

                imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                        (contactImCursor.getInt((contactImCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Im.PROTOCOL)))));

                imAccount.setIMAccountPublic(1);


                arrayListImAccount.add(imAccount);

            }
            contactImCursor.close();
        }

        operation.setPbIMAccounts(arrayListImAccount);
        //</editor-fold>

        //<editor-fold desc="Event">
        Cursor contactEventCursor = phoneBookContacts.getContactEvent(rawId);
        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();

        if (contactEventCursor != null && contactEventCursor.getCount() > 0) {
            int eventCount = 0;
            while (contactEventCursor.moveToNext()) {

                ProfileDataOperationEvent event = new ProfileDataOperationEvent();

                event.setEventId(String.valueOf(++eventCount));
                event.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                        contactEventCursor
                                .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Event.TYPE))));

                String eventDate = contactEventCursor.getString(contactEventCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                .START_DATE));

                if (StringUtils.startsWith(eventDate, "--")) {
                    eventDate = "1900" + eventDate.substring(1, StringUtils.length(eventDate));
                }

                event.setEventDateTime(eventDate);

                event.setEventPublic(1);

                arrayListEvent.add(event);

            }
            contactEventCursor.close();
        }

        operation.setPbEvent(arrayListEvent);
        //</editor-fold>

        arrayListOperation.add(operation);
        profileData.setOperation(arrayListOperation);

        arrayListUserContact.add(profileData);

        uploadContacts();

    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void uploadContacts() {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setProfileData(arrayListUserContact);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CONTACTS);
        }
    }

    private void deleteContact() {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setProfileData(arrayListDeletedUserContact);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CONTACTS);
        }
    }

    //</editor-fold>


}
