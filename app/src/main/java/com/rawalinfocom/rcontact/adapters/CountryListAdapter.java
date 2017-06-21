package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.Country;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.CountryViewHolder> {

    private Context context;
    private ArrayList<Country> arrayListCountry;
    private ArrayList<Country> arrayListTempCountry;

    public CountryListAdapter(Context context, ArrayList<Country> arrayListCountry) {
        this.context = context;
        this.arrayListCountry = arrayListCountry;
        arrayListTempCountry = new ArrayList<>();
        arrayListTempCountry.addAll(arrayListCountry);
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_country,
                parent, false);
        return new CountryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {

        Country country = arrayListCountry.get(position);

        holder.ripple_country_name.setTag(position);

        holder.textCountryName.setText(country.getCountryName() + " (" + country
                .getCountryCodeNumber() + ")");

        holder.ripple_country_name.setOnRippleCompleteListener(new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent();
                intent.putExtra(AppConstants.EXTRA_OBJECT_COUNTRY, arrayListCountry.get((Integer)
                        rippleView.getTag()));
                ((Activity) context).setResult(AppConstants.REQUEST_CODE_COUNTRY_REGISTRATION,
                        intent);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListCountry.size();
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        arrayListCountry.clear();
        if (charText.length() == 0) {
            arrayListCountry.addAll(arrayListTempCountry);
        } else {
            for (Country country : arrayListTempCountry) {
                if (country.getCountryName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayListCountry.add(country);
                }
            }
        }
        notifyDataSetChanged();
    }

    class CountryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_country_name)
        TextView textCountryName;
        @BindView(R.id.linear_root_item_country)
        LinearLayout linearRootItemCountry;
        @BindView(R.id.ripple_country_name)
        RippleView ripple_country_name;

        CountryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textCountryName.setTypeface(Utils.typefaceRegular(context));
        }
    }
}
