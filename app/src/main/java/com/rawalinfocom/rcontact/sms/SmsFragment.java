package com.rawalinfocom.rcontact.sms;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;

public class SmsFragment extends BaseFragment {

    public SmsFragment() {
        // Required empty public constructor
    }


    public static SmsFragment newInstance() {
        return new SmsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
