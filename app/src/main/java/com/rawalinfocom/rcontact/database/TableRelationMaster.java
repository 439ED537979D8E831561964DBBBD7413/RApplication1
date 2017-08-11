package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Relation;

import java.util.ArrayList;

/**
 * Created by Monal on 14/11/16.
 * <p>
 * Table operations rc_relation_master
 */

public class TableRelationMaster {

    private DatabaseHandler databaseHandler;

    public TableRelationMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    public static final String TABLE_RC_RELATION_MASTER = "rc_relation_master";

    // Column Names
    private static final String COLUMN_RM_ID = "rm_id";
    private static final String COLUMN_RM_RELATION_NAME = "rm_relation_name";
    private static final String COLUMN_RM_RELATION_TYPE = "rm_relation_type";
    private static final String COLUMN_RM_CUSTOM_TYPE = "rm_custom_type";
    private static final String COLUMN_RM_RELATED_PM_ID = "rm_related_pm_id";
    private static final String COLUMN_RM_IS_VALID = "rm_is_valid";
    private static final String COLUMN_RM_RELATION_PRIVACY = "rm_relation_privacy";
    private static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    static final String CREATE_TABLE_RC_RELATION_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_RELATION_MASTER + " (" +
            " " + COLUMN_RM_ID + " integer NOT NULL CONSTRAINT rc_relation_master_pk PRIMARY KEY," +
            " " + COLUMN_RM_RELATION_NAME + " text NOT NULL," +
            " " + COLUMN_RM_RELATION_TYPE + " text NOT NULL," +
            " " + COLUMN_RM_CUSTOM_TYPE + " text," +
            " " + COLUMN_RM_RELATED_PM_ID + " text," +
            " " + COLUMN_RM_IS_VALID + " integer," +
            " " + COLUMN_RM_RELATION_PRIVACY + " integer DEFAULT 1," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new Relation
    public void addRelation(Relation relation) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RM_ID, relation.getRmId());
        values.put(COLUMN_RM_RELATION_NAME, relation.getRmRelationName());
        values.put(COLUMN_RM_RELATION_TYPE, relation.getRmRelationType());
        values.put(COLUMN_RM_CUSTOM_TYPE, relation.getRmCustomType());
        values.put(COLUMN_RM_RELATED_PM_ID, relation.getRmRelatedPmId());
        values.put(COLUMN_RM_IS_VALID, relation.getRmIsValid());
        values.put(COLUMN_RM_RELATION_PRIVACY, relation.getRmRelationPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, relation.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_RELATION_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Relation
    public Relation getRelation(int rmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_RELATION_MASTER, new String[]{COLUMN_RM_ID,
                        COLUMN_RM_RELATION_NAME, COLUMN_RM_RELATION_TYPE, COLUMN_RM_CUSTOM_TYPE,
                        COLUMN_RM_RELATED_PM_ID, COLUMN_RM_IS_VALID, COLUMN_RM_RELATION_PRIVACY,
                        COLUMN_RC_PROFILE_MASTER_PM_ID},
                COLUMN_RM_ID + "=?", new String[]{String.valueOf(rmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Relation relation = new Relation();
        if (cursor != null) {
            relation.setRmId(cursor.getString(0));
            relation.setRmRelationName(cursor.getString(1));
            relation.setRmRelationType(cursor.getString(2));
            relation.setRmCustomType(cursor.getString(3));
            relation.setRmRelatedPmId(cursor.getString(4));
            relation.setRmIsValid(cursor.getString(5));
            relation.setRmRelationPrivacy(cursor.getString(6));
            relation.setRcProfileMasterPmId(cursor.getString(7));

            cursor.close();
        }

        db.close();

        // return relation
        return relation;
    }

    // Getting All relations
    public ArrayList<Relation> getAllRelations() {
        ArrayList<Relation> arrayListRelation = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_RELATION_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Relation relation = new Relation();
                relation.setRmId(cursor.getString(0));
                relation.setRmRelationName(cursor.getString(1));
                relation.setRmRelationType(cursor.getString(2));
                relation.setRmCustomType(cursor.getString(3));
                relation.setRmRelatedPmId(cursor.getString(4));
                relation.setRmIsValid(cursor.getString(5));
                relation.setRmRelationPrivacy(cursor.getString(6));
                relation.setRcProfileMasterPmId(cursor.getString(7));
                // Adding relation to list
                arrayListRelation.add(relation);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return Relation list
        return arrayListRelation;
    }

    // Getting Relation Count
    public int getRelationCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_RELATION_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single Relation
    public int updateRelation(Relation relation) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RM_ID, relation.getRmId());
        values.put(COLUMN_RM_RELATION_NAME, relation.getRmRelationName());
        values.put(COLUMN_RM_RELATION_TYPE, relation.getRmRelationType());
        values.put(COLUMN_RM_CUSTOM_TYPE, relation.getRmCustomType());
        values.put(COLUMN_RM_RELATED_PM_ID, relation.getRmRelatedPmId());
        values.put(COLUMN_RM_IS_VALID, relation.getRmIsValid());
        values.put(COLUMN_RM_RELATION_PRIVACY, relation.getRmRelationPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, relation.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_RELATION_MASTER, values, COLUMN_RM_ID + " = ?",
                new String[]{String.valueOf(relation.getRmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single relation
    public void deleteRelation(Relation relation) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_RELATION_MASTER, COLUMN_RM_ID + " = ?",
                new String[]{String.valueOf(relation.getRmId())});
        db.close();
    }
}
