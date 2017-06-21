package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.NotiRatingItem;
import com.rawalinfocom.rcontact.notifications.NotificationPopupDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRatingAdapter extends RecyclerView.Adapter<NotiRatingAdapter.MyViewHolder> {


    private Context context;
    private List<NotiRatingItem> list;
    private int recyclerPosition;
    NotificationPopupDialog notificationPopupDialog;

    public NotiRatingAdapter(Context context, List<NotiRatingItem> list, int recyclerPosition) {
        this.context = context;
        this.list = list;
    }

    public void updateList(List<NotiRatingItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_rater)
        ImageView imageRater;
        @BindView(R.id.text_rater_name)
        TextView textRaterName;
        @BindView(R.id.text_rating_noti_time)
        TextView textRatingNotiTime;
        @BindView(R.id.text_rating_detail_info)
        TextView textRatingDetailInfo;
        @BindView(R.id.button_rating_view_reply)
        Button buttonRatingViewReply;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_noti_rating, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NotiRatingItem item = list.get(position);
        holder.textRaterName.setText(item.getRaterName());
        if (recyclerPosition == 2) {
            holder.textRatingNotiTime.setText(Utils.formatDateTime(item.getNotiTime(), "dd MMM, hh:mm a"));
        } else {
            holder.textRatingNotiTime.setText(Utils.formatDateTime(item.getNotiTime(), "hh:mm a"));
        }
        holder.textRatingDetailInfo.setText(item.getRaterName() + context.getString(R.string.str_rating_comment_hint_1));
        holder.buttonRatingViewReply.setAllCaps(true);
        holder.buttonRatingViewReply.setText(context.getString(R.string.view_profile));
        holder.buttonRatingViewReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayListComments = new ArrayList<>();
                arrayListComments.add(item.getRaterName());
                arrayListComments.add(context.getString(R.string.str_tab_rating));
                arrayListComments.add(item.getComment());
                arrayListComments.add(Utils.formatDateTime(item.getCommentTime(), "dd MMM, hh:mm a"));
                arrayListComments.add(item.getReply());
                arrayListComments.add(Utils.formatDateTime(item.getReplyTime(), "dd MMM, hh:mm a"));
                notificationPopupDialog = new NotificationPopupDialog(context, arrayListComments, true);
                notificationPopupDialog.setDialogTitle(item.getRaterName() + context.getString(R.string.text_rate_you));
                notificationPopupDialog.setRatingInfo(item.getRating());
                notificationPopupDialog.showDialog();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
