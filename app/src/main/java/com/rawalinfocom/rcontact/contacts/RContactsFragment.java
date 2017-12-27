package com.rawalinfocom.rcontact.contacts;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.BuildConfig;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.RContactListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableEducationMaster;
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
import com.rawalinfocom.rcontact.helper.ProgressWheel;
import com.rawalinfocom.rcontact.helper.RecyclerItemDecoration;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Address;
import com.rawalinfocom.rcontact.model.Education;
import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.ImAccount;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEducation;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class RContactsFragment extends BaseFragment implements WsResponseListener {

    @BindView(R.id.progress_r_contact)
    ProgressWheel progressRContact;
    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    /*@BindView(R.id.relative_scroller)
    RelativeLayout relativeScroller;*/
    @BindView(R.id.text_total_contacts)
    TextView textTotalContacts;
    /*@BindView(R.id.scroller_all_contact)
    VerticalRecyclerViewFastScroller scrollerAllContact;
    @BindView(R.id.title_indicator)
    ColorGroupSectionTitleIndicator titleIndicator;*/

    ArrayList<String> arrayListContactHeaders;
    @BindView(R.id.relative_root_rcontacts)
    RelativeLayout relativeRootRcontacts;
    Unbinder unbinder;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    private ArrayList<UserProfile> arrayListDisplayProfile;
    public ArrayList<Object> arrayListRContact;

    RContactListAdapter rContactListAdapter;
    private PhoneBookContacts phoneBookContacts;

    //    MaterialDialog callConfirmationDialog, permissionConfirmationDialog;
    private TableProfileMobileMapping tableProfileMobileMapping;

    private View rootView;
    //    private boolean isReload = false;
    private String callNumber;
    private ArrayList<String> arrayListPBPhoneNumber;
    private ArrayList<String> arrayListPBEmailAddress;

    //<editor-fold desc="Constructors">

//    public RContactsFragment() {
    // // Required empty public constructor
//    }

//    public static RContactsFragment newInstance() {
//        return new RContactsFragment();
//    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.showProgressDialog(getMainActivity(), getString(R.string.msg_please_wait), false);
//        if (arrayListRContact == null) {
//            isReload = false;
//        } else {
//            isReload = true;
//        }
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_r_contacts, container, false);
            ButterKnife.bind(this, rootView);
        }
        registerLocalBroadCast();
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (!isReload) {
        arrayListContactHeaders = new ArrayList<>();
        phoneBookContacts = new PhoneBookContacts(getActivity());
        tableProfileMobileMapping = new TableProfileMobileMapping(getDatabaseHandler());
//        System.out.println("RContacts RContactsFragment init dialog");
        init();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterLocalBroadCast();
    }

    private void registerLocalBroadCast() {
        // rating update broadcast receiver register
        LocalBroadcastManager.getInstance(getMainActivity()).registerReceiver
                (localBroadcastReceiverRatingUpdate,
                        new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_RATING_UPDATE));
    }

    private void unregisterLocalBroadCast() {
        //  rating update broadcast receiver unregister
        LocalBroadcastManager.getInstance(getMainActivity()).unregisterReceiver
                (localBroadcastReceiverRatingUpdate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    AppConstants.setIsFirstTime(false);
                    // Permission Granted
                    Utils.callIntent(getActivity(), callNumber);
                } else {
                    // Permission Denied
                    Utils.showErrorSnackBar(getMainActivity(), relativeRootRcontacts, getString(R.string.error_call_permission));
                }
            }
            break;
        }
    }

    // rating update broadcast receiver
    boolean ratingUpdate;
    private BroadcastReceiver localBroadcastReceiverRatingUpdate = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {

            ratingUpdate = intent.getBooleanExtra(AppConstants.EXTRA_RATING_UPDATE, false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ratingUpdate) {
                        getRContactFromDB();
                        int pos = intent.getIntExtra(AppConstants.EXTRA_RCONTACT_POSITION, 0);
                        rContactListAdapter.updateList(pos, arrayListRContact);
                    }
                }
            }, 100);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
//            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == AppConstants.REQUEST_CODE_PROFILE_DETAIL && resultCode ==
                    RESULT_OK) {
                if (OptionMenuDialog.IS_CONTACT_DELETED) {
                    OptionMenuDialog.IS_CONTACT_DELETED = false;
                    init();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">
    private void init() {

        textTotalContacts.setTypeface(Utils.typefaceSemiBold(getActivity()));
        textTotalContacts.setVisibility(View.GONE);

        setRecyclerViewLayoutManager(recyclerViewContactList);

        initSwipe();

        progressRContact.setVisibility(View.GONE);

        getRContactFromDB();

        if (arrayListDisplayProfile.size() > 0) {
            rContactListAdapter = new RContactListAdapter(this, arrayListRContact,
                    arrayListContactHeaders, getUserPmId());
            recyclerViewContactList.setAdapter(rContactListAdapter);
        }

        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Utils.getBooleanPreference(RContactApplication.getInstance(), AppConstants.PREF_CONTACT_SYNCED, false)) {
                    // cancel the Visual indication of a refresh
                    swipeRefreshLayout.setRefreshing(true);
                    String fromDate = Utils.getStringPreference(getActivity(), AppConstants
                            .KEY_API_CALL_TIME_STAMP_RCP, "");
                    RCPContactServiceCall(fromDate, WsConstants.REQ_GET_RCP_CONTACT);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.hideProgressDialog();
//                System.out.println("RContacts RContactsFragment hide dialog");
            }
        }, 1000);
    }

    private void getRContactFromDB() {

        arrayListDisplayProfile = tableProfileMobileMapping.getRContactList();

        arrayListRContact = new ArrayList<>();
        if (arrayListDisplayProfile.size() > 0) {
            arrayListRContact.addAll(arrayListDisplayProfile);
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
//                int position = viewHolder.getAdapterPosition();
                callNumber = StringUtils.defaultString(((RContactListAdapter
                        .RContactViewHolder) viewHolder).textContactNumber.getText()
                        .toString());
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                  /*  smsIntent.setData(Uri.parse("sms:" + ((ProfileData)
                            arrayListPhoneBookContacts.get(position)).getOperation().get(0)
                            .getPbPhoneNumber().get(0).getPhoneNumber()));*/
                    smsIntent.setData(Uri.parse("sms:" + callNumber));
                    startActivity(smsIntent);

                } else {

                    callNumber = Utils.getFormattedNumber(getActivity(), callNumber);
//                    Utils.callIntent(getActivity(), actionNumber);
                    swipeToCall();
//                    showCallConfirmationDialog(actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rContactListAdapter.notifyDataSetChanged();
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
                if (viewHolder instanceof RContactListAdapter.ContactHeaderViewHolder || viewHolder
                        instanceof RContactListAdapter.ContactFooterViewHolder) {
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
                        p.setColor(ContextCompat.getColor(getMainActivity(), R.color
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
                        p.setColor(ContextCompat.getColor(getMainActivity(), R.color.brightOrange));
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

    private void swipeToCall() {
        if (ContextCompat.checkSelfPermission(getMainActivity(), Manifest
                .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission
                    .CALL_PHONE}, AppConstants
                    .MY_PERMISSIONS_REQUEST_PHONE_CALL);
        } else {
            AppConstants.setIsFirstTime(false);
            Utils.callIntent(getActivity(), Utils.getFormattedNumber(getActivity
                    (), callNumber));
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RContactApplication.getInstance());
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerItemDecoration decoration = new RecyclerItemDecoration(RContactApplication.getInstance(), ContextCompat
                .getColor(RContactApplication.getInstance(), R.color.colorVeryLightGray), 0.7f);
        recyclerView.addItemDecoration(decoration);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void RCPContactServiceCall(String timestamp, String url) {

        WsRequestObject deviceDetailObject = new WsRequestObject();
        deviceDetailObject.setTimeStamp(timestamp);

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), deviceDetailObject, null,
                    WsResponseObject.class, url, getResources().getString(R.string.msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT + url);
        } else {
            Utils.showErrorSnackBar(getActivity(), relativeRootRcontacts, getResources().getString(R.string.msg_no_network));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            // <editor-fold desc="REQ_GET_RCP_CONTACT">
            if (serviceType.contains(WsConstants.REQ_GET_RCP_CONTACT)) {
                WsResponseObject getRCPContactUpdateResponse = (WsResponseObject) data;
                if (getRCPContactUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getRCPContactUpdateResponse.getStatus(), WsConstants
                                .RESPONSE_STATUS_TRUE)) {

                    if (!Utils.isArraylistNullOrEmpty(getRCPContactUpdateResponse
                            .getArrayListUserRcProfile())) {

                        try {

                            /* Store Unique Contacts to ProfileMobileMapping */
                            storeToMobileMapping(getRCPContactUpdateResponse
                                    .getArrayListUserRcProfile());

                                /* Store Unique Emails to ProfileEmailMapping */
                            storeToEmailMapping(getRCPContactUpdateResponse
                                    .getArrayListUserRcProfile());

                                /* Store Profile Details to respective Table */
                            storeProfileDataToDb(getRCPContactUpdateResponse
                                    .getArrayListUserRcProfile(), getRCPContactUpdateResponse
                                    .getArrayListMapping());

                        } catch (Exception e) {
                            System.out.println("RContact error");
                        }
                    }
                    if (!Utils.isArraylistNullOrEmpty(getRCPContactUpdateResponse
                            .getArrayListMapping())) {
                        removeRemovedDataFromDb(getRCPContactUpdateResponse
                                .getArrayListMapping());
                    }

                    if (!StringUtils.isEmpty(getRCPContactUpdateResponse.getTimestamp())) {
                        Utils.setStringPreference(RContactApplication.getInstance(), AppConstants.KEY_API_CALL_TIME_STAMP_RCP,
                                getRCPContactUpdateResponse.getTimestamp());
                    }

                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    Utils.hideProgressDialog();

                    getRContactFromDB();
                    rContactListAdapter.notifyDataSetChanged();

                } else {

                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);

                    Utils.hideProgressDialog();
                    if (getRCPContactUpdateResponse != null) {
                        System.out.println("RContact error --> " + getRCPContactUpdateResponse
                                .getMessage());
                    } else {
                        System.out.println("RContact error --> getContactUpdateResponse null");
                    }
                }
            } else {
                Utils.hideProgressDialog();
                if (error != null) {
                    Log.e("error", error.getMessage());
                }
            }

        }
    }

    private void storeToMobileMapping(ArrayList<ProfileDataOperation> profileData) {

        try {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeToEmailMapping(ArrayList<ProfileDataOperation> profileData) {

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeProfileDataToDb(ArrayList<ProfileDataOperation> profileData,
                                      ArrayList<ProfileData> mapping) {

        try {
            // Hashmap with key as rcpId and value as rawId/s
            HashMap<String, String> mapLocalRcpId = new HashMap<>();

            if (!Utils.isArraylistNullOrEmpty(mapping)) {
                for (int i = 0; i < mapping.size(); i++) {
                    for (int j = 0; j < mapping.get(i).getRcpPmId().size(); j++) {
                        String phonebookRawId;
                        if (mapLocalRcpId.containsKey(mapping.get(i).getRcpPmId().get(j))) {

                            if (!StringUtils.isBlank(mapping.get(i).getLocalPhoneBookId())) {
                                phonebookRawId = mapLocalRcpId.get(mapping.get(i).getRcpPmId().get(j)) +
                                        "," + mapping.get(i).getLocalPhoneBookId();
                            } else {
                                phonebookRawId = mapping.get(i).getLocalPhoneBookId();
                            }
                        } else {
                            phonebookRawId = mapping.get(i).getLocalPhoneBookId();
                        }

                        mapLocalRcpId.put(mapping.get(i).getRcpPmId().get(j), phonebookRawId);
                    }
//            }
                }
            }

            // TODO : Hardik : Global Search Organisation
            Gson gson = new Gson();

            String jsonString = gson.toJson(profileData);
            Utils.setStringPreference(getActivity(), "search_data", jsonString);

            // Basic Profile Data
            TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());

            ArrayList<UserProfile> arrayListUserProfile = new ArrayList<>();
            for (int i = 0; i < profileData.size(); i++) {

                //<editor-fold desc="Profile Master">
                UserProfile userProfile = new UserProfile();
                userProfile.setPmFirstName(profileData.get(i).getPbNameFirst());
                userProfile.setPmLastName(profileData.get(i).getPbNameLast());
                userProfile.setPmIsFavourite(profileData.get(i).getIsFavourite());
                userProfile.setPmRcpId(profileData.get(i).getRcpPmId());
                userProfile.setPmNosqlMasterId(profileData.get(i).getNoSqlMasterId());
                userProfile.setPmBadge(profileData.get(i).getPmBadge());
                userProfile.setProfileRating(profileData.get(i).getProfileRating());
                userProfile.setPmProfileImage(profileData.get(i).getPbProfilePhoto());
                userProfile.setTotalProfileRateUser(profileData.get(i).getTotalProfileRateUser());
                userProfile.setPmLastSeen(profileData.get(i).getPmLastSeen());
                userProfile.setProfileRatingPrivacy(String.valueOf(profileData.get(i).getProfileRatingPrivacy()));
                userProfile.setRatingPrivate(String.valueOf(profileData.get(i).getRatingPrivate()));

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

                        getUserData(arrayListPhoneNumber.get(j).getPhoneId());

                        MobileNumber mobileNumber = new MobileNumber();
                        mobileNumber.setMnmRecordIndexId(arrayListPhoneNumber.get(j).getPhoneId());

                        if (arrayListPBPhoneNumber.size() > 0)
                            if (arrayListPBPhoneNumber.contains("+" + arrayListPhoneNumber.get(j).getOriginalNumber())) {
                                mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(j).getOriginalNumber());
                            } else {
                                mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(j).getPhoneNumber());
                            }
                        else
                            mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(j).getPhoneNumber());

                        mobileNumber.setMnmNumberType(arrayListPhoneNumber.get(j).getPhoneType());
                        mobileNumber.setMnmNumberPrivacy(String.valueOf(arrayListPhoneNumber.get(j)
                                .getPhonePublic()));
                        mobileNumber.setMnmIsPrivate(arrayListPhoneNumber.get(j).getIsPrivate());
                        mobileNumber.setMnmPhonePublic(arrayListPhoneNumber.get(j).getPhonePublic());
                        mobileNumber.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                        if (StringUtils.equalsIgnoreCase(profileData.get(i)
                                        .getVerifiedMobileNumber()
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

                            if (arrayListPBEmailAddress.size() > 0)
                                if (arrayListPBEmailAddress.contains(arrayListEmailId.get(j).getOriginalEmail())) {
                                    email.setEmEmailAddress(arrayListEmailId.get(j).getOriginalEmail());
                                } else {
                                    email.setEmEmailAddress(arrayListEmailId.get(j).getEmEmailId());
                                }
                            else
                                email.setEmEmailAddress(arrayListEmailId.get(j).getEmEmailId());

                            email.setEmSocialType(arrayListEmailId.get(j).getEmSocialType());
                            email.setEmRecordIndexId(arrayListEmailId.get(j).getEmId());
                            email.setEmEmailType(arrayListEmailId.get(j).getEmType());
                            email.setEmEmailPrivacy(String.valueOf(arrayListEmailId.get(j)
                                    .getEmPublic()));
                            email.setEmIsVerified(String.valueOf(arrayListEmailId.get(j)
                                    .getEmRcpType
                                            ()));
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

                    // <editor-fold desc="Education Master">
                    if (!Utils.isArraylistNullOrEmpty(profileData.get(i).getPbEducation())) {
                        ArrayList<ProfileDataOperationEducation> arrayListEducation = profileData.get
                                (i).getPbEducation();
                        ArrayList<Education> arrayListEdu = new ArrayList<>();
                        for (int j = 0; j < arrayListEducation.size(); j++) {

                            Education education = new Education();


                            education.setEdmRecordIndexId(arrayListEducation.get(j).getEduId());

                            education.setEdmSchoolCollegeName(arrayListEducation.get(j).getEduName());
                            education.setEdmCourse(arrayListEducation.get(j).getEduCourse());
                            education.setEdmEducationFromDate(arrayListEducation.get(j)
                                    .getEduFromDate());
                            education.setEdmEducationToDate(arrayListEducation.get(j).getEduToDate());
                            education.setEdmEducationIsCurrent(arrayListEducation.get(j).getIsCurrent
                                    ());
                            education.setEdmEducationIsPrivate(arrayListEducation.get(j).getIsPrivate());
                            education.setEdmEducationPrivacy(String.valueOf(arrayListEducation.get(j)
                                    .getEduPublic()));

                            education.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());

                            arrayListEdu.add(education);
                        }

                        TableEducationMaster tableEducationMaster = new TableEducationMaster
                                (getDatabaseHandler());
                        tableEducationMaster.addArrayEducation(arrayListEdu);
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
                            organization.setOmOrganizationFromDate(arrayListOrganization.get(j)
                                    .getOrgFromDate());
                            organization.setOmOrganizationToDate(arrayListOrganization.get(j)
                                    .getOrgToDate());
                            organization.setOmIsCurrent(String.valueOf(arrayListOrganization.get(j)
                                    .getIsCurrent()));

                            if (arrayListOrganization.get(j).getIsVerify() != null)
                                if (arrayListOrganization.get(j).getIsVerify() == IntegerConstants.RCP_TYPE_PRIMARY) {
                                    organization.setOmOrganizationType(arrayListOrganization.get(j).getOrgIndustryType());
//                                    organization.setOmEnterpriseOrgId(arrayListOrganization.get(j).getOrgEntId());
                                    organization.setOmOrganizationLogo(arrayListOrganization.get(j)
                                            .getEomLogoPath() + "/" + arrayListOrganization.get(j).getEomLogoName());
                                } else {
                                    organization.setOmOrganizationType("");
//                                    organization.setOmEnterpriseOrgId("");
                                    organization.setOmOrganizationLogo("");
                                }
                            else {
                                organization.setOmOrganizationType("");
//                                organization.setOmEnterpriseOrgId("");
                                organization.setOmOrganizationLogo("");
                            }

                            organization.setOmEnterpriseOrgId(arrayListOrganization.get(j).getOrgEntId());
                            organization.setOrgUrlSlug(arrayListOrganization.get(j).getOrgUrlSlug());
                            organization.setOmIsVerified(String.valueOf(arrayListOrganization.get(j).getIsVerify()));
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
                            address.setAmCityId(arrayListAddress.get(j).getCityId());
                            address.setAmCountry(arrayListAddress.get(j).getCountry());
                            address.setAmCountryId(arrayListAddress.get(j).getCountryId());
                            address.setAmFormattedAddress(arrayListAddress.get(j)
                                    .getFormattedAddress());
                            address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                            address.setAmPostCode(arrayListAddress.get(j).getPostCode());
                            address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                            address.setAmState(arrayListAddress.get(j).getState());
                            address.setAmStateId(arrayListAddress.get(j).getStateId());
                            address.setAmStreet(arrayListAddress.get(j).getStreet());
                            address.setAmAddressType(arrayListAddress.get(j).getAddressType());
                            address.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                            address.setAmAddressPrivacy(String.valueOf(arrayListAddress.get(j)
                                    .getAddPublic()));
                            address.setAmIsPrivate(arrayListAddress.get(j).getIsPrivate());
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

                            if (!StringUtils.isBlank(mapLocalRcpId.get(profileData.get(i).getRcpPmId()))) {
                                String newRawIds = existingRawId + "," + mapLocalRcpId.get(profileData
                                        .get(i)
                                        .getRcpPmId());
                                tableProfileMaster.updateRawIds(Integer.parseInt(userProfile
                                                .getPmRcpId()),
                                        newRawIds);
                            } else {
                                return;
                            }
                        }
                    } else {
                        if (existingRawId.equals(mapLocalRcpId.get(profileData.get(i)
                                .getRcpPmId())))
                            return;
                        else {

                            if (!StringUtils.isBlank(mapLocalRcpId.get(profileData.get(i).getRcpPmId()))) {
                                String newRawIds = existingRawId + "," + mapLocalRcpId.get(profileData
                                        .get(i)
                                        .getRcpPmId());
                                tableProfileMaster.updateRawIds(Integer.parseInt(userProfile
                                                .getPmRcpId()),
                                        newRawIds);
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeRemovedDataFromDb(ArrayList<ProfileData> mapping) {

        TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());

        for (int i = 0; i < mapping.size(); i++) {
            String rawId = mapping.get(i).getLocalPhoneBookId();

            ArrayList<String> newRcpIds = new ArrayList<>();
            for (int j = 0; j < mapping.get(i).getRcpPmId().size(); j++) {
                String rcPid = mapping.get(i).getRcpPmId().get(j);
                newRcpIds.add(rcPid);
            }

            ArrayList<String> existingRcpIds = tableProfileMaster.getAllRcpIdFromRawId(rawId);
            existingRcpIds.removeAll(newRcpIds);

            for (int k = 0; k < existingRcpIds.size(); k++) {
                QueryManager queryManager = new QueryManager(getDatabaseHandler());
                queryManager.updateRcProfileDetail(getActivity(), Integer.parseInt(existingRcpIds.get(k)),
                        rawId);
            }

        }
    }

    private void getUserData(String phoneBookId) {

        arrayListPBPhoneNumber = new ArrayList<>();
        arrayListPBEmailAddress = new ArrayList<>();

        // From PhoneBook
        Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(phoneBookId);

        if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
            while (contactNumberCursor.moveToNext()) {

                arrayListPBPhoneNumber.add(Utils.getFormattedNumber(getActivity(),
                        contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER))));

            }
            contactNumberCursor.close();
        }

        //</editor-fold>

        // <editor-fold desc="Email Id">

        // From PhoneBook
        Cursor contactEmailCursor = phoneBookContacts.getContactEmail(phoneBookId);

        if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
            while (contactEmailCursor.moveToNext()) {
                arrayListPBEmailAddress.add(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
            }
            contactEmailCursor.close();
        }

        //</editor-fold>
    }

//    private void showCallConfirmationDialog(final String number) {
//
//        final String finalNumber;
//
//        if (!number.startsWith("+91")) {
//            finalNumber = "+91" + number;
//        } else {
//            finalNumber = number;
//        }
//
//        RippleView.OnRippleCompleteListener cancelListener = new RippleView
//                .OnRippleCompleteListener() {
//
//            @Override
//            public void onComplete(RippleView rippleView) {
//                switch (rippleView.getId()) {
//                    case R.id.rippleLeft:
//                        callConfirmationDialog.dismissDialog();
//                        break;
//
//                    case R.id.rippleRight:
//                        callConfirmationDialog.dismissDialog();
//                       /* Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
//                                number));
//                        startActivity(intent);*/
//                        Utils.callIntent(getActivity(), finalNumber);
//                        break;
//                }
//            }
//        };
//
//        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
//        callConfirmationDialog.setTitleVisibility(View.GONE);
//        callConfirmationDialog.setLeftButtonText(getActivity().getString(R.string.action_cancel));
//        callConfirmationDialog.setRightButtonText(getActivity().getString(R.string.action_call));
//        callConfirmationDialog.setDialogBody(getActivity().getString(R.string.action_call) + " "
//                + finalNumber + "?");
//
//        callConfirmationDialog.showDialog();
//    }
    //</editor-fold>
}
