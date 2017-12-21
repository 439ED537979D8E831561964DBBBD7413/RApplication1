package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.SearchActivity;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.GlobalSearchType;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 14/06/17.
 */

public class GlobalSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    ArrayList<GlobalSearchType> globalSearchTypeArrayList;
    String nameToPass = "";
    String searchedText;

    public GlobalSearchAdapter(Context context, ArrayList<GlobalSearchType> globalSearchTypeList,
                               String searchChar) {
        this.context = context;
        this.globalSearchTypeArrayList = globalSearchTypeList;
        this.searchedText = searchChar;
    }

    @Override
    public GlobalSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_global_search,
                parent, false);
        return new GlobalSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder contactViewHolder, int position) {
        final GlobalSearchViewHolder holder = (GlobalSearchViewHolder) contactViewHolder;
        final GlobalSearchType globalSearchType = globalSearchTypeArrayList.get(position);
        String firstName = globalSearchType.getFirstName();
        int isRcpVerified = globalSearchType.getIsRcpVerified();
        if (isRcpVerified == 1) {
            holder.textContactFirstname.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
//            holder.textContactLastname.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.textContactNumber.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.textRatingUserCount.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.linearRating.setVisibility(View.VISIBLE);
        } else {
            holder.textContactFirstname.setTextColor(ContextCompat.getColor(context, R.color.textColorBlue));
//            holder.textContactLastname.setTextColor(ContextCompat.getColor(context, R.color.textColorBlue));
            holder.textContactNumber.setTextColor(ContextCompat.getColor(context, R.color.textColorBlue));
            holder.linearRating.setVisibility(View.GONE);
        }

        String contactNameToDisplay = "";
        if (!StringUtils.isEmpty(firstName)) {
            contactNameToDisplay = firstName;
        }

        String lastName = globalSearchType.getLastName();
        if (!StringUtils.isEmpty(lastName)) {
            contactNameToDisplay = contactNameToDisplay + " " + lastName;
        }

        holder.textContactFirstname.setText(contactNameToDisplay);


        /*if (!StringUtils.isEmpty(firstName)) {
            holder.textContactFirstname.setText(firstName);
        } else {
            holder.textContactFirstname.setText("");
        }*/

        /*if (!StringUtils.isEmpty(lastName))
            holder.textContactLastname.setText(lastName);
        else
            holder.textContactLastname.setText("");*/

        if (!StringUtils.isEmpty(firstName) && !StringUtils.isEmpty(lastName)) {
            nameToPass = firstName + " " + lastName;
        } else if (!StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName)) {
            nameToPass = firstName;
        } else if (StringUtils.isEmpty(firstName) && !StringUtils.isEmpty(lastName)) {
            nameToPass = lastName;
        } else {
            nameToPass = "";
        }

        String mobileNumber = globalSearchType.getMobileNumber();
        if (!StringUtils.isEmpty(mobileNumber)) {
            String subString = StringUtils.substring(mobileNumber, 0, 2);
            if (subString.equalsIgnoreCase("91")) {
                mobileNumber = "+" + mobileNumber;
            }
            holder.textContactNumber.setText(mobileNumber);
        } else
            holder.textContactNumber.setText("");

        final String profileImage = globalSearchType.getProfileImageUrl();
        if (!StringUtils.isEmpty(profileImage)) {
            Glide.with(context)
                    .load(profileImage)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(500, 500)
                    .into(holder.imageProfile);

        } else {
            holder.imageProfile.setImageResource(R.drawable.home_screen_profile);
        }

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.length(profileImage) > 0) if (context instanceof SearchActivity)
                    Utils.zoomImageFromThumb((Activity) context, holder.imageProfile,
                            profileImage, ((SearchActivity) context).frameImageEnlarge, (
                                    (SearchActivity) context).imageEnlarge, ((SearchActivity)
                                    context).frameContainer);
            }
        });

        String userRatingCount = globalSearchType.getProfileRatedCount();
        if (!StringUtils.isEmpty(userRatingCount)) {
            holder.textRatingUserCount.setText(userRatingCount);
        } else {
            holder.textRatingUserCount.setText("");
        }

        if ((MoreObjects.firstNonNull(globalSearchType.getRatingPrivate(), 0)) == IntegerConstants
                .IS_PRIVATE) {
            holder.ratingUser.setRating(0);
        } else {
            String averageRating = globalSearchType.getAverageRating();
            if (!StringUtils.isEmpty(averageRating)) {
                holder.ratingUser.setRating(Float.parseFloat(averageRating));
            } else {
                holder.ratingUser.setRating(Float.parseFloat("0"));
            }
        }

        if (!StringUtils.isBlank(searchedText)) {
            if (searchedText.matches("[+][0-9]+") || searchedText.matches("\\d+")) {
                hightLightSearchedText(holder.textContactNumber.getText().toString(), holder.textContactNumber);
            } else {
                if (searchedText.contains(" ")) {
                    try {
                        String originalStringCloud = holder.textContactFirstname.getText().toString();
                        searchedText = searchedText.trim();
                        String[] separatedCloud = searchedText.split(" ");
                        String secondPartCloud = "";
                        String firstPartCloud = "";
                        if (!StringUtils.isBlank(separatedCloud[0]))
                            firstPartCloud = separatedCloud[0];
                        if (searchedText.contains(" ")) {
                            if (separatedCloud[1] != null && separatedCloud[1].length() > 0)
                                secondPartCloud = separatedCloud[1];
                        }

                        SpannableStringBuilder builderCloud = new SpannableStringBuilder(originalStringCloud);
                        if (!StringUtils.isBlank(firstPartCloud)) {
                            int startPos1 = originalStringCloud.toLowerCase(Locale.US).indexOf(firstPartCloud
                                    .toLowerCase(Locale.US));
                            int endPos1 = startPos1 + firstPartCloud.length();
                            if (startPos1 != -1) {
                                ColorStateList hightlightColor;
                                hightlightColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
                                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, hightlightColor, null);
                                builderCloud.setSpan(highlightSpan, startPos1, endPos1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                        if (!StringUtils.isBlank(secondPartCloud)) {
                            int startPos2 = originalStringCloud.toLowerCase(Locale.US).indexOf(secondPartCloud
                                    .toLowerCase(Locale.US));
                            int endPos2 = startPos2 + secondPartCloud.length();
                            if (startPos2 != -1) {
                                ColorStateList hightlightColor;
                                hightlightColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
                                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, hightlightColor, null);
                                builderCloud.setSpan(highlightSpan, startPos2, endPos2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        holder.textContactFirstname.setText(builderCloud, TextView.BufferType.SPANNABLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    hightLightSearchedText(holder.textContactFirstname.getText().toString(), holder.textContactFirstname);
                }
            }
        }
        /*final String publicUrl = globalSearchType.getPublicProfileUrl();
        if(!StringUtils.isEmpty(publicUrl)){
            if(isRcpVerified == 1){
                holder.relativeRowMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PublicProfileOfGlobalContactActivity.class);
                        String publicProfileUrl =  globalSearchType.getPublicProfileUrl();
                        if(!StringUtils.isEmpty(publicProfileUrl))
                            intent.putExtra(AppConstants.EXTRA_GLOBAL_PUBLIC_PROFILE_URL,publicUrl);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
            }else {
                holder.relativeRowMain.setClickable(false);
            }
        }*/

       /* if(isRcpVerified ==1){
            holder.image3dotsCallLog.setVisibility(View.VISIBLE);
            holder.image3dotsCallLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String formattedNumber = globalSearchType.getMobileNumber();
                    if(!StringUtils.isEmpty(formattedNumber)){
                        String subString =  StringUtils.substring(formattedNumber,0,2);
                        if(subString.equalsIgnoreCase("91")){
                            formattedNumber =  "+" + formattedNumber;
                        }
                        ArrayList<String>arrayListForUnknownContact = new ArrayList<>(Arrays.asList("Call " + formattedNumber,
                                context.getString(R.string.add_to_contact),
                                context.getString(R.string.add_to_existing_contact)
                                , context.getString(R.string.send_sms),
                                context.getString(R.string.copy_phone_number)));

                        MaterialListDialog materialListDialog = new MaterialListDialog(context, arrayListForUnknownContact,
                                formattedNumber, 0, nameToPass, "", "");
                        if(!StringUtils.isEmpty(nameToPass))
                            materialListDialog.setDialogTitle(nameToPass);
                        else{
                            materialListDialog.setDialogTitle(formattedNumber);
                        }
                        materialListDialog.setCallingAdapter(GlobalSearchAdapter.this);
                        materialListDialog.showDialog();
                    }
                }
            });
        }else{
            holder.image3dotsCallLog.setVisibility(View.GONE);
        }*/

        if (isRcpVerified == 1) {
            if (!StringUtils.isEmpty(mobileNumber)) {
//                holder.image3dotsCallLog.setVisibility(View.VISIBLE);
                holder.image3dotsCallLog.setVisibility(View.GONE);
            } else {
                holder.image3dotsCallLog.setVisibility(View.GONE);
                holder.llNumber.setVisibility(View.GONE);
            }
        } else {
            holder.image3dotsCallLog.setVisibility(View.GONE);
        }

    }

    private void hightLightSearchedText(String originalText, TextView textView) {
        searchedText = searchedText.trim();
        int startPos = originalText.toLowerCase(Locale.US).indexOf(searchedText
                .toLowerCase(Locale.US));
        int endPos = startPos + searchedText.length();
        if (startPos != -1) {
            Spannable spannable = new SpannableString(originalText);
            ColorStateList blueColor;
            blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
            spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannable);
        } else {
            textView.setText(originalText);
        }
    }

    @Override
    public int getItemCount() {
        return globalSearchTypeArrayList.size();
    }


    public class GlobalSearchViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.image_3dots_call_log)
        ImageView image3dotsCallLog;
        @BindView(R.id.text_temp_number)
        TextView textTempNumber;
        @BindView(R.id.image_social_media)
        ImageView imageSocialMedia;
        @BindView(R.id.button_invite)
        Button buttonInvite;
        @BindView(R.id.text_contact_firstname)
        TextView textContactFirstname;
        /* @BindView(R.id.text_contact_lastname)
         TextView textContactLastname;*/
        @BindView(R.id.image_call_type)
        ImageView imageCallType;
        @BindView(R.id.textCount)
        TextView textCount;
        @BindView(R.id.text_contact_number)
        public TextView textContactNumber;
        @BindView(R.id.text_contact_date)
        TextView textContactDate;
        @BindView(R.id.text_sim_type)
        TextView textSimType;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;
        @BindView(R.id.linear_rating)
        LinearLayout linearRating;
        @BindView(R.id.linear_content_main)
        LinearLayout linearContentMain;
        @BindView(R.id.relative_row_main)
        RelativeLayout relativeRowMain;
        @BindView(R.id.ll_number)
        LinearLayout llNumber;


        GlobalSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            LayerDrawable stars = (LayerDrawable) ratingUser.getProgressDrawable();
            // Filled stars
            Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(context, R
                    .color.vivid_yellow));
            // half stars
            Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(context,
                    android.R.color.darker_gray));
            // Empty stars
            Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(context,
                    android.R.color.darker_gray));

            image3dotsCallLog.setVisibility(View.GONE);

        }
    }
}
