package com.rawalinfocom.rcontact.timeline;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineSectionAdapter extends RecyclerView.Adapter<TimelineSectionAdapter.MyViewHolder> {

    private List<TimelineItem> list;

    public TimelineSectionAdapter(List<TimelineItem> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TimelineItem item = list.get(position);
        holder.textWisherName.setText(item.getWisherName());
        holder.textEventName.setText(item.getEventName());
        String wisherComment = item.getWisherComment();
        String userComment = item.getUserComment();
        int notiType = item.getNotiType();
        if (wisherComment != null && wisherComment.length() != 0) {
            holder.textWisherComment.setText(wisherComment);
        } else {
            holder.textWisherComment.setVisibility(View.GONE);
        }
        if (userComment != null && userComment.length() != 0) {
            holder.textUserComment.setText(userComment);
            holder.layoutUserCommentPending.setVisibility(View.GONE);
        } else {
            holder.layoutUserCommentDone.setVisibility(View.GONE);
            holder.layoutUserCommentPending.setVisibility(View.VISIBLE);
        }
        if (notiType == 1) {
            holder.textRatingGiven.setText(item.getEventDetail());
            holder.textEventDetailInfo.setVisibility(View.GONE);
        } else if (notiType == 0) {
            holder.textEventDetailInfo.setText(item.getEventDetail());
            holder.ratingInfo.setVisibility(View.GONE);
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
        @BindView(R.id.text_rating_given)
        TextView textRatingGiven;
        @BindView(R.id.given_rating_bar)
        RatingBar givenRatingBar;
        @BindView(R.id.rating_info)
        LinearLayout ratingInfo;
        @BindView(R.id.image_wisher_small)
        ImageView imageWisherSmall;
        @BindView(R.id.text_wisher_comment)
        TextView textWisherComment;
        @BindView(R.id.text_wisher_comment_time)
        TextView textWisherCommentTime;
        @BindView(R.id.image_user)
        ImageView imageUser;
        @BindView(R.id.edittext_user_comment)
        EditText edittextUserComment;
        @BindView(R.id.button_user_comment_submit)
        ImageView buttonUserCommentSubmit;
        @BindView(R.id.layout_user_comment_pending)
        LinearLayout layoutUserCommentPending;
        @BindView(R.id.text_user_comment)
        TextView textUserComment;
        @BindView(R.id.text_user_comment_time)
        TextView textUserCommentTime;
        @BindView(R.id.layout_user_comment_done)
        LinearLayout layoutUserCommentDone;
        @BindView(R.id.linear_content_main)
        LinearLayout linearContentMain;
        @BindView(R.id.relative_row_main)
        RelativeLayout relativeRowMain;
        @BindView(R.id.divider_timeline_item)
        View dividerTimelineItem;
        @BindView(R.id.relative_row_all_contact)
        RelativeLayout relativeRowAllContact;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
