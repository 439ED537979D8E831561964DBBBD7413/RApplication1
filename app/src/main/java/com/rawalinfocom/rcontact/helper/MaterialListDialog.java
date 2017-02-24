package com.rawalinfocom.rcontact.helper;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Aniruddh on 24/12/16.
 */

public class MaterialListDialog {

    RecyclerView recycleViewDialog;
    private Context context;
    private Dialog dialog;
    private String dialogTag;
    private TextView tvDialogTitle;


    public MaterialListDialog(Context context, ArrayList<String> arrayList) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list_material);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setTypeface(Utils.typefaceBold(context));
        recycleViewDialog = (RecyclerView) dialog.findViewById(R.id.recycle_view_dialog);
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
}
