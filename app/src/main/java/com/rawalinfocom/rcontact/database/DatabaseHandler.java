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
    private static final int DATABASE_VERSION = 2;

    // Database Name
    public static final String DATABASE_NAME = "RContact.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("RContact db create --> ");

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

        System.out.println("RContact db version --> " + oldVersion  +  " --" + newVersion);

        // on upgrade drop older tables
        if(newVersion > oldVersion){
            /*db.execSQL("DROP TABLE IF EXISTS " + TableCountryMaster.TABLE_RC_COUNTRY_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableOtpLogDetails.TABLE_RC_OTP_LOG_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + TableProfileMaster.TABLE_RC_PROFILE_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableEmailMaster.TABLE_RC_EMAIL_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableMobileMaster
                    .TABLE_RC_MOBILE_NUMBER_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableProfileMobileMapping
                    .TABLE_PB_PROFILE_MOBILE_MAPPING);
            db.execSQL("DROP TABLE IF EXISTS " + TableProfileEmailMapping
                    .TABLE_PB_PROFILE_EMAIL_MAPPING);
            db.execSQL("DROP TABLE IF EXISTS " + TableAddressMaster.TABLE_RC_ADDRESS_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableEventMaster.TABLE_RC_EVENT_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableFlagMaster.TABLE_RC_FLAG_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableImMaster.TABLE_RC_IM_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableOfflineBackupMaster
                    .TABLE_PB_OFFLINE_BACKUP_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableOrganizationMaster
                    .TABLE_RC_ORGANIZATION_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableRelationMaster.TABLE_RC_RELATION_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableContactRatingMaster
                .CREATE_TABLE_RC_CONTACT_RATING_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableCommentMaster.TABLE_RC_COMMENT_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableRCNotificationUpdates.TABLE_RC_NOTIFICATION_UPDATES);
            db.execSQL("DROP TABLE IF EXISTS " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST);
            db.execSQL("DROP TABLE IF EXISTS " + TableNotificationStateMaster.TABLE_NOTIFICATION_STATE_MASTER);
            db.execSQL("DROP TABLE IF EXISTS " + TableSpamDetailMaster.TABLE_SPAM_DETAIL_MASTER);*/

            db.execSQL("ALTER TABLE " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST +
                    " ADD COLUMN " +  TableRCContactRequest.COLUMN_CAR_IMG  + " text ") ;

            db.execSQL("ALTER TABLE " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST +
                    " ADD COLUMN " +  TableRCContactRequest.COLUMN_CAR_PROFILE_DETAILS  + " text ") ;

            System.out.println("RContact db drop --> ");

            // create new tables
            onCreate(db);
        }

    }
}
