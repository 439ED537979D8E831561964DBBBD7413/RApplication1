package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    private Cursor cursor;
    private ArrayList<String> vCard;
    private String vfile;

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

        if (type.equals(getString(R.string.str_short))) {
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

            shortByContactArrayList.add(new AppLanguage(getString(R.string.phone_storage), "0", true));
            shortByContactArrayList.add(new AppLanguage(getString(R.string.sd_card), "1", false));
            shortByContactArrayList.add(new AppLanguage(getString(R.string.google_drive), "2", false));
        }
    }

    public void ShortContactMaterialDialog() {

        setArrayList(getString(R.string.str_short));

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
                    Utils.showErrorSnackBar(activity, activityContactSettings, getString(R.string.str_validation_short_type));
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

        setArrayList(getString(R.string.str_export));

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
                    Utils.showErrorSnackBar(activity, activityContactSettings, getString(R.string.str_validation_export_type));
                } else {

                    textExportContact.setText(shortByContactListAdapter.getSelected());
//                    Utils.setStringPreference(activity, AppConstants.PREF_EXPORT_CONTACT, shortByContactListAdapter.getSelectedType());
                    dialog.dismiss();

                    if (shortByContactListAdapter.getSelectedType().equals("0") || shortByContactListAdapter.getSelectedType().equals("1"))
                        new ExportContact().execute();
                }
            }
        });

        dialog.show();
    }

    private class ExportContact extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.showProgressDialog(activity, "Please wait...", false);
            vfile = "Contacts" + "_" + System.currentTimeMillis() + ".vcf";
        }

        protected Void doInBackground(Void... urls) {
            getVcardString();
            return null;
        }

        protected void onPostExecute(Void result) {
            Utils.hideProgressDialog();
        }
    }

    private void getVcardString() {
        // TODO Auto-generated method stub
        vCard = new ArrayList<String>();
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                get(cursor);
                cursor.moveToNext();
            }

        } else {
            Log.d("TAG", "No Contacts in Your Phone");
        }

    }

    public void get(Cursor cursor) {


        //cursor.moveToFirst();
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        AssetFileDescriptor fd;
        try {
            fd = this.getContentResolver().openAssetFileDescriptor(uri, "r");

            // Your Complex Code and you used function without loop so how can you get all Contacts Vcard.??


           /* FileInputStream fis = fd.createInputStream();
            byte[] buf = new byte[(int) fd.getDeclaredLength()];
            fis.read(buf);
            String VCard = new String(buf);
            String path = Environment.getExternalStorageDirectory().toString() + File.separator + vfile;
            FileOutputStream out = new FileOutputStream(path);
            out.write(VCard.toString().getBytes());
            Log.d("Vcard",  VCard);*/

            FileInputStream fis = fd.createInputStream();
            byte[] buf = new byte[(int) fd.getDeclaredLength()];
            fis.read(buf);
            String vcardstring = new String(buf);
            vCard.add(vcardstring);

            String storage_path = Environment.getExternalStorageDirectory().toString() + File.separator + vfile;
            FileOutputStream mFileOutputStream = new FileOutputStream(storage_path, false);
            mFileOutputStream.write(vcardstring.toString().getBytes());

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
