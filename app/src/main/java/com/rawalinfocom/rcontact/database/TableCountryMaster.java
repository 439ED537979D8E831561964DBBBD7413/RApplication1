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
    public static final String TABLE_RC_COUNTRY_MASTER = "rc_country_master";


    // Column Names
    private static final String COLUMN_CM_ID = "cm_id";
    private static final String COLUMN_CM_COUNTRY_CODE = "cm_country_code";
    private static final String COLUMN_CM_COUNTRY_CODE_NUMBER = "cm_country_code_number";
    private static final String COLUMN_CM_COUNTRY_NAME = "cm_country_name";
    private static final String COLUMN_CM_MAX_DIGITS = "cm_max_digits";
    static final String COLUMN_CM_MIN_DIGITS = "cm_min_digits";

    // Table Create Statements
    static final String CREATE_TABLE_RC_COUNTRY_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_COUNTRY_MASTER +
            "(" + COLUMN_CM_ID + " integer NOT NULL CONSTRAINT rc_country_master_pk PRIMARY KEY," +
            " " + COLUMN_CM_COUNTRY_CODE + " text NOT NULL," +
            " " + COLUMN_CM_COUNTRY_CODE_NUMBER + " text NOT NULL," +
            " " + COLUMN_CM_COUNTRY_NAME + " text NOT NULL," +
            " " + COLUMN_CM_MAX_DIGITS + " integer NOT NULL," +
            " " + COLUMN_CM_MIN_DIGITS + " integer NOT NULL" +
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
        values.put(COLUMN_CM_MIN_DIGITS, country.getCountryNumberMinDigits());

        // Inserting Row
        db.insert(TABLE_RC_COUNTRY_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding Array new country
    public void addArrayCountry(ArrayList<Country> arrayListCountry) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        for (int i = 0; i < arrayListCountry.size(); i++) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_CM_ID, arrayListCountry.get(i).getCountryId());
            values.put(COLUMN_CM_COUNTRY_CODE, arrayListCountry.get(i).getCountryCode());
            values.put(COLUMN_CM_COUNTRY_CODE_NUMBER, arrayListCountry.get(i)
                    .getCountryCodeNumber());
            values.put(COLUMN_CM_COUNTRY_NAME, arrayListCountry.get(i).getCountryName());
            values.put(COLUMN_CM_MAX_DIGITS, arrayListCountry.get(i).getCountryNumberMaxDigits());
            values.put(COLUMN_CM_MIN_DIGITS, arrayListCountry.get(i).getCountryNumberMinDigits());

            int count = 0;
            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RC_COUNTRY_MASTER + " " +
                    "WHERE " + COLUMN_CM_ID + " = " + arrayListCountry.get(i).getCountryId(), null);
            if (mCount != null) {
                mCount.moveToFirst();
                count = mCount.getInt(0);
                mCount.close();
            }

            if (count > 0) {
                // Update if already exists
                db.update(TABLE_RC_COUNTRY_MASTER, values, COLUMN_CM_ID + " = ?",
                        new String[]{String.valueOf(arrayListCountry.get(i).getCountryId())});
            } else {
                // Inserting Row
                db.insert(TABLE_RC_COUNTRY_MASTER, null, values);
            }

        }
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Country
    public Country getCountry(int countryId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_COUNTRY_MASTER, new String[]{COLUMN_CM_ID,
                COLUMN_CM_COUNTRY_CODE, COLUMN_CM_COUNTRY_CODE_NUMBER, COLUMN_CM_COUNTRY_NAME,
                COLUMN_CM_MAX_DIGITS, COLUMN_CM_MIN_DIGITS}, COLUMN_CM_ID + "=?", new
                String[]{String.valueOf(countryId)}, null, null, null, null);
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

    // Getting single Country
    public Country getCountryIdFromName(String countryName) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_COUNTRY_MASTER, new String[]{COLUMN_CM_ID,
                COLUMN_CM_COUNTRY_CODE, COLUMN_CM_COUNTRY_CODE_NUMBER, COLUMN_CM_COUNTRY_NAME,
                COLUMN_CM_MAX_DIGITS, COLUMN_CM_MIN_DIGITS}, COLUMN_CM_COUNTRY_NAME + "=?", new
                String[]{String.valueOf(countryName)}, null, null, null, null);
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
                country.setCountryId(cursor.getString(cursor.getColumnIndex(COLUMN_CM_ID)));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CM_COUNTRY_CODE)));
                country.setCountryCodeNumber(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CM_COUNTRY_CODE_NUMBER)));
                country.setCountryName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CM_COUNTRY_NAME)));
                country.setCountryNumberMaxDigits(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CM_MAX_DIGITS)));
                country.setCountryNumberMinDigits(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CM_MIN_DIGITS)));
                // Adding contact to list
                arrayListCountry.add(country);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return country list
        return arrayListCountry;
    }

    // Getting All Countries
    public ArrayList<String> getAllCountryName() {
        ArrayList<String> arrayListCountryName = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + COLUMN_CM_COUNTRY_NAME + " FROM " +
                TABLE_RC_COUNTRY_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding country name to list
                arrayListCountryName.add(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CM_COUNTRY_NAME)));
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return country list
        return arrayListCountryName;
    }

    // Getting Country Count
    public int getCountryCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_RC_COUNTRY_MASTER;
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
