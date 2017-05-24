package com.rawalinfocom.rcontact.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.NotificationsMainAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableNotificationStateMaster;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.model.NotificationItem;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.recycler_notification_main)
    RecyclerView recyclerNotificationMain;
    @BindView(R.id.text_notifications_count)
    TextView textNotificationsCount;

    private NotificationsMainAdapter notificationsMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);
        init();

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }
    }

    public void init() {
        textToolbarTitle.setText(getResources().getString(R.string.text_notifications));
        rippleActionBack.setOnRippleCompleteListener(this);
        imageActionBack.setImageResource(R.drawable.ic_action_back);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        registerLocalBroadCastReceiver();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterLocalBroadCastReceiver();
    }

    private void unRegisterLocalBroadCastReceiver() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                .getInstance(this);
        localBroadcastManager.unregisterReceiver(localBroadCastReceiverUpdateCount);
    }

    private void initData() {

        int timeLineCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_TIMELINE);
        int profileRequestCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_PROFILE_REQUEST);
        int profileResponseCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_PROFILE_RESPONSE);
        int ratingCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_RATE);
        int commentsCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_COMMENTS);
        int rContactsCount = getNotificationCountByType(databaseHandler, AppConstants.NOTIFICATION_TYPE_RUPDATE);

        int profileCount = profileRequestCount + profileResponseCount;

        int totalCount = timeLineCount + profileCount + ratingCount + commentsCount + rContactsCount;
        if (totalCount > 0) {
            textNotificationsCount.setText(String.valueOf(totalCount));
            textNotificationsCount.setVisibility(View.VISIBLE);
        } else {
            textNotificationsCount.setVisibility(View.GONE);
        }
        NotificationItem item1 = new NotificationItem(getResources().getString(R.string.nav_text_timeline), timeLineCount, 1);
        NotificationItem item2 = new NotificationItem(getResources().getString(R.string.text_tab_profile), profileCount, 2);
        NotificationItem item3 = new NotificationItem(getResources().getString(R.string.text_rating), ratingCount, 3);
        NotificationItem item4 = new NotificationItem(getResources().getString(R.string.text_tab_comments), commentsCount, 4);
        NotificationItem item5 = new NotificationItem(getResources().getString(R.string.text_tab_rcontact), rContactsCount, 5);

        List<NotificationItem> listNotificationsMain = Arrays.asList(item1, item2, item3, item4, item5);
        notificationsMainAdapter = new NotificationsMainAdapter(listNotificationsMain, this);
        recyclerNotificationMain.setAdapter(notificationsMainAdapter);
        recyclerNotificationMain.setLayoutManager(new LinearLayoutManager(this));

    }


    private int getNotificationCountByType(DatabaseHandler databaseHandler, int type) {

        TableNotificationStateMaster notificationStateMaster = new TableNotificationStateMaster(databaseHandler);
        return notificationStateMaster.getTotalUnreadCountByType(type);
    }

    private void registerLocalBroadCastReceiver() {
        LocalBroadcastManager localBroadcastManagerUpdateNotificationCount = LocalBroadcastManager
                .getInstance(NotificationsActivity.this);
        IntentFilter intentFilterUpdateCount = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_UPDATE_NOTIFICATION_COUNT);
        localBroadcastManagerUpdateNotificationCount.registerReceiver(localBroadCastReceiverUpdateCount, intentFilterUpdateCount);
    }

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

    private void updateNotificationCount() {
        initData();
    }

}
