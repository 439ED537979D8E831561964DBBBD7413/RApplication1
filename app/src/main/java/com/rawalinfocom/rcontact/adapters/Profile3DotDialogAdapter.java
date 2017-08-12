package com.rawalinfocom.rcontact.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.calllog.CallLogDeleteActivity;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.MaterialDialogClipboard;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 20/03/17.
 */

public class Profile3DotDialogAdapter extends RecyclerView.Adapter<Profile3DotDialogAdapter
        .MaterialViewHolder> {

    private Context context;
    private ArrayList<String> arrayListString;
    private MaterialDialog callConfirmationDialog;
    private String numberToCall;
    private long callLogDateToDelete;
    private boolean isFromCallLogFragment = false;
    private ArrayList<CallLogType> arrayListCallLogType;
    private String dialogName;
    private String uniqueRowId;
    private String key;
    private String profileUrl;
    private String pmId;
    private boolean isCallLogRcpUser;
    String rcpVerifiedId;
    String cloudName;

    MaterialDialog clearConfirmationDialog;

    public Profile3DotDialogAdapter(Context context, ArrayList<String> arrayList, String number,
                                    long date, boolean isFromCallLogs, ArrayList<CallLogType>
                                            list, String name, String uniqueRowId, String key,
                                    String profileUrl, String pmId, boolean isCallLogRcpUser,
                                    String rcpVerifiedId, String cloudName) {
        this.context = context;
        this.arrayListString = arrayList;
        this.numberToCall = number;
        this.callLogDateToDelete = date;
        this.isFromCallLogFragment = isFromCallLogs;
        this.arrayListCallLogType = list;
        this.dialogName = name;
        this.profileUrl = profileUrl;
        this.uniqueRowId = uniqueRowId;
        this.key = key;
        this.pmId = pmId;
        this.isCallLogRcpUser = isCallLogRcpUser;
        this.rcpVerifiedId = rcpVerifiedId;
        this.cloudName = cloudName;
    }

    @Override
    public Profile3DotDialogAdapter.MaterialViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .list_item_dialog_call_log,
                parent, false);
        return new Profile3DotDialogAdapter.MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Profile3DotDialogAdapter.MaterialViewHolder holder, final int
            position) {
        final String value = arrayListString.get(position);
        holder.textItemValue.setText(value);

        holder.rippleRow.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (value.equalsIgnoreCase(context.getString(R.string.add_to_contact))) {

                    Utils.addToContact(context, numberToCall);

                } else if (value.equalsIgnoreCase(context.getString(R.string.edit))) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        Uri contactUri = ContentUris.withAppendedId(ContactsContract.RawContacts
                                .CONTENT_URI, Long.parseLong(key));
                        intent.setData(contactUri);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Unable to open activity", Toast.LENGTH_SHORT)
                                .show();
                    }

                } else if (value.equalsIgnoreCase(context.getString(R.string
                        .add_to_existing_contact))) {
                    Utils.addToExistingContact(context, numberToCall);

                } else if (value.equalsIgnoreCase(context.getString(R.string.view_profile))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.copy_phone_number))) {
                    MaterialDialogClipboard materialDialogClipboard = new MaterialDialogClipboard
                            (context, numberToCall);
                    materialDialogClipboard.showDialog();
                } else if (value.equalsIgnoreCase(context.getString(R.string.view_in_ac))) {

                    Intent intent = new Intent(context, ProfileDetailActivity.class);
                    intent.putExtra(AppConstants.EXTRA_CONTACT_NAME, dialogName);
                    intent.putExtra(AppConstants.EXTRA_PHONE_BOOK_ID, key);
                    intent.putExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL, profileUrl);
                    intent.putExtra(AppConstants.EXTRA_PM_ID, pmId);
                    intent.putExtra(AppConstants.EXTRA_IS_RCP_USER, isCallLogRcpUser);
                    intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER, numberToCall);
                    intent.putExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME, cloudName);
                    context.startActivity(intent);

                } else if (value.equalsIgnoreCase(context.getString(R.string.block))) {

                    ArrayList<CallLogType> listToBlock = new ArrayList<>();
                    HashMap<String, ArrayList<CallLogType>> listHashMap = new HashMap<>();
                    String uniqueContactId = "";
                    if (!TextUtils.isEmpty(dialogName)) {
                        listToBlock = getNumbersFromName(dialogName);
                        // Log.i("block list size =", listToBlock.size() + "");
                        for (int i = 0; i < listToBlock.size(); i++) {
                            CallLogType callLogType = listToBlock.get(i);
                            uniqueContactId = callLogType.getUniqueContactId();
                        }

                    } else {

                        // Log.i("Number to block", numberToCall);
                        CallLogType callLogType = new CallLogType();
                        uniqueContactId = uniqueRowId;
                        callLogType.setUniqueContactId(uniqueContactId);
                        callLogType.setNumber(numberToCall);
                        listToBlock.add(callLogType);
                        // Log.i("block list size =", listToBlock.size() + "");
                    }

                    if (Utils.getHashMapPreferenceForBlock(context, AppConstants
                            .PREF_BLOCK_CONTACT_LIST) != null) {
                        listHashMap.putAll(Utils.getHashMapPreferenceForBlock(context, AppConstants
                                .PREF_BLOCK_CONTACT_LIST));
                    }
                    if (listHashMap.containsKey(uniqueContactId)) {

                    } else {
                        listHashMap.put(uniqueContactId, listToBlock);
                    }
                    Utils.setHashMapPreference(context, AppConstants.PREF_BLOCK_CONTACT_LIST,
                            listHashMap);

                    Intent localBroadcastIntent = new Intent(AppConstants
                            .ACTION_LOCAL_BROADCAST_PROFILE_BLOCK);
                    localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_BLOCK,
                            true);
                    LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                            .getInstance(context);
                    myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                } else if (value.equalsIgnoreCase(context.getString(R.string.unblock))) {

                    if (Utils.getHashMapPreferenceForBlock(context, AppConstants
                            .PREF_BLOCK_CONTACT_LIST) != null) {
                        HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                                Utils.getHashMapPreferenceForBlock(context, AppConstants
                                        .PREF_BLOCK_CONTACT_LIST);
                        ArrayList<CallLogType> callLogTypeList = new ArrayList<CallLogType>();
                        String blockedNumber = "";
                        if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                            if (blockProfileHashMapList.containsKey(key))
                                callLogTypeList.addAll(blockProfileHashMapList.get(key));

                        }
                        if (callLogTypeList != null) {
                            for (int j = 0; j < callLogTypeList.size(); j++) {
                                String tempNumber = callLogTypeList.get(j).getNumber();
                                String blockContactName = callLogTypeList.get(j).getName();
                                if (!TextUtils.isEmpty(numberToCall)) {
                                    Pattern numberPat = Pattern.compile("\\d+");
                                    Matcher matcher1 = numberPat.matcher(numberToCall);
                                    if (matcher1.find()) {
                                        if (tempNumber.equalsIgnoreCase(numberToCall)) {
                                            blockedNumber = tempNumber;
                                        }
                                    } else {
                                        if (blockContactName.equalsIgnoreCase(numberToCall)) {
                                            blockedNumber = blockContactName;
                                        }
                                    }
                                }
                            }
                        }

                        if (!TextUtils.isEmpty(blockedNumber)) {
                            blockProfileHashMapList.remove(key);
                            Utils.setHashMapPreference(context, AppConstants
                                            .PREF_BLOCK_CONTACT_LIST,
                                    blockProfileHashMapList);
                        }
                    }

                    Intent localBroadcastIntent = new Intent(AppConstants
                            .ACTION_LOCAL_BROADCAST_PROFILE_BLOCK);
                    localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_BLOCK,
                            false);
                    LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                            .getInstance(context);
                    myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);


                } else if (value.equalsIgnoreCase(context.getString(R.string.unblock_All))) {

                    if (Utils.getHashMapPreferenceForBlock(context, AppConstants
                            .PREF_BLOCK_CONTACT_LIST) != null) {
                        HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                                Utils.getHashMapPreferenceForBlock(context, AppConstants
                                        .PREF_BLOCK_CONTACT_LIST);
                        if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                            blockProfileHashMapList.clear();
                            Utils.setHashMapPreference(context, AppConstants
                                            .PREF_BLOCK_CONTACT_LIST,
                                    blockProfileHashMapList);
                        }
                        Intent localBroadcastIntent = new Intent(AppConstants
                                .ACTION_LOCAL_BROADCAST_UNBLOCK);
                        LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                                .getInstance(context);
                        myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                    }
                } else if (value.equalsIgnoreCase(context.getString(R.string.call_reminder))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.clear_call_log))) {

                    /*if (isFromCallLogFragment) {

                        deleteCallLogByNumber(numberToCall);

                    } else {

                        Pattern numberPat = Pattern.compile("\\d+");
                        Matcher matcher1 = numberPat.matcher(numberToCall);
                        if (matcher1.find()) {
                            deleteCallHistoryByNumber(numberToCall);
                        } else {
                            deleteCallHistoryByName(numberToCall);
                        }

                    }*/
                    showClearConfirmationDialog();

                } else if (value.equalsIgnoreCase(context.getString(R.string.delete))) {

                    Intent intent = new Intent(context, CallLogDeleteActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(AppConstants.EXTRA_CALL_ARRAY_LIST, arrayListCallLogType);

                    if (StringUtils.isEmpty(dialogName))
                        b.putString(AppConstants.EXTRA_RCP_VERIFIED_ID, rcpVerifiedId);
                    else {
                        b.putString(AppConstants.EXTRA_RCP_VERIFIED_ID, "");
                        if (isCallLogRcpUser) {
                            rcpVerifiedId = "1";
                            b.putString(AppConstants.EXTRA_RCP_VERIFIED_ID, rcpVerifiedId);
                        }
                    }
                    intent.putExtras(b);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);

                } else if (value.equalsIgnoreCase(context.getString(R.string.edit))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.view_in_ac))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.view_in_rc))) {

                } else {

                    Toast.makeText(context, context.getString(R.string.please_select_one), Toast
                            .LENGTH_SHORT)
                            .show();
                }

                Intent localBroadcastIntent = new Intent(AppConstants
                        .ACTION_LOCAL_BROADCAST_DIALOG);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                        (context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

            }
        });

    }


    //    @TargetApi(Build.VERSION_CODES.M)
    private ArrayList<CallLogType> getNumbersFromName(String number) {
        Cursor cursor = null;
        ArrayList<CallLogType> listNumber = new ArrayList<>();
        try {
            final Uri Person = Uri.withAppendedPath(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                    Uri.encode(number));

            cursor = context.getContentResolver().query(Person, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " =?", new
                            String[]{number}, null);

            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (cursor.moveToNext()) {
                    CallLogType callLogType = new CallLogType();
                    String profileNumber = cursor.getString(number1);
                    String formattedNumber = Utils.getFormattedNumber(context, profileNumber);
                    String uniqueContactId = getStarredStatusFromNumber(profileNumber);
                    callLogType.setUniqueContactId(uniqueContactId);
                    callLogType.setName(number);
                    callLogType.setNumber(formattedNumber);
                    listNumber.add(callLogType);
                }
            }
            cursor.close();


        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return listNumber;
    }

    private String getStarredStatusFromNumber(String phoneNumber) {
        String numberId, rawId = "";
        try {

//            numberId = "";
            ContentResolver contentResolver = context.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                   /* String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));*/
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
                    Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] projection1 = new String[]{
                            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
                    };

                    String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
                    String[] selectionArgs = new String[]{numberId};

                    Cursor cursor1 = contentResolver.query(uri1, projection1, selection,
                            selectionArgs, null);
                    if (cursor1 != null) {
                        while (cursor1.moveToNext()) {
                            rawId = cursor1.getString(cursor1.getColumnIndexOrThrow
                                    (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                        }
                        cursor1.close();
                    }
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return rawId;
    }

    private void deleteCallLogByNumber(String number) {
        try {

            long dateToCompare = 0;
            long nextDate = 0;
            Date objDate1 = new Date(callLogDateToDelete);
            String dateToDelete = new SimpleDateFormat("dd/MM/yyyy").format(objDate1);

            /*Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date TomoDate;
            TomoDate = cal.getTime();
            String tomorrowDate = new SimpleDateFormat("dd/MM/yyyy").format(TomoDate);*/

            Date date1 = new Date(callLogDateToDelete + (1000 * 60 * 60 * 24));
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            String tomorrowDate = sdf1.format(date1);


            long callLogDate = callLogHistory(number);
            Date date = new Date(callLogDate);
            String dateToCompare1 = new SimpleDateFormat("dd/MM/yyyy").format(date);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate = sdf.parse(dateToDelete);
                dateToCompare = startDate.getTime();

                Date tomdate = sdf.parse(tomorrowDate);
                nextDate = tomdate.getTime();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Date wise deletion
            /*String  where =   CallLog.Calls.NUMBER + " =?"
                    + " AND " + android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?";
            String[] selectionArguments = new String[]{number,String.valueOf(dateToCompare),
            String.valueOf(nextDate)};
            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,where,
            selectionArguments);*/

            // Delete call records for selected number
            String where = CallLog.Calls.NUMBER + " =?";
            String[] selectionArguments = new String[]{number};
            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, where,
                    selectionArguments);

            /*int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog
            .Calls.NUMBER + " =?"
                    + " AND " + dateToCompare + "=?", new String[]{number, dateToDelete});
            Log.i("Delete Query value", value + "");*/

            if (value > 0) {
                // Log.i("Delete Query value", value + "");
                Toast.makeText(context, value + " " + context.getString(R.string.call_logs_deleted),
                        Toast.LENGTH_SHORT).show();

                Intent localBroadcastIntent = new Intent(AppConstants
                        .ACTION_LOCAL_BROADCAST_PROFILE);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                        (context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                Intent localBroadcastIntent1 = new Intent(AppConstants.ACTION_LOCAL_BROADCAST);
                localBroadcastIntent1.putExtra(AppConstants.EXTRA_CLEAR_CALL_LOGS, true);
                LocalBroadcastManager myLocalBroadcastManager1 = LocalBroadcastManager
                        .getInstance(context);
                myLocalBroadcastManager1.sendBroadcast(localBroadcastIntent1);

            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private void deleteCallHistoryByName(String name) {
        try {
            String where = CallLog.Calls.CACHED_NAME + " =?";
            String[] selectionArguments = new String[]{name};
            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, where,
                    selectionArguments);
            if (value > 0) {
                // Log.i("Delete Query value", value + "");
                Toast.makeText(context, value + context.getString(R.string.call_logs_deleted),
                        Toast.LENGTH_SHORT).show();

                Intent localBroadcastIntent = new Intent(AppConstants
                        .ACTION_LOCAL_BROADCAST_PROFILE);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                        (context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                Intent localBroadcastIntent1 = new Intent(AppConstants.ACTION_LOCAL_BROADCAST);
                localBroadcastIntent1.putExtra(AppConstants.EXTRA_CLEAR_CALL_LOGS_FROM_CONTACTS,
                        true);
                LocalBroadcastManager myLocalBroadcastManager1 = LocalBroadcastManager
                        .getInstance(context);
                myLocalBroadcastManager1.sendBroadcast(localBroadcastIntent1);

                Intent localBroadcastIntent2 = new Intent(AppConstants
                        .ACTION_LOCAL_BROADCAST_CALL_HISTORY_ACTIVITY);
                localBroadcastIntent2.putExtra("action", "delete");
                LocalBroadcastManager myLocalBroadcastManager2 = LocalBroadcastManager
                        .getInstance(context);
                myLocalBroadcastManager2.sendBroadcast(localBroadcastIntent2);

            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private void deleteCallHistoryByNumber(String number) {
        try {
            String where = CallLog.Calls.NUMBER + " =?";
            String[] selectionArguments = new String[]{number};
            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, where,
                    selectionArguments);
            if (value > 0) {
                // Log.i("Delete Query value", value + "");
                Toast.makeText(context, value + context.getString(R.string.call_logs_deleted),
                        Toast.LENGTH_SHORT).show();

                Intent localBroadcastIntent = new Intent(AppConstants
                        .ACTION_LOCAL_BROADCAST_PROFILE);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                        (context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                Intent localBroadcastIntent1 = new Intent(AppConstants.ACTION_LOCAL_BROADCAST);
                localBroadcastIntent1.putExtra(AppConstants.EXTRA_CLEAR_CALL_LOGS_FROM_CONTACTS,
                        true);
                LocalBroadcastManager myLocalBroadcastManager1 = LocalBroadcastManager
                        .getInstance(context);
                myLocalBroadcastManager1.sendBroadcast(localBroadcastIntent1);

                Intent localBroadcastIntent2 = new Intent(AppConstants
                        .ACTION_LOCAL_BROADCAST_CALL_HISTORY_ACTIVITY);
                LocalBroadcastManager myLocalBroadcastManager2 = LocalBroadcastManager
                        .getInstance(context);
                myLocalBroadcastManager2.sendBroadcast(localBroadcastIntent2);


            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayListString.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_item_value)
        TextView textItemValue;

        @BindView(R.id.linear_main)
        LinearLayout linearMain;

        @BindView(R.id.rippleRow)
        RippleView rippleRow;

        MaterialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

//    private void showCallConfirmationDialog(final String number) {
//
//        final String finalNumber;
//
//        if (!number.startsWith("+91")) {
//            finalNumber = "+91" + number;
//        } else {
//            finalNumber = number;
//        }
//
//        RippleView.OnRippleCompleteListener cancelListener = new RippleView
//                .OnRippleCompleteListener() {
//
//            @Override
//            public void onComplete(RippleView rippleView) {
//                switch (rippleView.getId()) {
//                    case R.id.rippleLeft:
//                        callConfirmationDialog.dismissDialog();
//                        break;
//
//                    case R.id.rippleRight:
//                        callConfirmationDialog.dismissDialog();
//                        /*Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
//                        number));
//                        try {
//                            context.startActivity(intent);
//
//                        } catch (SecurityException e) {
//                            e.printStackTrace();
//                        }*/
//                        Utils.callIntent(context, finalNumber);
//                        break;
//                }
//            }
//        };
//
//        callConfirmationDialog = new MaterialDialog(context, cancelListener);
//        callConfirmationDialog.setTitleVisibility(View.GONE);
//        callConfirmationDialog.setLeftButtonText(context.getString(R.string.action_cancel));
//        callConfirmationDialog.setRightButtonText(context.getString(R.string.action_call));
//        callConfirmationDialog.setDialogBody(context.getString(R.string.action_call) + " " + finalNumber + "?");
//        callConfirmationDialog.showDialog();
//
//    }

    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByNumber(String number) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.NUMBER + " =?", new String[]{number}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    private long callLogHistory(String number) {
        Cursor cursor = getCallHistoryDataByNumber(number);
        long callDateToDelete = 0;
        try {
            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                while (cursor.moveToNext()) {
                    String callDate = cursor.getString(date);
                    callDateToDelete = Long.parseLong(callDate);
                }
            }
            cursor.close();


        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return callDateToDelete;
    }

    private void showClearConfirmationDialog() {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        clearConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        if (isFromCallLogFragment) {

                            deleteCallLogByNumber(numberToCall);

                        } else {

                            Pattern numberPat = Pattern.compile("\\d+");
                            Matcher matcher1 = numberPat.matcher(numberToCall);
                            if (matcher1.find()) {
                                deleteCallHistoryByNumber(numberToCall);
                            } else {
                                deleteCallHistoryByName(numberToCall);
                            }

                        }
                        break;
                }

            }
        };

        clearConfirmationDialog = new MaterialDialog(context, cancelListener);
        clearConfirmationDialog.setTitleVisibility(View.GONE);
        clearConfirmationDialog.setLeftButtonText(context.getString(R.string.action_cancel));
        clearConfirmationDialog.setRightButtonText("Yes");
        clearConfirmationDialog.setDialogBody("Are you sure you want to clear all call logs?");

        clearConfirmationDialog.showDialog();

    }
}
