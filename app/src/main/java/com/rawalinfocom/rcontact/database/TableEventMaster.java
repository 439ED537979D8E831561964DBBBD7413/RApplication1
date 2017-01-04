package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Event;

import java.util.ArrayList;

/**
 * Created by Monal on 29/11/16.
 * <p>
 * Table operations rc_event_master
 */

public class TableEventMaster {

    private DatabaseHandler databaseHandler;

    public TableEventMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_EVENT_MASTER = "rc_event_master";

    // Column Names
    private static final String COLUMN_EVM_ID = "evm_id";
    static final String COLUMN_EVM_START_DATE = "evm_start_date";
    static final String COLUMN_EVM_EVENT_TYPE = "evm_event_type";
    private static final String COLUMN_EVM_CUSTOM_TYPE = "evm_custom_type";
    static final String COLUMN_EVM_EVENT_PRIVACY = "evm_event_privacy";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    static final String CREATE_TABLE_RC_EVENT_MASTER = "CREATE TABLE " + TABLE_RC_EVENT_MASTER +
            " (" +
            " " + COLUMN_EVM_ID + " integer NOT NULL CONSTRAINT rc_event_master_pk PRIMARY KEY," +
            " " + COLUMN_EVM_START_DATE + " datetime NOT NULL," +
            " " + COLUMN_EVM_EVENT_TYPE + " text NOT NULL," +
            " " + COLUMN_EVM_CUSTOM_TYPE + " text," +
            " " + COLUMN_EVM_EVENT_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new Event
    public void addEvent(Event event) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVM_ID, event.getEvmId());
        values.put(COLUMN_EVM_START_DATE, event.getEvmStartDate());
        values.put(COLUMN_EVM_EVENT_TYPE, event.getEvmEventType());
        values.put(COLUMN_EVM_CUSTOM_TYPE, event.getEvmCustomType());
        values.put(COLUMN_EVM_EVENT_PRIVACY, event.getEvmEventPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, event.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_EVENT_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding array Event
    public void addArrayEvent(ArrayList<Event> arrayListEvent) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListEvent.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EVM_ID, arrayListEvent.get(i).getEvmId());
            values.put(COLUMN_EVM_START_DATE, arrayListEvent.get(i).getEvmStartDate());
            values.put(COLUMN_EVM_EVENT_TYPE, arrayListEvent.get(i).getEvmEventType());
            values.put(COLUMN_EVM_CUSTOM_TYPE, arrayListEvent.get(i).getEvmCustomType());
            values.put(COLUMN_EVM_EVENT_PRIVACY, arrayListEvent.get(i).getEvmEventPrivacy());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListEvent.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_EVENT_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    // Getting single Event
    public Event getEvent(int evmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_EVENT_MASTER, new String[]{COLUMN_EVM_ID,
                        COLUMN_EVM_START_DATE, COLUMN_EVM_EVENT_TYPE, COLUMN_EVM_CUSTOM_TYPE,
                        COLUMN_EVM_EVENT_PRIVACY, COLUMN_RC_PROFILE_MASTER_PM_ID},
                COLUMN_EVM_ID + "=?", new String[]{String.valueOf(evmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Event event = new Event();
        if (cursor != null) {
            event.setEvmId(cursor.getString(0));
            event.setEvmStartDate(cursor.getString(1));
            event.setEvmEventType(cursor.getString(2));
            event.setEvmCustomType(cursor.getString(3));
            event.setEvmEventPrivacy(cursor.getString(4));
            event.setRcProfileMasterPmId(cursor.getString(5));

            cursor.close();
        }

        db.close();

        // return event
        return event;
    }

    // Getting All Events
    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> arrayListEvent = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_EVENT_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setEvmId(cursor.getString(0));
                event.setEvmStartDate(cursor.getString(1));
                event.setEvmEventType(cursor.getString(2));
                event.setEvmCustomType(cursor.getString(3));
                event.setEvmEventPrivacy(cursor.getString(4));
                event.setRcProfileMasterPmId(cursor.getString(5));
                // Adding event to list
                arrayListEvent.add(event);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Event list
        return arrayListEvent;
    }

    // Getting Event Count
    public int getEventCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_EVENT_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single Event
    public int updateEvent(Event event) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVM_ID, event.getEvmId());
        values.put(COLUMN_EVM_START_DATE, event.getEvmStartDate());
        values.put(COLUMN_EVM_EVENT_TYPE, event.getEvmEventType());
        values.put(COLUMN_EVM_CUSTOM_TYPE, event.getEvmCustomType());
        values.put(COLUMN_EVM_EVENT_PRIVACY, event.getEvmEventPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, event.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_EVENT_MASTER, values, COLUMN_EVM_ID + " = ?",
                new String[]{String.valueOf(event.getEvmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single event
    public void deleteEmail(Event event) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_EVENT_MASTER, COLUMN_EVM_ID + " = ?",
                new String[]{String.valueOf(event.getEvmId())});
        db.close();
    }
}
