package com.rawalinfocom.rcontact.timeline;

import android.content.Context;
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
import com.rawalinfocom.rcontact.TimelineActivity;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.MyViewHolder> {

    private List<TimelineItem> list;
    private Context context;
    private int recyclerPosition;

    public TimelineAdapter(Context context, List<TimelineItem> list, int recyclerPosition) {
        this.list = list;
        this.context = context;
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
        int notiType = item.getNotiType();
        if (wisherComment != null && wisherComment.length() != 0) {
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
                holder.textWisherCommentTime.setText("12:03PM");
            }
        }
        if (userComment != null && userComment.length() != 0) {
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
            holder.textRatingGiven.setText(item.getEventDetail());
            holder.textEventDetailInfo.setVisibility(View.GONE);
        } else if (notiType == 0) {
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
                    Toast.makeText(context, "Please enter some comment first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addReplyonComment(String crmType, String crmCloudPrId, String userComment, int commentStatusReceived, int evmRecordIndexId) {
        WsRequestObject addCommentObject = new WsRequestObject();

        addCommentObject.setType(crmType);
        addCommentObject.setCommentId(crmCloudPrId);
        addCommentObject.setReply(userComment);
        addCommentObject.setEvmRecordIndexId(evmRecordIndexId);
        addCommentObject.setStatus(commentStatusReceived + "");

        if (Utils.isNetworkAvailable(context)) {
            new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    addCommentObject, null, WsResponseObject.class, WsConstants
                    .REQ_ADD_EVENT_COMMENT, "Sending comments..", true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_ADD_EVENT_COMMENT);
        } else {
            //show no toast
            Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
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

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            textWisherName.setTypeface(Utils.typefaceRegular(context));

            textEventName.setTypeface(Utils.typefaceRegular(context));
            textTimelineNotiTime.setTypeface(Utils.typefaceRegular(context));

            textEventDetailInfo.setTypeface(Utils.typefaceRegular(context));
            textRatingGiven.setTypeface(Utils.typefaceRegular(context));


            textWisherComment.setTypeface(Utils.typefaceRegular(context));
            textWisherCommentTime.setTypeface(Utils.typefaceRegular(context));

            edittextUserComment.setTypeface(Utils.typefaceRegular(context));
            textUserComment.setTypeface(Utils.typefaceRegular(context));
            textUserCommentTime.setTypeface(Utils.typefaceRegular(context));
        }
    }


}
