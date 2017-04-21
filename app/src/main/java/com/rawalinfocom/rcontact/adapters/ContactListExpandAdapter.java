package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
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
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 10/04/17.
 */

public class ContactListExpandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ProfileData> arrayListUserContact;
    private int previousPosition = 0;

    private int colorBlack, colorPineGreen;

    public ContactListExpandAdapter(Context context, ArrayList<ProfileData> arrayListUserContact) {
        this.context = context;
        this.arrayListUserContact = arrayListUserContact;
        colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v2 = inflater.inflate(R.layout.list_item_all_contacts, parent, false);
        viewHolder = new AllContactViewHolder(v2);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AllContactViewHolder contactViewHolder = (AllContactViewHolder) holder;
        configureAllContactViewHolder(contactViewHolder, position);
    }

    private void configureAllContactViewHolder(final AllContactViewHolder holder, int position) {

        final ProfileData contact = arrayListUserContact.get(position);
        holder.textContactName.setText(contact.getTempFirstName());
        holder.textContactNumber.setText(contact.getTempNumber());

        holder.textCloudContactName.setVisibility(View.VISIBLE);
        holder.textCloudContactName.setText(contact.getTempRcpName());
       /* if (StringUtils.contains(contact.getRcpName(), ",")) {
            holder.textRcpName.setText(String.valueOf(StringUtils.countMatches(contact
                    .getRcpName(), ",") + 1));
        }*/

    }

    @Override
    public int getItemCount() {
        return arrayListUserContact.size();
    }

    public class AllContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.image_social_media)
        ImageView imageSocialMedia;
        @BindView(R.id.text_contact_name)
        TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;
        @BindView(R.id.text_contact_number)
        public TextView textContactNumber;
        @BindView(R.id.divider_all_contact)
        View dividerAllContact;
        @BindView(R.id.relative_row_all_contact)
        RelativeLayout relativeRowAllContact;
        @BindView(R.id.linear_rating)
        LinearLayout linearRating;
        @BindView(R.id.recycler_view_multiple_rc)
        RecyclerView recyclerViewMultipleRc;
        @BindView(R.id.button_invite)
        Button buttonInvite;

        AllContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceSemiBold(context));
            textCloudContactName.setTypeface(Utils.typefaceSemiBold(context));
            textContactNumber.setTypeface(Utils.typefaceRegular(context));
            textRatingUserCount.setTypeface(Utils.typefaceRegular(context));

            textContactName.setTextColor(colorBlack);
            textContactNumber.setTextColor(colorBlack);

            textCloudContactName.setTextColor(colorPineGreen);
            textContactNumber.setTextColor(colorPineGreen);

            recyclerViewMultipleRc.setVisibility(View.GONE);
            linearRating.setVisibility(View.GONE);
            buttonInvite.setVisibility(View.GONE);
            textContactName.setVisibility(View.GONE);

          /*  LayerDrawable stars = (LayerDrawable) ratingUser.getProgressDrawable();
            // Filled stars
            Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(context, R
                    .color.vivid_yellow));
            // half stars
            Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(context,
                    android.R.color.darker_gray));
            // Empty stars
            Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(context,
                    android.R.color.darker_gray));*/

        }
    }

}
