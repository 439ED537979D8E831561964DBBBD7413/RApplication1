package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.UserContact;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class AllContactListAdapter extends RecyclerView.Adapter<AllContactListAdapter
        .AllContactViewHolder> {

    Context context;
    ArrayList<UserContact> arrayListUserContact;

    public AllContactListAdapter(Context context, ArrayList<UserContact> arrayListUserContact) {
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

        UserContact userContact = arrayListUserContact.get(position);

        holder.textContactName.setText(userContact.getUserProfile().getPmFirstName());
      /*  holder.textContactNumber.setText(userContact.getArrayListMobileNumber().get(position)
                .getMnmMobileNumber());*/


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

        }
    }

}
