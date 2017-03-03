package com.rawalinfocom.rcontact.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rawalinfocom.rcontact.OnlineDataSync;

public class NetworkConnectionReceiver extends BroadcastReceiver {

    OnlineDataSync onlineDataSync;
    Context context;

    public NetworkConnectionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = context;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            onlineDataSync = new OnlineDataSync(context);
        }

    }
}
