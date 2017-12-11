package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.ProfileDataOperationAadharNumber;
import com.rawalinfocom.rcontact.model.SpamDataType;

import java.util.ArrayList;

/**
 * Created by Aniruddh on 06/12/17.
 */

public class TableCallReminder {

    private DatabaseHandler databaseHandler;

    public TableCallReminder(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public static final String TABLE_CALL_REMINDER = "table_call_reminder";

    public static final String COLUMN_NUMBER =  "column_number";
    public static final String COLUMN_REMINDER_TIME =  "column_reminder_time";
    public static final String COLUMN_SNOOZE_TIME =  "column_snooze_time";

    public static String CREATE_TABLE_CALL_REMINDER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_CALL_REMINDER + " (" +
            " " + COLUMN_NUMBER + " text," +
            " " + COLUMN_REMINDER_TIME + " text ," +
            " " + COLUMN_SNOOZE_TIME + " text " +
            ");";


    public void addReminderToDB(String number, String time) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, number);
        values.put(COLUMN_REMINDER_TIME, time);
        // Inserting Row
        db.insert(TABLE_CALL_REMINDER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    public void updateReminderTime(String number, String time){
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();

            values.put(COLUMN_REMINDER_TIME, time);

            // Update Row
            db.update(TABLE_CALL_REMINDER, values,  COLUMN_NUMBER + " = \'" + number + "\'", null);
            db.close(); // Closing database connection
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Getting time from  number
    public String getReminderTimeFromNumber(String number) {
        String time = "";
        // Select All Query
        String selectQuery = "SELECT " +
                COLUMN_REMINDER_TIME + " FROM " +
                TABLE_CALL_REMINDER + " WHERE " +
                COLUMN_NUMBER + " = \'" + number + "\'";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            time =  cursor.getString(cursor.getColumnIndex
                    (COLUMN_REMINDER_TIME));

            cursor.close();
        }

        db.close();

        // return
        return time;
    }


    public ArrayList<String> getAllTimeforCallReminder() {
        ArrayList<String> arrayListTime = new ArrayList<>();
        String time = "";
        // Select All Query
        String selectQuery = "SELECT " +
                COLUMN_REMINDER_TIME + " FROM " +
                TABLE_CALL_REMINDER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                time =  cursor.getString(cursor.getColumnIndex
                        (COLUMN_REMINDER_TIME));
                arrayListTime.add(time);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return  list
        return arrayListTime;
    }

    // Getting time from  number
    public String getNumberFromTable(String number) {
        String numberPresent = "";
        // Select All Query
        String selectQuery = "SELECT " +
                COLUMN_NUMBER + " FROM " +
                TABLE_CALL_REMINDER + " WHERE " +
                COLUMN_NUMBER + " = \'" + number + "\'";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            numberPresent =  cursor.getString(cursor.getColumnIndex
                    (COLUMN_NUMBER));

            cursor.close();
        }

        db.close();

        // return
        return numberPresent;
    }

    // Getting time from  number
    public String getNumberFromTime(String time) {
        String numberPresent = "";
        // Select All Query
        String selectQuery = "SELECT " +
                COLUMN_NUMBER + " FROM " +
                TABLE_CALL_REMINDER + " WHERE " +
                COLUMN_REMINDER_TIME + " = \'" + time + "\'";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            numberPresent =  cursor.getString(cursor.getColumnIndex
                    (COLUMN_NUMBER));

            cursor.close();
        }

        db.close();

        // return
        return numberPresent;
    }



    // Deleting Details by Number
    public void deleteReminderDetails(String number) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_CALL_REMINDER, COLUMN_NUMBER + " = ?",
                new String[]{String.valueOf(number)});
        db.close();
    }

    // Deleting details by time
    public void deleteReminderDetailsbyTime(String time) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_CALL_REMINDER, COLUMN_REMINDER_TIME + " = ?",
                new String[]{String.valueOf(time)});
        db.close();
    }

}
