package com.rawalinfocom.rcontact.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAadharNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEducation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by user on 11/01/17.
 * <p>
 * A Class to Manage Query from multiple tables
 */

public class QueryManager {

    private DatabaseHandler databaseHandler;

    public QueryManager(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public ProfileDataOperation getRcProfileDetail(Context context, String rcpId1) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        String rcpId = rcpId1;

        ProfileDataOperation profileDataOperation = new ProfileDataOperation();

        //<editor-fold desc="Profile Detail">
        // Select All Query
        String profileDetailQuery = "SELECT profile." + TableProfileMaster.COLUMN_PM_RAW_ID +
                ",profile." + TableProfileMaster.COLUMN_PM_FIRST_NAME + ",profile." +
                TableProfileMaster.COLUMN_PM_LAST_NAME +
                ",profile." + TableProfileMaster.COLUMN_PM_PROFILE_IMAGE + ",profile." +
                TableProfileMaster.COLUMN_PM_GENDER +
                ",profile." + TableProfileMaster.COLUMN_PM_GENDER_PRIVACY + ",profile." +
                TableProfileMaster.COLUMN_PM_IS_FAVOURITE +
                ",profile." + TableProfileMaster.COLUMN_PM_PROFILE_RATING + ", profile." +
                TableProfileMaster.COLUMN_PM_PROFILE_RATE_USER + ", profile." +
                TableProfileMaster.COLUMN_PM_RATING_PRIVACY + ", profile." +
                TableProfileMaster.COLUMN_PM_RATING_PRIVATE + ", profile." +
                TableProfileMaster.COLUMN_PM_LAST_SEEN + " from " +
                TableProfileMaster.TABLE_RC_PROFILE_MASTER + " profile WHERE profile."
                + TableProfileMaster.COLUMN_PM_RCP_ID + " IN (" + rcpId + ")";

        Cursor cursor = db.rawQuery(profileDetailQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {

                profileDataOperation.setRcpPmId(rcpId);
//            profileDataOperation.setPbNamePrefix(StringUtils.defaultString(cursor.getString
//                    (cursor.getColumnIndex(TableProfileMaster.COLUMN_PM_PREFIX))));
                profileDataOperation.setPbNameFirst(StringUtils.defaultString(cursor.getString
                        (cursor
                                .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_FIRST_NAME))));
//            profileDataOperation.setPbNameMiddle(StringUtils.defaultString(cursor.getString
//                    (cursor.getColumnIndex(TableProfileMaster.COLUMN_PM_MIDDLE_NAME))));
                profileDataOperation.setPbNameLast(StringUtils.defaultString(cursor.getString(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_LAST_NAME))));
                profileDataOperation.setPbGender(StringUtils.defaultString(cursor.getString(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_GENDER))));
                profileDataOperation.setPbProfilePhoto(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_PROFILE_IMAGE)
                        )));
                profileDataOperation.setIsFavourite(StringUtils.defaultString(cursor.getString
                        (cursor
                                .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_IS_FAVOURITE)
                        )));
                profileDataOperation.setProfileRating(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableProfileMaster
                                .COLUMN_PM_PROFILE_RATING)), "0"));
                profileDataOperation.setTotalProfileRateUser(StringUtils.defaultString(cursor
                        .getString(cursor.getColumnIndexOrThrow(TableProfileMaster
                                .COLUMN_PM_PROFILE_RATE_USER)), "0"));
                profileDataOperation.setPmLastSeen(StringUtils.defaultString(cursor.getString(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_LAST_SEEN))));
                profileDataOperation.setProfileRatingPrivacy(cursor.getInt(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_RATING_PRIVACY)));
                profileDataOperation.setRatingPrivate(cursor.getInt(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_RATING_PRIVATE)));

                cursor.close();
            }
        }

        //</editor-fold>

        //<editor-fold desc="Phone Number">
        String mobileNumberQuery = "SELECT mobile." + TableMobileMaster.COLUMN_MNM_MOBILE_NUMBER
                + ",mobile." + TableMobileMaster.COLUMN_MNM_NUMBER_TYPE
                + ",mobile." + TableMobileMaster.COLUMN_MNM_IS_PRIVATE + ",mobile." +
                TableMobileMaster.COLUMN_MNM_RECORD_INDEX_ID + ",mobile." + TableMobileMaster
                .COLUMN_MNM_IS_PRIMARY + ",mobile." + TableMobileMaster.COLUMN_MNM_NUMBER_PRIVACY
                + " from " + TableMobileMaster.TABLE_RC_MOBILE_NUMBER_MASTER + " mobile WHERE " +
                "mobile." + TableMobileMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId +
                ")";

        Cursor mobileNumberCursor = db.rawQuery(mobileNumberQuery, null);

        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

        // looping through all rows and adding to list
        if (mobileNumberCursor != null && mobileNumberCursor.getCount() > 0) {
            if (mobileNumberCursor.moveToFirst()) {
                do {
                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();
                    phoneNumber.setPhoneNumber(StringUtils.defaultString(mobileNumberCursor
                            .getString
                                    (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                            .COLUMN_MNM_MOBILE_NUMBER))));
                    phoneNumber.setPhoneType(StringUtils.defaultString(mobileNumberCursor.getString
                            (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                    .COLUMN_MNM_NUMBER_TYPE))));
                    if (mobileNumberCursor.getString
                            (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                    .COLUMN_MNM_IS_PRIMARY)) != null) {
                        phoneNumber.setPbRcpType(Integer.parseInt(mobileNumberCursor.getString
                                (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                        .COLUMN_MNM_IS_PRIMARY))));
                    } else {
                        phoneNumber.setPbRcpType(2);
                    }

                    phoneNumber.setPhonePublic(Integer.parseInt(StringUtils.defaultString
                            (mobileNumberCursor.getString(mobileNumberCursor.getColumnIndexOrThrow
                                    (TableMobileMaster.COLUMN_MNM_NUMBER_PRIVACY)), "0")));
                    phoneNumber.setIsPrivate(Integer.parseInt(StringUtils.defaultString
                            (mobileNumberCursor.getString(mobileNumberCursor.getColumnIndexOrThrow
                                    (TableMobileMaster.COLUMN_MNM_IS_PRIVATE)), "0")));
                    phoneNumber.setPhoneId(StringUtils.defaultString(mobileNumberCursor.getString
                            (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                    .COLUMN_MNM_RECORD_INDEX_ID))));
                    arrayListPhoneNumber.add(phoneNumber);
                } while (mobileNumberCursor.moveToNext());
                mobileNumberCursor.close();
            }
            profileDataOperation.setPbPhoneNumber(arrayListPhoneNumber);
        }
        //</editor-fold>

        //<editor-fold desc="EmailId">
        String emailIdQuery = "SELECT email." + TableEmailMaster.COLUMN_EM_EMAIL_ADDRESS + "," +
                "email." + TableEmailMaster.COLUMN_EM_EMAIL_TYPE +
                ",email." + TableEmailMaster.COLUMN_EM_SOCIAL_TYPE +
                ",email." + TableEmailMaster.COLUMN_EM_IS_PRIVATE + ",email." + TableEmailMaster
                .COLUMN_EM_EMAIL_PRIVACY + ",email." + TableEmailMaster.COLUMN_EM_RECORD_INDEX_ID +
                ",email." + TableEmailMaster.COLUMN_EM_IS_VERIFIED + " FROM " + TableEmailMaster
                .TABLE_RC_EMAIL_MASTER + " email where email." + TableEmailMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor emailIdCursor = db.rawQuery(emailIdQuery, null);

        ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();

        // looping through all rows and adding to list
        if (emailIdCursor != null && emailIdCursor.getCount() > 0) {
            if (emailIdCursor.moveToFirst()) {
                do {
                    ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                    email.setEmEmailId(StringUtils.defaultString(emailIdCursor.getString
                            (emailIdCursor.getColumnIndexOrThrow(TableEmailMaster
                                    .COLUMN_EM_EMAIL_ADDRESS))));
                    email.setEmType(StringUtils.defaultString(emailIdCursor.getString(emailIdCursor
                            .getColumnIndexOrThrow(TableEmailMaster.COLUMN_EM_EMAIL_TYPE))));
                    email.setEmSocialType(StringUtils.defaultString(emailIdCursor.getString
                            (emailIdCursor
                                    .getColumnIndexOrThrow(TableEmailMaster
                                            .COLUMN_EM_SOCIAL_TYPE))));
                    email.setEmPublic(Integer.parseInt(StringUtils.defaultString(emailIdCursor
                            .getString(emailIdCursor.getColumnIndexOrThrow(TableEmailMaster
                                    .COLUMN_EM_EMAIL_PRIVACY)), "0")));
                    email.setEmIsPrivate(Integer.parseInt(StringUtils.defaultString(emailIdCursor
                            .getString(emailIdCursor.getColumnIndexOrThrow(TableEmailMaster
                                    .COLUMN_EM_IS_PRIVATE)), "0")));
                    email.setEmId(StringUtils.defaultString(emailIdCursor.getString(emailIdCursor
                            .getColumnIndexOrThrow(TableEmailMaster.COLUMN_EM_RECORD_INDEX_ID))));
                    email.setEmRcpType(Integer.parseInt(emailIdCursor.getString
                            (emailIdCursor.getColumnIndexOrThrow(TableEmailMaster
                                    .COLUMN_EM_IS_VERIFIED))));
                    arrayListEmail.add(email);
                } while (emailIdCursor.moveToNext());
                emailIdCursor.close();
            }
            profileDataOperation.setPbEmailId(arrayListEmail);
        }

        //</editor-fold>

        // <editor-fold desc="Education">
        String educationQuery = "SELECT edu." + TableEducationMaster
                .COLUMN_EDM_SCHOOL_COLLEGE_NAME + ",edu." + TableEducationMaster
                .COLUMN_EDM_COURSE + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_FROM_DATE + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_TO_DATE + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_PRIVACY + ",edu." + TableEducationMaster
                .COLUMN_EDM_RECORD_INDEX_ID + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_IS_PRIVATE + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_IS_CURRENT + " FROM " + TableEducationMaster
                .TABLE_RC_EDUCATION_MASTER + " edu where edu." + TableEducationMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor educationCursor = db.rawQuery(educationQuery, null);

        ArrayList<ProfileDataOperationEducation> arrayListEducation = new ArrayList<>();

        // looping through all rows and adding to list
        if (educationCursor != null && educationCursor.getCount() > 0) {
            if (educationCursor.moveToFirst()) {
                do {
                    ProfileDataOperationEducation education = new ProfileDataOperationEducation();
                    education.setEduName(StringUtils.defaultString(educationCursor.getString
                            (educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_SCHOOL_COLLEGE_NAME))));
                    education.setEduCourse(StringUtils.defaultString(educationCursor.getString
                            (educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_COURSE))));
                    education.setEduFromDate(StringUtils.defaultString(educationCursor.getString
                            (educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_EDUCATION_FROM_DATE))));
                    education.setEduToDate(StringUtils.defaultString(educationCursor
                            .getString(educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_EDUCATION_TO_DATE))));
                    education.setEduPublic(Integer.parseInt(StringUtils.defaultString
                            (educationCursor.getString(educationCursor.getColumnIndexOrThrow
                                    (TableEducationMaster.COLUMN_EDM_EDUCATION_PRIVACY)), "2")));
                    education.setEduId(StringUtils.defaultString(educationCursor.getString
                            (educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_RECORD_INDEX_ID))));
                    arrayListEducation.add(education);
                } while (educationCursor.moveToNext());
                educationCursor.close();
            }
            profileDataOperation.setPbEducation(arrayListEducation);
        }

        //</editor-fold>

        // <editor-fold desc="Organization">
        String organizationQuery = "SELECT org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_COMPANY + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_TYPE + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_ENT_ID + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_URL_SLUG + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_IMAGE + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_FROM_DATE + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_TO_DATE + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_IS_VERIFIED + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_IS_CURRENT + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_DESIGNATION + " from " + TableOrganizationMaster
                .TABLE_RC_ORGANIZATION_MASTER + " org WHERE org." + TableOrganizationMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " = " + rcpId + " ORDER BY org." +
                TableOrganizationMaster.COLUMN_OM_ORGANIZATION_IS_CURRENT + " DESC, date(org." +
                TableOrganizationMaster.COLUMN_OM_ORGANIZATION_FROM_DATE + ") DESC";

        Cursor organizationCursor = db.rawQuery(organizationQuery, null);

        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

        // looping through all rows and adding to list
        if (organizationCursor != null && organizationCursor.getCount() > 0) {
            if (organizationCursor.moveToFirst()) {
                do {
                    ProfileDataOperationOrganization organization = new
                            ProfileDataOperationOrganization();
                    organization.setOrgName(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_COMPANY))));
                    organization.setOrgIndustryType(StringUtils.defaultString(organizationCursor
                            .getString(organizationCursor.getColumnIndexOrThrow
                                    (TableOrganizationMaster.COLUMN_OM_ORGANIZATION_TYPE))));
                    organization.setOrgEntId(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_ENT_ID))));
                    organization.setOrgUrlSlug(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_URL_SLUG))));
                    organization.setOrgLogo(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_IMAGE))));
                    organization.setOrgJobTitle(StringUtils.defaultString(organizationCursor
                            .getString(organizationCursor.getColumnIndexOrThrow
                                    (TableOrganizationMaster
                                            .COLUMN_OM_ORGANIZATION_DESIGNATION))));
                    organization.setOrgFromDate(StringUtils.defaultString(organizationCursor
                            .getString
                                    (organizationCursor.getColumnIndexOrThrow
                                            (TableOrganizationMaster
                                                    .COLUMN_OM_ORGANIZATION_FROM_DATE))));
                    organization.setOrgToDate(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_TO_DATE))));
                    organization.setIsCurrent(organizationCursor.getInt((organizationCursor
                            .getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_IS_CURRENT))));
                    if (!StringUtils.isBlank(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_IS_VERIFIED)))) {
                        organization.setIsVerify(Integer.parseInt(organizationCursor.getString
                                (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                        .COLUMN_OM_ORGANIZATION_IS_VERIFIED))));
                    } else {
                        organization.setIsVerify(0);
                    }

                    organization.setOrgRcpType(String.valueOf(IntegerConstants
                            .RCP_TYPE_CLOUD_PHONE_BOOK));
                    arrayListOrganization.add(organization);
                } while (organizationCursor.moveToNext());
                organizationCursor.close();
            }
            profileDataOperation.setPbOrganization(arrayListOrganization);
        }

        //</editor-fold>

        // <editor-fold desc="Event">
        String eventQuery = "SELECT event." + TableEventMaster.COLUMN_EVM_START_DATE + ",event."
                + TableEventMaster.COLUMN_EVM_EVENT_TYPE + ",event." + TableEventMaster
                .COLUMN_EVM_IS_YEAR_HIDDEN + ",event." + TableEventMaster.COLUMN_EVM_IS_PRIVATE +
                ",event." + TableEventMaster.COLUMN_EVM_RECORD_INDEX_ID + ",event." +
                TableEventMaster.COLUMN_EVM_EVENT_PRIVACY + " FROM " + TableEventMaster
                .TABLE_RC_EVENT_MASTER + " event WHERE event." + TableEventMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor eventCursor = db.rawQuery(eventQuery, null);

        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();

        // looping through all rows and adding to list
        if (eventCursor != null && eventCursor.getCount() > 0) {
            if (eventCursor.moveToFirst()) {
                do {
                    ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                    event.setEventDateTime(StringUtils.defaultString(eventCursor.getString
                            (eventCursor
                                    .getColumnIndexOrThrow(TableEventMaster
                                            .COLUMN_EVM_START_DATE))));
                    event.setEventType(StringUtils.defaultString(eventCursor.getString(eventCursor
                            .getColumnIndexOrThrow(TableEventMaster.COLUMN_EVM_EVENT_TYPE))));
                    event.setEventId(StringUtils.defaultString(eventCursor.getString(eventCursor
                            .getColumnIndexOrThrow(TableEventMaster.COLUMN_EVM_RECORD_INDEX_ID))));
                    event.setEventPublic(Integer.parseInt(StringUtils.defaultString(eventCursor
                            .getString(eventCursor.getColumnIndexOrThrow(TableEventMaster
                                    .COLUMN_EVM_EVENT_PRIVACY)), "0")));
                    event.setIsPrivate(Integer.parseInt(StringUtils.defaultString(eventCursor
                            .getString(eventCursor.getColumnIndexOrThrow(TableEventMaster
                                    .COLUMN_EVM_IS_PRIVATE)), "0")));
                    event.setIsYearHidden(Integer.parseInt(StringUtils.defaultString(eventCursor
                            .getString(eventCursor.getColumnIndexOrThrow(TableEventMaster
                                    .COLUMN_EVM_IS_YEAR_HIDDEN)), "0")));
                    event.setEventRcType(String.valueOf(IntegerConstants
                            .RCP_TYPE_CLOUD_PHONE_BOOK));
                    arrayListEvent.add(event);
                } while (eventCursor.moveToNext());
                eventCursor.close();
            }
            profileDataOperation.setPbEvent(arrayListEvent);
        }

        //</editor-fold>

        // <editor-fold desc="Im Account">
        String imAccountQuery = "SELECT im." +
                TableImMaster.COLUMN_IM_PROTOCOL + ", im." + TableImMaster
                .COLUMN_IM_FIRST_NAME + ", im." + TableImMaster
                .COLUMN_IM_LAST_NAME + ", im." + TableImMaster
                .COLUMN_IM_PRIVACY + ", im." + TableImMaster
                .COLUMN_IM_IS_PRIVATE + ", im." + TableImMaster
                .COLUMN_IM_RECORD_INDEX_ID + ", im." + TableImMaster.COLUMN_IM_DETAIL +
                " FROM " + TableImMaster.TABLE_RC_IM_MASTER + " im WHERE " +
                "im." + TableImMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor imAccountCursor = db.rawQuery(imAccountQuery, null);

        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();

        // looping through all rows and adding to list
        if (imAccountCursor != null && imAccountCursor.getCount() > 0) {
            if (imAccountCursor.moveToFirst()) {
                do {
                    ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
                    imAccount.setIMAccountProtocol(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_PROTOCOL))));
                    imAccount.setIMAccountDetails(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_DETAIL))));
                    imAccount.setIMAccountFirstName(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_FIRST_NAME))));
                    imAccount.setIMAccountLastName(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_LAST_NAME))));
                    imAccount.setIMId(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_RECORD_INDEX_ID))));
                    imAccount.setIMAccountProtocol(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_PROTOCOL))));
                    imAccount.setIMAccountPublic(Integer.parseInt(StringUtils.defaultString
                            (imAccountCursor
                                    .getString(imAccountCursor.getColumnIndex(TableImMaster
                                            .COLUMN_IM_PRIVACY)), "0")));
                    imAccount.setIMAccountIsPrivate(Integer.parseInt(StringUtils.defaultString
                            (imAccountCursor
                                    .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                            .COLUMN_IM_IS_PRIVATE)), "0")));
                    imAccount.setIMRcpType(String.valueOf(IntegerConstants
                            .RCP_TYPE_CLOUD_PHONE_BOOK));
                    arrayListImAccount.add(imAccount);
                } while (imAccountCursor.moveToNext());
                imAccountCursor.close();
            }
            profileDataOperation.setPbIMAccounts(arrayListImAccount);
        }

        //</editor-fold>

        // <editor-fold desc="Address">
        String addressQuery = "SELECT address." + TableAddressMaster.COLUMN_AM_FORMATTED_ADDRESS
                + ", address." + TableAddressMaster.COLUMN_AM_ADDRESS_TYPE + ", address." +
                TableAddressMaster.COLUMN_AM_IS_PRIVATE + ", address." +
                TableAddressMaster.COLUMN_AM_GOOGLE_LATITUDE + ", address." +
                TableAddressMaster.COLUMN_AM_GOOGLE_LONGITUDE + ", address." +
                TableAddressMaster.COLUMN_AM_RECORD_INDEX_ID + ", address." +
                TableAddressMaster.COLUMN_AM_ADDRESS_PRIVACY + " FROM " + TableAddressMaster
                .TABLE_RC_ADDRESS_MASTER + " address WHERE address." + TableAddressMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor addressCursor = db.rawQuery(addressQuery, null);

        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();

        // looping through all rows and adding to list
        if (addressCursor != null && addressCursor.getCount() > 0) {
            if (addressCursor.moveToFirst()) {
                do {
                    ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                    address.setFormattedAddress(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_FORMATTED_ADDRESS))));
                    address.setAddressType(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_ADDRESS_TYPE))));
                    address.setAddPublic(Integer.parseInt(StringUtils.defaultString(addressCursor
                            .getString(addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_ADDRESS_PRIVACY)), "0")));
                    address.setIsPrivate(Integer.parseInt(StringUtils.defaultString(addressCursor
                            .getString(addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_IS_PRIVATE)), "0")));
                    address.setAddId(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_RECORD_INDEX_ID))));
                    ArrayList<String> arrayListLatLng = new ArrayList<>();
                    arrayListLatLng.add(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_GOOGLE_LONGITUDE)), "0.0"));
                    arrayListLatLng.add(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_GOOGLE_LATITUDE)), "0.0"));
                    address.setGoogleLatLong(arrayListLatLng);
                    address.setRcpType(String.valueOf(IntegerConstants.RCP_TYPE_CLOUD_PHONE_BOOK));
                    arrayListAddress.add(address);
                } while (addressCursor.moveToNext());
                addressCursor.close();
            }
            profileDataOperation.setPbAddress(arrayListAddress);
        }

        //</editor-fold>

        // <editor-fold desc="Aadhar Card">
        String aadharCardQuery = "SELECT " + TableAadharMaster.COLUMN_AADHAR_ID
                + "," + TableAadharMaster.COLUMN_AADHAR_NUMBER + "," +
                TableAadharMaster.COLUMN_AADHAR_IS_VARIFIED + "," +
                TableAadharMaster.COLUMN_AADHAR_PUBLIC + " FROM " + TableAadharMaster
                .TABLE_AADHAR_MASTER + " WHERE " + TableAadharMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor aadharCardCursor = db.rawQuery(aadharCardQuery, null);
        // looping through all rows and adding to list
        ProfileDataOperationAadharNumber aadharDetails = new ProfileDataOperationAadharNumber();
        if (aadharCardCursor != null && aadharCardCursor.getCount() > 0) {
            if (aadharCardCursor.moveToFirst()) {
                aadharDetails.setAadharId(aadharCardCursor.getInt(aadharCardCursor.
                        getColumnIndexOrThrow(TableAadharMaster.COLUMN_AADHAR_ID)));
                aadharDetails.setAadharNumber(aadharCardCursor.getString(aadharCardCursor.
                        getColumnIndexOrThrow(TableAadharMaster.COLUMN_AADHAR_NUMBER)));
                aadharDetails.setAadharIsVerified(aadharCardCursor.getInt(aadharCardCursor.
                        getColumnIndexOrThrow(TableAadharMaster.COLUMN_AADHAR_IS_VARIFIED)));
                aadharDetails.setAadharPublic(aadharCardCursor.getInt(aadharCardCursor.
                        getColumnIndexOrThrow(TableAadharMaster.COLUMN_AADHAR_PUBLIC)));
                aadharCardCursor.close();
            }
            profileDataOperation.setPbAadhar(aadharDetails);
        }

        //</editor-fold>

        // <editor-fold desc="Website">
        String websiteQuery = "select website." + TableWebsiteMaster.COLUMN_WM_WEBSITE_URL + " " +
                "FROM " + TableWebsiteMaster.TABLE_RC_WEBSITE_MASTER + " website WHERE website."
                + TableWebsiteMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor websiteCursor = db.rawQuery(websiteQuery, null);

        ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();
        // looping through all rows and adding to list
        if (websiteCursor != null && websiteCursor.getCount() > 0) {
            if (websiteCursor.moveToFirst()) {
                do {
                    ProfileDataOperationWebAddress webAddress = new
                            ProfileDataOperationWebAddress();
                    webAddress.setWebAddress(StringUtils.defaultString(websiteCursor.getString
                            (websiteCursor.getColumnIndexOrThrow(TableWebsiteMaster
                                    .COLUMN_WM_WEBSITE_URL))));
                    webAddress.setWebRcpType(String.valueOf(IntegerConstants
                            .RCP_TYPE_CLOUD_PHONE_BOOK));
               /* arrayListWebsite.add(StringUtils.defaultString(websiteCursor.getString
                        (websiteCursor.getColumnIndex(TableWebsiteMaster.COLUMN_WM_WEBSITE_URL)))
                        );*/
                    arrayListWebsite.add(webAddress);
                } while (websiteCursor.moveToNext());
                websiteCursor.close();
            }
            profileDataOperation.setPbWebAddress(arrayListWebsite);
        }

        //</editor-fold>

        db.close();

        // return profile data operation
        return profileDataOperation;
    }


    /*public ArrayList<ProfileDataOperation> getAllRcpProfileDetails(Context context) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
//        String rcpId = rcpId1;
        ArrayList<ProfileDataOperation> profileDataOperationArrayList =  new ArrayList<>();

        ProfileDataOperation profileDataOperation = new ProfileDataOperation();

        //<editor-fold desc="Profile Detail">
        // Select All Query
        String profileDetailQuery = "SELECT profile." + TableProfileMaster.COLUMN_PM_RAW_ID +
                ",profile." + TableProfileMaster.COLUMN_PM_FIRST_NAME + ",profile." +
                TableProfileMaster.COLUMN_PM_LAST_NAME +
                ",profile." + TableProfileMaster.COLUMN_PM_PROFILE_IMAGE + ",profile." +
                TableProfileMaster.COLUMN_PM_GENDER +
                ",profile." + TableProfileMaster.COLUMN_PM_GENDER_PRIVACY + ",profile." +
                TableProfileMaster.COLUMN_PM_IS_FAVOURITE +
                ",profile." + TableProfileMaster.COLUMN_PM_PROFILE_RATING + ", profile." +
                TableProfileMaster.COLUMN_PM_PROFILE_RATE_USER + ", profile." +
                TableProfileMaster.COLUMN_PM_RATING_PRIVACY + ", profile." +
                TableProfileMaster.COLUMN_PM_RATING_PRIVATE + ", profile." +
                TableProfileMaster.COLUMN_PM_LAST_SEEN + " from " +
                TableProfileMaster.TABLE_RC_PROFILE_MASTER;

        Cursor cursor = db.rawQuery(profileDetailQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

//                profileDataOperation.setRcpPmId(rcpId);
//            profileDataOperation.setPbNamePrefix(StringUtils.defaultString(cursor.getString
//                    (cursor.getColumnIndex(TableProfileMaster.COLUMN_PM_PREFIX))));
                profileDataOperation.setPbNameFirst(StringUtils.defaultString(cursor.getString
                        (cursor
                                .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_FIRST_NAME))));
//            profileDataOperation.setPbNameMiddle(StringUtils.defaultString(cursor.getString
//                    (cursor.getColumnIndex(TableProfileMaster.COLUMN_PM_MIDDLE_NAME))));
                profileDataOperation.setPbNameLast(StringUtils.defaultString(cursor.getString(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_LAST_NAME))));
                profileDataOperation.setPbGender(StringUtils.defaultString(cursor.getString(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_GENDER))));
                profileDataOperation.setPbProfilePhoto(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_PROFILE_IMAGE)
                        )));
                profileDataOperation.setIsFavourite(StringUtils.defaultString(cursor.getString
                        (cursor
                                .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_IS_FAVOURITE)
                        )));
                profileDataOperation.setProfileRating(StringUtils.defaultString(cursor.getString
                        (cursor.getColumnIndexOrThrow(TableProfileMaster
                                .COLUMN_PM_PROFILE_RATING)), "0"));
                profileDataOperation.setTotalProfileRateUser(StringUtils.defaultString(cursor
                        .getString(cursor.getColumnIndexOrThrow(TableProfileMaster
                                .COLUMN_PM_PROFILE_RATE_USER)), "0"));
                profileDataOperation.setPmLastSeen(StringUtils.defaultString(cursor.getString(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_LAST_SEEN))));
                profileDataOperation.setProfileRatingPrivacy(cursor.getInt(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_RATING_PRIVACY)));
                profileDataOperation.setRatingPrivate(cursor.getInt(cursor
                        .getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_RATING_PRIVATE)));

                cursor.close();
            }
        }

        //</editor-fold>

        //<editor-fold desc="Phone Number">
        String mobileNumberQuery = "SELECT mobile." + TableMobileMaster.COLUMN_MNM_MOBILE_NUMBER
                + ",mobile." + TableMobileMaster.COLUMN_MNM_NUMBER_TYPE
                + ",mobile." + TableMobileMaster.COLUMN_MNM_IS_PRIVATE + ",mobile." +
                TableMobileMaster.COLUMN_MNM_RECORD_INDEX_ID + ",mobile." + TableMobileMaster
                .COLUMN_MNM_IS_PRIMARY + ",mobile." + TableMobileMaster.COLUMN_MNM_NUMBER_PRIVACY
                + " from " + TableMobileMaster.TABLE_RC_MOBILE_NUMBER_MASTER;

        Cursor mobileNumberCursor = db.rawQuery(mobileNumberQuery, null);

        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

        // looping through all rows and adding to list
        if (mobileNumberCursor != null && mobileNumberCursor.getCount() > 0) {
            if (mobileNumberCursor.moveToFirst()) {
                do {
                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();
                    phoneNumber.setPhoneNumber(StringUtils.defaultString(mobileNumberCursor
                            .getString
                                    (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                            .COLUMN_MNM_MOBILE_NUMBER))));
                    phoneNumber.setPhoneType(StringUtils.defaultString(mobileNumberCursor.getString
                            (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                    .COLUMN_MNM_NUMBER_TYPE))));
                    if (mobileNumberCursor.getString
                            (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                    .COLUMN_MNM_IS_PRIMARY)) != null) {
                        phoneNumber.setPbRcpType(Integer.parseInt(mobileNumberCursor.getString
                                (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                        .COLUMN_MNM_IS_PRIMARY))));
                    } else {
                        phoneNumber.setPbRcpType(2);
                    }

                    phoneNumber.setPhonePublic(Integer.parseInt(StringUtils.defaultString
                            (mobileNumberCursor.getString(mobileNumberCursor.getColumnIndexOrThrow
                                    (TableMobileMaster.COLUMN_MNM_NUMBER_PRIVACY)), "0")));
                    phoneNumber.setIsPrivate(Integer.parseInt(StringUtils.defaultString
                            (mobileNumberCursor.getString(mobileNumberCursor.getColumnIndexOrThrow
                                    (TableMobileMaster.COLUMN_MNM_IS_PRIVATE)), "0")));
                    phoneNumber.setPhoneId(StringUtils.defaultString(mobileNumberCursor.getString
                            (mobileNumberCursor.getColumnIndexOrThrow(TableMobileMaster
                                    .COLUMN_MNM_RECORD_INDEX_ID))));
                    arrayListPhoneNumber.add(phoneNumber);
                } while (mobileNumberCursor.moveToNext());
                mobileNumberCursor.close();
            }
            profileDataOperation.setPbPhoneNumber(arrayListPhoneNumber);
        }
        //</editor-fold>

        //<editor-fold desc="EmailId">
        String emailIdQuery = "SELECT email." + TableEmailMaster.COLUMN_EM_EMAIL_ADDRESS + "," +
                "email." + TableEmailMaster.COLUMN_EM_EMAIL_TYPE +
                ",email." + TableEmailMaster.COLUMN_EM_SOCIAL_TYPE +
                ",email." + TableEmailMaster.COLUMN_EM_IS_PRIVATE + ",email." + TableEmailMaster
                .COLUMN_EM_EMAIL_PRIVACY + ",email." + TableEmailMaster.COLUMN_EM_RECORD_INDEX_ID +
                ",email." + TableEmailMaster.COLUMN_EM_IS_VERIFIED + " FROM " + TableEmailMaster
                .TABLE_RC_EMAIL_MASTER;

        Cursor emailIdCursor = db.rawQuery(emailIdQuery, null);

        ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();

        // looping through all rows and adding to list
        if (emailIdCursor != null && emailIdCursor.getCount() > 0) {
            if (emailIdCursor.moveToFirst()) {
                do {
                    ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                    email.setEmEmailId(StringUtils.defaultString(emailIdCursor.getString
                            (emailIdCursor.getColumnIndexOrThrow(TableEmailMaster
                                    .COLUMN_EM_EMAIL_ADDRESS))));
                    email.setEmType(StringUtils.defaultString(emailIdCursor.getString(emailIdCursor
                            .getColumnIndexOrThrow(TableEmailMaster.COLUMN_EM_EMAIL_TYPE))));
                    email.setEmSocialType(StringUtils.defaultString(emailIdCursor.getString
                            (emailIdCursor
                                    .getColumnIndexOrThrow(TableEmailMaster
                                            .COLUMN_EM_SOCIAL_TYPE))));
                    email.setEmPublic(Integer.parseInt(StringUtils.defaultString(emailIdCursor
                            .getString(emailIdCursor.getColumnIndexOrThrow(TableEmailMaster
                                    .COLUMN_EM_EMAIL_PRIVACY)), "0")));
                    email.setEmIsPrivate(Integer.parseInt(StringUtils.defaultString(emailIdCursor
                            .getString(emailIdCursor.getColumnIndexOrThrow(TableEmailMaster
                                    .COLUMN_EM_IS_PRIVATE)), "0")));
                    email.setEmId(StringUtils.defaultString(emailIdCursor.getString(emailIdCursor
                            .getColumnIndexOrThrow(TableEmailMaster.COLUMN_EM_RECORD_INDEX_ID))));
                    email.setEmRcpType(Integer.parseInt(emailIdCursor.getString
                            (emailIdCursor.getColumnIndexOrThrow(TableEmailMaster
                                    .COLUMN_EM_IS_VERIFIED))));
                    arrayListEmail.add(email);
                } while (emailIdCursor.moveToNext());
                emailIdCursor.close();
            }
            profileDataOperation.setPbEmailId(arrayListEmail);
        }

        //</editor-fold>

        // <editor-fold desc="Education">
        String educationQuery = "SELECT edu." + TableEducationMaster
                .COLUMN_EDM_SCHOOL_COLLEGE_NAME + ",edu." + TableEducationMaster
                .COLUMN_EDM_COURSE + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_FROM_DATE + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_TO_DATE + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_PRIVACY + ",edu." + TableEducationMaster
                .COLUMN_EDM_RECORD_INDEX_ID + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_IS_PRIVATE + ",edu." + TableEducationMaster
                .COLUMN_EDM_EDUCATION_IS_CURRENT + " FROM " + TableEducationMaster
                .TABLE_RC_EDUCATION_MASTER;

        Cursor educationCursor = db.rawQuery(educationQuery, null);

        ArrayList<ProfileDataOperationEducation> arrayListEducation = new ArrayList<>();

        // looping through all rows and adding to list
        if (educationCursor != null && educationCursor.getCount() > 0) {
            if (educationCursor.moveToFirst()) {
                do {
                    ProfileDataOperationEducation education = new ProfileDataOperationEducation();
                    education.setEduName(StringUtils.defaultString(educationCursor.getString
                            (educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_SCHOOL_COLLEGE_NAME))));
                    education.setEduCourse(StringUtils.defaultString(educationCursor.getString
                            (educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_COURSE))));
                    education.setEduFromDate(StringUtils.defaultString(educationCursor.getString
                            (educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_EDUCATION_FROM_DATE))));
                    education.setEduToDate(StringUtils.defaultString(educationCursor
                            .getString(educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_EDUCATION_TO_DATE))));
                    education.setEduPublic(Integer.parseInt(StringUtils.defaultString
                            (educationCursor.getString(educationCursor.getColumnIndexOrThrow
                                    (TableEducationMaster.COLUMN_EDM_EDUCATION_PRIVACY)), "2")));
                    education.setEduId(StringUtils.defaultString(educationCursor.getString
                            (educationCursor.getColumnIndexOrThrow(TableEducationMaster
                                    .COLUMN_EDM_RECORD_INDEX_ID))));
                    arrayListEducation.add(education);
                } while (educationCursor.moveToNext());
                educationCursor.close();
            }
            profileDataOperation.setPbEducation(arrayListEducation);
        }

        //</editor-fold>

        // <editor-fold desc="Organization">
        String organizationQuery = "SELECT org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_COMPANY + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_TYPE + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_ENT_ID + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_URL_SLUG + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_IMAGE + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_FROM_DATE + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_TO_DATE + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_IS_VERIFIED + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_IS_CURRENT + ",org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_DESIGNATION + " from " + TableOrganizationMaster
                .TABLE_RC_ORGANIZATION_MASTER + " ORDER BY org." +
                TableOrganizationMaster.COLUMN_OM_ORGANIZATION_IS_CURRENT + " DESC, date(org." +
                TableOrganizationMaster.COLUMN_OM_ORGANIZATION_FROM_DATE + ") DESC";

        Cursor organizationCursor = db.rawQuery(organizationQuery, null);

        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

        // looping through all rows and adding to list
        if (organizationCursor != null && organizationCursor.getCount() > 0) {
            if (organizationCursor.moveToFirst()) {
                do {
                    ProfileDataOperationOrganization organization = new
                            ProfileDataOperationOrganization();
                    organization.setOrgName(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_COMPANY))));
                    organization.setOrgIndustryType(StringUtils.defaultString(organizationCursor
                            .getString(organizationCursor.getColumnIndexOrThrow
                                    (TableOrganizationMaster.COLUMN_OM_ORGANIZATION_TYPE))));
                    organization.setOrgEntId(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_ENT_ID))));
                    organization.setOrgUrlSlug(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_URL_SLUG))));
                    organization.setOrgLogo(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_IMAGE))));
                    organization.setOrgJobTitle(StringUtils.defaultString(organizationCursor
                            .getString(organizationCursor.getColumnIndexOrThrow
                                    (TableOrganizationMaster
                                            .COLUMN_OM_ORGANIZATION_DESIGNATION))));
                    organization.setOrgFromDate(StringUtils.defaultString(organizationCursor
                            .getString
                                    (organizationCursor.getColumnIndexOrThrow
                                            (TableOrganizationMaster
                                                    .COLUMN_OM_ORGANIZATION_FROM_DATE))));
                    organization.setOrgToDate(StringUtils.defaultString(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_TO_DATE))));
                    organization.setIsCurrent(organizationCursor.getInt((organizationCursor
                            .getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_IS_CURRENT))));
                    if (!StringUtils.isBlank(organizationCursor.getString
                            (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                    .COLUMN_OM_ORGANIZATION_IS_VERIFIED)))) {
                        organization.setIsVerify(Integer.parseInt(organizationCursor.getString
                                (organizationCursor.getColumnIndexOrThrow(TableOrganizationMaster
                                        .COLUMN_OM_ORGANIZATION_IS_VERIFIED))));
                    } else {
                        organization.setIsVerify(0);
                    }

                    organization.setOrgRcpType(String.valueOf(IntegerConstants
                            .RCP_TYPE_CLOUD_PHONE_BOOK));
                    arrayListOrganization.add(organization);
                } while (organizationCursor.moveToNext());
                organizationCursor.close();
            }
            profileDataOperation.setPbOrganization(arrayListOrganization);
        }

        //</editor-fold>

        // <editor-fold desc="Event">
        String eventQuery = "SELECT event." + TableEventMaster.COLUMN_EVM_START_DATE + ",event."
                + TableEventMaster.COLUMN_EVM_EVENT_TYPE + ",event." + TableEventMaster
                .COLUMN_EVM_IS_YEAR_HIDDEN + ",event." + TableEventMaster.COLUMN_EVM_IS_PRIVATE +
                ",event." + TableEventMaster.COLUMN_EVM_RECORD_INDEX_ID + ",event." +
                TableEventMaster.COLUMN_EVM_EVENT_PRIVACY + " FROM " + TableEventMaster
                .TABLE_RC_EVENT_MASTER;

        Cursor eventCursor = db.rawQuery(eventQuery, null);

        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();

        // looping through all rows and adding to list
        if (eventCursor != null && eventCursor.getCount() > 0) {
            if (eventCursor.moveToFirst()) {
                do {
                    ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                    event.setEventDateTime(StringUtils.defaultString(eventCursor.getString
                            (eventCursor
                                    .getColumnIndexOrThrow(TableEventMaster
                                            .COLUMN_EVM_START_DATE))));
                    event.setEventType(StringUtils.defaultString(eventCursor.getString(eventCursor
                            .getColumnIndexOrThrow(TableEventMaster.COLUMN_EVM_EVENT_TYPE))));
                    event.setEventId(StringUtils.defaultString(eventCursor.getString(eventCursor
                            .getColumnIndexOrThrow(TableEventMaster.COLUMN_EVM_RECORD_INDEX_ID))));
                    event.setEventPublic(Integer.parseInt(StringUtils.defaultString(eventCursor
                            .getString(eventCursor.getColumnIndexOrThrow(TableEventMaster
                                    .COLUMN_EVM_EVENT_PRIVACY)), "0")));
                    event.setIsPrivate(Integer.parseInt(StringUtils.defaultString(eventCursor
                            .getString(eventCursor.getColumnIndexOrThrow(TableEventMaster
                                    .COLUMN_EVM_IS_PRIVATE)), "0")));
                    event.setIsYearHidden(Integer.parseInt(StringUtils.defaultString(eventCursor
                            .getString(eventCursor.getColumnIndexOrThrow(TableEventMaster
                                    .COLUMN_EVM_IS_YEAR_HIDDEN)), "0")));
                    event.setEventRcType(String.valueOf(IntegerConstants
                            .RCP_TYPE_CLOUD_PHONE_BOOK));
                    arrayListEvent.add(event);
                } while (eventCursor.moveToNext());
                eventCursor.close();
            }
            profileDataOperation.setPbEvent(arrayListEvent);
        }

        //</editor-fold>

        // <editor-fold desc="Im Account">
        String imAccountQuery = "SELECT im." +
                TableImMaster.COLUMN_IM_PROTOCOL + ", im." + TableImMaster
                .COLUMN_IM_FIRST_NAME + ", im." + TableImMaster
                .COLUMN_IM_LAST_NAME + ", im." + TableImMaster
                .COLUMN_IM_PRIVACY + ", im." + TableImMaster
                .COLUMN_IM_IS_PRIVATE + ", im." + TableImMaster
                .COLUMN_IM_RECORD_INDEX_ID + ", im." + TableImMaster.COLUMN_IM_DETAIL +
                " FROM " + TableImMaster.TABLE_RC_IM_MASTER;

        Cursor imAccountCursor = db.rawQuery(imAccountQuery, null);

        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();

        // looping through all rows and adding to list
        if (imAccountCursor != null && imAccountCursor.getCount() > 0) {
            if (imAccountCursor.moveToFirst()) {
                do {
                    ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
                    imAccount.setIMAccountProtocol(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_PROTOCOL))));
                    imAccount.setIMAccountDetails(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_DETAIL))));
                    imAccount.setIMAccountFirstName(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_FIRST_NAME))));
                    imAccount.setIMAccountLastName(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_LAST_NAME))));
                    imAccount.setIMId(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_RECORD_INDEX_ID))));
                    imAccount.setIMAccountProtocol(StringUtils.defaultString(imAccountCursor
                            .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                    .COLUMN_IM_PROTOCOL))));
                    imAccount.setIMAccountPublic(Integer.parseInt(StringUtils.defaultString
                            (imAccountCursor
                                    .getString(imAccountCursor.getColumnIndex(TableImMaster
                                            .COLUMN_IM_PRIVACY)), "0")));
                    imAccount.setIMAccountIsPrivate(Integer.parseInt(StringUtils.defaultString
                            (imAccountCursor
                                    .getString(imAccountCursor.getColumnIndexOrThrow(TableImMaster
                                            .COLUMN_IM_IS_PRIVATE)), "0")));
                    imAccount.setIMRcpType(String.valueOf(IntegerConstants
                            .RCP_TYPE_CLOUD_PHONE_BOOK));
                    arrayListImAccount.add(imAccount);
                } while (imAccountCursor.moveToNext());
                imAccountCursor.close();
            }
            profileDataOperation.setPbIMAccounts(arrayListImAccount);
        }

        //</editor-fold>

        // <editor-fold desc="Address">
        String addressQuery = "SELECT address." + TableAddressMaster.COLUMN_AM_FORMATTED_ADDRESS
                + ", address." + TableAddressMaster.COLUMN_AM_ADDRESS_TYPE + ", address." +
                TableAddressMaster.COLUMN_AM_IS_PRIVATE + ", address." +
                TableAddressMaster.COLUMN_AM_GOOGLE_LATITUDE + ", address." +
                TableAddressMaster.COLUMN_AM_GOOGLE_LONGITUDE + ", address." +
                TableAddressMaster.COLUMN_AM_RECORD_INDEX_ID + ", address." +
                TableAddressMaster.COLUMN_AM_ADDRESS_PRIVACY + " FROM " + TableAddressMaster
                .TABLE_RC_ADDRESS_MASTER;

        Cursor addressCursor = db.rawQuery(addressQuery, null);

        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();

        // looping through all rows and adding to list
        if (addressCursor != null && addressCursor.getCount() > 0) {
            if (addressCursor.moveToFirst()) {
                do {
                    ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                    address.setFormattedAddress(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_FORMATTED_ADDRESS))));
                    address.setAddressType(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_ADDRESS_TYPE))));
                    address.setAddPublic(Integer.parseInt(StringUtils.defaultString(addressCursor
                            .getString(addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_ADDRESS_PRIVACY)), "0")));
                    address.setIsPrivate(Integer.parseInt(StringUtils.defaultString(addressCursor
                            .getString(addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_IS_PRIVATE)), "0")));
                    address.setAddId(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_RECORD_INDEX_ID))));
                    ArrayList<String> arrayListLatLng = new ArrayList<>();
                    arrayListLatLng.add(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_GOOGLE_LONGITUDE)), "0.0"));
                    arrayListLatLng.add(StringUtils.defaultString(addressCursor.getString
                            (addressCursor.getColumnIndexOrThrow(TableAddressMaster
                                    .COLUMN_AM_GOOGLE_LATITUDE)), "0.0"));
                    address.setGoogleLatLong(arrayListLatLng);
                    address.setRcpType(String.valueOf(IntegerConstants.RCP_TYPE_CLOUD_PHONE_BOOK));
                    arrayListAddress.add(address);
                } while (addressCursor.moveToNext());
                addressCursor.close();
            }
            profileDataOperation.setPbAddress(arrayListAddress);
        }

        //</editor-fold>

        // <editor-fold desc="Aadhar Card">
        String aadharCardQuery = "SELECT " + TableAadharMaster.COLUMN_AADHAR_ID
                + "," + TableAadharMaster.COLUMN_AADHAR_NUMBER + "," +
                TableAadharMaster.COLUMN_AADHAR_IS_VARIFIED + "," +
                TableAadharMaster.COLUMN_AADHAR_PUBLIC + " FROM " + TableAadharMaster
                .TABLE_AADHAR_MASTER;

        Cursor aadharCardCursor = db.rawQuery(aadharCardQuery, null);
        // looping through all rows and adding to list
        ProfileDataOperationAadharNumber aadharDetails = new ProfileDataOperationAadharNumber();
        if (aadharCardCursor != null && aadharCardCursor.getCount() > 0) {
            if (aadharCardCursor.moveToFirst()) {
                aadharDetails.setAadharId(aadharCardCursor.getInt(aadharCardCursor.
                        getColumnIndexOrThrow(TableAadharMaster.COLUMN_AADHAR_ID)));
                aadharDetails.setAadharNumber(aadharCardCursor.getString(aadharCardCursor.
                        getColumnIndexOrThrow(TableAadharMaster.COLUMN_AADHAR_NUMBER)));
                aadharDetails.setAadharIsVerified(aadharCardCursor.getInt(aadharCardCursor.
                        getColumnIndexOrThrow(TableAadharMaster.COLUMN_AADHAR_IS_VARIFIED)));
                aadharDetails.setAadharPublic(aadharCardCursor.getInt(aadharCardCursor.
                        getColumnIndexOrThrow(TableAadharMaster.COLUMN_AADHAR_PUBLIC)));
                aadharCardCursor.close();
            }
            profileDataOperation.setPbAadhar(aadharDetails);
        }

        //</editor-fold>

        // <editor-fold desc="Website">
        String websiteQuery = "select website." + TableWebsiteMaster.COLUMN_WM_WEBSITE_URL + " " +
                "FROM " + TableWebsiteMaster.TABLE_RC_WEBSITE_MASTER;

        Cursor websiteCursor = db.rawQuery(websiteQuery, null);

        ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();
        // looping through all rows and adding to list
        if (websiteCursor != null && websiteCursor.getCount() > 0) {
            if (websiteCursor.moveToFirst()) {
                do {
                    ProfileDataOperationWebAddress webAddress = new
                            ProfileDataOperationWebAddress();
                    webAddress.setWebAddress(StringUtils.defaultString(websiteCursor.getString
                            (websiteCursor.getColumnIndexOrThrow(TableWebsiteMaster
                                    .COLUMN_WM_WEBSITE_URL))));
                    webAddress.setWebRcpType(String.valueOf(IntegerConstants
                            .RCP_TYPE_CLOUD_PHONE_BOOK));
               *//* arrayListWebsite.add(StringUtils.defaultString(websiteCursor.getString
                        (websiteCursor.getColumnIndex(TableWebsiteMaster.COLUMN_WM_WEBSITE_URL)))
                        );*//*
                    arrayListWebsite.add(webAddress);
                } while (websiteCursor.moveToNext());
                websiteCursor.close();
            }
            profileDataOperation.setPbWebAddress(arrayListWebsite);
        }

        //</editor-fold>

        db.close();

        // return profile data operation
        return profileDataOperation;
    }*/


    private void deleteRcProfileDetail(String rcpId) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        //<editor-fold desc="TABLE_PB_PROFILE_EMAIL_MAPPING">
        db.delete(TableProfileEmailMapping.TABLE_PB_PROFILE_EMAIL_MAPPING,
                TableProfileEmailMapping.COLUMN_EPM_CLOUD_PM_ID + " = ?", new String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_PB_PROFILE_MOBILE_MAPPING">
        db.delete(TableProfileMobileMapping.TABLE_PB_PROFILE_MOBILE_MAPPING,
                TableProfileMobileMapping.COLUMN_MPM_CLOUD_PM_ID + " = ?", new String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_ADDRESS_MASTER">
        db.delete(TableAddressMaster.TABLE_RC_ADDRESS_MASTER,
                TableAddressMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?", new String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_COMMENT_MASTER">
        db.delete(TableCommentMaster.TABLE_RC_COMMENT_MASTER,
                TableCommentMaster.COLUMN_CRM_RC_PROFILE_MASTER_PM_ID + " = ?", new
                        String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_CONTACT_ACCESS_REQUEST">
        db.delete(TableRCContactRequest.TABLE_RC_CONTACT_ACCESS_REQUEST,
                TableRCContactRequest.COLUMN_CRM_RC_PROFILE_MASTER_PM_ID + " = ?", new
                        String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_EMAIL_MASTER">
        db.delete(TableEmailMaster.TABLE_RC_EMAIL_MASTER,
                TableEmailMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?", new String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_EVENT_MASTER">
        db.delete(TableEventMaster.TABLE_RC_EVENT_MASTER,
                TableEventMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?", new String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_IM_MASTER">
        db.delete(TableImMaster.TABLE_RC_IM_MASTER,
                TableImMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?", new String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_MOBILE_NUMBER_MASTER">
        db.delete(TableMobileMaster.TABLE_RC_MOBILE_NUMBER_MASTER,
                TableMobileMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?", new String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_ORGANIZATION_MASTER">
        db.delete(TableOrganizationMaster.TABLE_RC_ORGANIZATION_MASTER,
                TableOrganizationMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?", new
                        String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_PROFILE_MASTER">
        db.delete(TableProfileMaster.TABLE_RC_PROFILE_MASTER,
                TableProfileMaster.COLUMN_PM_RCP_ID + " = ?", new String[]{rcpId});
        //</editor-fold>

        // <editor-fold desc="TABLE_RC_WEBSITE_MASTER">
        db.delete(TableWebsiteMaster.TABLE_RC_WEBSITE_MASTER,
                TableWebsiteMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " = ?", new String[]{rcpId});
        //</editor-fold>


        db.close();

    }

    public void updateRcProfileDetail(Context context, int rcpId, String rawId) {
        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
        String rawIdFromRcpId = tableProfileMaster.getRawIdFromRcpId(rcpId);
        if (StringUtils.contains(rawIdFromRcpId, ",")) {
            /* Multiple Contacts with single pm_id */
            String rawIds[] = rawIdFromRcpId.split(",");
            ArrayList<String> arrayListRawIds = new ArrayList<>(Arrays.asList(rawIds));
            arrayListRawIds.remove(rawId);
            tableProfileMaster.updateRawIds(rcpId, StringUtils.join(arrayListRawIds, ","));
        } else {
            if (String.valueOf(rcpId).equalsIgnoreCase(((BaseActivity) context).getUserPmId())) {
                /* Single Contact with self registered pm_id */
                tableProfileMaster.updateRawIds(rcpId, "");
            } else {
                /* Single Contact with single pm_id */
                deleteRcProfileDetail(String.valueOf(rcpId));
            }
        }
    }

    public ArrayList<ProfileData> getRcpNumberName(String cloudPmIds) {
        ArrayList<ProfileData> arrayListProfileData = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT " + TableProfileMaster.COLUMN_PM_RCP_ID + ", " +
                TableProfileMaster.COLUMN_PM_PROFILE_IMAGE + ", " + TableProfileMaster
                .COLUMN_PM_FIRST_NAME + ", " + TableProfileMaster.COLUMN_PM_LAST_NAME + ", " +
                TableProfileMobileMapping.COLUMN_MPM_MOBILE_NUMBER + ", " +
                TableProfileEmailMapping.COLUMN_EPM_EMAIL_ID + " FROM " + TableProfileMaster
                .TABLE_RC_PROFILE_MASTER + " LEFT JOIN " + TableProfileMobileMapping
                .TABLE_PB_PROFILE_MOBILE_MAPPING + " ON " + TableProfileMaster.COLUMN_PM_RCP_ID +
                " = " + TableProfileMobileMapping.COLUMN_MPM_CLOUD_PM_ID + " LEFT JOIN " +
                TableProfileEmailMapping.TABLE_PB_PROFILE_EMAIL_MAPPING + " ON " +
                TableProfileMaster.COLUMN_PM_RCP_ID + " = " + TableProfileEmailMapping
                .COLUMN_EPM_CLOUD_PM_ID + " WHERE " + TableProfileMaster.COLUMN_PM_RCP_ID + " IN " +
                "(" + cloudPmIds + ")";

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProfileData profileData = new ProfileData();
                profileData.setTempRcpName(cursor.getString(cursor.getColumnIndexOrThrow
                        (TableProfileMaster.COLUMN_PM_FIRST_NAME)) + " " + cursor.getString
                        (cursor.getColumnIndexOrThrow(TableProfileMaster.COLUMN_PM_LAST_NAME)));
                profileData.setTempNumber(cursor.getString(cursor.getColumnIndexOrThrow
                        (TableProfileMobileMapping.COLUMN_MPM_MOBILE_NUMBER)));
                profileData.setTempEmail(cursor.getString(cursor.getColumnIndexOrThrow
                        (TableProfileEmailMapping.COLUMN_EPM_EMAIL_ID)));
                profileData.setTempRcpId(cursor.getString(cursor.getColumnIndexOrThrow
                        (TableProfileMaster.COLUMN_PM_RCP_ID)));
                profileData.setTempRcpImageURL(cursor.getString(cursor.getColumnIndexOrThrow
                        (TableProfileMaster.COLUMN_PM_PROFILE_IMAGE)));
                profileData.setTempIsRcp(true);
                arrayListProfileData.add(profileData);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        return arrayListProfileData;
    }

}