package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ContactsFragment;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.receivers.NetworkConnectionReceiver;
import com.rawalinfocom.rcontact.services.ContactSyncService;
import com.rawalinfocom.rcontact.sms.SmsFragment;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

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

//    OnlineDataSync onlineDataSync;

    NetworkConnectionReceiver networkConnectionReceiver;

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

            networkConnectionReceiver = new NetworkConnectionReceiver();

            init();
            registerBroadcastReceiver();

          /*  if (Utils.isNetworkAvailable(this)) {
                onlineDataSync = new OnlineDataSync(this);
            }*/

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
                    String to[] = {"development@rawalinfocom.com"};
//                    emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
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
        } else if (id == R.id.nav_db_export) {
            if (BuildConfig.DEBUG) {
                String exportedFileName = Utils.exportDB(this);
                if (exportedFileName != null) {
                    File filelocation = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath(), exportedFileName);
                    Uri path = Uri.fromFile(filelocation);
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("vnd.android.cursor.dir/email");
                    String to[] = {"development@rawalinfocom.com"};
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    Toast.makeText(getApplicationContext(), "DB dump failed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
        /*}*/
       /* else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

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
                        Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_TABCHANGE);
                        localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_SWITCH_TAB,
                                AppConstants.EXTRA_CALL_LOG_SWITCH_TAB_VALUE);
                        LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
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
        callConfirmationDialog.setDialogBody("If you switch, your current data will be lost. \n Would you like to continue?");
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

    //</editor-fold>


}
