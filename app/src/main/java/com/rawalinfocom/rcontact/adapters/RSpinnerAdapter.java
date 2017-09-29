package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rawalinfocom.rcontact.helper.Utils;

import java.util.List;

/**
 * Created by Monal on 28/09/17.
 */

public class RSpinnerAdapter extends ArrayAdapter<String> {

    Context context;
    int headerColor, dropDownColor;

    public RSpinnerAdapter(Context context, int resource, List<String> items, int headerColor, int
            dropDownColor) {
        super(context, resource, items);
        this.context = context;
        this.headerColor = headerColor;
        this.dropDownColor = dropDownColor;
    }

    // Affects default (closed) state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(Utils.typefaceRegular(context));
        view.setTextColor(headerColor);
        return view;
    }

    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(Utils.typefaceRegular(context));
        view.setTextColor(dropDownColor);
        return view;
    }

}
