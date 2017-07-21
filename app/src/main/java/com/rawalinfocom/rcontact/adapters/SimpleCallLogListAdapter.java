package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.CallLogType;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 01/05/17.
 */

public class SimpleCallLogListAdapter extends RecyclerView.Adapter<SimpleCallLogListAdapter.CallLogViewHolder> {

    private Context context;
    private ArrayList<CallLogType> arrayListCallLogs;
    String address;
    private int previousPosition = 0;

    private ArrayList<String> arrayListForKnownContact;
    private ArrayList<String> arrayListForUnknownContact;
    MaterialListDialog materialListDialog;
    private String number = "";
    private int selectedPosition = 0;
    public ActionMode mActionMode;
    Activity mActivity;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    private int nr = 0;
    CallLogType selectedCallLogData;
    long selectedLogDate = 0;
    long dateFromReceiver;
    String formattedNumber = "";
    ArrayList<CallLogType> arrayList;
    private int searchCount;

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public CallLogType getSelectedCallLogData() {
        return selectedCallLogData;
    }

    public long getSelectedLogDate() {
        return selectedLogDate;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    public ArrayList<CallLogType> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<CallLogType> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<CallLogType> getArrayListCallLogs() {
        return arrayListCallLogs;
    }

    public void setArrayListCallLogs(ArrayList<CallLogType> arrayListCallLogs) {
        this.arrayListCallLogs = arrayListCallLogs;
    }

    public SimpleCallLogListAdapter(Context context, ArrayList<CallLogType> callLogTypes) {
        this.context = context;
        if (AppConstants.isFromSearchActivity) {
            this.arrayListCallLogs = new ArrayList<>();
            this.arrayListCallLogs.addAll(callLogTypes);
        } else {
            this.arrayListCallLogs = callLogTypes;
        }
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(arrayListCallLogs);

    }

    @Override
    public CallLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_call_log_list,
                parent, false);
        return new CallLogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CallLogViewHolder contactViewHolder, int position) {
        final CallLogType callLogType = arrayListCallLogs.get(position);
        final String name = callLogType.getName();
        final String number = callLogType.getNumber();
        if (!TextUtils.isEmpty(number)) {
            formattedNumber = Utils.getFormattedNumber(context, number);
        }
        final String uniqueRowID = callLogType.getUniqueContactId();

        if (!TextUtils.isEmpty(number)) {
            contactViewHolder.textTempNumber.setText(formattedNumber);
        }
        if (!TextUtils.isEmpty(name)) {
            contactViewHolder.textContactName.setTypeface(Utils.typefaceBold(context));
            contactViewHolder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                    .colorBlack));
            if (MoreObjects.firstNonNull(callLogType.isRcpUser(), false)) {
                String contactDisplayName = "";
                String firstName = callLogType.getRcpFirstName();
                String lastName = callLogType.getRcpLastName();

                if (StringUtils.length(firstName) > 0) {
                    contactDisplayName = contactDisplayName + firstName + " ";
                }

                if (StringUtils.length(lastName) > 0) {
                    contactDisplayName = contactDisplayName + lastName + "";
                }

                if (StringUtils.equalsIgnoreCase(name, contactDisplayName)) {
                    contactViewHolder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                            .colorAccent));
                    contactViewHolder.textCloudContactName.setVisibility(View.GONE);
                    contactViewHolder.textContactName.setText(name);
                } else {
                    contactViewHolder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                            .colorBlack));
                    contactViewHolder.textCloudContactName.setVisibility(View.VISIBLE);
                    contactViewHolder.textCloudContactName.setTextColor(ContextCompat.getColor(context, R.color
                            .colorAccent));
                    contactViewHolder.textContactName.setText(String.format("%s ", name));
                    contactViewHolder.textCloudContactName.setText("(" + contactDisplayName + ")");
                }

            } else {
                contactViewHolder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                        .colorBlack));
                contactViewHolder.textCloudContactName.setVisibility(View.GONE);
                contactViewHolder.textContactName.setText(name);
            }
            Pattern numberPat = Pattern.compile("\\d+");
            Matcher matcher1 = numberPat.matcher(name);
            if (StringUtils.containsOnly(name, "\\d+")) {
                contactViewHolder.textContactNumber.setText(context.getString(R.string.str_unsaved));
            } else {
                contactViewHolder.textContactNumber.setText(String.format("%s,", formattedNumber));
            }

        } else {
            contactViewHolder.textCloudContactName.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(number)) {
                contactViewHolder.textContactName.setTypeface(Utils.typefaceBold(context));
                contactViewHolder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                        .colorBlack));
                contactViewHolder.textContactName.setText(formattedNumber);
                contactViewHolder.textContactNumber.setText(context.getString(R.string.str_unsaved));
            } else {
                contactViewHolder.textContactName.setText(" ");
            }
        }

        final long date = callLogType.getDate();
//        long logDate1 = callLogType.getDate();
        Date date1 = new Date(date);
        String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date1);
//        Log.i("Call Log date", logDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesDate;
        yesDate = cal.getTime();
        String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesDate);
//        Log.i("Call yesterday date", yesterdayDate);

        Calendar c = Calendar.getInstance();
        Date cDate = c.getTime();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cDate);
//        Log.i("Call Current date", currentDate);
        Date dateFromReceiver1 = callLogType.getCallReceiverDate();
        if (dateFromReceiver1 != null) {
            dateFromReceiver = dateFromReceiver1.getTime();
        }
        String finalDate = "";
        if (date > 0) {
            Date dateCallLog = new Date(date);
            if (logDate.equalsIgnoreCase(currentDate)) {
                finalDate = context.getString(R.string.str_today) + ", " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(dateCallLog);
            } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
                finalDate = context.getString(R.string.str_yesterday) + ", " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(dateCallLog);
            } else {
                finalDate = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(dateCallLog);
            }
            contactViewHolder.textContactDate.setText(finalDate);
        } else {
            Date callDate = callLogType.getCallReceiverDate();
            if (callDate != null) {
                if (logDate.equalsIgnoreCase(currentDate)) {
                    finalDate = context.getString(R.string.str_today) + ", " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(callDate);
                } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
                    finalDate = context.getString(R.string.str_yesterday) + ", " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(callDate);
                } else {
                    finalDate = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(callDate);
                }
                contactViewHolder.textContactDate.setText(finalDate);
            }
        }


        int blockedType = callLogType.getBlockedType();

        if (blockedType > 0) {
            contactViewHolder.imageCallType.setImageResource(R.drawable.ic_block);
        } else {

            int callType = callLogType.getType();
            if (callType > 0) {
                switch (callType) {
                    case AppConstants.INCOMING:
                        contactViewHolder.imageCallType.setImageResource(R.drawable.ic_call_incoming);
                        break;
                    case AppConstants.OUTGOING:
                        contactViewHolder.imageCallType.setImageResource(R.drawable.ic_call_outgoing);
                        break;
                    case AppConstants.MISSED:
                        contactViewHolder.imageCallType.setImageResource(R.drawable.ic_call_missed);
                        break;
                    default:
                        break;

                }
            }
        }

        boolean isDual = AppConstants.isDualSimPhone();
        String simNumber;
        simNumber = callLogType.getCallSimNumber();
        if (isDual) {
            if (!TextUtils.isEmpty(simNumber)) {
                if (simNumber.equalsIgnoreCase("2")) {
                    contactViewHolder.textSimType.setTextColor(ContextCompat.getColor(context, R.color
                            .darkCyan));
                    contactViewHolder.textSimType.setText(context.getString(R.string.im_sim_2));
                    contactViewHolder.textSimType.setTypeface(Utils.typefaceIcons(context));
                } else {
                    contactViewHolder.textSimType.setTextColor(ContextCompat.getColor(context, R.color
                            .vividBlue));
                    contactViewHolder.textSimType.setText(context.getString(R.string.im_sim_1));
                    contactViewHolder.textSimType.setTypeface(Utils.typefaceIcons(context));
                }
            } else {
                contactViewHolder.textSimType.setVisibility(View.GONE);
            }

        } else {

            contactViewHolder.textSimType.setVisibility(View.GONE);

        }

        final String thumbnailUrl = callLogType.getProfileImage();
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(200, 200)
                    .into(contactViewHolder.imageProfile);

        } else {
            contactViewHolder.imageProfile.setImageResource(R.drawable.home_screen_profile);
        }

        contactViewHolder.image3dotsCallLog.setTag(position);
        contactViewHolder.image3dotsCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = (int) v.getTag();

                System.out.println("RContact selectedPosition --> " + selectedPosition);

//                selectedCallLogData = callLogType;
                String blockedNumber = "";
                String key = "";
                key = callLogType.getLocalPbRowId();
                if (key.equalsIgnoreCase(" ")) {
                    key = callLogType.getUniqueContactId();
                }

                ArrayList<CallLogType> callLogTypeList = new ArrayList<CallLogType>();
                HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                        Utils.getHashMapPreferenceForBlock(context, AppConstants.PREF_BLOCK_CONTACT_LIST);

                if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                    if (blockProfileHashMapList.containsKey(key))
                        callLogTypeList.addAll(blockProfileHashMapList.get(key));

                }
                if (callLogTypeList != null) {
                    for (int j = 0; j < callLogTypeList.size(); j++) {
//                        Log.i("value", callLogTypeList.get(j) + "");
                        String tempNumber = callLogTypeList.get(j).getNumber();
                        if (tempNumber.equalsIgnoreCase(number)) {
                            blockedNumber = tempNumber;
                        }
                    }
                }

                if (!TextUtils.isEmpty(blockedNumber)) {
                    if (!TextUtils.isEmpty(name)) {
                        Pattern numberPat = Pattern.compile("\\d+");
                        Matcher matcher1 = numberPat.matcher(name);
                        if (StringUtils.containsOnly(name, "\\d+")) {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                            + " " + name, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number)/*,context.getString(R.string.call_reminder),*/ /*context.getString(R.string.unblock)*/));
                        } else {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                            + " " + name, context.getString(R.string.send_sms),
                                    context.getString(R.string.remove_from_call_log), context.getString(R.string.copy_phone_number)/*,
                                    context.getString(R.string.call_reminder), context.getString(R.string.unblock)*/));
                        }

                        materialListDialog = new MaterialListDialog(context, arrayListForKnownContact, number, date, name, uniqueRowID,
                                key);
                        materialListDialog.setDialogTitle(name);
                        materialListDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(number)) {
                            String formatedNumber = Utils.getFormattedNumber(context, number);
                            arrayListForUnknownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                            + " " + formatedNumber, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number)/*,context.getString(R.string.call_reminder),
                                    context.getString(R.string.unblock)*/));

                            materialListDialog = new MaterialListDialog(context, arrayListForUnknownContact, number, date, "", uniqueRowID,
                                    key);
                            materialListDialog.setDialogTitle(number);
                            materialListDialog.setCallingAdapter(SimpleCallLogListAdapter.this);
                            materialListDialog.showDialog();
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(name)) {
                        /*Pattern numberPat = Pattern.compile("\\d+");
                        Matcher matcher1 = numberPat.matcher(name);
                        if (matcher1.find()) {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)+
                                            " " + name, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number)*//*,context.getString(R.string.call_reminder), context.getString(R.string.block)*//*));
                        } else {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)+
                                            " " + name, context.getString(R.string.send_sms),
                                    context.getString(R.string.remove_from_call_log), context.getString(R.string.copy_phone_number)*//*,
                                    context.getString(R.string.call_reminder), context.getString(R.string.block)*//*));
                        }*/
                        if (StringUtils.containsOnly(name, "\\d+")) {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call) +
                                            " " + name, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number)/*,context.getString(R.string.call_reminder), context.getString(R.string.block)*/));

                        } else {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call) +
                                            " " + name, context.getString(R.string.send_sms),
                                    context.getString(R.string.remove_from_call_log), context.getString(R.string.copy_phone_number)
                                    /*context.getString(R.string.call_reminder), context.getString(R.string.block)*/));
                        }

                        materialListDialog = new MaterialListDialog(context, arrayListForKnownContact, number, date, name, uniqueRowID, "");
                        materialListDialog.setDialogTitle(name);
                        materialListDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(number)) {
                            String formatedNumber = Utils.getFormattedNumber(context, number);
                            arrayListForUnknownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call) +
                                            " " + formatedNumber, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number)/*,context.getString(R.string.call_reminder), context.getString(R.string.block)*/));

                            materialListDialog = new MaterialListDialog(context, arrayListForUnknownContact, number, date, "", uniqueRowID,
                                    "");
                            materialListDialog.setDialogTitle(number);
                            materialListDialog.setCallingAdapter(SimpleCallLogListAdapter.this);
                            materialListDialog.showDialog();
                        }
                    }
                }

            }
        });

        contactViewHolder.relativeRowMain.setTag(position);
        contactViewHolder.relativeRowMain.setClickable(true);
        contactViewHolder.relativeRowMain.setEnabled(true);
        contactViewHolder.relativeRowMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = (int) v.getTag();
                selectedCallLogData = callLogType;
                String key = "";
                key = callLogType.getLocalPbRowId();
                if (key.equalsIgnoreCase(" ")) {
                    key = callLogType.getUniqueContactId();
                }

                Boolean isRcpUser = selectedCallLogData.isRcpUser();
                String firstName = selectedCallLogData.getRcpFirstName();
                String lastName = selectedCallLogData.getRcpLastName();
                String name = selectedCallLogData.getName();
                String cloudName = "";
                String contactDisplayName = "";

                if (MoreObjects.firstNonNull(isRcpUser, false)) {
                    if (StringUtils.length(firstName) > 0) {
                        contactDisplayName = contactDisplayName + firstName + " ";
                    }
                    if (StringUtils.length(lastName) > 0) {
                        contactDisplayName = contactDisplayName + lastName + "";
                    }
                    if (!StringUtils.equalsIgnoreCase(name, contactDisplayName)) {
                        cloudName = contactDisplayName;
                    }
                }

                if (date == 0) {
                    selectedLogDate = dateFromReceiver;
                } else {
                    selectedLogDate = date;
                }
                AppConstants.isFromReceiver = false;
                String formatedNumber = Utils.getFormattedNumber(context, number);
                Intent intent = new Intent(context, ProfileDetailActivity.class);
                intent.putExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, true);
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER, formatedNumber);

                if (selectedCallLogData.getRcpId() == null)
                    intent.putExtra(AppConstants.EXTRA_PM_ID, "-1");
                else
                    intent.putExtra(AppConstants.EXTRA_PM_ID, selectedCallLogData.getRcpId());

                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NAME, name);
                if (date == 0) {
                    intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, dateFromReceiver);
                } else {
                    intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, date);
                }
                intent.putExtra(AppConstants.EXTRA_CALL_UNIQUE_ID, key);
                intent.putExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID, uniqueRowID);
                intent.putExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE, thumbnailUrl);
                intent.putExtra(AppConstants.EXTRA_IS_RCP_USER, isRcpUser);
                intent.putExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME, cloudName);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayListCallLogs.size();
    }

    public class CallLogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.image_3dots_call_log)
        ImageView image3dotsCallLog;
        @BindView(R.id.image_social_media)
        ImageView imageSocialMedia;
        @BindView(R.id.text_contact_name)
        public TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.image_call_type)
        ImageView imageCallType;
        @BindView(R.id.text_contact_date)
        TextView textContactDate;
        @BindView(R.id.text_sim_type)
        TextView textSimType;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;
        @BindView(R.id.linear_rating)
        LinearLayout linearRating;
        @BindView(R.id.linear_content_main)
        LinearLayout linearContentMain;
        @BindView(R.id.relative_row_main)
        RelativeLayout relativeRowMain;
        @BindView(R.id.textCount)
        TextView textCount;
        @BindView(R.id.text_temp_number)
        public TextView textTempNumber;
        @BindView(R.id.text_contact_number)
        TextView textContactNumber;

        CallLogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            textSimType.setVisibility(View.GONE);
            /*if (AppConstants.isFromSearchActivity) {
                image3dotsCallLog.setVisibility(View.GONE);
            } else {
                image3dotsCallLog.setVisibility(View.VISIBLE);
            }*/
            image3dotsCallLog.setVisibility(View.VISIBLE);

        }
    }

    // Filter Class
    public void filter(String charText) {
        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(charText);
        if (matcher1.find()) {
            arrayListCallLogs.clear();
            if (charText.length() == 0) {
                arrayListCallLogs.addAll(arrayList);
            } else {
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof CallLogType) {
                        CallLogType profileData = arrayList.get(i);
                        if (!TextUtils.isEmpty(profileData.getNumber())) {
                            if (profileData.getNumber().contains(charText)) {
                                arrayListCallLogs.add(profileData);
                            }
                        }
                        setArrayList(arrayList);
                    }
                }
            }

        }/*else{
            charText = charText.toLowerCase(Locale.getDefault());
            arrayListCallLogs.clear();
            if (charText.length() == 0) {
                arrayListCallLogs.addAll(arrayList);
            } else {

                for(int i=0; i<arrayList.size(); i++){
                    if(arrayList.get(i) instanceof CallLogType){
                        ProfileData profileData = (CallLogType) arrayList.get(i);
                        if(!TextUtils.isEmpty(profileData.getName())){
                            if (profileData.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                                arrayListCallLogs.add(profileData);
                            }
                        }

                    }
                }
            }
        }*/

        if (arrayListCallLogs.size() > 0) {
            setSearchCount(arrayListCallLogs.size());
            setArrayListCallLogs(arrayListCallLogs);
        }
//        else
//            setSearchCount(arrayList.size());

        notifyDataSetChanged();
    }
}
