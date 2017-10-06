package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.model.IndividualRelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hardik on 05/10/17.
 */

class ExistingRelationListAdapter extends RecyclerView.Adapter
        <ExistingRelationListAdapter.ExistingRelationViewHolder> {

    private ArrayList<RelationRecommendationType> arrayListRelationType;
    private Activity mActivity;

    @Override
    public ExistingRelationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_relation, parent, false);
        return new ExistingRelationViewHolder(v);
    }

    ExistingRelationListAdapter(Activity activity, ArrayList<RelationRecommendationType> list) {
        this.arrayListRelationType = list;
        this.mActivity = activity;
    }

    @Override
    public void onBindViewHolder(ExistingRelationViewHolder holder, int position) {

        RelationRecommendationType relationRecommendationType = arrayListRelationType.get(position);
        holder.textName.setText(relationRecommendationType.getFirstName() + " " +
                relationRecommendationType.getLastName());
        holder.textNumber.setText(relationRecommendationType.getNumber());
//        viewHolder.textDateAndTime.setText(relationRecommendationType.getDateAndTime());

        ArrayList<IndividualRelationRecommendationType> list = relationRecommendationType.
                getIndividualRelationRecommendationTypeArrayList();
        if (list.size() > 0) {
            IndividualExistingRelationListAdapter adapter = new IndividualExistingRelationListAdapter(mActivity, list);

            holder.recycleIndividualRelationList.setLayoutManager(new LinearLayoutManager(mActivity));
            holder.recycleIndividualRelationList.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return arrayListRelationType.size();
    }

    class ExistingRelationViewHolder extends RecyclerView.ViewHolder {

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

        ExistingRelationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imageViewDelete.setVisibility(View.VISIBLE);
            imageViewCorrect.setVisibility(View.GONE);
            textDateAndTime.setVisibility(View.GONE);
        }
    }
}
