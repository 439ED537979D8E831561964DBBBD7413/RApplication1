package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.State;

import java.util.ArrayList;

/**
 * Created by Monal on 22/10/16.
 * <p>
 * Table operations rc_country_master
 */

public class TableStateMaster {

    private DatabaseHandler databaseHandler;

    public TableStateMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    public static final String TABLE_RC_STATE_MASTER = "rc_state_master";


    // Column Names
    private static final String COLUMN_SM_ID = "sm_id";
    private static final String COLUMN_SM_STATE_NAME = "sm_name";
    private static final String COLUMN_SM_COUNTRY_ID = "sm_country_id";

    // Table Create Statements
    static final String CREATE_TABLE_RC_STATE_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_STATE_MASTER +
            "(" + COLUMN_SM_ID + " integer NOT NULL CONSTRAINT rc_state_master_pk PRIMARY KEY," +
            " " + COLUMN_SM_STATE_NAME + " text NOT NULL," +
            " " + COLUMN_SM_COUNTRY_ID + " integer NOT NULL" +
            ");";

    // Adding new State
    public void addState(State state) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SM_ID, state.getStateId());
        values.put(COLUMN_SM_STATE_NAME, state.getStateName());
        values.put(COLUMN_SM_COUNTRY_ID, state.getCountryId());

        // Inserting Row
        db.insert(TABLE_RC_STATE_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding Array new state
    public void addArrayState(ArrayList<State> arrayListState) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        for (int i = 0; i < arrayListState.size(); i++) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_SM_ID, arrayListState.get(i).getStateId());
            values.put(COLUMN_SM_STATE_NAME, arrayListState.get(i).getStateName());
            values.put(COLUMN_SM_COUNTRY_ID, arrayListState.get(i).getCountryId());

            int count = 0;
            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RC_STATE_MASTER + " " +
                    "WHERE " + COLUMN_SM_ID + " = " + arrayListState.get(i).getCountryId(), null);
            if (mCount != null) {
                mCount.moveToFirst();
                count = mCount.getInt(0);
                mCount.close();
            }

            if (count > 0) {
                // Update if already exists
                db.update(TABLE_RC_STATE_MASTER, values, COLUMN_SM_ID + " = ?",
                        new String[]{String.valueOf(arrayListState.get(i).getCountryId())});
            } else {
                // Inserting Row
                db.insert(TABLE_RC_STATE_MASTER, null, values);
            }

        }
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single state
    public State getState(int stateId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_STATE_MASTER, new String[]{COLUMN_SM_ID,
                COLUMN_SM_STATE_NAME, COLUMN_SM_COUNTRY_ID}, COLUMN_SM_ID + "=?", new
                String[]{String.valueOf(stateId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        State state = new State();
        if (cursor != null) {
            state.setStateId(cursor.getString(cursor.getColumnIndex(COLUMN_SM_ID)));
            state.setStateName(cursor.getString(cursor.getColumnIndex(COLUMN_SM_STATE_NAME)));
            state.setCountryId(cursor.getString(cursor.getColumnIndex(COLUMN_SM_COUNTRY_ID)));

            cursor.close();
        }

        db.close();

        // return state
        return state;
    }

    // Getting All state
    public ArrayList<State> getStateFromCountry(String countryId) {
        ArrayList<State> arrayListState = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_STATE_MASTER + " WHERE " +
                COLUMN_SM_COUNTRY_ID + " = " + countryId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                State state = new State();
                state.setStateId(cursor.getString(cursor.getColumnIndex(COLUMN_SM_ID)));
                state.setStateName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_SM_STATE_NAME)));
                state.setCountryId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_SM_COUNTRY_ID)));
                // Adding state to list
                arrayListState.add(state);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return state list
        return arrayListState;
    }

    // Getting All States
    public ArrayList<String> getAllStateName() {
        ArrayList<String> arrayListStateName = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + COLUMN_SM_STATE_NAME + " FROM " +
                TABLE_RC_STATE_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding country name to list
                arrayListStateName.add(cursor.getString(cursor.getColumnIndex
                        (COLUMN_SM_STATE_NAME)));
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return country list
        return arrayListStateName;
    }

    // Getting state Count
    public int getStateCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_RC_STATE_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
//        int count = cursor.getCount();
        int count = 0;

        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();

        // return count
        return count;
    }

    // Updating single state
    public int updateState(State state) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SM_ID, state.getStateId());
        values.put(COLUMN_SM_STATE_NAME, state.getStateName());
        values.put(COLUMN_SM_COUNTRY_ID, state.getCountryId());

        // updating row
        int isUpdated = db.update(TABLE_RC_STATE_MASTER, values, COLUMN_SM_ID + " = ?",
                new String[]{String.valueOf(state.getStateId())});

        db.close();

        return isUpdated;
    }

    // Deleting single state
    public void deleteState(State state) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_STATE_MASTER, COLUMN_SM_ID + " = ?",
                new String[]{String.valueOf(state.getStateId())});
        db.close();
    }
}
