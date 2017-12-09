package com.rawalinfocom.rcontact.adapters;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableCallReminder;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.MaterialDialogClipboard;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.instagram.util.StringUtil;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.receivers.CallReminderReceiver;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 24/02/17.
 */

public class CallLogDialogListAdapter extends RecyclerView.Adapter<CallLogDialogListAdapter
        .MaterialViewHolder> {


    private Context context;
    private ArrayList<String> arrayListString;
    private String dialogTitle;
    MaterialDialog callConfirmationDialog;
    String numberToCall;
    String dialogName;
    Class classToReceive;
    long callLogDateToDelete;
    String uniqueRowId;
    boolean isblocked = false;
    String key;
    DatabaseHandler databaseHandler;

    public CallLogDialogListAdapter(Context context, ArrayList<String> arrayList, String number,
                                    long date, String name,
                                    String uniqueRowId, String key) {
        this.context = context;
        this.arrayListString = arrayList;
        this.numberToCall = number;
        this.callLogDateToDelete = date;
        this.dialogName = name;
        this.uniqueRowId = uniqueRowId;
        this.key = key;
        databaseHandler = new DatabaseHandler(context);
    }

    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .list_item_dialog_call_log,
                parent, false);
        return new MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, final int position) {
        final String value = arrayListString.get(position);
        holder.textItemValue.setText(value);

        holder.rippleRow.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (position == 0) {
                    if (!TextUtils.isEmpty(numberToCall)) {
                        numberToCall = Utils.getFormattedNumber(context, numberToCall);
                        if (!value.equalsIgnoreCase(context.getString(R.string.min15)))
                            Utils.callIntent(context, numberToCall);
//                        showCallConfirmationDialog(numberToCall);
                    }
                }

                if (value.equalsIgnoreCase(context.getString(R.string.add_to_contact))) {

                    Utils.addToContact(context, numberToCall);

                } else if (value.equalsIgnoreCase(context.getString(R.string
                        .add_to_existing_contact))) {
                    Utils.addToExistingContact(context, numberToCall);

                }/*else if(value.equalsIgnoreCase(context.getString(R.string.show_call_history))){
                    Pattern numberPat = Pattern.compile("\\d+");
                    Matcher matcher1 = numberPat.matcher(dialogName);
                    if (matcher1.find()) {
                        // number
                        Intent intent = new Intent(context, ProfileDetailActivity.class);
                        intent.putExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, true);
                        intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER, dialogName);
                        context.startActivity(intent);
                    } else {
                        // name
                        Intent intent = new Intent(context, ProfileDetailActivity.class);
                        intent.putExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, true);
                        intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NAME, dialogName);
                        context.startActivity(intent);
                    }


                }*/ else if (value.equalsIgnoreCase(context.getString(R.string.send_sms))) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + numberToCall));
                    context.startActivity(smsIntent);

                } else if (value.equalsIgnoreCase(context.getString(R.string
                        .remove_from_call_log))) {
                    deleteCallLogByNumber(numberToCall);

                } else if (value.equalsIgnoreCase(context.getString(R.string.copy_phone_number))) {
                    MaterialDialogClipboard materialDialogClipboard = new MaterialDialogClipboard
                            (context, numberToCall);
                    materialDialogClipboard.showDialog();

                } else if (value.equalsIgnoreCase(context.getString(R.string.block))) {
                    ArrayList<CallLogType> listToBlock = new ArrayList<CallLogType>();
                    HashMap<String, ArrayList<CallLogType>> listHashMap = new HashMap<String,
                            ArrayList<CallLogType>>();
                    String uniqueContactId = "";
                    if (!TextUtils.isEmpty(dialogName)) {
                        listToBlock = getNumbersFromName(dialogName);
                        Log.i("block list size =", listToBlock.size() + "");
                        for (int i = 0; i < listToBlock.size(); i++) {
                            CallLogType callLogType = listToBlock.get(i);
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
                                if (tempNumber.equalsIgnoreCase(numberToCall)) {
                                    blockedNumber = tempNumber;
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

                } else if (value.equalsIgnoreCase(context.getString(R.string.call_reminder))) {
                    showCallReminderPopUp();
                } else {

//                    Toast.makeText(context, "Please select any one option", Toast.LENGTH_SHORT)
// .show();
                }

                long timeToSet = 0;
                TableCallReminder tableCallReminder = new TableCallReminder(databaseHandler);
                if (value.equalsIgnoreCase(context.getString(R.string.min15))) {
                    timeToSet = System.currentTimeMillis() + (15 * 60 * 1000);
                    Utils.setLongPreference(context, AppConstants.PREF_CALL_REMINDER, timeToSet);
                    String number = tableCallReminder.getNumberFromTable(numberToCall);
                    if (StringUtils.isEmpty(number)) {
                        tableCallReminder.addReminderToDB(numberToCall, String.valueOf(timeToSet));
                    } else {
                        tableCallReminder.updateReminderTime(numberToCall, String.valueOf(timeToSet));
                    }
                    setAlarm();

                } else if (value.equalsIgnoreCase(context.getString(R.string.hour1))) {
                    timeToSet = 0;
                    timeToSet = System.currentTimeMillis() + (60 * 60 * 1000);
                    Utils.setLongPreference(context, AppConstants.PREF_CALL_REMINDER, timeToSet);
                    String number = tableCallReminder.getNumberFromTable(numberToCall);
                    if (StringUtils.isEmpty(number)) {
                        tableCallReminder.addReminderToDB(numberToCall, String.valueOf(timeToSet));
                    } else {
                        tableCallReminder.updateReminderTime(numberToCall, String.valueOf(timeToSet));
                    }
                    setAlarm();

                } else if (value.equalsIgnoreCase(context.getString(R.string.hour2))) {
                    timeToSet = 0;
                    timeToSet = System.currentTimeMillis() + (2 * 60 * 60 * 1000);
                    Utils.setLongPreference(context, AppConstants.PREF_CALL_REMINDER, timeToSet);
                    String number = tableCallReminder.getNumberFromTable(numberToCall);
                    if (StringUtils.isEmpty(number)) {
                        tableCallReminder.addReminderToDB(numberToCall, String.valueOf(timeToSet));
                    } else {
                        tableCallReminder.updateReminderTime(numberToCall, String.valueOf(timeToSet));
                    }
                    setAlarm();

                } else if (value.equalsIgnoreCase(context.getString(R.string.hour6))) {
                    timeToSet = 0;
                    timeToSet = System.currentTimeMillis() + (6 * 60 * 60 * 1000);
                    Utils.setLongPreference(context, AppConstants.PREF_CALL_REMINDER, timeToSet);
                    String number = tableCallReminder.getNumberFromTable(numberToCall);
                    if (StringUtils.isEmpty(number)) {
                        tableCallReminder.addReminderToDB(numberToCall, String.valueOf(timeToSet));
                    } else {
                        tableCallReminder.updateReminderTime(numberToCall, String.valueOf(timeToSet));
                    }
                    setAlarm();

                } else if (value.equalsIgnoreCase(context.getString(R.string.setDateAndTime))) {
                    long datePickerTime = 0;
                    //Open time picker
                    showDateTimePicker();
                } else {
                    long datePickerTimeEdit = Utils.getLongPreference(context, AppConstants.PREF_CALL_REMINDER, 0);
                    if (datePickerTimeEdit > 0) {
                        datePickerTimeEdit = 0;
                        Utils.setLongPreference(context, AppConstants.PREF_CALL_REMINDER, 0);
                        // open time picker
                        showDateTimePicker();
                    }
                }

//                setAlarm();
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

    private void showCallReminderPopUp() {
        ArrayList<String> arrayListCallReminderOption;
        TableCallReminder tableCallReminder = new TableCallReminder(new DatabaseHandler(context));
//        Long callReminderTime = Utils.getLongPreference(context, AppConstants.PREF_CALL_REMINDER, 0);
        String number =  numberToCall;
        if(number.contains("("))
            number = number.replace("(","");
        if(number.contains(")"))
            number = number.replace(")","");
        if(number.contains("-"))
            number =  number.replace("-","");
        if(number.contains(" "))
            number =  number.replace(" ","");

        number = number.trim();
        String formattedNumber =  Utils.getFormattedNumber(context,number);
        String time =  tableCallReminder.getReminderTimeFromNumber(formattedNumber);
        Long callReminderTime = 0L;
        if(!StringUtils.isEmpty(time))
            callReminderTime =  Long.parseLong(time);

        if (callReminderTime > 0) {
            Date date1 = new Date(callReminderTime);
            String setTime = new SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.getDefault()).format(date1);
            arrayListCallReminderOption = new ArrayList<>(Arrays.asList(context.getString(R.string.min15),
                    context.getString(R.string.hour1), context.getString(R.string.hour2), context.getString(R.string.hour6),setTime + "     Edit"));
            MaterialListDialog materialListDialog = new MaterialListDialog(context, arrayListCallReminderOption,
                    formattedNumber, 0, "", "", "");
            materialListDialog.setDialogTitle(context.getString(R.string.call_reminder));
            materialListDialog.showDialog();
        } else {
            arrayListCallReminderOption = new ArrayList<>(Arrays.asList(context.getString(R.string.min15),
                    context.getString(R.string.hour1), context.getString(R.string.hour2), context.getString(R.string.hour6),
                    context.getString(R.string.setDateAndTime)));
            MaterialListDialog materialListDialog = new MaterialListDialog(context, arrayListCallReminderOption,
                    formattedNumber, 0, "", "", "");
            materialListDialog.setDialogTitle(context.getString(R.string.call_reminder).toUpperCase());
            materialListDialog.showDialog();
        }
    }

    Calendar date;
    long selectedDateAndTime;

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        final TableCallReminder tableCallReminder = new TableCallReminder(databaseHandler);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Log.v("call_reminder", "The choosen one " + date.getTime());
                        String actualTime = date.getTime().toString();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                        try {
                            Date mDate = sdf.parse(actualTime);
                            long timeInMilliseconds = mDate.getTime();
                            System.out.println("Date in milli :: " + timeInMilliseconds);
                            selectedDateAndTime = timeInMilliseconds;
                            Utils.setLongPreference(context, AppConstants.PREF_CALL_REMINDER, selectedDateAndTime);
                            String number = tableCallReminder.getNumberFromTable(numberToCall);
                            if (StringUtils.isEmpty(number)) {
                                tableCallReminder.addReminderToDB(numberToCall, String.valueOf(selectedDateAndTime));
                            } else {
                                tableCallReminder.updateReminderTime(numberToCall, String.valueOf(selectedDateAndTime));
                            }
                            setAlarm();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                } else {
                    timePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                }

                timePickerDialog.show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            datePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            datePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        datePickerDialog.show();
    }

    private void setAlarm() {
        TableCallReminder tableCallReminder = new TableCallReminder(databaseHandler);
        ArrayList<String> listOfAllCallReminderTime = tableCallReminder.getAllTimeforCallReminder();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CallReminderReceiver.class);
        if (listOfAllCallReminderTime.size() > 0) {
            for (int i = 0; i < listOfAllCallReminderTime.size(); i++) {
                String time = listOfAllCallReminderTime.get(i);
                Long timeToTrigger = Long.parseLong(time);
                if (timeToTrigger >= System.currentTimeMillis()) {
                    intent.putExtra(AppConstants.EXTRA_CALL_REMINDER_NUMBER, tableCallReminder.getNumberFromTime(time));
                    intent.putExtra(AppConstants.EXTRA_CALL_REMINDER_TIME, timeToTrigger);
                    PendingIntent pi = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (am != null) {
                        am.setExact(AlarmManager.RTC_WAKEUP, timeToTrigger, pi);
                    }
                } else {
                    listOfAllCallReminderTime.remove(time);
                    tableCallReminder.deleteReminderDetailsbyTime(time);
                    cancelAlarm(context);
                }
            }
        }
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, CallReminderReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(sender);
        }
    }

    private void deleteCallLogByNumber(String number) {
        try {

            long dateToCompare = 0;
            long nextDate = 0;
            Date objDate1 = new Date(callLogDateToDelete);
            String dateToDelete = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format
                    (objDate1);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date TomoDate;
            TomoDate = cal.getTime();
            String tomorrowDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format
                    (TomoDate);

            long callLogDate = callLogHistory(number);
            Date date = new Date(callLogDate);
            String dateToCompare1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(date);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date startDate = sdf.parse(dateToDelete);
                dateToCompare = startDate.getTime();

                Date tomdate = sdf.parse(tomorrowDate);
                nextDate = tomdate.getTime();

            } catch (Exception e) {
                e.printStackTrace();
            }

            String where = CallLog.Calls.NUMBER + " =?"
                    + " AND " + android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?"
                    + " AND " + CallLog.Calls._ID + "=?";
            String[] selectionArguments = new String[]{number, String.valueOf(dateToCompare),
                    String.valueOf(nextDate), uniqueRowId};
            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, where,
                    selectionArguments);

            /*int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog
            .Calls.NUMBER + " =?"
                    + " AND " + dateToCompare + "=?", new String[]{number, dateToDelete});
            Log.i("Delete Query value", value + "");*/

            /*String where = CallLog.Calls.NUMBER + " =?"
                    + " AND " + android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?";
            String[] selectionArguments = new String[]{number, String.valueOf(dateToCompare),
            String.valueOf(nextDate)};
            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, where,
            selectionArguments);*/

           /* String where = CallLog.Calls.NUMBER + " =?" + " AND " + CallLog.Calls.DATE + " =?";
            String[] selectionArguments = new String[]{number, String.valueOf(callLogDateToDelete)};
            int value = context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, where,
            selectionArguments);*/

            if (value > 0) {
                Log.i("Delete Query value", value + "");
                Toast.makeText(context, value + " " + context.getString(R.string.call_logs_deleted),
                        Toast.LENGTH_SHORT).show();

                Intent localBroadcastIntent = new Intent(AppConstants
                        .ACTION_LOCAL_BROADCAST_REMOVE_CALL_LOGS);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_REMOVE_CALL_LOGS, true);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                        (context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
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
