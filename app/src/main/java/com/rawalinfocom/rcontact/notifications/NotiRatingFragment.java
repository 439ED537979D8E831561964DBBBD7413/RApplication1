package com.rawalinfocom.rcontact.notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.rawalinfocom.rcontact.events.MyLayoutManager;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRatingFragment extends BaseFragment {


    @BindView(R.id.search_view_events)
    SearchView searchViewEvents;

    @BindView(R.id.header1)
    TextView textTodayTitle;
    @BindView(R.id.header1_icon)
    ImageView headerTodayIcon;
    @BindView(R.id.relative_header1)
    RelativeLayout headerTodayLayout;
    @BindView(R.id.recycler_view1)
    RecyclerView recyclerTodayRating;

    @BindView(R.id.text_header2)
    TextView textYesterDayTitle;
    @BindView(R.id.header2_icon)
    ImageView headerYesterDayIcon;
    @BindView(R.id.relative_header2)
    RelativeLayout headerYesterdayLayout;
    @BindView(R.id.recycler_view2)
    RecyclerView recyclerYesterDayRating;

    @BindView(R.id.text_header3)
    TextView textPastDaysTitle;
    @BindView(R.id.header3_icon)
    ImageView headerPastDayIcon;
    @BindView(R.id.relative_header3)
    RelativeLayout headerPastdayLayout;
    @BindView(R.id.recycler_view3)
    RecyclerView recyclerPastDayRating;

    @BindView(R.id.text_view_more)
    TextView textViewMore;

    @Override
    public void getFragmentArguments() {

    }

    public static NotiRatingFragment newInstance() {
        return new NotiRatingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_rating, container, false);
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
        NotiRatingItem item1 = new NotiRatingItem("Aakar Jain", "Aakar Jain reply you on your rating and comments.", "11:15 PM");
        NotiRatingItem item2 = new NotiRatingItem("Angarika Shah", "Angarika Shah reply you on your rating and comments.", "11:15 PM");
        NotiRatingItem item3 = new NotiRatingItem("Angarika Shah 1", "Angarika Shah 1 reply you on your rating and comments.", "11:15 PM");
        NotiRatingItem item4 = new NotiRatingItem("Keval Pandit", "Keval Pandit reply you on your rating and comments.", "11:15 PM");
        NotiRatingItem item5 = new NotiRatingItem("Keyur Kambli", "Keyur Kambli reply you on your rating and comments.", "11:15 PM");
        NotiRatingItem item6 = new NotiRatingItem("Virat Gujarati", "Virat Gujarati reply you on your rating and comments.", "11:15 PM");

        List<NotiRatingItem> listTodayRating = Arrays.asList(item1, item2, item3, item4, item5, item6);
        List<NotiRatingItem> listYesterdayRating = Arrays.asList(item1, item2, item3);
        List<NotiRatingItem> listPastRating = Arrays.asList(item1, item2, item3, item4);

        NotiRatingAdapter todayRatingAdapter = new NotiRatingAdapter(getActivity(), listTodayRating);
        NotiRatingAdapter yesterdayRatingAdapter = new NotiRatingAdapter(getActivity(), listYesterdayRating);
        NotiRatingAdapter pastRatingAdapter = new NotiRatingAdapter(getActivity(), listPastRating);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * 49) / 100;

        recyclerTodayRating.setAdapter(todayRatingAdapter);
        recyclerTodayRating.setLayoutManager(new MyLayoutManager(getActivity(), recyclerTodayRating, height));
        RecyclerView.Adapter mAdapter = recyclerTodayRating.getAdapter();
        int totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerTodayRating.getLayoutParams().height = height;
        }

        recyclerYesterDayRating.setAdapter(yesterdayRatingAdapter);
        recyclerYesterDayRating.setLayoutManager(new MyLayoutManager(getActivity(), recyclerPastDayRating, height));
        mAdapter = recyclerYesterDayRating.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerYesterDayRating.getLayoutParams().height = height;
        }
        recyclerPastDayRating.setAdapter(pastRatingAdapter);
        recyclerPastDayRating.setLayoutManager(new MyLayoutManager(getActivity(), recyclerPastDayRating, height));
        mAdapter = recyclerPastDayRating.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerPastDayRating.getLayoutParams().height = height;
        }
        recyclerYesterDayRating.setVisibility(View.GONE);
        recyclerPastDayRating.setVisibility(View.GONE);
    }

    private void init() {
        textTodayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textYesterDayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textPastDaysTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textViewMore.setTypeface(Utils.typefaceRegular(getActivity()));

        headerTodayIcon.setImageResource(R.drawable.ic_collapse);
        headerTodayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerTodayRating.getVisibility() == View.VISIBLE) {
                    recyclerTodayRating.setVisibility(View.GONE);
                    headerTodayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerTodayRating.setVisibility(View.VISIBLE);
                    headerTodayIcon.setImageResource(R.drawable.ic_collapse);
                }
                recyclerYesterDayRating.setVisibility(View.GONE);
                headerYesterDayIcon.setImageResource(R.drawable.ic_expand);

                recyclerPastDayRating.setVisibility(View.GONE);
                headerPastDayIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerYesterdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerTodayRating.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);

                if (recyclerYesterDayRating.getVisibility() == View.VISIBLE) {
                    recyclerYesterDayRating.setVisibility(View.GONE);
                    headerYesterDayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerYesterDayRating.setVisibility(View.VISIBLE);
                    headerYesterDayIcon.setImageResource(R.drawable.ic_collapse);
                }

                recyclerPastDayRating.setVisibility(View.GONE);
                headerPastDayIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerPastdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerTodayRating.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);

                recyclerYesterDayRating.setVisibility(View.GONE);
                headerYesterDayIcon.setImageResource(R.drawable.ic_expand);

                if (recyclerPastDayRating.getVisibility() == View.VISIBLE) {
                    recyclerPastDayRating.setVisibility(View.GONE);
                    headerPastDayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerPastDayRating.setVisibility(View.VISIBLE);
                    headerPastDayIcon.setImageResource(R.drawable.ic_collapse);
                }
            }
        });
    }
}
