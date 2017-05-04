package com.rawalinfocom.rcontact.adapters;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.SmsDataType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Aniruddh on 21/04/17.
 */

public class SmsListAdapter extends RecyclerView.Adapter<SmsListAdapter.CountryViewHolder> {


    private Context context;
    private ArrayList<SmsDataType> typeArrayList;
    SmsDataType selectedSmsType;
    int selectedPosition = -1;

    public SmsListAdapter(Context context, ArrayList<SmsDataType> SmsListAdapter) {
        this.context = context;
        this.typeArrayList = SmsListAdapter;

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


    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_sms,
                parent, false);
        return new CountryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, final int position) {

        final SmsDataType smsDataType = typeArrayList.get(position);
        String isRead = smsDataType.getIsRead();
       /* Log.i("SMS Read", isRead);
        Log.i("SMS", " Number : " + smsDataType.getNumber() + " Thread ID " + smsDataType.getThreadId());*/


        final String address = smsDataType.getAddress();
        final String number = smsDataType.getNumber();
        final String add;
        if(!TextUtils.isEmpty(number)) {
            add = number;
        }else{
            add =  address;
        }
        if (!TextUtils.isEmpty(address)) {
            if (isRead.equalsIgnoreCase("0")) {
                holder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                        .colorBlack));
                holder.textNumber.setTypeface(null, Typeface.BOLD);
                holder.textNumber.setText(address);

            } else {
                holder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                        .colorBlack));
                holder.textNumber.setTypeface(null, Typeface.NORMAL);
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
                holder.textBody.setTypeface(null, Typeface.NORMAL);
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
                holder.textDateNTime.setTypeface(null, Typeface.NORMAL);
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", add, null));
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

    @Override
    public int getItemCount() {
        return typeArrayList.size();

    }

    class CountryViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView textNumber;
        TextView textBody;
        TextView textDateNTime;
        LinearLayout llContent;
        ImageView image3dotsSmsLog;
        RelativeLayout llMain;


        CountryViewHolder(View itemView) {
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

}
