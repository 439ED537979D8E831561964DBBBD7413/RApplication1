package com.rawalinfocom.rcontact;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rawalinfocom.rcontact.adapters.NotificationPopupListAdapter;
import com.rawalinfocom.rcontact.adapters.RatingHistoryPopupListAdapter;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.ArrayList;

/**
 * Created by maulik on 15/03/17.
 */

public class RatingHistoryPopupDialog {

    private RecyclerView recycleViewDialog;
    private Dialog dialog;
    private LinearLayout ratingInfoLayout;
    private String dialogTag;
    private TextView tvDialogTitle;
    private TextView tvDialogRating;
    private RatingBar ratingBar;
    private ArrayList<String> stringArrayList;
    private String dialogTitle;
    private String ratingInfo;

    public String getRatingInfo() {
        return ratingInfo;
    }

    public void setRatingInfo(String ratingInfo) {
        this.ratingInfo = ratingInfo;
        tvDialogRating.setText(this.ratingInfo);
        ratingBar.setRating(Float.parseFloat(this.ratingInfo));
    }


    public RatingHistoryPopupDialog(Context context, ArrayList<String> arrayList, Boolean isRatingPopup) {
        this.stringArrayList = arrayList;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notification_popup);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        ratingInfoLayout = (LinearLayout) dialog.findViewById(R.id.rating_info);

        tvDialogTitle.setTypeface(Utils.typefaceSemiBold(context));
        recycleViewDialog = (RecyclerView) dialog.findViewById(R.id.recycle_view_dialog);
        dialogTitle = getDialogTitle();
        if (!TextUtils.isEmpty(dialogTitle))
            tvDialogTitle.setText(dialogTitle);
        if (isRatingPopup) {
            ratingInfoLayout.setVisibility(View.VISIBLE);
            tvDialogRating = (TextView) dialog.findViewById(R.id.text_rating_given);
            ratingBar = (RatingBar) dialog.findViewById(R.id.given_rating_bar);

            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(context, R.color
                    .vivid_yellow));
            Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(context, android.R
                    .color.darker_gray));
            // Empty stars
            Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(context, android.R
                    .color.darker_gray));
        } else {
            ratingInfoLayout.setVisibility(View.GONE);
        }
        RatingHistoryPopupListAdapter materialListAdapter = new RatingHistoryPopupListAdapter(context, stringArrayList);
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
