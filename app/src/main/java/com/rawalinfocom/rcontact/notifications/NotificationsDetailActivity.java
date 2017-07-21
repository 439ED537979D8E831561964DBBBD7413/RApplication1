package com.rawalinfocom.rcontact.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableNotificationStateMaster;
import com.rawalinfocom.rcontact.helper.RippleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.leolin.shortcutbadger.ShortcutBadger;

public class NotificationsDetailActivity extends BaseActivity implements RippleView.OnRippleCompleteListener {

    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.tab_notifications)
    TabLayout tabNotifications;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;

    public static final int TAB_REQUEST = 0;
    public static final int TAB_RATING = 1;
    public static final int TAB_COMMENTS = 2;
    public static final int TAB_RCONTACTS = 3;

    private NotiProfileFragment notiProfileFragment;
    private NotiRContactsFragment notiRContactsFragment;
    private NotiRatingFragment notiRatingFragment;
    private NotiCommentsFragment notiCommentsFragment;
    int currentTabIndex;
    int subTabIndex;

    public boolean firstTime = true;
    int profileCount;
    int ratingCount;
    int commentsCount;
    int rContactsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            currentTabIndex = intent.getIntExtra("TAB_INDEX", -1);
            subTabIndex = intent.getIntExtra("SUB_TAB_INDEX", 0);
            init();
        }
    }

    public void init() {
        textToolbarTitle.setText(getString(R.string.text_notifications));
        int profileRequestCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_PROFILE_REQUEST);
        int profileResponseCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_PROFILE_RESPONSE);
        profileCount = profileRequestCount + profileResponseCount;
        ratingCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_RATE);
        commentsCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_COMMENTS);
        rContactsCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_RUPDATE);
        rippleActionBack.setOnRippleCompleteListener(this);
        bindWidgetsWithAnEvent();
        setupTabLayout();

    }

    private void bindWidgetsWithAnEvent() {
        tabNotifications.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (firstTime && currentTabIndex != 0) {
                    firstTime = false;
                } else {
                    setCurrentTabFragment(tab.getPosition());
                    firstTime = false;
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

    private void setupTabLayout() {
        notiProfileFragment = NotiProfileFragment.newInstance();
        Bundle args = new Bundle();
        args.putInt("SUB_TAB_INDEX", subTabIndex);
        notiProfileFragment.setArguments(args);
        notiRatingFragment = NotiRatingFragment.newInstance();
        notiCommentsFragment = NotiCommentsFragment.newInstance();
        notiRContactsFragment = NotiRContactsFragment.newInstance();

        tabNotifications.addTab(tabNotifications.newTab().setText(getResources().getString(R.string.text_tab_profile)));
        tabNotifications.addTab(tabNotifications.newTab().setText(getResources().getString(R.string.str_tab_rating)));
        tabNotifications.addTab(tabNotifications.newTab().setText(getResources().getString(R.string.text_tab_comments)));
        tabNotifications.addTab(tabNotifications.newTab().setText(getResources().getString(R.string.text_tab_rcontact)));
        for (int i = 0; i < tabNotifications.getTabCount(); i++) {
            TabLayout.Tab tab = tabNotifications.getTabAt(i);
            tab.setCustomView(getTabView(i));
        }
        tabNotifications.getTabAt(currentTabIndex).select();
    }

    private View getTabView(int position) {
        View v = LayoutInflater.from(NotificationsDetailActivity.this).inflate(R.layout.custom_tab, null);
        TextView titleText = (TextView) v.findViewById(R.id.text_toolbar_title);
        TextView countText = (TextView) v.findViewById(R.id.text_notifications_count);
        String title = "";
        int count = 0;
        switch (position) {
            case 0:
                title = getString(R.string.str_tab_profile);
                count = profileCount;
                break;
            case 1:
                title = getString(R.string.str_tab_rating);
                count = ratingCount;
                break;
            case 2:
                title = getString(R.string.str_tab_comment);
                count = commentsCount;
                break;
            case 3:
                title = getString(R.string.str_tab_r_contact);
                count = rContactsCount;
                break;
        }

        titleText.setText(title);
        if (count > 0) {
            countText.setText(String.valueOf(count));
        } else {
            countText.setVisibility(View.GONE);
        }

        return v;
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(notiProfileFragment);
                break;
            case 1:
                replaceFragment(notiRatingFragment);
                break;
            case 2:
                replaceFragment(notiCommentsFragment);
                break;
            case 3:
                replaceFragment(notiRContactsFragment);
                break;

        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container_notification_tab, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }
    }

    private int getNotificationCountByType(DatabaseHandler databaseHandler, int type) {

        TableNotificationStateMaster notificationStateMaster = new TableNotificationStateMaster(databaseHandler);
        return notificationStateMaster.getTotalUnreadCountByType(type);
    }

    public void updateNotificationCount(int type) {
        TableNotificationStateMaster tableNotificationStateMaster = new TableNotificationStateMaster(databaseHandler);
        tableNotificationStateMaster.makeAllNotificationsAsReadByType(type);
        int profileRequestCount;
        int profileResponseCount;


        switch (type) {
            case AppConstants.NOTIFICATION_TYPE_PROFILE_REQUEST:
                profileRequestCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_PROFILE_REQUEST);
                profileResponseCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_PROFILE_RESPONSE);
                profileCount = profileRequestCount + profileResponseCount;
                TabLayout.Tab tab = tabNotifications.getTabAt(0);
                View view = tab.getCustomView();
                TextView countText = (TextView)
                        view.findViewById(R.id.text_notifications_count);
                if (profileCount > 0) {
                    countText.setText(String.valueOf(profileCount));
                } else {
                    countText.setVisibility(View.GONE);
                }
                break;
            case AppConstants.NOTIFICATION_TYPE_PROFILE_RESPONSE:
                profileRequestCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_PROFILE_REQUEST);
                profileResponseCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_PROFILE_RESPONSE);
                profileCount = profileRequestCount + profileResponseCount;
                tab = tabNotifications.getTabAt(0);
                view = tab.getCustomView();
                countText = (TextView)
                        view.findViewById(R.id.text_notifications_count);
                if (profileCount > 0) {
                    countText.setText(String.valueOf(profileCount));
                } else {
                    countText.setVisibility(View.GONE);
                }
                break;
            case AppConstants.NOTIFICATION_TYPE_RATE:
                ratingCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_RATE);
                tab = tabNotifications.getTabAt(1);
                view = tab.getCustomView();
                countText = (TextView)
                        view.findViewById(R.id.text_notifications_count);
                if (ratingCount > 0) {
                    countText.setText(String.valueOf(ratingCount));
                } else {
                    countText.setVisibility(View.GONE);
                }
                break;
            case AppConstants.NOTIFICATION_TYPE_COMMENTS:
                commentsCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_COMMENTS);
                tab = tabNotifications.getTabAt(2);
                view = tab.getCustomView();
                countText = (TextView)
                        view.findViewById(R.id.text_notifications_count);
                if (commentsCount > 0) {
                    countText.setText(String.valueOf(commentsCount));
                } else {
                    countText.setVisibility(View.GONE);
                }
                break;
            case AppConstants.NOTIFICATION_TYPE_RUPDATE:
                rContactsCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_RUPDATE);
                tab = tabNotifications.getTabAt(3);
                view = tab.getCustomView();
                countText = (TextView)
                        view.findViewById(R.id.text_notifications_count);
                if (rContactsCount > 0) {
                    countText.setText(String.valueOf(rContactsCount));
                } else {
                    countText.setVisibility(View.GONE);
                }
                break;
        }
        int badgeCount = tableNotificationStateMaster.getTotalUnreadCount();
        ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
    }
}
