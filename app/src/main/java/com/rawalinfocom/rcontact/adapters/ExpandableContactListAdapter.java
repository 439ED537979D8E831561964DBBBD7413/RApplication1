package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
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
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 07/02/17.
 */

public class ExpandableContactListAdapter extends RecyclerView
        .Adapter<ExpandableContactListAdapter.ExpandableContactListViewHolder> {

    private Context context;
    private Fragment fragment;
    private int colorBlack, colorPineGreen;
    private String contactDisplayName;
    private ArrayList<ProfileMobileMapping> arrayListDbMobileNumbers;

    private TableProfileMaster tableProfileMaster;

    public ExpandableContactListAdapter(Fragment fragment, ArrayList<ProfileMobileMapping>
            arrayListDbMobileNumbers, String contactDisplayName) {

        this.fragment = fragment;
        this.context = fragment.getActivity();
        this.contactDisplayName = contactDisplayName;

        this.arrayListDbMobileNumbers = arrayListDbMobileNumbers;

        colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);

        tableProfileMaster = new TableProfileMaster(((BaseActivity) context).databaseHandler);

    }

    @Override
    public ExpandableContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_all_contacts, parent, false);
        return new ExpandableContactListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExpandableContactListViewHolder holder, int position) {
        ProfileMobileMapping profileMobileMapping = arrayListDbMobileNumbers.get(position);
        holder.textContactNumber.setText(profileMobileMapping.getMpmMobileNumber());
        String displayNamePmId = profileMobileMapping.getMpmCloudPmId();
        holder.relativeRowAllContact.setTag(displayNamePmId);

        holder.textContactName.setText(contactDisplayName.length() > 0 ? contactDisplayName :
                "[Unknown]");

        UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                .parseInt(displayNamePmId));

        String rating = userProfile.getTotalProfileRateUser();
        String totalRatingUser = userProfile.getProfileRating();

        holder.textRatingUserCount.setText(rating);
        holder.ratingUser.setRating(Float.parseFloat(totalRatingUser));

        String displayName = ((userProfile.getPmFirstName().length() > 0 || userProfile
                .getPmLastName().length() > 0) ? " (" + userProfile.getPmFirstName() + " " +
                "" + userProfile.getPmLastName() + ")" : "");

        if (StringUtils.equals(displayName, (" (" + contactDisplayName + ")"))) {
            holder.textCloudContactName.setVisibility(View.GONE);
            holder.textContactName.setTextColor(colorPineGreen);
        } else {
            holder.textCloudContactName.setVisibility(View.VISIBLE);
            holder.textContactName.setTextColor(colorBlack);
        }

        holder.textCloudContactName.setText(displayName);

    }

    @Override
    public int getItemCount() {
        return arrayListDbMobileNumbers.size();
    }

    //<editor-fold desc="View Holders">

    class ExpandableContactListViewHolder extends RecyclerView.ViewHolder {

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

        ExpandableContactListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceSemiBold(context));
            textCloudContactName.setTypeface(Utils.typefaceSemiBold(context));
            textContactNumber.setTypeface(Utils.typefaceRegular(context));
            textRatingUserCount.setTypeface(Utils.typefaceRegular(context));

            textContactName.setTextColor(colorBlack);
            textContactNumber.setTextColor(colorBlack);

            textCloudContactName.setTextColor(colorPineGreen);

            recyclerViewMultipleRc.setVisibility(View.GONE);

        }
    }

    //</editor-fold>

}
