package com.rawalinfocom.rcontact.notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.notifications.adapters.NotiRContactsAdapter;
import com.rawalinfocom.rcontact.notifications.model.NotiRContactsItem;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRContactsFragment extends BaseFragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerRContactsNoti;

    @Override
    public void getFragmentArguments() {

    }

    public static NotiRContactsFragment newInstance() {
        return new NotiRContactsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_rcontacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
        initData();
    }

    private void initData() {
        NotiRContactsItem item1 = new NotiRContactsItem("RContacts app update", "New app update is available for RContact app in Play Store.Kindly download latest update.", "11:15 PM");
        NotiRContactsItem item2 = new NotiRContactsItem("RContacts app update", "New app update is available for RContact app in Play Store.Kindly download latest update.", "11:15 PM");
        NotiRContactsItem item3 = new NotiRContactsItem("RContacts app update", "New app update is available for RContact app in Play Store.Kindly download latest update.", "11:15 PM");
        NotiRContactsItem item4 = new NotiRContactsItem("RContacts app update", "New app update is available for RContact app in Play Store.Kindly download latest update.", "11:15 PM");
        NotiRContactsItem item5 = new NotiRContactsItem("RContacts app update", "New app update is available for RContact app in Play Store.Kindly download latest update.", "11:15 PM");
        NotiRContactsItem item6 = new NotiRContactsItem("RContacts app update", "New app update is available for RContact app in Play Store.Kindly download latest update.", "11:15 PM");
        List<NotiRContactsItem> listTodayRequest = Arrays.asList(item1, item2, item3, item4, item5, item6);
        NotiRContactsAdapter todayRequestAdapter = new NotiRContactsAdapter(getActivity(), listTodayRequest);
        recyclerRContactsNoti.setAdapter(todayRequestAdapter);
        recyclerRContactsNoti.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void init() {

    }
}
