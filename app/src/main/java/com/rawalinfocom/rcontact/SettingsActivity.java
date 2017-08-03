package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.adapters.AppLanguageListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.AppLanguage;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_right)
    ImageView imageRight;
    @BindView(R.id.ripple_action_right)
    RippleView rippleActionRight;
    @BindView(R.id.text_pop_up)
    TextView textPopUp;
    @BindView(R.id.ll_pop_up)
    LinearLayout llPopUp;
    private Activity activity;

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_app_language)
    TextView textAppLanguage;
    @BindView(R.id.text_contacts)
    TextView textContacts;
    @BindView(R.id.text_notification)
    TextView textNotification;
    @BindView(R.id.activity_settings)
    RelativeLayout activitySettings;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.ll_contacts)
    LinearLayout llContacts;
    @BindView(R.id.ll_notification)
    LinearLayout llNotification;
    @BindView(R.id.ll_app_language)
    LinearLayout llAppLanguage;

    private ArrayList<AppLanguage> languageArrayList;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        activity = SettingsActivity.this;

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

        textToolbarTitle.setText(getResources().getString(R.string.action_settings));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        textAppLanguage.setTypeface(Utils.typefaceRegular(this));
        textContacts.setTypeface(Utils.typefaceRegular(this));
        textNotification.setTypeface(Utils.typefaceRegular(this));
        textPopUp.setTypeface(Utils.typefaceRegular(this));

        llAppLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLanguageMaterialDialog();
            }
        });

        llContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIntent(activity, ContactsSettingsActivity.class, null);
            }
        });

        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NotificationSettingsActivity.class);
                intent.putExtra("from", "notification");
                startActivity(intent);
            }
        });

        llPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NotificationSettingsActivity.class);
                intent.putExtra("from", "popup");
                startActivity(intent);
            }
        });
    }

    private void setLanguage() {

        languageArrayList = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                .array.types_language)));

//        if (Utils.getStringPreference(activity, AppConstants.PREF_APP_LANGUAGE, "0").equalsIgnoreCase("0"))
//            languageArrayList.add(new AppLanguage(Locale.getDefault().getDisplayLanguage(), "0", true));
//        else
//            languageArrayList.add(new AppLanguage(Locale.getDefault().getDisplayLanguage(), "0", false));

        for (int i = 0; i < temp.size(); i++) {
            if (Utils.getStringPreference(activity, AppConstants.PREF_APP_LANGUAGE, "0").equalsIgnoreCase(String.valueOf(i))) {
                languageArrayList.add(new AppLanguage(temp.get(i), String.valueOf(i), true));
            } else {
                languageArrayList.add(new AppLanguage(temp.get(i), String.valueOf(i), false));
            }
        }
    }

    public void AppLanguageMaterialDialog() {

        setLanguage();

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_language);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText(getString(R.string.app_language));
        tvDialogTitle.setTypeface(Utils.typefaceBold(activity));

        Button btnRight = (Button) dialog.findViewById(R.id.btnRight);
        btnRight.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        btnRight.setTypeface(Utils.typefaceRegular(activity));
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.rippleRight);

        RecyclerView recyclerViewAppLanguage = (RecyclerView) dialog.findViewById(R.id.recyclerViewAppLanguage);
        recyclerViewAppLanguage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        final AppLanguageListAdapter appLanguageListAdapter = new AppLanguageListAdapter(languageArrayList);
        recyclerViewAppLanguage.setAdapter(appLanguageListAdapter);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (appLanguageListAdapter.getSelectedLanguage().equals("")) {
                    Utils.showErrorSnackBar(activity, activitySettings, "Please select any language");
                } else {

                    Utils.setStringPreference(activity, AppConstants.PREF_APP_LANGUAGE, appLanguageListAdapter.getSelectedLanguageType());
                    dialog.dismiss();

                    RContactApplication.getInstance().setLanguage();

                    // Redirect to MainActivity
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra(AppConstants.PREF_IS_FROM, AppConstants.PREF_RE_LOGIN);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            }
        });

        dialog.show();
    }
}
