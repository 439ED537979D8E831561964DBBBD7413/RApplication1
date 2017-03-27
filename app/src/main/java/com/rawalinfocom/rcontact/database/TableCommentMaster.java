package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.model.Comment;

import java.util.ArrayList;

/**
 * Created by Maulik on 18/03/17.
 * <p>
 * Table operations rc_comment_master
 */


public class TableCommentMaster {

    private DatabaseHandler databaseHandler;

    public TableCommentMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_COMMENT_MASTER = "rc_comment_master";

    // Column Names
    private static final String COLUMN_CRM_ID = "crm_id";
    private static final String COLUMN_CRM_STATUS = "crm_status"; //1. Sent, 2. Received
    private static final String COLUMN_CRM_RATING = "crm_rating";
    private static final String COLUMN_CRM_TYPE = "crm_type"; // "eventName" "birthday" ,"anniversary" , "become father"
    private static final String COLUMN_CRM_CLOUD_PR_ID = "crm_cloud_pr_id";
    private static final String COLUMN_CRM_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";
    private static final String COLUMN_CRM_COMMENT = "crm_comment";
    private static final String COLUMN_CRM_REPLY = "crm_reply";
    private static final String COLUMN_CRM_CREATED_AT = "crm_created_at";
    private static final String COLUMN_CRM_REPLIED_AT = "crm_replied_at";
    private static final String COLUMN_CRM_UPDATED_AT = "crm_updated_at";
    private static final String COLUMN_EVM_RECORD_INDEX_ID = "evm_record_index_id";

    // Table Create Statements
    static final String CREATE_TABLE_RC_COMMENT_MASTER = "CREATE TABLE " + TABLE_RC_COMMENT_MASTER +
            " (" +
            " " + COLUMN_CRM_ID + " integer NOT NULL CONSTRAINT rc_comment_master_pk PRIMARY KEY AUTOINCREMENT," +
            " " + COLUMN_CRM_STATUS + " integer NOT NULL," +
            " " + COLUMN_CRM_RATING + " text," +
            " " + COLUMN_CRM_TYPE + " text NOT NULL," +
            " " + COLUMN_CRM_CLOUD_PR_ID + " text NOT NULL," +
            " " + COLUMN_CRM_RC_PROFILE_MASTER_PM_ID + " integer NOT NULL," +
            " " + COLUMN_CRM_COMMENT + " text NOT NULL," +
            " " + COLUMN_CRM_REPLY + " text," +
            " " + COLUMN_CRM_CREATED_AT + " datetime NOT NULL," +
            " " + COLUMN_CRM_REPLIED_AT + " datetime," +
            " " + COLUMN_CRM_UPDATED_AT + " datetime NOT NULL," +
            " " + COLUMN_EVM_RECORD_INDEX_ID + " text NOT NULL" +
            ");";

    // Adding new Event
    public void addComment(Comment comment) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CRM_STATUS, comment.getCrmStatus());
        values.put(COLUMN_CRM_RATING, comment.getCrmRating());
        values.put(COLUMN_CRM_TYPE, comment.getCrmType());
        values.put(COLUMN_CRM_CLOUD_PR_ID, comment.getCrmCloudPrId());
        values.put(COLUMN_CRM_RC_PROFILE_MASTER_PM_ID, comment.getRcProfileMasterPmId());
        values.put(COLUMN_CRM_COMMENT, comment.getCrmComment());
        values.put(COLUMN_CRM_REPLY, comment.getCrmReply());
        values.put(COLUMN_CRM_CREATED_AT, comment.getCrmCreatedAt());
        values.put(COLUMN_CRM_REPLIED_AT, comment.getCrmRepliedAt());
        values.put(COLUMN_CRM_UPDATED_AT, comment.getCrmUpdatedAt());
        values.put(COLUMN_EVM_RECORD_INDEX_ID, comment.getEvmRecordIndexId());

        db.insert(TABLE_RC_COMMENT_MASTER, null, values);
        db.close();
    }


    // Getting single Comment
    public Comment getComment(String evmRecordIndexId) {
        String selectQuery = "SELECT * FROM " + TABLE_RC_COMMENT_MASTER + " where " + COLUMN_EVM_RECORD_INDEX_ID + " =" + evmRecordIndexId + ";";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() > 0)
            cursor.moveToFirst();
        else
            cursor = null;

        Comment comment = null;
        if (cursor != null) {
            comment = new Comment();
            comment.setCrmId(cursor.getInt(cursor.getColumnIndex(COLUMN_CRM_ID)));
            comment.setCrmStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_CRM_STATUS)));
            comment.setCrmRating(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_RATING)));
            comment.setCrmType(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_TYPE)));
            comment.setCrmCloudPrId(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_CLOUD_PR_ID)));
            comment.setRcProfileMasterPmId(cursor.getInt(cursor.getColumnIndex(COLUMN_CRM_RC_PROFILE_MASTER_PM_ID)));
            comment.setCrmComment(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_COMMENT)));
            comment.setCrmReply(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_REPLY)));
            comment.setCrmCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_CREATED_AT)));
            comment.setCrmRepliedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_REPLIED_AT)));
            comment.setCrmUpdatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_UPDATED_AT)));
            comment.setEvmRecordIndexId(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_RECORD_INDEX_ID)));
            cursor.close();
        }
        db.close();
        // return comment
        return comment;
    }

    // Getting All Comment Received Between two dates
    public ArrayList<Comment> getAllCommentReceivedBetween(String fromDate, String toDate) {
        ArrayList<Comment> arrayListCommentReceived = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_RC_COMMENT_MASTER +
                " WHERE " + "crm_status=" + AppConstants.COMMENT_STATUS_RECEIVED + " and strftime('%m-%d'," + COLUMN_CRM_CREATED_AT +
                ") between '" + fromDate + "' and '" + toDate + "' order by " + COLUMN_CRM_CREATED_AT + " desc";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Comment comment = new Comment();
                comment.setCrmId(cursor.getInt(cursor.getColumnIndex(COLUMN_CRM_ID)));
                comment.setCrmStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_CRM_STATUS)));
                comment.setCrmRating(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_RATING)));
                comment.setCrmType(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_TYPE)));
                comment.setCrmCloudPrId(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_CLOUD_PR_ID)));
                comment.setRcProfileMasterPmId(cursor.getInt(cursor.getColumnIndex(COLUMN_CRM_RC_PROFILE_MASTER_PM_ID)));
                comment.setCrmComment(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_COMMENT)));
                comment.setCrmReply(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_REPLY)));
                comment.setCrmCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_CREATED_AT)));
                comment.setCrmRepliedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_REPLIED_AT)));
                comment.setCrmUpdatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CRM_UPDATED_AT)));
                comment.setEvmRecordIndexId(cursor.getString(cursor.getColumnIndex(COLUMN_EVM_RECORD_INDEX_ID)));
                arrayListCommentReceived.add(comment);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return arrayListCommentReceived;
    }

    public void deleteAllReceivedComments() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.execSQL("delete from " + TABLE_RC_COMMENT_MASTER + " WHERE crm_status=2");
    }

    public int addReply(String id, String reply, String replyAt, String updatedDate) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CRM_REPLY, reply);
        values.put(COLUMN_CRM_REPLIED_AT, replyAt);
        values.put(COLUMN_CRM_UPDATED_AT, updatedDate);

        int isUpdated = db.update(TABLE_RC_COMMENT_MASTER, values, COLUMN_CRM_CLOUD_PR_ID + " = ?",
                new String[]{id});

        db.close();

        return isUpdated;
    }


}