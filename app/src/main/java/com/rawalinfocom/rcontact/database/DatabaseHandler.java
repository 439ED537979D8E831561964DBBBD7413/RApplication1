package com.rawalinfocom.rcontact.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Monal on 21/10/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private final String LOG_TAG = "DatabaseHelper";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    public static final String DATABASE_NAME = "RContact.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//        System.out.println("RContact db create --> ");

        // creating required tables
        db.execSQL(TableCountryMaster.CREATE_TABLE_RC_COUNTRY_MASTER);
        db.execSQL(TableOtpLogDetails.CREATE_TABLE_OTP_LOG_DETAILS);
        db.execSQL(TableProfileMaster.CREATE_TABLE_RC_PROFILE_MASTER);
        db.execSQL(TableEmailMaster.CREATE_TABLE_RC_EMAIL_MASTER);
        db.execSQL(TableMobileMaster.CREATE_TABLE_RC_MOBILE_NUMBER_MASTER);
        db.execSQL(TableProfileMobileMapping.CREATE_TABLE_PB_PROFILE_MOBILE_MAPPING);
        db.execSQL(TableProfileEmailMapping.CREATE_TABLE_PB_PROFILE_EMAIL_MAPPING);
        db.execSQL(TableAddressMaster.CREATE_TABLE_RC_ADDRESS_MASTER);
        db.execSQL(TableEventMaster.CREATE_TABLE_RC_EVENT_MASTER);
        db.execSQL(TableFlagMaster.CREATE_TABLE_RC_FLAG_MASTER);
        db.execSQL(TableImMaster.CREATE_TABLE_RC_IM_MASTER);
        db.execSQL(TableOfflineBackupMaster.CREATE_TABLE_PB_OFFLINE_BACKUP_MASTER);
        db.execSQL(TableOrganizationMaster.CREATE_TABLE_RC_ORGANIZATION_MASTER);
        db.execSQL(TableRelationMaster.CREATE_TABLE_RC_RELATION_MASTER);
        db.execSQL(TableWebsiteMaster.CREATE_TABLE_RC_WEBSITE_MASTER);
        /*db.execSQL(TableContactRatingMaster.CREATE_TABLE_RC_CONTACT_RATING_MASTER);*/
        db.execSQL(TableCommentMaster.CREATE_TABLE_RC_COMMENT_MASTER);
        db.execSQL(TableRCNotificationUpdates.CREATE_TABLE_RC_NOTIFICATION_UPDATES);
        db.execSQL(TableRCContactRequest.CREATE_TABLE_RC_CONTACT_REQUEST);
        db.execSQL(TableNotificationStateMaster.CREATE_TABLE_NOTIFICATION_STATE_MASTER);
        db.execSQL(TableSpamDetailMaster.CREATE_TABLE_SPAM_DETAIL_MASTER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        /*if(newVersion > oldVersion){

            db.execSQL("ALTER TABLE " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST +
                    " ADD COLUMN " +  TableRCContactRequest.COLUMN_CAR_IMG  + " text ") ;

            db.execSQL("ALTER TABLE " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST +
                    " ADD COLUMN " +  TableRCContactRequest.COLUMN_CAR_PROFILE_DETAILS  + " text
                    ") ;

            // create new tables
            onCreate(db);
        }*/

        switch (oldVersion) {
            case 1:
                // For version 2
                db.execSQL("ALTER TABLE " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST +
                        " ADD COLUMN " + TableRCContactRequest.COLUMN_CAR_IMG + " text ");

                db.execSQL("ALTER TABLE " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST
                        + " ADD COLUMN " + TableRCContactRequest.COLUMN_CAR_PROFILE_DETAILS + " " +
                        "text ");
            case 2:
                // For version 3
                db.execSQL("ALTER TABLE " + TableProfileMaster.TABLE_RC_PROFILE_MASTER + " ADD "
                        + "COLUMN " + TableProfileMaster.COLUMN_PM_BADGE + " text ");
        }

        // create new tables
        onCreate(db);

    }
}
