package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ContactsFragment;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.services.ContactSyncService;
import com.rawalinfocom.rcontact.sms.SmsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView
        .OnNavigationItemSelectedListener {

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

            init();

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

    //<editor-fold desc="Private Methods">

    private void init() {

        textImageNotification = (TextView) toolbar.findViewById(R.id.text_image_notification);
        textImageNotification.setTypeface(Utils.typefaceIcons(this));
//        textImageNotification.setText(Html.fromHtml("&#xe966;"));
//        textImageNotification.setText(Html.fromHtml("&#xe900;"));
        textImageNotification.setText(Html.fromHtml(getResources().getString(R.string.im_bell)));

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
        Utils.changeTabsFont(this, tabMain);



    }


    private void showAddToContact(boolean value){
        ImageView imageViewAddContact = (ImageView) findViewById(R.id.image_add_contact);
        if(value){
            imageViewAddContact.setVisibility(View.GONE);
            imageViewAddContact.setVisibility(View.VISIBLE);
            imageViewAddContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.addToContact(MainActivity.this, "");
                }
            });
        }else {
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

    //</editor-fold>

}
