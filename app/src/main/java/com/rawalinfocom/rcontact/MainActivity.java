package com.rawalinfocom.rcontact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ContactsFragment;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.services.ContactIdFetchService;
import com.rawalinfocom.rcontact.sms.SmsFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView
        .OnNavigationItemSelectedListener, WsResponseListener {

    private final int CONTACT_CHUNK = 5;

    @BindView(R.id.relative_root_contacts_main)
    RelativeLayout relativeRootContactsMain;
    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;

    TabLayout tabMain;

    ContactsFragment contactsFragment;
    CallLogFragment callLogFragment;
    SmsFragment smsFragment;

    ArrayList<ProfileData> arrayListUserContact;

    ArrayList<String> arrayListContactId;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_main);
        ButterKnife.bind(this);

        // TODO uncomment code
       /* if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT, getResources()
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
            UserProfile userProfile = (UserProfile) Utils.getObjectPreference(this, AppConstants
                    .PREF_REGS_USER_OBJECT, UserProfile.class);
            if (userProfile != null && StringUtils.equalsIgnoreCase(userProfile
                    .getIsAlreadyVerified(), String.valueOf(getResources().getInteger(R.integer
                    .profile_not_verified)))) {
                finish();
                startActivityIntent(this, ProfileRegistrationActivity.class, null);
            }
        } else {*/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        HashSet<String> retrievedContactIdSet = (HashSet<String>) Utils.getStringSetPreference
                (this, AppConstants.PREF_CONTACT_ID_SET);
        if (retrievedContactIdSet != null) {
            arrayListContactId = new ArrayList<>(retrievedContactIdSet);
            phoneBookOperations();
        } else {
            LocalBroadcastManager.getInstance(this).registerReceiver(cursorListReceiver,
                    new IntentFilter(AppConstants.ACTION_CONTACT_FETCH));
            Intent contactIdFetchService = new Intent(this, ContactIdFetchService.class);
            startService(contactIdFetchService);
        }


//        }

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_UPLOAD_CONTACTS">

            if (serviceType.contains(WsConstants.REQ_UPLOAD_CONTACTS)) {
                WsResponseObject uploadContactResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (uploadContactResponse != null && StringUtils.equalsIgnoreCase
                        (uploadContactResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    String previouslySyncedData = (StringUtils.split(serviceType, "_"))[1];
                    int nextNumber = Integer.parseInt(StringUtils.defaultString
                            (previouslySyncedData, "0")) + CONTACT_CHUNK;
                    Utils.setIntegerPreference(MainActivity.this, AppConstants
                            .PREF_SYNCED_CONTACTS, nextNumber);

                    if (nextNumber < arrayListContactId.size()) {
                        phoneBookOperations();
                    }

                } else {
                    if (uploadContactResponse != null) {
                        Log.e("error response", uploadContactResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "uploadContactResponse null");
                        Utils.showErrorSnackBar(this, relativeRootContactsMain, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootContactsMain, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cursorListReceiver);
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

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

        tabMain = (TabLayout) findViewById(R.id.tab_main);

        bindWidgetsWithAnEvent();
        setupTabLayout();
    }

    private void setupTabLayout() {
        contactsFragment = ContactsFragment.newInstance();
        callLogFragment = CallLogFragment.newInstance();
        smsFragment = SmsFragment.newInstance();
        tabMain.addTab(tabMain.newTab().setText("CONTACTS"), true);
        tabMain.addTab(tabMain.newTab().setText("CALL LOG"));
        tabMain.addTab(tabMain.newTab().setText("SMS"));
    }

    private void bindWidgetsWithAnEvent() {
        tabMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(contactsFragment);
                break;
            case 1:
                replaceFragment(callLogFragment);
                break;
            case 2:
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

    //</editor-fold>

    //<editor-fold desc="Local Broadcast Receiver">
    private BroadcastReceiver cursorListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(AppConstants.EXTRA_LOCAL_BROADCAST_MESSAGE);
            if (StringUtils.equals(message, WsConstants.RESPONSE_STATUS_TRUE)) {
                HashSet<String> retrievedContactIdSet = (HashSet<String>) Utils
                        .getStringSetPreference(MainActivity.this, AppConstants
                                .PREF_CONTACT_ID_SET);
                if (retrievedContactIdSet != null) {
                    arrayListContactId = new ArrayList<>(retrievedContactIdSet);
                    phoneBookOperations();
                } else {
                    Log.e("Local Broadcast Receiver onReceive: ", "Error while Retriving Ids!");
                }
            } else {
                Log.e("Local Broadcast Receiver onReceive: ", "Error while Retriving Ids!");
            }
        }
    };
    //</editor-fold>

    //<editor-fold desc="Phone book Data Cursor">

    private void phoneBookOperations() {
        arrayListUserContact = new ArrayList<>();
        int previouslySyncedData = Utils.getIntegerPreference(MainActivity.this, AppConstants
                .PREF_SYNCED_CONTACTS, 0);
        int forFrom = previouslySyncedData + CONTACT_CHUNK;
        if (forFrom > arrayListContactId.size()) {
            forFrom = arrayListContactId.size();
        }

        for (int i = previouslySyncedData; i < forFrom; i++) {

            ProfileData profileData = new ProfileData();

            String rawId = arrayListContactId.get(i);

            profileData.setLocalPhoneBookId(rawId);

            /* profileData.setGivenName(contactNameCursor.getString(contactNameCursor
            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));*/

            ProfileDataOperation operation = new ProfileDataOperation();

            //<editor-fold desc="Structured Name">
            Cursor contactStructuredNameCursor = getStructuredName(rawId);
            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

            if (contactStructuredNameCursor != null && contactStructuredNameCursor.getCount()
                    > 0) {

                /*   operation.setIsFavourite(contactNameCursor.getString(contactNameCursor
                .getColumnIndex(ContactsContract.Contacts.STARRED)));*/
                while (contactStructuredNameCursor.moveToNext()) {

                    operation.setFlag("1");
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
            Cursor starredContactCursor = getStarredStatus(rawId);

            if (starredContactCursor != null && starredContactCursor.getCount() > 0) {


                while (starredContactCursor.moveToNext()) {

                    operation.setIsFavourite(starredContactCursor.getString(starredContactCursor
                            .getColumnIndex(ContactsContract.Contacts.STARRED)));

                }
                starredContactCursor.close();
            }
            //</editor-fold>

            //<editor-fold desc="Contact Number">
            Cursor contactNumberCursor = getContactNumbers(rawId);
            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

            if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                int numberCount = 0;
                while (contactNumberCursor.moveToNext()) {

                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();

                    phoneNumber.setPhoneId(++numberCount);
                    phoneNumber.setPhoneNumber(contactNumberCursor.getString(contactNumberCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    phoneNumber.setPhoneType(getPhoneNumberType(contactNumberCursor.getInt
                            (contactNumberCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Phone.TYPE))));
                    phoneNumber.setPhonePublic(1);

                    arrayListPhoneNumber.add(phoneNumber);

                }
                contactNumberCursor.close();
            }
            operation.setPbPhoneNumber(arrayListPhoneNumber);
            //</editor-fold>

            arrayListOperation.add(operation);
            profileData.setOperation(arrayListOperation);

            arrayListUserContact.add(profileData);

        }
//            contactNameCursor.close();

//        }

        uploadContacts(previouslySyncedData);

    }

    private Cursor getAllContactNames() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
//                ContactsContract.Contacts.STARRED,
        };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '1'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

//        return getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        return getContentResolver().query(uri, projection, null, null, sortOrder);
    }

    private Cursor getStarredStatus(String contactId) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts.STARRED,
        };

        String selection = ContactsContract.Contacts._ID + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getStructuredName(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.StructuredName._ID,
                ContactsContract.CommonDataKinds.StructuredName.PREFIX,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME,
        };

        String selection = ContactsContract.Data.MIMETYPE + " = '" +
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID
                + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactNumbers(String contactId) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };

        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    //</editor-fold>

    //<editor-fold desc="Types">
    public String getPhoneNumberType(int type) {
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
    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void uploadContacts(int previouslySyncedData) {

        WsRequestObject uploadContactObject = new WsRequestObject();
        //TODO pmid Modification
        uploadContactObject.setPmId("1");
        uploadContactObject.setProfileData(arrayListUserContact);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS + "_" + previouslySyncedData, getString(R.string
                    .msg_please_wait)).execute(WsConstants.WS_ROOT + WsConstants
                    .REQ_UPLOAD_CONTACTS);
        } else {
            Utils.showErrorSnackBar(this, relativeRootContactsMain, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    //</editor-fold>
}
