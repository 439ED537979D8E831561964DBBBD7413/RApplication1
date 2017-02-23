package com.rawalinfocom.rcontact.calllog;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
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
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.CallLogListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.model.CallLogType;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogFragment extends BaseFragment {

    private Runnable logsRunnable;
    private String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int READ_LOGS = 725;
    public static final int INCOMING_CALLS = 672;
    public static final int OUTGOING_CALLS = 609;
    public static final int MISSED_CALLS = 874;
    public static final int ALL_CALLS = 814;
    public static final int INCOMING = 1;
    public static final int OUTGOING = 2;
    public static final int MISSED = 3;

    public  static  CallLogType callLogTypeReceiver ;


    public CallLogFragment() {
        // Required empty public constructor
    }


    public static CallLogFragment newInstance() {
        return new CallLogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callLogTypeReceiver = new CallLogType();
    }

    @Override
    public void getFragmentArguments() {

    }


    private Spinner spinnerCallFilter;
    private RecyclerView recyclerCallLogs;
    private CallLogListAdapter callLogListAdapter;
    ArrayList<Object> arrayListObjectCallLogs;
    ArrayList<String> arrayListCallLogHeader;
    ArrayList<CallLogType> arrayListCallLogs;
    ArrayList<CallLogType> arrayListCallLogsHistroy;
    MaterialDialog callConfirmationDialog;
    String selectedcallType = "";
    View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_call_log, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        telephonyInit();

        /*myContentObserver = new MyContentObserver(handler);
        handler =  new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().getApplicationContext()
                        .getContentResolver()
                        .registerContentObserver(
                                CallLog.Calls.CONTENT_URI, true,
                                myContentObserver);

            }
        },0);*/
      /*  getActivity().getApplicationContext()
                .getContentResolver()
                .registerContentObserver(
                        CallLog.Calls.CONTENT_URI, true,
                        new MyContentObserver(handler));*/

    }

    private void init() {
        spinnerCallFilter = (Spinner) mainView.findViewById(R.id.spinner_call_filter);
        recyclerCallLogs = (RecyclerView) mainView.findViewById(R.id.recycler_call_logs);
        initSpinner();

        arrayListCallLogs = new ArrayList<>();
        arrayListCallLogHeader = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();

        logsRunnable = new Runnable() {
            @Override
            public void run() {
//                loadLogs("All Calls");
            }
        };

        // Checking for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionToExecute(requiredPermissions, READ_LOGS, logsRunnable);
        } else {
            logsRunnable.run();
        }

    }

    private void initSpinner() {
        final List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("All Calls");
        spinnerArray.add("Incoming Calls");
        spinnerArray.add("Outgoing Calls");
        spinnerArray.add("Missed Calls");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCallFilter.setAdapter(adapter);

        spinnerCallFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getSelectedItem().toString();
                if (!TextUtils.isEmpty(value)) {
                    Log.i("callType", value);
                    selectedcallType = value;
                    loadLogs(value);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    // This is to be run only when READ_CONTACTS and READ_CALL_LOG permission are granted
    private void loadLogs(String callType) {

        List<CallLogType> callLogs = null;
        arrayListCallLogs = new ArrayList<>();
        arrayListCallLogHeader = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();
        arrayListCallLogsHistroy = new ArrayList<>();

        if (callType.equalsIgnoreCase("Missed Calls")) {
            callLogs = getLogsbyCallType(MISSED_CALLS);
        } else if (callType.equalsIgnoreCase("Incoming Calls")) {
            callLogs = getLogsbyCallType(INCOMING_CALLS);
        } else if (callType.equalsIgnoreCase("Outgoing Calls")) {
            callLogs = getLogsbyCallType(OUTGOING_CALLS);
        } else {
            callLogs = getLogsbyCallType(ALL_CALLS);
        }

        // To show recent call on top
//        Collections.reverse(callLogs);
        List<String> listOfDates = new ArrayList<>();
        if (callLogs != null && callLogs.size() > 0)
            for (int i = 0; i < callLogs.size(); i++) {
                CallLogType callLogType  = callLogs.get(i);
                long logDate1 = callLogType.getDate();
                Date date1 = new Date(logDate1);
                String logDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                Log.i("Call Log date", logDate);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date yesDate = new Date();
                yesDate = cal.getTime();
                String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd").format(yesDate);
                Log.i("Call yesterday date", yesterdayDate);

                Calendar c = Calendar.getInstance();
                Date cDate = c.getTime();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                Log.i("Call Current date", currentDate);

                String finalDate = "";
                if (logDate.equalsIgnoreCase(currentDate)) {
                    finalDate = "Today";
                    listOfDates.add(finalDate);
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }
                    arrayListObjectCallLogs.add(callLogType);

                } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
                    finalDate = "Yesterday";
                    listOfDates.add(finalDate);
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }
                    arrayListObjectCallLogs.add(callLogType);

                } else {
                    finalDate = new SimpleDateFormat("dd/MM,EEE").format(date1);
                    listOfDates.add(finalDate);
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }
                    arrayListObjectCallLogs.add(callLogType);
                }

            }
        /*arrayListCallLogHeader.addAll(listOfDates);
        Log.i("Call Logs size", callLogs.size()+"");
        Log.i("Header List size", arrayListCallLogHeader.size()+"");*/

        setAdapter();
        initSwipe();
    }

    private void setAdapter() {
        if (arrayListCallLogHeader != null && arrayListObjectCallLogs != null
                && arrayListCallLogHeader.size() > 0 && arrayListObjectCallLogs.size() > 0) {
            callLogListAdapter = new CallLogListAdapter(CallLogFragment.this, arrayListObjectCallLogs, arrayListCallLogHeader);
            recyclerCallLogs.setAdapter(callLogListAdapter);
            setRecyclerViewLayoutManager(recyclerCallLogs);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }


    private List<CallLogType> getLogsbyCallType(int callType) {
        ArrayList logs = new ArrayList();
        String order = CallLog.Calls.DATE + " DESC";
        try {
            String selection;
            switch (callType) {
                case 609:
                    selection = "type = 2";
                    break;
                case 672:
                    selection = "type = 1";
                    break;
                case 814:
                    selection = null;
                default:
                    selection = null;
                    break;
                case 874:
                    selection = "type = 3";
            }

            Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, (String[]) null, selection, (String[]) null, order);
            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int name =  cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
            int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);

//            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                CallLogType log = new CallLogType(getActivity());
                log.setNumber(cursor.getString(number));
                log.setName(cursor.getString(name));
                log.setType(cursor.getInt(type));
                log.setDuration(cursor.getInt(duration));
                log.setDate(cursor.getLong(date));
                log.setUniqueContactId(cursor.getString(rowId));
                String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                Log.i("Number Type", numberTypeLog + " of number " + cursor.getString(number));
                Log.i("Number Log Type", getLogType(cursor.getInt(type)) + " of number " + cursor.getString(number));
                log.setNumberType(numberTypeLog);


                ArrayList<CallLogType> arrayListHistroy = callLogHistroy(cursor.getString(number));
//                arrayListCallLogsHistroy.addAll(arrayListHistroy);
                int logCount = arrayListHistroy.size();
                log.setHistroyLogCount(logCount);
                Log.i("Histroy size ", logCount + "" + " of " + cursor.getString(number));
                Log.i("Histroy", "----------------------------------");

                for (int i = 0; i < arrayListHistroy.size(); i++) {
                    String simNumber = arrayListHistroy.get(i).getHistroyCallSimNumber();
                    log.setCallSimNumber(simNumber);
                }

                logs.add(log);
            }
            cursor.close();

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public String getLogType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "Missed call";
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
                        .AllCallLogViewHolder) viewHolder).textContactName.getText()
                        .toString());
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                  /*  smsIntent.setData(Uri.parse("sms:" + ((ProfileData)
                            arrayListPhoneBookContacts.get(position)).getOperation().get(0)
                            .getPbPhoneNumber().get(0).getPhoneNumber()));*/
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


    private void showCallConfirmationDialog(final String number) {

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

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + number + "?");
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

//        telephonyInfo.fetchClassInfo();
        isDualSIM = telephonyInfo.isDualSIM();
        AppConstants.setIsDualSimPhone(isDualSIM);
        Log.i("IsDual", isDualSIM + "");
        Log.i("SIM1 ready", isSIM1Ready + "");
        Log.i("SIM2 ready", isSIM2Ready + "");

        /*TextView tv = (TextView) findViewById(R.id.tv);
        tv.setText(" IME1 : " + imsiSIM1 + "\n" +
                " IME2 : " + imsiSIM2 + "\n" +
                " IS DUAL SIM : " + isDualSIM + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n");*/


    }


    // A method to check if a permission is granted then execute tasks depending on that particular permission
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String permissions[], int requestCode, Runnable runnable) {

        boolean logs = ContextCompat.checkSelfPermission(getActivity(), permissions[0]) != PackageManager.PERMISSION_GRANTED;
        boolean contacts = ContextCompat.checkSelfPermission(getActivity(), permissions[1]) != PackageManager.PERMISSION_GRANTED;
        boolean location = ContextCompat.checkSelfPermission(getActivity(), permissions[2]) != PackageManager.PERMISSION_GRANTED;
        if (logs || contacts || location) {
            requestPermissions(permissions, requestCode);
        } else {
            runnable.run();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_LOGS && permissions[0].equals(Manifest.permission.READ_CALL_LOG) && permissions[1].equals(Manifest.permission.READ_CONTACTS)
                && permissions[2].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED && grantResults[1] == PermissionChecker.PERMISSION_GRANTED &&
                    grantResults[2] == PermissionChecker.PERMISSION_GRANTED) {
                logsRunnable.run();
            } else {
                new AlertDialog.Builder(getActivity())
                        .setMessage("The app needs these permissions to work, Exit?")
                        .setTitle("Permission Denied")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkPermissionToExecute(requiredPermissions, READ_LOGS, logsRunnable);
                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        }).show();
            }
        }
    }


    private void simSlotDetection(String number) {
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = getActivity().getContentResolver().query(allCalls, null, CallLog.Calls.NUMBER + " =?", new String[]{number}, null);
        if (c != null && c.getCount() > 0) {
            String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            Log.i("Sim slot id", id + " of number " + number);
        }
        c.close();

    }

    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistroyData(String number) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " =?", new String[]{number}, order);
//            }else {
//                cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, "NUMBER=" + number, null, order);
//            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }


    private ArrayList callLogHistroy(String number) {
        String numberToSearch = number;
        ArrayList<CallLogType> callDetails = new ArrayList<>();
        Cursor cursor = getCallHistroyData(numberToSearch);
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
                    account = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME);//for versions above lollipop
                    account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                    profileImage = cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);
                } else {
//                        account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                }
                while (cursor.moveToNext()) {
                    String phNum = cursor.getString(number1);
                    int callType = Integer.parseInt(cursor.getString(type));
                    String callDate = cursor.getString(date);
                    long dateOfCall = Long.parseLong(callDate);
                    String callDuration = cursor.getString(duration);
                    String accountId = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        accountId = cursor.getString(account_id);
                        if (!TextUtils.isEmpty(accountId) && account_id > 0)
                            Log.e("Sim Type", accountId);
                        String accountName = cursor.getString(account);
                        if (!TextUtils.isEmpty(accountName))
                            Log.e("Sim Name", accountName);
                        String userImage = cursor.getString(profileImage);
                        if (userImage != null)
                            Log.e("User Image", userImage);
                    }
                    int histroyId = Integer.parseInt(cursor.getString(callLogId));
                    CallLogType logObject = new CallLogType();
                    logObject.setHistroyNumber(phNum);
                    logObject.setHistroyType(callType);
                    logObject.setHistroyDate(dateOfCall);
                    logObject.setHistroydDuration(Integer.parseInt(callDuration));
                    logObject.setHistroyCallSimNumber(accountId);
                    logObject.setHistroyId(histroyId);
                    callDetails.add(logObject);
                }
            }
            cursor.close();

//            simSlotDetection(number);


        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return callDetails;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
        try{
            if(AppConstants.isFromReceiver){
                ArrayList<CallLogType> arrayListHistroy = callLogHistroy(callLogTypeReceiver.getNumber());
                int count =  arrayListHistroy.size();
                callLogTypeReceiver.setHistroyLogCount(count);
                String receiverDate =  "Today";
                if (!arrayListObjectCallLogs.contains(receiverDate)) {
                    arrayListCallLogHeader.add(0,receiverDate);
                    arrayListObjectCallLogs.add(0,receiverDate);
                    callLogListAdapter.notifyItemInserted(0);
                    recyclerCallLogs.scrollToPosition(0);
                }

                arrayListObjectCallLogs.add(1,callLogTypeReceiver);
                callLogListAdapter.notifyItemInserted(1);

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }



    @Override
    public void onPause() {
        super.onPause();
//        getActivity().unregisterReceiver(broadcastReceiver);
        AppConstants.isFromReceiver = false;
    }

    @Override
    public void onDestroy() {
//        getActivity().unregisterReceiver(broadcastReceiver);
//        getActivity().getApplicationContext()
//                .getContentResolver().unregisterContentObserver(myContentObserver);
        super.onDestroy();

    }


   /* class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler h) {
            super(h);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d("Call Logs", "MyContentObserver.onChange("+selfChange+")");
            super.onChange(selfChange);
            // here you call the method to fill the list
            String order = CallLog.Calls.DATE + " DESC";
            try{

                Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, (String[]) null, null, null, order);
                int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);

                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    CallLogType log = new CallLogType(getActivity());
                    log.setNumber(cursor.getString(number));
                    log.setType(cursor.getInt(type));
                    log.setDuration(cursor.getInt(duration));
                    log.setDate(cursor.getLong(date));
                    log.setUniqueContactId(cursor.getString(rowId));
                    String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                    Log.i("Number Type", numberTypeLog + " of number " + cursor.getString(number));
                    Log.i("Number Log Type", getLogType(cursor.getInt(type)) + " of number " + cursor.getString(number));
                    log.setNumberType(numberTypeLog);


                    ArrayList<CallLogType> arrayListHistroy = callLogHistroy(cursor.getString(number));
//                arrayListCallLogsHistroy.addAll(arrayListHistroy);
                    int logCount = arrayListHistroy.size();
                    log.setHistroyLogCount(logCount);
                    Log.i("Histroy size ", logCount + "" + " of " + cursor.getString(number));
                    Log.i("Histroy", "----------------------------------");

                    for (int i = 0; i < arrayListHistroy.size(); i++) {
                        String simNumber = arrayListHistroy.get(i).getHistroyCallSimNumber();
                        log.setCallSimNumber(simNumber);
                    }

                    arrayListObjectCallLogs.add(log);
                    setAdapter();
                }
                cursor.close();

            }catch (SecurityException e){
                e.printStackTrace();
            }


        }
    }*/


   /* private int lastState = TelephonyManager.CALL_STATE_IDLE;
    private Date callStartTime;
    private boolean isIncoming;
    private String savedNumber;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("Broadcast Received", "CallLogFragment");
            try {
                if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                    savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                } else {
                    String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                    String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    int state = 0;
                    if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        state = TelephonyManager.CALL_STATE_IDLE;
                    } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        state = TelephonyManager.CALL_STATE_OFFHOOK;
                    } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        state = TelephonyManager.CALL_STATE_RINGING;
                    }
                    onCallStateChanged(context, state, number);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
    }


    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }*/

}
