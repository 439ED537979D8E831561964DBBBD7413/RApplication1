package com.rawalinfocom.rcontact.notifications;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRequestAdapter extends RecyclerView.Adapter<NotiRequestAdapter.MyViewHolder> {

    private Context context;
    private List<NotiRequestItem> list;

    public NotiRequestAdapter(Context context, List<NotiRequestItem> list) {
        this.context = context;
        this.list = list;
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
        NotiRequestItem item = list.get(position);
        holder.textRequesterName.setText(item.getRequesterName());
        holder.textRequestDetailInfo.setText(item.getRequestInfo());
        holder.textRequestNotiTime.setText(item.getNotiRequestTime());
        holder.buttonRequestConfirm.setText("CONFIRM");
        holder.buttonRequestReject.setText("REJECT");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
