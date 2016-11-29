package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.ProfileMobileMapping;

import java.util.ArrayList;

/**
 * Created by Monal on 14/11/16.
 * <p>
 * Table operations pb_profile_mobile_mapping
 */

public class TableProfileMobileMapping {

    private DatabaseHandler databaseHandler;

    public TableProfileMobileMapping(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_PB_PROFILE_MOBILE_MAPPING = "pb_profile_mobile_mapping";

    // Column Names
    private static final String COLUMN_MPM_ID = "mpm_id";
    private static final String COLUMN_MPM_MOBILE_NUMBER = "mpm_mobile_number";
    private static final String COLUMN_MPM_CLOUD_MNM_ID = "mpm_cloud_mnm_id";
    private static final String COLUMN_MPM_CLOUD_PM_ID = "mpm_cloud_pm_id";
    private static final String COLUMN_MPM_IS_RCP = "mpm_is_rcp";


    // Table Create Statements
    static final String CREATE_TABLE_PB_PROFILE_MOBILE_MAPPING = "CREATE TABLE " +
            TABLE_PB_PROFILE_MOBILE_MAPPING + " (" +
            " " + COLUMN_MPM_ID + " integer NOT NULL CONSTRAINT pb_profile_mobile_mapping_pk " +
            "PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_MPM_MOBILE_NUMBER + " text NOT NULL," +
            " " + COLUMN_MPM_CLOUD_MNM_ID + " integer," +
            " " + COLUMN_MPM_CLOUD_PM_ID + " integer," +
            " " + COLUMN_MPM_IS_RCP + " tinyint DEFAULT 0" +
            ");";

    // Adding new Profile Mobile Mapping
    public void addProfileMobileMapping(ProfileMobileMapping profileMobileMapping) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MPM_ID, profileMobileMapping.getMpmId());
        values.put(COLUMN_MPM_MOBILE_NUMBER, profileMobileMapping.getMpmMobileNumber());
        values.put(COLUMN_MPM_CLOUD_MNM_ID, profileMobileMapping.getMpmCloudMnmId());
        values.put(COLUMN_MPM_CLOUD_PM_ID, profileMobileMapping.getMpmCloudPmId());
        values.put(COLUMN_MPM_IS_RCP, profileMobileMapping.getMpmIsRcp());

        // Inserting Row
        db.insert(TABLE_PB_PROFILE_MOBILE_MAPPING, null, values);
        db.close(); // Closing database connection
    }

    // Adding array Profile Mobile Mapping
    public void addArrayProfileMobileMapping(ArrayList<ProfileMobileMapping>
                                                     arrayListProfileMobileMapping) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListProfileMobileMapping.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_MPM_ID, arrayListProfileMobileMapping.get(i).getMpmId());
            values.put(COLUMN_MPM_MOBILE_NUMBER, arrayListProfileMobileMapping.get(i)
                    .getMpmMobileNumber());
            values.put(COLUMN_MPM_CLOUD_MNM_ID, arrayListProfileMobileMapping.get(i)
                    .getMpmCloudMnmId());
            values.put(COLUMN_MPM_CLOUD_PM_ID, arrayListProfileMobileMapping.get(i)
                    .getMpmCloudPmId());
            values.put(COLUMN_MPM_IS_RCP, arrayListProfileMobileMapping.get(i).getMpmIsRcp());

            // Inserting Row
            db.insert(TABLE_PB_PROFILE_MOBILE_MAPPING, null, values);
        }
        db.close(); // Closing database connection
    }

    // Getting single Profile Mobile Mapping
    public ProfileMobileMapping getProfileMobileMapping(int mpmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PB_PROFILE_MOBILE_MAPPING, new String[]{COLUMN_MPM_ID,
                COLUMN_MPM_MOBILE_NUMBER, COLUMN_MPM_CLOUD_MNM_ID, COLUMN_MPM_CLOUD_PM_ID,
                COLUMN_MPM_IS_RCP}, COLUMN_MPM_ID + "=?", new
                String[]{String.valueOf(mpmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
        if (cursor != null) {
            profileMobileMapping.setMpmId(cursor.getString(0));
            profileMobileMapping.setMpmMobileNumber(cursor.getString(1));
            profileMobileMapping.setMpmCloudMnmId(cursor.getString(2));
            profileMobileMapping.setMpmCloudPmId(cursor.getString(3));
            profileMobileMapping.setMpmIsRcp(cursor.getString(4));

            cursor.close();
        }

        db.close();

        // return Profile Mobile Mapping
        return profileMobileMapping;
    }

    // Getting single Profile Mobile Mapping from MobileNumber
    public boolean getIsMobileNumberExists(String mobileNumber) {

        int count = 0;

        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PB_PROFILE_MOBILE_MAPPING, new String[]{COLUMN_MPM_ID,
                        COLUMN_MPM_CLOUD_MNM_ID, COLUMN_MPM_CLOUD_PM_ID, COLUMN_MPM_IS_RCP},
                COLUMN_MPM_MOBILE_NUMBER + "=?", new String[]{String.valueOf(mobileNumber)},
                null, null, null, null);

        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }

        db.close();

        // return Profile Mobile Mapping Count
        return count > 0;
    }

    // Getting All Profile Mobile Mapping
    public ArrayList<ProfileMobileMapping> getAllProfileMobileMapping() {
        ArrayList<ProfileMobileMapping> arrayListProfileMobileMapping = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PB_PROFILE_MOBILE_MAPPING;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
                profileMobileMapping.setMpmId(cursor.getString(0));
                profileMobileMapping.setMpmMobileNumber(cursor.getString(1));
                profileMobileMapping.setMpmCloudMnmId(cursor.getString(2));
                profileMobileMapping.setMpmCloudPmId(cursor.getString(3));
                profileMobileMapping.setMpmIsRcp(cursor.getString(4));
                // Adding profileMobileMapping to list
                arrayListProfileMobileMapping.add(profileMobileMapping);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return profileMobileMapping list
        return arrayListProfileMobileMapping;
    }

    // Getting profileMobileMapping Count
    public int getProfileMobileMappingCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PB_PROFILE_MOBILE_MAPPING;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single profileMobileMapping
    public int updateProfileMobileMapping(ProfileMobileMapping profileMobileMapping) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MPM_ID, profileMobileMapping.getMpmId());
        values.put(COLUMN_MPM_MOBILE_NUMBER, profileMobileMapping.getMpmMobileNumber());
        values.put(COLUMN_MPM_CLOUD_MNM_ID, profileMobileMapping.getMpmCloudMnmId());
        values.put(COLUMN_MPM_CLOUD_PM_ID, profileMobileMapping.getMpmCloudPmId());
        values.put(COLUMN_MPM_IS_RCP, profileMobileMapping.getMpmIsRcp());

        // updating row
        int isUpdated = db.update(TABLE_PB_PROFILE_MOBILE_MAPPING, values, COLUMN_MPM_ID + " = ?",
                new String[]{String.valueOf(profileMobileMapping.getMpmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single profileMobileMapping
    public void deleteProfileMobileMapping(ProfileMobileMapping profileMobileMapping) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_PB_PROFILE_MOBILE_MAPPING, COLUMN_MPM_ID + " = ?",
                new String[]{String.valueOf(profileMobileMapping.getMpmId())});
        db.close();
    }
}
