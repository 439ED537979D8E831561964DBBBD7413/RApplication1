package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAadharNumber;

import java.util.ArrayList;

/**
 * Created by Aniruddh on 02/11/17.
 */

public class TableAadharMaster {

    private DatabaseHandler databaseHandler;

    public TableAadharMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public static final String TABLE_AADHAR_MASTER = "table_aadhar_master";

    public static final String COLUMN_AADHAR_ID = "aadhaar_id";
    public static final String COLUMN_AADHAR_NUMBER = "aadhaar_number";
    public static final String COLUMN_AADHAR_IS_VARIFIED = "is_verified";
    public static final String COLUMN_AADHAR_PUBLIC = "aadhaar_public";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    public static String CREATE_TABLE_AADHAR_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_AADHAR_MASTER + " (" +
            " " + COLUMN_AADHAR_ID + " integer," +
            " " + COLUMN_AADHAR_NUMBER + " text ," +
            " " + COLUMN_AADHAR_IS_VARIFIED + " integer," +
            " " + COLUMN_AADHAR_PUBLIC + " integer," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new Mobile Number
    public void addAadharDetail(ProfileDataOperationAadharNumber profileDataOperationAadharNumber) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_AADHAR_ID, profileDataOperationAadharNumber.getAadharId());
        values.put(COLUMN_AADHAR_NUMBER, profileDataOperationAadharNumber.getAadharNumber());
        values.put(COLUMN_AADHAR_IS_VARIFIED, profileDataOperationAadharNumber.getAadharIsVerified());
        values.put(COLUMN_AADHAR_PUBLIC, profileDataOperationAadharNumber.getAadharPublic());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, profileDataOperationAadharNumber.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_AADHAR_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting All Aadhar Details from Profile Master Id
    public ProfileDataOperationAadharNumber getAadharDetailFromPmId(int pmid) {
        ProfileDataOperationAadharNumber profileDataOperationAadharNumber = new ProfileDataOperationAadharNumber();
        // Select All Query
        String selectQuery = "SELECT " +
                COLUMN_AADHAR_ID + ", " +
                COLUMN_AADHAR_NUMBER + ", " +
                COLUMN_AADHAR_IS_VARIFIED + ", " +
                COLUMN_AADHAR_PUBLIC + " FROM " +
                TABLE_AADHAR_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmid;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            profileDataOperationAadharNumber.setAadharId(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_AADHAR_ID)));
            profileDataOperationAadharNumber.setAadharNumber(cursor.getString(cursor.getColumnIndex
                    (COLUMN_AADHAR_NUMBER)));
            profileDataOperationAadharNumber.setAadharIsVerified(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_AADHAR_IS_VARIFIED)));
            profileDataOperationAadharNumber.setAadharPublic(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_AADHAR_PUBLIC)));

            cursor.close();
        }

        db.close();

        // return Mobile Number list
        return profileDataOperationAadharNumber;
    }

    // Getting All Aadhar Public value from Aadhar number
    public int getAadharPublicValueFromAadharNumber(String aadharNumber) {
        ProfileDataOperationAadharNumber profileDataOperationAadharNumber = new ProfileDataOperationAadharNumber();
        int aadharPublicId = 0;
        // Select All Query
        String selectQuery = "SELECT " +
                COLUMN_AADHAR_PUBLIC + " FROM " +
                TABLE_AADHAR_MASTER + " WHERE " +
                COLUMN_AADHAR_NUMBER + " = " + aadharNumber;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            aadharPublicId =  cursor.getInt(cursor.getColumnIndex
                   (COLUMN_AADHAR_PUBLIC));

            cursor.close();
        }

        db.close();

        // return aadharPublicId
        return aadharPublicId;
    }

    // Deleting AadharDetails From RcpId
    public void deleteAadharDetails(String pmID) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_AADHAR_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?",
                new String[]{String.valueOf(pmID)});
        db.close();
    }
}
