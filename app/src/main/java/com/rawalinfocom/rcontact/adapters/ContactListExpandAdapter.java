package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileData;

import org.apache.commons.lang3.StringUtils;

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
    private String phonebookId;
    private String contactName;
    PhoneBookContacts phoneBookContacts;

    private int colorBlack, colorPineGreen;

    public ContactListExpandAdapter(Context context, ArrayList<ProfileData> arrayListUserContact,
                                    String phonebookId, String contactName) {
        this.context = context;
        this.phonebookId = phonebookId;
        this.contactName = contactName;
        phoneBookContacts = new PhoneBookContacts(context);
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
        holder.textContactName.setText(contactName);
        if (StringUtils.length(contact.getTempEmail()) > 0) {
            if (phoneBookContacts.getIfContactNumbersExists(phonebookId, contact.getTempNumber())) {
                holder.textContactNumber.setText(contact.getTempNumber());
            } else {
                holder.textContactNumber.setText(contact.getTempEmail());
            }
        } else {
            holder.textContactNumber.setText(contact.getTempNumber());
        }


       /* holder.textCloudContactName.setVisibility(View.VISIBLE);
        holder.textCloudContactName.setText(contact.getTempRcpName());*/
        /*if (StringUtils.contains(contact.getTempRcpName(), ",")) {
            holder.textCloudContactName.setText(String.valueOf(StringUtils.countMatches(contact
                    .getTempRcpName(), ",") + 1));
        }*/
        holder.textCloudContactName.setVisibility(View.VISIBLE);
        holder.textCloudContactName.setText(" (" + contact.getTempRcpName() + ")");
//        holder.imageSocialMedia.setVisibility(View.VISIBLE);
        holder.relativeRowAllContact.setTag(contact.getTempRcpId());
        if (StringUtils.contains(contact.getTempRcpName(), ",")) {
            holder.relativeRowAllContact.setTag(contact.getTempRcpName());
            holder.textCloudContactName.setText(" (" + String.valueOf(StringUtils.countMatches
                    (contact.getTempRcpName(), ",") + 1) + " RC)");
        }

//        holder.imageSocialMedia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showBottomSheet();
//            }
//        });

        holder.relativeRowAllContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView textName = (TextView) view.findViewById(R.id.text_contact_name);
                TextView textCloudName = (TextView) view.findViewById(R.id
                        .text_cloud_contact_name);

                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_PM_ID, contact.getTempRcpId());
                bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, phonebookId);
                bundle.putString(AppConstants.EXTRA_CONTACT_NAME, textName.getText().toString());
                if (textCloudName.getVisibility() == View.VISIBLE) {
                    bundle.putString(AppConstants.EXTRA_CLOUD_CONTACT_NAME, textCloudName
                            .getText().toString());
                }
                ((BaseActivity) context).startActivityIntent(context, ProfileDetailActivity
                        .class, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayListUserContact.size();
    }

    private void showBottomSheet() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);

        View view = ((Activity) context).getLayoutInflater().inflate(R.layout
                .layout_bottom_sheet, null);
        RecyclerView recyclerViewShare = ButterKnife.findById(view, R.id.recycler_view_share);
        TextView textSheetHeader = ButterKnife.findById(view, R.id.text_sheet_header);

        textSheetHeader.setText("Social Media");
        textSheetHeader.setTypeface(Utils.typefaceBold(context));

        BottomSheetSocialMediaAdapter adapter = new BottomSheetSocialMediaAdapter(context);

        recyclerViewShare.setLayoutManager(gridLayoutManager);
        recyclerViewShare.setAdapter(adapter);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    public class AllContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
//        @BindView(R.id.image_social_media)
//        ImageView imageSocialMedia;
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
//        @BindView(R.id.button_invite)
//        Button buttonInvite;

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
//            buttonInvite.setVisibility(View.GONE);
//            imageSocialMedia.setVisibility(View.GONE);
//            textContactName.setVisibility(View.GONE);

            relativeRowAllContact.setBackgroundColor(ContextCompat.getColor(context, R.color
                    .colorLightGrayishCyan1));

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
