package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotificationPopupListAdapter extends RecyclerView.Adapter<NotificationPopupListAdapter.MyViewHolder> {
    private ArrayList<String> stringArrayList;
    private Context context;

    public NotificationPopupListAdapter(Context context, ArrayList<String> stringArrayList) {
        this.stringArrayList = stringArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_notification_popup, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textWisherName.setText(stringArrayList.get(0));
        holder.textTimelineNotiTime.setText(stringArrayList.get(1));
        if (stringArrayList.get(2) != null && stringArrayList.get(2).length() > 0) {
            holder.textWisherComment.setText(stringArrayList.get(2));
        } else {
            holder.textWisherComment.setVisibility(View.GONE);
        }
        holder.textWisherCommentTime.setText(stringArrayList.get(3));
        if (stringArrayList.get(4) != null && stringArrayList.get(4).length() > 0) {
            holder.textUserComment.setText(stringArrayList.get(4));
            holder.textUserCommentTime.setText(stringArrayList.get(5));
        } else {
            holder.textUserComment.setVisibility(View.GONE);
            holder.textUserCommentTime.setVisibility(View.GONE);
            holder.imageUser.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(stringArrayList.get(6))) {
            Glide.with(context)
                    .load(stringArrayList.get(6))
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(500, 500)
                    .into(holder.imageWisher);

        } else {
            holder.imageWisher.setImageResource(R.drawable.home_screen_profile);
        }
        if (!TextUtils.isEmpty(stringArrayList.get(7))) {
            Glide.with(context)
                    .load(stringArrayList.get(7))
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(500, 500)
                    .into(holder.imageWisherSmall);

        } else {
            holder.imageWisherSmall.setImageResource(R.drawable.home_screen_profile);
        }
        if (!TextUtils.isEmpty(stringArrayList.get(6))) {
            Glide.with(context)
                    .load(stringArrayList.get(6))
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(500, 500)
                    .into(holder.imageUser);

        } else {
            holder.imageUser.setImageResource(R.drawable.home_screen_profile);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_wisher)
        ImageView imageWisher;
        @BindView(R.id.text_wisher_name)
        TextView textWisherName;
        @BindView(R.id.text_event_name)
        TextView textEventName;
        @BindView(R.id.text_timeline_noti_time)
        TextView textTimelineNotiTime;
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

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
