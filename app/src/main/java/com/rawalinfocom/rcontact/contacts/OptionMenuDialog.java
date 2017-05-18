package com.rawalinfocom.rcontact.contacts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.OptionMenuAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RecyclerItemClickListener;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.PrivacyDataItem;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Monal on 24/12/16.
 */

public class OptionMenuDialog {

    public static final int ALL_CONTACT_MY_PROFILE = 0;
    public static final int ALL_CONTACT_NON_RCP = 1;
    public static final int ALL_CONTACT_RCP = 2;
    public static final int R_CONTACT_RCP = 3;

    private Context context;
    private Dialog dialog;

    private String dialogTag;
    private String rawId;

    private RecyclerView recyclerViewOptionMenu;

    //<editor-fold desc="Constructor">
    public OptionMenuDialog(final Context context, String rawId, final int menuType) {
        this.context = context;
        this.rawId = rawId;

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option_menu);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.60);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setGravity(Gravity.TOP | Gravity.RIGHT);

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        recyclerViewOptionMenu = (RecyclerView) dialog.findViewById(R.id.recycler_view_option_menu);

        String[] menus = new String[0];
        switch (menuType) {
            case ALL_CONTACT_NON_RCP:
                menus = new String[]{"Edit", "Delete"};
                break;

            case ALL_CONTACT_RCP:
                menus = new String[]{"Edit", "View in Phonebook", "Rate Profile", "Delete"};
                break;

            case R_CONTACT_RCP:
                menus = new String[]{"Edit", "View in AC", "Rate Profile", "Delete"};
                break;
        }

        recyclerViewOptionMenu.setLayoutManager(new LinearLayoutManager(context));
        OptionMenuAdapter optionMenuAdapter = new OptionMenuAdapter(context, new ArrayList<>
                (Arrays.asList(menus)));
        recyclerViewOptionMenu.setAdapter(optionMenuAdapter);

        recyclerViewOptionMenu.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener
                        .OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        dismissDialog();
                        switch (menuType) {
                            case ALL_CONTACT_NON_RCP:
                                menuAllContactNonRcp(view, position);
                                break;

                            case ALL_CONTACT_RCP:
                                menuAllContactRcp(view, position);
                                break;

                            case R_CONTACT_RCP:
                                menuRContactRcp(view, position);
                                break;
                        }
                    }
                })
        );

    }
    //</editor-fold>

    //<editor-fold desc="Public Methods">
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
    //</editor-fold>

    //<editor-fold desc="Menu Item Click listener">

    private void menuAllContactNonRcp(View view, int position) {
        switch (position) {
            //<editor-fold desc="Edit">
            case 0:
                break;
            //</editor-fold>

            // <editor-fold desc="Delete">
            case 1:
                break;
            //</editor-fold>
        }
    }

    private void menuAllContactRcp(View view, int position) {
        switch (position) {
            //<editor-fold desc="Edit">
            case 0:
                break;
            //</editor-fold>

            // <editor-fold desc="View in Phonebook">
            case 1:
                // In Case of invalid lookup key
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts
                            .CONTENT_LOOKUP_URI, rawId);
                    Uri res = ContactsContract.Contacts.lookupContact(context.getContentResolver
                            (), lookupUri);
                    intent.setData(res);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error while retrieving contact raw id", Toast
                            .LENGTH_SHORT).show();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="Rate Profile">
            case 2:
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onRatingClick();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="Delete">
            case 3:
                break;
            //</editor-fold>
        }
    }

    private void menuRContactRcp(View view, int position) {
        switch (position) {
            //<editor-fold desc="Edit">
            case 0:
                break;
            //</editor-fold>

            // <editor-fold desc="View in AC">
            case 1:
                break;
            //</editor-fold>

            // <editor-fold desc="Rate Profile">
            case 2:
                if (context instanceof ProfileDetailActivity) {
                    ((ProfileDetailActivity) context).onRatingClick();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="Delete">
            case 3:
                break;
            //</editor-fold>
        }
    }

    //</editor-fold>

}
