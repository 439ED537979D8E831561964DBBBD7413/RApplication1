package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.Country;
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

public class AllContactListAdapter extends RecyclerView.Adapter<AllContactListAdapter
        .AllContactViewHolder> {

    private Context context;
    /* phone book contacts */
    private ArrayList<ProfileData> arrayListUserContact;

    private int colorBlack, colorPineGreen;
    String defaultCountryCode;

    public AllContactListAdapter(Context context, ArrayList<ProfileData> arrayListUserContact) {
        this.context = context;
        this.arrayListUserContact = arrayListUserContact;

        colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);

        Country country = (Country) Utils.getObjectPreference(context, AppConstants
                .PREF_SELECTED_COUNTRY_OBJECT, Country.class);
        if (country != null) {
            defaultCountryCode = country.getCountryCodeNumber();
        }
    }

    @Override
    public AllContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_all_contacts,
                parent, false);
        return new AllContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AllContactViewHolder holder, int position) {

        ProfileData profileData = arrayListUserContact.get(position);


        if (profileData.getOperation().get(0).getPbPhoneNumber().size() > 0) {
            displayNumberName(holder, profileData);
        } else if (profileData.getOperation().get(0).getPbEmailId().size() > 0) {
            displayEmailName(holder, profileData, null);
        }

    }

    @Override
    public int getItemCount() {
        return arrayListUserContact.size();
    }

    private void displayNumberName(AllContactViewHolder holder, ProfileData profileData) {
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
            String displayNamePmId = arrayListDbMobileNumbers.get(0).getMpmCloudPmId();

            if (arrayListDbMobileNumbers.size() == 1) {
                TableProfileMaster tableProfileMaster = new TableProfileMaster(((BaseActivity)
                        context).databaseHandler);
                UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                        .parseInt(displayNamePmId));

                displayName = ((userProfile.getPmFirstName().length() > 0 || userProfile
                        .getPmLastName().length() > 0) ? " (" + userProfile
                        .getPmFirstName() + " " + userProfile
                        .getPmLastName() + ")" : "");

            } else {
                displayName = " (" + arrayListDbMobileNumbers.size() + "RC)";
            }

            holder.textContactNumber.setTextColor(colorPineGreen);
            isRcp = true;
        } else {
            displayNumber = profileData.getOperation().get(0).getPbPhoneNumber().get(0)
                    .getPhoneNumber();
            displayName = "";
            holder.textContactNumber.setTextColor(colorBlack);
            isRcp = false;
        }

        if (!StringUtils.startsWith(displayNumber, "+")) {
            if (StringUtils.startsWith(displayNumber, "0")) {
                displayNumber = defaultCountryCode + StringUtils.substring(displayNumber, 1);
            } else {
                displayNumber = defaultCountryCode + displayNumber;
            }
        }

        holder.textContactName.setText(profileData.getOperation().get(0).getPbNameFirst() + "" +
                " " + profileData.getOperation().get(0).getPbNameLast());
        holder.textCloudContactName.setText(displayName);
        holder.textContactNumber.setText(displayNumber);

        if (!isRcp) {
            displayEmailName(holder, profileData, displayNumber);
        }

    }

    private void displayEmailName(AllContactViewHolder holder, ProfileData profileData, String
            displayNumber) {
        TableProfileEmailMapping tableProfileEmailMapping = new TableProfileEmailMapping((
                (BaseActivity) context).databaseHandler);

        ArrayList<String> arrayListEmails = new ArrayList<>();
        for (int i = 0; i < profileData.getOperation().get(0).getPbEmailId().size(); i++) {
            arrayListEmails.add(profileData.getOperation().get(0).getPbEmailId().get
                    (i).getEmEmailId());
        }

        ArrayList<ProfileEmailMapping> arrayListDbEmailIds = tableProfileEmailMapping
                .getProfileEmailMappingFromEmailId(arrayListEmails.toArray(new
                        String[arrayListEmails.size()]));

        String displayEmailId, displayName;
        boolean isRcp;
        if (arrayListDbEmailIds.size() > 0) {
            displayEmailId = arrayListDbEmailIds.get(0).getEpmEmailId();
            String displayNamePmId = arrayListDbEmailIds.get(0).getEpmCloudPmId();

            if (arrayListDbEmailIds.size() == 1) {
                TableProfileMaster tableProfileMaster = new TableProfileMaster(((BaseActivity)
                        context).databaseHandler);
                UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                        .parseInt(displayNamePmId));

                displayName = ((userProfile.getPmFirstName().length() > 0 || userProfile
                        .getPmLastName().length() > 0) ? " (" + userProfile
                        .getPmFirstName() + " " + userProfile
                        .getPmLastName() + ")" : "");

            } else {
                displayName = " (" + arrayListDbEmailIds.size() + "RC)";
            }

            holder.textContactNumber.setTextColor(colorPineGreen);
            isRcp = true;

        } else {
            displayEmailId = profileData.getOperation().get(0).getPbEmailId().get(0)
                    .getEmEmailId();
            displayName = "";
            holder.textContactNumber.setTextColor(colorBlack);
            isRcp = false;
            /* Display mobile number if Email Id is not rcp */
            if (displayNumber != null) {
                displayEmailId = displayNumber;
            }
        }

        holder.textContactName.setText(profileData.getOperation().get(0).getPbNameFirst() + "" +
                " " + profileData.getOperation().get(0).getPbNameLast());
        holder.textCloudContactName.setText(displayName);
        holder.textContactNumber.setText(displayEmailId);
    }

    class AllContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.text_contact_name)
        TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.text_contact_number)
        TextView textContactNumber;

        AllContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceSemiBold(context));
            textCloudContactName.setTypeface(Utils.typefaceSemiBold(context));
            textContactNumber.setTypeface(Utils.typefaceRegular(context));

            textContactName.setTextColor(colorBlack);
            textContactNumber.setTextColor(colorBlack);

            textCloudContactName.setTextColor(colorPineGreen);

        }
    }

}
