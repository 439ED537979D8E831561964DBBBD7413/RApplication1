package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.UserProfile;

import java.util.ArrayList;

/**
 * Created by Monal on 14/11/16.
 */

public class TableProfileMaster {

    Context context;
    DatabaseHandler databaseHandler;

    public TableProfileMaster(Context context, DatabaseHandler databaseHandler) {
        this.context = context;
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_PROFILE_MASTER = "rc_profile_master";

    // Column Names
    private static final String COLUMN_PM_ID = "pm_id";
    private static final String COLUMN_PM_RAW_ID = "pm_raw_id";
    private static final String COLUMN_PM_PREFIX = "pm_prefix";
    private static final String COLUMN_PM_FIRST_NAME = "pm_first_name";
    private static final String COLUMN_PM_MIDDLE_NAME = "pm_middle_name";
    private static final String COLUMN_PM_LAST_NAME = "pm_last_name";
    private static final String COLUMN_PM_SUFFIX = "pm_suffix";
    private static final String COLUMN_PM_NICK_NAME = "pm_nick_name";
    private static final String COLUMN_PM_PHONETIC_FIRST_NAME = "pm_phonetic_first_name";
    private static final String COLUMN_PM_PHONETIC_MIDDLE_NAME = "pm_phonetic_middle_name";
    private static final String COLUMN_PM_PHONETIC_LAST_NAME = "pm_phonetic_last_name";
    private static final String COLUMN_PM_PROFILE_IMAGE = "pm_profile_image";
    private static final String COLUMN_PM_RCP_ID = "pm_rcp_id";
    private static final String COLUMN_PM_NICK_NAME_PRIVACY = "pm_nick_name_privacy";
    private static final String COLUMN_PM_NOTES = "pm_notes";
    private static final String COLUMN_PM_NOTES_PRIVACY = "pm_notes_privacy";
    private static final String COLUMN_PM_GENDER = "pm_gender";
    private static final String COLUMN_PM_GENDER_PRIVACY = "pm_gender_privacy";
    private static final String COLUMN_PM_IS_FAVOURITE = "pm_is_favourite";
    private static final String COLUMN_PM_IS_FAVOURITE_PRIVACY = "pm_is_favourite_privacy";
    private static final String COLUMN_PM_ACCESS_TOKEN = "pm_access_token";
    private static final String COLUMN_PM_NOSQL_MASTER_ID = "pm_nosql_master_id";
    private static final String COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE = "pm_signup_social_media_type";
    private static final String COLUMN_PM_UPDATED_AT = "pm_updated_at";
    private static final String COLUMN_PM_UPDATED_DATA = "pm_updated_data";
    private static final String COLUMN_PM_DELETED_AT = "pm_deleted_at";

    // Table Create Statements
    public static final String CREATE_TABLE_RC_PROFILE_MASTER = "CREATE TABLE " +
            TABLE_RC_PROFILE_MASTER + " (" +
            " " + COLUMN_PM_ID + " integer NOT NULL CONSTRAINT rc_profile_master_pk PRIMARY KEY " +
            "AUTOINCREMENT," +
            " " + COLUMN_PM_RAW_ID + " text," +
            " " + COLUMN_PM_PREFIX + " text," +
            " " + COLUMN_PM_FIRST_NAME + " text NOT NULL," +
            " " + COLUMN_PM_MIDDLE_NAME + " text," +
            " " + COLUMN_PM_LAST_NAME + " text," +
            " " + COLUMN_PM_SUFFIX + " text," +
            " " + COLUMN_PM_NICK_NAME + " text," +
            " " + COLUMN_PM_PHONETIC_FIRST_NAME + " text," +
            " " + COLUMN_PM_PHONETIC_MIDDLE_NAME + " text," +
            " " + COLUMN_PM_PHONETIC_LAST_NAME + " text," +
            " " + COLUMN_PM_PROFILE_IMAGE + " text," +
            " " + COLUMN_PM_RCP_ID + " integer," +
            " " + COLUMN_PM_NICK_NAME_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_PM_NOTES + " text," +
            " " + COLUMN_PM_NOTES_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_PM_GENDER + " text," +
            " " + COLUMN_PM_GENDER_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_PM_IS_FAVOURITE + " integer," +
            " " + COLUMN_PM_IS_FAVOURITE_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_PM_ACCESS_TOKEN + " text NOT NULL," +
            " " + COLUMN_PM_NOSQL_MASTER_ID + " text," +
            " " + COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE + " integer," +
            " " + COLUMN_PM_UPDATED_AT + " datetime," +
            " " + COLUMN_PM_UPDATED_DATA + " integer," +
            " " + COLUMN_PM_DELETED_AT + " datetime" +
            ");";

    // Adding new Profile
    public void addProfile(UserProfile userProfile) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PM_ID, userProfile.getPmId());
        values.put(COLUMN_PM_RAW_ID, userProfile.getPmRawId());
        values.put(COLUMN_PM_PREFIX, userProfile.getPmPrefix());
        values.put(COLUMN_PM_FIRST_NAME, userProfile.getPmFirstName());
        values.put(COLUMN_PM_MIDDLE_NAME, userProfile.getPmMiddleName());
        values.put(COLUMN_PM_LAST_NAME, userProfile.getPmLastName());
        values.put(COLUMN_PM_SUFFIX, userProfile.getPmSuffix());
        values.put(COLUMN_PM_NICK_NAME, userProfile.getPmNickName());
        values.put(COLUMN_PM_PHONETIC_FIRST_NAME, userProfile.getPmPhoneticFirstName());
        values.put(COLUMN_PM_PHONETIC_MIDDLE_NAME, userProfile.getPmPhoneticMiddleName());
        values.put(COLUMN_PM_PHONETIC_LAST_NAME, userProfile.getPmPhoneticLastName());
        values.put(COLUMN_PM_PROFILE_IMAGE, userProfile.getPmProfileImage());
        values.put(COLUMN_PM_RCP_ID, userProfile.getPmRcpId());
        values.put(COLUMN_PM_NICK_NAME_PRIVACY, userProfile.getPmNickNamePrivacy());
        values.put(COLUMN_PM_NOTES, userProfile.getPmNotes());
        values.put(COLUMN_PM_NOTES_PRIVACY, userProfile.getPmNotesPrivacy());
        values.put(COLUMN_PM_GENDER, userProfile.getPmGender());
        values.put(COLUMN_PM_GENDER_PRIVACY, userProfile.getPmGenderPrivacy());
        values.put(COLUMN_PM_IS_FAVOURITE, userProfile.getPmIsFavourite());
        values.put(COLUMN_PM_IS_FAVOURITE_PRIVACY, userProfile.getPmIsFavouritePrivacy());
        values.put(COLUMN_PM_ACCESS_TOKEN, userProfile.getPmAccessToken());
        values.put(COLUMN_PM_NOSQL_MASTER_ID, userProfile.getPmNosqlMasterId());
        values.put(COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE, userProfile.getPmSignupSocialMediaType());
        values.put(COLUMN_PM_UPDATED_AT, userProfile.getPmUpdatedAt());
        values.put(COLUMN_PM_UPDATED_DATA, userProfile.getPmUpdatedData());
        values.put(COLUMN_PM_DELETED_AT, userProfile.getPmDeletedAt());

        // Inserting Row
        db.insert(TABLE_RC_PROFILE_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Profile
    public UserProfile getProfile(int profileId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_PROFILE_MASTER, new String[]{COLUMN_PM_ID,
                COLUMN_PM_RAW_ID, COLUMN_PM_PREFIX, COLUMN_PM_FIRST_NAME, COLUMN_PM_MIDDLE_NAME,
                COLUMN_PM_LAST_NAME, COLUMN_PM_SUFFIX, COLUMN_PM_NICK_NAME,
                COLUMN_PM_PHONETIC_FIRST_NAME, COLUMN_PM_PHONETIC_MIDDLE_NAME,
                COLUMN_PM_PHONETIC_LAST_NAME, COLUMN_PM_PROFILE_IMAGE, COLUMN_PM_RCP_ID,
                COLUMN_PM_NICK_NAME_PRIVACY, COLUMN_PM_NOTES, COLUMN_PM_NOTES_PRIVACY,
                COLUMN_PM_GENDER, COLUMN_PM_GENDER_PRIVACY, COLUMN_PM_IS_FAVOURITE,
                COLUMN_PM_IS_FAVOURITE_PRIVACY, COLUMN_PM_ACCESS_TOKEN, COLUMN_PM_NOSQL_MASTER_ID,
                COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE, COLUMN_PM_UPDATED_AT, COLUMN_PM_UPDATED_DATA,
                COLUMN_PM_DELETED_AT}, COLUMN_PM_ID + "=?", new String[]{String.valueOf
                (profileId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        UserProfile userProfile = new UserProfile();
        if (cursor != null) {
            userProfile.setPmId(cursor.getString(0));
            userProfile.setPmRawId(cursor.getString(1));
            userProfile.setPmPrefix(cursor.getString(2));
            userProfile.setPmFirstName(cursor.getString(3));
            userProfile.setPmMiddleName(cursor.getString(4));
            userProfile.setPmLastName(cursor.getString(5));
            userProfile.setPmSuffix(cursor.getString(6));
            userProfile.setPmNickName(cursor.getString(7));
            userProfile.setPmPhoneticFirstName(cursor.getString(8));
            userProfile.setPmPhoneticMiddleName(cursor.getString(9));
            userProfile.setPmPhoneticLastName(cursor.getString(10));
            userProfile.setPmProfileImage(cursor.getString(11));
            userProfile.setPmRcpId(cursor.getString(12));
            userProfile.setPmNickNamePrivacy(cursor.getString(13));
            userProfile.setPmNotes(cursor.getString(14));
            userProfile.setPmNotesPrivacy(cursor.getString(15));
            userProfile.setPmGender(cursor.getString(16));
            userProfile.setPmGenderPrivacy(cursor.getString(17));
            userProfile.setPmIsFavourite(cursor.getString(18));
            userProfile.setPmIsFavouritePrivacy(cursor.getString(19));
            userProfile.setPmAccessToken(cursor.getString(20));
            userProfile.setPmNosqlMasterId(cursor.getString(21));
            userProfile.setPmSignupSocialMediaType(cursor.getString(22));
            userProfile.setPmUpdatedAt(cursor.getString(23));
            userProfile.setPmUpdatedData(cursor.getString(24));
            userProfile.setPmDeletedAt(cursor.getString(25));
        }
        // return Profile
        return userProfile;
    }

    // Getting All Profiles
    public ArrayList<UserProfile> getAllUserProfiles() {
        ArrayList<UserProfile> arrayListUserProfile = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_PROFILE_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserProfile userProfile = new UserProfile();
                userProfile.setPmId(cursor.getString(0));
                userProfile.setPmRawId(cursor.getString(1));
                userProfile.setPmPrefix(cursor.getString(2));
                userProfile.setPmFirstName(cursor.getString(3));
                userProfile.setPmMiddleName(cursor.getString(4));
                userProfile.setPmLastName(cursor.getString(5));
                userProfile.setPmSuffix(cursor.getString(6));
                userProfile.setPmNickName(cursor.getString(7));
                userProfile.setPmPhoneticFirstName(cursor.getString(8));
                userProfile.setPmPhoneticMiddleName(cursor.getString(9));
                userProfile.setPmPhoneticLastName(cursor.getString(10));
                userProfile.setPmProfileImage(cursor.getString(11));
                userProfile.setPmRcpId(cursor.getString(12));
                userProfile.setPmNickNamePrivacy(cursor.getString(13));
                userProfile.setPmNotes(cursor.getString(14));
                userProfile.setPmNotesPrivacy(cursor.getString(15));
                userProfile.setPmGender(cursor.getString(16));
                userProfile.setPmGenderPrivacy(cursor.getString(17));
                userProfile.setPmIsFavourite(cursor.getString(18));
                userProfile.setPmIsFavouritePrivacy(cursor.getString(19));
                userProfile.setPmAccessToken(cursor.getString(20));
                userProfile.setPmNosqlMasterId(cursor.getString(21));
                userProfile.setPmSignupSocialMediaType(cursor.getString(22));
                userProfile.setPmUpdatedAt(cursor.getString(23));
                userProfile.setPmUpdatedData(cursor.getString(24));
                userProfile.setPmDeletedAt(cursor.getString(25));
                // Adding user profile to list
                arrayListUserProfile.add(userProfile);
            } while (cursor.moveToNext());
        }

        // return user profile list
        return arrayListUserProfile;
    }

    // Getting user profile Count
    public int getUserProfileCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_PROFILE_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single profile
    public int updateUserProfile(UserProfile userProfile) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PM_ID, userProfile.getPmId());
        values.put(COLUMN_PM_RAW_ID, userProfile.getPmRawId());
        values.put(COLUMN_PM_PREFIX, userProfile.getPmPrefix());
        values.put(COLUMN_PM_FIRST_NAME, userProfile.getPmFirstName());
        values.put(COLUMN_PM_MIDDLE_NAME, userProfile.getPmMiddleName());
        values.put(COLUMN_PM_LAST_NAME, userProfile.getPmLastName());
        values.put(COLUMN_PM_SUFFIX, userProfile.getPmSuffix());
        values.put(COLUMN_PM_NICK_NAME, userProfile.getPmNickName());
        values.put(COLUMN_PM_PHONETIC_FIRST_NAME, userProfile.getPmPhoneticFirstName());
        values.put(COLUMN_PM_PHONETIC_MIDDLE_NAME, userProfile.getPmPhoneticMiddleName());
        values.put(COLUMN_PM_PHONETIC_LAST_NAME, userProfile.getPmPhoneticLastName());
        values.put(COLUMN_PM_PROFILE_IMAGE, userProfile.getPmProfileImage());
        values.put(COLUMN_PM_RCP_ID, userProfile.getPmRcpId());
        values.put(COLUMN_PM_NICK_NAME_PRIVACY, userProfile.getPmNickNamePrivacy());
        values.put(COLUMN_PM_NOTES, userProfile.getPmNotes());
        values.put(COLUMN_PM_NOTES_PRIVACY, userProfile.getPmNotesPrivacy());
        values.put(COLUMN_PM_GENDER, userProfile.getPmGender());
        values.put(COLUMN_PM_GENDER_PRIVACY, userProfile.getPmGenderPrivacy());
        values.put(COLUMN_PM_IS_FAVOURITE, userProfile.getPmIsFavourite());
        values.put(COLUMN_PM_IS_FAVOURITE_PRIVACY, userProfile.getPmIsFavouritePrivacy());
        values.put(COLUMN_PM_ACCESS_TOKEN, userProfile.getPmAccessToken());
        values.put(COLUMN_PM_NOSQL_MASTER_ID, userProfile.getPmNosqlMasterId());
        values.put(COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE, userProfile.getPmSignupSocialMediaType());
        values.put(COLUMN_PM_UPDATED_AT, userProfile.getPmUpdatedAt());
        values.put(COLUMN_PM_UPDATED_DATA, userProfile.getPmUpdatedData());
        values.put(COLUMN_PM_DELETED_AT, userProfile.getPmDeletedAt());

        // updating row
        return db.update(TABLE_RC_PROFILE_MASTER, values, COLUMN_PM_ID + " = ?",
                new String[]{String.valueOf(userProfile.getPmId())});
    }

    // Deleting single profile
    public void deleteUserProfile(UserProfile userProfile) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_PROFILE_MASTER, COLUMN_PM_ID + " = ?",
                new String[]{String.valueOf(userProfile.getPmId())});
        db.close();
    }
}
