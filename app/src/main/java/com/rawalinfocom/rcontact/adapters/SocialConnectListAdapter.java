package com.rawalinfocom.rcontact.adapters;

import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class SocialConnectListAdapter extends RecyclerView.Adapter<SocialConnectListAdapter.CountryViewHolder> {

    private ArrayList<String> socialConnectArrayList;
    private onClickListener clickListener;

    public SocialConnectListAdapter(ArrayList<String> socialConnectArrayList, onClickListener clickListener) {
        this.socialConnectArrayList = socialConnectArrayList;
        this.clickListener = clickListener;
    }

    public interface onClickListener {
        void onClick(String socialName);
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app_language,
                parent, false);
        return new CountryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        holder.textSocialName.setText(socialConnectArrayList.get(position));

        holder.textSocialName.setTag(position);
        holder.textSocialName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onClick(socialConnectArrayList.get((int) view.getTag()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return socialConnectArrayList.size();
    }

    class CountryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_language_name)
        TextView textSocialName;
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

            imgSelection.setVisibility(View.GONE);
            checkboxLanguage.setVisibility(View.GONE);

            textSocialName.setGravity(Gravity.CENTER);
        }
    }
}
