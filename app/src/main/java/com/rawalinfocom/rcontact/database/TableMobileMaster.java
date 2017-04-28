package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.MobileNumber;

import java.util.ArrayList;

/**
 * Created by Monal on 15/11/16.
 * <p>
 * Table operations rc_mobile_number_master
 */

public class TableMobileMaster {

    private DatabaseHandler databaseHandler;

    public TableMobileMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_MOBILE_NUMBER_MASTER = "rc_mobile_number_master";

    // Column Names
    private static final String COLUMN_MNM_ID = "mnm_id";
    static final String COLUMN_MNM_RECORD_INDEX_ID = "mnm_record_index_id";
    static final String COLUMN_MNM_MOBILE_NUMBER = "mnm_mobile_number";
    static final String COLUMN_MNM_NUMBER_TYPE = "mnm_number_type";
    static final String COLUMN_MNM_IS_PRIMARY = "mnm_is_primary";
    static final String COLUMN_MNM_NUMBER_PRIVACY = "mnm_number_privacy";
    private static final String COLUMN_MNM_MOBILE_SERVICE_PROVIDER = "mnm_mobile_service_provider";
    private static final String COLUMN_MNM_CIRCLE_OF_SERVICE = "mnm_circle_of_service";
    private static final String COLUMN_MNM_SPAM_COUNT = "mnm_spam_count";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";

    //    private static final String COLUMN_MNM_CLOUD_ID = "mnm_cloud_id";
    //    private static final String COLUMN_MNM_CUSTOM_TYPE = "mnm_custom_type";
    //    private static final String COLUMN_MNM_IS_DEFAULT = "mnm_is_default";
    //    private static final String COLUMN_MNM_IS_VERIFIED = "mnm_is_verified";

    // Table Create Statements
    static final String CREATE_TABLE_RC_MOBILE_NUMBER_MASTER = "CREATE TABLE " +
            TABLE_RC_MOBILE_NUMBER_MASTER + " (" +
            " " + COLUMN_MNM_ID + " integer NOT NULL CONSTRAINT rc_mobile_number_master_pk " +
            "PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_MNM_RECORD_INDEX_ID + " text," +
            " " + COLUMN_MNM_MOBILE_NUMBER + " text NOT NULL," +
            " " + COLUMN_MNM_NUMBER_TYPE + " text," +
            " " + COLUMN_MNM_IS_PRIMARY + " integer," +
            " " + COLUMN_MNM_NUMBER_PRIVACY + " integer DEFAULT 2," +
            " " + COLUMN_MNM_MOBILE_SERVICE_PROVIDER + " text," +
            " " + COLUMN_MNM_CIRCLE_OF_SERVICE + " text," +
            " " + COLUMN_MNM_SPAM_COUNT + " integer," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new Mobile Number
    public void addMobileNumber(MobileNumber mobileNumber) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MNM_ID, mobileNumber.getMnmId());
        values.put(COLUMN_MNM_RECORD_INDEX_ID, mobileNumber.getMnmRecordIndexId());
        values.put(COLUMN_MNM_MOBILE_NUMBER, mobileNumber.getMnmMobileNumber());
        values.put(COLUMN_MNM_NUMBER_TYPE, mobileNumber.getMnmNumberType());
        values.put(COLUMN_MNM_IS_PRIMARY, mobileNumber.getMnmIsPrimary());
        values.put(COLUMN_MNM_NUMBER_PRIVACY, mobileNumber.getMnmNumberPrivacy());
        values.put(COLUMN_MNM_MOBILE_SERVICE_PROVIDER, mobileNumber.getMnmMobileServiceProvider());
        values.put(COLUMN_MNM_CIRCLE_OF_SERVICE, mobileNumber.getMnmCircleOfService());
        values.put(COLUMN_MNM_SPAM_COUNT, mobileNumber.getMnmSpamCount());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, mobileNumber.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_MOBILE_NUMBER_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding array Mobile Number
    public void addArrayMobileNumber(ArrayList<MobileNumber> arrayListMobileNumber) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListMobileNumber.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_MNM_ID, arrayListMobileNumber.get(i).getMnmId());
            values.put(COLUMN_MNM_RECORD_INDEX_ID, arrayListMobileNumber.get(i)
                    .getMnmRecordIndexId());
            values.put(COLUMN_MNM_MOBILE_NUMBER, arrayListMobileNumber.get(i).getMnmMobileNumber());
            values.put(COLUMN_MNM_NUMBER_TYPE, arrayListMobileNumber.get(i).getMnmNumberType());
            values.put(COLUMN_MNM_IS_PRIMARY, arrayListMobileNumber.get(i).getMnmIsPrimary());
            values.put(COLUMN_MNM_NUMBER_PRIVACY, arrayListMobileNumber.get(i)
                    .getMnmNumberPrivacy());
            values.put(COLUMN_MNM_MOBILE_SERVICE_PROVIDER, arrayListMobileNumber.get(i)
                    .getMnmMobileServiceProvider());
            values.put(COLUMN_MNM_CIRCLE_OF_SERVICE, arrayListMobileNumber.get(i)
                    .getMnmCircleOfService());
            values.put(COLUMN_MNM_SPAM_COUNT, arrayListMobileNumber.get(i).getMnmSpamCount());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListMobileNumber.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_MOBILE_NUMBER_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    // Getting single Mobile Number
    public MobileNumber getMobileNumber(int mnmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_MOBILE_NUMBER_MASTER, new String[]{COLUMN_MNM_ID,
                COLUMN_MNM_RECORD_INDEX_ID, COLUMN_MNM_MOBILE_NUMBER, COLUMN_MNM_NUMBER_TYPE,
                COLUMN_MNM_IS_PRIMARY, COLUMN_MNM_NUMBER_PRIVACY,
                COLUMN_MNM_MOBILE_SERVICE_PROVIDER, COLUMN_MNM_CIRCLE_OF_SERVICE,
                COLUMN_MNM_SPAM_COUNT, COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_MNM_ID + "=?", new
                String[]{String.valueOf(mnmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MobileNumber mobileNumber = new MobileNumber();
        if (cursor != null) {
            mobileNumber.setMnmId(cursor.getString(cursor.getColumnIndex(COLUMN_MNM_ID)));
            mobileNumber.setMnmRecordIndexId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_MNM_RECORD_INDEX_ID)));
            mobileNumber.setMnmMobileNumber(cursor.getString(cursor.getColumnIndex
                    (COLUMN_MNM_MOBILE_NUMBER)));
            mobileNumber.setMnmNumberType(cursor.getString(cursor.getColumnIndex
                    (COLUMN_MNM_NUMBER_TYPE)));
            mobileNumber.setMnmIsPrimary(cursor.getString(cursor.getColumnIndex
                    (COLUMN_MNM_IS_PRIMARY)));
            mobileNumber.setMnmNumberPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_MNM_NUMBER_PRIVACY)));
            mobileNumber.setMnmCircleOfService(cursor.getString(cursor.getColumnIndex
                    (COLUMN_MNM_CIRCLE_OF_SERVICE)));
            mobileNumber.setMnmSpamCount(cursor.getString(cursor.getColumnIndex
                    (COLUMN_MNM_SPAM_COUNT)));
            mobileNumber.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

            cursor.close();
        }

        db.close();

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
                mobileNumber.setMnmId(cursor.getString(cursor.getColumnIndex(COLUMN_MNM_ID)));
                mobileNumber.setMnmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_RECORD_INDEX_ID)));
                mobileNumber.setMnmMobileNumber(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_MOBILE_NUMBER)));
                mobileNumber.setMnmNumberType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_NUMBER_TYPE)));
                mobileNumber.setMnmIsPrimary(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_IS_PRIMARY)));
                mobileNumber.setMnmNumberPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_NUMBER_PRIVACY)));
                mobileNumber.setMnmCircleOfService(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_CIRCLE_OF_SERVICE)));
                mobileNumber.setMnmSpamCount(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_SPAM_COUNT)));
                mobileNumber.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding Mobile Number to list
                arrayListMobileNumber.add(mobileNumber);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Mobile Number list
        return arrayListMobileNumber;
    }

    // Getting All Mobile Numbers from Profile Master Id
    public ArrayList<MobileNumber> getMobileNumbersFromPmId(int pmId) {
        ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " +
                COLUMN_MNM_RECORD_INDEX_ID + ", " +
                COLUMN_MNM_MOBILE_NUMBER + ", " +
                COLUMN_MNM_NUMBER_TYPE + ", " +
                COLUMN_MNM_IS_PRIMARY + ", " +
                COLUMN_MNM_NUMBER_PRIVACY + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_MOBILE_NUMBER_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MobileNumber mobileNumber = new MobileNumber();
                mobileNumber.setMnmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_RECORD_INDEX_ID)));
                mobileNumber.setMnmMobileNumber(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_MOBILE_NUMBER)));
                mobileNumber.setMnmNumberType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_NUMBER_TYPE)));
                mobileNumber.setMnmIsPrimary(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_IS_PRIMARY)));
                mobileNumber.setMnmNumberPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_NUMBER_PRIVACY)));
                mobileNumber.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding Mobile Number to list
                arrayListMobileNumber.add(mobileNumber);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Mobile Number list
        return arrayListMobileNumber;
    }

    // Getting Own Verified Mobile Number
    public MobileNumber getOwnVerifiedMobileNumbersFromPmId(Context context) {
        MobileNumber mobileNumber = new MobileNumber();

        try {

            String pmId = Utils.getStringPreference(context, AppConstants.PREF_USER_PM_ID, "0");
            String rcpType = String.valueOf(IntegerConstants.RCP_TYPE_PRIMARY);

            String selectQuery = "SELECT * FROM " + TABLE_RC_MOBILE_NUMBER_MASTER + " WHERE " +
                    COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId + " AND " +
                    COLUMN_MNM_IS_PRIMARY +
                    " = " + rcpType;

            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);


            if (cursor.moveToFirst()) {

                mobileNumber.setMnmId(cursor.getString(cursor.getColumnIndex(COLUMN_MNM_ID)));
                mobileNumber.setMnmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_RECORD_INDEX_ID)));
                mobileNumber.setMnmMobileNumber(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_MOBILE_NUMBER)));
                mobileNumber.setMnmNumberType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_NUMBER_TYPE)));
                mobileNumber.setMnmIsPrimary(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_IS_PRIMARY)));
                mobileNumber.setMnmNumberPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_NUMBER_PRIVACY)));
                mobileNumber.setMnmCircleOfService(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_CIRCLE_OF_SERVICE)));
                mobileNumber.setMnmSpamCount(cursor.getString(cursor.getColumnIndex
                        (COLUMN_MNM_SPAM_COUNT)));
                mobileNumber.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));

                cursor.close();

            }

            db.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


        return mobileNumber;
    }

    // Getting Mobile Number Count
    public int getMobileNumberCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_MOBILE_NUMBER_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }

    // Updating single Mobile Number
    public int updateMobileNumber(MobileNumber mobileNumber) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MNM_ID, mobileNumber.getMnmId());
        values.put(COLUMN_MNM_MOBILE_NUMBER, mobileNumber.getMnmMobileNumber());
        values.put(COLUMN_MNM_NUMBER_TYPE, mobileNumber.getMnmNumberType());
        values.put(COLUMN_MNM_IS_PRIMARY, mobileNumber.getMnmIsPrimary());
        values.put(COLUMN_MNM_NUMBER_PRIVACY, mobileNumber.getMnmNumberPrivacy());
        values.put(COLUMN_MNM_MOBILE_SERVICE_PROVIDER, mobileNumber.getMnmMobileServiceProvider());
        values.put(COLUMN_MNM_CIRCLE_OF_SERVICE, mobileNumber.getMnmCircleOfService());
        values.put(COLUMN_MNM_SPAM_COUNT, mobileNumber.getMnmSpamCount());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, mobileNumber.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_MOBILE_NUMBER_MASTER, values, COLUMN_MNM_ID + " = ?",
                new String[]{String.valueOf(mobileNumber.getMnmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single Mobile Number
    public void deleteMobileNumber(MobileNumber mobileNumber) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_MOBILE_NUMBER_MASTER, COLUMN_MNM_ID + " = ?",
                new String[]{String.valueOf(mobileNumber.getMnmId())});
        db.close();
    }

    // Deleting single Mobile Number From RcpId
    public void deleteMobileNumber(String rcpId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_MOBILE_NUMBER_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?",
                new String[]{String.valueOf(rcpId)});
        db.close();
    }

    public int updatePrivacySettingToDefault(String cloudMongoId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MNM_NUMBER_PRIVACY, 2);

        // updating row
        int isUpdated = db.update(TABLE_RC_MOBILE_NUMBER_MASTER, values,
                COLUMN_MNM_RECORD_INDEX_ID + " = ?",
                new String[]{cloudMongoId});

        db.close();

        return isUpdated;
    }
}
