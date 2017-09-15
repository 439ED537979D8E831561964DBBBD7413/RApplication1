package com.rawalinfocom.rcontact.calllog;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.Toast;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.SearchActivity;
import com.rawalinfocom.rcontact.adapters.SimpleCallLogListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.PhoneBookCallLogs;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.database.TableSpamDetailMaster;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RecyclerItemClickListener;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.WrapContentLinearLayoutManager;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.SpamDataType;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogFragment extends BaseFragment implements WsResponseListener, RippleView
        .OnRippleCompleteListener/*, LoaderManager.LoaderCallbacks<Cursor>*/, SimpleCallLogListAdapter.SimpleCallLogListAdapterListener {

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
    //    private int LIST_PARTITION_COUNT = 10;
    RContactApplication rContactApplication;
    MaterialDialog permissionConfirmationDialog;

    boolean isCallLogFragment = false;
    private boolean isResumeCalled = false;

    private PhoneBookCallLogs phoneBookCallLogs;
    private TableProfileMaster tableProfileMaster;
    private TableProfileMobileMapping tableProfileMobileMapping;
    private GetRCPNameAndProfileImage nameAndProfileImage;
    boolean isFromDeleteBroadcast = false;
    private UpdateOldLogWithUpdatedDetails updateOldLogWithUpdatedDetails;
    Toolbar toolbar;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

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
        toolbar =  getMainActivity().getToolbar();

    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = getMainActivity().startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (simpleCallLogListAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        }else{

            CallLogType selectedCallLogData = callLogTypeArrayList.get(position);
            String key = "";
            key = selectedCallLogData.getLocalPbRowId();
            if (key.equalsIgnoreCase(" ")) {
                key = selectedCallLogData.getUniqueContactId();
            }

            Boolean isRcpUser = selectedCallLogData.isRcpUser();
            String firstName = selectedCallLogData.getRcpFirstName();
            String lastName = selectedCallLogData.getRcpLastName();
            String name = selectedCallLogData.getName();
            String cloudName = "";
            String contactDisplayName = "";
            String contactNameToDisplay = "";
            String prefix = selectedCallLogData.getPrefix();
            String suffix = selectedCallLogData.getSuffix();
            String middleName = selectedCallLogData.getMiddleName();

            if (StringUtils.length(prefix) > 0)
                contactNameToDisplay = contactNameToDisplay + prefix + " ";
            if (StringUtils.length(suffix) > 0)
                contactNameToDisplay = contactNameToDisplay + suffix + " ";
            if (StringUtils.length(firstName) > 0)
                contactNameToDisplay = contactNameToDisplay + firstName + " ";
            if (StringUtils.length(middleName) > 0)
                contactNameToDisplay = contactNameToDisplay + middleName + " ";
            if (StringUtils.length(lastName) > 0)
                contactNameToDisplay = contactNameToDisplay + lastName + "";


            if (MoreObjects.firstNonNull(isRcpUser, false)) {
                if (StringUtils.length(firstName) > 0) {
                    contactDisplayName = contactDisplayName + firstName + " ";
                }
                if (StringUtils.length(lastName) > 0) {
                    contactDisplayName = contactDisplayName + lastName + "";
                }
                if (!StringUtils.equalsIgnoreCase(name, contactDisplayName)) {
                    cloudName = contactDisplayName;
                }
            }
            long date =  selectedCallLogData.getDate();
            long selectedLogDate, dateFromReceiver = 0;
            Date dateFromReceiver1 = selectedCallLogData.getCallReceiverDate();
            if (dateFromReceiver1 != null) {
                dateFromReceiver = dateFromReceiver1.getTime();
            }
            if (date == 0) {
                selectedLogDate = dateFromReceiver;
            } else {
                selectedLogDate = date;
            }
            AppConstants.isFromReceiver = false;
//                String formatedNumber = Utils.getFormattedNumber(mActivity, number);
            Intent intent = new Intent(getActivity(), ProfileDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, true);
            intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER, selectedCallLogData.getNumber());

            if (selectedCallLogData.getRcpId() == null)
                intent.putExtra(AppConstants.EXTRA_PM_ID, "-1");
            else
                intent.putExtra(AppConstants.EXTRA_PM_ID, selectedCallLogData.getRcpId());

            intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NAME, name);
            if (date == 0) {
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, dateFromReceiver);
            } else {
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, date);
            }
            intent.putExtra(AppConstants.EXTRA_RCP_VERIFIED_ID, selectedCallLogData.getIsRcpVerfied());
            intent.putExtra(AppConstants.EXTRA_CALL_UNIQUE_ID, key);
            intent.putExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID, selectedCallLogData.getUniqueContactId());
            intent.putExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE, selectedCallLogData.getProfileImage());
            intent.putExtra(AppConstants.EXTRA_IS_RCP_USER, isRcpUser);
            if (!StringUtils.isEmpty(cloudName))
                intent.putExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME, cloudName);
            else
                intent.putExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME, contactNameToDisplay);
            getActivity().startActivity(intent);
            (getActivity()).overridePendingTransition(R.anim.enter, R.anim.exit);

        }

    }

    @Override
    public void onRowLongClicked(int position) {
        enableActionMode(position);
    }


    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = getMainActivity().startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        simpleCallLogListAdapter.toggleSelection(position);
        int count = simpleCallLogListAdapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count) + " selected");
            actionMode.invalidate();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            toolbar.setVisibility(View.GONE);
            mode.getMenuInflater().inflate(R.menu.menu_action_mode_call_log, menu);
            // disable swipe refresh if action mode is enabled
//            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            toolbar.setVisibility(View.VISIBLE);
            simpleCallLogListAdapter.clearSelections();
//            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerCallLogs.post(new Runnable() {
                @Override
                public void run() {
                    simpleCallLogListAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        simpleCallLogListAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                simpleCallLogListAdapter.getSelectedItems();
        /*for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
//            simpleCallLogListAdapter.removeData(selectedItemPositions.get(i));
            int itemIndexToRemove =  selectedItemPositions.get(i);
            callLogTypeArrayList.remove(itemIndexToRemove);
            rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
            simpleCallLogListAdapter.resetCurrentIndex();
            simpleCallLogListAdapter.notifyItemRemoved(itemIndexToRemove);
            simpleCallLogListAdapter.notifyItemRangeChanged(itemIndexToRemove,
                    simpleCallLogListAdapter.getItemCount());
        }*/

        ArrayList<CallLogType> listToDelete = simpleCallLogListAdapter.getArrayListToDelete();
        if (listToDelete.size() > 0) {
            for (int j = 0; j < listToDelete.size(); j++) {
                CallLogType callLogType = listToDelete.get(j);
                String number = callLogType.getNumber();
                long dateAndTime = callLogType.getDate();
                // delete operation
                String where = CallLog.Calls.NUMBER + " =?" + " AND " + CallLog.Calls.DATE + " =?";
                String[] selectionArguments = new String[]{number, String.valueOf(dateAndTime)};
                int value = getActivity().getContentResolver().delete(CallLog.Calls.CONTENT_URI, where,
                        selectionArguments);
                if (value > 0) {
                    callLogTypeArrayList.remove(callLogType);
                    rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                    simpleCallLogListAdapter.resetCurrentIndex();
                }
            }
            simpleCallLogListAdapter.notifyDataSetChanged();
        }
//        simpleCallLogListAdapter.notifyDataSetChanged();
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
        actionModeCallback = new ActionModeCallback();
        // Checking for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionToExecute(requiredPermissions, AppConstants.READ_LOGS);
        } else {
            initSpinner();
        }
//        telephonyInit();
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


            if (simpleCallLogListAdapter != null) {
                String phoneBookName = getNameFromNumber(simpleCallLogListAdapter.getSelectedCallLogData().getNumber());
                if (StringUtils.length(phoneBookName) > 0) {
                    String number = simpleCallLogListAdapter.getSelectedCallLogData().getNumber();
                    for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                        CallLogType callLogType = callLogTypeArrayList.get(i);
                        String numberToUpdate = callLogType.getNumber();
                        if (numberToUpdate.equalsIgnoreCase(number)) {
                            callLogType.setName(phoneBookName);
                            callLogTypeArrayList.set(i, callLogType);
                            rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                            simpleCallLogListAdapter.notifyDataSetChanged();
                            //simpleCallLogListAdapter.notifyItemRangeChanged(itemIndexToUpdate, simpleCallLogListAdapter.getItemCount());
                        }
                    }
                } else {
                    String number = simpleCallLogListAdapter.getSelectedCallLogData().getNumber();
                    for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                        CallLogType callLogType = callLogTypeArrayList.get(i);
                        String numberToUpdate = callLogType.getNumber();
                        if (numberToUpdate.equalsIgnoreCase(number)) {
                            callLogType.setName("");
                            callLogType.setNumber(Utils.getFormattedNumber(getActivity(), number));
                            callLogType = setRCPDetailsAndSpamCountforUnsavedNumbers(number, callLogType);
                            callLogTypeArrayList.set(i, callLogType);
                            rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                            simpleCallLogListAdapter.notifyDataSetChanged();
//                            simpleCallLogListAdapter.notifyItemRangeChanged(itemIndexToUpdate, simpleCallLogListAdapter.getItemCount());
                        }
                    }
                }
            }

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
//        AppConstants.isFromReceiver = false;

        if (nameAndProfileImage != null) {
            nameAndProfileImage.cancel(true);
            getDatabaseHandler().close();
        }

        if (updateOldLogWithUpdatedDetails != null) {
            updateOldLogWithUpdatedDetails.cancel(true);
            getDatabaseHandler().close();
        }

        LocalBroadcastManager localBroadcastManagerTabChange = LocalBroadcastManager.getInstance
                (getActivity());
        localBroadcastManagerTabChange.unregisterReceiver(localBroadcastReceiverTabChange);
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
//        try {
//            if (error == null) {
//                if (serviceType.equalsIgnoreCase(WsConstants.REQ_UPLOAD_CALL_LOGS)) {
//                    WsResponseObject callLogInsertionResponse = (WsResponseObject) data;
//                    if (callLogInsertionResponse != null && StringUtils.equalsIgnoreCase
//                            (callLogInsertionResponse
//                                    .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
//
//                        Utils.setStringPreference(getActivity(), AppConstants
// .PREF_CALL_LOG_SYNC_TIME, callLogInsertionResponse.getCallDateAndTime());
//                        Utils.setStringPreference(getActivity(), AppConstants
// .PREF_CALL_LOG_ROW_ID, callLogInsertionResponse.getCallLogRowId());
//                        Utils.setBooleanPreference(getActivity(), AppConstants
// .PREF_CALL_LOG_SYNCED, true);
//
//                    } else {
//                        if (callLogInsertionResponse != null) {
//                            Log.e("error response", callLogInsertionResponse.getMessage());
//                            Utils.showErrorSnackBar(getActivity(), linearMainContent,
// callLogInsertionResponse.getMessage());
//                        } else {
//                            Log.e("onDeliveryResponse: ", "userProfileResponse null");
//                            Utils.showErrorSnackBar(getActivity(), linearMainContent, getString(R
//                                    .string.msg_try_later));
//                        }
//                    }
//
//                } else {
//                    Utils.showErrorSnackBar(getActivity(), linearMainContent, "" + error
//                            .getLocalizedMessage());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

            if (StringUtils.isEmpty(name)) {
                name = getNameFromNumber(Utils.getFormattedNumber(getActivity(), number));
            }
//            String formattedNumber =  Utils.getFormattedNumber(getActivity(), number);

            Long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

            final ArrayList<CallLogType> arrayListHistory = callLogHistory(number);

            callLogType.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));

            if (!StringUtils.isEmpty(number))
                callLogType.setNumber(number);
//            else
//                callLogType.setNumber("");

            if (!StringUtils.isEmpty(name))
                callLogType.setName(name);
//            else
//                callLogType.setName("");

            callLogType.setDurationToPass(callLogType.getCoolDuration(Float.parseFloat
                    (cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)))));

            String photoThumbNail = getPhotoUrlFromNumber(Utils.getFormattedNumber(getActivity(),
                    number));

            if (!TextUtils.isEmpty(photoThumbNail)) {
                callLogType.setProfileImage(photoThumbNail);
            } else {
                callLogType.setProfileImage("");
            }

            callLogType.setUniqueContactId(rowId);
            String uniquePhoneBookId = getStarredStatusFromNumber(Utils.getFormattedNumber
                    (getActivity(), number));
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
//                    callLogType.setDurationToPass(callLogType1.getDurationToPass());
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
                            tableProfileMobileMapping.getCloudPmIdFromProfileMappingFromNumber
                                    (number);

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

                            callLogType = setRCPDetailsAndSpamCountforUnsavedNumbers(number,
                                    callLogType);
                            callLogTypeArrayList.add(0, callLogType);
                            updateOldLogWithUpdatedDetails = new UpdateOldLogWithUpdatedDetails();
                            updateOldLogWithUpdatedDetails.execute(callLogType);
//                            rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                        }
                    } else {
                        callLogType = setRCPDetailsAndSpamCountforUnsavedNumbers(number,
                                callLogType);
                        callLogTypeArrayList.add(0, callLogType);
                        updateOldLogWithUpdatedDetails = new UpdateOldLogWithUpdatedDetails();
                        updateOldLogWithUpdatedDetails.execute(callLogType);
//                        rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                    }
                } else {
                    callLogType = setRCPDetailsAndSpamCountforUnsavedNumbers(number, callLogType);
                    callLogTypeArrayList.add(0, callLogType);
                    updateOldLogWithUpdatedDetails = new UpdateOldLogWithUpdatedDetails();
                    updateOldLogWithUpdatedDetails.execute(callLogType);
//                    rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                }

                if (simpleCallLogListAdapter != null) {
                    simpleCallLogListAdapter.notifyItemInserted(0);
                    simpleCallLogListAdapter.notifyDataSetChanged();
                }
            }

            recyclerCallLogs.smoothScrollToPosition(0);

//            ArrayList<CallLogType> callLogTypeArrayList = new ArrayList<>();
//            callLogTypeArrayList.add(callLogType);
//            if (Utils.getBooleanPreference(getActivity(), AppConstants.PREF_CONTACT_SYNCED,
// false) &&
//                    Utils.getBooleanPreference(getActivity(), AppConstants
// .PREF_CALL_LOG_SYNCED, false)) {
//                if (!TextUtils.isEmpty(callLogType.getNumber()))
//                    insertServiceCall(callLogTypeArrayList);
//            }
        }
    }


    private class UpdateOldLogWithUpdatedDetails extends AsyncTask<CallLogType, Void, Void> {

        protected Void doInBackground(CallLogType... urls) {
            updateLogsWithLatestDetails(urls[0]);
            return null;
        }

        protected void onPostExecute(Void result) {

           /* getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });*/

            try {
                simpleCallLogListAdapter.notifyDataSetChanged();
                //Aniruddh -- TO do save 0th record dateTime and rawId in preference;
                if (callLogTypeArrayList.size() > 0) {
                    rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                    Long dateTime = callLogTypeArrayList.get(0).getDate();
                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale
                            .getDefault());

                    Date cursorDate = new Date(dateTime);
                    String latestCallDate = sdf.format(cursorDate);
                    String rawId = callLogTypeArrayList.get(0).getUniqueContactId();

                    Utils.setStringPreference(getActivity(), AppConstants
                            .PREF_LATEST_CALL_DATE_TIME, latestCallDate);
                    Utils.setStringPreference(getActivity(), AppConstants
                            .PREF_LATEST_CALL_RAW_ID, rawId);
                } else {
                    textNoCallsFound.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void updateLogsWithLatestDetails(CallLogType callLogType) {
        if (callLogType != null) {
            String updatedNumber = callLogType.getNumber();
            TableSpamDetailMaster tableSpamDetailMaster = new TableSpamDetailMaster
                    (getDatabaseHandler());
//            String savedNumberFormat = Utils.getFormattedNumber(getActivity(),updatedNumber);
            if (updatedNumber.startsWith("0")) {
                updatedNumber = Utils.getFormattedNumber(getActivity(), updatedNumber);
            }

            String savedNumberFormat = updatedNumber;
            if (savedNumberFormat.startsWith("+91"))
                savedNumberFormat = savedNumberFormat.replace("+", "");
            else
                savedNumberFormat = "91" + savedNumberFormat;

            SpamDataType spamDataType = tableSpamDetailMaster.getSpamDetailsFromNumber
                    (savedNumberFormat);
            String spamCount = spamDataType.getSpamCount();
            if (callLogTypeArrayList.size() > 0) {
                for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                    /*if (nameAndProfileImage != null && nameAndProfileImage.isCancelled())
                        return;
                    if (updateOldLogWithUpdatedDetails != null && updateOldLogWithUpdatedDetails
                    .isCancelled())
                        return;*/

                    CallLogType callLogTypeOfList = callLogTypeArrayList.get(i);
                    String numberToUpdate = callLogTypeOfList.getNumber();
                    // TODO: 13/08/17 Need to handle number begin with 0 and add logic to remove
                    // + and add 91
                    // (if issue found)
                    if (StringUtils.equalsIgnoreCase(updatedNumber, numberToUpdate)) {
                        callLogTypeOfList.setRcpLastName(callLogType.getRcpLastName());
                        callLogTypeOfList.setRcpFirstName(callLogType.getRcpFirstName());
                        callLogTypeOfList.setPrefix(callLogType.getPrefix());
                        callLogTypeOfList.setSuffix(callLogType.getSuffix());
                        callLogTypeOfList.setMiddleName(callLogType.getMiddleName());
                        callLogTypeOfList.setIsRcpVerfied(callLogType.getIsRcpVerfied());
                        callLogTypeOfList.setCallLogProfileRating(callLogType
                                .getCallLogProfileRating());
                        callLogTypeOfList.setCallLogTotalProfileRateUser(callLogType
                                .getCallLogTotalProfileRateUser());
                        if (StringUtils.equalsIgnoreCase(callLogType.getSpamCount(), spamCount)) {
                            callLogTypeOfList.setSpamCount(callLogType.getSpamCount());
                        } else {
                            callLogTypeOfList.setSpamCount(spamCount);
                        }
                        callLogTypeOfList.setProfileImage(callLogType.getProfileImage());

                        callLogTypeArrayList.set(i, callLogTypeOfList);
                    }
                }
            }
        }
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void initSpinner() {

        callLogTypeArrayList = new ArrayList<>();
        callLogTypeArrayList = rContactApplication.getArrayListCallLogType();

        final List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(CALL_LOG_ALL_CALLS);
        spinnerArray.add(CALL_LOG_INCOMING_CALLS);
        spinnerArray.add(CALL_LOG_OUTGOING_CALLS);
        spinnerArray.add(CALL_LOG_MISSED_CALLS);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_spinner_item, spinnerArray);*/
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout
                .header_spinner_call_log, spinnerArray);

        adapter.setDropDownViewResource(R.layout.list_item_spinner_call_log);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

//                            if (AppConstants.isFirstTime()) {
//                                AppConstants.setIsFirstTime(false);
//                                fetchCallLogs();
//                            } else {
                            if (callLogTypeArrayList.size() > 0) {
                                getLatestData();
//                                    makeSimpleData();
                            } else {
                                fetchCallLogs();
                            }
//                            }
                        }
                    }, 250);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

//    private ArrayList<CallLogType> divideCallLogByChunck() {
//        callLogsListbyChunck = new ArrayList<>();
//        for (ArrayList<CallLogType> partition : choppedCallLog(callLogTypeArrayList,
// LIST_PARTITION_COUNT)) {
//            // do something with partition
//            callLogsListbyChunck.addAll(partition);
////            callLogTypeArrayList.removeAll(partition);
//            break;
//        }
//        return callLogsListbyChunck;
//    }

    private void fetchCallLogs() {

        callLogTypeArrayList.clear();
        simpleCallLogListAdapter = new SimpleCallLogListAdapter(getActivity(),
                callLogTypeArrayList,this);
        recyclerCallLogs.setAdapter(simpleCallLogListAdapter);

        try {

            String order = CallLog.Calls.DATE + " DESC";
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                    .READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, null, null, order);

            if (cursor != null) {
                while (cursor.moveToNext()) {

                    String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

                    CallLogType callLogType = new CallLogType(getActivity());

                    callLogType.setNumber(number);

                    String userName = cursor.getString(cursor.getColumnIndex(CallLog.Calls
                            .CACHED_NAME));

                    if (!StringUtils.isEmpty(userName))
                        callLogType.setName(userName);
//                    else
//                        callLogType.setName("");

                    callLogType.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                    callLogType.setDuration(cursor.getInt(cursor.getColumnIndex(CallLog.Calls
                            .DURATION)));
                    callLogType.setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
                    callLogType.setUniqueContactId(cursor.getString(cursor.getColumnIndex(CallLog
                            .Calls._ID)));
                    callLogType.setLocalPbRowId(" ");
//                    callLogType.setProfileImage("");
                    if (callLogType.getType() != AppConstants.NEW_CONTACT_MI) {
                        callLogTypeArrayList.add(callLogType);
                    }
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeSimpleData();
                nameAndProfileImage = new GetRCPNameAndProfileImage();
                nameAndProfileImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 150);
    }

    private void getLatestData() {

        try {
            String order = CallLog.Calls.DATE + " ASC";
            String prefDate = Utils.getStringPreference(getActivity(), AppConstants
                    .PREF_LATEST_CALL_DATE_TIME, "");
            String prefRowId = Utils.getStringPreference(getActivity(), AppConstants
                    .PREF_LATEST_CALL_RAW_ID, "");
            String dateToCompare = "", tempDate = "";
            String currentDate = "";
            long dateToConvert = 0;
            if (!StringUtils.isEmpty(prefDate)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale
                        .getDefault());
                dateToConvert = sdf.parse(prefDate).getTime();
                dateToCompare = String.valueOf(dateToConvert);
//                System.out.println("RContact last Call-log date : " + dateToCompare);
                currentDate = String.valueOf(System.currentTimeMillis());
            }

            Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, CallLog.Calls.DATE + " BETWEEN ? AND ?"
                    , new String[]{dateToCompare, currentDate}, order);

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale
                            .getDefault());

                    Date cursorDate = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls
                            .DATE)));
                    String cursorDateToCompare = sdf.format(cursorDate);

                    Date compareDate = new Date(dateToConvert);
                    String prefDateToCompare = sdf.format(compareDate);

                    Date curDate = sdf.parse(cursorDateToCompare);
                    Date preferenceDate = sdf.parse(prefDateToCompare);

                    if (curDate.getTime() > preferenceDate.getTime() && (Integer.parseInt(cursor
                            .getString(cursor.getColumnIndex(CallLog.Calls._ID)))
                            > Integer.parseInt(prefRowId))) {
                        String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls
                                .NUMBER));

                        CallLogType callLogType = new CallLogType(getActivity());

                        callLogType.setNumber(number);


                        String userName = cursor.getString(cursor.getColumnIndex(CallLog.Calls
                                .CACHED_NAME));

                        if (!StringUtils.isEmpty(userName))
                            callLogType.setName(userName);
//                        else
//                            callLogType.setName("");

                        callLogType.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls
                                .TYPE)));
                        callLogType.setDuration(cursor.getInt(cursor.getColumnIndex(CallLog.Calls
                                .DURATION)));
                        callLogType.setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls
                                .DATE)));
                        callLogType.setUniqueContactId(cursor.getString(cursor.getColumnIndex
                                (CallLog.Calls._ID)));
                        callLogType.setLocalPbRowId(" ");
//                        callLogType.setProfileImage("");
                        if (callLogType.getType() != AppConstants.NEW_CONTACT_MI) {
                            callLogTypeArrayList.add(0, callLogType);
                        }
//                        callLogTypeArrayList.add(0, callLogType);

                    }
                }
                cursor.close();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    makeSimpleData();
                    nameAndProfileImage = new GetRCPNameAndProfileImage();
                    nameAndProfileImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }, 150);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    simpleCallLogListAdapter = new SimpleCallLogListAdapter(getActivity(),
                            filteredList,this);

                } else {
                    simpleCallLogListAdapter = new SimpleCallLogListAdapter(getActivity(),
                            callLogTypeArrayList,this);
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
            getContactName();
            getPhoto();
            setRCPUserName();
            setRCPDetailsAndSpamCountforUnsavedNumbers();
            return null;
        }

        protected void onPostExecute(Void result) {

            try {
                simpleCallLogListAdapter.notifyDataSetChanged();
                //Aniruddh -- TO do save 0th record dateTime and rawId in preference;
                if (callLogTypeArrayList.size() > 0) {
                    rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                    Long dateTime = callLogTypeArrayList.get(0).getDate();
                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale
                            .getDefault());

                    Date cursorDate = new Date(dateTime);
                    String latestCallDate = sdf.format(cursorDate);
                    String rawId = callLogTypeArrayList.get(0).getUniqueContactId();

                    Utils.setStringPreference(getActivity(), AppConstants
                            .PREF_LATEST_CALL_DATE_TIME, latestCallDate);
                    Utils.setStringPreference(getActivity(), AppConstants
                            .PREF_LATEST_CALL_RAW_ID, rawId);
                } else {
                    textNoCallsFound.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private CallLogType setRCPDetailsAndSpamCountforUnsavedNumbers(String number, CallLogType
            callLogType) {
        try {
            TableSpamDetailMaster tableSpamDetailMaster = new TableSpamDetailMaster
                    (getDatabaseHandler());
            if (!StringUtils.isEmpty(number)) {

                if (number.startsWith("0")) {
                    number = Utils.getFormattedNumber(getActivity(), number);
                }

                if (number.startsWith("+91"))
                    number = number.replace("+", "");
                else
                    number = "91" + number;

                SpamDataType spamDataType = tableSpamDetailMaster.getSpamDetailsFromNumber(number);
                if (spamDataType != null && !StringUtils.isEmpty(spamDataType.getSpamCount())) {
                    String lastName = spamDataType.getLastName();
                    String firstName = spamDataType.getFirstName();
                    String prefix = spamDataType.getPrefix();
                    String suffix = spamDataType.getSuffix();
                    String middleName = spamDataType.getMiddleName();
                    String isRcpVerified = spamDataType.getRcpVerfiy();
//                    String rcpId = spamDataType.getRcpPmId();
                    String profileRating = spamDataType.getProfileRating();
                    String totalProfileRateUser = spamDataType.getTotalProfileRateUser();
                    String spamCount = spamDataType.getSpamCount();
                    String photoUrl = spamDataType.getSpamPhotoUrl();

                    if (MoreObjects.firstNonNull(callLogType.isRcpUser(), false)) {
                        callLogType.setRcpFirstName(callLogType.getRcpFirstName());
                        callLogType.setRcpLastName(callLogType.getRcpLastName());
                        if (!StringUtils.isEmpty(spamCount))
                            callLogType.setSpamCount(spamCount);
                    } else {
                        if (!StringUtils.isEmpty(lastName))
                            callLogType.setRcpLastName(lastName);
                        if (!StringUtils.isEmpty(firstName))
                            callLogType.setRcpFirstName(firstName);
                        if (!StringUtils.isEmpty(prefix))
                            callLogType.setPrefix(prefix);
                        if (!StringUtils.isEmpty(suffix))
                            callLogType.setSuffix(suffix);
                        if (!StringUtils.isEmpty(middleName))
                            callLogType.setMiddleName(middleName);
                        if (!StringUtils.isEmpty(isRcpVerified))
                            callLogType.setIsRcpVerfied(isRcpVerified);
                        /*if (!StringUtils.isEmpty(rcpId))
                            callLogType.setRcpId(rcpId);*/
                        if (!StringUtils.isEmpty(profileRating))
                            callLogType.setCallLogProfileRating(profileRating);
                        if (!StringUtils.isEmpty(totalProfileRateUser))
                            callLogType.setCallLogTotalProfileRateUser(totalProfileRateUser);
                        if (!StringUtils.isEmpty(spamCount))
                            callLogType.setSpamCount(spamCount);
                        if (!StringUtils.isEmpty(photoUrl))
                            callLogType.setProfileImage(photoUrl);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return callLogType;
    }

    private void setRCPDetailsAndSpamCountforUnsavedNumbers() {
        try {
            if (callLogTypeArrayList.size() > 0) {
                TableSpamDetailMaster tableSpamDetailMaster = new TableSpamDetailMaster
                        (getDatabaseHandler());
                for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                    if (nameAndProfileImage != null && nameAndProfileImage.isCancelled())
                        return;
                    if (updateOldLogWithUpdatedDetails != null && updateOldLogWithUpdatedDetails
                            .isCancelled())
                        return;

                    CallLogType callLogType = callLogTypeArrayList.get(i);
                    String number = callLogType.getNumber();
                    if (!StringUtils.isEmpty(number)) {
                        if (number.startsWith("0")) {
                            number = Utils.getFormattedNumber(getActivity(), number);
                        }
                        if (number.startsWith("+91"))
                            number = number.replace("+", "");
                        else
                            number = "91" + number;

                        SpamDataType spamDataType = tableSpamDetailMaster
                                .getSpamDetailsFromNumber(number);
                        if (spamDataType != null && !StringUtils.isEmpty(spamDataType
                                .getSpamCount())) {
                            String lastName = spamDataType.getLastName();
                            String firstName = spamDataType.getFirstName();
                            String prefix = spamDataType.getPrefix();
                            String suffix = spamDataType.getSuffix();
                            String middleName = spamDataType.getMiddleName();
                            String isRcpVerified = spamDataType.getRcpVerfiy();
//                            String rcpId = spamDataType.getRcpPmId();
                            String profileRating = spamDataType.getProfileRating();
                            String totalProfileRateUser = spamDataType.getTotalProfileRateUser();
                            String spamCount = spamDataType.getSpamCount();
                            String publicUrl = spamDataType.getSpamPublicUrl();
                            String photoUrl = spamDataType.getSpamPhotoUrl();

                            if (MoreObjects.firstNonNull(callLogType.isRcpUser(), false)) {
                                callLogType.setRcpFirstName(callLogType.getRcpFirstName());
                                callLogType.setRcpLastName(callLogType.getRcpLastName());
                                if (!StringUtils.isEmpty(spamCount))
                                    callLogType.setSpamCount(spamCount);
                            } else {
                                if (!StringUtils.isEmpty(lastName))
                                    callLogType.setRcpLastName(lastName);
                                if (!StringUtils.isEmpty(firstName))
                                    callLogType.setRcpFirstName(firstName);
                                if (!StringUtils.isEmpty(prefix))
                                    callLogType.setPrefix(prefix);
                                if (!StringUtils.isEmpty(suffix))
                                    callLogType.setSuffix(suffix);
                                if (!StringUtils.isEmpty(middleName))
                                    callLogType.setMiddleName(middleName);
                                if (!StringUtils.isEmpty(isRcpVerified))
                                    callLogType.setIsRcpVerfied(isRcpVerified);
                                /*if (!StringUtils.isEmpty(rcpId))
                                    callLogType.setRcpId(rcpId);*/
                                if (!StringUtils.isEmpty(profileRating))
                                    callLogType.setCallLogProfileRating(profileRating);
                                if (!StringUtils.isEmpty(totalProfileRateUser))
                                    callLogType.setCallLogTotalProfileRateUser
                                            (totalProfileRateUser);
                                if (!StringUtils.isEmpty(spamCount))
                                    callLogType.setSpamCount(spamCount);
                                if (!StringUtils.isEmpty(photoUrl))
                                    callLogType.setProfileImage(photoUrl);
                            }

                            callLogTypeArrayList.set(i, callLogType);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPhoto() {
        try {
            if (callLogTypeArrayList.size() > 0) {
                for (int i = 0; i < callLogTypeArrayList.size(); i++) {
                    if (nameAndProfileImage != null && nameAndProfileImage.isCancelled())
                        return;
                    if (updateOldLogWithUpdatedDetails != null && updateOldLogWithUpdatedDetails
                            .isCancelled())
                        return;
                    CallLogType callLogType = callLogTypeArrayList.get(i);
                    String number = Utils.getFormattedNumber(getActivity(), callLogType.getNumber
                            ());
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
                    if (updateOldLogWithUpdatedDetails != null && updateOldLogWithUpdatedDetails
                            .isCancelled())
                        return;
                    CallLogType callLogType = callLogTypeArrayList.get(i);
                    String number = Utils.getFormattedNumber(getActivity(), callLogType.getNumber
                            ());
                    String name = callLogType.getName();
                    if (StringUtils.isEmpty(name)) {
                        name = getNameFromNumber(number);
                        if (!StringUtils.isEmpty(name)) {
                            callLogType.setName(name);
                        } else {
//                            callLogType.setName("");
                        }
                    }
                    callLogTypeArrayList.set(i, callLogType);
                }
            }

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
                    if (updateOldLogWithUpdatedDetails != null && updateOldLogWithUpdatedDetails.isCancelled())
                        return;
                    CallLogType callLogType = callLogTypeArrayList.get(i);

                    String number = Utils.getFormattedNumber(getActivity(), callLogType.getNumber
                            ());

                    if (!StringUtils.isEmpty(number)) {

                        ProfileMobileMapping profileMobileMapping =
                                tableProfileMobileMapping
                                        .getCloudPmIdFromProfileMappingFromNumber(number);
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

//    private void insertServiceCall(ArrayList<CallLogType> callLogTypeArrayList) {
//
//        if (Utils.isNetworkAvailable(getActivity())) {
//            WsRequestObject deviceDetailObject = new WsRequestObject();
//            deviceDetailObject.setFlag(IntegerConstants.SYNC_INSERT_CALL_LOG);
//            deviceDetailObject.setArrayListCallLogType(callLogTypeArrayList);
//            if (Utils.isNetworkAvailable(getActivity())) {
//                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
//                        deviceDetailObject, null, WsResponseObject.class, WsConstants
//                        .REQ_UPLOAD_CALL_LOGS, null, true).executeOnExecutor(AsyncTask
// .THREAD_POOL_EXECUTOR,
//                        WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CALL_LOGS);
//            } else {
//                Utils.showErrorSnackBar(getActivity(), linearCallLogMain, getResources()
//                        .getString(R.string.msg_no_network));
//            }
//        }
//
//    }

    private String getNameFromNumber(String phoneNumber) {
        String contactName = "";
        try {

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null,
                    null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                cursor.close();
            } else {
                if (!cursor.isClosed())
                    cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactName;
    }

    private String getPhotoUrlFromNumber(String phoneNumber) {
        String photoThumbUrl = "";
        Cursor cursor = null;
        try {

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

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
        return getActivity().getString(R.string.type_other);
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
                    actionNumber = Utils.getFormattedNumber(getActivity(), actionNumber);
                    Utils.callIntent(getActivity(), actionNumber);
//                    showCallConfirmationDialog(actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        simpleCallLogListAdapter.notifyItemChanged(position);
                    }
                }, 300);
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

//    private void showCallConfirmationDialog(String number) {
//        final String formattedNumber = Utils.getFormattedNumber(getActivity(), number);
//        RippleView.OnRippleCompleteListener cancelListener = new RippleView
//                .OnRippleCompleteListener() {
//
//            @Override
//            public void onComplete(RippleView rippleView) {
//                switch (rippleView.getId()) {
//                    case R.id.rippleLeft:
//                        callConfirmationDialog.dismissDialog();
//                        break;
//
//                    case R.id.rippleRight:
//                        callConfirmationDialog.dismissDialog();
//                        Utils.callIntent(getActivity(), formattedNumber);
//                        break;
//                }
//            }
//        };
//
//        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
//        callConfirmationDialog.setTitleVisibility(View.GONE);
//        callConfirmationDialog.setLeftButtonText(getActivity().getString(R.string.action_cancel));
//        callConfirmationDialog.setRightButtonText(getActivity().getString(R.string.action_call));
//        callConfirmationDialog.setDialogBody(getActivity().getString(R.string.action_call) + " "
//                + formattedNumber + "?");
//        callConfirmationDialog.showDialog();
//
//    }

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
        if (getActivity() == null)
            return null;
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
        if (getActivity() == null)
            return null;
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
                int account_id = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //for versions above lollipop
                    account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
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

//                    String durationtoPass = logObject.getCoolDuration(Float.parseFloat
//                            (callDuration));
//                    logObject.setDurationToPass(durationtoPass);

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
                                    rContactApplication.setArrayListCallLogType
                                            (callLogTypeArrayList);
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
                                        rContactApplication.setArrayListCallLogType
                                                (callLogTypeArrayList);
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

//                            System.out.println("RContact itemIndexToRemove --> " +
// itemIndexToRemove);

                            // updated on 19/04/2017, when data are loading from arraylist
//                            CallLogType callDataToUpdate = simpleCallLogListAdapter
// .getSelectedCallLogData();
                            callLogTypeArrayList.remove(itemIndexToRemove);
                            rContactApplication.setArrayListCallLogType(callLogTypeArrayList);
                            simpleCallLogListAdapter.notifyItemRemoved(itemIndexToRemove);
                            simpleCallLogListAdapter.notifyItemRangeChanged(itemIndexToRemove,
                                    simpleCallLogListAdapter.getItemCount());
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

            boolean deleteAll = intent.getBooleanExtra(AppConstants.EXTRA_DELETE_ALL_CALL_LOGS,
                    false);
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

