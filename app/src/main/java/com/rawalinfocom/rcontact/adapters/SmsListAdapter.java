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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.SMSMenuOptionsDialog;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.listener.OnLoadMoreListener;
import com.rawalinfocom.rcontact.model.SmsDataType;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

/**
 * Created by Aniruddh on 21/04/17.
 */

public class SmsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<SmsDataType> typeArrayList;
    SmsDataType selectedSmsType;
    int selectedPosition = -1;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private RecyclerView recyclerViewSmsLogs;

    private boolean isLoading;
    private boolean isMoreData = false;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;
    ArrayList<String> arrayListForKnownContact;
    ArrayList<SmsDataType> arrayList;
    private int searchCount;


    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    public ArrayList<SmsDataType> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<SmsDataType> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<SmsDataType> getTypeArrayList() {
        return typeArrayList;
    }

    public void setTypeArrayList(ArrayList<SmsDataType> typeArrayList) {
        this.typeArrayList = typeArrayList;
    }

    public SmsListAdapter(Context context, ArrayList<SmsDataType> SmsListAdapter, RecyclerView recyclerView) {
        this.context = context;
//        this.typeArrayList = SmsListAdapter;
        this.recyclerViewSmsLogs = recyclerView;
        if (AppConstants.isFromSearchActivity) {
            this.typeArrayList = new ArrayList<>();
            this.typeArrayList.addAll(SmsListAdapter);
        } else {
            this.typeArrayList = SmsListAdapter;
        }

        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(typeArrayList);

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
    public int getItemViewType(int position) {
        return typeArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_sms, parent, false);
            return new SMSViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof SMSViewHolder) {
            final SmsDataType smsDataType = typeArrayList.get(position);
            SMSViewHolder userViewHolder = (SMSViewHolder) holder;
            String isRead = smsDataType.getIsRead();
       /* Log.i("SMS Read", isRead);
        Log.i("SMS", " Number : " + smsDataType.getNumber() + " Thread ID " + smsDataType.getThreadId());*/
            final String threadID = smsDataType.getThreadId();

            final String address = smsDataType.getAddress();
            final String number = smsDataType.getNumber();
            final String add;

            if (!TextUtils.isEmpty(address)) {
                if (isRead.equalsIgnoreCase("0")) {
                    userViewHolder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                            .colorBlack));
                    userViewHolder.textNumber.setTypeface(Utils.typefaceBold(context));
                    userViewHolder.textNumber.setText(address);

                } else {
                    userViewHolder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                            .colorBlack));
                    userViewHolder.textNumber.setTypeface(Utils.typefaceRegular(context));
                    userViewHolder.textNumber.setText(address);
                }
            } else {
                userViewHolder.textNumber.setText(" ");
            }

            if (!TextUtils.isEmpty(number)) {
                add = number;
                if (MoreObjects.firstNonNull(smsDataType.isRcpUser(), false)) {
                    userViewHolder.textCloudContactName.setVisibility(View.VISIBLE);
                    String contactDisplayName = "";
                    String prefix = smsDataType.getPrefix();
                    String firstName = smsDataType.getRcpFirstName();
                    String lastName = smsDataType.getRcpLastName();
                    String middleName = smsDataType.getMiddleName();
                    String suffix = smsDataType.getSuffix();
               /* if (StringUtils.length(prefix) > 0) {
                    contactDisplayName = prefix + " ";
                }*/
                    if (StringUtils.length(firstName) > 0) {
                        contactDisplayName = contactDisplayName + firstName + " ";
                    }
               /* if (StringUtils.length(middleName) > 0) {
                    contactDisplayName = contactDisplayName + middleName + " ";
                }*/
                    if (StringUtils.length(lastName) > 0) {
                        contactDisplayName = contactDisplayName + lastName + "";
                    }
                /*if (StringUtils.length(suffix) > 0) {
                    contactDisplayName = contactDisplayName + suffix;
                }*/
                    if (address != null && address.matches("^[\\p{L} .'-]+$")) {
                        if (StringUtils.equalsIgnoreCase(address, contactDisplayName)) {
                            userViewHolder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                                    .colorAccent));
                            userViewHolder.textCloudContactName.setVisibility(View.GONE);
                            userViewHolder.textNumber.setText(address);
                        } else {
                            userViewHolder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                                    .colorBlack));
                            userViewHolder.textCloudContactName.setVisibility(View.VISIBLE);
                            userViewHolder.textCloudContactName.setTextColor(ContextCompat.getColor(context, R.color
                                    .colorAccent));
                            userViewHolder.textNumber.setText(address);
                            userViewHolder.textCloudContactName.setText(" " + "(" + contactDisplayName + ")");
                        }
                    } else {
//                        if (StringUtils.equalsIgnoreCase(add, contactDisplayName)) {
                        if (StringUtils.equalsIgnoreCase(address, contactDisplayName)) {
                            userViewHolder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                                    .colorAccent));
                            userViewHolder.textCloudContactName.setVisibility(View.GONE);
                            userViewHolder.textNumber.setText(address);
                        } else {
                            userViewHolder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                                    .colorBlack));
                            userViewHolder.textCloudContactName.setVisibility(View.VISIBLE);
                            userViewHolder.textCloudContactName.setTextColor(ContextCompat.getColor(context, R.color
                                    .colorAccent));
                            userViewHolder.textNumber.setText(address);
                            userViewHolder.textCloudContactName.setText(" " + "(" + contactDisplayName + ")");
                        }
                    }

                 } else {
                    userViewHolder.textCloudContactName.setVisibility(View.GONE);
                    userViewHolder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                            .colorBlack));
                    userViewHolder.textCloudContactName.setVisibility(View.GONE);
//                    userViewHolder.textNumber.setText(add);
                    userViewHolder.textNumber.setText(address);
                }

            } else {
                userViewHolder.textCloudContactName.setVisibility(View.GONE);
                add = address;
            }

            String body = smsDataType.getBody();
            if (!TextUtils.isEmpty(body)) {
                if (isRead.equalsIgnoreCase("0")) {
                    userViewHolder.textBody.setText(body);
                    userViewHolder.textBody.setTypeface(Utils.typefaceBold(context));
                } else {
                    userViewHolder.textBody.setTypeface(Utils.typefaceRegular(context));
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
                    userViewHolder.textDateNTime.setTypeface(Utils.typefaceBold(context));
                } else {
                    userViewHolder.textDateNTime.setTypeface(Utils.typefaceRegular(context));
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

//                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", add, null)));

                }
            });

            userViewHolder.image3dotsSmsLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setSelectedSmsType(smsDataType);
                    setSelectedPosition(position);

                    /*Pattern numberPat = Pattern.compile("\\d+");
                    Matcher matcher1 = numberPat.matcher(address);
                    if (matcher1.find()) {
                        arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                        + " " + address, context.getString(R.string.add_to_contact),
                                context.getString(R.string.add_to_existing_contact),
                                context.getString(R.string.copy_phone_number) *//*,context.getString(R.string.delete)*//*));
                    } else {
                        arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                        + " " + address,
                                context.getString(R.string.copy_phone_number) *//*, context.getString(R.string.delete)*//*));
                    }*/

                    if (address.matches("[+][0-9]+")) {
                        arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                        + " " + address, context.getString(R.string.add_to_contact),
                                context.getString(R.string.add_to_existing_contact),
                                context.getString(R.string.copy_phone_number) /*,context.getString(R.string.delete)*/));
                    } else {
                        arrayListForKnownContact = new ArrayList<>(Arrays.asList(context.getString(R.string.action_call)
                                        + " " + address,
                                context.getString(R.string.copy_phone_number) /*, context.getString(R.string.delete)*/));
                    }

                    SMSMenuOptionsDialog smsMenuOptionsDialog = new SMSMenuOptionsDialog(context, arrayListForKnownContact,
                            add, address, threadID);
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

    public class SMSViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        public TextView textNumber;
        TextView textBody;
        TextView textDateNTime;
        LinearLayout llContent;
        ImageView image3dotsSmsLog;
        RelativeLayout llMain;
        TextView textCloudContactName;


        SMSViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            textBody = (TextView) itemView.findViewById(R.id.text_body);
            textNumber = (TextView) itemView.findViewById(R.id.text_number);
            textDateNTime = (TextView) itemView.findViewById(R.id.text_date_n_time);
            llContent = (LinearLayout) itemView.findViewById(R.id.llContent);
            llMain = (RelativeLayout) itemView.findViewById(R.id.llMain);
            image3dotsSmsLog = (ImageView) itemView.findViewById(R.id.image_3dots_sms_log);
            textCloudContactName = (TextView) itemView.findViewById(R.id.text_cloud_contact_name);
            textBody.setTypeface(Utils.typefaceRegular(context));
            textNumber.setTypeface(Utils.typefaceRegular(context));
            textDateNTime.setTypeface(Utils.typefaceRegular(context));
            textCloudContactName.setTypeface(Utils.typefaceRegular(context));

        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    // Filter Class
    public void filter(String charText) {
        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(charText);
        if (matcher1.find()) {
            typeArrayList.clear();
            if (charText.length() == 0) {
                typeArrayList.addAll(arrayList);
            } else {
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof SmsDataType) {
                        SmsDataType profileData = (SmsDataType) arrayList.get(i);
                        if (!StringUtils.isEmpty(profileData.getAddress())) {
                            if (StringUtils.containsIgnoreCase(profileData.getAddress(), charText)) {
                                typeArrayList.add(profileData);
                            }
                        }
                        setArrayList(arrayList);
                    }
                }
            }

        } else {
            charText = charText.toLowerCase(Locale.getDefault());
            typeArrayList.clear();
            if (charText.length() == 0) {
                typeArrayList.addAll(arrayList);
            } else {
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof SmsDataType) {
                        SmsDataType profileData = arrayList.get(i);
                        String address = profileData.getAddress();
                        if (!StringUtils.isEmpty(address)) {
//                            if (address.contains(charText)) {
                            if (StringUtils.containsIgnoreCase(address, charText)) {
                                typeArrayList.add(profileData);
                            }
                        }
                        setArrayList(arrayList);
                    }
                }
            }
        }

        if (typeArrayList.size() > 0) {
            setTypeArrayList(typeArrayList);
            setSearchCount(typeArrayList.size());
        } else
            setSearchCount(arrayList.size());

        notifyDataSetChanged();
    }
}
