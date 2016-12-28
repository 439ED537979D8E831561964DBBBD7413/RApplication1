package com.rawalinfocom.rcontact.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;

/**
 * Created by Monal on 09/11/16.
 * <p>
 * A class for customised Progress Dialog
 */

public class MaterialProgressDialog extends ProgressDialog {

    private static String text = "";
    private static Context context = null;
    private ProgressWheel progressWheel;

    public static ProgressDialog ctor(Context context, String text) {

        MaterialProgressDialog dialog = new MaterialProgressDialog(context);
        dialog.setIndeterminate(true);
        MaterialProgressDialog.text = text;
        MaterialProgressDialog.context = context;
        // dialog.setCancelable(false);
        return dialog;
    }

    public MaterialProgressDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_dialog);

        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        // progressWheel.setCircleRadius(50);

        TextView progressText = (TextView) findViewById(R.id.text_progress);
        progressText.setText(text);
        if (context != null)
            progressText.setTypeface(Utils.typefaceRegular(context));

        Rect displayRectangle = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        getWindow().setLayout((int) (displayRectangle.width() * 0.85f), LinearLayout.LayoutParams
                .WRAP_CONTENT);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public ProgressWheel getProgressWheel() {
        return progressWheel;
    }

}
