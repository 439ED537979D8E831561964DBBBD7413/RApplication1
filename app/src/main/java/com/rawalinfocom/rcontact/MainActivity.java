package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ContactsFragment;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
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
import com.rawalinfocom.rcontact.services.ContactSyncService;
import com.rawalinfocom.rcontact.sms.SmsFragment;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_main);
        ButterKnife.bind(this);
        Intent contactIdFetchService = new Intent(this, ContactSyncService.class);
        startService(contactIdFetchService);

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


    @Override
    protected void onPause() {
        super.onPause();

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
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
//                String shareBody = "Here is the share content body";
            String shareBody = AppConstants.PLAY_STORE_LINK + appPackageName;
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Contact Via");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
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

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkConnectionReceiver != null) {
            unregisterBroadcastReceiver();
        }
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
                Snackbar.make(view, "Dial Pad", Snackbar.LENGTH_SHORT).show();
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
                        profileDataOperation.setFlag(String.valueOf(getResources().getInteger(R
                                .integer
                                .sync_delete)));
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
        operation.setFlag(flag);

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

                imAccount.setIMAccountPublic("1");


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
