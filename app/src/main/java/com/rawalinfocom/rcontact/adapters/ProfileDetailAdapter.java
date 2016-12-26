package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 22/12/16.
 */

public class ProfileDetailAdapter extends RecyclerView.Adapter<ProfileDetailAdapter
        .ProfileDetailViewHolder> {

    private final int PHONE_NUMBER = 0;
    private final int EMAIL = 1;
    private final int WEBSITE = 2;
    private final int ADDRESS = 3;
    private final int IM_ACCOUNT = 4;
    private final int EVENT = 5;
    private final int GENDER = 6;

    private Context context;
    private ArrayList<Object> arrayList;

    private int profileDetailType;

    public ProfileDetailAdapter(Context context, ArrayList<Object> arrayList, int
            profileDetailType) {
        this.context = context;
        this.profileDetailType = profileDetailType;
        this.arrayList = arrayList;
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
            case PHONE_NUMBER:
                displayPhoneNumber(holder, position);
                break;

            case EMAIL:
                displayEmail(holder, position);
                break;

            case WEBSITE:
                displayWebsite(holder, position);
                break;

            case ADDRESS:
                displayAddress(holder, position);
                break;

            case IM_ACCOUNT:
                displayImAccount(holder, position);
                break;

            case EVENT:
                displayEvent(holder, position);
                break;

            case GENDER:
                displayGender(holder, position);
                break;
        }

    }

    private void displayPhoneNumber(ProfileDetailViewHolder holder, int position) {
        ProfileDataOperationPhoneNumber phoneNumber = (ProfileDataOperationPhoneNumber)
                arrayList.get(position);
        holder.textMain.setText(phoneNumber.getPhoneNumber());
        holder.textSub.setText(phoneNumber.getPhoneType());
        holder.textSub.setVisibility(View.VISIBLE);

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(context, "Copied Number", ((TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackbar(context, ((ProfileDetailActivity) context)
                        .getRelativeRootProfileDetail(), "Number copied to Clipboard");
                return false;
            }
        });

    }

    private void displayEmail(final ProfileDetailViewHolder holder, int position) {
        ProfileDataOperationEmail email = (ProfileDataOperationEmail) arrayList.get(position);
        holder.textMain.setText(email.getEmEmailId());
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
                Utils.showSuccessSnackbar(context, ((ProfileDetailActivity) context)
                        .getRelativeRootProfileDetail(), "Email copied to Clipboard");
                return false;
            }
        });

    }

    private void displayWebsite(final ProfileDetailViewHolder holder, int position) {
        String website = (String) arrayList.get(position);
        holder.textMain.setText(website);
        holder.textSub.setVisibility(View.GONE);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(holder.textMain.getText().toString()));
                context.startActivity(intent);
            }
        });

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(context, "Copied Website", ((TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackbar(context, ((ProfileDetailActivity) context)
                        .getRelativeRootProfileDetail(), "Website copied to Clipboard");
                return false;
            }
        });
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
                Utils.showSuccessSnackbar(context, ((ProfileDetailActivity) context)
                        .getRelativeRootProfileDetail(), "Address copied to Clipboard");
                return false;
            }
        });
    }

    private void displayImAccount(ProfileDetailViewHolder holder, int position) {
        ProfileDataOperationImAccount imAccount = (ProfileDataOperationImAccount) arrayList.get
                (position);
        holder.textMain.setText(imAccount.getIMAccountDetails());
        holder.textSub.setText(imAccount.getIMAccountType());
    }

    private void displayEvent(ProfileDetailViewHolder holder, int position) {
        ProfileDataOperationEvent event = (ProfileDataOperationEvent) arrayList.get(position);

        String convertedDate = Utils.convertDateFormat(event.getEventDate(), "yyyy-MM-dd",
                "dd'th' MMM, yyyy");

        holder.textMain.setText(convertedDate);
        holder.textSub.setText(event.getEventType());
        holder.textSub.setVisibility(View.VISIBLE);
    }

    private void displayGender(ProfileDetailViewHolder holder, int position) {
        String gender = (String) arrayList.get(position);
        holder.textMain.setText(gender);
        holder.textSub.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ProfileDetailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_main)
        TextView textMain;
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
