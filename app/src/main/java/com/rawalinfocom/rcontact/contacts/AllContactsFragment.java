package com.rawalinfocom.rcontact.contacts;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.AllContactListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
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
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.ProgressWheel;
import com.rawalinfocom.rcontact.helper.RippleView;
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
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllContactsFragment extends BaseFragment implements WsResponseListener {

    private final int CONTACT_CHUNK = 10;

    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.relative_root_all_contacts)
    RelativeLayout relativeRootAllContacts;
    @BindView(R.id.progress_all_contact)
    ProgressWheel progressAllContact;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    /* @BindView(R.id.scroller_all_contact)
     VerticalRecyclerViewFastScroller scrollerAllContact;
     @BindView(R.id.title_indicator)
     ColorGroupSectionTitleIndicator titleIndicator;*/
    @BindView(R.id.text_total_contacts)
    TextView textTotalContacts;

    ArrayList<Object> arrayListPhoneBookContacts;
    ArrayList<String> arrayListContactHeaders;
    ArrayList<ProfileData> arrayListUserContact;
    ArrayList<String> arrayListContactId;
    ArrayList<String> arrayListContactNumbers;
    ArrayList<String> arrayListContactEmails;
    ArrayList<String> arrayListFavouriteContacts;

    MaterialDialog callConfirmationDialog, permissionConfirmationDialog;

    PhoneBookContacts phoneBookContacts;

    AllContactListAdapter allContactListAdapter;

    private View rootView;
    private boolean isReload = false;
    RContactApplication rContactApplication;

    boolean isFromSettings = false;

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
         /*LocalBroadcastManager.getInstance(getActivity()).registerReceiver(CallLogFragment
         .broadcastReceiver,
                        new IntentFilter(AppConstants.ACTION_START_CALL_LOG_INSERTION));*/

        rContactApplication = (RContactApplication) getActivity().getApplicationContext();

        if (arrayListPhoneBookContacts == null) {

            arrayListPhoneBookContacts = new ArrayList<>();
            arrayListContactHeaders = new ArrayList<>();
            arrayListFavouriteContacts = new ArrayList<>();

            arrayListContactHeaders.add(" ");

            arrayListPhoneBookContacts.add("My Profile");

            ProfileData myProfileData = new ProfileData();

            TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer.parseInt((
                    (BaseActivity) getActivity()).getUserPmId()));

            TableMobileMaster tableMobileMaster = new TableMobileMaster(getDatabaseHandler());
            MobileNumber mobileNumber = tableMobileMaster.getOwnVerifiedMobileNumbersFromPmId
                    (getActivity());

            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

            ProfileDataOperation myOperation = new ProfileDataOperation();
            myOperation.setPbNameFirst(userProfile.getPmFirstName());
            myOperation.setPbNameLast(userProfile.getPmLastName());
            myOperation.setProfileRating(userProfile.getProfileRating());
            myOperation.setTotalProfileRateUser(userProfile.getTotalProfileRateUser());

            ArrayList<ProfileDataOperationPhoneNumber> operationPhoneNumber = new ArrayList<>();
            ProfileDataOperationPhoneNumber phoneNumber = new ProfileDataOperationPhoneNumber();
            phoneNumber.setPhoneNumber(mobileNumber.getMnmMobileNumber());

            operationPhoneNumber.add(phoneNumber);

            myOperation.setPbPhoneNumber(operationPhoneNumber);

            arrayListOperation.add(myOperation);
            myProfileData.setOperation(arrayListOperation);
            arrayListPhoneBookContacts.add(myProfileData);

            phoneBookContacts = new PhoneBookContacts(getActivity());

        } else {
            isReload = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFromSettings) {
            isFromSettings = false;
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission
                    .READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                if (!isReload) {
                    init();
                }
            }
        }
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_all_contacts, container, false);
            ButterKnife.bind(this, rootView);

          /*  // Connect the recycler to the scroller (to let the scroller scroll the list)
            scrollerAllContact.setRecyclerView(recyclerViewContactList);

            // Connect the scroller to the recycler (to let the recycler scroll the scroller's
            // handle)
            recyclerViewContactList.setOnScrollListener(scrollerAllContact.getOnScrollListener());

            // Connect the section indicator to the scroller
            scrollerAllContact.setSectionIndicator(titleIndicator);*/

            setRecyclerViewLayoutManager(recyclerViewContactList);

        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission
                .READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                    AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            if (!isReload) {
                init();
            }
        }
    }


    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {


        try {
            if (error == null && getActivity() != null) {

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
                            storeProfileDataToDb(uploadContactResponse.getArrayListUserRcProfile(),
                                    uploadContactResponse.getArrayListMapping());

                        } else {

                        /* Store Unique Contacts to ProfileMobileMapping */
                            storeToMobileMapping(null);

                        /* Store Unique Contacts to ProfileMobileMapping */
                            storeToEmailMapping(null);

                        }

                 /* Call uploadContact api if there is more data to sync */
                        if (nextNumber < arrayListContactId.size()) {
//                            phoneBookOperations();
                            textTotalContacts.setText(previouslySyncedData + " Contacts");
                        } else {
                            textTotalContacts.setText(arrayListContactId.size() + " Contacts");
                            Utils.showSuccessSnackBar(getActivity(), relativeRootAllContacts,
                                    "All " +
                                            "Contact Synced");
                            Utils.setStringPreference(getActivity(), AppConstants
                                    .PREF_CONTACT_LAST_SYNC_TIME, String.valueOf(System
                                    .currentTimeMillis() - 10000));
//                        sendBroadCastToStartCallLogInsertion();
                            Utils.setBooleanPreference(getActivity(), AppConstants
                                    .PREF_CONTACT_SYNCED, true);

                            Intent localBroadcastIntent = new Intent(AppConstants
                                    .ACTION_LOCAL_BROADCAST_CALL_LOG_SYNC);
                            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                                    .getInstance(getActivity());
                            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                        }

                    /* Populate recycler view */
                        populateRecyclerView();

                    } else {
                        if (uploadContactResponse != null) {
                            Log.e("error response", uploadContactResponse.getMessage());
                        } else {
                            Log.e("onDeliveryResponse: ", "uploadContactResponse null");
                            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts,
                                    getString(R
                                            .string.msg_try_later));
                        }
                    }
                }
                //</editor-fold>

                // <editor-fold desc="REQ_SEND_INVITATION">

                else if (serviceType.contains(WsConstants.REQ_SEND_INVITATION)) {
                    WsResponseObject inviteContactResponse = (WsResponseObject) data;
                    if (inviteContactResponse != null && StringUtils.equalsIgnoreCase
                            (inviteContactResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                        Utils.showSuccessSnackBar(getActivity(), relativeRootAllContacts,
                                "Invitation" +
                                        " sent successfully");
                    } else {
                        if (inviteContactResponse != null) {
                            Log.e("error response", inviteContactResponse.getMessage());
                        } else {
                            Log.e("onDeliveryResponse: ", "uploadContactResponse null");
                            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts,
                                    getString(R
                                            .string.msg_try_later));
                        }
                    }
                }
                //</editor-fold>

            } else {
                progressAllContact.setVisibility(View.GONE);
                Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, "" + (error !=
                        null ?
                        error.getLocalizedMessage() : null));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*private void sendBroadCastToStartCallLogInsertion(){
        Intent intent = new Intent(AppConstants.ACTION_START_CALL_LOG_INSERTION);
        intent.putExtra(AppConstants.EXTRA_CALL_LOG_BROADCAST_KEY, true);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {

                    // Permission Granted

                    if (!isReload) {
                        init();
                    }


                } else {

                    // Permission Denied
//                    getActivity().onBackPressed();
                    showPermissionConfirmationDialog();

                }
            }
            break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(cursorListReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        allContactListAdapter = null;
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        textTotalContacts.setTypeface(Utils.typefaceSemiBold(getActivity()));


     /*   VerticalRecyclerViewFastScroller scrollerAllContact = new
                VerticalRecyclerViewFastScroller(getActivity());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        relativeRootAllContacts.addView(scrollerAllContact);*/

        // Connect the recycler to the scroller (to let the scroller scroll the list)
      /* scrollerAllContact.setRecyclerView(recyclerViewContactList);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerViewContactList.setOnScrollListener(scrollerAllContact.getOnScrollListener());

        // Connect the section indicator to the scroller
        scrollerAllContact.setSectionIndicator(titleIndicator);

        setRecyclerViewLayoutManager(recyclerViewContactList);*/

        initSwipe();

      /*  ArrayList<String> arrayListContactIds = Utils.getArrayListPreference(getActivity(),
                AppConstants.PREF_CONTACT_ID_SET);
        if (arrayListContactIds != null) {
            arrayListContactId = new ArrayList<>(arrayListContactIds);
            if (rContactApplication.getArrayListAllPhoneBookContacts().size() <= 0) {
//                phoneBookOperations();
            } else {
                progressAllContact.setVisibility(View.GONE);
                arrayListPhoneBookContacts = rContactApplication.getArrayListAllPhoneBookContacts();
                arrayListContactHeaders = rContactApplication.getArrayListAllContactHeaders();
                populateRecyclerView();
            }
        } else {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(cursorListReceiver,
                    new IntentFilter(AppConstants.ACTION_CONTACT_FETCH));
            Intent contactIdFetchService = new Intent(getActivity(), com.rawalinfocom.rcontact
                    .services.ContactIdFetchService.class);
            getActivity().startService(contactIdFetchService);
        }*/

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
                if (!tableProfileMobileMapping.getIsMobileNumberExists(arrayListContactNumbers
                        .get(i))) {

                    ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
                    profileMobileMapping.setMpmMobileNumber(arrayListContactNumbers.get(i));
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
                            if (!Utils.isArraylistNullOrEmpty(profileData.get(j)
                                    .getVerifiedEmailIds())) {
                                for (int k = 0; k < profileData.get(j).getVerifiedEmailIds().size();
                                     k++) {
                                    if (StringUtils.equalsIgnoreCase(arrayListContactEmails.get(i),
                                            profileData.get(j).getVerifiedEmailIds().get(k)
                                                    .getEmEmailId())) {
                                        profileEmailMapping.setEpmCloudEmId(String.valueOf
                                                (profileData.get(j).getVerifiedEmailIds().get(k)
                                                        .getEmId()));
                                        profileEmailMapping.setEpmCloudPmId(profileData.get(j)
                                                .getRcpPmId());
                                        profileEmailMapping.setEpmIsRcp("1");
                                    }
                                }
                            }
                        }
                    }
                    arrayListProfileEmailMapping.add(profileEmailMapping);
                }
            }
            tableProfileEmailMapping.addArrayProfileEmailMapping(arrayListProfileEmailMapping);
        }
    }

    private void storeProfileDataToDb(ArrayList<ProfileDataOperation> profileData,
                                      ArrayList<ProfileData> mapping) {

        // Hashmap with key as rcpId and value as rawId/s
        HashMap<String, String> mapLocalRcpId = new HashMap<>();

        for (int i = 0; i < mapping.size(); i++) {
            if (mapping.get(i).getRcpPmId().size() > 0) {
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
            }
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
            userProfile.setTotalProfileRateUser(profileData.get(i).getTotalProfileRateUser());

            if (mapLocalRcpId.containsKey(profileData.get(i).getRcpPmId())) {
                userProfile.setPmRawId(mapLocalRcpId.get(profileData.get(i).getRcpPmId()));
            }

            if (tableProfileMaster.getRcpIdCount(Integer.parseInt(userProfile.getPmRcpId())) <= 0) {


                arrayListUserProfile.add(userProfile);
                tableProfileMaster.addArrayProfile(arrayListUserProfile);
                //</editor-fold>

                //<editor-fold desc="Mobile Master">
                ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = profileData.get(i)
                        .getPbPhoneNumber();
                ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
                for (int j = 0; j < arrayListPhoneNumber.size(); j++) {

                    MobileNumber mobileNumber = new MobileNumber();
                    mobileNumber.setMnmRecordIndexId(arrayListPhoneNumber.get(j).getPhoneId());
                    mobileNumber.setMnmMobileNumber(arrayListPhoneNumber.get(j).getPhoneNumber());
                    mobileNumber.setMnmNumberType(arrayListPhoneNumber.get(j).getPhoneType());
                    mobileNumber.setMnmNumberPrivacy(String.valueOf(arrayListPhoneNumber.get(j)
                            .getPhonePublic()));
                    mobileNumber.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                    if (StringUtils.equalsIgnoreCase(profileData.get(i).getVerifiedMobileNumber(),
                            mobileNumber.getMnmMobileNumber())) {
                        mobileNumber.setMnmIsPrimary(String.valueOf(IntegerConstants
                                .RCP_TYPE_PRIMARY));
                    } else {
                        mobileNumber.setMnmIsPrimary(String.valueOf(IntegerConstants
                                .RCP_TYPE_SECONDARY));
                    }
//                arrayListPhoneNumber.get(j).
                    arrayListMobileNumber.add(mobileNumber);
                }

                TableMobileMaster tableMobileMaster = new TableMobileMaster(getDatabaseHandler());
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

                        email.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());

                        if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getVerifiedEmailIds
                                ())) {
                            for (int k = 0; k < profileData.get(i).getVerifiedEmailIds().size();
                                 k++) {
                                if (StringUtils.equalsIgnoreCase(profileData.get(i)
                                        .getVerifiedEmailIds().get(k).getEmEmailId(), email
                                        .getEmEmailAddress())) {
//                                email.setEmIsPrimary(String.valueOf(getActivity().getResources()
//                                        .getInteger(R.integer.rcp_type_primary)));
                                    email.setEmIsVerified("1");
                                } else {
//                                email.setEmIsPrimary(String.valueOf(getActivity().getResources()
//                                        .getInteger(R.integer.rcp_type_secondary)));
                                    email.setEmIsVerified("0");
                                }
                            }
                        }
                        arrayListEmail.add(email);
                    }

                    TableEmailMaster tableEmailMaster = new TableEmailMaster(getDatabaseHandler());
                    tableEmailMaster.addArrayEmail(arrayListEmail);
                }
                //</editor-fold>

                //<editor-fold desc="Organization Master">
                if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbOrganization())) {
                    ArrayList<ProfileDataOperationOrganization> arrayListOrganization = profileData
                            .get(i).getPbOrganization();
                    ArrayList<Organization> organizationList = new ArrayList<>();
                    for (int j = 0; j < arrayListOrganization.size(); j++) {
                        Organization organization = new Organization();
                        organization.setOmRecordIndexId(arrayListOrganization.get(j).getOrgId());
                        organization.setOmOrganizationCompany(arrayListOrganization.get(j)
                                .getOrgName
                                        ());
//                    organization.setOmOrganizationType(arrayListOrganization.get(j).getOrgType());
//                    organization.setOmOrganizationTitle(arrayListOrganization.get(j).getOrgName
// ());
//                    organization.setOmOrganizationDepartment(arrayListOrganization.get(j)
//                            .getOrgDepartment());
//                    organization.setOmJobDescription(arrayListOrganization.get(j)
//                            .getOrgJobTitle());
//                    organization.setOmOfficeLocation(arrayListOrganization.get(j)
//                            .getOrgOfficeLocation());
                        organization.setOmOrganizationDesignation(arrayListOrganization.get(j)
                                .getOrgJobTitle());
                        organization.setOmIsCurrent(String.valueOf(arrayListOrganization.get(j)
                                .getIsCurrent()));
                        organization.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        organizationList.add(organization);
                    }

                    TableOrganizationMaster tableOrganizationMaster = new TableOrganizationMaster
                            (getDatabaseHandler());
                    tableOrganizationMaster.addArrayOrganization(organizationList);
                }
                //</editor-fold>

                // <editor-fold desc="Website Master">
                if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbWebAddress())) {
//                ArrayList<String> arrayListWebsite = profileData.get(i).getPbWebAddress();
                    ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = profileData.get(i)
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
                            (getDatabaseHandler
                                    ());
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
                        address.setAmFormattedAddress(arrayListAddress.get(j).getFormattedAddress
                                ());
                        address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                        address.setAmPostCode(arrayListAddress.get(j).getPostCode());
                        address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                        address.setAmStreet(arrayListAddress.get(j).getStreet());
                        address.setAmAddressType(arrayListAddress.get(j).getAddressType());
//                        address.setAmGoogleLatitude(arrayListAddress.get(j).getGoogleLatitude());
//                        address.setAmGoogleLongitude(arrayListAddress.get(j).getGoogleLongitude
// ());
//                        address.setAmGoogleAddress(arrayListAddress.get(j).getGoogleAddress());
                        address.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        address.setAmAddressPrivacy(String.valueOf(arrayListAddress.get(j)
                                .getAddPublic()));
                        addressList.add(address);
                    }

                    TableAddressMaster tableAddressMaster = new TableAddressMaster
                            (getDatabaseHandler
                                    ());
                    tableAddressMaster.addArrayAddress(addressList);
                }
                //</editor-fold>

                // <editor-fold desc="Im Account Master">
                if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbIMAccounts())) {
                    ArrayList<ProfileDataOperationImAccount> arrayListImAccount = profileData.get(i)
                            .getPbIMAccounts();
                    ArrayList<ImAccount> imAccountsList = new ArrayList<>();
                    for (int j = 0; j < arrayListImAccount.size(); j++) {
                        ImAccount imAccount = new ImAccount();
                        imAccount.setImImDetail(arrayListImAccount.get(j).getIMAccountDetails());
                        imAccount.setImRecordIndexId(arrayListImAccount.get(j).getIMId());
//                    imAccount.setImImType(arrayListImAccount.get(j).getIMAccountType());
                        imAccount.setImImProtocol(arrayListImAccount.get(j).getIMAccountProtocol());
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
                        event.setEvmEventPrivacy(String.valueOf(arrayListEvent.get(j)
                                .getEventPublic()));
                        event.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        eventList.add(event);
                    }

                    TableEventMaster tableEventMaster = new TableEventMaster(getDatabaseHandler());
                    tableEventMaster.addArrayEvent(eventList);
                }
                //</editor-fold>
            }
        }
    }

    private void populateRecyclerView() {

        if (allContactListAdapter == null) {
            allContactListAdapter = new AllContactListAdapter(this,
                    arrayListPhoneBookContacts, arrayListContactHeaders);
            recyclerViewContactList.setAdapter(allContactListAdapter);

            setRecyclerViewLayoutManager(recyclerViewContactList);

        } else {
            allContactListAdapter.notifyDataSetChanged();
        }

    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String actionNumber = StringUtils.defaultString(((AllContactListAdapter
                        .AllContactViewHolder) viewHolder).textContactNumber.getText()
                        .toString());
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                  /*  smsIntent.setData(Uri.parse("sms:" + ((ProfileData)
                            arrayListPhoneBookContacts.get(position)).getOperation().get(0)
                            .getPbPhoneNumber().get(0).getPhoneNumber()));*/
                    smsIntent.setData(Uri.parse("sms:" + actionNumber));
                    startActivity(smsIntent);

                } else {
                    showCallConfirmationDialog(actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        allContactListAdapter.notifyDataSetChanged();
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
                if (viewHolder instanceof AllContactListAdapter.ContactHeaderViewHolder) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                Paint p = new Paint();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(ContextCompat.getColor(getActivity(), R.color
                                .darkModerateLimeGreen));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView
                                .getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_call);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float)
                                itemView.getTop() + width, (float) itemView.getLeft() + 2 *
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(ContextCompat.getColor(getActivity(), R.color.brightOrange));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float)
                                itemView.getTop(), (float) itemView.getRight(), (float) itemView
                                .getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_sms);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width, (float) itemView.getRight() -
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewContactList);
    }


    private void showCallConfirmationDialog(final String number) {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        callConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        callConfirmationDialog.dismissDialog();
                       /* Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                number));
                        startActivity(intent);*/
                        Utils.callIntent(getActivity(), number);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + number + "?");

        callConfirmationDialog.showDialog();

    }

    private void showPermissionConfirmationDialog() {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        permissionConfirmationDialog.dismissDialog();
                        getActivity().finish();
                        break;

                    case R.id.rippleRight:
                        permissionConfirmationDialog.dismissDialog();
                        isFromSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getActivity().getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        };

        permissionConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        permissionConfirmationDialog.setTitleVisibility(View.GONE);
        permissionConfirmationDialog.setLeftButtonText("Cancel");
        permissionConfirmationDialog.setRightButtonText("OK");
        permissionConfirmationDialog.setDialogBody("Contact read permission is required of this " +
                "app. Do you want to try again?");

        permissionConfirmationDialog.showDialog();

    }

    //</editor-fold>

    //<editor-fold desc="Local Broadcast Receiver">
 /*   private BroadcastReceiver cursorListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(AppConstants.EXTRA_LOCAL_BROADCAST_MESSAGE);
            if (StringUtils.equals(message, WsConstants.RESPONSE_STATUS_TRUE)) {
                *//*HashSet<String> retrievedContactIdSet = (HashSet<String>) Utils
                        .getStringSetPreference(getActivity(), AppConstants
                                .PREF_CONTACT_ID_SET);*//*
                ArrayList<String> arrayListContactIds = Utils.getArrayListPreference(getActivity
                        (), AppConstants.PREF_CONTACT_ID_SET);
                if (arrayListContactIds != null) {
                    arrayListContactId = new ArrayList<>(arrayListContactIds);
//                    phoneBookOperations();
                } else {
                    Log.e("Local onReceive: ", "Error while Retriving Ids!");
                }
            } else {
                Log.e("Local onReceive: ", "Error while Retriving Ids!");
            }
        }
    };*/
    //</editor-fold>

    //<editor-fold desc="Phone book Data Cursor">
    private String getPhotoUrlFromRawId(String phoneNumber) {
        String photoThumbUrl = "";
        try {

            ContentResolver contentResolver = getActivity().getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                    photoThumbUrl = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.PHOTO_THUMBNAIL_URI));
//                Log.d("LocalPBId", "contactMatch id: " + numberId + " of " + contactName);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoThumbUrl;
    }

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
            String isFavourite = "0";

            profileData.setLocalPhoneBookId(rawId);

            /* profileData.setGivenName(contactNameCursor.getString(contactNameCursor
            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));*/

            ProfileDataOperation operation = new ProfileDataOperation();
            operation.setFlag(IntegerConstants.SYNC_INSERT_CONTACT);

            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();
            //<editor-fold desc="Structured Name">
          /*  Cursor contactStructuredNameCursor = phoneBookContacts.getStructuredName(rawId);


            if (contactStructuredNameCursor != null && contactStructuredNameCursor.getCount() > 0) {

                *//*   operation.setIsFavourite(contactNameCursor.getString(contactNameCursor
                .getColumnIndex(ContactsContract.Contacts.STARRED)));*//*
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
            }*/
//                arrayListOperation.add(operation);
            //</editor-fold>

            // <editor-fold desc="Starred Contact">
            Cursor starredContactCursor = phoneBookContacts.getStarredStatus(rawId);

            if (starredContactCursor != null && starredContactCursor.getCount() > 0) {

                if (starredContactCursor.moveToNext()) {
                    isFavourite = starredContactCursor.getString(starredContactCursor
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
//                int numberCount = 0;
                while (contactNumberCursor.moveToNext()) {

                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();

//                    phoneNumber.setPhoneId(String.valueOf(++numberCount));
                    phoneNumber.setPhoneNumber(Utils.getFormattedNumber(getActivity(),
                            contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.NUMBER))));
                    phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                            (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.TYPE))));
//                    phoneNumber.setPhonePublic(1);
                    profileData.setProfileUrl(getPhotoUrlFromRawId(Utils.getFormattedNumber
                            (getActivity(),
                                    contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                            (ContactsContract.CommonDataKinds.Phone.NUMBER)))));
                    phoneNumber.setPhonePublic(IntegerConstants.PRIVACY_MY_CONTACT);

                    arrayListPhoneNumber.add(phoneNumber);

                    if (!arrayListContactNumbers.contains(Utils.getFormattedNumber(getActivity(),
                            phoneNumber.getPhoneNumber()))) {
                        arrayListContactNumbers.add(Utils.getFormattedNumber(getActivity(),
                                phoneNumber.getPhoneNumber()));
                    }
                    if (isFavourite.equalsIgnoreCase("1")) {
                        arrayListFavouriteContacts.add(Utils.getFormattedNumber(getActivity(),
                                phoneNumber.getPhoneNumber()));
                    }

                }
                contactNumberCursor.close();
            }
            operation.setPbPhoneNumber(arrayListPhoneNumber);
            //</editor-fold>

            //<editor-fold desc="Email Id">
            Cursor contactEmailCursor = phoneBookContacts.getContactEmail(rawId);
            ArrayList<ProfileDataOperationEmail> arrayListEmailId = new ArrayList<>();

            if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
//                int emailCount = 0;
                while (contactEmailCursor.moveToNext()) {

                    ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

//                    emailId.setEmId(String.valueOf(++emailCount));
                    emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                    emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                            contactEmailCursor.getInt
                                    (contactEmailCursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Email.TYPE))));
                    emailId.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);

                    arrayListEmailId.add(emailId);
                    arrayListContactEmails.add(emailId.getEmEmailId());
                    if (isFavourite.equalsIgnoreCase("1")) {
                        arrayListFavouriteContacts.add(emailId.getEmEmailId());
                    }
                }
                contactEmailCursor.close();
            }
            operation.setPbEmailId(arrayListEmailId);
            //</editor-fold>

            //<editor-fold desc="Nick Name">
           /* Cursor contactNickNameCursor = phoneBookContacts.getContactNickName(rawId);

            if (contactNickNameCursor != null && contactNickNameCursor.getCount() > 0) {
                while (contactNickNameCursor.moveToNext()) {

                    operation.setPbNickname(contactNickNameCursor.getString
                            (contactNickNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Nickname.DATA1)));

                }
                contactNickNameCursor.close();
            }*/
            //</editor-fold>

            //<editor-fold desc="Note">
          /*  Cursor contactNoteCursor = phoneBookContacts.getContactNote(rawId);

            if (contactNoteCursor != null && contactNoteCursor.getCount() > 0) {
                while (contactNoteCursor.moveToNext()) {

                    operation.setPbNote(contactNoteCursor.getString(contactNoteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Note.DATA1)));

                }
                contactNoteCursor.close();
            }*/
            //</editor-fold>

            //<editor-fold desc="Website">
            Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(rawId);
//            ArrayList<String> arrayListWebsite = new ArrayList<>();
            ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();

            if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
//                int websiteCount = 0;
                while (contactWebsiteCursor.moveToNext()) {

                    ProfileDataOperationWebAddress webAddress = new
                            ProfileDataOperationWebAddress();

//                    webAddress.setWebId(String.valueOf(++websiteCount));
                    webAddress.setWebAddress(contactWebsiteCursor.getString(contactWebsiteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                    webAddress.setWebType(phoneBookContacts.getWebsiteType(contactWebsiteCursor,
                            (contactWebsiteCursor.getInt(contactWebsiteCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Website.TYPE)))));
                    webAddress.setWebPublic(IntegerConstants.PRIVACY_EVERYONE);

                   /* String website = contactWebsiteCursor.getString(contactWebsiteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));*/

//                    arrayListWebsite.add(website);
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

//                int organizationCount = 0;

                while (contactOrganizationCursor.moveToNext()) {

                    ProfileDataOperationOrganization organization = new
                            ProfileDataOperationOrganization();

//                    organization.setOrgId(String.valueOf(++organizationCount));
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
                    organization.setIsCurrent(1);
                    organization.setOrgPublic(IntegerConstants.PRIVACY_EVERYONE);

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
//                int addressCount = 0;
                while (contactAddressCursor.moveToNext()) {

                    ProfileDataOperationAddress address = new ProfileDataOperationAddress();

//                    address.setAddId(String.valueOf(++addressCount));
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
                    address.setAddPublic(IntegerConstants.PRIVACY_MY_CONTACT);

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

//                int imCount = 0;
                while (contactImCursor.moveToNext()) {

                    ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();

//                    imAccount.setIMId(String.valueOf(++imCount));
                    imAccount.setIMAccountDetails(contactImCursor.getString(contactImCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                    imAccount.setIMAccountType(phoneBookContacts.getImAccountType(contactImCursor,
                            contactImCursor.getInt(contactImCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Im.TYPE))));

                    imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                            (contactImCursor.getInt((contactImCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Im.PROTOCOL)))));

                    imAccount.setIMAccountPublic(IntegerConstants.PRIVACY_MY_CONTACT);


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
//                int eventCount = 0;
                while (contactEventCursor.moveToNext()) {

                    ProfileDataOperationEvent event = new ProfileDataOperationEvent();

//                    event.setEventId(String.valueOf(++eventCount));
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

                    /*event.setEventDate(contactEventCursor.getString(contactEventCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                    .START_DATE)));*/
                    event.setEventDateTime(eventDate);

                    event.setEventPublic(IntegerConstants.PRIVACY_MY_CONTACT);

                    arrayListEvent.add(event);

                }
                contactEventCursor.close();
            }

            operation.setPbEvent(arrayListEvent);
            //</editor-fold>

            //<editor-fold desc="Relation">
            /*Cursor contactRelationCursor = phoneBookContacts.getContactRelationShip(rawId);
            ArrayList<ProfileDataOperationRelationship> arrayListRelationship = new
                    ArrayList<>();

            if (contactRelationCursor != null && contactRelationCursor.getCount() > 0) {
                while (contactRelationCursor.moveToNext()) {

                    ProfileDataOperationRelationship relationship = new
                            ProfileDataOperationRelationship();

                    relationship.setRelationshipDetails(contactRelationCursor.getString
                            (contactRelationCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Relation.NAME)));
                    relationship.setRelationshipType(phoneBookContacts.getRelationType
                            (contactRelationCursor,
                                    contactRelationCursor.getInt((contactRelationCursor
                                            .getColumnIndex(ContactsContract.CommonDataKinds
                                                    .Relation
                                                    .TYPE)))));
                    relationship.setRelationshipPublic("1");

                    arrayListRelationship.add(relationship);

                }
                contactRelationCursor.close();
            }

            operation.setPbRelationship(arrayListRelationship);*/
            //</editor-fold>

            arrayListOperation.add(operation);
            profileData.setOperation(arrayListOperation);

            arrayListUserContact.add(profileData);

            String headerLetter = StringUtils.upperCase(StringUtils.substring
                    (profileData.getOperation().get(0).getPbNameFirst(), 0, 1));
            headerLetter = StringUtils.length(headerLetter) > 0 ? headerLetter : "#";
            if (!arrayListPhoneBookContacts.contains(headerLetter)) {
                arrayListContactHeaders.add(headerLetter);
                arrayListPhoneBookContacts.add(headerLetter);
            }
            arrayListPhoneBookContacts.add(profileData);

        }

        Utils.setArrayListPreference(getActivity(), AppConstants
                .PREF_FAVOURITE_CONTACT_NUMBER_EMAIL, arrayListFavouriteContacts);

        if (arrayListUserContact.size() > 0) {
//            arrayListPhoneBookContacts.addAll(arrayListUserContact);

         /*   for (int i = 0; i < arrayListUserContact.size(); i++) {
                String headerLetter = StringUtils.upperCase(StringUtils.substring
                        (arrayListUserContact.get(i).getOperation().get(0).getPbNameFirst(), 0, 1));
                headerLetter = StringUtils.length(headerLetter) > 0 ? headerLetter : "#";
                if (!arrayListPhoneBookContacts.contains(headerLetter)) {
                    arrayListContactHeaders.add(headerLetter);
                    arrayListPhoneBookContacts.add(headerLetter);
                }
                arrayListPhoneBookContacts.add(arrayListUserContact.get(i));

            }*/

            rContactApplication.setArrayListAllPhoneBookContacts(arrayListPhoneBookContacts);
            rContactApplication.setArrayListAllContactHeaders(arrayListContactHeaders);

            if (previouslySyncedData < previousTo) {
                uploadContacts(previouslySyncedData);
            } else {
                textTotalContacts.setText(arrayListContactId.size() + " Contacts");
                progressAllContact.setVisibility(View.GONE);
                populateRecyclerView();
            }

        } else {
            progressAllContact.setVisibility(View.GONE);
        }

    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void uploadContacts(int previouslySyncedData) {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setPmId(Integer.parseInt(((BaseActivity) getActivity()).getUserPmId()));
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
