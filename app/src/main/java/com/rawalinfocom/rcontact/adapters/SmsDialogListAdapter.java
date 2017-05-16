package com.rawalinfocom.rcontact.adapters;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
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
import com.rawalinfocom.rcontact.model.CallLogType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 29/04/17.
 */

public class SmsDialogListAdapter  extends RecyclerView.Adapter<SmsDialogListAdapter.MaterialViewHolder>{


    private Context context;
    private ArrayList<String> arrayListString;
    MaterialDialog callConfirmationDialog;
    String numberToCall;
    String dialogName;
    String smsThreadId;

    public SmsDialogListAdapter(Context context, ArrayList<String> arrayList, String number, String name,String threadId) {
        this.context = context;
        this.arrayListString = arrayList;
        this.numberToCall = number;
        this.dialogName = name;
        this.smsThreadId =  threadId;
    }

    @Override
    public SmsDialogListAdapter.MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dialog_call_log,
                parent, false);
        return new SmsDialogListAdapter.MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SmsDialogListAdapter.MaterialViewHolder holder, final int position) {
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

                }  else if (value.equalsIgnoreCase(context.getString(R.string.copy_phone_number))) {
                    MaterialDialogClipboard materialDialogClipboard = new MaterialDialogClipboard(context, numberToCall);
                    materialDialogClipboard.showDialog();

                }else if(value.equalsIgnoreCase(context.getString(R.string.delete))){
                    if(!TextUtils.isEmpty(smsThreadId))
                        deleteMessageThreadWise(smsThreadId);

                }else{
//                    Toast.makeText(context, "Please select any one option", Toast.LENGTH_SHORT).show();

                }

                Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_DIALOG_SMS);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

            }
        });

    }


    private void deleteMessageThreadWise(String threadId){
        try {
            String where =  Telephony.Sms.THREAD_ID + "=?";
            String[] selectionArguments = new String[]{threadId};
            int value = context.getContentResolver().delete(Telephony.Sms.CONTENT_URI,where,selectionArguments);
//            int value = context.getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
            if(value>0){
                Toast.makeText(context, value + " message deleted." , Toast.LENGTH_SHORT);

                Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_DELETE_SMS_RECEIVER);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
            }

            /*Uri deleteUri = Uri.parse("content://sms");
            int count = 0;
            Cursor c = context.getContentResolver().query(deleteUri, null, null,
                    null, null);
            while (c.moveToNext()) {
                try {
                    // Delete the SMS
                    String pid = c.getString(0); // Get id;
                    String uri = "content://sms/" + pid;
                    count = context.getContentResolver().delete(Uri.parse(uri),
                            null, null);
                } catch (Exception e) {
                }
            }
            if(count>0){

            }*/

        } catch (Exception ex) {
            ex.printStackTrace();
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

}
