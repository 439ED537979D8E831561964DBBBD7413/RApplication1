package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.model.CallLogType;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 22/03/17.
 */

public class DeleteCallLogListAdapter extends RecyclerView.Adapter<DeleteCallLogListAdapter
        .callLogViewHolder> {

    private Context context;
    private ArrayList<CallLogType> arrayListCallType;
    private ArrayList<CallLogType> arrayListTempCallType;
    private ArrayList<CallLogType> arrayListToDelete;
    private String rcpVerifiedId;

    public ArrayList<CallLogType> getArrayListToDelete() {
        return arrayListToDelete;
    }

    public void setArrayListToDelete(ArrayList<CallLogType> arrayListToDelete) {
        this.arrayListToDelete = arrayListToDelete;
    }

    public boolean isSelectedAll;
    private ArrayList<Integer> arrayListCheckedPositions;

    public DeleteCallLogListAdapter(Context context, ArrayList<CallLogType>
            arrayList, String rcpVerifiedId) {
        this.context = context;
        this.arrayListCallType = arrayList;
        this.rcpVerifiedId =  rcpVerifiedId;
        this.arrayListTempCallType = new ArrayList<>();
        arrayListCheckedPositions = new ArrayList<>();
        arrayListTempCallType.addAll(arrayList);
        arrayListToDelete = new ArrayList<>();
    }


    @Override
    public callLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_delete_call_history, parent, false);
        return new callLogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(callLogViewHolder holder, int position) {

        final CallLogType callLogType = arrayListCallType.get(position);
        String number = callLogType.getHistoryNumber();
        if (!TextUtils.isEmpty(number)) {
            if(StringUtils.equalsIgnoreCase(rcpVerifiedId,"0")){
                holder.textHistroyNumber.setTextColor(ContextCompat.getColor(context,R.color.textColorBlue));
            }else if(StringUtils.equalsIgnoreCase(rcpVerifiedId,"1")){
                holder.textHistroyNumber.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
            }else{
                holder.textHistroyNumber.setTextColor(ContextCompat.getColor(context,R.color.darkGray));
            }
            holder.textHistroyNumber.setText(number);
        }

        String numberType = callLogType.getHistoryNumberType();
        if (!TextUtils.isEmpty(numberType)) {
            holder.textHistroyCallType.setText("(" + numberType + ")" +
                    "");
        }

        long logDate1 = callLogType.getHistoryDate();
        Date date1 = new Date(logDate1);
        String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date1);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesDate;
        yesDate = cal.getTime();
        String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesDate);

        Calendar c = Calendar.getInstance();
        Date cDate = c.getTime();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cDate);

        String finalDate;
        if (logDate.equalsIgnoreCase(currentDate)) {
            finalDate = "Today";
        } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
            finalDate = "Yesterday";
        } else {
            finalDate = new SimpleDateFormat("EEE,dd/MM", Locale.getDefault()).format(date1);
        }
        holder.textHistroyDate.setText(finalDate);


        int callType = callLogType.getHistoryType();
        if (callType > 0) {
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

        Date historyDate = new Date(callLogType.getHistoryDate());
        String callTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(historyDate);
        holder.textHistroyCallTime.setText(callTime);

        holder.checkboxSelectCallHistory.setTag(position);

        if (!isSelectedAll) {
            holder.checkboxSelectCallHistory.setChecked(false);
            if (arrayListCheckedPositions.contains(position)) {
                holder.checkboxSelectCallHistory.setChecked(true);
            } else {
                holder.checkboxSelectCallHistory.setChecked(false);
            }
        } else {
            holder.checkboxSelectCallHistory.setChecked(true);
        }

        holder.checkboxSelectCallHistory.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!arrayListCheckedPositions.contains(buttonView.getTag())) {
                        arrayListCheckedPositions.add((Integer) buttonView.getTag());
                        arrayListToDelete.add(callLogType);
                        setArrayListToDelete(arrayListToDelete);
                    }
                } else {
                    if (arrayListCheckedPositions.contains(buttonView.getTag())) {
                        arrayListCheckedPositions.remove(buttonView.getTag());
                        arrayListToDelete.remove(callLogType);
                        setArrayListToDelete(arrayListToDelete);

                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListCallType.size();
    }

    class callLogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_histroy_number)
        TextView textHistroyNumber;
        @BindView(R.id.text_histroy_call_type)
        TextView textHistroyCallType;
        @BindView(R.id.text_histroy_date)
        TextView textHistroyDate;
        @BindView(R.id.linear_top)
        LinearLayout linearTop;
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
        @BindView(R.id.linear_bottom)
        LinearLayout linearBottom;
        @BindView(R.id.checkbox_select_call_history)
        CheckBox checkboxSelectCallHistory;
        @BindView(R.id.relative_checkbox)
        RelativeLayout relativeCheckbox;
        @BindView(R.id.relative_main)
        RelativeLayout relativeMain;

        callLogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void isSelectAll(boolean checked) {
        isSelectedAll = checked;
        arrayListCheckedPositions.clear();
        arrayListToDelete.removeAll(arrayListTempCallType);
        setArrayListToDelete(arrayListToDelete);
        if (checked) {
            for (int i = 0; i < getItemCount(); i++) {
                if (!arrayListCheckedPositions.contains(i)) {
                    arrayListCheckedPositions.add(i);
                }
            }
            arrayListToDelete.addAll(arrayListTempCallType);
            setArrayListToDelete(arrayListToDelete);
        }
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getArrayListCheckedPositions() {
        return arrayListCheckedPositions;
    }
}
