package com.rawalinfocom.rcontact.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

    private ArrayList<String> arrayListForKnownContact ;
    private ArrayList<String> arrayListForUnknownContact ;
    MaterialListDialog materialListDialog;
    HashMap<String,Integer> historyCountMap;
    private String number = "";


    //<editor-fold desc="Constructor">
    public CallLogListAdapter(Context context, ArrayList<Object> arrayListCallLogs,
                              ArrayList<String> arrayListCallLogHeader, HashMap<String,Integer> map) {
        this.context = context;
        this.arrayListCallLogs = arrayListCallLogs;
        this.arrayListCallLogHeader = arrayListCallLogHeader;
        this.historyCountMap =  map;
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
                viewHolder = new CallLogListAdapter.CallLogHeaderViewHolder(v1);
                break;
            case CALL_LOGS:
                View v2 = inflater.inflate(R.layout.list_item_call_log_list, parent, false);
                viewHolder = new CallLogListAdapter.AllCallLogViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case HEADER:
                CallLogListAdapter.CallLogHeaderViewHolder contactHeaderViewHolder =
                        (CallLogListAdapter.CallLogHeaderViewHolder) holder;
                configureHeaderViewHolder(contactHeaderViewHolder, position);
                break;
            case CALL_LOGS:
                CallLogListAdapter.AllCallLogViewHolder contactViewHolder = (CallLogListAdapter
                        .AllCallLogViewHolder) holder;
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

        CallLogType callLogType = (CallLogType) arrayListCallLogs.get(position);
        final String name = callLogType.getName();
        final String number =  callLogType.getNumber();
        if(!TextUtils.isEmpty(number)){
            holder.textTempNumber.setText(number);
        }
        if(!TextUtils.isEmpty(name))
        {
            holder.textContactName.setTypeface(Utils.typefaceBold(context));
            holder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                    .colorBlack));
            holder.textContactName.setText(name);
        }else
        {
            if (!TextUtils.isEmpty(number)) {
                holder.textContactName.setTypeface(Utils.typefaceBold(context));
                holder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                        .colorBlack));
                String formattedNumber = Utils.getFormattedNumber(context, number);
                holder.textContactName.setText(formattedNumber);
            } else {
                holder.textContactName.setText(" ");

            }
        }
        final long date = callLogType.getDate();
        if (date > 0) {
            Date date1 = new Date(date);
            String logDate = new SimpleDateFormat("MMMM dd, hh:mm a").format(date1);
            holder.textContactDate.setText(logDate);
        } else {
            String callReceiverDate = callLogType.getLogDate();
            holder.textContactDate.setText(callReceiverDate);
        }
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
        int hashMapValue =0;
        for(String key : historyCountMap.keySet()){
            hashMapValue = historyCountMap.get(key);
            Log.d("Key Value",key+" "+hashMapValue);
            if(key.equalsIgnoreCase(number)){
                hashMapValue = historyCountMap.get(number);
                if(hashMapValue >0 ){
                    holder.textCount.setText("(" + hashMapValue + "" + ")");
                    break;
                }else {
                    holder.textCount.setText(" ");
                    break;
                }
            }

        }
      /*  for(Map.Entry<String, Integer> entry : historyCountMap.entrySet())
        {
            int hashMapValue = entry.getValue();
            if (hashMapValue > 0) {
                holder.textCount.setText("(" + hashMapValue + "" + ")");
            } else {
                holder.textCount.setText(" ");
            }
        }
*/
        /*Iterator<Map.Entry<String, Integer>> iterator = historyCountMap.entrySet().iterator();
        Map.Entry< String, Integer> entry;
        while(iterator.hasNext()){
            entry = iterator.next();
            Log.d("Key Value",entry.getKey()+" "+entry.getValue());
            int hashMapValue = entry.getValue();
            if (hashMapValue > 0) {
                holder.textCount.setText("(" + hashMapValue + "" + ")");
            } else {
                holder.textCount.setText(" ");
            }
        }*/


       /* int logCount = hashMapValue;
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
        holder.text3dotsCallLog.setTypeface(Utils.typefaceIcons(context));

        holder.text3dotsCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(name)){
                    Pattern numberPat = Pattern.compile("\\d+");
                    Matcher matcher1 = numberPat.matcher(name);
                    if (matcher1.find()) {
                        arrayListForKnownContact=  new ArrayList<>(Arrays.asList("Call " + name,context.getString(R.string.add_to_contact),
                                context.getString(R.string.add_to_existing_contact)
                                ,context.getString(R.string.send_sms),context.getString(R.string.remove_from_call_log),
                                context.getString(R.string.copy_phone_number),context.getString(R.string.call_reminder),context.getString(R.string.block)));
                    } else {
                        arrayListForKnownContact = new ArrayList<>(Arrays.asList("Call " + name ,context.getString(R.string.send_sms),
                                context.getString(R.string.remove_from_call_log), context.getString(R.string.copy_phone_number),
                                context.getString(R.string.call_reminder),context.getString(R.string.block)));
                    }

                    materialListDialog = new MaterialListDialog(context,arrayListForKnownContact,number);
                    materialListDialog.setDialogTitle(name);
                    materialListDialog.showDialog();

                }else{
                    if(!TextUtils.isEmpty(number)){
                        String formatedNumber =  Utils.getFormattedNumber(context,number);
                        arrayListForUnknownContact = new ArrayList<>(Arrays.asList("Call " + formatedNumber,context.getString(R.string.add_to_contact),
                                context.getString(R.string.add_to_existing_contact)
                                ,context.getString(R.string.send_sms),context.getString(R.string.remove_from_call_log),
                                context.getString(R.string.copy_phone_number),context.getString(R.string.call_reminder),context.getString(R.string.block)));
                        materialListDialog = new MaterialListDialog(context,arrayListForUnknownContact,number);
                        materialListDialog.setDialogTitle(number);
                        materialListDialog.showDialog();
                    }
                }
            }
        });

        holder.relativeRowMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConstants.isFromReceiver = false;
                Intent intent = new Intent(context, ProfileDetailActivity.class);
                intent.putExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, true);
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER, number);
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NAME, name);
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE,date);
                context.startActivity(intent);
            }
        });

    }

    private void configureHeaderViewHolder(CallLogListAdapter.CallLogHeaderViewHolder holder, int
            position) {
        String date = (String) arrayListCallLogs.get(position);
        holder.textHeader.setText(date);
    }
    //</editor-fold>

    //<editor-fold desc="View Holder">
    public class AllCallLogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.text_3dots_call_log)
        TextView text3dotsCallLog;
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


}
