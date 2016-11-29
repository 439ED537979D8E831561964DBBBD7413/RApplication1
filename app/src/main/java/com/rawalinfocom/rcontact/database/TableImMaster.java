package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.ImAccount;

import java.util.ArrayList;

/**
 * Created by Monal on 29/11/16.
 * <p>
 * Table operations rc_im_master
 */

public class TableImMaster {

    private DatabaseHandler databaseHandler;

    public TableImMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_IM_MASTER = "rc_im_master";

    // Column Names
    private static final String COLUMN_IM_ID = "im_id";
    private static final String COLUMN_IM_IM_TYPE = "im_im_type";
    private static final String COLUMN_IM_CUSTOM_TYPE = "im_custom_type";
    private static final String COLUMN_IM_IM_PROTOCOL = "im_im_protocol";
    private static final String COLUMN_IM_IM_PRIVACY = "im_im_privacy";
    private static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    static final String CREATE_TABLE_RC_IM_MASTER = "CREATE TABLE " + TABLE_RC_IM_MASTER + " (" +
            " " + COLUMN_IM_ID + " integer NOT NULL CONSTRAINT rc_im_master_pk PRIMARY KEY," +
            " " + COLUMN_IM_IM_TYPE + " text NOT NULL," +
            " " + COLUMN_IM_CUSTOM_TYPE + " text," +
            " " + COLUMN_IM_IM_PROTOCOL + " text NOT NULL," +
            " " + COLUMN_IM_IM_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new ImAccount Account
    public void addImAccount(ImAccount imAccount) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IM_ID, imAccount.getImId());
        values.put(COLUMN_IM_IM_TYPE, imAccount.getImImType());
        values.put(COLUMN_IM_CUSTOM_TYPE, imAccount.getImCustomType());
        values.put(COLUMN_IM_IM_PROTOCOL, imAccount.getImImProtocol());
        values.put(COLUMN_IM_IM_PRIVACY, imAccount.getImImPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, imAccount.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_IM_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Im Account
    public ImAccount getImAccount(int imId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_IM_MASTER, new String[]{COLUMN_IM_ID,
                        COLUMN_IM_IM_TYPE, COLUMN_IM_CUSTOM_TYPE, COLUMN_IM_IM_PROTOCOL,
                        COLUMN_IM_IM_PRIVACY, COLUMN_RC_PROFILE_MASTER_PM_ID},
                COLUMN_IM_ID + "=?", new String[]{String.valueOf(imId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ImAccount imAccount = new ImAccount();
        if (cursor != null) {
            imAccount.setImId(cursor.getString(0));
            imAccount.setImImType(cursor.getString(1));
            imAccount.setImCustomType(cursor.getString(2));
            imAccount.setImImProtocol(cursor.getString(3));
            imAccount.setImImPrivacy(cursor.getString(4));
            imAccount.setRcProfileMasterPmId(cursor.getString(5));

            cursor.close();
        }

        db.close();

        // return Im Account
        return imAccount;
    }

    // Getting All Im Accounts
    public ArrayList<ImAccount> getAllImAccounts() {
        ArrayList<ImAccount> arrayListImAccount = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_IM_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ImAccount imAccount = new ImAccount();
                imAccount.setImId(cursor.getString(0));
                imAccount.setImImType(cursor.getString(1));
                imAccount.setImCustomType(cursor.getString(2));
                imAccount.setImImProtocol(cursor.getString(3));
                imAccount.setImImPrivacy(cursor.getString(4));
                imAccount.setRcProfileMasterPmId(cursor.getString(5));
                // Adding Im Account to list
                arrayListImAccount.add(imAccount);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Im Account list
        return arrayListImAccount;
    }

    // Getting Im Account Count
    public int getImAccountCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_IM_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single Im Account
    public int updateImAccount(ImAccount imAccount) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IM_ID, imAccount.getImId());
        values.put(COLUMN_IM_IM_TYPE, imAccount.getImImType());
        values.put(COLUMN_IM_CUSTOM_TYPE, imAccount.getImCustomType());
        values.put(COLUMN_IM_IM_PROTOCOL, imAccount.getImImProtocol());
        values.put(COLUMN_IM_IM_PRIVACY, imAccount.getImImPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, imAccount.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_IM_MASTER, values, COLUMN_IM_ID + " = ?",
                new String[]{String.valueOf(imAccount.getImId())});

        db.close();

        return isUpdated;
    }

    // Deleting single Im Account
    public void deleteImAccount(ImAccount imAccount) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_IM_MASTER, COLUMN_IM_ID + " = ?",
                new String[]{String.valueOf(imAccount.getImId())});
        db.close();
    }
}
