package com.rawalinfocom.rcontact.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.rawalinfocom.rcontact.OnlineDataSync;

public class NetworkConnectionReceiver extends BroadcastReceiver {

    OnlineDataSync onlineDataSync;
    Activity activity;

    public NetworkConnectionReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.activity = (Activity) context;
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    onlineDataSync = new OnlineDataSync(activity);
                }
            });

        }
    }
}