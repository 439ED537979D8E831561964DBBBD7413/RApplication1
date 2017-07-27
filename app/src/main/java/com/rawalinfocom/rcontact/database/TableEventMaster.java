package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.model.ContactRequestData;
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
    static final String COLUMN_EVM_RECORD_INDEX_ID = "evm_record_index_id";
    static final String COLUMN_EVM_START_DATE = "evm_start_date";
    static final String COLUMN_EVM_EVENT_TYPE = "evm_event_type";
    static final String COLUMN_EVM_IS_YEAR_HIDDEN = "evm_is_year_hidden";  //0 not hidden , 1 hidden
    static final String COLUMN_EVM_EVENT_PRIVACY = "evm_event_privacy";
    static final String COLUMN_EVM_IS_PRIVATE = "evm_is_private";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";

    //    private static final String COLUMN_EVM_CUSTOM_TYPE = "evm_custom_type";

    // Table Create Statements
    static final String CREATE_TABLE_RC_EVENT_MASTER = "CREATE TABLE " + TABLE_RC_EVENT_MASTER +
            " (" +
            " " + COLUMN_EVM_ID + " integer NOT NULL CONSTRAINT rc_event_master_pk PRIMARY KEY," +
            " " + COLUMN_EVM_RECORD_INDEX_ID + " text," +
            " " + COLUMN_EVM_START_DATE + " text NOT NULL," +
            " " + COLUMN_EVM_EVENT_TYPE + " text NOT NULL," +
            " " + COLUMN_EVM_IS_YEAR_HIDDEN + " integer," +
            " " + COLUMN_EVM_IS_PRIVATE + " integer," +
            " " + COLUMN_EVM_EVENT_PRIVACY + " integer DEFAULT 2," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer," +
            " UNIQUE(" + COLUMN_EVM_RECORD_INDEX_ID + ", " + COLUMN_RC_PROFILE_MASTER_PM_ID + ")" +
            ");";

    // Adding new Event
    public void addEvent(Event event) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVM_ID, event.getEvmId());
        values.put(COLUMN_EVM_RECORD_INDEX_ID, event.getEvmRecordIndexId());
        values.put(COLUMN_EVM_START_DATE, event.getEvmStartDate());
        values.put(COLUMN_EVM_EVENT_TYPE, event.getEvmEventType());
        values.put(COLUMN_EVM_IS_YEAR_HIDDEN, event.getEvmIsYearHidden());
        values.put(COLUMN_EVM_EVENT_PRIVACY, event.getEvmEventPrivacy());
        values.put(COLUMN_EVM_IS_PRIVATE, event.getEvmIsPrivate());
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
            values.put(COLUMN_EVM_RECORD_INDEX_ID, arrayListEvent.get(i).getEvmRecordIndexId());
            values.put(COLUMN_EVM_START_DATE, arrayListEvent.get(i).getEvmStartDate());
            values.put(COLUMN_EVM_EVENT_TYPE, arrayListEvent.get(i).getEvmEventType());
            values.put(COLUMN_EVM_IS_YEAR_HIDDEN, arrayListEvent.get(i).getEvmIsYearHidden());
            values.put(COLUMN_EVM_EVENT_PRIVACY, arrayListEvent.get(i).getEvmEventPrivacy());
            values.put(COLUMN_EVM_IS_PRIVATE, arrayListEvent.get(i).getEvmIsPrivate());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListEvent.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_EVENT_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    public void deleteData(String RcpPmId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        int count = db.delete(TABLE_RC_EVENT_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
                RcpPmId, null);
        if (count > 0) System.out.println("RContact data delete ");

        db.close(); // Closing database connection
    }

    // Adding or Updating array Event
    public void addUpdateArrayEvent(ArrayList<Event> arrayListEvent, String RcpPmId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        int count = db.delete(TABLE_RC_EVENT_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + RcpPmId, null);
        if (count > 0) System.out.println("RContact data delete ");

        for (int i = 0; i < arrayListEvent.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_EVM_ID, arrayListEvent.get(i).getEvmId());
            values.put(COLUMN_EVM_RECORD_INDEX_ID, arrayListEvent.get(i).getEvmRecordIndexId());
            values.put(COLUMN_EVM_START_DATE, arrayListEvent.get(i).getEvmStartDate());
            values.put(COLUMN_EVM_EVENT_TYPE, arrayListEvent.get(i).getEvmEventType());
            values.put(COLUMN_EVM_IS_YEAR_HIDDEN, arrayListEvent.get(i).getEvmIsYearHidden());
            values.put(COLUMN_EVM_IS_PRIVATE, MoreObjects.firstNonNull(arrayListEvent.get(i).getEvmIsPrivate(), 0));
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListEvent.get(i).getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_EVENT_MASTER, null, values);

//            int count = 0;
//            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RC_EVENT_MASTER + " " +
//                    "WHERE " + COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
//                    arrayListEvent.get(i).getRcProfileMasterPmId(), null);
//            if (mCount != null) {
//                mCount.moveToFirst();
//                count = mCount.getInt(0);
//                mCount.close();
//            }
//
//            if (count > 0) {
//                // Update if already exists
//                db.update(TABLE_RC_EVENT_MASTER, values, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
//                        arrayListEvent.get(i).getRcProfileMasterPmId(), null);
//            } else {
//                // Inserting Row
//                values.put(COLUMN_EVM_ID, arrayListEvent.get(i).getEvmId());
//                db.insert(TABLE_RC_EVENT_MASTER, null, values);
//            }
        }
        db.close(); // Closing database connection
    }

    // Getting single Event
    public Event getEvent(int evmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_EVENT_MASTER, new String[]{COLUMN_EVM_ID,
                        COLUMN_EVM_RECORD_INDEX_ID, COLUMN_EVM_START_DATE, COLUMN_EVM_EVENT_TYPE,
                        COLUMN_EVM_IS_YEAR_HIDDEN, COLUMN_EVM_EVENT_PRIVACY, COLUMN_EVM_IS_PRIVATE,
                        COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_EVM_ID
                        + "=?",
                new String[]{String.valueOf(evmId)}, null, null, null, null);

        Event event = new Event();
        if (cursor.moveToFirst()) {
            event.setEvmId(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_ID)));
            event.setEvmRecordIndexId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EVM_RECORD_INDEX_ID)));
            event.setEvmStartDate(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_START_DATE)));
            event.setEvmEventType(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_EVENT_TYPE)));
            event.setEvmIsYearHidden(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_EVM_IS_YEAR_HIDDEN)));
            event.setEvmEventPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EVM_EVENT_PRIVACY)));
            event.setEvmIsPrivate(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_EVM_IS_PRIVATE)));
            event.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

            cursor.close();
        }

        db.close();

        // return event
        return event;
    }

    public Event getEventByEvmRecordIndexId(String evmRecordIndexId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_EVENT_MASTER, new String[]{COLUMN_EVM_ID,
                        COLUMN_EVM_RECORD_INDEX_ID, COLUMN_EVM_START_DATE, COLUMN_EVM_EVENT_TYPE,
                        COLUMN_EVM_IS_YEAR_HIDDEN, COLUMN_EVM_EVENT_PRIVACY, COLUMN_EVM_IS_PRIVATE,
                        COLUMN_RC_PROFILE_MASTER_PM_ID},
                COLUMN_EVM_RECORD_INDEX_ID + "=?", new String[]{evmRecordIndexId
                }, null, null, null, null);

        Event event = new Event();
        if (cursor.moveToFirst()) {
            event.setEvmId(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_ID)));
            event.setEvmRecordIndexId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EVM_RECORD_INDEX_ID)));
            event.setEvmStartDate(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_START_DATE)));
            event.setEvmEventType(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_EVENT_TYPE)));
            event.setEvmIsYearHidden(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_EVM_IS_YEAR_HIDDEN)));
            event.setEvmEventPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EVM_EVENT_PRIVACY)));
            event.setEvmIsPrivate(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_EVM_IS_PRIVATE)));
            event.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

            cursor.close();
        }

        db.close();

        // return event
        return event;
    }

    // Getting All events from Profile Master Id
    public ArrayList<Event> getEventsFromPmId(int pmId) {
        ArrayList<Event> arrayListEvent = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_EVM_RECORD_INDEX_ID + ", " +
                COLUMN_EVM_START_DATE + ", " +
                COLUMN_EVM_EVENT_TYPE + ", " +
                COLUMN_EVM_EVENT_PRIVACY + ", " +
                COLUMN_EVM_IS_PRIVATE + ", " +
                COLUMN_EVM_IS_YEAR_HIDDEN + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_EVENT_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setEvmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_RECORD_INDEX_ID)));
                event.setEvmStartDate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_START_DATE)));
                event.setEvmEventType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_EVENT_TYPE)));
                event.setEvmIsPrivate(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_EVM_IS_PRIVATE)));
                event.setEvmIsYearHidden(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_EVM_IS_YEAR_HIDDEN)));
                event.setEvmEventPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_EVENT_PRIVACY)));
                event.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding Event to list
                arrayListEvent.add(event);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Event list
        return arrayListEvent;
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
                event.setEvmId(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_ID)));
                event.setEvmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_RECORD_INDEX_ID)));
                event.setEvmStartDate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_START_DATE)));
                event.setEvmEventType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_EVENT_TYPE)));
                event.setEvmIsYearHidden(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_EVM_IS_YEAR_HIDDEN)));
                event.setEvmIsPrivate(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_EVM_IS_PRIVATE)));
                event.setEvmEventPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_EVENT_PRIVACY)));
                event.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding event to list
                arrayListEvent.add(event);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Event list
        return arrayListEvent;
    }

    // Getting All Events
    public ArrayList<Event> getAllEventsBetWeenExceptCurrentUser(String fromDate, String toDate,
                                                                 int loggedInUserPMID) {
        ArrayList<Event> arrayListEvent = new ArrayList<>();
        //SELECT  distinct evm_record_index_id, evm_start_date, evm_event_type,
        // rc_profile_master_pm_id FROM rc_event_master
        // WHERE rc_profile_master_pm_id !=2 and strftime('%m-%d',evm_start_date) between '03-23'
        // and '03-27' order by strftime('%m-%d',evm_start_date) asc


        String selectQuery = "SELECT DISTINCT " + COLUMN_EVM_RECORD_INDEX_ID + ", " +
                COLUMN_EVM_START_DATE + ", " + COLUMN_EVM_EVENT_TYPE + ", " + COLUMN_EVM_EVENT_PRIVACY + ", " + COLUMN_EVM_IS_YEAR_HIDDEN + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " + TABLE_RC_EVENT_MASTER +
                " WHERE " + COLUMN_RC_PROFILE_MASTER_PM_ID + " !=" + loggedInUserPMID + " and " +
                "strftime('%m-%d'," + COLUMN_EVM_START_DATE + ") between '" + fromDate + "' and '" +
                toDate + "' order by strftime('%m-%d', " + COLUMN_EVM_START_DATE + ") asc";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                //  event.setEvmId(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_ID)));
                event.setEvmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_RECORD_INDEX_ID)));
                event.setEvmStartDate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_START_DATE)));
                event.setEvmEventType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_EVENT_TYPE)));
                event.setEvmIsYearHidden(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_EVM_IS_YEAR_HIDDEN)));
                event.setEvmEventPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EVM_EVENT_PRIVACY)));
                event.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
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
        values.put(COLUMN_EVM_RECORD_INDEX_ID, event.getEvmRecordIndexId());
        values.put(COLUMN_EVM_START_DATE, event.getEvmStartDate());
        values.put(COLUMN_EVM_EVENT_TYPE, event.getEvmEventType());
        values.put(COLUMN_EVM_IS_YEAR_HIDDEN, event.getEvmIsYearHidden());
        values.put(COLUMN_EVM_EVENT_PRIVACY, event.getEvmEventPrivacy());
        values.put(COLUMN_EVM_IS_PRIVATE, event.getEvmIsPrivate());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, event.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_EVENT_MASTER, values, COLUMN_EVM_ID + " = ?",
                new String[]{String.valueOf(event.getEvmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single event
    public void deleteEvent(Event event) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_EVENT_MASTER, COLUMN_EVM_ID + " = ?",
                new String[]{String.valueOf(event.getEvmId())});
        db.close();
    }

    // Deleting single ImAccount From RcpId
    public void deleteEvent(String rcpId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_EVENT_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?",
                new String[]{String.valueOf(rcpId)});
        db.close();
    }

    public int updatePrivacySetting(ContactRequestData obj, String cloudMongoId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVM_IS_PRIVATE, 0);
        values.put(COLUMN_EVM_START_DATE, obj.getEventDatetime());
        values.put(COLUMN_EVM_IS_YEAR_HIDDEN, obj.getIsYearHidden());
        // updating row
        int isUpdated = db.update(TABLE_RC_EVENT_MASTER, values, COLUMN_EVM_RECORD_INDEX_ID + " =" +
                        " ?",
                new String[]{cloudMongoId});

        db.close();

        return isUpdated;
    }
}
