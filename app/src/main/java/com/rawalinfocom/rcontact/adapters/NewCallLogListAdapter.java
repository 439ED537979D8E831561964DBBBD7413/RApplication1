package com.rawalinfocom.rcontact.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 02/05/17.
 */

public class NewCallLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CALL_LOGS = 1, FOOTER = 2;
    private Context context;
    private ArrayList<CallLogType> arrayListCallLogs;
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

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public CallLogType getSelectedCallLogData() {
        return selectedCallLogData;
    }

    public long getSelectedLogDate() {
        return selectedLogDate;
    }

    //<editor-fold desc="Constructor">
    public NewCallLogListAdapter(Context context, ArrayList<CallLogType> arrayListCallLogs) {
        this.context = context;
        this.arrayListCallLogs = arrayListCallLogs;
    }

    public NewCallLogListAdapter(Activity activity, ArrayList<CallLogType> arrayListCallLogs) {
        this.mActivity = activity;
        this.arrayListCallLogs = arrayListCallLogs;
        this.context = activity;
    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case CALL_LOGS:
                View v2 = inflater.inflate(R.layout.list_item_call_log_list, parent, false);
                viewHolder = new NewCallLogListAdapter.AllCallLogViewHolder(v2);
                break;
            case FOOTER:
                View v3 = inflater.inflate(R.layout.footer_all_call_logs, parent, false);
                viewHolder = new NewCallLogListAdapter.ContactFooterViewHolder(v3);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case CALL_LOGS:
                NewCallLogListAdapter.AllCallLogViewHolder contactViewHolder = (NewCallLogListAdapter.AllCallLogViewHolder) holder;
                configureAllContactViewHolder(contactViewHolder, position);
                break;
            case FOOTER:
                NewCallLogListAdapter.ContactFooterViewHolder contactFooterViewHolder = (NewCallLogListAdapter.ContactFooterViewHolder) holder;
                configureFooterViewHolder(contactFooterViewHolder);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == arrayListCallLogs.size()) {
            return FOOTER;
        } else if (arrayListCallLogs.get(position) != null) {
            return CALL_LOGS;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return arrayListCallLogs.size();
    }

    /*@Override
    public Object[] getSections() {
        return arrayListCallLogHeader.toArray(new String[arrayListCallLogHeader.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return previousPosition;
    }*/
    //</editor-fold>

    //<editor-fold desc="Private Public Methods">
    @SuppressLint("SimpleDateFormat")

    private void configureAllContactViewHolder(final NewCallLogListAdapter.AllCallLogViewHolder holder, final int
            position) {

        final CallLogType callLogType = arrayListCallLogs.get(position);
        final String name = callLogType.getName();
        final String number = callLogType.getNumber();
        final String uniqueRowID = callLogType.getUniqueContactId();

        if (!TextUtils.isEmpty(number)) {
            holder.textTempNumber.setText(number);
        }
        if (!TextUtils.isEmpty(name)) {
            holder.textContactName.setTypeface(Utils.typefaceBold(context));
            holder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                    .colorBlack));
            holder.textContactName.setText(name);
            Pattern numberPat = Pattern.compile("\\d+");
            Matcher matcher1 = numberPat.matcher(name);
            if (matcher1.find()) {
                holder.textContactNumber.setText(context.getString(R.string.str_unsaved));
            } else {
                String formattedNumber = Utils.getFormattedNumber(context, number);
                holder.textContactNumber.setText(formattedNumber + ",");
            }

        } else {
            if (!TextUtils.isEmpty(number)) {
                holder.textContactName.setTypeface(Utils.typefaceBold(context));
                holder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                        .colorBlack));
                String formattedNumber = Utils.getFormattedNumber(context, number);
                holder.textContactName.setText(formattedNumber);
                holder.textContactNumber.setText(context.getString(R.string.str_unsaved));
            } else {
                holder.textContactName.setText(" ");
            }
        }


        final long date = callLogType.getDate();
        Date dateFromReceiver1 = callLogType.getCallReceiverDate();
        if (dateFromReceiver1 != null) {
            dateFromReceiver = dateFromReceiver1.getTime();
        }

        if (date > 0) {
            Date date1 = new Date(date);
            String logDate = new SimpleDateFormat("hh:mm a").format(date1);
            holder.textContactDate.setText(logDate);
        } else {
            Date callDate = callLogType.getCallReceiverDate();
            String callReceiverDate = new SimpleDateFormat("hh:mm a").format(callDate);
            holder.textContactDate.setText(callReceiverDate);
        }


        int blockedType = callLogType.getBlockedType();

        if (blockedType > 0) {
            holder.imageCallType.setImageResource(R.drawable.ic_block);
        } else {

            int callType = callLogType.getType();
            if (callType > 0) {
                switch (callType) {
                    case AppConstants.INCOMING:
                        holder.imageCallType.setImageResource(R.drawable.ic_call_incoming);
                        break;
                    case AppConstants.OUTGOING:
                        holder.imageCallType.setImageResource(R.drawable.ic_call_outgoing);
                        break;
                    case AppConstants.MISSED:
                        holder.imageCallType.setImageResource(R.drawable.ic_call_missed);
                        break;
                    default:
                        break;

                }
            }
        }

      /*  int logCount = callLogType.getHistoryLogCount();
        if (logCount > 0) {
            holder.textCount.setText("(" + logCount + "" + ")");
        } else {
            holder.textCount.setText(" ");
        }*/

        boolean isDual = AppConstants.isDualSimPhone();
        String simNumber;
        simNumber = callLogType.getCallSimNumber();
        if (isDual) {
            if (!TextUtils.isEmpty(simNumber)) {
                if (simNumber.equalsIgnoreCase("2")) {
                    holder.textSimType.setTextColor(ContextCompat.getColor(context, R.color
                            .darkCyan));
                    holder.textSimType.setText(context.getString(R.string.im_sim_2));
                    holder.textSimType.setTypeface(Utils.typefaceIcons(context));
                } else {
                    holder.textSimType.setTextColor(ContextCompat.getColor(context, R.color
                            .vividBlue));
                    holder.textSimType.setText(context.getString(R.string.im_sim_1));
                    holder.textSimType.setTypeface(Utils.typefaceIcons(context));
                }
            } else {
                holder.textSimType.setVisibility(View.GONE);
            }

        } else {

            holder.textSimType.setVisibility(View.GONE);

        }

        final String thumbnailUrl = callLogType.getProfileImage();
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(200, 200)
                    .into(holder.imageProfile);

        } else {
            holder.imageProfile.setImageResource(R.drawable.home_screen_profile);
        }

        holder.image3dotsCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = position;
                selectedCallLogData = callLogType;
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
                        Log.i("value", callLogTypeList.get(j) + "");
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
                        if (matcher1.find()) {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                            + " " + name, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number)/*,context.getString(R.string.call_reminder),*/ /*context.getString(R.string.unblock)*/));
                        } else {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                            + " " +  name, context.getString(R.string.send_sms),
                                    context.getString(R.string.remove_from_call_log), context.getString(R.string.copy_phone_number)/*,
                                    context.getString(R.string.call_reminder), context.getString(R.string.unblock)*/));
                        }

                        materialListDialog = new MaterialListDialog(context, arrayListForKnownContact, number, date, name, "",
                                key);
                        materialListDialog.setDialogTitle(name);
                        materialListDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(number)) {
                            String formatedNumber = Utils.getFormattedNumber(context, number);
                            arrayListForUnknownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                            + " " +  formatedNumber, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number)/*,context.getString(R.string.call_reminder),
                                    context.getString(R.string.unblock)*/));

                            materialListDialog = new MaterialListDialog(context, arrayListForUnknownContact, number, date, "", uniqueRowID,
                                    key);
                            materialListDialog.setDialogTitle(number);
                            materialListDialog.setCallingAdapter(NewCallLogListAdapter.this);
                            materialListDialog.showDialog();
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(name)) {
                        Pattern numberPat = Pattern.compile("\\d+");
                        Matcher matcher1 = numberPat.matcher(name);
                        if (matcher1.find()) {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                            + " " + name, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number)/*,context.getString(R.string.call_reminder), context.getString(R.string.block)*/));
                        } else {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                            + " " +  name, context.getString(R.string.send_sms),
                                    context.getString(R.string.remove_from_call_log), context.getString(R.string.copy_phone_number)/*,
                                    context.getString(R.string.call_reminder), context.getString(R.string.block)*/));
                        }

                        materialListDialog = new MaterialListDialog(context, arrayListForKnownContact, number, date, name, "", "");
                        materialListDialog.setDialogTitle(name);
                        materialListDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(number)) {
                            String formatedNumber = Utils.getFormattedNumber(context, number);
                            arrayListForUnknownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                            + " " + formatedNumber, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number)/*,context.getString(R.string.call_reminder), context.getString(R.string.block)*/));

                            materialListDialog = new MaterialListDialog(context, arrayListForUnknownContact, number, date, "", uniqueRowID,
                                    "");
                            materialListDialog.setDialogTitle(number);
                            materialListDialog.setCallingAdapter(NewCallLogListAdapter.this);
                            materialListDialog.showDialog();
                        }
                    }
                }

            }
        });

        holder.relativeRowMain.setClickable(true);
        holder.relativeRowMain.setEnabled(true);
        holder.relativeRowMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = position;
                selectedCallLogData = callLogType;
                String key = "";
                key = callLogType.getLocalPbRowId();
                if (key.equalsIgnoreCase(" ")) {
                    key = callLogType.getUniqueContactId();
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
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NAME, name);
                if (date == 0) {
                    intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, dateFromReceiver);
                } else {
                    intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, date);
                }
                intent.putExtra(AppConstants.EXTRA_CALL_UNIQUE_ID, key);
                intent.putExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID, uniqueRowID);
                intent.putExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE, thumbnailUrl);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

    }

    private void configureFooterViewHolder(NewCallLogListAdapter.ContactFooterViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);

    }

    public class ContactFooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        ContactFooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //</editor-fold>

    //<editor-fold desc="View Holder">
    public class AllCallLogViewHolder extends RecyclerView.ViewHolder {

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

        AllCallLogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            textSimType.setVisibility(View.GONE);
        }
    }
//</editor-fold>
}
