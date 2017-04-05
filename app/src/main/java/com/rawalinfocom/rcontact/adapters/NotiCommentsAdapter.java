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
import com.rawalinfocom.rcontact.notifications.NotificationPopupDialog;
import com.rawalinfocom.rcontact.model.NotiCommentsItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiCommentsAdapter extends RecyclerView.Adapter<NotiCommentsAdapter.MyViewHolder> {


    private Context context;
    private List<NotiCommentsItem> list;
    private int recyclerPosition;
    NotificationPopupDialog notificationPopupDialog;

    public NotiCommentsAdapter(Context context, List<NotiCommentsItem> list, int recyclerPosition) {
        this.context = context;
        this.list = list;
        this.recyclerPosition = recyclerPosition;
    }

    public void updateList(List<NotiCommentsItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_commenter)
        ImageView imageCommenter;
        @BindView(R.id.text_commenter_name)
        TextView textCommenterName;
        @BindView(R.id.text_comment_noti_time)
        TextView textCommentNotiTime;
        @BindView(R.id.text_comment_detail_info)
        TextView textCommentDetailInfo;
        @BindView(R.id.button_comment_view_reply)
        Button buttonCommentViewReply;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_noti_comments, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NotiCommentsItem item = list.get(position);
        holder.textCommenterName.setText(item.getCommenterName());
        holder.textCommentDetailInfo.setText(item.getCommenterInfo());
        if (recyclerPosition == 2) {
            holder.textCommentNotiTime.setText(Utils.formatDateTime(item.getNotiCommentTime(), "dd MMM, hh:mm a"));
        } else {
            holder.textCommentNotiTime.setText(Utils.formatDateTime(item.getNotiCommentTime(), "hh:mm a"));
        }
        holder.buttonCommentViewReply.setText("VIEW REPLY");
        holder.buttonCommentViewReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> arrayListComments = new ArrayList<>();

                arrayListComments.add(item.getCommenterName());
                arrayListComments.add(item.getEventName());
                arrayListComments.add(item.getComment());
                arrayListComments.add(Utils.formatDateTime(item.getCommentTime(), "dd MMM, hh:mm a"));
                arrayListComments.add(item.getReply());
                arrayListComments.add(Utils.formatDateTime(item.getReplyTime(), "dd MMM, hh:mm a"));

                notificationPopupDialog = new NotificationPopupDialog(context, arrayListComments, false);
                notificationPopupDialog.setDialogTitle(item.getCommenterName() + " Reply You");
                notificationPopupDialog.showDialog();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
