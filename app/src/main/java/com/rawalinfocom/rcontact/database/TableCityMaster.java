package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.City;

import java.util.ArrayList;

/**
 * Created by Monal on 22/10/16.
 * <p>
 * Table operations rc_country_master
 */

public class TableCityMaster {

    private DatabaseHandler databaseHandler;

    public TableCityMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    public static final String TABLE_RC_CITY_MASTER = "rc_city_master";


    // Column Names
    private static final String COLUMN_CM_ID = "cm_id";
    private static final String COLUMN_CM_CITY_NAME = "cm_name";
    private static final String COLUMN_CM_STATE_ID = "cm_state_id";

    // Table Create Statements
    static final String CREATE_TABLE_RC_STATE_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_CITY_MASTER +
            "(" + COLUMN_CM_ID + " integer NOT NULL CONSTRAINT rc_city_master_pk PRIMARY KEY," +
            " " + COLUMN_CM_CITY_NAME + " text NOT NULL," +
            " " + COLUMN_CM_STATE_ID + " integer NOT NULL" +
            ");";

    // Adding new City
    public void addCity(City city) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CM_ID, city.getCityId());
        values.put(COLUMN_CM_CITY_NAME, city.getCityName());
        values.put(COLUMN_CM_STATE_ID, city.getStateId());

        // Inserting Row
        db.insert(TABLE_RC_CITY_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding Array new city
    public void addArrayCity(ArrayList<City> arrayListCity) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        for (int i = 0; i < arrayListCity.size(); i++) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_CM_ID, arrayListCity.get(i).getCityId());
            values.put(COLUMN_CM_CITY_NAME, arrayListCity.get(i).getCityName());
            values.put(COLUMN_CM_STATE_ID, arrayListCity.get(i).getStateId());

            int count = 0;
            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RC_CITY_MASTER + " " +
                    "WHERE " + COLUMN_CM_ID + " = " + arrayListCity.get(i).getStateId(), null);
            if (mCount != null) {
                mCount.moveToFirst();
                count = mCount.getInt(0);
                mCount.close();
            }

            if (count > 0) {
                // Update if already exists
                db.update(TABLE_RC_CITY_MASTER, values, COLUMN_CM_ID + " = ?",
                        new String[]{String.valueOf(arrayListCity.get(i).getStateId())});
            } else {
                // Inserting Row
                db.insert(TABLE_RC_CITY_MASTER, null, values);
            }

        }
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single city
    public City getCity(int cityId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_CITY_MASTER, new String[]{COLUMN_CM_ID,
                COLUMN_CM_CITY_NAME, COLUMN_CM_STATE_ID}, COLUMN_CM_ID + "=?", new
                String[]{String.valueOf(cityId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        City city = new City();
        if (cursor != null) {
            city.setCityId(cursor.getString(cursor.getColumnIndex(COLUMN_CM_ID)));
            city.setCityName(cursor.getString(cursor.getColumnIndex(COLUMN_CM_CITY_NAME)));
            city.setStateId(cursor.getString(cursor.getColumnIndex(COLUMN_CM_STATE_ID)));

            cursor.close();
        }

        db.close();

        // return city
        return city;
    }

    // Getting All city
    public ArrayList<City> getCityFromState(String stateId) {
        ArrayList<City> arrayListCity = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_CITY_MASTER + " WHERE " +
                COLUMN_CM_STATE_ID + " = " + stateId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setCityId(cursor.getString(cursor.getColumnIndex(COLUMN_CM_ID)));
                city.setCityName(cursor.getString(cursor.getColumnIndex(COLUMN_CM_CITY_NAME)));
                city.setStateId(cursor.getString(cursor.getColumnIndex(COLUMN_CM_STATE_ID)));
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return city list
        return arrayListCity;
    }

    // Getting All Citys
    public ArrayList<String> getAllCityName() {
        ArrayList<String> arrayListCityName = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + COLUMN_CM_CITY_NAME + " FROM " +
                TABLE_RC_CITY_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding city name to list
                arrayListCityName.add(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CM_CITY_NAME)));
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return country list
        return arrayListCityName;
    }

    // Getting city Count
    public int getCityCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_RC_CITY_MASTER;
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

    // Updating single city
    public int updateCity(City city) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CM_ID, city.getCityId());
        values.put(COLUMN_CM_CITY_NAME, city.getCityName());
        values.put(COLUMN_CM_STATE_ID, city.getStateId());

        // updating row
        int isUpdated = db.update(TABLE_RC_CITY_MASTER, values, COLUMN_CM_ID + " = ?",
                new String[]{String.valueOf(city.getCityId())});

        db.close();

        return isUpdated;
    }

    // Deleting single city
    public void deleteCity(City city) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_CITY_MASTER, COLUMN_CM_ID + " = ?",
                new String[]{String.valueOf(city.getCityId())});
        db.close();
    }
}
