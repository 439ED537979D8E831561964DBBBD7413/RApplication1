package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rawalinfocom.rcontact.model.NotificationData;
import com.rawalinfocom.rcontact.model.RcontactUpdatesData;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.NotiRContactsItem;

import java.util.ArrayList;

/**
 * Created by maulik on 28/03/17.
 */

public class TableRCNotificationUpdates {

    private DatabaseHandler databaseHandler;

    public TableRCNotificationUpdates(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_NOTIFICATION_UPDATES = "rc_notification_updates";

    // Column Names
    private static final String COLUMN_NU_ID = "nu_id";
    private static final String COLUMN_NU_CLOUD_ID = "nu_cloud_rupdate_id";
    private static final String COLUMN_NU_TITLE = "nu_title";
    private static final String COLUMN_NU_DETAILS = "nu_details";
    private static final String COLUMN_CREATED_AT = "created_at";

    // Table Create Statements
    static final String CREATE_TABLE_RC_NOTIFICATION_UPDATES = "CREATE TABLE " + TABLE_RC_NOTIFICATION_UPDATES +
            " (" +
            " " + COLUMN_NU_ID + " integer NOT NULL CONSTRAINT rc_notification_updates_pk PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_NU_TITLE + " text," +
            " " + COLUMN_NU_DETAILS + " text," +
            " " + COLUMN_NU_CLOUD_ID + " text," +
            " " + COLUMN_CREATED_AT + " datetime" +
            ");";

    public int addUpdate(NotificationData rconUpdate) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NU_TITLE, rconUpdate.getTitle());
        values.put(COLUMN_NU_DETAILS, rconUpdate.getDetails());
        values.put(COLUMN_NU_CLOUD_ID, rconUpdate.getId());
        values.put(COLUMN_CREATED_AT, Utils.getLocalTimeFromUTCTime(rconUpdate.getCreatedAt()));
        try {
            int id = (int) db.insert(TABLE_RC_NOTIFICATION_UPDATES, null, values);

            db.close();
            return id;
        } catch (Exception E) {
            Log.i("MAULIK", "error duplicate entry");
            return -1;
        }
    }

//    public void deleteAllPreviousUpdates() {
//        SQLiteDatabase db = databaseHandler.getWritableDatabase();
//        db.execSQL("delete from " + TABLE_RC_NOTIFICATION_UPDATES);
//    }

    public ArrayList<NotiRContactsItem> getAllUpdatesFromDB() {
        ArrayList<NotiRContactsItem> arrayListEvent = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_RC_NOTIFICATION_UPDATES + " order by " +
                COLUMN_CREATED_AT + " desc";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NotiRContactsItem item = new NotiRContactsItem();
                item.setNotiId(cursor.getString(cursor.getColumnIndex(COLUMN_NU_CLOUD_ID)));
                item.setNotiTitle(cursor.getString(cursor.getColumnIndex
                        (COLUMN_NU_TITLE)));
                item.setNotiDetails(cursor.getString(cursor.getColumnIndex
                        (COLUMN_NU_DETAILS)));
                item.setNotiTime(cursor.getString(cursor.getColumnIndex
                        (COLUMN_CREATED_AT)));
                arrayListEvent.add(item);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        return arrayListEvent;
    }
}
