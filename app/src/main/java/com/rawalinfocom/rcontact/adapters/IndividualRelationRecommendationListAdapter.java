package com.rawalinfocom.rcontact.adapters;

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

public class IndividualRelationRecommendationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity mActivity;
    private ArrayList<IndividualRelationRecommendationType> individualRelationRecommendationTypeList;

    @Override
    public IndividualRelationRecommendationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .list_item_individual_relation,
                parent, false);
        return new IndividualRelationRecommendationViewHolder(v);
    }

    public IndividualRelationRecommendationListAdapter(Activity mActivity,
                                                       ArrayList<IndividualRelationRecommendationType> individualRelationRecommendationTypeList) {
        this.mActivity = mActivity;
        this.individualRelationRecommendationTypeList = individualRelationRecommendationTypeList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IndividualRelationRecommendationViewHolder viewHolder = (IndividualRelationRecommendationViewHolder) holder;
        IndividualRelationRecommendationType type = individualRelationRecommendationTypeList.get(position);
        int relationType =  type.getRelationType();
        if(relationType==0){
            viewHolder.textRelationName.setText(type.getRelationName());
        }else if(relationType == 1){
            viewHolder.textRelationName.setText(type.getRelationName());
        }else if(relationType == 2){
            viewHolder.textRelationName.setText(type.getRelationName());
            viewHolder.llRelationOrganization.setVisibility(View.VISIBLE);
            viewHolder.textOrganizationName.setText(type.getOrganization());
        }
    }

    @Override
    public int getItemCount() {
        return individualRelationRecommendationTypeList.size();
    }

    public class IndividualRelationRecommendationViewHolder extends RecyclerView.ViewHolder {

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
        @BindView(R.id.ll_contain)
        RelativeLayout llContain;
        @BindView(R.id.ll_relation_organization)
        LinearLayout llRelationOrganization;
        @BindView(R.id.text_relation_name)
        TextView textRelationName;

        public IndividualRelationRecommendationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            llRelationOrganization.setVisibility(View.GONE);

        }
    }
}
