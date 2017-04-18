package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.NotiRContactsItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRContactsAdapter extends RecyclerView.Adapter<NotiRContactsAdapter.MyViewHolder> {

    private List<NotiRContactsItem> list;
    private Context context;

    public NotiRContactsAdapter(Context context, List<NotiRContactsItem> list) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_rcontacts_icon)
        ImageView imageRcontactsIcon;
        @BindView(R.id.text_title)
        TextView textTitle;
        @BindView(R.id.text_noti_time)
        TextView textNotiTime;
        @BindView(R.id.text_detail_info)
        TextView textDetailInfo;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void updateList(List<NotiRContactsItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_noti_rcontacts, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotiRContactsItem item = list.get(position);
        holder.textTitle.setText(item.getNotiTitle());
        holder.textNotiTime.setText(Utils.formatDateTime(item.getNotiTime(), "dd MMM, hh:mm a"));
        holder.textDetailInfo.setText(item.getNotiDetails());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
