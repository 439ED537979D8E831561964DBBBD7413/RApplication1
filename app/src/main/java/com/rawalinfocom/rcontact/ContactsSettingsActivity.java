package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.Dialog;
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
import com.rawalinfocom.rcontact.adapters.ShortByContactListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.AppLanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsSettingsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_short_by)
    TextView textShortBy;
    @BindView(R.id.text_export_contact)
    TextView textExportContact;
    @BindView(R.id.ll_export)
    LinearLayout llExport;
    @BindView(R.id.ll_short_by)
    LinearLayout llShortBy;
    @BindView(R.id.activity_contact_settings)
    RelativeLayout activityContactSettings;
    @BindView(R.id.txt_short_by)
    TextView txtShortBy;
    @BindView(R.id.txt_export_contact)
    TextView txtExportContact;
    private Activity activity;

    private ArrayList<AppLanguage> shortByContactArrayList;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_settings);
        ButterKnife.bind(this);

        activity = ContactsSettingsActivity.this;

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

        textToolbarTitle.setText(getResources().getString(R.string.contacts));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        textShortBy.setTypeface(Utils.typefaceRegular(this));
        textExportContact.setTypeface(Utils.typefaceRegular(this));
        txtShortBy.setTypeface(Utils.typefaceRegular(this));
        txtExportContact.setTypeface(Utils.typefaceRegular(this));

        if (Utils.getStringPreference(activity, AppConstants.PREF_SHORT_BY_CONTACT, "0").equalsIgnoreCase("0")) {
            textShortBy.setText(getString(R.string.abc_by_first_name));
        } else {
            textShortBy.setText(getString(R.string.abc_by_last_name));
        }

        if (Utils.getStringPreference(activity, AppConstants.PREF_EXPORT_CONTACT, "0").equalsIgnoreCase("0")) {
            textExportContact.setText(getString(R.string.phone_storage));
        } else if (Utils.getStringPreference(activity, AppConstants.PREF_EXPORT_CONTACT, "0").equalsIgnoreCase("1")) {
            textExportContact.setText(R.string.sd_card);
        } else {
            textExportContact.setText(R.string.google_drive);
        }

        llExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportContactMaterialDialog();
            }
        });

        llShortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShortContactMaterialDialog();
            }
        });
    }

    private void setArrayList(String type) {

        if (type.equals("short")) {
            shortByContactArrayList = new ArrayList<>();

            if (Utils.getStringPreference(activity, AppConstants.PREF_SHORT_BY_CONTACT, "0").equalsIgnoreCase("0")) {
                shortByContactArrayList.add(new AppLanguage(getString(R.string.abc_by_first_name), "0", true));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.abc_by_last_name), "1", false));
            } else {
                shortByContactArrayList.add(new AppLanguage(getString(R.string.abc_by_first_name), "0", false));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.abc_by_last_name), "1", true));
            }
        } else {

            shortByContactArrayList = new ArrayList<>();

            if (Utils.getStringPreference(activity, AppConstants.PREF_EXPORT_CONTACT, "0").equalsIgnoreCase("0")) {
                shortByContactArrayList.add(new AppLanguage(getString(R.string.phone_storage), "0", true));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.sd_card), "1", false));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.google_drive), "2", false));
            } else if (Utils.getStringPreference(activity, AppConstants.PREF_EXPORT_CONTACT, "0").equalsIgnoreCase("1")) {
                shortByContactArrayList.add(new AppLanguage(getString(R.string.phone_storage), "0", false));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.sd_card), "1", true));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.google_drive), "2", false));
            } else {
                shortByContactArrayList.add(new AppLanguage(getString(R.string.phone_storage), "0", false));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.sd_card), "1", false));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.google_drive), "2", true));
            }
        }
    }

    public void ShortContactMaterialDialog() {

        setArrayList("short");

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_language);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText(getString(R.string.short_by));
        tvDialogTitle.setTypeface(Utils.typefaceBold(activity));

        Button btnRight = (Button) dialog.findViewById(R.id.btnRight);
        btnRight.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        btnRight.setTypeface(Utils.typefaceRegular(activity));
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.rippleRight);

        RecyclerView recyclerViewAppLanguage = (RecyclerView) dialog.findViewById(R.id.recyclerViewAppLanguage);
        recyclerViewAppLanguage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        final ShortByContactListAdapter shortByContactListAdapter = new ShortByContactListAdapter(shortByContactArrayList);
        recyclerViewAppLanguage.setAdapter(shortByContactListAdapter);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (shortByContactListAdapter.getSelected().equals("")) {
                    Utils.showErrorSnackBar(activity, activityContactSettings, "Please select any short type");
                } else {

                    AppConstants.isFromSettingActivity = true;
                    textShortBy.setText(shortByContactListAdapter.getSelected());
                    Utils.setStringPreference(activity, AppConstants.PREF_SHORT_BY_CONTACT, shortByContactListAdapter.getSelectedType());
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    public void ExportContactMaterialDialog() {

        setArrayList("export");

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_language);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText(getString(R.string.short_by));
        tvDialogTitle.setTypeface(Utils.typefaceBold(activity));

        Button btnRight = (Button) dialog.findViewById(R.id.btnRight);
        btnRight.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        btnRight.setTypeface(Utils.typefaceRegular(activity));
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.rippleRight);

        RecyclerView recyclerViewAppLanguage = (RecyclerView) dialog.findViewById(R.id.recyclerViewAppLanguage);
        recyclerViewAppLanguage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        final ShortByContactListAdapter shortByContactListAdapter = new ShortByContactListAdapter(shortByContactArrayList);
        recyclerViewAppLanguage.setAdapter(shortByContactListAdapter);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (shortByContactListAdapter.getSelected().equals("")) {
                    Utils.showErrorSnackBar(activity, activityContactSettings, "Please select any export type");
                } else {

                    textExportContact.setText(shortByContactListAdapter.getSelected());
                    Utils.setStringPreference(activity, AppConstants.PREF_EXPORT_CONTACT, shortByContactListAdapter.getSelectedType());
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}
