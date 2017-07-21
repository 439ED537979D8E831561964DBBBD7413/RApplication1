package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 30/03/17.
 */

public class BlockedContactsListAdapter extends RecyclerView.Adapter<BlockedContactsListAdapter
        .callLogViewHolder> {


    private Context context;
    private ArrayList<CallLogType> arrayListCallType;
    private ArrayList<CallLogType> arrayListTempCallType;
    private ArrayList<CallLogType> arrayListToUnblock;

    public ArrayList<CallLogType> getArrayListToDelete() {
        return arrayListToUnblock;
    }

    public void setArrayListToUnblock(ArrayList<CallLogType> arrayListToDelete) {
        this.arrayListToUnblock = arrayListToDelete;
    }

    public boolean isSelectedAll;
    private ArrayList<Integer> arrayListCheckedPositions;

    public BlockedContactsListAdapter(Context context, ArrayList<CallLogType>
            arrayList) {
        this.context = context;
        this.arrayListCallType = arrayList;
        this.arrayListTempCallType = new ArrayList<>();
        arrayListCheckedPositions = new ArrayList<>();
        arrayListTempCallType.addAll(arrayList);
        arrayListToUnblock = new ArrayList<>();
    }

    @Override
    public callLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_blocked_contact, parent, false);
        return new callLogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(callLogViewHolder holder, int position) {
        final CallLogType callLogType = arrayListCallType.get(position);

        String name = callLogType.getName();
        if (!TextUtils.isEmpty(name)) {
            holder.textContactName.setTypeface(Utils.typefaceBold(context));
            holder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                    .colorBlack));
            holder.textContactName.setText(name);
        } else {
            holder.textContactName.setTypeface(Utils.typefaceBold(context));
            holder.textContactName.setTextColor(ContextCompat.getColor(context, R.color
                    .colorBlack));
            holder.textContactName.setText(context.getString(R.string.unknown).replace("[", "").replace("]", ""));
        }

        String number = callLogType.getNumber();
        if (!TextUtils.isEmpty(number)) {
            holder.textNumber.setTextColor(ContextCompat.getColor(context, R.color
                    .mostlyDesaturatedDarkCyanLimeGreen));
            holder.textNumber.setText(number);
        } else {

        }

        holder.imageUnblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.getHashMapPreferenceForBlock(context, AppConstants
                        .PREF_BLOCK_CONTACT_LIST) != null) {
                    HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                            Utils.getHashMapPreferenceForBlock(context, AppConstants.PREF_BLOCK_CONTACT_LIST);
                    if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                        String hasMapKey = callLogType.getUniqueContactId();
                        blockProfileHashMapList.remove(hasMapKey);
                        Utils.setHashMapPreference(context, AppConstants.PREF_BLOCK_CONTACT_LIST,
                                blockProfileHashMapList);
                        Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_UNBLOCK);
                        LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                        myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayListCallType.size();
    }

    class callLogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.image_unblock)
        ImageView imageUnblock;
        @BindView(R.id.text_contact_name)
        TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.text_number)
        TextView textNumber;
        @BindView(R.id.linear_content_main)
        LinearLayout linearContentMain;
        @BindView(R.id.relative_row_main)
        RelativeLayout relativeRowMain;

        callLogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public void isSelectAll(boolean checked) {
        isSelectedAll = checked;
        arrayListCheckedPositions.clear();
        arrayListToUnblock.removeAll(arrayListTempCallType);
        setArrayListToUnblock(arrayListToUnblock);
        if (checked) {
            for (int i = 0; i < getItemCount(); i++) {
                if (!arrayListCheckedPositions.contains(i)) {
                    arrayListCheckedPositions.add(i);
                }
            }
            arrayListToUnblock.addAll(arrayListTempCallType);
            setArrayListToUnblock(arrayListToUnblock);
        }
        notifyDataSetChanged();
    }


    public ArrayList<Integer> getArrayListCheckedPositions() {
        return arrayListCheckedPositions;
    }

}
