package com.rawalinfocom.rcontact.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Aniruddh on 07/12/17.
 */

public class CallReminderReceiver extends BroadcastReceiver {

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        mContext = context;

        // Put here YOUR code.
        String number = intent.getStringExtra(AppConstants.EXTRA_CALL_REMINDER_NUMBER);
        Long time = intent.getLongExtra(AppConstants.EXTRA_CALL_REMINDER_TIME, 0);
        Date date1 = new Date(time);
        String setTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date1);
        String name = getNameFromNumber(number);
        showNotification(name,setTime);
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
        Toast.makeText(context, setTime + " with " + name, Toast.LENGTH_LONG).show(); // For example

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
