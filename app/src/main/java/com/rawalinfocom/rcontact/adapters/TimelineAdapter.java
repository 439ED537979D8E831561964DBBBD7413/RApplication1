package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.TimelineItem;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.notifications.TimelineActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.MyViewHolder> {

    private List<TimelineItem> list;
    private Activity activity;
    private int recyclerPosition;

    public TimelineAdapter(Activity activity, List<TimelineItem> list, int recyclerPosition) {
        this.list = list;
        this.activity = activity;
        this.recyclerPosition = recyclerPosition;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_item_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TimelineItem item = list.get(position);
        holder.textWisherName.setText(item.getWisherName());
        holder.textEventName.setText(item.getEventName());
        holder.textTimelineNotiTime.setText(item.getNotiTime());
        String wisherComment = item.getWisherComment();
        String userComment = item.getUserComment();

        int notiType = 0;
        if (activity.getResources().getString(R.string.str_tab_rating).equalsIgnoreCase(item.getCrmType()))
            notiType = 1;
        if (wisherComment != null && wisherComment.length() > 0) {
            holder.textWisherComment.setVisibility(View.VISIBLE);
            holder.textWisherComment.setText(wisherComment);
            if (recyclerPosition == 0)
                holder.textWisherCommentTime.setText(Utils.formatDateTime(item.getWisherCommentTime(), "hh:mm a"));
            else
                holder.textWisherCommentTime.setText(Utils.formatDateTime(item.getWisherCommentTime(), "dd MMM, hh:mm a"));
        } else {
            holder.textWisherComment.setVisibility(View.GONE);
            if (notiType == 1) {
                //set rating done time only
                if (recyclerPosition == 0)
                    holder.textWisherCommentTime.setText(Utils.formatDateTime(item.getWisherCommentTime(), "hh:mm a"));
                else
                    holder.textWisherCommentTime.setText(Utils.formatDateTime(item.getWisherCommentTime(), "dd MMM, hh:mm a"));
            }
        }
        if (userComment != null && userComment.length() > 0) {
            holder.layoutUserCommentDone.setVisibility(View.VISIBLE);
            holder.textUserComment.setText(userComment);

            if (recyclerPosition == 0)
                holder.textUserCommentTime.setText(Utils.formatDateTime(item.getUserCommentTime(), "hh:mm a"));
            else
                holder.textUserCommentTime.setText(Utils.formatDateTime(item.getUserCommentTime(), "dd MMM, hh:mm a"));

            holder.layoutUserCommentPending.setVisibility(View.GONE);
        } else {
            holder.layoutUserCommentDone.setVisibility(View.GONE);
            holder.layoutUserCommentPending.setVisibility(View.VISIBLE);
        }
        if (notiType == 1) {
            holder.ratingInfo.setVisibility(View.VISIBLE);
            holder.textRatingGiven.setText(item.getCrmRating());
            holder.givenRatingBar.setRating(Float.parseFloat(item.getCrmRating()));
            LayerDrawable stars = (LayerDrawable) holder.givenRatingBar.getProgressDrawable();
            // Filled stars
            Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(activity, R.color
                    .vivid_yellow));
            Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(activity, android.R
                    .color.darker_gray));
            // Empty stars
            Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(activity, android.R
                    .color.darker_gray));
            holder.textEventDetailInfo.setVisibility(View.GONE);
        } else {
            holder.textEventDetailInfo.setText(item.getEventDetail());
            holder.ratingInfo.setVisibility(View.GONE);
        }
        holder.buttonUserCommentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userComment = holder.edittextUserComment.getText().toString();
                if (userComment != null && userComment.length() > 0) {
                    TimelineActivity.selectedRecycler = recyclerPosition;
                    TimelineActivity.selectedRecyclerItem = position;
                    addReplyonComment(item.getCrmType(), item.getCrmCloudPrId(), userComment, AppConstants.COMMENT_STATUS_RECEIVED, item.getEvmRecordIndexId());
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.msg_please_enter_some_comment), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addReplyonComment(String crmType, String crmCloudPrId, String userComment, int commentStatusReceived, String evmRecordIndexId) {

        WsRequestObject addCommentObject = new WsRequestObject();
        if (crmType.equalsIgnoreCase(activity.getResources().getString(R.string.str_tab_rating))) {
            addCommentObject.setPrId(crmCloudPrId);
            addCommentObject.setPrReply(userComment);
            addCommentObject.setPrStatus(commentStatusReceived + "");
        } else {
            addCommentObject.setType(crmType);
            addCommentObject.setCommentId(crmCloudPrId);
            addCommentObject.setReply(userComment);
            addCommentObject.setEvmRecordIndexId(evmRecordIndexId);
            addCommentObject.setStatus(commentStatusReceived + "");
        }
        if (Utils.isNetworkAvailable(activity)) {
            if (crmType.equalsIgnoreCase(activity.getResources().getString(R.string.str_tab_rating))) {
                new AsyncWebServiceCall(activity, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        addCommentObject, null, WsResponseObject.class, WsConstants
                        .REQ_PROFILE_RATING, activity.getResources().getString(R.string.msg_please_wait), true).execute
                        (WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_RATING);
            } else {
                new AsyncWebServiceCall(activity, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        addCommentObject, null, WsResponseObject.class, WsConstants
                        .REQ_ADD_EVENT_COMMENT, activity.getResources().getString(R.string.msg_please_wait), true).execute
                        (WsConstants.WS_ROOT + WsConstants.REQ_ADD_EVENT_COMMENT);
            }
        } else {
            //show no toast
            Toast.makeText(activity, activity.getResources().getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<TimelineItem> list) {
        this.list = list;
        notifyDataSetChanged();
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

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            textWisherName.setTypeface(Utils.typefaceRegular(activity));

            textEventName.setTypeface(Utils.typefaceRegular(activity));
            textTimelineNotiTime.setTypeface(Utils.typefaceRegular(activity));

            textEventDetailInfo.setTypeface(Utils.typefaceRegular(activity));
            textRatingGiven.setTypeface(Utils.typefaceRegular(activity));


            textWisherComment.setTypeface(Utils.typefaceRegular(activity));
            textWisherCommentTime.setTypeface(Utils.typefaceRegular(activity));

            edittextUserComment.setTypeface(Utils.typefaceRegular(activity));
            textUserComment.setTypeface(Utils.typefaceRegular(activity));
            textUserCommentTime.setTypeface(Utils.typefaceRegular(activity));
        }
    }


}
