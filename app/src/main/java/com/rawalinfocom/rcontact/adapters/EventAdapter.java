package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BuildConfig;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.EventItem;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.notifications.EventsActivity;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {


    private List<EventItem> list;
    private Activity activity;
//    private int recyclerPosition;

    public EventAdapter(Activity activity, List<EventItem> list/*, int recyclerPosition*/) {
        this.list = list;
        this.activity = activity;
//        this.recyclerPosition = recyclerPosition;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final EventItem item = list.get(position);
        holder.textPersonName.setText(item.getPersonName());
        holder.textEventName.setText(item.getEventName());
//        holder.textEventCommentTime.setText(Utils.formatDateTime(item.getCommentTime(), "dd-MM hh:mm a"));

        String notiTime =  item.getEventDate();
        String date =  Utils.formatDateTime(notiTime,"MM-dd");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("MM-dd");
        String current = s.format(c.getTime());

        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DATE, 1);
        SimpleDateFormat s1 = new SimpleDateFormat("MM-dd");
        String tomorrowDate =  s1.format(c1.getTime());

        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, -1);
        SimpleDateFormat s2 = new SimpleDateFormat("MM-dd");
        String yesDate =  s2.format(c2.getTime());

        if(StringUtils.equalsIgnoreCase(current,date)){
            holder.textEventCommentTime.setVisibility(View.VISIBLE);
            holder.textEventCommentTime.setText("Today");
        }else if(StringUtils.equalsIgnoreCase(yesDate,date)){
            holder.textEventCommentTime.setVisibility(View.VISIBLE);
            holder.textEventCommentTime.setText("Yesterday");
        }else{
            holder.textEventCommentTime.setVisibility(View.VISIBLE);
            holder.textEventCommentTime.setText(Utils.formatDateTime(notiTime,"dd MMM, yy"));
        }

        int notiType = item.getEventType();
        String on = activity.getResources().getString(R.string.text_on);
        String eventDetail = "";
        if (item.getEventDetail().length() != 0) {
            eventDetail = item.getEventDetail() + ", ";
        }
        if (notiType == AppConstants.COMMENT_TYPE_BIRTHDAY) {
            holder.textEventDetailInfo.setText(eventDetail + item.getEventName() + on + Utils.formatDateTime(item.getEventDate(), "dd MMM"));
        } else if (notiType == AppConstants.COMMENT_TYPE_ANNIVERSARY) {
            holder.textEventDetailInfo.setText(eventDetail + item.getEventName() + on + Utils.formatDateTime(item.getEventDate(), "dd MMM"));
        } else {
            holder.textEventDetailInfo.setText(item.getEventName() + on + Utils.formatDateTime(item.getEventDate(), "dd MMM"));
        }

        if(StringUtils.equalsIgnoreCase(current,date)){
            if (!item.isEventCommentPending()) {
                holder.textUserComment.setVisibility(View.VISIBLE);
                holder.textUserComment.setText(activity.getResources().getString(R.string.text_you_wrote_on_timeline, item.getPersonFirstName()));
                holder.layoutUserCommentPending.setVisibility(View.GONE);
            }else{
                holder.textUserComment.setVisibility(View.GONE);
                holder.layoutUserCommentPending.setVisibility(View.VISIBLE);
            }
        }else if(StringUtils.equalsIgnoreCase(yesDate,date)){
            if (!item.isEventCommentPending()) {
                holder.textUserComment.setVisibility(View.VISIBLE);
                holder.textUserComment.setText(activity.getResources().getString(R.string.text_you_wrote_on_timeline, item.getPersonFirstName()));
                holder.layoutUserCommentPending.setVisibility(View.GONE);
            }else{
                holder.textUserComment.setVisibility(View.GONE);
                holder.layoutUserCommentPending.setVisibility(View.VISIBLE);
            }
        }else{
            holder.layoutUserCommentPending.setVisibility(View.GONE);
            holder.textUserComment.setVisibility(View.GONE);
            holder.textEventCommentTime.setVisibility(View.GONE);
        }


       /* if (recyclerPosition != 2) {
            if (!item.isEventCommentPending()) {
                holder.textUserComment.setVisibility(View.VISIBLE);
                holder.textUserComment.setText(activity.getResources().getString(R.string.text_you_wrote_on_timeline, item.getPersonFirstName()));
                holder.layoutUserCommentPending.setVisibility(View.GONE);
            } else {
                holder.textUserComment.setVisibility(View.GONE);
                holder.layoutUserCommentPending.setVisibility(View.VISIBLE);
            }
        } else {
            // upcoming events
            holder.layoutUserCommentPending.setVisibility(View.GONE);
            holder.textUserComment.setVisibility(View.GONE);
            holder.textEventCommentTime.setVisibility(View.GONE);
        }*/

        if (!TextUtils.isEmpty(item.getPersonImage())) {
            Glide.with(activity)
                    .load(item.getPersonImage())
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .override(500, 500)
                    .into(holder.imagePerson);

        } else {
            holder.imagePerson.setImageResource(R.drawable.home_screen_profile);
        }

        holder.imagePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isBlank(item.getPersonImage())) {
                    Utils.zoomImageFromThumb(activity, holder.imagePerson, item
                            .getPersonImage(), ((EventsActivity) activity)
                            .frameImageEnlarge, ((EventsActivity) activity).imageEnlarge, (
                            (EventsActivity ) activity).frameContainer);
                }
            }
        });

        holder.buttonUserCommentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userComment = holder.edittextUserComment.getText().toString().trim();
                if (!(userComment.matches(""))) {
                    EventsActivity.evmRecordId = item.getEventRecordIndexId();
                    EventsActivity.selectedRecycler = 0;
                    EventsActivity.selectedRecyclerItem = position;
                    addEventComment(item.getEventName(), item.getPersonRcpPmId(), userComment, item.getEventDate(), AppConstants.COMMENT_STATUS_SENT, item.getEventRecordIndexId());
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.msg_please_enter_some_comment), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<EventItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_person)
        ImageView imagePerson;
        @BindView(R.id.text_person_name)
        TextView textPersonName;
        @BindView(R.id.text_event_name)
        TextView textEventName;
        @BindView(R.id.text_event_comment_time)
        TextView textEventCommentTime;
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
            textPersonName.setTypeface(Utils.typefaceRegular(activity));

            textEventName.setTypeface(Utils.typefaceRegular(activity));
            textEventCommentTime.setTypeface(Utils.typefaceRegular(activity));

            textEventDetailInfo.setTypeface(Utils.typefaceRegular(activity));

            edittextUserComment.setTypeface(Utils.typefaceRegular(activity));
            textUserComment.setTypeface(Utils.typefaceRegular(activity));

        }

    }

    private void addEventComment(String eventType, int toPmId, String comment, String date, int status, String eventRecordIndexId) {

        WsRequestObject addCommentObject = new WsRequestObject();

        addCommentObject.setType(eventType);
        addCommentObject.setToPmId(toPmId);
        addCommentObject.setComment(comment);
        addCommentObject.setDate(date);
        addCommentObject.setEvmRecordIndexId(eventRecordIndexId);
        addCommentObject.setStatus(status + "");

        if (Utils.isNetworkAvailable(activity)) {
            new AsyncWebServiceCall(activity, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    addCommentObject, null, WsResponseObject.class, WsConstants
                    .REQ_ADD_EVENT_COMMENT, activity.getResources().getString(R.string.msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT + WsConstants.REQ_ADD_EVENT_COMMENT);
        } else {
            //show no toast
            Toast.makeText(activity, activity.getResources().getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }
}
