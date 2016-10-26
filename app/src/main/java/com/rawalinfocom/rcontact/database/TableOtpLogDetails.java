package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.OtpLog;

import java.util.ArrayList;

/**
 * Created by Monal on 25/10/16.
 */

public class TableOtpLogDetails {

    Context context;
    DatabaseHandler databaseHandler;

    public TableOtpLogDetails(Context context, DatabaseHandler databaseHandler) {
        this.context = context;
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_OTP_LOG_DETAILS = "rc_otp_log_details";


    // Column Names
    private static final String COLUMN_OLD_ID = "old_id";
    private static final String COLUMN_OLD_OTP_STRING = "old_otp_string";
    private static final String COLUMN_OLD_VALID_UPTO = "old_valid_upto";
    private static final String COLUMN_OLD_DELIVERED_TIME = "old_delivered_time";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";

    // Table Create Statements
    public static final String CREATE_TABLE_OTP_LOG_DETAILS = "CREATE TABLE " +
            TABLE_RC_OTP_LOG_DETAILS + " (" +
            " " + COLUMN_OLD_ID + " integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_OLD_OTP_STRING + " text NOT NULL," +
            " " + COLUMN_OLD_VALID_UPTO + " datetime NOT NULL," +
            " " + COLUMN_OLD_DELIVERED_TIME + " datetime," +
            " " + COLUMN_CREATED_AT + " datetime NOT NULL," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding OTP
    public void addOtp(OtpLog otpLog) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OLD_ID, otpLog.getOldId());
        values.put(COLUMN_OLD_OTP_STRING, otpLog.getOldOtpString());
        values.put(COLUMN_OLD_VALID_UPTO, otpLog.getOldValidUpto());
        values.put(COLUMN_OLD_DELIVERED_TIME, otpLog.getOldDeliveredTime());
        values.put(COLUMN_CREATED_AT, otpLog.getCreatedAt());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, otpLog.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_OTP_LOG_DETAILS, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting Otp
    public OtpLog getOtp(int otpId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_OTP_LOG_DETAILS, new String[]{COLUMN_OLD_ID,
                        COLUMN_OLD_OTP_STRING, COLUMN_OLD_VALID_UPTO,
                        COLUMN_OLD_DELIVERED_TIME, COLUMN_CREATED_AT,
                        COLUMN_RC_PROFILE_MASTER_PM_ID},
                COLUMN_OLD_ID + "=?", new String[]{String.valueOf(otpId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        OtpLog otpLog = new OtpLog();
        if (cursor != null) {
            otpLog.setOldId(cursor.getString(0));
            otpLog.setOldOtpString(cursor.getString(1));
            otpLog.setOldValidUpto(cursor.getString(2));
            otpLog.setOldDeliveredTime(cursor.getString(3));
            otpLog.setCreatedAt(cursor.getString(4));
            otpLog.setRcProfileMasterPmId(cursor.getString(5));
        }
        // return otpLog
        return otpLog;
    }

    // Getting All Otp
    public ArrayList<OtpLog> getAllOtp() {
        ArrayList<OtpLog> arrayListOtp = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_OTP_LOG_DETAILS;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OtpLog otpLog = new OtpLog();
                otpLog.setOldId(cursor.getString(0));
                otpLog.setOldOtpString(cursor.getString(1));
                otpLog.setOldValidUpto(cursor.getString(2));
                otpLog.setOldDeliveredTime(cursor.getString(3));
                otpLog.setCreatedAt(cursor.getString(4));
                otpLog.setRcProfileMasterPmId(cursor.getString(5));
                // Adding contact to list
                arrayListOtp.add(otpLog);
            } while (cursor.moveToNext());
        }

        // return otp list
        return arrayListOtp;
    }

    // Updating single otp
    public int updateOtp(OtpLog otpLog) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OLD_ID, otpLog.getOldId());
        values.put(COLUMN_OLD_OTP_STRING, otpLog.getOldOtpString());
        values.put(COLUMN_OLD_VALID_UPTO, otpLog.getOldValidUpto());
        values.put(COLUMN_OLD_DELIVERED_TIME, otpLog.getOldDeliveredTime());
        values.put(COLUMN_CREATED_AT, otpLog.getCreatedAt());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, otpLog.getRcProfileMasterPmId());

        // updating row
        return db.update(TABLE_RC_OTP_LOG_DETAILS, values, COLUMN_OLD_ID + " = ?",
                new String[]{String.valueOf(otpLog.getOldId())});
    }

    // Deleting single otp
    public void deleteOtp(OtpLog otpLog) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_OTP_LOG_DETAILS, COLUMN_OLD_ID + " = ?",
                new String[]{String.valueOf(otpLog.getOldId())});
        db.close();
    }

    // Drop Otp Table
    public void dropOtpTable() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RC_OTP_LOG_DETAILS);
    }
}
