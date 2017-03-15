package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Organization;

import java.util.ArrayList;

/**
 * Created by Monal on 29/11/16.
 * <p>
 * Table operations rc_organization_master
 */

public class TableOrganizationMaster {

    private DatabaseHandler databaseHandler;

    public TableOrganizationMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_ORGANIZATION_MASTER = "rc_organization_master";

    // Column Names
    private static final String COLUMN_OM_ID = "om_id";
    private static final String COLUMN_OM_RECORD_INDEX_ID = "om_record_index_id";
    private static final String COLUMN_OM_ORGANIZATION_COMPANY = "om_organization_company";
    private static final String COLUMN_OM_ORGANIZATION_TYPE = "om_organization_type";
    private static final String COLUMN_OM_CUSTOM_TYPE = "om_custom_type";
    static final String COLUMN_OM_ORGANIZATION_TITLE = "om_organization_title";
    private static final String COLUMN_OM_ORGANIZATION_DEPARTMENT = "om_organization_department";
    static final String COLUMN_OM_JOB_DESCRIPTION = "om_job_description";
    private static final String COLUMN_OM_OFFICE_LOCATION = "om_office_location";
    private static final String COLUMN_OM_ORGANIZATION_PRIVACY = "om_organization_privacy";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    static final String CREATE_TABLE_RC_ORGANIZATION_MASTER = "CREATE TABLE " +
            TABLE_RC_ORGANIZATION_MASTER + " (" +
            " " + COLUMN_OM_ID + " integer NOT NULL CONSTRAINT rc_organization_master_pk PRIMARY " +
            "KEY," +
            " " + COLUMN_OM_RECORD_INDEX_ID + " text," +
            " " + COLUMN_OM_ORGANIZATION_COMPANY + " text NOT NULL," +
            " " + COLUMN_OM_ORGANIZATION_TYPE + " text," +
            " " + COLUMN_OM_CUSTOM_TYPE + " text," +
            " " + COLUMN_OM_ORGANIZATION_TITLE + " text," +
            " " + COLUMN_OM_ORGANIZATION_DEPARTMENT + " text," +
            " " + COLUMN_OM_JOB_DESCRIPTION + " text," +
            " " + COLUMN_OM_OFFICE_LOCATION + " text," +
            " " + COLUMN_OM_ORGANIZATION_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new Org
    public void addOrganization(Organization organization) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OM_ID, organization.getOmId());
        values.put(COLUMN_OM_RECORD_INDEX_ID, organization.getOmRecordIndexId());
        values.put(COLUMN_OM_ORGANIZATION_COMPANY, organization.getOmOrganizationCompany());
        values.put(COLUMN_OM_ORGANIZATION_TYPE, organization.getOmOrganizationType());
        values.put(COLUMN_OM_CUSTOM_TYPE, organization.getOmCustomType());
        values.put(COLUMN_OM_ORGANIZATION_TITLE, organization.getOmOrganizationTitle());
        values.put(COLUMN_OM_ORGANIZATION_DEPARTMENT, organization.getOmOrganizationDepartment());
        values.put(COLUMN_OM_JOB_DESCRIPTION, organization.getOmJobDescription());
        values.put(COLUMN_OM_OFFICE_LOCATION, organization.getOmOfficeLocation());
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
            values.put(COLUMN_OM_ORGANIZATION_TYPE, arrayListOrganization.get(i)
                    .getOmOrganizationType());
            values.put(COLUMN_OM_CUSTOM_TYPE, arrayListOrganization.get(i).getOmCustomType());
            values.put(COLUMN_OM_ORGANIZATION_TITLE, arrayListOrganization.get(i)
                    .getOmOrganizationTitle());
            values.put(COLUMN_OM_ORGANIZATION_DEPARTMENT, arrayListOrganization.get(i)
                    .getOmOrganizationDepartment());
            values.put(COLUMN_OM_JOB_DESCRIPTION, arrayListOrganization.get(i)
                    .getOmJobDescription());
            values.put(COLUMN_OM_OFFICE_LOCATION, arrayListOrganization.get(i)
                    .getOmOfficeLocation());
            values.put(COLUMN_OM_ORGANIZATION_PRIVACY, arrayListOrganization.get(i)
                    .getOmOrganizationPrivacy());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListOrganization.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_ORGANIZATION_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    // Getting single org
    public Organization getOrganization(int omId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_ORGANIZATION_MASTER, new String[]{COLUMN_OM_ID,
                COLUMN_OM_RECORD_INDEX_ID, COLUMN_OM_ORGANIZATION_COMPANY,
                COLUMN_OM_ORGANIZATION_TYPE, COLUMN_OM_CUSTOM_TYPE, COLUMN_OM_ORGANIZATION_TITLE,
                COLUMN_OM_ORGANIZATION_DEPARTMENT, COLUMN_OM_JOB_DESCRIPTION,
                COLUMN_OM_OFFICE_LOCATION, COLUMN_OM_ORGANIZATION_PRIVACY,
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
            organization.setOmOrganizationType(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_TYPE)));
            organization.setOmCustomType(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_CUSTOM_TYPE)));
            organization.setOmOrganizationTitle(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_TITLE)));
            organization.setOmOrganizationDepartment(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_ORGANIZATION_DEPARTMENT)));
            organization.setOmJobDescription(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_JOB_DESCRIPTION)));
            organization.setOmOfficeLocation(cursor.getString(cursor.getColumnIndex
                    (COLUMN_OM_OFFICE_LOCATION)));
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
        ArrayList<Organization> arrayListOrganization = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_OM_RECORD_INDEX_ID + ", " +
                COLUMN_OM_ORGANIZATION_COMPANY + ", " +
                COLUMN_OM_JOB_DESCRIPTION + ", " +
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
                organization.setOmJobDescription(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_JOB_DESCRIPTION)));
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
                organization.setOmOrganizationType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_TYPE)));
                organization.setOmCustomType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_CUSTOM_TYPE)));
                organization.setOmOrganizationTitle(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_TITLE)));
                organization.setOmOrganizationDepartment(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_ORGANIZATION_DEPARTMENT)));
                organization.setOmJobDescription(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_JOB_DESCRIPTION)));
                organization.setOmOfficeLocation(cursor.getString(cursor.getColumnIndex
                        (COLUMN_OM_OFFICE_LOCATION)));
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
        values.put(COLUMN_OM_ORGANIZATION_TYPE, organization.getOmOrganizationType());
        values.put(COLUMN_OM_CUSTOM_TYPE, organization.getOmCustomType());
        values.put(COLUMN_OM_ORGANIZATION_TITLE, organization.getOmOrganizationTitle());
        values.put(COLUMN_OM_ORGANIZATION_DEPARTMENT, organization.getOmOrganizationDepartment());
        values.put(COLUMN_OM_JOB_DESCRIPTION, organization.getOmJobDescription());
        values.put(COLUMN_OM_OFFICE_LOCATION, organization.getOmOfficeLocation());
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
}
