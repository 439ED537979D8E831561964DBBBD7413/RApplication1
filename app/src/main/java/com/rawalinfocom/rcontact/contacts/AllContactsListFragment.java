package com.rawalinfocom.rcontact.contacts;


import android.Manifest;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LongSparseArray;
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
import com.rawalinfocom.rcontact.adapters.AllContactAdapter;
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
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllContactsListFragment extends BaseFragment implements LoaderManager
        .LoaderCallbacks<Cursor>, WsResponseListener {

    private final int CONTACT_CHUNK = 2;

    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.relative_root_all_contacts)
    RelativeLayout relativeRootAllContacts;
    @BindView(R.id.progress_all_contact)
    ProgressWheel progressAllContact;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    /*    @BindView(R.id.scroller_all_contact)
        VerticalRecyclerViewFastScroller scrollerAllContact;
        @BindView(R.id.title_indicator)
        ColorGroupSectionTitleIndicator titleIndicator;*/
    @BindView(R.id.text_total_contacts)
    TextView textTotalContacts;

    ArrayList<Object> arrayListPhoneBookContacts;
    ArrayList<String> arrayListContactHeaders;
    ArrayList<ProfileData> arrayListUserContact = new ArrayList<>();
    ArrayList<ProfileData> arrayListSyncUserContact = new ArrayList<>();
    ArrayList<String> arrayListContactId;
    ArrayList<String> arrayListContactNumbers;
    ArrayList<String> arrayListContactEmails;
    ArrayList<String> arrayListFavouriteContacts;

    LongSparseArray<ProfileData> array = new LongSparseArray<>();

    MaterialDialog callConfirmationDialog, permissionConfirmationDialog;

    PhoneBookContacts phoneBookContacts;

    //    AllContactListAdapter allContactListAdapter;
    AllContactAdapter allContactListAdapter;

    private View rootView;
    private boolean isReload = false;
    RContactApplication rContactApplication;
    int lastSyncedData = 0;

    boolean isFromSettings = false;
    int settingRequestPermission = 0;
    private String callNumber = "";

    //<editor-fold desc="Constructors">

    public AllContactsListFragment() {
        // Required empty public constructor

    }

    public static AllContactsListFragment newInstance() {
        return new AllContactsListFragment();
    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        phoneBookContacts = new PhoneBookContacts(getActivity());

        rContactApplication = (RContactApplication) getActivity().getApplicationContext();

        lastSyncedData = Utils.getIntegerPreference(getActivity(), AppConstants
                .PREF_SYNCED_CONTACTS, 0);

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

            myProfileData.setTempFirstName(userProfile.getPmFirstName());
            myProfileData.setTempLastName(userProfile.getPmLastName());
            myProfileData.setTempNumber(mobileNumber.getMnmMobileNumber());
            myProfileData.setTempIsRcp(true);
            myProfileData.setTempRcpId(((BaseActivity) getActivity()).getUserPmId());

           /* ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

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
            myProfileData.setOperation(arrayListOperation);*/
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
            if (settingRequestPermission == AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission
                        .READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    if (!isReload) {
                        init();
                    }
                }
            }
            /*else if (settingRequestPermission == AppConstants
                    .MY_PERMISSIONS_REQUEST_READ_CONTACTS) {

            }*/
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

            // Connect the recycler to the scroller (to let the scroller scroll the list)
//            scrollerAllContact.setRecyclerView(recyclerViewContactList);

            // Connect the scroller to the recycler (to let the recycler scroll the scroller's
            // handle)
//            recyclerViewContactList.setOnScrollListener(scrollerAllContact.getOnScrollListener());

            // Connect the section indicator to the scroller
//            scrollerAllContact.setSectionIndicator(titleIndicator);

//            setRecyclerViewLayoutManager(recyclerViewContactList);

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

                 /*   *//* Save synced data details in Preference *//*
                        String previouslySyncedData = (StringUtils.split(serviceType, "_"))[1];
                        int nextNumber = Integer.parseInt(StringUtils.defaultString
                                (previouslySyncedData, "0")) + CONTACT_CHUNK;
                        Utils.setIntegerPreference(getActivity(), AppConstants.PREF_SYNCED_CONTACTS,
                                nextNumber);

                        if (!Utils.isArraylistNullOrEmpty(uploadContactResponse
                                .getArrayListUserRcProfile())) {

                        *//* Store Unique Contacts to ProfileMobileMapping *//*
                            storeToMobileMapping(uploadContactResponse.getArrayListUserRcProfile());

                        *//* Store Unique Emails to ProfileEmailMapping *//*
                            storeToEmailMapping(uploadContactResponse.getArrayListUserRcProfile());

                        *//* Store Profile Details to respective Table *//*
                            storeProfileDataToDb(uploadContactResponse.getArrayListUserRcProfile(),
                                    uploadContactResponse.getArrayListMapping());

                        } else {

                        *//* Store Unique Contacts to ProfileMobileMapping *//*
                            storeToMobileMapping(null);

                        *//* Store Unique Contacts to ProfileMobileMapping *//*
                            storeToEmailMapping(null);

                        }

                 *//* Call uploadContact api if there is more data to sync *//*
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
                                    .PREF_SYNC_CALL_LOG, true);

                            Intent localBroadcastIntent = new Intent(AppConstants
                                    .ACTION_LOCAL_BROADCAST_CALL_LOG_SYNC);
                            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                                    .getInstance(getActivity());
                            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                        }

                    *//* Populate recycler view *//*
                        populateRecyclerView();*/

                        lastSyncedData = lastSyncedData + CONTACT_CHUNK;
                        /*textSyncedContactCount.setText("Synced: " + String.valueOf
                        (lastSyncedData) + " Contacts");*/
                        Utils.setIntegerPreference(getActivity(), AppConstants.PREF_SYNCED_CONTACTS,
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

                            Utils.showSuccessSnackBar(getActivity(), relativeRootAllContacts,
                                    "All Contact Synced");
                            Utils.setStringPreference(getActivity(), AppConstants
                                    .PREF_CONTACT_LAST_SYNC_TIME, String.valueOf(System
                                    .currentTimeMillis() - 10000));
                            Utils.setBooleanPreference(getActivity(), AppConstants
                                    .PREF_CONTACT_SYNCED, true);
                            getRcpDetail();
                        /*    AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    TableProfileMaster tableProfileMaster = new
                                            TableProfileMaster(getDatabaseHandler());
                                    ArrayList<String> arrayListIds = tableProfileMaster
                                            .getAllRcpId();
                                    for (int i = 2; i < arrayListPhoneBookContacts.size(); i++) {
                                        if (arrayListPhoneBookContacts.get(i) instanceof
                                                ProfileData) {
                                            if (arrayListIds.contains(((ProfileData)
                                                    arrayListPhoneBookContacts.get(i))
                                                    .getLocalPhoneBookId())) {
                                                ((ProfileData) arrayListPhoneBookContacts.get(i))
                                                        .setTempIsRcp(true);
                                                *//*String name = tableProfileMaster
                                                * .getNameFromRawId
                                                        (((ProfileData)
                                                                arrayListPhoneBookContacts.get(i)
                                                        ).getLocalPhoneBookId());
                                                ((ProfileData) arrayListPhoneBookContacts.get(i))
                                                        .setTempRcpName(name);*//*
                                                ArrayList<UserProfile> userProfiles = new
                                                        ArrayList<>();
                                                userProfiles.addAll(tableProfileMaster
                                                        .getProfileDetailsFromRawId((
                                                                (ProfileData)
                                                                        arrayListPhoneBookContacts.get(i)).getLocalPhoneBookId()));
                                                String name = "0";
                                                String rcpID = "0";
                                                if (userProfiles.size() > 1) {
                                                    for (int j = 0; j < userProfiles.size();
                                                         j++) {
                                                        if (name.equalsIgnoreCase("0")) {
                                                            name = userProfiles.get(j).getPmRcpId();
                                                        } else {
                                                            name = name + "," + userProfiles.get
                                                                    (j).getPmRcpId();
                                                        }
                                                    }
                                                } else if (userProfiles.size() == 1) {
                                                    name = userProfiles.get(0).getPmFirstName() +
                                                            " " + userProfiles.get(0)
                                                            .getPmLastName();
                                                    rcpID = userProfiles.get(0).getPmRcpId();
                                                }
                                                ((ProfileData) arrayListPhoneBookContacts.get(i))
                                                        .setTempRcpName(name);
                                                ((ProfileData) arrayListPhoneBookContacts.get(i))
                                                        .setTempRcpId(rcpID);
                                            } else {
                                                ((ProfileData) arrayListPhoneBookContacts.get(i))
                                                        .setTempIsRcp(false);
                                            }
                                            final int finalI = i;
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    allContactListAdapter.notifyItemChanged(finalI);
                                                }
                                            });
                                        }
                                    }
                                }
                            });*/
                            Intent localBroadcastIntent = new Intent(AppConstants
                                    .ACTION_LOCAL_BROADCAST_CALL_LOG_SYNC);
                            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                                    .getInstance(getActivity());
                            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                        }
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
                                "Invitation sent successfully");
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
                    showPermissionConfirmationDialog(AppConstants
                            .MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
            break;

            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNumber));
                    startActivity(intent);
                } else {
                    // Permission Denied
                    showPermissionConfirmationDialog(AppConstants
                            .MY_PERMISSIONS_REQUEST_PHONE_CALL);
                }
            }
            break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

      /*  arrayListPhoneBookContacts = new ArrayList<>();
        arrayListContactHeaders = new ArrayList<>();

        arrayListContactHeaders.add(" ");
        arrayListPhoneBookContacts.add("My Profile");

        ProfileData myProfileData = new ProfileData();

        TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
        UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer.parseInt((
                (BaseActivity) getActivity()).getUserPmId()));

        TableMobileMaster tableMobileMaster = new TableMobileMaster(getDatabaseHandler());
        MobileNumber mobileNumber = tableMobileMaster.getOwnVerifiedMobileNumbersFromPmId
                (getActivity());

        myProfileData.setTempFirstName(userProfile.getPmFirstName());
        myProfileData.setTempLastName(userProfile.getPmLastName());
        myProfileData.setTempNumber(mobileNumber.getMnmMobileNumber());
        myProfileData.setTempIsRcp(true);
        myProfileData.setTempRcpId(((BaseActivity) getActivity()).getUserPmId());

        arrayListPhoneBookContacts.add(myProfileData);*/

        Set<String> set = new HashSet<>();
        set.add(ContactsContract.Data.MIMETYPE);
        set.add(ContactsContract.Data.CONTACT_ID);
        set.add(ContactsContract.CommonDataKinds.Phone.NUMBER);
//        set.add(ContactsContract.CommonDataKinds.Phone.TYPE);
        set.add(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
        set.add(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
        set.add(ContactsContract.CommonDataKinds.StructuredName.PREFIX);
        set.add(ContactsContract.CommonDataKinds.StructuredName.SUFFIX);
        set.add(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
        set.add(ContactsContract.Contacts.PHOTO_ID);
        set.add(ContactsContract.Contacts.LOOKUP_KEY);

        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = set.toArray(new String[0]);
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
        };
        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";

        // Starts the query
        return new CursorLoader(
                getActivity(),
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        getContactsFromPhonebook(data);
        data.close();

        setRecyclerViewLayoutManager();
        initSwipe();

        textTotalContacts.setVisibility(View.GONE);
        progressAllContact.setVisibility(View.GONE);

        Runnable run = new Runnable() {
            @Override
            public void run() {
                syncContacts();
            }
        };
        if (!Utils.getBooleanPreference(getActivity(), AppConstants.PREF_CONTACT_SYNCED, false)) {
            AsyncTask.execute(run);
        }
//        AsyncTask.execute(run);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        allContactListAdapter = null;
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        getLoaderManager().initLoader(0, null, this);

//        textTotalContacts.setTypeface(Utils.typefaceSemiBold(getActivity()));

      /*  VerticalRecyclerViewFastScroller scrollerAllContact = new
                VerticalRecyclerViewFastScroller(getActivity());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        relativeRootAllContacts.addView(scrollerAllContact);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        scrollerAllContact.setRecyclerView(recyclerViewContactList);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerViewContactList.setOnScrollListener(scrollerAllContact.getOnScrollListener());

        // Connect the section indicator to the scroller
        scrollerAllContact.setSectionIndicator(titleIndicator);

        setRecyclerViewLayoutManager();*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContactList.setLayoutManager(linearLayoutManager);

        /*ArrayList<String> arrayListContactIds = Utils.getArrayListPreference(getActivity(),
                AppConstants.PREF_CONTACT_ID_SET);
        if (arrayListContactIds != null) {
            arrayListContactId = new ArrayList<>(arrayListContactIds);
            if (rContactApplication.getArrayListAllPhoneBookContacts().size() <= 0) {
                phoneBookOperations();
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
    public void setRecyclerViewLayoutManager() {
       /* int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);*/

        if (arrayListUserContact.size() > 0) {
            for (int i = 0; i < arrayListUserContact.size(); i++) {
                String headerLetter = StringUtils.upperCase(StringUtils.substring
                        (arrayListUserContact.get(i).getTempFirstName(), 0, 1));
                headerLetter = StringUtils.length(headerLetter) > 0 ? headerLetter : "#";
                if (!arrayListPhoneBookContacts.contains(headerLetter)) {
                    arrayListContactHeaders.add(headerLetter);
                    arrayListPhoneBookContacts.add(headerLetter);
                }
                arrayListPhoneBookContacts.add(arrayListUserContact.get(i));
            }

            allContactListAdapter = new AllContactAdapter(this, arrayListPhoneBookContacts,
                    arrayListContactHeaders);
            recyclerViewContactList.setAdapter(allContactListAdapter);

            /*rContactApplication.setArrayListAllPhoneBookContacts(arrayListPhoneBookContacts);
            rContactApplication.setArrayListAllContactHeaders(arrayListContactHeaders);*/
        }

        getRcpDetail();
      /*  AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler
                        ());
                ArrayList<String> arrayListIds = tableProfileMaster.getAllRcpId();
                for (int i = 2; i < arrayListPhoneBookContacts.size(); i++) {
                    if (arrayListPhoneBookContacts.get(i) instanceof ProfileData) {
                        if (arrayListIds.contains(((ProfileData) arrayListPhoneBookContacts.get
                                (i)).getLocalPhoneBookId())) {
                            ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(true);
                  *//*  String name = tableProfileMaster.getNameFromRawId(((ProfileData)
                            arrayListPhoneBookContacts.get(i)).getLocalPhoneBookId());
                    ((ProfileData) arrayListPhoneBookContacts.get(i))
                            .setTempRcpName(name);*//*
                            ArrayList<UserProfile> userProfiles = new ArrayList<>();
                            userProfiles.addAll(tableProfileMaster.getProfileDetailsFromRawId((
                                    (ProfileData) arrayListPhoneBookContacts.get(i))
                                    .getLocalPhoneBookId()));
                            String name = "0";
                            String rcpID = "0";
                            if (userProfiles.size() > 1) {
                                for (int j = 0; j < userProfiles.size();
                                     j++) {
                                    if (name.equalsIgnoreCase("0")) {
                                        name = userProfiles.get(j).getPmRcpId();
                                    } else {
                                        name = name + "," + userProfiles.get(j).getPmRcpId();
                                    }
                                }
                            } else if (userProfiles.size() == 1) {
                                name = userProfiles.get(0).getPmFirstName() + " " + userProfiles
                                        .get(0)
                                        .getPmLastName();
                                rcpID = userProfiles.get(0).getPmRcpId();
                            }
                            ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempRcpName(name);
                            ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempRcpId(rcpID);
                        } else {
                            ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(false);
                        }
                        final int finalI = i;
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                allContactListAdapter.notifyItemChanged(finalI);
                            }
                        });
                    }
                }
            }
        });*/

    }

    private void getRcpDetail() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler
                        ());
                ArrayList<String> arrayListIds = tableProfileMaster.getAllRcpId();
                for (int i = 2; i < arrayListPhoneBookContacts.size(); i++) {
                    if (arrayListPhoneBookContacts.get(i) instanceof ProfileData) {
                        if (arrayListIds.contains(((ProfileData) arrayListPhoneBookContacts.get
                                (i)).getLocalPhoneBookId())) {
                            ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(true);
                  /*  String name = tableProfileMaster.getNameFromRawId(((ProfileData)
                            arrayListPhoneBookContacts.get(i)).getLocalPhoneBookId());
                    ((ProfileData) arrayListPhoneBookContacts.get(i))
                            .setTempRcpName(name);*/
                            ArrayList<UserProfile> userProfiles = new ArrayList<>();
                            userProfiles.addAll(tableProfileMaster.getProfileDetailsFromRawId((
                                    (ProfileData) arrayListPhoneBookContacts.get(i))
                                    .getLocalPhoneBookId()));
                            String name = "0";
                            String rcpID = "0";
                            if (userProfiles.size() > 1) {
                                for (int j = 0; j < userProfiles.size();
                                     j++) {
                                    if (name.equalsIgnoreCase("0")) {
                                        name = userProfiles.get(j).getPmRcpId();
                                    } else {
                                        name = name + "," + userProfiles.get(j).getPmRcpId();
                                    }
                                }
                            } else if (userProfiles.size() == 1) {
                                name = userProfiles.get(0).getPmFirstName() + " " + userProfiles
                                        .get(0)
                                        .getPmLastName();
                                rcpID = userProfiles.get(0).getPmRcpId();
                            }
                            ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempRcpName(name);
                            ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempRcpId(rcpID);
                        } else {
                            ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(false);
                        }
                        final int finalI = i;
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                allContactListAdapter.notifyItemChanged(finalI);
                            }
                        });
                    }
                }
            }
        });
    }

    private void getContactsFromPhonebook(Cursor data) {
        final int mimeTypeIdx = data.getColumnIndex(ContactsContract.Data.MIMETYPE);
        final int idIdx = data.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        final int phoneIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//        final int phoneTypeIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
        final int givenNameIdx = data.getColumnIndex(ContactsContract.CommonDataKinds
                .StructuredName.GIVEN_NAME);
        final int familyNameIdx = data.getColumnIndex(ContactsContract.CommonDataKinds
                .StructuredName.FAMILY_NAME);
        final int middleNameIdx = data.getColumnIndex(ContactsContract.CommonDataKinds
                .StructuredName.MIDDLE_NAME);
        final int suffixNameIdx = data.getColumnIndex(ContactsContract.CommonDataKinds
                .StructuredName.SUFFIX);
        final int prefixNameIdx = data.getColumnIndex(ContactsContract.CommonDataKinds
                .StructuredName.PREFIX);
        final int photoIdIdx = data.getColumnIndex(ContactsContract.Data.PHOTO_ID);
        final int lookUpKeyIdx = data.getColumnIndex(ContactsContract.Data.LOOKUP_KEY);

        while (data.moveToNext()) {

            long id = data.getLong(idIdx);
            ProfileData profileData = array.get(id);

            if (profileData == null) {
                profileData = new ProfileData();
                array.put(id, profileData);
                arrayListUserContact.add(profileData);
            }

            profileData.setLocalPhoneBookId(data.getString(lookUpKeyIdx));

            switch (data.getString(mimeTypeIdx)) {
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                    profileData.setTempNumber(Utils.getFormattedNumber(getActivity(), data
                            .getString(phoneIdx)));
                    break;
                case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                    profileData.setTempFirstName(data.getString(givenNameIdx));
                    profileData.setTempLastName(data.getString(familyNameIdx));
                    profileData.setTempPrefix(data.getString(prefixNameIdx));
                    profileData.setTempSufix(data.getString(suffixNameIdx));
                    profileData.setTempMiddleName(data.getString(middleNameIdx));
                    break;
            }
        }
    }

    private void storeToMobileMapping(ArrayList<ProfileDataOperation> profileData) {
//        if (!Utils.isArraylistNullOrEmpty(arrayListContactNumbers)) {
        TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                (getDatabaseHandler());
        ArrayList<ProfileMobileMapping> arrayListProfileMobileMapping = new ArrayList<>();
//            for (int i = 0; i < arrayListContactNumbers.size(); i++) {
           /* if (!tableProfileMobileMapping.getIsMobileNumberExists(arrayListContactNumbers
                    .get(i))) {*/

//                ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
//                profileMobileMapping.setMpmMobileNumber(arrayListContactNumbers.get(i));
//                profileMobileMapping.setMpmIsRcp("0");
        if (!Utils.isArraylistNullOrEmpty(profileData)) {
            for (int j = 0; j < profileData.size(); j++) {
                /*if (!tableProfileMobileMapping.getIsMobileNumberExists(profileData.get(j)
                        .getVerifiedMobileNumber())) {*/
                ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
                profileMobileMapping.setMpmMobileNumber(profileData.get(j)
                        .getVerifiedMobileNumber());
                profileMobileMapping.setMpmCloudMnmId(profileData.get(j)
                        .getMnmCloudId());
                profileMobileMapping.setMpmCloudPmId(profileData.get(j).getRcpPmId());
                profileMobileMapping.setMpmIsRcp("1");
                arrayListProfileMobileMapping.add(profileMobileMapping);
//                }
            }
        }
               /* arrayListProfileMobileMapping.add(profileMobileMapping);
            }*/
//            }
        tableProfileMobileMapping.addArrayProfileMobileMapping(arrayListProfileMobileMapping);
       /* int count = tableProfileMobileMapping.getProfileMobileMappingCount();
        Log.i("storeToMobileMapping: ", String.valueOf(count));*/
//        }
    }

    private void storeToEmailMapping(ArrayList<ProfileDataOperation> profileData) {
//        if (!Utils.isArraylistNullOrEmpty(arrayListContactEmails)) {
        TableProfileEmailMapping tableProfileEmailMapping = new TableProfileEmailMapping
                (getDatabaseHandler());
        ArrayList<ProfileEmailMapping> arrayListProfileEmailMapping = new ArrayList<>();
//            for (int i = 0; i < arrayListContactEmails.size(); i++) {
//            if (!tableProfileEmailMapping.getIsEmailIdExists(arrayListContactEmails.get(i))) {

//                    ProfileEmailMapping profileEmailMapping = new ProfileEmailMapping();
//                    profileEmailMapping.setEpmEmailId(arrayListContactEmails.get(i));
//                profileEmailMapping.setEpmIsRcp("0");
        if (!Utils.isArraylistNullOrEmpty(profileData)) {
            for (int j = 0; j < profileData.size(); j++) {
                if (!Utils.isArraylistNullOrEmpty(profileData.get(j).getVerifiedEmailIds())) {
                    for (int k = 0; k < profileData.get(j).getVerifiedEmailIds().size(); k++) {
                               /* if (StringUtils.equalsIgnoreCase(arrayListContactEmails.get(i),
                                        profileData.get(j).getVerifiedEmailIds().get(k)
                                                .getEmEmailId())) {*/
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
//                                }
                            arrayListProfileEmailMapping.add(profileEmailMapping);
                        }
                    }
//                            }
                }
            }

//                    arrayListProfileEmailMapping.add(profileEmailMapping);
        }
//            }
        tableProfileEmailMapping.addArrayProfileEmailMapping(arrayListProfileEmailMapping);
//            }
//        }
    }

    private void storeProfileDataToDb(ArrayList<ProfileDataOperation> profileData,
                                      ArrayList<ProfileData> mapping) {

        // Hashmap with key as rcpId and value as rawId/s
        HashMap<String, String> mapLocalRcpId = new HashMap<>();

        for (int i = 0; i < mapping.size(); i++) {
//            if (mapping.get(i).getRcpPmId().size() > 0) {
            for (int j = 0; j < mapping.get(i).getRcpPmId().size(); j++) {
                String phonebookRawId;
                if (mapLocalRcpId.containsKey(mapping.get(i).getRcpPmId().get(j))) {
                    phonebookRawId = mapLocalRcpId.get(mapping.get(i).getRcpPmId().get(j)) +
                            ", " + mapping.get(i).getLocalPhoneBookId();
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
            userProfile.setTotalProfileRateUser(profileData.get(i).getTotalProfileRateUser());

            if (mapLocalRcpId.containsKey(profileData.get(i).getRcpPmId())) {
                userProfile.setPmRawId(mapLocalRcpId.get(profileData.get(i).getRcpPmId()));
            }

//            if (tableProfileMaster.getRcpIdCount(Integer.parseInt(userProfile.getPmRcpId())) <=
// 0) {
            String existingRawId = tableProfileMaster.getRawIdFromRcpId(Integer.parseInt
                    (userProfile.getPmRcpId()));
            if (StringUtils.length(existingRawId) <= 0) {

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
                        address.setAmFormattedAddress(arrayListAddress.get(j).getFormattedAddress
                                ());
                        address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                        address.setAmPostCode(arrayListAddress.get(j).getPostCode());
                        address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                        address.setAmStreet(arrayListAddress.get(j).getStreet());
                        address.setAmAddressType(arrayListAddress.get(j).getAddressType());
                        address.setAmGoogleLatitude(arrayListAddress.get(j).getGoogleLatitude());
                        address.setAmGoogleLongitude(arrayListAddress.get(j).getGoogleLongitude());
//                    address.setAmGoogleAddress(arrayListAddress.get(j).getGoogleAddress());
                        address.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
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
                    ArrayList<ProfileDataOperationImAccount> arrayListImAccount = profileData.get(i)
                            .getPbIMAccounts();
                    ArrayList<ImAccount> imAccountsList = new ArrayList<>();
                    for (int j = 0; j < arrayListImAccount.size(); j++) {
                        ImAccount imAccount = new ImAccount();
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
                        event.setEvmEventPrivacy(String.valueOf(arrayListEvent.get(j).getEventPublic
                                ()));
                        event.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        eventList.add(event);
                    }

                    TableEventMaster tableEventMaster = new TableEventMaster(getDatabaseHandler());
                    tableEventMaster.addArrayEvent(eventList);
                }
                //</editor-fold>
            } else {
                String newRawIds = existingRawId + "," + mapLocalRcpId.get(profileData.get(i)
                        .getRcpPmId());
                tableProfileMaster.updateRawIds(Integer.parseInt(userProfile.getPmRcpId()),
                        newRawIds);
            }
        }
    }

    private void populateRecyclerView() {

        if (allContactListAdapter == null) {
            allContactListAdapter = new AllContactAdapter(this,
                    arrayListPhoneBookContacts, arrayListContactHeaders);
            recyclerViewContactList.setAdapter(allContactListAdapter);

            setRecyclerViewLayoutManager();

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
                String actionNumber = StringUtils.defaultString(((AllContactAdapter
                        .AllContactViewHolder) viewHolder).textContactNumber.getText().toString());
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
                    callNumber = actionNumber;
                    showCallConfirmationDialog();
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
                if (viewHolder instanceof AllContactAdapter.ContactHeaderViewHolder || viewHolder
                        instanceof AllContactAdapter.ContactFooterViewHolder) {
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


    private void showCallConfirmationDialog() {

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
                        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest
                                .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission
                                    .CALL_PHONE}, AppConstants
                                    .MY_PERMISSIONS_REQUEST_PHONE_CALL);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                    callNumber));
                            startActivity(intent);
                        }
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + callNumber + "?");

        callConfirmationDialog.showDialog();

    }

    private void showPermissionConfirmationDialog(final int permissionType) {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        permissionConfirmationDialog.dismissDialog();
                        switch (permissionType) {
                            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                                getActivity().finish();
                                break;
                            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL:
                                break;
                        }
                        break;

                    case R.id.rippleRight:
                        permissionConfirmationDialog.dismissDialog();
                        isFromSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getActivity().getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        switch (permissionType) {
                            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                                settingRequestPermission = AppConstants
                                        .MY_PERMISSIONS_REQUEST_READ_CONTACTS;
                                break;
                            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL:
                                settingRequestPermission = AppConstants
                                        .MY_PERMISSIONS_REQUEST_PHONE_CALL;
                                break;
                        }
                        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;
                }
            }
        };

        String message = "";
        switch (permissionType) {
            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                message = "Contact read permission is required of this app. Do you want to try " +
                        "again?";
                break;
            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL:
                message = "Calling permission is required to make the call. Do you want to try " +
                        "again?";
                break;
        }

        permissionConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        permissionConfirmationDialog.setTitleVisibility(View.GONE);
        permissionConfirmationDialog.setLeftButtonText("Cancel");
        permissionConfirmationDialog.setRightButtonText("OK");
        permissionConfirmationDialog.setDialogBody(message);

        permissionConfirmationDialog.showDialog();

    }

    //</editor-fold>

    //<editor-fold desc="Local Broadcast Receiver">
   /* private BroadcastReceiver cursorListReceiver = new BroadcastReceiver() {
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
                    phoneBookOperations();
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

    private void syncContacts() {

//        List<AddressBookContact> list = new LinkedList<>();
//        List<ProfileDataOperation> profileDataList = new LinkedList<>();
        LongSparseArray<ProfileDataOperation> profileDetailSparseArray = new LongSparseArray<>();

        //<editor-fold desc="Create Cursor">
        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.LOOKUP_KEY,
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
        //  String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;
        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
        Uri uri = ContactsContract.Data.CONTENT_URI;

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection,
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
//                    profileDataList.add(phoneBookContact);
                }
                phoneBookContact.setLookupKey(cursor.getString(cursor.getColumnIndex
                        (ContactsContract.Contacts.LOOKUP_KEY)));
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

                        phoneNumber.setPhoneNumber(Utils.getFormattedNumber(getActivity(), cursor
                                .getString(cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Phone.NUMBER))));
                        phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                                (cursor.getInt(cursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.TYPE))));
                        phoneNumber.setPhonePublic(IntegerConstants.PRIVACY_MY_CONTACT);

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
                        emailId.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);


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
                        address.setAddPublic(IntegerConstants.PRIVACY_MY_CONTACT);

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

                        imAccount.setIMAccountPublic(IntegerConstants.PRIVACY_MY_CONTACT);


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

                        event.setEventPublic(IntegerConstants.PRIVACY_MY_CONTACT);

                        phoneBookContact.addEvent(event);
                        break;
                }
            }
            cursor.close();
        }
        //</editor-fold>

        //<editor-fold desc="Prepare Data">
        for (int i = 0; i < profileDetailSparseArray.size(); i++) {
//            AddressBookContact bookContact = profileDetailSparseArray.valueAt(i);
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

    private void backgroundSync(final boolean addToDatabase, final WsResponseObject
            uploadContactResponse) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
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
                        /*else {

                        *//* Store Unique Contacts to ProfileMobileMapping *//*
                            storeToMobileMapping(null);

                        *//* Store Unique Contacts to ProfileMobileMapping *//*
                            storeToEmailMapping(null);

                        }*/
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
        };
        AsyncTask.execute(run);
    }


    //</editor-fold>

    //<editor-fold desc="Web Service Call">

  /*  private void uploadContacts(int previouslySyncedData) {

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
    }*/

    private void uploadContacts(int previouslySyncedData, ArrayList<ProfileData>
            arrayListUserContact) {

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
