package com.rawalinfocom.rcontact.adapters;

import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.model.AppLanguage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class ShortByContactListAdapter extends RecyclerView.Adapter<ShortByContactListAdapter.CountryViewHolder> {

    private ArrayList<AppLanguage> appLanguageArrayList;
    private String selectedTypeName = "", selectedType = "";

    public ShortByContactListAdapter(ArrayList<AppLanguage> appLanguageArrayList) {
        this.appLanguageArrayList = appLanguageArrayList;
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app_language,
                parent, false);
        return new CountryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {

        AppLanguage appLanguage = appLanguageArrayList.get(position);

        holder.textLanguageName.setText(appLanguage.getLanguageName());

        if (appLanguage.getIsSelected()) {
            selectedTypeName = appLanguage.getLanguageName();
            selectedType = appLanguage.getLanguageType();
            holder.imgSelection.setImageResource(R.drawable.ic_check_circle_privacy_dialog_checked);
        } else
            holder.imgSelection.setImageResource(R.drawable.ic_check_circle_privacy_dialog);

        holder.rippleLanguageName.setTag(position);
        holder.rippleLanguageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSelected((int) v.getTag());
            }
        });
    }

    private void updateSelected(int position) {

        AppLanguage appLanguage = appLanguageArrayList.get(position);
        if (appLanguage.getIsSelected()) {
            appLanguageArrayList.set(position, new AppLanguage(appLanguage.getLanguageName(), appLanguage.getLanguageType(), true));
        }

        for (int i = 0; i < appLanguageArrayList.size(); i++) {
            AppLanguage appLanguage1 = appLanguageArrayList.get(i);
            if (i == position)
                appLanguageArrayList.set(position, new AppLanguage(appLanguage1.getLanguageName(), appLanguage.getLanguageType(), true));
            else
                appLanguageArrayList.set(i, new AppLanguage(appLanguage1.getLanguageName(), appLanguage.getLanguageType(), false));

        }
        notifyDataSetChanged();
    }

    public String getSelected() {
        return this.selectedTypeName;
    }

    public String getSelectedType() {
        return this.selectedType;
    }

    @Override
    public int getItemCount() {
        return appLanguageArrayList.size();
    }

    class CountryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_language_name)
        TextView textLanguageName;
        @BindView(R.id.checkboxLanguage)
        AppCompatRadioButton checkboxLanguage;
        @BindView(R.id.ripple_language_name)
        RippleView rippleLanguageName;
        @BindView(R.id.linear_root_item_country)
        LinearLayout linearRootItemCountry;
        @BindView(R.id.imgSelection)
        ImageView imgSelection;

        CountryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imgSelection.setVisibility(View.VISIBLE);
        }
    }
}
