package com.rawalinfocom.rcontact.contacts;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.PrivacySettingPopupListAdapter;
import com.rawalinfocom.rcontact.adapters.ProfileDetailAdapter;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.ArrayList;

/**
 * Created by maulik on 15/03/17.
 */

public class PrivacySettingPopupDialog {


    public interface DialogCallback {
        void onSettingSaved(ProfileDetailAdapter
                                    .ProfileDetailViewHolder view, int whichItem, int newPrivacy, int itemPosition, int oldPrivacy, String cloudId);
    }

    private RecyclerView recycleViewDialog;
    private Dialog dialog;
    private String dialogTag;
    private TextView tvDialogTitle;
    private String dialogTitle;
    private Button cancelButton;
    private Button okButton;
    public static int currentPrivacy = -1;
    private DialogCallback callback;

    public PrivacySettingPopupDialog(final ProfileDetailAdapter
            .ProfileDetailViewHolder viewHolder, final Context context, DialogCallback listner, final int itemType, final int itemPosition, final int privacy, final String cloudId) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_privacy_policy);
        callback = listner;
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        currentPrivacy = privacy - 1;
        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        cancelButton = dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSettingSaved(viewHolder, itemType, currentPrivacy, itemPosition, privacy, cloudId);
                dialog.cancel();
            }
        });


        tvDialogTitle.setTypeface(Utils.typefaceSemiBold(context));
        recycleViewDialog = dialog.findViewById(R.id.recycle_view_dialog);
        dialogTitle = getDialogTitle();
        if (!TextUtils.isEmpty(dialogTitle))
            tvDialogTitle.setText(dialogTitle);

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(context.getResources().getString(R.string.privacy_everyone));
        stringArrayList.add(context.getResources().getString(R.string.privacy_my_contact));
        stringArrayList.add(context.getResources().getString(R.string.privacy_only_me));

        PrivacySettingPopupListAdapter materialListAdapter = new PrivacySettingPopupListAdapter(context, stringArrayList);
        recycleViewDialog.setAdapter(materialListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recycleViewDialog.setLayoutManager(linearLayoutManager);
        recycleViewDialog.setHasFixedSize(true);

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


}
