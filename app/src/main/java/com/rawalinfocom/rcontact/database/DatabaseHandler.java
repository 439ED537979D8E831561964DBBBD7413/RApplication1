package com.rawalinfocom.rcontact.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Monal on 21/10/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private final String LOG_TAG = "DatabaseHelper";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "RContact";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(TableCountryMaster.CREATE_TABLE_RC_COUNTRY_MASTER);
        db.execSQL(TableOtpLogDetails.CREATE_TABLE_OTP_LOG_DETAILS);
        db.execSQL(TableProfileMaster.CREATE_TABLE_RC_PROFILE_MASTER);
        db.execSQL(TableEmailMaster.CREATE_TABLE_RC_EMAIL_MASTER);
        db.execSQL(TableMobileMaster.CREATE_TABLE_RC_MOBILE_NUMBER_MASTER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TableCountryMaster.CREATE_TABLE_RC_COUNTRY_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableOtpLogDetails.CREATE_TABLE_OTP_LOG_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TableProfileMaster.CREATE_TABLE_RC_PROFILE_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableEmailMaster.CREATE_TABLE_RC_EMAIL_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableMobileMaster
                .CREATE_TABLE_RC_MOBILE_NUMBER_MASTER);

        // create new tables
        onCreate(db);
    }
}
