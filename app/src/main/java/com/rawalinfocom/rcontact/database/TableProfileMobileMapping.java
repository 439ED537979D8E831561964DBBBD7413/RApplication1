package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;

import java.util.ArrayList;

import static com.rawalinfocom.rcontact.database.TableProfileEmailMapping.COLUMN_EPM_CLOUD_PM_ID;
import static com.rawalinfocom.rcontact.database.TableProfileEmailMapping.COLUMN_EPM_IS_RCP;
import static com.rawalinfocom.rcontact.database.TableProfileEmailMapping
        .TABLE_PB_PROFILE_EMAIL_MAPPING;
import static com.rawalinfocom.rcontact.database.TableProfileMaster.COLUMN_PM_FIRST_NAME;
import static com.rawalinfocom.rcontact.database.TableProfileMaster.COLUMN_PM_LAST_NAME;
import static com.rawalinfocom.rcontact.database.TableProfileMaster.COLUMN_PM_PROFILE_IMAGE;
import static com.rawalinfocom.rcontact.database.TableProfileMaster.COLUMN_PM_PROFILE_RATE_USER;
import static com.rawalinfocom.rcontact.database.TableProfileMaster.COLUMN_PM_PROFILE_RATING;
import static com.rawalinfocom.rcontact.database.TableProfileMaster.COLUMN_PM_RAW_ID;
import static com.rawalinfocom.rcontact.database.TableProfileMaster.COLUMN_PM_RCP_ID;
import static com.rawalinfocom.rcontact.database.TableProfileMaster.TABLE_RC_PROFILE_MASTER;

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
    static final String TABLE_PB_PROFILE_MOBILE_MAPPING = "pb_profile_mobile_mapping";

    // Column Names
    private static final String COLUMN_MPM_ID = "mpm_id";
    static final String COLUMN_MPM_MOBILE_NUMBER = "mpm_mobile_number";
    private static final String COLUMN_MPM_CLOUD_MNM_ID = "mpm_cloud_mnm_id";
    static final String COLUMN_MPM_CLOUD_PM_ID = "mpm_cloud_pm_id";
    private static final String COLUMN_MPM_IS_RCP = "mpm_is_rcp";


    // Table Create Statements
    static final String CREATE_TABLE_PB_PROFILE_MOBILE_MAPPING = "CREATE TABLE " +
            TABLE_PB_PROFILE_MOBILE_MAPPING + " (" +
            " " + COLUMN_MPM_ID + " integer NOT NULL CONSTRAINT pb_profile_mobile_mapping_pk " +
            "PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_MPM_MOBILE_NUMBER + " text NOT NULL," +
            " " + COLUMN_MPM_CLOUD_MNM_ID + " integer," +
            " " + COLUMN_MPM_CLOUD_PM_ID + " integer," +
            " " + COLUMN_MPM_IS_RCP + " tinyint DEFAULT 0," +
            " UNIQUE(" + COLUMN_MPM_CLOUD_PM_ID + ", " + COLUMN_MPM_MOBILE_NUMBER + ")" +
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

    // Getting Profile Mobile Mapping from Mobile Numbers
    public ArrayList<ProfileMobileMapping> getProfileMobileMappingFromNumber(String[]
                                                                                     mobileNumbers) {

        ArrayList<ProfileMobileMapping> arrayListProfileMapping = new ArrayList<>();

        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        String query = "SELECT " + COLUMN_MPM_MOBILE_NUMBER + ", " + COLUMN_MPM_CLOUD_PM_ID + " " +
                "FROM " + TABLE_PB_PROFILE_MOBILE_MAPPING + " where " + COLUMN_MPM_IS_RCP + " = 1" +
                " and " + COLUMN_MPM_MOBILE_NUMBER + " IN (" + makePlaceholders(mobileNumbers
                .length) + ")";

        Cursor cursor = db.rawQuery(query, mobileNumbers);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
                profileMobileMapping.setMpmMobileNumber(cursor.getString(0));
                profileMobileMapping.setMpmCloudPmId(cursor.getString(1));

                // Adding profileMapping to list
                arrayListProfileMapping.add(profileMobileMapping);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return profileMapping list
        return arrayListProfileMapping;
    }

    public ProfileMobileMapping getCloudPmIdFromProfileMappingFromNumber(String mobileNumber) {

        ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();

        try {

            String cloudPmId = "";
            SQLiteDatabase db = databaseHandler.getReadableDatabase();

            String query = "SELECT " + COLUMN_MPM_CLOUD_PM_ID +
                    " FROM " + TABLE_PB_PROFILE_MOBILE_MAPPING + " where " + COLUMN_MPM_MOBILE_NUMBER + " = \"" + mobileNumber + "\"";

            Cursor cursor = db.rawQuery(query, null);

            // looping through all rows and adding to list
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                profileMobileMapping.setMpmCloudPmId(cursor.getString(0));
            } else {
                profileMobileMapping = null;
            }
            cursor.close();
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return profileMobileMapping;
    }

    // Getting Profile Mobile Mapping from pm id
    public ArrayList<ProfileMobileMapping> getProfileMobileMappingPmId(int pmId) {

        ArrayList<ProfileMobileMapping> arrayListProfileMapping = new ArrayList<>();

        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        String query = "SELECT " + COLUMN_MPM_MOBILE_NUMBER + " FROM " +
                TABLE_PB_PROFILE_MOBILE_MAPPING + " where " + COLUMN_MPM_IS_RCP + " = 1 and " +
                COLUMN_MPM_CLOUD_PM_ID + " = " + pmId;

        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
                profileMobileMapping.setMpmMobileNumber(cursor.getString(0));

                // Adding profileMapping to list
                arrayListProfileMapping.add(profileMobileMapping);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return profileMapping list
        return arrayListProfileMapping;
    }

    // Getting single Profile Mobile Mapping from MobileNumber
    public boolean getIsMobileNumberExists(String mobileNumber) {

        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_PB_PROFILE_MOBILE_MAPPING,
                COLUMN_MPM_MOBILE_NUMBER + "=?", new String[]{mobileNumber});
        db.close();

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

    String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException(RContactApplication.getInstance().getString(R.string.error_no_placeholders));
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    // Getting RContact List
    public ArrayList<UserProfile> getRContactList() {
        ArrayList<UserProfile> arrayListRContact = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + TABLE_RC_PROFILE_MASTER + "." +
                COLUMN_PM_FIRST_NAME +
                "," + TABLE_RC_PROFILE_MASTER + "." + COLUMN_PM_LAST_NAME + "," +
                TABLE_RC_PROFILE_MASTER + "." + COLUMN_PM_RCP_ID + "," +
                TABLE_RC_PROFILE_MASTER + "." + COLUMN_PM_RAW_ID + "," +
                TABLE_RC_PROFILE_MASTER + "." + COLUMN_PM_PROFILE_RATING + "," +
                TABLE_RC_PROFILE_MASTER + "." + COLUMN_PM_PROFILE_RATE_USER + "," +
                TABLE_RC_PROFILE_MASTER + "." + COLUMN_PM_PROFILE_IMAGE + "," +
//                TABLE_PB_PROFILE_EMAIL_MAPPING + "." + COLUMN_EPM_EMAIL_ID + "," +
                TABLE_PB_PROFILE_MOBILE_MAPPING + "." + COLUMN_MPM_MOBILE_NUMBER + " FROM " +
                TABLE_RC_PROFILE_MASTER + " LEFT JOIN " + TABLE_PB_PROFILE_MOBILE_MAPPING + " ON " +
                "" + TABLE_RC_PROFILE_MASTER + "." + COLUMN_PM_RCP_ID + " = " +
                TABLE_PB_PROFILE_MOBILE_MAPPING + "." + COLUMN_MPM_CLOUD_PM_ID + " LEFT JOIN " +
                TABLE_PB_PROFILE_EMAIL_MAPPING + " ON " + TABLE_RC_PROFILE_MASTER + "." +
                COLUMN_PM_RCP_ID + " = " + TABLE_PB_PROFILE_EMAIL_MAPPING + "." +
                COLUMN_EPM_CLOUD_PM_ID + " WHERE " + TABLE_PB_PROFILE_MOBILE_MAPPING + "." +
                COLUMN_MPM_IS_RCP + " = 1 OR " + TABLE_PB_PROFILE_EMAIL_MAPPING + "." +
                COLUMN_EPM_IS_RCP + " = 1 ORDER BY UPPER(" + TABLE_RC_PROFILE_MASTER + "." +
                COLUMN_PM_FIRST_NAME + ")";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserProfile userProfile = new UserProfile();
                userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                        (TableProfileMaster.COLUMN_PM_FIRST_NAME)));
                userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                        (TableProfileMaster.COLUMN_PM_LAST_NAME)));
                userProfile.setPmId(cursor.getString(cursor.getColumnIndex
                        (TableProfileMaster.COLUMN_PM_RCP_ID)));
                userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex
                        (TableProfileMaster.COLUMN_PM_RAW_ID)));
                userProfile.setProfileRating(cursor.getString(cursor.getColumnIndex
                        (TableProfileMaster.COLUMN_PM_PROFILE_RATING)));
                userProfile.setTotalProfileRateUser(cursor.getString(cursor.getColumnIndex
                        (TableProfileMaster.COLUMN_PM_PROFILE_RATE_USER)));
                userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                        (TableProfileMaster.COLUMN_PM_PROFILE_IMAGE)));
               /* userProfile.setEmailId(cursor.getString(cursor.getColumnIndex
                        (TableProfileEmailMapping.COLUMN_EPM_EMAIL_ID)));*/
                userProfile.setMobileNumber(cursor.getString(cursor.getColumnIndex
                        (TableProfileMobileMapping.COLUMN_MPM_MOBILE_NUMBER)));
                // Adding profileMobileMapping to list
                arrayListRContact.add(userProfile);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return RContact list
        return arrayListRContact;
    }
}
