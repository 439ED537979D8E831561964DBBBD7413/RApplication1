package com.rawalinfocom.rcontact.calllog;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.DeleteCallLogListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogDeleteActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {


    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    RippleView rippleActionBack;
    RippleView rippleActionToolbarDelete;
    ImageView imageDelete;
    @BindView(R.id.recycle_view_delete_call_log)
    RecyclerView recycleViewDeleteCallLog;

    ArrayList<CallLogType> arrayListCallLogType;
    DeleteCallLogListAdapter deleteCallLogListAdapter;
    @BindView(R.id.checkbox_select_all)
    CheckBox checkboxSelectAll;
    @BindView(R.id.no_record_to_display)
    TextView textNoRecordToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log_delete);
        ButterKnife.bind(this);
        receiveBundleData();
        init();

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_delete:
                fetchDataToDelete();
                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isTaskRoot()) {
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        }
    }

    private void receiveBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            arrayListCallLogType = (ArrayList<CallLogType>) bundle.getSerializable(AppConstants.EXTRA_CALL_ARRAY_LIST);
        }
    }


    private void init() {
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        imageDelete = ButterKnife.findById(includeToolbar, R.id.image_delete);
        rippleActionToolbarDelete = ButterKnife.findById(includeToolbar, R.id.ripple_action_delete);
        deleteCallLogListAdapter = new DeleteCallLogListAdapter(this, arrayListCallLogType);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textToolbarTitle.setText("Delete");
        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionToolbarDelete.setOnRippleCompleteListener(this);
        checkboxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                deleteCallLogListAdapter.isSelectAll(isChecked);
                if (!isChecked) {
                    deleteCallLogListAdapter.getArrayListCheckedPositions().clear();
                }

            }
        });

        setAdapter();

    }

    private void setAdapter() {
        if (arrayListCallLogType != null && arrayListCallLogType.size() > 0) {
            recycleViewDeleteCallLog.setVisibility(View.VISIBLE);
            textNoRecordToDisplay.setVisibility(View.GONE);
            checkboxSelectAll.setEnabled(true);
            rippleActionToolbarDelete.setEnabled(true);
            deleteCallLogListAdapter = new
                    DeleteCallLogListAdapter(this, arrayListCallLogType);
            recycleViewDeleteCallLog.setAdapter(deleteCallLogListAdapter);
            setRecyclerViewLayoutManager(recycleViewDeleteCallLog);
        }else{
            textNoRecordToDisplay.setVisibility(View.VISIBLE);
            textNoRecordToDisplay.setText("No history to delete");
            checkboxSelectAll.setEnabled(false);
            rippleActionToolbarDelete.setEnabled(false);
        }
    }

    private void fetchDataToDelete() {
        ArrayList<CallLogType> listToDelete = deleteCallLogListAdapter.getArrayListToDelete();
        boolean selectAll = deleteCallLogListAdapter.isSelectedAll;
        if (listToDelete.size() > 0) {
           for(int i = 0; i<listToDelete.size(); i++){
               CallLogType callLogType = listToDelete.get(i);
               String number = callLogType.getHistoryNumber();
               long dateAndTime = callLogType.getHistoryDate();
               // delete operation
               String where = CallLog.Calls.NUMBER + " =?" + " AND " + CallLog.Calls.DATE + " =?";
               String[] selectionArguments = new String[]{number, String.valueOf(dateAndTime)};
               int value = this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, where, selectionArguments);
               if (value > 0) {
                   arrayListCallLogType.remove(callLogType);
                   deleteCallLogListAdapter.notifyDataSetChanged();
               }
           }

            if (arrayListCallLogType.size() <= 0) {
                recycleViewDeleteCallLog.setVisibility(View.GONE);
                textNoRecordToDisplay.setVisibility(View.VISIBLE);
                checkboxSelectAll.setChecked(false);
            }

            Intent localBroadcastIntent1 = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_DELETE_LOGS);
            localBroadcastIntent1.putExtra(AppConstants.EXTRA_DELETE_ALL_CALL_LOGS, selectAll);
            LocalBroadcastManager myLocalBroadcastManager1 = LocalBroadcastManager.getInstance(this);
            myLocalBroadcastManager1.sendBroadcast(localBroadcastIntent1);

            finish();
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);

        } else {
            Toast.makeText(this, "Please select call history to delete", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Set RecyclerView's LayoutManager
     */
    private void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }


}
