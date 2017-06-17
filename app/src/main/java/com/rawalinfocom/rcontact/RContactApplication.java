package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.SmsDataType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Monal on 20/10/16.
 * <p>
 * Base class for maintaining global application state.
 */

public class RContactApplication extends Application {

    public static final int FAVOURITE_UNMODIFIED = 0;
    public static final int FAVOURITE_ADDED = 1;
    public static final int FAVOURITE_REMOVED = 2;


    ArrayList<Object> arrayListAllPhoneBookContacts;
    ArrayList<String> arrayListAllContactHeaders;

    ArrayList<Object> arrayListFavPhoneBookContacts;
    ArrayList<String> arrayListFavContactHeaders;

    int favouriteStatus;

    ArrayList<Object> arrayListObjectCallLogs;
    ArrayList<String> arrayListcallLogsHeaders;
    ArrayList<CallLogType> arrayListCallLogType;

    ArrayList<SmsDataType> arrayListSmsLogType;
    ArrayList<Object> arrayListObjectSmsLogs;
    ArrayList<String> arrayListSmsLogsHeaders;


    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("KeyHash:", "We are here");
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.rawalinfocom.rcontact",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
//        Log.d("KeyHash:", "We are here end");
//         Fabric Initialization
        Fabric.with(this, new Crashlytics());

        arrayListAllPhoneBookContacts = new ArrayList<>();
        arrayListAllContactHeaders = new ArrayList<>();
        arrayListFavPhoneBookContacts = new ArrayList<>();
        arrayListFavContactHeaders = new ArrayList<>();

        arrayListcallLogsHeaders = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();
        arrayListCallLogType = new ArrayList<>();

        arrayListObjectSmsLogs = new ArrayList<>();
        arrayListSmsLogsHeaders = new ArrayList<>();

        // Facebook Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public ArrayList<Object> getArrayListAllPhoneBookContacts() {
        return arrayListAllPhoneBookContacts;
    }

    public void setArrayListAllPhoneBookContacts(ArrayList<Object> arrayListAllPhoneBookContacts) {
        this.arrayListAllPhoneBookContacts = arrayListAllPhoneBookContacts;
    }

    public ArrayList<String> getArrayListAllContactHeaders() {
        return arrayListAllContactHeaders;
    }

    public void setArrayListAllContactHeaders(ArrayList<String> arrayListAllContactHeaders) {
        this.arrayListAllContactHeaders = arrayListAllContactHeaders;
    }

    public ArrayList<Object> getArrayListFavPhoneBookContacts() {
        return arrayListFavPhoneBookContacts;
    }

    public void setArrayListFavPhoneBookContacts(ArrayList<Object> arrayListFavPhoneBookContacts) {
        this.arrayListFavPhoneBookContacts = arrayListFavPhoneBookContacts;
    }

    public ArrayList<String> getArrayListFavContactHeaders() {
        return arrayListFavContactHeaders;
    }

    public void setArrayListFavContactHeaders(ArrayList<String> arrayListFavContactHeaders) {
        this.arrayListFavContactHeaders = arrayListFavContactHeaders;
    }

 /*   public boolean isFavouriteModified() {
        return isFavouriteModified;
    }

    public void setFavouriteModified(boolean favouriteModified) {
        isFavouriteModified = favouriteModified;
    }*/

    public int getFavouriteStatus() {
        return favouriteStatus;
    }

    public void setFavouriteStatus(int favouriteStatus) {
        this.favouriteStatus = favouriteStatus;
    }

    public ArrayList<Object> getArrayListObjectCallLogs() {
        return arrayListObjectCallLogs;
    }

    public void setArrayListObjectCallLogs(ArrayList<Object> arrayListObjectCallLogs) {
        this.arrayListObjectCallLogs = arrayListObjectCallLogs;
    }

    public ArrayList<String> getArrayListcallLogsHeaders() {
        return arrayListcallLogsHeaders;
    }

    public void setArrayListcallLogsHeaders(ArrayList<String> arrayListcallLogsHeaders) {
        this.arrayListcallLogsHeaders = arrayListcallLogsHeaders;
    }

    public ArrayList<CallLogType> getArrayListCallLogType() {
        return arrayListCallLogType;
    }

    public void setArrayListCallLogType(ArrayList<CallLogType> arrayListCallLogType) {
        this.arrayListCallLogType = arrayListCallLogType;
    }

    public ArrayList<SmsDataType> getArrayListSmsLogType() {
        return arrayListSmsLogType;
    }

    public void setArrayListSmsLogType(ArrayList<SmsDataType> arrayListSmsLogType) {
        this.arrayListSmsLogType = arrayListSmsLogType;
    }

    public ArrayList<Object> getArrayListObjectSmsLogs() {
        return arrayListObjectSmsLogs;
    }

    public void setArrayListObjectSmsLogs(ArrayList<Object> arrayListObjectSmsLogs) {
        this.arrayListObjectSmsLogs = arrayListObjectSmsLogs;
    }

    public ArrayList<String> getArrayListSmsLogsHeaders() {
        return arrayListSmsLogsHeaders;
    }

    public void setArrayListSmsLogsHeaders(ArrayList<String> arrayListSmsLogsHeaders) {
        this.arrayListSmsLogsHeaders = arrayListSmsLogsHeaders;
    }
}
