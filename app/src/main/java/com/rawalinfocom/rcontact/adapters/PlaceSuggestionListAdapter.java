package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.GetGoogleLocationResultObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 05/06/17.
 */

public class PlaceSuggestionListAdapter extends RecyclerView.Adapter<PlaceSuggestionListAdapter
        .SuggestionViewHolder> {

    private Context context;
    private ArrayList<GetGoogleLocationResultObject> arrayListSuggestion;

    public PlaceSuggestionListAdapter(Context context, ArrayList<GetGoogleLocationResultObject>
            arrayListSuggestion) {
        this.context = context;
        this.arrayListSuggestion = arrayListSuggestion;
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_place_suggestion, parent, false);
        return new SuggestionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {

        GetGoogleLocationResultObject googleLocation = arrayListSuggestion.get(position);
        holder.textSuggestion.setText(googleLocation.getDescription());

    }

    @Override
    public int getItemCount() {
        return arrayListSuggestion.size();
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_suggestion)
        TextView textSuggestion;
        @BindView(R.id.text_image_map_marker)
        TextView textImageMapMarker;

        SuggestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textSuggestion.setTypeface(Utils.typefaceRegular(context));
            textImageMapMarker.setTypeface(Utils.typefaceIcons(context));
        }
    }
}
