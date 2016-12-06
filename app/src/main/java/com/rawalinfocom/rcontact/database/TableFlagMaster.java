package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Flag;

import java.util.ArrayList;

/**
 * Created by Monal on 29/10/16.
 * <p>
 * Table operations rc_flag_master
 */

public class TableFlagMaster {

    private DatabaseHandler databaseHandler;

    public TableFlagMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_FLAG_MASTER = "rc_flag_master";


    // Column Names
    private static final String COLUMN_FM_ID = "fm_id";
    private static final String COLUMN_FM_VALUE = "fm_value";

    // Table Create Statements
    static final String CREATE_TABLE_RC_FLAG_MASTER = "CREATE TABLE " + TABLE_RC_FLAG_MASTER +
            " (" +
            " " + COLUMN_FM_ID + " integer NOT NULL CONSTRAINT rc_flag_master_pk PRIMARY KEY," +
            " " + COLUMN_FM_VALUE + " text NOT NULL" +
            ");";

    // Adding new Flag
    public void addFlag(Flag flag) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FM_ID, flag.getFmId());
        values.put(COLUMN_FM_VALUE, flag.getFmValue());

        // Inserting Row
        db.insert(TABLE_RC_FLAG_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Flag
    public Flag getFlag(int flagId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_FLAG_MASTER, new String[]{COLUMN_FM_ID,
                COLUMN_FM_VALUE}, COLUMN_FM_ID + "=?", new String[]{String.valueOf
                (flagId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Flag flag = new Flag();
        if (cursor != null) {
            flag.setFmId(cursor.getString(0));
            flag.setFmValue(cursor.getString(1));

            cursor.close();
        }

        db.close();

        // return Flag
        return flag;
    }

    // Getting All Flags
    public ArrayList<Flag> getAllFlags() {
        ArrayList<Flag> arrayListFlag = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_FLAG_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Flag flag = new Flag();
                flag.setFmId(cursor.getString(0));
                flag.setFmValue(cursor.getString(1));
                // Adding flag to list
                arrayListFlag.add(flag);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return flag list
        return arrayListFlag;
    }

    // Getting Flag Count
    public int getFlagCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_FLAG_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        // return count
        return count;
    }

    // Updating single Flag
    public int updateFlag(Flag flag) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FM_ID, flag.getFmId());
        values.put(COLUMN_FM_VALUE, flag.getFmValue());

        // updating row
        int isUpdated = db.update(TABLE_RC_FLAG_MASTER, values, COLUMN_FM_ID + " = ?",
                new String[]{String.valueOf(flag.getFmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single Flag
    public void deleteFlag(Flag Flag) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_FLAG_MASTER, COLUMN_FM_ID + " = ?",
                new String[]{String.valueOf(Flag.getFmId())});
        db.close();
    }
}
