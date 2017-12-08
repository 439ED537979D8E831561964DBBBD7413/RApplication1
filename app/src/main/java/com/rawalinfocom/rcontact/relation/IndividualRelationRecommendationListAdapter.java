//package com.rawalinfocom.rcontact.relation;
//
//import android.app.Activity;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.rawalinfocom.rcontact.R;
//import com.rawalinfocom.rcontact.model.IndividualRelationType;
//
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.ArrayList;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * Created by Aniruddh on 04/10/17.
// */
//
//class IndividualRelationRecommendationListAdapter extends RecyclerView.Adapter
//        <IndividualRelationRecommendationListAdapter.IndividualRelationRecommendationViewHolder> {
//
//    private Activity mActivity;
//    private ArrayList<IndividualRelationType> individualRelationRecommendationTypeList;
//    private String from;
//
//    private OnClickListener clickListener;
//
//    public interface OnClickListener {
//        void onClick(int innerPosition);
//    }
//
//    @Override
//    public IndividualRelationRecommendationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_individual_relation, parent, false);
//        return new IndividualRelationRecommendationViewHolder(v);
//    }
//
//    IndividualRelationRecommendationListAdapter(Activity mActivity, ArrayList<IndividualRelationType>
//            individualRelationRecommendationTypeList, String from, OnClickListener clickListener) {
//        this.mActivity = mActivity;
//        this.from = from;
//        this.clickListener = clickListener;
//        this.individualRelationRecommendationTypeList = individualRelationRecommendationTypeList;
//    }
//
//    @Override
//    public void onBindViewHolder(final IndividualRelationRecommendationViewHolder holder, int position) {
//
//        IndividualRelationType type = individualRelationRecommendationTypeList.get(position);
//
//        holder.llBusinessRelation.setVisibility(View.VISIBLE);
//
//        if (!StringUtils.isEmpty(type.getRelationName())) {
//
//            holder.llRelationOrganization.setVisibility(View.VISIBLE);
//
//            holder.textRelationName.setText(type.getRelationName());
//            holder.textOrganizationName.setText(type.getOrganizationName());
//            holder.imageRelation.setImageResource(R.drawable.ico_relation_business_svg);
//
//            if (type.getRcStatus() == 2)
//                holder.textRelationName.setCompoundDrawablesWithIntrinsicBounds
//                        (0, 0, R.drawable.ico_relation_double_tick_svg, 0);
//            else
//                holder.textRelationName.setCompoundDrawablesWithIntrinsicBounds
//                        (0, 0, R.drawable.ico_relation_single_tick_svg, 0);
//        }
//
//        if (!StringUtils.isEmpty(type.getFamilyName())) {
//
//            holder.llRelationOrganization.setVisibility(View.GONE);
//
//            holder.textRelationName.setText(type.getFamilyName());
//            holder.imageRelation.setImageResource(R.drawable.ico_realtion_family_svg);
//
//            if (type.getRcStatus() == 2)
//                holder.textRelationName.setCompoundDrawablesWithIntrinsicBounds
//                        (0, 0, R.drawable.ico_relation_double_tick_svg, 0);
//            else
//                holder.textRelationName.setCompoundDrawablesWithIntrinsicBounds
//                        (0, 0, R.drawable.ico_relation_single_tick_svg, 0);
//        }
//
//        if (type.getIsFriendRelation()) {
//
//            holder.llRelationOrganization.setVisibility(View.GONE);
//
//            holder.textRelationName.setText(mActivity.getString(R.string.str_friend));
//            holder.imageRelation.setImageResource(R.drawable.ico_relation_friend_svg);
//
//            if (type.getRcStatus() == 2)
//                holder.textRelationName.setCompoundDrawablesWithIntrinsicBounds
//                        (0, 0, R.drawable.ico_relation_double_tick_svg, 0);
//            else
//                holder.textRelationName.setCompoundDrawablesWithIntrinsicBounds
//                        (0, 0, R.drawable.ico_relation_single_tick_svg, 0);
//        }
//
//        holder.checkboxRelation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//                if (b) {
//                    if (clickListener != null)
//                        clickListener.onClick(holder.getAdapterPosition());
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return individualRelationRecommendationTypeList.size();
//    }
//
//    class IndividualRelationRecommendationViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.image_relation)
//        ImageView imageRelation;
//        @BindView(R.id.text_relation_name)
//        TextView textRelationName;
//        @BindView(R.id.text_at)
//        TextView textAt;
//        @BindView(R.id.text_organization_name)
//        TextView textOrganizationName;
//        @BindView(R.id.ll_relation_organization)
//        LinearLayout llRelationOrganization;
//        @BindView(R.id.ll_relation_name)
//        LinearLayout llRelationName;
//        @BindView(R.id.checkbox_relation)
//        CheckBox checkboxRelation;
//        @BindView(R.id.ll_business_relation)
//        RelativeLayout llBusinessRelation;
//        @BindView(R.id.ll_relation)
//        LinearLayout llRelation;
//        @BindView(R.id.rl_root_relation)
//        FrameLayout rlRootRelation;
//
//        IndividualRelationRecommendationViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//
//            // business
//            llRelationOrganization.setVisibility(View.GONE);
//            llBusinessRelation.setVisibility(View.GONE);
//
//            // family
////            llFamilyRelation.setVisibility(View.GONE);
//
//            // friend
////            llFriendRelation.setVisibility(View.GONE);
//
////            llRelation.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////
////                    checkboxRelation.performClick();
////
//////                    if (clickListener != null)
//////                        clickListener.onClick(getAdapterPosition());
////                }
////            });
//        }
//    }
//}
