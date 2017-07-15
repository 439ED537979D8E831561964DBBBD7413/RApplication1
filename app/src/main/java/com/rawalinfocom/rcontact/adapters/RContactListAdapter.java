package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 07/12/16.
 */

public class RContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        SectionIndexer {

    private Activity activity;
    private Fragment fragment;
    private ArrayList<Object> arrayListUserProfile;
    private ArrayList<String> arrayListContactHeader;

    private final int HEADER = 0, CONTACT = 1, FOOTER = 2;

    private int previousPosition = 0;

    private ArrayList<Integer> mSectionPositions;

    //<editor-fold desc="Constructor">
    public RContactListAdapter(Fragment fragment, ArrayList<Object> arrayListUserProfile,
                               ArrayList<String> arrayListContactHeader) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.arrayListUserProfile = arrayListUserProfile;
        this.arrayListContactHeader = arrayListContactHeader;
    }

    public void updateList(int pos, ArrayList<Object> arrayListUserProfile) {
        this.arrayListUserProfile = arrayListUserProfile;
        notifyItemChanged(pos);
    }
    //</editor-fold>

    //<editor-fold desc="Override Methods">

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case HEADER:
                View v1 = inflater.inflate(R.layout.list_item_header_contact, parent, false);
                viewHolder = new ContactHeaderViewHolder(v1);
                break;
            case CONTACT:
                View v2 = inflater.inflate(R.layout.list_item_all_contacts, parent, false);
                viewHolder = new RContactViewHolder(v2);
                break;
            case FOOTER:
                View v3 = inflater.inflate(R.layout.footer_all_contacts, parent, false);
                viewHolder = new ContactFooterViewHolder(v3);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case CONTACT:
                RContactViewHolder contactViewHolder = (RContactViewHolder) holder;
                configureRContactViewHolder(contactViewHolder, position);
                break;
            case FOOTER:
                ContactFooterViewHolder contactFooterViewHolder = (ContactFooterViewHolder) holder;
                configureFooterViewHolder(contactFooterViewHolder, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == arrayListUserProfile.size()) {
            return FOOTER;
        } else if (arrayListUserProfile.get(position) instanceof UserProfile) {
            return CONTACT;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return (arrayListUserProfile.size() + 1);
    }

    /**
     * Section Indexer
     */

    @Override
    public Object[] getSections() {
//        return arrayListContactHeader.toArray(new String[arrayListContactHeader.size()]);
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        for (int i = 0, size = arrayListUserProfile.size(); i < size; i++) {
            if (arrayListUserProfile.get(i) instanceof UserProfile) {
                String name = ((UserProfile) arrayListUserProfile.get(i)).getPmFirstName();
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
        /*if (position >= arrayListUserProfile.size()) {
            position = arrayListUserProfile.size() - 1;
        }

        if (arrayListUserProfile.get(position) instanceof String) {
            String letter = (String) arrayListUserProfile.get(position);
            previousPosition = arrayListContactHeader.indexOf(letter);
        } else {
            for (int i = position; i >= 0; i--) {
                if (arrayListUserProfile.get(i) instanceof String) {
                    String letter = (String) arrayListUserProfile.get(i);
                    previousPosition = arrayListContactHeader.indexOf(letter);
                    break;
                }
            }
        }

        return previousPosition;*/
        return 0;
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void configureRContactViewHolder(final RContactViewHolder holder, int position) {

        final UserProfile userProfile = (UserProfile) arrayListUserProfile.get(position);

//        holder.relativeRowAllContact.setTag(position);

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
                Bundle bundle = new Bundle();
                bundle.putInt(AppConstants.EXTRA_RCONTACT_POSITION, (int) view.getTag());
                bundle.putString(AppConstants.EXTRA_PM_ID, userProfile.getPmId());
                bundle.putString(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE, userProfile
                        .getPmRawId());
                bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, "-1");
//                bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, userProfile.getPmRawId());
                TextView textName = (TextView) view.findViewById(R.id.text_contact_name);
                bundle.putString(AppConstants.EXTRA_CONTACT_NAME, textName.getText().toString());
                bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, userProfile
                        .getPmProfileImage());
                bundle.putString(AppConstants.EXTRA_CALL_HISTORY_NUMBER, userProfile.getMobileNumber());

               /* ((BaseActivity) activity).startActivityIntent(activity, ProfileDetailActivity
                        .class, bundle);*/
                bundle.putString(AppConstants.EXTRA_CALL_HISTORY_NUMBER, userProfile
                        .getMobileNumber());
                Intent intent = new Intent(activity, ProfileDetailActivity.class);
                intent.putExtras(bundle);
                fragment.startActivityForResult(intent, AppConstants
                        .REQUEST_CODE_PROFILE_DETAIL);
                ((BaseActivity) activity).overridePendingTransition(R.anim.enter, R
                        .anim.exit);
            }
        });
    }

    private void configureFooterViewHolder(ContactFooterViewHolder holder, int position) {
//        String letter = (String) arrayListUserContact.get(position);
        holder.textTotalContacts.setText(arrayListUserProfile.size() - arrayListContactHeader
                .size() + " " + activity.getString(R.string.contacts));
    }

    //</editor-fold>

    //<editor-fold desc="View Holders">

    public class RContactViewHolder extends RecyclerView.ViewHolder {

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

        RContactViewHolder(View itemView) {
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

    public class ContactHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_header)
        TextView textHeader;

        ContactHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textHeader.setTypeface(Utils.typefaceSemiBold(activity));
        }
    }

    public class ContactFooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_total_contacts)
        TextView textTotalContacts;

        ContactFooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textTotalContacts.setTypeface(Utils.typefaceSemiBold(activity));

        }
    }
    //</editor-fold>
}
