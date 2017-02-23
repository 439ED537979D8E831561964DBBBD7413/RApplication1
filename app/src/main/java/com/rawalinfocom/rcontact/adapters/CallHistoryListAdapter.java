package com.rawalinfocom.rcontact.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 20/02/17.
 */


public class CallHistoryListAdapter extends RecyclerView.Adapter<CallHistoryListAdapter.MyViewHolder> {

    private ArrayList<CallLogType> listCallHistroy;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.text_histroy_number)
        TextView textHistroyNumber;
        @BindView(R.id.text_histroy_call_type)
        TextView textHistroyCallType;
        @BindView(R.id.text_histroy_date)
        TextView textHistroyDate;
        @BindView(R.id.image_histroy_call_type)
        ImageView imageHistroyCallType;
        @BindView(R.id.text_histroy_sim_type)
        TextView textHistroySimType;
        @BindView(R.id.text_duration)
        TextView textDuration;
        @BindView(R.id.text_histroy_duration_value)
        TextView textHistroyDurationValue;
        @BindView(R.id.text_histroy_call_time)
        TextView textHistroyCallTime;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,itemView);

        }
    }


    public CallHistoryListAdapter(ArrayList<CallLogType> listCallLogType) {
        this.listCallHistroy = listCallLogType;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_call_histroy_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CallLogType callLogType =  listCallHistroy.get(position);
        String number =  callLogType.getHistroyNumber();
        if(!TextUtils.isEmpty(number))
            holder.textHistroyNumber.setText(number);

        String numberType =  callLogType.getHistroyNumberType();
        if(!TextUtils.isEmpty(numberType)){
            holder.textHistroyCallType.setText("(" + numberType+ ")" +
                    "");
        }

        long logDate1 =  callLogType.getHistroyDate();
        Date date1=  new Date (logDate1);
        String logDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesDate =  new Date();
        yesDate =  cal.getTime();
        String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd").format(yesDate);

        Calendar c = Calendar.getInstance();
        Date cDate =  c.getTime();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

        String finalDate = "";
        if(logDate.equalsIgnoreCase(currentDate)){
            finalDate = "Today";

        }else if(logDate.equalsIgnoreCase(yesterdayDate)){
            finalDate = "Yesterday";

        }else
        {
            finalDate =  new SimpleDateFormat("EEE,dd/MM").format(date1);;

        }
        holder.textHistroyDate.setText(finalDate);


        int callType =  callLogType.getHistroyType();
        if(callType>0){
            switch (callType) {
                case AppConstants.INCOMING:
                    holder.imageHistroyCallType.setImageResource(R.drawable.ic_call_incoming);
                    break;
                case AppConstants.OUTGOING:
                    holder.imageHistroyCallType.setImageResource(R.drawable.ic_call_outgoing);
                    break;
                case AppConstants.MISSED:
                    holder.imageHistroyCallType.setImageResource(R.drawable.ic_call_missed);
                    break;
                default:
                    break;

            }
        }

        String duration = callLogType.getHistroyCoolDuration();
        holder.textHistroyDurationValue.setText(duration);

        Date histroyDate = new Date(callLogType.getHistroyDate());
        String callTime = new SimpleDateFormat("hh:mm a").format(histroyDate);
        holder.textHistroyCallTime.setText(callTime);


    }

    @Override
    public int getItemCount() {

        return listCallHistroy.size();
    }


}
