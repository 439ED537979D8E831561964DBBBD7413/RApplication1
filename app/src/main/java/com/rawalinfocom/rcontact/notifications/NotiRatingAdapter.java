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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRatingAdapter extends RecyclerView.Adapter<NotiRatingAdapter.MyViewHolder> {


    private Context context;
    private List<NotiRatingItem> list;
    NotificationPopupDialog notificationPopupDialog;

    public NotiRatingAdapter(Context context, List<NotiRatingItem> list) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_rater)
        ImageView imageRater;
        @BindView(R.id.text_rater_name)
        TextView textRaterName;
        @BindView(R.id.text_rating_noti_time)
        TextView textRatingNotiTime;
        @BindView(R.id.text_rating_detail_info)
        TextView textRatingDetailInfo;
        @BindView(R.id.button_rating_view_reply)
        Button buttonRatingViewReply;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_noti_rating, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NotiRatingItem item = list.get(position);
        holder.textRaterName.setText(item.getRaterName());
        holder.textRatingNotiTime.setText(item.getNotiRatingTime());
        holder.textRatingDetailInfo.setText(item.getRaterInfo());
        holder.buttonRatingViewReply.setText("VIEW REPLY");
        holder.buttonRatingViewReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayListComments = new ArrayList<>();
                arrayListComments.add("Hello");
                arrayListComments.add("Thanks");
                notificationPopupDialog = new NotificationPopupDialog(context, arrayListComments);
                notificationPopupDialog.setDialogTitle(item.getRaterName());
                notificationPopupDialog.showDialog();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
