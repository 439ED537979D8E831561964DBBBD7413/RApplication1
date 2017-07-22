package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.model.ContactRequestData;
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
    static final String TABLE_RC_IM_MASTER = "rc_im_master";

    // Column Names
    private static final String COLUMN_IM_ID = "im_id";
    static final String COLUMN_IM_RECORD_INDEX_ID = "im_record_index_id";
    //    static final String COLUMN_IM_IM_TYPE = "im_im_type";
//    private static final String COLUMN_IM_CUSTOM_TYPE = "im_custom_type";
    static final String COLUMN_IM_DETAIL = "im_detail";
    static final String COLUMN_IM_PROTOCOL = "im_protocol";
    static final String COLUMN_IM_PRIVACY = "im_privacy";
    static final String COLUMN_IM_IS_PRIVATE = "im_is_private";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    static final String CREATE_TABLE_RC_IM_MASTER = "CREATE TABLE " + TABLE_RC_IM_MASTER + " (" +
            " " + COLUMN_IM_ID + " integer NOT NULL CONSTRAINT rc_im_master_pk PRIMARY KEY," +
            " " + COLUMN_IM_RECORD_INDEX_ID + " text, " +
            " " + COLUMN_IM_DETAIL + " text," +
            " " + COLUMN_IM_PROTOCOL + " text NOT NULL," +
            " " + COLUMN_IM_PRIVACY + " integer DEFAULT 2," +
            " " + COLUMN_IM_IS_PRIVATE + " integer," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer," +
            " UNIQUE(" + COLUMN_IM_RECORD_INDEX_ID + ", " + COLUMN_RC_PROFILE_MASTER_PM_ID + ")" +
            ");";

    // Adding new ImAccount Account
    public void addImAccount(ImAccount imAccount) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IM_ID, imAccount.getImId());
        values.put(COLUMN_IM_RECORD_INDEX_ID, imAccount.getImRecordIndexId());
        values.put(COLUMN_IM_DETAIL, imAccount.getImImDetail());
        values.put(COLUMN_IM_PROTOCOL, imAccount.getImImProtocol());
        values.put(COLUMN_IM_PRIVACY, imAccount.getImImPrivacy());
        values.put(COLUMN_IM_IS_PRIVATE, imAccount.getImIsPrivate());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, imAccount.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_IM_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding array Im Account
    public void addArrayImAccount(ArrayList<ImAccount> arrayListImAccount) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListImAccount.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_IM_ID, arrayListImAccount.get(i).getImId());
            values.put(COLUMN_IM_RECORD_INDEX_ID, arrayListImAccount.get(i).getImRecordIndexId());
            values.put(COLUMN_IM_DETAIL, arrayListImAccount.get(i).getImImDetail());
            values.put(COLUMN_IM_PROTOCOL, arrayListImAccount.get(i).getImImProtocol());
            values.put(COLUMN_IM_PRIVACY, arrayListImAccount.get(i).getImImPrivacy());
            values.put(COLUMN_IM_IS_PRIVATE, arrayListImAccount.get(i).getImIsPrivate());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListImAccount.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_IM_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    public void deleteData(String RcpPmId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        int count = db.delete(TABLE_RC_IM_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
                RcpPmId, null);
        if (count > 0) System.out.println("RContact data delete ");

        db.close(); // Closing database connection
    }

    // Adding or Updating array Im Account
    public void addUpdateArrayImAccount(ArrayList<ImAccount> arrayListImAccount, String RcpPmId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        int count = db.delete(TABLE_RC_IM_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + RcpPmId, null);
        if (count > 0) System.out.println("RContact data delete ");

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListImAccount.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_IM_ID, arrayListImAccount.get(i).getImId());
            values.put(COLUMN_IM_RECORD_INDEX_ID, arrayListImAccount.get(i).getImRecordIndexId());
            values.put(COLUMN_IM_DETAIL, arrayListImAccount.get(i).getImImDetail());
            values.put(COLUMN_IM_PROTOCOL, arrayListImAccount.get(i).getImImProtocol());
            values.put(COLUMN_IM_PRIVACY, MoreObjects.firstNonNull(
                    Integer.parseInt(arrayListImAccount.get(i).getImImPrivacy()), 0));
            values.put(COLUMN_IM_IS_PRIVATE,
                    MoreObjects.firstNonNull(arrayListImAccount.get(i).getImIsPrivate(), 0));
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListImAccount.get(i).getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_IM_MASTER, null, values);

//            int count = 0;
//            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RC_IM_MASTER + " " +
//                    "WHERE " + COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
//                    arrayListImAccount.get(i).getRcProfileMasterPmId(), null);
//            if (mCount != null) {
//                mCount.moveToFirst();
//                count = mCount.getInt(0);
//                mCount.close();
//            }
//
//            if (count > 0) {
//                // Update if already exists
//                db.update(TABLE_RC_IM_MASTER, values, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
//                        arrayListImAccount.get(i).getRcProfileMasterPmId(), null);
//            } else {
//                // Inserting Row
//                values.put(COLUMN_IM_ID, arrayListImAccount.get(i).getImId());
//                db.insert(TABLE_RC_IM_MASTER, null, values);
//            }
        }
        db.close(); // Closing database connection
    }


    // Getting All Im Accounts from Profile Master Id
    public ArrayList<ImAccount> getImAccountFromPmId(int pmId) {
        ArrayList<ImAccount> arrayListImAccount = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " +
                COLUMN_IM_RECORD_INDEX_ID + ", " +
                COLUMN_IM_DETAIL + ", " +
                COLUMN_IM_PROTOCOL + ", " +
                COLUMN_IM_PRIVACY + ", " +
                COLUMN_IM_IS_PRIVATE + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_IM_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ImAccount imAccount = new ImAccount();
                imAccount.setImRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_IM_RECORD_INDEX_ID)));
                imAccount.setImImDetail(cursor.getString(cursor.getColumnIndex(COLUMN_IM_DETAIL)));
                imAccount.setImImProtocol(cursor.getString(cursor.getColumnIndex
                        (COLUMN_IM_PROTOCOL)));
                imAccount.setImImPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_IM_PRIVACY)));
                imAccount.setImIsPrivate(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_IM_IS_PRIVATE)));
                imAccount.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding Im Account to list
                arrayListImAccount.add(imAccount);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Im Account list
        return arrayListImAccount;
    }

    // Getting single Im Account
    public ImAccount getImAccount(int imId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_IM_MASTER, new String[]{COLUMN_IM_ID,
                COLUMN_IM_RECORD_INDEX_ID, COLUMN_IM_DETAIL, COLUMN_IM_PROTOCOL, COLUMN_IM_IS_PRIVATE,
                COLUMN_IM_PRIVACY, COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_IM_ID + "=?", new
                String[]{String.valueOf(imId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ImAccount imAccount = new ImAccount();
        if (cursor != null) {
            imAccount.setImId(cursor.getString(cursor.getColumnIndex(COLUMN_IM_ID)));
            imAccount.setImRecordIndexId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_IM_RECORD_INDEX_ID)));
            imAccount.setImImDetail(cursor.getString(cursor.getColumnIndex(COLUMN_IM_DETAIL)));
            imAccount.setImImProtocol(cursor.getString(cursor.getColumnIndex
                    (COLUMN_IM_PROTOCOL)));
            imAccount.setImImPrivacy(cursor.getString(cursor.getColumnIndex(COLUMN_IM_PRIVACY)));
            imAccount.setImIsPrivate(cursor.getInt(cursor.getColumnIndex(COLUMN_IM_IS_PRIVATE)));
            imAccount.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

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
                imAccount.setImId(cursor.getString(cursor.getColumnIndex(COLUMN_IM_ID)));
                imAccount.setImRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_IM_RECORD_INDEX_ID)));
                imAccount.setImImDetail(cursor.getString(cursor.getColumnIndex(COLUMN_IM_DETAIL)));
                imAccount.setImImProtocol(cursor.getString(cursor.getColumnIndex
                        (COLUMN_IM_PROTOCOL)));
                imAccount.setImImPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_IM_PRIVACY)));
                imAccount.setImIsPrivate(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_IM_IS_PRIVATE)));
                imAccount.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
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
        values.put(COLUMN_IM_RECORD_INDEX_ID, imAccount.getImRecordIndexId());
        values.put(COLUMN_IM_DETAIL, imAccount.getImImDetail());
        values.put(COLUMN_IM_PROTOCOL, imAccount.getImImProtocol());
        values.put(COLUMN_IM_PRIVACY, imAccount.getImImPrivacy());
        values.put(COLUMN_IM_IS_PRIVATE, imAccount.getImIsPrivate());
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

    // Deleting single ImAccount From RcpId
    public void deleteImAccount(String rcpId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_IM_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?",
                new String[]{String.valueOf(rcpId)});
        db.close();
    }

    public int updatePrivacySetting(ContactRequestData obj, String cloudMongoId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IM_IS_PRIVATE, 0);
        values.put(COLUMN_IM_DETAIL, obj.getImAccountDetails());

        // updating row
        int isUpdated = db.update(TABLE_RC_IM_MASTER, values, COLUMN_IM_RECORD_INDEX_ID + " = ?",
                new String[]{cloudMongoId});

        db.close();

        return isUpdated;
    }
}
