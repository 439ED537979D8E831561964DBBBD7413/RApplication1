package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;

/**
 * Created by Monal on 20/10/16.
 * <p>
 * Base class for maintaining global application state.
 */

public class RContactApplication extends Application {

    ArrayList<Object> arrayListAllPhoneBookContacts;
    ArrayList<String> arrayListAllContactHeaders;
    ArrayList<Object> arrayListFavPhoneBookContacts;
    ArrayList<String> arrayListFavContactHeaders;

    @Override
    public void onCreate() {
        super.onCreate();

        // Fabric Initialization
//        Fabric.with(this, new Crashlytics());

        arrayListAllPhoneBookContacts = new ArrayList<>();
        arrayListAllContactHeaders = new ArrayList<>();
        arrayListFavPhoneBookContacts = new ArrayList<>();
        arrayListFavContactHeaders = new ArrayList<>();

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
}
