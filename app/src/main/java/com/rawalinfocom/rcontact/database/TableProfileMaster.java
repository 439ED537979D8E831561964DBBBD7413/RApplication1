package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

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
    public static final String TABLE_RC_PROFILE_MASTER = "rc_profile_master";

    // Column Names
//    private static final String COLUMN_PM_ID = "pm_id";
//    static final String COLUMN_PM_PREFIX = "pm_prefix";
//    static final String COLUMN_PM_MIDDLE_NAME = "pm_middle_name";
//    static final String COLUMN_PM_SUFFIX = "pm_suffix";
//    static final String COLUMN_PM_NICK_NAME = "pm_nick_name";
//    static final String COLUMN_PM_PHONETIC_FIRST_NAME = "pm_phonetic_first_name";
//    static final String COLUMN_PM_PHONETIC_MIDDLE_NAME = "pm_phonetic_middle_name";
//    static final String COLUMN_PM_PHONETIC_LAST_NAME = "pm_phonetic_last_name";
//    static final String COLUMN_PM_NICK_NAME_PRIVACY = "pm_nick_name_privacy";
//    static final String COLUMN_PM_NOTES = "pm_notes";
//    static final String COLUMN_PM_NOTES_PRIVACY = "pm_notes_privacy";
//    private static final String COLUMN_PM_ACCESS_TOKEN = "pm_access_token";
//    private static final String COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE =
// "pm_signup_social_media_type";

    static final String COLUMN_PM_RCP_ID = "pm_rcp_id";
    static final String COLUMN_PM_RAW_ID = "pm_raw_id";
    static final String COLUMN_PM_FIRST_NAME = "pm_first_name";
    static final String COLUMN_PM_LAST_NAME = "pm_last_name";
    static final String COLUMN_PM_PROFILE_IMAGE = "pm_profile_image";
    static final String COLUMN_PM_GENDER = "pm_gender";
    static final String COLUMN_PM_GENDER_PRIVACY = "pm_gender_privacy";
    static final String COLUMN_PM_PROFILE_RATING = "pm_profile_rating";
    static final String COLUMN_PM_RATING_PRIVACY = "pm_rating_privacy";
    static final String COLUMN_PM_RATING_PRIVATE = "pm_rating_private";
    static final String COLUMN_PM_PROFILE_RATE_USER = "pm_total_user_rating";
    static final String COLUMN_PM_IS_FAVOURITE = "pm_is_favourite";
    private static final String COLUMN_PM_NOSQL_MASTER_ID = "pm_nosql_master_id";
    static final String COLUMN_PM_BADGE = "pm_badge";
    private static final String COLUMN_PM_JOINING_DATE = "pm_joining_date";
    static final String COLUMN_PM_LAST_SEEN = "pm_last_seen";

    // Table Create Statements
//    static final String CREATE_TABLE_RC_PROFILE_MASTER = "CREATE TABLE IF NOT EXISTS " +
//            TABLE_RC_PROFILE_MASTER + " (" +
//            " " + COLUMN_PM_RAW_ID + " text," +
//            " " + COLUMN_PM_RCP_ID + " integer UNIQUE," +
//            " " + COLUMN_PM_PREFIX + " text," +
//            " " + COLUMN_PM_FIRST_NAME + " text," +
//            " " + COLUMN_PM_MIDDLE_NAME + " text," +
//            " " + COLUMN_PM_LAST_NAME + " text," +
//            " " + COLUMN_PM_SUFFIX + " text," +
//            " " + COLUMN_PM_NICK_NAME + " text," +
//            " " + COLUMN_PM_PHONETIC_FIRST_NAME + " text," +
//            " " + COLUMN_PM_PHONETIC_MIDDLE_NAME + " text," +
//            " " + COLUMN_PM_PHONETIC_LAST_NAME + " text," +
//            " " + COLUMN_PM_PROFILE_IMAGE + " text," +
//            " " + COLUMN_PM_NICK_NAME_PRIVACY + " integer DEFAULT 1," +
//            " " + COLUMN_PM_NOTES + " text," +
//            " " + COLUMN_PM_NOTES_PRIVACY + " integer DEFAULT 1," +
//            " " + COLUMN_PM_GENDER + " varchar," +
//            " " + COLUMN_PM_GENDER_PRIVACY + " integer DEFAULT 1," +
//            " " + COLUMN_PM_PROFILE_RATING + " integer," +
//            " " + COLUMN_PM_PROFILE_RATE_USER + " integer," +
//            " " + COLUMN_PM_IS_FAVOURITE + " integer," +
//            " " + COLUMN_PM_ACCESS_TOKEN + " text," +
//            " " + COLUMN_PM_NOSQL_MASTER_ID + " text," +
//            " " + COLUMN_PM_SIGNUP_SOCIAL_MEDIA_TYPE + " integer," +
//            " " + COLUMN_PM_JOINING_DATE + " text" +
//            ");";

    static final String CREATE_TABLE_RC_PROFILE_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_PROFILE_MASTER + " (" +
            " " + COLUMN_PM_RAW_ID + " text," +
            " " + COLUMN_PM_RCP_ID + " integer UNIQUE," +
            " " + COLUMN_PM_FIRST_NAME + " text," +
            " " + COLUMN_PM_LAST_NAME + " text," +
            " " + COLUMN_PM_PROFILE_IMAGE + " text," +
            " " + COLUMN_PM_GENDER + " varchar," +
            " " + COLUMN_PM_GENDER_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_PM_PROFILE_RATING + " integer," +
            " " + COLUMN_PM_RATING_PRIVACY + " integer," +
            " " + COLUMN_PM_RATING_PRIVATE + " integer," +
            " " + COLUMN_PM_PROFILE_RATE_USER + " integer," +
            " " + COLUMN_PM_IS_FAVOURITE + " integer," +
            " " + COLUMN_PM_NOSQL_MASTER_ID + " text," +
            " " + COLUMN_PM_BADGE + " text," +
            " " + COLUMN_PM_LAST_SEEN + " text," +
            " " + COLUMN_PM_JOINING_DATE + " text" +
            ");";

    // Adding new Profile
    public void addProfile(UserProfile userProfile) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(COLUMN_PM_ID, userProfile.getPmId());
        if (StringUtils.length(userProfile.getPmRawId()) > 0) {
            values.put(COLUMN_PM_RAW_ID, userProfile.getPmRawId());
        }
        values.put(COLUMN_PM_FIRST_NAME, userProfile.getPmFirstName());
        values.put(COLUMN_PM_LAST_NAME, userProfile.getPmLastName());
        values.put(COLUMN_PM_PROFILE_IMAGE, userProfile.getPmProfileImage());
        values.put(COLUMN_PM_RCP_ID, userProfile.getPmRcpId());
        values.put(COLUMN_PM_GENDER, userProfile.getPmGender());
        values.put(COLUMN_PM_PROFILE_RATING, userProfile.getProfileRating());
        values.put(COLUMN_PM_PROFILE_RATE_USER, userProfile.getTotalProfileRateUser());
        values.put(COLUMN_PM_IS_FAVOURITE, userProfile.getPmIsFavourite());
        values.put(COLUMN_PM_NOSQL_MASTER_ID, userProfile.getPmNosqlMasterId());
        values.put(COLUMN_PM_BADGE, userProfile.getPmBadge());
        values.put(COLUMN_PM_JOINING_DATE, userProfile.getPmJoiningDate());
        values.put(COLUMN_PM_LAST_SEEN, userProfile.getPmLastSeen());
        values.put(COLUMN_PM_RATING_PRIVACY, userProfile.getProfileRatingPrivacy());
        values.put(COLUMN_PM_RATING_PRIVATE, userProfile.getRatingPrivate());

        int count = 0;
        Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RC_PROFILE_MASTER + " " +
                "WHERE " + COLUMN_PM_RCP_ID + " = " + userProfile.getPmRcpId(), null);
        if (mCount != null) {
            mCount.moveToFirst();
            count = mCount.getInt(0);
            mCount.close();
        }

        if (count > 0) {
            // Update if already exists
            db.update(TABLE_RC_PROFILE_MASTER, values, COLUMN_PM_RCP_ID + " = ?",
                    new String[]{String.valueOf(userProfile.getPmRcpId())});
        } else {
            // Inserting Row
            db.insert(TABLE_RC_PROFILE_MASTER, null, values);
        }

        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    public void updateRatingPrivacy(String pmId, String ratingPrivacy) {

        try {

            SQLiteDatabase db = databaseHandler.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_PM_RATING_PRIVACY, ratingPrivacy);

            // Update if already exists
            db.update(TABLE_RC_PROFILE_MASTER, values, COLUMN_PM_RCP_ID + " = ?",
                    new String[]{String.valueOf(pmId)});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adding Array new Profile
    public void addArrayProfile(ArrayList<UserProfile> arrayListUserProfile) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        for (int i = 0; i < arrayListUserProfile.size(); i++) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_PM_RAW_ID, arrayListUserProfile.get(i).getPmRawId());
            values.put(COLUMN_PM_FIRST_NAME, arrayListUserProfile.get(i).getPmFirstName());
            values.put(COLUMN_PM_LAST_NAME, arrayListUserProfile.get(i).getPmLastName());
            values.put(COLUMN_PM_PROFILE_IMAGE, arrayListUserProfile.get(i).getPmProfileImage());
            values.put(COLUMN_PM_RCP_ID, arrayListUserProfile.get(i).getPmRcpId());
//            values.put(COLUMN_PM_GENDER, arrayListUserProfile.get(i).getPmGender());
            values.put(COLUMN_PM_PROFILE_RATING, arrayListUserProfile.get(i).getProfileRating());
            values.put(COLUMN_PM_PROFILE_RATE_USER, arrayListUserProfile.get(i)
                    .getTotalProfileRateUser());
            values.put(COLUMN_PM_IS_FAVOURITE, arrayListUserProfile.get(i).getPmIsFavourite());
            values.put(COLUMN_PM_NOSQL_MASTER_ID, arrayListUserProfile.get(i).getPmNosqlMasterId());
            values.put(COLUMN_PM_BADGE, arrayListUserProfile.get(i).getPmBadge());
            values.put(COLUMN_PM_JOINING_DATE, arrayListUserProfile.get(i).getPmJoiningDate());
            values.put(COLUMN_PM_LAST_SEEN, arrayListUserProfile.get(i).getPmLastSeen());
            values.put(COLUMN_PM_RATING_PRIVACY, arrayListUserProfile.get(i).getProfileRatingPrivacy());
            values.put(COLUMN_PM_RATING_PRIVATE, arrayListUserProfile.get(i).getRatingPrivate());

            int count = 0;
            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RC_PROFILE_MASTER + " " +
                    "WHERE " + COLUMN_PM_RCP_ID + " = " + arrayListUserProfile.get(i).getPmRcpId
                    (), null);
            if (mCount != null) {
                mCount.moveToFirst();
                count = mCount.getInt(0);
                mCount.close();
            }

            if (count > 0) {
                // Update if already exists
                db.update(TABLE_RC_PROFILE_MASTER, values, COLUMN_PM_RCP_ID + " = ?",
                        new String[]{String.valueOf(arrayListUserProfile.get(i).getPmRcpId())});
            } else {
                // Inserting Row
                db.insert(TABLE_RC_PROFILE_MASTER, null, values);
            }

        }
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single RCP Profile from Pm id
    public UserProfile getRCPProfileFromPmId(int cloudPmd) {
        UserProfile userProfile = new UserProfile();

        try {
            SQLiteDatabase db = databaseHandler.getReadableDatabase();

            Cursor cursor = db.query(TABLE_RC_PROFILE_MASTER, new String[]{COLUMN_PM_FIRST_NAME,
                            COLUMN_PM_LAST_NAME, COLUMN_PM_PROFILE_IMAGE, COLUMN_PM_RCP_ID},
                    COLUMN_PM_RCP_ID + "=?", new String[]{String.valueOf(cloudPmd)}, null, null,
                    null, null);
            if (cursor != null)
                cursor.moveToFirst();

            if (cursor != null) {
                userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_FIRST_NAME)));
                userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_LAST_NAME)));
                userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_IMAGE)));
                userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RCP_ID)));
                cursor.close();
            }

            db.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


        // return Profile
        return userProfile;
    }

    // get rcp user profile
    public UserProfile getProfileFromPmId(int cloudPmd) {
        UserProfile userProfile = new UserProfile();

        try {
            SQLiteDatabase db = databaseHandler.getReadableDatabase();

            Cursor cursor = db.query(TABLE_RC_PROFILE_MASTER, new String[]{COLUMN_PM_RAW_ID,
                            COLUMN_PM_FIRST_NAME, COLUMN_PM_LAST_NAME, COLUMN_PM_PROFILE_IMAGE, COLUMN_PM_PROFILE_RATE_USER,
                            COLUMN_PM_PROFILE_RATING, COLUMN_PM_LAST_SEEN, COLUMN_PM_RATING_PRIVACY, COLUMN_PM_RATING_PRIVATE,
                            COLUMN_PM_RCP_ID,}, COLUMN_PM_RCP_ID + "=?", new String[]{String.valueOf(cloudPmd)},
                    null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();

            if (cursor != null) {
                userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RAW_ID)));
                userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_FIRST_NAME)));
                userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_LAST_NAME)));
                userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_IMAGE)));
                userProfile.setProfileRating(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_RATING)));
                userProfile.setTotalProfileRateUser(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_RATE_USER)));
                userProfile.setPmLastSeen(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_LAST_SEEN)));
                userProfile.setProfileRatingPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_RATING_PRIVACY)));
                userProfile.setRatingPrivate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_RATING_PRIVATE)));

                cursor.close();
            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // return Profile
        return userProfile;
    }

    // Getting single Profile from Cloud Pm id
    public UserProfile getProfileFromCloudPmId(int cloudPmd) {
        UserProfile userProfile = new UserProfile();

        try {
            SQLiteDatabase db = databaseHandler.getReadableDatabase();

            Cursor cursor = db.query(TABLE_RC_PROFILE_MASTER, new String[]{COLUMN_PM_RAW_ID,
                    COLUMN_PM_FIRST_NAME, COLUMN_PM_LAST_NAME, COLUMN_PM_PROFILE_IMAGE,
                    COLUMN_PM_RCP_ID, COLUMN_PM_GENDER, COLUMN_PM_GENDER_PRIVACY,
                    COLUMN_PM_PROFILE_RATING, COLUMN_PM_PROFILE_RATE_USER, COLUMN_PM_RATING_PRIVACY,
                    COLUMN_PM_IS_FAVOURITE, COLUMN_PM_NOSQL_MASTER_ID, COLUMN_PM_BADGE, COLUMN_PM_RATING_PRIVATE,
                    COLUMN_PM_LAST_SEEN, COLUMN_PM_JOINING_DATE}, COLUMN_PM_RCP_ID + "=?", new String[]{String.valueOf
                    (cloudPmd)}, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();

            if (cursor != null) {
                userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RAW_ID)));
//                userProfile.setPmPrefix(cursor.getString(cursor.getColumnIndex
// (COLUMN_PM_PREFIX)));
                userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_FIRST_NAME)));
                userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_LAST_NAME)));
                userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_IMAGE)));
                userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RCP_ID)));
                userProfile.setPmGender(cursor.getString(cursor.getColumnIndex(COLUMN_PM_GENDER)));
                userProfile.setPmGenderPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_GENDER_PRIVACY)));
                userProfile.setProfileRating(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_RATING)));
                userProfile.setTotalProfileRateUser(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_RATE_USER)));
                userProfile.setPmIsFavourite(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_IS_FAVOURITE)));
                userProfile.setPmNosqlMasterId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_NOSQL_MASTER_ID)));
                userProfile.setPmBadge(cursor.getString(cursor.getColumnIndex(COLUMN_PM_BADGE)));
                userProfile.setPmJoiningDate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_JOINING_DATE)));
                userProfile.setPmLastSeen(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_LAST_SEEN)));
                userProfile.setProfileRatingPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_RATING_PRIVACY)));
                userProfile.setRatingPrivate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_RATING_PRIVATE)));

                cursor.close();
            }

            db.close();


        } catch (Exception e) {
            e.printStackTrace();
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
                userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RAW_ID)));
                userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_FIRST_NAME)));
                userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_LAST_NAME)));
                userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_IMAGE)));
                userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex(COLUMN_PM_RCP_ID)));
                userProfile.setPmGender(cursor.getString(cursor.getColumnIndex(COLUMN_PM_GENDER)));
                userProfile.setPmGenderPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_GENDER_PRIVACY)));
                userProfile.setProfileRating(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_RATING)));
                userProfile.setTotalProfileRateUser(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_PROFILE_RATE_USER)));
                userProfile.setPmIsFavourite(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_IS_FAVOURITE)));
                userProfile.setPmNosqlMasterId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_PM_NOSQL_MASTER_ID)));
                userProfile.setPmBadge(cursor.getString(cursor.getColumnIndex(COLUMN_PM_BADGE)));
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
        values.put(COLUMN_PM_FIRST_NAME, userProfile.getPmFirstName());
        values.put(COLUMN_PM_LAST_NAME, userProfile.getPmLastName());
        values.put(COLUMN_PM_PROFILE_IMAGE, userProfile.getPmProfileImage());
        values.put(COLUMN_PM_RCP_ID, userProfile.getPmRcpId());
        values.put(COLUMN_PM_GENDER, userProfile.getPmGender());
        values.put(COLUMN_PM_GENDER_PRIVACY, userProfile.getPmGenderPrivacy());
        values.put(COLUMN_PM_PROFILE_RATING, userProfile.getProfileRating());
        values.put(COLUMN_PM_PROFILE_RATE_USER, userProfile.getTotalProfileRateUser());
        values.put(COLUMN_PM_IS_FAVOURITE, userProfile.getPmIsFavourite());
        values.put(COLUMN_PM_NOSQL_MASTER_ID, userProfile.getPmNosqlMasterId());
        values.put(COLUMN_PM_BADGE, userProfile.getPmBadge());
        values.put(COLUMN_PM_JOINING_DATE, userProfile.getPmJoiningDate());
        values.put(COLUMN_PM_LAST_SEEN, userProfile.getPmLastSeen());
        values.put(COLUMN_PM_RATING_PRIVACY, userProfile.getProfileRatingPrivacy());
        values.put(COLUMN_PM_RATING_PRIVATE, userProfile.getRatingPrivate());

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

    public String getUserGender(int rcpId) {
        String gender = "";
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor mCount = db.rawQuery("SELECT " + COLUMN_PM_GENDER + " FROM " + TABLE_RC_PROFILE_MASTER + " WHERE " +
                COLUMN_PM_RCP_ID + " = " + rcpId, null);
        if (mCount != null) {
            mCount.moveToFirst();
            gender = mCount.getString(0);
            mCount.close();
        }
        db.close();
        return gender;
    }

    public String getRawIdFromRcpId(int rcpId) {
        String rawId = "";
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PM_RAW_ID + " FROM " +
                TABLE_RC_PROFILE_MASTER + " WHERE " + COLUMN_PM_RCP_ID + " = " + rcpId, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                rawId = cursor.getString(cursor.getColumnIndex(COLUMN_PM_RAW_ID));
            }
            cursor.close();
        }
        db.close();
        return rawId;
    }

    public void updateRawIds(int rcpId, String rawId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PM_RAW_ID, rawId);

        int isUpdated = db.update(TABLE_RC_PROFILE_MASTER, values, COLUMN_PM_RCP_ID + " = ?",
                new String[]{String.valueOf(rcpId)});

        db.close();

    }

    public ArrayList<String> getAllRawIds() {

        ArrayList<String> arrayListRawId = new ArrayList<>();
        SQLiteDatabase db = null;
        // Select All Query

        try {

            String selectQuery = "SELECT " + COLUMN_PM_RAW_ID + " FROM " + TABLE_RC_PROFILE_MASTER;

            db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        UserProfile userProfile = new UserProfile();
                        userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex
                                (COLUMN_PM_RAW_ID)));
                        if (userProfile.getPmRawId().contains(",")) {
                            String[] multipleRawIds = userProfile.getPmRawId().split(",");
                            Collections.addAll(arrayListRawId, multipleRawIds);
                        } else {
                            arrayListRawId.add(userProfile.getPmRawId());
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db != null) {
                db.close();
            }
        }

        // return user profile list
        return arrayListRawId;
    }


    public ArrayList<String> getAllRcpIds() {

        ArrayList<String> arrayListRcpId = new ArrayList<>();
        SQLiteDatabase db = null;
        // Select All Query

        try {

            String selectQuery = "SELECT " + COLUMN_PM_RCP_ID + " FROM " + TABLE_RC_PROFILE_MASTER;

            db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        UserProfile userProfile = new UserProfile();
                        userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex
                                (COLUMN_PM_RCP_ID)));
                        arrayListRcpId.add(userProfile.getPmRcpId());

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db != null) {
                db.close();
            }
        }

        // return user profile list
        return arrayListRcpId;
    }

    public String getNameFromRawId(String rawId) {

        String name = "0";
        // Select All Query
        String selectQuery = "SELECT " + COLUMN_PM_RCP_ID + "," + COLUMN_PM_FIRST_NAME + "," +
                COLUMN_PM_LAST_NAME + " FROM " + TABLE_RC_PROFILE_MASTER + " WHERE " +
                COLUMN_PM_RAW_ID + " LIKE '%" + rawId + "%'";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null) {

            if (cursor.getCount() > 1) {
//                name = String.valueOf(cursor.getCount());
                if (cursor.moveToFirst()) {
                    do {
                        UserProfile userProfile = new UserProfile();
                        userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                                (COLUMN_PM_FIRST_NAME)));
                        userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                                (COLUMN_PM_LAST_NAME)));
                        userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex
                                (COLUMN_PM_RCP_ID)));
//                        name = userProfile.getPmFirstName() + " " + userProfile.getPmLastName();
                        if (name.equalsIgnoreCase("0")) {
                            name = userProfile.getPmRcpId();
                        } else {
                            name = name + "," + userProfile.getPmRcpId();
                        }
                    } while (cursor.moveToNext());
                }
            } else if (cursor.getCount() == 1) {
                if (cursor.moveToFirst()) {
//                        do {
                    UserProfile userProfile = new UserProfile();
                    /*userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_RAW_ID)));*/
                    userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_FIRST_NAME)));
                    userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_LAST_NAME)));
                    // Adding user profile to list
                    name = userProfile.getPmFirstName() + " " + userProfile.getPmLastName();
//                        } while (cursor.moveToNext());
                }
            }
            cursor.close();
        }

        db.close();

        // return user profile list
        return name;
    }

    public ArrayList<UserProfile> getProfileDetailsFromRawIdTemp(String rawId) {

        ArrayList<UserProfile> userProfiles = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + COLUMN_PM_RCP_ID + "," + COLUMN_PM_FIRST_NAME + "," +
                COLUMN_PM_PROFILE_IMAGE + "," + COLUMN_PM_LAST_NAME + " FROM " +
                TABLE_RC_PROFILE_MASTER + " WHERE " + COLUMN_PM_RAW_ID + " LIKE '%" + rawId + "%'";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null) {

//            if (cursor.getCount() > 1) {
//                name = String.valueOf(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_FIRST_NAME)));
                    userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_LAST_NAME)));
                    userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_RCP_ID)));
                    userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_PROFILE_IMAGE)));
//                        name = userProfile.getPmFirstName() + " " + userProfile.getPmLastName();
                   /* if (name.equalsIgnoreCase("0")) {
                        name = userProfile.getPmRcpId();
                    } else {
                        name = name + "," + userProfile.getPmRcpId();
                    }*/
                    userProfiles.add(userProfile);
                } while (cursor.moveToNext());
            }
//            }
           /* else if (cursor.getCount() == 1) {
                if (cursor.moveToFirst()) {
//                        do {
                    UserProfile userProfile = new UserProfile();
                    *//*userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_RAW_ID)));*//*
                    userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_FIRST_NAME)));
                    userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_LAST_NAME)));
                    // Adding user profile to list
                    name = userProfile.getPmFirstName() + " " + userProfile.getPmLastName();
//                        } while (cursor.moveToNext());
                }
            }*/
            cursor.close();
        }

        db.close();

        // return user profile list
        return userProfiles;
    }

    public ArrayList<UserProfile> getProfileDetailsFromRawId(String rawId) {

        ArrayList<UserProfile> userProfiles = new ArrayList<>();

        String selectQuery1 = "SELECT " + COLUMN_PM_RAW_ID + ", " + COLUMN_PM_RCP_ID + " FROM " +
                TABLE_RC_PROFILE_MASTER + " WHERE " + COLUMN_PM_RAW_ID + " LIKE '%" + rawId + "%'";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery1, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_RCP_ID)));
                    userProfile.setPmRawId(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_RAW_ID)));
                    userProfiles.add(userProfile);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();

        ArrayList<String> rawIds = new ArrayList<>();
        ArrayList<String> rcpIds = new ArrayList<>();
        for (int i = 0; i < userProfiles.size(); i++) {
            if (StringUtils.contains(userProfiles.get(i).getPmRawId(), ",")) {
                String[] tempRawIds = StringUtils.split(userProfiles.get(i).getPmRawId(), ",");
                for (String tempRawId : tempRawIds) {
                    rawIds.add(tempRawId);
                    rcpIds.add(userProfiles.get(i).getPmRcpId());
                }
            } else {
                rawIds.add(userProfiles.get(i).getPmRawId());
                rcpIds.add(userProfiles.get(i).getPmRcpId());
            }
        }

        ArrayList<String> finalRcpIds = new ArrayList<>();
        for (int i = 0; i < rawIds.size(); i++) {
            if (StringUtils.equalsAnyIgnoreCase(rawIds.get(i), rawId)) {
                finalRcpIds.add(rcpIds.get(i));
            }
        }

        String allRcpId = StringUtils.join(finalRcpIds.toArray(new String[finalRcpIds.size()]),
                ",");

        String selectQuery = "SELECT " + COLUMN_PM_RCP_ID + "," + COLUMN_PM_FIRST_NAME + "," +
                COLUMN_PM_PROFILE_IMAGE + "," + COLUMN_PM_LAST_NAME + " FROM " +
                TABLE_RC_PROFILE_MASTER + " WHERE " + COLUMN_PM_RCP_ID + " IN (" + allRcpId + ")";

        userProfiles = new ArrayList<>();
        db = databaseHandler.getWritableDatabase();
        cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_FIRST_NAME)));
                    userProfile.setPmLastName(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_LAST_NAME)));
                    userProfile.setPmRcpId(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_RCP_ID)));
                    userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_PROFILE_IMAGE)));
                    userProfiles.add(userProfile);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();

        return userProfiles;
    }

    public ArrayList<String> getAllRcpIdFromRawId(String rawId) {

        ArrayList<String> rcpIds = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + COLUMN_PM_RCP_ID + "," + COLUMN_PM_FIRST_NAME + "," +
                COLUMN_PM_LAST_NAME + " FROM " + TABLE_RC_PROFILE_MASTER + " WHERE " +
                COLUMN_PM_RAW_ID + " LIKE '%" + rawId + "%'";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    rcpIds.add(cursor.getString(cursor.getColumnIndex
                            (COLUMN_PM_RCP_ID)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();

        return rcpIds;
    }

    public String getRCPIdFromRawId(String rawId) {

        String rcpID = "";

        String selectQuery = "SELECT " + COLUMN_PM_RCP_ID + " FROM " + TABLE_RC_PROFILE_MASTER + " WHERE " +
                COLUMN_PM_RAW_ID + " LIKE '%" + rawId + "%'";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    rcpID = cursor.getString(cursor.getColumnIndex(COLUMN_PM_RCP_ID));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return rcpID;

    }
}
