package com.rawalinfocom.rcontact.adapters;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.BuildConfig;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.NotiProfileItem;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.notifications.NotificationsDetailActivity;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiProfileAdapter extends RecyclerView.Adapter<NotiProfileAdapter.MyViewHolder> {

    private Fragment activity;
    private List<NotiProfileItem> list;
    //    private int recyclerPosition;
    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick(String type, String carId, int rcpId);
    }

    public NotiProfileAdapter(Fragment activity, List<NotiProfileItem> list, OnClickListener onClickListener) {
        this.activity = activity;
        this.list = list;
        this.onClickListener = onClickListener;
    }

    public void updateList(List<NotiProfileItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_requester)
        ImageView imageRequester;
        @BindView(R.id.text_requester_name)
        TextView textRequesterName;
        @BindView(R.id.text_request_noti_time)
        TextView textRequestNotiTime;
        @BindView(R.id.text_request_detail_info)
        TextView textRequestDetailInfo;
        @BindView(R.id.button_request_confirm)
        Button buttonRequestConfirm;
        @BindView(R.id.button_request_reject)
        Button buttonRequestReject;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_noti_request, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final NotiProfileItem item = list.get(position);
        holder.textRequesterName.setText(item.getPersonName());
        holder.textRequestDetailInfo.setText(item.getNotiInfo());
        holder.buttonRequestConfirm.setAllCaps(true);
        if (item.getProfileNotiType() == 0) {
            holder.buttonRequestConfirm.setText(activity.getString(R.string.str_confirm));
            holder.buttonRequestReject.setVisibility(View.VISIBLE);
            holder.buttonRequestReject.setText(activity.getString(R.string.str_reject));
        } else {
            holder.buttonRequestConfirm.setText(activity.getString(R.string.view_profile));
            holder.buttonRequestReject.setVisibility(View.GONE);
        }
//        if (recyclerPosition == 1) {
//            holder.textRequestNotiTime.setText(Utils.formatDateTime(item.getNotiRequestTime(),
// "dd MMM, hh:mm a"));
//        } else {
//            holder.textRequestNotiTime.setText(Utils.formatDateTime(item.getNotiRequestTime(),
// "hh:mm a"));
//        }

        String notiTime = item.getNotiRequestTime();
        String date = Utils.formatDateTime(notiTime, "yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String current = s.format(c.getTime());
        if (StringUtils.equalsIgnoreCase(current, date)) {
            holder.textRequestNotiTime.setText(Utils.formatDateTime(notiTime, "hh:mm a"));
        } else {
            holder.textRequestNotiTime.setText(Utils.formatDateTime(notiTime, "dd MMM, yy"));
        }

        if (!TextUtils.isEmpty(item.getPersonImage())) {
            Glide.with(activity)
                    .load(item.getPersonImage())
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(activity.getContext()))
                    .override(500, 500)
                    .into(holder.imageRequester);

        } else {
            holder.imageRequester.setImageResource(R.drawable.home_screen_profile);
        }

        holder.imageRequester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isBlank(item.getPersonImage())) {
                    Utils.zoomImageFromThumb(activity.getActivity(), holder.imageRequester, item
                            .getPersonImage(), ((NotificationsDetailActivity) (activity
                            .getActivity())).frameImageEnlarge, (((NotificationsDetailActivity)
                            (activity.getActivity())).imageEnlarge), ((
                            (NotificationsDetailActivity) (activity.getActivity()))
                            .frameContainer));
                }
            }
        });

        holder.buttonRequestConfirm.setTag(position);
        holder.buttonRequestConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = (int) v.getTag();
                NotiProfileItem notiProfileItem = list.get(pos);

                if (notiProfileItem.getProfileNotiType() == 0) {

                    if (onClickListener != null)
                        onClickListener.onClick(notiProfileItem.getPpmTag(), notiProfileItem.getCardCloudId(), Integer.parseInt(notiProfileItem.getRcpUserPmId()));

                    System.out.println("RContacts data accept --> " + notiProfileItem.getCardCloudId() + " -- " + Integer.parseInt(notiProfileItem.getRcpUserPmId()));
                    // confirming the request
                    sendRespondToServer(1, notiProfileItem.getCardCloudId(), notiProfileItem.getPpmTag(), Integer.parseInt(notiProfileItem.getRcpUserPmId()));
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_PM_ID, notiProfileItem.getRcpUserPmId());
                    bundle.putString(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE, notiProfileItem.getPmRawId());
                    bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, "-1");
                    bundle.putString(AppConstants.EXTRA_CONTACT_NAME, notiProfileItem.getPersonName());
                    bundle.putBoolean(AppConstants.EXTRA_FROM_NOTI_PROFILE, true);
                    bundle.putBoolean(AppConstants.EXTRA_IS_RCP_USER, true);
                    bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, notiProfileItem.getPersonImage());
                    ((BaseActivity) (activity.getActivity())).startActivityIntent(activity
                            .getActivity(), ProfileDetailActivity
                            .class, bundle);
                }
            }
        });

        holder.buttonRequestReject.setTag(position);
        holder.buttonRequestReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = (int) v.getTag();
                NotiProfileItem notiProfileItem = list.get(pos);

                if (onClickListener != null)
                    onClickListener.onClick(notiProfileItem.getPpmTag(), notiProfileItem.getCardCloudId(), Integer.parseInt(notiProfileItem.getRcpUserPmId()));
                // rejecting the request

                System.out.println("RContacts data reject --> " + notiProfileItem.getCardCloudId() + " -- " + Integer.parseInt(notiProfileItem.getRcpUserPmId()));
                sendRespondToServer(2, notiProfileItem.getCardCloudId(), notiProfileItem.getPpmTag(), Integer.parseInt(notiProfileItem.getRcpUserPmId()));

            }
        });
    }

    private void sendRespondToServer(int status, String cardCloudId, String type, int carPmIdFrom) {
        WsRequestObject requestObj = new WsRequestObject();

        if (type.equalsIgnoreCase("request all")) {
            requestObj.setCarPmIdFrom(carPmIdFrom);
            requestObj.setRequestAll("1");
        } else {
            requestObj.setCarId(Integer.parseInt(cardCloudId));
        }
        requestObj.setCarStatus(status);
        if (Utils.isNetworkAvailable(activity.getActivity())) {
            new AsyncWebServiceCall(activity, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    requestObj, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_PRIVACY_RESPOND, activity.getResources().getString(R.string
                    .msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT_V2 +
                            WsConstants.REQ_PROFILE_PRIVACY_RESPOND);
        } else {
            //show no net
            Toast.makeText(activity.getActivity(), activity.getResources().getString(R.string
                    .msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
