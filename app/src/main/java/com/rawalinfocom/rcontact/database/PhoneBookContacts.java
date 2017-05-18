package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.rawalinfocom.rcontact.helper.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by user on 26/12/16.
 */

public class PhoneBookContacts {

    public static final int STATUS_FAVOURITE = 1;
    public static final int STATUS_UN_FAVOURITE = 0;

    private Context context;

    public PhoneBookContacts(Context context) {
        this.context = context;
    }

    //<editor-fold desc="Phone book Data Cursor">

    public Cursor getAllContactId() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
//                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
        };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '1'";
        String[] selectionArgs = null;
//        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";

       /* return getContentResolver().query(uri, projection, ContactsContract.RawContacts
                .ACCOUNT_TYPE + " <> 'com.android.contacts.sim' "
                + " AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'com.google' ",
                null, sortOrder);*/
        return context.getContentResolver().query(uri, projection, null, null, sortOrder);
    }

    public Cursor getStarredStatus(String contactId) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts.STARRED,
        };

//        String selection = ContactsContract.Contacts._ID + " = ?";
        String selection = ContactsContract.Contacts.LOOKUP_KEY + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public boolean getStarredStatusFromRawId(String rawId) {
        int count = 0;
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts.STARRED,
        };

        String selection = ContactsContract.Contacts.LOOKUP_KEY + " IN (?) AND starred = ?";
        String[] selectionArgs = new String[]{rawId, "1"};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count > 0;

    }

    public Cursor getStarredContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
//                ContactsContract.Contacts._ID,
//                ContactsContract.Contacts.STARRED,

                ContactsContract.Contacts.LOOKUP_KEY,

//                ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY,
        };

        String selection = "starred = ?";
        String[] selectionArgs = new String[]{"1"};
//        String sortOrder = "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC";
        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
//        String sortOrder = "UPPER(" + ContactsContract.CommonDataKinds.StructuredName
// .GIVEN_NAME + ") ASC";

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, sortOrder);
    }

    public Cursor getAllContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
//                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
        };

//        String selection = "starred = ?";
//        String[] selectionArgs = new String[]{"1"};
        String sortOrder = "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC";

        return context.getContentResolver().query(uri, projection, null, null, sortOrder);
    }

    /*public Cursor getStructuredName(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
//                ContactsContract.CommonDataKinds.StructuredName._ID,
                ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY,
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
//                ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID
                ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY
                + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    } */

    public String getStructuredName(String contactId) {
        String contactDisplayName = "";
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
//                ContactsContract.CommonDataKinds.StructuredName._ID,
                ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.StructuredName.PREFIX,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
        };

        String selection = ContactsContract.Data.MIMETYPE + " = '" +
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "' AND " +
//                ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID
                ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY
                + " = ?";
        String[] selectionArgs = new String[]{contactId};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String prefix = cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.StructuredName.PREFIX));
                String firstName = cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.StructuredName.GIVEN_NAME));
                String lastName = cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.StructuredName.FAMILY_NAME));
                String middleName = cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.StructuredName.MIDDLE_NAME));
                String suffix = cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.StructuredName.SUFFIX));

                if (StringUtils.length(prefix) > 0) {
                    contactDisplayName = prefix + " ";
                }
                if (StringUtils.length(firstName) > 0) {
                    contactDisplayName = contactDisplayName + firstName + " ";
                }
                if (StringUtils.length(middleName) > 0) {
                    contactDisplayName = contactDisplayName + middleName + " ";
                }
                if (StringUtils.length(lastName) > 0) {
                    contactDisplayName = contactDisplayName + lastName + " ";
                }
                if (StringUtils.length(suffix) > 0) {
                    contactDisplayName = contactDisplayName + suffix;
                }
                contactDisplayName = StringUtils.trimToEmpty(contactDisplayName);

            }
            cursor.close();
        }
      /*  return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);*/
        return contactDisplayName;
    }

    public Cursor getContactNumbers(String contactId) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
//                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };

//        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public boolean getIfContactNumbersExists(String contactId, String contactNumber) {
        ArrayList<String> arrayListNumbers = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
//                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };

//        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
        String[] selectionArgs = new String[]{contactId};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    arrayListNumbers.add(Utils.getFormattedNumber(context, cursor.getString
                            (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return arrayListNumbers.contains(contactNumber);
    }

    public Cursor getContactNickName(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Nickname.DATA1,
        };

//        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
        String selection = ContactsContract.CommonDataKinds.Nickname.LOOKUP_KEY + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Nickname.CONTENT_ITEM_TYPE};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public Cursor getContactNote(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Note.DATA1,
        };

//        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
        String selection = ContactsContract.CommonDataKinds.Event.LOOKUP_KEY + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Note.CONTENT_ITEM_TYPE};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public Cursor getContactEmail(String contactId) {
        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = new String[]{
//                ContactsContract.CommonDataKinds.Email._ID,
                ContactsContract.CommonDataKinds.Email.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.LABEL,
        };

//        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

//        String selection = ContactsContract.CommonDataKinds.Organization.CONTACT_ID + " = ? AND "
        String selection = ContactsContract.CommonDataKinds.Organization.LOOKUP_KEY + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Organization.CONTENT_ITEM_TYPE};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public Cursor getContactAddress(String contactId) {
        Uri uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        String[] projection = new String[]{
//                ContactsContract.CommonDataKinds.StructuredPostal._ID,
                ContactsContract.CommonDataKinds.StructuredPostal.LOOKUP_KEY,
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

//        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public Cursor getContactWebsite(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Website.TYPE,
                ContactsContract.CommonDataKinds.Website.URL,
        };

//        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
        String selection = ContactsContract.CommonDataKinds.Event.LOOKUP_KEY + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Website.CONTENT_ITEM_TYPE};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public Cursor getContactIm(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Im.TYPE,
                ContactsContract.CommonDataKinds.Im.LABEL,
                ContactsContract.CommonDataKinds.Im.DATA1,
                ContactsContract.CommonDataKinds.Im.PROTOCOL,
        };

//        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
        String selection = ContactsContract.CommonDataKinds.Event.LOOKUP_KEY + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Im.CONTENT_ITEM_TYPE};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public Cursor getContactEvent(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.LABEL,
                ContactsContract.CommonDataKinds.Event.START_DATE,
        };

//        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
        String selection = ContactsContract.CommonDataKinds.Event.LOOKUP_KEY + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Event.CONTENT_ITEM_TYPE};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public Cursor getContactRelationShip(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Relation.NAME,
                ContactsContract.CommonDataKinds.Relation.TYPE,
                ContactsContract.CommonDataKinds.Relation.LABEL,
        };

//        String selection = ContactsContract.CommonDataKinds.Relation.CONTACT_ID + " = ? AND "
        String selection = ContactsContract.CommonDataKinds.Relation.LOOKUP_KEY + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Relation.CONTENT_ITEM_TYPE};

        return context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public Cursor getUpdatedContacts(String lastUpdate) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.DATA_VERSION,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts._ID,
        };

        String selection = ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " >= ?";
        String[] selectionArgs = new String[]{lastUpdate};

        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    //</editor-fold>

    //<editor-fold desc="Types">

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

    public String getWebsiteType(Cursor cursor, int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE:
                return "Homepage";

            case ContactsContract.CommonDataKinds.Website.TYPE_BLOG:
                return "Blog";

            case ContactsContract.CommonDataKinds.Website.TYPE_PROFILE:
                return "Profile";

            case ContactsContract.CommonDataKinds.Website.TYPE_HOME:
                return "Home";

            case ContactsContract.CommonDataKinds.Website.TYPE_WORK:
                return "Work";

            case ContactsContract.CommonDataKinds.Website.TYPE_FTP:
                return "FTP";

            case ContactsContract.CommonDataKinds.Website.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM:
                return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                        .Website.LABEL));

        }
        return "Other";
    }

    //</editor-fold>

    public int setFavouriteStatus(String contactRawId, int status) throws NullPointerException {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Contacts.STARRED, status);
        return context.getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, values,
                ContactsContract.Contacts.LOOKUP_KEY + "= ?", new String[]{String.valueOf
                        (contactRawId)});
//                ContactsContract.Contacts._ID + "= ?", new String[]{String.valueOf
// (contactRawId)});
    }

}
