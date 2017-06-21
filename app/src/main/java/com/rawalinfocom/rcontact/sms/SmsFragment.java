package com.rawalinfocom.rcontact.sms;


import android.Manifest;
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
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.SimpleCallLogListAdapter;
import com.rawalinfocom.rcontact.adapters.SmsListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.PhoneBookSMSLogs;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.listener.OnLoadMoreListener;
import com.rawalinfocom.rcontact.model.SmsDataType;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SmsFragment extends BaseFragment /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {


    @BindView(R.id.recycler_sms_logs)
    RecyclerView recyclerSmsLogs;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.text_grant_permission)
    TextView textGrantPermission;
    Unbinder unbinder;
    @BindView(R.id.text_no_sms_found)
    TextView textNoSmsFound;
    private String[] requiredPermissions = {Manifest.permission.READ_SMS};
    ArrayList<Object> arrayListObjectSmsLogs;
    ArrayList<String> arrayListSmsLogHeader;
    private ArrayList<SmsDataType> smsDataTypeArrayList;
    LinearLayoutManager mLinearLayoutManager;
    boolean isFromSettings = false;
    int previousIndex = 0;
    //    SmsListDateWiseAdapter smsListAdapter;
    SmsListAdapter smsListAdapter;
    RContactApplication rContactApplication;
    private boolean isFirstTime;
    private int LIST_PARTITION_COUNT = 10;
    boolean isFirstChuck = false;
    int count = 0;
    int logsDisplayed = 0;
    boolean isLastRecord = false;
    private static int firstVisibleInListview;
    ArrayList<String> listOfIds;
    ArrayList<String> smsLogIdsListByChunck;
    public static SmsDataType smsDataTypeReceiver;
    private MaterialDialog callConfirmationDialog;

    public SmsFragment() {
        // Required empty public constructor
    }

    public static SmsFragment newInstance() {
        return new SmsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.getBooleanPreference(getActivity(), AppConstants
                .PREF_SMS_LOG_STARTS_FIRST_TIME, true)) {
            isFirstTime = true;
            Utils.setBooleanPreference(getActivity(), AppConstants
                    .PREF_SMS_LOG_STARTS_FIRST_TIME, false);
        }

        if (!AppConstants.isFirstTime())
            AppConstants.setIsFirstTime(true);

        Utils.setBooleanPreference(getActivity(), AppConstants.PREF_RECENT_SMS_BROADCAST_RECEIVER_MAIN_INSTANCE, false);
        Utils.setBooleanPreference(getActivity(), AppConstants.PREF_RECENT_CALLS_BROADCAST_RECEIVER_CALL_LOG_TAB, false);

        registerLocalBroadcast();
        smsDataTypeReceiver = new SmsDataType();
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public void onPause() {
        super.onPause();
        isFirstTime = false;
//        AppConstants.isComposingSMS = true;
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sms, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionToExecute();
        } else {
            textGrantPermission.setVisibility(View.GONE);
            recyclerSmsLogs.setVisibility(View.VISIBLE);
            init();
//            getLoaderManager().initLoader(0, null, SmsFragment.this);
            if (isFirstTime) {
                if (AppConstants.isFirstTime()) {
                    AppConstants.setIsFirstTime(false);
//                    getLoaderManager().initLoader(0, null, SmsFragment.this);
                    loadData();
                }
            } else {
                 /*arrayListObjectSmsLogs = rContactApplication.getArrayListObjectSmsLogs();
                arrayListSmsLogHeader =  rContactApplication.getArrayListSmsLogsHeaders();
                if(arrayListSmsLogHeader!=null && arrayListSmsLogHeader.size()>0
                        && arrayListObjectSmsLogs!=null && arrayListObjectSmsLogs.size()>0)*/
                smsDataTypeArrayList = rContactApplication.getArrayListSmsLogType();
                if (smsDataTypeArrayList != null && smsDataTypeArrayList.size() > 0) {
                    setAdapter();
                } else {
//                    getLoaderManager().initLoader(0, null, SmsFragment.this);
                    loadData();
                }
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute() {
        boolean smsLogs = ContextCompat.checkSelfPermission(getActivity(),
                requiredPermissions[0]) ==
                PackageManager.PERMISSION_GRANTED;
        if (smsLogs) {
            textGrantPermission.setVisibility(View.GONE);
            recyclerSmsLogs.setVisibility(View.VISIBLE);
            init();
//            progressBar.setVisibility(View.VISIBLE);
            if (isFirstTime) {
                if (AppConstants.isFirstTime()) {
                    AppConstants.setIsFirstTime(false);
//                    getLoaderManager().initLoader(0, null, SmsFragment.this);
                    loadData();
                }
            } else {
                /*arrayListObjectSmsLogs = rContactApplication.getArrayListObjectSmsLogs();
                arrayListSmsLogHeader =  rContactApplication.getArrayListSmsLogsHeaders();
                if(arrayListSmsLogHeader!=null && arrayListSmsLogHeader.size()>0
                        && arrayListObjectSmsLogs!=null && arrayListObjectSmsLogs.size()>0)*/
                smsDataTypeArrayList = rContactApplication.getArrayListSmsLogType();
                if (smsDataTypeArrayList != null && smsDataTypeArrayList.size() > 0) {
                    setAdapter();
                } else {
//                    getLoaderManager().initLoader(0, null, SmsFragment.this);
                    loadData();
                }
            }

        } else {
            isFromSettings = true;
            textGrantPermission.setVisibility(View.VISIBLE);
            recyclerSmsLogs.setVisibility(View.GONE);
        }
    }

  /*  @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String order = Telephony.Sms.DEFAULT_SORT_ORDER;
        Uri uri = Telephony.Sms.CONTENT_URI;

        return new CursorLoader(getActivity(), uri, null, null, null, order);
    }

    Cursor cursorMain;
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        loadData(cursor);
        cursorMain=  cursor;
//        new LoadsSMSInBackground().execute();
        loadData(cursorMain);

    }


    @Override
    public void onLoaderReset(Loader loader) {

    }*/

    /*private class LoadsSMSInBackground extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.showProgressDialog(getActivity(),"Please wait",false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(cursorMain.getCount() > 0){
                loadData(cursorMain);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Utils.hideProgressDialog();
            setAdapter();
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (AppConstants.isFromReceiver) {
            AppConstants.isRecentCallFromSMSTab = true;
        }
        if (isFromSettings) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission
                    .READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                // Initialize Query to fetch SMS-log
//                progressBar.setVisibility(View.VISIBLE);
                textGrantPermission.setVisibility(View.GONE);
                isFromSettings = false;
//                getLoaderManager().initLoader(0, null, SmsFragment.this);
                loadData();
            }
        } else if (isFirstTime) {
            if (AppConstants.isFirstTime()) {
                AppConstants.setIsFirstTime(false);
                loadData();
//                getLoaderManager().initLoader(0, null, SmsFragment.this);
            }
        } else if (smsListAdapter != null) {
            if (AppConstants.isComposingSMS) {
                AppConstants.isComposingSMS = false;
                PhoneBookSMSLogs phoneBookSmsLogs = new PhoneBookSMSLogs(getActivity());
                listOfIds = new ArrayList<>();
                Cursor cursor = phoneBookSmsLogs.getAllSMSLogId();
                if (cursor != null) {
                    int rowId = cursor.getColumnIndex(Telephony.Sms._ID);
                    while (cursor.moveToNext()) {
                        listOfIds.add(cursor.getString(rowId));
                    }
                }
                cursor.close();
                Utils.setArrayListPreference(getActivity(), AppConstants.PREF_SMS_LOGS_ID_SET,
                        listOfIds);
                smsDataTypeArrayList = null;
                logsDisplayed = 0;
                smsListAdapter = null;
                count = 0;
              /*  Utils.setBooleanPreference(getActivity(), AppConstants
                        .PREF_SMS_LOG_STARTS_FIRST_TIME, true);*/
                AppConstants.setIsFirstTime(true);
                loadData();

            } else {
                SmsDataType smsDataType = smsListAdapter.getSelectedSmsType();
                int indexPosition = smsListAdapter.getSelectedPosition();
                if (smsDataType != null && indexPosition >= 0) {
                    smsDataType.setIsRead("1");
                    smsDataTypeArrayList.set(indexPosition, smsDataType);
                    smsListAdapter.notifyDataSetChanged();
                }
            }

        } else {
            /*arrayListObjectSmsLogs = rContactApplication.getArrayListObjectSmsLogs();
            arrayListSmsLogHeader =  rContactApplication.getArrayListSmsLogsHeaders();
            if(arrayListSmsLogHeader!=null && arrayListSmsLogHeader.size()>0 &&
                    arrayListObjectSmsLogs!=null && arrayListObjectSmsLogs.size()>0)*/
            smsDataTypeArrayList = rContactApplication.getArrayListSmsLogType();
            if (smsDataTypeArrayList != null && smsDataTypeArrayList.size() > 0) {
                textNoSmsFound.setVisibility(View.GONE);
                setAdapter();
            } else {
                loadData();
//                getLoaderManager().initLoader(0, null, SmsFragment.this);
//                textNoSmsFound.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterLocalBroadcast();
        smsListAdapter = null;
        logsDisplayed = 0;
        AppConstants.setIsFirstTime(true);

    }

    private void registerLocalBroadcast() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance
                (getActivity());
        IntentFilter intentFilter = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_SMS_RECEIVER);
        localBroadcastManager.registerReceiver(localBroadcastSmsReceiver, intentFilter);

        LocalBroadcastManager localBroadcastManagerDeleteSMS = LocalBroadcastManager.getInstance
                (getActivity());
        IntentFilter intentFilter1 = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_DELETE_SMS_RECEIVER);
        localBroadcastManagerDeleteSMS.registerReceiver(localBroadcastReceiverDeleteSMS, intentFilter1);
    }

    private void unRegisterLocalBroadcast() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance
                (getActivity());
        localBroadcastManager.unregisterReceiver(localBroadcastSmsReceiver);

        LocalBroadcastManager localBroadcastManagerDeleteSMS = LocalBroadcastManager.getInstance
                (getActivity());
        localBroadcastManagerDeleteSMS.unregisterReceiver(localBroadcastReceiverDeleteSMS);
    }

    private void init() {
//        progressBar.setVisibility(View.GONE);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerSmsLogs.setLayoutManager(mLinearLayoutManager);
        rContactApplication = (RContactApplication)
                getActivity().getApplicationContext();
        smsDataTypeArrayList = new ArrayList<>();
        arrayListObjectSmsLogs = new ArrayList<>();
        arrayListSmsLogHeader = new ArrayList<>();
        listOfIds = new ArrayList<>();
        PhoneBookSMSLogs phoneBookSmsLogs = new PhoneBookSMSLogs(getActivity());
        Cursor cursor = phoneBookSmsLogs.getAllSMSLogId();
        if (cursor != null) {
            int rowId = cursor.getColumnIndex(Telephony.Sms._ID);
            while (cursor.moveToNext()) {
                listOfIds.add(cursor.getString(rowId));
            }
        }

        cursor.close();
        Utils.setArrayListPreference(getActivity(), AppConstants.PREF_SMS_LOGS_ID_SET,
                listOfIds);
    }

    private ArrayList<String> divideSMSLogIdsByChunck() {
        int size = listOfIds.size();
        smsLogIdsListByChunck = new ArrayList<>();
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
            smsLogIdsListByChunck.addAll(partition);
            listOfIds.removeAll(partition);
            break;
        }
        return smsLogIdsListByChunck;
    }


    private void loadData() {
        ArrayList<String> listOfIds = Utils.getArrayListPreference(getActivity(), AppConstants
                .PREF_SMS_LOGS_ID_SET);
        if (listOfIds == null) {
            PhoneBookSMSLogs phoneBookSmsLogs = new PhoneBookSMSLogs(getActivity());
            listOfIds = new ArrayList<>();
            Cursor cursor = phoneBookSmsLogs.getAllSMSLogId();
            if (cursor != null) {
                int rowId = cursor.getColumnIndex(Telephony.Sms._ID);
                while (cursor.moveToNext()) {
                    listOfIds.add(cursor.getString(rowId));
                }
            }
            cursor.close();
            Utils.setArrayListPreference(getActivity(), AppConstants.PREF_SMS_LOGS_ID_SET,
                    listOfIds);
        } else {
            if (listOfIds != null && listOfIds.size() > 0) {
                textNoSmsFound.setVisibility(View.GONE);
                int indexToBeginSync = logsDisplayed;
                ArrayList<String> tempIdsList = new ArrayList<>();
                for (int i = indexToBeginSync; i < listOfIds.size(); i++) {
                    String ids = listOfIds.get(i);
                    tempIdsList.add(ids);
                }

                if (isFirstTime) {
                    LIST_PARTITION_COUNT = 20;
                } else {
                    LIST_PARTITION_COUNT = 20;
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
                        fetchSMSDataById(partition);
                        break;
                    }
                } else {
//                    fetchSMSDataById(tempIdsList);
                    if (tempIdsList.size() <= 0)
                        fetchSMSDataById(listOfIds);
                    else {
                        fetchSMSDataById(tempIdsList);

                    }
                }

            } else {
                textNoSmsFound.setVisibility(View.VISIBLE);
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

    private void fetchSMSDataById(ArrayList<String> listOfRowIds) {

        try {
            ArrayList<SmsDataType> smsDataTypeList = new ArrayList<>();
            for (int i = 0; i < listOfRowIds.size(); i++) {
                String uniqueCallLogId = listOfRowIds.get(i);
                if (!TextUtils.isEmpty(uniqueCallLogId)) {
                    String order = Telephony.Sms.DEFAULT_SORT_ORDER;
                    Cursor cursor = getActivity().getContentResolver().query(Telephony.Sms.CONTENT_URI,
                            null, Telephony.Sms._ID + " = " + uniqueCallLogId, null, order);

                    if (cursor != null) {
                        if (cursor != null && cursor.getCount() > 0) {
                            int number = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS);
                            int id = cursor.getColumnIndexOrThrow(Telephony.Sms._ID);
                            int body = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY);
                            int date = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE);
                            int read = cursor.getColumnIndexOrThrow(Telephony.Sms.READ);
                            int type = cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE);
                            int thread_id = cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID);
                            while (cursor.moveToNext()) {
                                SmsDataType smsDataType = new SmsDataType();
                                String address = cursor.getString(number);
                                String contactNumber = "";
                                if (!TextUtils.isEmpty(address)) {
                                    Pattern numberPat = Pattern.compile("[a-zA-Z]+");
                                    Matcher matcher1 = numberPat.matcher(address);
                                    if (matcher1.find()) {
                                        smsDataType.setAddress(address);
                                    } else {
                                        // Todo: Add format number method before setting the address
                                        final String formattedNumber = Utils.getFormattedNumber(getActivity(), address);
                                        String contactName = getContactNameFromNumber(formattedNumber);
                                        if (!TextUtils.isEmpty(contactName)) {
                                            smsDataType.setAddress(contactName);
                                            smsDataType.setNumber(formattedNumber);
                                        } else {
                                            smsDataType.setAddress(formattedNumber);
                                            smsDataType.setNumber(formattedNumber);
                                        }
                                        contactNumber = formattedNumber;
                                    }
                                    smsDataType.setBody(cursor.getString(body));
                                    smsDataType.setDataAndTime(cursor.getLong(date));
                                    smsDataType.setIsRead(cursor.getString(read));
                                    smsDataType.setUniqueRowId(cursor.getString(id));
                                    smsDataType.setThreadId(cursor.getString(thread_id));
                                    String smsType = getMessageType(cursor.getInt(type));
                                    smsDataType.setTypeOfMessage(smsType);
                                    smsDataType.setFlag(11);
                                    String photoThumbNail = getPhotoUrlFromNumber(contactNumber);
                                    if (!TextUtils.isEmpty(photoThumbNail)) {
                                        smsDataType.setProfileImage(photoThumbNail);
                                    } else {
                                        smsDataType.setProfileImage("");
                                    }
                                    smsDataTypeList.add(smsDataType);
                                }

                            }
                            cursor.close();

                        }

                    }
                }
            }
            makeSimpleDataThreadWise(smsDataTypeList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void makeSimpleDataThreadWise(ArrayList<SmsDataType> filteredList) {
        if (filteredList != null && filteredList.size() > 0) {
            recyclerSmsLogs.setVisibility(View.VISIBLE);
            textNoSmsFound.setVisibility(View.GONE);
            if (smsDataTypeArrayList == null) {
                smsDataTypeArrayList = new ArrayList<>();
            }
            for (int k = 0; k < filteredList.size(); k++) {
                SmsDataType smsDataType = filteredList.get(k);
                String threadId = smsDataType.getThreadId();
                if (smsDataTypeArrayList.size() == 0) {
                    smsDataTypeArrayList.add(smsDataType);

                } else {
                    boolean isNumberExists = false;
                    for (int j = 0; j < smsDataTypeArrayList.size(); j++) {
                        if (smsDataTypeArrayList.get(j) instanceof SmsDataType) {
                            if (!((smsDataTypeArrayList.get(j))
                                    .getThreadId().equalsIgnoreCase(threadId))) {
                                isNumberExists = false;
                            } else {
                                isNumberExists = true;
                                break;
                            }
                        }
                    }
                    if (!isNumberExists) {
                        smsDataTypeArrayList.add(smsDataType);
                    }
                }
            }

            rContactApplication.setArrayListSmsLogType(smsDataTypeArrayList);
            if (smsListAdapter == null) {
//                llLoading.setVisibility(View.GONE);
                setAdapter();
            } else {
//                llLoading.setVisibility(View.GONE);
                smsListAdapter.notifyDataSetChanged();
            }

        } else {
            recyclerSmsLogs.setVisibility(View.GONE);
            textNoSmsFound.setVisibility(View.VISIBLE);
        }


    }

    private void makeData(ArrayList<SmsDataType> filteredList) {
        try {

            if (filteredList != null && filteredList.size() > 0) {
                for (int i = 0; i < filteredList.size(); i++) {
                    SmsDataType callLogType = filteredList.get(i);
                    long logDate1 = callLogType.getDataAndTime();
                    Date date1 = new Date(logDate1);
                    String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date1);
                    Log.i("Call Log date", logDate);

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                    Date yesDate;
                    yesDate = cal.getTime();
                    String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesDate);
                    Log.i("Call yesterday date", yesterdayDate);

                    Calendar c = Calendar.getInstance();
                    Date cDate = c.getTime();
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cDate);
                    Log.i("Call Current date", currentDate);
                    String number = callLogType.getThreadId();
                    String finalDate;
                    if (logDate.equalsIgnoreCase(currentDate)) {
                        finalDate = "Today";
                        if (!arrayListObjectSmsLogs.contains(finalDate)) {
                            arrayListSmsLogHeader.add(finalDate);
                            arrayListObjectSmsLogs.add(finalDate);
                        }
                        if (arrayListObjectSmsLogs.size() == 1) {
                            arrayListObjectSmsLogs.add(callLogType);
                        } else {
                            boolean isNumberExists = false;
                            for (int j = 0; j < arrayListObjectSmsLogs.size(); j++) {
                                if (arrayListObjectSmsLogs.get(j) instanceof SmsDataType) {
                                    if (!(((SmsDataType) arrayListObjectSmsLogs.get(j)).getThreadId()
                                            .equalsIgnoreCase(number))) {
                                        isNumberExists = false;
                                    } else {
                                        isNumberExists = true;
                                        break;
                                    }
                                }
                            }
                            if (!isNumberExists) {
                                arrayListObjectSmsLogs.add(callLogType);
                            }
                        }

                    } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
                        finalDate = "Yesterday";
                        if (!arrayListObjectSmsLogs.contains(finalDate)) {
                            arrayListSmsLogHeader.add(finalDate);
                            arrayListObjectSmsLogs.add(finalDate);
                        }

//                    String number = callLogType.getThreadId();
                        if (arrayListObjectSmsLogs.size() == 1) {
                            arrayListObjectSmsLogs.add(callLogType);
                        } else {
                            boolean isNumberExists = false;
                            for (int j = 0; j < arrayListObjectSmsLogs.size(); j++) {
                                if (arrayListObjectSmsLogs.get(j) instanceof SmsDataType) {
                                    long objDate = ((SmsDataType) arrayListObjectSmsLogs.get(j))
                                            .getDataAndTime();
                                    Date objDate1 = new Date(objDate);
                                    String arrayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format
                                            (objDate1);
//                                if (arrayDate.equalsIgnoreCase(logDate)) {
                                    if (!(((SmsDataType) arrayListObjectSmsLogs.get(j))
                                            .getThreadId().equalsIgnoreCase(number))) {
                                        isNumberExists = false;
                                    } else {
                                        isNumberExists = true;
                                        break;
                                    }
//                                }

                                }
                            }
                            if (!isNumberExists) {
                                arrayListObjectSmsLogs.add(callLogType);
                            }
                        }
                    } else {
                        finalDate = new SimpleDateFormat("dd/MM,EEE", Locale.getDefault()).format(date1);
//                    String number = callLogType.getThreadId();
                        if (!arrayListObjectSmsLogs.contains(finalDate)) {
                            arrayListSmsLogHeader.add(finalDate);
                            arrayListObjectSmsLogs.add(finalDate);
                        }

                        if (arrayListObjectSmsLogs.size() == 1) {
                            arrayListObjectSmsLogs.add(callLogType);
                        } else {
                            boolean isNumberExists = false;
                            for (int j = 0; j < arrayListObjectSmsLogs.size(); j++) {
                                if (arrayListObjectSmsLogs.get(j) instanceof SmsDataType) {
                                    long objDate = ((SmsDataType) arrayListObjectSmsLogs.get(j))
                                            .getDataAndTime();
                                    Date objDate1 = new Date(objDate);
                                    String arrayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format
                                            (objDate1);
//                                if (arrayDate.equalsIgnoreCase(logDate)) {
                                    if (!(((SmsDataType) arrayListObjectSmsLogs.get(j))
                                            .getThreadId().equalsIgnoreCase(number))) {
                                        isNumberExists = false;
                                    } else {
                                        isNumberExists = true;
                                        break;
                                    }
//                                }

                                }
                            }
                            if (!isNumberExists) {
                                arrayListObjectSmsLogs.add(callLogType);
                            }
                        }
                    }
                }
            }
//            rContactApplication.setArrayListSmsLogsHeaders(arrayListSmsLogHeader);
            rContactApplication.setArrayListObjectSmsLogs(arrayListObjectSmsLogs);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setAdapter() {
//        progressBar.setVisibility(View.GONE);
       /* if (arrayListSmsLogHeader != null && arrayListObjectSmsLogs != null
                && arrayListSmsLogHeader.size() > 0 && arrayListObjectSmsLogs.size() > 0) {
            textNoSmsFound.setVisibility(View.GONE);
            recyclerSmsLogs.setVisibility(View.VISIBLE);
            smsListAdapter = new SmsListDateWiseAdapter(getActivity(), arrayListObjectSmsLogs,
                    arrayListSmsLogHeader);
            recyclerSmsLogs.setAdapter(smsListAdapter);
        } else {
                textNoSmsFound.setVisibility(View.VISIBLE);
        }*/

//        progressBar.setVisibility(View.GONE);
        if (smsDataTypeArrayList != null && smsDataTypeArrayList.size() > 0) {
            smsListAdapter = new SmsListAdapter(getActivity(), smsDataTypeArrayList, recyclerSmsLogs);
            recyclerSmsLogs.setAdapter(smsListAdapter);
            recyclerSmsLogs.setFocusable(false);
        }
        initSwipe();

        smsListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                smsDataTypeArrayList.add(null);
                smsListAdapter.notifyItemInserted(smsDataTypeArrayList.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");
                        try {
                            //Remove loading item
                            if (smsDataTypeArrayList != null && smsListAdapter != null) {
                                smsDataTypeArrayList.remove(smsDataTypeArrayList.size() - 1);
                                smsListAdapter.notifyItemRemoved(smsDataTypeArrayList.size());
                            }
                            //Load data
                            if (!isLastRecord) {
                                if (isFirstTime) {
//                                llLoading.setVisibility(View.VISIBLE);
                                    ArrayList<String> smsLogIdsArrayList = divideSMSLogIdsByChunck();
                                    if (smsLogIdsArrayList != null && smsLogIdsArrayList.size() > 0) {
                                        logsDisplayed = logsDisplayed + smsLogIdsArrayList.size();
                                        loadData();
                                    } else {
                                    /*Utils.showSuccessSnackBar(getActivity(), linearCallLogMain, "Last" +
                                            " log shown.");*/
//                                    llLoading.setVisibility(View.GONE);
                                        isLastRecord = true;
//                                        smsListAdapter.setMoreData(true);
                                    }
                                } else {
                                    if (!isLastRecord) {
//                                    llLoading.setVisibility(View.VISIBLE);
                                        ArrayList<String> smsLogIdsArrayList = divideSMSLogIdsByChunck();
                                        if (smsLogIdsArrayList != null && smsLogIdsArrayList.size() > 0) {
                                            logsDisplayed = logsDisplayed + smsLogIdsArrayList.size();
                                            loadData();
                                        } else {
                                        /*Utils.showSuccessSnackBar(getActivity(), linearCallLogMain,
                                                "Last log shown.");*/
//                                        llLoading.setVisibility(View.GONE);
                                            isLastRecord = true;
//                                          smsListAdapter.setMoreData(true);
                                        }
                                    }
                                }
                            }

                            smsListAdapter.notifyDataSetChanged();
                            if (!isLastRecord)
                                smsListAdapter.setLoaded();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, 200);
            }
        });

    }

    public String getMessageType(int type) {
        switch (type) {

            case Telephony.Sms.MESSAGE_TYPE_DRAFT:
                return getActivity().getString(R.string.msg_draft);

            case Telephony.Sms.MESSAGE_TYPE_FAILED:
                return getActivity().getString(R.string.msg_failed);

            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                return getActivity().getString(R.string.msg_received);

            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                return getActivity().getString(R.string.msg_outbox);

            case Telephony.Sms.MESSAGE_TYPE_QUEUED:
                return getActivity().getString(R.string.msg_queued);

            case Telephony.Sms.MESSAGE_TYPE_SENT:
                return getActivity().getString(R.string.msg_sent);

        }
        return getActivity().getString(R.string.type_other);
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


    private String getContactNameFromNumber(String phoneNumber) {
        String contactName = "";
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

    private BroadcastReceiver localBroadcastSmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("SmsFragment", "onReceive() of LocalBroadcast");
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (smsDataTypeReceiver != null) {
                            boolean isRecordPresent = false;
                            SmsDataType smsDataTypeFromReceiver = new SmsDataType();
                            String address = smsDataTypeReceiver.getAddress();
                            String body = smsDataTypeReceiver.getBody();
                            long dateAndTime = smsDataTypeReceiver.getDataAndTime();
                            Log.i("Sms Receiver details", address + " " + body);
                            if (smsDataTypeArrayList != null && smsDataTypeArrayList.size() > 0) {
                                for (int i = 0; i < smsDataTypeArrayList.size(); i++) {
                                    SmsDataType smsDataType = smsDataTypeArrayList.get(i);
                                    if (smsDataType != null) {
                                        String add = smsDataType.getAddress();
                                        if (address.equalsIgnoreCase(add)) {
                                            isRecordPresent = true;
                                            String contactName = getContactNameFromNumber(address);
                                            if (!TextUtils.isEmpty(contactName)) {
                                                smsDataType.setAddress(contactName);
                                            } else {
                                                smsDataType.setAddress(address);
                                            }
                                            smsDataType.setBody(body);
                                            smsDataType.setDataAndTime(dateAndTime);
                                            smsDataType.setIsRead("0");
                                            smsDataType.setUniqueRowId(smsDataType.getUniqueRowId());
                                            smsDataType.setThreadId(smsDataType.getThreadId());
                                            String smsType = getMessageType(1);
                                            smsDataType.setTypeOfMessage(smsType);
                                            smsDataType.setFlag(11);
                                            String photoThumbNail = getPhotoUrlFromNumber(address);
                                            if (!TextUtils.isEmpty(photoThumbNail)) {
                                                smsDataType.setProfileImage(photoThumbNail);
                                            } else {
                                                smsDataType.setProfileImage("");
                                            }

                                            smsDataTypeArrayList.set(0, smsDataType);
                                            rContactApplication.setArrayListSmsLogType(smsDataTypeArrayList);
                                            if (smsListAdapter != null) {
                                                smsListAdapter.notifyDataSetChanged();
                                            } else {
                                                setAdapter();
                                            }
                                            recyclerSmsLogs.scrollToPosition(0);
                                            break;
                                        }
                                    }
                                }
                                if (!isRecordPresent) {
                                    smsDataTypeFromReceiver.setAddress(address);
                                    smsDataTypeFromReceiver.setBody(body);
                                    smsDataTypeFromReceiver.setDataAndTime(dateAndTime);
                                    smsDataTypeFromReceiver.setIsRead("0");
                                    String smsType = getMessageType(1);
                                    smsDataTypeFromReceiver.setTypeOfMessage(smsType);
                                    smsDataTypeFromReceiver.setFlag(11);
                                    String photoThumbNail = getPhotoUrlFromNumber(address);
                                    if (!TextUtils.isEmpty(photoThumbNail)) {
                                        smsDataTypeFromReceiver.setProfileImage(photoThumbNail);
                                    } else {
                                        smsDataTypeFromReceiver.setProfileImage("");
                                    }

                                    PhoneBookSMSLogs phoneBookSmsLogs = new PhoneBookSMSLogs(getActivity());
                                    Cursor cursor = phoneBookSmsLogs.getAllSMSLogId();
                                    String id = "";
                                    if (cursor != null) {
                                        int rowId = cursor.getColumnIndex(Telephony.Sms._ID);
                                        while (cursor.moveToNext()) {
                                            id = cursor.getString(rowId);
                                            smsDataTypeFromReceiver.setUniqueRowId(id);
                                            break;
                                        }
                                        cursor.close();
                                    }

                                    if (!TextUtils.isEmpty(id)) {
                                        listOfIds.add(0, id);
                                        Utils.setArrayListPreference(getActivity(), AppConstants.PREF_SMS_LOGS_ID_SET,
                                                listOfIds);
                                    }

                                    smsDataTypeArrayList.add(0, smsDataTypeFromReceiver);
                                    rContactApplication.setArrayListSmsLogType(smsDataTypeArrayList);
                                    /*if(smsListAdapter!= null){
                                        smsListAdapter.notifyItemInserted(0);
                                    }else{
                                        setAdapter();
                                    }*/
                                    setAdapter();
                                    recyclerSmsLogs.scrollToPosition(0);
                                }

                            }
                        }

                    }
                }, 1000);


            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    };


    private BroadcastReceiver localBroadcastReceiverDeleteSMS = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("SmsFragment", "onReceive() of LocalBroadcast");
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (smsListAdapter != null) {
                            int itemIndexToRemove = smsListAdapter.getSelectedPosition();
                            SmsDataType smsDataToUpdate = smsListAdapter
                                    .getSelectedSmsType();
                            smsDataTypeArrayList.remove(smsDataToUpdate);
                            rContactApplication.setArrayListSmsLogType(smsDataTypeArrayList);
                            String idToRemove = smsDataToUpdate.getUniqueRowId();
                            listOfIds.remove(idToRemove);
                            Utils.setArrayListPreference(getActivity(), AppConstants.PREF_SMS_LOGS_ID_SET,
                                    listOfIds);
                            smsListAdapter.notifyItemRemoved(itemIndexToRemove);
                        }
                    }
                }, 1000);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


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
                String numberToSend = "";
                String actionNumber = StringUtils.defaultString(((SmsListAdapter
                        .SMSViewHolder) viewHolder).textNumber.getText()
                        .toString());
                Pattern numberPat = Pattern.compile("\\d+");
                Matcher matcher1 = numberPat.matcher(actionNumber);
                if (matcher1.find()) {
                    numberToSend = actionNumber;
                } else {
                    numberToSend = getNumberFromName(actionNumber);
                    if (TextUtils.isEmpty(numberToSend)) {
                        numberToSend = actionNumber;
                    }
                }

                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + numberToSend));
                    startActivity(smsIntent);

                } else {
                    showCallConfirmationDialog(numberToSend, actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (smsListAdapter != null)
                            smsListAdapter.notifyDataSetChanged();
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
                /*if (viewHolder instanceof SimpleCallLogListAdapter.CallLogViewHolder) {
                    return 0;
                }*/
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
        itemTouchHelper.attachToRecyclerView(recyclerSmsLogs);
    }


    private String getNumberFromName(String name) {
        String number = "";
//        Cursor cursor = null;
        try {
           /* Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(name));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};*/

            Cursor cursor =
                    getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                            new String[]{name}, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    number = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.CommonDataKinds.Phone.NUMBER));

                }
                cursor.close();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return number;
    }

    private void showCallConfirmationDialog(final String number, String name) {
//        final String formattedNumber = Utils.getFormattedNumber(getActivity(), number);
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
                                number));
                        startActivity(intent);
                        break;
                }
            }
        };

        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(name);
        if (matcher1.find()) {
            name = number;
        }

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(getActivity().getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(getActivity().getString(R.string.action_call));
        callConfirmationDialog.setDialogBody(getActivity().getString(R.string.action_call) + " " + name + "?");
        callConfirmationDialog.showDialog();
    }
}
