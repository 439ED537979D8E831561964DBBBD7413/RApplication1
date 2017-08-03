package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;

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
    @BindView(R.id.image_right)
    ImageView imageRight;
    @BindView(R.id.ripple_action_right)
    RippleView rippleActionRight;
    @BindView(R.id.ll_push_notification)
    LinearLayout llPushNotification;
    @BindView(R.id.ll_event_notification)
    LinearLayout llEventNotification;
    @BindView(R.id.txt_call_pop_up)
    TextView txtCallPopUp;
    @BindView(R.id.sbCallPopUp)
    Switch sbCallPopUp;
    @BindView(R.id.text_call_pop_up)
    TextView textCallPopUp;
    @BindView(R.id.ll_call_pop_up)
    LinearLayout llCallPopUp;

    private Activity activity;
    private String from;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);

        activity = NotificationSettingsActivity.this;

        from = getIntent().getStringExtra("from");

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

        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        txtPushNotification.setTypeface(Utils.typefaceRegular(this));
        textPushNotification.setTypeface(Utils.typefaceRegular(this));
        txtEventNotification.setTypeface(Utils.typefaceRegular(this));
        textEventNotification.setTypeface(Utils.typefaceRegular(this));
        txtCallPopUp.setTypeface(Utils.typefaceRegular(this));
        textCallPopUp.setTypeface(Utils.typefaceRegular(this));

        if (from.equals("notification")) {
            textToolbarTitle.setText(getResources().getString(R.string.text_notifications));
            llPushNotification.setVisibility(View.VISIBLE);
            llEventNotification.setVisibility(View.VISIBLE);
            llCallPopUp.setVisibility(View.GONE);

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

        } else {

            textToolbarTitle.setText(getResources().getString(R.string.str_pop_up));
            llPushNotification.setVisibility(View.GONE);
            llEventNotification.setVisibility(View.GONE);
            llCallPopUp.setVisibility(View.VISIBLE);

            if (Utils.getBooleanPreference(activity, AppConstants.PREF_DISABLE_POPUP, false)) {
                sbCallPopUp.setChecked(false);
            } else {
                sbCallPopUp.setChecked(true);
            }

            sbCallPopUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // If the switch button is on
                        Utils.setBooleanPreference(activity, AppConstants.PREF_DISABLE_POPUP, false);
                    } else {
                        // If the switch button is off
                        Utils.setBooleanPreference(activity, AppConstants.PREF_DISABLE_POPUP, true);
                    }
                }
            });
        }
    }
}
