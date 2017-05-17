package com.rawalinfocom.rcontact.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.RippleView;

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

    private NotiProfileFragment notiProfileFragment;
    private NotiRContactsFragment notiRContactsFragment;
    private NotiRatingFragment notiRatingFragment;
    private NotiCommentsFragment notiCommentsFragment;
    int currentTabIndex;

    public boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            currentTabIndex = intent.getIntExtra("TAB_INDEX", -1);
            init();
        }


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

                if (firstTime && currentTabIndex != 0) {
                    firstTime = false;
                } else {
                    Log.i("MAULIK", "onTabSelected" + tab.getPosition());
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
        notiRatingFragment = NotiRatingFragment.newInstance();
        notiCommentsFragment = NotiCommentsFragment.newInstance();
        notiRContactsFragment = NotiRContactsFragment.newInstance();

        tabNotifications.addTab(tabNotifications.newTab().setText(getResources().getString(R.string.text_tab_profile)));
        tabNotifications.addTab(tabNotifications.newTab().setText(getResources().getString(R.string.text_rating)));
        tabNotifications.addTab(tabNotifications.newTab().setText(getResources().getString(R.string.text_tab_comments)));
        tabNotifications.addTab(tabNotifications.newTab().setText(getResources().getString(R.string.text_tab_rcontact)));
        for (int i = 0; i < tabNotifications.getTabCount(); i++) {
            TabLayout.Tab tab = tabNotifications.getTabAt(i);
            tab.setCustomView(getTabView(i));
        }
        tabNotifications.getTabAt(currentTabIndex).select();
    }

    private String tabTitles[] = new String[]{"Profile", "Rating", "Comment", "RContact"};

    private View getTabView(int position) {
        View v = LayoutInflater.from(NotificationsDetailActivity.this).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.text_toolbar_title);
        tv.setText(tabTitles[position]);
        TextView img = (TextView) v.findViewById(R.id.text_notifications_count);
        img.setText(""+100);
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
}
