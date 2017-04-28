package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class OrganizationListAdapter extends RecyclerView.Adapter<OrganizationListAdapter
        .OrganizationViewHolder> {

    private Context context;
    private ArrayList<ProfileDataOperationOrganization> arrayListOrganization;

    public OrganizationListAdapter(Context context, ArrayList<ProfileDataOperationOrganization>
            arrayListOrganization) {
        this.context = context;
        this.arrayListOrganization = arrayListOrganization;
    }

    @Override
    public OrganizationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_all_organization, parent, false);
        return new OrganizationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrganizationViewHolder holder, int position) {

        ProfileDataOperationOrganization organization = arrayListOrganization.get(position);

        holder.textMain.setText(organization.getOrgName());
        holder.textSub.setText(organization.getOrgJobTitle());

        /*int orgRcpType = Integer.parseInt(StringUtils.defaultString(organization.getOrgRcpType()
                , String.valueOf(context.getResources().getInteger(R.integer
                        .rcp_type_cloud_phone_book))));*/
        int orgRcpType = Integer.parseInt(StringUtils.defaultString(organization.getOrgRcpType()
                , String.valueOf(IntegerConstants.RCP_TYPE_CLOUD_PHONE_BOOK)));

        if (orgRcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textSub.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
        } else {
            holder.textSub.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

    }

    @Override
    public int getItemCount() {
        return arrayListOrganization.size();
    }


    class OrganizationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_main)
        TextView textMain;
        @BindView(R.id.text_sub)
        TextView textSub;

        OrganizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textMain.setTypeface(Utils.typefaceRegular(context));
            textSub.setTypeface(Utils.typefaceRegular(context));

        }
    }

}
