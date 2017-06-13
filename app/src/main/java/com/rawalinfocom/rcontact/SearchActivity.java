package com.rawalinfocom.rcontact;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.adapters.AllContactAdapter;
import com.rawalinfocom.rcontact.adapters.SimpleCallLogListAdapter;
import com.rawalinfocom.rcontact.adapters.SmsListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.PhoneBookCallLogs;
import com.rawalinfocom.rcontact.database.PhoneBookSMSLogs;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.SmsDataType;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    @BindView(R.id.text_search_count)
    TextView textSearchCount;
    @BindView(R.id.recycle_view_pb_contact)
    RecyclerView recycleViewPbContact;
    @BindView(R.id.text_pb_header)
    TextView textPbHeader;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    ArrayList<Object> objectArrayListContact;
    RContactApplication rContactApplication;
    AllContactAdapter allContactAdapter;
    ArrayList<CallLogType> callLogTypeArrayListMain;
    SimpleCallLogListAdapter simpleCallLogListAdapter;
    MaterialDialog callConfirmationDialog;
    ArrayList<SmsDataType> smsDataTypeArrayList;
    SmsListAdapter smsListAdapter;
    private String[] requiredPermissions = {android.Manifest
            .permission.READ_CALL_LOG, android.Manifest.permission.READ_SMS};
    MaterialDialog permissionConfirmationDialog;
    boolean isHomePressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        init();
        onClickEvents();
        displayData();
    }

    @Override
    protected void onDestroy() {
        AppConstants.isFromSearchActivity = false;
        super.onDestroy();
    }

     @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(isHomePressed){
            isHomePressed = false;
            if(callLogTypeArrayListMain!=null && callLogTypeArrayListMain.size()==0){
                if(rContactApplication.getArrayListCallLogType()!=null && rContactApplication.getArrayListCallLogType().size()>0){
                    callLogTypeArrayListMain.addAll(rContactApplication.getArrayListCallLogType());
                } else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        checkPermissionToExecute(requiredPermissions, AppConstants.READ_LOGS);
                    }else{
                        getCallLogData();
                    }
                }
            }


            if(smsDataTypeArrayList!=null && smsDataTypeArrayList.size()==0){
                if(rContactApplication.getArrayListSmsLogType()!=null && rContactApplication.getArrayListSmsLogType().size()>0){
                    smsDataTypeArrayList.addAll(rContactApplication.getArrayListSmsLogType());
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        checkPermissionToExecute(requiredPermissions, AppConstants.READ_SMS);
                    }else{
                        getSMSData();
                    }
                }
            }
        }
        super.onResume();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppConstants.READ_LOGS && permissions[0].equals(Manifest.permission
                .READ_CALL_LOG) && permissions[1].equals(Manifest.permission.READ_SMS)) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED && grantResults[1] ==
                    PermissionChecker.PERMISSION_GRANTED ) {
                getCallLogData();
                getSMSData();
            } else {
                showPermissionConfirmationDialog();
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String permissions[], int requestCode) {

        boolean logs = ContextCompat.checkSelfPermission(SearchActivity.this, permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean sms = ContextCompat.checkSelfPermission(SearchActivity.this, permissions[1]) !=
                PackageManager.PERMISSION_GRANTED;
        if (logs || sms) {
            requestPermissions(permissions, requestCode);
        } else {
            getCallLogData();
            getSMSData();
        }
    }

    private void showPermissionConfirmationDialog() {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        permissionConfirmationDialog.dismissDialog();
                        finish();
                        break;

                    case R.id.rippleRight:
                        permissionConfirmationDialog.dismissDialog();
                        isHomePressed = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        };

        permissionConfirmationDialog = new MaterialDialog(SearchActivity.this, cancelListener);
        permissionConfirmationDialog.setTitleVisibility(View.GONE);
        permissionConfirmationDialog.setLeftButtonText("Cancel");
        permissionConfirmationDialog.setRightButtonText("OK");
        permissionConfirmationDialog.setDialogBody("Call log permission is required. Do you want " +
                "to try again?");

        permissionConfirmationDialog.showDialog();

    }
    private void init() {
        textPbHeader.setTypeface(Utils.typefaceSemiBold(this));
        rlTitle.setVisibility(View.GONE);
        objectArrayListContact = new ArrayList<>();
        callLogTypeArrayListMain = new ArrayList<>();
        smsDataTypeArrayList = new ArrayList<>();
        rContactApplication = (RContactApplication) getApplicationContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleViewPbContact.setLayoutManager(linearLayoutManager);
        if (rContactApplication.getArrayListAllPhoneBookContacts() != null)
            objectArrayListContact.addAll(rContactApplication.getArrayListAllPhoneBookContacts());

        if(rContactApplication.getArrayListCallLogType()!=null && rContactApplication.getArrayListCallLogType().size()>0){
            callLogTypeArrayListMain.addAll(rContactApplication.getArrayListCallLogType());
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                checkPermissionToExecute(requiredPermissions, AppConstants.READ_LOGS);
            }else{
                getCallLogData();
            }
        }

        if(rContactApplication.getArrayListSmsLogType()!=null && rContactApplication.getArrayListSmsLogType().size()>0){
            smsDataTypeArrayList.addAll(rContactApplication.getArrayListSmsLogType());
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                checkPermissionToExecute(requiredPermissions, AppConstants.READ_SMS);
            }else{
                getSMSData();
            }
        }

    }

    private void onClickEvents() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getText().toString().length() > 0) {
                    search.clearFocus();
                    search.setText("");
                } else {
                    finish();
                    if (!isTaskRoot()) {
                        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                    }
                }
            }
        });
    }

    private void displayData() {
        if (objectArrayListContact != null && objectArrayListContact.size() > 0) {
            allContactAdapter = new AllContactAdapter(SearchActivity.this, objectArrayListContact);
        }

        if(callLogTypeArrayListMain !=null && callLogTypeArrayListMain.size()>0){
            simpleCallLogListAdapter = new SimpleCallLogListAdapter(SearchActivity.this,callLogTypeArrayListMain);
        }

        if(smsDataTypeArrayList != null && smsDataTypeArrayList.size()>0){
            smsListAdapter =  new SmsListAdapter(SearchActivity.this,smsDataTypeArrayList,recycleViewPbContact);
        }

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (arg0.length() > 0) {
                    Pattern numberPat = Pattern.compile("\\d+");
                    Matcher matcher1 = numberPat.matcher(arg0);
                    if (matcher1.find()) {
                        String text = arg0.toString();
                        if(allContactAdapter!=null){
                            allContactAdapter.filter(text);
                            if (allContactAdapter.getSearchCount() == 0) {
                                if (callLogTypeArrayListMain != null && callLogTypeArrayListMain.size() > 0) {
                                    AppConstants.isFromSearchActivity = true;
                                    simpleCallLogListAdapter = new SimpleCallLogListAdapter(SearchActivity.this,
                                            callLogTypeArrayListMain);
                                    simpleCallLogListAdapter.filter(text);
                                    if (simpleCallLogListAdapter.getArrayListCallLogs().size() > 0) {
                                        rlTitle.setVisibility(View.VISIBLE);
                                        recycleViewPbContact.setAdapter(simpleCallLogListAdapter);
                                    } else {
                                        if (simpleCallLogListAdapter.getSearchCount() == 0) {
                                            smsListAdapter = new SmsListAdapter(SearchActivity.this, smsDataTypeArrayList, recycleViewPbContact);
                                            smsListAdapter.filter(text);
                                            rlTitle.setVisibility(View.VISIBLE);
                                            recycleViewPbContact.setAdapter(smsListAdapter);
                                        }
                                    }
                                }
                            } else {
                                rlTitle.setVisibility(View.VISIBLE);
                                recycleViewPbContact.setAdapter(allContactAdapter);
                            }
                        }

                    } else {
                        String text = arg0.toString().toLowerCase(Locale.getDefault());
//                        allContactAdapter.filter(text);
                        if (allContactAdapter != null /*&& allContactAdapter.getSearchCount()>0*/) {
//                            ArrayList<Object> objectArrayList = allContactAdapter.getArrayListUserContact();
                            allContactAdapter = new AllContactAdapter(SearchActivity.this, objectArrayListContact);
                            allContactAdapter.filter(text);
                            int count = allContactAdapter.getSearchCount();
                            if (count > 0) {
                                rlTitle.setVisibility(View.VISIBLE);
                                textSearchCount.setText(count + "");
                            } else {
                                rlTitle.setVisibility(View.GONE);
                            }
                            recycleViewPbContact.setAdapter(allContactAdapter);
                        }
                    }
                    if (allContactAdapter != null) {
                        int count = allContactAdapter.getSearchCount();
                        if (count > 0) {
                            textSearchCount.setText(count + "");
                        } else {
                            Pattern numberPat1 = Pattern.compile("\\d+");
                            Matcher matcher11 = numberPat1.matcher(arg0);
                            if (matcher11.find()) {
                                if (simpleCallLogListAdapter != null) {
                                    if (simpleCallLogListAdapter.getSearchCount() > 0) {
//                                        simpleCallLogListAdapter.filter(arg0.toString());
//                                        ArrayList<CallLogType> tempList = simpleCallLogListAdapter.getArrayList();
                                        if (callLogTypeArrayListMain != null && callLogTypeArrayListMain.size() > 0) {
                                            AppConstants.isFromSearchActivity = true;
                                            simpleCallLogListAdapter = new SimpleCallLogListAdapter(SearchActivity.this,
                                                    callLogTypeArrayListMain);
                                            simpleCallLogListAdapter.filter(arg0.toString());
                                            if (simpleCallLogListAdapter.getArrayListCallLogs().size() > 0) {
                                                int countOfCallLogs = simpleCallLogListAdapter.getSearchCount();
                                                if (countOfCallLogs > 0)
                                                    textSearchCount.setText(countOfCallLogs + "");
                                                rlTitle.setVisibility(View.VISIBLE);
                                                recycleViewPbContact.setAdapter(simpleCallLogListAdapter);
                                            }

                                        }

                                    } else {
                                        if (simpleCallLogListAdapter.getSearchCount() == 0) {
                                            textSearchCount.setText("");
                                            if (smsListAdapter != null && smsListAdapter.getSearchCount() > 0) {
                                                ArrayList<SmsDataType> tempList = smsListAdapter.getArrayList();
                                                if (tempList != null && tempList.size() > 0) {
                                                    smsListAdapter = new SmsListAdapter(SearchActivity.this,
                                                            tempList, recycleViewPbContact);
                                                    smsListAdapter.filter(arg0.toString());
                                                    if(smsListAdapter.getTypeArrayList().size()>0){
                                                        int countOfSmsLogs = smsListAdapter.getSearchCount();
                                                        if (countOfSmsLogs > 0) {
                                                            textSearchCount.setText(countOfSmsLogs + "");
                                                        }
                                                        rlTitle.setVisibility(View.VISIBLE);
                                                        recycleViewPbContact.setAdapter(smsListAdapter);
                                                    }else{
                                                        rlTitle.setVisibility(View.GONE);
                                                        textSearchCount.setText("");
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (allContactAdapter.getSearchCount() == 0) {
                                    textSearchCount.setText("");
//                                    return;
                                    AppConstants.isFromSearchActivity = true;

                                    if(smsDataTypeArrayList!=null){
                                        smsListAdapter = new SmsListAdapter(SearchActivity.this,
                                                smsDataTypeArrayList, recycleViewPbContact);
                                        smsListAdapter.filter(arg0.toString());
                                        if (smsListAdapter.getSearchCount() > 0) {
                                            if(smsListAdapter.getTypeArrayList().size()>0){
                                                rlTitle.setVisibility(View.VISIBLE);
                                                int smsCount = smsListAdapter.getSearchCount();
                                                if (smsCount > 0)
                                                    textSearchCount.setText(smsCount+"");
                                                recycleViewPbContact.setAdapter(smsListAdapter);
                                            }else{
                                                rlTitle.setVisibility(View.GONE);
                                                textSearchCount.setText("");
                                            }

                                        } else {
                                            rlTitle.setVisibility(View.GONE);
                                            textSearchCount.setText("");
                                        }
                                    }


                                    /*if (smsListAdapter.getSearchCount() == 0) {
                                        textSearchCount.setText("");
                                    } else {
                                        if (smsListAdapter != null && smsListAdapter.getSearchCount() > 0) {
                                            int countOfSmsLogs = smsListAdapter.getSearchCount();
                                            if (countOfSmsLogs > 0) {
                                                textSearchCount.setText(countOfSmsLogs + "");
                                            }
                                            ArrayList<SmsDataType> tempList = smsListAdapter.getArrayList();
                                            if (tempList != null && tempList.size() > 0) {
                                                smsListAdapter = new SmsListAdapter(SearchActivity.this,
                                                        tempList, recycleViewPbContact);
                                                smsListAdapter.filter(arg0.toString());
                                                rlTitle.setVisibility(View.VISIBLE);
                                                recycleViewPbContact.setAdapter(smsListAdapter);
                                            }
                                        }
                                    }*/
                                }
                            }

                        }
                    }

                }

                if (arg0.length() == 0) {
                    recycleViewPbContact.setAdapter(null);
                    rlTitle.setVisibility(View.GONE);
                    textSearchCount.setText("");
                }

                initSwipe();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void getCallLogData() {
        getCallLogsByRawId();
    }

    private void getCallLogsByRawId() {

        ArrayList<String> callLogsIdsList = new ArrayList<>();
        PhoneBookCallLogs phoneBookCallLogs = new PhoneBookCallLogs(this);
        callLogsIdsList = new ArrayList<>();
        Cursor cursor = phoneBookCallLogs.getAllCallLogId();
        if (cursor != null) {
            int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
            while (cursor.moveToNext()) {
                callLogsIdsList.add(cursor.getString(rowId));
            }
        }
        cursor.close();

        if (callLogsIdsList != null && callLogsIdsList.size() > 0) {
            fetchCallLogsFromIds(callLogsIdsList);
        }
    }

    private void fetchCallLogsFromIds(ArrayList<String> listOfRowIds) {
        try {
            for (int i = 0; i < listOfRowIds.size(); i++) {
                String uniqueCallLogId = listOfRowIds.get(i);
                if (!TextUtils.isEmpty(uniqueCallLogId)) {
                    String order = CallLog.Calls.DATE + " ASC";
                    Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
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
                            CallLogType log = new CallLogType(this);
                            log.setNumber(cursor.getString(number));
                            String userName = cursor.getString(name);
                            if (!TextUtils.isEmpty(userName))
                                log.setName(userName);
                            else
                                log.setName("");
                            if (TextUtils.isEmpty(userName)) {
                                log.setType(cursor.getInt(type));
                                log.setDuration(cursor.getInt(duration));
                                log.setDate(cursor.getLong(date));
                                log.setUniqueContactId(cursor.getString(rowId));
                                String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                                log.setNumberType(numberTypeLog);
                                String userNumber = cursor.getString(number);
                                String uniquePhoneBookId = getStarredStatusFromNumber(userNumber);
                                if (!TextUtils.isEmpty(uniquePhoneBookId))
                                    log.setLocalPbRowId(uniquePhoneBookId);
                                else
                                    log.setLocalPbRowId(" ");

                                callLogTypeArrayListMain.add(log);
//                                rContactApplication.setArrayListCallLogType(callLogTypeArrayListMain);
                            }
                        }
                        cursor.close();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getPhoneNumberType(int type) {
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

    private String getStarredStatusFromNumber(String phoneNumber) {
        String numberId = "";
        try {

            numberId = "";
            ContentResolver contentResolver = this.getContentResolver();

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
                String actionNumber ="";
                if(allContactAdapter!=null && simpleCallLogListAdapter!=null){
                    if (allContactAdapter.getSearchCount() == 0) {
                        if(simpleCallLogListAdapter.getSearchCount() == 0){
                            actionNumber = StringUtils.defaultString(((SmsListAdapter
                                    .SMSViewHolder) viewHolder).textNumber.getText()
                                    .toString());
                            Pattern numberPat = Pattern.compile("\\d+");
                            Matcher matcher1 = numberPat.matcher(actionNumber);
                            if (matcher1.find()) {
                                numberToSend = actionNumber;
                            } else {
                                numberToSend =  getNumberFromName(actionNumber);
                                if(TextUtils.isEmpty(numberToSend)){
                                    numberToSend = actionNumber;
                                }
                            }
                        }else{
                            numberToSend = StringUtils.defaultString(((SimpleCallLogListAdapter
                                    .CallLogViewHolder) viewHolder).textTempNumber.getText()
                                    .toString());
                        }

                    }else{
                        numberToSend = StringUtils.defaultString(((AllContactAdapter
                                .AllContactViewHolder) viewHolder).textContactNumber.getText()
                                .toString());
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
                    if (allContactAdapter != null && simpleCallLogListAdapter != null) {
                        if (allContactAdapter.getSearchCount() == 0) {
                            if (simpleCallLogListAdapter.getSearchCount() == 0) {
                                showCallConfirmationDialog(numberToSend, actionNumber);
                            }else{
                                showCallConfirmationDialog(numberToSend);
                            }
                        }else{
                            showCallConfirmationDialog(numberToSend);
                        }
                    }
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (allContactAdapter != null && simpleCallLogListAdapter != null) {
                            if (allContactAdapter.getSearchCount() == 0) {
                                if(simpleCallLogListAdapter.getSearchCount() == 0){
                                        if(smsListAdapter != null && smsListAdapter.getSearchCount()>0)
                                            smsListAdapter.notifyDataSetChanged();
                                }else{
                                    if (simpleCallLogListAdapter != null && simpleCallLogListAdapter.getSearchCount() > 0) {
                                        simpleCallLogListAdapter.notifyDataSetChanged();
                                    }
                                }

                            } else {
                                allContactAdapter.notifyDataSetChanged();
                            }
                        }
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
                        p.setColor(ContextCompat.getColor(SearchActivity.this, R.color
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
                        p.setColor(ContextCompat.getColor(SearchActivity.this, R.color.brightOrange));
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
        itemTouchHelper.attachToRecyclerView(recycleViewPbContact);
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
        } else {
        }

        callConfirmationDialog = new MaterialDialog(SearchActivity.this, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + name + "?");
        callConfirmationDialog.showDialog();

    }

    private String  getNumberFromName(String name) {
        String number =  "";
//        Cursor cursor = null;
        try {
           /* Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(name));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};*/

            Cursor cursor =
                    getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +" = ?",
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

    private void showCallConfirmationDialog(final String number) {
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


        /*Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(name);
        if (matcher1.find()) {
            name = number;
        } else {
        }*/

        callConfirmationDialog = new MaterialDialog(this, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + number + "?");
        callConfirmationDialog.showDialog();

    }

    private void getSMSData() {
        getSMSLogIdById();
    }

    private void getSMSLogIdById() {
        ArrayList<String> listOfIds = new ArrayList<>();
        PhoneBookSMSLogs phoneBookSmsLogs = new PhoneBookSMSLogs(this);
        listOfIds = new ArrayList<>();
        Cursor cursor = phoneBookSmsLogs.getAllSMSLogId();
        if (cursor != null) {
            int rowId = cursor.getColumnIndex(Telephony.Sms._ID);
            while (cursor.moveToNext()) {
                listOfIds.add(cursor.getString(rowId));
            }
        }
        if (listOfIds != null && listOfIds.size() > 0)
            fetchSMSDataById(listOfIds);

    }

    private void fetchSMSDataById(ArrayList<String> listOfRowIds) {

        try {
            ArrayList<SmsDataType> smsDataTypeList = new ArrayList<>();
            for (int i = 0; i < listOfRowIds.size(); i++) {
                String uniqueCallLogId = listOfRowIds.get(i);
                if (!TextUtils.isEmpty(uniqueCallLogId)) {
                    String order = Telephony.Sms.DEFAULT_SORT_ORDER;
                    Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI,
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
                                        final String formattedNumber = Utils.getFormattedNumber(this, address);
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
//            rContactApplication.setArrayListSmsLogType(smsDataTypeArrayList);

        } else {
        }


    }


    private String getContactNameFromNumber(String phoneNumber) {
        String contactName = "";
        try {

            ContentResolver contentResolver = getContentResolver();

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
            ContentResolver contentResolver = getContentResolver();

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


}
