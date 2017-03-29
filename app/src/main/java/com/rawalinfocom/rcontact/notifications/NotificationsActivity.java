package com.rawalinfocom.rcontact.notifications;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.notifications.NotificationItem;
import com.rawalinfocom.rcontact.notifications.NotificationsMainAdapter;

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

    private NotificationsMainAdapter notificationsMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);
        init();
        initData();

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
        textToolbarTitle.setText("Notifications");
        rippleActionBack.setOnRippleCompleteListener(this);
        imageActionBack.setImageResource(R.drawable.ic_action_back);
    }

    private void initData() {
        NotificationItem item1 = new NotificationItem("Timeline", 100, new String[]{"Paras Savaliya and 6 other friends Birthday wish  on your Birthday",
                "Paras Savaliya and 6 other friends Birthday wish  on your Birthday",}, 1);
        NotificationItem item2 = new NotificationItem("Requests", 100, new String[]{"Paras Savaliya and 6 other friends Birthday wish  on your Birthday",
                "Paras Savaliya and 6 other friends Birthday wish  on your Birthday",}, 2);
        NotificationItem item3 = new NotificationItem("Rating", 100, new String[]{"Paras Savaliya and 6 other friends Birthday wish  on your Birthday",
                "Paras Savaliya and 6 other friends Birthday wish  on your Birthday",}, 3);
        NotificationItem item4 = new NotificationItem("Comments", 100, new String[]{"Paras Savaliya and 6 other friends Birthday wish  on your Birthday",
                "Paras Savaliya and 6 other friends Birthday wish  on your Birthday",}, 4);
        NotificationItem item5 = new NotificationItem("RContacts", 100, new String[]{"Paras Savaliya and 6 other friends Birthday wish  on your Birthday",
                "Paras Savaliya and 6 other friends Birthday wish  on your Birthday",}, 5);

        List<NotificationItem> listNotificationsMain = Arrays.asList(item1, item2, item3, item4, item5);
        notificationsMainAdapter = new NotificationsMainAdapter(listNotificationsMain, this);
        recyclerNotificationMain.setAdapter(notificationsMainAdapter);
        recyclerNotificationMain.setLayoutManager(new LinearLayoutManager(this));

    }

}
