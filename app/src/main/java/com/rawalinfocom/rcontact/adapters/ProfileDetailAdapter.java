package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 22/12/16.
 */

public class ProfileDetailAdapter extends RecyclerView.Adapter<ProfileDetailAdapter
        .ProfileDetailViewHolder> {

    private Context context;
    private ArrayList<Object> arrayList;

    private int profileDetailType;

    private int colorBlack, colorPineGreen;

    public ProfileDetailAdapter(Context context, ArrayList<Object> arrayList, int
            profileDetailType) {
        this.context = context;
        this.profileDetailType = profileDetailType;
        this.arrayList = arrayList;

        colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);

    }

    @Override
    public ProfileDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile_detail,
                parent, false);
        return new ProfileDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProfileDetailViewHolder holder, int position) {

        switch (profileDetailType) {
            case AppConstants.PHONE_NUMBER:
                displayPhoneNumber(holder, position);
                break;

            case AppConstants.EMAIL:
                displayEmail(holder, position);
                break;

            case AppConstants.WEBSITE:
                displayWebsite(holder, position);
                break;

            case AppConstants.ADDRESS:
                displayAddress(holder, position);
                break;

            case AppConstants.IM_ACCOUNT:
                displayImAccount(holder, position);
                break;

            case AppConstants.EVENT:
                displayEvent(holder, position);
                break;

            case AppConstants.GENDER:
                displayGender(holder, position);
                break;
        }

    }

    private void displayPhoneNumber(ProfileDetailViewHolder holder, int position) {
        ProfileDataOperationPhoneNumber phoneNumber = (ProfileDataOperationPhoneNumber)
                arrayList.get(position);
        String number = phoneNumber.getPhoneNumber();
        holder.textSub.setText(phoneNumber.getPhoneType());
        holder.textSub.setVisibility(View.VISIBLE);

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(context, "Copied Number", ((TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(context, ((ProfileDetailActivity) context)
                        .getRelativeRootProfileDetail(), "Number copied to Clipboard");
                return false;
            }
        });

        int pbRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(phoneNumber.getPbRcpType(),
                String.valueOf(context.getResources().getInteger(R.integer
                        .rcp_type_secondary))));

        if (pbRcpType == context.getResources().getInteger(R.integer.rcp_type_primary)) {
            holder.textMain.setText(number + " ◊");
            holder.textMain.setTextColor(colorPineGreen);
        } else if (pbRcpType == context.getResources().getInteger(R.integer.rcp_type_secondary)) {
            holder.textMain.setText(number);
            holder.textMain.setTextColor(colorPineGreen);
        } else {
            holder.textMain.setText(number);
            holder.textMain.setTextColor(colorBlack);
        }

    }

    private void displayEmail(final ProfileDetailViewHolder holder, int position) {
        ProfileDataOperationEmail email = (ProfileDataOperationEmail) arrayList.get(position);
        String emailId = email.getEmEmailId();
        holder.textSub.setText(email.getEmType());
        holder.textSub.setVisibility(View.VISIBLE);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" +
                        holder.textMain.getText()));
                context.startActivity(Intent.createChooser(emailIntent, "Send Email Via:"));
            }
        });

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(context, "Copied Email", ((TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(context, ((ProfileDetailActivity) context)
                        .getRelativeRootProfileDetail(), "Email copied to Clipboard");
                return false;
            }
        });

        int emRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(email.getEmRcpType(), String
                .valueOf(context.getResources().getInteger(R.integer.rcp_type_secondary))));

        if (emRcpType == context.getResources().getInteger(R.integer.rcp_type_primary)) {
            holder.textMain.setText(emailId + " ◊");
            holder.textMain.setTextColor(colorPineGreen);
        } else if (emRcpType == context.getResources().getInteger(R.integer
                .rcp_type_secondary)) {
            holder.textMain.setText(emailId);
            holder.textMain.setTextColor(colorPineGreen);
        } else {
            holder.textMain.setText(emailId);
            holder.textMain.setTextColor(colorBlack);
        }


    }

    private void displayWebsite(final ProfileDetailViewHolder holder, int position) {
//        String website = (String) arrayList.get(position);
        ProfileDataOperationWebAddress webAddress = (ProfileDataOperationWebAddress) arrayList
                .get(position);

        holder.textSub.setVisibility(View.GONE);
/*
        if (website.contains(";")) {
            String[] websiteSplit = website.split(";");
            website = websiteSplit[0];
            rcpType = Integer.parseInt(websiteSplit[1]);
        }*/

        holder.textMain.setText(webAddress.getWebAddress());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = holder.textMain.getText().toString();
                if (!StringUtils.startsWithIgnoreCase(url, "http://") && !StringUtils
                        .startsWithIgnoreCase(url, "https://")) {
                    url = "http://" + url;
                }
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(context, "Copied Website", ((TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(context, ((ProfileDetailActivity) context)
                        .getRelativeRootProfileDetail(), "Website copied to Clipboard");
                return false;
            }
        });

        int rcpType = Integer.parseInt(StringUtils.defaultIfEmpty(webAddress.getWebRcpType(),
                String.valueOf(context.getResources().getInteger(R.integer
                        .rcp_type_secondary))));

        if (rcpType == context.getResources().getInteger(R.integer
                .rcp_type_local_phone_book)) {
            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);
        }
    }

    private void displayAddress(final ProfileDetailViewHolder holder, int position) {
        ProfileDataOperationAddress address = (ProfileDataOperationAddress) arrayList.get(position);
        holder.textMain.setText(address.getFormattedAddress());
        holder.textSub.setText(address.getAddressType());
        holder.textSub.setVisibility(View.VISIBLE);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + holder.textMain.getText()));
                context.startActivity(intent);
            }
        });

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(context, "Copied Address", ((TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(context, ((ProfileDetailActivity) context)
                        .getRelativeRootProfileDetail(), "Address copied to Clipboard");
                return false;
            }
        });

        int addressRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(address.getRcpType(),
                String.valueOf(context.getResources().getInteger(R.integer
                        .rcp_type_secondary))));

        if (addressRcpType == context.getResources().getInteger(R.integer
                .rcp_type_local_phone_book)) {
            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);
        }

    }

    private void displayImAccount(ProfileDetailViewHolder holder, int position) {
        final ProfileDataOperationImAccount imAccount = (ProfileDataOperationImAccount) arrayList
                .get
                (position);
        holder.textMain.setText(imAccount.getIMAccountDetails());
        holder.textSub.setText(imAccount.getIMAccountProtocol());

        holder.textSub.setVisibility(View.GONE);

        int imRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(imAccount.getIMRcpType(),
                String.valueOf(context.getResources().getInteger(R.integer
                        .rcp_type_secondary))));

        if (imRcpType == context.getResources().getInteger(R.integer.rcp_type_local_phone_book)) {
            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);
        }

        /*holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imAccount.getIMAccountProtocol())
            }
        });*/

    }

    private void displayEvent(ProfileDetailViewHolder holder, int position) {
        ProfileDataOperationEvent event = (ProfileDataOperationEvent) arrayList.get(position);

        String convertedDate;
        if (StringUtils.startsWith(event.getEventDate(), "--")) {
            convertedDate = Utils.convertDateFormat(event.getEventDate(), "--MM-dd", "dd'th' MMM");
        } else {
            convertedDate = Utils.convertDateFormat(event.getEventDate(), "yyyy-MM-dd", "dd'th' " +
                    "MMM, yyyy");
        }

        holder.textMain.setText(convertedDate);
        holder.textSub.setText(event.getEventType());
        holder.textSub.setVisibility(View.VISIBLE);

        int eventRcType = Integer.parseInt(StringUtils.defaultIfEmpty(event.getEventRcType(),
                String.valueOf(context.getResources().getInteger(R.integer
                        .rcp_type_secondary))));

        if (eventRcType == context.getResources().getInteger(R.integer.rcp_type_cloud_phone_book)) {
            holder.textMain.setTextColor(colorPineGreen);
        } else {
            holder.textMain.setTextColor(colorBlack);
        }
    }

    private void displayGender(ProfileDetailViewHolder holder, int position) {
        String gender = (String) arrayList.get(position);
        holder.textMain.setText(gender);
        holder.textSub.setVisibility(View.GONE);

        holder.textMain.setTextColor(colorBlack);
    }

    public ArrayList<Object> getDetailList() {
        return arrayList;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ProfileDetailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_main)
        public TextView textMain;
        @BindView(R.id.text_sub)
        TextView textSub;
        @BindView(R.id.image_view)
        ImageView imageView;

        ProfileDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textMain.setTypeface(Utils.typefaceRegular(context));
            textSub.setTypeface(Utils.typefaceRegular(context));

        }
    }

}
