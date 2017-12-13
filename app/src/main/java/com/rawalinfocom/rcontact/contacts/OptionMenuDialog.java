package com.rawalinfocom.rcontact.contacts;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
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
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.OptionMenuAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableCallReminder;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.RecyclerItemClickListener;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Monal on 24/12/16.
 */

class OptionMenuDialog {

    public static final int ALL_CONTACT_MY_PROFILE = 0;
    static final int ALL_CONTACT_NON_RCP = 1;
    static final int ALL_CONTACT_RCP = 2;
    static final int R_CONTACT_RCP = 3;
    static boolean IS_CONTACT_DELETED = false;
    private final boolean isCallLogRcpUser;

    private boolean isFavourite;
    private boolean isFromFavourite;

    private Context context;
    private Dialog dialog;

    private String dialogTag;
    private String rawId;

    private PhoneBookContacts phoneBookContacts;
    private RContactApplication rContactApplication;
//    private ArrayList<String> pbRating;

    MaterialDialog deleteConfirmationDialog;

    //<editor-fold desc="Constructor">
    OptionMenuDialog(final Context context, String rawId, final int menuType, boolean
            isFavourite, boolean isFromFavourite, boolean isCallLogRcpUser, ArrayList<String>
                             pbRating) {
        this.context = context;
        this.rawId = rawId;
        this.isFavourite = isFavourite;
        this.isFromFavourite = isFromFavourite;
        this.isCallLogRcpUser = isCallLogRcpUser;
//        this.pbRating = pbRating;

        rContactApplication = (RContactApplication) context.getApplicationContext();
        phoneBookContacts = new PhoneBookContacts(context);
        if (menuType == R_CONTACT_RCP) {
            if (StringUtils.contains(this.rawId, ",")) {
                String rawIds[] = this.rawId.split(",");
                this.rawId = rawIds[0];
            }
        }
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option_menu);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.60);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setGravity(Gravity.TOP | Gravity.END);

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        RecyclerView recyclerViewOptionMenu = dialog.findViewById(R.id.recycler_view_option_menu);

        String[] menus = new String[0];
        switch (menuType) {
            case ALL_CONTACT_NON_RCP:
                menus = new String[]{context.getString(R.string.edit), context.getString(R.string
                        .delete)/*, context.getString(R.string.call_reminder)*/};
                break;

            case ALL_CONTACT_RCP:

                if (pbRating.size() > 0) {
                    menus = new String[]{context.getString(R.string.edit), context.getString(R
                            .string
                            .str_view_in_phone_book), context.getString(R.string.delete)/*,
                            context.getString(R.string.call_reminder)*/};
                } else {
                    menus = new String[]{context.getString(R.string.edit), context.getString(R
                            .string
                            .str_view_in_phone_book),
                            context.getString(R.string.str_rate_profile), context.getString(R.string
                            .delete)/*, context.getString(R.string.call_reminder)*/};
                }

                break;

            case R_CONTACT_RCP:
                if (pbRating.size() > 0) {
                    menus = new String[]{context.getString(R.string.edit), context.getString(R
                            .string
                            .view_in_ac), context.getString(R.string
                            .delete)
                            /*, context.getString(R.string.call_reminder)*/};
                } else {
                    menus = new String[]{context.getString(R.string.edit), context.getString(R
                            .string
                            .view_in_ac),
                            context.getString(R.string.str_rate_profile), context.getString(R.string
                            .delete)/*, context.getString(R.string.call_reminder)*/};
                }
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
        QueryManager queryManager = new QueryManager(((BaseActivity) context).databaseHandler);

        switch (position) {

            //<editor-fold desc="Edit">
            case 0:
                intent = new Intent(Intent.ACTION_EDIT);
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.RawContacts
                        .CONTENT_URI, Long.parseLong(rawId));
                intent.setData(contactUri);
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).isContactEdited = true;
                }
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            //</editor-fold>

            // <editor-fold desc="Delete">
            case 1:
                showDeleteConfirmationDialog(queryManager, ALL_CONTACT_NON_RCP);
               /* PhoneBookContacts phoneBookContacts = new PhoneBookContacts(context);
                phoneBookContacts.deleteContact(rawId);
                IS_CONTACT_DELETED = true;
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onBackPressed();
                }
                if (isFavourite) {
                    rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_REMOVED);
                }
                if (isFromFavourite) {
                    AllContactsListFragment.arrayListPhoneBookContacts = null;
//                    rContactApplication.setArrayListAllPhoneBookContacts(new ArrayList<>());
                }*/
                break;
            //</editor-fold>

            // <editor-fold desc="Call Reminder">
            case 2:
                showCallReminderPopUp();
                break;
            //</editor-fold>
        }
    }

    private void menuAllContactRcp(View view, int position) {

        PhoneBookContacts phoneBookContacts = new PhoneBookContacts(context);
        QueryManager queryManager = new QueryManager(((BaseActivity) context).databaseHandler);
        Intent intent;
        Uri lookupUri, res;
        TextView textView = view.findViewById(R.id.text_option_menu);

        switch (position) {

            //<editor-fold desc="Edit">
            case 0:
                intent = new Intent(Intent.ACTION_EDIT);
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.RawContacts
                        .CONTENT_URI, Long.parseLong(rawId));
                intent.setData(contactUri);
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
                    lookupUri = ContentUris.withAppendedId(ContactsContract.RawContacts
                            .CONTENT_URI, Long.parseLong(rawId));
                    intent.setData(lookupUri);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string
                            .str_error_retrieving_contact_raw_id), Toast
                            .LENGTH_SHORT).show();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="Rate Profile">
            case 2:
//                Log.i("menuAllContactRcp", textView.getText().toString());
                if (StringUtils.equalsAnyIgnoreCase(textView.getText().toString(), context
                        .getString(R.string.delete))) {
                    showDeleteConfirmationDialog(queryManager, ALL_CONTACT_RCP);
                } else {
                    if (context instanceof ProfileDetailActivity) {
                        ((ProfileDetailActivity) context).onRatingClick();
                    }
                }
                break;
            //</editor-fold>

            // <editor-fold desc="Delete">
            case 3:
                /*TableProfileMaster tableProfileMaster = new TableProfileMaster(((BaseActivity)
                        context).databaseHandler);
                String rawIdFromRcpId = tableProfileMaster.getRawIdFromRcpId(Integer.parseInt(
                        ((ProfileDetailActivity) context).pmId));
                if (StringUtils.contains(rawIdFromRcpId, ",")) {
                    *//* Multiple Contacts with single pm_id *//*
                    String rawIds[] = rawIdFromRcpId.split(",");
                    ArrayList<String> arrayListRawIds = new ArrayList<>(Arrays.asList(rawIds));
                    arrayListRawIds.remove(rawId);
                    tableProfileMaster.updateRawIds(Integer.parseInt(((ProfileDetailActivity)
                            context).pmId), StringUtils.join(arrayListRawIds, ","));
                } else {
                    if ((((ProfileDetailActivity) context).pmId).equalsIgnoreCase(((BaseActivity)
                            context).getUserPmId())) {
                        *//* Single Contact with self registered pm_id *//*
                        tableProfileMaster.updateRawIds(Integer.parseInt(((ProfileDetailActivity)
                                context).pmId), "");
                    } else {
                        *//* Single Contact with single pm_id *//*
                        queryManager.deleteRcProfileDetail(((ProfileDetailActivity) context).pmId);
                    }
                }*/
                if (StringUtils.equalsAnyIgnoreCase(textView.getText().toString(), context
                        .getString(R.string.delete))) {
                    showDeleteConfirmationDialog(queryManager, ALL_CONTACT_RCP);
                } else {
                    showCallReminderPopUp();
                }
               /* phoneBookContacts.deleteContact(rawId);
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onBackPressed();
                }
                IS_CONTACT_DELETED = true;
                if (isFavourite) {
                    rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_REMOVED);
                }
                if (isFromFavourite) {
                    AllContactsListFragment.arrayListPhoneBookContacts = null;
//                    rContactApplication.setArrayListAllPhoneBookContacts(new ArrayList<>());
                }*/
                break;
            //</editor-fold>

            // <editor-fold desc="Call Reminder">
            case 4:
                showCallReminderPopUp();
                break;
            //</editor-fold>
        }
    }

    private void menuRContactRcp(View view, int position) {

        PhoneBookContacts phoneBookContacts = new PhoneBookContacts(context);
        QueryManager queryManager = new QueryManager(((BaseActivity) context).databaseHandler);
        Intent intent;
        Uri lookupUri, res;
//        String rcpRawId = ((ProfileDetailActivity) context).checkNumberFavourite;
//        if (StringUtils.contains(rcpRawId, ",")) {
//            rcpRawId = rcpRawId.split(",")[0];
//        }
        switch (position) {

            //<editor-fold desc="Edit">
            case 0:
                intent = new Intent(Intent.ACTION_EDIT);
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.RawContacts
                        .CONTENT_URI, Long.parseLong(rawId));
                intent.setData(contactUri);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            //</editor-fold>

            // <editor-fold desc="View in AC">
            case 1:
                String phonebookName = phoneBookContacts.getStructuredName(rawId);
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
                bundle.putBoolean(AppConstants.EXTRA_IS_RCP_USER, isCallLogRcpUser);
                bundle.putString(AppConstants.EXTRA_PM_ID, ((ProfileDetailActivity) context).pmId);
                bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, rawId);
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
                /*TableProfileMaster tableProfileMaster = new TableProfileMaster(((BaseActivity)
                        context).databaseHandler);
                String rawIdFromRcpId = tableProfileMaster.getRawIdFromRcpId(Integer.parseInt(
                        ((ProfileDetailActivity) context).pmId));
                if (StringUtils.contains(rawIdFromRcpId, ",")) {
                    *//* Multiple Contacts with single pm_id *//*
                    String rawIds[] = rawIdFromRcpId.split(",");
                    ArrayList<String> arrayListRawIds = new ArrayList<>(Arrays.asList(rawIds));
                    arrayListRawIds.remove(rcpRawId);
                    tableProfileMaster.updateRawIds(Integer.parseInt(((ProfileDetailActivity)
                            context).pmId), StringUtils.join(arrayListRawIds, ","));
                } else {
                    if ((((ProfileDetailActivity) context).pmId).equalsIgnoreCase(((BaseActivity)
                            context).getUserPmId())) {
                        *//* Single Contact with self registered pm_id *//*
                        tableProfileMaster.updateRawIds(Integer.parseInt(((ProfileDetailActivity)
                                context).pmId), "");
                    } else {
                        *//* Single Contact with single pm_id *//*
                        queryManager.deleteRcProfileDetail(((ProfileDetailActivity) context).pmId);
                    }
                }*/

                showDeleteConfirmationDialog(queryManager, R_CONTACT_RCP);
               /* phoneBookContacts.deleteContact(rawId);
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onBackPressed();
                }
                IS_CONTACT_DELETED = true;
                if (isFavourite) {
                    rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_REMOVED);
                }
                AllContactsListFragment.arrayListPhoneBookContacts = null;*/
//                rContactApplication.setArrayListAllPhoneBookContacts(new ArrayList<>());
                break;
            //</editor-fold>

            // <editor-fold desc="Call Reminder">
            case 4:
                showCallReminderPopUp();
                break;
            //</editor-fold>
        }
    }
    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void showDeleteConfirmationDialog(final QueryManager queryManager, final int
            deleteFrom) {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        deleteConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        deleteConfirmationDialog.dismissDialog();
                        PhoneBookContacts phoneBookContacts = new PhoneBookContacts
                                (context);
                        switch (deleteFrom) {
                            case ALL_CONTACT_NON_RCP:
                              /*  PhoneBookContacts phoneBookContacts = new PhoneBookContacts
                                        (context);*/
                                phoneBookContacts.deleteContact(rawId);
                                IS_CONTACT_DELETED = true;
                                if (context instanceof ProfileDetailActivity) {
                                    ((ProfileDetailActivity) context).onBackPressed();
                                }
                                if (isFavourite) {
                                    rContactApplication.setFavouriteStatus(RContactApplication
                                            .FAVOURITE_REMOVED);
                                }
                                if (isFromFavourite) {
                                    AllContactsListFragment.arrayListPhoneBookContacts = null;
//                    rContactApplication.setArrayListAllPhoneBookContacts(new ArrayList<>());
                                }
                                break;

                            case ALL_CONTACT_RCP:

                                queryManager.updateRcProfileDetail(context, Integer.parseInt(
                                        ((ProfileDetailActivity) context).pmId), rawId);

                                phoneBookContacts.deleteContact(rawId);
                                if (context instanceof ProfileDetailActivity) {
                                    ((ProfileDetailActivity) context).onBackPressed();
                                }
                                IS_CONTACT_DELETED = true;
                                if (isFavourite) {
                                    rContactApplication.setFavouriteStatus(RContactApplication
                                            .FAVOURITE_REMOVED);
                                }
                                if (isFromFavourite) {
                                    AllContactsListFragment.arrayListPhoneBookContacts = null;
                                }
                                break;

                            case R_CONTACT_RCP:

                                queryManager.updateRcProfileDetail(context, Integer.parseInt(
                                        ((ProfileDetailActivity) context).pmId), rawId);

                                phoneBookContacts.deleteContact(rawId);
                                if (context instanceof ProfileDetailActivity) {
                                    ((ProfileDetailActivity) context).onBackPressed();
                                }
                                IS_CONTACT_DELETED = true;
                                if (isFavourite) {
                                    rContactApplication.setFavouriteStatus(RContactApplication
                                            .FAVOURITE_REMOVED);
                                }
                                AllContactsListFragment.arrayListPhoneBookContacts = null;
                                break;
                        }
                        break;
                }

            }
        };

        deleteConfirmationDialog = new MaterialDialog(context, cancelListener);
        deleteConfirmationDialog.setTitleVisibility(View.GONE);
        deleteConfirmationDialog.setLeftButtonText(context.getString(R.string.action_cancel));
        deleteConfirmationDialog.setRightButtonText("Yes");
        deleteConfirmationDialog.setDialogBody("Are you sure you want to delete this contact?");

        deleteConfirmationDialog.showDialog();

    }

    private void showCallReminderPopUp() {
        ArrayList<String> arrayListCallReminderOption;
        TableCallReminder tableCallReminder = new TableCallReminder(new DatabaseHandler(context));
//        Long callReminderTime = Utils.getLongPreference(context, AppConstants
// .PREF_CALL_REMINDER, 0);
        String number = ((ProfileDetailActivity) context).historyNumber;
        if (number.contains("("))
            number = number.replace("(", "");
        if (number.contains(")"))
            number = number.replace(")", "");
        if (number.contains("-"))
            number = number.replace("-", "");
        if (number.contains(" "))
            number = number.replace(" ", "");

        number = number.trim();
        String formattedNumber = Utils.getFormattedNumber(context, number);
        String time = tableCallReminder.getReminderTimeFromNumber(formattedNumber);
        Long callReminderTime = 0L;
        if (!StringUtils.isEmpty(time))
            callReminderTime = Long.parseLong(time);

        if (callReminderTime > 0) {
            Date date1 = new Date(callReminderTime);
            String setTime = new SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.getDefault())
                    .format(date1);
            arrayListCallReminderOption = new ArrayList<>(Arrays.asList(context.getString(R
                            .string.min15),
                    context.getString(R.string.hour1), context.getString(R.string.hour2), context
                            .getString(R.string.hour6), setTime + "     Edit"));
            MaterialListDialog materialListDialog = new MaterialListDialog(context,
                    arrayListCallReminderOption,
                    formattedNumber, 0, "", "", "");
            materialListDialog.setDialogTitle(context.getString(R.string.call_reminder));
            materialListDialog.showDialog();
        } else {
            arrayListCallReminderOption = new ArrayList<>(Arrays.asList(context.getString(R
                            .string.min15),
                    context.getString(R.string.hour1), context.getString(R.string.hour2), context
                            .getString(R.string.hour6),
                    context.getString(R.string.setDateAndTime)));
            MaterialListDialog materialListDialog = new MaterialListDialog(context,
                    arrayListCallReminderOption,
                    formattedNumber, 0, "", "", "");
            materialListDialog.setDialogTitle(context.getString(R.string.call_reminder)
                    .toUpperCase());
            materialListDialog.showDialog();
        }
    }

    //</editor-fold>
}
