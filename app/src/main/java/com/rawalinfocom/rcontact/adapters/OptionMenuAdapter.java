package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 17/05/17.
 */

public class OptionMenuAdapter extends RecyclerView.Adapter<OptionMenuAdapter
        .OptionMenuViewHolder> {

    private Context context;
    private ArrayList<String> arrayListMenu;

    public OptionMenuAdapter(Context context, ArrayList<String> arrayListMenu) {
        this.context = context;
        this.arrayListMenu = arrayListMenu;
    }

    @Override
    public OptionMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_option_menu,
                parent, false);
        return new OptionMenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OptionMenuViewHolder holder, int position) {
        holder.textOptionMenu.setText(arrayListMenu.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListMenu.size();
    }


    class OptionMenuViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_option_menu)
        TextView textOptionMenu;
     /*   @BindView(R.id.ripple_option_menu)
        RippleView rippleOptionMenu;*/

        OptionMenuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textOptionMenu.setTypeface(Utils.typefaceRegular(context));

        }
    }

}
