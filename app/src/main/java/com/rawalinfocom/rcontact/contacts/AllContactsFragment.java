package com.rawalinfocom.rcontact.contacts;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.AllContactListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.ProgressWheel;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.ColorBubble
        .ColorGroupSectionTitleIndicator;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.vertical
        .VerticalRecyclerViewFastScroller;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Email;
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
import com.rawalinfocom.rcontact.model.ProfileEmailMapping;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.services.ContactIdFetchService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllContactsFragment extends BaseFragment implements WsResponseListener {

    private final int CONTACT_CHUNK = 4;

    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.relative_root_all_contacts)
    RelativeLayout relativeRootAllContacts;
    @BindView(R.id.progress_all_contact)
    ProgressWheel progressAllContact;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    @BindView(R.id.scroller_all_contact)
    VerticalRecyclerViewFastScroller scrollerAllContact;
    @BindView(R.id.title_indicator)
    ColorGroupSectionTitleIndicator titleIndicator;

    ArrayList<Object> arrayListPhoneBookContacts;
    //    ArrayList<ProfileData> arrayListPhoneBookContacts;
    ArrayList<ProfileData> arrayListUserContact;
    ArrayList<String> arrayListContactId;
    ArrayList<String> arrayListContactNumbers;
    ArrayList<String> arrayListContactEmails;

//    DatabaseHandler databaseHandler;

    AllContactListAdapter allContactListAdapter;


    //<editor-fold desc="Constructors">
    public AllContactsFragment() {
        // Required empty public constructor

    }

    public static AllContactsFragment newInstance() {
        return new AllContactsFragment();
    }
    //</editor-fold>

    //<editor-fold desc="Override Methods">

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        databaseHandler = ((BaseActivity) getActivity()).databaseHandler;
        arrayListPhoneBookContacts = new ArrayList<>();
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_contacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_UPLOAD_CONTACTS">

            if (serviceType.contains(WsConstants.REQ_UPLOAD_CONTACTS)) {
                WsResponseObject uploadContactResponse = (WsResponseObject) data;
                progressAllContact.setVisibility(View.GONE);
                if (uploadContactResponse != null && StringUtils.equalsIgnoreCase
                        (uploadContactResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    /* Save synced data details in Preference */
                    String previouslySyncedData = (StringUtils.split(serviceType, "_"))[1];
                    int nextNumber = Integer.parseInt(StringUtils.defaultString
                            (previouslySyncedData, "0")) + CONTACT_CHUNK;
                    Utils.setIntegerPreference(getActivity(), AppConstants.PREF_SYNCED_CONTACTS,
                            nextNumber);

                    if (!Utils.isArraylistNullOrEmpty(uploadContactResponse
                            .getArrayListUserRcProfile())) {

                        /* Store Unique Contacts to ProfileMobileMapping */
                        storeToMobileMapping(uploadContactResponse.getArrayListUserRcProfile());

                        /* Store Unique Emails to ProfileEmailMapping */
                        storeToEmailMapping(uploadContactResponse.getArrayListUserRcProfile());

                        /* Store Profile Details to respective Table */
                        storeProfileDataToDb(uploadContactResponse.getArrayListUserRcProfile());

                    } else {

                        /* Store Unique Contacts to ProfileMobileMapping */
                        storeToMobileMapping(null);

                        /* Store Unique Contacts to ProfileMobileMapping */
                        storeToEmailMapping(null);

                    }

                    /* Call uploadContact api if there is more data to sync */
                    if (nextNumber < arrayListContactId.size()) {
                        phoneBookOperations();
                    } else {
                        Utils.showSuccessSnackbar(getActivity(), relativeRootAllContacts, "All " +
                                "Contact Synced");
                    }

                    /* Populate recycler view */
                    populateRecyclerView();

                } else {
                    if (uploadContactResponse != null) {
                        Log.e("error response", uploadContactResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "uploadContactResponse null");
                        Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
            progressAllContact.setVisibility(View.GONE);
            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(cursorListReceiver);
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        scrollerAllContact.setRecyclerView(recyclerViewContactList);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerViewContactList.setOnScrollListener(scrollerAllContact.getOnScrollListener());

        // Connect the section indicator to the scroller
        scrollerAllContact.setSectionIndicator(titleIndicator);
        titleIndicator.setTitleText("A");

        setRecyclerViewLayoutManager(recyclerViewContactList);
//        recyclerViewContactList.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<String> arrayListContactIds = Utils.getArrayListPreference(getActivity(),
                AppConstants.PREF_CONTACT_ID_SET);
        if (arrayListContactIds != null) {
            arrayListContactId = new ArrayList<>(arrayListContactIds);
            phoneBookOperations();
        } else {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(cursorListReceiver,
                    new IntentFilter(AppConstants.ACTION_CONTACT_FETCH));
            Intent contactIdFetchService = new Intent(getActivity(), ContactIdFetchService.class);
            getActivity().startService(contactIdFetchService);
        }


    }

    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    private void storeToMobileMapping(ArrayList<ProfileDataOperation> profileData) {
        if (!Utils.isArraylistNullOrEmpty(arrayListContactNumbers)) {
            TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                    (getDatabaseHandler());
            ArrayList<ProfileMobileMapping> arrayListProfileMobileMapping = new ArrayList<>();
            for (int i = 0; i < arrayListContactNumbers.size(); i++) {
                if (!tableProfileMobileMapping.getIsMobileNumberExists
                        (arrayListContactNumbers.get(i))) {

                    ProfileMobileMapping profileMobileMapping = new
                            ProfileMobileMapping();
                    profileMobileMapping.setMpmMobileNumber(arrayListContactNumbers
                            .get(i));
                    profileMobileMapping.setMpmIsRcp("0");
                    if (!Utils.isArraylistNullOrEmpty(profileData)) {
                        for (int j = 0; j < profileData.size(); j++) {
                            if (StringUtils.equalsIgnoreCase(arrayListContactNumbers.get(i),
                                    profileData.get(j).getVerifiedMobileNumber())) {
                                profileMobileMapping.setMpmCloudMnmId(profileData.get(j)
                                        .getMnmCloudId());
                                profileMobileMapping.setMpmCloudPmId(profileData.get(j)
                                        .getRcpPmId());
                                profileMobileMapping.setMpmIsRcp("1");
                            }
                        }
                    }

                    arrayListProfileMobileMapping.add(profileMobileMapping);
                }
            }
            tableProfileMobileMapping.addArrayProfileMobileMapping
                    (arrayListProfileMobileMapping);
        }


    }

    private void storeToEmailMapping(ArrayList<ProfileDataOperation> profileData) {
        if (!Utils.isArraylistNullOrEmpty(arrayListContactEmails)) {
            TableProfileEmailMapping tableProfileEmailMapping = new TableProfileEmailMapping
                    (getDatabaseHandler());
            ArrayList<ProfileEmailMapping> arrayListProfileEmailMapping = new ArrayList<>();
            for (int i = 0; i < arrayListContactEmails.size(); i++) {
                if (!tableProfileEmailMapping.getIsEmailIdExists(arrayListContactEmails.get(i))) {

                    ProfileEmailMapping profileEmailMapping = new ProfileEmailMapping();
                    profileEmailMapping.setEpmEmailId(arrayListContactEmails.get(i));
                    profileEmailMapping.setEpmIsRcp("0");
                    if (!Utils.isArraylistNullOrEmpty(profileData)) {
                        for (int j = 0; j < profileData.size(); j++) {
                            if (StringUtils.equalsIgnoreCase(arrayListContactEmails.get(i),
                                    profileData.get(j).getVerifiedEmailAddress())) {
                                profileEmailMapping.setEpmCloudEmId(profileData.get(j)
                                        .getEmCloudId());
                                profileEmailMapping.setEpmCloudPmId(profileData.get(j)
                                        .getRcpPmId());
                                profileEmailMapping.setEpmIsRcp("1");
                            }
                        }
                    }

                    arrayListProfileEmailMapping.add(profileEmailMapping);
                }
            }
            tableProfileEmailMapping.addArrayProfileEmailMapping(arrayListProfileEmailMapping);
        }


    }

    private void storeProfileDataToDb(ArrayList<ProfileDataOperation> profileData) {

        // Basic Profile Data
        TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());

        ArrayList<UserProfile> arrayListUserProfile = new ArrayList<>();
        for (int i = 0; i < profileData.size(); i++) {
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

            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = profileData.get(i)
                    .getPbPhoneNumber();
            ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
            for (int j = 0; j < arrayListPhoneNumber.size(); j++) {
                MobileNumber mobileNumber = new MobileNumber();
                mobileNumber.setMnmMobileNumber(arrayListPhoneNumber.get(j).getPhoneNumber());
                mobileNumber.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
//                arrayListPhoneNumber.get(j).
                arrayListMobileNumber.add(mobileNumber);
            }

            TableMobileMaster tableMobileMaster = new TableMobileMaster(getDatabaseHandler());
            tableMobileMaster.addArrayMobileNumber(arrayListMobileNumber);

            ArrayList<ProfileDataOperationEmail> arrayListEmailId = profileData.get(i)
                    .getPbEmailId();
            ArrayList<Email> arrayListEmail = new ArrayList<>();
            for (int j = 0; j < arrayListEmailId.size(); j++) {
                Email email = new Email();
                email.setEmEmailAddress(arrayListEmailId.get(j).getEmEmailId());
                email.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                arrayListEmail.add(email);
            }

            TableEmailMaster tableEmailMaster = new TableEmailMaster(getDatabaseHandler());
            tableEmailMaster.addArrayEmail(arrayListEmail);

            arrayListUserProfile.add(userProfile);
        }

        tableProfileMaster.addArrayProfile(arrayListUserProfile);

    }

    private void populateRecyclerView() {

        if (allContactListAdapter == null) {
            allContactListAdapter = new AllContactListAdapter(getActivity(),
                    arrayListPhoneBookContacts);
            recyclerViewContactList.setAdapter(allContactListAdapter);
        } else {
            allContactListAdapter.notifyDataSetChanged();
        }

    }

    //</editor-fold>

    //<editor-fold desc="Local Broadcast Receiver">
    private BroadcastReceiver cursorListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(AppConstants.EXTRA_LOCAL_BROADCAST_MESSAGE);
            if (StringUtils.equals(message, WsConstants.RESPONSE_STATUS_TRUE)) {
                /*HashSet<String> retrievedContactIdSet = (HashSet<String>) Utils
                        .getStringSetPreference(getActivity(), AppConstants
                                .PREF_CONTACT_ID_SET);*/
                ArrayList<String> arrayListContactIds = Utils.getArrayListPreference(getActivity(),
                        AppConstants.PREF_CONTACT_ID_SET);
                if (arrayListContactIds != null) {
                    arrayListContactId = new ArrayList<>(arrayListContactIds);
                    phoneBookOperations();
                } else {
                    Log.e("Local Broadcast Receiver onReceive: ", "Error while Retriving Ids!");
                }
            } else {
                Log.e("Local Broadcast Receiver onReceive: ", "Error while Retriving Ids!");
            }
        }
    };
    //</editor-fold>

    //<editor-fold desc="Phone book Data Cursor">

    private void phoneBookOperations() {
        arrayListUserContact = new ArrayList<>();
        arrayListContactNumbers = new ArrayList<>();
        arrayListContactEmails = new ArrayList<>();
        int previouslySyncedData = Utils.getIntegerPreference(getActivity(), AppConstants
                .PREF_SYNCED_CONTACTS, 0);
        int previousTo = previouslySyncedData + CONTACT_CHUNK;
        if (previousTo > arrayListContactId.size()) {
            previousTo = arrayListContactId.size();
        }

        int forFrom, forTo;

        if (previouslySyncedData < previousTo) {
            forFrom = previouslySyncedData;
            forTo = previousTo;
        } else {
            forFrom = 0;
            forTo = arrayListContactId.size();
        }

        for (int i = forFrom; i < forTo; i++) {

            ProfileData profileData = new ProfileData();

            String rawId = arrayListContactId.get(i);

            profileData.setLocalPhoneBookId(rawId);

            /* profileData.setGivenName(contactNameCursor.getString(contactNameCursor
            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));*/

            ProfileDataOperation operation = new ProfileDataOperation();

            //<editor-fold desc="Structured Name">
            Cursor contactStructuredNameCursor = getStructuredName(rawId);
            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

            if (contactStructuredNameCursor != null && contactStructuredNameCursor.getCount()
                    > 0) {

                /*   operation.setIsFavourite(contactNameCursor.getString(contactNameCursor
                .getColumnIndex(ContactsContract.Contacts.STARRED)));*/
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
//                arrayListOperation.add(operation);
            //</editor-fold>

            // <editor-fold desc="Starred Contact">
            Cursor starredContactCursor = getStarredStatus(rawId);

            if (starredContactCursor != null && starredContactCursor.getCount() > 0) {


                while (starredContactCursor.moveToNext()) {

                    operation.setIsFavourite(starredContactCursor.getString(starredContactCursor
                            .getColumnIndex(ContactsContract.Contacts.STARRED)));

                }
                starredContactCursor.close();
            }
            //</editor-fold>

            //<editor-fold desc="Contact Number">
            Cursor contactNumberCursor = getContactNumbers(rawId);
            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

            if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                int numberCount = 0;
                while (contactNumberCursor.moveToNext()) {

                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();

                    phoneNumber.setPhoneId(++numberCount);
                    phoneNumber.setPhoneNumber(contactNumberCursor.getString(contactNumberCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    phoneNumber.setPhoneType(getPhoneNumberType(contactNumberCursor.getInt
                            (contactNumberCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Phone.TYPE))));
                    phoneNumber.setPhonePublic(1);

                    arrayListPhoneNumber.add(phoneNumber);
                    arrayListContactNumbers.add(phoneNumber.getPhoneNumber());

                }
                contactNumberCursor.close();
            }
            operation.setPbPhoneNumber(arrayListPhoneNumber);
            //</editor-fold>

            //<editor-fold desc="Email Id">
            Cursor contactEmailCursor = getContactEmail(rawId);
            ArrayList<ProfileDataOperationEmail> arrayListEmailId = new ArrayList<>();

            if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
                int emailCount = 0;
                while (contactEmailCursor.moveToNext()) {

                    ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                    emailId.setEmId(++emailCount);
                    emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                    emailId.setEmType(getEmailType(contactEmailCursor, contactEmailCursor.getInt
                            (contactEmailCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Email.TYPE))));
                    emailId.setEmPublic(1);

                    arrayListEmailId.add(emailId);
                    arrayListContactEmails.add(emailId.getEmEmailId());
                }
                contactEmailCursor.close();
            }
            operation.setPbEmailId(arrayListEmailId);
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

            arrayListOperation.add(operation);
            profileData.setOperation(arrayListOperation);

            arrayListUserContact.add(profileData);

        }

        if (arrayListUserContact.size() > 0) {
//            arrayListPhoneBookContacts.addAll(arrayListUserContact);

            for (int i = 0; i < arrayListUserContact.size(); i++) {
                String headerLetter = StringUtils.upperCase(StringUtils.substring
                        (arrayListUserContact.get(i).getOperation().get(0).getPbNameFirst(), 0, 1));
                if (!arrayListPhoneBookContacts.contains(headerLetter)) {
                    arrayListPhoneBookContacts.add(headerLetter);
                }
                arrayListPhoneBookContacts.add(arrayListUserContact.get(i));
            }

            if (previouslySyncedData < previousTo) {
                uploadContacts(previouslySyncedData);
            } else {
                progressAllContact.setVisibility(View.GONE);
                populateRecyclerView();
            }

        } else {
            progressAllContact.setVisibility(View.GONE);
        }

    }

    private Cursor getStarredStatus(String contactId) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts.STARRED,
        };

        String selection = ContactsContract.Contacts._ID + " = ?";
        String[] selectionArgs = new String[]{contactId};

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
    }

    public Cursor getStructuredName(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.StructuredName._ID,
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

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

        return getActivity().getContentResolver().query(uri, projection, selection,
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

        String selection = ContactsContract.CommonDataKinds.Organization.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Organization.CONTENT_ITEM_TYPE};

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

        return getActivity().getContentResolver().query(uri, projection, selection,
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

        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds
                .Im.CONTENT_ITEM_TYPE};

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

        return getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
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

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void uploadContacts(int previouslySyncedData) {

        WsRequestObject uploadContactObject = new WsRequestObject();
        //TODO pmid Modification
        uploadContactObject.setPmId(((BaseActivity) getActivity()).getUserPmId());
        uploadContactObject.setProfileData(arrayListUserContact);

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS + "_" + previouslySyncedData, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CONTACTS);
        } else {
            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>


}
