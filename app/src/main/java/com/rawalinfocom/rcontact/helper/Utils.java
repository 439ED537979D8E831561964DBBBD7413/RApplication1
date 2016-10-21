package com.rawalinfocom.rcontact.helper;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;

import java.util.ArrayList;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Class containing some static utility methods.
 */

public class Utils {

    //<editor-fold desc="Check Android OS Version">

    /**
     * Uses static final constants to detect if the device's platform version is Jellybean or
     * later.
     */
    public static boolean hasJellybean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
    //</editor-fold>

    //<editor-fold desc="Network Info">

    /**
     * Check Network Availability
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="SnackBar">
    public static void showErrorSnackBar(Context context, View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color
                .colorSnackBarNegative));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showSuccessSnackbar(Context context, View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color
                .colorSnackBarPositive));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    //</editor-fold>

    public static boolean isArraylistNullOrEmpty(ArrayList arrayList) {
        return !(arrayList != null && arrayList.size() > 0);
    }

}
