package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.model.IndividualRelationType;

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
    private ArrayList<IndividualRelationType> individualRelationRecommendationTypeList;
    private String from;

    @Override
    public IndividualRelationRecommendationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_individual_relation, parent, false);
        return new IndividualRelationRecommendationViewHolder(v);
    }

    IndividualRelationRecommendationListAdapter(Activity mActivity, ArrayList<IndividualRelationType>
            individualRelationRecommendationTypeList, String from) {
        this.mActivity = mActivity;
        this.from = from;
        this.individualRelationRecommendationTypeList = individualRelationRecommendationTypeList;
    }

    @Override
    public void onBindViewHolder(IndividualRelationRecommendationViewHolder holder, int position) {

        IndividualRelationType type = individualRelationRecommendationTypeList.get(position);

        if (from.equalsIgnoreCase("rcp")) {
            holder.checkboxRelationFriend.setVisibility(View.GONE);
            holder.checkboxRelationFamily.setVisibility(View.GONE);
            holder.checkboxRelation.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(type.getRelationName())) {

            holder.llBusinessRelation.setVisibility(View.VISIBLE);
            holder.llRelationOrganization.setVisibility(View.VISIBLE);

            holder.textRelationName.setText(type.getRelationName());
            holder.textOrganizationName.setText(type.getOrganizationName());
            holder.imageRelation.setImageResource(R.drawable.ico_relation_business_svg);
            holder.textRelationName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_relation_double_tick_svg, 0);

        } else {
            holder.llBusinessRelation.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(type.getFamilyName())) {

            holder.llFamilyRelation.setVisibility(View.VISIBLE);

            holder.textRelationFamily.setText(type.getFamilyName());
            holder.imageRelationFamily.setImageResource(R.drawable.ico_realtion_family_svg);
            holder.textRelationFamily.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_relation_single_tick_svg, 0);

        } else {
            holder.llFamilyRelation.setVisibility(View.GONE);
        }

        if (type.getIsFriendRelation()) {

            holder.llFriendRelation.setVisibility(View.VISIBLE);

            holder.textRelationFriend.setText(mActivity.getString(R.string.str_friend));
            holder.imageRelationFriend.setImageResource(R.drawable.ico_relation_friend_svg);
            holder.textRelationFriend.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_relation_single_tick_svg, 0);

        } else {
            holder.llFriendRelation.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return individualRelationRecommendationTypeList.size();
    }

    class IndividualRelationRecommendationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_relation)
        ImageView imageRelation;
        @BindView(R.id.text_relation_name)
        TextView textRelationName;
        @BindView(R.id.text_at)
        TextView textAt;
        @BindView(R.id.text_organization_name)
        TextView textOrganizationName;
        @BindView(R.id.ll_relation_organization)
        LinearLayout llRelationOrganization;
        @BindView(R.id.ll_relation_name)
        LinearLayout llRelationName;
        @BindView(R.id.checkbox_relation)
        CheckBox checkboxRelation;
        @BindView(R.id.ll_business_relation)
        LinearLayout llBusinessRelation;
        @BindView(R.id.image_relation_family)
        ImageView imageRelationFamily;
        @BindView(R.id.text_relation_family)
        TextView textRelationFamily;
        @BindView(R.id.checkbox_relation_family)
        CheckBox checkboxRelationFamily;
        @BindView(R.id.ll_family_relation)
        LinearLayout llFamilyRelation;
        @BindView(R.id.image_relation_friend)
        ImageView imageRelationFriend;
        @BindView(R.id.text_relation_friend)
        TextView textRelationFriend;
        @BindView(R.id.checkbox_relation_friend)
        CheckBox checkboxRelationFriend;
        @BindView(R.id.ll_friend_relation)
        LinearLayout llFriendRelation;
        @BindView(R.id.rl_root_relation)
        LinearLayout rlRootRelation;

        IndividualRelationRecommendationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // business
            llRelationOrganization.setVisibility(View.GONE);
            llBusinessRelation.setVisibility(View.GONE);

            // family
            llFamilyRelation.setVisibility(View.GONE);

            // friend
            llFriendRelation.setVisibility(View.GONE);
        }
    }
}
