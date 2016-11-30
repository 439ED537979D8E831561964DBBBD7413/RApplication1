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
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileData;

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

    private int defaultNameColor, defaultNumberColor;

    public AllContactListAdapter(Context context, ArrayList<ProfileData> arrayListUserContact) {
        this.context = context;
        this.arrayListUserContact = arrayListUserContact;
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

        ArrayList<String> arrayListDbMobileNumbers = tableProfileMobileMapping
                .getProfileMobileMappingFromNumber(arrayListMobileNumbers.toArray(new
                        String[arrayListMobileNumbers.size()]));

        String displayNumber;
        boolean isRcp;
        if (arrayListDbMobileNumbers.size() > 0) {
            displayNumber = arrayListDbMobileNumbers.get(0);
            isRcp = true;
        } else {
            displayNumber = profileData.getOperation().get(0).getPbPhoneNumber().get
                    (0).getPhoneNumber();
            isRcp = false;
        }

        if (isRcp) {
            holder.textContactNumber.setTextColor(ContextCompat.getColor(context, R.color
                    .colorPrimary));
        } else {
            holder.textContactNumber.setTextColor(defaultNumberColor);
        }

        holder.textContactName.setText(profileData.getOperation().get(0).getPbNameFirst());
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
        @BindView(R.id.text_contact_number)
        TextView textContactNumber;

        AllContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceRegular(context));
            textContactNumber.setTypeface(Utils.typefaceRegular(context));

            defaultNameColor = textContactName.getTextColors().getDefaultColor();
            defaultNumberColor = textContactNumber.getTextColors().getDefaultColor();

        }
    }

}
