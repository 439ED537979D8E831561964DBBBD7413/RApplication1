package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 07/12/16.
 */

public class RContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        SectionIndexer {

    private Context context;
    private ArrayList<Object> arrayListUserProfile;
    private ArrayList<String> arrayListContactHeader;

    private final int HEADER = 0, CONTACT = 1, FOOTER = 2;

    private int previousPosition = 0;

    //<editor-fold desc="Constructor">
    public RContactListAdapter(Context context, ArrayList<Object> arrayListUserProfile,
                               ArrayList<String> arrayListContactHeader) {
        this.context = context;
        this.arrayListUserProfile = arrayListUserProfile;
        this.arrayListContactHeader = arrayListContactHeader;
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
            case HEADER:
                ContactHeaderViewHolder contactHeaderViewHolder = (ContactHeaderViewHolder) holder;
                configureHeaderViewHolder(contactHeaderViewHolder, position);
                break;
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
        } else if (arrayListUserProfile.get(position) instanceof String) {
            return HEADER;
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
        return arrayListContactHeader.toArray(new String[arrayListContactHeader.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= arrayListUserProfile.size()) {
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

        return previousPosition;
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

        holder.textRatingUserCount.setText(userProfile.getTotalProfileRateUser());
        holder.ratingUser.setRating(Float.parseFloat(userProfile.getProfileRating()));

          /* Hide Divider if row is last in Section */
        if ((position + 1) < arrayListUserProfile.size()) {
            if (arrayListUserProfile.get(position + 1) instanceof String) {
                holder.dividerAllContact.setVisibility(View.GONE);
            } else {
                holder.dividerAllContact.setVisibility(View.VISIBLE);
            }
        }

        holder.relativeRowAllContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_PM_ID, userProfile.getPmId());
                bundle.putString(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE, userProfile
                        .getPmRawId());
                bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, "-1");
//                bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, userProfile.getPmRawId());
                TextView textName = (TextView) view.findViewById(R.id.text_contact_name);
                bundle.putString(AppConstants.EXTRA_CONTACT_NAME, textName.getText().toString());
                ((BaseActivity) context).startActivityIntent(context, ProfileDetailActivity
                        .class, bundle);
            }
        });

    }

    private void configureHeaderViewHolder(ContactHeaderViewHolder holder, int position) {
        String letter = (String) arrayListUserProfile.get(position);
        holder.textHeader.setText(letter);
    }

    private void configureFooterViewHolder(ContactFooterViewHolder holder, int position) {
//        String letter = (String) arrayListUserContact.get(position);
        holder.textTotalContacts.setText(arrayListUserProfile.size() - arrayListContactHeader
                .size() + " Contacts");
    }

    //</editor-fold>

    //<editor-fold desc="View Holders">

    public class RContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.image_social_media)
        ImageView imageSocialMedia;
        @BindView(R.id.text_contact_name)
        TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.text_contact_number)
        public TextView textContactNumber;
        @BindView(R.id.divider_all_contact)
        View dividerAllContact;
        @BindView(R.id.relative_row_all_contact)
        RelativeLayout relativeRowAllContact;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;
        @BindView(R.id.button_invite)
        Button buttonInvite;
        @BindView(R.id.linear_rating)
        LinearLayout linearRating;

        RContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceSemiBold(context));
            textCloudContactName.setTypeface(Utils.typefaceSemiBold(context));
            textContactNumber.setTypeface(Utils.typefaceRegular(context));

            textRatingUserCount.setTypeface(Utils.typefaceRegular(context));

            textContactName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            textContactNumber.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

            textCloudContactName.setVisibility(View.GONE);

            buttonInvite.setVisibility(View.GONE);
            imageSocialMedia.setVisibility(View.GONE);
//            linearRating.setVisibility(View.GONE);

        }
    }

    public class ContactHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_header)
        TextView textHeader;

        ContactHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textHeader.setTypeface(Utils.typefaceSemiBold(context));

        }
    }

    public class ContactFooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_total_contacts)
        TextView textTotalContacts;

        ContactFooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textTotalContacts.setTypeface(Utils.typefaceSemiBold(context));

        }
    }

    //</editor-fold>

}
