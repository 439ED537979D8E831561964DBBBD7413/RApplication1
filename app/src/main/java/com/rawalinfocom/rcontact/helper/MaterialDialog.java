package com.rawalinfocom.rcontact.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;

/**
 * Created by user on 24/12/16.
 */

public class MaterialDialog {

    Context context;
    Dialog dialog;

    String dialogTag;

    TextView tvDialogTitle, tvDialogBody;
    Button btnLeft, btnRight;
    RippleView.OnRippleCompleteListener onRippleCompleteListener;

    RippleView rippleLeft, rippleRight;

    public MaterialDialog(Context context, RippleView.OnRippleCompleteListener
            onRippleCompleteListener) {
        this.context = context;
        this.onRippleCompleteListener = onRippleCompleteListener;

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_material);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogBody = (TextView) dialog.findViewById(R.id.tvDialogBody);

        btnLeft = (Button) dialog.findViewById(R.id.btnLeft);
        btnRight = (Button) dialog.findViewById(R.id.btnRight);

        rippleLeft = (RippleView) dialog.findViewById(R.id.rippleLeft);
        rippleRight = (RippleView) dialog.findViewById(R.id.rippleRight);

        rippleLeft.setOnRippleCompleteListener(onRippleCompleteListener);
        rippleRight.setOnRippleCompleteListener(onRippleCompleteListener);

        tvDialogTitle.setTypeface(Utils.typefaceBold(context));
        tvDialogBody.setTypeface(Utils.typefaceRegular(context));
        btnLeft.setTypeface(Utils.typefaceRegular(context));
        btnRight.setTypeface(Utils.typefaceRegular(context));
    }


    public void setRightButtonText(String text) {
        btnRight.setText(text);
    }

    public void setLeftButtonText(String text) {
        btnLeft.setText(text);
    }

    public void setDialogTitle(String text) {
        tvDialogTitle.setText(text);
    }

    public void setDialogBody(String text) {
        tvDialogBody.setText(text);
    }

    public void setTitleVisibility(int visibility) {
        tvDialogTitle.setVisibility(visibility);
    }

    public void setLeftButtonVisibility(int visibility) {
        btnLeft.setVisibility(visibility);
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
}
