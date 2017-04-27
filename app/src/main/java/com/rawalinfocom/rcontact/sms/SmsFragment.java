package com.rawalinfocom.rcontact.sms;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.SmsListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.SmsDataType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SmsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {


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
    ArrayList<String> arrayListCallLogHeader;
    private ArrayList<SmsDataType> smsDataTypeArrayList;
    LinearLayoutManager mLinearLayoutManager;
    boolean isFromSettings = false;
    int previousIndex = 0;
    SmsListAdapter smsListAdapter;
    RContactApplication rContactApplication;
    private boolean isFirstTime;


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
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public void onPause() {
        super.onPause();
        isFirstTime = false;
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
        }else{
            textGrantPermission.setVisibility(View.GONE);
            recyclerSmsLogs.setVisibility(View.VISIBLE);
            init();
//            getLoaderManager().initLoader(0, null, SmsFragment.this);
            if (isFirstTime) {
                if (AppConstants.isFirstTime()) {
                    AppConstants.setIsFirstTime(false);
                    getLoaderManager().initLoader(0, null, SmsFragment.this);
                }
            }else{
                arrayListObjectSmsLogs = rContactApplication.getArrayListObjectSmsLogs();
                arrayListCallLogHeader =  rContactApplication.getArrayListSmsLogsHeaders();
                if(arrayListCallLogHeader!=null && arrayListCallLogHeader.size()>0 &&
                        arrayListObjectSmsLogs!=null && arrayListObjectSmsLogs.size()>0){
                    setAdapter();
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
                    getLoaderManager().initLoader(0, null, SmsFragment.this);
                }
            }else{
                arrayListObjectSmsLogs = rContactApplication.getArrayListObjectSmsLogs();
                arrayListCallLogHeader =  rContactApplication.getArrayListSmsLogsHeaders();
                if(arrayListCallLogHeader!=null && arrayListCallLogHeader.size()>0
                        && arrayListObjectSmsLogs!=null && arrayListObjectSmsLogs.size()>0){
                    setAdapter();
                }
            }
        } else {
            isFromSettings = true;
            textGrantPermission.setVisibility(View.VISIBLE);
            recyclerSmsLogs.setVisibility(View.GONE);
        }
    }

    @Override
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
        new LoadsSMSInBackground().execute();
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

    private class LoadsSMSInBackground extends AsyncTask<Void, Void, Void> {


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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFromSettings) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission
                    .READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                // Initialize Query to fetch SMS-log
//                progressBar.setVisibility(View.VISIBLE);
                textGrantPermission.setVisibility(View.GONE);
                isFromSettings = false;
                getLoaderManager().initLoader(0, null, SmsFragment.this);

            }
        }else if(isFirstTime){
            if (AppConstants.isFirstTime()) {
                AppConstants.setIsFirstTime(false);
                getLoaderManager().initLoader(0, null, SmsFragment.this);
            }
        }else if(smsListAdapter!=null) {
            SmsDataType smsDataType =  smsListAdapter.getSelectedSmsType();
            int indexPosition = smsListAdapter.getSelectedPosition();
            if(smsDataType!=null && indexPosition >= 0){
                smsDataType.setIsRead("1");
                arrayListObjectSmsLogs.set(indexPosition,smsDataType);
                smsListAdapter.notifyDataSetChanged();
            }
        } else{
            arrayListObjectSmsLogs = rContactApplication.getArrayListObjectSmsLogs();
            arrayListCallLogHeader =  rContactApplication.getArrayListSmsLogsHeaders();
            if(arrayListCallLogHeader!=null && arrayListCallLogHeader.size()>0 &&
                    arrayListObjectSmsLogs!=null && arrayListObjectSmsLogs.size()>0){
                textNoSmsFound.setVisibility(View.VISIBLE);
                setAdapter();
            }else{
                textNoSmsFound.setVisibility(View.VISIBLE);
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
        smsListAdapter =  null;
        AppConstants.setIsFirstTime(true);
    }

    private void init() {
//        progressBar.setVisibility(View.GONE);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerSmsLogs.setLayoutManager(mLinearLayoutManager);
        rContactApplication = (RContactApplication)
                getActivity().getApplicationContext();
        smsDataTypeArrayList = new ArrayList<>();
        arrayListObjectSmsLogs = new ArrayList<>();
        arrayListCallLogHeader = new ArrayList<>();
    }


    private void loadData(Cursor cursor){
        ArrayList<SmsDataType> smsDataTypeList = new ArrayList<>();
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
                        } else {
                            smsDataType.setAddress(formattedNumber);
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

        makeData(smsDataTypeList);
//        setAdapter();
    }

    private void makeData(ArrayList<SmsDataType> filteredList) {
        try{

            if (filteredList != null && filteredList.size() > 0) {
                for (int i = 0; i < filteredList.size(); i++) {
                    SmsDataType callLogType = filteredList.get(i);
                    long logDate1 = callLogType.getDataAndTime();
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
                    String number = callLogType.getThreadId();
                    String finalDate;
                    if (logDate.equalsIgnoreCase(currentDate)) {
                        finalDate = "Today";
                        if (!arrayListObjectSmsLogs.contains(finalDate)) {
                            arrayListCallLogHeader.add(finalDate);
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
                            arrayListCallLogHeader.add(finalDate);
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
                                    String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format
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
                        finalDate = new SimpleDateFormat("dd/MM,EEE").format(date1);
//                    String number = callLogType.getThreadId();
                        if (!arrayListObjectSmsLogs.contains(finalDate)) {
                            arrayListCallLogHeader.add(finalDate);
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
                                    String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format
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
            rContactApplication.setArrayListSmsLogsHeaders(arrayListCallLogHeader);
            rContactApplication.setArrayListObjectSmsLogs(arrayListObjectSmsLogs);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void setAdapter() {
//        progressBar.setVisibility(View.GONE);
        if (arrayListCallLogHeader != null && arrayListObjectSmsLogs != null
                && arrayListCallLogHeader.size() > 0 && arrayListObjectSmsLogs.size() > 0) {
            textNoSmsFound.setVisibility(View.GONE);
            recyclerSmsLogs.setVisibility(View.VISIBLE);
            smsListAdapter = new SmsListAdapter(getActivity(), arrayListObjectSmsLogs,
                    arrayListCallLogHeader);
            recyclerSmsLogs.setAdapter(smsListAdapter);
        } else {
                textNoSmsFound.setVisibility(View.VISIBLE);
        }

    }

    public String getMessageType(int type) {
        switch (type) {
            case Telephony.Sms.MESSAGE_TYPE_DRAFT:
                return "Draft";

            case Telephony.Sms.MESSAGE_TYPE_FAILED:
                return "Failed";

            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                return "Received";

            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                return "Outbox";

            case Telephony.Sms.MESSAGE_TYPE_QUEUED:
                return "Queued";

            case Telephony.Sms.MESSAGE_TYPE_SENT:
                return "Sent";

        }
        return "Other";
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

}
