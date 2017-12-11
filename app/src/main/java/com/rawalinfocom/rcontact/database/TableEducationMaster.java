package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.model.Education;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEducation;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by Monal on 21/11/17.
 * <p>
 * Table operations rc_education_master
 */

public class TableEducationMaster {

    private DatabaseHandler databaseHandler;
    private int pmId;

    public TableEducationMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_EDUCATION_MASTER = "rc_education_master";

    // Column Names
    private static final String COLUMN_EDM_ID = "edm_id";
    static final String COLUMN_EDM_RECORD_INDEX_ID = "edm_record_index_id";
    static final String COLUMN_EDM_SCHOOL_COLLEGE_NAME = "edm_school_college_name";
    static final String COLUMN_EDM_COURSE = "edm_course";
    static final String COLUMN_EDM_EDUCATION_FROM_DATE = "edm_education_from_date";
    static final String COLUMN_EDM_EDUCATION_TO_DATE = "edm_education_to_date";
    static final String COLUMN_EDM_EDUCATION_IS_CURRENT = "edm_education_is_current";
    static final String COLUMN_EDM_EDUCATION_IS_PRIVATE = "edm_education_is_private";
    static final String COLUMN_EDM_EDUCATION_PRIVACY = "edm_education_privacy";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    static final String CREATE_TABLE_RC_EDUCATION_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_EDUCATION_MASTER + " (" +
            " " + COLUMN_EDM_ID + " integer NOT NULL CONSTRAINT rc_education_master_pk PRIMARY " +
            "" + "KEY," +
            " " + COLUMN_EDM_RECORD_INDEX_ID + " text," +
            " " + COLUMN_EDM_SCHOOL_COLLEGE_NAME + " text NOT NULL," +
            " " + COLUMN_EDM_COURSE + " text," +
            " " + COLUMN_EDM_EDUCATION_FROM_DATE + " text," +
            " " + COLUMN_EDM_EDUCATION_TO_DATE + " text," +
            " " + COLUMN_EDM_EDUCATION_IS_CURRENT + " integer," +
            " " + COLUMN_EDM_EDUCATION_IS_PRIVATE + " integer," +
            " " + COLUMN_EDM_EDUCATION_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer," +
            " UNIQUE(" + COLUMN_EDM_RECORD_INDEX_ID + ", " + COLUMN_RC_PROFILE_MASTER_PM_ID + ")" +
            ");";

    // Adding new Org
    public void addEducation(Education education) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EDM_ID, education.getEdmId());
        values.put(COLUMN_EDM_RECORD_INDEX_ID, education.getEdmRecordIndexId());
        values.put(COLUMN_EDM_SCHOOL_COLLEGE_NAME, education.getEdmSchoolCollegeName());
        values.put(COLUMN_EDM_COURSE, education.getEdmCourse());
        values.put(COLUMN_EDM_EDUCATION_IS_CURRENT, education.getEdmEducationIsCurrent());
        values.put(COLUMN_EDM_EDUCATION_IS_PRIVATE, education.getEdmEducationIsPrivate());
        values.put(COLUMN_EDM_EDUCATION_PRIVACY, education.getEdmEducationPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, education.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_EDUCATION_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding array Org
    public void addArrayEducation(ArrayList<Education> arrayListEducation) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListEducation.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EDM_ID, arrayListEducation.get(i).getEdmId());
            values.put(COLUMN_EDM_RECORD_INDEX_ID, arrayListEducation.get(i).getEdmRecordIndexId
                    ());
            values.put(COLUMN_EDM_SCHOOL_COLLEGE_NAME, arrayListEducation.get(i)
                    .getEdmSchoolCollegeName());
            values.put(COLUMN_EDM_COURSE, arrayListEducation.get(i)
                    .getEdmCourse());
            values.put(COLUMN_EDM_EDUCATION_FROM_DATE, arrayListEducation.get(i)
                    .getEdmEducationFromDate());
            values.put(COLUMN_EDM_EDUCATION_TO_DATE, arrayListEducation.get(i)
                    .getEdmEducationToDate());
            values.put(COLUMN_EDM_EDUCATION_IS_CURRENT, arrayListEducation.get(i)
                    .getEdmEducationIsCurrent());
            values.put(COLUMN_EDM_EDUCATION_IS_PRIVATE, arrayListEducation.get(i)
                    .getEdmEducationIsPrivate());
            values.put(COLUMN_EDM_EDUCATION_PRIVACY, arrayListEducation.get(i)
                    .getEdmEducationPrivacy());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListEducation.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_EDUCATION_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    public void deleteEducationData(String RcpPmId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        int count = db.delete(TABLE_RC_EDUCATION_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
                RcpPmId, null);
        if (count > 0) System.out.println("RContact data delete ");

        db.close(); // Closing database connection
    }

    // Adding or Updating array Org
//    public void addUpdateArrayEducation(ArrayList<Education> arrayListEducation, String RcpPmId) {
//        SQLiteDatabase db = databaseHandler.getWritableDatabase();
//
//        int count = db.delete(TABLE_RC_EDUCATION_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
//                RcpPmId, null);
//        if (count > 0) System.out.println("RContact data delete ");
//
////        ContentValues values = new ContentValues();
//        for (int i = 0; i < arrayListEducation.size(); i++) {
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_EDM_ID, arrayListEducation.get(i).getEdmId());
//            values.put(COLUMN_EDM_RECORD_INDEX_ID, arrayListEducation.get(i).getEdmRecordIndexId());
//            values.put(COLUMN_EDM_SCHOOL_COLLEGE_NAME, arrayListEducation.get(i)
//                    .getEdmSchoolCollegeName());
//            values.put(COLUMN_EDM_EDUCATION_FROM_DATE, arrayListEducation.get(i)
//                    .getEdmEducationFromDate());
//            values.put(COLUMN_EDM_EDUCATION_TO_DATE, arrayListEducation.get(i)
//                    .getEdmEducationToDate());
//            values.put(COLUMN_EDM_COURSE, arrayListEducation.get(i)
//                    .getEdmCourse());
//            values.put(COLUMN_EDM_EDUCATION_IS_PRIVATE, MoreObjects.firstNonNull
//                    (arrayListEducation.get(i).getEdmEducationIsPrivate(), 0));
//            values.put(COLUMN_EDM_EDUCATION_IS_CURRENT, arrayListEducation.get(i)
//                    .getEdmEducationIsCurrent());
//            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListEducation.get(i)
//                    .getRcProfileMasterPmId());
//
//            // Inserting Row
//            db.insert(TABLE_RC_EDUCATION_MASTER, null, values);
//
////            int count = 0;
////            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RC_EDUCATION_MASTER
//// + " " +
////                    "WHERE " + COLUMN_RC_PROFILE_MASTER_PM_ID + " = " +
////                    arrayListEducation.get(i).getRcProfileMasterPmId(), null);
////            if (mCount != null) {
////                mCount.moveToFirst();
////                count = mCount.getInt(0);
////                mCount.close();
////            }
////
////            if (count > 0) {
////                // Update if already exists
////                db.update(TABLE_RC_EDUCATION_MASTER, values, COLUMN_RC_PROFILE_MASTER_PM_ID
//// + " = " +
////                        arrayListEducation.get(i).getRcProfileMasterPmId(), null);
////            } else {
////                // Inserting Row
////                db.insert(TABLE_RC_EDUCATION_MASTER, null, values);
////            }
//        }
//
//        db.close(); // Closing database connection
//    }

    // Getting single org
    public Education getEducation(int edmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_EDUCATION_MASTER, new String[]{COLUMN_EDM_ID,
                COLUMN_EDM_RECORD_INDEX_ID, COLUMN_EDM_SCHOOL_COLLEGE_NAME,
                COLUMN_EDM_COURSE, COLUMN_EDM_EDUCATION_FROM_DATE,
                COLUMN_EDM_EDUCATION_TO_DATE, COLUMN_EDM_EDUCATION_IS_CURRENT,
                COLUMN_EDM_EDUCATION_IS_PRIVATE, COLUMN_EDM_EDUCATION_PRIVACY,
                COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_EDM_ID + "=?", new String[]{String.valueOf
                (edmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Education education = new Education();
        if (cursor != null) {
            education.setEdmId(cursor.getString(cursor.getColumnIndex(COLUMN_EDM_ID)));
            education.setEdmRecordIndexId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EDM_RECORD_INDEX_ID)));
            education.setEdmSchoolCollegeName(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EDM_SCHOOL_COLLEGE_NAME)));
            education.setEdmCourse(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EDM_COURSE)));
            education.setEdmEducationFromDate(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EDM_EDUCATION_FROM_DATE)));
            education.setEdmEducationToDate(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EDM_EDUCATION_TO_DATE)));
            education.setEdmEducationIsCurrent(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_EDM_EDUCATION_IS_CURRENT)));
            education.setEdmEducationIsPrivate(cursor.getInt(cursor.getColumnIndex
                    (COLUMN_EDM_EDUCATION_IS_PRIVATE)));
            education.setEdmEducationPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_EDM_EDUCATION_PRIVACY)));
            education.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

            cursor.close();
        }

        db.close();

        // return education
        return education;
    }

    // Getting All Educations from Profile Master Id
    public ArrayList<Education> getEducationsFromPmId(int pmId) {
        this.pmId = pmId;
        ArrayList<Education> arrayListEducation = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_EDM_RECORD_INDEX_ID + ", " +
                COLUMN_EDM_SCHOOL_COLLEGE_NAME + ", " +
                COLUMN_EDM_COURSE + ", " +
                COLUMN_EDM_EDUCATION_FROM_DATE + ", " +
                COLUMN_EDM_EDUCATION_TO_DATE + ", " +
                COLUMN_EDM_EDUCATION_IS_CURRENT + ", " +
                COLUMN_EDM_EDUCATION_IS_PRIVATE + ", " +
                COLUMN_EDM_EDUCATION_PRIVACY + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_EDUCATION_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Education education = new Education();
                education.setEdmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_RECORD_INDEX_ID)));
                education.setEdmSchoolCollegeName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_SCHOOL_COLLEGE_NAME)));
                education.setEdmCourse(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_COURSE)));
                education.setEdmEducationFromDate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_EDUCATION_FROM_DATE)));
                education.setEdmEducationToDate(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_EDUCATION_TO_DATE)));
                education.setEdmEducationIsCurrent(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_EDM_EDUCATION_IS_CURRENT)));
                education.setEdmEducationIsPrivate(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_EDM_EDUCATION_IS_PRIVATE)));
                education.setEdmEducationPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_EDUCATION_PRIVACY)));
                education.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding education to list
                arrayListEducation.add(education);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return education list
        return arrayListEducation;
    }

    // Getting All Educations from Profile Master Id
    public ArrayList<ProfileDataOperationEducation> getAllEducationsFromPmId(int pmId) {
        this.pmId = pmId;
        ArrayList<ProfileDataOperationEducation> arrayListEducation = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_EDM_RECORD_INDEX_ID + ", " +
                COLUMN_EDM_SCHOOL_COLLEGE_NAME + ", " +
                COLUMN_EDM_COURSE + ", " +
                COLUMN_EDM_EDUCATION_FROM_DATE + ", " +
                COLUMN_EDM_EDUCATION_TO_DATE + ", " +
                COLUMN_EDM_EDUCATION_IS_CURRENT + ", " +
                COLUMN_EDM_EDUCATION_IS_PRIVATE + ", " +
                COLUMN_EDM_EDUCATION_PRIVACY + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_EDUCATION_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId + " ORDER BY " +
                TableEducationMaster.COLUMN_EDM_EDUCATION_IS_CURRENT + " DESC, date(" +
                TableEducationMaster.COLUMN_EDM_EDUCATION_FROM_DATE + ") DESC";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProfileDataOperationEducation education = new
                        ProfileDataOperationEducation();

                education.setEduId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_RECORD_INDEX_ID)));
                education.setEduName(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableEducationMaster
                                .COLUMN_EDM_SCHOOL_COLLEGE_NAME))));
                education.setEduCourse(StringUtils.defaultString(cursor
                        .getString(cursor.getColumnIndexOrThrow(TableEducationMaster
                                .COLUMN_EDM_COURSE))));
                education.setEduFromDate(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableEducationMaster
                                .COLUMN_EDM_EDUCATION_FROM_DATE))));
                education.setEduToDate(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableEducationMaster
                                .COLUMN_EDM_EDUCATION_TO_DATE))));

                // Adding education to list
                arrayListEducation.add(education);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return education list
        return arrayListEducation;
    }


    // Getting All Educations
    public ArrayList<Education> getAllEducations() {
        ArrayList<Education> arrayListEducation = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_EDUCATION_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Education education = new Education();
                education.setEdmId(cursor.getString(cursor.getColumnIndex(COLUMN_EDM_ID)));
                education.setEdmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_RECORD_INDEX_ID)));
                education.setEdmSchoolCollegeName(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_SCHOOL_COLLEGE_NAME)));
                education.setEdmCourse(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_COURSE)));
                education.setEdmEducationIsCurrent(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_EDM_EDUCATION_IS_CURRENT)));
                education.setEdmEducationIsPrivate(cursor.getInt(cursor.getColumnIndex
                        (COLUMN_EDM_EDUCATION_IS_PRIVATE)));
                education.setEdmEducationPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_EDM_EDUCATION_PRIVACY)));
                education.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding education to list
                arrayListEducation.add(education);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return education list
        return arrayListEducation;
    }

    // Getting education Count
    public int getEducationCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_EDUCATION_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single education
    public int updateEducation(Education education) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EDM_ID, education.getEdmId());
        values.put(COLUMN_EDM_RECORD_INDEX_ID, education.getEdmRecordIndexId());
        values.put(COLUMN_EDM_SCHOOL_COLLEGE_NAME, education.getEdmSchoolCollegeName());
        values.put(COLUMN_EDM_COURSE, education.getEdmCourse());
        values.put(COLUMN_EDM_EDUCATION_IS_CURRENT, education.getEdmEducationIsCurrent());
        values.put(COLUMN_EDM_EDUCATION_IS_PRIVATE, education.getEdmEducationIsPrivate());
        values.put(COLUMN_EDM_EDUCATION_PRIVACY, education.getEdmEducationPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, education.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_EDUCATION_MASTER, values, COLUMN_EDM_ID + " = ?",
                new String[]{String.valueOf(education.getEdmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single education
    public void deleteEducation(Education education) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_EDUCATION_MASTER, COLUMN_EDM_ID + " = ?",
                new String[]{String.valueOf(education.getEdmId())});
        db.close();
    }

    // Deleting single Education From RcpId
    public void deleteEducation(String rcpId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_EDUCATION_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?",
                new String[]{String.valueOf(rcpId)});
        db.close();
    }
}
