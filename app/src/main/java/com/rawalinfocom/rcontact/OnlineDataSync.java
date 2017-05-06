package com.rawalinfocom.rcontact;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

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
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Monal on 01/03/17.
 */

public class OnlineDataSync {

    private Context context;
    private PhoneBookContacts phoneBookContacts;
    private ArrayList<ProfileData> arrayListUserContact;
    private ArrayList<ProfileData> arrayListDeletedUserContact;
    String currentStamp;

    public OnlineDataSync(Context context) {
        this.context = context;
        phoneBookContacts = new PhoneBookContacts(context);
        arrayListDeletedUserContact = new ArrayList<>();
        syncOfflineProfileViews();
        if (Utils.getBooleanPreference(context, AppConstants.PREF_CONTACT_SYNCED, false)) {
            if (Utils.getBooleanPreference(context, AppConstants.PREF_CALL_LOG_SYNCED,
                    false)) {
                if (Utils.getBooleanPreference(context, AppConstants.PREF_SMS_SYNCED,
                        true)) {
                    //Add SMS boolean default shoiuld be false
                    // Start uodated contact sync after above 3 conditions are true

//                    AsyncTask.execute(new Runnable() {
//                        @Override
//                        public void run() {
////                            Log.i("MAULIK", "Checking for updated contacts");
//                            syncPhoneBookContactList();
//                        }
//                    });

                }
            }
        }


    }

    private void syncOfflineProfileViews() {
        HashMap<String, String> mapProfileViews = new HashMap<>();
        if (Utils.getHashMapPreference(context, AppConstants
                .PREF_PROFILE_VIEWS) != null) {
            mapProfileViews.putAll(Utils.getHashMapPreference(context, AppConstants
                    .PREF_PROFILE_VIEWS));

            ArrayList<ProfileVisit> arrayListProfileVisit = new ArrayList<>();
            Iterator iterator = mapProfileViews.entrySet().iterator();
            while (iterator.hasNext()) {
                ProfileVisit profileVisit = new ProfileVisit();
                Map.Entry pair = (Map.Entry) iterator.next();
                profileVisit.setVisitorPmId(Integer.parseInt(pair.getKey().toString()));
                profileVisit.setVisitCount(Integer.parseInt(pair.getValue().toString()));
                iterator.remove(); // avoids a ConcurrentModificationException
                arrayListProfileVisit.add(profileVisit);
            }

            WsRequestObject profileVisitObject = new WsRequestObject();
            profileVisitObject.setArrayListProfileVisit(arrayListProfileVisit);

            sendToCloud(profileVisitObject, WsConstants.REQ_ADD_PROFILE_VISIT);

        }
    }

    private void syncPhoneBookContactList() {
        currentStamp = String.valueOf(System.currentTimeMillis());
        String lastStamp = Utils.getStringPreference(context, AppConstants
                .PREF_CONTACT_LAST_SYNC_TIME, currentStamp);

        Cursor cursor = phoneBookContacts.getUpdatedContacts(lastStamp);
        arrayListUserContact = new ArrayList<>();
//        Log.i("MAULIK", "currenstamp" + currentStamp);
//        Log.i("MAULIK", "lastStamp" + lastStamp);

        if (Utils.getArrayListPreference(context, AppConstants.PREF_CONTACT_ID_SET) == null)
            return;
        if (cursor != null) {
            if (cursor.getCount() == 0) {
//                Log.i("MAULIK", "returning since no changes found");
                return;
            } else {
//                Log.i("MAULIK", "changes found" + cursor.getCount());
            }
        }
        String rawId = "-1";

        ArrayList<String> rawIdsUpdated = new ArrayList<>();

        while (cursor.moveToNext()) {
            rawId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts
                    .LOOKUP_KEY));
//            Log.i("MAULIK", "LOOKUP_KEY" + rawId);
//            Log.i("MAULIK", "DISPLAY_NAME" + cursor.getString(cursor.getColumnIndex
// (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
//            Log.i("MAULIK", "NUMBER" + cursor.getString(cursor.getColumnIndex(ContactsContract
// .CommonDataKinds.Phone.NUMBER)));
//            Log.i("MAULIK", "RAW_CONTACT_ID" + cursor.getString(cursor.getColumnIndex
// (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID)));
//            Log.i("MAULIK", "CONTACT_ID" + cursor.getString(cursor.getColumnIndex
// (ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
//            Log.i("MAULIK", "Contacts._ID" + cursor.getString(cursor.getColumnIndex
// (ContactsContract.Contacts._ID)));
            rawIdsUpdated.add(rawId);
        }
        cursor.close();
//        Log.i("MAULIK", "updatedRawIds" + rawIdsUpdated.size());
//        Log.i("MAULIK", "updatedRawIds" + rawIdsUpdated.toString());

        ArrayList<String> arrayListContactIds = new ArrayList<>();
        arrayListContactIds.addAll(Utils.getArrayListPreference(context, AppConstants
                .PREF_CONTACT_ID_SET));
//        Log.i("MAULIK", "arrayListContactIds ALL" + arrayListContactIds.toString());
//        Log.i("MAULIK", "arrayListContactIds SIZE" + arrayListContactIds.size());

        for (int i = 0; i < rawIdsUpdated.size(); i++) {

            String _rawId = rawIdsUpdated.get(i);
//            Log.i("MAULIK", "_rawId" + _rawId);
            if (arrayListContactIds.contains(_rawId)) {
//                Log.i("MAULIK", "update" + _rawId);
                // Update
                phoneBookOperations(_rawId, String.valueOf(IntegerConstants.SYNC_UPDATE_CONTACT));

            } else if (_rawId.equalsIgnoreCase("-1")) {
//                Log.i("MAULIK", "delete" + _rawId);
//
//                // Delete
//                Log.i("MAULIK", "delete" + _rawId);
//
//                Cursor contactNameCursor = phoneBookContacts.getAllContactId();
//
//                ArrayList<String> arrayListNewContactId = new ArrayList<>();
//
//                while (contactNameCursor.moveToNext()) {
//                    arrayListNewContactId.add(contactNameCursor.getString(contactNameCursor
//                            .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)));
//                }
//
//                contactNameCursor.close();
//                Log.i("MAULIK", "arrayListNewContactId" + arrayListNewContactId.toString());
//                Utils.setArrayListPreference(context, AppConstants.PREF_CONTACT_ID_SET,
//                        arrayListNewContactId);
//
//                Set<String> oldContactIds = new HashSet<>(arrayListContactIds);
//                Set<String> newContactIds = new HashSet<>(arrayListNewContactId);
//                oldContactIds.removeAll(newContactIds);
//                ArrayList<String> arrayListDeletedIds = new ArrayList<>(oldContactIds);

//                for (String deletedRawId : arrayListDeletedIds) {
//                    Log.i("MAULIK", "actually deleted" + _rawId);
//                    ProfileData profileData = new ProfileData();
//                    profileData.setLocalPhoneBookId(arrayListDeletedIds.get(0));
//
//                    ArrayList<ProfileDataOperation> arrayListOperations = new ArrayList<>();
//                    ProfileDataOperation profileDataOperation = new ProfileDataOperation();
//                    profileDataOperation.setFlag(context.getResources().getInteger(R.integer
//                            .sync_delete));
//                    arrayListOperations.add(profileDataOperation);
//
//                    profileData.setOperation(arrayListOperations);
//
//                    arrayListDeletedUserContact.add(profileData);
//                    Log.i("MAULIK", "arrayListDeletedUserContact" + arrayListDeletedUserContact
// .toString());
//                    Log.i("MAULIK", "arrayListDeletedUserContact" + arrayListDeletedUserContact
// .size());

//                    deleteContact();
                // Update
//                    phoneBookOperations(deletedRawId, String.valueOf(context.getResources()
// .getInteger(R
//                            .integer.sync_delete)));
//                }

            } else {
                // Insert
                if (!arrayListContactIds.contains(_rawId)) {
//                    Log.i("MAULIK", "insert" + _rawId);
                    phoneBookOperations(_rawId, String.valueOf(IntegerConstants
                            .SYNC_INSERT_CONTACT));
                }

            }
        }
        updateSavedIdsArray();

        uploadContacts();
    }

    private void updateSavedIdsArray() {
        Cursor contactNameCursor = phoneBookContacts.getAllContactId();

        ArrayList<String> arrayListNewContactId = new ArrayList<>();

        while (contactNameCursor.moveToNext()) {
            arrayListNewContactId.add(contactNameCursor.getString(contactNameCursor
                    .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)));
        }

        contactNameCursor.close();
        Utils.setArrayListPreference(context, AppConstants.PREF_CONTACT_ID_SET,
                arrayListNewContactId);
//        Log.i("MAULIK", "arrayListNewContactId ALL" + arrayListNewContactId.toString());
//        Log.i("MAULIK", "arrayListNewContactId SIZE" + arrayListNewContactId.size());
    }

    private void sendToCloud(WsRequestObject requestObject, String requestApi) {
        if (Utils.isNetworkAvailable(context)) {
            new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    requestObject, null, WsResponseObject.class, requestApi, null, true).execute
                    (WsConstants.WS_ROOT + requestApi);
        }
    }

    private void phoneBookOperations(String rawId, String flag) {


//        for (int i = forFrom; i < forTo; i++) {

        ProfileData profileData = new ProfileData();

        profileData.setLocalPhoneBookId(rawId);

        ProfileDataOperation operation = new ProfileDataOperation();
        operation.setFlag(Integer.parseInt(flag));

        //<editor-fold desc="Structured Name">
        Cursor contactStructuredNameCursor = phoneBookContacts.getStructuredName(rawId);
        ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

        if (contactStructuredNameCursor != null && contactStructuredNameCursor.getCount() > 0) {

            while (contactStructuredNameCursor.moveToNext()) {

                operation.setPbNamePrefix(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.PREFIX)));
                operation.setPbNameFirst(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.GIVEN_NAME)));
                operation.setPbNameMiddle(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.MIDDLE_NAME)));
                operation.setPbNameLast(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.FAMILY_NAME)));
                operation.setPbNameSuffix(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.SUFFIX)));
                operation.setPbPhoneticNameFirst(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME)));
                operation.setPbPhoneticNameMiddle(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME)));
                operation.setPbPhoneticNameLast(contactStructuredNameCursor.getString
                        (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME)));

            }
            contactStructuredNameCursor.close();
        }
//                arrayListOperation.add(operation);
        //</editor-fold>

        // <editor-fold desc="Starred Contact">
        Cursor starredContactCursor = phoneBookContacts.getStarredStatus(rawId);

        if (starredContactCursor != null && starredContactCursor.getCount() > 0) {

            if (starredContactCursor.moveToNext()) {
                String isFavourite = starredContactCursor.getString(starredContactCursor
                        .getColumnIndex(ContactsContract.Contacts.STARRED));
                operation.setIsFavourite(isFavourite);
            }
            starredContactCursor.close();
        }
        //</editor-fold>

        //<editor-fold desc="Contact Number">
        Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(rawId);
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

        if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
            int numberCount = 0;
            while (contactNumberCursor.moveToNext()) {

                ProfileDataOperationPhoneNumber phoneNumber = new
                        ProfileDataOperationPhoneNumber();

                phoneNumber.setPhoneId(String.valueOf(++numberCount));
                phoneNumber.setPhoneNumber(Utils.getFormattedNumber(context,
                        contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER))));
                phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                        (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.TYPE))));
                phoneNumber.setPhonePublic(1);

                arrayListPhoneNumber.add(phoneNumber);

            }
            contactNumberCursor.close();
        }
        operation.setPbPhoneNumber(arrayListPhoneNumber);
        //</editor-fold>

        //<editor-fold desc="Email Id">
        Cursor contactEmailCursor = phoneBookContacts.getContactEmail(rawId);
        ArrayList<ProfileDataOperationEmail> arrayListEmailId = new ArrayList<>();

        if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
            int emailCount = 0;
            while (contactEmailCursor.moveToNext()) {

                ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                emailId.setEmId(String.valueOf(++emailCount));
                emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                        contactEmailCursor.getInt
                                (contactEmailCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Email.TYPE))));
                emailId.setEmPublic(1);

                arrayListEmailId.add(emailId);

            }
            contactEmailCursor.close();
        }
        operation.setPbEmailId(arrayListEmailId);
        //</editor-fold>

        //<editor-fold desc="Website">
        Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(rawId);
        ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();

        if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
            int websiteCount = 0;
            while (contactWebsiteCursor.moveToNext()) {

                ProfileDataOperationWebAddress webAddress = new
                        ProfileDataOperationWebAddress();

                webAddress.setWebId(String.valueOf(++websiteCount));
                webAddress.setWebAddress(contactWebsiteCursor.getString(contactWebsiteCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                webAddress.setWebType(phoneBookContacts.getWebsiteType(contactWebsiteCursor,
                        (contactWebsiteCursor.getInt(contactWebsiteCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Website.TYPE)))));

                arrayListWebsite.add(webAddress);

            }
            contactWebsiteCursor.close();
        }

        operation.setPbWebAddress(arrayListWebsite);
        //</editor-fold>

        //<editor-fold desc="Organization">
        Cursor contactOrganizationCursor = phoneBookContacts.getContactOrganization(rawId);
        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new
                ArrayList<>();

        if (contactOrganizationCursor != null && contactOrganizationCursor.getCount() > 0) {

            int organizationCount = 0;

            while (contactOrganizationCursor.moveToNext()) {

                ProfileDataOperationOrganization organization = new
                        ProfileDataOperationOrganization();

                organization.setOrgId(String.valueOf(++organizationCount));
                organization.setOrgName(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.COMPANY)));
                organization.setOrgJobTitle(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.TITLE)));
                organization.setOrgDepartment(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.DEPARTMENT)));
                organization.setOrgType(phoneBookContacts.getOrganizationType
                        (contactOrganizationCursor,
                                contactOrganizationCursor.getInt((contactOrganizationCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .Organization.TYPE)))));
                organization.setOrgJobDescription(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                organization.setOrgOfficeLocation(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.OFFICE_LOCATION)));

                arrayListOrganization.add(organization);

            }
            contactOrganizationCursor.close();
        }

        operation.setPbOrganization(arrayListOrganization);
        //</editor-fold>

        //<editor-fold desc="Address">
        Cursor contactAddressCursor = phoneBookContacts.getContactAddress(rawId);
        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();

        if (contactAddressCursor != null && contactAddressCursor.getCount() > 0) {
            int addressCount = 0;
            while (contactAddressCursor.moveToNext()) {

                ProfileDataOperationAddress address = new ProfileDataOperationAddress();

                address.setAddId(String.valueOf(++addressCount));
                address.setFormattedAddress(contactAddressCursor.getString
                        (contactAddressCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                address.setCity(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .CITY)));
                address.setCountry(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .COUNTRY)));
                address.setNeighborhood(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .NEIGHBORHOOD)));
                address.setPostCode(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POSTCODE)));
                address.setPoBox(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POBOX)));
                address.setStreet(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .STREET)));
                address.setAddressType(phoneBookContacts.getAddressType(contactAddressCursor,
                        contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));
                address.setAddPublic(1);


                arrayListAddress.add(address);

            }
            contactAddressCursor.close();
        }

        operation.setPbAddress(arrayListAddress);
        //</editor-fold>

        //<editor-fold desc="IM Account">
        Cursor contactImCursor = phoneBookContacts.getContactIm(rawId);
        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();

        if (contactImCursor != null && contactImCursor.getCount() > 0) {

            int imCount = 0;
            while (contactImCursor.moveToNext()) {

                ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();

                imAccount.setIMId(String.valueOf(++imCount));
                imAccount.setIMAccountDetails(contactImCursor.getString(contactImCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                imAccount.setIMAccountType(phoneBookContacts.getImAccountType(contactImCursor,
                        contactImCursor.getInt(contactImCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Im.TYPE))));

                imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                        (contactImCursor.getInt((contactImCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Im.PROTOCOL)))));

                imAccount.setIMAccountPublic(1);


                arrayListImAccount.add(imAccount);

            }
            contactImCursor.close();
        }

        operation.setPbIMAccounts(arrayListImAccount);
        //</editor-fold>

        //<editor-fold desc="Event">
        Cursor contactEventCursor = phoneBookContacts.getContactEvent(rawId);
        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();

        if (contactEventCursor != null && contactEventCursor.getCount() > 0) {
            int eventCount = 0;
            while (contactEventCursor.moveToNext()) {

                ProfileDataOperationEvent event = new ProfileDataOperationEvent();

                event.setEventId(String.valueOf(++eventCount));
                event.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                        contactEventCursor
                                .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Event.TYPE))));

                String eventDate = contactEventCursor.getString(contactEventCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                .START_DATE));

                if (StringUtils.startsWith(eventDate, "--")) {
                    eventDate = "1900" + eventDate.substring(1, StringUtils.length(eventDate));
                }

                event.setEventDateTime(eventDate);

                event.setEventPublic(1);

                arrayListEvent.add(event);

            }
            contactEventCursor.close();
        }

        operation.setPbEvent(arrayListEvent);
        //</editor-fold>

        arrayListOperation.add(operation);
        profileData.setOperation(arrayListOperation);

        arrayListUserContact.add(profileData);

    }

    private void uploadContacts() {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setProfileData(arrayListUserContact);

        if (Utils.isNetworkAvailable(context)) {
            new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS + "_" + currentStamp, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CONTACTS);
        }
    }

//    private void deleteContact() {
//
//        WsRequestObject uploadContactObject = new WsRequestObject();
//        uploadContactObject.setProfileData(arrayListDeletedUserContact);
//
//        if (Utils.isNetworkAvailable(context)) {
//            new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
//                    uploadContactObject, null, WsResponseObject.class, WsConstants
//                    .REQ_UPLOAD_CONTACTS, null, true).execute
//                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CONTACTS);
//        }
//    }
}
