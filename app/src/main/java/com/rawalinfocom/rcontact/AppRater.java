package com.rawalinfocom.rcontact;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;




/**
 * Created by Aniruddh on 31/10/17.
 */

public class AppRater {

    private final static int DAYS_UNTIL_PROMPT = 5 ;//Min number of days

    public static void app_launched(Context mContext) {
        if (Utils.getBooleanPreference(mContext, AppConstants.PREF_DONTSHOWAGAIN_POPUP, false)) {
            return;
        }
        Long date_firstLaunch = Utils.getLongPreference(mContext, AppConstants.PREF_RATE_APP_DATE, 0);
        date_firstLaunch = System.currentTimeMillis();
        Utils.setLongPreference(mContext, AppConstants.PREF_RATE_APP_DATE, date_firstLaunch);

        if (System.currentTimeMillis() >= date_firstLaunch +
                (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
            showRateDialog(mContext);
        }
    }

    public static void showRateDialog(final Context mContext) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rate_app);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDialogBody = (TextView) dialog.findViewById(R.id.tvDialogBody);
        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.rippleLeft);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.rippleRight);

        Button btnLeft = (Button) dialog.findViewById(R.id.btnLeft);
        Button btnRight = (Button) dialog.findViewById(R.id.btnRight);

        tvDialogTitle.setTypeface(Utils.typefaceBold(mContext));
        tvDialogBody.setTypeface(Utils.typefaceRegular(mContext));
        btnLeft.setTypeface(Utils.typefaceRegular(mContext));
        btnRight.setTypeface(Utils.typefaceRegular(mContext));

        tvDialogTitle.setText(mContext.getResources().getString(R.string.str_like_this_application));
        tvDialogTitle.setTextColor(mContext.getResources().getColor(R.color.colorAccent));

        tvDialogBody.setText(mContext.getResources().getString(R.string.str_rate_app_body));
        btnLeft.setText(mContext.getResources().getString(R.string.str_later));
        btnRight.setText(mContext.getResources().getString(R.string.str_rate_app));

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        Utils.setBooleanPreference(mContext, AppConstants.PREF_DONTSHOWAGAIN_POPUP, false);
                        Long date_firstLaunch = System.currentTimeMillis();
                        Utils.setLongPreference(mContext, AppConstants.PREF_RATE_APP_DATE, date_firstLaunch);
                        dialog.dismiss();
                        break;

                    case R.id.rippleRight:
                        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                        try {
                            mContext.startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(AppConstants.PLAY_STORE_LINK + mContext.getPackageName())));
                        }
                        Utils.setBooleanPreference(mContext,AppConstants.PREF_DONTSHOWAGAIN_POPUP, true);
                        dialog.dismiss();
                        break;
                }
            }
        };

        rippleLeft.setOnRippleCompleteListener(cancelListener);
        rippleRight.setOnRippleCompleteListener(cancelListener);
        dialog.show();
    }
}
