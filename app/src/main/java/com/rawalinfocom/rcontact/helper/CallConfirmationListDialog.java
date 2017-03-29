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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.CallConfirmationListAdapter;
import com.rawalinfocom.rcontact.adapters.CallLogDialogListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;

import java.util.ArrayList;

/**
 * Created by Aniruddh on 27/03/17.
 */

public class CallConfirmationListDialog {
    RecyclerView recycleViewDialog;
    private Context context;
    private Dialog dialog;
    private String dialogTag;
    private TextView tvDialogTitle;
    private ArrayList<String> stringArrayList;
    String dialogTitle;
    RecyclerView.Adapter callingAdapter;

    public CallConfirmationListDialog(Context context, ArrayList<String> arrayList) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list_call_confirmation);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setTypeface(Utils.typefaceBold(context));
        recycleViewDialog = (RecyclerView) dialog.findViewById(R.id.recycle_view_dialog);


        stringArrayList = arrayList;
        dialogTitle = getDialogTitle();
        if (!TextUtils.isEmpty(dialogTitle))
            tvDialogTitle.setText(dialogTitle);

        setAdapter();

        LocalBroadcastManager localBroadcastManager =  LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_DIALOG);
        localBroadcastManager.registerReceiver(localBroadcastReceiverDialog,intentFilter);
    }



    private void setAdapter() {
            CallConfirmationListAdapter materialListAdapter = new CallConfirmationListAdapter(context, stringArrayList);
            recycleViewDialog.setAdapter(materialListAdapter);
            setRecyclerViewLayoutManager(recycleViewDialog);

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

    public RecyclerView.Adapter getCallingAdapter() {
        return callingAdapter;
    }

    public void setCallingAdapter(RecyclerView.Adapter callingAdapter) {
        this.callingAdapter = callingAdapter;
    }

    private BroadcastReceiver localBroadcastReceiverDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("CallLogFragment","onReceive() of LocalBroadcast");
            dismissDialog();

        }
    };
}
