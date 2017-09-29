package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.OrganizationData;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class OrganizationSearchListAdapter extends RecyclerView.Adapter<OrganizationSearchListAdapter
        .OrganizationViewHolder> implements Filterable {

    private Context context;
    private ArrayList<OrganizationData> arrayListOrganization;
    private ArrayList<OrganizationData> filteredList;
    private OnClickListener clickListener;

    //    private ArrayList<OrganizationData> filteredList;
    private CustomFilter mFilter;

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public interface OnClickListener {
        void onClick(String organizationName);
    }

    public OrganizationSearchListAdapter(Context context, ArrayList<OrganizationData>
            arrayListOrganization, OnClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        this.arrayListOrganization = arrayListOrganization;
        this.filteredList = new ArrayList<>();
        this.filteredList.addAll(arrayListOrganization);
        mFilter = new CustomFilter(OrganizationSearchListAdapter.this);
    }

    @Override
    public OrganizationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_organization_list, parent, false);
        return new OrganizationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrganizationViewHolder holder, int position) {

        OrganizationData organization = arrayListOrganization.get(position);

        holder.textOrganizationName.setText(organization.getOmOrganizationCompany());
        holder.textOrganizationDetails.setText(organization.getOmOrganizationDesignation());

        Glide.with(context)
                .load(organization.getOmOrganizationProfileImage())
                .placeholder(R.drawable.home_screen_profile)
                .error(R.drawable.home_screen_profile)
                .bitmapTransform(new CropCircleTransformation(context))
                .override(512, 512)
                .into(holder.organizationProfileImage);

        holder.linearRootItemOrganization.setTag(position);
        holder.linearRootItemOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = (int) view.getTag();

                if (clickListener != null)
                    clickListener.onClick(arrayListOrganization.get(pos).getOmOrganizationCompany());
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayListOrganization.size();
    }

    class OrganizationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.organization_profile_image)
        ImageView organizationProfileImage;
        @BindView(R.id.text_organization_name)
        TextView textOrganizationName;
        @BindView(R.id.text_organization_details)
        TextView textOrganizationDetails;
        @BindView(R.id.linear_root_item_organization)
        LinearLayout linearRootItemOrganization;

        OrganizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textOrganizationName.setTypeface(Utils.typefaceRegular(context));
            textOrganizationDetails.setTypeface(Utils.typefaceRegular(context));
        }
    }

    private class CustomFilter extends Filter {
        private OrganizationSearchListAdapter mAdapter;

        private CustomFilter(OrganizationSearchListAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            arrayListOrganization.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                arrayListOrganization.addAll(filteredList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final OrganizationData mWords : filteredList) {
                    if (mWords.getOmOrganizationCompany().toLowerCase().startsWith(filterPattern)
                            || mWords.getOmOrganizationDesignation().toLowerCase().startsWith(filterPattern)) {
                        arrayListOrganization.add(mWords);
                    }
                }
            }
//            System.out.println("RContacts Count Number " + arrayListOrganization.size());
            results.values = arrayListOrganization;
            results.count = arrayListOrganization.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            System.out.println("RContacts Count Number 2 " + ((List<OrganizationData>) results.values).size());
            this.mAdapter.notifyDataSetChanged();
        }
    }
}