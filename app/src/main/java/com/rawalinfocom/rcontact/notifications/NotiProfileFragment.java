package com.rawalinfocom.rcontact.notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.NotiRequestAdapter;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.NotiRequestItem;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiProfileFragment extends BaseFragment {

    @BindView(R.id.search_view_profile)
    SearchView searchViewEvents;

    @BindView(R.id.header1)
    TextView textTodayTitle;

    @BindView(R.id.header1_icon)
    ImageView headerTodayIcon;

    @BindView(R.id.relative_header1)
    RelativeLayout headerTodayLayout;

    @BindView(R.id.recycler_view1)
    RecyclerView recyclerTodayRequests;

    @BindView(R.id.text_header2)
    TextView textPastTitle;

    @BindView(R.id.header2_icon)
    ImageView headerPastIcon;

    @BindView(R.id.relative_header2)
    RelativeLayout headerPastLayout;

    @BindView(R.id.recycler_view2)
    RecyclerView recyclerPastRequests;

    @BindView(R.id.text_view_more)
    TextView textViewMore;

    @BindView(R.id.tab_profile)
    TabLayout tabProfile;

    @Override
    public void getFragmentArguments() {

    }

    public static NotiProfileFragment newInstance() {
        return new NotiProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_profile, container, false);
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
        NotiRequestItem item1 = new NotiRequestItem("Aakar Jain", "Aakar Jain Requested for your Home Adress", "11:15 PM");
        NotiRequestItem item2 = new NotiRequestItem("Aakar Jain", "Aakar Jain Requested for your Mobile No", "11:15 PM");
        NotiRequestItem item3 = new NotiRequestItem("Aakar Jain", "Aakar Jain Requested for your Email", "11:15 PM");
        NotiRequestItem item4 = new NotiRequestItem("Aakar Kaushik", "Aakar Jain Requested for your HomeAdress", "11:15 PM");
        NotiRequestItem item5 = new NotiRequestItem("Aakar Kaushik", "Aakar Jain Requested for your Mobile no", "11:15 PM");
        NotiRequestItem item6 = new NotiRequestItem("Aakar Kaushik", "Aakar Jain Requested for your Email", "11:15 PM");
        List<NotiRequestItem> listTodayRequest = Arrays.asList(item1, item2, item3, item4, item5, item6);
        List<NotiRequestItem> listPastRequest = Arrays.asList(item1, item2, item3, item4, item5, item6);
        NotiRequestAdapter todayRequestAdapter = new NotiRequestAdapter(getActivity(), listTodayRequest);
        NotiRequestAdapter pastRequestAdapter = new NotiRequestAdapter(getActivity(), listPastRequest);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        int density = getResources().getDisplayMetrics().densityDpi;
        int heightPercent = 35;
        int maxItemCount = 1;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW: /*120*/
                heightPercent = 25;
                maxItemCount = 1;
                break;
            case DisplayMetrics.DENSITY_MEDIUM: /*160*/
                heightPercent = 25;
                maxItemCount = 1;
                break;
            case DisplayMetrics.DENSITY_HIGH: /*320*/
                heightPercent = 30;
                maxItemCount = 1;
                break;
            case DisplayMetrics.DENSITY_XXHIGH: /*480*/
            case DisplayMetrics.DENSITY_XXXHIGH: /*680*/
                heightPercent = 35;
                maxItemCount = 2;
                break;
        }
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * heightPercent) / 100;

        recyclerTodayRequests.setAdapter(todayRequestAdapter);
        recyclerTodayRequests.setLayoutManager(new CustomLayoutManager(getActivity(), recyclerTodayRequests, height));
        RecyclerView.Adapter mAdapter = recyclerTodayRequests.getAdapter();
        int totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > maxItemCount) {
            recyclerTodayRequests.getLayoutParams().height = height;
        }


        recyclerPastRequests.setAdapter(pastRequestAdapter);
        recyclerPastRequests.setLayoutManager(new CustomLayoutManager(getActivity(), recyclerPastRequests, height));
        mAdapter = recyclerPastRequests.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > maxItemCount) {
            recyclerPastRequests.getLayoutParams().height = height;
        }
        recyclerPastRequests.setVisibility(View.GONE);
    }

    private void init() {
        tabProfile.addTab(tabProfile.newTab().setText(getResources().getString(R.string.text_tab_request)), true);
        tabProfile.addTab(tabProfile.newTab().setText(getResources().getString(R.string.text_tab_response)));
        textTodayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textPastTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textViewMore.setTypeface(Utils.typefaceRegular(getActivity()));
        headerTodayIcon.setImageResource(R.drawable.ic_collapse);
        headerTodayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerTodayRequests.getVisibility() == View.VISIBLE) {
                    recyclerTodayRequests.setVisibility(View.GONE);
                    headerTodayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerTodayRequests.setVisibility(View.VISIBLE);
                    headerTodayIcon.setImageResource(R.drawable.ic_collapse);
                }
                recyclerPastRequests.setVisibility(View.GONE);
                headerPastIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerPastLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerTodayRequests.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);
                if (recyclerPastRequests.getVisibility() == View.VISIBLE) {
                    recyclerPastRequests.setVisibility(View.GONE);
                    headerPastIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerPastRequests.setVisibility(View.VISIBLE);
                    headerPastIcon.setImageResource(R.drawable.ic_collapse);
                }

            }
        });
    }
}
