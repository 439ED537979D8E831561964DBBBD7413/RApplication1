package com.rawalinfocom.rcontact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.rawalinfocom.rcontact.asynctasks.AsyncGetDeviceToken;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.UserProfile;

public class BaseActivity extends AppCompatActivity {

    public DatabaseHandler databaseHandler;
    public UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHandler = new DatabaseHandler(this);

        if (Utils.getStringPreference(this, AppConstants.PREF_DEVICE_TOKEN_ID, "").equals(""))
            new AsyncGetDeviceToken(this).execute();
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isTaskRoot()) {
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        }
    }

    public void startActivityIntent(Context packageContext, Class cls, Bundle extras) {
        Intent intent = new Intent(packageContext, cls);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings
                .Secure.ANDROID_ID);
    }

    public String getDeviceTokenId() {
        return Utils.getStringPreference(this, AppConstants.PREF_DEVICE_TOKEN_ID, "");
    }

    public String getUserPmId() {
        return Utils.getStringPreference(this, AppConstants.PREF_USER_PM_ID, "0");
    }

    public UserProfile getUserProfile() {
        return (UserProfile) Utils.getObjectPreference(this, AppConstants.PREF_REGS_USER_OBJECT,
                UserProfile.class);
    }
}
