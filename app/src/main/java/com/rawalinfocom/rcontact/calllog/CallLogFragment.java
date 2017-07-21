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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.view.animation.Animation;
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
import com.rawalinfocom.rcontact.helper.WrapContentLinearLayoutManager;
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

    public String CALL_LOG_ALL_CALLS = "All";
    public String CALL_LOG_INCOMING_CALLS = "Incoming";
    public String CALL_LOG_OUTGOING_CALLS = "Outgoing";
    public String CALL_LOG_MISSED_CALLS = "Missed";

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
    private ArrayList<CallLogType> callLogTypeArrayList;

    private SimpleCallLogListAdapter simpleCallLogListAdapter;

    ArrayList<CallLogType> callLogsListbyChunck;
    ArrayList<CallLogType> newList;
    MaterialDialog callConfirmationDialog;
    String selectedCallType = "";
    View mainView;

    private String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG};
    private boolean isFirstTime;
    private int LIST_PARTITION_COUNT = 7;
    RContactApplication rContactApplication;
    MaterialDialog permissionConfirmationDialog;

    boolean isCallLogFragment = false;
    private boolean isResumeCalled = false;

    private PhoneBookCallLogs phoneBookCallLogs;
    private TableProfileMaster tableProfileMaster;
    private TableProfileMobileMapping tableProfileMobileMapping;
    private GetRCPNameAndProfileImage nameAndProfileImage;
    boolean isFromDeleteBroadcast = false;


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
        if (Utils.getBooleanPreference(getActivity(), AppConstants
                .PREF_CALL_LOG_STARTS_FIRST_TIME, true)) {
            isFirstTime = true;
            Utils.setBooleanPreference(getActivity(), AppConstants
                    .PREF_CALL_LOG_STARTS_FIRST_TIME, false);
        }

        if (!AppConstants.isFirstTime())
            AppConstants.setIsFirstTime(true);
        else
            AppConstants.setIsFirstTime(false);

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
        phoneBookCallLogs = new PhoneBookCallLogs(getActivity());
        tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
        tableProfileMobileMapping = new TableProfileMobileMapping(getDatabaseHandler());
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
    public void replaceFragment(int containerView, Fragment newFragment, String fragmentTag) {
        super.replaceFragment(containerView, newFragment, fragmentTag);
    }

    private void init() {

        textNoCallsFound = (TextView) mainView.findViewById(R.id.text_no_logs_found);

        recyclerCallLogs.setLayoutManager(new WrapContentLinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false));

        rContactApplication = (RContactApplication) getActivity().getApplicationContext();
//        makeBlockedNumberList();

        // Checking for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionToExecute(requiredPermissions, AppConstants.READ_LOGS);
        } else {
            initSpinner();
        }

        initSwipe();

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
        registerLocalBroadcast();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isFirstTime) {
            isFirstTime = false;
        }
        AppConstants.isFromReceiver = false;

        if (nameAndProfileImage != null) {
            nameAndProfileImage.cancel(true);
            getDatabaseHandler().close();
        }

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

                        Utils.setStringPreference(getActivity(), AppConstants.PREF_CALL_LOG_SYNC_TIME, callLogInsertionResponse.getCallDateAndTime());

                        ArrayList<CallLogType> callLogTypeArrayList = divideCallLogByChunck();
                        if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0) {
//                                insertServiceCall(callLogTypeArrayList);
                        } else {
                            System.out.println("RContact All Call Logs Synced");
//                                Utils.showSuccessSnackBar(getActivity(), linearCallLogMain, "All " +
//                                        "" + "Call Logs Synced");
                            Utils.setBooleanPreference(getActivity(), AppConstants.PREF_CALL_LOG_SYNCED, true);
                        }
//                        }
                    } else {
                        if (callLogInsertionResponse != null) {
                            Log.e("error response", callLogInsertionResponse.getMessage());
                            Utils.showErrorSnackBar(getActivity(), linearMainContent, callLogInsertionResponse.getMessage());
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
        unregisterLocalBroadcast();
//        AppConstants.setIsFirstTime(true);
        simpleCallLogListAdapter = null;
    }

    private void registerLocalBroadcast() {
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

    private void unregisterLocalBroadcast() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void getRecentCallLog() {

        Cursor cursor = phoneBookCallLogs.getAllCallLogId();
        if (cursor != null) {

            cursor.moveToFirst();

            CallLogType callLogType = new CallLogType(getActivity());

            String rowId = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
            String number = Utils.getFormattedNumber(getActivity(),
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

            if (StringUtils.isEmpty(name)) {
                name = getNameFromNumber(number);
            }

            Long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

            final ArrayList<CallLogType> arrayListHistory = callLogHistory(number);

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

            if (arrayListHistory != null && arrayListHistory.size() > 0) {
                CallLogType callLogType1 = arrayListHistory.get(0);
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
                callLogType.setArrayListCallHistory(arrayListHistory);
            }

            if (number != null) {

                if (!StringUtils.isEmpty(name)) {
                    ProfileMobileMapping profileMobileMapping =
                            tableProfileMobileMapping.getCloudPmIdFromProfileMappingFromNumber(number);

                    if (profileMobileMapping != null) {
                        String cloudPmId = profileMobileMapping.getMpmCloudPmId();
                        // To do
                        // Pass this cloudId to fetch FirstName and Last Name from
                        // ProfileMasterTable
                        if (!StringUtils.isEmpty(cloudPmId)) {
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

                            callLogTypeArrayList.add(0, callLogType);
                            rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                        }
                    } else {
                        callLogTypeArrayList.add(0, callLogType);
                        rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                    }
                } else {
                    callLogTypeArrayList.add(0, callLogType);
                    rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                }

                if (simpleCallLogListAdapter != null)
                    simpleCallLogListAdapter.notifyItemInserted(0);

                recyclerCallLogs.scrollToPosition(0);
                ArrayList<CallLogType> callLogTypeArrayList = new ArrayList<>();
                callLogTypeArrayList.add(callLogType);
                if (Utils.getBooleanPreference(getActivity(), AppConstants.PREF_CONTACT_SYNCED, false) &&
                        Utils.getBooleanPreference(getActivity(), AppConstants.PREF_CALL_LOG_SYNCED, false)) {
                    if (!TextUtils.isEmpty(callLogType.getNumber()))
                        insertServiceCall(callLogTypeArrayList);
                }
            }
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    //<editor-fold desc="Public Private Methods">

//    private void makeBlockedNumberList() {
//        if (Utils.getHashMapPreferenceForBlock(getActivity(), AppConstants
//                .PREF_BLOCK_CONTACT_LIST) != null) {
//            HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
//                    Utils.getHashMapPreferenceForBlock(getActivity(), AppConstants
//                            .PREF_BLOCK_CONTACT_LIST);
//            ArrayList<CallLogType> callLogTypeList = new ArrayList<CallLogType>();
//            ArrayList<String> listOfBlockedNumber = new ArrayList<>();
//            String blockedNumber = "";
//            String hashKey = "";
//            if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
//                for (String key : blockProfileHashMapList.keySet()) {
//                    System.out.println(key);
//                    hashKey = key;
//                    if (blockProfileHashMapList.containsKey(hashKey)) {
//                        callLogTypeList.addAll(blockProfileHashMapList.get(hashKey));
//                    }
//                }
//
//                if (callLogTypeList != null) {
//                    for (int j = 0; j < callLogTypeList.size(); j++) {
//                        String tempNumber = callLogTypeList.get(j).getNumber();
//                        if (!TextUtils.isEmpty(tempNumber)) {
//                            listOfBlockedNumber.add(tempNumber);
//                        }
//                    }
//                    Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOG_LIST,
//                            listOfBlockedNumber);
//                }
//            } else {
//                Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOG_LIST, new
//                        ArrayList());
//            }
//
//        }
//    }

    private void initSpinner() {

        callLogTypeArrayList = new ArrayList<>();
        nameAndProfileImage = new GetRCPNameAndProfileImage();

        callLogTypeArrayList = rContactApplication.getArrayListCallLogType();

        final List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(CALL_LOG_ALL_CALLS);
        spinnerArray.add(CALL_LOG_INCOMING_CALLS);
        spinnerArray.add(CALL_LOG_OUTGOING_CALLS);
        spinnerArray.add(CALL_LOG_MISSED_CALLS);

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

                            if (AppConstants.isFirstTime()) {
                                AppConstants.setIsFirstTime(false);
//                                new GetCallLogs().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                fetchCallLogs();
                            } else {
                                if (callLogTypeArrayList.size() > 0) {
                                    makeSimpleData();
                                } else {
//                                    new GetCallLogs().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    fetchCallLogs();
                                }
                            }
                        }
                    }, 250);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ArrayList<CallLogType> divideCallLogByChunck() {
        int size = callLogTypeArrayList.size();
        callLogsListbyChunck = new ArrayList<>();
        for (ArrayList<CallLogType> partition : choppedCallLog(callLogTypeArrayList, LIST_PARTITION_COUNT)) {
            // do something with partition
            callLogsListbyChunck.addAll(partition);
            callLogTypeArrayList.removeAll(partition);
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

    private class GetCallLogs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callLogTypeArrayList.clear();
            simpleCallLogListAdapter = new SimpleCallLogListAdapter(getActivity(), callLogTypeArrayList);
            recyclerCallLogs.setAdapter(simpleCallLogListAdapter);
        }

        protected Void doInBackground(Void... urls) {
            fetchCallLogs();
            return null;
        }

        protected void onPostExecute(Void result) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeSimpleData();
                    nameAndProfileImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        }
    }

    private void fetchCallLogs() {

        callLogTypeArrayList.clear();
        simpleCallLogListAdapter = new SimpleCallLogListAdapter(getActivity(), callLogTypeArrayList);
        recyclerCallLogs.setAdapter(simpleCallLogListAdapter);

        try {

            String order = CallLog.Calls.DATE + " DESC";
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, order);

            if (cursor != null) {
                while (cursor.moveToNext()) {

//                    String number = Utils.getFormattedNumber(getActivity(),
//                            cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));

                    String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

                    CallLogType callLogType = new CallLogType(getActivity());

                    callLogType.setNumber(number);

                    String userName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

                    if (!TextUtils.isEmpty(userName))
                        callLogType.setName(userName);
                    else
                        callLogType.setName("");

                    callLogType.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                    callLogType.setDuration(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION)));
                    callLogType.setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
                    callLogType.setUniqueContactId(cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID)));
//                    String uniquePhoneBookId = getStarredStatusFromNumber(number);
//                    if (!TextUtils.isEmpty(uniquePhoneBookId)) {
//                        callLogType.setLocalPbRowId(uniquePhoneBookId);
//                    } else
                    callLogType.setLocalPbRowId(" ");
                    callLogType.setProfileImage("");

                    callLogTypeArrayList.add(callLogType);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                makeSimpleData();
//                if(isFromDeleteBroadcast){
//                    isFromDeleteBroadcast =  false;
//                    new GetRCPNameAndProfileImage().execute();
//                }else{
                nameAndProfileImage = new GetRCPNameAndProfileImage();
                nameAndProfileImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                }

            }
        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                makeSimpleData();
//                /*if(isFromDeleteBroadcast){
//                    isFromDeleteBroadcast =  false;
//                    nameAndProfileImage = new GetRCPNameAndProfileImage();
//                    nameAndProfileImage.execute();
//                }else{
//                    nameAndProfileImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                }*/
//
//                nameAndProfileImage.execute();
//            }
//        }, 300);

    }

    private void makeSimpleData() {

        System.out.println("RContact callLogTypeArrayList size --> " + callLogTypeArrayList.size());

        ArrayList<CallLogType> filteredList = new ArrayList<>();

        try {
            if (callLogTypeArrayList != null && callLogTypeArrayList.size() > 0) {

                textNoCallsFound.setVisibility(View.GONE);

                if (!selectedCallType.equalsIgnoreCase(CALL_LOG_ALL_CALLS)) {
                    for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                        CallLogType callLogType = callLogTypeArrayList.get(i);
                        int callFilter = MoreObjects.firstNonNull(callLogType.getType(), 0);

                        if (selectedCallType.equalsIgnoreCase(CALL_LOG_MISSED_CALLS)
                                && callFilter == AppConstants.MISSED) {
                            filteredList.add(callLogType);
                        } else if (selectedCallType.equalsIgnoreCase(CALL_LOG_INCOMING_CALLS)
                                && callFilter == AppConstants.INCOMING) {
                            filteredList.add(callLogType);
                        } else if (selectedCallType.equalsIgnoreCase(CALL_LOG_OUTGOING_CALLS)
                                && callFilter == AppConstants.OUTGOING) {
                            filteredList.add(callLogType);
                        }
                    }

                    simpleCallLogListAdapter = new SimpleCallLogListAdapter(getActivity(), filteredList);

                } else {
                    simpleCallLogListAdapter = new SimpleCallLogListAdapter(getActivity(), callLogTypeArrayList);
                }

                recyclerCallLogs.setAdapter(simpleCallLogListAdapter);
                simpleCallLogListAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetRCPNameAndProfileImage extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... urls) {
            setRCPUserName();
            getPhoto();
//            getContactName();
            return null;
        }

        protected void onPostExecute(Void result) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    simpleCallLogListAdapter.notifyDataSetChanged();
                }
            });

            rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
        }
    }

    private void getPhoto() {
        try {
            if (callLogTypeArrayList.size() > 0) {
                for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                    if (nameAndProfileImage != null && nameAndProfileImage.isCancelled())
                        return;
                    CallLogType callLogType = callLogTypeArrayList.get(i);
                    String number = callLogType.getNumber();
                    if (!StringUtils.isEmpty(number)) {

                        String photoThumbNail = getPhotoUrlFromNumber(number);
                        if (!TextUtils.isEmpty(photoThumbNail)) {
                            callLogType.setProfileImage(photoThumbNail);
                        } else {
                            callLogType.setProfileImage("");
                        }

                        callLogTypeArrayList.set(i, callLogType);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getContactName() {
        try {
            if (callLogTypeArrayList.size() > 0) {
                for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                    if (nameAndProfileImage != null && nameAndProfileImage.isCancelled())
                        return;
                    CallLogType callLogType = callLogTypeArrayList.get(i);
                    String number = callLogType.getNumber();

                    if (!number.startsWith("+91")) {
                        number = "+91" + number;
                    }

                    String name = callLogType.getName();
                    if (StringUtils.isEmpty(name)) {
                        name = getNameFromNumber(number);
                        if (!StringUtils.isEmpty(name)) {
                            callLogType.setName(name);
                            callLogTypeArrayList.set(i, callLogType);
                        } else {
                            callLogType.setName("");
                        }
                    }
                }
            }
//9374538264
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setRCPUserName() {

        try {
            if (callLogTypeArrayList.size() > 0) {
                for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                    if (nameAndProfileImage != null && nameAndProfileImage.isCancelled())
                        return;
                    CallLogType callLogType = callLogTypeArrayList.get(i);

                    String number = callLogType.getNumber();

                    if (!number.startsWith("+91")) {
                        number = "+91" + number;
                    }

                    if (!StringUtils.isEmpty(number)) {

                        ProfileMobileMapping profileMobileMapping =
                                tableProfileMobileMapping.getCloudPmIdFromProfileMappingFromNumber(number);
                        if (profileMobileMapping != null) {
                            String cloudPmId = profileMobileMapping.getMpmCloudPmId();
                            if (!StringUtils.isEmpty(cloudPmId)) {
                                UserProfile userProfile = tableProfileMaster
                                        .getRCPProfileFromPmId(Integer.parseInt(cloudPmId));
                                callLogType.setRcpUser(true);
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

                                callLogTypeArrayList.set(i, callLogType);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                        .REQ_UPLOAD_CALL_LOGS, null, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CALL_LOGS);
            } else {
                Utils.showErrorSnackBar(getActivity(), linearCallLogMain, getResources()
                        .getString(R.string.msg_no_network));
            }
        }

    }

    private ArrayList<ArrayList<CallLogType>> choppedCallLog(ArrayList<CallLogType> list,
                                                             final
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

    private String getPhotoUrlFromNumber(String phoneNumber) {
        String photoThumbUrl = "";
        Cursor cursor = null;
        try {

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    photoThumbUrl = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.PHOTO_THUMBNAIL_URI));
                }
                cursor.close();
            }

        } catch (Exception e) {
            if (cursor != null)
                cursor.close();
            e.printStackTrace();
        }

        return photoThumbUrl;
    }

    private String getStarredStatusFromNumber(String phoneNumber) {
        String numberId, rawId = "";
        Cursor cursor = null;
        try {

            ContentResolver contentResolver = getActivity().getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            cursor =
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
            if (cursor != null)
                cursor.close();
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
                final int position = viewHolder.getAdapterPosition();
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
                        simpleCallLogListAdapter.notifyItemChanged(position);
                    }
                }, 1000);
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


    private ArrayList<CallLogType> callLogHistory(String number) {
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
                            for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                                CallLogType callLogType = callLogTypeArrayList.get(i);
                                String numberToDelete = callLogType.getNumber();
                                if (numberToDelete.equalsIgnoreCase(number)) {
                                    callLogTypeArrayList.remove(callLogType);
                                    rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
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
                                for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                                    CallLogType callLogType = callLogTypeArrayList.get(i);
                                    String numberToDelete = callLogType.getNumber();
                                    if (numberToDelete.equalsIgnoreCase(number)) {
                                        callLogTypeArrayList.remove(callLogType);
                                        rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
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

                            System.out.println("RContact itemIndexToRemove --> " + itemIndexToRemove);

                            // updated on 19/04/2017, when data are loading from arraylist
//                            CallLogType callDataToUpdate = simpleCallLogListAdapter.getSelectedCallLogData();
                            callLogTypeArrayList.remove(itemIndexToRemove);
                            rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                            simpleCallLogListAdapter.notifyItemRemoved(itemIndexToRemove);
                            simpleCallLogListAdapter.notifyItemRangeChanged(itemIndexToRemove, simpleCallLogListAdapter.getItemCount());
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
                    for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                        CallLogType callLogType = callLogTypeArrayList.get(i);
                        String numberToDelete = callLogType.getNumber();
                        if (numberToDelete.equalsIgnoreCase(number)) {
                            callLogTypeArrayList.remove(callLogType);
                            rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
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
                callLogTypeArrayList = new ArrayList<>();
                isFromDeleteBroadcast = true;
                fetchCallLogs();
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

    private String getNameFromNumber(String phoneNumber) {
        String contactName = "";
        try {

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactName;
    }
}
