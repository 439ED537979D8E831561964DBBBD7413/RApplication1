package com.rawalinfocom.rcontact.contacts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.OptionMenuAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.helper.RecyclerItemClickListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Monal on 24/12/16.
 */

class OptionMenuDialog {

    public static final int ALL_CONTACT_MY_PROFILE = 0;
    static final int ALL_CONTACT_NON_RCP = 1;
    static final int ALL_CONTACT_RCP = 2;
    static final int R_CONTACT_RCP = 3;
    static boolean IS_CONTACT_DELETED = false;

    private boolean isFavourite;

    private Context context;
    private Dialog dialog;

    private String dialogTag;
    private String rawId;

    RContactApplication rContactApplication;

    //<editor-fold desc="Constructor">
    OptionMenuDialog(final Context context, String rawId, final int menuType, boolean isFavourite) {
        this.context = context;
        this.rawId = rawId;
        this.isFavourite = isFavourite;

        rContactApplication = (RContactApplication) context.getApplicationContext();

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option_menu);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.60);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setGravity(Gravity.TOP | Gravity.END);

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        RecyclerView recyclerViewOptionMenu = (RecyclerView) dialog.findViewById(R.id
                .recycler_view_option_menu);

        String[] menus = new String[0];
        switch (menuType) {
            case ALL_CONTACT_NON_RCP:
                menus = new String[]{"Edit", "Delete"};
                break;

            case ALL_CONTACT_RCP:
                menus = new String[]{"Edit", "View in Phonebook", "Rate Profile", "Delete"};
                break;

            case R_CONTACT_RCP:
                menus = new String[]{"Edit", "View in AC", "Rate Profile", "Delete"};
                break;
        }

        recyclerViewOptionMenu.setLayoutManager(new LinearLayoutManager(context));
        OptionMenuAdapter optionMenuAdapter = new OptionMenuAdapter(context, new ArrayList<>
                (Arrays.asList(menus)));
        recyclerViewOptionMenu.setAdapter(optionMenuAdapter);

        recyclerViewOptionMenu.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener
                        .OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        dismissDialog();
                        switch (menuType) {
                            case ALL_CONTACT_NON_RCP:
                                menuAllContactNonRcp(view, position);
                                break;

                            case ALL_CONTACT_RCP:
                                menuAllContactRcp(view, position);
                                break;

                            case R_CONTACT_RCP:
                                menuRContactRcp(view, position);
                                break;
                        }
                    }
                })
        );

    }
    //</editor-fold>

    //<editor-fold desc="Public Methods">
    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public boolean isDialogShowing() {
        return dialog.isShowing();
    }

    public String getDialogTag() {
        return dialogTag;
    }

    public void setDialogTag(String dialogTag) {
        this.dialogTag = dialogTag;
    }
    //</editor-fold>

    //<editor-fold desc="Menu Item Click listener">

    private void menuAllContactNonRcp(View view, int position) {

        Intent intent;
        Uri lookupUri, res;

        switch (position) {

            //<editor-fold desc="Edit">
            case 0:
                intent = new Intent(Intent.ACTION_EDIT);
                lookupUri = Uri.withAppendedPath(ContactsContract.Contacts
                        .CONTENT_LOOKUP_URI, rawId);
                res = ContactsContract.Contacts.lookupContact(context.getContentResolver
                        (), lookupUri);
                intent.setData(res);
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).isContactEdited = true;
                }
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            //</editor-fold>

            // <editor-fold desc="Delete">
            case 1:
                PhoneBookContacts phoneBookContacts = new PhoneBookContacts(context);
                phoneBookContacts.deleteContact(rawId);
                IS_CONTACT_DELETED = true;
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onBackPressed();
                }
                if (isFavourite) {
                    rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_REMOVED);
                }
                break;
            //</editor-fold>
        }
    }

    private void menuAllContactRcp(View view, int position) {

        PhoneBookContacts phoneBookContacts = new PhoneBookContacts(context);
        QueryManager queryManager = new QueryManager(((BaseActivity) context).databaseHandler);
        Intent intent;
        Uri lookupUri, res;

        switch (position) {

            //<editor-fold desc="Edit">
            case 0:
                intent = new Intent(Intent.ACTION_EDIT);
                lookupUri = Uri.withAppendedPath(ContactsContract.Contacts
                        .CONTENT_LOOKUP_URI, rawId);
                res = ContactsContract.Contacts.lookupContact(context.getContentResolver
                        (), lookupUri);
                intent.setData(res);
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).isContactEdited = true;
                }
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            //</editor-fold>

            // <editor-fold desc="View in Phonebook">
            case 1:
                // In Case of invalid lookup key
                try {
                    intent = new Intent(Intent.ACTION_VIEW);
                    lookupUri = Uri.withAppendedPath(ContactsContract.Contacts
                            .CONTENT_LOOKUP_URI, rawId);
                    res = ContactsContract.Contacts.lookupContact(context.getContentResolver
                            (), lookupUri);
                    intent.setData(res);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error while retrieving contact raw id", Toast
                            .LENGTH_SHORT).show();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="Rate Profile">
            case 2:
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onRatingClick();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="Delete">
            case 3:
                TableProfileMaster tableProfileMaster = new TableProfileMaster(((BaseActivity)
                        context).databaseHandler);
                String rawIdFromRcpId = tableProfileMaster.getRawIdFromRcpId(Integer.parseInt(
                        ((ProfileDetailActivity) context).pmId));
                if (StringUtils.contains(rawIdFromRcpId, ",")) {
                    /* Multiple Contacts with single pm_id */
                    String rawIds[] = rawIdFromRcpId.split(",");
                    ArrayList<String> arrayListRawIds = new ArrayList<>(Arrays.asList(rawIds));
                    arrayListRawIds.remove(rawId);
                    tableProfileMaster.updateRawIds(Integer.parseInt(((ProfileDetailActivity)
                            context).pmId), StringUtils.join(arrayListRawIds, ","));
                } else {
                    if ((((ProfileDetailActivity) context).pmId).equalsIgnoreCase(((BaseActivity)
                            context).getUserPmId())) {
                        /* Single Contact with self registered pm_id */
                        tableProfileMaster.updateRawIds(Integer.parseInt(((ProfileDetailActivity)
                                context).pmId), "");
                    } else {
                        /* Single Contact with single pm_id */
                        queryManager.deleteRcProfileDetail(((ProfileDetailActivity) context).pmId);
                    }
                }
                phoneBookContacts.deleteContact(rawId);
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onBackPressed();
                }
                IS_CONTACT_DELETED = true;
                if (isFavourite) {
                    rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_REMOVED);
                }
                break;
            //</editor-fold>
        }
    }

    private void menuRContactRcp(View view, int position) {

        PhoneBookContacts phoneBookContacts = new PhoneBookContacts(context);
        QueryManager queryManager = new QueryManager(((BaseActivity) context).databaseHandler);
        Intent intent;
        Uri lookupUri, res;
        String rcpRawId = ((ProfileDetailActivity) context).checkNumberFavourite;
        if (StringUtils.contains(rcpRawId, ",")) {
            rcpRawId = rcpRawId.split(",")[0];
        }

        switch (position) {

            //<editor-fold desc="Edit">
            case 0:
                intent = new Intent(Intent.ACTION_EDIT);
                lookupUri = Uri.withAppendedPath(ContactsContract.Contacts
                        .CONTENT_LOOKUP_URI, rawId);
                res = ContactsContract.Contacts.lookupContact(context.getContentResolver(),
                        lookupUri);
                intent.setData(res);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            //</editor-fold>

            // <editor-fold desc="View in AC">
            case 1:
                String phonebookName = phoneBookContacts.getStructuredName(rcpRawId);
                String cloudName = ((ProfileDetailActivity) context).contactName;
                Bundle bundle = new Bundle();
                if (StringUtils.equals(phonebookName, cloudName)) {
                    bundle.putString(AppConstants.EXTRA_CONTACT_NAME, ((ProfileDetailActivity)
                            context).contactName);
                } else {
                    bundle.putString(AppConstants.EXTRA_CONTACT_NAME, phonebookName);
                    bundle.putString(AppConstants.EXTRA_CLOUD_CONTACT_NAME, " (" + (
                            (ProfileDetailActivity) context).contactName + ")");
                }
                bundle.putString(AppConstants.EXTRA_PM_ID, ((ProfileDetailActivity) context).pmId);
                bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, rcpRawId);
                bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, ((ProfileDetailActivity)
                        context).thumbnailUrl);
                bundle.putString(AppConstants.EXTRA_CONTACT_POSITION, ((ProfileDetailActivity)
                        context).listClickedPosition + "");
                ((BaseActivity) context).startActivityIntent(context,
                        ProfileDetailActivity.class, bundle);
                break;
            //</editor-fold>

            // <editor-fold desc="Rate Profile">
            case 2:
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onRatingClick();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="Delete">
            case 3:
                TableProfileMaster tableProfileMaster = new TableProfileMaster(((BaseActivity)
                        context).databaseHandler);
                String rawIdFromRcpId = tableProfileMaster.getRawIdFromRcpId(Integer.parseInt(
                        ((ProfileDetailActivity) context).pmId));
                if (StringUtils.contains(rawIdFromRcpId, ",")) {
                    /* Multiple Contacts with single pm_id */
                    String rawIds[] = rawIdFromRcpId.split(",");
                    ArrayList<String> arrayListRawIds = new ArrayList<>(Arrays.asList(rawIds));
                    arrayListRawIds.remove(rcpRawId);
                    tableProfileMaster.updateRawIds(Integer.parseInt(((ProfileDetailActivity)
                            context).pmId), StringUtils.join(arrayListRawIds, ","));
                } else {
                    if ((((ProfileDetailActivity) context).pmId).equalsIgnoreCase(((BaseActivity)
                            context).getUserPmId())) {
                        /* Single Contact with self registered pm_id */
                        tableProfileMaster.updateRawIds(Integer.parseInt(((ProfileDetailActivity)
                                context).pmId), "");
                    } else {
                        /* Single Contact with single pm_id */
                        queryManager.deleteRcProfileDetail(((ProfileDetailActivity) context).pmId);
                    }
                }
                phoneBookContacts.deleteContact(rcpRawId);
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onBackPressed();
                }
                IS_CONTACT_DELETED = true;
                if (isFavourite) {
                    rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_REMOVED);
                }
                break;
            //</editor-fold>
        }
    }

    //</editor-fold>

}
