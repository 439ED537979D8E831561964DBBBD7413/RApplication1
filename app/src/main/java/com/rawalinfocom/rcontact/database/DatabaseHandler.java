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
    private static final int DATABASE_VERSION = 5;  //update to 4 for production/staging // For QA 5

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
        db.execSQL(TableEmailMaster.CREATE_TABLE_RC_EMAIL_MASTER_1);
//        db.execSQL(TableEmailMaster.CREATE_TABLE_RC_EMAIL_MASTER);
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

        db.execSQL("ALTER TABLE " + TableEmailMaster.TABLE_RC_EMAIL_MASTER_TEMP
                + " RENAME TO " + TableEmailMaster.TABLE_RC_EMAIL_MASTER + ";");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("RContact db upgrade --> ");

        // on upgrade drop older tables
        switch (oldVersion) {
            case 1:
                System.out.println("RContact db upgrade case 1 --> ");
                // For version 2
                db.execSQL("ALTER TABLE " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST +
                        " ADD COLUMN " + TableRCContactRequest.COLUMN_CAR_IMG + " text ");

                db.execSQL("ALTER TABLE " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST
                        + " ADD COLUMN " + TableRCContactRequest.COLUMN_CAR_PROFILE_DETAILS + " " +
                        "text ");
            case 2:
                System.out.println("RContact db upgrade case 2 --> ");
                // For version 3
                db.execSQL("ALTER TABLE " + TableProfileMaster.TABLE_RC_PROFILE_MASTER + " ADD "
                        + "COLUMN " + TableProfileMaster.COLUMN_PM_BADGE + " text ");
            case 3:
                System.out.println("RContact db upgrade case 3 r-contact --> ");
                // For version 4
                db.execSQL("ALTER TABLE " + TableRCNotificationUpdates.TABLE_RC_NOTIFICATION_UPDATES + " ADD "
                        + "COLUMN " + TableRCNotificationUpdates.COLUMN_NU_TYPE + " text ");
                db.execSQL("ALTER TABLE " + TableRCNotificationUpdates.TABLE_RC_NOTIFICATION_UPDATES + " ADD "
                        + "COLUMN " + TableRCNotificationUpdates.COLUMN_NU_URL + " text ");

                System.out.println("RContact db upgrade case 3 organization --> ");
                // For Organization
                db.execSQL("ALTER TABLE " + TableOrganizationMaster.TABLE_RC_ORGANIZATION_MASTER + " ADD "
                        + "COLUMN " + TableOrganizationMaster.COLUMN_OM_ORGANIZATION_FROM_DATE + " text ");
                db.execSQL("ALTER TABLE " + TableOrganizationMaster.TABLE_RC_ORGANIZATION_MASTER + " ADD "
                        + "COLUMN " + TableOrganizationMaster.COLUMN_OM_ORGANIZATION_TO_DATE + " text ");

                System.out.println("RContact db upgrade case 3 im account --> ");
                // For IM Account
                db.execSQL("ALTER TABLE " + TableImMaster.TABLE_RC_IM_MASTER + " ADD "
                        + "COLUMN " + TableImMaster.COLUMN_IM_FIRST_NAME + " text ");
                db.execSQL("ALTER TABLE " + TableImMaster.TABLE_RC_IM_MASTER + " ADD "
                        + "COLUMN " + TableImMaster.COLUMN_IM_LAST_NAME + " text ");
                db.execSQL("ALTER TABLE " + TableImMaster.TABLE_RC_IM_MASTER + " ADD "
                        + "COLUMN " + TableImMaster.COLUMN_IM_PROFILE_IMAGE + " text ");

                /*System.out.println("RContact db upgrade case 3 email --> ");
                // For Email
                db.execSQL(TableEmailMaster.CREATE_TABLE_RC_EMAIL_MASTER_1);
                db.execSQL("INSERT INTO " + TableEmailMaster.TABLE_RC_EMAIL_MASTER_TEMP + "(em_id,em_email_address," +
                        "em_email_type,em_record_index_id,em_email_privacy,em_is_private,em_is_verified,rc_profile_master_pm_id)" +
                        " SELECT em_id,em_email_address,em_email_type,em_record_index_id,em_email_privacy,em_is_private," +
                        "em_is_verified,rc_profile_master_pm_id FROM " + TableEmailMaster.TABLE_RC_EMAIL_MASTER + ";");
                db.execSQL("DROP TABLE IF EXISTS '" + TableEmailMaster.TABLE_RC_EMAIL_MASTER + "'");
                db.execSQL("ALTER TABLE " + TableEmailMaster.TABLE_RC_EMAIL_MASTER_TEMP
                        + " RENAME TO " + TableEmailMaster.TABLE_RC_EMAIL_MASTER + ";");*/

            case 4:
                System.out.println("RContact db upgrade case 4 email --> ");
                // For version 5
                db.execSQL(TableEmailMaster.CREATE_TABLE_RC_EMAIL_MASTER_1);
                db.execSQL("INSERT INTO " + TableEmailMaster.TABLE_RC_EMAIL_MASTER_TEMP + "(em_id,em_email_address," +
                        "em_email_type,em_record_index_id,em_email_privacy,em_is_private,em_is_verified,rc_profile_master_pm_id)" +
                        " SELECT em_id,em_email_address,em_email_type,em_record_index_id,em_email_privacy,em_is_private," +
                        "em_is_verified,rc_profile_master_pm_id FROM " + TableEmailMaster.TABLE_RC_EMAIL_MASTER + ";");
                db.execSQL("DROP TABLE IF EXISTS '" + TableEmailMaster.TABLE_RC_EMAIL_MASTER + "'");
                db.execSQL("ALTER TABLE " + TableEmailMaster.TABLE_RC_EMAIL_MASTER_TEMP
                        + " RENAME TO " + TableEmailMaster.TABLE_RC_EMAIL_MASTER + ";");
        }

        // create new tables
//        onCreate(db);
    }

    public void clearAllData() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.execSQL("delete from " + TableCountryMaster.TABLE_RC_COUNTRY_MASTER);
            db.execSQL("delete from " + TableProfileMaster.TABLE_RC_PROFILE_MASTER);
            db.execSQL("delete from " + TableEmailMaster.TABLE_RC_EMAIL_MASTER);
            db.execSQL("delete from " + TableMobileMaster.TABLE_RC_MOBILE_NUMBER_MASTER);
            db.execSQL("delete from " + TableProfileMobileMapping.TABLE_PB_PROFILE_MOBILE_MAPPING);
            db.execSQL("delete from " + TableProfileEmailMapping.TABLE_PB_PROFILE_EMAIL_MAPPING);
            db.execSQL("delete from " + TableAddressMaster.TABLE_RC_ADDRESS_MASTER);
            db.execSQL("delete from " + TableEventMaster.TABLE_RC_EVENT_MASTER);
            db.execSQL("delete from " + TableFlagMaster.TABLE_RC_FLAG_MASTER);
            db.execSQL("delete from " + TableImMaster.TABLE_RC_IM_MASTER);
            db.execSQL("delete from " + TableOfflineBackupMaster.TABLE_PB_OFFLINE_BACKUP_MASTER);
            db.execSQL("delete from " + TableOrganizationMaster.TABLE_RC_ORGANIZATION_MASTER);
            db.execSQL("delete from " + TableRelationMaster.TABLE_RC_RELATION_MASTER);
            db.execSQL("delete from " + TableWebsiteMaster.TABLE_RC_WEBSITE_MASTER);
            db.execSQL("delete from " + TableCommentMaster.TABLE_RC_COMMENT_MASTER);
            db.execSQL("delete from " + TableRCNotificationUpdates.TABLE_RC_NOTIFICATION_UPDATES);
            db.execSQL("delete from " + TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST);
            db.execSQL("delete from " + TableNotificationStateMaster.TABLE_NOTIFICATION_STATE_MASTER);
            db.execSQL("delete from " + TableSpamDetailMaster.TABLE_SPAM_DETAIL_MASTER);
        } catch (Exception e) {
            System.out.println("RContacts table clear error");
        }
    }
}
