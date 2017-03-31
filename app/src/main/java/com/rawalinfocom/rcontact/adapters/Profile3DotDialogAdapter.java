package com.rawalinfocom.rcontact.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.calllog.CallLogDeleteActivity;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.MaterialDialogClipboard;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 20/03/17.
 */

public class Profile3DotDialogAdapter extends RecyclerView.Adapter<Profile3DotDialogAdapter.MaterialViewHolder>  {
    private Context context;
    private ArrayList<String> arrayListString;
    MaterialDialog callConfirmationDialog;
    String numberToCall;
    long callLogDateToDelete;
    boolean isFromCallLogFragment = false;
    ArrayList<CallLogType> arrayListCallLogType;
    String dialogName;
    String uniqueRowId;
    String key;

    public Profile3DotDialogAdapter(Context context, ArrayList<String> arrayList, String number, long date,
                                    boolean isFromCallLogs, ArrayList<CallLogType> list, String name, String uniqueRowId,
                                    String key) {
        this.context = context;
        this.arrayListString = arrayList;
        this.numberToCall = number;
        this.callLogDateToDelete = date;
        this.isFromCallLogFragment =  isFromCallLogs;
        this.arrayListCallLogType =  list;
        this.dialogName =  name;
        this.uniqueRowId =  uniqueRowId;
        this.key =  key;
    }

    @Override
    public Profile3DotDialogAdapter.MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dialog_call_log,
                parent, false);
        return new Profile3DotDialogAdapter.MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Profile3DotDialogAdapter.MaterialViewHolder holder, final int position) {
        final String value = arrayListString.get(position);
        holder.textItemValue.setText(value);

        holder.rippleRow.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (value.equalsIgnoreCase(context.getString(R.string.add_to_contact))) {

                    Utils.addToContact(context, numberToCall);

                } else if (value.equalsIgnoreCase(context.getString(R.string.add_to_existing_contact))) {
                    Utils.addToExistingContact(context, numberToCall);

                } else if (value.equalsIgnoreCase(context.getString(R.string.view_profile))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.copy_phone_number))) {
                    MaterialDialogClipboard materialDialogClipboard = new MaterialDialogClipboard(context, numberToCall);
                    materialDialogClipboard.showDialog();

                } else if (value.equalsIgnoreCase(context.getString(R.string.block))) {

                    ArrayList<CallLogType> listToBlock = new ArrayList<CallLogType>();
                    HashMap<String,ArrayList<CallLogType>> listHashMap =  new HashMap<String, ArrayList<CallLogType>>();
                    String uniqueContactId = "";
                    if (!TextUtils.isEmpty(dialogName)) {
                        listToBlock = getNumbersFromName(dialogName);
                        Log.i("block list size =", listToBlock.size() + "");
                        for (int i=0; i<listToBlock.size();i++){
                            CallLogType callLogType =  listToBlock.get(i);
                            uniqueContactId = callLogType.getUniqueContactId();
                        }

                    } else {

                        Log.i("Number to block", numberToCall);
                        CallLogType callLogType = new CallLogType();
                        uniqueContactId = uniqueRowId;
                        callLogType.setUniqueContactId(uniqueContactId);
                        callLogType.setNumber(numberToCall);
                        listToBlock.add(callLogType);
                        Log.i("block list size =", listToBlock.size() + "");
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

                    Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_PROFILE_BLOCK);
                    localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_BLOCK,
                            true);
                    LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                    myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                } else if(value.equalsIgnoreCase(context.getString(R.string.unblock))){

                    if (Utils.getHashMapPreferenceForBlock(context, AppConstants
                            .PREF_BLOCK_CONTACT_LIST) != null) {
                        HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                                Utils.getHashMapPreferenceForBlock(context, AppConstants.PREF_BLOCK_CONTACT_LIST);
                        ArrayList<CallLogType> callLogTypeList = new ArrayList<CallLogType>();
                        String blockedNumber = "";
                        if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                            if (blockProfileHashMapList.containsKey(key))
                                callLogTypeList.addAll(blockProfileHashMapList.get(key));

                        }
                        if (callLogTypeList != null) {
                            for (int j = 0; j < callLogTypeList.size(); j++) {
                                String tempNumber = callLogTypeList.get(j).getNumber();
                                String blockContactName =  callLogTypeList.get(j).getName();
                                if(!TextUtils.isEmpty(numberToCall)){
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

                        if(!TextUtils.isEmpty(blockedNumber)){
                            blockProfileHashMapList.remove(key);
                            Utils.setHashMapPreference(context, AppConstants.PREF_BLOCK_CONTACT_LIST,
                                    blockProfileHashMapList);
                        }
                    }

                    Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_PROFILE_BLOCK);
                    localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_BLOCK,
                            false);
                    LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                    myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);


                }else if(value.equalsIgnoreCase(context.getString(R.string.unblock_All))){

                    if (Utils.getHashMapPreferenceForBlock(context, AppConstants
                            .PREF_BLOCK_CONTACT_LIST) != null) {
                        HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                                Utils.getHashMapPreferenceForBlock(context, AppConstants.PREF_BLOCK_CONTACT_LIST);
                        if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                            blockProfileHashMapList.clear();
                            Utils.setHashMapPreference(context, AppConstants.PREF_BLOCK_CONTACT_LIST,
                                    blockProfileHashMapList);
                        }
                        Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_UNBLOCK);
                        LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                        myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                    }
                }else if (value.equalsIgnoreCase(context.getString(R.string.call_reminder))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.clear_call_log))) {

                    if(isFromCallLogFragment){

                        deleteCallLogByNumber(numberToCall);

                    }else{

                        Pattern numberPat = Pattern.compile("\\d+");
                        Matcher matcher1 = numberPat.matcher(numberToCall);
                        if (matcher1.find()) {
                            deleteCallHistoryByNumber(numberToCall);
                        } else {
                            deleteCallHistoryByName(numberToCall);
                        }

                    }

                } else if (value.equalsIgnoreCase(context.getString(R.string.delete))) {

                    Intent intent = new Intent(context, CallLogDeleteActivity.class);
                    Bundle b =  new Bundle();
                    b.putSerializable(AppConstants.EXTRA_CALL_ARRAY_LIST,arrayListCallLogType);
                    intent.putExtras(b);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);

                } else if (value.equalsIgnoreCase(context.getString(R.string.edit))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.view_in_ac))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.view_in_rc))) {

                } else {

                    Toast.makeText(context, "Please select any one option", Toast.LENGTH_SHORT).show();
                }

                Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_DIALOG);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
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
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " =?", new String[]{number}, null);

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
        String numberId = "";
        try {

            numberId = "";
            ContentResolver contentResolver = context.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
//                Log.d("LocalPBId", "contactMatch id: " + numberId + " of " + contactName);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return numberId;
    }

    private void deleteCallLogByNumber(String number) {
        try {

            long dateToCompare =0;
            long nextDate = 0;
            Date objDate1 = new Date(callLogDateToDelete);
            String dateToDelete = new SimpleDateFormat("dd/MM/yyyy").format(objDate1);

            /*Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date TomoDate;
            TomoDate = cal.getTime();
            String tomorrowDate = new SimpleDateFormat("dd/MM/yyyy").format(TomoDate);*/

            Date date1=new Date(callLogDateToDelete + (1000 * 60 * 60 * 24));
            SimpleDateFormat sdf1=new SimpleDateFormat("dd/MM/yyyy");
            String tomorrowDate = sdf1.format(date1);


            long callLogDate = callLogHistory(number);
            Date date = new Date(callLogDate);
            String dateToCompare1 = new SimpleDateFormat("dd/MM/yyyy").format(date);

            try{
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate = sdf.parse(dateToDelete);
                dateToCompare = startDate.getTime();

                Date tomdate = sdf.parse(tomorrowDate);
                nextDate = tomdate.getTime();

            }catch (Exception e){
                e.printStackTrace();
            }

            String  where =   CallLog.Calls.NUMBER + " =?"
                    + " AND " + android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?";
            String[] selectionArguments = new String[]{number,String.valueOf(dateToCompare),String.valueOf(nextDate)};

            /*int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls.NUMBER + " =?"
                    + " AND " + dateToCompare + "=?", new String[]{number, dateToDelete});
            Log.i("Delete Query value", value + "");*/

            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,where, selectionArguments);
            if(value > 0){
                Log.i("Delete Query value", value + "");
                Toast.makeText(context, value + " CallLogs deleted", Toast.LENGTH_SHORT).show();

                Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_PROFILE);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                Intent localBroadcastIntent1 = new Intent(AppConstants.ACTION_LOCAL_BROADCAST);
                localBroadcastIntent1.putExtra(AppConstants.EXTRA_CLEAR_CALL_LOGS,true);
                LocalBroadcastManager myLocalBroadcastManager1 = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager1.sendBroadcast(localBroadcastIntent1);

            }else{

            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private void deleteCallHistoryByName(String name){
        try{
            String  where =   CallLog.Calls.CACHED_NAME + " =?";
            String[] selectionArguments = new String[]{name};
            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,where, selectionArguments);
            if(value > 0){
                Log.i("Delete Query value", value + "");
                Toast.makeText(context, value + " CallLogs deleted", Toast.LENGTH_SHORT).show();

                Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_PROFILE);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                Intent localBroadcastIntent1 = new Intent(AppConstants.ACTION_LOCAL_BROADCAST);
                localBroadcastIntent1.putExtra(AppConstants.EXTRA_CLEAR_CALL_LOGS_FROM_CONTACTS,true);
                LocalBroadcastManager myLocalBroadcastManager1 = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager1.sendBroadcast(localBroadcastIntent1);

                Intent localBroadcastIntent2 = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_CALL_HISTORY_ACTIVITY);
                LocalBroadcastManager myLocalBroadcastManager2 = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager2.sendBroadcast(localBroadcastIntent2);

            }

        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    private void deleteCallHistoryByNumber(String number){
        try{
            String  where =   CallLog.Calls.NUMBER + " =?";
            String[] selectionArguments = new String[]{number};
            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,where, selectionArguments);
            if(value > 0){
                Log.i("Delete Query value", value + "");
                Toast.makeText(context, value + " CallLogs deleted", Toast.LENGTH_SHORT).show();

                Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_PROFILE);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                Intent localBroadcastIntent1 = new Intent(AppConstants.ACTION_LOCAL_BROADCAST);
                localBroadcastIntent1.putExtra(AppConstants.EXTRA_CLEAR_CALL_LOGS_FROM_CONTACTS,true);
                LocalBroadcastManager myLocalBroadcastManager1 = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager1.sendBroadcast(localBroadcastIntent1);

                Intent localBroadcastIntent2 = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_CALL_HISTORY_ACTIVITY);
                LocalBroadcastManager myLocalBroadcastManager2 = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager2.sendBroadcast(localBroadcastIntent2);


            }

        }catch (SecurityException e){
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

    private void showCallConfirmationDialog(final String number) {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        callConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        callConfirmationDialog.dismissDialog();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        try {
                            context.startActivity(intent);

                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(context, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + number + "?");
        callConfirmationDialog.showDialog();

    }


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
}
