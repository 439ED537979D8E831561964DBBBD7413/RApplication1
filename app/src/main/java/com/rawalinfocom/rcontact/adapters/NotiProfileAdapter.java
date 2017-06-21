package com.rawalinfocom.rcontact.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.NotiProfileItem;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiProfileAdapter extends RecyclerView.Adapter<NotiProfileAdapter.MyViewHolder> {

    private Fragment context;
    private List<NotiProfileItem> list;
    private int recyclerPosition;

    public NotiProfileAdapter(Fragment context, List<NotiProfileItem> list, int recyclerPosition) {
        this.context = context;
        this.list = list;
        this.recyclerPosition = recyclerPosition;
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NotiProfileItem item = list.get(position);
        holder.textRequesterName.setText(item.getPersonName());
        holder.textRequestDetailInfo.setText(item.getNotiInfo());
        holder.buttonRequestConfirm.setAllCaps(true);
        if (item.getProfileNotiType() == 0) {
            holder.buttonRequestConfirm.setText(context.getString(R.string.str_confirm));
            holder.buttonRequestReject.setVisibility(View.VISIBLE);
            holder.buttonRequestReject.setText(context.getString(R.string.str_reject));
        } else {
            holder.buttonRequestConfirm.setText(context.getString(R.string.view_profile));
            holder.buttonRequestReject.setVisibility(View.GONE);
        }
        if (recyclerPosition == 1) {
            holder.textRequestNotiTime.setText(Utils.formatDateTime(item.getNotiRequestTime(), "dd MMM, hh:mm a"));
        } else {
            holder.textRequestNotiTime.setText(Utils.formatDateTime(item.getNotiRequestTime(), "hh:mm a"));
        }

        holder.buttonRequestConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.getProfileNotiType() == 0) {
                    // confirming the request
                    sendRespondToServer(1, item.getCardCloudId());
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_PM_ID, item.getRcpUserPmId());
                    bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, "-1");
                    bundle.putString(AppConstants.EXTRA_CONTACT_NAME, item.getPersonName());
                    ((BaseActivity) (context.getActivity())).startActivityIntent(context.getActivity(), ProfileDetailActivity
                            .class, bundle);
                }
            }
        });
        holder.buttonRequestReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // rejecting the request
                sendRespondToServer(2, item.getCardCloudId());

            }
        });
    }

    private void sendRespondToServer(int status, int cardCloudId) {
        WsRequestObject requestObj = new WsRequestObject();
        requestObj.setCarStatus(status);
        requestObj.setCarId(cardCloudId);
        if (Utils.isNetworkAvailable(context.getActivity())) {
            new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    requestObj, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_PRIVACY_REQUEST, context.getResources().getString(R.string.msg_please_wait), true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_PRIVACY_REQUEST);
        } else {
            //show no net
            Toast.makeText(context.getActivity(), context.getResources().getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
