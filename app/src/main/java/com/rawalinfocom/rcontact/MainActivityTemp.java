package com.rawalinfocom.rcontact;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.rawalinfocom.rcontact.adapters.AllContactListAdapter;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationRelationship;
import com.rawalinfocom.rcontact.model.UserContact;
import com.rawalinfocom.rcontact.model.UserProfile;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityTemp extends BaseActivity {

    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.relative_root_main)
    RelativeLayout relativeRootMain;

    ArrayList<ProfileData> arrayListUserContact;
    ArrayList<String> arrayListContactJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);
        ButterKnife.bind(this);

//        stopService(new Intent(MainActivityTemp.this, OtpTimerService.class));

        //TODO uncomment
        /*if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT, getResources()
                .getInteger(R.integer.launch_mobile_registration)) == getResources().getInteger(R
                .integer.launch_mobile_registration)) {
            finish();
            startActivityIntent(this, MobileNumberRegistrationActivity.class, null);
        } else if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT,
                getResources().getInteger(R.integer.launch_mobile_registration)) == getResources
                ().getInteger(R.integer.launch_otp_verification)) {
            finish();
            startActivityIntent(this, OtpVerificationActivity.class, null);
        } else if (Utils.getIntegerPreference(this, AppConstants.PREF_LAUNCH_SCREEN_INT,
                getResources().getInteger(R.integer.launch_mobile_registration)) == getResources
                ().getInteger(R.integer.launch_profile_registration)) {
            UserProfile userProfile = (UserProfile) Utils.getObjectPreference(this, AppConstants
                    .PREF_REGS_USER_OBJECT, UserProfile.class);
            if (userProfile != null && StringUtils.equalsIgnoreCase(userProfile
                    .getIsAlreadyVerified(), String.valueOf(getResources().getInteger(R.integer
                    .profile_not_verified)))) {
                finish();
                startActivityIntent(this, ProfileRegistrationActivity.class, null);
            }
        }*/

        arrayListUserContact = new ArrayList<>();

        getContactDetails();

      /*  if (arrayListUserContact.size() > 0) {
            AllContactListAdapter contactAdapter = new AllContactListAdapter
                    (getApplicationContext(), arrayListUserContact);
            recyclerViewContactList.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewContactList.setAdapter(contactAdapter);
        }*/

    }

    public void getContactDetails() {
        Cursor contactNameCursor = getAllContactNames();

        if (contactNameCursor != null && contactNameCursor.getCount() > 0) {
            while (contactNameCursor.moveToNext()) {

                ProfileData profileData = new ProfileData();

                String rawId = contactNameCursor.getString(contactNameCursor
                        .getColumnIndex(ContactsContract.Contacts._ID));

                profileData.setLocalPhonebookId(rawId);

                profileData.setGivenName(contactNameCursor.getString(contactNameCursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                ProfileDataOperation operation = new ProfileDataOperation();

                //<editor-fold desc="Structured Name">
                Cursor contactStructuredNameCursor = getStructuredName(rawId);
                ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

                if (contactStructuredNameCursor != null && contactStructuredNameCursor.getCount()
                        > 0) {

                    operation.setIsFavourite(contactNameCursor.getString(contactNameCursor
                            .getColumnIndex(ContactsContract.Contacts.STARRED)));
                    while (contactStructuredNameCursor.moveToNext()) {

                        operation.setFlag("1");
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
                arrayListOperation.add(operation);
                //</editor-fold>

                //<editor-fold desc="Nick Name">
                Cursor contactNickNameCursor = getContactNickName(rawId);

                if (contactNickNameCursor != null && contactNickNameCursor.getCount() > 0) {
                    while (contactNickNameCursor.moveToNext()) {

                        operation.setPbNickname(contactNickNameCursor.getString
                                (contactNickNameCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Nickname.DATA1)));

                    }
                    contactNickNameCursor.close();
                }
                //</editor-fold>

                //<editor-fold desc="Note">
                Cursor contactNoteCursor = getContactNote(rawId);

                if (contactNoteCursor != null && contactNoteCursor.getCount() > 0) {
                    while (contactNoteCursor.moveToNext()) {

                        operation.setPbNote(contactNoteCursor.getString(contactNoteCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Note.DATA1)));

                    }
                    contactNoteCursor.close();
                }
                //</editor-fold>

                //<editor-fold desc="Profile Image">
               /* Cursor contactPhotoCursor = getContactPhotoUri(contact.getContactRawId());

                if (contactPhotoCursor.getCount() > 0) {
                    while (contactPhotoCursor.moveToNext()) {

                        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts
                                .CONTENT_URI, Long
                                .parseLong(contact.getContactRawId()));

                        contact.setProfileImage(Uri.withAppendedPath(person, ContactsContract
                                .Contacts.Photo.CONTENT_DIRECTORY).toString());

                    }

                }*/
                //</editor-fold>

                //<editor-fold desc="Contact Number">
                Cursor contactNumberCursor = getContactNumbers(rawId);
                ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

                if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                    int i = 0;
                    while (contactNumberCursor.moveToNext()) {

                        ProfileDataOperationPhoneNumber phoneNumber = new
                                ProfileDataOperationPhoneNumber();

                        phoneNumber.setPhoneId(++i);
                        phoneNumber.setPhoneNumber(contactNumberCursor.getString(contactNumberCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        phoneNumber.setPhoneType(getPhoneNumberType(contactNumberCursor.getInt
                                (contactNumberCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Phone.TYPE))));
                        phoneNumber.setPhonePublic(1);

                        arrayListPhoneNumber.add(phoneNumber);

                    }
                    contactNumberCursor.close();
                }
                operation.setPbPhoneNumber(arrayListPhoneNumber);
                //</editor-fold>

                //<editor-fold desc="Email Id">
                Cursor contactEmailCursor = getContactEmail(rawId);
                ArrayList<ProfileDataOperationEmail> arrayListEmailId = new ArrayList<>();

                if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
                    int i = 0;
                    while (contactEmailCursor.moveToNext()) {

                        ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                        emailId.setEmId(++i);
                        emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                        emailId.setEmType(getEmailType(contactEmailCursor, contactEmailCursor.getInt
                                (contactEmailCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Email.TYPE))));
                        emailId.setEmPublic(1);

                        arrayListEmailId.add(emailId);

                    }
                    contactEmailCursor.close();
                }
                operation.setPbEmailId(arrayListEmailId);
                //</editor-fold>

                //<editor-fold desc="Organization">
                Cursor contactOrganizationCursor = getContactOrganization(rawId);
                ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new
                        ArrayList<>();

                if (contactOrganizationCursor != null && contactOrganizationCursor.getCount() > 0) {
                    while (contactOrganizationCursor.moveToNext()) {

                        ProfileDataOperationOrganization organization = new
                                ProfileDataOperationOrganization();

                        organization.setOrgName(contactOrganizationCursor.getString
                                (contactOrganizationCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.COMPANY)));
                        organization.setOrgJobTitle(contactOrganizationCursor.getString
                                (contactOrganizationCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.TITLE)));
                        organization.setOrgDepartment(contactOrganizationCursor.getString
                                (contactOrganizationCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.DEPARTMENT)));
                        organization.setOrgType(getOrganizationType(contactOrganizationCursor,
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

                //<editor-fold desc="Relation">
                Cursor contactRelationCursor = getContactRelationShip(rawId);
                ArrayList<ProfileDataOperationRelationship> arrayListRelationship = new
                        ArrayList<>();

                if (contactRelationCursor != null && contactRelationCursor.getCount() > 0) {
                    while (contactRelationCursor.moveToNext()) {

                        ProfileDataOperationRelationship relationship = new
                                ProfileDataOperationRelationship();

                        relationship.setRelationshipDetails(contactRelationCursor.getString
                                (contactRelationCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Relation.NAME)));
                        relationship.setRelationshipType(getRelationType(contactRelationCursor,
                                contactRelationCursor.getInt((contactRelationCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Relation
                                                .TYPE)))));
                        relationship.setRelationshipPublic("1");

                        arrayListRelationship.add(relationship);

                    }
                    contactRelationCursor.close();
                }

                operation.setPbRelationship(arrayListRelationship);
                //</editor-fold>

                //<editor-fold desc="Address">
                Cursor contactAddressCursor = getContactAddress(rawId);
                ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();

                if (contactAddressCursor != null && contactAddressCursor.getCount() > 0) {
                    while (contactAddressCursor.moveToNext()) {

                        ProfileDataOperationAddress address = new ProfileDataOperationAddress();

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
                        address.setAddressType(getAddressType(contactAddressCursor,
                                contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));

                        arrayListAddress.add(address);

                    }
                    contactAddressCursor.close();
                }

                operation.setPbAddress(arrayListAddress);
                //</editor-fold>

                //<editor-fold desc="Website">
                Cursor contactWebsiteCursor = getContactWebsite(rawId);
                ArrayList<String> arrayListWebsite = new ArrayList<>();

                if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
                    while (contactWebsiteCursor.moveToNext()) {

                        String website = contactWebsiteCursor.getString(contactWebsiteCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));

                        arrayListWebsite.add(website);

                    }
                    contactWebsiteCursor.close();
                }

                operation.setPbWebAddress(arrayListWebsite);
                //</editor-fold>

                //<editor-fold desc="IM Account">
                Cursor contactImCursor = getContactIm(rawId);
                ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();

                if (contactImCursor != null && contactImCursor.getCount() > 0) {
                    while (contactImCursor.moveToNext()) {

                        ProfileDataOperationImAccount imAccount = new
                                ProfileDataOperationImAccount();

                        imAccount.setIMAccountDetails(contactImCursor.getString(contactImCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                        imAccount.setIMAccountType(getImAccountType(contactImCursor,
                                contactImCursor.getInt(contactImCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Im.TYPE))));

                        imAccount.setIMAccountProtocol(getImProtocol(contactImCursor.getInt(
                                (contactImCursor.getColumnIndex(ContactsContract.CommonDataKinds
                                        .Im.PROTOCOL)))));

                        imAccount.setIMAccountPublic("1");


                        arrayListImAccount.add(imAccount);

                    }
                    contactImCursor.close();
                }

                operation.setPbIMAccounts(arrayListImAccount);
                //</editor-fold>

                //<editor-fold desc="Event">
                Cursor contactEventCursor = getContactEvent(rawId);
                ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();

                if (contactEventCursor != null && contactEventCursor.getCount() > 0) {
                    while (contactEventCursor.moveToNext()) {

                        ProfileDataOperationEvent event = new ProfileDataOperationEvent();

                        event.setEventType(getEventType(contactEventCursor, contactEventCursor
                                .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Event.TYPE))));

                        event.setEventDate(contactEventCursor.getString(contactEventCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                        .START_DATE)));

                        event.setEventPublic("1");

                        arrayListEvent.add(event);

                    }
                    contactEventCursor.close();
                }

                operation.setPbEvent(arrayListEvent);
                //</editor-fold>

                profileData.setOperation(arrayListOperation);

                arrayListUserContact.add(profileData);
              /*  Gson gson = new Gson();
                String json = gson.toJson(profileData);
                Log.i("JSON String :" + operation.getPbNameFirst(), json);*/

            }
            contactNameCursor.close();

        }

        arrayListContactJson = new ArrayList<>();
        for (int i = 0; i < arrayListUserContact.size(); i++) {
            Gson gson = new Gson();
            String json = gson.toJson(arrayListUserContact.get(i));
            Log.i("JSON String " + i, json);
            arrayListContactJson.add(json);
        }
    }

    public Cursor getAllContactNames() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.STARRED,
        };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '1'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

//        return getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        return getContentResolver().query(uri, projection, null, null, sortOrder);
    }

    public Cursor getContactNumbers(String contactId) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };

        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getStructuredName(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.StructuredName.PREFIX,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME,
        };

        String selection = ContactsContract.Data.MIMETYPE + " = '" +
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID
                + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactEmail(String contactId) {
        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Email._ID,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.LABEL,
        };

        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactOrganization(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Organization.COMPANY,
                ContactsContract.CommonDataKinds.Organization.TITLE,
                ContactsContract.CommonDataKinds.Organization.TYPE,
                ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
                ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION,
                ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION,
                ContactsContract.CommonDataKinds.Organization.LABEL,
        };

        String selection = ContactsContract.CommonDataKinds.Organization.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Organization.CONTENT_ITEM_TYPE};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactRelationShip(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Relation.NAME,
                ContactsContract.CommonDataKinds.Relation.TYPE,
                ContactsContract.CommonDataKinds.Relation.LABEL,
        };

        String selection = ContactsContract.CommonDataKinds.Relation.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Relation.CONTENT_ITEM_TYPE};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactAddress(String contactId) {
        Uri uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.StructuredPostal._ID,
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD,
                ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
                ContactsContract.CommonDataKinds.StructuredPostal.REGION,
                ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                ContactsContract.CommonDataKinds.StructuredPostal.LABEL,
        };

        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactEvent(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.LABEL,
                ContactsContract.CommonDataKinds.Event.START_DATE,
        };

        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Event.CONTENT_ITEM_TYPE};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactWebsite(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Website.TYPE,
                ContactsContract.CommonDataKinds.Website.URL,
        };

        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Website.CONTENT_ITEM_TYPE};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactNickName(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Nickname.DATA1,
        };

        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Nickname.CONTENT_ITEM_TYPE};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactNote(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Note.DATA1,
        };

        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Note.CONTENT_ITEM_TYPE};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactIm(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Im.TYPE,
                ContactsContract.CommonDataKinds.Im.LABEL,
                ContactsContract.CommonDataKinds.Im.DATA1,
                ContactsContract.CommonDataKinds.Im.PROTOCOL,
        };

        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Im.CONTENT_ITEM_TYPE};

        return getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor getContactPhotoUri(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;

        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Photo.CONTENT_ITEM_TYPE};

        return getContentResolver().query(uri, null, selection, selectionArgs, null);
    }

    public String getPhoneNumberType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return "Home";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "Mobile";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "Work";

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return "Fax Work";

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                return "Fax Home";

            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return "Pager";

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return "Callback";

            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return "Car";

            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                return "Company Main";

            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                return "ISDN";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return "Main";

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                return "Other Fax";

            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                return "Radio";

            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                return "Telex";

            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                return "Tty Tdd";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return "Work Mobile";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                return "Work Pager";

            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return "Assistant";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                return "MMS";

        }
        return "Other";
    }

    public String getRelationType(Cursor cursor, int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Relation.TYPE_ASSISTANT:
                return "Assistant";

            case ContactsContract.CommonDataKinds.Relation.TYPE_BROTHER:
                return "Brother";

            case ContactsContract.CommonDataKinds.Relation.TYPE_CHILD:
                return "Child";

            case ContactsContract.CommonDataKinds.Relation.TYPE_DOMESTIC_PARTNER:
                return "Domestic Partner";

            case ContactsContract.CommonDataKinds.Relation.TYPE_FATHER:
                return "Father";

            case ContactsContract.CommonDataKinds.Relation.TYPE_FRIEND:
                return "Friend";

            case ContactsContract.CommonDataKinds.Relation.TYPE_MANAGER:
                return "Manager";

            case ContactsContract.CommonDataKinds.Relation.TYPE_MOTHER:
                return "Mother";

            case ContactsContract.CommonDataKinds.Relation.TYPE_PARENT:
                return "Parent";

            case ContactsContract.CommonDataKinds.Relation.TYPE_PARTNER:
                return "Partner";

            case ContactsContract.CommonDataKinds.Relation.TYPE_REFERRED_BY:
                return "Referred By";

            case ContactsContract.CommonDataKinds.Relation.TYPE_RELATIVE:
                return "Relative";

            case ContactsContract.CommonDataKinds.Relation.TYPE_SISTER:
                return "Sister";

            case ContactsContract.CommonDataKinds.Relation.TYPE_SPOUSE:
                return "Spouse";

            case ContactsContract.CommonDataKinds.Relation.TYPE_CUSTOM:
                return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                        .Relation.LABEL));

        }
        return "Other";
    }

    public String getEmailType(Cursor cursor, int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                return "Home";

            case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                return "Mobile";

            case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                return "Work";

            case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM:
                return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                        .Email.LABEL));
        }
        return "Other";
    }

    public String getOrganizationType(Cursor cursor, int type) {
        switch (type) {

            case ContactsContract.CommonDataKinds.Organization.TYPE_WORK:
                return "Home";

            case ContactsContract.CommonDataKinds.Organization.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM:
                return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                        .Organization.LABEL));
        }
        return "Other";
    }

    public String getAddressType(Cursor cursor, int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                return "Home";

            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                return "Work";

            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM:
                return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                        .StructuredPostal.LABEL));
        }
        return "Other";
    }

    public String getImAccountType(Cursor cursor, int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                return "Home";

            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                return "Work";

            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM:
                return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                        .StructuredPostal.LABEL));
        }
        return "Other";
    }

    public String getImProtocol(int protocol) {
        switch (protocol) {
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_AIM:
                return "AIM";

            case ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN:
                return "MSN";

            case ContactsContract.CommonDataKinds.Im.PROTOCOL_YAHOO:
                return "Yahoo";

            case ContactsContract.CommonDataKinds.Im.PROTOCOL_SKYPE:
                return "Skype";

            case ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ:
                return "QQ";

            case ContactsContract.CommonDataKinds.Im.PROTOCOL_GOOGLE_TALK:
                return "Google Talk";

            case ContactsContract.CommonDataKinds.Im.PROTOCOL_ICQ:
                return "ICQ";

            case ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER:
                return "Jabber";

            case ContactsContract.CommonDataKinds.Im.PROTOCOL_NETMEETING:
                return "NetMeeting";

            case 9:
                return "WhatsApp";

            case 10:
                return "Facebook";

            case ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM:
                return ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL;
        }
        return "Other";
    }

    public String getEventType(Cursor cursor, int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY:
                return "Anniversary";

            case ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY:
                return "Birthday";

            case ContactsContract.CommonDataKinds.Event.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM:
                return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                        .Event.LABEL));
        }
        return "Other";
    }

    public String getWebsiteType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Website.TYPE_HOME:
                return "Home";

            case ContactsContract.CommonDataKinds.Website.TYPE_WORK:
                return "Work";

            case ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE:
                return "Homepage";

            case ContactsContract.CommonDataKinds.Website.TYPE_BLOG:
                return "Blog";

            case ContactsContract.CommonDataKinds.Website.TYPE_PROFILE:
                return "Profile";

            case ContactsContract.CommonDataKinds.Website.TYPE_FTP:
                return "FTP";

            case ContactsContract.CommonDataKinds.Website.TYPE_OTHER:
                return "Other";
        }
        return "Other";
    }

    private void getAllContacts() {
        ArrayList<UserContact> contactVOList = new ArrayList<>();
        UserContact userContact;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null,
                null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex
                        (ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts
                            ._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract
                            .Contacts.DISPLAY_NAME));

                    userContact = new UserContact();
                    UserProfile userProfile = new UserProfile();
                    userProfile.setPmFirstName(name);
                    userContact.setUserProfile(userProfile);

                    ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        MobileNumber mobileNumber = new MobileNumber();
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mobileNumber.setMnmMobileNumber(phoneNumber);
                        arrayListMobileNumber.add(mobileNumber);
                    }

                    userContact.setArrayListMobileNumber(arrayListMobileNumber);

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    contactVOList.add(userContact);
                }
            }

            AllContactListAdapter contactAdapter = new AllContactListAdapter
                    (getApplicationContext(), contactVOList);
            recyclerViewContactList.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewContactList.setAdapter(contactAdapter);
        }
    }


}
