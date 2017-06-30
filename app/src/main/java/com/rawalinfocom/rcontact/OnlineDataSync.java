package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.util.LongSparseArray;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.ProfileVisit;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OnlineDataSync {

    private Activity activity;
    private PhoneBookContacts phoneBookContacts;
    private ArrayList<ProfileData> arrayListSyncUserContact;
    private String currentStamp;

    public OnlineDataSync(Activity activity) {
        this.activity = activity;
        phoneBookContacts = new PhoneBookContacts(activity);
        syncOfflineProfileViews();
//        if (Utils.getBooleanPreference(context, AppConstants.PREF_CONTACT_SYNCED, false)) {
//            if (Utils.getBooleanPreference(context, AppConstants.PREF_CALL_LOG_SYNCED,
//                    false)) {
//                if (Utils.getBooleanPreference(context, AppConstants.PREF_SMS_SYNCED,
//                        false)) {
//                    AsyncTask.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            syncPhoneBookContactList();
//                        }
//                    });
//
//                }
//            }
//        }
    }

    private void syncOfflineProfileViews() {
        HashMap<String, String> mapProfileViews = new HashMap<>();
        if (Utils.getHashMapPreference(activity, AppConstants
                .PREF_PROFILE_VIEWS) != null) {
            mapProfileViews.putAll(Utils.getHashMapPreference(activity, AppConstants
                    .PREF_PROFILE_VIEWS));

            ArrayList<ProfileVisit> arrayListProfileVisit = new ArrayList<>();
            Iterator iterator = mapProfileViews.entrySet().iterator();
            while (iterator.hasNext()) {
                ProfileVisit profileVisit = new ProfileVisit();
                Map.Entry pair = (Map.Entry) iterator.next();
                profileVisit.setVisitorPmId(Integer.parseInt(pair.getKey().toString()));
                profileVisit.setVisitCount(Integer.parseInt(pair.getValue().toString()));
                iterator.remove();
                arrayListProfileVisit.add(profileVisit);
            }

            WsRequestObject profileVisitObject = new WsRequestObject();
            profileVisitObject.setArrayListProfileVisit(arrayListProfileVisit);

            sendToCloud(profileVisitObject, WsConstants.REQ_ADD_PROFILE_VISIT);

        }
    }

    private void sendToCloud(WsRequestObject requestObject, String requestApi) {
        if (Utils.isNetworkAvailable(activity)) {
            new AsyncWebServiceCall(activity, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    requestObject, null, WsResponseObject.class, requestApi, null, true).execute
                    (WsConstants.WS_ROOT + requestApi);
        }
    }

    private void syncPhoneBookContactList() {
        currentStamp = String.valueOf(System.currentTimeMillis());
        String lastStamp = Utils.getStringPreference(activity, AppConstants
                .PREF_CONTACT_LAST_SYNC_TIME, currentStamp);

        Cursor cursor = phoneBookContacts.getUpdatedRawId(lastStamp);

        if (Utils.getArrayListPreference(activity, AppConstants.PREF_CONTACT_ID_SET) == null)
            return;

        ArrayList<String> rawIdsUpdated = new ArrayList<>();

        while (cursor.moveToNext()) {
            String rawId = cursor.getString(cursor.getColumnIndex
                    (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
            rawIdsUpdated.add(rawId);
        }
        cursor.close();

        Set<String> updatedContactIds = new HashSet<>(rawIdsUpdated);

        ArrayList<String> arrayListContactIds = new ArrayList<>();
        arrayListContactIds.addAll(Utils.getArrayListPreference(activity, AppConstants
                .PREF_CONTACT_ID_SET));

        Cursor contactNameCursor = phoneBookContacts.getAllContactRawId();

        ArrayList<String> arrayListNewContactId = new ArrayList<>();

        while (contactNameCursor.moveToNext()) {
            arrayListNewContactId.add(contactNameCursor.getString(contactNameCursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID)));
        }

        contactNameCursor.close();

        Set<String> oldContactIds = new HashSet<>(arrayListContactIds);
        Set<String> newContactIds = new HashSet<>(arrayListNewContactId);

        Set<String> removedContactIds = new HashSet<>(oldContactIds);
        Set<String> insertedContactIds = new HashSet<>(newContactIds);
        removedContactIds.removeAll(insertedContactIds);
        insertedContactIds.removeAll(oldContactIds);

//        Log.i("MAULIK", "inserted" + insertedContactIds.toString());
//
//        Log.i("MAULIK", "removed" + removedContactIds.toString());

        updatedContactIds.removeAll(insertedContactIds);
//        Log.i("MAULIK", "updated" + updatedContactIds.toString());

        arrayListSyncUserContact = new ArrayList<>();

        if (removedContactIds.size() > 0) {
            //inserted 1
            ArrayList<String> list = new ArrayList<>(removedContactIds);
            prepareForDeletion(list);
        }
        if (updatedContactIds.size() > 0) {
            //updated 6
            ArrayList<String> list = new ArrayList<>(updatedContactIds);
            String inClause = list.toString();
            inClause = inClause.replace("[", "(");
            inClause = inClause.replace("]", ")");
//            Log.i("MAULIK", "updated inCaluse:" + inClause);
            prepareData(IntegerConstants.SYNC_UPDATE_CONTACT, inClause);
        }
        if (insertedContactIds.size() > 0) {
            // deleted 5
            ArrayList<String> list = new ArrayList<>(insertedContactIds);
            String inClause = list.toString();
            inClause = inClause.replace("[", "(");
            inClause = inClause.replace("]", ")");
//            Log.i("MAULIK", "inserted inCaluse:" + inClause);
            prepareData(IntegerConstants.SYNC_INSERT_CONTACT, inClause);

        }
//        Log.i("MAULIK", "arrayListSyncUserContact.size: " + arrayListSyncUserContact.size());
        if (activity != null && Utils.isNetworkAvailable(activity) && arrayListSyncUserContact.size() > 0) {
            uploadContacts();
        }

//        Utils.setArrayListPreference(activity, AppConstants.PREF_CONTACT_ID_SET,
//                saveContactIds);
//        Utils.setStringPreference(activity, AppConstants
//                .PREF_CONTACT_LAST_SYNC_TIME, currentStamp);
    }

    private void prepareData(int flag, String inCaluse) {

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
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?, ?, ?, ?, ?, ?, ?) and " + ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " in " + inCaluse;
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
        //  String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;
        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
        Uri uri = ContactsContract.Data.CONTENT_URI;
//        if (syncingTask != null && syncingTask.isCancelled()) {
//            return;
//        }
        Cursor cursor = activity.getContentResolver().query(uri, projection, selection,
                selectionArgs, sortOrder);
        //</editor-fold>

        //<editor-fold desc="Data Read from Cursor">
        if (cursor != null) {
            final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
            final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);

            while (cursor.moveToNext()) {
//                if (syncingTask != null && syncingTask.isCancelled()) {
//                    return;
//                }
                //ProfileDataOperation operation = new ProfileDataOperation();
                //operation.setFlag(flag);
                long id = cursor.getLong(idIdx);
                ProfileDataOperation phoneBookContact = profileDetailSparseArray.get(id);
                if (phoneBookContact == null) {
                    phoneBookContact = new ProfileDataOperation(id);
                    profileDetailSparseArray.put(id, phoneBookContact);
//                    profileDataList.add(phoneBookContact);
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

                        phoneNumber.setPhoneNumber(Utils.getFormattedNumber(activity, cursor
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

        //<editor-fold desc="Prepare Data">
        for (int i = 0; i < profileDetailSparseArray.size(); i++) {
//            if (syncingTask != null && syncingTask.isCancelled()) {
//                return;
//            }
//            AddressBookContact bookContact = profileDetailSparseArray.valueAt(i);
            ProfileDataOperation profileContact = profileDetailSparseArray.valueAt(i);

            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

            ProfileData profileData = new ProfileData();
            profileData.setLocalPhoneBookId(profileContact.getLookupKey());

            ProfileDataOperation operation = new ProfileDataOperation();

            operation.setFlag(flag);
            //operation.setIsFirst(1);

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


    }

    private void prepareForDeletion(ArrayList<String> list) {
        for (String deletedRawId : list) {
//            Log.i("MAULIK", "actually deleted" + deletedRawId);

            ProfileData profileData = new ProfileData();
            profileData.setLocalPhoneBookId(deletedRawId);

            ArrayList<ProfileDataOperation> arrayListOperations = new ArrayList<>();

            ProfileDataOperation operation = new ProfileDataOperation();
            operation.setFlag(IntegerConstants.SYNC_DELETE_CONTACT);

            arrayListOperations.add(operation);

            profileData.setOperation(arrayListOperations);

            arrayListSyncUserContact.add(profileData);
        }
    }

    private void uploadContacts() {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setPmId(Integer.parseInt(Utils.getStringPreference(activity, AppConstants.PREF_USER_PM_ID, "0")));

        uploadContactObject.setProfileData(arrayListSyncUserContact);
//        Log.i("MAULIK", "uploadContactObject::");
        if (Utils.isNetworkAvailable(activity)) {
            new AsyncWebServiceCall(activity, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS + "_" + currentStamp, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CONTACTS);
        }
    }
}
