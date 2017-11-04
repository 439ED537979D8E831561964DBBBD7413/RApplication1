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

public class TableRelationMappingMaster {

    private DatabaseHandler databaseHandler;

    public TableRelationMappingMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    public static final String TABLE_RC_RCP_RELATION_MAPPING = "rc_rcp_relation_mapping";

    // Column Names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_RRM_PROFILE_DETAILS = "rrm_profiledetails";
    private static final String COLUMN_RRM_TYPE = "rrm_type";
    private static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";
    private static final String COLUMN_RC_RELATIONS_MASTER_ID = "rc_relations_master_id";
    private static final String COLUMN_RRM_STATUS = "rrm_status";
    private static final String COLUMN_RRM_ORG_ENT_ID = "rrm_org_ent_id";
    private static final String COLUMN_CREATED_AT = "created_at";

    // Table Create Statements
    static final String CREATE_TABLE_RC_RCP_RELATION_MAPPING = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_RCP_RELATION_MAPPING + " (" +
            " " + COLUMN_ID + " integer NOT NULL CONSTRAINT rc_rcp_relation_mapping_pk PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer NOT NULL," +
            " " + COLUMN_RC_RELATIONS_MASTER_ID + " integer NOT NULL," +
            " " + COLUMN_RRM_PROFILE_DETAILS + " text ," +
            " " + COLUMN_RRM_TYPE + " integer NOT NULL," +
            " " + COLUMN_RRM_STATUS + " integer NOT NULL," +
            " " + COLUMN_RRM_ORG_ENT_ID + " integer NOT NULL," +
            " " + COLUMN_CREATED_AT + " integer NOT NULL" +
            ");";

    // Adding new Relation
    public void addRelationMapping(Relation relation) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, relation.getRmId());
        values.put(COLUMN_RRM_PROFILE_DETAILS, relation.getRmRelationName());
        values.put(COLUMN_RRM_TYPE, relation.getRmRelationType());

        // Inserting Row
        db.insert(TABLE_RC_RCP_RELATION_MAPPING, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Relation
    public Relation getRelationMapping(int rmId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_RCP_RELATION_MAPPING, new String[]{COLUMN_ID,
                        COLUMN_RRM_PROFILE_DETAILS, COLUMN_RRM_TYPE},
                COLUMN_ID + "=?", new String[]{String.valueOf(rmId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Relation relation = new Relation();
        if (cursor != null) {
            relation.setRmId(cursor.getInt(0));
            relation.setRmRelationName(cursor.getString(1));
            relation.setRmRelationType(cursor.getString(2));

            cursor.close();
        }

        db.close();

        // return relation
        return relation;
    }

    // Getting All relations
    public ArrayList<Relation> getAllRelationsMapping() {
        ArrayList<Relation> arrayListRelation = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_RCP_RELATION_MAPPING;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Relation relation = new Relation();
                relation.setRmId(cursor.getInt(0));
                relation.setRmRelationName(cursor.getString(1));
                relation.setRmRelationType(cursor.getString(2));

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
        String countQuery = "SELECT  * FROM " + TABLE_RC_RCP_RELATION_MAPPING;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();

        // return count
        return count;
    }

    // Updating single Relation
    public int updateRelationMapping(Relation relation) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, relation.getRmId());
        values.put(COLUMN_RRM_PROFILE_DETAILS, relation.getRmRelationName());
        values.put(COLUMN_RRM_TYPE, relation.getRmRelationType());

        // updating row
        int isUpdated = db.update(TABLE_RC_RCP_RELATION_MAPPING, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(relation.getRmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single relation
    public void deleteRelationMapping(Relation relation) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_RCP_RELATION_MAPPING, COLUMN_ID + " = ?",
                new String[]{String.valueOf(relation.getRmId())});
        db.close();
    }
}
