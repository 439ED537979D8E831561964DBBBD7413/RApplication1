package com.rawalinfocom.rcontact.notifications;

import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseFragment;
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
import com.rawalinfocom.rcontact.model.NotiProfileItem;
import com.rawalinfocom.rcontact.model.PrivacyRequestDataItem;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiProfileFragment extends BaseFragment implements WsResponseListener {

    @BindView(R.id.search_view_profile)
    SearchView searchView;

    @BindView(R.id.header1)
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

    @BindView(R.id.text_view_more)
    TextView textViewMore;

    @BindView(R.id.tab_profile)
    TabLayout tabProfile;
    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;

    @Override
    public void getFragmentArguments() {

    }

    NotiProfileAdapter todayProfileAdapter;
    NotiProfileAdapter pastProfileAdapter;
    List<NotiProfileItem> listTodayRequest;
    List<NotiProfileItem> listPastRequest;
    List<NotiProfileItem> listTodayResponse;
    List<NotiProfileItem> listPastResponse;
    private static int tabIndex = 0;
    SoftKeyboard softKeyboard;
    String today;
    String yesterDay;
    String dayBeforeYesterday;
    String pastday6thDay;
    TableRCContactRequest tableRCContactRequest;

    public static NotiProfileFragment newInstance() {
        return new NotiProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
        initData();
        getAllProfileRequestAndResponse(this);
    }

    private void getAllProfileRequestAndResponse(NotiProfileFragment fragment) {

        WsRequestObject requestObject = new WsRequestObject();

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(fragment, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    requestObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_PROFILE_PRIVACY_REQUEST, getResources().getString(R.string.msg_please_wait), true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_GET_PROFILE_PRIVACY_REQUEST);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        tableRCContactRequest = new TableRCContactRequest(getDatabaseHandler());
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
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
        tabProfile.addTab(tabProfile.newTab().setText(getResources().getString(R.string.text_tab_request)), true);
        tabProfile.addTab(tabProfile.newTab().setText(getResources().getString(R.string.text_tab_response)));
        tabProfile.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabIndex = tab.getPosition();
                if (tabIndex == 0) {
                    if (todayProfileAdapter != null)
                        todayProfileAdapter.updateList(listTodayRequest);
                    if (pastProfileAdapter != null) {
                        pastProfileAdapter.updateList(listPastRequest);
                        updateHeight();
                    }
                } else {
                    if (todayProfileAdapter != null)
                        todayProfileAdapter.updateList(listTodayResponse);
                    if (pastProfileAdapter != null) {
                        pastProfileAdapter.updateList(listPastResponse);
                        updateHeight();
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        textTodayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textPastTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textViewMore.setTypeface(Utils.typefaceRegular(getActivity()));
        headerTodayIcon.setImageResource(R.drawable.ic_collapse);
        headerTodayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerTodayProfile.getVisibility() == View.VISIBLE) {
                    recyclerTodayProfile.setVisibility(View.GONE);
                    headerTodayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerTodayProfile.setVisibility(View.VISIBLE);
                    headerTodayIcon.setImageResource(R.drawable.ic_collapse);
                }
                recyclerPastProfile.setVisibility(View.GONE);
                headerPastIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerPastLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerTodayProfile.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);
                if (recyclerPastProfile.getVisibility() == View.VISIBLE) {
                    recyclerPastProfile.setVisibility(View.GONE);
                    headerPastIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerPastProfile.setVisibility(View.VISIBLE);
                    headerPastIcon.setImageResource(R.drawable.ic_collapse);
                }

            }
        });
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
                        todayProfileAdapter.updateList(listTodayRequest);
                        pastProfileAdapter.updateList(listPastRequest);
                        updateHeight();
                    }
                } else {
                    if (TextUtils.isEmpty(newText)) {
                        todayProfileAdapter.updateList(listTodayResponse);
                        pastProfileAdapter.updateList(listPastResponse);
                        updateHeight();
                    }

                }
                return false;
            }

        });
    }

    private void filter(String query, int tabIndex) {

        if (tabIndex == 0) {
            List<NotiProfileItem> temp = new ArrayList<>();
            for (NotiProfileItem item : listTodayRequest) {
                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            todayProfileAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (NotiProfileItem item : listPastRequest) {
                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            pastProfileAdapter.updateList(temp);
            updateHeight();
        } else {

            List<NotiProfileItem> temp = new ArrayList<>();
            for (NotiProfileItem item : listTodayResponse) {
                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            todayProfileAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (NotiProfileItem item : listPastResponse) {
                if (item.getPersonName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            pastProfileAdapter.updateList(temp);
            updateHeight();
        }
    }

    private void initData() {

        today = getDate(0);
        yesterDay = getDate(-1);
        pastday6thDay = getDate(-6);
        ArrayList<PrivacyRequestDataItem> pendingRequestToday = tableRCContactRequest.getAllPendingRequest(today, today);
        ArrayList<PrivacyRequestDataItem> pendingRequestPastDays = tableRCContactRequest.getAllPendingRequest(pastday6thDay, yesterDay);

        ArrayList<PrivacyRequestDataItem> responseReceivedToday = tableRCContactRequest.getAllResponseReceived(today, today);
        ArrayList<PrivacyRequestDataItem> responseReceivedPastDays = tableRCContactRequest.getAllResponseReceived(pastday6thDay, yesterDay);

        listTodayRequest = createRatingList(pendingRequestToday, 0);
        listPastRequest = createRatingList(pendingRequestPastDays, 0);
        listTodayResponse = createRatingList(responseReceivedToday, 1);
        listPastResponse = createRatingList(responseReceivedPastDays, 1);

        todayProfileAdapter = new NotiProfileAdapter(this, listTodayRequest, 0);
        pastProfileAdapter = new NotiProfileAdapter(this, listPastRequest, 1);

        recyclerTodayProfile.setAdapter(todayProfileAdapter);
        recyclerPastProfile.setAdapter(pastProfileAdapter);

        updateHeight();

        recyclerPastProfile.setVisibility(View.GONE);
    }

    private List<NotiProfileItem> createRatingList(ArrayList<PrivacyRequestDataItem> listRequests, int listType) {
        List<NotiProfileItem> list = new ArrayList<>();
        for (PrivacyRequestDataItem request : listRequests) {
            NotiProfileItem item = new NotiProfileItem();
            TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());

            int pmId = request.getCarPmIdFrom();
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);
            item.setPersonName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
            if (listType == 0) {

                item.setProfileNotiType(0);
                item.setNotiInfo(item.getPersonName() + " Requested for your " + request.getPpmTag());
            } else {
                item.setProfileNotiType(1);
                item.setNotiInfo(item.getPersonName() + " confirmed your request for " + request.getPpmTag());
            }
            item.setRcpUserPmId(pmId + "");
            item.setCardCloudId(request.getCarId());
            item.setNotiRequestTime(request.getUpdatedAt());
            list.add(item);

        }
        return list;
    }

    private String getDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
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


        setRecyclerViewHeight(recyclerTodayProfile, height);
        setRecyclerViewHeight(recyclerPastProfile, height);
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
                ArrayList<PrivacyRequestDataItem> profileData = wsResponseObject.getPrivacyRequestData();
                saveDataToDB(profileData);
            } else if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_PRIVACY_REQUEST)) {
                String msg = wsResponseObject.getMessage();
                PrivacyRequestDataItem item = wsResponseObject.getContactRequestData();
                if (item.getCarAccessPermissionStatus() == 1 || item.getCarAccessPermissionStatus() == 2) {
                    boolean deleted = tableRCContactRequest.removeRequest(item.getCarId());
                    if (deleted) {
                        refreshAllList();
                    }
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
                }

            }
            Utils.hideProgressDialog();
        } else {
            Utils.hideProgressDialog();
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDataToDB(ArrayList<PrivacyRequestDataItem> profileData) {
        if (profileData == null) {
            return;
        }
        for (PrivacyRequestDataItem data : profileData) {

            if (data.getCarPmIdTo() == Integer.parseInt(getUserPmId()) && data.getCarAccessPermissionStatus() == 0) {
                tableRCContactRequest.addRequest(AppConstants.COMMENT_STATUS_RECEIVED,
                        data.getCarId() + "",
                        data.getCarMongodbRecordIndex(),
                        data.getCarPmIdFrom(),
                        data.getPpmParticular(),
                        Utils.getLocalTimeFromUTCTime(data.getCreatedAt()),
                        Utils.getLocalTimeFromUTCTime(data.getUpdatedAt()));
            } else if (data.getCarPmIdFrom() == Integer.parseInt(getUserPmId()) && data.getCarAccessPermissionStatus() == 1) {
                tableRCContactRequest.addRequest(AppConstants.COMMENT_STATUS_SENT,
                        data.getCarId() + "",
                        data.getCarMongodbRecordIndex(),
                        data.getCarPmIdTo(),
                        data.getPpmParticular(),
                        Utils.getLocalTimeFromUTCTime(data.getCreatedAt()),
                        Utils.getLocalTimeFromUTCTime(data.getUpdatedAt()));
            }

        }
        refreshAllList();
    }

    private void refreshAllList() {

        ArrayList<PrivacyRequestDataItem> pendingRequestToday = tableRCContactRequest.getAllPendingRequest(today, today);
        ArrayList<PrivacyRequestDataItem> pendingRequestPastDays = tableRCContactRequest.getAllPendingRequest(pastday6thDay, yesterDay);
        ArrayList<PrivacyRequestDataItem> responseReceivedToday = tableRCContactRequest.getAllResponseReceived(today, today);
        ArrayList<PrivacyRequestDataItem> responseReceivedPastDays = tableRCContactRequest.getAllResponseReceived(pastday6thDay, yesterDay);


        listTodayRequest = createRatingList(pendingRequestToday, 0);
        listPastRequest = createRatingList(pendingRequestPastDays, 0);
        listTodayResponse = createRatingList(responseReceivedToday, 1);
        listPastResponse = createRatingList(responseReceivedPastDays, 1);

        if (tabIndex == 0) {
            todayProfileAdapter.updateList(listTodayRequest);
            pastProfileAdapter.updateList(listPastRequest);
        } else {
            todayProfileAdapter.updateList(listTodayResponse);
            pastProfileAdapter.updateList(listPastResponse);
        }

        updateHeight();
    }

    private String getRequestText(String ppmTag, String cloudMongoId) {
        String requestTypeString;
        switch (ppmTag) {
            case "pb_phone_number":
                break;
            case "pb_email_id":
                break;
            case "pb_organization":
                break;
            case "pb_address":
                break;
            case "pb_web_address":
                break;
            case "pb_event":
                break;
            case "pb_im_accounts":
                break;
        }
        return "";

    }
}
