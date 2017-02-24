package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 24/02/17.
 */

public class MaterialListAdapter extends RecyclerView.Adapter<MaterialListAdapter.MaterialViewHolder> {



    private Context context;
    private ArrayList<String> arrayListString;

    public MaterialListAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayListString = arrayList;
    }


    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dialog_for_call_log,
                parent, false);
        return new MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return arrayListString.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_item_value)
        TextView textItemValue;

        MaterialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
