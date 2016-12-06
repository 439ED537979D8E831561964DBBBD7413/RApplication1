package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.OfflineBackup;

import java.util.ArrayList;

/**
 * Created by Monal on 29/11/16.
 * <p>
 * Table operations pb_offline_backup_master
 */

public class TableOfflineBackupMaster {

    private DatabaseHandler databaseHandler;

    public TableOfflineBackupMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_PB_OFFLINE_BACKUP_MASTER = "pb_offline_backup_master";

    // Column Names
    private static final String COLUMN_OBM_ID = "obm_id";
    private static final String COLUMN_OBM_VALUE = "obm_value";
    private static final String COLUMN_RC_FLAG_MASTER_FM_ID = "rc_flag_master_fm_id";


    // Table Create Statements
    static final String CREATE_TABLE_PB_OFFLINE_BACKUP_MASTER = "CREATE TABLE " +
            TABLE_PB_OFFLINE_BACKUP_MASTER + " (" +
            " " + COLUMN_OBM_ID + " integer NOT NULL CONSTRAINT pb_offline_backup_master_pk " +
            "PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_OBM_VALUE + " text NOT NULL," +
            " " + COLUMN_RC_FLAG_MASTER_FM_ID + " integer" +
            ");";

    // Adding new backup data
    public void addOfflineBackup(OfflineBackup offlineBackup) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OBM_ID, offlineBackup.getObmId());
        values.put(COLUMN_OBM_VALUE, offlineBackup.getObmValue());
        values.put(COLUMN_RC_FLAG_MASTER_FM_ID, offlineBackup.getRcFlagMasterFmId());

        // Inserting Row
        db.insert(TABLE_PB_OFFLINE_BACKUP_MASTER, null, values);
        db.close(); // Closing database connection
    }

    // Adding array Offline Backup
    public void addArrayOfflineBackup(ArrayList<OfflineBackup> arrayListOfflineBackup) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        for (int i = 0; i < arrayListOfflineBackup.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_OBM_ID, arrayListOfflineBackup.get(i).getObmId());
            values.put(COLUMN_OBM_VALUE, arrayListOfflineBackup.get(i).getObmValue());
            values.put(COLUMN_RC_FLAG_MASTER_FM_ID, arrayListOfflineBackup.get(i)
                    .getRcFlagMasterFmId());

            // Inserting Row
            db.insert(TABLE_PB_OFFLINE_BACKUP_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    // Getting single Offline Backup
    public OfflineBackup getOfflineBackup(int obmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PB_OFFLINE_BACKUP_MASTER, new String[]{COLUMN_OBM_ID,
                COLUMN_OBM_VALUE, COLUMN_RC_FLAG_MASTER_FM_ID}, COLUMN_OBM_ID + "=?", new
                String[]{String.valueOf(obmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        OfflineBackup offlineBackup = new OfflineBackup();
        if (cursor != null) {
            offlineBackup.setObmId(cursor.getString(0));
            offlineBackup.setObmValue(cursor.getString(1));
            offlineBackup.setRcFlagMasterFmId(cursor.getString(2));

            cursor.close();
        }

        db.close();

        // return Offline Backup
        return offlineBackup;
    }

    // Getting All Offline Backup
    public ArrayList<OfflineBackup> getAllBackupData() {
        ArrayList<OfflineBackup> arrayListOfflineBackup = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PB_OFFLINE_BACKUP_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OfflineBackup offlineBackup = new OfflineBackup();
                offlineBackup.setObmId(cursor.getString(0));
                offlineBackup.setObmValue(cursor.getString(1));
                offlineBackup.setRcFlagMasterFmId(cursor.getString(2));
                // Adding profileMobileMapping to list
                arrayListOfflineBackup.add(offlineBackup);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Backup list
        return arrayListOfflineBackup;
    }

    // Getting offline Backup Count
    public int getOfflineBackupCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PB_OFFLINE_BACKUP_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single offline Backup
    public int updateOfflineBackup(OfflineBackup offlineBackup) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OBM_ID, offlineBackup.getObmId());
        values.put(COLUMN_OBM_VALUE, offlineBackup.getObmValue());
        values.put(COLUMN_RC_FLAG_MASTER_FM_ID, offlineBackup.getRcFlagMasterFmId());

        // updating row
        int isUpdated = db.update(TABLE_PB_OFFLINE_BACKUP_MASTER, values, COLUMN_OBM_ID + " = ?",
                new String[]{String.valueOf(offlineBackup.getObmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single backup
    public void deleteOfflineBackup(OfflineBackup offlineBackup) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_PB_OFFLINE_BACKUP_MASTER, COLUMN_OBM_ID + " = ?",
                new String[]{String.valueOf(offlineBackup.getObmId())});
        db.close();
    }
}
