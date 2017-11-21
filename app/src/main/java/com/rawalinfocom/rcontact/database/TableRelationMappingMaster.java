package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.Relation;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRequestResponse;

import org.apache.commons.lang3.StringUtils;

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
    public void addRelationMapping(ArrayList<RelationRequestResponse> relationRequestResponses) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        for (int i = 0; i < relationRequestResponses.size(); i++) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, relationRequestResponses.get(i).getId());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, relationRequestResponses.get(i).getRrmToPmId());
            values.put(COLUMN_RC_RELATIONS_MASTER_ID, relationRequestResponses.get(i).getRcRelationMasterId());
            values.put(COLUMN_RRM_TYPE, relationRequestResponses.get(i).getRrmType());
            values.put(COLUMN_RRM_STATUS, relationRequestResponses.get(i).getRcStatus());

            if (relationRequestResponses.get(i).getRrmType() == 3) {
                values.put(COLUMN_RRM_ORG_ENT_ID, relationRequestResponses.get(i).getRcOrgId());
            } else {
                values.put(COLUMN_RRM_ORG_ENT_ID, "0");
            }
            values.put(COLUMN_CREATED_AT, relationRequestResponses.get(i).getCreatedAt());

            // Inserting Row
            int id = (int) db.insert(TABLE_RC_RCP_RELATION_MAPPING, null, values);

            try {
                if (id == 1)
                    System.out.println("RContacts record insert!!!");
            } catch (Exception e) {
                db.close();
            }
        }

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
    public void deleteRelationMapping(String pmId) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_RCP_RELATION_MAPPING, COLUMN_RC_PROFILE_MASTER_PM_ID +
                " = ?", new String[]{String.valueOf(pmId)});
        db.close();
    }

    // Deleting single relation
    public void deleteRelationMapping(String pmId, ArrayList<String> relationIds) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        for (int i = 0; i < relationIds.size(); i++) {
            db.delete(TABLE_RC_RCP_RELATION_MAPPING, COLUMN_RC_PROFILE_MASTER_PM_ID +
                            " = ? and " + COLUMN_ID + " = ? ",
                    new String[]{String.valueOf(pmId), relationIds.get(i)});
        }
        db.close();
    }

    public ArrayList<RelationRecommendationType> getExistingRelation(String pmId) {

        ArrayList<RelationRecommendationType> existingRelationList = new ArrayList<>();
        ArrayList<IndividualRelationType> arrayList = new ArrayList<>();

        try {

            SQLiteDatabase db = databaseHandler.getWritableDatabase();

            String selectQuery = "SELECT a.pm_first_name,a.pm_last_name,a.pm_profile_image,d.mnm_mobile_number," +
                    "b.rc_profile_master_pm_id,b.rc_relations_master_id,b.created_at,b.rrm_type,b.rrm_status," +
                    "c.rm_particular,e.om_organization_company,e.om_organization_ent_id FROM rc_rcp_relation_mapping b " +
                    "left join rc_profile_master a on a.pm_rcp_id = b.rc_profile_master_pm_id " +
                    "left join rc_mobile_number_master d on d.rc_profile_master_pm_id = a.pm_rcp_id " +
                    "left join rc_relation_master c on c.id = b.rc_relations_master_id " +
                    "left join rc_organization_master e on e.om_organization_ent_id = b.rrm_org_ent_id " +
                    "where b.rc_profile_master_pm_id" + " = '" + pmId + "' " + "group by b.rc_relations_master_id " +
                    "order by b.rrm_type DESC";

            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                RelationRecommendationType recommendationType = new RelationRecommendationType();

                recommendationType.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(
                        TableProfileMaster.COLUMN_PM_FIRST_NAME)));
                recommendationType.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(
                        TableProfileMaster.COLUMN_PM_LAST_NAME)));
                recommendationType.setNumber(cursor.getString(cursor.getColumnIndexOrThrow
                        (TableMobileMaster.COLUMN_MNM_MOBILE_NUMBER)));
                recommendationType.setPmId(cursor.getString(cursor.getColumnIndexOrThrow
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                recommendationType.setDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow
                        (COLUMN_CREATED_AT)));
                recommendationType.setProfileImage(cursor.getString(cursor.getColumnIndexOrThrow
                        (TableProfileMaster.COLUMN_PM_PROFILE_IMAGE)));

                do {

                    IndividualRelationType individualRelationTypeList = new IndividualRelationType();

                    String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RRM_TYPE));
                    individualRelationTypeList.setRelationType(Integer.parseInt(type));

                    if (type.equalsIgnoreCase("3")) {

                        individualRelationTypeList.setRelationId(cursor.getString(cursor.getColumnIndexOrThrow
                                (COLUMN_RC_RELATIONS_MASTER_ID)));
                        individualRelationTypeList.setRelationName(cursor.getString(cursor.getColumnIndexOrThrow
                                (TableRelationMaster.COLUMN_RM_PARTICULAR)));
                        individualRelationTypeList.setOrganizationName(cursor.getString(cursor.getColumnIndexOrThrow
                                (TableOrganizationMaster.COLUMN_OM_ORGANIZATION_COMPANY)));
                        individualRelationTypeList.setFamilyName("");
                        individualRelationTypeList.setOrganizationId(cursor.getString(cursor.getColumnIndexOrThrow
                                (TableOrganizationMaster.COLUMN_OM_ORGANIZATION_ENT_ID)));
                        individualRelationTypeList.setIsFriendRelation(false);
                        individualRelationTypeList.setRcStatus(cursor.getInt(cursor.getColumnIndexOrThrow
                                (COLUMN_RRM_STATUS)));
                        individualRelationTypeList.setIsVerify("1");

                    } else if (type.equalsIgnoreCase("1")) {

                        individualRelationTypeList.setRelationId(cursor.getString(cursor.getColumnIndexOrThrow
                                (COLUMN_RC_RELATIONS_MASTER_ID)));
                        individualRelationTypeList.setRelationName("");
                        individualRelationTypeList.setOrganizationName("");
                        individualRelationTypeList.setFamilyName("");
                        individualRelationTypeList.setOrganizationId("");
                        individualRelationTypeList.setIsFriendRelation(true);
                        individualRelationTypeList.setRcStatus(cursor.getInt(cursor.getColumnIndexOrThrow
                                (COLUMN_RRM_STATUS)));
                        individualRelationTypeList.setIsVerify("1");

                    } else if (type.equalsIgnoreCase("2")) {

                        individualRelationTypeList.setRelationId(cursor.getString(cursor.getColumnIndexOrThrow
                                (COLUMN_RC_RELATIONS_MASTER_ID)));
                        individualRelationTypeList.setRelationName("");
                        individualRelationTypeList.setOrganizationName("");
                        individualRelationTypeList.setFamilyName(cursor.getString(cursor.getColumnIndexOrThrow
                                (TableRelationMaster.COLUMN_RM_PARTICULAR)));
                        individualRelationTypeList.setOrganizationId("");
                        individualRelationTypeList.setIsFriendRelation(false);
                        individualRelationTypeList.setRcStatus(cursor.getInt(cursor.getColumnIndexOrThrow
                                (COLUMN_RRM_STATUS)));
                        individualRelationTypeList.setIsVerify("1");

                    }

                    arrayList.add(individualRelationTypeList);
                }
                while (cursor.moveToNext());
                recommendationType.setIndividualRelationTypeList(arrayList);
                existingRelationList.add(recommendationType);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return existingRelationList;
    }

    public ArrayList<RelationRecommendationType> getAllExistingRelation() {

        ArrayList<RelationRecommendationType> pmIdList = new ArrayList<>();
        ArrayList<RelationRecommendationType> existingRelationList = new ArrayList<>();

        try {

            SQLiteDatabase db = databaseHandler.getWritableDatabase();

            String query = "SELECT a.pm_first_name,a.pm_last_name,a.pm_profile_image,b.created_at," +
                    "b.rc_profile_master_pm_id," + "d.mnm_mobile_number\n" + "FROM rc_rcp_relation_mapping b\n" +
                    "left Join rc_profile_master a on a.pm_rcp_id = b.rc_profile_master_pm_id \n" +
                    "left join rc_mobile_number_master d on d.rc_profile_master_pm_id = a.pm_rcp_id group by" +
                    " b.rc_profile_master_pm_id";

            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                do {
                    RelationRecommendationType recommendationType = new RelationRecommendationType();

                    recommendationType.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow
                            (TableProfileMaster.COLUMN_PM_FIRST_NAME)));
                    recommendationType.setLastName(cursor.getString(cursor.getColumnIndexOrThrow
                            (TableProfileMaster.COLUMN_PM_LAST_NAME)));
                    recommendationType.setNumber(cursor.getString(cursor.getColumnIndexOrThrow
                            (TableMobileMaster.COLUMN_MNM_MOBILE_NUMBER)));
                    recommendationType.setPmId(cursor.getString(cursor.getColumnIndexOrThrow
                            (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                    recommendationType.setDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow
                            (COLUMN_CREATED_AT)));
                    recommendationType.setProfileImage(cursor.getString(cursor.getColumnIndexOrThrow
                            (TableProfileMaster.COLUMN_PM_PROFILE_IMAGE)));

                    pmIdList.add(recommendationType);
                } while (cursor.moveToNext());
            }

            cursor.close();

            for (int i = 0; i < pmIdList.size(); i++) {

                String selectQuery = "SELECT b.id,b.rc_profile_master_pm_id,b.rc_relations_master_id,b.rrm_type," +
                        "b.rrm_status,c.rm_particular,e.om_organization_company,e.om_organization_ent_id FROM " +
                        "rc_rcp_relation_mapping b " +
                        "left Join rc_profile_master a on a.pm_rcp_id = b.rc_profile_master_pm_id " +
                        "left join rc_relation_master c on c.id = b.rc_relations_master_id " +
                        "left join rc_organization_master e on e.om_organization_ent_id = b.rrm_org_ent_id " +
                        "where" + " b.rc_profile_master_pm_id" + " = '" + pmIdList.get(i).getPmId() + "' " +
                        "group by b.rc_relations_master_id order by b.rrm_type DESC";

                Cursor cursor1 = db.rawQuery(selectQuery, null);
                cursor1.moveToFirst();

                ArrayList<IndividualRelationType> arrayList = new ArrayList<>();

                RelationRecommendationType recommendationType = new RelationRecommendationType();

                recommendationType.setFirstName(pmIdList.get(i).getFirstName());
                recommendationType.setLastName(pmIdList.get(i).getLastName());
                recommendationType.setNumber(pmIdList.get(i).getNumber());
                recommendationType.setPmId(pmIdList.get(i).getPmId());
                recommendationType.setDateAndTime(pmIdList.get(i).getDateAndTime());
                recommendationType.setProfileImage(pmIdList.get(i).getProfileImage());

                do {

                    IndividualRelationType individualRelationTypeList = new IndividualRelationType();

                    String type = cursor1.getString(cursor1.getColumnIndexOrThrow(COLUMN_RRM_TYPE));
                    individualRelationTypeList.setRelationType(Integer.parseInt(type));
                    individualRelationTypeList.setId(cursor1.getString(cursor1.getColumnIndexOrThrow
                            (COLUMN_ID)));

                    if (type.equalsIgnoreCase("3")) {

                        individualRelationTypeList.setRelationId(cursor1.getString(cursor1.getColumnIndexOrThrow
                                (COLUMN_RC_RELATIONS_MASTER_ID)));
                        individualRelationTypeList.setRelationName(cursor1.getString(cursor1.getColumnIndexOrThrow
                                (TableRelationMaster.COLUMN_RM_PARTICULAR)));
                        individualRelationTypeList.setOrganizationName(cursor1.getString(cursor1.getColumnIndexOrThrow
                                (TableOrganizationMaster.COLUMN_OM_ORGANIZATION_COMPANY)));
                        individualRelationTypeList.setFamilyName("");
                        individualRelationTypeList.setOrganizationId(cursor1.getString(cursor1.getColumnIndexOrThrow
                                (TableOrganizationMaster.COLUMN_OM_ORGANIZATION_ENT_ID)));
                        individualRelationTypeList.setIsFriendRelation(false);
                        individualRelationTypeList.setRcStatus(cursor1.getInt(cursor1.getColumnIndexOrThrow
                                (COLUMN_RRM_STATUS)));
                        individualRelationTypeList.setIsVerify("1");
                        individualRelationTypeList.setIsSelected(false);

                    } else if (type.equalsIgnoreCase("1")) {

                        individualRelationTypeList.setRelationId(cursor1.getString(cursor1.getColumnIndexOrThrow
                                (COLUMN_RC_RELATIONS_MASTER_ID)));
                        individualRelationTypeList.setRelationName("");
                        individualRelationTypeList.setOrganizationName("");
                        individualRelationTypeList.setFamilyName("");
                        individualRelationTypeList.setOrganizationId("");
                        individualRelationTypeList.setIsFriendRelation(true);
                        individualRelationTypeList.setRcStatus(cursor1.getInt(cursor1.getColumnIndexOrThrow
                                (COLUMN_RRM_STATUS)));
                        individualRelationTypeList.setIsVerify("1");
                        individualRelationTypeList.setIsSelected(false);

                    } else if (type.equalsIgnoreCase("2")) {

                        individualRelationTypeList.setRelationId(cursor1.getString(cursor1.getColumnIndexOrThrow
                                (COLUMN_RC_RELATIONS_MASTER_ID)));
                        individualRelationTypeList.setRelationName("");
                        individualRelationTypeList.setOrganizationName("");
                        individualRelationTypeList.setFamilyName(cursor1.getString(cursor1.getColumnIndexOrThrow
                                (TableRelationMaster.COLUMN_RM_PARTICULAR)));
                        individualRelationTypeList.setOrganizationId("");
                        individualRelationTypeList.setIsFriendRelation(false);
                        individualRelationTypeList.setRcStatus(cursor1.getInt(cursor1.getColumnIndexOrThrow
                                (COLUMN_RRM_STATUS)));
                        individualRelationTypeList.setIsVerify("1");
                        individualRelationTypeList.setIsSelected(false);
                    }

                    arrayList.add(individualRelationTypeList);
                }
                while (cursor1.moveToNext());

                recommendationType.setIndividualRelationTypeList(arrayList);
                existingRelationList.add(recommendationType);
                cursor1.close();
            }

            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return existingRelationList;
    }
}
