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

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 04/10/17.
 */

class RelationRecommendationListAdapter extends RecyclerView.Adapter<
        RelationRecommendationListAdapter.RelationRecommendationViewHolder> implements Filterable {


    private ArrayList<RelationRecommendationType> arrayListRelationType;
    private ArrayList<RelationRecommendationType> filteredList;
    private Activity activity;
    private CustomFilter mFilter;
    private OnClickListener clickListener;

    public interface OnClickListener {
        void onClick(int position, String name, String pmId);

        void onDeleteClick(int position, String name, String pmId);
    }


    @Override
    public RelationRecommendationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_relation, parent, false);
        return new RelationRecommendationViewHolder(v);
    }

    RelationRecommendationListAdapter(Activity activity, ArrayList<RelationRecommendationType> list,
                                      OnClickListener clickListener) {
        this.arrayListRelationType = list;
        this.activity = activity;
        this.clickListener = clickListener;
        this.filteredList = new ArrayList<>();
        this.filteredList.addAll(list);
        mFilter = new CustomFilter(RelationRecommendationListAdapter.this);

    }

    @Override
    public void onBindViewHolder(RelationRecommendationViewHolder viewHolder, final int position) {
        RelationRecommendationType relationRecommendationType = arrayListRelationType.get(position);
        viewHolder.textName.setText(String.format("%s %s", relationRecommendationType.getFirstName(),
                relationRecommendationType.getLastName()));
        viewHolder.textNumber.setText(relationRecommendationType.getNumber());
        viewHolder.textDateAndTime.setText(relationRecommendationType.getDateAndTime());

        Glide.with(activity)
                .load(relationRecommendationType.getProfileImage())
                .placeholder(R.drawable.home_screen_profile)
                .error(R.drawable.home_screen_profile)
                .bitmapTransform(new CropCircleTransformation(activity))
                .override(512, 512)
                .into(viewHolder.imageProfile);

        ArrayList<IndividualRelationType> list = relationRecommendationType.getIndividualRelationTypeList();
        if (list.size() > 0) {

            IndividualRelationRecommendationListAdapter adapter = new IndividualRelationRecommendationListAdapter(
                    activity, list, "recommendation",
                    new IndividualRelationRecommendationListAdapter.OnClickListener() {
                        @Override
                        public void onClick(int innerPosition) {
                            updateSelected(position, innerPosition);
                        }
                    });

            viewHolder.recycleIndividualRelationList.setLayoutManager(new LinearLayoutManager(activity));
            viewHolder.recycleIndividualRelationList.setAdapter(adapter);
        }

        viewHolder.imageViewCorrect.setTag(position);
        viewHolder.imageViewDelete.setTag(position);

        viewHolder.imageViewCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = (int) view.getTag();
                String name = arrayListRelationType.get(pos).getFirstName() + " " +
                        arrayListRelationType.get(pos).getLastName();

                if (clickListener != null)
                    clickListener.onClick(pos, name, arrayListRelationType.get(pos).getPmId());
            }
        });

        viewHolder.imageViewDelete.setTag(position);
        viewHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = (int) view.getTag();
                String name = arrayListRelationType.get(pos).getFirstName() + " " +
                        arrayListRelationType.get(pos).getLastName();

                if (clickListener != null)
                    clickListener.onDeleteClick(pos, name, arrayListRelationType.get(pos).getPmId());
            }
        });
    }

    private void updateSelected(int position, int innerPosition) {

        IndividualRelationType relationType = arrayListRelationType.get(position)
                .getIndividualRelationTypeList().get(innerPosition);
        if (relationType.getIsSelected()) {
            setData(relationType, position, innerPosition, false); // true
        } else {
            setData(relationType, position, innerPosition, true); // true
        }

        notifyItemChanged(position);

//        notifyDataSetChanged();
    }

    private void setData(IndividualRelationType relationType, int position, int innerPosition, boolean b) {

        IndividualRelationType individualRelationType = new IndividualRelationType();

        if (relationType.getRelationType() == 1) {

            individualRelationType.setId(String.valueOf(relationType.getId()));
            individualRelationType.setRelationId(String.valueOf(relationType.getRelationId()));
            individualRelationType.setRelationName("");
            individualRelationType.setOrganizationName("");
            individualRelationType.setFamilyName("");
            individualRelationType.setOrganizationId("");
            individualRelationType.setIsFriendRelation(true);
            individualRelationType.setIsVerify("1");
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
            individualRelationType.setIsVerify("1");
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
            individualRelationType.setIsVerify("1");
            individualRelationType.setRcStatus(relationType.getRcStatus());
            individualRelationType.setRelationType(relationType.getRelationType());
            individualRelationType.setIsSelected(b);
        }

        arrayListRelationType.get(position).getIndividualRelationTypeList().set(innerPosition,
                individualRelationType); // true
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
        private RelationRecommendationListAdapter mAdapter;

        private CustomFilter(RelationRecommendationListAdapter mAdapter) {
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

                            for (int i = 0; i < splitString.length; i++) {

                                if ((recommendationType.getFirstName().toLowerCase().contains(splitString[i])
                                        || recommendationType.getLastName().toLowerCase().contains(splitString[i]))
                                        && (recommendationType.getFirstName().toLowerCase().contains(splitString[i + 1])
                                        || recommendationType.getLastName().toLowerCase().contains(splitString[i + 1]))) {
                                    arrayListRelationType.add(recommendationType);
                                }
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

    class RelationRecommendationViewHolder extends RecyclerView.ViewHolder {

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

        RelationRecommendationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
