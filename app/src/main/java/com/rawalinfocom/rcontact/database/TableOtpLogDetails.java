package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.OtpLog;

import java.util.ArrayList;

/**
 * Created by Monal on 25/10/16.
 * <p>
 * Table operations rc_otp_log_details
 */

public class TableOtpLogDetails {

    private DatabaseHandler databaseHandler;

    public TableOtpLogDetails(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_OTP_LOG_DETAILS = "rc_otp_log_details";


    // Column Names
    private static final String COLUMN_OLD_ID = "old_id";
    private static final String COLUMN_OLD_OTP = "old_otp";
    private static final String COLUMN_OLD_GENERATED_AT = "old_generated_at";
    private static final String COLUMN_OLD_MSP_DELIVERY_TIME = "old_msp_delivery_time";
    private static final String COLUMN_OLD_VALID_UPTO = "old_valid_upto";
    private static final String COLUMN_OLD_VALIDITY_FLAG = "old_validity_flag";
    private static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";

    // Table Create Statements
    static final String CREATE_TABLE_OTP_LOG_DETAILS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_OTP_LOG_DETAILS +
            "(" + COLUMN_OLD_ID + " integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_OLD_OTP + " text NOT NULL," +
            " " + COLUMN_OLD_GENERATED_AT + " datetime NOT NULL," +
            " " + COLUMN_OLD_MSP_DELIVERY_TIME + " datetime," +
            " " + COLUMN_OLD_VALID_UPTO + " datetime NOT NULL," +
            " " + COLUMN_OLD_VALIDITY_FLAG + " integer," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding OTP
    public void addOtp(OtpLog otpLog) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OLD_OTP, otpLog.getOldOtp());
        values.put(COLUMN_OLD_GENERATED_AT, otpLog.getOldGeneratedAt());
        values.put(COLUMN_OLD_MSP_DELIVERY_TIME, otpLog.getOldMspDeliveryTime());
        values.put(COLUMN_OLD_VALID_UPTO, otpLog.getOldValidUpto());
        values.put(COLUMN_OLD_VALIDITY_FLAG, otpLog.getOldValidityFlag());
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
                COLUMN_OLD_OTP, COLUMN_OLD_GENERATED_AT,
                COLUMN_OLD_MSP_DELIVERY_TIME, COLUMN_OLD_VALID_UPTO, COLUMN_OLD_VALIDITY_FLAG,
                COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_OLD_ID + "=?", new String[]{String
                .valueOf(otpId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        OtpLog otpLog = new OtpLog();
        if (cursor != null) {
            otpLog.setOldId(cursor.getString(0));
            otpLog.setOldOtp(cursor.getString(1));
            otpLog.setOldGeneratedAt(cursor.getString(2));
            otpLog.setOldMspDeliveryTime(cursor.getString(3));
            otpLog.setOldValidUpto(cursor.getString(4));
            otpLog.setOldValidityFlag(cursor.getString(5));
            otpLog.setRcProfileMasterPmId(cursor.getString(6));

            cursor.close();
        }

        db.close();

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
                otpLog.setOldOtp(cursor.getString(1));
                otpLog.setOldGeneratedAt(cursor.getString(2));
                otpLog.setOldMspDeliveryTime(cursor.getString(3));
                otpLog.setOldValidUpto(cursor.getString(4));
                otpLog.setOldValidityFlag(cursor.getString(5));
                otpLog.setRcProfileMasterPmId(cursor.getString(6));
                // Adding otp to list
                arrayListOtp.add(otpLog);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return otp list
        return arrayListOtp;
    }

    // Getting Last Otp Details
    public OtpLog getLastOtpDetails() {
        OtpLog otpLog = new OtpLog();
        // Select Query
        String selectQuery = "SELECT * FROM " + TABLE_RC_OTP_LOG_DETAILS + " ORDER BY " +
                COLUMN_OLD_ID + " DESC LIMIT 1;";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows
        if (cursor.moveToFirst()) {
            do {
                otpLog.setOldId(cursor.getString(0));
                otpLog.setOldOtp(cursor.getString(1));
                otpLog.setOldGeneratedAt(cursor.getString(2));
                otpLog.setOldMspDeliveryTime(cursor.getString(3));
                otpLog.setOldValidUpto(cursor.getString(4));
                otpLog.setOldValidityFlag(cursor.getString(5));
                otpLog.setRcProfileMasterPmId(cursor.getString(6));
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return otp
        return otpLog;
    }

    // Updating single otp
    public int updateOtp(OtpLog otpLog) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OLD_ID, otpLog.getOldId());
        values.put(COLUMN_OLD_OTP, otpLog.getOldOtp());
        values.put(COLUMN_OLD_GENERATED_AT, otpLog.getOldGeneratedAt());
        values.put(COLUMN_OLD_MSP_DELIVERY_TIME, otpLog.getOldMspDeliveryTime());
        values.put(COLUMN_OLD_VALID_UPTO, otpLog.getOldValidUpto());
        values.put(COLUMN_OLD_VALIDITY_FLAG, otpLog.getOldValidityFlag());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, otpLog.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_OTP_LOG_DETAILS, values, COLUMN_OLD_ID + " = ?",
                new String[]{String.valueOf(otpLog.getOldId())});

        db.close();

        return isUpdated;
    }

    // Deleting single otp
    public void deleteOtp(OtpLog otpLog) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_OTP_LOG_DETAILS, COLUMN_OLD_ID + " = ?",
                new String[]{String.valueOf(otpLog.getOldId())});
        db.close();
    }

    // Getting otp Count
    public int getOtpCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_OTP_LOG_DETAILS;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();

        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Drop Otp Table
    public void dropOtpTable() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RC_OTP_LOG_DETAILS);
        db.close();
    }
}
