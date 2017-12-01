package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by Monal on 29/11/16.
 * <p>
 * Table operations rc_organization_master
 */

public class TableOrganizationMaster {

    private DatabaseHandler databaseHandler;
    private int pmId;

    public TableOrganizationMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_ORGANIZATION_MASTER = "rc_organization_master";

    // Column Names
    private static final String COLUMN_OM_ID = "om_id";
    private static final String COLUMN_OM_RECORD_INDEX_ID = "om_record_index_id";
    static final String COLUMN_OM_ORGANIZATION_COMPANY = "om_organization_company";
    static final String COLUMN_OM_ORGANIZATION_ENT_ID = "om_organization_ent_id";
    static final String COLUMN_OM_ORGANIZATION_IMAGE = "om_organization_image";
    static final String COLUMN_OM_ORGANIZATION_DESIGNATION = "om_organization_designation";
    static final String COLUMN_OM_ORGANIZATION_TYPE = "column_om_organization_type";
    static final String COLUMN_OM_ORGANIZATION_FROM_DATE = "om_organization_from_date";
    static final String COLUMN_OM_ORGANIZATION_TO_DATE = "om_organization_to_date";
    private static final String COLUMN_OM_ORGANIZATION_PRIVACY = "om_organization_privacy";
    public static final String COLUMN_OM_ORGANIZATION_IS_VERIFIED = "om_organization_is_verified";
    private static final String COLUMN_OM_ORGANIZATION_IS_PRIVATE = "om_organization_is_private";
    static final String COLUMN_OM_ORGANIZATION_IS_CURRENT = "om_organization_is_current";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    static final String CREATE_TABLE_RC_ORGANIZATION_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_ORGANIZATION_MASTER + " (" +
            " " + COLUMN_OM_ID + " integer NOT NULL CONSTRAINT rc_organization_master_pk PRIMARY " +
            "" + "KEY," +
            " " + COLUMN_OM_RECORD_INDEX_ID + " text," +
            " " + COLUMN_OM_ORGANIZATION_COMPANY + " text NOT NULL," +
            " " + COLUMN_OM_ORGANIZATION_DESIGNATION + " text," +
            " " + COLUMN_OM_ORGANIZATION_TYPE + " text," +
            " " + COLUMN_OM_ORGANIZATION_ENT_ID + " text," +
            " " + COLUMN_OM_ORGANIZATION_IMAGE + " text," +
            " " + COLUMN_OM_ORGANIZATION_FROM_DATE + " text," +
            " " + COLUMN_OM_ORGANIZATION_TO_DATE + " text," +
            " " + COLUMN_OM_ORGANIZATION_IS_CURRENT + " integer," +
            " " + COLUMN_OM_ORGANIZATION_IS_VERIFIED + " integer," +
            " " + COLUMN_OM_ORGANIZATION_IS_PRIVATE + " integer," +
            " " + COLUMN_OM_ORGANIZATION_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer," +
            " UNIQUE(" + COLUMN_OM_RECORD_INDEX_ID + ", " + COLUMN_RC_PROFILE_MASTER_PM_ID + ")" +
            ");";

    // Adding new Org
    public void addOrganization(Organization organization) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OM_ID, organization.getOmId());
        values.put(COLUMN_OM_RECORD_INDEX_ID, organization.getOmRecordIndexId());
        values.put(COLUMN_OM_ORGANIZATION_COMPANY, organization.getOmOrganizationCompany());
        values.put(COLUMN_OM_ORGANIZATION_DESIGNATION, organization.getOmOrganizationDesignation());
        values.put(COLUMN_OM_ORGANIZATION_IS_CURRENT, organization.getOmIsCurrent());
        values.put(COLUMN_OM_ORGANIZATION_IS_PRIVATE, organization.getOmIsPrivate());
        values.put(COLUMN_OM_ORGANIZATION_PRIVACY, organization.getOmOrganizationPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, organization.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_ORGANIZATION_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding array Org
    public void addArrayOrganization(ArrayList<Organization> arrayListOrganization) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListOrganization.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_OM_ID, arrayListOrganization.get(i).getOmId());
            values.put(COLUMN_OM_RECORD_INDEX_ID, arrayListOrganization.get(i).getOmRecordIndexId
                    ());
            values.put(COLUMN_OM_ORGANIZATION_COMPANY, arrayListOrganization.get(i)
                    .getOmOrganizationCompany());
            values.put(COLUMN_OM_ORGANIZATION_DESIGNATION, arrayListOrganization.get(i)
                    .getOmOrganizationDesignation());
            values.put(COLUMN_OM_ORGANIZATION_TYPE, arrayListOrganization.get(i)
                    .getOmOrganizationType());
            values.put(COLUMN_OM_ORGANIZATION_ENT_ID, arrayListOrganization.get(i)
                    .getOmEnterpriseOrgId());
            values.put(COLUMN_OM_ORGANIZATION_IMAGE, arrayListOrganization.get(i)
                    .getOmOrganizationLogo());
            values.put(COLUMN_OM_ORGANIZATION_FROM_DATE, arrayListOrganization.get(i)
                    .getOmOrganizationFromDate());
            values.put(COLUMN_OM_ORGANIZATION_TO_DATE, arrayListOrganization.get(i)
                    .getOmOrganizationToDate());
            values.put(COLUMN_OM_ORGANIZATION_IS_CURRENT, arrayListOrganization.get(i)
                    .getOmIsCurrent());
            values.put(COLUMN_OM_ORGANIZATION_IS_PRIVATE, arrayListOrganization.get(i)
                    .getOmIsPrivate());
            values.put(COLUMN_OM_ORGANIZATION_IS_VERIFIED, arrayListOrganization.get(i)
                    .getOmIsVerified());
            values.put(COLUMN_OM_ORGANIZATION_PRIVACY, arrayListOrganization.get(i)
                    .getOmOrganizationPrivacy());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListOrganization.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_ORGANIZATION_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    public void deleteData(String RcpPmId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        int count = db.delete(TABLE_RC_ORGANIZATION_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
                RcpPmId, null);
        if (count > 0) System.out.println("RContact data delete ");

        db.close(); // Closing database connection
    }

    // Adding or Updating array Org
    public void addUpdateArrayOrganization(ArrayList<Organization> arrayListOrganization, String
            RcpPmId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        int count = db.delete(TABLE_RC_ORGANIZATION_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
                RcpPmId, null);
        if (count > 0) System.out.println("RContact data delete ");


//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListOrganization.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_OM_ID, arrayListOrganization.get(i).getOmId());
            values.put(COLUMN_OM_RECORD_INDEX_ID, arrayListOrganization.get(i).getOmRecordIndexId
                    ());
            values.put(COLUMN_OM_ORGANIZATION_COMPANY, arrayListOrganization.get(i)
                    .getOmOrganizationCompany());
            values.put(COLUMN_OM_ORGANIZATION_TYPE, arrayListOrganization.get(i)
                    .getOmOrganizationType());
            values.put(COLUMN_OM_ORGANIZATION_ENT_ID, arrayListOrganization.get(i)
                    .getOmEnterpriseOrgId());
            values.put(COLUMN_OM_ORGANIZATION_IMAGE, arrayListOrganization.get(i)
                    .getOmOrganizationLogo());
            values.put(COLUMN_OM_ORGANIZATION_FROM_DATE, arrayListOrganization.get(i)
                    .getOmOrganizationFromDate());
            values.put(COLUMN_OM_ORGANIZATION_TO_DATE, arrayListOrganization.get(i)
                    .getOmOrganizationToDate());
            values.put(COLUMN_OM_ORGANIZATION_DESIGNATION, arrayListOrganization.get(i)
                    .getOmOrganizationDesignation());
            values.put(COLUMN_OM_ORGANIZATION_IS_PRIVATE,
                    MoreObjects.firstNonNull(arrayListOrganization.get(i).getOmIsPrivate(), 0));
            values.put(COLUMN_OM_ORGANIZATION_IS_CURRENT, arrayListOrganization.get(i)
                    .getOmIsCurrent());
            values.put(COLUMN_OM_ORGANIZATION_IS_VERIFIED, arrayListOrganization.get(i)
                    .getOmIsVerified());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListOrganization.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_ORGANIZATION_MASTER, null, values);

//            int count = 0;
//            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RC_ORGANIZATION_MASTER
// + " " +
//                    "WHERE " + COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
//                    arrayListOrganization.get(i).getRcProfileMasterPmId(), null);
//            if (mCount != null) {
//                mCount.moveToFirst();
//                count = mCount.getInt(0);
//                mCount.close();
//            }
//
//            if (count > 0) {
//                // Update if already exists
//                db.update(TABLE_RC_ORGANIZATION_MASTER, values, COLUMN_RC_PROFILE_MASTER_PM_ID
// + " = " +
//                        arrayListOrganization.get(i).getRcProfileMasterPmId(), null);
//            } else {
//                // Inserting Row
//                db.insert(TABLE_RC_ORGANIZATION_MASTER, null, values);
//            }
        }

        db.close(); // Closing database connection
    }

    // Getting single org
    public Organization getOrganization(int omId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_ORGANIZATION_MASTER, new String[]{COLUMN_OM_ID,
                COLUMN_OM_RECORD_INDEX_ID, COLUMN_OM_ORGANIZATION_COMPANY,
                COLUMN_OM_ORGANIZATION_DESIGNATION, COLUMN_OM_ORGANIZATION_FROM_DATE,
                COLUMN_OM_ORGANIZATION_TO_DATE, COLUMN_OM_ORGANIZATION_IS_CURRENT,
                COLUMN_OM_ORGANIZATION_IS_PRIVATE, COLUMN_OM_ORGANIZATION_PRIVACY,
                COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_OM_ID + "=?", new String[]{String.valueOf
                (omId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Organization organization = new Organization();
        if (cursor != null) {
            organization.setOmId(cursor.getString(cursor.getColumnIndex(COLUMN_OM_ID)));
            organization.setOmRecordIndexId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_RECORD_INDEX_ID)));
            organization.setOmOrganizationCompany(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_COMPANY)));
            organization.setOmOrganizationDesignation(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_DESIGNATION)));
            organization.setOmOrganizationFromDate(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_FROM_DATE)));
            organization.setOmOrganizationToDate(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_TO_DATE)));
            organization.setOmIsCurrent(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_IS_CURRENT)));
            organization.setOmIsPrivate(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_IS_PRIVATE)));
            organization.setOmOrganizationPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_PRIVACY)));
            organization.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

            cursor.close();
        }

        db.close();

        // return organization
        return organization;
    }

    // Getting All Organizations from Profile Master Id
    public ArrayList<Organization> getOrganizationsFromPmId(int pmId) {
        this.pmId = pmId;
        ArrayList<Organization> arrayListOrganization = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_OM_RECORD_INDEX_ID + ", " +
                COLUMN_OM_ORGANIZATION_COMPANY + ", " +
                COLUMN_OM_ORGANIZATION_DESIGNATION + ", " +
                COLUMN_OM_ORGANIZATION_TYPE + ", " +
                COLUMN_OM_ORGANIZATION_ENT_ID + ", " +
                COLUMN_OM_ORGANIZATION_IMAGE + ", " +
                COLUMN_OM_ORGANIZATION_FROM_DATE + ", " +
                COLUMN_OM_ORGANIZATION_TO_DATE + ", " +
                COLUMN_OM_ORGANIZATION_IS_CURRENT + ", " +
                COLUMN_OM_ORGANIZATION_IS_PRIVATE + ", " +
                COLUMN_OM_ORGANIZATION_IS_VERIFIED + ", " +
                COLUMN_OM_ORGANIZATION_PRIVACY + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_ORGANIZATION_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Organization organization = new Organization();
                organization.setOmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_RECORD_INDEX_ID)));
                organization.setOmOrganizationCompany(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_COMPANY)));
                organization.setOmOrganizationDesignation(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_DESIGNATION)));
                organization.setOmOrganizationType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_TYPE)));
                organization.setOmEnterpriseOrgId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_ENT_ID)));
                organization.setOmOrganizationLogo(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_IMAGE)));
                organization.setOmOrganizationFromDate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_FROM_DATE)));
                organization.setOmOrganizationToDate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_TO_DATE)));
                organization.setOmIsCurrent(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_IS_CURRENT)));
                organization.setOmIsPrivate(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_IS_PRIVATE)));
                organization.setOmIsVerified(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_IS_VERIFIED)));
                organization.setOmOrganizationPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_PRIVACY)));
                organization.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding organization to list
                arrayListOrganization.add(organization);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return organization list
        return arrayListOrganization;
    }

    // Getting All Organizations from Profile Master Id
    public ArrayList<ProfileDataOperationOrganization> getAllOrganizationsFromPmId(int pmId) {
        this.pmId = pmId;
        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_OM_RECORD_INDEX_ID + ", " +
                COLUMN_OM_ORGANIZATION_COMPANY + ", " +
                COLUMN_OM_ORGANIZATION_DESIGNATION + ", " +
                COLUMN_OM_ORGANIZATION_TYPE + ", " +
                COLUMN_OM_ORGANIZATION_ENT_ID + ", " +
                COLUMN_OM_ORGANIZATION_IMAGE + ", " +
                COLUMN_OM_ORGANIZATION_FROM_DATE + ", " +
                COLUMN_OM_ORGANIZATION_TO_DATE + ", " +
                COLUMN_OM_ORGANIZATION_IS_CURRENT + ", " +
                COLUMN_OM_ORGANIZATION_IS_PRIVATE + ", " +
                COLUMN_OM_ORGANIZATION_IS_VERIFIED + ", " +
                COLUMN_OM_ORGANIZATION_PRIVACY + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_ORGANIZATION_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId + " ORDER BY " +
                TableOrganizationMaster.COLUMN_OM_ORGANIZATION_IS_CURRENT + " DESC, date(" +
                TableOrganizationMaster.COLUMN_OM_ORGANIZATION_FROM_DATE + ") DESC";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProfileDataOperationOrganization organization = new
                        ProfileDataOperationOrganization();

                organization.setOrgId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_RECORD_INDEX_ID)));
                organization.setOrgName(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableOrganizationMaster
                                .COLUMN_OM_ORGANIZATION_COMPANY))));
                organization.setOrgJobTitle(StringUtils.defaultString(cursor
                        .getString(cursor.getColumnIndexOrThrow(TableOrganizationMaster
                                .COLUMN_OM_ORGANIZATION_DESIGNATION))));
                organization.setOrgIndustryType(StringUtils.defaultString(cursor
                        .getString(cursor.getColumnIndexOrThrow(TableOrganizationMaster
                                .COLUMN_OM_ORGANIZATION_TYPE))));
                organization.setOrgEntId(StringUtils.defaultString(cursor
                        .getString(cursor.getColumnIndexOrThrow(TableOrganizationMaster
                                .COLUMN_OM_ORGANIZATION_ENT_ID))));
                organization.setOrgLogo(StringUtils.defaultString(cursor
                        .getString(cursor.getColumnIndexOrThrow(TableOrganizationMaster
                                .COLUMN_OM_ORGANIZATION_IMAGE))));
                organization.setOrgFromDate(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableOrganizationMaster
                                .COLUMN_OM_ORGANIZATION_FROM_DATE))));
                organization.setOrgToDate(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableOrganizationMaster
                                .COLUMN_OM_ORGANIZATION_TO_DATE))));
                organization.setIsVerify(Integer.parseInt(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableOrganizationMaster
                                .COLUMN_OM_ORGANIZATION_IS_VERIFIED))));
                organization.setOrgRcpType(String.valueOf(IntegerConstants
                        .RCP_TYPE_CLOUD_PHONE_BOOK));

                // Adding organization to list
                arrayListOrganization.add(organization);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return organization list
        return arrayListOrganization;
    }


    // Getting All Organizations
    public ArrayList<Organization> getAllOrganizations() {
        ArrayList<Organization> arrayListOrganization = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_ORGANIZATION_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Organization organization = new Organization();
                organization.setOmId(cursor.getString(cursor.getColumnIndex(COLUMN_OM_ID)));
                organization.setOmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_RECORD_INDEX_ID)));
                organization.setOmOrganizationCompany(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_COMPANY)));
                organization.setOmOrganizationDesignation(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_DESIGNATION)));
                organization.setOmIsCurrent(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_IS_CURRENT)));
                organization.setOmIsPrivate(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_IS_PRIVATE)));
                organization.setOmOrganizationPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_PRIVACY)));
                organization.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding organization to list
                arrayListOrganization.add(organization);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return organization list
        return arrayListOrganization;
    }

    // Getting organization Count
    public int getOrganizationCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_ORGANIZATION_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single organization
    public int updateOrganization(Organization organization) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OM_ID, organization.getOmId());
        values.put(COLUMN_OM_RECORD_INDEX_ID, organization.getOmRecordIndexId());
        values.put(COLUMN_OM_ORGANIZATION_COMPANY, organization.getOmOrganizationCompany());
        values.put(COLUMN_OM_ORGANIZATION_DESIGNATION, organization.getOmOrganizationDesignation());
        values.put(COLUMN_OM_ORGANIZATION_IS_CURRENT, organization.getOmIsCurrent());
        values.put(COLUMN_OM_ORGANIZATION_IS_PRIVATE, organization.getOmIsPrivate());
        values.put(COLUMN_OM_ORGANIZATION_PRIVACY, organization.getOmOrganizationPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, organization.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_ORGANIZATION_MASTER, values, COLUMN_OM_ID + " = ?",
                new String[]{String.valueOf(organization.getOmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single organization
    public void deleteOrganization(Organization organization) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_ORGANIZATION_MASTER, COLUMN_OM_ID + " = ?",
                new String[]{String.valueOf(organization.getOmId())});
        db.close();
    }

    // Deleting single Organization From RcpId
    public void deleteOrganization(String rcpId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_ORGANIZATION_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?",
                new String[]{String.valueOf(rcpId)});
        db.close();
    }
}
