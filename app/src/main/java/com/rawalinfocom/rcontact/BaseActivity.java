package com.rawalinfocom.rcontact;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHandler = new DatabaseHandler(this);
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

    public String getDeviceTokenId() {
        return Utils.getStringPreference(this, AppConstants.PREF_DEVICE_TOKEN_ID, "");
    }


}
