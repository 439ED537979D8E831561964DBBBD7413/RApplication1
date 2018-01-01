package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
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
import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.MainActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.SearchActivity;
import com.rawalinfocom.rcontact.calldialer.DialerActivity;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ArrayList<Object> arraylist;
    private ArrayList<ProfileDataOperation> searchArrayList;

    private final int HEADER = 0, CONTACT = 1, FOOTER = 2;

    private int previousPosition = 0;

    private ArrayList<Integer> mSectionPositions;
    private int searchCount;
    private String searchChar;
    private String ownProfileId;

    //<editor-fold desc="Constructor">
    public RContactListAdapter(Fragment fragment, ArrayList<Object> arrayListUserProfile,
                               ArrayList<String> arrayListContactHeader, String ownUserId) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.arrayListUserProfile = arrayListUserProfile;
        this.arrayListContactHeader = arrayListContactHeader;
        this.ownProfileId = ownUserId;
    }

    public RContactListAdapter(Activity activity, ArrayList<Object> arrayListUserContact, String ownProfileId) {
        this.activity = activity;
        this.arrayListUserProfile = new ArrayList<>();
        this.arrayListUserProfile.addAll(arrayListUserContact);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayListUserContact);
        this.ownProfileId = ownProfileId;
    }

    public void updateList(int pos, ArrayList<Object> arrayListUserProfile) {
        this.arrayListUserProfile = arrayListUserProfile;
        notifyItemChanged(pos);
    }
    //</editor-fold>

    //<editor-fold desc="Override Methods">

    public int getSearchCount() {
        return searchCount;
    }

    private void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

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
                configureFooterViewHolder(contactFooterViewHolder);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == arrayListUserProfile.size() && !(activity instanceof SearchActivity)
                && !(activity instanceof DialerActivity)) {
            return FOOTER;
        } else if (arrayListUserProfile.get(position) instanceof UserProfile) {
            return CONTACT;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        if (!(activity instanceof SearchActivity) && !(activity instanceof DialerActivity)) {
            return (arrayListUserProfile.size() + 1);
        } else {
            return arrayListUserProfile.size();
        }
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

        String contactDisplayName = userProfile.getPmFirstName() + " " + userProfile
                .getPmLastName();
        holder.textContactName.setText(contactDisplayName);
        if (StringUtils.length(userProfile.getMobileNumber()) > 0) {
            holder.textContactNumber.setText(userProfile.getMobileNumber());
        } else if (StringUtils.length(userProfile.getEmailId()) > 0) {
            holder.textContactNumber.setText(userProfile.getEmailId());
        }

        if (!StringUtils.isBlank(searchChar)) {
            Pattern numberPat = Pattern.compile("\\d+");
            Matcher matcher1 = numberPat.matcher(searchChar);
            if (matcher1.find() || searchChar.matches("[+][0-9]+")) {
                int startPos = holder.textContactNumber.getText().toString().toLowerCase(Locale.US).indexOf(searchChar
                        .toLowerCase(Locale.US));
                int endPos = startPos + searchChar.length();
                if (startPos != -1) {
                    Spannable spannable = new SpannableString(holder.textContactNumber.getText().toString());
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.textContactNumber.setText(spannable);
                } else {
                    holder.textContactNumber.setText(holder.textContactNumber.getText().toString());
                }
            } else {
                if (searchChar.contains(" ")) {
                    String originalString = holder.textContactName.getText().toString();
                    String[] separated = searchChar.split(" ");
                    String firstPart = separated[0];
                    String secondPart = separated[1];
                    SpannableStringBuilder builder = new SpannableStringBuilder(originalString);
                    if (!StringUtils.isBlank(firstPart)) {
                        int startPos1 = originalString.toLowerCase(Locale.US).indexOf(firstPart
                                .toLowerCase(Locale.US));
                        int endPos1 = startPos1 + firstPart.length();
                        if (startPos1 != -1) {
                            ColorStateList hightlightColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
                            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, hightlightColor, null);
                            builder.setSpan(highlightSpan, startPos1, endPos1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    if (!StringUtils.isBlank(secondPart)) {
                        int startPos2 = originalString.toLowerCase(Locale.US).indexOf(secondPart
                                .toLowerCase(Locale.US));
                        int endPos2 = startPos2 + secondPart.length();
                        if (startPos2 != -1) {
                            ColorStateList hightlightColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
                            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, hightlightColor, null);
                            builder.setSpan(highlightSpan, startPos2, endPos2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    holder.textContactName.setText(builder, TextView.BufferType.SPANNABLE);

                } else {

                    int startPos = holder.textContactName.getText().toString().toLowerCase(Locale.US).indexOf(searchChar
                            .toLowerCase(Locale.US));
                    int endPos = startPos + searchChar.length();

                    if (startPos != -1) {
                        Spannable spannable = new SpannableString(holder.textContactName.getText().toString());
                        ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
                        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
                        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        holder.textContactName.setText(spannable);
                    } else {
                        holder.textContactName.setText(holder.textContactName.getText().toString());
                    }
                }

            }

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

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.length(userProfile.getPmProfileImage()) > 0)
                    Utils.zoomImageFromThumb(activity, holder.imageProfile, userProfile
                            .getPmProfileImage(), ((MainActivity) activity).frameImageEnlarge, (
                            (MainActivity) activity).imageEnlarge, ((MainActivity)
                            activity).frameContainer);
            }
        });

        holder.textRatingUserCount.setText(userProfile.getTotalProfileRateUser());

        if (!StringUtils.equalsIgnoreCase(ownProfileId, userProfile.getPmId())) {
            if (!StringUtils.isBlank(userProfile.getProfileRatingPrivacy())) {
                if (Integer.parseInt((String) MoreObjects.firstNonNull(userProfile.getProfileRatingPrivacy()
                        , 0)) == IntegerConstants.PRIVACY_EVERYONE) {
                    holder.ratingUser.setRating(Float.parseFloat(userProfile.getProfileRating()));
                } else if (Integer.parseInt((String) MoreObjects.firstNonNull(userProfile.getProfileRatingPrivacy(),
                        0)) == IntegerConstants.PRIVACY_MY_CONTACT) {
                    if (MoreObjects.firstNonNull(userProfile.getRatingPrivate(), 0) == IntegerConstants.IS_PRIVATE) {
                        holder.ratingUser.setRating(0);
                    } else {
                        holder.ratingUser.setRating(Float.parseFloat(userProfile.getProfileRating()));
                    }

                } else {
                    holder.ratingUser.setRating(0);
                }
            } else {
                holder.ratingUser.setRating(0);
            }
        } else {
            holder.ratingUser.setRating(Float.parseFloat(userProfile.getProfileRating()));
        }

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
                TextView textName = view.findViewById(R.id.text_contact_name);
                bundle.putString(AppConstants.EXTRA_CONTACT_NAME, textName.getText().toString());
                bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, userProfile
                        .getPmProfileImage());
//                bundle.putString(AppConstants.EXTRA_CALL_HISTORY_NUMBER, userProfile.getMobileNumber());

               /* ((BaseActivity) activity).startActivityIntent(activity, ProfileDetailActivity
                        .class, bundle);*/
                bundle.putBoolean(AppConstants.EXTRA_IS_RCP_USER, true);
//                bundle.putString(AppConstants.EXTRA_CLOUD_CONTACT_NAME, textName.getText().toString());

                bundle.putString(AppConstants.EXTRA_CALL_HISTORY_NUMBER, userProfile
                        .getMobileNumber());
                if (fragment != null) {
                    Intent intent = new Intent(activity, ProfileDetailActivity.class);
                    intent.putExtras(bundle);
                    fragment.startActivityForResult(intent, AppConstants
                            .REQUEST_CODE_PROFILE_DETAIL);
                    activity.overridePendingTransition(R.anim.enter, R
                            .anim.exit);
                } else {
                    Intent intent = new Intent(activity, ProfileDetailActivity.class);
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, AppConstants
                            .REQUEST_CODE_PROFILE_DETAIL);
                    activity.overridePendingTransition(R.anim.enter, R
                            .anim.exit);
                }

            }
        });
    }

    private void configureFooterViewHolder(ContactFooterViewHolder holder) {
        if (!(activity instanceof SearchActivity) && !(activity instanceof DialerActivity)) {
            holder.textTotalContacts.setText(String.format(Locale.getDefault(), "%d %s", arrayListUserProfile.size()
                    - arrayListContactHeader.size(), activity.getString(R.string.str_count_contacts)));
        }
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

    public void getData() {

        Gson gson = new Gson();
        //For default value, just to get no errors while getting no value from the SharedPreferences
        String empty_list = gson.toJson(new ArrayList<ProfileDataOperation>());

        searchArrayList = new ArrayList<>();
        searchArrayList.clear();
        searchArrayList = gson.fromJson(Utils.getStringPreference(activity, "search_data", empty_list),
                new TypeToken<ArrayList<ProfileDataOperation>>() {
                }.getType());
    }

    // Filter Class
    public void filter(String charText) {
        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(charText);
        if (matcher1.find()) {
            charText = charText.toLowerCase(Locale.getDefault());
            charText = charText.trim();
            arrayListUserProfile.clear();
            if (charText.length() == 0) {
                arrayListUserProfile.addAll(arraylist);
            } else {
                for (int i = 0; i < arraylist.size(); i++) {
                    /*if (arraylist.get(i) instanceof ProfileData) {
                        charText = charText.trim();
                        ProfileData profileData = (ProfileData) arraylist.get(i);
                        if (!StringUtils.isEmpty(profileData.getTempNumber())) {
                            String number = profileData.getTempNumber();
                            number = number.replace(" ", "").replace("-", "");
                            if (number.contains(charText)) {
                                arrayListUserProfile.add(profileData);
                            }
                        }
                    }*/
                    if (arraylist.get(i) instanceof UserProfile) {
                        UserProfile profileData = (UserProfile) arraylist.get(i);
                        String name = profileData.getMobileNumber();
                        if (!StringUtils.isEmpty(name)) {
                            name = name.replace(" ", "").replace("-", "");
                            if (name.toLowerCase(Locale.getDefault()).contains
                                    (charText)) {
                                arrayListUserProfile.add(profileData);
                            }
                        }
                    }
                }
            }
        } else {
            charText = charText.toLowerCase(Locale.getDefault());
            charText = charText.trim();
            arrayListUserProfile.clear();
            if (charText.length() == 0) {
                arrayListUserProfile.addAll(arraylist);
            } else {
                for (int i = 0; i < arraylist.size(); i++) {
                    if (arraylist.get(i) instanceof UserProfile) {
                        UserProfile profileData = (UserProfile) arraylist.get(i);
                        String name = profileData.getPmFirstName() + " " + profileData.getPmFirstName();
                        ArrayList<ProfileDataOperationOrganization> organizationArrayList =
                                profileData.getPbOrganization();
                        String orgName = "";
                        String orgDesign = "";
                        if (!StringUtils.isEmpty(name)) {
                            if (name.toLowerCase(Locale.getDefault()).contains
                                    (charText)) {
                                arrayListUserProfile.add(profileData);
                            } else {
                                if (!StringUtils.isBlank(profileData.getPmFirstName())
                                        && !StringUtils.isBlank(profileData.getPmLastName())) {
                                    nameFilter(charText, profileData);
                                }
                            }
                        }
                        if (organizationArrayList.size() > 0) {
                            for (int j = 0; j < organizationArrayList.size(); j++) {
                                orgName = organizationArrayList.get(j).getOrgName();
                                orgDesign = organizationArrayList.get(j).getOrgJobTitle();
                                if (!StringUtils.isEmpty(orgName) || !StringUtils.isEmpty(orgDesign)) {
                                    if (orgName.toLowerCase(Locale.getDefault()).contains
                                            (charText) || orgDesign.toLowerCase(Locale.getDefault()).contains
                                            (charText)) {
                                        if (!arrayListUserProfile.contains(profileData))
                                            arrayListUserProfile.add(profileData);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        searchChar = charText;
        setSearchCount(arrayListUserProfile.size());
        notifyDataSetChanged();
    }

    // TODO : Hardik : Global Search Organisation
    // Filter Class
//    public void filter(String charText) {
//
//        Pattern numberPat = Pattern.compile("\\d+");
//        Matcher matcher1 = numberPat.matcher(charText);
//
//        charText = charText.toLowerCase(Locale.getDefault());
//        charText = charText.trim();
//        arrayListUserProfile.clear();
//
//        if (matcher1.find()) {
//            if (charText.length() == 0) {
////                arrayListUserProfile.addAll(arraylist);
//                arrayListUserProfile.addAll(searchArrayList);
//            } else {
////                for (int i = 0; i < arraylist.size(); i++) {
//                for (int i = 0; i < searchArrayList.size(); i++) {
//                    /*if (arraylist.get(i) instanceof ProfileData) {
//                        charText = charText.trim();
//                        ProfileData profileData = (ProfileData) arraylist.get(i);
//                        if (!StringUtils.isEmpty(profileData.getTempNumber())) {
//                            String number = profileData.getTempNumber();
//                            number = number.replace(" ", "").replace("-", "");
//                            if (number.contains(charText)) {
//                                arrayListUserProfile.add(profileData);
//                            }
//                        }
//                    }*/
//
//                    UserProfile userProfile = new UserProfile();
//                    userProfile.setPmFirstName(searchArrayList.get(i).getPbNameFirst());
//                    userProfile.setPmLastName(searchArrayList.get(i).getPbNameLast());
//                    userProfile.setMobileNumber(Utils.getFormattedNumber(activity, searchArrayList.get(i).getVerifiedMobileNumber()));
//                    userProfile.setPmRcpId(searchArrayList.get(i).getRcpPmId());
//                    userProfile.setPmId(searchArrayList.get(i).getRcpPmId());
//                    userProfile.setPmNosqlMasterId(searchArrayList.get(i).getNoSqlMasterId());
//                    userProfile.setPmBadge(searchArrayList.get(i).getPmBadge());
//                    userProfile.setProfileRating(searchArrayList.get(i).getProfileRating());
//                    userProfile.setPmProfileImage(searchArrayList.get(i).getPbProfilePhoto());
//                    userProfile.setTotalProfileRateUser(searchArrayList.get(i).getTotalProfileRateUser());
//                    userProfile.setPmLastSeen(searchArrayList.get(i).getPmLastSeen());
//                    userProfile.setProfileRatingPrivacy(String.valueOf(searchArrayList.get(i).getProfileRatingPrivacy()));
//                    userProfile.setRatingPrivate(String.valueOf(searchArrayList.get(i).getRatingPrivate()));
//
//                    String name = userProfile.getMobileNumber();
//
//                    if (!StringUtils.isEmpty(name)) {
//                        if (!StringUtils.isEmpty(name)) {
//                            name = name.replace(" ", "").replace("-", "");
//                            if (name.toLowerCase(Locale.getDefault()).contains
//                                    (charText)) {
//                                arrayListUserProfile.add(userProfile);
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//
//            if (charText.length() == 0) {
////                arrayListUserProfile.addAll(arraylist);
//                arrayListUserProfile.addAll(searchArrayList);
//            } else {
////                for (int i = 0; i < arraylist.size(); i++) {
//                for (int i = 0; i < searchArrayList.size(); i++) {
////                    if (arraylist.get(i) instanceof UserProfile) {
////                    if (searchArrayList.get(i) instanceof UserProfile) {
////                        UserProfile profileData = (UserProfile) arraylist.get(i);
////                        UserProfile profileData = (UserProfile) searchArrayList.get(i);
//
//                    UserProfile userProfile = new UserProfile();
//                    userProfile.setPmFirstName(searchArrayList.get(i).getPbNameFirst());
//                    userProfile.setPmLastName(searchArrayList.get(i).getPbNameLast());
//                    userProfile.setMobileNumber(Utils.getFormattedNumber(activity, searchArrayList.get(i).getVerifiedMobileNumber()));
//                    userProfile.setPmRcpId(searchArrayList.get(i).getRcpPmId());
//                    userProfile.setPmId(searchArrayList.get(i).getRcpPmId());
//                    userProfile.setPmNosqlMasterId(searchArrayList.get(i).getNoSqlMasterId());
//                    userProfile.setPmBadge(searchArrayList.get(i).getPmBadge());
//                    userProfile.setProfileRating(searchArrayList.get(i).getProfileRating());
//                    userProfile.setPmProfileImage(searchArrayList.get(i).getPbProfilePhoto());
//                    userProfile.setTotalProfileRateUser(searchArrayList.get(i).getTotalProfileRateUser());
//                    userProfile.setPmLastSeen(searchArrayList.get(i).getPmLastSeen());
//                    userProfile.setProfileRatingPrivacy(String.valueOf(searchArrayList.get(i).getProfileRatingPrivacy()));
//                    userProfile.setRatingPrivate(String.valueOf(searchArrayList.get(i).getRatingPrivate()));
//
//                    String name = userProfile.getPmFirstName() + " " + userProfile.getPmLastName();
//
//                    if (!StringUtils.isEmpty(name)) {
//                        if (name.toLowerCase(Locale.getDefault()).contains
//                                (charText)) {
//                            arrayListUserProfile.add(userProfile);
//                        } else {
//
//                            ArrayList<ProfileDataOperationOrganization> arrayListOrganization =
//                                    searchArrayList.get(i).getPbOrganization();
//
//                            if (!Utils.isArraylistNullOrEmpty(arrayListOrganization)) {
//
//                                for (int j = 0; j < arrayListOrganization.size(); j++) {
//
//                                    Organization organization = new Organization();
//
//                                    organization.setOmOrganizationCompany(arrayListOrganization.get(j).getOrgName());
//                                    organization.setOmOrganizationDesignation(arrayListOrganization.get(j)
//                                            .getOrgJobTitle());
//
//                                    String orgName = organization.getOmOrganizationCompany();
//                                    String orgDesignation = organization.getOmOrganizationDesignation();
//                                    if (!StringUtils.isEmpty(orgName)) {
//                                        orgName = orgName.replace(" ", "").replace("-", "");
//                                        if (orgName.toLowerCase(Locale.getDefault()).contains(charText) ||
//                                                orgDesignation.toLowerCase(Locale.getDefault()).contains(charText)) {
//                                            arrayListUserProfile.add(userProfile);
//                                        }
//                                    }
//                                }
//                            }
////                            if (!StringUtils.isBlank(userProfile.getPmFirstName())
////                                    && !StringUtils.isBlank(userProfile.getPmLastName())) {
////                                nameFilter(charText, userProfile);
////                            }
//                        }
//                    }
////                    }
//
////                    if (arraylist.get(i) instanceof ProfileDataOperationOrganization) {
////                    if (searchArrayList.get(i).getPbOrganizationList() instanceof ProfileDataOperationOrganization) {
////                        Organization organization = (Organization) arraylist.get(i);
////                    }
//                }
//            }
//        }
//        searchChar = charText;
//        setSearchCount(arrayListUserProfile.size());
//        notifyDataSetChanged();
//    }

    private void nameFilter(String charText, UserProfile profileData) {

        if (charText.contains(" ")) {

            String[] splitString = charText.split("\\s+");

            if (splitString.length == 2) {

                if ((profileData.getPmFirstName().toLowerCase().startsWith(splitString[0])
                        || profileData.getPmLastName().toLowerCase().startsWith(splitString[0]))
                        && (profileData.getPmFirstName().toLowerCase().startsWith(splitString[1])
                        || profileData.getPmLastName().toLowerCase().startsWith(splitString[1]))) {
                    arrayListUserProfile.add(profileData);
                }

            } else {

                if ((profileData.getPmFirstName().toLowerCase().startsWith(splitString[0])
                        || profileData.getPmLastName().toLowerCase().startsWith(splitString[0]))
                        && (profileData.getPmFirstName().toLowerCase().startsWith(splitString[1])
                        || profileData.getPmLastName().toLowerCase().startsWith(splitString[1]))) {
                    arrayListUserProfile.add(profileData);
                }
            }

        } else {

            if ((profileData.getPmFirstName().toLowerCase().startsWith(charText)
                    || profileData.getPmLastName().toLowerCase().startsWith(charText))
                    && (profileData.getPmFirstName().toLowerCase().startsWith(charText)
                    || profileData.getPmLastName().toLowerCase().startsWith(charText))) {
                arrayListUserProfile.add(profileData);
            }
        }

    }

}
