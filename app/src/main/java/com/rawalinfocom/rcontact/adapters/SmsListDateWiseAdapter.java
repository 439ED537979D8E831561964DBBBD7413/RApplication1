package com.rawalinfocom.rcontact.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.SmsDataType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;

/**
 * Created by Aniruddh on 21/04/17.
 */

public class SmsListDateWiseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SectionIndexer {


    private final int HEADER = 0, CALL_LOGS = 1;
    private Context context;
    private ArrayList<Object> arrayListCallLogs;
    private ArrayList<String> arrayListCallLogHeader;
    private ArrayList<String> listOfDateToDisplay;
    private int previousPosition = 0;
    private String number = "";
    Activity mActivity;
    SmsDataType selectedSmsType;
    int selectedPosition = -1;
    private ArrayList<String> arrayListForKnownContact;
    private ArrayList<String> arrayListForUnknownContact;

    //<editor-fold desc="Constructor">
    public SmsListDateWiseAdapter(Context context, ArrayList<Object> arrayListCallLogs,
                                  ArrayList<String> arrayListCallLogHeader) {
        this.context = context;
        this.arrayListCallLogs = arrayListCallLogs;
        this.arrayListCallLogHeader = arrayListCallLogHeader;
    }

    public SmsListDateWiseAdapter(Activity activity, ArrayList<Object> arrayListCallLogs,
                                  ArrayList<String> arrayListCallLogHeader) {
        this.mActivity = activity;
        this.arrayListCallLogs = arrayListCallLogs;
        this.arrayListCallLogHeader = arrayListCallLogHeader;
        this.context = activity;
        listOfDateToDisplay = new ArrayList<>();
        for (int i = 0; i < arrayListCallLogs.size(); i++) {
            if (arrayListCallLogs.get(i) instanceof SmsDataType) {
                long objDate = ((SmsDataType) arrayListCallLogs.get(i))
                        .getDataAndTime();
                String finalDate = new SimpleDateFormat("dd/MM,EEE").format(objDate);
                listOfDateToDisplay.add(finalDate);
            }
        }
    }

    public SmsDataType getSelectedSmsType() {
        return selectedSmsType;
    }

    public void setSelectedSmsType(SmsDataType selectedSmsType) {
        this.selectedSmsType = selectedSmsType;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
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
                View v2 = inflater.inflate(R.layout.layout_item_sms, parent, false);
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
        if (arrayListCallLogs.get(position) instanceof SmsDataType) {
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

        final SmsDataType smsDataType = (SmsDataType) arrayListCallLogs.get(position);
        String isRead = smsDataType.getIsRead();
        Log.i("SMS Read", isRead);
        Log.i("SMS", " Number : " + smsDataType.getNumber() + " Thread ID " + smsDataType.getThreadId());
        final String address = smsDataType.getAddress();
        if (!TextUtils.isEmpty(address)) {
            if (isRead.equalsIgnoreCase("0")) {
                holder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                        .colorBlack));
                holder.textNumber.setTypeface(null, Typeface.BOLD);
                holder.textNumber.setText(address);

            } else {
                holder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                        .colorBlack));
                holder.textNumber.setText(address);
            }
        } else {
            holder.textNumber.setText(" ");
        }

        String body = smsDataType.getBody();
        if (!TextUtils.isEmpty(body)) {
            if (isRead.equalsIgnoreCase("0")) {
                holder.textBody.setText(body);
                holder.textBody.setTypeface(null, Typeface.BOLD);
            } else {
                holder.textBody.setText(body);
            }
        } else {
            holder.textBody.setText(" ");

        }

        long date = smsDataType.getDataAndTime();
        if (date > 0) {
            Date date1 = new Date(date);
            String logDate = new SimpleDateFormat("dd MMM,yy hh:mm:ss a").format(date1);
            if (isRead.equalsIgnoreCase("0")) {
                holder.textDateNTime.setText(logDate);
                holder.textDateNTime.setTypeface(null, Typeface.BOLD);
            } else {
                holder.textDateNTime.setText(logDate);
            }
        } else {
            holder.textDateNTime.setText(" ");
        }


        final String thumbnailUrl = smsDataType.getProfileImage();
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(200, 200)
                    .into(holder.icon);

        } else {
            holder.icon.setImageResource(R.drawable.home_screen_profile);
        }
        holder.llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setSelectedSmsType(smsDataType);
                setSelectedPosition(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", address, null));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setPackage(Telephony.Sms.getDefaultSmsPackage(context));
                }
                intent.putExtra("finishActivityOnSaveCompleted", true);
                context.startActivity(intent);


            }
        });

        holder.image3dotsSmsLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                arrayListForKnownContact =
            }
        });

    }

    private void configureHeaderViewHolder(CallLogHeaderViewHolder holder, int
            position) {
        String date = (String) arrayListCallLogs.get(position);

        if (listOfDateToDisplay.contains(date) ||
                date.equalsIgnoreCase(context.getString(R.string.str_today)) ||
                date.equalsIgnoreCase(context.getString(R.string.str_yesterday))) {
            holder.textHeader.setVisibility(View.VISIBLE);
            holder.textHeader.setText(date);
        } else {
            holder.textHeader.setVisibility(View.GONE);

        }
    }
    //</editor-fold>

    //<editor-fold desc="View Holder">
    public class AllCallLogViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView textNumber;
        TextView textBody;
        TextView textDateNTime;
        LinearLayout llContent;
        ImageView image3dotsSmsLog;
        RelativeLayout llMain;

        AllCallLogViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            textBody = (TextView) itemView.findViewById(R.id.text_body);
            textNumber = (TextView) itemView.findViewById(R.id.text_number);
            textDateNTime = (TextView) itemView.findViewById(R.id.text_date_n_time);
            llContent = (LinearLayout) itemView.findViewById(R.id.llContent);
            llMain = (RelativeLayout) itemView.findViewById(R.id.llMain);
            image3dotsSmsLog =  (ImageView) itemView.findViewById(R.id.image_3dots_sms_log);
        }
    }

    public class CallLogHeaderViewHolder extends RecyclerView.ViewHolder {

        TextView textHeader;

        CallLogHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader = (TextView) itemView.findViewById(R.id.text_header);

        }
    }
}