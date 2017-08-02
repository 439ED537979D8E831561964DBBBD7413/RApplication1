package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 27/03/17.
 */

public class CallConfirmationListAdapter extends RecyclerView.Adapter<CallConfirmationListAdapter
        .MaterialViewHolder> {

    private Context context;
    private ArrayList<String> arrayListString;
    MaterialDialog callConfirmationDialog;
    boolean isFromCallLog;

    public CallConfirmationListAdapter(Context context, ArrayList<String> arrayList, boolean
            isFromCallLog) {
        this.context = context;
        this.arrayListString = arrayList;
        this.isFromCallLog = isFromCallLog;
    }

    @Override
    public CallConfirmationListAdapter.MaterialViewHolder onCreateViewHolder(ViewGroup parent,
                                                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .list_item_dialog_call_log,
                parent, false);
        return new CallConfirmationListAdapter.MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CallConfirmationListAdapter.MaterialViewHolder holder, final int
            position) {
        final String value = arrayListString.get(position);
        holder.textItemValue.setText(value);

        holder.rippleRow.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (isFromCallLog) {
                    String number = Utils.getFormattedNumber(context, value);
                    Utils.callIntent(context, number);
//                    showCallConfirmationDialog(value);

                } else {
                    if (!TextUtils.isEmpty(value)) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
                                value, null));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            intent.setPackage(Telephony.Sms.getDefaultSmsPackage(context));
                        }
                        intent.putExtra("finishActivityOnSaveCompleted", true);
                        context.startActivity(intent);
                    }

                }

                Intent localBroadcastIntent = new Intent(AppConstants
                        .ACTION_LOCAL_BROADCAST_DIALOG);
                localBroadcastIntent.putExtra(AppConstants.EXTRA_CALL_LOG_DELETED_KEY,
                        AppConstants.EXTRA_CALL_LOG_DELETED_VALUE);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                        (context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayListString.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_item_value)
        TextView textItemValue;

        @BindView(R.id.linear_main)
        LinearLayout linearMain;

        @BindView(R.id.rippleRow)
        RippleView rippleRow;

        MaterialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

//    private void showCallConfirmationDialog(final String number) {
//
//        final String finalNumber;
//
//        if (!number.startsWith("+91")) {
//            finalNumber = "+91" + number;
//        } else {
//            finalNumber = number;
//        }
//
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
//                        /*Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
//                        number));
//                        try {
//                            context.startActivity(intent);
//
//                        } catch (SecurityException e) {
//                            e.printStackTrace();
//                        }*/
//                        Utils.callIntent(context, finalNumber);
//                        break;
//                }
//            }
//        };
//
//        callConfirmationDialog = new MaterialDialog(context, cancelListener);
//        callConfirmationDialog.setTitleVisibility(View.GONE);
//        callConfirmationDialog.setLeftButtonText(context.getString(R.string.action_cancel));
//        callConfirmationDialog.setRightButtonText(context.getString(R.string.action_call));
//        callConfirmationDialog.setDialogBody(context.getString(R.string.action_call) + number + "?");
//        callConfirmationDialog.showDialog();
//    }
}
