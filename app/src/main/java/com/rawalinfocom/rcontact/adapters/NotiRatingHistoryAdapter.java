package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.NotiRatingItem;
import com.rawalinfocom.rcontact.notifications.RatingHistory;
import com.rawalinfocom.rcontact.notifications.RatingHistoryPopupDialog;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRatingHistoryAdapter extends RecyclerView.Adapter<NotiRatingHistoryAdapter
        .MyViewHolder> {


    private Context context;
    private List<NotiRatingItem> list;
    //    private int recyclerPosition;
    RatingHistoryPopupDialog notificationPopupDialog;

    String imageurl;

    public NotiRatingHistoryAdapter(Context context, List<NotiRatingItem> list/*, int
    recyclerPosition*/) {
        this.context = context;
        this.list = list;
//        this.recyclerPosition = recyclerPosition;
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
        @BindView(R.id.text_rating_given)
        TextView textRatingGiven;
        @BindView(R.id.given_rating_bar)
        RatingBar givenRatingBar;
        @BindView(R.id.text_rating_noti_time)
        TextView textRatingNotiTime;

        @BindView(R.id.text_rating_detail_info)
        TextView textRatingDetailInfo;

        @BindView(R.id.button_rating_view_reply)
        AppCompatButton buttonRatingViewReply;
        @BindView(R.id.relative_row_layout)
        RelativeLayout relativeRowMain;
        @BindView(R.id.history_place_holder)
        TextView historyPlaceHolder;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_noti_rating_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final NotiRatingItem item = list.get(position);
        if (item.getHistoryType() == 0) {
            holder.textRaterName.setText(item.getReceiverPersonName());
        } else {
            holder.textRaterName.setText(item.getRaterName());
        }

        if (!StringUtils.isEmpty(item.getRating())) {
            Float rating = Float.parseFloat(item.getRating());
            holder.textRatingGiven.setText(String.format("%s", rating));
            holder.givenRatingBar.setRating(rating);
        } else {
            holder.textRatingGiven.setText(String.format("%s", "0"));
            holder.givenRatingBar.setRating(0f);
        }

        LayerDrawable stars = (LayerDrawable) holder.givenRatingBar.getProgressDrawable();
        Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(context, R.color
                .vivid_yellow));
        Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(context, android.R
                .color.darker_gray));
        Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(context, android.R
                .color.darker_gray));

        /*if (recyclerPosition == 0) {
            holder.textRatingNotiTime.setText(Utils.formatDateTime(item.getNotiTime(), "hh:mm a"));
        } else {
            holder.textRatingNotiTime.setText(Utils.formatDateTime(item.getNotiTime(), "dd MMM,
            hh:mm a"));
        }*/

        String notiTime = item.getNotiTime();
        String date = Utils.formatDateTime(notiTime, "yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String current = s.format(c.getTime());
        if (StringUtils.equalsIgnoreCase(current, date)) {
            holder.textRatingNotiTime.setText(Utils.formatDateTime(notiTime, "hh:mm a"));
        } else {
            holder.textRatingNotiTime.setText(Utils.formatDateTime(notiTime, "dd MMM, yy"));
        }

        if (item.getHistoryType() == 0) {
            holder.historyPlaceHolder.setText(context.getResources().getString(R.string
                    .text_you_rated));
            holder.textRatingDetailInfo.setText(String.format(context.getString(R.string
                            .str_rating_comment_hint_2)
                    , item.getReceiverPersonName()));
        } else {
            holder.historyPlaceHolder.setText(context.getResources().getString(R.string
                    .text_rated_you));
            holder.textRatingDetailInfo.setText(String.format(context.getString(R.string
                    .str_rating_comment_hint_3), item.getRaterName()));
        }

        if (item.getHistoryType() == 0) {
            imageurl = item.getReceiverPersonImage();
        } else {
            imageurl = item.getRaterPersonImage();
        }
        if (!TextUtils.isEmpty(imageurl)) {
            Glide.with(context)
                    .load(imageurl)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(500, 500)
                    .into(holder.imageRater);

        } else {
            holder.imageRater.setImageResource(R.drawable.home_screen_profile);
        }

        holder.imageRater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isBlank(imageurl)) {
                    Utils.zoomImageFromThumb((Activity) context, holder.imageRater, imageurl, (
                            (RatingHistory) context).frameImageEnlarge, ((RatingHistory) context)
                            .imageEnlarge, ((RatingHistory) context).frameContainer);
                }
            }
        });

        holder.relativeRowMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayListComments = new ArrayList<>();
                arrayListComments.add(item.getRaterName());
                arrayListComments.add(item.getReceiverPersonName());
                arrayListComments.add(item.getComment());
                arrayListComments.add(Utils.formatDateTime(item.getCommentTime(), "dd MMM, hh:mm " +
                        "a"));
                arrayListComments.add(item.getReply());
                arrayListComments.add(Utils.formatDateTime(item.getReplyTime(), "dd MMM, hh:mm a"));
                arrayListComments.add(item.getRaterPersonImage());
                arrayListComments.add(item.getReceiverPersonImage());

                notificationPopupDialog = new RatingHistoryPopupDialog(context,
                        arrayListComments, true);
                if (item.getHistoryType() == 0) {
                    notificationPopupDialog.setDialogTitle(context.getResources().getString(R
                            .string.text_you_rated)
                            + " " + item.getReceiverPersonName());
                } else {
                    notificationPopupDialog.setDialogTitle(item.getRaterName() + " " + context
                            .getResources().getString(R.string.text_rated_you));
                }
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
