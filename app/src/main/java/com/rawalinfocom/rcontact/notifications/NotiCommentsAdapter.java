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

public class NotiCommentsAdapter extends RecyclerView.Adapter<NotiCommentsAdapter.MyViewHolder> {


    private Context context;
    private List<NotiCommentsItem> list;

    public NotiCommentsAdapter(Context context, List<NotiCommentsItem> list) {
        this.context = context;
        this.list = list;
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
        NotiCommentsItem item = list.get(position);
        holder.textCommenterName.setText(item.getCommenterName());
        holder.textCommentDetailInfo.setText(item.getCommenterInfo());
        holder.textCommentNotiTime.setText(item.getNotiCommentTime());
        holder.buttonCommentViewReply.setText("VIEW REPLY");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
