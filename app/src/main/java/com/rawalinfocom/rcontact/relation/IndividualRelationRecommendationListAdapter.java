package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.model.IndividualRelationRecommendationType;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 04/10/17.
 */

class IndividualRelationRecommendationListAdapter extends RecyclerView.Adapter
        <IndividualRelationRecommendationListAdapter.IndividualRelationRecommendationViewHolder> {

    private Activity mActivity;
    private ArrayList<IndividualRelationRecommendationType> individualRelationRecommendationTypeList;

    @Override
    public IndividualRelationRecommendationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_individual_relation, parent, false);
        return new IndividualRelationRecommendationViewHolder(v);
    }

    IndividualRelationRecommendationListAdapter(Activity mActivity,
                                                ArrayList<IndividualRelationRecommendationType> individualRelationRecommendationTypeList) {
        this.mActivity = mActivity;
        this.individualRelationRecommendationTypeList = individualRelationRecommendationTypeList;
    }

    @Override
    public void onBindViewHolder(IndividualRelationRecommendationViewHolder holder, int position) {
        IndividualRelationRecommendationType type = individualRelationRecommendationTypeList.get(position);

        System.out.println("RContacts type relation --> " + type.getRelationName());
        System.out.println("RContacts type organization --> " + type.getOrganizationName());
        System.out.println("RContacts type family --> " + type.getFamilyName());
        System.out.println("RContacts type friend --> " + type.getIsFriendRelation());

        if (!StringUtils.isEmpty(type.getRelationName())) {
            holder.textRelationName.setText(type.getRelationName());
            holder.textRelationName.setVisibility(View.VISIBLE);
            holder.llRelationOrganization.setVisibility(View.VISIBLE);
            holder.checkboxRelation.setVisibility(View.VISIBLE);
            holder.textOrganizationName.setText(type.getOrganizationName());
        } else if (!StringUtils.isEmpty(type.getFamilyName())) {
            holder.textRelationName.setText(type.getFamilyName());
            holder.textRelationName.setVisibility(View.VISIBLE);
            holder.checkboxRelation.setVisibility(View.VISIBLE);
        } else if (type.getIsFriendRelation()) {
            holder.textRelationName.setText(mActivity.getString(R.string.str_friend));
            holder.textRelationName.setVisibility(View.VISIBLE);
            holder.checkboxRelation.setVisibility(View.VISIBLE);
        } else {
            viewVisibility(holder);
        }
    }

    private void viewVisibility(IndividualRelationRecommendationViewHolder viewHolder) {
        viewHolder.textRelationName.setVisibility(View.GONE);
        viewHolder.llRelationOrganization.setVisibility(View.GONE);
        viewHolder.checkboxRelation.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return individualRelationRecommendationTypeList.size();
    }

    class IndividualRelationRecommendationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_relation)
        ImageView imageRelation;
        @BindView(R.id.text_at)
        TextView textAt;
        @BindView(R.id.text_organization_name)
        TextView textOrganizationName;
        @BindView(R.id.ll_relation_name)
        LinearLayout llRelationName;
        @BindView(R.id.checkbox_relation)
        CheckBox checkboxRelation;
        @BindView(R.id.ll_relation_organization)
        LinearLayout llRelationOrganization;
        @BindView(R.id.text_relation_name)
        TextView textRelationName;

        IndividualRelationRecommendationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textRelationName.setVisibility(View.GONE);
            llRelationOrganization.setVisibility(View.GONE);
            checkboxRelation.setVisibility(View.GONE);

        }
    }
}
