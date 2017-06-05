package com.rawalinfocom.rcontact;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.PhoneBookCallLogs;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

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

    private void init() {
        textPbHeader.setTypeface(Utils.typefaceSemiBold(this));
        rlTitle.setVisibility(View.GONE);
        objectArrayListContact = new ArrayList<>();
        callLogTypeArrayListMain = new ArrayList<>();
        rContactApplication = (RContactApplication) getApplicationContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleViewPbContact.setLayoutManager(linearLayoutManager);
        if (rContactApplication.getArrayListAllPhoneBookContacts() != null)
            objectArrayListContact.addAll(rContactApplication.getArrayListAllPhoneBookContacts());
        getCallLogData();
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


        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (arg0.length() > 0) {
                    Pattern numberPat = Pattern.compile("\\d+");
                    Matcher matcher1 = numberPat.matcher(arg0);
                    if (matcher1.find()) {
                        String text = arg0.toString();
                        allContactAdapter.filter(text);
                        if (allContactAdapter.getSearchCount() == 0) {
                            if(callLogTypeArrayListMain!=null && callLogTypeArrayListMain.size()>0){
                                AppConstants.isFromSearchActivity =  true;
                                simpleCallLogListAdapter =  new SimpleCallLogListAdapter(SearchActivity.this,callLogTypeArrayListMain);
                                simpleCallLogListAdapter.filter(text);
                                rlTitle.setVisibility(View.VISIBLE);
                                recycleViewPbContact.setAdapter(simpleCallLogListAdapter);

                            }
                        }else{
                            rlTitle.setVisibility(View.VISIBLE);
                            recycleViewPbContact.setAdapter(allContactAdapter);
                        }

                    } else {
                        String text = arg0.toString().toLowerCase(Locale.getDefault());
                        allContactAdapter.filter(text);
                        rlTitle.setVisibility(View.VISIBLE);
                        recycleViewPbContact.setAdapter(allContactAdapter);
                    }
                    if (allContactAdapter != null) {
                        int count = allContactAdapter.getSearchCount();
                        if (count > 0) {
                            textSearchCount.setText(count + "");
                        }else{
                            Pattern numberPat1 = Pattern.compile("\\d+");
                            Matcher matcher11 = numberPat1.matcher(arg0);
                            if (matcher11.find()) {
                                if(simpleCallLogListAdapter!=null){
                                    if(simpleCallLogListAdapter.getSearchCount() > 0){
                                        int countOfCallLogs =  simpleCallLogListAdapter.getSearchCount();
                                        if(countOfCallLogs>0)
                                            textSearchCount.setText(countOfCallLogs + "");
                                        ArrayList<CallLogType> tempList =  simpleCallLogListAdapter.getArrayList();
                                        if(tempList!=null && tempList.size()>0){
                                            AppConstants.isFromSearchActivity =  true;
                                            simpleCallLogListAdapter =  new SimpleCallLogListAdapter(SearchActivity.this,
                                                    tempList);
                                            simpleCallLogListAdapter.filter(arg0.toString());
                                            rlTitle.setVisibility(View.VISIBLE);
                                            recycleViewPbContact.setAdapter(simpleCallLogListAdapter);
                                        }

                                    }else{
                                        textSearchCount.setText("");
                                    }
                                }
                            }else{
                                if(allContactAdapter.getSearchCount() == 0){
                                    textSearchCount.setText("");
                                    return;
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

}
