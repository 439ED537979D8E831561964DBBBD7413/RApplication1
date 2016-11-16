package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.MobileNumber;

import java.util.ArrayList;

/**
 * Created by Monal on 15/11/16.
 */

public class TableMobileMaster {

    Context context;
    DatabaseHandler databaseHandler;

    public TableMobileMaster(Context context, DatabaseHandler databaseHandler) {
        this.context = context;
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_MOBILE_NUMBER_MASTER = "rc_mobile_number_master";

    // Column Names
    private static final String COLUMN_MNM_ID = "mnm_id";
    private static final String COLUMN_MNM_MOBILE_NUMBER = "mnm_mobile_number";
    private static final String COLUMN_MNM_NUMBER_TYPE = "mnm_number_type";
    private static final String COLUMN_MNM_CUSTOM_TYPE = "mnm_custom_type";
    private static final String COLUMN_MNM_IS_PRIMARY = "mnm_is_primary";
    private static final String COLUMN_MNM_NUMBER_PRIVACY = "mnm_number_privacy";
    private static final String COLUMN_MNM_IS_DEFAULT = "mnm_is_default";
    private static final String COLUMN_MNM_IS_VERIFIED = "mnm_is_verified";
    private static final String COLUMN_MNM_MOBILE_SERVICE_PROVIDER = "mnm_mobile_service_provider";
    private static final String COLUMN_MNM_CIRCLE_OF_SERVICE = "mnm_circle_of_service";
    private static final String COLUMN_MNM_SPAM_COUNT = "mnm_spam_count";
    private static final String COLUMN_MNM_UPDATED_AT = "mnm_updated_at";
    private static final String COLUMN_MNM_UPDATED_DATA = "mnm_updated_data";
    private static final String COLUMN_MNM_DELETED_AT = "mnm_deleted_at";
    private static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    public static final String CREATE_TABLE_RC_MOBILE_NUMBER_MASTER = "CREATE TABLE " +
            TABLE_RC_MOBILE_NUMBER_MASTER + " (" +
            " " + COLUMN_MNM_ID + " integer NOT NULL CONSTRAINT rc_mobile_number_master_pk " +
            "PRIMARY KEY," +
            " " + COLUMN_MNM_MOBILE_NUMBER + " text NOT NULL," +
            " " + COLUMN_MNM_NUMBER_TYPE + " text NOT NULL," +
            " " + COLUMN_MNM_CUSTOM_TYPE + " text," +
            " " + COLUMN_MNM_IS_PRIMARY + " integer," +
            " " + COLUMN_MNM_NUMBER_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_MNM_IS_DEFAULT + " integer," +
            " " + COLUMN_MNM_IS_VERIFIED + " integer," +
            " " + COLUMN_MNM_MOBILE_SERVICE_PROVIDER + " text," +
            " " + COLUMN_MNM_CIRCLE_OF_SERVICE + " text," +
            " " + COLUMN_MNM_SPAM_COUNT + " integer," +
            " " + COLUMN_MNM_UPDATED_AT + " datetime," +
            " " + COLUMN_MNM_UPDATED_DATA + " integer," +
            " " + COLUMN_MNM_DELETED_AT + " datetime," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new Mobile Number
    public void addMobileNumber(MobileNumber mobileNumber) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MNM_ID, mobileNumber.getMnmId());
        values.put(COLUMN_MNM_MOBILE_NUMBER, mobileNumber.getMnmMobileNumber());
        values.put(COLUMN_MNM_NUMBER_TYPE, mobileNumber.getMnmNumberType());
        values.put(COLUMN_MNM_CUSTOM_TYPE, mobileNumber.getMnmCustomType());
        values.put(COLUMN_MNM_IS_PRIMARY, mobileNumber.getMnmIsPrimary());
        values.put(COLUMN_MNM_NUMBER_PRIVACY, mobileNumber.getMnmNumberPrivacy());
        values.put(COLUMN_MNM_IS_DEFAULT, mobileNumber.getMnmIsDefault());
        values.put(COLUMN_MNM_IS_VERIFIED, mobileNumber.getMnmIsVerified());
        values.put(COLUMN_MNM_MOBILE_SERVICE_PROVIDER, mobileNumber.getMnmMobileServiceProvider());
        values.put(COLUMN_MNM_CIRCLE_OF_SERVICE, mobileNumber.getMnmCircleOfService());
        values.put(COLUMN_MNM_SPAM_COUNT, mobileNumber.getMnmSpamCount());
        values.put(COLUMN_MNM_UPDATED_AT, mobileNumber.getMnmUpdatedAt());
        values.put(COLUMN_MNM_UPDATED_DATA, mobileNumber.getMnmUpdatedData());
        values.put(COLUMN_MNM_DELETED_AT, mobileNumber.getMnmDeletedAt());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, mobileNumber.getRcProfileMasterPmId());


        // Inserting Row
        db.insert(TABLE_RC_MOBILE_NUMBER_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Mobile Number
    public MobileNumber getMobileNumber(int mnmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_MOBILE_NUMBER_MASTER, new String[]{COLUMN_MNM_ID,
                COLUMN_MNM_MOBILE_NUMBER, COLUMN_MNM_NUMBER_TYPE, COLUMN_MNM_CUSTOM_TYPE,
                COLUMN_MNM_IS_PRIMARY, COLUMN_MNM_NUMBER_PRIVACY, COLUMN_MNM_IS_DEFAULT,
                COLUMN_MNM_IS_VERIFIED, COLUMN_MNM_MOBILE_SERVICE_PROVIDER,
                COLUMN_MNM_CIRCLE_OF_SERVICE,
                COLUMN_MNM_SPAM_COUNT, COLUMN_MNM_UPDATED_AT, COLUMN_MNM_UPDATED_DATA,
                COLUMN_MNM_DELETED_AT, COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_MNM_ID + "=?", new
                String[]{String.valueOf
                (mnmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MobileNumber mobileNumber = new MobileNumber();
        if (cursor != null) {
            mobileNumber.setMnmId(cursor.getString(0));
            mobileNumber.setMnmMobileNumber(cursor.getString(1));
            mobileNumber.setMnmNumberType(cursor.getString(2));
            mobileNumber.setMnmCustomType(cursor.getString(3));
            mobileNumber.setMnmIsPrimary(cursor.getString(4));
            mobileNumber.setMnmNumberPrivacy(cursor.getString(5));
            mobileNumber.setMnmIsDefault(cursor.getString(6));
            mobileNumber.setMnmIsVerified(cursor.getString(7));
            mobileNumber.setMnmCircleOfService(cursor.getString(8));
            mobileNumber.setMnmSpamCount(cursor.getString(9));
            mobileNumber.setMnmUpdatedAt(cursor.getString(10));
            mobileNumber.setMnmUpdatedData(cursor.getString(11));
            mobileNumber.setMnmDeletedAt(cursor.getString(12));
            mobileNumber.setRcProfileMasterPmId(cursor.getString(13));
        }
        // return Mobile Number
        return mobileNumber;
    }

    // Getting All Mobile Numbers
    public ArrayList<MobileNumber> getAllMobileNumbers() {
        ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_MOBILE_NUMBER_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MobileNumber mobileNumber = new MobileNumber();
                mobileNumber.setMnmId(cursor.getString(0));
                mobileNumber.setMnmMobileNumber(cursor.getString(1));
                mobileNumber.setMnmNumberType(cursor.getString(2));
                mobileNumber.setMnmCustomType(cursor.getString(3));
                mobileNumber.setMnmIsPrimary(cursor.getString(4));
                mobileNumber.setMnmNumberPrivacy(cursor.getString(5));
                mobileNumber.setMnmIsDefault(cursor.getString(6));
                mobileNumber.setMnmIsVerified(cursor.getString(7));
                mobileNumber.setMnmCircleOfService(cursor.getString(8));
                mobileNumber.setMnmSpamCount(cursor.getString(9));
                mobileNumber.setMnmUpdatedAt(cursor.getString(10));
                mobileNumber.setMnmUpdatedData(cursor.getString(11));
                mobileNumber.setMnmDeletedAt(cursor.getString(12));
                mobileNumber.setRcProfileMasterPmId(cursor.getString(13));
                // Adding Mobile Number to list
                arrayListMobileNumber.add(mobileNumber);
            } while (cursor.moveToNext());
        }

        // return Mobile Number list
        return arrayListMobileNumber;
    }

    // Getting Mobile Number Count
    public int getMobileNumberCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_MOBILE_NUMBER_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single Mobile Number
    public int updateMobileNumber(MobileNumber mobileNumber) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MNM_ID, mobileNumber.getMnmId());
        values.put(COLUMN_MNM_MOBILE_NUMBER, mobileNumber.getMnmMobileNumber());
        values.put(COLUMN_MNM_NUMBER_TYPE, mobileNumber.getMnmNumberType());
        values.put(COLUMN_MNM_CUSTOM_TYPE, mobileNumber.getMnmCustomType());
        values.put(COLUMN_MNM_IS_PRIMARY, mobileNumber.getMnmIsPrimary());
        values.put(COLUMN_MNM_NUMBER_PRIVACY, mobileNumber.getMnmNumberPrivacy());
        values.put(COLUMN_MNM_IS_DEFAULT, mobileNumber.getMnmIsDefault());
        values.put(COLUMN_MNM_IS_VERIFIED, mobileNumber.getMnmIsVerified());
        values.put(COLUMN_MNM_MOBILE_SERVICE_PROVIDER, mobileNumber.getMnmMobileServiceProvider());
        values.put(COLUMN_MNM_CIRCLE_OF_SERVICE, mobileNumber.getMnmCircleOfService());
        values.put(COLUMN_MNM_SPAM_COUNT, mobileNumber.getMnmSpamCount());
        values.put(COLUMN_MNM_UPDATED_AT, mobileNumber.getMnmUpdatedAt());
        values.put(COLUMN_MNM_UPDATED_DATA, mobileNumber.getMnmUpdatedData());
        values.put(COLUMN_MNM_DELETED_AT, mobileNumber.getMnmDeletedAt());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, mobileNumber.getRcProfileMasterPmId());

        // updating row
        return db.update(TABLE_RC_MOBILE_NUMBER_MASTER, values, COLUMN_MNM_ID + " = ?",
                new String[]{String.valueOf(mobileNumber.getMnmId())});
    }

    // Deleting single Mobile Number
    public void deleteMobileNumber(MobileNumber mobileNumber) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_MOBILE_NUMBER_MASTER, COLUMN_MNM_ID + " = ?",
                new String[]{String.valueOf(mobileNumber.getMnmId())});
        db.close();
    }
}
