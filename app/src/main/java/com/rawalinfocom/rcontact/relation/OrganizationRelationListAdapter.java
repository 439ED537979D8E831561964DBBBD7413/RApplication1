package com.rawalinfocom.rcontact.relation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class OrganizationRelationListAdapter extends RecyclerView.Adapter<OrganizationRelationListAdapter
        .OrganizationViewHolder> {

    private Context context;
    private ArrayList<ProfileDataOperationOrganization> arrayListOrganization;
    private String businessRelationName;

    private OnClickListener clickListener;

    public interface OnClickListener {
        void onClick(String orgId, String orgName);
    }

    public OrganizationRelationListAdapter(Context context, ArrayList<ProfileDataOperationOrganization>
            arrayListOrganization, OnClickListener clickListener, String businessRelationName) {
        this.context = context;
        this.clickListener = clickListener;
        this.arrayListOrganization = arrayListOrganization;
    }

    @Override
    public OrganizationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_organization_list_relation, parent, false);
        return new OrganizationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrganizationViewHolder holder, int position) {

        ProfileDataOperationOrganization organization = arrayListOrganization.get(position);

        holder.textMain.setText(organization.getOrgName());
        holder.checkbox.setChecked(position == (AddNewRelationActivity.orgPosition));

        if (organization.getIsInUse().equalsIgnoreCase("1")) {
            holder.textMain.setEnabled(false);
            holder.checkbox.setVisibility(View.GONE);
        }

        if (position == (AddNewRelationActivity.orgPosition)) {
            if (clickListener != null) {
                clickListener.onClick(arrayListOrganization.get(AddNewRelationActivity.orgPosition)
                        .getOrgEntId(), arrayListOrganization.get(AddNewRelationActivity.orgPosition)
                        .getOrgName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayListOrganization.size();
    }

    class OrganizationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_main)
        TextView textMain;
        @BindView(R.id.checkbox)
        RadioButton checkbox;

        OrganizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textMain.setTypeface(Utils.typefaceRegular(context));

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddNewRelationActivity.orgPosition = getAdapterPosition();
                    notifyDataSetChanged();

                }
            });
        }
    }
}
