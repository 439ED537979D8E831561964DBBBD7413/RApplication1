package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.UserProfile;

import java.util.ArrayList;

/**
 * Created by Monal on 14/11/16.
 * <p>
 * Table operations rc_profile_master
 */

public class TableProfileMaster {

    private DatabaseHandler databaseHandler;

    public TableProfileMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_PROFILE_MASTER = "rc_profile_master";

    // Column Names
    private static final String COLUMN_PM_ID = "pm_id";
    static final String COLUMN_PM_RAW_ID = "pm_raw_id";
    static final String COLUMN_PM_PREFIX = "pm_prefix";
    static final String COLUMN_PM_FIRST_NAME = "pm_first_name";
    static final String COLUMN_PM_MIDDLE_NAME = "pm_middle_name";
    static final String COLUMN_PM_LAST_NAME = "pm_last_name";
    static final String COLUMN_PM_SUFFIX = "pm_suffix";
    static final String COLUMN_PM_NICK_NAME = "pm_nick_name";
    static final String COLUMN_PM_PHONETIC_FIRST_NAME = "pm_phonetic_first_name";
    static final String COLUMN_PM_PHONETIC_MIDDLE_NAME = "pm_phonetic_middle_name";
    static final String COLUMN_PM_PHONETIC_LAST_NAME = "pm_phonetic_last_name";
    static final String COLUMN_PM_PROFILE_IMAGE = "pm_profile_image";
    static final String COLUMN_PM_RCP_ID = "pm_rcp_id";
    static final String COLUMN_PM_NICK_NAME_PRIVACY = "pm_nick_name_privacy";
    static final String COLUMN_PM_NOTES = "pm_notes";
    static final String COLUMN_PM_NOTES_PRIVACY = "pm_notes_privacy";
    static final String COLUMN_PM_GENDER = "pm_gender";
    static final String COLUMN_PM_GENDER_PRIVACY = "pm_gender_privacy";
    static final String COLUMN_PM_PROFILE_RATING = "pm_profile_rating";
    static final String COLUMN_PM_PROFILE_RATE_USER = "pm_total_user_rating";
    static final String COLUMN_PM_IS_FAVOURITE = "pm_is_favourite";
    private static final String COLUMN_PM_ACCESS_TOKEN = "pm_access_token";
    private static final String COLUMN_PM_NOSQL_MASTER_ID = "pm_nosql_master_id";
    private static final String COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE = "pm_signup_social_media_type";
    private static final String COLUMN_PM_JOINING_DATE = "pm_joining_date";

    // Table Create Statements
    static final String CREATE_TABLE_RC_PROFILE_MASTER = "CREATE TABLE " +
            TABLE_RC_PROFILE_MASTER + " (" +
            " " + COLUMN_PM_ID + " integer NOT NULL CONSTRAINT rc_profile_master_pk PRIMARY KEY " +
            "AUTOINCREMENT," +
            " " + COLUMN_PM_RAW_ID + " text," +
            " " + COLUMN_PM_RCP_ID + " integer UNIQUE," +
            " " + COLUMN_PM_PREFIX + " text," +
            " " + COLUMN_PM_FIRST_NAME + " text," +
            " " + COLUMN_PM_MIDDLE_NAME + " text," +
            " " + COLUMN_PM_LAST_NAME + " text," +
            " " + COLUMN_PM_SUFFIX + " text," +
            " " + COLUMN_PM_NICK_NAME + " text," +
            " " + COLUMN_PM_PHONETIC_FIRST_NAME + " text," +
            " " + COLUMN_PM_PHONETIC_MIDDLE_NAME + " text," +
            " " + COLUMN_PM_PHONETIC_LAST_NAME + " text," +
            " " + COLUMN_PM_PROFILE_IMAGE + " text," +
            " " + COLUMN_PM_NICK_NAME_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_PM_NOTES + " text," +
            " " + COLUMN_PM_NOTES_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_PM_GENDER + " text," +
            " " + COLUMN_PM_GENDER_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_PM_PROFILE_RATING + " integer," +
            " " + COLUMN_PM_PROFILE_RATE_USER + " integer," +
            " " + COLUMN_PM_IS_FAVOURITE + " integer," +
            " " + COLUMN_PM_ACCESS_TOKEN + " text," +
            " " + COLUMN_PM_NOSQL_MASTER_ID + " text," +
            " " + COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE + " integer," +
            " " + COLUMN_PM_JOINING_DATE + " text" +
            ");";

    // Adding new Profile
    public void addProfile(UserProfile userProfile) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(COLUMN_PM_ID, userProfile.getPmId());
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
        values.put(COLUMN_PM_PROFILE_RATING, userProfile.getProfileRating());
        values.put(COLUMN_PM_PROFILE_RATE_USER, userProfile.getTotalProfileRateUser());
        values.put(COLUMN_PM_IS_FAVOURITE, userProfile.getPmIsFavourite());
        values.put(COLUMN_PM_ACCESS_TOKEN, userProfile.getPmAccessToken());
        values.put(COLUMN_PM_NOSQL_MASTER_ID, userProfile.getPmNosqlMasterId());
        values.put(COLUMN_PM_JOINING_DATE, userProfile.getPmJoiningDate());

        // Inserting Row
        db.insert(TABLE_RC_PROFILE_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding Array new Profile
    public void addArrayProfile(ArrayList<UserProfile> arrayListUserProfile) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        for (int i = 0; i < arrayListUserProfile.size(); i++) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_PM_RAW_ID, arrayListUserProfile.get(i).getPmRawId());
            values.put(COLUMN_PM_PREFIX, arrayListUserProfile.get(i).getPmPrefix());
            values.put(COLUMN_PM_FIRST_NAME, arrayListUserProfile.get(i).getPmFirstName());
            values.put(COLUMN_PM_MIDDLE_NAME, arrayListUserProfile.get(i).getPmMiddleName());
            values.put(COLUMN_PM_LAST_NAME, arrayListUserProfile.get(i).getPmLastName());
            values.put(COLUMN_PM_SUFFIX, arrayListUserProfile.get(i).getPmSuffix());
            values.put(COLUMN_PM_NICK_NAME, arrayListUserProfile.get(i).getPmNickName());
            values.put(COLUMN_PM_PHONETIC_FIRST_NAME, arrayListUserProfile.get(i)
                    .getPmPhoneticFirstName());
            values.put(COLUMN_PM_PHONETIC_MIDDLE_NAME, arrayListUserProfile.get(i)
                    .getPmPhoneticMiddleName());
            values.put(COLUMN_PM_PHONETIC_LAST_NAME, arrayListUserProfile.get(i)
                    .getPmPhoneticLastName());
            values.put(COLUMN_PM_PROFILE_IMAGE, arrayListUserProfile.get(i).getPmProfileImage());
            values.put(COLUMN_PM_RCP_ID, arrayListUserProfile.get(i).getPmRcpId());
            values.put(COLUMN_PM_NICK_NAME_PRIVACY, arrayListUserProfile.get(i)
                    .getPmNickNamePrivacy());
            values.put(COLUMN_PM_NOTES, arrayListUserProfile.get(i).getPmNotes());
            values.put(COLUMN_PM_NOTES_PRIVACY, arrayListUserProfile.get(i).getPmNotesPrivacy());
            values.put(COLUMN_PM_GENDER, arrayListUserProfile.get(i).getPmGender());
            values.put(COLUMN_PM_GENDER_PRIVACY, arrayListUserProfile.get(i).getPmGenderPrivacy());
            values.put(COLUMN_PM_PROFILE_RATING, arrayListUserProfile.get(i).getProfileRating());
            values.put(COLUMN_PM_PROFILE_RATE_USER, arrayListUserProfile.get(i)
                    .getTotalProfileRateUser());
            values.put(COLUMN_PM_IS_FAVOURITE, arrayListUserProfile.get(i).getPmIsFavourite());
            values.put(COLUMN_PM_ACCESS_TOKEN, arrayListUserProfile.get(i).getPmAccessToken());
            values.put(COLUMN_PM_NOSQL_MASTER_ID, arrayListUserProfile.get(i).getPmNosqlMasterId());
            values.put(COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE, arrayListUserProfile.get(i)
                    .getPmSignupSocialMediaType());
            values.put(COLUMN_PM_JOINING_DATE, arrayListUserProfile.get(i).getPmJoiningDate());

            // Inserting Row
            db.insert(TABLE_RC_PROFILE_MASTER, null, values);
        }
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
                COLUMN_PM_GENDER, COLUMN_PM_GENDER_PRIVACY, COLUMN_PM_PROFILE_RATING,
                COLUMN_PM_PROFILE_RATE_USER, COLUMN_PM_IS_FAVOURITE, COLUMN_PM_ACCESS_TOKEN,
                COLUMN_PM_NOSQL_MASTER_ID, COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE,
                COLUMN_PM_JOINING_DATE}, COLUMN_PM_ID + "=?", new String[]{String.valueOf
                (profileId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        UserProfile userProfile = new UserProfile();
        if (cursor != null) {
            userProfile.setPmId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_ID)));
            userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RAW_ID)));
            userProfile.setPmPrefix(cursor.getString(cursor.getColumnIndex(COLUMN_PM_PREFIX)));
            userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_FIRST_NAME)));
            userProfile.setPmMiddleName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_MIDDLE_NAME)));
            userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex(COLUMN_PM_LAST_NAME)));
            userProfile.setPmSuffix(cursor.getString(cursor.getColumnIndex(COLUMN_PM_SUFFIX)));
            userProfile.setPmNickName(cursor.getString(cursor.getColumnIndex(COLUMN_PM_NICK_NAME)));
            userProfile.setPmPhoneticFirstName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PHONETIC_FIRST_NAME)));
            userProfile.setPmPhoneticMiddleName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PHONETIC_MIDDLE_NAME)));
            userProfile.setPmPhoneticLastName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PHONETIC_LAST_NAME)));
            userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PROFILE_IMAGE)));
            userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RCP_ID)));
            userProfile.setPmNickNamePrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_NICK_NAME_PRIVACY)));
            userProfile.setPmNotes(cursor.getString(cursor.getColumnIndex(COLUMN_PM_NOTES)));
            userProfile.setPmNotesPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_NOTES_PRIVACY)));
            userProfile.setPmGender(cursor.getString(cursor.getColumnIndex(COLUMN_PM_GENDER)));
            userProfile.setPmGenderPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_GENDER_PRIVACY)));
            userProfile.setProfileRating(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PROFILE_RATING)));
            userProfile.setTotalProfileRateUser(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PROFILE_RATE_USER)));
            userProfile.setPmIsFavourite(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_IS_FAVOURITE)));
            userProfile.setPmAccessToken(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_ACCESS_TOKEN)));
            userProfile.setPmNosqlMasterId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_NOSQL_MASTER_ID)));
            userProfile.setPmSignupSocialMediaType(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE)));
            userProfile.setPmJoiningDate(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_JOINING_DATE)));

            cursor.close();
        }

        db.close();

        // return Profile
        return userProfile;
    }

    // Getting single Profile from Cloud Pm id
    public UserProfile getProfileFromCloudPmId(int cloudPmd) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_PROFILE_MASTER, new String[]{COLUMN_PM_ID,
                COLUMN_PM_RAW_ID, COLUMN_PM_PREFIX, COLUMN_PM_FIRST_NAME, COLUMN_PM_MIDDLE_NAME,
                COLUMN_PM_LAST_NAME, COLUMN_PM_SUFFIX, COLUMN_PM_NICK_NAME,
                COLUMN_PM_PHONETIC_FIRST_NAME, COLUMN_PM_PHONETIC_MIDDLE_NAME,
                COLUMN_PM_PHONETIC_LAST_NAME, COLUMN_PM_PROFILE_IMAGE, COLUMN_PM_RCP_ID,
                COLUMN_PM_NICK_NAME_PRIVACY, COLUMN_PM_NOTES, COLUMN_PM_NOTES_PRIVACY,
                COLUMN_PM_GENDER, COLUMN_PM_GENDER_PRIVACY, COLUMN_PM_PROFILE_RATING,
                COLUMN_PM_PROFILE_RATE_USER, COLUMN_PM_IS_FAVOURITE,
                COLUMN_PM_ACCESS_TOKEN, COLUMN_PM_NOSQL_MASTER_ID,
                COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE, COLUMN_PM_JOINING_DATE}, COLUMN_PM_RCP_ID +
                "=?", new String[]{String
                .valueOf(cloudPmd)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        UserProfile userProfile = new UserProfile();
        if (cursor != null) {
            userProfile.setPmId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_ID)));
            userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RAW_ID)));
            userProfile.setPmPrefix(cursor.getString(cursor.getColumnIndex(COLUMN_PM_PREFIX)));
            userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_FIRST_NAME)));
            userProfile.setPmMiddleName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_MIDDLE_NAME)));
            userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex(COLUMN_PM_LAST_NAME)));
            userProfile.setPmSuffix(cursor.getString(cursor.getColumnIndex(COLUMN_PM_SUFFIX)));
            userProfile.setPmNickName(cursor.getString(cursor.getColumnIndex(COLUMN_PM_NICK_NAME)));
            userProfile.setPmPhoneticFirstName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PHONETIC_FIRST_NAME)));
            userProfile.setPmPhoneticMiddleName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PHONETIC_MIDDLE_NAME)));
            userProfile.setPmPhoneticLastName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PHONETIC_LAST_NAME)));
            userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PROFILE_IMAGE)));
            userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RCP_ID)));
            userProfile.setPmNickNamePrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_NICK_NAME_PRIVACY)));
            userProfile.setPmNotes(cursor.getString(cursor.getColumnIndex(COLUMN_PM_NOTES)));
            userProfile.setPmNotesPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_NOTES_PRIVACY)));
            userProfile.setPmGender(cursor.getString(cursor.getColumnIndex(COLUMN_PM_GENDER)));
            userProfile.setPmGenderPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_GENDER_PRIVACY)));
            userProfile.setProfileRating(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PROFILE_RATING)));
            userProfile.setTotalProfileRateUser(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_PROFILE_RATE_USER)));
            userProfile.setPmIsFavourite(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_IS_FAVOURITE)));
            userProfile.setPmAccessToken(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_ACCESS_TOKEN)));
            userProfile.setPmNosqlMasterId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_NOSQL_MASTER_ID)));
            userProfile.setPmSignupSocialMediaType(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE)));
            userProfile.setPmJoiningDate(cursor.getString(cursor.getColumnIndex
                    (COLUMN_PM_JOINING_DATE)));

            cursor.close();
        }

        db.close();

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
                userProfile.setPmId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_ID)));
                userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RAW_ID)));
                userProfile.setPmPrefix(cursor.getString(cursor.getColumnIndex(COLUMN_PM_PREFIX)));
                userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_FIRST_NAME)));
                userProfile.setPmMiddleName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_MIDDLE_NAME)));
                userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_LAST_NAME)));
                userProfile.setPmSuffix(cursor.getString(cursor.getColumnIndex(COLUMN_PM_SUFFIX)));
                userProfile.setPmNickName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_NICK_NAME)));
                userProfile.setPmPhoneticFirstName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PHONETIC_FIRST_NAME)));
                userProfile.setPmPhoneticMiddleName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PHONETIC_MIDDLE_NAME)));
                userProfile.setPmPhoneticLastName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PHONETIC_LAST_NAME)));
                userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_IMAGE)));
                userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RCP_ID)));
                userProfile.setPmNickNamePrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_NICK_NAME_PRIVACY)));
                userProfile.setPmNotes(cursor.getString(cursor.getColumnIndex(COLUMN_PM_NOTES)));
                userProfile.setPmNotesPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_NOTES_PRIVACY)));
                userProfile.setPmGender(cursor.getString(cursor.getColumnIndex(COLUMN_PM_GENDER)));
                userProfile.setPmGenderPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_GENDER_PRIVACY)));
                userProfile.setProfileRating(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_RATING)));
                userProfile.setTotalProfileRateUser(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_RATE_USER)));
                userProfile.setPmIsFavourite(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_IS_FAVOURITE)));
                userProfile.setPmAccessToken(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_ACCESS_TOKEN)));
                userProfile.setPmNosqlMasterId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_NOSQL_MASTER_ID)));
                userProfile.setPmSignupSocialMediaType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE)));
                userProfile.setPmJoiningDate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_JOINING_DATE)));
                // Adding user profile to list
                arrayListUserProfile.add(userProfile);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return user profile list
        return arrayListUserProfile;
    }

    // Getting user profile Count
    public int getUserProfileCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_PROFILE_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }

    // Updating single profile
    public int updateUserProfile(UserProfile userProfile) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
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
        values.put(COLUMN_PM_PROFILE_RATING, userProfile.getProfileRating());
        values.put(COLUMN_PM_PROFILE_RATE_USER, userProfile.getTotalProfileRateUser());
        values.put(COLUMN_PM_IS_FAVOURITE, userProfile.getPmIsFavourite());
        values.put(COLUMN_PM_ACCESS_TOKEN, userProfile.getPmAccessToken());
        values.put(COLUMN_PM_NOSQL_MASTER_ID, userProfile.getPmNosqlMasterId());
        values.put(COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE, userProfile.getPmSignupSocialMediaType());
        values.put(COLUMN_PM_JOINING_DATE, userProfile.getPmJoiningDate());

        int isUpdated = db.update(TABLE_RC_PROFILE_MASTER, values, COLUMN_PM_RCP_ID + " = ?",
                new String[]{String.valueOf(userProfile.getPmRcpId())});

        db.close();

        // updating row
        return isUpdated;
    }

    // Updating user rating detail
    public int updateUserProfileRating(String rcpId, String profileRating, String totalRateUser) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PM_PROFILE_RATING, profileRating);
        values.put(COLUMN_PM_PROFILE_RATE_USER, totalRateUser);

        int isUpdated = db.update(TABLE_RC_PROFILE_MASTER, values, COLUMN_PM_RCP_ID + " = ?",
                new String[]{String.valueOf(rcpId)});

        db.close();

        return isUpdated;
    }

    // Deleting single profile
    public void deleteUserProfile(UserProfile userProfile) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_PROFILE_MASTER, COLUMN_PM_ID + " = ?",
                new String[]{String.valueOf(userProfile.getPmId())});
        db.close();
    }

    /*public ProfileDataOperation getRcProfileDetail(Context context, String rcpId) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ProfileDataOperation profileDataOperation = new ProfileDataOperation();

        //<editor-fold desc="Profile Detail">
        // Select All Query
        String profileDetailQuery = "select profile.pm_raw_id,profile.pm_prefix,profile" +
                ".pm_first_name,profile.pm_middle_name,profile.pm_last_name,profile.pm_suffix," +
                "profile.pm_nick_name,profile.pm_phonetic_first_name,profile" +
                ".pm_phonetic_middle_name,profile.pm_phonetic_last_name,profile.pm_profile_image," +
                "profile.pm_nick_name_privacy,profile.pm_notes,profile.pm_notes_privacy,profile" +
                ".pm_gender,profile.pm_gender_privacy,profile.pm_is_favourite," +
                " profile.pm_profile_rating, profile" +
                ".pm_total_user_rating from rc_profile_master profile where profile.pm_rcp_id IN ("
                + rcpId + ")";

        Cursor cursor = db.rawQuery(profileDetailQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            profileDataOperation.setRcpPmId(rcpId);
            profileDataOperation.setPbNamePrefix(StringUtils.defaultString(cursor.getString(1)));
            profileDataOperation.setPbNameFirst(StringUtils.defaultString(cursor.getString(cursor
                    .getColumnIndex(COLUMN_PM_FIRST_NAME))));
            profileDataOperation.setPbNameMiddle(StringUtils.defaultString(cursor.getString(3)));
            profileDataOperation.setPbNameLast(StringUtils.defaultString(cursor.getString(4)));
            profileDataOperation.setPbNameSuffix(StringUtils.defaultString(cursor.getString(5)));
            profileDataOperation.setPbNickname(StringUtils.defaultString(cursor.getString(6)));
            profileDataOperation.setPbPhoneticNameFirst(StringUtils.defaultString(cursor
                    .getString(7)));
            profileDataOperation.setPbPhoneticNameMiddle(StringUtils.defaultString(cursor
                    .getString(8)));
            profileDataOperation.setPbPhoneticNameLast(StringUtils.defaultString(cursor.getString
                    (9)));
            profileDataOperation.setPbNote(StringUtils.defaultString(cursor.getString(12)));
            profileDataOperation.setIsFavourite(StringUtils.defaultString(cursor.getString(16)));
            profileDataOperation.setProfileRating(StringUtils.defaultString(cursor.getString
                            (cursor.getColumnIndex(COLUMN_PM_PROFILE_RATING)),
                    "0"));
            profileDataOperation.setTotalProfileRateUser(StringUtils.defaultString(cursor
                    .getString(18), "0"));

            cursor.close();
        }
        //</editor-fold>

        //<editor-fold desc="Phone Number">
        String mobileNumberQuery = "select mobile.mnm_mobile_number,mobile.mnm_number_type,mobile" +
                ".mnm_is_primary,mobile.mnm_number_privacy from rc_mobile_number_master mobile " +
                "where mobile.rc_profile_master_pm_id IN (" + rcpId + ")";

        Cursor mobileNumberCursor = db.rawQuery(mobileNumberQuery, null);

        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

        // looping through all rows and adding to list
        if (mobileNumberCursor.moveToFirst()) {
            do {
                ProfileDataOperationPhoneNumber phoneNumber = new ProfileDataOperationPhoneNumber();
                phoneNumber.setPhoneNumber(StringUtils.defaultString(mobileNumberCursor.getString
                        (0)));
                phoneNumber.setPhoneType(StringUtils.defaultString(mobileNumberCursor.getString
                        (1)));
                phoneNumber.setPbRcpType(Integer.parseInt(StringUtils.defaultString
                        (mobileNumberCursor.getString(2), "1")));
                phoneNumber.setPhonePublic(Integer.parseInt(StringUtils.defaultString
                        (mobileNumberCursor.getString(3), "0")));
                arrayListPhoneNumber.add(phoneNumber);
            } while (mobileNumberCursor.moveToNext());
            mobileNumberCursor.close();
        }
        profileDataOperation.setPbPhoneNumber(arrayListPhoneNumber);
        //</editor-fold>

        //<editor-fold desc="EmailId">
        String emailIdQuery = "select email.em_email_address,email.em_email_type,email" +
                ".em_email_privacy,email.em_is_primary ,email.em_is_verified from " +
                "rc_email_master email where email.rc_profile_master_pm_id IN (" + rcpId + ")";

        Cursor emailIdCursor = db.rawQuery(emailIdQuery, null);

        ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();

        // looping through all rows and adding to list
        if (emailIdCursor.moveToFirst()) {
            do {
                ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                email.setEmEmailId(StringUtils.defaultString(emailIdCursor.getString(0)));
                email.setEmType(StringUtils.defaultString(emailIdCursor.getString(1)));
                email.setEmPublic(Integer.parseInt(StringUtils.defaultString(emailIdCursor
                        .getString(2), "0")));
                email.setEmRcpType(Integer.parseInt(StringUtils.defaultString(emailIdCursor
                        .getString(3), "1")));
                arrayListEmail.add(email);
            } while (emailIdCursor.moveToNext());
            emailIdCursor.close();
        }
        profileDataOperation.setPbEmailId(arrayListEmail);
        //</editor-fold>

        // <editor-fold desc="Organization">
        String organizationQuery = "select org.om_organization_title, org.om_job_description from" +
                " rc_organization_master org where org.rc_profile_master_pm_id IN (" + rcpId + ")";

        Cursor organizationCursor = db.rawQuery(organizationQuery, null);

        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

        // looping through all rows and adding to list
        if (organizationCursor.moveToFirst()) {
            do {
                ProfileDataOperationOrganization organization = new
                        ProfileDataOperationOrganization();
                organization.setOrgName(StringUtils.defaultString(organizationCursor.getString(0)));
                organization.setOrgJobTitle(StringUtils.defaultString(organizationCursor
                        .getString(1)));
                organization.setOrgRcpType(context.getResources().getInteger(R.integer
                        .rcp_type_cloud_phone_book));
                arrayListOrganization.add(organization);
            } while (organizationCursor.moveToNext());
            organizationCursor.close();
        }
        profileDataOperation.setPbOrganization(arrayListOrganization);
        //</editor-fold>

        // <editor-fold desc="Event">
        String eventQuery = "select event.evm_start_date,event.evm_event_type,event" +
                ".evm_event_privacy from rc_event_master event where event" +
                ".rc_profile_master_pm_id IN (" + rcpId + ")";

        Cursor eventCursor = db.rawQuery(eventQuery, null);

        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();

        // looping through all rows and adding to list
        if (eventCursor.moveToFirst()) {
            do {
                ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                event.setEventDate(StringUtils.defaultString(eventCursor.getString(0)));
                event.setEventType(StringUtils.defaultString(eventCursor.getString(1)));
                event.setEventPublic(StringUtils.defaultString(eventCursor.getString(2), "0"));
                event.setEventRcType(context.getResources().getInteger(R.integer
                        .rcp_type_cloud_phone_book));
                arrayListEvent.add(event);
            } while (eventCursor.moveToNext());
            eventCursor.close();
        }
        profileDataOperation.setPbEvent(arrayListEvent);
        //</editor-fold>

        // <editor-fold desc="Im Account">
        String imAccountQuery = "select im.im_im_type, im.im_im_protocol, im.im_im_privacy from " +
                "rc_im_master im where im.rc_profile_master_pm_id IN (" + rcpId + ")";

        Cursor imAccountCursor = db.rawQuery(imAccountQuery, null);

        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();

        // looping through all rows and adding to list
        if (imAccountCursor.moveToFirst()) {
            do {
                ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
                imAccount.setIMAccountType(StringUtils.defaultString(imAccountCursor.getString(0)));
                imAccount.setIMAccountProtocol(StringUtils.defaultString(imAccountCursor
                        .getString(1)));
                imAccount.setIMAccountPublic(StringUtils.defaultString(imAccountCursor.getString
                        (2), "0"));
                imAccount.setIMRcpType(context.getResources().getInteger(R.integer
                        .rcp_type_cloud_phone_book));
                arrayListImAccount.add(imAccount);
            } while (imAccountCursor.moveToNext());
            imAccountCursor.close();
        }
        profileDataOperation.setPbIMAccounts(arrayListImAccount);
        //</editor-fold>

        // <editor-fold desc="Address">
        String addressQuery = "select address.am_formatted_address, address.am_address_type, " +
                "address.am_address_privacy from rc_address_master address where address" +
                ".rc_profile_master_pm_id IN (" + rcpId + ")";

        Cursor addressCursor = db.rawQuery(addressQuery, null);

        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();

        // looping through all rows and adding to list
        if (addressCursor.moveToFirst()) {
            do {
                ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                address.setFormattedAddress(StringUtils.defaultString(addressCursor.getString(0)));
                address.setAddressType(StringUtils.defaultString(addressCursor.getString(1)));
                address.setRcpType(context.getResources().getInteger(R.integer
                        .rcp_type_cloud_phone_book));
                arrayListAddress.add(address);
            } while (addressCursor.moveToNext());
            addressCursor.close();
        }
        profileDataOperation.setPbAddress(arrayListAddress);
        //</editor-fold>

        // <editor-fold desc="Website">
        String websiteQuery = "select website.wm_website_url from rc_website_master website where" +
                " website.rc_profile_master_pm_id IN (" + rcpId + ")";

        Cursor websiteCursor = db.rawQuery(websiteQuery, null);

        ArrayList<String> arrayListWebsite = new ArrayList<>();

        // looping through all rows and adding to list
        if (websiteCursor.moveToFirst()) {
            do {
                arrayListWebsite.add(StringUtils.defaultString(websiteCursor.getString(0)));
            } while (websiteCursor.moveToNext());
            websiteCursor.close();
        }
        profileDataOperation.setPbWebAddress(arrayListWebsite);
        //</editor-fold>

        db.close();

        // return profile data operation
        return profileDataOperation;
    }*/

}
