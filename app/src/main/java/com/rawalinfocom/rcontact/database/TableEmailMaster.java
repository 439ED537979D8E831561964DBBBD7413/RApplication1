package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Email;

import java.util.ArrayList;

/**
 * Created by Monal on 14/11/16.
 * <p>
 * Table operations rc_email_master
 */

public class TableEmailMaster {

    private DatabaseHandler databaseHandler;

    public TableEmailMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_EMAIL_MASTER = "rc_email_master";

    // Column Names
    private static final String COLUMN_EM_ID = "em_id";
    private static final String COLUMN_EM_CLOUD_ID = "em_cloud_id";
    static final String COLUMN_EM_EMAIL_ADDRESS = "em_email_address";
    static final String COLUMN_EM_EMAIL_TYPE = "em_email_type";
    static final String COLUMN_EM_RECORD_INDEX_ID = "em_record_index_id";
    private static final String COLUMN_EM_CUSTOM_TYPE = "em_custom_type";
    static final String COLUMN_EM_IS_PRIMARY = "em_is_primary";
    static final String COLUMN_EM_EMAIL_PRIVACY = "em_email_privacy";
    private static final String COLUMN_EM_IS_DEFAULT = "em_is_default";
    static final String COLUMN_EM_IS_VERIFIED = "em_is_verified";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    static final String CREATE_TABLE_RC_EMAIL_MASTER = "CREATE TABLE " +
            TABLE_RC_EMAIL_MASTER + " (" +
            " " + COLUMN_EM_ID + " integer NOT NULL CONSTRAINT rc_email_master_pk PRIMARY KEY," +
            " " + COLUMN_EM_CLOUD_ID + " integer," +
            " " + COLUMN_EM_EMAIL_ADDRESS + " text NOT NULL," +
            " " + COLUMN_EM_EMAIL_TYPE + " text," +
            " " + COLUMN_EM_RECORD_INDEX_ID + " text," +
            " " + COLUMN_EM_CUSTOM_TYPE + " text," +
            " " + COLUMN_EM_IS_PRIMARY + " integer," +
            " " + COLUMN_EM_EMAIL_PRIVACY + " integer," +
            " " + COLUMN_EM_IS_DEFAULT + " integer," +
            " " + COLUMN_EM_IS_VERIFIED + " integer," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new Email
    public void addEmail(Email email) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EM_ID, email.getEmId());
        values.put(COLUMN_EM_CLOUD_ID, email.getEmCloudId());
        values.put(COLUMN_EM_EMAIL_ADDRESS, email.getEmEmailAddress());
        values.put(COLUMN_EM_EMAIL_TYPE, email.getEmEmailType());
        values.put(COLUMN_EM_RECORD_INDEX_ID, email.getEmRecordIndexId());
        values.put(COLUMN_EM_CUSTOM_TYPE, email.getEmCustomType());
        values.put(COLUMN_EM_IS_PRIMARY, email.getEmIsPrimary());
        values.put(COLUMN_EM_EMAIL_PRIVACY, email.getEmEmailPrivacy());
        values.put(COLUMN_EM_IS_DEFAULT, email.getEmIsDefault());
        values.put(COLUMN_EM_IS_VERIFIED, email.getEmIsVerified());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, email.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_EMAIL_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding array Email
    public void addArrayEmail(ArrayList<Email> arrayListEmail) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListEmail.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EM_ID, arrayListEmail.get(i).getEmId());
            values.put(COLUMN_EM_CLOUD_ID, arrayListEmail.get(i).getEmCloudId());
            values.put(COLUMN_EM_EMAIL_ADDRESS, arrayListEmail.get(i).getEmEmailAddress());
            values.put(COLUMN_EM_EMAIL_TYPE, arrayListEmail.get(i).getEmEmailType());
            values.put(COLUMN_EM_CUSTOM_TYPE, arrayListEmail.get(i).getEmCustomType());
            values.put(COLUMN_EM_RECORD_INDEX_ID, arrayListEmail.get(i).getEmRecordIndexId());
            values.put(COLUMN_EM_IS_PRIMARY, arrayListEmail.get(i).getEmIsPrimary());
            values.put(COLUMN_EM_EMAIL_PRIVACY, arrayListEmail.get(i).getEmEmailPrivacy());
            values.put(COLUMN_EM_IS_DEFAULT, arrayListEmail.get(i).getEmIsDefault());
            values.put(COLUMN_EM_IS_VERIFIED, arrayListEmail.get(i).getEmIsVerified());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListEmail.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_EMAIL_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    // Getting single Email
    public Email getEmail(int emId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_EMAIL_MASTER, new String[]{COLUMN_EM_ID,
                        COLUMN_EM_CLOUD_ID, COLUMN_EM_EMAIL_ADDRESS, COLUMN_EM_EMAIL_TYPE,
                        COLUMN_EM_RECORD_INDEX_ID, COLUMN_EM_CUSTOM_TYPE, COLUMN_EM_IS_PRIMARY,
                        COLUMN_EM_EMAIL_PRIVACY, COLUMN_EM_IS_DEFAULT, COLUMN_EM_IS_VERIFIED,
                        COLUMN_RC_PROFILE_MASTER_PM_ID},
                COLUMN_EM_ID + "=?", new String[]{String.valueOf(emId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Email email = new Email();
        if (cursor != null) {
            email.setEmId(cursor.getString(cursor.getColumnIndex(COLUMN_EM_ID)));
            email.setEmCloudId(cursor.getString(cursor.getColumnIndex(COLUMN_EM_CLOUD_ID)));
            email.setEmEmailAddress(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EM_EMAIL_ADDRESS)));
            email.setEmEmailType(cursor.getString(cursor.getColumnIndex(COLUMN_EM_EMAIL_TYPE)));
            email.setEmRecordIndexId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EM_RECORD_INDEX_ID)));
            email.setEmCustomType(cursor.getString(cursor.getColumnIndex(COLUMN_EM_CUSTOM_TYPE)));
            email.setEmIsPrimary(cursor.getString(cursor.getColumnIndex(COLUMN_EM_IS_PRIMARY)));
            email.setEmEmailPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EM_EMAIL_PRIVACY)));
            email.setEmIsDefault(cursor.getString(cursor.getColumnIndex(COLUMN_EM_IS_DEFAULT)));
            email.setEmIsVerified(cursor.getString(cursor.getColumnIndex(COLUMN_EM_IS_VERIFIED)));
            email.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

            cursor.close();
        }

        db.close();

        // return Email
        return email;
    }

    // Getting All Emails
    public ArrayList<Email> getAllEmails() {
        ArrayList<Email> arrayListEmail = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_EMAIL_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Email email = new Email();
                email.setEmId(cursor.getString(cursor.getColumnIndex(COLUMN_EM_ID)));
                email.setEmCloudId(cursor.getString(cursor.getColumnIndex(COLUMN_EM_CLOUD_ID)));
                email.setEmEmailAddress(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EM_EMAIL_ADDRESS)));
                email.setEmEmailType(cursor.getString(cursor.getColumnIndex(COLUMN_EM_EMAIL_TYPE)));
                email.setEmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EM_RECORD_INDEX_ID)));
                email.setEmCustomType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EM_CUSTOM_TYPE)));
                email.setEmIsPrimary(cursor.getString(cursor.getColumnIndex(COLUMN_EM_IS_PRIMARY)));
                email.setEmEmailPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EM_EMAIL_PRIVACY)));
                email.setEmIsDefault(cursor.getString(cursor.getColumnIndex(COLUMN_EM_IS_DEFAULT)));
                email.setEmIsVerified(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EM_IS_VERIFIED)));
                email.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding email to list
                arrayListEmail.add(email);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Email list
        return arrayListEmail;
    }

    // Getting All Emails Numbers from Profile Master Id
    public ArrayList<Email> getEmailsFromPmId(int pmId) {
        ArrayList<Email> arrayListEmails = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_EM_CLOUD_ID + ", " +
                COLUMN_EM_EMAIL_ADDRESS + ", " +
                COLUMN_EM_EMAIL_TYPE + ", " +
                COLUMN_EM_RECORD_INDEX_ID + ", " +
//                COLUMN_EM_IS_PRIMARY + ", " +
                COLUMN_EM_EMAIL_PRIVACY + ", " +
                COLUMN_EM_IS_DEFAULT + ", " +
//                COLUMN_EM_IS_VERIFIED + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_EMAIL_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Email email = new Email();
                email.setEmCloudId(cursor.getString(cursor.getColumnIndex(COLUMN_EM_CLOUD_ID)));
                email.setEmEmailAddress(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EM_EMAIL_ADDRESS)));
                email.setEmEmailType(cursor.getString(cursor.getColumnIndex(COLUMN_EM_EMAIL_TYPE)));
                email.setEmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EM_RECORD_INDEX_ID)));
//                email.setEmIsPrimary(cursor.getString(cursor.getColumnIndex(COLUMN_EM_IS_PRIMARY)));
                email.setEmEmailPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EM_EMAIL_PRIVACY)));
                email.setEmIsDefault(cursor.getString(cursor.getColumnIndex(COLUMN_EM_IS_DEFAULT)));
                /*email.setEmIsVerified(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EM_IS_VERIFIED)));*/
                email.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding Email to list
                arrayListEmails.add(email);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Mobile Number list
        return arrayListEmails;
    }

    // Getting Email Count
    public int getEmailCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_EMAIL_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single Email
    public int updateEmail(Email email) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EM_ID, email.getEmId());
        values.put(COLUMN_EM_CLOUD_ID, email.getEmCloudId());
        values.put(COLUMN_EM_EMAIL_ADDRESS, email.getEmEmailAddress());
        values.put(COLUMN_EM_EMAIL_TYPE, email.getEmEmailType());
        values.put(COLUMN_EM_RECORD_INDEX_ID, email.getEmRecordIndexId());
        values.put(COLUMN_EM_CUSTOM_TYPE, email.getEmCustomType());
        values.put(COLUMN_EM_IS_PRIMARY, email.getEmIsPrimary());
        values.put(COLUMN_EM_EMAIL_PRIVACY, email.getEmEmailPrivacy());
        values.put(COLUMN_EM_IS_DEFAULT, email.getEmIsDefault());
        values.put(COLUMN_EM_IS_VERIFIED, email.getEmIsVerified());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, email.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_EMAIL_MASTER, values, COLUMN_EM_ID + " = ?",
                new String[]{String.valueOf(email.getEmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single email
    public void deleteEmail(Email email) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_EMAIL_MASTER, COLUMN_EM_ID + " = ?",
                new String[]{String.valueOf(email.getEmId())});
        db.close();
    }
}
