package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.rawalinfocom.rcontact.BuildConfig;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 08/09/17.
 */

public class MyProfileShareListAdapter extends RecyclerView.Adapter<MyProfileShareListAdapter
        .MaterialViewHolder> {


    private Context context;
    private ArrayList<String> arrayListString;
    String pmID;
    private DatabaseHandler databaseHandler;
    ProfileDataOperation profileDataOperationVcard;
    String contactName;
    UserProfile userProfile;
    Activity mActivity;

    public MyProfileShareListAdapter(Context context, ArrayList<String> arrayList, String pmId,
                                     ProfileDataOperation profileDataOperationVcard,String contactName,
                                     Activity activity) {
        this.context = context;
        this.arrayListString = arrayList;
        this.pmID = pmId;
        databaseHandler = new DatabaseHandler(context);
        this.profileDataOperationVcard = profileDataOperationVcard;
        if(!StringUtils.isEmpty(contactName))
            this.contactName =  contactName;
        this.mActivity = activity;

    }

    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .list_item_dialog_call_log,
                parent, false);
        return new MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, final int
            position) {
        final String value = arrayListString.get(position);
        holder.textItemValue.setText(value);

        holder.rippleRow.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                String number = "";
                if (!StringUtils.equalsAnyIgnoreCase(pmID, "-1")) {
                    TableProfileMaster tableProfileMaster = new TableProfileMaster
                            (databaseHandler);
                    userProfile = tableProfileMaster.getProfileFromCloudPmId
                            (Integer.parseInt(pmID));
                    TableMobileMaster tableMobileMaster = new TableMobileMaster
                            (databaseHandler);
                    number = tableMobileMaster.getUserMobileNumber(pmID);

                    if (StringUtils.startsWith(number, "+")) {
                        number = StringUtils.substring(number, 1);
                    }
                }

                if(value.equalsIgnoreCase(context.getString(R.string.average_rate_sharing))){
                    if (!StringUtils.equalsAnyIgnoreCase(pmID, "-1")){
                        String sharingUrl = "Click link to see " + contactName + " 's average rating." + "\n"
                                + WsConstants.WS_AVG_RATING_SHARE_BADGE_ROOT + userProfile.getPmBadge();
                        if(Utils.isNetworkAvailable(context))
                            shareAverageRating(sharingUrl);
                        else{
//                            Utils.showErrorSnackBar(context, llRoot, context.getResources()
//                                    .getString(R.string.msg_no_network));
                            Intent localBroadcastIntent = new Intent(AppConstants
                                    .ACTION_LOCAL_BROADCAST_DIALOG);
                            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                                    (context);
                            localBroadcastIntent.putExtra("networkIssue","true");
                            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                        }
                    }

                }else if(value.equalsIgnoreCase(context.getString(R.string.my_profile_share))){
                    if (!StringUtils.equalsAnyIgnoreCase(pmID, "-1")){
                        // RCP profile or Own Profile
                        if(Utils.isNetworkAvailable(context))
                        {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            String shareBody;
                            if (StringUtils.isBlank(userProfile.getPmBadge())) {
                                shareBody = WsConstants.WS_PROFILE_VIEW_BADGE_ROOT + number;
                            } else {
                                shareBody = WsConstants.WS_PROFILE_VIEW_BADGE_ROOT + userProfile
                                        .getPmBadge();
                            }
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                            context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string
                                    .str_share_contact_via)));

                        }else{
                            Intent localBroadcastIntent = new Intent(AppConstants
                                    .ACTION_LOCAL_BROADCAST_DIALOG);
                            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                                    (context);
                            localBroadcastIntent.putExtra("networkIssue","true");
                            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);                        }
                    }else{
                        shareContact();
                    }

                }
                Intent localBroadcastIntent = new Intent(AppConstants
                        .ACTION_LOCAL_BROADCAST_DIALOG);
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

    private void shareContact() {
        if (Utils.isNetworkAvailable(context)) {
            WsRequestObject uploadContactObject = new WsRequestObject();
            uploadContactObject.setSendProfileType(IntegerConstants.SEND_PROFILE_NON_RCP_SOCIAL);
            uploadContactObject.setContactData(profileDataOperationVcard);

            new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_RCP_PROFILE_SHARING, context.getResources().getString(R.string.msg_please_wait),
                    true, new WsResponseListener() {
                @Override
                public void onDeliveryResponse(String serviceType, Object data, Exception error) {
                    // <editor-fold desc="REQ_RCP_PROFILE_SHARING">
                    if (serviceType.equalsIgnoreCase(WsConstants.REQ_RCP_PROFILE_SHARING)) {
                        WsResponseObject profileSharingResponse = (WsResponseObject) data;
                        Utils.hideProgressDialog();
                        if (profileSharingResponse != null && StringUtils.equalsIgnoreCase
                                (profileSharingResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                            File vcfFile = new File(context.getExternalFilesDir(null), contactName + ".vcf");
                            FileWriter fw;
                            try {
                                fw = new FileWriter(vcfFile);
                                fw.write(profileSharingResponse.getProfileSharingData());
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                        StrictMode.setVmPolicy(builder.build());
//                    }

                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.setType("text/x-vcard");
                            sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    vcfFile));
                            context.startActivity(sendIntent);

                        } else {
                            if (profileSharingResponse != null) {
                                Log.e("error response", profileSharingResponse.getMessage());
//                                Utils.showErrorSnackBar(context, llRoot,
//                                        profileSharingResponse.getMessage());
                                Intent localBroadcastIntent = new Intent(AppConstants
                                        .ACTION_LOCAL_BROADCAST_DIALOG);
                                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                                        (context);
                                localBroadcastIntent.putExtra("responseError","true");
                                localBroadcastIntent.putExtra("responseMessage",profileSharingResponse.getMessage());
                                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                            } else {
                                Log.e("onDeliveryResponse: ", "otpDetailResponse null");
//                                Utils.showErrorSnackBar(context, llRoot, context.getString(R
//                                        .string.msg_try_later));
                                Intent localBroadcastIntent = new Intent(AppConstants
                                        .ACTION_LOCAL_BROADCAST_DIALOG);
                                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                                        (context);
                                localBroadcastIntent.putExtra("serverError","true");
                                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                            }
                        }

                    }
                    //</editor-fold>
                }
            }).execute(
                    WsConstants.WS_ROOT + WsConstants.REQ_RCP_PROFILE_SHARING);

        } else {
//            Utils.showErrorSnackBar(context, llRoot, context.getResources()
//                    .getString(R.string.msg_no_network));
            Intent localBroadcastIntent = new Intent(AppConstants
                    .ACTION_LOCAL_BROADCAST_DIALOG);
            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                    (context);
            localBroadcastIntent.putExtra("networkIssue","true");
            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

        }
    }

    private void shareAverageRating(String url) {

        PackageManager pm = mActivity.getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/*");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana")
                    || packageName.contains("com.linkedin.android")) {
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                if (packageName.contains("com.twitter.android")) {
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                } else if (packageName.contains("com.facebook.katana")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.TEXT", url);


                } else if (packageName.contains("com.linkedin.android")) {
                    // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, url);
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }

        }

        if(resInfo.size()>0){
            // convert intentList to array
            LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
            Intent openInChooser = Intent.createChooser(intent, context.getString(R.string.share_rating_via_social_media));
            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            context.startActivity(openInChooser);
        }else{
            // NO App Found
            Intent localBroadcastIntent = new Intent(AppConstants
                    .ACTION_LOCAL_BROADCAST_DIALOG);
            LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance
                    (context);
            localBroadcastIntent.putExtra("noApps","true");
            myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

        }

    }

}
