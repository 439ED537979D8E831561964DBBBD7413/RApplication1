package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.ProfileEmailMapping;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;

import java.util.ArrayList;

/**
 * Created by Monal on 14/11/16.
 * <p>
 * Table operations pb_profile_email_mapping
 */

public class TableProfileEmailMapping {

    private DatabaseHandler databaseHandler;

    public TableProfileEmailMapping(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_PB_PROFILE_EMAIL_MAPPING = "pb_profile_email_mapping";

    // Column Names
    private static final String COLUMN_EPM_ID = "epm_id";
    private static final String COLUMN_EPM_EMAIL_ID = "epm_email_id";
    private static final String COLUMN_EPM_CLOUD_EM_ID = "epm_cloud_em_id";
    private static final String COLUMN_EPM_CLOUD_PM_ID = "epm_cloud_pm_id";
    private static final String COLUMN_EPM_IS_RCP = "epm_is_rcp";


    // Table Create Statements
    static final String CREATE_TABLE_PB_PROFILE_EMAIL_MAPPING = "CREATE TABLE " +
            TABLE_PB_PROFILE_EMAIL_MAPPING + " (" +
            " " + COLUMN_EPM_ID + " integer NOT NULL CONSTRAINT pb_profile_email_mapping_pk " +
            "PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_EPM_EMAIL_ID + " text NOT NULL," +
            " " + COLUMN_EPM_CLOUD_EM_ID + " integer," +
            " " + COLUMN_EPM_CLOUD_PM_ID + " integer," +
            " " + COLUMN_EPM_IS_RCP + " tinyint DEFAULT 0" +
            ");";

    // Adding new Profile Email Mapping
    public void addProfileEmailMapping(ProfileEmailMapping profileEmailMapping) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EPM_ID, profileEmailMapping.getEpmId());
        values.put(COLUMN_EPM_EMAIL_ID, profileEmailMapping.getEpmEmailId());
        values.put(COLUMN_EPM_CLOUD_EM_ID, profileEmailMapping.getEpmCloudEmId());
        values.put(COLUMN_EPM_CLOUD_PM_ID, profileEmailMapping.getEpmCloudPmId());
        values.put(COLUMN_EPM_IS_RCP, profileEmailMapping.getEpmIsRcp());

        // Inserting Row
        db.insert(TABLE_PB_PROFILE_EMAIL_MAPPING, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Profile Email Mapping
    public ProfileEmailMapping getProfileMobileMapping(int mpmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PB_PROFILE_EMAIL_MAPPING, new String[]{COLUMN_EPM_ID,
                COLUMN_EPM_EMAIL_ID, COLUMN_EPM_CLOUD_EM_ID, COLUMN_EPM_CLOUD_PM_ID,
                COLUMN_EPM_IS_RCP}, COLUMN_EPM_ID + "=?", new
                String[]{String.valueOf(mpmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ProfileEmailMapping profileEmailMapping = new ProfileEmailMapping();
        if (cursor != null) {
            profileEmailMapping.setEpmId(cursor.getString(0));
            profileEmailMapping.setEpmEmailId(cursor.getString(1));
            profileEmailMapping.setEpmCloudEmId(cursor.getString(2));
            profileEmailMapping.setEpmCloudPmId(cursor.getString(3));
            profileEmailMapping.setEpmIsRcp(cursor.getString(4));

            cursor.close();
        }

        db.close();

        // return Profile Email Mapping
        return profileEmailMapping;
    }

    // Getting All Profile Email Mapping
    public ArrayList<ProfileEmailMapping> getAllProfileEmailMapping() {
        ArrayList<ProfileEmailMapping> arrayListProfileEmailMapping = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PB_PROFILE_EMAIL_MAPPING;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProfileEmailMapping profileEmailMapping = new ProfileEmailMapping();
                profileEmailMapping.setEpmId(cursor.getString(0));
                profileEmailMapping.setEpmEmailId(cursor.getString(1));
                profileEmailMapping.setEpmCloudEmId(cursor.getString(2));
                profileEmailMapping.setEpmCloudPmId(cursor.getString(3));
                profileEmailMapping.setEpmIsRcp(cursor.getString(4));
                // Adding profileMobileMapping to list
                arrayListProfileEmailMapping.add(profileEmailMapping);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return ProfileEmailMapping list
        return arrayListProfileEmailMapping;
    }

    // Getting ProfileEmailMapping Count
    public int getProfileEmailMappingCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PB_PROFILE_EMAIL_MAPPING;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single ProfileEmailMapping
    public int updateProfileEmailMapping(ProfileEmailMapping profileEmailMapping) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EPM_ID, profileEmailMapping.getEpmId());
        values.put(COLUMN_EPM_EMAIL_ID, profileEmailMapping.getEpmEmailId());
        values.put(COLUMN_EPM_CLOUD_EM_ID, profileEmailMapping.getEpmCloudEmId());
        values.put(COLUMN_EPM_CLOUD_PM_ID, profileEmailMapping.getEpmCloudPmId());
        values.put(COLUMN_EPM_IS_RCP, profileEmailMapping.getEpmIsRcp());

        // updating row
        int isUpdated = db.update(TABLE_PB_PROFILE_EMAIL_MAPPING, values, COLUMN_EPM_ID + " = ?",
                new String[]{String.valueOf(profileEmailMapping.getEpmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single profileMobileMapping
    public void deleteProfileMobileMapping(ProfileEmailMapping profileEmailMapping) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_PB_PROFILE_EMAIL_MAPPING, COLUMN_EPM_ID + " = ?",
                new String[]{String.valueOf(profileEmailMapping.getEpmId())});
        db.close();
    }
}
