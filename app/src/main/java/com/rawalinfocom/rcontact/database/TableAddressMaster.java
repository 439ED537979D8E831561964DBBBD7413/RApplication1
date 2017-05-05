package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Address;

import java.util.ArrayList;

/**
 * Created by Monal on 29/11/16.
 * <p>
 * Table operations rc_address_master
 */

public class TableAddressMaster {

    private DatabaseHandler databaseHandler;

    public TableAddressMaster(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    static final String TABLE_RC_ADDRESS_MASTER = "rc_address_master";

    // Column Names
    private static final String COLUMN_AM_ID = "am_id";
    static final String COLUMN_AM_RECORD_INDEX_ID = "am_record_index_id";
    private static final String COLUMN_AM_CITY = "am_city";
    private static final String COLUMN_AM_COUNTRY = "am_country";
    static final String COLUMN_AM_FORMATTED_ADDRESS = "am_formatted_address";
    private static final String COLUMN_AM_NEIGHBORHOOD = "am_neighborhood";
    private static final String COLUMN_AM_POST_CODE = "am_post_code";
    private static final String COLUMN_AM_PO_BOX = "am_po_box";
    private static final String COLUMN_AM_STATE = "am_state";
    private static final String COLUMN_AM_STREET = "am_street";
    static final String COLUMN_AM_ADDRESS_TYPE = "am_address_type";
    private static final String COLUMN_AM_GOOGLE_LATITUDE = "am_google_latitude";
    private static final String COLUMN_AM_GOOGLE_LONGITUDE = "am_google_longitude";
    static final String COLUMN_AM_ADDRESS_PRIVACY = "am_address_privacy";
    static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";

    /*private static final String COLUMN_AM_CUSTOM_TYPE = "am_custom_type";
    private static final String COLUMN_AM_GOOGLE_ADDRESS = "am_google_address";*/


    // Table Create Statements
    static final String CREATE_TABLE_RC_ADDRESS_MASTER = "CREATE TABLE rc_address_master (" +
            " " + COLUMN_AM_ID + " integer NOT NULL CONSTRAINT rc_address_master_pk PRIMARY KEY," +
            " " + COLUMN_AM_RECORD_INDEX_ID + " text," +
            " " + COLUMN_AM_CITY + " text," +
            " " + COLUMN_AM_COUNTRY + " text," +
            " " + COLUMN_AM_FORMATTED_ADDRESS + " text NOT NULL," +
            " " + COLUMN_AM_NEIGHBORHOOD + " text," +
            " " + COLUMN_AM_POST_CODE + " text," +
            " " + COLUMN_AM_PO_BOX + " text," +
            " " + COLUMN_AM_STATE + " text," +
            " " + COLUMN_AM_STREET + " text," +
            " " + COLUMN_AM_ADDRESS_TYPE + " text NOT NULL," +
            " " + COLUMN_AM_GOOGLE_LATITUDE + " text," +
            " " + COLUMN_AM_GOOGLE_LONGITUDE + " text," +
            " " + COLUMN_AM_ADDRESS_PRIVACY + " integer DEFAULT 2," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer," +
            " UNIQUE(" + COLUMN_AM_RECORD_INDEX_ID + ", " + COLUMN_RC_PROFILE_MASTER_PM_ID + ")" +
            ");";

    // Adding new Address
    public void addAddress(Address address) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_AM_ID, address.getAmId());
        values.put(COLUMN_AM_RECORD_INDEX_ID, address.getAmRecordIndexId());
        values.put(COLUMN_AM_CITY, address.getAmCity());
        values.put(COLUMN_AM_COUNTRY, address.getAmCountry());
        values.put(COLUMN_AM_FORMATTED_ADDRESS, address.getAmFormattedAddress());
        values.put(COLUMN_AM_NEIGHBORHOOD, address.getAmNeighborhood());
        values.put(COLUMN_AM_POST_CODE, address.getAmPostCode());
        values.put(COLUMN_AM_PO_BOX, address.getAmPoBox());
        values.put(COLUMN_AM_STATE, address.getAmState());
        values.put(COLUMN_AM_STREET, address.getAmStreet());
        values.put(COLUMN_AM_ADDRESS_TYPE, address.getAmAddressType());
        values.put(COLUMN_AM_GOOGLE_LATITUDE, address.getAmGoogleLatitude());
        values.put(COLUMN_AM_GOOGLE_LONGITUDE, address.getAmGoogleLongitude());
        values.put(COLUMN_AM_ADDRESS_PRIVACY, address.getAmAddressPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, address.getRcProfileMasterPmId());

        // Inserting Row
        db.insert(TABLE_RC_ADDRESS_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Adding array Address
    public void addArrayAddress(ArrayList<Address> arrayListAddress) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        ContentValues values = new ContentValues();
        for (int i = 0; i < arrayListAddress.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_AM_ID, arrayListAddress.get(i).getAmId());
            values.put(COLUMN_AM_RECORD_INDEX_ID, arrayListAddress.get(i).getAmRecordIndexId());
            values.put(COLUMN_AM_CITY, arrayListAddress.get(i).getAmCity());
            values.put(COLUMN_AM_COUNTRY, arrayListAddress.get(i).getAmCountry());
            values.put(COLUMN_AM_FORMATTED_ADDRESS, arrayListAddress.get(i).getAmFormattedAddress
                    ());
            values.put(COLUMN_AM_NEIGHBORHOOD, arrayListAddress.get(i).getAmNeighborhood());
            values.put(COLUMN_AM_POST_CODE, arrayListAddress.get(i).getAmPostCode());
            values.put(COLUMN_AM_PO_BOX, arrayListAddress.get(i).getAmPoBox());
            values.put(COLUMN_AM_STATE, arrayListAddress.get(i).getAmState());
            values.put(COLUMN_AM_STREET, arrayListAddress.get(i).getAmStreet());
            values.put(COLUMN_AM_ADDRESS_TYPE, arrayListAddress.get(i).getAmAddressType());
            values.put(COLUMN_AM_GOOGLE_LATITUDE, arrayListAddress.get(i).getAmGoogleLatitude());
            values.put(COLUMN_AM_GOOGLE_LONGITUDE, arrayListAddress.get(i).getAmGoogleLongitude());
            values.put(COLUMN_AM_ADDRESS_PRIVACY, arrayListAddress.get(i).getAmAddressPrivacy());
            values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, arrayListAddress.get(i)
                    .getRcProfileMasterPmId());

            // Inserting Row
            db.insert(TABLE_RC_ADDRESS_MASTER, null, values);
        }
        db.close(); // Closing database connection
    }

    // Getting single Address
    public Address getAddress(int amId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_ADDRESS_MASTER, new String[]{COLUMN_AM_ID,
                COLUMN_AM_RECORD_INDEX_ID, COLUMN_AM_CITY, COLUMN_AM_COUNTRY,
                COLUMN_AM_FORMATTED_ADDRESS, COLUMN_AM_NEIGHBORHOOD, COLUMN_AM_POST_CODE,
                COLUMN_AM_PO_BOX, COLUMN_AM_STATE, COLUMN_AM_STREET, COLUMN_AM_ADDRESS_TYPE,
                COLUMN_AM_GOOGLE_LATITUDE, COLUMN_AM_GOOGLE_LONGITUDE, COLUMN_AM_ADDRESS_PRIVACY,
                COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_AM_ID + "=?", new String[]{String.valueOf
                (amId)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Address address = new Address();
        if (cursor != null) {
            address.setAmId(cursor.getString(cursor.getColumnIndex(COLUMN_AM_ID)));
            address.setAmRecordIndexId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_AM_RECORD_INDEX_ID)));
            address.setAmCity(cursor.getString(cursor.getColumnIndex(COLUMN_AM_CITY)));
            address.setAmCountry(cursor.getString(cursor.getColumnIndex(COLUMN_AM_COUNTRY)));
            address.setAmFormattedAddress(cursor.getString(cursor.getColumnIndex
                    (COLUMN_AM_FORMATTED_ADDRESS)));
            address.setAmNeighborhood(cursor.getString(cursor.getColumnIndex
                    (COLUMN_AM_NEIGHBORHOOD)));
            address.setAmPostCode(cursor.getString(cursor.getColumnIndex(COLUMN_AM_POST_CODE)));
            address.setAmPoBox(cursor.getString(cursor.getColumnIndex(COLUMN_AM_PO_BOX)));
            address.setAmState(cursor.getString(cursor.getColumnIndex(COLUMN_AM_STATE)));
            address.setAmStreet(cursor.getString(cursor.getColumnIndex(COLUMN_AM_STREET)));
            address.setAmAddressType(cursor.getString(cursor.getColumnIndex
                    (COLUMN_AM_ADDRESS_TYPE)));
            address.setAmGoogleLatitude(cursor.getString(cursor.getColumnIndex
                    (COLUMN_AM_GOOGLE_LATITUDE)));
            address.setAmGoogleLongitude(cursor.getString(cursor.getColumnIndex
                    (COLUMN_AM_GOOGLE_LONGITUDE)));
            address.setAmAddressPrivacy(cursor.getString(cursor.getColumnIndex
                    (COLUMN_AM_ADDRESS_PRIVACY)));
            address.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                    (COLUMN_RC_PROFILE_MASTER_PM_ID)));

            cursor.close();
        }

        db.close();

        // return address
        return address;
    }

    // Getting All Addresses from Profile Master Id
    public ArrayList<Address> getAddressesFromPmId(int pmId) {
        ArrayList<Address> arrayListAddress = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_AM_RECORD_INDEX_ID + ", " +
                COLUMN_AM_COUNTRY + ", " +
                COLUMN_AM_STATE + ", " +
                COLUMN_AM_CITY + ", " +
                COLUMN_AM_STREET + ", " +
                COLUMN_AM_ADDRESS_PRIVACY + ", " +
                COLUMN_AM_NEIGHBORHOOD + ", " +
                COLUMN_AM_FORMATTED_ADDRESS + ", " +
                COLUMN_AM_POST_CODE + ", " +
                COLUMN_AM_ADDRESS_TYPE + ", " +
                COLUMN_AM_GOOGLE_LATITUDE + ", " +
                COLUMN_AM_GOOGLE_LONGITUDE + ", " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " FROM " +
                TABLE_RC_ADDRESS_MASTER + " WHERE " +
                COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + pmId;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Address address = new Address();
                address.setAmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_RECORD_INDEX_ID)));
                address.setAmCountry(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_COUNTRY)));
                address.setAmState(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_STATE)));
                address.setAmCity(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_CITY)));
                address.setAmStreet(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_STREET)));
                address.setAmNeighborhood(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_NEIGHBORHOOD)));
                address.setAmFormattedAddress(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_FORMATTED_ADDRESS)));
                address.setAmPostCode(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_POST_CODE)));
                address.setAmGoogleLatitude(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_GOOGLE_LATITUDE)));
                address.setAmGoogleLongitude(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_GOOGLE_LONGITUDE)));
                address.setAmAddressType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_ADDRESS_TYPE)));
                address.setAmAddressPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_ADDRESS_PRIVACY)));
                address.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding address to list
                arrayListAddress.add(address);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return organization list
        return arrayListAddress;
    }

    // Getting All Addresses
    public ArrayList<Address> getAllAddress() {
        ArrayList<Address> arrayListAddress = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_ADDRESS_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Address address = new Address();
                address.setAmId(cursor.getString(cursor.getColumnIndex(COLUMN_AM_ID)));
                address.setAmRecordIndexId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_RECORD_INDEX_ID)));
                address.setAmCity(cursor.getString(cursor.getColumnIndex(COLUMN_AM_CITY)));
                address.setAmCountry(cursor.getString(cursor.getColumnIndex(COLUMN_AM_COUNTRY)));
                address.setAmFormattedAddress(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_FORMATTED_ADDRESS)));
                address.setAmNeighborhood(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_NEIGHBORHOOD)));
                address.setAmPostCode(cursor.getString(cursor.getColumnIndex(COLUMN_AM_POST_CODE)));
                address.setAmPoBox(cursor.getString(cursor.getColumnIndex(COLUMN_AM_PO_BOX)));
                address.setAmState(cursor.getString(cursor.getColumnIndex(COLUMN_AM_STATE)));
                address.setAmStreet(cursor.getString(cursor.getColumnIndex(COLUMN_AM_STREET)));
                address.setAmAddressType(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_ADDRESS_TYPE)));
                address.setAmGoogleLatitude(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_GOOGLE_LATITUDE)));
                address.setAmGoogleLongitude(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_GOOGLE_LONGITUDE)));
                address.setAmAddressPrivacy(cursor.getString(cursor.getColumnIndex
                        (COLUMN_AM_ADDRESS_PRIVACY)));
                address.setRcProfileMasterPmId(cursor.getString(cursor.getColumnIndex
                        (COLUMN_RC_PROFILE_MASTER_PM_ID)));
                // Adding Address to list
                arrayListAddress.add(address);
            } while (cursor.moveToNext());

            cursor.close();

        }

        db.close();

        // return address list
        return arrayListAddress;
    }

    // Getting address Count
    public int getAddressCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_ADDRESS_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }

    // Updating single Address
    public int updateAddress(Address address) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_AM_ID, address.getAmId());
        values.put(COLUMN_AM_RECORD_INDEX_ID, address.getAmRecordIndexId());
        values.put(COLUMN_AM_CITY, address.getAmCity());
        values.put(COLUMN_AM_COUNTRY, address.getAmCountry());
        values.put(COLUMN_AM_FORMATTED_ADDRESS, address.getAmFormattedAddress());
        values.put(COLUMN_AM_NEIGHBORHOOD, address.getAmNeighborhood());
        values.put(COLUMN_AM_POST_CODE, address.getAmPostCode());
        values.put(COLUMN_AM_PO_BOX, address.getAmPoBox());
        values.put(COLUMN_AM_STATE, address.getAmState());
        values.put(COLUMN_AM_STREET, address.getAmStreet());
        values.put(COLUMN_AM_ADDRESS_TYPE, address.getAmAddressType());
        values.put(COLUMN_AM_GOOGLE_LATITUDE, address.getAmGoogleLatitude());
        values.put(COLUMN_AM_GOOGLE_LONGITUDE, address.getAmGoogleLongitude());
        values.put(COLUMN_AM_ADDRESS_PRIVACY, address.getAmAddressPrivacy());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, address.getRcProfileMasterPmId());

        // updating row
        int isUpdated = db.update(TABLE_RC_ADDRESS_MASTER, values, COLUMN_AM_ID + " = ?",
                new String[]{String.valueOf(address.getAmId())});

        db.close();

        return isUpdated;
    }

    // Deleting single Address
    public void deleteAddress(Address address) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_ADDRESS_MASTER, COLUMN_AM_ID + " = ?",
                new String[]{String.valueOf(address.getAmId())});
        db.close();
    }

    // Deleting single Address From RcpId
    public void deleteAddress(String rcpId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_ADDRESS_MASTER, COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?",
                new String[]{String.valueOf(rcpId)});
        db.close();
    }

    public int updatePrivacySettingToDefault(String cloudMongoId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_AM_ADDRESS_PRIVACY, 2);

        // updating row
        int isUpdated = db.update(TABLE_RC_ADDRESS_MASTER, values, COLUMN_AM_RECORD_INDEX_ID + " = ?",
                new String[]{cloudMongoId});

        db.close();

        return isUpdated;
    }
}
