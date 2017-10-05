package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.model.IndividualRelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 04/10/17.
 */

public class RelationRecommendationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<RelationRecommendationType> arrayListRelationType;
    Activity mActivity;
    @Override
    public RelationRecommendationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .list_item_relation,
                parent, false);
        return new RelationRecommendationViewHolder(v);
    }

    public RelationRecommendationListAdapter(Activity activity,ArrayList<RelationRecommendationType> list) {
        this.arrayListRelationType =  list;
        this.mActivity = activity;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            RelationRecommendationViewHolder viewHolder =  (RelationRecommendationViewHolder) holder;
            RelationRecommendationType relationRecommendationType =  arrayListRelationType.get(position);
            viewHolder.textName.setText(relationRecommendationType.getFirstName() + " " + relationRecommendationType.getLastName());
            viewHolder.textNumber.setText(relationRecommendationType.getNumber());
            viewHolder.textDateAndTime.setText(relationRecommendationType.getDateAndTime());

        ArrayList<IndividualRelationRecommendationType> list =  relationRecommendationType.getIndividualRelationRecommendationTypeArrayList();
        if(list.size()>0){
            IndividualRelationRecommendationListAdapter adapter = new IndividualRelationRecommendationListAdapter(mActivity,
                    list);

            viewHolder.recycleIndividualRelationList.setLayoutManager(new LinearLayoutManager(mActivity));
            viewHolder.recycleIndividualRelationList.setAdapter(adapter);
        }

    }

    @Override
    public int getItemCount() {
        return arrayListRelationType.size();
    }

    public class RelationRecommendationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.text_name)
        TextView textName;
        @BindView(R.id.text_number)
        TextView textNumber;
        @BindView(R.id.text_date_and_time)
        TextView textDateAndTime;
        @BindView(R.id.image_view_correct)
        ImageView imageViewCorrect;
        @BindView(R.id.image_view_delete)
        ImageView imageViewDelete;
        @BindView(R.id.ll_person_detail)
        LinearLayout llPersonDetail;
        @BindView(R.id.recycle_individual_relation_list)
        RecyclerView recycleIndividualRelationList;
        @BindView(R.id.rl_relation_list)
        RelativeLayout rlRelationList;

        public RelationRecommendationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
