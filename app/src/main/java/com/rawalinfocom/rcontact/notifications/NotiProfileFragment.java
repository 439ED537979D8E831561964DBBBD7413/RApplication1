package com.rawalinfocom.rcontact.notifications;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.BaseNotificationFragment;
import com.rawalinfocom.rcontact.BuildConfig;
import com.rawalinfocom.rcontact.MainActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.NotiProfileAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableRCContactRequest;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ContactRequestResponseDataItem;
import com.rawalinfocom.rcontact.model.NotiProfileItem;
import com.rawalinfocom.rcontact.model.PrivacyRequestDataItem;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiProfileFragment extends BaseNotificationFragment implements WsResponseListener {

    @BindView(R.id.search_view_profile)
    SearchView searchView;

    /*@BindView(R.id.header1)
    TextView textTodayTitle;

    @BindView(R.id.header1_icon)
    ImageView headerTodayIcon;

    @BindView(R.id.relative_header1)
    RelativeLayout headerTodayLayout;

    @BindView(R.id.recycler_view1)
    RecyclerView recyclerTodayProfile;

    @BindView(R.id.text_header2)
    TextView textPastTitle;

    @BindView(R.id.header2_icon)
    ImageView headerPastIcon;

    @BindView(R.id.relative_header2)
    RelativeLayout headerPastLayout;

    @BindView(R.id.recycler_view2)
    RecyclerView recyclerPastProfile;

    @BindView(R.id.tab_profile)
    TabLayout tabProfile;*/

    @BindView(R.id.tab_profile)
    TabLayout tabProfile;

    @BindView(R.id.text_view_more)
    TextView textViewMore;

    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;
    @BindView(R.id.recycler_view_profile_list)
    RecyclerView recyclerViewProfileList;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private String ppmTag, carID, actionType;
    private int rcpID;

    @Override
    public void getFragmentArguments() {

    }

    NotiProfileAdapter notiProfileAdapter;
    //    NotiProfileAdapter todayProfileAdapter;
//    NotiProfileAdapter pastProfileAdapter;
    List<NotiProfileItem> listAllRequest;
    //    List<NotiProfileItem> listTodayRequest;
//    List<NotiProfileItem> listPastRequest;
    List<NotiProfileItem> listAllResponse;
    //    List<NotiProfileItem> listTodayResponse;
//    List<NotiProfileItem> listPastResponse;
    private static int tabIndex = 0;
    SoftKeyboard softKeyboard;
    String today;
    String yesterDay;
    String pastday6thDay;
    TableRCContactRequest tableRCContactRequest;
    boolean isFirstTime = true;
    boolean isFirst = true;
    int tab = 0;

    public static NotiProfileFragment newInstance() {
        return new NotiProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        tab = arguments.getInt("SUB_TAB_INDEX");
//        args.putInt("SUB_TAB_INDEX", subTabIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_profile_temp, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
        initData();
        bindWidgetsWithAnEvent();
        setUpTabLayout();
//        getAllProfileRequestAndResponse(this);
    }

    private void setUpTabLayout() {
        if (tabProfile != null) {
            tabProfile.addTab(tabProfile.newTab().setText(getResources().getString(R.string
                    .text_tab_request)), true);
            tabProfile.addTab(tabProfile.newTab().setText(getResources().getString(R.string
                    .text_tab_response)));
        }
        if (tabProfile != null)
            tabProfile.getTabAt(tab).select();
        tabIndex = tab;
    }

    private void bindWidgetsWithAnEvent() {
        tabProfile.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabIndex = tab.getPosition();
                if (tabIndex == 0) {

                    if (notiProfileAdapter != null)
                        notiProfileAdapter.updateList(listAllRequest);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null)
                                ((NotificationsDetailActivity) getActivity())
                                        .updateNotificationCount(AppConstants
                                                .NOTIFICATION_TYPE_PROFILE_REQUEST);
                        }
                    }, 300);

//                    textPastTitle.setText(getString(R.string.past_pending_requests));

                    /*if (todayProfileAdapter != null)
                        todayProfileAdapter.updateList(listTodayRequest);
                    if (pastProfileAdapter != null) {
                        pastProfileAdapter.updateList(listPastRequest);
                        updateHeight();

                    }*/

                } else {

//                    textPastTitle.setText(getString(R.string.past_received_response));

                    if (notiProfileAdapter != null)
                        notiProfileAdapter.updateList(listAllResponse);

                    if (isFirstTime) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (getActivity() != null)
                                    ((NotificationsDetailActivity) getActivity())
                                            .updateNotificationCount(AppConstants
                                                    .NOTIFICATION_TYPE_PROFILE_RESPONSE);
                            }
                        }, 300);
                        isFirstTime = false;
                    }

//                    if (todayProfileAdapter != null)
//                        todayProfileAdapter.updateList(listTodayResponse);
//                    if (pastProfileAdapter != null) {
//                        pastProfileAdapter.updateList(listPastResponse);
//                        updateHeight();
//
//
//                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getAllProfileRequestAndResponse(NotiProfileFragment fragment) {

        WsRequestObject requestObject = new WsRequestObject();

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(fragment, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    requestObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_PROFILE_PRIVACY_REQUEST, getResources().getString(R.string
                    .msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_GET_PROFILE_PRIVACY_REQUEST);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_no_network),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        tableRCContactRequest = new TableRCContactRequest(getDatabaseHandler());
//        tableRCContactRequest.deleteAll();
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Service
                .INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(layoutRoot, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                // Code here
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        textViewMore.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onSoftKeyboardShow() {
                // Code here
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        textViewMore.setVisibility(View.GONE);
                    }
                });
            }
        });

//        textTodayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
//        textPastTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textViewMore.setTypeface(Utils.typefaceRegular(getActivity()));
//        headerTodayIcon.setImageResource(R.drawable.ic_collapse);
//        headerTodayLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (recyclerTodayProfile.getVisibility() == View.VISIBLE) {
//                    recyclerTodayProfile.setVisibility(View.GONE);
//                    headerTodayIcon.setImageResource(R.drawable.ic_expand);
//                } else {
//                    recyclerTodayProfile.setVisibility(View.VISIBLE);
//                    headerTodayIcon.setImageResource(R.drawable.ic_collapse);
//                }
//                recyclerPastProfile.setVisibility(View.GONE);
//                headerPastIcon.setImageResource(R.drawable.ic_expand);
//            }
//        });
//        headerPastLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerTodayProfile.setVisibility(View.GONE);
//                headerTodayIcon.setImageResource(R.drawable.ic_expand);
//                if (recyclerPastProfile.getVisibility() == View.VISIBLE) {
//                    recyclerPastProfile.setVisibility(View.GONE);
//                    headerPastIcon.setImageResource(R.drawable.ic_expand);
//                } else {
//                    recyclerPastProfile.setVisibility(View.VISIBLE);
//                    headerPastIcon.setImageResource(R.drawable.ic_collapse);
//                }
//
//            }
//        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query, tabIndex);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (tabIndex == 0) {
                    if (TextUtils.isEmpty(newText)) {
                        notiProfileAdapter.updateList(listAllRequest);
//                        todayProfileAdapter.updateList(listTodayRequest);
//                        pastProfileAdapter.updateList(listPastRequest);
//                        updateHeight();
                    }
                } else {
                    if (TextUtils.isEmpty(newText)) {
                        notiProfileAdapter.updateList(listAllResponse);
//                        todayProfileAdapter.updateList(listTodayResponse);
//                        pastProfileAdapter.updateList(listPastResponse);
//                        updateHeight();
                    }

                }
                return false;
            }

        });
        textViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openWebSite(getActivity().getApplicationContext());
            }
        });
    }

    private void filter(String query, int tabIndex) {

        if (tabIndex == 0) {
            List<NotiProfileItem> temp = new ArrayList<>();
            for (NotiProfileItem item : listAllRequest) {
                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            notiProfileAdapter.updateList(temp);

//            temp = new ArrayList<>();
//            for (NotiProfileItem item : listPastRequest) {
//                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            pastProfileAdapter.updateList(temp);
//            updateHeight();
//            List<NotiProfileItem> temp = new ArrayList<>();
//            for (NotiProfileItem item : listTodayRequest) {
//                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            todayProfileAdapter.updateList(temp);
//
//            temp = new ArrayList<>();
//            for (NotiProfileItem item : listPastRequest) {
//                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            pastProfileAdapter.updateList(temp);
//            updateHeight();
        } else {

            List<NotiProfileItem> temp = new ArrayList<>();
            for (NotiProfileItem item : listAllResponse) {
                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            notiProfileAdapter.updateList(temp);

//            List<NotiProfileItem> temp = new ArrayList<>();
//            for (NotiProfileItem item : listTodayResponse) {
//                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            todayProfileAdapter.updateList(temp);
//
//            temp = new ArrayList<>();
//            for (NotiProfileItem item : listPastResponse) {
//                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            pastProfileAdapter.updateList(temp);
//            updateHeight();
        }
    }

    private void initData() {

        today = getDate(0);
        yesterDay = getDate(-1);
        pastday6thDay = getDate(-6);

        ArrayList<PrivacyRequestDataItem> pendingRequestToday = tableRCContactRequest
                .getAllPendingRequest(pastday6thDay, today);
//        ArrayList<PrivacyRequestDataItem> pendingRequestToday = tableRCContactRequest
//                .getAllPendingRequest(today, today);
//        ArrayList<PrivacyRequestDataItem> pendingRequestPastDays = tableRCContactRequest
//                .getAllPendingRequest(pastday6thDay, yesterDay);

        ArrayList<PrivacyRequestDataItem> responseReceivedToday = tableRCContactRequest
                .getAllResponseReceived(pastday6thDay, today);
//        ArrayList<PrivacyRequestDataItem> responseReceivedToday = tableRCContactRequest
//                .getAllResponseReceived(today, today);
//        ArrayList<PrivacyRequestDataItem> responseReceivedPastDays = tableRCContactRequest
//                .getAllResponseReceived(pastday6thDay, yesterDay);

        listAllRequest = createRatingList(pendingRequestToday, 0);
//        listTodayRequest = createRatingList(pendingRequestToday, 0);
//        listPastRequest = createRatingList(pendingRequestPastDays, 0);
        listAllResponse = createRatingList(responseReceivedToday, 1);
//        listTodayResponse = createRatingList(responseReceivedToday, 1);
//        listPastResponse = createRatingList(responseReceivedPastDays, 1);

        notiProfileAdapter = new NotiProfileAdapter(this, listAllRequest,
                new NotiProfileAdapter.OnClickListener() {
                    @Override
                    public void onClick(String type, String carId, int rcpId, String action) {
                        ppmTag = type;
                        carID = carId;
                        rcpID = rcpId;
                        actionType = action;
                    }
                });
//        todayProfileAdapter = new NotiProfileAdapter(this, listTodayRequest, 0);
//        pastProfileAdapter = new NotiProfileAdapter(this, listPastRequest, 1);
        recyclerViewProfileList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewProfileList.setAdapter(notiProfileAdapter);
//        recyclerTodayProfile.setAdapter(todayProfileAdapter);
//        recyclerPastProfile.setAdapter(pastProfileAdapter);

//        updateHeight();

//        recyclerPastProfile.setVisibility(View.GONE);

        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(true);

                String fromDate = Utils.getStringPreference(getActivity(), AppConstants
                        .KEY_API_CALL_TIME_STAMP_PROFILE, "");
                pullMechanismServiceCall(fromDate, "", WsConstants
                        .REQ_GET_CONTACT_REQUEST);
            }
        });
    }

    private List<NotiProfileItem> createRatingList(ArrayList<PrivacyRequestDataItem>
                                                           listRequests, int listType) {
        List<NotiProfileItem> list = new ArrayList<>();
        for (PrivacyRequestDataItem request : listRequests) {
            NotiProfileItem item = new NotiProfileItem();
            TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());

            int pmId = request.getCarPmIdFrom();
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);
            if (userProfile.getPmFirstName().equalsIgnoreCase("") || userProfile.getPmLastName().equalsIgnoreCase("")) {
                item.setPersonName(request.getName());
                item.setPersonImage(request.getPmProfilePhoto());
            } else {
                item.setPersonName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
                item.setPersonImage(userProfile.getPmProfileImage());
                item.setPmRawId(userProfile.getPmRawId());
            }

            if (listType == 0) {
                item.setProfileNotiType(0);
                item.setNotiInfo(String.format(item.getPersonName() + " " + getActivity().getString(R.string
                        .str_requested_for_your) + " ", request.getPpmTag()));
            } else {
                item.setProfileNotiType(1);
                item.setNotiInfo(String.format(item.getPersonName() + " " + getActivity().getString(R.string
                        .str_confirmed_your_request_for) + " ", request.getPpmTag()));
            }
            item.setPpmTag(request.getPpmTag());
            item.setRcpUserPmId(pmId + "");
            item.setCardCloudId(request.getCarRequestId());
            item.setNotiRequestTime(request.getUpdatedAt());
            list.add(item);

        }
        return list;
    }

    private String getDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        date.setTime(date.getTime() + dayToAddorSub * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }

    private void updateHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int density = getResources().getDisplayMetrics().densityDpi;
        int heightPercent = 35;

        switch (density) {
            case DisplayMetrics.DENSITY_LOW: /*120*/
                heightPercent = 25;

                break;
            case DisplayMetrics.DENSITY_MEDIUM: /*160*/
                heightPercent = 25;

                break;
            case DisplayMetrics.DENSITY_HIGH: /*240*/
                heightPercent = 30;

                break;
            case DisplayMetrics.DENSITY_XHIGH: /*320*/
                heightPercent = 37;

                break;
            case DisplayMetrics.DENSITY_XXHIGH: /*480*/
            case DisplayMetrics.DENSITY_XXXHIGH: /*680*/
                heightPercent = 40;
                break;
        }
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * heightPercent) / 100;

//
//        setRecyclerViewHeight(recyclerTodayProfile, height);
//        setRecyclerViewHeight(recyclerPastProfile, height);
    }

    private void setRecyclerViewHeight(RecyclerView recyclerView, int height) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int heightRecycler = recyclerView.getMeasuredHeight();
        if (heightRecycler > height) {
            recyclerView.getLayoutParams().height = height;
        } else {
            recyclerView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();

    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            WsResponseObject wsResponseObject = (WsResponseObject) data;
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_PROFILE_PRIVACY_REQUEST)) {
                ArrayList<PrivacyRequestDataItem> profileData = wsResponseObject
                        .getPrivacyRequestData();
                saveDataToDB(profileData);
            }

            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_PRIVACY_RESPOND)) {
                WsResponseObject privacyResponse = (WsResponseObject) data;
                if (privacyResponse != null && StringUtils.equalsIgnoreCase
                        (privacyResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    String msg = wsResponseObject.getMessage();
                    PrivacyRequestDataItem item = wsResponseObject.getContactRequestData();

                    try {

//                        if (MoreObjects.firstNonNull(item.getCarAccessPermissionStatus(), 0) == 1 ||
//                                MoreObjects.firstNonNull(item.getCarAccessPermissionStatus(), 0)
//                                        == 2) {
//                            boolean deleted = tableRCContactRequest.removeRequest(item.getCarId());
                        boolean deleted = tableRCContactRequest.removeRequest(ppmTag, carID, rcpID, actionType);
                        if (deleted) {
                            refreshAllList();
                        }
                        Utils.showSuccessSnackBar(activity, layoutRoot, msg);
//                            Utils.showSuccessSnackBar(getActivity(),);
//                        } else {
//                            Utils.showErrorSnackBar(activity, layoutRoot, getResources().getString(R.string.msg_try_later));
//                        }
                    } catch (Exception e) {
                        Utils.showErrorSnackBar(activity, layoutRoot, getResources().getString(R.string.msg_try_later));
                    }
                } else {
                    if (privacyResponse != null) {
                        Utils.showErrorSnackBar(activity, layoutRoot, privacyResponse.getMessage());
                        System.out.println("RContact error --> " + privacyResponse.getMessage());
                    } else {
                        Utils.showErrorSnackBar(activity, layoutRoot, getResources().getString(R.string.msg_try_later));
                        System.out.println("RContact error --> privacyResponse null");
                    }
                }
            }
            // <editor-fold desc="REQ_GET_CONTACT_REQUEST">
            if (serviceType.contains(WsConstants.REQ_GET_CONTACT_REQUEST)) {
                WsResponseObject getContactUpdateResponse = (WsResponseObject) data;

                // cancel the Visual indication of a refresh
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if (getContactUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getContactUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    storeContactRequestResponseToDB(getContactUpdateResponse, getContactUpdateResponse.getRequestData(),
                            getContactUpdateResponse.getResponseData());
                    refreshAllList();

                } else {
                    if (getContactUpdateResponse != null) {
                        System.out.println("RContact error --> " + getContactUpdateResponse.getMessage());
                    } else {
                        System.out.println("RContact error --> getContactUpdateResponse null");
                    }
                }
            }
            Utils.hideProgressDialog();
        } else {
            Utils.hideProgressDialog();
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_try_later), Toast
                    .LENGTH_SHORT).show();
        }
    }

    // PROFILE PRIVATE DATA SHOWN REQUEST HISTORY RESTORE
    private void storeContactRequestResponseToDB(WsResponseObject getContactUpdateResponse,
                                                 ArrayList<ContactRequestResponseDataItem> requestData,
                                                 ArrayList<ContactRequestResponseDataItem> responseData) {

        try {

            for (int i = 0; i < requestData.size(); i++) {

                ContactRequestResponseDataItem dataItem = requestData.get(i);
                if (String.valueOf(dataItem.getCarPmIdTo()).equals(Utils.getStringPreference(getActivity(), AppConstants
                        .PREF_USER_PM_ID, "0")) && dataItem.getCarAccessPermissionStatus() == 0) {
                    tableRCContactRequest.addRequest(AppConstants
                                    .COMMENT_STATUS_RECEIVED,
                            String.valueOf(dataItem.getCarId()),
                            dataItem.getCarMongodbRecordIndex(),
                            dataItem.getCarPmIdFrom(),
                            dataItem.getCarPpmParticular(),
                            Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()),
                            Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()),
                            dataItem.getName(), dataItem.getPmProfilePhoto());
                }
            }

            for (int i = 0; i < responseData.size(); i++) {

                ContactRequestResponseDataItem dataItem = responseData.get(i);
                if (String.valueOf(dataItem.getCarPmIdFrom()).equals(Utils.getStringPreference(getActivity(), AppConstants
                        .PREF_USER_PM_ID, "0"))
                        && dataItem.getCarAccessPermissionStatus() == 1) {
                    tableRCContactRequest.addRequest(AppConstants
                                    .COMMENT_STATUS_SENT,
                            String.valueOf(dataItem.getCarId()),
                            dataItem.getCarMongodbRecordIndex(),
                            dataItem.getCarPmIdTo(),
                            dataItem.getCarPpmParticular(),
                            Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()),
                            Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()),
                            dataItem.getName(), dataItem.getPmProfilePhoto());
                }
            }

        } catch (Exception e) {
            System.out.println("RContact storeContactRequestResponseToDB error ");
        }

        if (!StringUtils.isEmpty(getContactUpdateResponse.getTimestamp())) {
            Utils.setStringPreference(getActivity(), AppConstants.KEY_API_CALL_TIME_STAMP_PROFILE,
                    getContactUpdateResponse.getTimestamp());
        }
    }

    private void saveDataToDB(ArrayList<PrivacyRequestDataItem> profileData) {
        if (profileData == null) {
            return;
        }
        for (PrivacyRequestDataItem data : profileData) {

            if (data.getCarPmIdTo() == Integer.parseInt(getUserPmId()) && data
                    .getCarAccessPermissionStatus() == 0) {
                //Request
                tableRCContactRequest.addRequest(AppConstants.COMMENT_STATUS_RECEIVED,
                        data.getCarId() + "",
                        data.getCarMongodbRecordIndex(),
                        data.getCarPmIdFrom(),
                        data.getPpmParticular(),
                        Utils.getLocalTimeFromUTCTime(data.getCreatedAt()),
                        Utils.getLocalTimeFromUTCTime(data.getUpdatedAt()),
                        data.getName(), data.getPmProfilePhoto());
            } else if (data.getCarPmIdFrom() == Integer.parseInt(getUserPmId()) && data
                    .getCarAccessPermissionStatus() == 1) {
                //Response
                tableRCContactRequest.addRequest(AppConstants.COMMENT_STATUS_SENT,
                        data.getCarId() + "",
                        data.getCarMongodbRecordIndex(),
                        data.getCarPmIdTo(),
                        data.getPpmParticular(),
                        Utils.getLocalTimeFromUTCTime(data.getCreatedAt()),
                        Utils.getLocalTimeFromUTCTime(data.getUpdatedAt()),
                        data.getName(), data.getPmProfilePhoto());
                // updatePrivacySetting(data.getPpmTag(),data.getCarMongodbRecordIndex());
            }

        }
        refreshAllList();
    }

    private void refreshAllList() {

        ArrayList<PrivacyRequestDataItem> pendingRequestToday = tableRCContactRequest
                .getAllPendingRequest(pastday6thDay, today);
//        ArrayList<PrivacyRequestDataItem> pendingRequestToday = tableRCContactRequest
//                .getAllPendingRequest(today, today);
//        ArrayList<PrivacyRequestDataItem> pendingRequestPastDays = tableRCContactRequest
//                .getAllPendingRequest(pastday6thDay, yesterDay);
        ArrayList<PrivacyRequestDataItem> responseReceivedToday = tableRCContactRequest
                .getAllResponseReceived(pastday6thDay, today);
//        ArrayList<PrivacyRequestDataItem> responseReceivedToday = tableRCContactRequest
//                .getAllResponseReceived(today, today);
//        ArrayList<PrivacyRequestDataItem> responseReceivedPastDays = tableRCContactRequest
//                .getAllResponseReceived(pastday6thDay, yesterDay);


        listAllRequest = createRatingList(pendingRequestToday, 0);
//        listTodayRequest = createRatingList(pendingRequestToday, 0);
//        listPastRequest = createRatingList(pendingRequestPastDays, 0);
        listAllResponse = createRatingList(responseReceivedToday, 1);
//        listTodayResponse = createRatingList(responseReceivedToday, 1);
//        listPastResponse = createRatingList(responseReceivedPastDays, 1);

        if (tabIndex == 0) {
            notiProfileAdapter.updateList(listAllRequest);
//            todayProfileAdapter.updateList(listTodayRequest);
//            pastProfileAdapter.updateList(listPastRequest);
        } else {
            notiProfileAdapter.updateList(listAllResponse);
//            todayProfileAdapter.updateList(listTodayResponse);
//            pastProfileAdapter.updateList(listPastResponse);
        }

//        updateHeight();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        null.unbind();
    }

    private void pullMechanismServiceCall(String fromDate, String toDate, String url) {

        WsRequestObject deviceDetailObject = new WsRequestObject();

        deviceDetailObject.setFromDate(fromDate);
        deviceDetailObject.setToDate(toDate);

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), deviceDetailObject, null,
                    WsResponseObject.class, url, getResources().getString(R.string.msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT + url);
        } else {
            Utils.showErrorSnackBar(getActivity(), layoutRoot, getResources().getString(R.string.msg_no_network));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private BroadcastReceiver localBroadCastReceiverUpdateCount = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                refreshAllList();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

//    private String updatePrivacySetting(String ppmTag, String cloudMongoId) {
//        switch (ppmTag) {
//            case "pb_phone_number":
//                TableMobileMaster tableMobileMaster= new TableMobileMaster(getDatabaseHandler());
//                tableMobileMaster.updatePrivacySetting(cloudMongoId);
//                break;
//            case "pb_email_id":
//                TableEmailMaster tableEmailMaster= new TableEmailMaster(getDatabaseHandler());
//                tableEmailMaster.updatePrivacySetting(cloudMongoId);
//                break;
//            case "pb_address":
//                TableAddressMaster tableAddressMaster= new TableAddressMaster
// (getDatabaseHandler());
//                tableAddressMaster.updatePrivacySetting(cloudMongoId);
//                break;
//            case "pb_im_accounts":
//                TableImMaster tableImMaster=new TableImMaster(getDatabaseHandler());
//                tableImMaster.updatePrivacySetting(cloudMongoId);
//                break;
//            case "pb_event":
//                TableEventMaster tableEventMaster= new TableEventMaster(getDatabaseHandler());
//                tableEventMaster.updatePrivacySetting(cloudMongoId);
//                break;
//        }
//        return "";
//
//    }
}
