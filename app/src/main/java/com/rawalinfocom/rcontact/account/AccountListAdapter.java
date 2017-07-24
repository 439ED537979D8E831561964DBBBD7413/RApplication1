package com.rawalinfocom.rcontact.account;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.AccountType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 24/07/17.
 */

public class AccountListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<AccountType> accountItemList;

    public AccountListAdapter(Context context, ArrayList<AccountType> accountList) {
        this.context = context;
        this.accountItemList = accountList;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user_account,
                parent, false);
        return new AccountViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder contactViewHolder, int position) {
        AccountViewHolder holder = (AccountViewHolder) contactViewHolder;
        AccountType accountType  = accountItemList.get(position);
        String itemName =  accountType.getItemName();
        String itemImage = accountType.getItemIcon();
        if(!TextUtils.isEmpty(itemName))
            holder.textViewName.setText(itemName);
        else
            holder.textViewName.setText("");

        if(!TextUtils.isEmpty(itemName))
            holder.textViewIcon.setText(itemImage);
        else
            holder.textViewIcon.setText("");
    }


    @Override
    public int getItemCount() {
        return accountItemList.size();
    }

    public class AccountViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView_icon)
        TextView textViewIcon;
        @BindView(R.id.textView_name)
        TextView textViewName;
        @BindView(R.id.ll_item)
        LinearLayout llItem;
        @BindView(R.id.view)
        View view;
        AccountViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            textViewName.setTypeface(Utils.typefaceRegular(context));
            textViewIcon.setTypeface(Utils.typefaceIcons(context));

        }
    }
}
