package com.rawalinfocom.rcontact.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;

import butterknife.BindView;

/**
 * Created by user on 24/12/16.
 */

public class MaterialDialogClipboard {

    RippleView rippleRow;
    private Context context;
    private Dialog dialog;

    private String dialogTag;

    private TextView tvDialogTitle, tvDialogBody;
    String numberToCopy;

    public MaterialDialogClipboard(Context context, String number) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_material_clipboard);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogBody = (TextView) dialog.findViewById(R.id.tvDialogBody);
        rippleRow = (RippleView) dialog.findViewById(R.id.rippleRow);

        tvDialogTitle.setTypeface(Utils.typefaceBold(context));
        tvDialogBody.setTypeface(Utils.typefaceRegular(context));
        numberToCopy =  number;
        tvDialogTitle.setText(number);
        tvDialogBody.setText(context.getString(R.string.copy_to_clipboard));

        clickEvents();
    }

    private void clickEvents() {

        rippleRow.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Utils.copyToClipboard(context, "Copied Number", numberToCopy);
               /* Utils.showSuccessSnackBar(context, ((ProfileDetailActivity) context)
                        .getRelativeRootProfileDetail(), "Number copied to Clipboard");*/
                Toast.makeText(context,"Number copied to Clipboard",Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });

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
