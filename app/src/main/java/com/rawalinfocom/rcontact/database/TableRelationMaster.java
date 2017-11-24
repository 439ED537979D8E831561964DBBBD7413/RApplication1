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
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_RM_PARTICULAR = "rm_particular";
    public static final String COLUMN_RM_TYPE = "rm_type";


    // Table Create Statements
    static final String CREATE_TABLE_RC_RELATION_MASTER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RC_RELATION_MASTER + " (" +
            " " + COLUMN_ID + " integer NOT NULL CONSTRAINT rc_relation_master_pk PRIMARY KEY," +
            " " + COLUMN_RM_PARTICULAR + " text NOT NULL," +
            " " + COLUMN_RM_TYPE + " text NOT NULL" +
            ");";


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

    private int relationCount() {

        try {
            String selectQuery = "SELECT  * FROM " + TABLE_RC_RELATION_MASTER;

            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            cursor.moveToFirst();

            return cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void insertData() {

        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        db.execSQL("INSERT INTO rc_relation_master (rm_particular, rm_type) VALUES ('Friend', 1),('Father', 2)," +
                "('Father-in-law', 2),('Stepfather', 2),('Grandfather', 2),('Son', 2),('Son-in-law', 2)," +
                "('Stepson', 2),('Grandson', 2),('Brother', 2),('Brother-in-law', 2),('Cousin Brother', 2)," +
                "('Stepbrother', 2),('Uncle', 2),('Maternal Uncle', 2),('Nephew', 2),('Husband', 2),('Mother', 2)," +
                "('Mother-in-law', 2),('Stepmother', 2),('Grandmother', 2),('Daughter', 2),('Daughter-in-law', 2)," +
                "('Granddaughter', 2),('Stepdaughter', 2),('Sister', 2),('Stepsister', 2),('Cousin Sister', 2)," +
                "('Sister-in-law', 2),('Aunt', 2),('Maternal Aunt', 2),('Niece', 2),('Wife', 2),('Co-Worker', 3)," +
                "('Competitor', 3),('Supplier', 3),('Customer', 3);");

        db.close();
    }

//    // Getting All relations
//    public ArrayList<Relation> getAllRelations() {
//        ArrayList<Relation> arrayListRelation = new ArrayList<>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_RC_RELATION_MASTER;
//
//        SQLiteDatabase db = databaseHandler.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                Relation relation = new Relation();
//                relation.setRmId(cursor.getInt(0));
//                relation.setRmRelationName(cursor.getString(1));
//                relation.setRmRelationType(cursor.getString(2));
//
//                // Adding relation to list
//                arrayListRelation.add(relation);
//            } while (cursor.moveToNext());
//
//            cursor.close();
//
//        }
//
//        db.close();
//
//        // return Relation list
//        return arrayListRelation;
//    }

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
}
