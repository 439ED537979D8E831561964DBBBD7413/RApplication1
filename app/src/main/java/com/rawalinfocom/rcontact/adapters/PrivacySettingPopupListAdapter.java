package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.contacts.PrivacySettingPopupDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class PrivacySettingPopupListAdapter extends RecyclerView.Adapter<PrivacySettingPopupListAdapter.MyViewHolder> {


    private ArrayList<String> stringArrayList;
    private Context context;

    public PrivacySettingPopupListAdapter(Context context, ArrayList<String> stringArrayList) {
        this.stringArrayList = stringArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_dialog_privacy_settings, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvDialogTitle.setText(stringArrayList.get(position));

        holder.checkbox.setChecked(position == PrivacySettingPopupDialog.lastCheckedPosition);
        switch (position) {
            case 0:
                //everyone
                holder.imageView.setImageResource(R.drawable.ic_privacy_public);
                break;
            case 1:
                //my contacts
                holder.imageView.setImageResource(R.drawable.ic_privacy_my_contact);
                break;
            case 2:
                //only me
                holder.imageView.setImageResource(R.drawable.ic_privacy_onlyme);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.tvDialogTitle)
        TextView tvDialogTitle;
        @BindView(R.id.checkbox)
        RadioButton checkbox;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivacySettingPopupDialog.lastCheckedPosition = getAdapterPosition();
                    notifyDataSetChanged();

                }
            });
        }


    }
}
