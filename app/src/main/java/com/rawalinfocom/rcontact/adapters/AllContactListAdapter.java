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
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class AllContactListAdapter extends RecyclerView.Adapter<AllContactListAdapter
        .AllContactViewHolder> {

    private Context context;
    private ArrayList<ProfileData> arrayListUserContact;

    int colorBlack, colorPineGreen;

    public AllContactListAdapter(Context context, ArrayList<ProfileData> arrayListUserContact) {
        this.context = context;
        this.arrayListUserContact = arrayListUserContact;

        colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);
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

            isRcp = true;

        } else {
            displayNumber = profileData.getOperation().get(0).getPbPhoneNumber().get
                    (0).getPhoneNumber();
            displayName = "";
            isRcp = false;
        }

        if (isRcp) {
            holder.textContactNumber.setTextColor(colorPineGreen);
        } else {
            holder.textContactNumber.setTextColor(colorBlack);
        }

        holder.textContactName.setText(profileData.getOperation().get(0).getPbNameFirst() + " " +
                profileData.getOperation().get(0).getPbNameLast());
        holder.textCloudContactName.setText(displayName);
        holder.textContactNumber.setText(displayNumber);


    }

    @Override
    public int getItemCount() {
        return arrayListUserContact.size();
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
