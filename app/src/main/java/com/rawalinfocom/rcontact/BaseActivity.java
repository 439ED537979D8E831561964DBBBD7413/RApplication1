package com.rawalinfocom.rcontact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rawalinfocom.rcontact.database.DatabaseHandler;

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
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }

    public void startActivityIntent(Context packageContext, Class cls) {
        Intent intent = new Intent(packageContext, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }
}
