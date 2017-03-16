package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 09/03/16.
 */

public class EditProfileAdapter extends RecyclerView.Adapter<EditProfileAdapter
        .EditProfileViewHolder> {

    private Context context;
    private ArrayList<Object> arrayListProfileObject;
    private int editProfileType;

    ArrayAdapter<String> spinnerAdapter;

    public EditProfileAdapter(Context context, ArrayList<Object> arrayListProfileObject, int
            editProfileType) {
        this.context = context;
        this.arrayListProfileObject = arrayListProfileObject;
        this.editProfileType = editProfileType;
    }

    @Override
    public EditProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_edit_profile,
                parent, false);
        return new EditProfileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EditProfileViewHolder holder, int position) {

        holder.relativeRowEditProfile.setTag(position);

        switch (editProfileType) {
            case AppConstants.PHONE_NUMBER:
                displayPhoneNumber(holder, position);
                break;

            case AppConstants.EMAIL:
                displayEmail(holder, position);
                break;

            case AppConstants.WEBSITE:
//                displayWebsite(holder, position);
                break;

            case AppConstants.ADDRESS:
//                displayAddress(holder, position);
                break;

            case AppConstants.IM_ACCOUNT:
//                displayImAccount(holder, position);
                break;

            case AppConstants.EVENT:
//                displayEvent(holder, position);
                break;

            case AppConstants.GENDER:
//                displayGender(holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return arrayListProfileObject.size();
    }

    private void displayPhoneNumber(final EditProfileViewHolder holder, final int position) {

        spinnerAdapter = new ArrayAdapter<>(context, R.layout.list_item_spinner, context
                .getResources().getStringArray(R.array.types_phone_number));
        holder.spinnerType.setAdapter(spinnerAdapter);

        holder.inputValue.setInputType(InputType.TYPE_CLASS_PHONE);

        if (!Utils.isArraylistNullOrEmpty(arrayListProfileObject)) {
            ProfileDataOperationPhoneNumber phoneNumber = (ProfileDataOperationPhoneNumber)
                    arrayListProfileObject.get(position);

            holder.inputValue.setText(phoneNumber.getPhoneNumber());
        }

        holder.textImageCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*arrayListProfileObject.remove((int) v.getTag());
                notifyDataSetChanged();*/
//                arrayListProfileObject.remove(position);
                notifyItemRemoved((int) v.getTag());
            }
        });

    }

    private void displayEmail(EditProfileViewHolder holder, final int position) {

        spinnerAdapter = new ArrayAdapter<>(context, R.layout.list_item_spinner, context
                .getResources().getStringArray(R.array.types_email_address));
        holder.spinnerType.setAdapter(spinnerAdapter);

        holder.inputValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        if (!Utils.isArraylistNullOrEmpty(arrayListProfileObject)) {
            ProfileDataOperationEmail email = (ProfileDataOperationEmail) arrayListProfileObject
                    .get(position);

            holder.inputValue.setText(email.getEmEmailId());
        }

        holder.textImageCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayListProfileObject.remove(position);
                notifyItemRemoved(position);
//                notifyDataSetChanged();
            }
        });

    }

    public ArrayList<Object> getArrayListProfileObject() {
        return arrayListProfileObject;
    }

    public void setArrayListProfileObject(ArrayList<Object> arrayListProfileObject) {
        this.arrayListProfileObject = arrayListProfileObject;
    }

    class EditProfileViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_image_cross)
        TextView textImageCross;
        @BindView(R.id.spinner_type)
        Spinner spinnerType;
        @BindView(R.id.input_value)
        EditText inputValue;
        @BindView(R.id.relative_row_edit_profile)
        RelativeLayout relativeRowEditProfile;

        EditProfileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textImageCross.setTypeface(Utils.typefaceIcons(context));
            inputValue.setTypeface(Utils.typefaceRegular(context));

        }
    }

}
