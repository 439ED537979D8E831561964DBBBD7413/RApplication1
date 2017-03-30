package com.rawalinfocom.rcontact.database;

/**
 * Created by maulik on 28/03/17.
 */

public class TableRCNotificationUpdates {

    private DatabaseHandler databaseHandler;

    public TableRCNotificationUpdates(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_NOTIFICATION_UPDATES = "rc_notification_updates";

    // Column Names
    private static final String COLUMN_NU_ID = "nu_id";
    private static final String COLUMN_NU_CLOUD_ID = "nu_cloud_rupdate_id";
    static final String COLUMN_NU_TITLE = "nu_title";
    static final String COLUMN_NU_DETAILS = "nu_details";
    static final String COLUMN_CREATED_AT = "created_at";

    // Table Create Statements
    static final String CREATE_TABLE_RC_NOTIFICATION_UPDATES = "CREATE TABLE " + TABLE_RC_NOTIFICATION_UPDATES +
            " (" +
            " " + COLUMN_NU_ID + " integer NOT NULL CONSTRAINT rc_notification_updates_pk PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_NU_TITLE + " text," +
            " " + COLUMN_NU_DETAILS + " text," +
            " " + COLUMN_NU_CLOUD_ID + " text," +
            " " + COLUMN_CREATED_AT + " datetime" +
            ");";

}
