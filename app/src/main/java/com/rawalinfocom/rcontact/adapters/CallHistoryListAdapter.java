package com.rawalinfocom.rcontact.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 20/02/17.
 */


public class CallHistoryListAdapter extends RecyclerView.Adapter<CallHistoryListAdapter
        .MyViewHolder> {

    private ArrayList<CallLogType> listCallHistory;
    private Context context;

    public CallHistoryListAdapter(Context context, ArrayList<CallLogType> listCallLogType) {
        this.context = context;
        this.listCallHistory = listCallLogType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_call_histroy_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CallLogType callLogType = listCallHistory.get(position);
        String number = callLogType.getHistoryNumber();
        if (!TextUtils.isEmpty(number)) {
            if (StringUtils.equalsIgnoreCase(callLogType.getIsHistoryRcpVerifiedId(), "0")) {
                holder.textHistoryNumber.setTextColor(ContextCompat.getColor(context, R.color
                        .textColorBlue));
            } else if (StringUtils.equalsIgnoreCase(callLogType.getIsHistoryRcpVerifiedId(), "1")) {
                holder.textHistoryNumber.setTextColor(ContextCompat.getColor(context, R.color
                        .colorAccent));
            } else {
                holder.textHistoryNumber.setTextColor(ContextCompat.getColor(context, R.color
                        .colorTextHeader));
            }
            holder.textHistoryNumber.setText(Utils.getFormattedNumber(context, number));
        }

        String numberType = callLogType.getHistoryNumberType();
        if (!TextUtils.isEmpty(numberType)) {
            holder.textHistoryCallType.setText("(" + numberType + ")" +
                    "");
        }


        long logDate1 = callLogType.getHistoryDate();
        Date date1 = new Date(logDate1);
        String logDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesDate;
        yesDate = cal.getTime();
        String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd").format(yesDate);

        Calendar c = Calendar.getInstance();
        Date cDate = c.getTime();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

        String finalDate = "";
        if (logDate.equalsIgnoreCase(currentDate)) {
            finalDate = context.getString(R.string.str_today);
        } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
            finalDate = context.getString(R.string.str_yesterday);
        } else {
            finalDate = new SimpleDateFormat("EEE,dd/MM").format(date1);
        }
        holder.textHistoryDate.setText(finalDate);


        int callType = callLogType.getHistoryType();
        if (callType > 0) {
            switch (callType) {
                case AppConstants.INCOMING:
                    holder.imageHistoryCallType.setImageResource(R.drawable.ic_call_incoming);
                    break;
                case AppConstants.OUTGOING:
                    holder.imageHistoryCallType.setImageResource(R.drawable.ic_call_outgoing);
                    break;
                case AppConstants.MISSED:
                    holder.imageHistoryCallType.setImageResource(R.drawable.ic_call_missed);
                    break;
                default:
                    break;

            }
        }

        /*if(!StringUtils.isEmpty(callLogType.getHistroyCoolDuration())){
            String duration = callLogType.getHistroyCoolDuration();
            holder.textHistoryDurationValue.setText(duration);
        }else{
            if(!StringUtils.isEmpty(callLogType.getWebDuration())){
                String duration = callLogType.getWebDuration();
                holder.textHistoryDurationValue.setText(duration);
            }
        }*/

        if (!StringUtils.isEmpty(callLogType.getWebDuration())) {
            String duration = callLogType.getWebDuration();
            holder.textHistoryDurationValue.setText(duration);
        } else {
            String duration = callLogType.getHistroyCoolDuration();
            holder.textHistoryDurationValue.setText(duration);
        }

        Date historyDate = new Date(callLogType.getHistoryDate());
        String callTime = new SimpleDateFormat("hh:mm a").format(historyDate);
        holder.textHistoryCallTime.setText(callTime);


    }

    @Override
    public int getItemCount() {

        return listCallHistory.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.text_histroy_number)
        TextView textHistoryNumber;
        @BindView(R.id.text_histroy_call_type)
        TextView textHistoryCallType;
        @BindView(R.id.text_histroy_date)
        TextView textHistoryDate;
        @BindView(R.id.image_histroy_call_type)
        ImageView imageHistoryCallType;
        @BindView(R.id.text_histroy_sim_type)
        TextView textHistorySimType;
        @BindView(R.id.text_duration)
        TextView textDuration;
        @BindView(R.id.text_histroy_duration_value)
        TextView textHistoryDurationValue;
        @BindView(R.id.text_histroy_call_time)
        TextView textHistoryCallTime;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);

            textHistoryNumber.setTypeface(Utils.typefaceRegular(context));
            textHistoryCallType.setTypeface(Utils.typefaceRegular(context));
            textHistoryDate.setTypeface(Utils.typefaceRegular(context));
            textDuration.setTypeface(Utils.typefaceRegular(context));
            textHistoryDurationValue.setTypeface(Utils.typefaceRegular(context));
            textHistoryCallTime.setTypeface(Utils.typefaceRegular(context));

        }
    }

}
