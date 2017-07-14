package com.rawalinfocom.rcontact.helper;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.Profile3DotDialogAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aniruddh on 20/03/17.
 */

public class ProfileMenuOptionDialog {


    private RecyclerView recycleViewDialog;
    private Context context;
    private Dialog dialog;
    private String dialogTag;
    private TextView tvDialogTitle;
    private ArrayList<String> stringArrayList;
    private String dialogTitle;
    private String numberToCall;
    private RecyclerView.Adapter callingAdapter;
    private long callLogDateToDelete;
    private boolean isFromCallLogFragment = false;
    private ArrayList<CallLogType> arrayListCallLogType;
    private String name = "";
    private String uniqueID = "";
    private String key;
    private String profileUrl;
    private String pmId;
    private boolean isCallLogRcpUser;

    public ProfileMenuOptionDialog(Context context, ArrayList<String> arrayList, String number,
                                   long date, boolean isFromCallTab, ArrayList<CallLogType> list,
                                   String name, String uniqueRowId, String key, String
                                           profileUrl, String pmId, boolean isCallLogRcpUser) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list_material);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(number);
        if (matcher1.find()) {
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels *
                    0.80);
        } else {
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels *
                    0.60);
        }

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setGravity(Gravity.TOP | Gravity.RIGHT);
        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setTypeface(Utils.typefaceBold(context));
        tvDialogTitle.setVisibility(View.GONE);
        recycleViewDialog = (RecyclerView) dialog.findViewById(R.id.recycle_view_dialog);

        stringArrayList = arrayList;
        numberToCall = number;
        callLogDateToDelete = date;
        dialogTitle = getDialogTitle();
        if (!TextUtils.isEmpty(dialogTitle))
            tvDialogTitle.setText(dialogTitle);

        isFromCallLogFragment = isFromCallTab;
        arrayListCallLogType = list;
        this.name = name;
        this.key = key;
        this.uniqueID = uniqueRowId;
        this.profileUrl = profileUrl;
        this.pmId = pmId;
        this.isCallLogRcpUser = isCallLogRcpUser;

        setAdapter();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_DIALOG);
        localBroadcastManager.registerReceiver(localBroadcastReceiverDialog, intentFilter);
    }


    private void setAdapter() {
        if (!TextUtils.isEmpty(numberToCall)) {
            Profile3DotDialogAdapter profile3DotDialogAdapter = new Profile3DotDialogAdapter
                    (context, stringArrayList, numberToCall, callLogDateToDelete,
                            isFromCallLogFragment, arrayListCallLogType, name, uniqueID, key,
                            profileUrl, pmId, isCallLogRcpUser);
            recycleViewDialog.setAdapter(profile3DotDialogAdapter);
            setRecyclerViewLayoutManager(recycleViewDialog);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }


    public void setDialogTitle(String text) {
        tvDialogTitle.setText(text);
    }

    public void setTitleVisibility(int visibility) {
        tvDialogTitle.setVisibility(visibility);
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public boolean isDialogShowing() {
        return dialog.isShowing();
    }

    public String getDialogTag() {
        return dialogTag;
    }

    public void setDialogTag(String dialogTag) {
        this.dialogTag = dialogTag;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public String getNumberToCall() {
        return numberToCall;
    }

    public void setNumberToCall(String numberToCall) {
        this.numberToCall = numberToCall;
    }

    public RecyclerView.Adapter getCallingAdapter() {
        return callingAdapter;
    }

    public void setCallingAdapter(RecyclerView.Adapter callingAdapter) {
        this.callingAdapter = callingAdapter;
    }

    private BroadcastReceiver localBroadcastReceiverDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("CallLogFragment", "onReceive() of LocalBroadcast");
            dismissDialog();
        }
    };
}
