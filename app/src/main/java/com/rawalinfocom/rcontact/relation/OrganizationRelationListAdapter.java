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
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;

import org.apache.commons.lang3.StringUtils;

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
    private ArrayList<IndividualRelationType> arrayListRelation;
    private ArrayList<String> arrayListOrgName, arrayListOrgId;
    private String businessRelationName;

    private OnClickListener clickListener;

    public interface OnClickListener {
        void onClick(String orgId, String orgName, boolean isOrgVerified);
    }

    OrganizationRelationListAdapter(Context context, ArrayList<ProfileDataOperationOrganization>
            arrayListOrganization, ArrayList<String> arrayListOrgName, ArrayList<String> arrayListOrgId,
                                    OnClickListener clickListener, String businessRelationName) {
        this.context = context;
        this.clickListener = clickListener;
        this.arrayListOrganization = arrayListOrganization;
        this.arrayListOrgId = arrayListOrgId;
        this.arrayListOrgName = arrayListOrgName;
        this.businessRelationName = businessRelationName;
    }

    OrganizationRelationListAdapter(Context context, ArrayList<IndividualRelationType>
            arrayListRelation, OnClickListener clickListener, String businessRelationName) {
        this.context = context;
        this.clickListener = clickListener;
        this.arrayListRelation = arrayListRelation;
        this.businessRelationName = businessRelationName;
    }

    @Override
    public OrganizationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_organization_list_relation, parent, false);
        return new OrganizationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrganizationViewHolder holder, int position) {

        if (businessRelationName.equalsIgnoreCase("existing")) {

            IndividualRelationType individualRelationType = arrayListRelation.get(position);

            if (!StringUtils.isEmpty(individualRelationType.getRelationName())) {
                holder.textMain.setText(String.format("%s\n at %s", individualRelationType.getRelationName(),
                        individualRelationType.getOrganizationName()));
            }

            if (!StringUtils.isEmpty(individualRelationType.getFamilyName())) {
                holder.textMain.setText(individualRelationType.getFamilyName());
            }

            if (individualRelationType.getIsFriendRelation()) {
                holder.textMain.setText(context.getString(R.string.str_friend));
            }

            holder.checkbox.setChecked(individualRelationType.getIsSelected());

        } else {

            ProfileDataOperationOrganization organization = arrayListOrganization.get(position);

            holder.textMain.setText(organization.getOrgName());

            if (organization.getIsVerify() == 1)
                holder.textMain.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_relation_single_tick_green_svg, 0);

            holder.checkbox.setChecked(position == (AddNewRelationActivity.orgPosition));

            if (arrayListOrgName.size() > 0) {
                if (arrayListOrgName.contains(organization.getOrgName())
                        && arrayListOrgId.contains(organization.getOrgEntId())) {
                    holder.textMain.setEnabled(false);
                    holder.checkbox.setVisibility(View.GONE);
                } else {
                    holder.textMain.setEnabled(true);
                    holder.checkbox.setVisibility(View.VISIBLE);
                }
            }

            if (position == (AddNewRelationActivity.orgPosition)) {
                if (clickListener != null) {
                    clickListener.onClick(arrayListOrganization.get(AddNewRelationActivity.orgPosition)
                            .getOrgEntId(), arrayListOrganization.get(AddNewRelationActivity.orgPosition)
                            .getOrgName(), arrayListOrganization.get(AddNewRelationActivity.orgPosition)
                            .getIsVerify() == 1);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (businessRelationName.equalsIgnoreCase("existing"))
            return arrayListRelation.size();
        else
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
                    if (businessRelationName.equalsIgnoreCase("existing")) {
                        updateSelected(getAdapterPosition());
                    } else {
                        AddNewRelationActivity.orgPosition = getAdapterPosition();
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void updateSelected(int position) {

        IndividualRelationType relationType = arrayListRelation.get(position);
        if (relationType.getIsSelected()) {
            setData(relationType, position, false); // true
        } else {
            setData(relationType, position, true); // true
        }
    }

    private void setData(IndividualRelationType relationType, int position, boolean b) {

        IndividualRelationType individualRelationType = new IndividualRelationType();

        if (relationType.getRelationType() == 1) {

            individualRelationType.setId(String.valueOf(relationType.getId()));
            individualRelationType.setRelationId(String.valueOf(relationType.getRelationId()));
            individualRelationType.setRelationName("");
            individualRelationType.setOrganizationName("");
            individualRelationType.setFamilyName("");
            individualRelationType.setOrganizationId("");
            individualRelationType.setIsFriendRelation(true);
//            individualRelationType.setIsVerify("1");
            individualRelationType.setIsInUse(relationType.getIsInUse());
            individualRelationType.setRcStatus(relationType.getRcStatus());
            individualRelationType.setRelationType(relationType.getRelationType());
            individualRelationType.setIsSelected(b);

        } else if (relationType.getRelationType() == 2) {

            individualRelationType.setId(String.valueOf(relationType.getId()));
            individualRelationType.setRelationId(String.valueOf(relationType.getRelationId()));
            individualRelationType.setRelationName("");
            individualRelationType.setOrganizationName("");
            individualRelationType.setFamilyName(relationType.getFamilyName());
            individualRelationType.setOrganizationId("");
            individualRelationType.setIsFriendRelation(false);
//            individualRelationType.setIsVerify("1");
            individualRelationType.setIsInUse(relationType.getIsInUse());
            individualRelationType.setRcStatus(relationType.getRcStatus());
            individualRelationType.setRelationType(relationType.getRelationType());
            individualRelationType.setIsSelected(b);

        } else {

            individualRelationType.setId(String.valueOf(relationType.getId()));
            individualRelationType.setRelationId(String.valueOf(relationType.getRelationId()));
            individualRelationType.setRelationName(relationType.getRelationName());
            individualRelationType.setOrganizationName(relationType.getOrganizationName());
            individualRelationType.setFamilyName("");
            individualRelationType.setOrganizationId(String.valueOf(relationType.getOrganizationId()));
            individualRelationType.setIsFriendRelation(false);
//            individualRelationType.setIsVerify("1");
            individualRelationType.setIsInUse(relationType.getIsInUse());
            individualRelationType.setRcStatus(relationType.getRcStatus());
            individualRelationType.setRelationType(relationType.getRelationType());
            individualRelationType.setIsSelected(b);
        }

        arrayListRelation.set(position, individualRelationType); // true
    }
}
