package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Country;

import java.util.ArrayList;

/**
 * Created by Monal on 22/10/16.
 * <p>
 * Table operations rc_country_master
 */

public class TableCountryMaster {

    private DatabaseHandler databaseHandler;

    public TableCountryMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_COUNTRY_MASTER = "rc_country_master";


    // Column Names
    private static final String COLUMN_CM_ID = "cm_id";
    private static final String COLUMN_CM_COUNTRY_CODE = "cm_country_code";
    private static final String COLUMN_CM_COUNTRY_CODE_NUMBER = "cm_country_code_number";
    private static final String COLUMN_CM_COUNTRY_NAME = "cm_country_name";
    private static final String COLUMN_CM_MAX_DIGITS = "cm_max_digits";

    // Table Create Statements
    static final String CREATE_TABLE_RC_COUNTRY_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_COUNTRY_MASTER +
            "(" + COLUMN_CM_ID + " integer NOT NULL CONSTRAINT rc_country_master_pk PRIMARY KEY," +
            " " + COLUMN_CM_COUNTRY_CODE + " text NOT NULL," +
            " " + COLUMN_CM_COUNTRY_CODE_NUMBER + " text NOT NULL," +
            " " + COLUMN_CM_COUNTRY_NAME + " text NOT NULL," +
            " " + COLUMN_CM_MAX_DIGITS + " integer NOT NULL" +
            ");";

    // Adding new Country
    public void addCountry(Country country) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CM_ID, country.getCountryId());
        values.put(COLUMN_CM_COUNTRY_CODE, country.getCountryCode());
        values.put(COLUMN_CM_COUNTRY_CODE_NUMBER, country.getCountryCodeNumber());
        values.put(COLUMN_CM_COUNTRY_NAME, country.getCountryName());
        values.put(COLUMN_CM_MAX_DIGITS, country.getCountryNumberMaxDigits());

        // Inserting Row
        db.insert(TABLE_RC_COUNTRY_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Country
    public Country getCountry(int countryId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_COUNTRY_MASTER, new String[]{COLUMN_CM_ID,
                COLUMN_CM_COUNTRY_CODE, COLUMN_CM_COUNTRY_CODE_NUMBER, COLUMN_CM_COUNTRY_NAME,
                COLUMN_CM_MAX_DIGITS}, COLUMN_CM_ID + "=?", new String[]{String.valueOf
                (countryId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Country country = new Country();
        if (cursor != null) {
            country.setCountryId(cursor.getString(0));
            country.setCountryCode(cursor.getString(1));
            country.setCountryCodeNumber(cursor.getString(2));
            country.setCountryName(cursor.getString(3));
            country.setCountryNumberMaxDigits(cursor.getString(4));

            cursor.close();
        }

        db.close();

        // return Country
        return country;
    }

    // Getting All Countries
    public ArrayList<Country> getAllCountries() {
        ArrayList<Country> arrayListCountry = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_COUNTRY_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Country country = new Country();
                country.setCountryId(cursor.getString(0));
                country.setCountryCode(cursor.getString(1));
                country.setCountryCodeNumber(cursor.getString(2));
                country.setCountryName(cursor.getString(3));
                country.setCountryNumberMaxDigits(cursor.getString(4));
                // Adding contact to list
                arrayListCountry.add(country);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return country list
        return arrayListCountry;
    }

    // Getting Country Count
    public int getCountryCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_COUNTRY_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        // return count
        return count;
    }

    // Updating single contact
    public int updateCountry(Country country) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CM_ID, country.getCountryId());
        values.put(COLUMN_CM_COUNTRY_CODE, country.getCountryCode());
        values.put(COLUMN_CM_COUNTRY_CODE_NUMBER, country.getCountryCodeNumber());
        values.put(COLUMN_CM_COUNTRY_NAME, country.getCountryName());
        values.put(COLUMN_CM_MAX_DIGITS, country.getCountryNumberMaxDigits());

        // updating row
        int isUpdated = db.update(TABLE_RC_COUNTRY_MASTER, values, COLUMN_CM_ID + " = ?",
                new String[]{String.valueOf(country.getCountryId())});

        db.close();

        return isUpdated;
    }

    // Deleting single contact
    public void deleteContact(Country country) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_COUNTRY_MASTER, COLUMN_CM_ID + " = ?",
                new String[]{String.valueOf(country.getCountryId())});
        db.close();
    }
}
