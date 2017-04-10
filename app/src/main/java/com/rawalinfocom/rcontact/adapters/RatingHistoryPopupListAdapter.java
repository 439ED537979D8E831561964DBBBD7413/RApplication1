package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class RatingHistoryPopupListAdapter extends RecyclerView.Adapter<RatingHistoryPopupListAdapter.MyViewHolder> {


    private ArrayList<String> stringArrayList;
    private Context context;

    public RatingHistoryPopupListAdapter(Context context, ArrayList<String> stringArrayList) {
        this.stringArrayList = stringArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_rating_history_popup, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textRaterName.setText(stringArrayList.get(0));
        holder.textReceiverName.setText(stringArrayList.get(1));
        if (stringArrayList.get(2) != null && stringArrayList.get(2).length() > 0) {
            holder.textRaterComment.setText(stringArrayList.get(2));
        } else {
            holder.textRaterComment.setVisibility(View.GONE);
        }
        holder.textRaterCommentTime.setText(stringArrayList.get(3));
        if (stringArrayList.get(4) != null && stringArrayList.get(4).length() > 0) {
            holder.textReceiverComment.setText(stringArrayList.get(4));
            holder.textReceiverCommentTime.setText(stringArrayList.get(5));
        } else {
            holder.textReceiverComment.setVisibility(View.GONE);
            holder.textReceiverCommentTime.setVisibility(View.GONE);
            holder.imageReceiver.setVisibility(View.GONE);
            holder.textReceiverName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_rater)
        ImageView imageRater;
        @BindView(R.id.text_rater_name)
        TextView textRaterName;
        @BindView(R.id.text_rater_comment)
        TextView textRaterComment;
        @BindView(R.id.text_rater_comment_time)
        TextView textRaterCommentTime;
        @BindView(R.id.image_receiver)
        ImageView imageReceiver;
        @BindView(R.id.text_receiver_name)
        TextView textReceiverName;
        @BindView(R.id.text_receiver_comment)
        TextView textReceiverComment;
        @BindView(R.id.text_receiver_comment_time)
        TextView textReceiverCommentTime;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
