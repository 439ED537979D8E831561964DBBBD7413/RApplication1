package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Website;

import java.util.ArrayList;

/**
 * Created by Monal on 29/11/16.
 * <p>
 * Table operations rc_website_master
 */

public class TableWebsiteMaster {

    private DatabaseHandler databaseHandler;

    public TableWebsiteMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_WEBSITE_MASTER = "rc_website_master";

    // Column Names
    private static final String COLUMN_WM_ID = "wm_id";
    private static final String COLUMN_WM_RECORD_INDEX_ID = "wm_record_index_id";
    static final String COLUMN_WM_WEBSITE_URL = "wm_website_url";
    private static final String COLUMN_WM_WEBSITE_TYPE = "wm_website_type";
    private static final String COLUMN_WM_CUSTOM_TYPE = "wm_custom_type";
    private static final String COLUMN_WM_WEBSITE_PRIVACY = "wm_website_privacy";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    static final String CREATE_TABLE_RC_WEBSITE_MASTER = "CREATE TABLE " + TABLE_RC_WEBSITE_MASTER
            + " (" +
            " " + COLUMN_WM_ID + " integer NOT NULL CONSTRAINT rc_website_master_pk PRIMARY KEY," +
            " " + COLUMN_WM_RECORD_INDEX_ID + " text," +
            " " + COLUMN_WM_WEBSITE_URL + " text NOT NULL," +
            " " + COLUMN_WM_WEBSITE_TYPE + " text," +
            " " + COLUMN_WM_CUSTOM_TYPE + " text," +
            " " + COLUMN_WM_WEBSITE_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new Website
    public void addWebsite(Website website) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WM_ID, website.getWmId());
        values.put(COLUMN_WM_RECORD_INDEX_ID, website.getWmRecordIndexId());
        values.put(COLUMN_WM_WEBSITE_URL, website.getWmWebsiteUrl());
        values.put(COLUMN_WM_WEBSITE_TYPE, website.getWmWebsiteType());
        values.put(COLUMN_WM_CUSTOM_TYPE, website.getWmCustomType());
        values.put(COLUMN_WM_WEBSITE_PRIVACY, website.getWmWebsitePrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, website.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_WEBSITE_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Website
    public Website getWebsite(int wmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_WEBSITE_MASTER, new String[]{COLUMN_WM_ID,
                COLUMN_WM_RECORD_INDEX_ID, COLUMN_WM_WEBSITE_URL, COLUMN_WM_WEBSITE_TYPE,
                COLUMN_WM_CUSTOM_TYPE, COLUMN_WM_WEBSITE_PRIVACY,
                COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_WM_ID + "=?", new String[]{String.valueOf
                (wmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Website website = new Website();
        if (cursor != null) {
            website.setWmId(cursor.getString(cursor.getColumnIndex(COLUMN_WM_ID)));
            website.setWmRecordIndexId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_WM_RECORD_INDEX_ID)));
            website.setWmWebsiteUrl(cursor.getString(cursor.getColumnIndex(COLUMN_WM_WEBSITE_URL)));
            website.setWmWebsiteType(cursor.getString(cursor.getColumnIndex
                    (COLUMN_WM_WEBSITE_TYPE)));
            website.setWmCustomType(cursor.getString(cursor.getColumnIndex(COLUMN_WM_CUSTOM_TYPE)));
            website.setWmWebsitePrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_WM_WEBSITE_PRIVACY)));
            website.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

            cursor.close();
        }

        db.close();

        // return Website
        return website;
    }

    // Adding array website
    public void addArrayWebsite(ArrayList<Website> arrayListWebsite) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListWebsite.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_WM_ID, arrayListWebsite.get(i).getWmId());
            values.put(COLUMN_WM_RECORD_INDEX_ID, arrayListWebsite.get(i).getWmRecordIndexId());
            values.put(COLUMN_WM_WEBSITE_URL, arrayListWebsite.get(i).getWmWebsiteUrl());
            values.put(COLUMN_WM_WEBSITE_TYPE, arrayListWebsite.get(i).getWmWebsiteType());
            values.put(COLUMN_WM_CUSTOM_TYPE, arrayListWebsite.get(i).getWmCustomType());
            values.put(COLUMN_WM_WEBSITE_PRIVACY, arrayListWebsite.get(i).getWmWebsitePrivacy());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListWebsite.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_WEBSITE_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    // Getting All Websites
    public ArrayList<Website> getAllWebsites() {
        ArrayList<Website> arrayListWebsite = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_WEBSITE_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Website website = new Website();
                website.setWmId(cursor.getString(cursor.getColumnIndex(COLUMN_WM_ID)));
                website.setWmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_WM_RECORD_INDEX_ID)));
                website.setWmWebsiteUrl(cursor.getString(cursor.getColumnIndex
                        (COLUMN_WM_WEBSITE_URL)));
                website.setWmWebsiteType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_WM_WEBSITE_TYPE)));
                website.setWmCustomType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_WM_CUSTOM_TYPE)));
                website.setWmWebsitePrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_WM_WEBSITE_PRIVACY)));
                website.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding Website to list
                arrayListWebsite.add(website);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Website list
        return arrayListWebsite;
    }

    // Getting All Websites from Profile Master Id
    public ArrayList<Website> getWebsiteFromPmId(int pmId) {
        ArrayList<Website> arrayListWebsites = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_WM_RECORD_INDEX_ID + ", " +
                COLUMN_WM_WEBSITE_URL + ", " +
                COLUMN_WM_WEBSITE_TYPE + ", " +
                COLUMN_WM_WEBSITE_PRIVACY + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_WEBSITE_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Website website = new Website();
                website.setWmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_WM_RECORD_INDEX_ID)));
                website.setWmWebsiteUrl(cursor.getString(cursor.getColumnIndex
                        (COLUMN_WM_WEBSITE_URL)));
                website.setWmWebsiteType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_WM_WEBSITE_TYPE)));
                website.setWmWebsitePrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_WM_WEBSITE_PRIVACY)));
                website.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding Website to list
                arrayListWebsites.add(website);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Mobile Number list
        return arrayListWebsites;
    }

    // Getting Website Count
    public int getWebsiteCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_WEBSITE_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single Website
    public int updateWebsite(Website website) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WM_ID, website.getWmId());
        values.put(COLUMN_WM_RECORD_INDEX_ID, website.getWmRecordIndexId());
        values.put(COLUMN_WM_WEBSITE_URL, website.getWmWebsiteUrl());
        values.put(COLUMN_WM_WEBSITE_TYPE, website.getWmWebsiteType());
        values.put(COLUMN_WM_CUSTOM_TYPE, website.getWmCustomType());
        values.put(COLUMN_WM_WEBSITE_PRIVACY, website.getWmWebsitePrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, website.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_WEBSITE_MASTER, values, COLUMN_WM_ID + " = ?",
                new String[]{String.valueOf(website.getWmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single Website
    public void deleteWebsite(Website website) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_WEBSITE_MASTER, COLUMN_WM_ID + " = ?",
                new String[]{String.valueOf(website.getWmId())});
        db.close();
    }

    // Deleting single Website From RcpId
    public void deleteWebsite(String rcpId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_WEBSITE_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?",
                new String[]{String.valueOf(rcpId)});
        db.close();
    }
}
