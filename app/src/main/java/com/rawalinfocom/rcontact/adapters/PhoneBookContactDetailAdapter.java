package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class PhoneBookContactDetailAdapter extends RecyclerView
        .Adapter<PhoneBookContactDetailAdapter.ContactDetailViewHolder> {


    private Context context;
    private ArrayList<Object> arrayListContactDetail;

    private ArrayList<Integer> arrayListSelectedContacts;

    private boolean isSelectedAll;

    public PhoneBookContactDetailAdapter(Context context, ArrayList<Object>
            arrayListContactDetail) {
        this.context = context;
        this.arrayListContactDetail = new ArrayList<>();
        this.arrayListSelectedContacts = new ArrayList<>();
        this.arrayListContactDetail.addAll(arrayListContactDetail);
    }

    @Override
    public ContactDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_contact_detail, parent, false);
        return new ContactDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ContactDetailViewHolder holder, final int position) {

        if (arrayListContactDetail.get(position) instanceof String) {
            holder.textSub.setVisibility(View.GONE);
            holder.textMain.setText((String) arrayListContactDetail.get(position));
        } else if (arrayListContactDetail.get(position) instanceof
                ProfileDataOperationPhoneNumber) {

            ProfileDataOperationPhoneNumber phoneNumber = (ProfileDataOperationPhoneNumber)
                    arrayListContactDetail.get(position);

            holder.textSub.setVisibility(View.VISIBLE);
            holder.textMain.setText(phoneNumber.getPhoneNumber());
            holder.textSub.setText(phoneNumber.getPhoneType());

        } else if (arrayListContactDetail.get(position) instanceof ProfileDataOperationEmail) {
            holder.textSub.setVisibility(View.VISIBLE);

            ProfileDataOperationEmail email = (ProfileDataOperationEmail)
                    arrayListContactDetail.get(position);

            holder.textSub.setVisibility(View.VISIBLE);
            holder.textMain.setText(email.getEmEmailId());
            holder.textSub.setText(email.getEmType());

        }

        if (!isSelectedAll) {
            holder.checkboxSelectContact.setChecked(false);
            if (arrayListSelectedContacts.contains(position)) {
                holder.checkboxSelectContact.setChecked(true);
            } else {
                holder.checkboxSelectContact.setChecked(false);
            }
        } else {
            holder.checkboxSelectContact.setChecked(true);
        }

        holder.checkboxSelectContact.setTag(position);

        holder.checkboxSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = (int) view.getTag();

                CheckBox checkBox = (CheckBox) view;
                if (pos == 0) {
                    isSelectAll(checkBox.isChecked());
                } else {
                    if (checkBox.isChecked()) {
                        if (!arrayListSelectedContacts.contains(pos)) {
                            arrayListSelectedContacts.add(pos);
                        }
                        if (arrayListSelectedContacts.size() == (arrayListContactDetail.size() -
                                1)) {
                            isSelectAll(true);
                        }
                    } else {
                        removeChecked(pos);
                    }
                }

            }
        });

//        holder.checkboxSelectContact.setOnCheckedChangeListener(new CompoundButton
//                .OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if ((Integer) buttonView.getTag() == 0) {
//                    isSelectAll(isChecked);
////                    if (!isChecked) {
////                        arrayListSelectedContacts.clear();
////                    }
//                } else {
//                    if (isChecked) {
//                        if (!arrayListSelectedContacts.contains(buttonView.getTag())) {
//                            arrayListSelectedContacts.add((Integer) buttonView.getTag());
//                        }
//                        if (arrayListSelectedContacts.size() == (arrayListContactDetail.size() -
//                                1)) {
//                            isSelectAll(true);
//                        }
//                    } else {
//                        removeChecked((Integer) buttonView.getTag());
//                    }
//                }
//                Log.i("onCheckedChanged", arrayListSelectedContacts.toString());
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return arrayListContactDetail.size();
    }

    private void isSelectAll(boolean checked) {
        isSelectedAll = checked;
        arrayListSelectedContacts.clear();
        if (checked) {
            for (int i = 1; i < getItemCount(); i++) {
                if (!arrayListSelectedContacts.contains(i)) {
                    arrayListSelectedContacts.add(i);
                }
            }
        }
        try {
            notifyDataSetChanged();
        } catch (Exception ignore) {
        }
    }

    private void removeChecked(int tagPosition) {
        isSelectedAll = false;
        arrayListSelectedContacts.remove((Integer) 0);
        if (arrayListSelectedContacts.contains(tagPosition)) {
            arrayListSelectedContacts.remove((Integer) tagPosition);
        }
        try {
            notifyDataSetChanged();
//            notifyItemChanged(0);
        } catch (Exception ignore) {
        }
    }

    public ArrayList<Integer> getArrayListSelectedContacts() {
        return arrayListSelectedContacts;
    }

    class ContactDetailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkbox_select_contact)
        CheckBox checkboxSelectContact;
        @BindView(R.id.text_main)
        TextView textMain;
        @BindView(R.id.text_sub)
        TextView textSub;
        @BindView(R.id.relative_row_contact_details)
        RelativeLayout relativeRowContactDetails;

        ContactDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textMain.setTypeface(Utils.typefaceSemiBold(context));
            textSub.setTypeface(Utils.typefaceRegular(context));

        }
    }

}
