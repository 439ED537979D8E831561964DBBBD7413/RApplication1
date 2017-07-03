package com.rawalinfocom.rcontact.contacts;


import android.Manifest;
import android.content.Context;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class AllContactsListFragment extends BaseFragment implements LoaderManager
        .LoaderCallbacks<Cursor>, WsResponseListener {

    private final int CONTACT_CHUNK = 50;

    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.relative_root_all_contacts)
    RelativeLayout relativeRootAllContacts;
    @BindView(R.id.progress_all_contact)
    ProgressWheel progressAllContact;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    @BindView(R.id.text_total_contacts)
    TextView textTotalContacts;

    public static ArrayList<Object> arrayListPhoneBookContacts;
    ArrayList<ProfileData> arrayListSyncUserContact = new ArrayList<>();
    ArrayList<String> arrayListFavouriteContacts;

    LongSparseArray<ProfileData> array = new LongSparseArray<>();

    MaterialDialog callConfirmationDialog, permissionConfirmationDialog;

    PhoneBookContacts phoneBookContacts;

    AllContactAdapter allContactListAdapter;

    private View rootView;
    private boolean isReload = false;
    RContactApplication rContactApplication;
    int lastSyncedData = 0;

    boolean isFromSettings = false;
    int settingRequestPermission = 0;
    public String callNumber = "";
    private SyncingTask syncingTask;

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
        phoneBookContacts = new PhoneBookContacts(getActivity());
        rContactApplication = (RContactApplication) getActivity().getApplicationContext();
        Utils.setBooleanPreference(getActivity(), AppConstants
                .PREF_RECENT_CALLS_BROADCAST_RECEIVER_MAIN_INSTANCE, true);
        Utils.setBooleanPreference(getActivity(), AppConstants
                .PREF_RECENT_SMS_BROADCAST_RECEIVER_MAIN_INSTANCE, true);
        Utils.setBooleanPreference(getActivity(), AppConstants
                .PREF_RECENT_CALLS_BROADCAST_RECEIVER_CALL_LOG_TAB, false);


        lastSyncedData = Utils.getIntegerPreference(getActivity(), AppConstants
                .PREF_SYNCED_CONTACTS, 0);

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
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (syncingTask != null) {
            syncingTask.cancel(true);
        }
        super.onDetach();
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

                        lastSyncedData = lastSyncedData + CONTACT_CHUNK;
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
                                    getActivity().getString(R.string.str_all_contact_sync));
                            Utils.setStringPreference(getActivity(), AppConstants
                                    .PREF_CONTACT_LAST_SYNC_TIME, String.valueOf(System
                                    .currentTimeMillis() - 10000));
                            Utils.setBooleanPreference(getActivity(), AppConstants
                                    .PREF_CONTACT_SYNCED, true);
                            getRcpDetail();
                            phoneBookContacts.saveRawIdsToPref();
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
                                getActivity().getString(R.string.invitation_sent));
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
                    Utils.callIntent(getActivity(), callNumber);
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
        Set<String> set = new HashSet<>();
        set.add(ContactsContract.Data.MIMETYPE);
        set.add(ContactsContract.Data.CONTACT_ID);
        set.add(ContactsContract.CommonDataKinds.Phone.NUMBER);
        set.add(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        set.add(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI);
        set.add(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID);

        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = set.toArray(new String[0]);
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
        };
        String sortOrder = "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC";

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
        if (arrayListPhoneBookContacts == null) {

            arrayListPhoneBookContacts = new ArrayList<>();
            arrayListFavouriteContacts = new ArrayList<>();
            arrayListPhoneBookContacts.add(getActivity().getString(R.string.title_my_profile));

            ProfileData myProfileData = new ProfileData();

            TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer.parseInt((
                    (BaseActivity) getActivity()).getUserPmId()));

            TableMobileMaster tableMobileMaster = new TableMobileMaster(getDatabaseHandler());
            MobileNumber mobileNumber = tableMobileMaster.getOwnVerifiedMobileNumbersFromPmId
                    (getActivity());

            myProfileData.setName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
            myProfileData.setProfileUrl(userProfile.getPmProfileImage());
            myProfileData.setTempNumber(mobileNumber.getMnmMobileNumber());
            myProfileData.setTempIsRcp(true);
            myProfileData.setTempRcpId(((BaseActivity) getActivity()).getUserPmId());

            arrayListPhoneBookContacts.add(myProfileData);
            arrayListPhoneBookContacts.add(getActivity().getString(R.string.privacy_my_contact));

            phoneBookContacts = new PhoneBookContacts(getActivity());
            isReload = false;

        } else {
            isReload = true;
        }

        getContactsFromPhoneBook(data);
        data.close();
        setRecyclerViewLayoutManager();
        initSwipe();

        textTotalContacts.setVisibility(View.GONE);
        progressAllContact.setVisibility(View.GONE);

        if (!Utils.getBooleanPreference(getActivity(), AppConstants.PREF_CONTACT_SYNCED, false)) {
            syncingTask = new SyncingTask();
            syncingTask.execute();
        }
        Intent localBroadcastIntent = new Intent(AppConstants
                .ACTION_LOCAL_BROADCAST_CONTACT_DISPLAYED);
        LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
//            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == AppConstants.REQUEST_CODE_PROFILE_DETAIL && resultCode ==
                    RESULT_OK) {
                if (OptionMenuDialog.IS_CONTACT_DELETED) {
                    OptionMenuDialog.IS_CONTACT_DELETED = false;
                    arrayListPhoneBookContacts.remove(allContactListAdapter
                            .getListClickedPosition());
                    allContactListAdapter.notifyItemRemoved(allContactListAdapter
                            .getListClickedPosition());
                    rContactApplication.setArrayListAllPhoneBookContacts
                            (arrayListPhoneBookContacts);
                    RContactsFragment.arrayListRContact = null;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        getLoaderManager().initLoader(0, null, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContactList.setLayoutManager(linearLayoutManager);
    }


    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager() {

        allContactListAdapter = new AllContactAdapter(this, arrayListPhoneBookContacts, null);
        recyclerViewContactList.setAdapter(allContactListAdapter);
        rContactApplication.setArrayListAllPhoneBookContacts(arrayListPhoneBookContacts);
        getRcpDetail();
    }

    private void getRcpDetail() {
        try {
            TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler
                    ());
            ArrayList<String> arrayListIds = tableProfileMaster.getAllRcpId();
            for (int i = 2; i < arrayListPhoneBookContacts.size(); i++) {
                if (arrayListPhoneBookContacts.get(i) instanceof ProfileData) {
                    if (arrayListIds.contains(((ProfileData) arrayListPhoneBookContacts.get
                            (i)).getRawContactId())) {
                        ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(true);
                        ArrayList<UserProfile> userProfiles = new ArrayList<>();
                        userProfiles.addAll(tableProfileMaster.getProfileDetailsFromRawId((
                                (ProfileData) arrayListPhoneBookContacts.get(i))
                                .getRawContactId()));
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
                            if (allContactListAdapter != null)
                                allContactListAdapter.notifyItemChanged(finalI);
                        }
                    });
                }
            }
        } catch (Exception ignore) {

        }
    }

    private void getContactsFromPhoneBook(Cursor data) {
        final int mimeTypeIdx = data.getColumnIndex(ContactsContract.Data.MIMETYPE);
        final int idIdx = data.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        final int phoneIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

//        final int lookUpKeyIdx = data.getColumnIndex(ContactsContract.Data.LOOKUP_KEY);
        final int photoURIIdx = data.getColumnIndex(ContactsContract.PhoneLookup
                .PHOTO_THUMBNAIL_URI);

        final int rawIdIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone
                .RAW_CONTACT_ID);
        final int display = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);


        while (data.moveToNext()) {
            try {
                long id = data.getLong(idIdx);
                ProfileData profileData = array.get(id);

                if (profileData == null) {
                    profileData = new ProfileData();
                    array.put(id, profileData);
                    arrayListPhoneBookContacts.add(profileData);
                }

                profileData.setLocalPhoneBookId(data.getString(rawIdIdx));
                profileData.setRawContactId(data.getString(rawIdIdx));

                switch (data.getString(mimeTypeIdx)) {
                    case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                        profileData.setTempNumber(data.getString(phoneIdx));
                        profileData.setProfileUrl(data.getString(photoURIIdx));
                        break;
                    case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                        profileData.setName(data.getString(display));
                        break;
                }
            } catch (Exception E) {
                Log.i("AllContacts", "Crash occurred when displaying contacts" + E.toString());
            }
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
        }

        // Basic Profile Data
        TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());

        ArrayList<UserProfile> arrayListUserProfile = new ArrayList<>();
        for (int i = 0; i < profileData.size(); i++) {

            //<editor-fold desc="Profile Master">
            UserProfile userProfile = new UserProfile();
//            userProfile.setPmSuffix(profileData.get(i).getPbNameSuffix());
//            userProfile.setPmPrefix(profileData.get(i).getPbNamePrefix());
            userProfile.setPmFirstName(profileData.get(i).getPbNameFirst());
//            userProfile.setPmMiddleName(profileData.get(i).getPbNameMiddle());
            userProfile.setPmLastName(profileData.get(i).getPbNameLast());
//            userProfile.setPmPhoneticFirstName(profileData.get(i).getPbPhoneticNameFirst());
//            userProfile.setPmPhoneticMiddleName(profileData.get(i).getPbPhoneticNameMiddle());
//            userProfile.setPmPhoneticLastName(profileData.get(i).getPbPhoneticNameLast());
            userProfile.setPmIsFavourite(profileData.get(i).getIsFavourite());
//            userProfile.setPmNotes(profileData.get(i).getPbNote());
//            userProfile.setPmNickName(profileData.get(i).getPbNickname());
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
                        if (allContactListAdapter != null)
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

                if (viewHolder instanceof AllContactAdapter.AllContactViewHolder) {
                    /* Disable swiping in multiple RC case */
                    if (((AllContactAdapter.AllContactViewHolder) viewHolder)
                            .recyclerViewMultipleRc.getVisibility() == View.VISIBLE) {
                        return 0;
                    }
                    /* Disable swiping for My Profile */
                    if (Integer.parseInt(((AllContactAdapter.AllContactViewHolder) viewHolder)
                            .textContactName.getTag().toString()) == 1) {
                        return 0;
                    }
                    /* Disable swiping for No number */
                    if (StringUtils.length(((AllContactAdapter.AllContactViewHolder) viewHolder)
                            .textContactNumber.getText().toString()) <= 0) {
                        return 0;
                    }
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
                            Utils.callIntent(getActivity(), callNumber);
                        }
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(getActivity().getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(getActivity().getString(R.string.action_call));
        callConfirmationDialog.setDialogBody(getActivity().getString(R.string.action_call) + " " + callNumber + "?");

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
                message = getActivity().getString(R.string.contact_read_permission);
                break;
            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL:
                message = getActivity().getString(R.string.calling_permission);
                break;
        }

        permissionConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        permissionConfirmationDialog.setTitleVisibility(View.GONE);
        permissionConfirmationDialog.setLeftButtonText(getActivity().getString(R.string.action_cancel));
        permissionConfirmationDialog.setRightButtonText(getActivity().getString(R.string.action_ok));
        permissionConfirmationDialog.setDialogBody(message);

        permissionConfirmationDialog.showDialog();

    }

    //</editor-fold>

    private void syncContacts() {

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
        String sortOrder = "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC";
        Uri uri = ContactsContract.Data.CONTENT_URI;
        if (syncingTask != null && syncingTask.isCancelled()) {
            return;
        }
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection,
                selectionArgs, sortOrder);
        //</editor-fold>

        //<editor-fold desc="Data Read from Cursor">
        if (cursor != null) {
            final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
            final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);

            while (cursor.moveToNext()) {
                if (syncingTask != null && syncingTask.isCancelled()) {
                    return;
                }
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

                        phoneNumber.setPhoneNumber(Utils.getFormattedNumber(getActivity(), cursor
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
            if (syncingTask != null && syncingTask.isCancelled()) {
                return;
            }
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
            if (syncingTask != null && syncingTask.isCancelled()) {
                return;
            }
            backgroundSync(false, null);
        } else if (arrayListSyncUserContact.size() == 0) {
            Utils.showSuccessSnackBar(getActivity(), relativeRootAllContacts,
                    getActivity().getString(R.string.str_all_contact_sync));
            Utils.setStringPreference(getActivity(), AppConstants
                    .PREF_CONTACT_LAST_SYNC_TIME, String.valueOf(System
                    .currentTimeMillis() - 10000));
            Utils.setBooleanPreference(getActivity(), AppConstants
                    .PREF_CONTACT_SYNCED, true);
            phoneBookContacts.saveRawIdsToPref();
            Intent localBroadcastIntent = new Intent(AppConstants
                    .ACTION_LOCAL_BROADCAST_CALL_LOG_SYNC);
            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                    .getInstance(getActivity());
            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
        }


    }

    private void backgroundSync(final boolean addToDatabase, final WsResponseObject
            uploadContactResponse) {
        if (syncingTask != null && syncingTask.isCancelled()) {
            return;
        }
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if (addToDatabase) {
                    if (uploadContactResponse != null) {
                        if (syncingTask != null && syncingTask.isCancelled()) {
                            return;
                        }
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
        };
        AsyncTask.execute(run);
    }

    public class SyncingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            syncContacts();
            return null;
        }
    }
    //</editor-fold>

    private void uploadContacts(int previouslySyncedData, ArrayList<ProfileData>
            arrayListUserContact) {
        if (syncingTask != null && syncingTask.isCancelled()) {
            return;
        }
        WsRequestObject uploadContactObject = new WsRequestObject();
//        uploadContactObject.setPmId(Integer.parseInt(((BaseActivity) getActivity()).getUserPmId()));
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
