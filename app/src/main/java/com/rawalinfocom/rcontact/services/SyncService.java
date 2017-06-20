package com.rawalinfocom.rcontact.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.database.TableWebsiteMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Address;
import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.ImAccount;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.ProfileEmailMapping;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.Website;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SyncService extends Service implements WsResponseListener {
    DatabaseHandler databaseHandler;
    ArrayList<ProfileData> arrayListSyncUserContact;
    PhoneBookContacts phoneBookContacts;
    int lastSyncedData = 0;
    private final int CONTACT_CHUNK = 50;
    public static boolean isRunning = false;
    private final String TAG = "SyncService";

    public SyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        databaseHandler = new DatabaseHandler(this);
        arrayListSyncUserContact = new ArrayList<>();
        phoneBookContacts = new PhoneBookContacts(this);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                syncContacts();
            }
        });
    }

    private void syncContacts() {
        Log.i(TAG, "Syncing Start" + System.currentTimeMillis());
        LongSparseArray<ProfileDataOperation> profileDetailSparseArray = new LongSparseArray<>();

        //<editor-fold desc="Create Cursor">
        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.STARRED,

                ContactsContract.CommonDataKinds.StructuredName.PREFIX,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME,

                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,

                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.TYPE,

                ContactsContract.CommonDataKinds.Website.TYPE,
                ContactsContract.CommonDataKinds.Website.URL,

                ContactsContract.CommonDataKinds.Organization.COMPANY,
                ContactsContract.CommonDataKinds.Organization.TITLE,
                ContactsContract.CommonDataKinds.Organization.TYPE,
                ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
                ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION,
                ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION,

                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD,
                ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
                ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE,

                ContactsContract.CommonDataKinds.Im.TYPE,
                ContactsContract.CommonDataKinds.Im.DATA1,
                ContactsContract.CommonDataKinds.Im.PROTOCOL,

                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.START_DATE,

        };
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?, ?, ?, ?, ?, ?, ?)";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                // starred contact not accessible
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
        };
        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
        Uri uri = ContactsContract.Data.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, projection, selection,
                selectionArgs, sortOrder);
        //</editor-fold>

        //<editor-fold desc="Data Read from Cursor">
        if (cursor != null) {
            final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
            final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);

            while (cursor.moveToNext()) {

                ProfileDataOperation operation = new ProfileDataOperation();
                operation.setFlag(1);
                long id = cursor.getLong(idIdx);
                ProfileDataOperation phoneBookContact = profileDetailSparseArray.get(id);
                if (phoneBookContact == null) {
                    phoneBookContact = new ProfileDataOperation(id);
                    profileDetailSparseArray.put(id, phoneBookContact);
                }
                phoneBookContact.setLookupKey(cursor.getString(cursor.getColumnIndex
                        (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID)));
                phoneBookContact.setIsFavourite(cursor.getString(cursor.getColumnIndex
                        (ContactsContract.Contacts.STARRED)));
                String mimeType = cursor.getString(mimeTypeIdx);
                switch (mimeType) {
                    case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:

                        phoneBookContact.setPbNamePrefix(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredName.PREFIX)));
                        phoneBookContact.setPbNameFirst(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredName.GIVEN_NAME)));
                        phoneBookContact.setPbNameMiddle(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredName.MIDDLE_NAME)));
                        phoneBookContact.setPbNameLast(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredName.FAMILY_NAME)));
                        phoneBookContact.setPbNameSuffix(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredName.SUFFIX)));
                        phoneBookContact.setPbPhoneticNameFirst(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME)));
                        phoneBookContact.setPbPhoneticNameMiddle(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME)));
                        phoneBookContact.setPbPhoneticNameLast(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME)));
                        break;
                    case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                        ProfileDataOperationPhoneNumber phoneNumber = new
                                ProfileDataOperationPhoneNumber();

                        phoneNumber.setPhoneNumber(Utils.getFormattedNumber(this, cursor
                                .getString(cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Phone.NUMBER))));
                        phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                                (cursor.getInt(cursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.TYPE))));
                        phoneNumber.setPhonePublic(IntegerConstants.PRIVACY_EVERYONE);

                        phoneBookContact.addPhone(phoneNumber);
                        break;
                    case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                        ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                        emailId.setEmEmailId(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                        emailId.setEmType(phoneBookContacts.getEmailType(cursor,
                                cursor.getInt
                                        (cursor.getColumnIndex(ContactsContract
                                                .CommonDataKinds.Email.TYPE))));
                        emailId.setEmPublic(IntegerConstants.PRIVACY_EVERYONE);


                        phoneBookContact.addEmail(emailId);
                        break;
                    case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                        ProfileDataOperationWebAddress webAddress = new
                                ProfileDataOperationWebAddress();

                        webAddress.setWebAddress(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                        webAddress.setWebType(phoneBookContacts.getWebsiteType(cursor, (cursor
                                .getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                                        .Website.TYPE)))));
                        webAddress.setWebPublic(IntegerConstants.PRIVACY_EVERYONE);

                        phoneBookContact.addWebsite(webAddress);

                        break;
                    case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                        ProfileDataOperationOrganization organization = new
                                ProfileDataOperationOrganization();

                        organization.setOrgName(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.COMPANY)));
                        organization.setOrgJobTitle(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.TITLE)));
                        organization.setOrgDepartment(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.DEPARTMENT)));
                        organization.setOrgType(phoneBookContacts.getOrganizationType(cursor,
                                cursor.getInt((cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.TYPE)))));
                        organization.setOrgJobDescription(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                        organization.setOrgOfficeLocation(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.OFFICE_LOCATION)));
                        organization.setOrgPublic(IntegerConstants.PRIVACY_EVERYONE);

                        phoneBookContact.addOrganization(organization);
                        break;
                    case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                        ProfileDataOperationAddress address = new ProfileDataOperationAddress();

                        address.setFormattedAddress(cursor.getString
                                (cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                        address.setCity(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .CITY)));
                        address.setCountry(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .COUNTRY)));
                        address.setNeighborhood(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .NEIGHBORHOOD)));
                        address.setPostCode(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .POSTCODE)));
                        address.setPoBox(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .POBOX)));
                        address.setStreet(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .STREET)));
                        address.setAddressType(phoneBookContacts.getAddressType(cursor, cursor
                                .getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                                        .StructuredPostal.TYPE))));
                        address.setAddPublic(IntegerConstants.PRIVACY_EVERYONE);

                        phoneBookContact.addAddress(address);
                        break;
                    case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                        ProfileDataOperationImAccount imAccount = new
                                ProfileDataOperationImAccount();


                        imAccount.setIMAccountDetails(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                        imAccount.setIMAccountType(phoneBookContacts.getImAccountType(cursor,
                                cursor.getInt(cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Im.TYPE))));

                        imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                                (cursor.getInt((cursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Im.PROTOCOL)))));

                        imAccount.setIMAccountPublic(IntegerConstants.PRIVACY_EVERYONE);


                        phoneBookContact.addImAccount(imAccount);
                        break;
                    case ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE:

                        ProfileDataOperationEvent event = new ProfileDataOperationEvent();

                        event.setEventType(phoneBookContacts.getEventType(cursor, cursor.getInt
                                (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event
                                        .TYPE))));

                        String eventDate = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                        .START_DATE));

                        if (StringUtils.startsWith(eventDate, "--")) {
                            eventDate = "1900" + eventDate.substring(1, StringUtils.length
                                    (eventDate));
                        }

                        event.setEventDateTime(eventDate);

                        event.setEventPublic(IntegerConstants.PRIVACY_EVERYONE);

                        phoneBookContact.addEvent(event);
                        break;
                }
            }
            cursor.close();
        }
        //</editor-fold>
        for (int i = 0; i < profileDetailSparseArray.size(); i++) {
            //<editor-fold desc="Prepare Data">
            ProfileDataOperation profileContact = profileDetailSparseArray.valueAt(i);

            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

            ProfileData profileData = new ProfileData();
            profileData.setLocalPhoneBookId(profileContact.getLookupKey());

            ProfileDataOperation operation = new ProfileDataOperation();

            operation.setFlag(IntegerConstants.SYNC_INSERT_CONTACT);
            operation.setIsFirst(1);

            operation.setPbNamePrefix(profileContact.getPbNamePrefix());
            operation.setPbNameFirst(profileContact.getPbNameFirst());
            operation.setPbNameMiddle(profileContact.getPbNameMiddle());
            operation.setPbNameLast(profileContact.getPbNameLast());
            operation.setPbNameSuffix(profileContact.getPbNameSuffix());
            operation.setPbPhoneticNameFirst(profileContact.getPbPhoneticNameFirst());
            operation.setPbPhoneticNameMiddle(profileContact.getPbPhoneticNameMiddle());
            operation.setPbPhoneticNameLast(profileContact.getPbPhoneticNameLast());

            operation.setIsFavourite(String.valueOf(profileContact.getIsFavourite()));

            operation.setPbPhoneNumber(profileContact.getPbPhoneNumber());
            operation.setPbEmailId(profileContact.getPbEmailId());
            operation.setPbWebAddress(profileContact.getPbWebAddress());
            operation.setPbOrganization(profileContact.getPbOrganization());
            operation.setPbAddress(profileContact.getPbAddress());
            operation.setPbIMAccounts(profileContact.getPbIMAccounts());
            operation.setPbEvent(profileContact.getPbEvent());

            arrayListOperation.add(operation);
            profileData.setOperation(arrayListOperation);

            arrayListSyncUserContact.add(profileData);
        }
        //</editor-fold>

        if (lastSyncedData < arrayListSyncUserContact.size()) {
            backgroundSync(false, null);
        }
    }

    private void backgroundSync(boolean addToDatabase, WsResponseObject
            uploadContactResponse) {
//        Runnable run = new Runnable() {
//            @Override
//            public void run() {
        if (addToDatabase) {
            if (uploadContactResponse != null) {
                if (!Utils.isArraylistNullOrEmpty(uploadContactResponse
                        .getArrayListUserRcProfile())) {

                        /* Store Unique Contacts to ProfileMobileMapping */
                    storeToMobileMapping(uploadContactResponse.getArrayListUserRcProfile());

                        /* Store Unique Emails to ProfileEmailMapping */
                    storeToEmailMapping(uploadContactResponse.getArrayListUserRcProfile());

                        /* Store Profile Details to respective Table */
                    storeProfileDataToDb(uploadContactResponse.getArrayListUserRcProfile(),
                            uploadContactResponse.getArrayListMapping());

                }
            }
        }
        int limit;
        if (arrayListSyncUserContact.size() > (lastSyncedData + CONTACT_CHUNK)) {
            limit = lastSyncedData + CONTACT_CHUNK;
        } else {
            limit = arrayListSyncUserContact.size();
        }
        ArrayList<ProfileData> subList = new ArrayList<>(arrayListSyncUserContact.subList
                (lastSyncedData, limit));
        uploadContacts(lastSyncedData, subList);
    }
//        };
//        AsyncTask.execute(run);
//}

    private void uploadContacts(int previouslySyncedData, ArrayList<ProfileData> arrayListUserContact) {
        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setPmId(Integer.parseInt(getUserPmId()));
        uploadContactObject.setProfileData(arrayListUserContact);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS + "_" + previouslySyncedData, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CONTACTS);
        }
    }

    private void storeToMobileMapping(ArrayList<ProfileDataOperation> profileData) {
        TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                (getDatabaseHandler());
        ArrayList<ProfileMobileMapping> arrayListProfileMobileMapping = new ArrayList<>();
        if (!Utils.isArraylistNullOrEmpty(profileData)) {
            for (int j = 0; j < profileData.size(); j++) {
                ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
                profileMobileMapping.setMpmMobileNumber("+" + profileData.get(j)
                        .getVerifiedMobileNumber());
                profileMobileMapping.setMpmCloudMnmId(profileData.get(j)
                        .getMnmCloudId());
                profileMobileMapping.setMpmCloudPmId(profileData.get(j).getRcpPmId());
                profileMobileMapping.setMpmIsRcp("1");
                arrayListProfileMobileMapping.add(profileMobileMapping);
            }
        }
        tableProfileMobileMapping.addArrayProfileMobileMapping(arrayListProfileMobileMapping);
    }

    private void storeToEmailMapping(ArrayList<ProfileDataOperation> profileData) {
        TableProfileEmailMapping tableProfileEmailMapping = new TableProfileEmailMapping
                (getDatabaseHandler());
        ArrayList<ProfileEmailMapping> arrayListProfileEmailMapping = new ArrayList<>();
        if (!Utils.isArraylistNullOrEmpty(profileData)) {
            for (int j = 0; j < profileData.size(); j++) {
                if (!Utils.isArraylistNullOrEmpty(profileData.get(j).getVerifiedEmailIds())) {
                    for (int k = 0; k < profileData.get(j).getVerifiedEmailIds().size(); k++) {
                        if (!tableProfileEmailMapping.getIsEmailIdExists(profileData.get(j)
                                .getVerifiedEmailIds().get(k).getEmEmailId())) {
                            ProfileEmailMapping profileEmailMapping = new ProfileEmailMapping();
                            profileEmailMapping.setEpmEmailId(profileData.get(j)
                                    .getVerifiedEmailIds().get(k).getEmEmailId());
                            profileEmailMapping.setEpmCloudEmId(String.valueOf(profileData
                                    .get(j).getVerifiedEmailIds().get(k).getEmId()));
                            profileEmailMapping.setEpmCloudPmId(profileData.get(j).getRcpPmId
                                    ());
                            profileEmailMapping.setEpmIsRcp("1");
                            arrayListProfileEmailMapping.add(profileEmailMapping);
                        }
                    }
                }
            }

        }

        tableProfileEmailMapping.addArrayProfileEmailMapping(arrayListProfileEmailMapping);
    }

    private void storeProfileDataToDb(ArrayList<ProfileDataOperation> profileData,
                                      ArrayList<ProfileData> mapping) {
        // Hashmap with key as rcpId and value as rawId/s
        HashMap<String, String> mapLocalRcpId = new HashMap<>();

        for (int i = 0; i < mapping.size(); i++) {
            for (int j = 0; j < mapping.get(i).getRcpPmId().size(); j++) {
                String phonebookRawId;
                if (mapLocalRcpId.containsKey(mapping.get(i).getRcpPmId().get(j))) {
                    phonebookRawId = mapLocalRcpId.get(mapping.get(i).getRcpPmId().get(j)) +
                            "," + mapping.get(i).getLocalPhoneBookId();
                } else {
                    phonebookRawId = mapping.get(i).getLocalPhoneBookId();
                }

                mapLocalRcpId.put(mapping.get(i).getRcpPmId().get(j), phonebookRawId);
            }
//            }
        }

        // Basic Profile Data
        TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());

        ArrayList<UserProfile> arrayListUserProfile = new ArrayList<>();
        for (int i = 0; i < profileData.size(); i++) {

            //<editor-fold desc="Profile Master">
            UserProfile userProfile = new UserProfile();
            userProfile.setPmSuffix(profileData.get(i).getPbNameSuffix());
            userProfile.setPmPrefix(profileData.get(i).getPbNamePrefix());
            userProfile.setPmFirstName(profileData.get(i).getPbNameFirst());
            userProfile.setPmMiddleName(profileData.get(i).getPbNameMiddle());
            userProfile.setPmLastName(profileData.get(i).getPbNameLast());
            userProfile.setPmPhoneticFirstName(profileData.get(i).getPbPhoneticNameFirst());
            userProfile.setPmPhoneticMiddleName(profileData.get(i).getPbPhoneticNameMiddle());
            userProfile.setPmPhoneticLastName(profileData.get(i).getPbPhoneticNameLast());
            userProfile.setPmIsFavourite(profileData.get(i).getIsFavourite());
            userProfile.setPmNotes(profileData.get(i).getPbNote());
            userProfile.setPmNickName(profileData.get(i).getPbNickname());
            userProfile.setPmRcpId(profileData.get(i).getRcpPmId());
            userProfile.setPmNosqlMasterId(profileData.get(i).getNoSqlMasterId());
            userProfile.setProfileRating(profileData.get(i).getProfileRating());
            userProfile.setPmProfileImage(profileData.get(i).getPbProfilePhoto());
            userProfile.setTotalProfileRateUser(profileData.get(i).getTotalProfileRateUser());


            if (mapLocalRcpId.containsKey(profileData.get(i).getRcpPmId())) {
                userProfile.setPmRawId(mapLocalRcpId.get(profileData.get(i).getRcpPmId()));
            }

            String existingRawId = tableProfileMaster.getRawIdFromRcpId(Integer.parseInt
                    (userProfile.getPmRcpId()));
            if (StringUtils.length(existingRawId) <= 0) {

                arrayListUserProfile.add(userProfile);
                tableProfileMaster.addArrayProfile(arrayListUserProfile);
                //</editor-fold>

                //<editor-fold desc="Mobile Master">
                ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = profileData
                        .get(i).getPbPhoneNumber();
                ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
                for (int j = 0; j < arrayListPhoneNumber.size(); j++) {

                    MobileNumber mobileNumber = new MobileNumber();
                    mobileNumber.setMnmRecordIndexId(arrayListPhoneNumber.get(j).getPhoneId());
                    mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(j)
                            .getPhoneNumber());
                    mobileNumber.setMnmNumberType(arrayListPhoneNumber.get(j).getPhoneType());
                    mobileNumber.setMnmNumberPrivacy(String.valueOf(arrayListPhoneNumber.get(j)
                            .getPhonePublic()));
                    mobileNumber.setMnmIsPrivate(arrayListPhoneNumber.get(j).getIsPrivate());
                    mobileNumber.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                    if (StringUtils.equalsIgnoreCase(profileData.get(i).getVerifiedMobileNumber()
                            , mobileNumber.getMnmMobileNumber())) {
                        mobileNumber.setMnmIsPrimary(String.valueOf(IntegerConstants
                                .RCP_TYPE_PRIMARY));
                    } else {
                        mobileNumber.setMnmIsPrimary(String.valueOf(IntegerConstants
                                .RCP_TYPE_SECONDARY));
                    }
                    arrayListMobileNumber.add(mobileNumber);
                }

                TableMobileMaster tableMobileMaster = new TableMobileMaster
                        (getDatabaseHandler());
                tableMobileMaster.addArrayMobileNumber(arrayListMobileNumber);
                //</editor-fold>

                //<editor-fold desc="Email Master">
                if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbEmailId())) {
                    ArrayList<ProfileDataOperationEmail> arrayListEmailId = profileData.get(i)
                            .getPbEmailId();
                    ArrayList<Email> arrayListEmail = new ArrayList<>();
                    for (int j = 0; j < arrayListEmailId.size(); j++) {
                        Email email = new Email();
                        email.setEmEmailAddress(arrayListEmailId.get(j).getEmEmailId());
                        email.setEmRecordIndexId(arrayListEmailId.get(j).getEmId());
                        email.setEmEmailType(arrayListEmailId.get(j).getEmType());
                        email.setEmEmailPrivacy(String.valueOf(arrayListEmailId.get(j)
                                .getEmPublic()));
                        email.setEmIsVerified(arrayListEmailId.get(j).getEmRcpType());
                        email.setEmIsPrivate(arrayListEmailId.get(j).getEmIsPrivate());

                        email.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());

                        if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getVerifiedEmailIds
                                ())) {
                            for (int k = 0; k < profileData.get(i).getVerifiedEmailIds().size();
                                 k++) {
                                if (StringUtils.equalsIgnoreCase(profileData.get(i)
                                        .getVerifiedEmailIds().get(k).getEmEmailId(), email
                                        .getEmEmailAddress())) {
                                    email.setEmIsVerified("1");
                                } else {
                                    email.setEmIsVerified("0");
                                }
                            }
                        }
                        arrayListEmail.add(email);
                    }

                    TableEmailMaster tableEmailMaster = new TableEmailMaster
                            (getDatabaseHandler());
                    tableEmailMaster.addArrayEmail(arrayListEmail);
                }
                //</editor-fold>

                //<editor-fold desc="Organization Master">
                if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbOrganization())) {
                    ArrayList<ProfileDataOperationOrganization> arrayListOrganization =
                            profileData
                                    .get(i).getPbOrganization();
                    ArrayList<Organization> organizationList = new ArrayList<>();
                    for (int j = 0; j < arrayListOrganization.size(); j++) {
                        Organization organization = new Organization();
                        organization.setOmRecordIndexId(arrayListOrganization.get(j).getOrgId
                                ());
                        organization.setOmOrganizationCompany(arrayListOrganization.get(j)
                                .getOrgName
                                        ());
                        organization.setOmOrganizationDesignation(arrayListOrganization.get(j)
                                .getOrgJobTitle());
                        organization.setOmIsCurrent(String.valueOf(arrayListOrganization.get(j)
                                .getIsCurrent()));
                        organization.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        organizationList.add(organization);
                    }

                    TableOrganizationMaster tableOrganizationMaster = new
                            TableOrganizationMaster
                            (getDatabaseHandler());
                    tableOrganizationMaster.addArrayOrganization(organizationList);
                }
                //</editor-fold>

                // <editor-fold desc="Website Master">
                if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbWebAddress())) {
                    ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = profileData
                            .get(i)
                            .getPbWebAddress();
                    ArrayList<Website> websiteList = new ArrayList<>();
                    for (int j = 0; j < arrayListWebsite.size(); j++) {
                        Website website = new Website();
                        website.setWmRecordIndexId(arrayListWebsite.get(j).getWebId());
                        website.setWmWebsiteUrl(arrayListWebsite.get(j).getWebAddress());
                        website.setWmWebsiteType(arrayListWebsite.get(j).getWebType());
                        website.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        websiteList.add(website);
                    }

                    TableWebsiteMaster tableWebsiteMaster = new TableWebsiteMaster
                            (getDatabaseHandler());
                    tableWebsiteMaster.addArrayWebsite(websiteList);
                }
                //</editor-fold>

                //<editor-fold desc="Address Master">
                if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbAddress())) {
                    ArrayList<ProfileDataOperationAddress> arrayListAddress = profileData.get(i)
                            .getPbAddress();
                    ArrayList<Address> addressList = new ArrayList<>();
                    for (int j = 0; j < arrayListAddress.size(); j++) {
                        Address address = new Address();
                        address.setAmRecordIndexId(arrayListAddress.get(j).getAddId());
                        address.setAmCity(arrayListAddress.get(j).getCity());
                        address.setAmCountry(arrayListAddress.get(j).getCountry());
                        address.setAmFormattedAddress(arrayListAddress.get(j)
                                .getFormattedAddress
                                        ());
                        address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                        address.setAmPostCode(arrayListAddress.get(j).getPostCode());
                        address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                        address.setAmStreet(arrayListAddress.get(j).getStreet());
                        address.setAmAddressType(arrayListAddress.get(j).getAddressType());
                        address.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        address.setAmIsPrivate(arrayListAddress.get(j).getIsPrivate());
                        address.setAmAddressPrivacy(String.valueOf(arrayListAddress.get(j)
                                .getAddPublic()));
                        addressList.add(address);
                    }

                    TableAddressMaster tableAddressMaster = new TableAddressMaster
                            (getDatabaseHandler());
                    tableAddressMaster.addArrayAddress(addressList);
                }
                //</editor-fold>

                // <editor-fold desc="Im Account Master">
                if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbIMAccounts())) {
                    ArrayList<ProfileDataOperationImAccount> arrayListImAccount = profileData
                            .get(i)
                            .getPbIMAccounts();
                    ArrayList<ImAccount> imAccountsList = new ArrayList<>();
                    for (int j = 0; j < arrayListImAccount.size(); j++) {
                        ImAccount imAccount = new ImAccount();
                        imAccount.setImRecordIndexId(arrayListImAccount.get(j).getIMId());
//                    imAccount.setImImType(arrayListImAccount.get(j).getIMAccountType());
                        imAccount.setImImProtocol(arrayListImAccount.get(j)
                                .getIMAccountProtocol());
                        imAccount.setImImDetail(arrayListImAccount.get(j)
                                .getIMAccountDetails());
                        imAccount.setImIsPrivate(arrayListImAccount.get(j)
                                .getIMAccountIsPrivate());
                        imAccount.setImImPrivacy(String.valueOf(arrayListImAccount.get(j)
                                .getIMAccountPublic()));
                        imAccount.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        imAccountsList.add(imAccount);
                    }

                    TableImMaster tableImMaster = new TableImMaster(getDatabaseHandler());
                    tableImMaster.addArrayImAccount(imAccountsList);
                }
                //</editor-fold>

                // <editor-fold desc="Event Master">
                if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbEvent())) {
                    ArrayList<ProfileDataOperationEvent> arrayListEvent = profileData.get(i)
                            .getPbEvent();
                    ArrayList<Event> eventList = new ArrayList<>();
                    for (int j = 0; j < arrayListEvent.size(); j++) {
                        Event event = new Event();
                        event.setEvmRecordIndexId(arrayListEvent.get(j).getEventId());
                        event.setEvmStartDate(arrayListEvent.get(j).getEventDateTime());
                        event.setEvmEventType(arrayListEvent.get(j).getEventType());
                        event.setEvmIsPrivate(arrayListEvent.get(j).getIsPrivate());
                        event.setEvmIsYearHidden(arrayListEvent.get(j).getIsYearHidden());
                        event.setEvmEventPrivacy(String.valueOf(arrayListEvent.get(j)
                                .getEventPublic()));
                        event.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        eventList.add(event);
                    }

                    TableEventMaster tableEventMaster = new TableEventMaster
                            (getDatabaseHandler());
                    tableEventMaster.addArrayEvent(eventList);
                }
                //</editor-fold>

            } else {
                if (StringUtils.contains(existingRawId, ",")) {
                    String rawIds[] = existingRawId.split(",");
                    ArrayList<String> arrayListRawIds = new ArrayList<>(Arrays.asList(rawIds));
                    if (arrayListRawIds.contains(mapLocalRcpId.get(profileData.get(i)
                            .getRcpPmId()))) {
                        return;
                    } else {
                        String newRawIds = existingRawId + "," + mapLocalRcpId.get(profileData
                                .get(i).getRcpPmId());
                        tableProfileMaster.updateRawIds(Integer.parseInt(userProfile.getPmRcpId()),
                                newRawIds);
                    }
                } else {
                    if (existingRawId.equals(mapLocalRcpId.get(profileData.get(i)
                            .getRcpPmId())))
                        return;
                    else {
                        String newRawIds = existingRawId + "," + mapLocalRcpId.get(profileData
                                .get(i).getRcpPmId());
                        tableProfileMaster.updateRawIds(Integer.parseInt(userProfile.getPmRcpId()),
                                newRawIds);
                    }
                }
            }
        }
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public String getUserPmId() {
        return Utils.getStringPreference(this, AppConstants.PREF_USER_PM_ID, "0");
    }


    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        try {
            if (error == null && getApplicationContext() != null) {

                //<editor-fold desc="REQ_UPLOAD_CONTACTS">

                if (serviceType.contains(WsConstants.REQ_UPLOAD_CONTACTS)) {
                    WsResponseObject uploadContactResponse = (WsResponseObject) data;
                    if (uploadContactResponse != null && StringUtils.equalsIgnoreCase
                            (uploadContactResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                        lastSyncedData = lastSyncedData + CONTACT_CHUNK;
                        if (getApplicationContext() != null)
                            Utils.setIntegerPreference(getApplicationContext(), AppConstants.PREF_SYNCED_CONTACTS,
                                    lastSyncedData);

                        if (lastSyncedData < arrayListSyncUserContact.size()) {
                            backgroundSync(true, uploadContactResponse);
                        } else {
                            if (!Utils.isArraylistNullOrEmpty(uploadContactResponse
                                    .getArrayListUserRcProfile())) {

                                /* Store Unique Contacts to ProfileMobileMapping */
                                storeToMobileMapping(uploadContactResponse
                                        .getArrayListUserRcProfile());

                                /* Store Unique Emails to ProfileEmailMapping */
                                storeToEmailMapping(uploadContactResponse
                                        .getArrayListUserRcProfile());

                                /* Store Profile Details to respective Table */
                                storeProfileDataToDb(uploadContactResponse
                                        .getArrayListUserRcProfile(), uploadContactResponse
                                        .getArrayListMapping());
                            }
                            if (getApplicationContext() != null) {
//                                Utils.showSuccessSnackBar(getApplicationContext(), relativeRootAllContacts,
//                                        "All Contact Synced");
                                Utils.setStringPreference(getApplicationContext(), AppConstants
                                        .PREF_CONTACT_LAST_SYNC_TIME, String.valueOf(System
                                        .currentTimeMillis() - 10000));
                                Utils.setBooleanPreference(getApplicationContext(), AppConstants
                                        .PREF_CONTACT_SYNCED, true);
                                Log.e(TAG, "All Contact Synced" + System.currentTimeMillis());
                                stopSelf();
                            }
                            phoneBookContacts.saveRawIdsToPref();
                            Intent localBroadcastIntent = new Intent(AppConstants
                                    .ACTION_LOCAL_BROADCAST_CALL_LOG_SYNC);

                            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                                    .getInstance(this);
                            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                        }
                    } else {
                        if (uploadContactResponse != null) {
                            Log.e(TAG, uploadContactResponse.getMessage());
                        } else {
                            Log.e(TAG, "uploadContactResponse null");
//                            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts,
//                                    getString(R
//                                            .string.msg_try_later));
                        }
                    }
                }
                //</editor-fold>

            } else {
                Log.d(TAG, "error::" + (error != null ? error.getLocalizedMessage() : null));
//                Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, "" + (error !=
//                        null ?
//                        error.getLocalizedMessage() : null));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
}
