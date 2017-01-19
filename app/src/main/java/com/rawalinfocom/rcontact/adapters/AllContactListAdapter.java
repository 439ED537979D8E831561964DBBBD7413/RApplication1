package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.AllContactsFragment;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileEmailMapping;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class AllContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SectionIndexer {

    /**
     * relativeRowAllContact tag :
     * rcp Contact: pm id
     * non rcp: -1
     * own profile: 0
     */

    private Context context;
    private Fragment fragment;
    /* phone book contacts */
    private ArrayList<Object> arrayListUserContact;
    private ArrayList<String> arrayListContactHeader;

    private final int HEADER = 0, CONTACT = 1;

    private int colorBlack, colorPineGreen;
    private int previousPosition = 0;
    int listClickedPosition = -1;

    //<editor-fold desc="Constructor">
    public AllContactListAdapter(Fragment fragment, ArrayList<Object> arrayListUserContact,
                                 ArrayList<String> arrayListContactHeader) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.arrayListUserContact = arrayListUserContact;
        this.arrayListContactHeader = arrayListContactHeader;
        this.arrayListContactHeader = new ArrayList<>();
        this.arrayListContactHeader.add("#");
        this.arrayListContactHeader.addAll(arrayListContactHeader);

        colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);
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
                viewHolder = new AllContactViewHolder(v2);
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
                AllContactViewHolder contactViewHolder = (AllContactViewHolder) holder;
                configureAllContactViewHolder(contactViewHolder, position);
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (arrayListUserContact.get(position) instanceof ProfileData) {
            return CONTACT;
        } else if (arrayListUserContact.get(position) instanceof String) {
            return HEADER;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return arrayListUserContact.size();
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
        if (position >= arrayListUserContact.size()) {
            position = arrayListUserContact.size() - 1;
        }

        if (arrayListUserContact.get(position) instanceof String) {
            String letter = (String) arrayListUserContact.get(position);
            previousPosition = arrayListContactHeader.indexOf(letter);

        } else {
            /*for (int i = position; i < arrayListUserContact.size(); i++) {
                if (arrayListUserContact.get(i) instanceof String) {
                    String letter = (String) arrayListUserContact.get(i);
                    previousPosition = arrayListContactHeader.indexOf(letter);
                    break;
                }
            }*/
            for (int i = position; i >= 0; i--) {
                if (arrayListUserContact.get(i) instanceof String) {
                    String letter = (String) arrayListUserContact.get(i);
                    previousPosition = arrayListContactHeader.indexOf(letter);
                    break;
                }
            }
        }

        return previousPosition;
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void configureAllContactViewHolder(final AllContactViewHolder holder, final int
            position) {
        final ProfileData profileData = (ProfileData) arrayListUserContact.get(position);

        holder.textContactName.setTag(position);

        String contactDisplayName = profileData.getOperation().get(0).getPbNameFirst() + "" +
                " " + profileData.getOperation().get(0).getPbNameLast();
        holder.textContactName.setText(contactDisplayName);

        if (profileData.getOperation().get(0).getPbPhoneNumber().size() > 0) {
            displayNumber(holder, profileData, contactDisplayName, position);
        } else if (!Utils.isArraylistNullOrEmpty(profileData.getOperation().get(0)
                .getPbEmailId()) && profileData.getOperation().get(0).getPbEmailId().size() > 0) {
            displayEmail(holder, profileData, null, contactDisplayName, position);
        }

        /* Hide Divider if row is last in Section */
        if ((position + 1) < arrayListUserContact.size()) {
            if (arrayListUserContact.get(position + 1) instanceof String) {
                holder.dividerAllContact.setVisibility(View.GONE);
            } else {
                holder.dividerAllContact.setVisibility(View.VISIBLE);
            }
        }

        holder.relativeRowAllContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                if (StringUtils.equalsIgnoreCase(view.getTag().toString(), "0")) {
                    // Display own profile
                    bundle.putString(AppConstants.EXTRA_PM_ID, ((BaseActivity) context)
                            .getUserPmId());
                    bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, "-1");
                } else if (!StringUtils.equalsIgnoreCase(view.getTag().toString(), "-1")) {
                    // RCP profile
                    bundle.putString(AppConstants.EXTRA_PM_ID, String.valueOf(view.getTag()));
                    bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, profileData
                            .getLocalPhoneBookId());
                } else {
                    // Non RCP profile
                    bundle.putString(AppConstants.EXTRA_PM_ID, "-1");
                    bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, profileData
                            .getLocalPhoneBookId());
                }
                TextView textName = (TextView) view.findViewById(R.id.text_contact_name);
                TextView textCloudName = (TextView) view.findViewById(R.id.text_cloud_contact_name);
                bundle.putString(AppConstants.EXTRA_CONTACT_NAME, textName.getText().toString());
                listClickedPosition = (int) textName.getTag();
                if (textCloudName.getVisibility() == View.VISIBLE) {
                    bundle.putString(AppConstants.EXTRA_CLOUD_CONTACT_NAME, textCloudName.getText()
                            .toString());
                }
                ((BaseActivity) context).startActivityIntent(context, ProfileDetailActivity
                        .class, bundle);
            }
        });

        holder.imageSocialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });

    }

    private void configureHeaderViewHolder(ContactHeaderViewHolder holder, int position) {
        String letter = (String) arrayListUserContact.get(position);
        holder.textHeader.setText(letter);
    }

    private void displayNumber(AllContactViewHolder holder, ProfileData profileData, String
            contactDisplayName, int position) {
        TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping((
                (BaseActivity) context).databaseHandler);

        ArrayList<String> arrayListMobileNumbers = new ArrayList<>();
        for (int i = 0; i < profileData.getOperation().get(0).getPbPhoneNumber().size(); i++) {
            arrayListMobileNumbers.add(profileData.getOperation().get(0).getPbPhoneNumber().get
                    (i).getPhoneNumber());
        }

        ArrayList<ProfileMobileMapping> arrayListDbMobileNumbers = tableProfileMobileMapping
                .getProfileMobileMappingFromNumber(arrayListMobileNumbers.toArray(new
                        String[arrayListMobileNumbers.size()]));

        String displayNumber, displayName;
        boolean isRcp;
        if (arrayListDbMobileNumbers.size() > 0) {
            displayNumber = arrayListDbMobileNumbers.get(0).getMpmMobileNumber();
//            String displayNamePmId = arrayListDbMobileNumbers.get(0).getMpmCloudPmId();

//            holder.relativeRowAllContact.setTag(displayNamePmId);


            if (arrayListDbMobileNumbers.size() == 1) {

                String displayNamePmId = arrayListDbMobileNumbers.get(0).getMpmCloudPmId();
                holder.relativeRowAllContact.setTag(displayNamePmId);

                TableProfileMaster tableProfileMaster = new TableProfileMaster(((BaseActivity)
                        context).databaseHandler);
                UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                        .parseInt(displayNamePmId));

                holder.linearRating.setVisibility(View.VISIBLE);
                holder.textRatingUserCount.setText(userProfile.getTotalProfileRateUser());
                holder.ratingUser.setRating(Float.parseFloat(userProfile.getProfileRating()));

                displayName = ((userProfile.getPmFirstName().length() > 0 || userProfile
                        .getPmLastName().length() > 0) ? " (" + userProfile.getPmFirstName() + " " +
                        "" + userProfile.getPmLastName() + ")" : "");

            } else {

                String[] pmIds = new String[arrayListDbMobileNumbers.size()];
                for (int i = 0; i < arrayListDbMobileNumbers.size(); i++) {
                    pmIds[i] = arrayListDbMobileNumbers.get(i).getMpmCloudPmId();
                }
                holder.relativeRowAllContact.setTag(StringUtils.join(pmIds, ","));

                displayName = " (" + arrayListDbMobileNumbers.size() + "RC)";
                holder.linearRating.setVisibility(View.GONE);
            }

            if (StringUtils.equals(displayName, (" (" + contactDisplayName + ")"))) {
                holder.textCloudContactName.setVisibility(View.GONE);
                holder.textContactName.setTextColor(colorPineGreen);
            } else {
                holder.textCloudContactName.setVisibility(View.VISIBLE);
                /*if (position != 1) {
                    holder.textContactName.setTextColor(colorBlack);
                } else {
                    holder.textContactName.setTextColor(colorPineGreen);
                }*/
                if (position == 1 && fragment instanceof AllContactsFragment) {
                    holder.textContactName.setTextColor(colorPineGreen);
                } else {
                    holder.textContactName.setTextColor(colorBlack);
                }
            }

            holder.textContactNumber.setTextColor(colorPineGreen);
            isRcp = true;
            holder.linearRating.setVisibility(View.VISIBLE);
        } else {
            displayNumber = profileData.getOperation().get(0).getPbPhoneNumber().get(0)
                    .getPhoneNumber();
            displayName = "";
           /* if (position != 1) {
                holder.relativeRowAllContact.setTag("-1");
                holder.textContactName.setTextColor(colorBlack);
                holder.textContactNumber.setTextColor(colorBlack);
                holder.linearRating.setVisibility(View.GONE);
            } else {
                holder.relativeRowAllContact.setTag("0");
                holder.textContactName.setTextColor(colorPineGreen);
                holder.textContactNumber.setTextColor(colorPineGreen);
                holder.linearRating.setVisibility(View.VISIBLE);
            }*/
            if (position == 1 && fragment instanceof AllContactsFragment) {
                holder.relativeRowAllContact.setTag("0");
                holder.textContactName.setTextColor(colorPineGreen);
                holder.textContactNumber.setTextColor(colorPineGreen);
                holder.linearRating.setVisibility(View.VISIBLE);
            } else {
                holder.relativeRowAllContact.setTag("-1");
                holder.textContactName.setTextColor(colorBlack);
                holder.textContactNumber.setTextColor(colorBlack);
                holder.linearRating.setVisibility(View.GONE);
            }

            isRcp = false;
        }

        displayNumber = Utils.getFormattedNumber(context, displayNumber);

        holder.textCloudContactName.setText(displayName);
        holder.textContactNumber.setText(displayNumber);

        if (!isRcp && !Utils.isArraylistNullOrEmpty(profileData.getOperation().get(0)
                .getPbEmailId())) {
            displayEmail(holder, profileData, displayNumber, contactDisplayName, position);
        }

    }

    private void displayEmail(AllContactViewHolder holder, ProfileData profileData, String
            displayNumber, String contactDisplayName, int position) {
        TableProfileEmailMapping tableProfileEmailMapping = new TableProfileEmailMapping((
                (BaseActivity) context).databaseHandler);

        ArrayList<String> arrayListEmails = new ArrayList<>();
        for (int i = 0; i < profileData.getOperation().get(0).getPbEmailId().size(); i++) {
            arrayListEmails.add(profileData.getOperation().get(0).getPbEmailId().get
                    (i).getEmEmailId());
        }

        if (arrayListEmails.size() > 0) {
            ArrayList<ProfileEmailMapping> arrayListDbEmailIds = tableProfileEmailMapping
                    .getProfileEmailMappingFromEmailId(arrayListEmails.toArray(new
                            String[arrayListEmails.size()]));

            String displayEmailId, displayName;
            boolean isRcp;
            if (arrayListDbEmailIds.size() > 0) {
                displayEmailId = arrayListDbEmailIds.get(0).getEpmEmailId();
              /*  String displayNamePmId = arrayListDbEmailIds.get(0).getEpmCloudPmId();
                holder.relativeRowAllContact.setTag(displayNamePmId);*/

                if (arrayListDbEmailIds.size() == 1) {

                    String displayNamePmId = arrayListDbEmailIds.get(0).getEpmCloudPmId();
                    holder.relativeRowAllContact.setTag(displayNamePmId);

                    TableProfileMaster tableProfileMaster = new TableProfileMaster(((BaseActivity)
                            context).databaseHandler);
                    UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                            .parseInt(displayNamePmId));

                    holder.linearRating.setVisibility(View.VISIBLE);
                    holder.textRatingUserCount.setText(userProfile.getTotalProfileRateUser());
                    holder.ratingUser.setRating(Float.parseFloat(userProfile.getProfileRating()));

                    displayName = ((userProfile.getPmFirstName().length() > 0 || userProfile
                            .getPmLastName().length() > 0) ? " (" + userProfile
                            .getPmFirstName() + " " + userProfile
                            .getPmLastName() + ")" : "");

                } else {

                    String[] pmIds = new String[arrayListDbEmailIds.size()];
                    for (int i = 0; i < arrayListDbEmailIds.size(); i++) {
                        pmIds[i] = arrayListDbEmailIds.get(i).getEpmCloudPmId();
                    }
                    holder.relativeRowAllContact.setTag(StringUtils.join(pmIds, ","));

                    displayName = " (" + arrayListDbEmailIds.size() + "RC)";
                    holder.linearRating.setVisibility(View.GONE);
                }

                if (StringUtils.equals(displayName, (" (" + contactDisplayName + ")"))) {
                    holder.textCloudContactName.setVisibility(View.GONE);
                    holder.textContactName.setTextColor(colorPineGreen);
                } else {
                    holder.textCloudContactName.setVisibility(View.VISIBLE);
                    if (position == 1) {
                        holder.textContactName.setTextColor(colorPineGreen);
                    } else {
                        holder.textContactName.setTextColor(colorBlack);
                    }
                }

//                holder.textContactNumber.setTextColor(colorPineGreen);
                isRcp = true;
                holder.linearRating.setVisibility(View.VISIBLE);
            } else {
             /*   if (position == 1) {
                    holder.linearRating.setVisibility(View.VISIBLE);
                    holder.relativeRowAllContact.setTag("0");
                } else {
                    holder.linearRating.setVisibility(View.GONE);
                    holder.relativeRowAllContact.setTag("-1");
                }*/
                if (position == 1 && fragment instanceof AllContactsFragment) {
                    holder.linearRating.setVisibility(View.VISIBLE);
                    holder.relativeRowAllContact.setTag("0");
                } else {
                    holder.linearRating.setVisibility(View.GONE);
                    holder.relativeRowAllContact.setTag("-1");
                }
                displayEmailId = profileData.getOperation().get(0).getPbEmailId().get(0)
                        .getEmEmailId();
                displayName = "";
//                holder.textContactNumber.setTextColor(colorBlack);
                isRcp = false;
            /* Display mobile number if Email Id is not rcp */
                if (displayNumber != null) {
                    displayEmailId = displayNumber;
                }
            }
            holder.textCloudContactName.setText(displayName);
             /*holder.textContactNumber.setText(displayEmailId);*/
            if (displayNumber != null) {
                holder.textContactNumber.setText(displayNumber);
            } else {
                if (isRcp || (position == 1 && fragment instanceof AllContactsFragment)) {
                    holder.textContactNumber.setTextColor(colorPineGreen);
                } else {
                    holder.textContactNumber.setTextColor(colorBlack);
                }
                holder.textContactNumber.setText(displayEmailId);
            }

        }

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

    public int getListClickedPosition() {
        return listClickedPosition;
    }

    //</editor-fold>

    //<editor-fold desc="View Holders">

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

//            textRatingUserCount.setText("0");
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

    //</editor-fold>

}
