package com.rawalinfocom.rcontact.events;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<EventItem> list;
    private Context context;

    public EventAdapter(Context context, List<EventItem> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EventItem item = list.get(position);
        holder.textWisherName.setText(item.getWisherName());
        holder.textEventName.setText(item.getEventName());
        holder.textTimelineNotiTime.setText(item.getNotiTime());
        String userComment = item.getUserComment();
        int notiType = item.getNotiType();
        if (userComment != null && userComment.length() != 0) {
            holder.textUserComment.setText("You wrote a message to " + item.getWisherName());
            holder.layoutUserCommentPending.setVisibility(View.GONE);
        } else {
            holder.textUserComment.setVisibility(View.GONE);
            holder.layoutUserCommentPending.setVisibility(View.VISIBLE);
        }
        if (notiType == 1) {
            holder.textEventDetailInfo.setVisibility(View.GONE);
        } else if (notiType == 0) {
            holder.textEventDetailInfo.setText(item.getEventDetail());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_wisher)
        ImageView imageWisher;

        @BindView(R.id.text_wisher_name)
        TextView textWisherName;

        @BindView(R.id.text_event_name)
        TextView textEventName;

        @BindView(R.id.text_timeline_noti_time)
        TextView textTimelineNotiTime;

        @BindView(R.id.text_event_detail_info)
        TextView textEventDetailInfo;

        @BindView(R.id.edittext_user_comment)
        EditText edittextUserComment;

        @BindView(R.id.button_user_comment_submit)
        ImageView buttonUserCommentSubmit;

        @BindView(R.id.layout_user_comment_pending)
        LinearLayout layoutUserCommentPending;

        @BindView(R.id.text_user_comment)
        TextView textUserComment;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            textWisherName.setTypeface(Utils.typefaceRegular(context));

            textEventName.setTypeface(Utils.typefaceRegular(context));
            textTimelineNotiTime.setTypeface(Utils.typefaceRegular(context));

            textEventDetailInfo.setTypeface(Utils.typefaceRegular(context));

            edittextUserComment.setTypeface(Utils.typefaceRegular(context));
            textUserComment.setTypeface(Utils.typefaceRegular(context));
        }
    }


}
