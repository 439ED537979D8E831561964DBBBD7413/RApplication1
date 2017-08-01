package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.model.Address;
import com.rawalinfocom.rcontact.model.SpamDataType;

import java.util.ArrayList;

/**
 * Created by Aniruddh on 27/07/17.
 */

public class TableSpamDetailMaster {

    private DatabaseHandler databaseHandler;

    public TableSpamDetailMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_SPAM_DETAIL_MASTER = "table_spam_detail_master";

    // Column Names
    private static final String COLUMN_PB_LAST_NAME = "pb_name_last";
    private static final String COLUMN_PB_NAME_SUFFIX = "pb_name_suffix";
    private static final String COLUMN_PB_NAME_FIRST = "pb_name_first";
    private static final String COLUMN_PB_RCP_VERIFY = "pb_rcp_verify";
    private static final String COLUMN_PB_NAME_MIDDLE = "pb_name_middle";
    private static final String COLUMN_RCP_PM_ID = "rcp_pm_id";
    private static final String COLUMN_MOBILE_NUMBER = "mobile_number";
    private static final String COLUMN_PB_NAME_PREFIX = "pb_name_prefix";
    private static final String COLUMN_SPAM_COUNT = "spam_count";
    private static final String COLUMN_PROFILE_RATING = "profile_rating";
    private static final String COLUMN_TOTAL_PROFILE_RATE_USER = "total_profile_rate_user";
    private static final String COLUMN_PUBLIC_URL = "public_url";
    private static final String COLUMN_PHOTO_URL = "pb_profile_photo";

    // Table Create Statements
    static final String CREATE_TABLE_SPAM_DETAIL_MASTER = "CREATE TABLE table_spam_detail_master (" +
            " " + COLUMN_PB_LAST_NAME + " text," +
            " " + COLUMN_PB_NAME_SUFFIX + " text," +
            " " + COLUMN_PB_NAME_FIRST + " text," +
            " " + COLUMN_PB_RCP_VERIFY + " text," +
            " " + COLUMN_PB_NAME_MIDDLE + " text," +
            " " + COLUMN_RCP_PM_ID + " text," +
            " " + COLUMN_MOBILE_NUMBER + " text," +
            " " + COLUMN_PB_NAME_PREFIX + " text," +
            " " + COLUMN_SPAM_COUNT + " text," +
            " " + COLUMN_PROFILE_RATING + " text," +
            " " + COLUMN_TOTAL_PROFILE_RATE_USER + " text," +
            " " + COLUMN_PUBLIC_URL + " text," +
            " " + COLUMN_PHOTO_URL + " text" +
            ");";

    public void insertSpamDetails(ArrayList<SpamDataType> spamDataTypeList) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        for (int i = 0; i < spamDataTypeList.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_PB_LAST_NAME, spamDataTypeList.get(i).getLastName());
            values.put(COLUMN_PB_NAME_SUFFIX, spamDataTypeList.get(i).getSuffix());
            values.put(COLUMN_PB_NAME_FIRST, spamDataTypeList.get(i).getFirstName());
            values.put(COLUMN_PB_RCP_VERIFY, spamDataTypeList.get(i).getRcpVerfiy());
            values.put(COLUMN_PB_NAME_MIDDLE, spamDataTypeList.get(i).getMiddleName());
            values.put(COLUMN_RCP_PM_ID, spamDataTypeList.get(i).getRcpPmId());
            values.put(COLUMN_MOBILE_NUMBER, spamDataTypeList.get(i).getMobileNumber());
            values.put(COLUMN_PB_NAME_PREFIX, spamDataTypeList.get(i).getPrefix());
            values.put(COLUMN_SPAM_COUNT, spamDataTypeList.get(i).getSpamCount());
            values.put(COLUMN_PROFILE_RATING, spamDataTypeList.get(i).getProfileRating());
            values.put(COLUMN_TOTAL_PROFILE_RATE_USER, spamDataTypeList.get(i).getTotalProfileRateUser());
            values.put(COLUMN_PUBLIC_URL, spamDataTypeList.get(i).getSpamPublicUrl());
            values.put(COLUMN_PHOTO_URL, spamDataTypeList.get(i).getSpamPhotoUrl());


            // Inserting Row
            db.insert(TABLE_SPAM_DETAIL_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    public ArrayList<SpamDataType> getSpamDetails() {
        ArrayList<SpamDataType> arrayListSpamDetails = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SPAM_DETAIL_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SpamDataType spamDataType = new SpamDataType();
                spamDataType.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_PB_LAST_NAME)));
                spamDataType.setSuffix(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PB_NAME_SUFFIX)));
                spamDataType.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_PB_NAME_FIRST)));
                spamDataType.setRcpVerfiy(cursor.getString(cursor.getColumnIndex(COLUMN_PB_RCP_VERIFY)));
                spamDataType.setMiddleName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PB_NAME_MIDDLE)));
                spamDataType.setRcpPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RCP_PM_ID)));
                spamDataType.setMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE_NUMBER)));
                spamDataType.setPrefix(cursor.getString(cursor.getColumnIndex(COLUMN_PB_NAME_PREFIX)));
                spamDataType.setSpamCount(cursor.getString(cursor.getColumnIndex(COLUMN_SPAM_COUNT)));
                spamDataType.setProfileRating(cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_RATING)));
                spamDataType.setTotalProfileRateUser(cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_PROFILE_RATE_USER)));
                spamDataType.setSpamPublicUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PUBLIC_URL)));
                spamDataType.setSpamPhotoUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_URL)));
                // Adding Address to list
                arrayListSpamDetails.add(spamDataType);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return address list
        return arrayListSpamDetails;
    }


    public SpamDataType getSpamDetailsFromNumber(String number) {
        // Select All Query
        SpamDataType spamDataType = new SpamDataType();
        String selectQuery = "SELECT  * FROM " + TABLE_SPAM_DETAIL_MASTER +
                " WHERE " + COLUMN_MOBILE_NUMBER + " = \'" + number + "\'";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            spamDataType.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_PB_LAST_NAME)));
            spamDataType.setSuffix(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PB_NAME_SUFFIX)));
            spamDataType.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_PB_NAME_FIRST)));
            spamDataType.setRcpVerfiy(cursor.getString(cursor.getColumnIndex(COLUMN_PB_RCP_VERIFY)));
            spamDataType.setMiddleName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PB_NAME_MIDDLE)));
            spamDataType.setRcpPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RCP_PM_ID)));
            spamDataType.setMobileNumber(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE_NUMBER)));
            spamDataType.setPrefix(cursor.getString(cursor.getColumnIndex(COLUMN_PB_NAME_PREFIX)));
            spamDataType.setSpamCount(cursor.getString(cursor.getColumnIndex(COLUMN_SPAM_COUNT)));
            spamDataType.setProfileRating(cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_RATING)));
            spamDataType.setTotalProfileRateUser(cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_PROFILE_RATE_USER)));
            spamDataType.setSpamPublicUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PUBLIC_URL)));
            spamDataType.setSpamPhotoUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_URL)));
            cursor.close();
        }
        db.close();

        // return address list
        return spamDataType;
    }

    public void updateSpamCount(String number, String spamCount){
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SPAM_COUNT, spamCount);

        // Update Row
        db.update(TABLE_SPAM_DETAIL_MASTER, values,  COLUMN_MOBILE_NUMBER + " = \'" + number + "\'", null);
        db.close(); // Closing database connection

    }

}
