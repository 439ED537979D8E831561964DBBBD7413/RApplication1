package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.notifications.NotiCommentsFragment;
import com.rawalinfocom.rcontact.notifications.NotiRContactsFragment;
import com.rawalinfocom.rcontact.notifications.NotiRatingFragment;
import com.rawalinfocom.rcontact.notifications.NotiRequestFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private NotiRequestFragment notiRequestFragment;
    private NotiRContactsFragment notiRContactsFragment;
    private NotiRatingFragment notiRatingFragment;
    private NotiCommentsFragment notiCommentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_detail);
        ButterKnife.bind(this);
        init();
        Intent intent = getIntent();
        if (intent != null) {
            int tabIndex = intent.getIntExtra("TAB_INDEX", 0);
            selectTab(tabIndex);
        }


    }

    private void selectTab(int tabIndex) {
        tabNotifications.getTabAt(tabIndex).select();
    }


    public void init() {
        textToolbarTitle.setText("Notifications");
        rippleActionBack.setOnRippleCompleteListener(this);
        bindWidgetsWithAnEvent();
        setupTabLayout();

    }

    private void bindWidgetsWithAnEvent() {
        tabNotifications.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void setupTabLayout() {
        notiRequestFragment = NotiRequestFragment.newInstance();
        notiRatingFragment = NotiRatingFragment.newInstance();
        notiCommentsFragment = NotiCommentsFragment.newInstance();
        notiRContactsFragment = NotiRContactsFragment.newInstance();

        tabNotifications.addTab(tabNotifications.newTab().setText("Request"), true);
        tabNotifications.addTab(tabNotifications.newTab().setText("Rating"));
        tabNotifications.addTab(tabNotifications.newTab().setText("Comments"));
        tabNotifications.addTab(tabNotifications.newTab().setText("Rcontact"));
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(notiRequestFragment);
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
}
