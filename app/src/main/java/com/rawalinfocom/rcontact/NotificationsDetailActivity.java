package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        setupTabLayout();
        textToolbarTitle.setText("Notifications");
        rippleActionBack.setOnRippleCompleteListener(this);
    }

    private void setupTabLayout() {
        tabNotifications.addTab(tabNotifications.newTab().setText("Request"), true);
        tabNotifications.addTab(tabNotifications.newTab().setText("Rating"));
        tabNotifications.addTab(tabNotifications.newTab().setText("Comments"));
        tabNotifications.addTab(tabNotifications.newTab().setText("Rcontact"));
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
