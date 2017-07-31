package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.rawalinfocom.rcontact.adapters.ShortByContactListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.AppLanguage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationSettingsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.txt_push_notification)
    TextView txtPushNotification;
    @BindView(R.id.sbPushNotification)
    Switch sbPushNotification;
    @BindView(R.id.text_push_notification)
    TextView textPushNotification;
    @BindView(R.id.txt_event_notification)
    TextView txtEventNotification;
    @BindView(R.id.sbEventNotification)
    Switch sbEventNotification;
    @BindView(R.id.text_event_notification)
    TextView textEventNotification;
    @BindView(R.id.activity_contact_settings)
    RelativeLayout activityContactSettings;
    private Activity activity;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);

        activity = NotificationSettingsActivity.this;

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

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
    }

    //</editor-fold>

    private void init() {

        rippleActionBack.setOnRippleCompleteListener(this);

        textToolbarTitle.setText(getResources().getString(R.string.text_notifications));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        txtPushNotification.setTypeface(Utils.typefaceRegular(this));
        textPushNotification.setTypeface(Utils.typefaceRegular(this));
        txtEventNotification.setTypeface(Utils.typefaceRegular(this));
        textEventNotification.setTypeface(Utils.typefaceRegular(this));

        if (Utils.getBooleanPreference(activity, AppConstants.PREF_DISABLE_PUSH, false)) {
            sbPushNotification.setChecked(false);
        } else {
            sbPushNotification.setChecked(true);
        }

        if (Utils.getBooleanPreference(activity, AppConstants.PREF_DISABLE_EVENT_PUSH, false)) {
            sbEventNotification.setChecked(false);
        } else {
            sbEventNotification.setChecked(true);
        }

        sbPushNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If the switch button is on
                    Utils.setBooleanPreference(activity, AppConstants.PREF_DISABLE_PUSH, false);
                } else {
                    // If the switch button is off
                    Utils.setBooleanPreference(activity, AppConstants.PREF_DISABLE_PUSH, true);
                }
            }
        });

        sbEventNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If the switch button is on
                    Utils.setBooleanPreference(activity, AppConstants.PREF_DISABLE_EVENT_PUSH, false);
                } else {
                    // If the switch button is off
                    Utils.setBooleanPreference(activity, AppConstants.PREF_DISABLE_EVENT_PUSH, true);
                }
            }
        });
    }
}
