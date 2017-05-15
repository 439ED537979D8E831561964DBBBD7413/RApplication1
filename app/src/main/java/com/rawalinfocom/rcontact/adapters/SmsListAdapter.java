package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.SMSMenuOptionsDialog;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.listener.OnLoadMoreListener;
import com.rawalinfocom.rcontact.model.SmsDataType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aniruddh on 21/04/17.
 */

public class SmsListAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private Context context;
    private ArrayList<SmsDataType> typeArrayList;
    SmsDataType selectedSmsType;
    int selectedPosition = -1;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private RecyclerView recyclerViewSmsLogs;

    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private boolean isMoreData = false;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    ArrayList<String> arrayListForKnownContact;


    public SmsListAdapter(Context context, ArrayList<SmsDataType> SmsListAdapter, RecyclerView recyclerView) {
        this.context = context;
        this.typeArrayList = SmsListAdapter;
        this.recyclerViewSmsLogs = recyclerView;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public boolean isMoreData() {
        return isMoreData;
    }

    public void setMoreData(boolean moreData) {
        isMoreData = moreData;
    }

    public SmsDataType getSelectedSmsType()
    {
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
    public int getItemViewType(int position) {
        return typeArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_sms, parent, false);
            return new CountryViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CountryViewHolder) {
            final SmsDataType smsDataType = typeArrayList.get(position);
            CountryViewHolder userViewHolder = (CountryViewHolder) holder;
            String isRead = smsDataType.getIsRead();
       /* Log.i("SMS Read", isRead);
        Log.i("SMS", " Number : " + smsDataType.getNumber() + " Thread ID " + smsDataType.getThreadId());*/
            final String threadID =  smsDataType.getThreadId();

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
                    userViewHolder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                            .colorBlack));
                    userViewHolder.textNumber.setTypeface(null, Typeface.BOLD);
                    userViewHolder.textNumber.setText(address);

                } else {
                    userViewHolder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                            .colorBlack));
                    userViewHolder.textNumber.setTypeface(null, Typeface.NORMAL);
                    userViewHolder.textNumber.setText(address);
                }
            } else {
                userViewHolder.textNumber.setText(" ");
            }

            String body = smsDataType.getBody();
            if (!TextUtils.isEmpty(body)) {
                if (isRead.equalsIgnoreCase("0")) {
                    userViewHolder.textBody.setText(body);
                    userViewHolder.textBody.setTypeface(null, Typeface.BOLD);
                } else {
                    userViewHolder.textBody.setTypeface(null, Typeface.NORMAL);
                    userViewHolder.textBody.setText(body);
                }
            } else {
                userViewHolder.textBody.setText(" ");

            }

            long date = smsDataType.getDataAndTime();
            if (date > 0) {
                Date date1 = new Date(date);
                String logDate = new SimpleDateFormat("dd MMM,yy hh:mm:ss a").format(date1);
                if (isRead.equalsIgnoreCase("0")) {
                    userViewHolder.textDateNTime.setText(logDate);
                    userViewHolder.textDateNTime.setTypeface(null, Typeface.BOLD);
                } else {
                    userViewHolder.textDateNTime.setTypeface(null, Typeface.NORMAL);
                    userViewHolder.textDateNTime.setText(logDate);
                }
            } else {
                userViewHolder.textDateNTime.setText(" ");
            }


            final String thumbnailUrl = smsDataType.getProfileImage();
            if (!TextUtils.isEmpty(thumbnailUrl)) {
                Glide.with(context)
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.home_screen_profile)
                        .error(R.drawable.home_screen_profile)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .override(200, 200)
                        .into(userViewHolder.icon);

            } else {
                userViewHolder.icon.setImageResource(R.drawable.home_screen_profile);
            }
            userViewHolder.llContent.setOnClickListener(new View.OnClickListener() {
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

            userViewHolder.image3dotsSmsLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setSelectedSmsType(smsDataType);
                    setSelectedPosition(position);

                    Pattern numberPat = Pattern.compile("\\d+");
                    Matcher matcher1 = numberPat.matcher(address);
                    if (matcher1.find()) {
                        arrayListForKnownContact = new ArrayList<>(Arrays.asList("Call " + address, context.getString(R.string.add_to_contact),
                                context.getString(R.string.add_to_existing_contact),
                                context.getString(R.string.copy_phone_number) , context.getString(R.string.delete)));
                    }else{
                        arrayListForKnownContact = new ArrayList<>(Arrays.asList("Call " + address,
                                context.getString(R.string.copy_phone_number) , context.getString(R.string.delete)));
                    }

                    SMSMenuOptionsDialog smsMenuOptionsDialog =  new SMSMenuOptionsDialog(context,arrayListForKnownContact,
                            add,address,threadID);
                    smsMenuOptionsDialog.setDialogTitle(address);
                    smsMenuOptionsDialog.showDialog();

                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
//        return typeArrayList.size();
        return typeArrayList == null ? 0 : typeArrayList.size();
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

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

}
