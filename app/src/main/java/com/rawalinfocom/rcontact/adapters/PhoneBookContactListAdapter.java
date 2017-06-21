package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 15/02/16.
 */

public class PhoneBookContactListAdapter extends RecyclerView.Adapter<PhoneBookContactListAdapter
        .contactViewHolder> {

    private Context context;
    private ArrayList<UserProfile> arrayListUserProfile;
    private ArrayList<UserProfile> arrayListTempUserProfile;

    private boolean isSelectedAll;
    private ArrayList<Integer> arrayListCheckedPositions;

    public PhoneBookContactListAdapter(Context context, ArrayList<UserProfile>
            arrayListUserProfile) {
        this.context = context;
        this.arrayListUserProfile = arrayListUserProfile;
        arrayListTempUserProfile = new ArrayList<>();
        arrayListCheckedPositions = new ArrayList<>();

//        arrayListTempUserProfile.addAll(arrayListUserProfile);
    }

    @Override
    public contactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_phonebook_contact, parent, false);
        return new contactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(contactViewHolder holder, int position) {

        if (arrayListTempUserProfile.size() <= 0) {
            arrayListTempUserProfile.addAll(arrayListUserProfile);
        }
        UserProfile userProfile = arrayListUserProfile.get(position);

        holder.textContactName.setText(userProfile.getPmFirstName());
        holder.checkboxSelectContact.setTag(position);

        String contactInformation = userProfile.getMobileNumber();
        if (StringUtils.length(contactInformation) <= 0) {
            contactInformation = userProfile.getEmailId();
        }

        holder.textContactNumber.setText(contactInformation);

       /* if (arrayListCheckedPositions.contains(position)) {
            holder.checkboxSelectContact.setChecked(true);
        } else {
            holder.checkboxSelectContact.setChecked(false);
        }*/

        if (!isSelectedAll) {
            holder.checkboxSelectContact.setChecked(false);
            if (arrayListCheckedPositions.contains(position)) {
                holder.checkboxSelectContact.setChecked(true);
            } else {
                holder.checkboxSelectContact.setChecked(false);
            }
        } else {
            holder.checkboxSelectContact.setChecked(true);
        }

        holder.checkboxSelectContact.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!arrayListCheckedPositions.contains((Integer) buttonView.getTag())) {
                        arrayListCheckedPositions.add((Integer) buttonView.getTag());
                    }
                } else {
                    if (arrayListCheckedPositions.contains((Integer) buttonView.getTag())) {
                        arrayListCheckedPositions.remove((Integer) buttonView.getTag());
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayListUserProfile.size();
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        arrayListUserProfile.clear();
        if (charText.length() == 0) {
            arrayListUserProfile.addAll(arrayListTempUserProfile);
        } else {
            for (UserProfile userProfile : arrayListTempUserProfile) {
                if (userProfile.getPmFirstName().toLowerCase(Locale.getDefault()).contains
                        (charText)) {
                    arrayListUserProfile.add(userProfile);
                }
            }
        }
        notifyDataSetChanged();
    }

    class contactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.checkbox_select_contact)
        CheckBox checkboxSelectContact;
        @BindView(R.id.text_contact_name)
        TextView textContactName;
        @BindView(R.id.text_contact_number)
        TextView textContactNumber;
        @BindView(R.id.linear_content_main)
        LinearLayout linearContentMain;
        @BindView(R.id.relative_row_main)
        RelativeLayout relativeRowMain;
        @BindView(R.id.divider_all_contact)
        View dividerAllContact;
        @BindView(R.id.relative_row_contact)
        RelativeLayout relativeRowContact;

        contactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceSemiBold(context));
            textContactNumber.setTypeface(Utils.typefaceRegular(context));

        }
    }

    public void isSelectAll(boolean checked) {
        isSelectedAll = checked;
        arrayListCheckedPositions.clear();
        if (checked) {
            for (int i = 0; i < getItemCount(); i++) {
                if (!arrayListCheckedPositions.contains(i)) {
                    arrayListCheckedPositions.add(i);
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getArrayListCheckedPositions() {
        return arrayListCheckedPositions;
    }
}
