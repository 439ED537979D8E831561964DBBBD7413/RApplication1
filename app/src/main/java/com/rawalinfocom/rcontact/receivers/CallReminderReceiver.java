package com.rawalinfocom.rcontact.receivers;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableCallReminder;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Aniruddh on 07/12/17.
 */

public class CallReminderReceiver extends BroadcastReceiver {

    Context mContext;
    String numberToCall;
    Dialog reminderDialog;
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        mContext = context;

        // Put here YOUR code.
        String number = intent.getStringExtra(AppConstants.EXTRA_CALL_REMINDER_NUMBER);
        Long time = intent.getLongExtra(AppConstants.EXTRA_CALL_REMINDER_TIME, 0);
        numberToCall =  number;
        Date date1 = new Date(time);
        String setTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date1);
        String name = getNameFromNumber(number);
        if(StringUtils.isEmpty(name))
            name =  number;

        showNotification(name,setTime);
        initializeReminderDialog(name,setTime);
        wl.release();

    }


    private void showNotification(String name, String time) {

        // prepare intent which is triggered if the
        // notification is selected

//        Intent intent = new Intent(this, NotificationReceiver.class);
//        // use System.currentTimeMillis() to have a unique ID for the pending intent
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);


        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(mContext)
                .setContentTitle("Call Reminder")
                .setContentText("You need to call " + name + " at " + time)
                .setSmallIcon(R.drawable.ic_notification_flat)
                .setVibrate(new long[]{0,200,0})
//                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
//                .addAction(R.drawable.ico_call_svg, "Call", pIntent).build();
//                .addAction(R.drawable.icon, "More", pIntent)
//                .addAction(R.drawable.icon, "And more", pIntent).build();


        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, n);
        }

    }


    private void initializeReminderDialog(final String name, String time) {
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(500);
        }

        reminderDialog = new Dialog(mContext);
        reminderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reminderDialog.setContentView(R.layout.dialog_call_reminder);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reminderDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            reminderDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        reminderDialog.setCancelable(true);
        reminderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        layoutParams.copyFrom(reminderDialog.getWindow().getAttributes());
        layoutParams.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        reminderDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textViewTime = (TextView) reminderDialog.findViewById(R.id.text_time);
        TextView textName = (TextView) reminderDialog.findViewById(R.id.text_name);
        LinearLayout llCall = (LinearLayout) reminderDialog.findViewById(R.id.ll_call);
        LinearLayout llSnooze = (LinearLayout) reminderDialog.findViewById(R.id.ll_snooze);
        LinearLayout llCancel = (LinearLayout) reminderDialog.findViewById(R.id.ll_cancel);

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderDialog.dismiss();
            }
        });

        llSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCallReminderPopUp();
            }
        });

        llCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String unicodeNumber = numberToCall.replace("*", Uri.encode("*")).replace("#", Uri.encode("#"));
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + unicodeNumber));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    reminderDialog.dismiss();
                    mContext.startActivity(intent);

                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
        });

        textViewTime.setText(time);
        textName.setText(name);

        if(mContext != null )
            reminderDialog.show();
    }


    private void showCallReminderPopUp() {
        ArrayList<String> arrayListCallReminderOption;
        String number =  numberToCall;
        TableCallReminder tableCallReminder = new TableCallReminder(new DatabaseHandler(mContext));
        if(number.contains("("))
            number = number.replace("(","");
        if(number.contains(")"))
            number = number.replace(")","");
        if(number.contains("-"))
            number =  number.replace("-","");
        if(number.contains(" "))
            number =  number.replace(" ","");

        number = number.trim();
        String formattedNumber =  Utils.getFormattedNumber(mContext,number);
        String time =  tableCallReminder.getReminderTimeFromNumber(formattedNumber);
        Long callReminderTime = 0L;
        if(!StringUtils.isEmpty(time))
            callReminderTime =  Long.parseLong(time);

        if (callReminderTime > 0) {
            Date date1 = new Date(callReminderTime);
            String setTime = new SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.getDefault()).format(date1);
            arrayListCallReminderOption = new ArrayList<>(Arrays.asList(mContext.getString(R.string.min15),
                    mContext.getString(R.string.hour1), mContext.getString(R.string.hour2), mContext.getString(R.string.hour6),setTime + "     Edit"));
            MaterialListDialog materialListDialog = new MaterialListDialog(mContext, arrayListCallReminderOption,
                    formattedNumber, 0, "", "", "");
            materialListDialog.setDialogTitle(mContext.getString(R.string.call_reminder));
            reminderDialog.dismiss();
            materialListDialog.showDialog();
        } else {
            arrayListCallReminderOption = new ArrayList<>(Arrays.asList(mContext.getString(R.string.min15),
                    mContext.getString(R.string.hour1), mContext.getString(R.string.hour2), mContext.getString(R.string.hour6),
                    mContext.getString(R.string.setDateAndTime)));
            MaterialListDialog materialListDialog = new MaterialListDialog(mContext, arrayListCallReminderOption,
                    formattedNumber, 0, "", "", "");
            materialListDialog.setDialogTitle(mContext.getString(R.string.call_reminder).toUpperCase());
            reminderDialog.dismiss();
            materialListDialog.showDialog();
        }
    }
    private String getNameFromNumber(String phoneNumber) {
        String contactName = "";
        try {

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null,
                    null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                cursor.close();
            } else {
                if (!cursor.isClosed())
                    cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactName;
    }
}
