package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hardik on 05/10/17.
 */

class ExistingRelationListAdapter extends RecyclerView.Adapter
        <ExistingRelationListAdapter.ExistingRelationViewHolder> implements Filterable {

    private ArrayList<RelationRecommendationType> arrayListRelationType;
    private Activity mActivity;
    private ArrayList<RelationRecommendationType> filteredList;
    private Activity activity;
    private CustomFilter mFilter;
    private OnClickListener clickListener;

    public interface OnClickListener {
        void onClick(int position);

        void onDeleteClick(int position, String name, String pmId);
    }

    @Override
    public ExistingRelationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_relation, parent, false);
        return new ExistingRelationViewHolder(v);
    }

    ExistingRelationListAdapter(Activity activity, ArrayList<RelationRecommendationType> list,
                                OnClickListener clickListener) {
        this.mActivity = activity;
        this.clickListener = clickListener;
        this.arrayListRelationType = list;
        this.filteredList = new ArrayList<>();
        this.filteredList.addAll(list);
        mFilter = new CustomFilter(ExistingRelationListAdapter.this);
    }

    @Override
    public void onBindViewHolder(ExistingRelationViewHolder holder, final int position) {

        RelationRecommendationType relationRecommendationType = arrayListRelationType.get(position);
        holder.textName.setText(String.format("%s %s", relationRecommendationType.getFirstName(),
                relationRecommendationType.getLastName()));
        holder.textNumber.setText(relationRecommendationType.getNumber());
//        holder.textDateAndTime.setText(relationRecommendationType.getDateAndTime());

        ArrayList<IndividualRelationType> list = relationRecommendationType.getIndividualRelationTypeList();
        if (list.size() > 0) {
            IndividualExistingRelationListAdapter adapter = new IndividualExistingRelationListAdapter(mActivity, list);

            holder.recycleIndividualRelationList.setLayoutManager(new LinearLayoutManager(mActivity));
            holder.recycleIndividualRelationList.setAdapter(adapter);
        }

        holder.llRootRelation.setTag(position);
        holder.llRootRelation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clickListener != null)
                    clickListener.onClick((int) view.getTag());
            }
        });

        holder.imageViewDelete.setTag(position);
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = (int) view.getTag();
                String name =  arrayListRelationType.get(pos).getFirstName() + " " +
                        arrayListRelationType.get(pos).getLastName();

                if (clickListener != null)
                    clickListener.onDeleteClick(pos,name,arrayListRelationType.get(pos).getPmId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListRelationType.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class CustomFilter extends Filter {
        private ExistingRelationListAdapter mAdapter;

        private CustomFilter(ExistingRelationListAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            arrayListRelationType.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                arrayListRelationType.addAll(filteredList);
            } else {

                String finalString = constraint.toString().toLowerCase().trim();

                if (finalString.contains(" ")) {

                    String[] splitString = finalString.split("\\s+");

                    if (splitString.length == 2) {

                        for (final RelationRecommendationType recommendationType : filteredList) {
                            if ((recommendationType.getFirstName().toLowerCase().startsWith(splitString[0])
                                    || recommendationType.getLastName().toLowerCase().startsWith(splitString[0]))
                                    && (recommendationType.getFirstName().toLowerCase().startsWith(splitString[1])
                                    || recommendationType.getLastName().toLowerCase().startsWith(splitString[1]))) {
                                arrayListRelationType.add(recommendationType);
                            }
                        }

                    } else {

                        for (final RelationRecommendationType recommendationType : filteredList) {
                            if (recommendationType.getFirstName().toLowerCase().contains(splitString[0])
                                    || recommendationType.getLastName().toLowerCase().contains(splitString[0])
                                    || recommendationType.getFirstName().toLowerCase().contains(splitString[1])
                                    || recommendationType.getLastName().toLowerCase().contains(splitString[1])) {
                                arrayListRelationType.add(recommendationType);
                            }
                        }
                    }

                } else {

                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final RelationRecommendationType recommendationType : filteredList) {
                        if (recommendationType.getFirstName().toLowerCase().contains(filterPattern)
                                || recommendationType.getLastName().toLowerCase().contains(filterPattern)
                                || recommendationType.getNumber().toLowerCase().contains(filterPattern)) {
                            arrayListRelationType.add(recommendationType);
                        }
                    }
                }
            }
            System.out.println("RContacts Count Number " + arrayListRelationType.size());
            results.values = arrayListRelationType;
            results.count = arrayListRelationType.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
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
        @BindView(R.id.ll_root_relation)
        LinearLayout llRootRelation;
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
