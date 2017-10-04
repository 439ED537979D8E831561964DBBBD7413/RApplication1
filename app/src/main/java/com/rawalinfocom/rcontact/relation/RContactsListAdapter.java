package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Monal on 07/12/16.
 */

public class RContactsListAdapter extends RecyclerView.Adapter<RContactsListAdapter.RContactsViewHolder> implements
        SectionIndexer, Filterable {

    private final ArrayList<UserProfile> filteredList;
    private Activity activity;
    private ArrayList<UserProfile> arrayListUserProfile;
    private CustomFilter mFilter;

    private ArrayList<Integer> mSectionPositions;
    private int searchCount;

    //<editor-fold desc="Constructor">
    RContactsListAdapter(Activity activity, ArrayList<UserProfile> arrayListUserProfile) {
        this.activity = activity;
        this.arrayListUserProfile = arrayListUserProfile;
        this.filteredList = new ArrayList<>();
        this.filteredList.addAll(arrayListUserProfile);
        mFilter = new CustomFilter(RContactsListAdapter.this);
    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">
    @Override
    public RContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_all_contacts, parent, false);
        return new RContactsViewHolder(v2);
    }

    @Override
    public void onBindViewHolder(RContactsViewHolder holder, int position) {

        final UserProfile userProfile = arrayListUserProfile.get(position);

        String contactDisplayName = userProfile.getPmFirstName() + " " + userProfile
                .getPmLastName();
        holder.textContactName.setText(contactDisplayName);
        if (StringUtils.length(userProfile.getMobileNumber()) > 0) {
            holder.textContactNumber.setText(userProfile.getMobileNumber());
        } else if (StringUtils.length(userProfile.getEmailId()) > 0) {
            holder.textContactNumber.setText(userProfile.getEmailId());
        }

        if (StringUtils.length(userProfile.getPmProfileImage()) > 0) {
            Glide.with(activity)
                    .load(userProfile.getPmProfileImage())
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .override(500, 500)
                    .into(holder.imageProfile);

        } else {
            holder.imageProfile.setImageResource(R.drawable.home_screen_profile);
        }

        holder.textRatingUserCount.setText(userProfile.getTotalProfileRateUser());
        holder.ratingUser.setRating(Float.parseFloat(userProfile.getProfileRating()));

        holder.relativeRowAllContact.setTag(position);
        holder.relativeRowAllContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = (int) view.getTag();
                TextView textName = view.findViewById(R.id.text_contact_name);

                Intent intent = new Intent();
                intent.putExtra("pmId", arrayListUserProfile.get(pos).getPmId());
                intent.putExtra("contactName", textName.getText().toString());
                intent.putExtra("profileImage", arrayListUserProfile.get(pos).getPmProfileImage());
                intent.putExtra("isBack", "0");
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();//finishing activity
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListUserProfile.size();
    }

    /**
     * Section Indexer
     */

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        for (int i = 0, size = arrayListUserProfile.size(); i < size; i++) {
            if (arrayListUserProfile.get(i) != null) {
                String name = arrayListUserProfile.get(i).getPmFirstName();
                if (name == null) {
                    String section = "#";
                    if (!sections.contains(section)) {
                        sections.add(section);
                        mSectionPositions.add(i);
                    }
                } else {
                    String section = String.valueOf(name.charAt(0)).toUpperCase();
                    if (!sections.contains(section)) {
                        sections.add(section);
                        mSectionPositions.add(i);
                    }
                }
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class CustomFilter extends Filter {
        private RContactsListAdapter mAdapter;

        private CustomFilter(RContactsListAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            arrayListUserProfile.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                arrayListUserProfile.addAll(filteredList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final UserProfile userProfile : filteredList) {
                    if (userProfile.getPmFirstName().toLowerCase().startsWith(filterPattern)
                            || userProfile.getPmLastName().toLowerCase().startsWith(filterPattern)) {
                        arrayListUserProfile.add(userProfile);
                    }
                }
            }
//            System.out.println("RContacts Count Number " + arrayListUserProfile.size());
            results.values = arrayListUserProfile;
            results.count = arrayListUserProfile.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    //</editor-fold>

    //<editor-fold desc="View Holders">

    class RContactsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        /*@BindView(R.id.image_social_media)
        ImageView imageSocialMedia;*/
        @BindView(R.id.text_contact_name)
        TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.text_contact_number)
        public TextView textContactNumber;
        @BindView(R.id.img_user_rating)
        ImageView imgUserRating;
        @BindView(R.id.relative_row_all_contact)
        RelativeLayout relativeRowAllContact;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;

        @BindView(R.id.linear_rating)
        LinearLayout linearRating;

        RContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceSemiBold(activity));
            textCloudContactName.setTypeface(Utils.typefaceSemiBold(activity));
            textContactNumber.setTypeface(Utils.typefaceRegular(activity));

            textRatingUserCount.setTypeface(Utils.typefaceRegular(activity));

            textContactName.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            textContactNumber.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));

            textCloudContactName.setVisibility(View.GONE);
            imgUserRating.setVisibility(View.VISIBLE);

            Utils.setRatingColor(activity, ratingUser);
        }
    }

    //</editor-fold>
}
