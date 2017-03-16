package com.rawalinfocom.rcontact.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
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
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.MaterialDialogClipboard;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 24/02/17.
 */

public class CallLogDialogListAdapter extends RecyclerView.Adapter<CallLogDialogListAdapter.MaterialViewHolder> {


    private Context context;
    private ArrayList<String> arrayListString;
    private String dialogTitle;
    MaterialDialog callConfirmationDialog;
    String numberToCall;
    String dialogName;
    Class classToReceive;
    long callLogDateToDelete;

    public CallLogDialogListAdapter(Context context, ArrayList<String> arrayList, String number, long date) {
        this.context = context;
        this.arrayListString = arrayList;
        this.numberToCall = number;
        this.callLogDateToDelete = date;
//        this.dialogName = dialogTitle;
    }

    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dialog_call_log,
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
                    if (!TextUtils.isEmpty(numberToCall))
                        showCallConfirmationDialog(numberToCall);
                }

                if (value.equalsIgnoreCase(context.getString(R.string.add_to_contact))) {

                    Utils.addToContact(context, numberToCall);

                } else if (value.equalsIgnoreCase(context.getString(R.string.add_to_existing_contact))) {
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

                } else if (value.equalsIgnoreCase(context.getString(R.string.remove_from_call_log))) {
                    deleteCallLogByNumber(numberToCall);

                } else if (value.equalsIgnoreCase(context.getString(R.string.copy_phone_number))) {
                    MaterialDialogClipboard materialDialogClipboard = new MaterialDialogClipboard(context, numberToCall);
                    materialDialogClipboard.showDialog();

                } else if (value.equalsIgnoreCase(context.getString(R.string.block))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.call_reminder))) {

                } else {

                    Toast.makeText(context, "Please select any one option", Toast.LENGTH_SHORT).show();
                }



            }
        });

    }

    private void deleteCallLogByNumber(String number) {
        try {

            long dateToCompare =0;
            long nextDate = 0;
            Date objDate1 = new Date(callLogDateToDelete);
            String dateToDelete = new SimpleDateFormat("dd/MM/yyyy").format(objDate1);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date TomoDate;
            TomoDate = cal.getTime();
            String tomorrowDate = new SimpleDateFormat("dd/MM/yyyy").format(TomoDate);

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

                Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

            }else{

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
