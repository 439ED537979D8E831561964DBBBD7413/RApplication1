package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.Country;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 07/12/16.
 */

public class RContactListAdapter extends RecyclerView.Adapter<RContactListAdapter
        .AllContactViewHolder> {

    private Context context;
    private ArrayList<UserProfile> arrayListUserProfile;

    String defaultCountryCode;

    public RContactListAdapter(Context context, ArrayList<UserProfile> arrayListUserProfile) {
        this.context = context;
        this.arrayListUserProfile = arrayListUserProfile;

        Country country = (Country) Utils.getObjectPreference(context, AppConstants
                .PREF_SELECTED_COUNTRY_OBJECT, Country.class);
        if (country != null) {
            defaultCountryCode = country.getCountryCodeNumber();
        }
    }

    @Override
    public AllContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_all_contacts,
                parent, false);
        return new AllContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AllContactViewHolder holder, int position) {

        UserProfile userProfile = arrayListUserProfile.get(position);

        String contactDisplayName = userProfile.getPmFirstName() + " " + userProfile
                .getPmLastName();
        holder.textContactName.setText(contactDisplayName);
        if (StringUtils.length(userProfile.getMobileNumber()) > 0) {
            holder.textContactNumber.setText(userProfile.getMobileNumber());
        } else if (StringUtils.length(userProfile.getEmailId()) > 0) {
            holder.textContactNumber.setText(userProfile.getEmailId());
        }

    }

    @Override
    public int getItemCount() {
        return arrayListUserProfile.size();
    }

    class AllContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.text_contact_name)
        TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.text_contact_number)
        TextView textContactNumber;

        AllContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceSemiBold(context));
            textCloudContactName.setTypeface(Utils.typefaceSemiBold(context));
            textContactNumber.setTypeface(Utils.typefaceRegular(context));

            textContactName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            textContactNumber.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

            textCloudContactName.setVisibility(View.GONE);

        }
    }

}
