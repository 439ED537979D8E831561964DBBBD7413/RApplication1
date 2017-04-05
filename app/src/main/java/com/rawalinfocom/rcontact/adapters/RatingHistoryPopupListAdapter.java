package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_wisher_small)
        ImageView imageWisherSmall;
        @BindView(R.id.text_wisher_comment)
        TextView textWisherComment;
        @BindView(R.id.text_wisher_comment_time)
        TextView textWisherCommentTime;
        @BindView(R.id.image_user)
        ImageView imageUser;
        @BindView(R.id.text_user_comment)
        TextView textUserComment;
        @BindView(R.id.text_user_comment_time)
        TextView textUserCommentTime;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
