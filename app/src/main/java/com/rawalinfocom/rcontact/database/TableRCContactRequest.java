package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.model.PrivacyRequestDataItem;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by maulik on 07/04/17.
 */

public class TableRCContactRequest {

    private DatabaseHandler databaseHandler;

    public TableRCContactRequest(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // PRIVACY REQUEST STATUS 0=REQUEST;1=CONFIRM;2=REJECT

    // PRIVACY 1=PUBLIC,2=MYCONTACT,3=PRIVATE

    // Table Names
    public static final String TABLE_RC_CONTACT_ACCESS_REQUEST = "rc_contact_access_request";

    // Column Names
    private static final String COLUMN_CAR_ID = "car_id";
    private static final String COLUMN_CAR_STATUS = "car_status"; //1. Sent, 2. Received
    private static final String COLUMN_CARTYPE = "crm_type"; // one of the entity name
    private static final String COLUMN_CAR_RECORD_INDEX_ID = "car_record_index_id";
    private static final String COLUMN_CAR_CLOUD_REQUEST_ID = "car_cloud_request_id";
    private static final String COLUMN_CAR_CREATED_AT = "car_created_at";
    private static final String COLUMN_CAR_UPDATED_AT = "car_updated_at";
    static final String COLUMN_CRM_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";
    public static final String COLUMN_CAR_IMG = "crm_img";
    public static final String COLUMN_CAR_PROFILE_DETAILS = "crm_profiledetails";


    // Table Create Statements
    static final String CREATE_TABLE_RC_CONTACT_REQUEST = "CREATE TABLE IF NOT EXISTS " + TABLE_RC_CONTACT_ACCESS_REQUEST +
            " (" +
            " " + COLUMN_CAR_ID + " integer NOT NULL CONSTRAINT rc_contact_access_request_pk PRIMARY KEY " +
            "AUTOINCREMENT," +
            " " + COLUMN_CAR_STATUS + " integer," +
            " " + COLUMN_CARTYPE + " text," +
            " " + COLUMN_CAR_RECORD_INDEX_ID + " text NOT NULL," +
            " " + COLUMN_CRM_RC_PROFILE_MASTER_PM_ID + " integer NOT NULL," +
            " " + COLUMN_CAR_CLOUD_REQUEST_ID + " text NOT NULL," +
            " " + COLUMN_CAR_CREATED_AT + " datetime NOT NULL," +
            " " + COLUMN_CAR_IMG + " text," +
            " " + COLUMN_CAR_PROFILE_DETAILS + " text," +
            " " + COLUMN_CAR_UPDATED_AT + " datetime NOT NULL," +
            " UNIQUE(" + COLUMN_CAR_CLOUD_REQUEST_ID + ")" +
            ");";

    public int addRequest(int status, String carId, String carMongodbRecordIndex, int carPmIdFrom, String requestType, String createdAt, String updatedAt
            , String name, String profilePhoto) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        boolean isDelete;

        if (requestType.equalsIgnoreCase("request all")) {
            isDelete = deleteRequestAllRequest(String.valueOf(carPmIdFrom), requestType);
            System.out.println("RContacts delete request all --> " + isDelete);
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAR_STATUS, status);
        if (requestType.equalsIgnoreCase("request all")) {
            values.put(COLUMN_CAR_CLOUD_REQUEST_ID, System.currentTimeMillis() + "" + new Random().nextInt(1234));
            values.put(COLUMN_CAR_RECORD_INDEX_ID, 0);
        } else {
            values.put(COLUMN_CAR_CLOUD_REQUEST_ID, carId);
            values.put(COLUMN_CAR_RECORD_INDEX_ID, carMongodbRecordIndex);
        }

        values.put(COLUMN_CAR_CREATED_AT, createdAt);
        values.put(COLUMN_CAR_UPDATED_AT, updatedAt);
        values.put(COLUMN_CRM_RC_PROFILE_MASTER_PM_ID, carPmIdFrom);
        values.put(COLUMN_CARTYPE, requestType);
        values.put(COLUMN_CAR_PROFILE_DETAILS, name);
        values.put(COLUMN_CAR_IMG, profilePhoto);
        try {
            int id = (int) db.insert(TABLE_RC_CONTACT_ACCESS_REQUEST, null, values);
            db.close();
            return id;
        } catch (Exception e) {
            db.close();
            return -1;
        }
    }

    private boolean deleteRequestAllRequest(String rcpID, String ppmTag) {

        boolean isDelete;
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        isDelete = db.delete(TABLE_RC_CONTACT_ACCESS_REQUEST, COLUMN_CARTYPE + " = '" + ppmTag + "' and " +
                COLUMN_CRM_RC_PROFILE_MASTER_PM_ID + " = '" + rcpID + "'", null) > 0;

        return isDelete;
    }

    public ArrayList<PrivacyRequestDataItem> getAllPendingRequest(String from, String to) {
        ArrayList<PrivacyRequestDataItem> arrayList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RC_CONTACT_ACCESS_REQUEST +
                " WHERE " + COLUMN_CAR_STATUS + "=" + AppConstants.COMMENT_STATUS_RECEIVED + " and strftime('%m-%d'," +
                COLUMN_CAR_UPDATED_AT + ") between '" + from + "' and '" + to + "' order by " + COLUMN_CAR_UPDATED_AT + " desc";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PrivacyRequestDataItem request = new PrivacyRequestDataItem();
                request.setCarPmIdFrom(cursor.getInt(cursor.getColumnIndex(COLUMN_CRM_RC_PROFILE_MASTER_PM_ID)));
                request.setPpmTag(cursor.getString(cursor.getColumnIndex(COLUMN_CARTYPE)));
                request.setCarMongodbRecordIndex(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_RECORD_INDEX_ID)));
                request.setUpdatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_UPDATED_AT)));
                System.out.println("RContacts id --> " + cursor.getString(cursor.getColumnIndex(COLUMN_CAR_CLOUD_REQUEST_ID)));
                request.setCarId((int) Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_CLOUD_REQUEST_ID))));
                request.setCarRequestId(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_CLOUD_REQUEST_ID)));
                request.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_PROFILE_DETAILS)));
                request.setPmProfilePhoto(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_IMG)));
                arrayList.add(request);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return arrayList;
    }

    public ArrayList<PrivacyRequestDataItem> getAllResponseReceived(String from, String to) {
        ArrayList<PrivacyRequestDataItem> arrayList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RC_CONTACT_ACCESS_REQUEST +
                " WHERE " + COLUMN_CAR_STATUS + "=" + AppConstants.COMMENT_STATUS_SENT + " and strftime('%m-%d'," +
                COLUMN_CAR_UPDATED_AT + ") between '" + from + "' and '" + to + "' order by " + COLUMN_CAR_UPDATED_AT + " desc";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PrivacyRequestDataItem request = new PrivacyRequestDataItem();
                request.setCarPmIdFrom(cursor.getInt(cursor.getColumnIndex(COLUMN_CRM_RC_PROFILE_MASTER_PM_ID)));
                request.setPpmTag(cursor.getString(cursor.getColumnIndex(COLUMN_CARTYPE)));
                request.setCarMongodbRecordIndex(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_RECORD_INDEX_ID)));
                request.setUpdatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_UPDATED_AT)));
                request.setCarId(cursor.getInt(cursor.getColumnIndex(COLUMN_CAR_CLOUD_REQUEST_ID)));
                request.setCarRequestId(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_CLOUD_REQUEST_ID)));
                request.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_PROFILE_DETAILS)));
                request.setPmProfilePhoto(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_IMG)));
                arrayList.add(request);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return arrayList;
    }

    public boolean deleteAll() {

        boolean isDelete;
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        isDelete = db.delete(TABLE_RC_CONTACT_ACCESS_REQUEST, null, null) > 0;

        return isDelete;
    }

    public boolean removeRequest(String ppmTag, String carId, int rcpID, String actionType) {
        boolean isDelete;
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        if (ppmTag.equalsIgnoreCase("request all")) {
//            String[] whereArgs = new String[]{String.valueOf(carId), String.valueOf(rcpID), ppmTag};
            isDelete = db.delete(TABLE_RC_CONTACT_ACCESS_REQUEST, COLUMN_CAR_CLOUD_REQUEST_ID + " = '" + carId + "' and " +
                    COLUMN_CARTYPE + " = '" + ppmTag + "' and " + COLUMN_CRM_RC_PROFILE_MASTER_PM_ID + " = '" + rcpID + "'", null) > 0;
            if (actionType.equalsIgnoreCase("accept"))
                db.delete(TABLE_RC_CONTACT_ACCESS_REQUEST, COLUMN_CRM_RC_PROFILE_MASTER_PM_ID + " = '" + rcpID + "'", null);
        } else {
            isDelete = db.delete(TABLE_RC_CONTACT_ACCESS_REQUEST, COLUMN_CAR_CLOUD_REQUEST_ID + "=" + carId, null) > 0;
        }
        return isDelete;
    }
}
