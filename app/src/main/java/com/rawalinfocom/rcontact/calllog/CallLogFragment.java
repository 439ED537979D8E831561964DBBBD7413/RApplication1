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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.CallLogListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookCallLogs;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogFragment extends BaseFragment implements WsResponseListener {


    private final String ALL_CALLS = "All";
    private final String INCOMING_CALLS = "Incoming";
    private final String OUTGOING_CALLS = "Outgoing";
    private final String MISSED_CALLS = "Missed";

    @BindView(R.id.progressBarCallLog)
    ProgressBar progressBarCallLog;
    /* @BindView(R.id.relativeProgressBar)
     RelativeLayout relativeProgressBar;*/
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
  /*  @BindView(R.id.progressBarLoadCallLogs)
    ProgressBar progressBarLoadCallLogs;
    @BindView(R.id.relativeLoadingData)
    RelativeLayout relativeLoadingData;*/

    private CallLogListAdapter callLogListAdapter;

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
    boolean isFirstChuck = false;

    private String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG, Manifest
            .permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static CallLogType callLogTypeReceiver;
    private int LIST_PARTITION_COUNT = 10;
    private static boolean startInsertion = false;
    private boolean isFirstTime;
    RContactApplication rContactApplication;
    MaterialDialog permissionConfirmationDialog;
    int logsDisplayed = 0;
    ArrayList<String> listOfIds;
    ArrayList<String> callLogIdsListByChunck;
    int count =0;

    //<editor-fold desc="Constructors">

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
        callLogTypeReceiver = new CallLogType();
        if (Utils.getBooleanPreference(getActivity(), AppConstants
                .PREF_CALL_LOG_STARTS_FIRST_TIME, true)) {
            isFirstTime = true;
            Utils.setBooleanPreference(getActivity(), AppConstants
                    .PREF_CALL_LOG_STARTS_FIRST_TIME, false);
        }
    }

    @Override
    public void getFragmentArguments() {

    }

//    LoadsCallLogsInBackground loadsCallLogsInBackgroundAsyncTask = new LoadsCallLogsInBackground();

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
//        telephonyInit();
    }

    int itemPosition;

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (AppConstants.isFromReceiver) {
                final CallLogType callLogType = new CallLogType(getActivity());
                String name = callLogType.findNameByNumber(callLogTypeReceiver.getNumber());
                if (!TextUtils.isEmpty(name))
                    callLogTypeReceiver.setName(name);

                final ArrayList<CallLogType> arrayListHistroy = callLogHistory
                        (callLogTypeReceiver.getNumber());
                final ArrayList<CallLogType> arrayListHistoryCountAsDay = new ArrayList<>();
                for (int i = 0; i < arrayListHistroy.size(); i++) {
                    CallLogType callLogTypeHistory = arrayListHistroy.get(i);
                    long date = callLogTypeHistory.getHistoryDate();
                    Date objDate1 = new Date(date);
                    String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format(objDate1);
                    String intentDate = new SimpleDateFormat("yyyy-MM-dd").format
                            (callLogTypeReceiver.getCallReceiverDate());
                    if (intentDate.equalsIgnoreCase(arrayDate)) {
                        arrayListHistoryCountAsDay.add(callLogTypeHistory);
                    }
                    String simNumber = arrayListHistroy.get(i).getHistoryCallSimNumber();
                    callLogTypeReceiver.setCallSimNumber(simNumber);
                }
                int count = arrayListHistoryCountAsDay.size();
                callLogTypeReceiver.setHistoryLogCount(count);
                callLogTypeReceiver.setArrayListCallHistory(arrayListHistoryCountAsDay);
                callLogType.setHistoryLogCount(count);
                String receiverDate = "Today";
                if (!arrayListObjectCallLogs.contains(receiverDate)) {
                    arrayListCallLogHeader.add(0, receiverDate);
                    arrayListObjectCallLogs.add(0, receiverDate);
                    callLogListAdapter.notifyItemInserted(0);
                    recyclerCallLogs.scrollToPosition(0);
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        String number = callLogTypeReceiver.getNumber();
                        Date callReceiverDate = callLogTypeReceiver.getCallReceiverDate();
                        String logDate = new SimpleDateFormat("yyyy-MM-dd").format
                                (callReceiverDate);
                        if (arrayListObjectCallLogs.size() == 1) {
                            arrayListObjectCallLogs.add(1, callLogTypeReceiver);
                            callLogListAdapter.notifyItemInserted(1);
                        } else {
                            boolean isNumberExists = false;
                            for (int j = 0; j < arrayListObjectCallLogs.size(); j++) {
                                if (arrayListObjectCallLogs.get(j) instanceof CallLogType) {
                                    CallLogType tempCallLogType = (CallLogType)
                                            arrayListObjectCallLogs.get(j);
                                    long objDate1 = tempCallLogType.getDate();
                                    String arrayDate = "";
                                    if (objDate1 > 0) {
                                        Date objDate = new Date(objDate1);
                                        arrayDate = new SimpleDateFormat("yyyy-MM-dd").format
                                                (objDate);

                                    } else {
                                        arrayDate = logDate;
                                    }

                                    if (arrayDate.equalsIgnoreCase(logDate)) {
                                        if (!(((CallLogType) arrayListObjectCallLogs.get(j))
                                                .getNumber().equalsIgnoreCase(number))) {
                                            isNumberExists = false;

                                        } else {
                                            isNumberExists = true;
                                            itemPosition = arrayListObjectCallLogs.indexOf
                                                    (tempCallLogType);
                                            break;
                                        }
                                    }

                                }
                            }
                            if (!isNumberExists) {
                                arrayListObjectCallLogs.add(1, callLogTypeReceiver);
                                callLogListAdapter.notifyItemInserted(1);
                            } else {
                                if (itemPosition != -1) {
                                    arrayListObjectCallLogs.set(itemPosition, callLogTypeReceiver);
                                    callLogListAdapter.notifyDataSetChanged();
                                }

                            }

                        }
                    }
                }, 1500);

               /* rContactApplication.setArrayListObjectCallLogs(arrayListObjectCallLogs);
                arrayListCallLogs.set(1, callLogTypeReceiver);
                rContactApplication.setArrayListCallLogType(arrayListCallLogs);*/
            }

            makeBlockedNumberList();
            ArrayList<String> listOfBlockedNumbers = Utils.getArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOG_LIST);
            if (listOfBlockedNumbers != null && listOfBlockedNumbers.size() > 0) {
                /*loadsCallLogsInBackgroundAsyncTask = new LoadsCallLogsInBackground();
                loadsCallLogsInBackgroundAsyncTask.execute();*/
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadLogs(selectedCallType);
                    }
                },100);

            } else {
                if (arrayListObjectCallLogs != null && arrayListObjectCallLogs.size() > 0) {
                    for (int k = 0; k < arrayListObjectCallLogs.size(); k++) {
                        if (arrayListObjectCallLogs.get(k) instanceof CallLogType) {
                            CallLogType tempCallLogType = (CallLogType) arrayListObjectCallLogs.get(k);
                            if ((((CallLogType) arrayListObjectCallLogs.get(k)).getBlockedType() == AppConstants.BLOCKED)) {
                                int itemPosition = arrayListObjectCallLogs.indexOf(tempCallLogType);
                                if (itemPosition != -1) {
                                    tempCallLogType.setBlockedType(AppConstants.UNBLOCK);
                                    arrayListObjectCallLogs.set(itemPosition, tempCallLogType);
                                    callLogListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        registerLocalbroadcast();
    }


    @Override
    public void onPause() {
        super.onPause();
        isFirstTime = false;
        AppConstants.isFromReceiver = false;

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

        LocalBroadcastManager localBroadcastManagerBlock = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter4 = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_PROFILE_BLOCK);
        localBroadcastManagerBlock.registerReceiver(localBroadcastReceiverBlock, intentFilter4);

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

        LocalBroadcastManager localBroadcastManagerProfileBlock = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManagerProfileBlock.unregisterReceiver(localBroadcastReceiverBlock);

    }

    private void showProgressBar() {

        /*if (linearMainContent.getVisibility() == View.VISIBLE)
            linearMainContent.setVisibility(View.GONE);

        if (relativeProgressBar.getVisibility() == View.GONE)
            relativeProgressBar.setVisibility(View.VISIBLE);*/

        if (progressBarCallLog.getVisibility() == View.GONE)
            progressBarCallLog.setVisibility(View.VISIBLE);

        /* textLoading.setVisibility(View.GONE);*/

    }

    private void hideProgressBar() {

       /* if (relativeProgressBar.getVisibility() == View.VISIBLE)
            relativeProgressBar.setVisibility(View.GONE);*/

        if (progressBarCallLog.getVisibility() == View.VISIBLE)
            progressBarCallLog.setVisibility(View.GONE);

       /* if (linearMainContent.getVisibility() == View.GONE)
            linearMainContent.setVisibility(View.VISIBLE);*/

//        textLoading.setVisibility(View.GONE);
    }

    private void init() {
        arrayListCallLogs = new ArrayList<>();
        arrayListCallLogHeader = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();
        rContactApplication = (RContactApplication)
                getActivity().getApplicationContext();
        makeBlockedNumberList();
        listOfIds = Utils.getArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOGS_ID_SET);
        initSpinner();

        // Checking for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionToExecute(requiredPermissions, AppConstants.READ_LOGS);
        }

        recyclerCallLogs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    ArrayList<String> callLogIdsArrayList = divideCallLogIdsByChunck();
                    if (callLogIdsArrayList != null && callLogIdsArrayList.size() > 0) {
                        logsDisplayed = logsDisplayed + callLogIdsArrayList.size();
                        loadLogs(selectedCallType);
                       /* loadsCallLogsInBackgroundAsyncTask = new LoadsCallLogsInBackground();
                        loadsCallLogsInBackgroundAsyncTask.execute();*/
                    } else {
//                        Toast.makeText(getActivity(),"Last Record",Toast.LENGTH_SHORT).show();
                    }
//                    Utils.setIntegerPreference(getActivity(),AppConstants.PREF_CALL_LOG_TO_FETCH_COUNT,logsDisplayed);

                    /*visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                        }
                    }*/
                }

            }
        });

    }

    private void makeBlockedNumberList() {
        if (Utils.getHashMapPreferenceForBlock(getActivity(), AppConstants
                .PREF_BLOCK_CONTACT_LIST) != null) {
            HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                    Utils.getHashMapPreferenceForBlock(getActivity(), AppConstants.PREF_BLOCK_CONTACT_LIST);
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
                    Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOG_LIST, listOfBlockedNumber);
                }
            } else {
                Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOG_LIST, new ArrayList());
            }

        }
    }

    /*private class LoadsCallLogsInBackground extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
//            Utils.showProgressDialog(mainView.getContext(),"Loading call logs",true);
            AppConstants.isProgressShowing = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            AppConstants.isBackgroundProcessStopped = false;
//            loadLogs(selectedCallType);
            loadCallLogsForOtherCallTypes(selectedCallType);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            Utils.hideProgressDialog();
            AppConstants.isProgressShowing = false;
            hideProgressBar();
            setAdapter();
            initSwipe();
        }
    }*/


    private void initSpinner() {
        final List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(ALL_CALLS);
        spinnerArray.add(INCOMING_CALLS);
        spinnerArray.add(OUTGOING_CALLS);
        spinnerArray.add(MISSED_CALLS);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCallFilter.setAdapter(adapter);

        spinnerCallFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getSelectedItem().toString();
                if (!TextUtils.isEmpty(value)) {
                    Log.i("callType", value);
                    selectedCallType = value;
                   /* AsyncTask.Status status =  loadsCallLogsInBackgroundAsyncTask.getStatus();
                    Log.i("Status", status+"");*/
                    /*loadsCallLogsInBackgroundAsyncTask = new LoadsCallLogsInBackground();
                    loadsCallLogsInBackgroundAsyncTask.execute();*/
//                   showProgressBar();
                    /*AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            if(value.equalsIgnoreCase(ALL_CALLS)){
                                loadLogs(value);
                            }else{
                                makeDataToDisplay(value,arrayListCallLogs);
                            }
                        }
                    });*/

                    /*if(!selectedCallType.equalsIgnoreCase(ALL_CALLS)){
                        loadsCallLogsInBackgroundAsyncTask = new LoadsCallLogsInBackground();
                        loadsCallLogsInBackgroundAsyncTask.execute();
                    }else {
                        Handler handler =  new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(selectedCallType.equalsIgnoreCase(ALL_CALLS)){
                                    loadLogs(selectedCallType);
                                }else{
//                                makeDataToDisplay(selectedCallType,arrayListCallLogs);

                                }
                            }
                        },200);

                    }*/

                    Handler handler =  new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(selectedCallType.equalsIgnoreCase(ALL_CALLS)){
                                loadLogs(selectedCallType);
                            }else{
                                makeDataToDisplay(selectedCallType,arrayListCallLogs);

                            }
                        }
                    },200);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private ArrayList<String> divideCallLogIdsByChunck() {
        int size = listOfIds.size();
        callLogIdsListByChunck = new ArrayList<>();
        if(isFirstChuck){
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
        return callLogIdsListByChunck;
    }

    private ArrayList<CallLogType> divideCallLogByChunck() {
        int size = tempList.size();
        callLogsListbyChunck = new ArrayList<>();
        for (ArrayList<CallLogType> partition : choppedCallLog(tempList, LIST_PARTITION_COUNT)) {
            // do something with partition
            Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
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
                for (ArrayList<CallLogType> partition : choppedCallLog(list, LIST_PARTITION_COUNT)) {
                    // do something with partition
                    Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
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
//        arrayListCallLogs =  new ArrayList<>();
        arrayListCallLogsHistory = new ArrayList<>();
        tempList = new ArrayList<>();
        ArrayList<String> listOfIds = Utils.getArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOGS_ID_SET);
        if (listOfIds == null) {
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
            Utils.setArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOGS_ID_SET, listOfIds);
        }

        if (listOfIds != null && listOfIds.size() > 0) {
//            int indexToBeginSync =  Utils.getIntegerPreference(getActivity(),AppConstants.PREF_CALL_LOG_TO_FETCH_COUNT,0);
            int indexToBeginSync = logsDisplayed;
            ArrayList<String> tempIdsList = new ArrayList<>();
            for (int i = indexToBeginSync; i < listOfIds.size(); i++) {
                String ids = listOfIds.get(i);
                tempIdsList.add(ids);
            }

            if (tempIdsList.size() > LIST_PARTITION_COUNT) {
                for (ArrayList<String> partition : chopped(tempIdsList, LIST_PARTITION_COUNT)) {
                    // do something with partition
                    if(count == 0){
                        isFirstChuck =  true;
                        count =  count+1;
                    }else{
                        isFirstChuck = false;
                    }
                    fetchCallLogsFromIds(partition);
                    break;
                }
            } else {
                fetchCallLogsFromIds(tempIdsList);
            }

        }

        initSwipe();

    }

    private void makeDataToDisplay(String callType, ArrayList<CallLogType> callLogs){
        ArrayList<String> listOfBlockedNumbers = Utils.getArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOG_LIST);
        List<CallLogType> filteredList =  new ArrayList<>();
        if (callType.equalsIgnoreCase(MISSED_CALLS)) {
//            filteredList = getLogsByCallType(AppConstants.MISSED_CALLS);
            arrayListObjectCallLogs =  new ArrayList<>();
            for(int i=0 ; i <callLogs.size();i++){
                CallLogType callLogType =  callLogs.get(i);
                int callfilter =  callLogType.getType();
                if(callfilter == AppConstants.MISSED){
                    filteredList.add(callLogType);
                }
            }
        } else if (callType.equalsIgnoreCase(INCOMING_CALLS)) {
//            filteredList = getLogsByCallType(AppConstants.INCOMING_CALLS);
            arrayListObjectCallLogs =  new ArrayList<>();
            for(int i=0 ; i <callLogs.size();i++){
                CallLogType callLogType =  callLogs.get(i);
                int callfilter =  callLogType.getType();
                if(callfilter == AppConstants.INCOMING){
                    filteredList.add(callLogType);
                }
            }
        } else if (callType.equalsIgnoreCase(OUTGOING_CALLS)) {
//            filteredList = getLogsByCallType(AppConstants.OUTGOING_CALLS);
            arrayListObjectCallLogs =  new ArrayList<>();
            for(int i=0 ; i <callLogs.size();i++){
                CallLogType callLogType =  callLogs.get(i);
                int callfilter =  callLogType.getType();
                if(callfilter == AppConstants.OUTGOING){
                    filteredList.add(callLogType);
                }
            }
        } else {
//            callLogs = rContactApplication.getArrayListCallLogType();
            arrayListObjectCallLogs =  new ArrayList<>();
            filteredList.addAll(callLogs);
        }

        int sizeOfCallLog = callLogs.size();
        tempList.addAll(callLogs);
        // To show recent call on top
//        Collections.reverse(callLogs);
        if (filteredList != null && filteredList.size() > 0)
        {
            for (int i = 0; i < filteredList.size(); i++) {
                CallLogType callLogType = filteredList.get(i);
                long logDate1 = callLogType.getDate();
                Date date1 = new Date(logDate1);
                String logDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                Log.i("Call Log date", logDate);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date yesDate;
                yesDate = cal.getTime();
                String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd").format(yesDate);
                Log.i("Call yesterday date", yesterdayDate);

                Calendar c = Calendar.getInstance();
                Date cDate = c.getTime();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                Log.i("Call Current date", currentDate);

                String finalDate;
                if (logDate.equalsIgnoreCase(currentDate)) {
                    finalDate = "Today";
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }
                    String number = callLogType.getNumber();
                    if (arrayListObjectCallLogs.size() == 1) {
                        arrayListObjectCallLogs.add(callLogType);
                    } else {
                        boolean isNumberExists = false;
                        for (int j = 0; j < arrayListObjectCallLogs.size(); j++) {
                            if (arrayListObjectCallLogs.get(j) instanceof CallLogType) {
                                if (!(((CallLogType) arrayListObjectCallLogs.get(j)).getNumber()
                                        .equalsIgnoreCase(number))) {
                                    isNumberExists = false;
                                } else {
                                    isNumberExists = true;
                                    break;
                                }
                            }
                        }
                        if (!isNumberExists) {
                            arrayListObjectCallLogs.add(callLogType);
                        }
                    }

                } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
                    finalDate = "Yesterday";
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }

                    String number = callLogType.getNumber();
                    if (arrayListObjectCallLogs.size() == 1) {
                        arrayListObjectCallLogs.add(callLogType);

                    } else {
                        boolean isNumberExists = false;
                        for (int j = 0; j < arrayListObjectCallLogs.size(); j++) {
                            if (arrayListObjectCallLogs.get(j) instanceof CallLogType) {
                                long objDate = ((CallLogType) arrayListObjectCallLogs.get(j))
                                        .getDate();
                                Date objDate1 = new Date(objDate);
                                String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format
                                        (objDate1);
                                if (arrayDate.equalsIgnoreCase(logDate)) {
                                    if (!(((CallLogType) arrayListObjectCallLogs.get(j))
                                            .getNumber().equalsIgnoreCase(number))) {
                                        isNumberExists = false;
                                    } else {
                                        isNumberExists = true;
                                        break;
                                    }
                                }

                            }
                        }
                        if (!isNumberExists) {
                            arrayListObjectCallLogs.add(callLogType);
                        }
                    }
                } else {
                    finalDate = new SimpleDateFormat("dd/MM,EEE").format(date1);
                    String number = callLogType.getNumber();
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }

                    if (arrayListObjectCallLogs.size() == 1) {
                        arrayListObjectCallLogs.add(callLogType);
                    } else {
                        boolean isNumberExists = false;
                        for (int j = 0; j < arrayListObjectCallLogs.size(); j++) {
                            if (arrayListObjectCallLogs.get(j) instanceof CallLogType) {
                                long objDate = ((CallLogType) arrayListObjectCallLogs.get(j))
                                        .getDate();
                                Date objDate1 = new Date(objDate);
                                String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format
                                        (objDate1);
                                if (arrayDate.equalsIgnoreCase(logDate)) {
                                    if (!(((CallLogType) arrayListObjectCallLogs.get(j))
                                            .getNumber().equalsIgnoreCase(number))) {
                                        isNumberExists = false;
                                    } else {
                                        isNumberExists = true;
                                        break;
                                    }
                                }

                            }
                        }
                        if (!isNumberExists) {
                            arrayListObjectCallLogs.add(callLogType);
                        }
                    }
                }
            }

            if (listOfBlockedNumbers != null && listOfBlockedNumbers.size() > 0) {
                for (int i = 0; i < listOfBlockedNumbers.size(); i++) {
                    String blockNumber = listOfBlockedNumbers.get(i);
                    for (int k = 0; k < arrayListObjectCallLogs.size(); k++) {
                        if (arrayListObjectCallLogs.get(k) instanceof CallLogType) {
                            CallLogType tempCallLogType = (CallLogType) arrayListObjectCallLogs.get(k);
                            if (!(((CallLogType) arrayListObjectCallLogs.get(k)).getNumber().equalsIgnoreCase(blockNumber))) {
                            } else {
                                int itemPosition = arrayListObjectCallLogs.indexOf(tempCallLogType);
                                if (itemPosition != -1) {
                                    tempCallLogType.setBlockedType(AppConstants.BLOCKED);
                                    arrayListObjectCallLogs.set(itemPosition, tempCallLogType);
                                }
                            }
                        }
                    }
                }
            }
            if(callType.equalsIgnoreCase(ALL_CALLS))
                rContactApplication.setArrayListObjectCallLogs(arrayListObjectCallLogs);
            //        hideProgressBar();
        /*if(callLogListAdapter == null){
            setAdapter();
        }else {
            callLogListAdapter.notifyDataSetChanged();
        }*/
            setAdapter();

        }else{
            arrayListObjectCallLogs =  new ArrayList<>();
            recyclerCallLogs.setVisibility(View.GONE);
            textLoading.setVisibility(View.VISIBLE);
        }


    }


    private void loadCallLogsForOtherCallTypes(String callType){

        List<CallLogType> callLogs = new ArrayList<>();
        arrayListCallLogs = new ArrayList<>();
        arrayListCallLogHeader = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();
        arrayListCallLogsHistory = new ArrayList<>();
        tempList = new ArrayList<>();

        ArrayList<String> listOfBlockedNumbers = Utils.getArrayListPreference(getActivity(), AppConstants.PREF_CALL_LOG_LIST);

        if (callType.equalsIgnoreCase(MISSED_CALLS)) {
            callLogs = getLogsByCallType(AppConstants.MISSED_CALLS);
        } else if (callType.equalsIgnoreCase(INCOMING_CALLS)) {
            callLogs = getLogsByCallType(AppConstants.INCOMING_CALLS);
        } else if (callType.equalsIgnoreCase(OUTGOING_CALLS)) {
            callLogs = getLogsByCallType(AppConstants.OUTGOING_CALLS);
        } else {
        }
        int sizeOfCallLog = callLogs.size();
        tempList.addAll(callLogs);
        // To show recent call on top
//        Collections.reverse(callLogs);
        if (callLogs != null && callLogs.size() > 0)
            for (int i = 0; i < callLogs.size(); i++) {
                CallLogType callLogType = callLogs.get(i);
                long logDate1 = callLogType.getDate();
                Date date1 = new Date(logDate1);
                String logDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                Log.i("Call Log date", logDate);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date yesDate;
                yesDate = cal.getTime();
                String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd").format(yesDate);
                Log.i("Call yesterday date", yesterdayDate);

                Calendar c = Calendar.getInstance();
                Date cDate = c.getTime();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                Log.i("Call Current date", currentDate);

                String finalDate;
                if (logDate.equalsIgnoreCase(currentDate)) {
                    finalDate = "Today";
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }
                    String number = callLogType.getNumber();
                    if (arrayListObjectCallLogs.size() == 1) {
                        arrayListObjectCallLogs.add(callLogType);
                    } else {
                        boolean isNumberExists = false;
                        for (int j = 0; j < arrayListObjectCallLogs.size(); j++) {
                            if (arrayListObjectCallLogs.get(j) instanceof CallLogType) {
                                if (!(((CallLogType) arrayListObjectCallLogs.get(j)).getNumber()
                                        .equalsIgnoreCase(number))) {
                                    isNumberExists = false;
                                } else {
                                    isNumberExists = true;
                                    break;
                                }
                            }
                        }
                        if (!isNumberExists) {
                            arrayListObjectCallLogs.add(callLogType);
                        }
                    }

                } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
                    finalDate = "Yesterday";
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }

                    String number = callLogType.getNumber();
                    if (arrayListObjectCallLogs.size() == 1) {
                        arrayListObjectCallLogs.add(callLogType);

                    } else {
                        boolean isNumberExists = false;
                        for (int j = 0; j < arrayListObjectCallLogs.size(); j++) {
                            if (arrayListObjectCallLogs.get(j) instanceof CallLogType) {
                                long objDate = ((CallLogType) arrayListObjectCallLogs.get(j))
                                        .getDate();
                                Date objDate1 = new Date(objDate);
                                String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format
                                        (objDate1);
                                if (arrayDate.equalsIgnoreCase(logDate)) {
                                    if (!(((CallLogType) arrayListObjectCallLogs.get(j))
                                            .getNumber().equalsIgnoreCase(number))) {
                                        isNumberExists = false;
                                    } else {
                                        isNumberExists = true;
                                        break;
                                    }
                                }

                            }
                        }
                        if (!isNumberExists) {
                            arrayListObjectCallLogs.add(callLogType);
                        }
                    }
                } else {
                    finalDate = new SimpleDateFormat("dd/MM,EEE").format(date1);
                    String number = callLogType.getNumber();
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }

                    if (arrayListObjectCallLogs.size() == 1) {
                        arrayListObjectCallLogs.add(callLogType);
                    } else {
                        boolean isNumberExists = false;
                        for (int j = 0; j < arrayListObjectCallLogs.size(); j++) {
                            if (arrayListObjectCallLogs.get(j) instanceof CallLogType) {
                                long objDate = ((CallLogType) arrayListObjectCallLogs.get(j))
                                        .getDate();
                                Date objDate1 = new Date(objDate);
                                String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format
                                        (objDate1);
                                if (arrayDate.equalsIgnoreCase(logDate)) {
                                    if (!(((CallLogType) arrayListObjectCallLogs.get(j))
                                            .getNumber().equalsIgnoreCase(number))) {
                                        isNumberExists = false;
                                    } else {
                                        isNumberExists = true;
                                        break;
                                    }
                                }

                            }
                        }
                        if (!isNumberExists) {
                            arrayListObjectCallLogs.add(callLogType);
                        }
                    }
                }
            }
        if (listOfBlockedNumbers != null && listOfBlockedNumbers.size() > 0) {
            for (int i = 0; i < listOfBlockedNumbers.size(); i++) {
                String blockNumber = listOfBlockedNumbers.get(i);
                for (int k = 0; k < arrayListObjectCallLogs.size(); k++) {
                    if (arrayListObjectCallLogs.get(k) instanceof CallLogType) {
                        CallLogType tempCallLogType = (CallLogType) arrayListObjectCallLogs.get(k);
                        if (!(((CallLogType) arrayListObjectCallLogs.get(k)).getNumber().equalsIgnoreCase(blockNumber))) {
                        } else {
                            int itemPosition = arrayListObjectCallLogs.indexOf(tempCallLogType);
                            if (itemPosition != -1) {
                                tempCallLogType.setBlockedType(AppConstants.BLOCKED);
                                arrayListObjectCallLogs.set(itemPosition, tempCallLogType);
                            }
                        }
                    }
                }
            }
        }

    }

    private void setAdapter() {
        recyclerCallLogs.setVisibility(View.VISIBLE);
        textLoading.setVisibility(View.GONE);
        if (arrayListCallLogHeader != null && arrayListObjectCallLogs != null
                && arrayListCallLogHeader.size() > 0 && arrayListObjectCallLogs.size() > 0) {
            callLogListAdapter = new CallLogListAdapter(getActivity(), arrayListObjectCallLogs,
                    arrayListCallLogHeader);
            recyclerCallLogs.setAdapter(callLogListAdapter);
            recyclerCallLogs.setFocusable(false);
            setRecyclerViewLayoutManager(recyclerCallLogs);
        }
    }

    private void insertServiceCall(ArrayList<CallLogType> callLogTypeArrayList) {

        WsRequestObject deviceDetailObject = new WsRequestObject();
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

    private ArrayList<ArrayList<CallLogType>> choppedCallLog(ArrayList<CallLogType> list, final int L) {
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

    private List<CallLogType> getLogsByCallType(int callType) {
        ArrayList logs = new ArrayList();
        String order = CallLog.Calls.DATE + " DESC";
        try {
            String selection;
            switch (callType) {
                case AppConstants.OUTGOING_CALLS:
                    selection = "type = 2";
                    break;
                case AppConstants.INCOMING_CALLS:
                    selection = "type = 1";
                    break;
                case AppConstants.ALL_CALLS:
                    selection = null;
                    break;
                case AppConstants.MISSED_CALLS:
                    selection = "type = 3";
                    break;
                default:
                    selection = null;
                    break;
            }


            Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, selection, null, order);
          /* Cursor cursor =  getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                   null, CallLog.Calls._ID +" = " + "208" , null, null);*/
            int cursorCount = cursor.getCount();

            if (cursor != null) {
                int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);

                while (cursor.moveToNext()) {
                   /* if (loadsCallLogsInBackgroundAsyncTask.isCancelled()) {
                        AppConstants.isProgressShowing = false;
                        break;
                    }*/
                    CallLogType log = new CallLogType(getActivity());
                    log.setNumber(cursor.getString(number));
                    String userName = cursor.getString(name);
                    if (!TextUtils.isEmpty(userName))
                        log.setName(userName);
                    else
                        log.setName("");

                    log.setType(cursor.getInt(type));
                    log.setDuration(cursor.getInt(duration));
                    log.setDate(cursor.getLong(date));
                    log.setUniqueContactId(cursor.getString(rowId));
                    String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                    Log.i("Number Type", numberTypeLog + " of number " + cursor.getString(number));
                    Log.i("Number Log Type", getLogType(cursor.getInt(type)) + " of number " +
                            cursor.getString(number));
                    log.setNumberType(numberTypeLog);
                    String userNumber = cursor.getString(number);
                    String uniquePhoneBookId = getStarredStatusFromNumber(userNumber);
                    Log.i("Unique PhoneBook Id", uniquePhoneBookId + " of no.:" + userNumber);
                    if (!TextUtils.isEmpty(uniquePhoneBookId))
                        log.setLocalPbRowId(uniquePhoneBookId);
                    else
                        log.setLocalPbRowId(" ");

                    log.setFlag(1);
                    ArrayList<CallLogType> arrayListHistory;
                   /* if (!TextUtils.isEmpty(userName)) {
                        arrayListHistory = callLogHistory(userName);
                    } else {*/
                    arrayListHistory = callLogHistory(userNumber);
//                    }
                    log.setArrayListCallHistory(arrayListHistory);

                    ArrayList<CallLogType> arrayListHistoryCount = new ArrayList<>();
                    for (int i = 0; i < arrayListHistory.size(); i++) {
                        CallLogType tempCallLogType = arrayListHistory.get(i);
                        String simNumber = arrayListHistory.get(i).getHistoryCallSimNumber();
                        log.setCallSimNumber(simNumber);
                        long tempdate = tempCallLogType.getHistoryDate();
                        Date objDate1 = new Date(tempdate);
                        String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format(objDate1);
                        long callLogDate = log.getDate();
                        Date intentDate1 = new Date(callLogDate);
                        String intentDate = new SimpleDateFormat("yyyy-MM-dd").format(intentDate1);
                        if (intentDate.equalsIgnoreCase(arrayDate)) {
                            arrayListHistoryCount.add(tempCallLogType);
                        }
                    }
                    int logCount = arrayListHistoryCount.size();
                    log.setHistoryLogCount(logCount);
                    Log.i("History size ", logCount + "" + " of " + cursor.getString(number));
                    Log.i("History", "----------------------------------");
                    logs.add(log);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }

    private void fetchCallLogsFromIds(ArrayList<String> listOfRowIds) {
        try {
            for (int i = 0; i < listOfRowIds.size(); i++) {
                String uniqueCallLogId = listOfRowIds.get(i);
                if (!TextUtils.isEmpty(uniqueCallLogId)) {
                    String order = CallLog.Calls.DATE + " DESC";
                    Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
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
                            log.setNumber(cursor.getString(number));
                            String userName = cursor.getString(name);
                            if (!TextUtils.isEmpty(userName))
                                log.setName(userName);
                            else
                                log.setName("");

                            log.setType(cursor.getInt(type));
                            log.setDuration(cursor.getInt(duration));
                            log.setDate(cursor.getLong(date));
                            log.setUniqueContactId(cursor.getString(rowId));
                            String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                            Log.i("Number Type", numberTypeLog + " of number " + cursor.getString(number));
                            Log.i("Number Log Type", getLogType(cursor.getInt(type)) + " of number " +
                                    cursor.getString(number));
                            log.setNumberType(numberTypeLog);
                            String userNumber = cursor.getString(number);
                            String uniquePhoneBookId = getStarredStatusFromNumber(userNumber);
                            Log.i("Unique PhoneBook Id", uniquePhoneBookId + " of no.:" + userNumber);
                            if (!TextUtils.isEmpty(uniquePhoneBookId))
                                log.setLocalPbRowId(uniquePhoneBookId);
                            else
                                log.setLocalPbRowId(" ");

                            log.setFlag(7);
                            ArrayList<CallLogType> arrayListHistory;
                   /* if (!TextUtils.isEmpty(userName)) {
                        arrayListHistory = callLogHistory(userName);
                    } else {*/
                            arrayListHistory = callLogHistory(userNumber);
//                    }
                            log.setArrayListCallHistory(arrayListHistory);

                            ArrayList<CallLogType> arrayListHistoryCount = new ArrayList<>();
                            for (int j = 0; j < arrayListHistory.size(); j++) {
                                CallLogType tempCallLogType = arrayListHistory.get(j);
                                String simNumber = arrayListHistory.get(j).getHistoryCallSimNumber();
                                log.setCallSimNumber(simNumber);
                                long tempdate = tempCallLogType.getHistoryDate();
                                Date objDate1 = new Date(tempdate);
                                String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format(objDate1);
                                long callLogDate = log.getDate();
                                Date intentDate1 = new Date(callLogDate);
                                String intentDate = new SimpleDateFormat("yyyy-MM-dd").format(intentDate1);
                                if (intentDate.equalsIgnoreCase(arrayDate)) {
                                    arrayListHistoryCount.add(tempCallLogType);
                                }
                            }
                            int logCount = arrayListHistoryCount.size();
                            log.setHistoryLogCount(logCount);
                            Log.i("History size ", logCount + "" + " of " + cursor.getString(number));
                            Log.i("History", "----------------------------------");
                            arrayListCallLogs.add(log);
                            rContactApplication.setArrayListCallLogType(arrayListCallLogs);
                        }
                        cursor.close();
                    }
                }
            }
            makeDataToDisplay(selectedCallType,arrayListCallLogs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getStarredStatusFromNumber(String phoneNumber) {
        String numberId = "";
        try {

            numberId = "";
            ContentResolver contentResolver = getActivity().getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
//                Log.d("LocalPBId", "contactMatch id: " + numberId + " of " + contactName);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return numberId;
    }

    public String getLogType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "Missed";
            case CallLog.Calls.REJECTED_TYPE:
                return "Rejected";
            case CallLog.Calls.BLOCKED_TYPE:
                return "Blocked";
            case CallLog.Calls.VOICEMAIL_TYPE:
                return "Voicemail";

        }
        return "OTHERS";
    }

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
                String actionNumber = StringUtils.defaultString(((CallLogListAdapter
                        .AllCallLogViewHolder) viewHolder).textTempNumber.getText()
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
                        callLogListAdapter.notifyDataSetChanged();
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
                if (viewHolder instanceof CallLogListAdapter.CallLogHeaderViewHolder) {
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
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                formattedNumber));
                        startActivity(intent);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + formattedNumber + "?");
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
        Log.i("IsDual", isDualSIM + "");
        Log.i("SIM1 ready", isSIM1Ready + "");
        Log.i("SIM2 ready", isSIM2Ready + "");


    }


    // A method to check if a permission is granted then execute tasks depending on that
    // particular permission
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String permissions[], int requestCode) {

        boolean logs = ContextCompat.checkSelfPermission(getActivity(), permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean readState = ContextCompat.checkSelfPermission(getActivity(), permissions[1]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean location = ContextCompat.checkSelfPermission(getActivity(), permissions[2]) !=
                PackageManager.PERMISSION_GRANTED;
        if (logs || location || readState) {
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
                .READ_CALL_LOG) && permissions[1].equals(Manifest.permission.READ_PHONE_STATE)
                && permissions[2].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED && grantResults[1] ==
                    PermissionChecker.PERMISSION_GRANTED &&
                    grantResults[2] == PermissionChecker.PERMISSION_GRANTED) {
                initSpinner();
                telephonyInit();
            } else {
                /*new AlertDialog.Builder(getActivity())
                        .setMessage("The app needs these permissions to work, Exit?")
                        .setTitle("Permission Denied")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkPermissionToExecute(requiredPermissions, AppConstants
                                        .READ_LOGS);
                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        }).show();*/
                showPermissionConfirmationDialog();
            }
        }
    }


    private void simSlotDetection(String number) {
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = getActivity().getContentResolver().query(allCalls, null, CallLog.Calls.NUMBER
                + " =?", new String[]{number}, null);
        if (c != null && c.getCount() > 0) {
            String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            Log.i("Sim slot id", id + " of number " + number);
        }
        c.close();

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
        String numberToSearch = number;
        ArrayList<CallLogType> callDetails = new ArrayList<>();
        Cursor cursor;

        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(number);
        if (matcher1.find()) {
            cursor = getCallHistoryDataByNumber(number);
        } else {
            cursor = getCallHistoryDataByName(number);
        }

        try {
            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int callLogId = cursor.getColumnIndex(CallLog.Calls._ID);
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
                    String accountId = " ";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        accountId = cursor.getString(account_id);
                        if (!TextUtils.isEmpty(accountId) && account_id > 0)
                            Log.e("Sim Type", accountId);

                        String accountName = cursor.getString(account);
                        if (!TextUtils.isEmpty(accountName))
                            Log.e("Sim Name", accountName);

//                        String userImage = cursor.getString(profileImage);
//                        if (userImage != null)
//                            Log.e("User Image", userImage);
                    } else {
                        if (account_id > 0) {
                            accountId = cursor.getString(account_id);
                            Log.e("Sim Type", accountId);
                        }
                    }
                    int histroyId = Integer.parseInt(cursor.getString(callLogId));
                    CallLogType logObject = new CallLogType();
                    logObject.setHistoryNumber(phNum);
                    logObject.setHistoryType(callType);
                    logObject.setHistoryDate(dateOfCall);
                    logObject.setHistoryDuration(Integer.parseInt(callDuration));
                    logObject.setHistoryCallSimNumber(accountId);
                    logObject.setHistoryId(histroyId);

                    Date date1 = new Date(dateOfCall);
                    String callDataAndTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format
                            (date1);
                    logObject.setCallDateAndTime(callDataAndTime);

                    String typeOfCall = getLogType(callType);
                    if (typeOfCall.equalsIgnoreCase("Rejected")) {
                        typeOfCall = "Missed";
                    }
                    logObject.setTypeOfCall(typeOfCall);

                    String durationtoPass = logObject.getCoolDuration(Float.parseFloat
                            (callDuration));
                    logObject.setDurationToPass(durationtoPass);

                    callDetails.add(logObject);
                }
            }

            cursor.close();
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
        permissionConfirmationDialog.setLeftButtonText("Cancel");
        permissionConfirmationDialog.setRightButtonText("OK");
        permissionConfirmationDialog.setDialogBody("Call log permission is required. Do you want " +
                "to try again?");

        permissionConfirmationDialog.showDialog();

    }

    //    @TargetApi(Build.VERSION_CODES.M)
    private ArrayList<CallLogType> getNumbersFromName(String number) {
        Cursor cursor = null;
        ArrayList<CallLogType> listNumber = new ArrayList<>();
        try {
            final Uri Person = Uri.withAppendedPath(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                    Uri.encode(number));

            cursor = getActivity().getContentResolver().query(Person, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " =?", new String[]{number}, null);

            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (cursor.moveToNext()) {
                    CallLogType callLogType = new CallLogType();
                    String profileNumber = cursor.getString(number1);
                    String formattedNumber = Utils.getFormattedNumber(getActivity(), profileNumber);
                    String uniqueContactId = getStarredStatusFromNumber(profileNumber);
                    callLogType.setUniqueContactId(uniqueContactId);
                    callLogType.setName(number);
                    callLogType.setNumber(formattedNumber);
                    listNumber.add(callLogType);
                }
            }
            cursor.close();


        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return listNumber;
    }

    //</editor-fold>

    boolean clearLogs;
    boolean clearLogsFromContacts;
    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("CallLogFragment", "onReceive() of LocalBroadcast");
            clearLogs = intent.getBooleanExtra(AppConstants.EXTRA_CLEAR_CALL_LOGS, false);
            clearLogsFromContacts = intent.getBooleanExtra(AppConstants
                    .EXTRA_CLEAR_CALL_LOGS_FROM_CONTACTS, false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clearLogs) {
                        if (callLogListAdapter != null) {
                            int itemIndexToRemove = callLogListAdapter.getSelectedPosition();
                            arrayListObjectCallLogs.remove(itemIndexToRemove);
                            callLogListAdapter.notifyItemRemoved(itemIndexToRemove);
                            clearLogs = false;
                        }
                    } else {
                        if (clearLogsFromContacts) {
                            if (callLogListAdapter != null) {
                                int itemIndexToRemove = callLogListAdapter.getSelectedPosition();
                                arrayListObjectCallLogs.remove(itemIndexToRemove);
                                callLogListAdapter.notifyItemRemoved(itemIndexToRemove);
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
            Log.i("CallLogFragment", "onReceive() of LocalBroadcast");

            removeLogs = intent.getBooleanExtra(AppConstants.EXTRA_REMOVE_CALL_LOGS, false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (removeLogs) {
                        if (callLogListAdapter != null) {
                            int itemIndexToRemove = callLogListAdapter.getSelectedPosition();
                            arrayListObjectCallLogs.remove(itemIndexToRemove);
                            callLogListAdapter.notifyItemRemoved(itemIndexToRemove);
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
            Log.i("CallLogFragment", "onReceive() of LocalBroadcast");

            boolean deleteAll = intent.getBooleanExtra(AppConstants.EXTRA_DELETE_ALL_CALL_LOGS,
                    false);
            if (deleteAll) {
                if (callLogListAdapter != null) {
                    int itemIndexToRemove = callLogListAdapter.getSelectedPosition();
                    arrayListObjectCallLogs.remove(itemIndexToRemove);
                    callLogListAdapter.notifyItemRemoved(itemIndexToRemove);
                    deleteAll = false;
                }
            } else {
                //update history count
                int itemIndexToRemove = callLogListAdapter.getSelectedPosition();
                CallLogType callDataToUpdate = callLogListAdapter.getSelectedCallLogData();
                long dateToUpdate = callLogListAdapter.getSelectedLogDate();
                if (callDataToUpdate != null) {
                    String number = callDataToUpdate.getNumber();
                    ArrayList<CallLogType> arrayListHistroy = callLogHistory(number);
                    ArrayList<CallLogType> arrayListHistoryCountAsDay = new ArrayList<>();
                    for (int i = 0; i < arrayListHistroy.size(); i++) {
                        CallLogType callLogTypeHistory = arrayListHistroy.get(i);
                        long date = callLogTypeHistory.getHistoryDate();
                        Date objDate1 = new Date(date);
                        String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format(objDate1);
                        Date compareDate = new Date(dateToUpdate);
                        String intentDate = new SimpleDateFormat("yyyy-MM-dd").format(compareDate);
                        if (intentDate.equalsIgnoreCase(arrayDate)) {
                            arrayListHistoryCountAsDay.add(callLogTypeHistory);
                        }
                    }
                    int count = arrayListHistoryCountAsDay.size();
                    callDataToUpdate.setHistoryLogCount(count);
                    arrayListObjectCallLogs.set(itemIndexToRemove, callDataToUpdate);
                    callLogListAdapter.notifyDataSetChanged();
                }

                LocalBroadcastManager localBroadcastManagerDeleteLogs = LocalBroadcastManager
                        .getInstance(getActivity());
                localBroadcastManagerDeleteLogs.unregisterReceiver
                        (localBroadcastReceiverDeleteLogs);

            }

        }
    };


    private BroadcastReceiver localBroadcastReceiverTabChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("CallLogFragment", "onReceive() of LocalBroadcast");

            if (AppConstants.EXTRA_CALL_LOG_SWITCH_TAB_VALUE) {
//                loadsCallLogsInBackgroundAsyncTask.cancel(true);
                AppConstants.isBackgroundProcessStopped = true;

            } else {
            }

        }
    };


    private BroadcastReceiver localBroadcastReceiverBlock = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("CallLogFragment", "onReceive() of LocalBroadcast");

            boolean isBlocked = intent.getBooleanExtra(AppConstants.EXTRA_CALL_LOG_BLOCK, false);

            if (isBlocked) {
                if (callLogListAdapter != null) {
                    CallLogType callDataToUpdate = callLogListAdapter.getSelectedCallLogData();
                    String number = callDataToUpdate.getNumber();
                    String key = "";
                    key = callDataToUpdate.getLocalPbRowId();
                    if (key.equalsIgnoreCase(" ")) {
                        key = callDataToUpdate.getUniqueContactId();
                    }
                    if (Utils.getHashMapPreferenceForBlock(context, AppConstants
                            .PREF_BLOCK_CONTACT_LIST) != null) {
                        HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                                Utils.getHashMapPreferenceForBlock(context, AppConstants.PREF_BLOCK_CONTACT_LIST);
                        ArrayList<CallLogType> callLogTypeList = new ArrayList<CallLogType>();
                        String blockedNumber = "";
                        if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                            if (blockProfileHashMapList.containsKey(key))
                                callLogTypeList.addAll(blockProfileHashMapList.get(key));

                        }
                        if (callLogTypeList != null) {
                            for (int j = 0; j < callLogTypeList.size(); j++) {
                                blockedNumber = callLogTypeList.get(j).getNumber();
                                for (int k = 0; k < arrayListObjectCallLogs.size(); k++) {
                                    if (arrayListObjectCallLogs.get(k) instanceof CallLogType) {
                                        CallLogType tempCallLogType = (CallLogType) arrayListObjectCallLogs.get(k);
                                        if (!(((CallLogType) arrayListObjectCallLogs.get(k)).getNumber().equalsIgnoreCase(blockedNumber))) {
                                        } else {
                                            itemPosition = arrayListObjectCallLogs.indexOf(tempCallLogType);
                                            if (itemPosition != -1) {
                                                tempCallLogType.setBlockedType(AppConstants.BLOCKED);
                                                arrayListObjectCallLogs.set(itemPosition, tempCallLogType);
                                                callLogListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                }

            } else {

                if (callLogListAdapter != null) {
                    CallLogType callDataToUpdate = callLogListAdapter.getSelectedCallLogData();
                    String number = callDataToUpdate.getNumber();
                    String name = callDataToUpdate.getName();
                    if (!TextUtils.isEmpty(name)) {
                        ArrayList<CallLogType> listOfBlock = getNumbersFromName(name);
                        for (int i = 0; i < listOfBlock.size(); i++) {
                            String numberToUnblock = listOfBlock.get(i).getNumber();
                            for (int j = 0; j < arrayListObjectCallLogs.size(); j++) {
                                if (arrayListObjectCallLogs.get(j) instanceof CallLogType) {
                                    CallLogType tempCallLogType = (CallLogType) arrayListObjectCallLogs.get(j);
                                    if (!(((CallLogType) arrayListObjectCallLogs.get(j)).getNumber().equalsIgnoreCase(numberToUnblock))) {
                                    } else {
                                        itemPosition = arrayListObjectCallLogs.indexOf(tempCallLogType);
                                        if (itemPosition != -1) {
                                            tempCallLogType.setBlockedType(AppConstants.UNBLOCK);
                                            arrayListObjectCallLogs.set(itemPosition, tempCallLogType);
                                            callLogListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (int j = 0; j < arrayListObjectCallLogs.size(); j++) {
                            if (arrayListObjectCallLogs.get(j) instanceof CallLogType) {
                                CallLogType tempCallLogType = (CallLogType) arrayListObjectCallLogs.get(j);
                                if (!(((CallLogType) arrayListObjectCallLogs.get(j)).getNumber().equalsIgnoreCase(number))) {
                                } else {
                                    itemPosition = arrayListObjectCallLogs.indexOf(tempCallLogType);
                                    if (itemPosition != -1) {
                                        tempCallLogType.setBlockedType(AppConstants.UNBLOCK);
                                        arrayListObjectCallLogs.set(itemPosition, tempCallLogType);
                                        callLogListAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }
                }

            }

        }
    };

}
