package com.rawalinfocom.rcontact.calllog;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.SimpleCallLogListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookCallLogs;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogFragment extends BaseFragment implements WsResponseListener, RippleView
        .OnRippleCompleteListener/*, LoaderManager.LoaderCallbacks<Cursor>*/ {


    private final String ALL_CALLS = "All";
    private final String INCOMING_CALLS = "Incoming";
    private final String OUTGOING_CALLS = "Outgoing";
    private final String MISSED_CALLS = "Missed";

    @BindView(R.id.progressBarCallLog)
    ProgressBar progressBarCallLog;
    @BindView(R.id.linearMainContent)
    LinearLayout linearMainContent;
    @BindView(R.id.linearCallLogMain)
    RelativeLayout linearCallLogMain;
    @BindView(R.id.text_loading)
    TextView textLoading;
    @BindView(R.id.spinner_call_filter)
    Spinner spinnerCallFilter;
    @BindView(R.id.recycler_call_logs)
    RecyclerView recyclerCallLogs;
    @BindView(R.id.progressBarLoadCallLogs)
    ProgressBar progressBarLoadCallLogs;
    @BindView(R.id.relativeLoadingData)
    RelativeLayout relativeLoadingData;
    @BindView(R.id.text_loading_logs)
    TextView textLoadingLogs;
    @BindView(R.id.button_view_old_records)
    Button buttonViewOldRecords;
    @BindView(R.id.ripple_view_old_records)
    RippleView rippleViewOldRecords;
    @BindView(R.id.text_grant_permission)
    TextView textGrantPermission;
    TextView textNoCallsFound;

    //    private CallLogListAdapter callLogListAdapter;
    private SimpleCallLogListAdapter simpleCallLogListAdapter;
//    private NewCallLogListAdapter newCallLogListAdapter;

    ArrayList<Object> arrayListObjectCallLogs;
    ArrayList<String> arrayListCallLogHeader;
    ArrayList<CallLogType> arrayListCallLogs;
    ArrayList<CallLogType> arrayListCallLogsHistory;
    ArrayList<CallLogType> tempList;
    ArrayList<CallLogType> callLogsListbyChunck;
    ArrayList<CallLogType> newList;
    MaterialDialog callConfirmationDialog;
    String selectedCallType = "";
    View mainView;

    private String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG};
    public static CallLogType callLogTypeReceiver;
    private static boolean startInsertion = false;
    private boolean isFirstTime;
    boolean isFirstChuck = false;
    private int LIST_PARTITION_COUNT = 7;
    int count = 0;
    int logsDisplayed = 0;
    RContactApplication rContactApplication;
    MaterialDialog permissionConfirmationDialog;
    ArrayList<String> listOfIds;
    ArrayList<String> callLogIdsListByChunck;
    int spinnerCount = 0;
    LinearLayoutManager mLinearLayoutManager;
    int adapterSetCount = 0;
    boolean isLastRecord = false;
    private static int firstVisibleInListview;
    public static boolean isIdsFetchedFirstTime = false;
    boolean noOfIdsToFetch = false;
    boolean isCallLogFragment = false;
    private boolean isResumeCalled = false;
    boolean isSpinnerAllCall = false;
    int allCalldisplayDataCount = 0;


    public CallLogFragment() {
        // Required empty public constructor
    }

    public static CallLogFragment newInstance() {
        return new CallLogFragment();
    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.isRecentCallFromSMSTab) {
            AppConstants.isRecentCallFromSMSTab = false;
        } else {
            callLogTypeReceiver = new CallLogType();
        }
        if (Utils.getBooleanPreference(getActivity(), AppConstants
                .PREF_CALL_LOG_STARTS_FIRST_TIME, true)) {
            isFirstTime = true;
            Utils.setBooleanPreference(getActivity(), AppConstants
                    .PREF_CALL_LOG_STARTS_FIRST_TIME, false);
        }

        if (!AppConstants.isFirstTime())
            AppConstants.setIsFirstTime(true);

        Utils.setBooleanPreference(getActivity(), AppConstants
                .PREF_RECENT_CALLS_BROADCAST_RECEIVER_MAIN_INSTANCE, false);
        Utils.setBooleanPreference(getActivity(), AppConstants
                .PREF_RECENT_SMS_BROADCAST_RECEIVER_MAIN_INSTANCE, true);
        Utils.setBooleanPreference(getActivity(), AppConstants
                .PREF_RECENT_CALLS_BROADCAST_RECEIVER_CALL_LOG_TAB, true);

        isCallLogFragment = true;
    }

    @Override
    public void getFragmentArguments() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_call_log, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }


    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_view_old_records:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (AppConstants.isFromReceiver) {
                        AppConstants.isFromReceiver = false;
                        isResumeCalled = true;
                        getRecentCallLog();

                    }
                }
            }, 1500);

        } catch (Exception e) {
            e.printStackTrace();
        }
        registerLocalbroadcast();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isFirstTime) {
            isFirstTime = false;
        }
        AppConstants.isFromReceiver = false;
        if (isLastRecord)
            allCalldisplayDataCount = 0;

        LocalBroadcastManager localBroadcastManagerTabChange = LocalBroadcastManager.getInstance
                (getActivity());
        localBroadcastManagerTabChange.unregisterReceiver(localBroadcastReceiverTabChange);

    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        try {
            if (error == null) {
                if (serviceType.equalsIgnoreCase(WsConstants.REQ_UPLOAD_CALL_LOGS)) {
                    WsResponseObject callLogInsertionResponse = (WsResponseObject) data;
                    if (callLogInsertionResponse != null && StringUtils.equalsIgnoreCase
                            (callLogInsertionResponse
                                    .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                        if (Utils.getBooleanPreference(getActivity(), AppConstants
                                .PREF_CALL_LOG_SYNCED, false)) {
                            ArrayList<CallLogType> temp = divideCallLogByChunck(newList);
                            if (temp.size() >= LIST_PARTITION_COUNT) {
                                if (temp != null && temp.size() > 0)
                                    insertServiceCall(newList);
                            } else {
                                Utils.showSuccessSnackBar(getActivity(), linearCallLogMain, "All " +
                                        "" + "Call Logs Synced");
                            }

                        } else {
                            ArrayList<CallLogType> callLogTypeArrayList = divideCallLogByChunck();
                            if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0)
                                insertServiceCall(callLogTypeArrayList);
                            else {
                                Utils.showSuccessSnackBar(getActivity(), linearCallLogMain, "All " +
                                        "" + "Call Logs Synced");
                                Utils.setBooleanPreference(getActivity(), AppConstants
                                        .PREF_CALL_LOG_SYNCED, true);
                            }
                        }
                    } else {
                        if (callLogInsertionResponse != null) {
                            Log.e("error response", callLogInsertionResponse.getMessage());
                        } else {
                            Log.e("onDeliveryResponse: ", "userProfileResponse null");
                            Utils.showErrorSnackBar(getActivity(), linearMainContent, getString(R
                                    .string.msg_try_later));
                        }
                    }

                } else {
                    Utils.showErrorSnackBar(getActivity(), linearMainContent, "" + error
                            .getLocalizedMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterLocalbroadcast();
        AppConstants.setIsFirstTime(true);
        logsDisplayed = 0;
        spinnerCount = 0;
        listOfIds.clear();
        simpleCallLogListAdapter = null;
        adapterSetCount = 0;
        allCalldisplayDataCount = 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void getRecentCallLog() {

        PhoneBookCallLogs phoneBookCallLogs = new PhoneBookCallLogs(getActivity());
        Cursor cursor = phoneBookCallLogs.getAllCallLogId();
        if (cursor != null) {

            listOfIds.clear();
            while (cursor.moveToNext()) {
                listOfIds.add(cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID)));
            }

            Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOGS_ID_SET, listOfIds);

            cursor.moveToFirst();

            CallLogType callLogType = new CallLogType(getActivity());

            String rowId = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            Long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

            final ArrayList<CallLogType> arrayListHistroy = callLogHistory(number);

            callLogType.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));

            if (!TextUtils.isEmpty(number))
                callLogType.setNumber(number);
            else
                callLogType.setNumber("");

            if (!TextUtils.isEmpty(name))
                callLogType.setName(name);
            else
                callLogType.setName("");

            String photoThumbNail = getPhotoUrlFromNumber(number);

            if (!TextUtils.isEmpty(photoThumbNail)) {
                callLogType.setProfileImage(photoThumbNail);
            } else {
                callLogType.setProfileImage("");
            }

            callLogType.setUniqueContactId(rowId);
            String uniquePhoneBookId = getStarredStatusFromNumber(number);
            if (!TextUtils.isEmpty(uniquePhoneBookId))
                callLogType.setLocalPbRowId(uniquePhoneBookId);
            else
                callLogType.setLocalPbRowId(" ");

            callLogType.setDate(date);

            if (arrayListHistroy != null && arrayListHistroy.size() > 0) {
                CallLogType callLogType1 = arrayListHistroy.get(0);
                String historyId = callLogType1.getHistoryId().toString();
                if (historyId.equalsIgnoreCase(rowId)) {
                    callLogType.setHistoryDuration(callLogType1.getHistoryDuration());
                    callLogType.setHistoryCallSimNumber(callLogType1.getHistoryCallSimNumber());
                    callLogType.setHistoryId(callLogType1.getHistoryId());
                    callLogType.setCallDateAndTime(callLogType1.getCallDateAndTime());
                    callLogType.setTypeOfCall(callLogType1.getTypeOfCall());
                    callLogType.setDurationToPass(callLogType1.getDurationToPass());
                    if (!StringUtils.isEmpty(callLogType1.getHistoryCallSimNumber()))
                        callLogType.setHistoryCallSimNumber(callLogType1.getHistoryCallSimNumber());
                    else
                        callLogType.setHistoryCallSimNumber(" ");

                    callLogType.setNumberType(callLogType1.getNumberType());
                }
                callLogType.setArrayListCallHistory(arrayListHistroy);
            }

            if (number != null) {

                if (!StringUtils.isEmpty(name)) {
                    TableProfileMobileMapping tableProfileMobileMapping = new
                            TableProfileMobileMapping(getDatabaseHandler());
                    ProfileMobileMapping profileMobileMapping = tableProfileMobileMapping.getCloudPmIdFromProfileMappingFromNumber(number);

                    if (profileMobileMapping != null) {
                        String cloudPmId = profileMobileMapping.getMpmCloudPmId();
                        // To do
                        // Pass this cloudId to fetch FirstName and Last Name from
                        // ProfileMasterTable
                        TableProfileMaster tableProfileMaster = new
                                TableProfileMaster(getDatabaseHandler());
                        UserProfile userProfile = tableProfileMaster
                                .getProfileFromCloudPmId(Integer.parseInt(cloudPmId));
                        String firstName = userProfile.getPmFirstName();
                        String lastName = userProfile.getPmLastName();
                        String rcpId = userProfile.getPmRcpId();
                        String imagePath = userProfile.getPmProfileImage();

                        if (!StringUtils.isEmpty(firstName))
                            callLogType.setRcpFirstName(firstName);
                        if (!StringUtils.isEmpty(lastName))
                            callLogType.setRcpLastName(lastName);
                        if (!StringUtils.isEmpty(rcpId))
                            callLogType.setRcpId(rcpId);
                        if (!StringUtils.isEmpty(imagePath))
                            callLogType.setProfileImage(imagePath);

                        callLogType.setRcpUser(true);

                        arrayListCallLogs.add(0, callLogType);
                        rContactApplication.setArrayListCallLogType(arrayListCallLogs);
                        tempList.add(0, callLogType);
                    } else {
                        arrayListCallLogs.add(0, callLogType);
                        rContactApplication.setArrayListCallLogType(arrayListCallLogs);
                        tempList.add(0, callLogType);
                    }
                } else {
                    arrayListCallLogs.add(0, callLogType);
                    rContactApplication.setArrayListCallLogType(arrayListCallLogs);
                    tempList.add(0, callLogType);
                }

                if (simpleCallLogListAdapter != null)
                    simpleCallLogListAdapter.notifyItemInserted(0);
                else {
                    setSimpleListAdapter();
                }
                recyclerCallLogs.scrollToPosition(0);
                ArrayList<CallLogType> callLogTypeArrayList = new ArrayList<>();
                callLogTypeArrayList.add(callLogType);
                if (Utils.getBooleanPreference(getActivity(), AppConstants
                                .PREF_CONTACT_SYNCED,
                        false) &&
                        Utils.getBooleanPreference(getActivity(), AppConstants
                                        .PREF_CALL_LOG_SYNCED,
                                false)) {
                    if (!TextUtils.isEmpty(callLogType.getNumber()))
                        insertServiceCall(callLogTypeArrayList);
                }
            }
        }
    }

    private void makeSimpleData(String callType, ArrayList<CallLogType> callLogs) {
        List<CallLogType> filteredList = new ArrayList<>();
        ArrayList<CallLogType> savedContactList = new ArrayList<>();
        try {
            if (callLogs != null && callLogs.size() > 0) {
                textNoCallsFound.setVisibility(View.GONE);
                if (callType.equalsIgnoreCase(MISSED_CALLS)) {
                    for (int i = 0; i < callLogs.size(); i++) {
                        tempList = new ArrayList<>();
                        CallLogType callLogType = callLogs.get(i);
                        int callfilter = MoreObjects.firstNonNull(callLogType.getType(), 0);
                        if (callfilter == AppConstants.MISSED) {
                            filteredList.add(callLogType);
                        }
                    }
                } else if (callType.equalsIgnoreCase(INCOMING_CALLS)) {
                    for (int i = 0; i < callLogs.size(); i++) {
                        tempList = new ArrayList<>();
                        CallLogType callLogType = callLogs.get(i);
                        int callfilter = MoreObjects.firstNonNull(callLogType.getType(), 0);
                        if (callfilter == AppConstants.INCOMING) {
                            filteredList.add(callLogType);
                        }
                    }
                } else if (callType.equalsIgnoreCase(OUTGOING_CALLS)) {
                    for (int i = 0; i < callLogs.size(); i++) {
                        tempList = new ArrayList<>();
                        CallLogType callLogType = callLogs.get(i);
                        int callfilter = MoreObjects.firstNonNull(callLogType.getType(), 0);
                        if (callfilter == AppConstants.OUTGOING) {
                            filteredList.add(callLogType);
                        }
                    }
                } else {
                    if (spinnerCount > 0) {
                        if (selectedCallType.equalsIgnoreCase(ALL_CALLS)) {
                            allCalldisplayDataCount = allCalldisplayDataCount + 1;
                            if (isSpinnerAllCall) {
                                isSpinnerAllCall = false;
                                allCalldisplayDataCount = 0;
                                tempList.clear();
                            }
                        }
                    }
                    filteredList.addAll(callLogs);
                }

                if (filteredList != null && filteredList.size() > 0) {
                    for (int i = 0; i < filteredList.size(); i++) {
                        CallLogType callLogType = filteredList.get(i);
                        String name = callLogType.getName();
                        if (!StringUtils.isEmpty(name)) {
                            callLogType.setRecordPosition(i);
                            savedContactList.add(callLogType);
                        }
                    }
                }

                if (savedContactList != null && savedContactList.size() > 0) {
                    for (int i = 0; i < savedContactList.size(); i++) {
                        CallLogType callLogType = savedContactList.get(i);
                        String number = callLogType.getNumber();
                        if (!StringUtils.isEmpty(number)) {
                            TableProfileMobileMapping tableProfileMobileMapping = new
                                    TableProfileMobileMapping(getDatabaseHandler());
                            ProfileMobileMapping profileMobileMapping = tableProfileMobileMapping.
                                    getCloudPmIdFromProfileMappingFromNumber(number);
                            if (profileMobileMapping != null) {
                                String cloudPmId = profileMobileMapping.getMpmCloudPmId();
                                // To do
                                // Pass this cloudId to fetch FirstName and Last Name from
                                // ProfileMasterTable
                                TableProfileMaster tableProfileMaster = new TableProfileMaster
                                        (getDatabaseHandler());
                                UserProfile userProfile = tableProfileMaster
                                        .getProfileFromCloudPmId(Integer.parseInt(cloudPmId));
                                String firstName = userProfile.getPmFirstName();
                                String lastName = userProfile.getPmLastName();
                                String rcpId = userProfile.getPmRcpId();
                                String imagePath = userProfile.getPmProfileImage();
                                if (!StringUtils.isEmpty(firstName))
                                    callLogType.setRcpFirstName(firstName);
                                if (!StringUtils.isEmpty(lastName))
                                    callLogType.setRcpLastName(lastName);
                                if (!StringUtils.isEmpty(rcpId))
                                    callLogType.setRcpId(rcpId);
                                if (!StringUtils.isEmpty(imagePath))
                                    callLogType.setProfileImage(imagePath);

                                callLogType.setRcpUser(true);
                                int positionToReplace = callLogType.getRecordPosition();

                                filteredList.set(positionToReplace, callLogType);
                            }
                        }
                    }
                }

                tempList.addAll(filteredList);
            }

            if (filteredList.size() > 0) {
                textNoCallsFound.setVisibility(View.GONE);
                recyclerCallLogs.setVisibility(View.VISIBLE);

                if (simpleCallLogListAdapter == null) {
                    setSimpleListAdapter();

                } else {
                    if (spinnerCount > 0 && selectedCallType.equalsIgnoreCase(ALL_CALLS) && allCalldisplayDataCount == 0) {
                        setSimpleListAdapter();
                    } else {
                        simpleCallLogListAdapter.notifyDataSetChanged();
                    }
                }

                if (spinnerCount > 0 && !selectedCallType.equalsIgnoreCase(ALL_CALLS)) {
                    adapterSetCount = 0;
                    setSimpleListAdapter();
                }

            } else {
                textNoCallsFound.setVisibility(View.VISIBLE);
                recyclerCallLogs.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSimpleListAdapter() {
        if (tempList != null && tempList.size() > 0) {
            simpleCallLogListAdapter = new SimpleCallLogListAdapter(getActivity(), tempList);
            recyclerCallLogs.setAdapter(simpleCallLogListAdapter);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Public Private Methods">

    private void registerLocalbroadcast() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance
                (getActivity());
        IntentFilter intentFilter = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST);
        localBroadcastManager.registerReceiver(localBroadcastReceiver, intentFilter);

        LocalBroadcastManager localBroadcastManagerTabChange = LocalBroadcastManager.getInstance
                (getActivity());
        IntentFilter intentFilter1 = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_TABCHANGE);
        localBroadcastManagerTabChange.registerReceiver(localBroadcastReceiverTabChange,
                intentFilter1);

        LocalBroadcastManager localBroadcastManagerDeleteLogs = LocalBroadcastManager.getInstance
                (getActivity());
        IntentFilter intentFilter2 = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_DELETE_LOGS);
        localBroadcastManagerDeleteLogs.registerReceiver(localBroadcastReceiverDeleteLogs,
                intentFilter2);

        LocalBroadcastManager localBroadcastManagerRemoveFromCallLogs = LocalBroadcastManager
                .getInstance(getActivity());
        IntentFilter intentFilter3 = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_REMOVE_CALL_LOGS);
        localBroadcastManagerRemoveFromCallLogs.registerReceiver
                (localBroadcastReceiverRemoveFromCallLogs, intentFilter3);

        LocalBroadcastManager localBroadcastManagerBlock = LocalBroadcastManager.getInstance
                (getActivity());
        IntentFilter intentFilter4 = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_PROFILE_BLOCK);
        localBroadcastManagerBlock.registerReceiver(localBroadcastReceiverBlock, intentFilter4);

        LocalBroadcastManager localBroadcastManagerReceiveRecentCalls = LocalBroadcastManager
                .getInstance
                        (getActivity());
        IntentFilter intentFilter5 = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_CALLS);
        localBroadcastManagerReceiveRecentCalls.registerReceiver
                (localBroadcastReceiverRecentCalls, intentFilter5);


    }

    private void unregisterLocalbroadcast() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance
                (getActivity());
        localBroadcastManager.unregisterReceiver(localBroadcastReceiver);

        LocalBroadcastManager localBroadcastManagerDeleteLogs = LocalBroadcastManager.getInstance
                (getActivity());
        localBroadcastManagerDeleteLogs.unregisterReceiver(localBroadcastReceiverDeleteLogs);

        LocalBroadcastManager localBroadcastManagerRemoveLogs = LocalBroadcastManager.getInstance
                (getActivity());
        localBroadcastManagerRemoveLogs.unregisterReceiver
                (localBroadcastReceiverRemoveFromCallLogs);

        LocalBroadcastManager localBroadcastManagerProfileBlock = LocalBroadcastManager
                .getInstance(getActivity());
        localBroadcastManagerProfileBlock.unregisterReceiver(localBroadcastReceiverBlock);

        localBroadcastManager.unregisterReceiver(localBroadcastReceiverRecentCalls);

    }

    private void init() {
        textNoCallsFound = (TextView) mainView.findViewById(R.id.text_no_logs_found);
        arrayListCallLogs = new ArrayList<>();
        tempList = new ArrayList<>();
        arrayListCallLogHeader = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerCallLogs.setLayoutManager(mLinearLayoutManager);
        firstVisibleInListview = mLinearLayoutManager.findFirstVisibleItemPosition();
        listOfIds = new ArrayList<>();

        rContactApplication = (RContactApplication)
                getActivity().getApplicationContext();
        makeBlockedNumberList();
        // Checking for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionToExecute(requiredPermissions, AppConstants.READ_LOGS);
        } else {
            initSpinner();
        }
        initSwipe();

        recyclerCallLogs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int currentFirstVisible = mLinearLayoutManager.findFirstVisibleItemPosition();

                if (currentFirstVisible > firstVisibleInListview)
                    Log.i("RecyclerView scrolled: ", "scroll up!");
                else {
                    Log.i("RecyclerView scrolled: ", "scroll down!");
                    if (selectedCallType.equalsIgnoreCase(ALL_CALLS)) {
                        if (!isLastRecord && newState == 0) {
                            if (isFirstTime) {
                                ArrayList<String> callLogIdsArrayList = divideCallLogIdsByChunck();
                                if (callLogIdsArrayList != null && callLogIdsArrayList.size() > 0) {
                                    logsDisplayed = logsDisplayed + callLogIdsArrayList.size();
                                    loadLogs(selectedCallType);
                                } else {
                                    Utils.showSuccessSnackBar(getActivity(), linearCallLogMain, "Last" +
                                            " log shown.");
                                    isLastRecord = true;
                                }
                            } else {
                                if (!isLastRecord) {
                                    ArrayList<String> callLogIdsArrayList = divideCallLogIdsByChunck();
                                    if (callLogIdsArrayList != null && callLogIdsArrayList.size() > 0) {
                                        logsDisplayed = logsDisplayed + callLogIdsArrayList.size();
                                        loadLogs(selectedCallType);
                                    } else {
                                        Utils.showSuccessSnackBar(getActivity(), linearCallLogMain,
                                                "Last log shown.");
                                        isLastRecord = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //check for scroll down
                if (dy > 0) {
                    if (selectedCallType.equalsIgnoreCase(ALL_CALLS)) {
                        int visibleItemCount = mLinearLayoutManager.getChildCount();
                        int totalItemCount = mLinearLayoutManager.getItemCount();
                        int pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            //Do pagination.. i.e. fetch new data
                            if (isFirstTime) {
                                ArrayList<String> callLogIdsArrayList = divideCallLogIdsByChunck();
                                if (callLogIdsArrayList != null && callLogIdsArrayList.size() > 0) {
                                    logsDisplayed = logsDisplayed + callLogIdsArrayList.size();
                                    loadLogs(selectedCallType);
                                } else {
                                    Utils.showSuccessSnackBar(getActivity(), linearCallLogMain, "Last" +
                                            " log shown.");
                                    isLastRecord = true;
                                }
                            } else {
                                if (!isLastRecord) {
                                    ArrayList<String> callLogIdsArrayList = divideCallLogIdsByChunck();
                                    if (callLogIdsArrayList != null && callLogIdsArrayList.size() > 0) {
                                        logsDisplayed = logsDisplayed + callLogIdsArrayList.size();
                                        loadLogs(selectedCallType);
                                    } else {
                                        Utils.showSuccessSnackBar(getActivity(), linearCallLogMain,
                                                "Last log shown.");
                                        isLastRecord = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void makeBlockedNumberList() {
        if (Utils.getHashMapPreferenceForBlock(getActivity(), AppConstants
                .PREF_BLOCK_CONTACT_LIST) != null) {
            HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                    Utils.getHashMapPreferenceForBlock(getActivity(), AppConstants
                            .PREF_BLOCK_CONTACT_LIST);
            ArrayList<CallLogType> callLogTypeList = new ArrayList<CallLogType>();
            ArrayList<String> listOfBlockedNumber = new ArrayList<>();
            String blockedNumber = "";
            String hashKey = "";
            if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                for (String key : blockProfileHashMapList.keySet()) {
                    System.out.println(key);
                    hashKey = key;
                    if (blockProfileHashMapList.containsKey(hashKey)) {
                        callLogTypeList.addAll(blockProfileHashMapList.get(hashKey));
                    }
                }

                if (callLogTypeList != null) {
                    for (int j = 0; j < callLogTypeList.size(); j++) {
                        String tempNumber = callLogTypeList.get(j).getNumber();
                        if (!TextUtils.isEmpty(tempNumber)) {
                            listOfBlockedNumber.add(tempNumber);
                        }
                    }
                    Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOG_LIST,
                            listOfBlockedNumber);
                }
            } else {
                Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOG_LIST, new
                        ArrayList());
            }

        }
    }

    private void initSpinner() {
        final List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(ALL_CALLS);
        spinnerArray.add(INCOMING_CALLS);
        spinnerArray.add(OUTGOING_CALLS);
        spinnerArray.add(MISSED_CALLS);

        if (!isIdsFetchedFirstTime) {
            isIdsFetchedFirstTime = true;
            PhoneBookCallLogs phoneBookCallLogs = new PhoneBookCallLogs(getActivity());
            Cursor cursor = phoneBookCallLogs.getAllCallLogId();
            if (cursor != null) {
                int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                while (cursor.moveToNext()) {
                    listOfIds.add(cursor.getString(rowId));
                }
            }
            cursor.close();
            Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOGS_ID_SET,
                    listOfIds);
            isFirstChuck = false;
            count = 0;
        }

        if (listOfIds != null && listOfIds.size() == 0) {
            if (listOfIds != null && listOfIds.size() == 0) {
                PhoneBookCallLogs phoneBookCallLogs = new PhoneBookCallLogs(getActivity());
                Cursor cursor = phoneBookCallLogs.getAllCallLogId();
                if (cursor != null) {
                    int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                    while (cursor.moveToNext()) {
                        listOfIds.add(cursor.getString(rowId));
                    }
                    cursor.close();
                }
                Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOGS_ID_SET,
                        listOfIds);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCallFilter.setAdapter(adapter);

        spinnerCallFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String value = parent.getSelectedItem().toString();
                if (!TextUtils.isEmpty(value)) {
                    selectedCallType = value;

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isFirstTime) {
                                if (value.equalsIgnoreCase(ALL_CALLS) && spinnerCount > 0) {
                                    isSpinnerAllCall = true;
                                }
                                if (AppConstants.isFirstTime()) {
                                    AppConstants.setIsFirstTime(false);
                                    loadLogs(selectedCallType);
                                } else {
                                    if (!value.equalsIgnoreCase(ALL_CALLS)) {
                                        spinnerCount = spinnerCount + 1;
                                    }

                                    arrayListCallLogs = rContactApplication.getArrayListCallLogType();
                                    makeSimpleData(selectedCallType, arrayListCallLogs);
                                    initSwipe();
                                }
                            } else {
                                if (!isLastRecord) {
                                    if (!value.equalsIgnoreCase(ALL_CALLS)) {
                                        spinnerCount = spinnerCount + 1;
                                        arrayListCallLogs = rContactApplication
                                                .getArrayListCallLogType();
                                        makeSimpleData(selectedCallType, arrayListCallLogs);
                                        initSwipe();
                                    } else {
                                        if (value.equalsIgnoreCase(ALL_CALLS) && spinnerCount > 0) {
                                            isSpinnerAllCall = true;
                                            if (listOfIds != null) {
                                                listOfIds.clear();
                                                arrayListCallLogs.clear();
                                                rContactApplication.setArrayListCallLogType(arrayListCallLogs);
                                                logsDisplayed = 0;
                                                Utils.setArrayListPreference(getActivity(), AppConstants
                                                                .PREF_CALL_LOGS_ID_SET,
                                                        listOfIds);
                                                PhoneBookCallLogs phoneBookCallLogs = new
                                                        PhoneBookCallLogs(getActivity());
                                                Cursor cursor = phoneBookCallLogs.getAllCallLogId();
                                                if (cursor != null) {
                                                    int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                                                    while (cursor.moveToNext()) {
                                                        listOfIds.add(cursor.getString(rowId));
                                                    }
                                                    cursor.close();
                                                }
                                                Utils.setArrayListPreference(getActivity(), AppConstants
                                                                .PREF_CALL_LOGS_ID_SET,
                                                        listOfIds);
                                            }
                                            loadLogs(selectedCallType);
                                        } else {
                                            if (listOfIds != null) {
                                                listOfIds.clear();
                                                Utils.setArrayListPreference(getActivity(), AppConstants
                                                                .PREF_CALL_LOGS_ID_SET,
                                                        listOfIds);
                                                PhoneBookCallLogs phoneBookCallLogs = new
                                                        PhoneBookCallLogs(getActivity());
                                                Cursor cursor = phoneBookCallLogs.getAllCallLogId();
                                                if (cursor != null) {
                                                    int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                                                    while (cursor.moveToNext()) {
                                                        listOfIds.add(cursor.getString(rowId));
                                                    }
                                                    cursor.close();
                                                }
                                                Utils.setArrayListPreference(getActivity(), AppConstants
                                                                .PREF_CALL_LOGS_ID_SET,
                                                        listOfIds);
                                            }
                                            loadLogs(selectedCallType);
                                        }
                                    }
                                } else {
                                    arrayListCallLogs = rContactApplication
                                            .getArrayListCallLogType();
                                    if (arrayListCallLogs != null && arrayListCallLogs.size() ==
                                            1) {
                                        if (!value.equalsIgnoreCase(ALL_CALLS)) {
                                            spinnerCount = spinnerCount + 1;
                                        } else {
                                            arrayListCallLogs.clear();
                                            tempList.clear();
                                            PhoneBookCallLogs phoneBookCallLogs = new
                                                    PhoneBookCallLogs(getActivity());
                                            listOfIds.clear();
                                            Utils.setArrayListPreference(getActivity(), AppConstants
                                                            .PREF_CALL_LOGS_ID_SET,
                                                    listOfIds);
                                            Cursor cursor = phoneBookCallLogs.getAllCallLogId();
                                            if (cursor != null) {
                                                int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                                                while (cursor.moveToNext()) {
                                                    listOfIds.add(cursor.getString(rowId));
                                                }
                                                cursor.close();
                                            }
                                            Utils.setArrayListPreference(getActivity(), AppConstants
                                                            .PREF_CALL_LOGS_ID_SET,
                                                    listOfIds);
                                            loadLogs(selectedCallType);
                                        }

                                    } else {
                                        if (arrayListCallLogs != null && arrayListCallLogs.size() > 0) {
                                            if (!value.equalsIgnoreCase(ALL_CALLS)) {
                                                spinnerCount = spinnerCount + 1;
                                            } else {
                                                if (value.equalsIgnoreCase(ALL_CALLS) && spinnerCount > 0) {
                                                    isSpinnerAllCall = true;

                                                }
                                            }
                                            makeSimpleData(selectedCallType, arrayListCallLogs);
                                            initSwipe();
                                        }
                                    }
                                }
                            }
                        }
                    }, 200);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private ArrayList<String> divideCallLogIdsByChunck() {
        callLogIdsListByChunck = new ArrayList<>();
        if (listOfIds != null && listOfIds.size() > 0) {
            int size = listOfIds.size();
            if (isFirstChuck) {
                for (ArrayList<String> partition : chopped(listOfIds, LIST_PARTITION_COUNT)) {
                    // do something with partition
                    Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
                    listOfIds.removeAll(partition);
                    break;
                }
            }

            for (ArrayList<String> partition : chopped(listOfIds, LIST_PARTITION_COUNT)) {
                // do something with partition
                Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
                callLogIdsListByChunck.addAll(partition);
                listOfIds.removeAll(partition);
                break;
            }
        }
        return callLogIdsListByChunck;
    }

    private ArrayList<CallLogType> divideCallLogByChunck() {
        int size = tempList.size();
        callLogsListbyChunck = new ArrayList<>();
        for (ArrayList<CallLogType> partition : choppedCallLog(tempList, LIST_PARTITION_COUNT)) {
            // do something with partition
            callLogsListbyChunck.addAll(partition);
            tempList.removeAll(partition);
            break;
        }
        return callLogsListbyChunck;
    }

    private ArrayList<CallLogType> divideCallLogByChunck(ArrayList<CallLogType> list) {
        int size = 0;
        callLogsListbyChunck = new ArrayList<>();
        if (list != null && list.size() > 0) {
            size = list.size();
            if (size > LIST_PARTITION_COUNT) {
                for (ArrayList<CallLogType> partition : choppedCallLog(list,
                        LIST_PARTITION_COUNT)) {
                    // do something with partition
                    callLogsListbyChunck.addAll(partition);
                    newList.removeAll(partition);
                    break;
                }
            } else {
                callLogsListbyChunck.addAll(list);
                newList.removeAll(list);

            }
        }

        return callLogsListbyChunck;
    }

    // This is to be run only when READ_CONTACTS and READ_CALL_LOG permission are granted
    @SuppressLint("SimpleDateFormat")
    private void loadLogs(String callType) {

        List<CallLogType> callLogs = new ArrayList<>();
        arrayListCallLogHeader = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();
        arrayListCallLogsHistory = new ArrayList<>();
        if (getActivity() == null) {
            return;
        }

        ArrayList<String> listOfIds = Utils.getArrayListPreference(getActivity(), AppConstants
                .PREF_CALL_LOGS_ID_SET);
        if (listOfIds == null || listOfIds.size() == 1) {
            PhoneBookCallLogs phoneBookCallLogs = new PhoneBookCallLogs(getActivity());
            listOfIds = new ArrayList<>();
            Cursor cursor = phoneBookCallLogs.getAllCallLogId();
            if (cursor != null) {
                int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                while (cursor.moveToNext()) {
                    listOfIds.add(cursor.getString(rowId));
                }
            }
            cursor.close();
            Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOGS_ID_SET,
                    listOfIds);
        }

        if (listOfIds != null && listOfIds.size() > 0) {
            textNoCallsFound.setVisibility(View.GONE);
            int indexToBeginSync = logsDisplayed;
            ArrayList<String> tempIdsList = new ArrayList<>();
            for (int i = indexToBeginSync; i < listOfIds.size(); i++) {
                String ids = listOfIds.get(i);
                tempIdsList.add(ids);
            }

            if (!noOfIdsToFetch) {
                noOfIdsToFetch = true;
                LIST_PARTITION_COUNT = 10;
            } else {
                LIST_PARTITION_COUNT = 10;
            }

            if (tempIdsList.size() > LIST_PARTITION_COUNT) {
                for (ArrayList<String> partition : chopped(tempIdsList, LIST_PARTITION_COUNT)) {
                    // do something with partition
                    if (count == 0) {
                        isFirstChuck = true;
                        count = count + 1;
                    } else {
                        isFirstChuck = false;
                    }
                    fetchCallLogsFromIds(partition);
                    break;
                }
            } else {
                if (tempIdsList.size() <= 0) {
                    if (logsDisplayed == listOfIds.size()) {
                        // Same records will be appended in the list
                    } else {
                        fetchCallLogsFromIds(listOfIds);
                    }
                } else {
                    fetchCallLogsFromIds(tempIdsList);
                }
            }

        } else {
            textNoCallsFound.setVisibility(View.VISIBLE);
        }
    }

    private void insertServiceCall(ArrayList<CallLogType> callLogTypeArrayList) {

        if (Utils.isNetworkAvailable(getActivity())) {
            WsRequestObject deviceDetailObject = new WsRequestObject();
            deviceDetailObject.setFlag(IntegerConstants.SYNC_INSERT_CALL_LOG);
            deviceDetailObject.setArrayListCallLogType(callLogTypeArrayList);
            if (Utils.isNetworkAvailable(getActivity())) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        deviceDetailObject, null, WsResponseObject.class, WsConstants
                        .REQ_UPLOAD_CALL_LOGS, null, true).execute
                        (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CALL_LOGS);
            } else {
                Utils.showErrorSnackBar(getActivity(), linearCallLogMain, getResources()
                        .getString(R.string.msg_no_network));
            }
        }

    }

    private ArrayList<ArrayList<String>> chopped(ArrayList<String> list, final int L) {
        ArrayList<ArrayList<String>> parts = new ArrayList<ArrayList<String>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<String>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    private ArrayList<ArrayList<CallLogType>> choppedCallLog(ArrayList<CallLogType> list, final
    int L) {
        ArrayList<ArrayList<CallLogType>> parts = new ArrayList<ArrayList<CallLogType>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<CallLogType>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
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

    private void fetchCallLogsFromIds(ArrayList<String> listOfRowIds) {
        try {
            ArrayList<CallLogType> callLogTypeArrayList = new ArrayList<>();
            for (int i = 0; i < listOfRowIds.size(); i++) {
                String uniqueCallLogId = listOfRowIds.get(i);
                if (!TextUtils.isEmpty(uniqueCallLogId)) {
                    String order = CallLog.Calls.DATE + " DESC";
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls
                                    .CONTENT_URI,
                            null, CallLog.Calls._ID + " = " + uniqueCallLogId, null, order);

                    if (cursor != null) {
                        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                        int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                        int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);

                        while (cursor.moveToNext()) {
                            CallLogType log = new CallLogType(getActivity());
                            String numberToSave = cursor.getString(number);
                            log.setNumber(numberToSave);
                            String userName = cursor.getString(name);
                            if (!TextUtils.isEmpty(userName))
                                log.setName(userName);
                            else
                                log.setName("");

                            log.setType(cursor.getInt(type));
                            log.setDuration(cursor.getInt(duration));
                            log.setDate(cursor.getLong(date));
                            log.setUniqueContactId(cursor.getString(rowId));
                            String userNumber = cursor.getString(number);
                            String uniquePhoneBookId = getStarredStatusFromNumber(userNumber);
                            if (!TextUtils.isEmpty(uniquePhoneBookId)) {
                                log.setLocalPbRowId(uniquePhoneBookId);
                            } else
                                log.setLocalPbRowId(" ");

                            String photoThumbNail = getPhotoUrlFromNumber(userNumber);
                            if (!TextUtils.isEmpty(photoThumbNail)) {
                                log.setProfileImage(photoThumbNail);
                            } else {
                                log.setProfileImage("");
                            }
                            callLogTypeArrayList.add(log);
                            arrayListCallLogs.add(log);
                            rContactApplication.setArrayListCallLogType(arrayListCallLogs);
                        }
                        cursor.close();
                    }
                }
            }
            makeSimpleData(selectedCallType, callLogTypeArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPhotoUrlFromNumber(String phoneNumber) {
        String photoThumbUrl = "";
        try {

            photoThumbUrl = "";
            ContentResolver contentResolver = getActivity().getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    photoThumbUrl = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.PHOTO_THUMBNAIL_URI));
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoThumbUrl;
    }

    private String getStarredStatusFromNumber(String phoneNumber) {
        String numberId, rawId = "";
        try {

            ContentResolver contentResolver = getActivity().getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
                    Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] projection1 = new String[]{
                            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
                    };

                    String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
                    String[] selectionArgs = new String[]{numberId};

                    Cursor cursor1 = contentResolver.query(uri1, projection1, selection,
                            selectionArgs, null);
                    if (cursor1 != null) {
                        while (cursor1.moveToNext()) {
                            rawId = cursor1.getString(cursor1.getColumnIndexOrThrow
                                    (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                        }
                        cursor1.close();
                    }
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rawId;
    }

    public String getLogType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return getActivity().getString(R.string.call_log_incoming);
            case CallLog.Calls.OUTGOING_TYPE:
                return getActivity().getString(R.string.call_log_outgoing);
            case CallLog.Calls.MISSED_TYPE:
                return getActivity().getString(R.string.call_log_missed);
            case CallLog.Calls.REJECTED_TYPE:
                return getActivity().getString(R.string.call_log_rejected);
            case CallLog.Calls.BLOCKED_TYPE:
                return getActivity().getString(R.string.call_log_blocked);
            case CallLog.Calls.VOICEMAIL_TYPE:
                return getActivity().getString(R.string.call_log_voice_mail);

        }
        return getActivity().getString(R.string.type_other_caps);
    }

    public String getPhoneNumberType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return getActivity().getString(R.string.type_home);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return getActivity().getString(R.string.type_mobile);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return getActivity().getString(R.string.type_work);

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return getActivity().getString(R.string.type_fax_work);

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                return getActivity().getString(R.string.type_fax_home);

            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return getActivity().getString(R.string.type_pager);

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return getActivity().getString(R.string.type_other);

            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return getActivity().getString(R.string.type_callback);

            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return getActivity().getString(R.string.type_car);

            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                return getActivity().getString(R.string.type_company_main);

            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                return getActivity().getString(R.string.type_isdn);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return getActivity().getString(R.string.type_home);

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                return getActivity().getString(R.string.type_other_fax);

            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                return getActivity().getString(R.string.type_radio);

            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                return getActivity().getString(R.string.type_telex);

            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                return getActivity().getString(R.string.type_tty_tdd);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return getActivity().getString(R.string.type_work_mobile);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                return getActivity().getString(R.string.type_work_pager);

            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return getActivity().getString(R.string.type_assistant);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                return getActivity().getString(R.string.type_mms);
        }
        return getActivity().getString(R.string.type_other);
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
                String actionNumber = StringUtils.defaultString(((SimpleCallLogListAdapter
                        .CallLogViewHolder) viewHolder).textTempNumber.getText()
                        .toString());
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + actionNumber));
                    startActivity(smsIntent);

                } else {
                    showCallConfirmationDialog(actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        simpleCallLogListAdapter.notifyDataSetChanged();
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
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
        itemTouchHelper.attachToRecyclerView(recyclerCallLogs);
    }


    private void showCallConfirmationDialog(String number) {
        final String formattedNumber = Utils.getFormattedNumber(getActivity(), number);
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
                        Utils.callIntent(getActivity(), formattedNumber);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(getActivity().getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(getActivity().getString(R.string.action_call));
        callConfirmationDialog.setDialogBody(getActivity().getString(R.string.action_call) + " "
                + formattedNumber + "?");
        callConfirmationDialog.showDialog();

    }

    boolean isDualSIM;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void telephonyInit() {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(getActivity());

        String imsiSIM1 = telephonyInfo.getImsiSIM1();
        String imsiSIM2 = telephonyInfo.getImsiSIM2();
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        isDualSIM = telephonyInfo.isDualSIM();
        AppConstants.setIsDualSimPhone(isDualSIM);
    }


    // A method to check if a permission is granted then execute tasks depending on that
    // particular permission
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String permissions[], int requestCode) {

        boolean logs = ContextCompat.checkSelfPermission(getActivity(), permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        if (logs) {
            requestPermissions(permissions, requestCode);
        } else {
            initSpinner();
            telephonyInit();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppConstants.READ_LOGS && permissions[0].equals(Manifest.permission
                .READ_CALL_LOG)) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                initSpinner();
                telephonyInit();
            } else {
                showPermissionConfirmationDialog();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByNumber(String number) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.NUMBER + " =?", new String[]{number}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByName(String name) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.CACHED_NAME + " =?", new String[]{name}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }


    private ArrayList callLogHistory(String number) {
        ArrayList<CallLogType> callDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            if (!TextUtils.isEmpty(number)) {
                Pattern numberPat = Pattern.compile("\\d+");
                Matcher matcher1 = numberPat.matcher(number);
                if (matcher1.find()) {
                    cursor = getCallHistoryDataByNumber(number);
                } else {
                    cursor = getCallHistoryDataByName(number);
                }
            }

            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int callLogId = cursor.getColumnIndex(CallLog.Calls._ID);
                int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);
                int account = -1;
                int account_id = -1;
                int profileImage = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    account = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME);
                    //for versions above lollipop
                    account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                    profileImage = cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);
                } else {
                    account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                }
                while (cursor.moveToNext()) {
                    String phNum = cursor.getString(number1);
                    int callType = Integer.parseInt(cursor.getString(type));
                    String callDate = cursor.getString(date);
                    long dateOfCall = Long.parseLong(callDate);
                    String callDuration = cursor.getString(duration);

                    int histroyId = Integer.parseInt(cursor.getString(callLogId));
                    CallLogType logObject = new CallLogType();
                    logObject.setHistoryNumber(phNum);
                    logObject.setHistoryType(callType);
                    logObject.setHistoryDate(dateOfCall);
                    logObject.setHistoryDuration(Integer.parseInt(callDuration));
                    if (account_id != -1)
                        logObject.setHistoryCallSimNumber(cursor.getString(account_id));
                    else
                        logObject.setHistoryCallSimNumber(" ");

                    logObject.setHistoryId(histroyId);
                    String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                    logObject.setNumberType(numberTypeLog);
                    Date date1 = new Date(dateOfCall);
                    String callDataAndTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format
                            (date1);
                    logObject.setCallDateAndTime(callDataAndTime);

                    String typeOfCall = getLogType(callType);
                    if (typeOfCall.equalsIgnoreCase(getActivity().getString(R.string
                            .call_log_rejected))) {
                        typeOfCall = getActivity().getString(R.string.call_log_missed);
                    }
                    logObject.setTypeOfCall(typeOfCall);

                    String durationtoPass = logObject.getCoolDuration(Float.parseFloat
                            (callDuration));
                    logObject.setDurationToPass(durationtoPass);

                    callDetails.add(logObject);
                }
                cursor.close();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return callDetails;
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
        permissionConfirmationDialog.setLeftButtonText(getActivity().getString(R.string
                .action_cancel));
        permissionConfirmationDialog.setRightButtonText(getActivity().getString(R.string
                .action_ok));
        permissionConfirmationDialog.setDialogBody(getActivity().getString(R.string
                .call_log_permission));

        permissionConfirmationDialog.showDialog();

    }

    //</editor-fold>

    //<editor-fold desc="Local Broadcast Receivers">
    boolean clearLogs;
    boolean clearLogsFromContacts;
    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.i("CallLogFragment", "onReceive() of LocalBroadcast");
            clearLogs = intent.getBooleanExtra(AppConstants.EXTRA_CLEAR_CALL_LOGS, false);
            clearLogsFromContacts = intent.getBooleanExtra(AppConstants
                    .EXTRA_CLEAR_CALL_LOGS_FROM_CONTACTS, false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clearLogs) {
                        if (simpleCallLogListAdapter != null) {
                            // when data was loaded everytime

                            // updated on 19/04/2017, when data are loading from arraylist
                            CallLogType callDataToUpdate = simpleCallLogListAdapter
                                    .getSelectedCallLogData();
                            String number = callDataToUpdate.getNumber();
                            for (int i = 0; i < arrayListCallLogs.size(); i++) {
                                CallLogType callLogType = arrayListCallLogs.get(i);
                                String numberToDelete = callLogType.getNumber();
                                if (numberToDelete.equalsIgnoreCase(number)) {
                                    arrayListCallLogs.remove(callLogType);
                                    rContactApplication.setArrayListCallLogType(arrayListCallLogs);
                                    tempList.remove(callLogType);
                                    String idToRemove = callLogType.getUniqueContactId();
                                    listOfIds.remove(idToRemove);
                                    Utils.setArrayListPreference(getActivity(), AppConstants
                                                    .PREF_CALL_LOGS_ID_SET,
                                            listOfIds);
                                    simpleCallLogListAdapter.notifyDataSetChanged();
                                }
                            }

                            clearLogs = false;
                        }
                    } else {
                        if (clearLogsFromContacts) {
                            if (simpleCallLogListAdapter != null) {
                                // when data was loaded everytime

                                // updated on 19/04/2017, when data are loading from arraylist
                                CallLogType callDataToUpdate = simpleCallLogListAdapter
                                        .getSelectedCallLogData();
                                String number = callDataToUpdate.getNumber();
                                for (int i = 0; i < arrayListCallLogs.size(); i++) {
                                    CallLogType callLogType = arrayListCallLogs.get(i);
                                    String numberToDelete = callLogType.getNumber();
                                    if (numberToDelete.equalsIgnoreCase(number)) {
                                        arrayListCallLogs.remove(callLogType);
                                        rContactApplication.setArrayListCallLogType
                                                (arrayListCallLogs);
                                        tempList.remove(callLogType);
                                        String idToRemove = callLogType.getUniqueContactId();
                                        listOfIds.remove(idToRemove);
                                        Utils.setArrayListPreference(getActivity(), AppConstants
                                                        .PREF_CALL_LOGS_ID_SET,
                                                listOfIds);
                                        simpleCallLogListAdapter.notifyDataSetChanged();
                                    }
                                }
                                clearLogsFromContacts = false;
                            }
                        }
                    }

                }
            }, 1000);
        }
    };

    boolean removeLogs;
    private BroadcastReceiver localBroadcastReceiverRemoveFromCallLogs = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            removeLogs = intent.getBooleanExtra(AppConstants.EXTRA_REMOVE_CALL_LOGS, false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (removeLogs) {
                        if (simpleCallLogListAdapter != null) {
                            int itemIndexToRemove = simpleCallLogListAdapter.getSelectedPosition();
                            // updated on 19/04/2017, when data are loading from arraylist
                            CallLogType callDataToUpdate = simpleCallLogListAdapter
                                    .getSelectedCallLogData();
                            arrayListCallLogs.remove(callDataToUpdate);
                            rContactApplication.setArrayListCallLogType(arrayListCallLogs);
                            tempList.remove(callDataToUpdate);
                            String idToRemove = callDataToUpdate.getUniqueContactId();
                            listOfIds.remove(idToRemove);
                            Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOGS_ID_SET, listOfIds);
                            simpleCallLogListAdapter.notifyItemRemoved(itemIndexToRemove);
                            removeLogs = false;
                        }
                    }
                }
            }, 1000);

        }
    };

    private BroadcastReceiver localBroadcastReceiverDeleteLogs = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean deleteAll = intent.getBooleanExtra(AppConstants.EXTRA_DELETE_ALL_CALL_LOGS, false);
            if (deleteAll) {
                if (simpleCallLogListAdapter != null) {
                    // when data was loaded everytime

                    // updated on 19/04/2017, when data are loading from arraylist
                    CallLogType callDataToUpdate = simpleCallLogListAdapter
                            .getSelectedCallLogData();
                    String number = callDataToUpdate.getNumber();
                    for (int i = 0; i < arrayListCallLogs.size(); i++) {
                        CallLogType callLogType = arrayListCallLogs.get(i);
                        String numberToDelete = callLogType.getNumber();
                        if (numberToDelete.equalsIgnoreCase(number)) {
                            arrayListCallLogs.remove(callLogType);
                            rContactApplication.setArrayListCallLogType(arrayListCallLogs);
                            tempList.remove(callLogType);
                            String idToRemove = callLogType.getUniqueContactId();
                            listOfIds.remove(idToRemove);
                            Utils.setArrayListPreference(getActivity(), AppConstants
                                            .PREF_CALL_LOGS_ID_SET,
                                    listOfIds);
                            simpleCallLogListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } else {
                //update history count
                LocalBroadcastManager localBroadcastManagerDeleteLogs = LocalBroadcastManager
                        .getInstance(getActivity());
                localBroadcastManagerDeleteLogs.unregisterReceiver
                        (localBroadcastReceiverDeleteLogs);
                arrayListCallLogs = new ArrayList<>();
                spinnerCount = 4;
                loadLogs(selectedCallType);
            }
        }
    };

    private BroadcastReceiver localBroadcastReceiverTabChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (AppConstants.EXTRA_CALL_LOG_SWITCH_TAB_VALUE) {
                AppConstants.isBackgroundProcessStopped = true;
            }
        }
    };

    private BroadcastReceiver localBroadcastReceiverBlock = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    private BroadcastReceiver localBroadcastReceiverRecentCalls = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isResumeCalled) {
                            isResumeCalled = false;
                            return;
                        } else {
                            if (AppConstants.isFromReceiver) {
                                AppConstants.isFromReceiver = false;
                                getRecentCallLog();

                            }
                        }
                    }
                }, 2500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    //</editor-fold>
}
