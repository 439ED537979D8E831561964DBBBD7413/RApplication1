package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.MaterialDialogClipboard;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 24/02/17.
 */

public class MaterialListAdapter extends RecyclerView.Adapter<MaterialListAdapter.MaterialViewHolder> {


    private Context context;
    private ArrayList<String> arrayListString;
    private String dialogTitle;
    MaterialDialog callConfirmationDialog;
    String numberToCall;
    String dialogName;

    public MaterialListAdapter(Context context, ArrayList<String> arrayList, String number) {
        this.context = context;
        this.arrayListString = arrayList;
        this.numberToCall = number;
//        this.dialogName = dialogTitle;
    }


    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dialog_call_log,
                parent, false);
        return new MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, final int position) {
        final String value = arrayListString.get(position);
        holder.textItemValue.setText(value);

        holder.rippleRow.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (position == 0) {
                    if (!TextUtils.isEmpty(numberToCall))
                        showCallConfirmationDialog(numberToCall);
                }

                if (value.equalsIgnoreCase(context.getString(R.string.add_to_contact))) {

                    Utils.addToContact(context, numberToCall);

                } else if (value.equalsIgnoreCase(context.getString(R.string.add_to_existing_contact))) {
                    Utils.addToExistingContact(context, numberToCall);

                }/*else if(value.equalsIgnoreCase(context.getString(R.string.show_call_history))){
                    Pattern numberPat = Pattern.compile("\\d+");
                    Matcher matcher1 = numberPat.matcher(dialogName);
                    if (matcher1.find()) {
                        // number
                        Intent intent = new Intent(context, ProfileDetailActivity.class);
                        intent.putExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, true);
                        intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER, dialogName);
                        context.startActivity(intent);
                    } else {
                        // name
                        Intent intent = new Intent(context, ProfileDetailActivity.class);
                        intent.putExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, true);
                        intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NAME, dialogName);
                        context.startActivity(intent);
                    }


                }*/ else if (value.equalsIgnoreCase(context.getString(R.string.send_sms))) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + numberToCall));
                    context.startActivity(smsIntent);

                } else if (value.equalsIgnoreCase(context.getString(R.string.remove_from_call_log))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.copy_phone_number))) {
                    MaterialDialogClipboard materialDialogClipboard = new MaterialDialogClipboard(context, numberToCall);
                    materialDialogClipboard.showDialog();

                } else if (value.equalsIgnoreCase(context.getString(R.string.block))) {

                } else if (value.equalsIgnoreCase(context.getString(R.string.call_reminder))) {

                } else {

                    Toast.makeText(context, context.getString(R.string.please_select_one), Toast.LENGTH_SHORT).show();
                }

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

    private void showCallConfirmationDialog(final String number) {

        final String finalNumber;

        if (!number.startsWith("+91")) {
            finalNumber = "+91" + number;
        } else {
            finalNumber = number;
        }

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
                        /*Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        try {
                            context.startActivity(intent);

                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }*/
                        Utils.callIntent(context, finalNumber);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(context, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(context.getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(context.getString(R.string.action_call));
        callConfirmationDialog.setDialogBody(context.getString(R.string.action_call) + " " + finalNumber + "?");
        callConfirmationDialog.showDialog();
    }
}
