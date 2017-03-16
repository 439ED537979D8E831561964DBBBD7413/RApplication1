package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.DbRating;

import java.util.ArrayList;

/**
 * Created by Monal on 16/01/17.
 * <p>
 * Table operations rc_contact_rating_master
 */

public class TableContactRatingMaster {

    private DatabaseHandler databaseHandler;

    public TableContactRatingMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_CONTACT_RATING_MASTER = "rc_contact_rating_master";


    // Column Names
    private static final String COLUMN_CRM_ID = "crm_id";
    private static final String COLUMN_CRM_STATUS = "crm_status";
    private static final String COLUMN_CRM_RATING = "crm_rating";
    private static final String COLUMN_CRM_CLOUD_PR_ID = "crm_cloud_pr_id";
    private static final String COLUMN_CRM_COMMENT = "crm_comment";
    private static final String COLUMN_CRM_REPLY = "crm_reply";
    private static final String COLUMN_CRM_CREATED_AT = "crm_created_at";
    private static final String COLUMN_CRM_REPLIED_AT = "crm_replied_at";
    private static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";

    private String[] tableColumns = {COLUMN_CRM_ID, COLUMN_CRM_STATUS, COLUMN_CRM_RATING,
            COLUMN_CRM_CLOUD_PR_ID, COLUMN_CRM_COMMENT, COLUMN_CRM_REPLY, COLUMN_CRM_CREATED_AT,
            COLUMN_CRM_REPLIED_AT, COLUMN_RC_PROFILE_MASTER_PM_ID};

    // Table Create Statements
    static final String CREATE_TABLE_RC_CONTACT_RATING_MASTER = "CREATE TABLE " +
            TABLE_RC_CONTACT_RATING_MASTER + " " +
            "(" +
            " " + COLUMN_CRM_ID + " integer NOT NULL CONSTRAINT rc_contact_rating_master_pk " +
            "PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer," +
            " " + COLUMN_CRM_STATUS + " tinyint NOT NULL," +
            " " + COLUMN_CRM_RATING + " integer NOT NULL," +
            " " + COLUMN_CRM_CLOUD_PR_ID + " integer NOT NULL," +
            " " + COLUMN_CRM_COMMENT + " text," +
            " " + COLUMN_CRM_REPLY + " text," +
            " " + COLUMN_CRM_CREATED_AT + " text NOT NULL," +
            " " + COLUMN_CRM_REPLIED_AT + " text" +
            ");";

    // Adding new Rating
    public void addRating(DbRating rating) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CRM_ID, rating.getCrmId());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, rating.getRcProfileMasterPmId());
        values.put(COLUMN_CRM_STATUS, rating.getCrmStatus());
        values.put(COLUMN_CRM_RATING, rating.getCrmRating());
        values.put(COLUMN_CRM_CLOUD_PR_ID, rating.getCrmCloudPrId());
        values.put(COLUMN_CRM_COMMENT, rating.getCrmComment());
        values.put(COLUMN_CRM_REPLY, rating.getCrmReply());
        values.put(COLUMN_CRM_CREATED_AT, rating.getCrmCreatedAt());
        values.put(COLUMN_CRM_REPLIED_AT, rating.getCrmRepliedAt());

        db.insert(TABLE_RC_CONTACT_RATING_MASTER, null, values);

        db.close();
    }

    // Getting single rating
    public DbRating getRating(int ratingId) {

        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_CONTACT_RATING_MASTER, tableColumns, COLUMN_CRM_ID +
                "=?", new String[]{String.valueOf
                (ratingId)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        DbRating rating = new DbRating();
        if (cursor != null) {
            rating.setCrmId(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_ID)));
            rating.setCrmStatus(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_STATUS)));
            rating.setCrmRating(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_RATING)));
            rating.setCrmCloudPrId(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_CLOUD_PR_ID)));
            rating.setCrmComment(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_COMMENT)));
            rating.setCrmReply(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_REPLY)));
            rating.setCrmCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_CREATED_AT)));
            rating.setCrmRepliedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_REPLIED_AT)));
            rating.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

            cursor.close();
        }

        db.close();

        return rating;
    }

    // Getting All Ratings
    public ArrayList<DbRating> getAllRatings() {

        ArrayList<DbRating> arrayListRatings = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_RC_CONTACT_RATING_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DbRating rating = new DbRating();
                rating.setCrmId(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_ID)));
                rating.setCrmStatus(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_STATUS)));
                rating.setCrmRating(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_RATING)));
                rating.setCrmCloudPrId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CRM_CLOUD_PR_ID)));
                rating.setCrmComment(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_COMMENT)));
                rating.setCrmReply(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_REPLY)));
                rating.setCrmCreatedAt(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CRM_CREATED_AT)));
                rating.setCrmRepliedAt(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CRM_REPLIED_AT)));
                rating.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                arrayListRatings.add(rating);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        return arrayListRatings;
    }

    // Getting Rating Count
    public int getRatingCount() {

        String countQuery = "SELECT  * FROM " + TABLE_RC_CONTACT_RATING_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    // Updating single rating
    public int updateRating(DbRating rating) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CRM_ID, rating.getCrmId());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, rating.getRcProfileMasterPmId());
        values.put(COLUMN_CRM_STATUS, rating.getCrmStatus());
        values.put(COLUMN_CRM_RATING, rating.getCrmRating());
        values.put(COLUMN_CRM_CLOUD_PR_ID, rating.getCrmCloudPrId());
        values.put(COLUMN_CRM_COMMENT, rating.getCrmComment());
        values.put(COLUMN_CRM_REPLY, rating.getCrmReply());
        values.put(COLUMN_CRM_CREATED_AT, rating.getCrmCreatedAt());
        values.put(COLUMN_CRM_REPLIED_AT, rating.getCrmRepliedAt());

        int isUpdated = db.update(TABLE_RC_CONTACT_RATING_MASTER, values, COLUMN_CRM_ID + " = ?",
                new String[]{String.valueOf(rating.getCrmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single rating
    public void deleteRating(DbRating rating) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_CONTACT_RATING_MASTER, COLUMN_CRM_ID + " = ?",
                new String[]{String.valueOf(rating.getCrmId())});
        db.close();
    }
}
