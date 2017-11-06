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

    public TableRelationMaster(DatabaseHandler dbHandler) {
        databaseHandler = dbHandler;
    }

    // Table Names
    static final String TABLE_RC_RELATION_MASTER = "rc_relation_master";

    // Column Names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_RM_PARTICULAR = "rm_particular";
    private static final String COLUMN_RM_TYPE = "rm_type";


    // Table Create Statements
    static final String CREATE_TABLE_RC_RELATION_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_RELATION_MASTER + " (" +
            " " + COLUMN_ID + " integer NOT NULL CONSTRAINT rc_relation_master_pk PRIMARY KEY," +
            " " + COLUMN_RM_PARTICULAR + " text NOT NULL," +
            " " + COLUMN_RM_TYPE + " text NOT NULL" +
            ");";

    // Adding new Relation
    public void addRelation(Relation relation) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, relation.getRmId());
        values.put(COLUMN_RM_PARTICULAR, relation.getRmRelationName());
        values.put(COLUMN_RM_TYPE, relation.getRmRelationType());

        // Inserting Row
        db.insert(TABLE_RC_RELATION_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting Relation Name
    public ArrayList<Relation> getRelation(int rmType) {

        ArrayList<Relation> arrayListRelation = new ArrayList<>();

        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_RELATION_MASTER, new String[]{COLUMN_ID,
                        COLUMN_RM_PARTICULAR, COLUMN_RM_TYPE},
                COLUMN_RM_TYPE + "=?", new String[]{String.valueOf(rmType)}, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Relation relation = new Relation();
                relation.setRmId(cursor.getInt(0));
                relation.setRmRelationName(cursor.getString(1));
                relation.setRmRelationType(cursor.getString(2));

                arrayListRelation.add(relation);
            }
            cursor.close();
        }

        db.close();

        // return relation
        return arrayListRelation;
    }

    public void insertData() {

        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Friend', 1);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Father', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Father-in-law', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Stepfather', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Grandfather', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Son', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Son-in-law', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Stepson', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Grandson', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Brother', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Brother-in-law', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Cousin Brother', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Stepbrother', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Uncle', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Maternal Uncle', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Nephew', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Husband', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Mother', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Mother-in-law', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Stepmother', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Grandmother', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Daughter', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Daughter-in-law', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Granddaughter', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Stepdaughter', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Sister', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Stepsister', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Cousin Sister', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Sister-in-law', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Aunt', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Maternal Aunt', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Niece', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Wife', 2);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Co-Worker', 3);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Competitor', 3);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Supplier', 3);");
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Customer', 3);");

        db.close();
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
        values.put(COLUMN_ID, relation.getRmId());
        values.put(COLUMN_RM_PARTICULAR, relation.getRmRelationName());
        values.put(COLUMN_RM_TYPE, relation.getRmRelationType());

        // updating row
        int isUpdated = db.update(TABLE_RC_RELATION_MASTER, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(relation.getRmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single relation
    public void deleteRelation(Relation relation) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_RELATION_MASTER, COLUMN_ID + " = ?",
                new String[]{String.valueOf(relation.getRmId())});
        db.close();
    }
}
