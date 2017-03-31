package com.rawalinfocom.rcontact.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rawalinfocom.rcontact.MainActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 15/02/17.
 */

public class CallLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SectionIndexer {


    private final int HEADER = 0, CALL_LOGS = 1;
    private Context context;
    private ArrayList<Object> arrayListCallLogs;
    private ArrayList<String> arrayListCallLogHeader;
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
    public CallLogListAdapter(Context context, ArrayList<Object> arrayListCallLogs,
                              ArrayList<String> arrayListCallLogHeader) {
        this.context = context;
        this.arrayListCallLogs = arrayListCallLogs;
        this.arrayListCallLogHeader = arrayListCallLogHeader;
    }

    public CallLogListAdapter(Activity activity, ArrayList<Object> arrayListCallLogs,
                              ArrayList<String> arrayListCallLogHeader) {
        this.mActivity = activity;
        this.arrayListCallLogs = arrayListCallLogs;
        this.arrayListCallLogHeader = arrayListCallLogHeader;
        this.context = activity;
    }


    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case HEADER:
                View v1 = inflater.inflate(R.layout.list_item_call_log_header, parent, false);
                viewHolder = new CallLogHeaderViewHolder(v1);
                break;
            case CALL_LOGS:
                View v2 = inflater.inflate(R.layout.list_item_call_log_list, parent, false);
                viewHolder = new AllCallLogViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case HEADER:
                CallLogHeaderViewHolder contactHeaderViewHolder =
                        (CallLogHeaderViewHolder) holder;
                configureHeaderViewHolder(contactHeaderViewHolder, position);
                break;
            case CALL_LOGS:
                AllCallLogViewHolder contactViewHolder = (AllCallLogViewHolder) holder;
                configureAllContactViewHolder(contactViewHolder, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayListCallLogs.get(position) instanceof CallLogType) {
            return CALL_LOGS;
        } else if (arrayListCallLogs.get(position) instanceof String) {
            return HEADER;
        }
        return -1;
    }

    @Override
    public int getItemCount() {

        return arrayListCallLogs.size();
    }

    @Override
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
    }
    //</editor-fold>

    //<editor-fold desc="Private Public Methods">
    @SuppressLint("SimpleDateFormat")

    private void configureAllContactViewHolder(final AllCallLogViewHolder holder, final int
            position) {

        final CallLogType callLogType = (CallLogType) arrayListCallLogs.get(position);
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
                holder.textContactNumber.setText("Unsaved,");
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
                holder.textContactNumber.setText("Unsaved,");
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

        int logCount = callLogType.getHistoryLogCount();
        if (logCount > 0) {
            holder.textCount.setText("(" + logCount + "" + ")");
        } else {
            holder.textCount.setText(" ");
        }

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
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList("Call " + name, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number), context.getString(R.string.call_reminder), context.getString(R.string.unblock)));
                        } else {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList("Call " + name, context.getString(R.string.send_sms),
                                    context.getString(R.string.remove_from_call_log), context.getString(R.string.copy_phone_number),
                                    context.getString(R.string.call_reminder), context.getString(R.string.unblock)));
                        }

                        materialListDialog = new MaterialListDialog(context, arrayListForKnownContact, number, date, name, "",
                                key);
                        materialListDialog.setDialogTitle(name);
                        materialListDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(number)) {
                            String formatedNumber = Utils.getFormattedNumber(context, number);
                            arrayListForUnknownContact = new ArrayList<>(Arrays.asList("Call " + formatedNumber, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number), context.getString(R.string.call_reminder),
                                    context.getString(R.string.unblock)));

                            materialListDialog = new MaterialListDialog(context, arrayListForUnknownContact, number, date, "", uniqueRowID,
                                    key);
                            materialListDialog.setDialogTitle(number);
                            materialListDialog.setCallingAdapter(CallLogListAdapter.this);
                            materialListDialog.showDialog();
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(name)) {
                        Pattern numberPat = Pattern.compile("\\d+");
                        Matcher matcher1 = numberPat.matcher(name);
                        if (matcher1.find()) {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList("Call " + name, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number), context.getString(R.string.call_reminder), context.getString(R.string.block)));
                        } else {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList("Call " + name, context.getString(R.string.send_sms),
                                    context.getString(R.string.remove_from_call_log), context.getString(R.string.copy_phone_number),
                                    context.getString(R.string.call_reminder), context.getString(R.string.block)));
                        }

                        materialListDialog = new MaterialListDialog(context, arrayListForKnownContact, number, date, name, "", "");
                        materialListDialog.setDialogTitle(name);
                        materialListDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(number)) {
                            String formatedNumber = Utils.getFormattedNumber(context, number);
                            arrayListForUnknownContact = new ArrayList<>(Arrays.asList("Call " + formatedNumber, context.getString(R.string.add_to_contact),
                                    context.getString(R.string.add_to_existing_contact)
                                    , context.getString(R.string.send_sms), context.getString(R.string.remove_from_call_log),
                                    context.getString(R.string.copy_phone_number), context.getString(R.string.call_reminder), context.getString(R.string.block)));

                            materialListDialog = new MaterialListDialog(context, arrayListForUnknownContact, number, date, "", uniqueRowID,
                                    "");
                            materialListDialog.setDialogTitle(number);
                            materialListDialog.setCallingAdapter(CallLogListAdapter.this);
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
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        /*holder.relativeRowMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.relativeRowMain.setClickable(false);
                holder.relativeRowMain.setEnabled(false);
                nr++;
                setNewSelection(position, true);
                holder.imageProfile.setBackgroundResource(R.drawable.image_background_delete);
                mActionMode = ((AppCompatActivity) mActivity).startActionMode(new MyActionModeCallback());
                return false;
            }
        });*/

       /* if (mSelection.get(position) != null) {
            holder.imageProfile.setBackgroundResource(R.drawable.image_background_delete);
//            holder.imageProfile.setBackgroundResource(R.drawable.rcontacticon);

        }else {
            holder.imageProfile.setBackgroundResource(R.drawable.rcontacticon);
//            holder.imageProfile.setBackgroundResource(R.drawable.image_background_delete);


        }*/

    }

    private void configureHeaderViewHolder(CallLogHeaderViewHolder holder, int
            position) {
        String date = (String) arrayListCallLogs.get(position);
        holder.textHeader.setText(date);
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


        }
    }

    public class CallLogHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_header)
        TextView textHeader;

        CallLogHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            textHeader.setTypeface(Utils.typefaceSemiBold(context));

        }
    }

//</editor-fold>

    class MyActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            nr = 0;
            mode.getMenuInflater().inflate(R.menu.menu_delete_call_log, menu);
            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(nr + " Selected");
            return false;
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    Toast.makeText(mActivity, "Delete clicked", Toast.LENGTH_SHORT).show();
                    nr = 0;
                    clearSelection();
                    mode.finish();
                    return true;

                case R.id.selectAll:
                    Toast.makeText(mActivity, "Select All clicked", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            clearSelection();
        }
    }

    class temp implements AbsListView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            if (checked) {
                nr++;
                setNewSelection(position, checked);
            } else {
                nr--;
                removeSelection(position);
            }
            mode.setTitle(nr + " selected");

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            nr = 0;
            mode.getMenuInflater().inflate(R.menu.menu_delete_call_log, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    Toast.makeText(mActivity, "Delete clicked", Toast.LENGTH_SHORT).show();
                    nr = 0;
                    clearSelection();
                    mode.finish();
                    return true;

                case R.id.selectAll:
                    Toast.makeText(mActivity, "Select All clicked", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelection();
        }
    }
}
